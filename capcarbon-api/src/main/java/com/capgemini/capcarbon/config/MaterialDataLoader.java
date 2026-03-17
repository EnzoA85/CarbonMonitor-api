package com.capgemini.capcarbon.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.capgemini.capcarbon.entity.Material;
import com.capgemini.capcarbon.repository.MaterialRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Charge 10 matériaux de construction issus de la Base Carbone® ADEME
 * (https://data.ademe.fr/datasets/base-carboner/full).
 * Facteurs d'émission en kgCO₂e/kg, sources : Base Carbone®, INIES, documentation bâtiments.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class MaterialDataLoader {

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
            if (materialRepository.count() > 0) {
                log.debug("Materials already seeded, skipping.");
                return;
            }
            for (Object[] row : BASE_CARBONE_MATERIALS) {
                Material m = Material.builder()
                        .name((String) row[0])
                        .emissionFactor((Double) row[1])
                        .unit((String) row[2])
                        .build();
                materialRepository.save(m);
            }
            log.info("Seeded {} materials from Base Carbone® ADEME.", BASE_CARBONE_MATERIALS.length);
        };
    }
}
