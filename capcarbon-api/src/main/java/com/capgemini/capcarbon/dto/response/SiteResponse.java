package com.capgemini.capcarbon.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SiteResponse {
    private Long id;
    private String name;
    private String location;
    private Double surface;
    private Integer parkingSpaces;
    private Integer employees;
    private Double energyConsumption;
    private LocalDateTime createdAt;
    private String createdBy;
}
