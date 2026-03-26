/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.opd.repository;

import com.hospital.opd.entity.Consultation;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author mduhijod
 */
// ========== ConsultationRepository ==========
@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {

    Optional<Consultation> findByConsultationId(String consultationId);

    @Query("SELECT c.consultationId FROM Consultation c ORDER BY c.id DESC")
    List<String> findTopByOrderByIdDesc();

    List<Consultation> findByPinNumberOrderByConsultationDateDesc(String pinNumber);

    List<Consultation> findByDoctorIdAndConsultationDateBetween(String doctorId, LocalDateTime startDate, LocalDateTime endDate);

    Optional<Consultation> findByCvrNumber(String cvrNumber);

    Optional<Consultation> findByAppointmentId(String appointmentId);

    @Query(value = "SELECT c.* FROM consultations c "
            + "JOIN appointment_slots a ON c.appointment_id = a.appointment_id "
            + "WHERE a.doctor_id = :doctorId "
            + "AND c.status = 'COMPLETED' "
            + "AND c.consultation_date BETWEEN :start AND :end",
            nativeQuery = true)
    List<Consultation> findCompletedByDoctorFromAppointment(
            @Param("doctorId") String doctorId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

}
