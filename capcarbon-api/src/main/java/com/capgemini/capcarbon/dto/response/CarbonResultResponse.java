package com.capgemini.capcarbon.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CarbonResultResponse {
    private Long id;
    private Long siteId;
    private Double constructionEmission;
    private Double exploitationEmission;
    private Double totalEmission;
    private Double co2PerM2;
    private Double co2PerEmployee;
    private LocalDateTime calculatedAt;
}
