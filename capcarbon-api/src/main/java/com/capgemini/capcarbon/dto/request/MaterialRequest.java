package com.capgemini.capcarbon.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MaterialRequest {

    @NotBlank
    private String name;

    @NotNull
    private Double emissionFactor;

    @NotBlank
    private String unit;
}
