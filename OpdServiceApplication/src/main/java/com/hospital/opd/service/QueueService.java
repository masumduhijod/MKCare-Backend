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

import com.hospital.opd.dto.*;
import com.hospital.opd.entity.*;
import com.hospital.opd.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

// ========== QueueService ==========
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class QueueService {
    
    private final OpdQueueRepository queueRepository;
    private final ModelMapper modelMapper;
    
    public QueueDTO addToQueue(CreateQueueDTO dto) {
        log.info("Adding to queue: Appointment {}, Token {}", dto.getAppointmentId(), dto.getTokenNumber());
        
        OpdQueue queue = new OpdQueue();
        queue.setAppointmentId(dto.getAppointmentId());
        queue.setCvrNumber(dto.getCvrNumber());
        queue.setPinNumber(dto.getPinNumber());
        queue.setDoctorId(dto.getDoctorId());
        queue.setTokenNumber(dto.getTokenNumber());
        queue.setPatientId(1L); // TODO: Fetch from patient service
        queue.setPriority(dto.getPriority() != null 
            ? OpdQueue.Priority.valueOf(dto.getPriority()) 
            : OpdQueue.Priority.NORMAL);
        
        OpdQueue saved = queueRepository.save(queue);
        log.info("Added to queue successfully");
        return modelMapper.map(saved, QueueDTO.class);
    }
    
    public List<QueueDTO> getDoctorQueue(String doctorId, LocalDate date) {
        log.info("Fetching queue for doctor: {} on {}", doctorId, date);
        List<OpdQueue> queue = queueRepository.findByDoctorIdAndQueueDateOrderByTokenNumberAsc(doctorId, date);
        return queue.stream().map(q -> modelMapper.map(q, QueueDTO.class)).collect(Collectors.toList());
    }
    
    public QueueDTO callNext(String doctorId, LocalDate date) {
        log.info("Calling next patient for doctor: {}", doctorId);
        List<OpdQueue> waiting = queueRepository.findActiveQueueByDoctorAndDate(doctorId, date);
        
        if (waiting.isEmpty()) {
            throw new RuntimeException("No patients waiting");
        }
        
        OpdQueue next = waiting.get(0);
        next.callPatient();
        OpdQueue updated = queueRepository.save(next);
        return modelMapper.map(updated, QueueDTO.class);
    }
    
    public QueueDTO startConsultation(Long queueId) {
        OpdQueue queue = queueRepository.findById(queueId)
            .orElseThrow(() -> new RuntimeException("Queue not found"));
        queue.startConsultation();
        return modelMapper.map(queueRepository.save(queue), QueueDTO.class);
    }
    
    public QueueDTO completeQueue(Long queueId) {
        OpdQueue queue = queueRepository.findById(queueId)
            .orElseThrow(() -> new RuntimeException("Queue not found"));
        queue.completeConsultation();
        return modelMapper.map(queueRepository.save(queue), QueueDTO.class);
    }
}

