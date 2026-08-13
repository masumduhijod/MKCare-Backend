package com.hospital.opd.service;

import com.hospital.opd.entity.Medicine;
import com.hospital.opd.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicineService {

    private final MedicineRepository medicineRepository;

    public Medicine createMedicine(Medicine medicine) {
        log.info("Creating medicine: {}", medicine.getMedicineName());
        return medicineRepository.save(medicine);
    }

    @Transactional
    public List<Medicine> createMedicines(List<Medicine> medicines) {
        log.info("Bulk creating {} medicines", medicines.size());
        return medicineRepository.saveAll(medicines);
    }

    public Medicine updateMedicine(Long id, Medicine medicineDetails) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicine not found with id: " + id));

        medicine.setMedicineName(medicineDetails.getMedicineName());
        medicine.setGenericName(medicineDetails.getGenericName());
        medicine.setBrandName(medicineDetails.getBrandName());
        medicine.setComposition(medicineDetails.getComposition());
        medicine.setMedicineType(medicineDetails.getMedicineType());
        medicine.setStrength(medicineDetails.getStrength());
        medicine.setUnit(medicineDetails.getUnit());
        medicine.setPackaging(medicineDetails.getPackaging());
        medicine.setManufacturer(medicineDetails.getManufacturer());
        medicine.setDescription(medicineDetails.getDescription());
        medicine.setIsActive(medicineDetails.getIsActive());

        return medicineRepository.save(medicine);
    }

    public Medicine getMedicineById(Long id) {
        return medicineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicine not found with id: " + id));
    }

    public Page<Medicine> getAllMedicines(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return medicineRepository.findAll(pageable);
    }

    public Page<Medicine> searchMedicines(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("medicineName").ascending());
        if (query == null || query.isEmpty()) {
            return medicineRepository.findAllByIsActiveTrue(pageable);
        }
        return medicineRepository.searchMedicines(query, pageable);
    }

    public List<Medicine> searchMedicinesList(String query) {
        return medicineRepository.searchMedicinesList(query);
    }

    @Transactional
    public void deleteMedicine(Long id) {
        Medicine medicine = getMedicineById(id);
        medicine.setIsActive(false); // Soft delete
        medicineRepository.save(medicine);
    }
}
