package com.capgemini.capcarbon.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SiteHistoryRequest {

    @NotNull
    private Integer year;

    private Double energyConsumption;

    private Integer employees;

    private Double totalEmission;
}
