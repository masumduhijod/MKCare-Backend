package com.hospital.opd.controller;

import com.hospital.opd.entity.Medicine;
import com.hospital.opd.service.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/opd/medicines")
@RequiredArgsConstructor
public class MedicineController {

    private final MedicineService medicineService;

    @PostMapping("/create")
    public ResponseEntity<Medicine> createMedicine(@RequestBody Medicine medicine) {
        return ResponseEntity.ok(medicineService.createMedicine(medicine));
    }

    @PostMapping("/bulk-create")
    public ResponseEntity<?> createMedicines(@RequestBody List<Medicine> medicines) {
        try {
            System.out.println("Received bulk request for " + (medicines != null ? medicines.size() : 0) + " medicines");
            return ResponseEntity.ok(medicineService.createMedicines(medicines));
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Bulk insert failed: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Medicine> updateMedicine(@PathVariable Long id, @RequestBody Medicine medicine) {
        return ResponseEntity.ok(medicineService.updateMedicine(id, medicine));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Medicine> getMedicine(@PathVariable Long id) {
        return ResponseEntity.ok(medicineService.getMedicineById(id));
    }

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getAllMedicines(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "medicineName") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        
        Page<Medicine> medicinePage = medicineService.getAllMedicines(page, size, sortBy, direction);
        
        Map<String, Object> response = new HashMap<>();
        response.put("data", medicinePage.getContent());
        response.put("currentPage", medicinePage.getNumber());
        response.put("totalItems", medicinePage.getTotalElements());
        response.put("totalPages", medicinePage.getTotalPages());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchMedicines(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<Medicine> medicinePage = medicineService.searchMedicines(query, page, size);
        
        Map<String, Object> response = new HashMap<>();
        response.put("data", medicinePage.getContent());
        response.put("currentPage", medicinePage.getNumber());
        response.put("totalItems", medicinePage.getTotalElements());
        response.put("totalPages", medicinePage.getTotalPages());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search-list")
    public ResponseEntity<List<Medicine>> searchMedicinesList(@RequestParam String query) {
        return ResponseEntity.ok(medicineService.searchMedicinesList(query));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteMedicine(@PathVariable Long id) {
        medicineService.deleteMedicine(id);
        return ResponseEntity.ok().build();
    }
}
