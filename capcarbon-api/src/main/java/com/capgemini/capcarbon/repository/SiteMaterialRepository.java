package com.capgemini.capcarbon.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.capgemini.capcarbon.entity.SiteMaterial;

@Repository
public interface SiteMaterialRepository extends JpaRepository<SiteMaterial, Long> {
    List<SiteMaterial> findBySiteId(Long siteId);
    void deleteBySiteIdAndMaterialId(Long siteId, Long materialId);
}
