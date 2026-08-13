package com.hospital.report.service;

import com.hospital.report.client.*;
import com.hospital.report.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final PatientServiceClient patientClient;
    private final CvrServiceClient cvrClient;
    private final AppointmentServiceClient appointmentClient;
    private final DoctorServiceClient doctorClient;
    private final BillingServiceClient billingClient;
    private final OpdServiceClient opdClient;

    // =====================================================================
    // 1. PATIENT REGISTRATION REPORT
    // =====================================================================
    public ReportResponse<List<PatientDTO>> getPatientRegistrationReport(
            String fromDate, String toDate, String status) {
        try {
            List<PatientDTO> patients = patientClient.getAllActivePatients().getData();
            if (patients == null) {
                patients = Collections.emptyList();
            }

            // Filter by status if provided
            if (status != null && !status.isEmpty()) {
                String s = status.toUpperCase();
                patients = patients.stream()
                        .filter(p -> s.equals(p.getStatus()))
                        .collect(Collectors.toList());
            }

            // Filter by registration date range if provided
            if (fromDate != null && toDate != null) {
                patients = patients.stream()
                        .filter(p -> p.getRegistrationDate() != null
                        && p.getRegistrationDate().toString().compareTo(fromDate) >= 0
                        && p.getRegistrationDate().toString().compareTo(toDate) <= 0)
                        .collect(Collectors.toList());
            }

            Map<String, Object> summary = new LinkedHashMap<>();
            summary.put("totalPatients", patients.size());
            summary.put("maleCount", patients.stream().filter(p -> "MALE".equalsIgnoreCase(p.getGender())).count());
            summary.put("femaleCount", patients.stream().filter(p -> "FEMALE".equalsIgnoreCase(p.getGender())).count());
            summary.put("activeCount", patients.stream().filter(p -> "ACTIVE".equalsIgnoreCase(p.getStatus())).count());
            summary.put("fromDate", fromDate);
            summary.put("toDate", toDate);

            return ReportResponse.success("Patient Registration Report", patients, summary);
        } catch (Exception e) {
            log.error("Error generating patient registration report", e);
            throw new RuntimeException("Failed to generate Patient Registration Report: " + e.getMessage());
        }
    }

    // =====================================================================
    // 2. PATIENT DEMOGRAPHICS REPORT
    // =====================================================================
    public ReportResponse<Map<String, Object>> getPatientDemographicsReport() {
        try {
            List<PatientDTO> patients = patientClient.getAllActivePatients().getData();
            if (patients == null) {
                patients = Collections.emptyList();
            }

            Map<String, Object> demographics = new LinkedHashMap<>();

            // Gender distribution
            Map<String, Long> genderDist = patients.stream()
                    .collect(Collectors.groupingBy(
                            p -> p.getGender() != null ? p.getGender() : "UNKNOWN",
                            Collectors.counting()));
            demographics.put("genderDistribution", genderDist);

            // Blood group distribution
            Map<String, Long> bloodGroupDist = patients.stream()
                    .filter(p -> p.getBloodGroup() != null)
                    .collect(Collectors.groupingBy(PatientDTO::getBloodGroup, Collectors.counting()));
            demographics.put("bloodGroupDistribution", bloodGroupDist);

            // City distribution
            Map<String, Long> cityDist = patients.stream()
                    .filter(p -> p.getCity() != null)
                    .collect(Collectors.groupingBy(PatientDTO::getCity, Collectors.counting()));
            demographics.put("cityDistribution", cityDist);

            // Age group distribution
            Map<String, Long> ageGroupDist = new LinkedHashMap<>();
            ageGroupDist.put("0-17 (Children)",
                    patients.stream().filter(p -> p.getAge() != null && p.getAge() <= 17).count());
            ageGroupDist.put("18-30 (Young Adults)",
                    patients.stream().filter(p -> p.getAge() != null && p.getAge() >= 18 && p.getAge() <= 30).count());
            ageGroupDist.put("31-45 (Adults)",
                    patients.stream().filter(p -> p.getAge() != null && p.getAge() >= 31 && p.getAge() <= 45).count());
            ageGroupDist.put("46-60 (Middle Age)",
                    patients.stream().filter(p -> p.getAge() != null && p.getAge() >= 46 && p.getAge() <= 60).count());
            ageGroupDist.put("60+ (Senior)",
                    patients.stream().filter(p -> p.getAge() != null && p.getAge() > 60).count());
            demographics.put("ageGroupDistribution", ageGroupDist);

            // Insurance coverage
            long insuredPatients = patients.stream()
                    .filter(p -> p.getInsuranceProvider() != null && !p.getInsuranceProvider().isEmpty()).count();
            demographics.put("totalPatients", patients.size());
            demographics.put("insuredPatients", insuredPatients);
            demographics.put("nonInsuredPatients", patients.size() - insuredPatients);

            // State distribution
            Map<String, Long> stateDist = patients.stream()
                    .filter(p -> p.getState() != null)
                    .collect(Collectors.groupingBy(PatientDTO::getState, Collectors.counting()));
            demographics.put("stateDistribution", stateDist);

            return ReportResponse.success("Patient Demographics Report", demographics);
        } catch (Exception e) {
            log.error("Error generating demographics report", e);
            throw new RuntimeException("Failed to generate Patient Demographics Report: " + e.getMessage());
        }
    }

    // =====================================================================
    // 3. PATIENT VISIT HISTORY REPORT (PIN required)
    // =====================================================================
    public ReportResponse<PatientVisitHistoryDTO> getPatientVisitHistoryReport(String pinNumber) {
        try {
            PatientVisitHistoryDTO history = cvrClient.getPatientHistory(pinNumber).getData();
            PatientDTO patient = patientClient.getPatientByPIN(pinNumber).getData();

            Map<String, Object> summary = new LinkedHashMap<>();
            summary.put("pinNumber", pinNumber);
            if (patient != null) {
                summary.put("patientName", patient.getFullName());
                summary.put("gender", patient.getGender());
                summary.put("age", patient.getAge());
                summary.put("bloodGroup", patient.getBloodGroup());
                summary.put("contact", patient.getContactNumber());
            }
            if (history != null) {
                summary.put("totalVisits", history.getTotalVisits());
                summary.put("lastVisitDate", history.getLastVisitDate());
                summary.put("lastCvrNumber", history.getLastCvrNumber());
            }

            return ReportResponse.success("Patient Visit History Report", history, summary);
        } catch (Exception e) {
            log.error("Error generating patient visit history report", e);
            throw new RuntimeException("Failed to generate Patient Visit History Report: " + e.getMessage());
        }
    }

    // =====================================================================
    // 4. OPD DAILY REPORT
    // =====================================================================
    public ReportResponse<OpdDailyReportDTO> getOpdDailyReport(String date) {
        try {
            List<CvrSummaryDTO> cvrList = cvrClient.getCVRsByDate(date).getData();
            if (cvrList == null) {
                cvrList = Collections.emptyList();
            }

            Map<String, Long> departmentWise = cvrList.stream()
                    .collect(Collectors.groupingBy(
                            c -> c.getDepartment() != null ? c.getDepartment() : "GENERAL",
                            Collectors.counting()));

            Map<String, Long> statusWise = cvrList.stream()
                    .collect(Collectors.groupingBy(
                            c -> c.getStatus() != null ? c.getStatus() : "UNKNOWN",
                            Collectors.counting()));

            OpdDailyReportDTO report = OpdDailyReportDTO.builder()
                    .reportDate(date != null ? java.time.LocalDate.parse(date) : java.time.LocalDate.now())
                    .totalVisits(cvrList.size())
                    .totalPatients((int) cvrList.stream().map(CvrSummaryDTO::getPinNumber).distinct().count())
                    .pendingCVRs((int) cvrList.stream()
                            .filter(c -> "PENDING".equalsIgnoreCase(c.getStatus())
                            || "REGISTERED".equalsIgnoreCase(c.getStatus()))
                            .count())
                    .completedCVRs(
                            (int) cvrList.stream().filter(c -> "COMPLETED".equalsIgnoreCase(c.getStatus())).count())
                    .cancelledCVRs(
                            (int) cvrList.stream().filter(c -> "CANCELLED".equalsIgnoreCase(c.getStatus())).count())
                    .departmentWiseCount(departmentWise)
                    .statusWiseCount(statusWise)
                    .cvrList(cvrList)
                    .build();

            return ReportResponse.success("OPD Daily Report", report);
        } catch (Exception e) {
            log.error("Error generating OPD daily report", e);
            throw new RuntimeException("Failed to generate OPD Daily Report: " + e.getMessage());
        }
    }

    // =====================================================================
    // 5. OPD DEPARTMENT-WISE REPORT
    // =====================================================================
    public ReportResponse<Map<String, Object>> getOpdDepartmentWiseReport(String fromDate, String toDate) {
        try {
            // Get appointments in range for department data
            List<AppointmentSummaryDTO> appointments = appointmentClient.getAppointmentsByDateRange(fromDate, toDate)
                    .getData();
            if (appointments == null) {
                appointments = Collections.emptyList();
            }

            Map<String, List<AppointmentSummaryDTO>> byDept = appointments.stream()
                    .collect(Collectors.groupingBy(
                            a -> a.getDepartment() != null ? a.getDepartment() : "GENERAL"));

            Map<String, Object> deptReport = new LinkedHashMap<>();
            for (Map.Entry<String, List<AppointmentSummaryDTO>> entry : byDept.entrySet()) {
                Map<String, Object> deptData = new LinkedHashMap<>();
                List<AppointmentSummaryDTO> deptAppts = entry.getValue();
                deptData.put("totalAppointments", deptAppts.size());
                deptData.put("completed",
                        deptAppts.stream().filter(a -> "COMPLETED".equalsIgnoreCase(a.getStatus())).count());
                deptData.put("cancelled",
                        deptAppts.stream().filter(a -> "CANCELLED".equalsIgnoreCase(a.getStatus())).count());
                deptData.put("pending", deptAppts.stream().filter(
                        a -> "SCHEDULED".equalsIgnoreCase(a.getStatus()) || "CONFIRMED".equalsIgnoreCase(a.getStatus()))
                        .count());
                deptData.put("doctors", deptAppts.stream().map(AppointmentSummaryDTO::getDoctorName).distinct()
                        .collect(Collectors.toList()));
                deptReport.put(entry.getKey(), deptData);
            }

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("fromDate", fromDate);
            result.put("toDate", toDate);
            result.put("totalAppointments", appointments.size());
            result.put("departmentWiseData", deptReport);

            return ReportResponse.success("OPD Department-wise Report", result);
        } catch (Exception e) {
            log.error("Error generating OPD department-wise report", e);
            throw new RuntimeException("Failed to generate OPD Department-wise Report: " + e.getMessage());
        }
    }

    // =====================================================================
    // 6. OPD REVENUE REPORT
    // =====================================================================
    public ReportResponse<RevenueReportDTO> getOpdRevenueReport(String fromDate, String toDate) {
        try {
            List<AppointmentSummaryDTO> appointments = appointmentClient.getAppointmentsByDateRange(fromDate, toDate)
                    .getData();
            if (appointments == null) {
                appointments = Collections.emptyList();
            }

            // Collect all invoices for these appointments
            List<InvoiceDTO> allInvoices = new ArrayList<>();
            Set<String> processedPins = new HashSet<>();

            for (AppointmentSummaryDTO appt : appointments) {
                if (appt.getPinNumber() != null && !processedPins.contains(appt.getPinNumber())) {
                    try {
                        List<InvoiceDTO> pinInvoices = billingClient.getPatientInvoices(appt.getPinNumber()).getData();
                        if (pinInvoices != null) {
                            // Filter invoices within date range
                            List<InvoiceDTO> filtered = pinInvoices.stream()
                                    .filter(inv -> inv.getInvoiceDate() != null
                                    && inv.getInvoiceDate().toString().compareTo(fromDate) >= 0
                                    && inv.getInvoiceDate().toString().compareTo(toDate) <= 0)
                                    .collect(Collectors.toList());
                            allInvoices.addAll(filtered);
                        }
                        processedPins.add(appt.getPinNumber());
                    } catch (Exception ex) {
                        log.warn("Could not fetch invoices for PIN: {}", appt.getPinNumber());
                    }
                }
            }

            return buildRevenueReport(fromDate, toDate, allInvoices);
        } catch (Exception e) {
            log.error("Error generating OPD revenue report", e);
            throw new RuntimeException("Failed to generate OPD Revenue Report: " + e.getMessage());
        }
    }

    // =====================================================================
    // 7. APPOINTMENT SCHEDULE REPORT
    // =====================================================================
    public ReportResponse<List<AppointmentSummaryDTO>> getAppointmentScheduleReport(
            String fromDate, String toDate, String doctorId, String status) {
        try {
            List<AppointmentSummaryDTO> appointments;

            if (doctorId != null && !doctorId.isEmpty() && fromDate != null) {
                // Doctor specific for a date
                appointments = appointmentClient.getDoctorAppointments(doctorId, fromDate).getData();
            } else {
                appointments = appointmentClient.getAppointmentsByDateRange(fromDate, toDate).getData();
            }

            if (appointments == null) {
                appointments = Collections.emptyList();
            }

            if (status != null && !status.isEmpty()) {
                String s = status.toUpperCase();
                appointments = appointments.stream()
                        .filter(a -> s.equals(a.getStatus()))
                        .collect(Collectors.toList());
            }

            if (doctorId != null && !doctorId.isEmpty()) {
                appointments = appointments.stream()
                        .filter(a -> doctorId.equals(a.getDoctorId()))
                        .collect(Collectors.toList());
            }

            Map<String, Object> summary = new LinkedHashMap<>();
            summary.put("totalAppointments", appointments.size());
            summary.put("fromDate", fromDate);
            summary.put("toDate", toDate);
            summary.put("doctorId", doctorId);
            Map<String, Long> statusCount = appointments.stream()
                    .collect(Collectors.groupingBy(a -> a.getStatus() != null ? a.getStatus() : "UNKNOWN",
                            Collectors.counting()));
            summary.put("statusWiseCount", statusCount);

            return ReportResponse.success("Appointment Schedule Report", appointments, summary);
        } catch (Exception e) {
            log.error("Error generating appointment schedule report", e);
            throw new RuntimeException("Failed to generate Appointment Schedule Report: " + e.getMessage());
        }
    }

    // =====================================================================
    // 8. APPOINTMENT STATUS REPORT
    // =====================================================================
    public ReportResponse<Map<String, Object>> getAppointmentStatusReport(String fromDate, String toDate) {
        try {
            List<AppointmentSummaryDTO> appointments = appointmentClient.getAppointmentsByDateRange(fromDate, toDate)
                    .getData();
            if (appointments == null) {
                appointments = Collections.emptyList();
            }

            Map<String, List<AppointmentSummaryDTO>> byStatus = appointments.stream()
                    .collect(Collectors.groupingBy(
                            a -> a.getStatus() != null ? a.getStatus() : "UNKNOWN"));

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("fromDate", fromDate);
            result.put("toDate", toDate);
            result.put("totalAppointments", appointments.size());
            result.put("statusBreakdown", byStatus);

            Map<String, Long> statusCount = appointments.stream()
                    .collect(Collectors.groupingBy(a -> a.getStatus() != null ? a.getStatus() : "UNKNOWN",
                            Collectors.counting()));
            result.put("statusSummary", statusCount);

            return ReportResponse.success("Appointment Status Report", result);
        } catch (Exception e) {
            log.error("Error generating appointment status report", e);
            throw new RuntimeException("Failed to generate Appointment Status Report: " + e.getMessage());
        }
    }

    // =====================================================================
    // 9. DOCTOR AVAILABILITY REPORT
    // =====================================================================
    public ReportResponse<Map<String, Object>> getDoctorAvailabilityReport(String date) {
        try {
            List<DoctorDTO> doctors = doctorClient.getAllActiveDoctors().getData();
            if (doctors == null) {
                doctors = Collections.emptyList();
            }

            List<Map<String, Object>> availabilityList = new ArrayList<>();
            for (DoctorDTO doctor : doctors) {
                Map<String, Object> docInfo = new LinkedHashMap<>();
                docInfo.put("doctorId", doctor.getDoctorId());
                docInfo.put("doctorName", doctor.getFullName());
                docInfo.put("specialization", doctor.getSpecialization());
                docInfo.put("department", doctor.getDepartment());
                docInfo.put("status", doctor.getStatus());
                docInfo.put("availableForOPD", doctor.getAvailableForOPD());
                docInfo.put("availableForEmergency", doctor.getAvailableForEmergency());
                docInfo.put("roomNumber", doctor.getRoomNumber());
                docInfo.put("consultationFee", doctor.getConsultationFee());

                if (date != null) {
                    try {
                        DoctorScheduleDTO schedule = doctorClient.getScheduleByDate(doctor.getDoctorId(), date)
                                .getData();
                        docInfo.put("scheduleForDate", schedule);
                    } catch (Exception ex) {
                        docInfo.put("scheduleForDate", null);
                    }
                }
                availabilityList.add(docInfo);
            }

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("date", date);
            result.put("totalDoctors", doctors.size());
            result.put("opdAvailable",
                    doctors.stream().filter(d -> Boolean.TRUE.equals(d.getAvailableForOPD())).count());
            result.put("emergencyAvailable",
                    doctors.stream().filter(d -> Boolean.TRUE.equals(d.getAvailableForEmergency())).count());
            result.put("doctorAvailability", availabilityList);

            return ReportResponse.success("Doctor Availability Report", result);
        } catch (Exception e) {
            log.error("Error generating doctor availability report", e);
            throw new RuntimeException("Failed to generate Doctor Availability Report: " + e.getMessage());
        }
    }

    // =====================================================================
    // 10. CVR SUMMARY REPORT
    // =====================================================================
    public ReportResponse<Map<String, Object>> getCvrSummaryReport(
            String fromDate, String toDate, String cvrNumber, String pinNumber, String doctorId) {
        try {
            Map<String, Object> result = new LinkedHashMap<>();

            // Single CVR lookup
            if (cvrNumber != null && !cvrNumber.isEmpty()) {
                CvrDTO cvr = cvrClient.getCVRByNumber(cvrNumber).getData();
                result.put("cvr", cvr);
                result.put("type", "SINGLE_CVR");
                return ReportResponse.success("CVR Summary Report", result);
            }

            // Patient-wise CVR history
            if (pinNumber != null && !pinNumber.isEmpty()) {
                PatientVisitHistoryDTO history = cvrClient.getPatientHistory(pinNumber).getData();
                result.put("patientHistory", history);
                result.put("pinNumber", pinNumber);
                result.put("type", "PATIENT_CVR_HISTORY");
                return ReportResponse.success("CVR Summary Report", result);
            }

            // Doctor + date CVRs
            if (doctorId != null && !doctorId.isEmpty() && fromDate != null) {
                List<CvrSummaryDTO> cvrList = cvrClient.getCVRsByDoctorAndDate(doctorId, fromDate).getData();
                if (cvrList == null) {
                    cvrList = Collections.emptyList();
                }

                Map<String, Long> statusWise = cvrList.stream()
                        .collect(Collectors.groupingBy(c -> c.getStatus() != null ? c.getStatus() : "UNKNOWN",
                                Collectors.counting()));

                result.put("doctorId", doctorId);
                result.put("date", fromDate);
                result.put("totalCVRs", cvrList.size());
                result.put("statusWise", statusWise);
                result.put("cvrList", cvrList);
                result.put("type", "DOCTOR_DATE_CVR");
                return ReportResponse.success("CVR Summary Report", result);
            }

            // Date-range CVRs
            if (fromDate != null) {
                List<CvrSummaryDTO> cvrList = new ArrayList<>();

                if (toDate != null && !toDate.equals(fromDate)) {
                    // Fetch CVRs for each date in range
                    java.time.LocalDate start = java.time.LocalDate.parse(fromDate);
                    java.time.LocalDate end = java.time.LocalDate.parse(toDate);
                    for (java.time.LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
                        try {
                            List<CvrSummaryDTO> dayList = cvrClient.getCVRsByDate(d.toString()).getData();
                            if (dayList != null) {
                                cvrList.addAll(dayList);
                            }
                        } catch (Exception ex) {
                            log.warn("Could not fetch CVRs for date: {}", d);
                        }
                    }
                } else {
                    List<CvrSummaryDTO> dayList = cvrClient.getCVRsByDate(fromDate).getData();
                    if (dayList != null) {
                        cvrList = dayList;
                    }
                }

                Map<String, Long> statusWise = cvrList.stream()
                        .collect(Collectors.groupingBy(c -> c.getStatus() != null ? c.getStatus() : "UNKNOWN",
                                Collectors.counting()));
                Map<String, Long> deptWise = cvrList.stream()
                        .collect(Collectors.groupingBy(c -> c.getDepartment() != null ? c.getDepartment() : "GENERAL",
                                Collectors.counting()));

                result.put("fromDate", fromDate);
                result.put("toDate", toDate != null ? toDate : fromDate);
                result.put("totalCVRs", cvrList.size());
                result.put("statusWise", statusWise);
                result.put("departmentWise", deptWise);
                result.put("cvrList", cvrList);
                result.put("type", "DATE_RANGE_CVR_SUMMARY");
                return ReportResponse.success("CVR Summary Report", result);
            }

            // Default: today
            List<CvrSummaryDTO> todayCvrs = cvrClient.getTodaysCVRs().getData();
            if (todayCvrs == null) {
                todayCvrs = Collections.emptyList();
            }
            result.put("date", "TODAY");
            result.put("totalCVRs", todayCvrs.size());
            result.put("cvrList", todayCvrs);

            return ReportResponse.success("CVR Summary Report", result);
        } catch (Exception e) {
            log.error("Error generating CVR summary report", e);
            throw new RuntimeException("Failed to generate CVR Summary Report: " + e.getMessage());
        }
    }

    // =====================================================================
    // 11. PRESCRIPTION REPORT
    // =====================================================================
    public ReportResponse<Map<String, Object>> getPrescriptionReport(
            String pinNumber, String prescriptionId, String doctorId, String date) {
        try {
            Map<String, Object> result = new LinkedHashMap<>();

            // Single prescription
            if (prescriptionId != null && !prescriptionId.isEmpty()) {
                PrescriptionDTO prescription = opdClient.getPrescription(prescriptionId).getData();
                result.put("prescription", prescription);
                result.put("type", "SINGLE_PRESCRIPTION");
                return ReportResponse.success("Prescription Report", result);
            }

            // Patient prescriptions
            if (pinNumber != null && !pinNumber.isEmpty()) {
                List<PrescriptionDTO> prescriptions = opdClient.getPatientPrescriptions(pinNumber).getData();
                if (prescriptions == null) {
                    prescriptions = Collections.emptyList();
                }

                result.put("pinNumber", pinNumber);
                result.put("totalPrescriptions", prescriptions.size());
                result.put("activePrescriptions", prescriptions.stream()
                        .filter(p -> "ACTIVE".equalsIgnoreCase(p.getStatus())).count());
                result.put("prescriptions", prescriptions);
                result.put("type", "PATIENT_PRESCRIPTIONS");
                return ReportResponse.success("Prescription Report", result);
            }

            // Doctor + date prescriptions (via consultations)
            if (doctorId != null && !doctorId.isEmpty() && date != null) {
                List<ConsultationDTO> consultations = opdClient.getConsultationsByDoctorAndDate(doctorId, date)
                        .getData();
                if (consultations == null) {
                    consultations = Collections.emptyList();
                }

                List<PrescriptionDTO> prescriptions = new ArrayList<>();
                for (ConsultationDTO c : consultations) {
                    try {
                        PrescriptionDTO rx = opdClient.getPrescriptionByConsultation(c.getConsultationId()).getData();
                        if (rx != null) {
                            prescriptions.add(rx);
                        }
                    } catch (Exception ex) {
                        // No prescription for this consultation
                    }
                }

                result.put("doctorId", doctorId);
                result.put("date", date);
                result.put("totalConsultations", consultations.size());
                result.put("totalPrescriptions", prescriptions.size());
                result.put("prescriptions", prescriptions);
                result.put("type", "DOCTOR_DATE_PRESCRIPTIONS");
                return ReportResponse.success("Prescription Report", result);
            }

            // âœ… CORRECT LOGIC
            if (date != null && (doctorId == null || doctorId.isEmpty())) {

                List<PrescriptionDTO> prescriptions
                        = opdClient.getPrescriptionsByDate(date).getData();

                if (prescriptions == null) {
                    prescriptions = Collections.emptyList();
                }

                // ðŸ”¥ DEBUG (optional but useful)
                log.info("TOTAL PRESCRIPTIONS FROM OPD: {}", prescriptions.size());

                result.put("date", date);
                result.put("totalPrescriptions", prescriptions.size());
                result.put("prescriptions", prescriptions);
                result.put("type", "DATE_PRESCRIPTIONS");
                System.out.println("ðŸ‘‰ FROM OPD SIZE = " + prescriptions.size());
                return ReportResponse.success("Prescription Report", result);
            }
            return ReportResponse.success("Prescription Report", result);
        } catch (Exception e) {
            log.error("Error generating prescription report", e);
            throw new RuntimeException("Failed to generate Prescription Report: " + e.getMessage());
        }
    }

    // =====================================================================
    // 12. INVOICE SUMMARY REPORT
    // =====================================================================
//    public ReportResponse<Map<String, Object>> getInvoiceSummaryReport(
//            String pinNumber, String invoiceNumber, String fromDate, String toDate) {
//        try {
//            Map<String, Object> result = new LinkedHashMap<>();
//
//            // Single invoice
//            if (invoiceNumber != null && !invoiceNumber.isEmpty()) {
//                InvoiceDTO invoice = billingClient.getInvoice(invoiceNumber).getData();
//                if (invoice != null) {
//                    List<PaymentDTO> payments = billingClient.getInvoicePayments(invoiceNumber).getData();
//                    result.put("invoice", invoice);
//                    result.put("payments", payments);
//                }
//                result.put("type", "SINGLE_INVOICE");
//                return ReportResponse.success("Invoice Summary Report", result);
//            }
//
//            // Patient invoices
//            if (pinNumber != null && !pinNumber.isEmpty()) {
//                List<InvoiceDTO> invoices = billingClient.getPatientInvoices(pinNumber).getData();
//                if (invoices == null) {
//                    invoices = Collections.emptyList();
//                }
//
//                BigDecimal totalAmount = invoices.stream()
//                        .map(InvoiceDTO::getTotalAmount)
//                        .filter(Objects::nonNull)
//                        .reduce(BigDecimal.ZERO, BigDecimal::add);
//                BigDecimal paidAmount = invoices.stream()
//                        .map(InvoiceDTO::getPaidAmount)
//                        .filter(Objects::nonNull)
//                        .reduce(BigDecimal.ZERO, BigDecimal::add);
//                BigDecimal outstanding = invoices.stream()
//                        .map(InvoiceDTO::getOutstandingAmount)
//                        .filter(Objects::nonNull)
//                        .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//                result.put("pinNumber", pinNumber);
//                result.put("totalInvoices", invoices.size());
//                result.put("totalAmount", totalAmount);
//                result.put("paidAmount", paidAmount);
//                result.put("outstandingAmount", outstanding);
//                result.put("invoices", invoices);
//                result.put("type", "PATIENT_INVOICES");
//                return ReportResponse.success("Invoice Summary Report", result);
//            }
//
//            return ReportResponse.success("Invoice Summary Report", result);
//        } catch (Exception e) {
//            log.error("Error generating invoice summary report", e);
//            throw new RuntimeException("Failed to generate Invoice Summary Report: " + e.getMessage());
//        }
//    }
    public ReportResponse<Map<String, Object>> getInvoiceSummaryReport(
        String pinNumber, String invoiceNumber, String fromDate, String toDate) {
    try {
        Map<String, Object> result = new LinkedHashMap<>();

        // Single invoice
        if (invoiceNumber != null && !invoiceNumber.isEmpty()) {
            InvoiceDTO invoice = billingClient.getInvoice(invoiceNumber).getData();
            if (invoice != null) {
                List<PaymentDTO> payments = billingClient.getInvoicePayments(invoiceNumber).getData();
                result.put("invoice", invoice);
                result.put("payments", payments);
            }
            result.put("type", "SINGLE_INVOICE");
            return ReportResponse.success("Invoice Summary Report", result);
        }

        // Patient invoices
        if (pinNumber != null && !pinNumber.isEmpty()) {
            List<InvoiceDTO> invoices = billingClient.getPatientInvoices(pinNumber).getData();
            if (invoices == null) {
                invoices = Collections.emptyList();
            }

            BigDecimal totalAmount = invoices.stream()
                    .map(InvoiceDTO::getTotalAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal paidAmount = invoices.stream()
                    .map(InvoiceDTO::getPaidAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal outstanding = invoices.stream()
                    .map(InvoiceDTO::getOutstandingAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            result.put("pinNumber", pinNumber);
            result.put("totalInvoices", invoices.size());
            result.put("totalAmount", totalAmount);
            result.put("paidAmount", paidAmount);
            result.put("outstandingAmount", outstanding);
            result.put("invoices", invoices);
            result.put("type", "PATIENT_INVOICES");
            return ReportResponse.success("Invoice Summary Report", result);
        }

        // âœ… DATE RANGE SEARCH - ADD THIS
        if (fromDate != null && toDate != null) {
            List<InvoiceDTO> allInvoices = new ArrayList<>();
            
            // Get all patients
            List<PatientDTO> patients = patientClient.getAllActivePatients().getData();
            if (patients != null) {
                for (PatientDTO patient : patients) {
                    try {
                        List<InvoiceDTO> patientInvoices = billingClient
                                .getPatientInvoices(patient.getPinNumber())
                                .getData();
                        if (patientInvoices != null) {
                            for (InvoiceDTO inv : patientInvoices) {
                                if (inv.getInvoiceDate() != null) {
                                    String invDate = inv.getInvoiceDate().toString();
                                    if (invDate.compareTo(fromDate) >= 0 && invDate.compareTo(toDate) <= 0) {
                                        allInvoices.add(inv);
                                    }
                                }
                            }
                        }
                    } catch (Exception ex) {
                        // Skip
                    }
                }
            }

            BigDecimal totalAmount = allInvoices.stream()
                    .map(InvoiceDTO::getTotalAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal paidAmount = allInvoices.stream()
                    .map(InvoiceDTO::getPaidAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal outstandingAmount = allInvoices.stream()
                    .map(InvoiceDTO::getOutstandingAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            result.put("fromDate", fromDate);
            result.put("toDate", toDate);
            result.put("totalInvoices", allInvoices.size());
            result.put("totalAmount", totalAmount);
            result.put("paidAmount", paidAmount);
            result.put("outstandingAmount", outstandingAmount);
            result.put("invoices", allInvoices);
            result.put("type", "DATE_RANGE_INVOICES");
            
            return ReportResponse.success("Invoice Summary Report", result);
        }

        return ReportResponse.success("Invoice Summary Report", result);
    } catch (Exception e) {
        log.error("Error generating invoice summary report", e);
        throw new RuntimeException("Failed to generate Invoice Summary Report: " + e.getMessage());
    }
}
    // =====================================================================
    // 13. PAYMENT COLLECTION REPORT
    // =====================================================================
    public ReportResponse<Map<String, Object>> getPaymentCollectionReport(
            String fromDate, String toDate, String doctorId) {
        try {
            // â”€â”€â”€ DIRECT APPROACH: query payments by date range from BillingService â”€â”€â”€
            // This avoids the broken appointmentsâ†’invoices chain and is much more reliable.
            List<PaymentDTO> allPayments;
            try {
                ApiResponse<List<PaymentDTO>> paymentResponse = billingClient.getPaymentsByDateRange(fromDate, toDate,
                        doctorId);
                allPayments = (paymentResponse != null && paymentResponse.getData() != null)
                        ? paymentResponse.getData()
                        : Collections.emptyList();
            } catch (Exception ex) {
                log.warn("Direct payment-collection call failed, falling back to invoice scan: {}", ex.getMessage());
                allPayments = fallbackPaymentCollection(fromDate, toDate, doctorId);
            }

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("fromDate", fromDate);
            result.put("toDate", toDate);
            result.put("totalPayments", allPayments.size());
            result.put("totalCollected", allPayments.stream()
                    .map(PaymentDTO::getAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));

            // Payment mode wise breakdown
            Map<String, BigDecimal> paymentModeWise = new LinkedHashMap<>();
            for (PaymentDTO payment : allPayments) {
                String mode = payment.getPaymentMode() != null ? payment.getPaymentMode() : "UNKNOWN";
                paymentModeWise.merge(mode,
                        payment.getAmount() != null ? payment.getAmount() : BigDecimal.ZERO,
                        BigDecimal::add);
            }
            result.put("paymentModeWise", paymentModeWise);
            result.put("payments", allPayments);

            return ReportResponse.success("Payment Collection Report", result);
        } catch (Exception e) {
            log.error("Error generating payment collection report", e);
            throw new RuntimeException("Failed to generate Payment Collection Report: " + e.getMessage());
        }
    }

    /**
     * Fallback: scan invoices via patient PINs obtained from appointments. Used
     * only if the direct payment-collection endpoint is unavailable.
     */
    private List<PaymentDTO> fallbackPaymentCollection(String fromDate, String toDate, String doctorId) {
        try {
            List<InvoiceDTO> invoices;
            if (doctorId != null && !doctorId.isEmpty()) {
                invoices = billingClient.getInvoicesByDoctorAndDate(doctorId, fromDate).getData();
            } else {
                // Get appointments by date range to collect patient PINs
                ApiResponse<List<AppointmentSummaryDTO>> apptResponse = appointmentClient
                        .getAppointmentsByDateRange(fromDate, toDate);
                List<AppointmentSummaryDTO> appointments = (apptResponse != null && apptResponse.getData() != null)
                        ? apptResponse.getData()
                        : Collections.emptyList();
                invoices = new ArrayList<>();
                Set<String> processedPins = new HashSet<>();
                for (AppointmentSummaryDTO appt : appointments) {
                    if (appt.getPinNumber() != null && !processedPins.contains(appt.getPinNumber())) {
                        try {
                            List<InvoiceDTO> pinInvoices = billingClient.getPatientInvoices(appt.getPinNumber())
                                    .getData();
                            if (pinInvoices != null) {
                                invoices.addAll(pinInvoices);
                            }
                        } catch (Exception ex) {
                            log.warn("Fallback: cannot fetch invoices for PIN: {}", appt.getPinNumber());
                        }
                        processedPins.add(appt.getPinNumber());
                    }
                }
            }
            if (invoices == null) {
                invoices = Collections.emptyList();
            }

            List<PaymentDTO> payments = new ArrayList<>();
            for (InvoiceDTO invoice : invoices) {
                if (!"UNPAID".equalsIgnoreCase(invoice.getPaymentStatus())) {
                    try {
                        List<PaymentDTO> invPayments = billingClient.getInvoicePayments(invoice.getInvoiceNumber())
                                .getData();
                        if (invPayments != null) {
                            payments.addAll(invPayments);
                        }
                    } catch (Exception ex) {
                        log.warn("Fallback: cannot fetch payments for invoice: {}", invoice.getInvoiceNumber());
                    }
                }
            }
            return payments;
        } catch (Exception ex) {
            log.error("Fallback payment collection also failed", ex);
            return Collections.emptyList();
        }
    }

    // =====================================================================
    // 14. OUTSTANDING DUES REPORT
    // =====================================================================
    public ReportResponse<Map<String, Object>> getOutstandingDuesReport() {
        try {
            List<InvoiceDTO> pendingInvoices = billingClient.getPendingInvoices().getData();
            if (pendingInvoices == null) {
                pendingInvoices = Collections.emptyList();
            }

            BigDecimal totalOutstanding = pendingInvoices.stream()
                    .map(InvoiceDTO::getOutstandingAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, Long> statusBreakdown = pendingInvoices.stream()
                    .collect(Collectors.groupingBy(
                            i -> i.getPaymentStatus() != null ? i.getPaymentStatus() : "UNKNOWN",
                            Collectors.counting()));

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("totalPendingInvoices", pendingInvoices.size());
            result.put("totalOutstandingAmount", totalOutstanding);
            result.put("statusBreakdown", statusBreakdown);
            result.put("pendingInvoices", pendingInvoices);

            return ReportResponse.success("Outstanding Dues Report", result);
        } catch (Exception e) {
            log.error("Error generating outstanding dues report", e);
            throw new RuntimeException("Failed to generate Outstanding Dues Report: " + e.getMessage());
        }
    }

    // =====================================================================
    // 15. REVENUE ANALYSIS REPORT
    // =====================================================================
    public ReportResponse<RevenueReportDTO> getRevenueAnalysisReport(String fromDate, String toDate) {
        try {
            List<AppointmentSummaryDTO> appointments = appointmentClient.getAppointmentsByDateRange(fromDate, toDate)
                    .getData();
            List<InvoiceDTO> allInvoices = new ArrayList<>();

            if (appointments != null) {
                Set<String> processedPins = new HashSet<>();
                for (AppointmentSummaryDTO appt : appointments) {
                    if (appt.getPinNumber() != null && !processedPins.contains(appt.getPinNumber())) {
                        try {
                            List<InvoiceDTO> pinInvoices = billingClient.getPatientInvoices(appt.getPinNumber())
                                    .getData();
                            if (pinInvoices != null) {
                                List<InvoiceDTO> filtered = pinInvoices.stream()
                                        .filter(inv -> inv.getInvoiceDate() != null
                                        && inv.getInvoiceDate().toString().compareTo(fromDate) >= 0
                                        && inv.getInvoiceDate().toString().compareTo(toDate) <= 0)
                                        .collect(Collectors.toList());
                                allInvoices.addAll(filtered);
                            }
                            processedPins.add(appt.getPinNumber());
                        } catch (Exception ex) {
                            log.warn("Cannot fetch invoices for PIN: {}", appt.getPinNumber());
                        }
                    }
                }
            }

            return buildRevenueReport(fromDate, toDate, allInvoices);
        } catch (Exception e) {
            log.error("Error generating revenue analysis report", e);
            throw new RuntimeException("Failed to generate Revenue Analysis Report: " + e.getMessage());
        }
    }

    // =====================================================================
    // 16. DOCTOR CONSULTATION REPORT
    // =====================================================================
    public ReportResponse<DoctorConsultationReportDTO> getDoctorConsultationReport(
            String doctorId, String fromDate, String toDate) {
        try {
            DoctorDTO doctor = doctorClient.getDoctorById(doctorId).getData();

            List<ConsultationDTO> consultations = opdClient.getConsultationsByDoctorAndDate(doctorId, fromDate)
                    .getData();
            if (consultations == null) {
                consultations = Collections.emptyList();
            }

            List<AppointmentSummaryDTO> appointments = appointmentClient.getDoctorAppointments(doctorId, fromDate)
                    .getData();
            if (appointments == null) {
                appointments = Collections.emptyList();
            }

            // Revenue for this doctor
            List<InvoiceDTO> invoices = billingClient.getInvoicesByDoctorAndDate(doctorId, fromDate).getData();
            if (invoices == null) {
                invoices = Collections.emptyList();
            }

            BigDecimal revenue = invoices.stream()
                    .map(InvoiceDTO::getPaidAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            List<AppointmentDTO> apptDetails = appointments.stream()
                    .map(a -> {
                        AppointmentDTO dto = new AppointmentDTO();
                        dto.setAppointmentId(a.getAppointmentId());
                        dto.setPinNumber(a.getPinNumber());
                        dto.setPatientName(a.getPatientName());
                        dto.setDoctorId(a.getDoctorId());
                        dto.setDoctorName(a.getDoctorName());
                        dto.setAppointmentDate(a.getAppointmentDate());
                        dto.setStatus(a.getStatus());
                        dto.setAppointmentType(a.getAppointmentType());
                        dto.setCvrNumber(a.getCvrNumber());
                        return dto;
                    }).collect(Collectors.toList());

            DoctorConsultationReportDTO report = DoctorConsultationReportDTO.builder()
                    .doctorId(doctorId)
                    .doctorName(doctor != null ? doctor.getFullName() : doctorId)
                    .specialization(doctor != null ? doctor.getSpecialization() : null)
                    .department(doctor != null ? doctor.getDepartment() : null)
                    .fromDate(fromDate != null ? java.time.LocalDate.parse(fromDate) : null)
                    .toDate(toDate != null ? java.time.LocalDate.parse(toDate) : null)
                    .totalConsultations(consultations.size())
                    .completedConsultations((int) consultations.stream()
                            .filter(c -> "COMPLETED".equalsIgnoreCase(c.getStatus())).count())
                    .followUpRequired((int) consultations.stream()
                            .filter(c -> Boolean.TRUE.equals(c.getFollowUpRequired())).count())
                    .totalRevenue(revenue)
                    .consultations(consultations)
                    .appointments(apptDetails)
                    .build();

            return ReportResponse.success("Doctor Consultation Report", report);
        } catch (Exception e) {
            log.error("Error generating doctor consultation report", e);
            throw new RuntimeException("Failed to generate Doctor Consultation Report: " + e.getMessage());
        }
    }

    // =====================================================================
    // 17. DOCTOR SCHEDULE REPORT
    // =====================================================================
    public ReportResponse<Map<String, Object>> getDoctorScheduleReport(
            String doctorId, String fromDate, String toDate) {
        try {
            Map<String, Object> result = new LinkedHashMap<>();

            if (doctorId != null && !doctorId.isEmpty()) {
                DoctorDTO doctor = doctorClient.getDoctorById(doctorId).getData();
                List<DoctorScheduleDTO> schedules;

                if (fromDate != null && toDate != null) {
                    schedules = doctorClient.getSchedulesByDateRange(doctorId, fromDate, toDate).getData();
                } else {
                    schedules = doctorClient.getUpcomingSchedules(doctorId, 30).getData();
                }

                if (schedules == null) {
                    schedules = Collections.emptyList();
                }

                result.put("doctorId", doctorId);
                result.put("doctorName", doctor != null ? doctor.getFullName() : doctorId);
                result.put("specialization", doctor != null ? doctor.getSpecialization() : null);
                result.put("department", doctor != null ? doctor.getDepartment() : null);
                result.put("totalSchedules", schedules.size());
                result.put("activeSchedules",
                        schedules.stream().filter(s -> Boolean.TRUE.equals(s.getIsActive())).count());
                result.put("schedules", schedules);
            } else {
                // All doctors schedule
                List<DoctorDTO> doctors = doctorClient.getAllActiveDoctors().getData();
                if (doctors == null) {
                    doctors = Collections.emptyList();
                }

                List<Map<String, Object>> allSchedules = new ArrayList<>();
                for (DoctorDTO doctor : doctors) {
                    Map<String, Object> docSched = new LinkedHashMap<>();
                    docSched.put("doctorId", doctor.getDoctorId());
                    docSched.put("doctorName", doctor.getFullName());
                    docSched.put("specialization", doctor.getSpecialization());
                    docSched.put("department", doctor.getDepartment());
                    try {
                        List<DoctorScheduleDTO> schedules = doctorClient.getUpcomingSchedules(doctor.getDoctorId(), 7)
                                .getData();
                        docSched.put("upcomingSchedules", schedules);
                    } catch (Exception ex) {
                        docSched.put("upcomingSchedules", Collections.emptyList());
                    }
                    allSchedules.add(docSched);
                }
                result.put("totalDoctors", doctors.size());
                result.put("doctorSchedules", allSchedules);
            }

            result.put("fromDate", fromDate);
            result.put("toDate", toDate);

            return ReportResponse.success("Doctor Schedule Report", result);
        } catch (Exception e) {
            log.error("Error generating doctor schedule report", e);
            throw new RuntimeException("Failed to generate Doctor Schedule Report: " + e.getMessage());
        }
    }

    // =====================================================================
    // 18. PATIENT SEARCH REPORT (Extra - useful for admin)
    // =====================================================================
    public ReportResponse<Map<String, Object>> getPatientSearchReport(String query, String searchType) {
        try {
            List<PatientDTO> patients = patientClient.searchPatients(query, searchType).getData();
            if (patients == null) {
                patients = Collections.emptyList();
            }

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("query", query);
            result.put("searchType", searchType);
            result.put("totalResults", patients.size());
            result.put("patients", patients);

            return ReportResponse.success("Patient Search Report", result);
        } catch (Exception e) {
            log.error("Error generating patient search report", e);
            throw new RuntimeException("Failed to generate Patient Search Report: " + e.getMessage());
        }
    }

    // =====================================================================
    // HELPER: Build Revenue Report from invoices list
    // =====================================================================
    private ReportResponse<RevenueReportDTO> buildRevenueReport(String fromDate, String toDate,
            List<InvoiceDTO> allInvoices) {
        BigDecimal totalBilled = allInvoices.stream().map(InvoiceDTO::getTotalAmount).filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCollected = allInvoices.stream().map(InvoiceDTO::getPaidAmount).filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalOutstanding = allInvoices.stream().map(InvoiceDTO::getOutstandingAmount)
                .filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalDiscount = allInvoices.stream().map(InvoiceDTO::getDiscountAmount).filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalTax = allInvoices.stream().map(InvoiceDTO::getTaxAmount).filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> invoiceTypeWise = new LinkedHashMap<>();
        for (InvoiceDTO inv : allInvoices) {
            String type = inv.getInvoiceType() != null ? inv.getInvoiceType() : "OTHER";
            invoiceTypeWise.merge(type, inv.getTotalAmount() != null ? inv.getTotalAmount() : BigDecimal.ZERO,
                    BigDecimal::add);
        }

        RevenueReportDTO report = RevenueReportDTO.builder()
                .fromDate(fromDate != null ? java.time.LocalDate.parse(fromDate) : null)
                .toDate(toDate != null ? java.time.LocalDate.parse(toDate) : null)
                .totalBilled(totalBilled)
                .totalCollected(totalCollected)
                .totalOutstanding(totalOutstanding)
                .totalDiscount(totalDiscount)
                .totalTax(totalTax)
                .totalInvoices(allInvoices.size())
                .paidInvoices(
                        (int) allInvoices.stream().filter(i -> "PAID".equalsIgnoreCase(i.getPaymentStatus())).count())
                .unpaidInvoices(
                        (int) allInvoices.stream().filter(i -> "UNPAID".equalsIgnoreCase(i.getPaymentStatus())).count())
                .partiallyPaidInvoices((int) allInvoices.stream()
                        .filter(i -> "PARTIALLY_PAID".equalsIgnoreCase(i.getPaymentStatus())).count())
                .invoiceTypeWiseRevenue(invoiceTypeWise)
                .invoiceList(allInvoices)
                .build();

        return ReportResponse.success("Revenue Report", report);
    }
}
