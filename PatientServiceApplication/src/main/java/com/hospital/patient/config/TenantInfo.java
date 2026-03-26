/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.patient.config;

import lombok.Data;

/**
 *
 * @author mduhijod
 */
/**
 * Tenant information from master database
 */
@Data
public class TenantInfo {
    private String tenantId;
    private String clinicName;

    // Single database per clinic
    private String clinicCode;
    private String organizationId;
    private String operationalId;
    private String dbName;

    private String address;
    private String phone;
    private String email;
    private String logoPath;
    private boolean isActive;
}
