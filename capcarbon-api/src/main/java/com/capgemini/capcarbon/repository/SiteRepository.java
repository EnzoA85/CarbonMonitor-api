package com.capgemini.capcarbon.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.capgemini.capcarbon.entity.Site;

@Repository
public interface SiteRepository extends JpaRepository<Site, Long> {
    List<Site> findByCreatedByEmailOrderByCreatedAtDesc(String email);

    Optional<Site> findByIdAndCreatedByEmail(Long id, String email);

    boolean existsByIdAndCreatedByEmail(Long id, String email);
}
