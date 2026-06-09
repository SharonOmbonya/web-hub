package com.web_hub.web_hub.assetManagement.repository;


import com.web_hub.web_hub.assetManagement.model.Asset;
import com.web_hub.web_hub.assetManagement.model.AssetStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetManagementRepository extends JpaRepository<Asset, Long> {

    // Scenario A: User selected a status tab AND typed a search query
    @Query("SELECT a FROM Asset a WHERE a.status = :status " +
            "AND (LOWER(a.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(a.serialNumber) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(a.assetCode) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Asset> findByStatusAndSearch(@Param("status") AssetStatus status, @Param("search") String search);

    // Scenario B: User only typed a search query (All statuses)
    @Query("SELECT a FROM Asset a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(a.serialNumber) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(a.assetCode) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Asset> findBySearch(@Param("search") String search);

    // Scenario C: User only clicked a status tab
    List<Asset> findByStatus(AssetStatus status);

    // Dashboard count helpers
    long countByStatus(AssetStatus status);

    // Validation helper
    boolean existsBySerialNumber(String serialNumber);

    // Code generation helper
    Optional<Asset> findFirstByOrderByIdDesc();
}