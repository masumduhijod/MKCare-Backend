/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.opd.service;

import com.hospital.opd.dto.ConsultationDTO;
import com.hospital.opd.dto.CreateConsultationDTO;
import com.hospital.opd.entity.Consultation;
import com.hospital.opd.entity.Consultation.ConsultationStatus;
import com.hospital.opd.repository.ConsultationRepository;
import static java.lang.StrictMath.log;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author mduhijod
 */
// ========== ConsultationService ==========
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final ModelMapper modelMapper;
    @Autowired
private JdbcTemplate jdbcTemplate;

    public ConsultationDTO createConsultation(CreateConsultationDTO dto) {
        log.info("Creating consultation for CVR: {}", dto.getCvrNumber());

        String consultationId = generateConsultationId();

        Consultation consultation = new Consultation();
        consultation.setConsultationId(consultationId);
        consultation.setAppointmentId(dto.getAppointmentId());
        consultation.setCvrNumber(dto.getCvrNumber());
        consultation.setPinNumber(dto.getPinNumber());
        consultation.setDoctorId(dto.getDoctorId());
        consultation.setPatientId(1L); // TODO
        consultation.setChiefComplaint(dto.getChiefComplaint());
        consultation.setPresentIllness(dto.getPresentIllness());
        consultation.setExaminationFindings(dto.getExaminationFindings());
        consultation.setDiagnosis(dto.getDiagnosis());
        consultation.setTreatmentPlan(dto.getTreatmentPlan());
        consultation.setSubjective(dto.getSubjective());
        consultation.setObjective(dto.getObjective());
        consultation.setAssessment(dto.getAssessment());
        consultation.setPlan(dto.getPlan());
        consultation.setFollowUpRequired(dto.getFollowUpRequired());
        consultation.setFollowUpDate(dto.getFollowUpDate());
        consultation.setFollowUpInstructions(dto.getFollowUpInstructions());

        Consultation saved = consultationRepository.save(consultation);
        log.info("Consultation created: {}", consultationId);
        return modelMapper.map(saved, ConsultationDTO.class);
    }

    public ConsultationDTO getConsultationById(String consultationId) {
        Consultation consultation = consultationRepository.findByConsultationId(consultationId)
                .orElseThrow(() -> new RuntimeException("Consultation not found"));
        return modelMapper.map(consultation, ConsultationDTO.class);
    }

    public ConsultationDTO completeConsultation(String consultationId) {
        Consultation consultation = consultationRepository.findByConsultationId(consultationId)
                .orElseThrow(() -> new RuntimeException("Consultation not found"));
        consultation.complete();
        return modelMapper.map(consultationRepository.save(consultation), ConsultationDTO.class);
    }

    public List<ConsultationDTO> getPatientConsultations(String pinNumber) {
        List<Consultation> consultations = consultationRepository.findByPinNumberOrderByConsultationDateDesc(pinNumber);
        return consultations.stream().map(c -> modelMapper.map(c, ConsultationDTO.class)).collect(Collectors.toList());
    }

    private String generateConsultationId() {
        List<String> ids = consultationRepository.findTopByOrderByIdDesc();
        String lastId = ids.isEmpty() ? null : ids.get(0);
        int nextNum = 1;
        if (lastId != null) {
            try {
                nextNum = Integer.parseInt(lastId.substring(4)) + 1;
            } catch (Exception e) {
            }
        }
        return String.format("CONS%010d", nextNum);
    }

    @Transactional
    public ConsultationDTO updateConsultation(String consultationId, ConsultationDTO dto) {

        Consultation consultation = consultationRepository
                .findByConsultationId(consultationId)
                .orElseThrow(() -> new RuntimeException("Consultation not found"));

        // Map only updatable fields
        modelMapper.map(dto, consultation);

        Consultation updated = consultationRepository.save(consultation);
        return modelMapper.map(updated, ConsultationDTO.class);
    }

    public void deleteConsultation(String consultationId) {

        Consultation consultation = consultationRepository
                .findByConsultationId(consultationId)
                .orElseThrow(() -> new RuntimeException("Consultation not found"));

        consultationRepository.delete(consultation);

        log.info("Consultation deleted: {}", consultationId);
    }
// Get consultations for a doctor on a specific date

    public List<ConsultationDTO> getCompletedConsultationsByDoctor(String doctorId, LocalDate date) {
    LocalDateTime startDate = date.atStartOfDay();
    LocalDateTime endDate = date.atTime(23, 59, 59);
    
    // ⭐ Single DB per clinic — no cross-DB joins needed
    String sql = "SELECT c.* FROM consultations c " +
        "JOIN appointments a ON c.appointment_id = a.appointment_id " +
        "WHERE a.doctor_id = ? " +
        "AND c.status = ? " +
        "AND c.consultation_date >= ? " +
        "AND c.consultation_date <= ?";
    
    System.out.println("🔍 [OPD] Executing query for doctor: " + doctorId);
    System.out.println("📝 Params: doctorId=" + doctorId + ", status=COMPLETED, start=" + startDate + ", end=" + endDate);
    
    List<Consultation> consultations = jdbcTemplate.query(
        sql,
        new Object[]{doctorId, "COMPLETED", startDate, endDate},
        new BeanPropertyRowMapper<>(Consultation.class)
    );
    
    System.out.println("✅ Found " + consultations.size() + " consultations");
    
    // Map to DTOs
    return consultations.stream()
        .map(consultation -> {
            ConsultationDTO dto = new ConsultationDTO();
            BeanUtils.copyProperties(consultation, dto);
            return dto;
        })
        .collect(Collectors.toList());
}
// Get consultations for a doctor on a specific date
public List<ConsultationDTO> getConsultationsByDoctorAndDate(String doctorId, LocalDate date) {

    List<Consultation> consultations =
            consultationRepository.findCompletedByDoctorFromAppointment(
                    doctorId,
                    date.atStartOfDay(),
                    date.plusDays(1).atStartOfDay()
            );

    return consultations.stream()
            .map(c -> modelMapper.map(c, ConsultationDTO.class))
            .collect(Collectors.toList());
}

}
