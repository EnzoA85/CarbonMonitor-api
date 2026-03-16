package com.capgemini.capcarbon.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.capgemini.capcarbon.entity.SiteHistory;

@Repository
public interface SiteHistoryRepository extends JpaRepository<SiteHistory, Long> {
    List<SiteHistory> findBySiteIdOrderByYearAsc(Long siteId);
}
