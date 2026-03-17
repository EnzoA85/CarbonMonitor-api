package com.capgemini.capcarbon.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.capgemini.capcarbon.entity.SiteMaterial;

@Repository
public interface SiteMaterialRepository extends JpaRepository<SiteMaterial, Long> {
    List<SiteMaterial> findBySiteId(Long siteId);

    @Modifying
    @Query("DELETE FROM SiteMaterial sm WHERE sm.id = :id AND sm.site.id = :siteId")
    int deleteByIdAndSiteId(@Param("id") Long id, @Param("siteId") Long siteId);
}
