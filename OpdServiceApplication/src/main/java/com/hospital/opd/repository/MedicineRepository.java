package com.hospital.opd.repository;

import com.hospital.opd.entity.Medicine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    @Query("SELECT m FROM Medicine m WHERE m.isActive = true AND " +
           "(LOWER(m.medicineName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(m.genericName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(m.brandName) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Medicine> searchMedicines(@Param("query") String query, Pageable pageable);

    @Query("SELECT m FROM Medicine m WHERE m.isActive = true AND " +
           "(LOWER(m.medicineName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(m.genericName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(m.brandName) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Medicine> searchMedicinesList(@Param("query") String query);

    Page<Medicine> findAllByIsActiveTrue(Pageable pageable);
}
