/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.opd.service;

/**
 *
 * @author mduhijod
 */
// ========== PrescriptionService ==========
import com.hospital.opd.dto.CreatePrescriptionDTO;
import com.hospital.opd.dto.PrescriptionDTO;
import com.hospital.opd.dto.PrescriptionItemDTO;
import com.hospital.opd.entity.Prescription;
import com.hospital.opd.entity.PrescriptionItem;
import com.hospital.opd.repository.PrescriptionItemRepository;
import com.hospital.opd.repository.PrescriptionRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionItemRepository itemRepository;
    private final ModelMapper modelMapper;

    public PrescriptionDTO createPrescription(CreatePrescriptionDTO dto) {
        log.info("Creating prescription for PIN: {}", dto.getPinNumber());

        String prescriptionId = generatePrescriptionId();

        Prescription prescription = new Prescription();
        prescription.setPrescriptionId(prescriptionId);
        prescription.setConsultationId(dto.getConsultationId());
        prescription.setConsultationNumber(dto.getConsultationNumber());
        prescription.setPinNumber(dto.getPinNumber());
        prescription.setDoctorId(dto.getDoctorId());
        prescription.setPatientId(1L); // TODO
        prescription.setValidityDays(dto.getValidityDays() != null ? dto.getValidityDays() : 30);
        prescription.setInstructions(dto.getInstructions());

        // Add items
        for (PrescriptionItemDTO itemDTO : dto.getItems()) {
            PrescriptionItem item = modelMapper.map(itemDTO, PrescriptionItem.class);
            prescription.addItem(item);
        }

        Prescription saved = prescriptionRepository.save(prescription);
        log.info("Prescription created: {}", prescriptionId);
        return mapToDTO(saved);
    }

    public PrescriptionDTO getPrescriptionById(String prescriptionId) {
        Prescription prescription = prescriptionRepository.findByPrescriptionId(prescriptionId)
                .orElseThrow(() -> new RuntimeException("Prescription not found"));
        return mapToDTO(prescription);
    }

    public List<PrescriptionDTO> getPatientPrescriptions(String pinNumber) {
        List<Prescription> prescriptions = prescriptionRepository.findByPinNumberOrderByPrescriptionDateDesc(pinNumber);
        return prescriptions.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private PrescriptionDTO mapToDTO(Prescription prescription) {
        PrescriptionDTO dto = modelMapper.map(prescription, PrescriptionDTO.class);
        List<PrescriptionItemDTO> items = prescription.getItems().stream()
                .map(i -> modelMapper.map(i, PrescriptionItemDTO.class))
                .collect(Collectors.toList());
        dto.setItems(items);
        return dto;
    }

    private String generatePrescriptionId() {
        List<String> ids = prescriptionRepository.findTopByOrderByIdDesc();
        String lastId = ids.isEmpty() ? null : ids.get(0);
        int nextNum = 1;
        if (lastId != null) {
            try {
                nextNum = Integer.parseInt(lastId.substring(2)) + 1;
            } catch (Exception e) {
                System.out.println("Check Exception = " + e);
            }
        }
        return String.format("RX%010d", nextNum);
    }

    public PrescriptionDTO getByConsultationId(String consultationId) {

        Prescription prescription = prescriptionRepository
                .findByConsultationId(consultationId)
                .orElseThrow(()
                        -> new RuntimeException("Prescription not found for consultation: " + consultationId));

        return mapToDTO(prescription);
    }

    public void deletePrescription(String prescriptionId) {

        Prescription prescription = prescriptionRepository
                .findByPrescriptionId(prescriptionId)
                .orElseThrow(()
                        -> new RuntimeException("Prescription not found: " + prescriptionId));

        prescriptionRepository.delete(prescription);

        log.info("Prescription deleted: {}", prescriptionId);
    }

    public PrescriptionDTO updatePrescription(String prescriptionId,
            CreatePrescriptionDTO dto) {

        log.info("Updating prescription {}", prescriptionId);

        Prescription prescription = prescriptionRepository
                .findByPrescriptionId(prescriptionId)
                .orElseThrow(()
                        -> new RuntimeException("Prescription not found: " + prescriptionId));

        // ✅ Update basic fields
        prescription.setValidityDays(dto.getValidityDays());
        prescription.setInstructions(dto.getInstructions());
        prescription.setDoctorId(dto.getDoctorId());

        // ✅ REMOVE OLD ITEMS (IMPORTANT)
        itemRepository.deleteAll(prescription.getItems());
        prescription.getItems().clear();

        // ✅ ADD NEW ITEMS
        for (PrescriptionItemDTO itemDTO : dto.getItems()) {
            PrescriptionItem item = modelMapper.map(itemDTO, PrescriptionItem.class);
            prescription.addItem(item);
        }

        Prescription saved = prescriptionRepository.save(prescription);

        log.info("Prescription updated: {}", prescriptionId);

        return mapToDTO(saved);
    }

}
