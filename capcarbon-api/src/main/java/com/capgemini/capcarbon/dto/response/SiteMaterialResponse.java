package com.capgemini.capcarbon.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SiteMaterialResponse {
    private Long id;
    private Long siteId;
    private MaterialResponse material;
    private Double quantity;
    private Double calculatedEmission;
}
