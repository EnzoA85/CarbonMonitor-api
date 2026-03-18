package com.capgemini.capcarbon.config;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.capgemini.capcarbon.entity.Material;
import com.capgemini.capcarbon.repository.MaterialRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Importe les facteurs d'émission depuis la Base Carbone® ADEME
 * (https://data.ademe.fr/datasets/base-carboner/full) via l'API Data-Fair.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class MaterialDataLoader {

    private static final String ADEME_BASE_CARBONE_LINES =
            "https://data.ademe.fr/data-fair/api/v1/datasets/base-carboner/lines";

    private static final int BULK_IMPORT_THRESHOLD = 1000;
    private static final int PAGE_SIZE = 1000;
    private static final Pattern AFTER_PARAM = Pattern.compile("[?&]after=([^&]+)");

    private static final Object[][] BASE_CARBONE_MATERIALS = {
        { "Béton", 0.13, "kg" },
        { "Acier", 1.37, "kg" },
        { "Verre", 0.85, "kg" },
        { "Bois", 0.04, "kg" },
        { "Aluminium", 6.73, "kg" },
        { "Cuivre", 3.81, "kg" },
        { "PVC", 2.73, "kg" },
        { "Plâtre", 0.12, "kg" },
        { "Terre cuite", 0.23, "kg" },
        { "Chanvre", 0.15, "kg" },
    };

    @Bean
    CommandLineRunner initMaterials(MaterialRepository materialRepository) {
        return args -> {
            final RestClient client = RestClient.create();

            // Si la base ne contient pas encore la Base Carbone, on importe tout.
            // (Sinon, la liste déroulante n'a que quelques matériaux.)
            if (materialRepository.count() < BULK_IMPORT_THRESHOLD) {
                try {
                    int imported = importAllBaseCarbone(client, materialRepository);
                    log.info("ADEME Base Carbone bulk import: {} materials created.", imported);
                    return;
                } catch (Exception e) {
                    log.warn("ADEME Base Carbone bulk import impossible ({}). Fallback activé.", e.getMessage());
                    seedFallback(materialRepository);
                    return;
                }
            }

            log.debug("Materials already present ({}), skipping bulk import.", materialRepository.count());
        };
    }

    private void seedFallback(MaterialRepository materialRepository) {
        for (Object[] row : BASE_CARBONE_MATERIALS) {
            String name = (String) row[0];
            if (materialRepository.existsByNameIgnoreCase(name)) continue;
            Material m = Material.builder()
                    .name(name)
                    .emissionFactor((Double) row[1])
                    .unit((String) row[2])
                    .build();
            materialRepository.save(m);
        }
        log.info("Seeded fallback materials: {} entries available.", BASE_CARBONE_MATERIALS.length);
    }

    private int importAllBaseCarbone(RestClient client, MaterialRepository materialRepository) {
        int created = 0;
        String after = null;

        while (true) {
            UriComponentsBuilder b = UriComponentsBuilder.fromHttpUrl(ADEME_BASE_CARBONE_LINES)
                    .queryParam("size", PAGE_SIZE);
            if (after != null) {
                b = b.queryParam("after", after);
            }
            URI uri = b.build(true).toUri();

            @SuppressWarnings("unchecked")
            Map<String, Object> body = client.get().uri(uri).retrieve().body(Map.class);
            if (body == null) break;

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> results = (List<Map<String, Object>>) body.get("results");
            if (results == null || results.isEmpty()) break;

            List<Material> batch = new ArrayList<>();
            for (Map<String, Object> row : results) {
                String type = asString(row.get("Type_de_l'élément"));
                String statut = asString(row.get("Statut_de_l'élément"));
                String structure = asString(row.get("Structure"));
                Double total = asDouble(row.get("Total_poste_non_décomposé"));
                String unit = asString(row.get("Unité_français"));
                String baseName = asString(row.get("Nom_base_français"));
                String attrName = asString(row.get("Nom_attribut_français"));

                if (total == null || unit == null || baseName == null) continue;
                if (!"Facteur d'émission".equalsIgnoreCase(type)) continue;
                if (statut == null || !statut.toLowerCase(Locale.ROOT).contains("valide")) continue;
                if (structure == null || !structure.toLowerCase(Locale.ROOT).contains("élément non décomposé")) continue;

                String quantityUnit = parseQuantityUnit(unit);
                if (quantityUnit == null) continue;

                String name = baseName;
                if (attrName != null && !attrName.equalsIgnoreCase(baseName)) {
                    name = baseName + " — " + attrName;
                }
                name = name.replaceAll("\\s+", " ").trim();
                if (name.isEmpty()) continue;

                if (materialRepository.existsByNameIgnoreCase(name)) continue;

                batch.add(Material.builder()
                        .name(name)
                        .emissionFactor(total)
                        .unit(quantityUnit)
                        .build());
            }

            if (!batch.isEmpty()) {
                materialRepository.saveAll(batch);
                created += batch.size();
            }

            String next = asString(body.get("next"));
            if (next == null) break;
            Matcher m = AFTER_PARAM.matcher(next);
            if (!m.find()) break;
            after = m.group(1);
        }

        return created;
    }

    private String parseQuantityUnit(String baseCarboneUnit) {
        // Exemples: "kgCO2e/tonne", "kgCO2e/kg de poids net"
        String u = baseCarboneUnit.toLowerCase(Locale.ROOT).replace(" ", "");
        int idx = u.indexOf('/');
        if (idx < 0 || idx == u.length() - 1) return null;
        String rhs = u.substring(idx + 1);
        // normalisations simples pour l'app
        if (rhs.startsWith("kg")) return "kg";
        if (rhs.startsWith("tonne") || rhs.startsWith("ton")) return "tonne";
        if (rhs.startsWith("m3")) return "m3";
        if (rhs.startsWith("l")) return "l";
        if (rhs.startsWith("kwh")) return "kWh";
        if (rhs.startsWith("unité") || rhs.startsWith("unite")) return "unité";
        // fallback: garder une version courte (max 12 chars) pour ne pas exploser l'UI
        rhs = rhs.replaceAll("[^a-z0-9]+", "");
        if (rhs.isEmpty()) return null;
        return rhs.length() > 12 ? rhs.substring(0, 12) : rhs;
    }

    private static String asString(Object value) {
        if (value == null) return null;
        String s = String.valueOf(value).trim();
        return s.isEmpty() ? null : s;
    }

    private static Double asDouble(Object value) {
        if (value == null) return null;
        if (value instanceof Number n) return n.doubleValue();
        try {
            return Double.parseDouble(String.valueOf(value).replace(",", ".").trim());
        } catch (Exception e) {
            return null;
        }
    }
}
