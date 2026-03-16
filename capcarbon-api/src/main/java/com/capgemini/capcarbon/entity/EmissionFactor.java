package com.capgemini.capcarbon.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "emission_factors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmissionFactor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(nullable = false)
    private String name;        // Ex: "Électricité", "Béton"

    @Column(nullable = false)
    private String unit;        // Ex: "kWh", "m3", "tonne"

    @Column(nullable = false)
    private Double value;  // kgCO2e par unité

    public enum Category {
        ENERGY,    // Énergie (électricité, gaz...)
        MATERIAL   // Matériaux de construction
    }
}
