package com.capgemini.capcarbon.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MaterialResponse {
    private Long id;
    private String name;
    private Double emissionFactor;
    private String unit;
}
