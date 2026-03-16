package com.capgemini.capcarbon.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.capgemini.capcarbon.dto.response.DashboardKpiResponse;
import com.capgemini.capcarbon.entity.CarbonResult;
import com.capgemini.capcarbon.repository.CarbonResultRepository;
import com.capgemini.capcarbon.repository.SiteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final SiteRepository siteRepository;
    private final CarbonResultRepository carbonResultRepository;

    public DashboardKpiResponse getGlobalKpis() {
        long totalSites = siteRepository.count();
        List<CarbonResult> allResults = carbonResultRepository.findAll();
        
        // Take the latest result per site by grouping
        // For simplicity, sum all for the PoC or fetch all calculate endpoints
        double totalCarbonFootprint = allResults.stream().mapToDouble(CarbonResult::getTotalEmission).sum();

        double avgM2 = allResults.isEmpty() ? 0 :
                allResults.stream().mapToDouble(CarbonResult::getCo2PerM2).average().orElse(0);

        double avgEmp = allResults.isEmpty() ? 0 :
                allResults.stream().mapToDouble(CarbonResult::getCo2PerEmployee).average().orElse(0);

        return DashboardKpiResponse.builder()
                .totalSites(totalSites)
                .totalCarbonFootprint(totalCarbonFootprint)
                .averageCo2PerM2(avgM2)
                .averageCo2PerEmployee(avgEmp)
                .build();
    }
}
