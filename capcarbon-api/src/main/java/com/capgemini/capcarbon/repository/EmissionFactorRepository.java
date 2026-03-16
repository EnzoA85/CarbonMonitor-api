package com.capgemini.capcarbon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.capgemini.capcarbon.entity.EmissionFactor;

@Repository
public interface EmissionFactorRepository extends JpaRepository<EmissionFactor, Long> {
    boolean existsByName(String name);
}
