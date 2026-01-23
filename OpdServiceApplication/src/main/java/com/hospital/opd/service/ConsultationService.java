/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.opd.service;

import com.hospital.opd.dto.ConsultationDTO;
import com.hospital.opd.dto.CreateConsultationDTO;
import com.hospital.opd.entity.Consultation;
import com.hospital.opd.repository.ConsultationRepository;
import static java.lang.StrictMath.log;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
            } catch (Exception e) {}
        }
        return String.format("CONS%010d", nextNum);
    }
}
