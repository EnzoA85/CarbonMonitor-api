package com.capgemini.capcarbon.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SiteDataResponse {
    private Long id;
    private Long siteId;
    private String siteName;
    private Long factorId;
    private String factorName;
    private String category;
    private Double quantity;
    private String unit;
    private Double co2Equivalent; // calculated: quantity * factor.co2EquivalentPerUnit
}
