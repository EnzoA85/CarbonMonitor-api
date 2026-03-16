package com.capgemini.capcarbon.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.capgemini.capcarbon.entity.CarbonResult;

@Repository
public interface CarbonResultRepository extends JpaRepository<CarbonResult, Long> {
    List<CarbonResult> findBySiteId(Long siteId);
    Optional<CarbonResult> findFirstBySiteIdOrderByCalculatedAtDesc(Long siteId);
}
