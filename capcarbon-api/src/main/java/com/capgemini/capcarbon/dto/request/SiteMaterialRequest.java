package com.capgemini.capcarbon.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SiteMaterialRequest {

    @NotNull
    private Long materialId;

    @NotNull
    private Double quantity;
}
