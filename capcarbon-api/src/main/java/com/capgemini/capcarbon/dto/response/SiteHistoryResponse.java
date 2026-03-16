package com.capgemini.capcarbon.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SiteHistoryResponse {
    private Long id;
    private Long siteId;
    private Integer year;
    private Double energyConsumption;
    private Integer employees;
    private Double totalEmission;
}
