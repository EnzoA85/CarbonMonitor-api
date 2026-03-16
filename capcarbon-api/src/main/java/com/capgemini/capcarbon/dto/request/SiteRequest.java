package com.capgemini.capcarbon.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SiteRequest {

    @NotBlank
    private String name;

    private String location;

    @NotNull
    private Double surface;

    private Integer parkingSpaces;

    @NotNull
    private Integer employees;

    private Double energyConsumption;
}
