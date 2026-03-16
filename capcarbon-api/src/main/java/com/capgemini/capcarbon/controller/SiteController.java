package com.capgemini.capcarbon.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capgemini.capcarbon.dto.request.SiteHistoryRequest;
import com.capgemini.capcarbon.dto.request.SiteMaterialRequest;
import com.capgemini.capcarbon.dto.request.SiteRequest;
import com.capgemini.capcarbon.dto.response.CarbonResultResponse;
import com.capgemini.capcarbon.dto.response.SiteHistoryResponse;
import com.capgemini.capcarbon.dto.response.SiteMaterialResponse;
import com.capgemini.capcarbon.dto.response.SiteResponse;
import com.capgemini.capcarbon.service.SiteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/sites")
@RequiredArgsConstructor
public class SiteController {
    private final SiteService siteService;

    // Site CRUD
    @GetMapping
    public ResponseEntity<List<SiteResponse>> findAll() {
        return ResponseEntity.ok(siteService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SiteResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(siteService.findById(id));
    }

    @PostMapping
    public ResponseEntity<SiteResponse> createSite(@Valid @RequestBody SiteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(siteService.createSite(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SiteResponse> updateSite(@PathVariable Long id, @Valid @RequestBody SiteRequest request) {
        return ResponseEntity.ok(siteService.updateSite(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSite(@PathVariable Long id) {
        siteService.deleteSite(id);
        return ResponseEntity.noContent().build();
    }

    // Site Materials
    @GetMapping("/{id}/materials")
    public ResponseEntity<List<SiteMaterialResponse>> getSiteMaterials(@PathVariable Long id) {
        return ResponseEntity.ok(siteService.getSiteMaterials(id));
    }

    @PostMapping("/{id}/materials")
    public ResponseEntity<SiteMaterialResponse> addSiteMaterial(
            @PathVariable Long id, 
            @Valid @RequestBody SiteMaterialRequest request) {
        return ResponseEntity.ok(siteService.addSiteMaterial(id, request));
    }

    @DeleteMapping("/{siteId}/materials/{materialId}")
    public ResponseEntity<Void> removeSiteMaterial(
            @PathVariable Long siteId, 
            @PathVariable Long materialId) {
        siteService.removeSiteMaterial(siteId, materialId);
        return ResponseEntity.noContent().build();
    }

    // Site History
    @GetMapping("/{id}/history")
    public ResponseEntity<List<SiteHistoryResponse>> getSiteHistory(@PathVariable Long id) {
        return ResponseEntity.ok(siteService.getSiteHistory(id));
    }

    @PostMapping("/{id}/history")
    public ResponseEntity<SiteHistoryResponse> addSiteHistory(
            @PathVariable Long id, 
            @Valid @RequestBody SiteHistoryRequest request) {
        return ResponseEntity.ok(siteService.addSiteHistory(id, request));
    }

    // Calculate
    @PostMapping("/{id}/calculate")
    public ResponseEntity<CarbonResultResponse> calculateEmission(@PathVariable Long id) {
        return ResponseEntity.ok(siteService.calculateEmission(id));
    }

    // Report (Mocked as returning the latest calculate or you could create a new DTO)
    @GetMapping("/{id}/report")
    public ResponseEntity<CarbonResultResponse> getSiteReport(@PathVariable Long id) {
        // Here we just trigger calculation for the report format as requested
        return ResponseEntity.ok(siteService.calculateEmission(id));
    }
}
