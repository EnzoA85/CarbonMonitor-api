package com.capgemini.capcarbon.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardKpiResponse {
    private Long totalSites;
    private Double totalCarbonFootprint;
    private Double averageCo2PerM2;
    private Double averageCo2PerEmployee;
}
