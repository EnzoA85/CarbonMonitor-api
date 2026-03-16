package com.capgemini.capcarbon.dto.response;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KpiResponse {
    private Long siteId;
    private String siteName;
    private Double totalCo2Equivalent;
    private Map<String, Double> co2ByCategory; // ex: {"ENERGY": 150.5, "MATERIAL": 45.2}
}
