package com.capgemini.capcarbon.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.capgemini.capcarbon.dto.request.SiteHistoryRequest;
import com.capgemini.capcarbon.dto.request.SiteMaterialRequest;
import com.capgemini.capcarbon.dto.request.SiteRequest;
import com.capgemini.capcarbon.dto.response.CarbonResultResponse;
import com.capgemini.capcarbon.dto.response.MaterialResponse;
import com.capgemini.capcarbon.dto.response.SiteHistoryResponse;
import com.capgemini.capcarbon.dto.response.SiteMaterialResponse;
import com.capgemini.capcarbon.dto.response.SiteResponse;
import com.capgemini.capcarbon.entity.CarbonResult;
import com.capgemini.capcarbon.entity.Material;
import com.capgemini.capcarbon.entity.Site;
import com.capgemini.capcarbon.entity.SiteHistory;
import com.capgemini.capcarbon.entity.SiteMaterial;
import com.capgemini.capcarbon.entity.User;
import com.capgemini.capcarbon.repository.CarbonResultRepository;
import com.capgemini.capcarbon.repository.MaterialRepository;
import com.capgemini.capcarbon.repository.SiteHistoryRepository;
import com.capgemini.capcarbon.repository.SiteMaterialRepository;
import com.capgemini.capcarbon.repository.SiteRepository;
import com.capgemini.capcarbon.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SiteService {
    private final SiteRepository siteRepository;
    private final MaterialRepository materialRepository;
    private final SiteMaterialRepository siteMaterialRepository;
    private final SiteHistoryRepository siteHistoryRepository;
    private final UserRepository userRepository;
    private final CarbonResultRepository carbonResultRepository;

    public SiteResponse createSite(SiteRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email).orElse(null);

        Site site = Site.builder()
                .name(request.getName())
                .location(request.getLocation())
                .surface(request.getSurface())
                .parkingSpaces(request.getParkingSpaces())
                .employees(request.getEmployees())
                .energyConsumption(request.getEnergyConsumption())
                .createdBy(currentUser)
                .build();
        Site saved = siteRepository.save(site);
        return mapToSiteResponse(saved);
    }

    public SiteResponse updateSite(Long id, SiteRequest request) {
        Site site = siteRepository.findById(id).orElseThrow(() -> new RuntimeException("Site not found"));
        site.setName(request.getName());
        site.setLocation(request.getLocation());
        site.setSurface(request.getSurface());
        site.setParkingSpaces(request.getParkingSpaces());
        site.setEmployees(request.getEmployees());
        site.setEnergyConsumption(request.getEnergyConsumption());
        return mapToSiteResponse(siteRepository.save(site));
    }

    public void deleteSite(Long id) {
        if (!siteRepository.existsById(id)) throw new RuntimeException("Site not found");
        siteRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<SiteResponse> findAll() {
        return siteRepository.findAll().stream().map(this::mapToSiteResponse).collect(Collectors.toList());
    }

    public SiteResponse findById(Long id) {
        return siteRepository.findById(id).map(this::mapToSiteResponse)
                .orElseThrow(() -> new RuntimeException("Site not found"));
    }

    // Material Methods
    public List<SiteMaterialResponse> getSiteMaterials(Long siteId) {
        return siteMaterialRepository.findBySiteId(siteId).stream()
                .map(this::mapToSiteMaterialResponse).collect(Collectors.toList());
    }

    public SiteMaterialResponse addSiteMaterial(Long siteId, SiteMaterialRequest request) {
        Site site = siteRepository.findById(siteId).orElseThrow(() -> new RuntimeException("Site not found"));
        Material material = materialRepository.findById(request.getMaterialId())
                .orElseThrow(() -> new RuntimeException("Material not found"));

        double calculatedEmission = request.getQuantity() * material.getEmissionFactor();

        SiteMaterial sm = SiteMaterial.builder()
                .site(site)
                .material(material)
                .quantity(request.getQuantity())
                .calculatedEmission(calculatedEmission)
                .build();

        return mapToSiteMaterialResponse(siteMaterialRepository.save(sm));
    }

    public void removeSiteMaterial(Long siteId, Long materialId) {
        siteMaterialRepository.deleteBySiteIdAndMaterialId(siteId, materialId);
    }

    // History Methods
    public List<SiteHistoryResponse> getSiteHistory(Long siteId) {
        return siteHistoryRepository.findBySiteIdOrderByYearAsc(siteId).stream()
                .map(this::mapToSiteHistoryResponse).collect(Collectors.toList());
    }

    public SiteHistoryResponse addSiteHistory(Long siteId, SiteHistoryRequest request) {
        Site site = siteRepository.findById(siteId).orElseThrow(() -> new RuntimeException("Site not found"));
        SiteHistory history = SiteHistory.builder()
                .site(site)
                .year(request.getYear())
                .energyConsumption(request.getEnergyConsumption())
                .employees(request.getEmployees())
                .totalEmission(request.getTotalEmission())
                .build();
        return mapToSiteHistoryResponse(siteHistoryRepository.save(history));
    }

    // Calcul
    public CarbonResultResponse calculateEmission(Long siteId) {
        Site site = siteRepository.findById(siteId).orElseThrow(() -> new RuntimeException("Site not found"));
        
        // Sum material emissions for construction
        List<SiteMaterial> materials = siteMaterialRepository.findBySiteId(siteId);
        double constructionEmission = materials.stream().mapToDouble(SiteMaterial::getCalculatedEmission).sum();

        // Very basic mock calculation for exploitation (could use real emission factors with site's energy)
        double exploitationEmission = (site.getEnergyConsumption() != null ? site.getEnergyConsumption() : 0) * 0.5; // dummy factor 0.5

        double totalEmission = constructionEmission + exploitationEmission;
        double co2PerM2 = site.getSurface() != null && site.getSurface() > 0 ? totalEmission / site.getSurface() : 0;
        double co2PerEmployee = site.getEmployees() != null && site.getEmployees() > 0 ? totalEmission / site.getEmployees() : 0;

        CarbonResult result = CarbonResult.builder()
                .site(site)
                .constructionEmission(constructionEmission)
                .exploitationEmission(exploitationEmission)
                .totalEmission(totalEmission)
                .co2PerM2(co2PerM2)
                .co2PerEmployee(co2PerEmployee)
                .calculatedAt(LocalDateTime.now())
                .build();

        return mapToCarbonResultResponse(carbonResultRepository.save(result));
    }
    
    // Mappers
    private SiteResponse mapToSiteResponse(Site site) {
        return SiteResponse.builder()
                .id(site.getId())
                .name(site.getName())
                .location(site.getLocation())
                .surface(site.getSurface())
                .parkingSpaces(site.getParkingSpaces())
                .employees(site.getEmployees())
                .energyConsumption(site.getEnergyConsumption())
                .createdAt(site.getCreatedAt())
                .createdBy(site.getCreatedBy() != null ? site.getCreatedBy().getEmail() : null)
                .build();
    }

    private SiteMaterialResponse mapToSiteMaterialResponse(SiteMaterial sm) {
        MaterialResponse mResp = MaterialResponse.builder()
                .id(sm.getMaterial().getId())
                .name(sm.getMaterial().getName())
                .emissionFactor(sm.getMaterial().getEmissionFactor())
                .unit(sm.getMaterial().getUnit())
                .build();
        return SiteMaterialResponse.builder()
                .id(sm.getId())
                .siteId(sm.getSite().getId())
                .material(mResp)
                .quantity(sm.getQuantity())
                .calculatedEmission(sm.getCalculatedEmission())
                .build();
    }

    private SiteHistoryResponse mapToSiteHistoryResponse(SiteHistory sh) {
        return SiteHistoryResponse.builder()
                .id(sh.getId())
                .siteId(sh.getSite().getId())
                .year(sh.getYear())
                .energyConsumption(sh.getEnergyConsumption())
                .employees(sh.getEmployees())
                .totalEmission(sh.getTotalEmission())
                .build();
    }

    private CarbonResultResponse mapToCarbonResultResponse(CarbonResult cr) {
        return CarbonResultResponse.builder()
                .id(cr.getId())
                .siteId(cr.getSite().getId())
                .constructionEmission(cr.getConstructionEmission())
                .exploitationEmission(cr.getExploitationEmission())
                .totalEmission(cr.getTotalEmission())
                .co2PerM2(cr.getCo2PerM2())
                .co2PerEmployee(cr.getCo2PerEmployee())
                .calculatedAt(cr.getCalculatedAt())
                .build();
    }
}
