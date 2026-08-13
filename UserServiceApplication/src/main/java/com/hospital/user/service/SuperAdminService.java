package com.hospital.user.service;

import com.hospital.user.config.TenantContext;
import com.hospital.user.dto.*;
import com.hospital.user.entity.User;
import com.hospital.user.repository.UserRepository;
import com.hospital.user.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Super Admin Service
 * Manages clinic CRUD operations, clinic admin creation, and super admin
 * authentication
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SuperAdminService {

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    @Qualifier("masterJdbcTemplate")
    private JdbcTemplate masterJdbcTemplate;

    /**
     * ⭐ Automatically sync medicines table and RBAC tables for all clinics on startup
     */
    @javax.annotation.PostConstruct
    public void initializeSystem() {
        log.info("🔄 Starting global system initialization...");
        
        // 1. Initialize Master DB RBAC Tables
        initializeMasterRbacTables();
        
        // 2. Sync medicine tables for all tenants
        syncMedicineTableForAllClinics();
        
        log.info("✅ Global system initialization completed.");
    }

    private void initializeMasterRbacTables() {
        log.info("🔐 Initializing Master RBAC tables...");
        try {
            
            // Modules table
            masterJdbcTemplate.execute("CREATE TABLE IF NOT EXISTS modules (" +
                    "module_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "module_name VARCHAR(100) NOT NULL, " +
                    "module_code VARCHAR(50) UNIQUE NOT NULL, " +
                    "description TEXT, " +
                    "icon VARCHAR(50)" +
                    ") ENGINE=InnoDB");

            // Role-Module mapping table (Global if tenant_id is NULL, otherwise Clinic-specific)
            masterJdbcTemplate.execute("CREATE TABLE IF NOT EXISTS role_module_mapping (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "role_name VARCHAR(50) NOT NULL, " +
                    "module_code VARCHAR(50) NOT NULL, " +
                    "tenant_id VARCHAR(50) DEFAULT NULL, " +
                    "UNIQUE KEY uk_role_module_tenant (role_name, module_code, tenant_id)" +
                    ") ENGINE=InnoDB");

            // ⭐ ROBUST FIX: Add tenant_id if missing (Works on MySQL 5.7 and 8.0)
            try {
                // MySQL specific check for column existence
                masterJdbcTemplate.execute("ALTER TABLE role_module_mapping ADD COLUMN tenant_id VARCHAR(50) DEFAULT NULL");
                log.info("✅ Added missing 'tenant_id' column to role_module_mapping");
                
                // ⭐ Fix Index: Drop old index that didn't include tenant_id
                try {
                    masterJdbcTemplate.execute("ALTER TABLE role_module_mapping DROP INDEX uk_role_module");
                    log.info("✅ Dropped old unique index 'uk_role_module'");
                } catch (Exception ex) { /* Ignore if already dropped */ }
                
                // Add new tenant-aware unique index
                try {
                    masterJdbcTemplate.execute("ALTER TABLE role_module_mapping ADD UNIQUE KEY uk_role_module_tenant (role_name, module_code, tenant_id)");
                    log.info("✅ Added new tenant-aware unique index 'uk_role_module_tenant'");
                } catch (Exception ex) { /* Ignore if already exists */ }
                
            } catch (Exception e) {
                // If it fails, it's likely because the column already exists
                if (!e.getMessage().contains("Duplicate column name")) {
                    log.warn("Note: Role table update status: {}", e.getMessage());
                }
            }

            // User-specific Module Mapping (Per Clinic, Per User)
            masterJdbcTemplate.execute("CREATE TABLE IF NOT EXISTS user_module_mapping (" +
                    "mapping_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "tenant_id VARCHAR(50) NOT NULL, " +
                    "user_id BIGINT NOT NULL, " +
                    "module_code VARCHAR(50) NOT NULL, " +
                    "UNIQUE KEY (tenant_id, user_id, module_code)" +
                    ") ENGINE=InnoDB");

            try {
                masterJdbcTemplate.execute("ALTER TABLE user_module_mapping ADD COLUMN tenant_id VARCHAR(50) DEFAULT NULL");
                log.info("✅ Added missing 'tenant_id' column to user_module_mapping");
            } catch (Exception e) {
                if (!e.getMessage().contains("Duplicate column name")) {
                    log.warn("Note: User table update status: {}", e.getMessage());
                }
            }
            
            log.info("🔐 RBAC tables initialized (Modules, Role Mapping, User Mapping).");

            // Insert default modules if none exist
            Integer moduleCount = masterJdbcTemplate.queryForObject("SELECT COUNT(*) FROM modules", Integer.class);
            if (moduleCount == null || moduleCount == 0) {
                log.info("📦 Inserting default modules into master DB...");
                String[][] defaultModules = {
                    {"Dashboard", "DASHBOARD", "fa-th-large"},
                    {"User Management", "USER_MGMT", "fa-users-cog"},
                    {"Patient Registration", "PATIENT_REG", "fa-user-plus"},
                    {"Patient List", "PATIENT_LIST", "fa-users"},
                    {"Appointment Booking", "APP_BOOKING", "fa-calendar-plus"},
                    {"Appointment List", "APP_LIST", "fa-calendar-alt"},
                    {"OPD Queue", "OPD_QUEUE", "fa-list-ol"},
                    {"OPD Consultation", "OPD_CONSULT", "fa-user-md"},
                    {"Medicine Master", "MED_MASTER", "fa-pills"},
                    {"Billing & Invoices", "BILLING", "fa-file-invoice-dollar"},
                    {"Payment History", "PAYMENTS", "fa-history"},
                    {"Reports Dashboard", "REPORTS", "fa-chart-pie"}
                };

                for (String[] mod : defaultModules) {
                    masterJdbcTemplate.update("INSERT INTO modules (module_name, module_code, icon) VALUES (?, ?, ?)",
                            mod[0], mod[1], mod[2]);
                }
                
                // Set default permissions for ADMIN (all access)
                for (String[] mod : defaultModules) {
                    masterJdbcTemplate.update("INSERT IGNORE INTO role_module_mapping (role_name, module_code) VALUES (?, ?)",
                            "ADMIN", mod[1]);
                }

                // Set default permissions for DOCTOR
                String[] doctorMods = {"DASHBOARD", "PATIENT_LIST", "OPD_QUEUE", "OPD_CONSULT", "MED_MASTER", "REPORTS"};
                for (String code : doctorMods) {
                    masterJdbcTemplate.update("INSERT IGNORE INTO role_module_mapping (role_name, module_code) VALUES (?, ?)",
                            "DOCTOR", code);
                }

                // Set default permissions for RECEPTIONIST
                String[] receptionMods = {"DASHBOARD", "PATIENT_REG", "PATIENT_LIST", "APP_BOOKING", "APP_LIST", "OPD_QUEUE", "BILLING"};
                for (String code : receptionMods) {
                    masterJdbcTemplate.update("INSERT IGNORE INTO role_module_mapping (role_name, module_code) VALUES (?, ?)",
                            "RECEPTIONIST", code);
                }
                
                log.info("✅ Default modules and role permissions initialized.");
            }
        } catch (Exception e) {
            log.error("❌ Failed to initialize RBAC tables: {}", e.getMessage());
        }
    }

    private void syncMedicineTableForAllClinics() {
        try {
            List<Map<String, Object>> tenants = masterJdbcTemplate.queryForList("SELECT tenant_id, db_name FROM tenants");
            for (Map<String, Object> tenant : tenants) {
                String tenantId = (String) tenant.get("tenant_id");
                String dbName = (String) tenant.get("db_name");
                try {
                    log.info("📦 Syncing medicine table for Tenant: {} | DB: {}", tenantId, dbName);
                    JdbcTemplate clinicJdbc = createClinicJdbcTemplate(dbName);
                    clinicJdbc.execute("CREATE TABLE IF NOT EXISTS medicines (" +
                            "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                            "medicine_name VARCHAR(255) NOT NULL, " +
                            "generic_name VARCHAR(255), " +
                            "brand_name VARCHAR(255), " +
                            "composition VARCHAR(255), " +
                            "medicine_type VARCHAR(50), " +
                            "strength VARCHAR(50), " +
                            "unit VARCHAR(20), " +
                            "packaging VARCHAR(100), " +
                            "manufacturer VARCHAR(255), " +
                            "description TEXT, " +
                            "is_active TINYINT(1) DEFAULT 1, " +
                            "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                            "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
                            "created_by VARCHAR(100), " +
                            "INDEX idx_medicine_name (medicine_name), " +
                            "INDEX idx_brand_name (brand_name)" +
                            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci");
                } catch (Exception e) {
                    log.error("❌ Failed to sync medicine table for tenant {}: {}", tenantId, e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("❌ Error during global sync: {}", e.getMessage());
        }
    }

    // =====================================================================
    // 1. SUPER ADMIN LOGIN
    // =====================================================================
    public LoginResponse superAdminLogin(SuperAdminLoginRequest request) {
        log.info("🔐 Super Admin login attempt: {}", request.getUsername());

        String sql = "SELECT id, username, password, full_name, email, is_active FROM super_admins WHERE username = ?";

        Map<String, Object> admin;
        try {
            admin = masterJdbcTemplate.queryForMap(sql, request.getUsername());
        } catch (Exception e) {
            log.error("❌ Super Admin not found: {}", request.getUsername());
            throw new RuntimeException("Invalid username or password");
        }

        // Check if active
        Boolean isActive = (Boolean) admin.get("is_active");
        if (isActive == null || !isActive) {
            throw new RuntimeException("Super Admin account is inactive");
        }

        // Verify password
        String storedPassword = (String) admin.get("password");
        if (!passwordEncoder.matches(request.getPassword(), storedPassword)) {
            log.error("❌ Password mismatch for super admin: {}", request.getUsername());
            throw new RuntimeException("Invalid username or password");
        }

        // Update last login
        masterJdbcTemplate.update("UPDATE super_admins SET last_login = NOW() WHERE id = ?", admin.get("id"));

        // Generate JWT with SUPER_ADMIN role
        String token = jwtUtil.generateToken(request.getUsername(), "SUPER_ADMIN");

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUsername((String) admin.get("username"));
        response.setFullName((String) admin.get("full_name"));
        response.setEmail((String) admin.get("email"));
        response.setRole("SUPER_ADMIN");
        response.setExpiresAt(LocalDateTime.now().plusHours(24));
        response.setTenantId("MASTER");
        response.setClinicName("Super Admin Portal");
        
        // ⭐ SUPER ADMIN gets all module access
        try {
            List<String> allModules = masterJdbcTemplate.queryForList("SELECT module_code FROM modules", String.class);
            response.setPermissions(allModules);
        } catch (Exception e) {
            response.setPermissions(new java.util.ArrayList<>());
        }

        log.info("✅ Super Admin login successful: {}", request.getUsername());
        return response;
    }

    // =====================================================================
    // 2. LIST ALL CLINICS
    // =====================================================================
    public List<ClinicDTO> getAllClinics() {
        String sql = "SELECT tenant_id, clinic_code, clinic_name, organization_id, operational_id, " +
                "db_name, address, phone, email, logo_path, is_active, subscription_start_date, subscription_expiry, created_at, updated_at, created_by " +
                "FROM tenants ORDER BY created_at DESC";

        List<Map<String, Object>> rows = masterJdbcTemplate.queryForList(sql);

        return rows.stream().map(row -> {
            ClinicDTO dto = new ClinicDTO();
            dto.setTenantId((String) row.get("tenant_id"));
            dto.setClinicCode((String) row.get("clinic_code"));
            dto.setClinicName((String) row.get("clinic_name"));
            dto.setOrganizationId((String) row.get("organization_id"));
            dto.setOperationalId((String) row.get("operational_id"));
            dto.setDbName((String) row.get("db_name"));
            dto.setAddress((String) row.get("address"));
            dto.setPhone((String) row.get("phone"));
            dto.setEmail((String) row.get("email"));
            dto.setLogoPath((String) row.get("logo_path"));

            // Handle is_active - MySQL returns different types
            Object isActiveObj = row.get("is_active");
            if (isActiveObj instanceof Boolean) {
                dto.setActive((Boolean) isActiveObj);
            } else if (isActiveObj instanceof Number) {
                dto.setActive(((Number) isActiveObj).intValue() == 1);
            }

            if (row.get("subscription_start_date") instanceof java.sql.Timestamp) {
                dto.setSubscriptionStartDate(((java.sql.Timestamp) row.get("subscription_start_date")).toLocalDateTime());
            } else if (row.get("subscription_start_date") instanceof LocalDateTime) {
                dto.setSubscriptionStartDate((LocalDateTime) row.get("subscription_start_date"));
            }

            if (row.get("subscription_expiry") instanceof java.sql.Timestamp) {
                dto.setSubscriptionExpiry(((java.sql.Timestamp) row.get("subscription_expiry")).toLocalDateTime());
            } else if (row.get("subscription_expiry") instanceof LocalDateTime) {
                dto.setSubscriptionExpiry((LocalDateTime) row.get("subscription_expiry"));
            }

            if (row.get("created_at") instanceof java.sql.Timestamp) {
                dto.setCreatedAt(((java.sql.Timestamp) row.get("created_at")).toLocalDateTime());
            }
            if (row.get("updated_at") instanceof java.sql.Timestamp) {
                dto.setUpdatedAt(((java.sql.Timestamp) row.get("updated_at")).toLocalDateTime());
            }
            dto.setCreatedBy((String) row.get("created_by"));
            return dto;
        }).collect(Collectors.toList());
    }

    // =====================================================================
    // 3. GET CLINIC BY TENANT ID
    // =====================================================================
    public ClinicDTO getClinicByTenantId(String tenantId) {
        String sql = "SELECT tenant_id, clinic_code, clinic_name, organization_id, operational_id, " +
                "db_name, address, phone, email, logo_path, is_active, subscription_start_date, subscription_expiry, created_at, created_by " +
                "FROM tenants WHERE tenant_id = ?";

        Map<String, Object> row;
        try {
            row = masterJdbcTemplate.queryForMap(sql, tenantId);
        } catch (Exception e) {
            throw new RuntimeException("Clinic not found: " + tenantId);
        }

        ClinicDTO dto = new ClinicDTO();
        dto.setTenantId((String) row.get("tenant_id"));
        dto.setClinicCode((String) row.get("clinic_code"));
        dto.setClinicName((String) row.get("clinic_name"));
        dto.setOrganizationId((String) row.get("organization_id"));
        dto.setOperationalId((String) row.get("operational_id"));
        dto.setDbName((String) row.get("db_name"));
        dto.setAddress((String) row.get("address"));
        dto.setPhone((String) row.get("phone"));
        dto.setEmail((String) row.get("email"));
        dto.setLogoPath((String) row.get("logo_path"));

        Object isActiveObj = row.get("is_active");
        if (isActiveObj instanceof Boolean) {
            dto.setActive((Boolean) isActiveObj);
        } else if (isActiveObj instanceof Number) {
            dto.setActive(((Number) isActiveObj).intValue() == 1);
        }

        if (row.get("subscription_start_date") instanceof java.sql.Timestamp) {
            dto.setSubscriptionStartDate(((java.sql.Timestamp) row.get("subscription_start_date")).toLocalDateTime());
        } else if (row.get("subscription_start_date") instanceof LocalDateTime) {
            dto.setSubscriptionStartDate((LocalDateTime) row.get("subscription_start_date"));
        }

        if (row.get("subscription_expiry") instanceof java.sql.Timestamp) {
            dto.setSubscriptionExpiry(((java.sql.Timestamp) row.get("subscription_expiry")).toLocalDateTime());
        } else if (row.get("subscription_expiry") instanceof LocalDateTime) {
            dto.setSubscriptionExpiry((LocalDateTime) row.get("subscription_expiry"));
        }

        return dto;
    }

    // =====================================================================
    // 4. GET CLINIC BY CLINIC CODE (for URL-based identification)
    // =====================================================================
    public ClinicDTO getClinicByCode(String clinicCode) {
        String sql = "SELECT tenant_id, clinic_code, clinic_name, organization_id, operational_id, " +
                "db_name, address, phone, email, logo_path, is_active, subscription_start_date, subscription_expiry " +
                "FROM tenants WHERE clinic_code = ? AND is_active = TRUE";

        Map<String, Object> row;
        try {
            row = masterJdbcTemplate.queryForMap(sql, clinicCode.toLowerCase());
        } catch (Exception e) {
            throw new RuntimeException("Clinic not found with code: " + clinicCode);
        }

        ClinicDTO dto = new ClinicDTO();
        dto.setTenantId((String) row.get("tenant_id"));
        dto.setClinicCode((String) row.get("clinic_code"));
        dto.setClinicName((String) row.get("clinic_name"));
        dto.setOrganizationId((String) row.get("organization_id"));
        dto.setOperationalId((String) row.get("operational_id"));
        dto.setDbName((String) row.get("db_name"));
        dto.setAddress((String) row.get("address"));
        dto.setPhone((String) row.get("phone"));
        dto.setEmail((String) row.get("email"));
        dto.setLogoPath((String) row.get("logo_path"));
        if (row.get("subscription_start_date") instanceof java.sql.Timestamp) {
            dto.setSubscriptionStartDate(((java.sql.Timestamp) row.get("subscription_start_date")).toLocalDateTime());
        } else if (row.get("subscription_start_date") instanceof LocalDateTime) {
            dto.setSubscriptionStartDate((LocalDateTime) row.get("subscription_start_date"));
        }
        if (row.get("subscription_expiry") instanceof java.sql.Timestamp) {
            dto.setSubscriptionExpiry(((java.sql.Timestamp) row.get("subscription_expiry")).toLocalDateTime());
        } else if (row.get("subscription_expiry") instanceof LocalDateTime) {
            dto.setSubscriptionExpiry((LocalDateTime) row.get("subscription_expiry"));
        }
        dto.setActive(true);

        return dto;
    }

    // =====================================================================
    // 5. CREATE NEW CLINIC (with DB + Admin user)
    // =====================================================================
    @Transactional
    public ClinicDTO createClinic(ClinicDTO clinicDTO) {
        log.info("🏥 Creating new clinic: {} (code: {}, db: {})",
                clinicDTO.getClinicName(), clinicDTO.getClinicCode(), clinicDTO.getDbName());

        // Validate unique clinic_code
        Integer codeCount = masterJdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM tenants WHERE clinic_code = ?",
                Integer.class, clinicDTO.getClinicCode().toLowerCase());
        if (codeCount != null && codeCount > 0) {
            throw new RuntimeException("Clinic code already exists: " + clinicDTO.getClinicCode());
        }

        // Generate tenant_id if not provided
        if (clinicDTO.getTenantId() == null || clinicDTO.getTenantId().isEmpty()) {
            clinicDTO.setTenantId(
                    clinicDTO.getClinicCode().toUpperCase() + String.format("%03d", System.currentTimeMillis() % 1000));
        }

        // Step 1: Create the clinic database
        String dbName = clinicDTO.getDbName();
        try {
            masterJdbcTemplate.execute("CREATE DATABASE IF NOT EXISTS `" + dbName +
                    "` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci");
            log.info("✅ Database created: {}", dbName);
        } catch (Exception e) {
            log.error("❌ Failed to create database: {}", dbName, e);
            throw new RuntimeException("Failed to create clinic database: " + e.getMessage());
        }

        // Step 2: Create tables in the new database
        try {
            createClinicTables(dbName);
            log.info("✅ Tables created in database: {}", dbName);
        } catch (Exception e) {
            log.error("❌ Failed to create tables in: {}", dbName, e);
            // Cleanup: drop the database
            masterJdbcTemplate.execute("DROP DATABASE IF EXISTS `" + dbName + "`");
            throw new RuntimeException("Failed to create clinic tables: " + e.getMessage());
        }

        // Step 3: Insert tenant record in master DB
        String insertSql = "INSERT INTO tenants (tenant_id, clinic_code, clinic_name, organization_id, " +
                "operational_id, db_name, address, phone, email, logo_path, created_by, subscription_start_date, subscription_expiry) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // Handle user-provided dates or defaults
        LocalDateTime startDate = clinicDTO.getSubscriptionStartDate() != null ? 
                                 clinicDTO.getSubscriptionStartDate() : 
                                 LocalDateTime.now();
        clinicDTO.setSubscriptionStartDate(startDate);

        LocalDateTime expiry = clinicDTO.getSubscriptionExpiry() != null ? 
                               clinicDTO.getSubscriptionExpiry() : 
                               startDate.plusDays(30);
        clinicDTO.setSubscriptionExpiry(expiry);

        masterJdbcTemplate.update(insertSql,
                clinicDTO.getTenantId(),
                clinicDTO.getClinicCode().toLowerCase(),
                clinicDTO.getClinicName(),
                clinicDTO.getOrganizationId(),
                clinicDTO.getOperationalId(),
                dbName,
                clinicDTO.getAddress(),
                clinicDTO.getPhone(),
                clinicDTO.getEmail(),
                clinicDTO.getLogoPath(),
                clinicDTO.getCreatedBy(),
                startDate,
                expiry);

        log.info("✅ Tenant record created: {}", clinicDTO.getTenantId());

        // Step 4: Create admin user in the new clinic database
        if (clinicDTO.getAdminUsername() != null && !clinicDTO.getAdminUsername().isEmpty()) {
            createClinicAdmin(dbName, clinicDTO);
            log.info("✅ Admin user created: {} in database: {}", clinicDTO.getAdminUsername(), dbName);
        }

        clinicDTO.setActive(true);
        clinicDTO.setCreatedAt(LocalDateTime.now());

        log.info("🎉 Clinic created successfully: {} ({})", clinicDTO.getClinicName(), clinicDTO.getTenantId());
        return clinicDTO;
    }

    // =====================================================================
    // 6. UPDATE CLINIC
    // =====================================================================
    public ClinicDTO updateClinic(String tenantId, ClinicDTO clinicDTO) {
        log.info("📝 Updating clinic: {}", tenantId);

        String sql = "UPDATE tenants SET clinic_name = ?, organization_id = ?, operational_id = ?, " +
                "address = ?, phone = ?, email = ?, logo_path = ?, subscription_start_date = ?, subscription_expiry = ?, updated_at = NOW() WHERE tenant_id = ?";

        int rows = masterJdbcTemplate.update(sql,
                clinicDTO.getClinicName(),
                clinicDTO.getOrganizationId(),
                clinicDTO.getOperationalId(),
                clinicDTO.getAddress(),
                clinicDTO.getPhone(),
                clinicDTO.getEmail(),
                clinicDTO.getLogoPath(),
                clinicDTO.getSubscriptionStartDate(),
                clinicDTO.getSubscriptionExpiry(),
                tenantId);

        if (rows == 0) {
            throw new RuntimeException("Clinic not found: " + tenantId);
        }

        log.info("✅ Clinic updated: {}", tenantId);
        return getClinicByTenantId(tenantId);
    }

    // =====================================================================
    // 7. ACTIVATE/DEACTIVATE CLINIC
    // =====================================================================
    public void toggleClinicStatus(String tenantId, boolean active) {
        String sql = "UPDATE tenants SET is_active = ?, updated_at = NOW() WHERE tenant_id = ?";
        int rows = masterJdbcTemplate.update(sql, active ? 1 : 0, tenantId);
        if (rows == 0) {
            throw new RuntimeException("Clinic not found: " + tenantId);
        }
        log.info("✅ Clinic {} status changed to: {}", tenantId, active ? "ACTIVE" : "INACTIVE");
    }

    // =====================================================================
    // 8. CREATE ADMIN USER FOR A CLINIC
    // =====================================================================
    public void createClinicAdminUser(String tenantId, ClinicDTO adminDto) {
        ClinicDTO clinic = getClinicByTenantId(tenantId);
        createClinicAdmin(clinic.getDbName(), adminDto);
        log.info("✅ Admin user created for clinic: {}", tenantId);
    }

    // =====================================================================
    // 9. RENEW/UPDATE CLINIC SUBSCRIPTION
    // =====================================================================
    public ClinicDTO renewClinicSubscription(String tenantId, ClinicDTO dto) {
        log.info("📅 Renewing/Updating subscription for clinic: {}", tenantId);
        
        String sql = "UPDATE tenants SET subscription_expiry = ?, updated_at = NOW() WHERE tenant_id = ?";
        int rows = masterJdbcTemplate.update(sql, dto.getSubscriptionExpiry(), tenantId);

        if (rows == 0) {
            throw new RuntimeException("Clinic not found: " + tenantId);
        }

        log.info("✅ Clinic subscription updated: {}", tenantId);
        return getClinicByTenantId(tenantId);
    }

    // =====================================================================
    // HELPER: Create tables in a new clinic database
    // =====================================================================
//    private void createClinicTables(String dbName) {
//        // Execute each CREATE TABLE statement on the clinic database
//        JdbcTemplate clinicJdbc = createClinicJdbcTemplate(dbName);
//
//        // Users table
//        clinicJdbc.execute("CREATE TABLE IF NOT EXISTS users (" +
//                "user_id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
//                "username VARCHAR(50) UNIQUE NOT NULL, " +
//                "password VARCHAR(255) NOT NULL, " +
//                "email VARCHAR(100) UNIQUE NOT NULL, " +
//                "role ENUM('ADMIN','DOCTOR','RECEPTIONIST','NURSE','PHARMACIST','LAB_TECH','BILLING','PATIENT') NOT NULL, " +
//                "status ENUM('ACTIVE','INACTIVE','LOCKED') NOT NULL DEFAULT 'ACTIVE', " +
//                "first_name VARCHAR(100), " +
//                "last_name VARCHAR(100), " +
//                "contact_number VARCHAR(15), " +
//                "last_login DATETIME, " +
//                "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
//                "updated_at DATETIME, " +
//                "created_by VARCHAR(100)" +
//                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci");
//
//        // Patients table
//        clinicJdbc.execute("CREATE TABLE IF NOT EXISTS patients (" +
//                "patient_id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
//                "pin_number VARCHAR(20) UNIQUE NOT NULL, " +
//                "first_name VARCHAR(100) NOT NULL, " +
//                "last_name VARCHAR(100), " +
//                "date_of_birth DATE NOT NULL, " +
//                "age INT, " +
//                "gender ENUM('MALE','FEMALE','OTHER') NOT NULL, " +
//                "blood_group VARCHAR(5), " +
//                "contact_number VARCHAR(15) NOT NULL, " +
//                "alternate_contact VARCHAR(15), " +
//                "email VARCHAR(100), " +
//                "aadhar_number VARCHAR(12) UNIQUE, " +
//                "address_line1 TEXT, address_line2 TEXT, " +
//                "city VARCHAR(100), state VARCHAR(100), pincode VARCHAR(10), " +
//                "emergency_contact_name VARCHAR(100), emergency_contact_number VARCHAR(15), emergency_contact_relation VARCHAR(50), " +
//                "insurance_provider VARCHAR(100), insurance_id VARCHAR(50), insurance_expiry_date DATE, " +
//                "registration_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
//                "registered_by VARCHAR(100), " +
//                "status ENUM('ACTIVE','INACTIVE','DECEASED') NOT NULL DEFAULT 'ACTIVE', " +
//                "photo_url TEXT, remarks TEXT, " +
//                "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
//                "updated_at DATETIME" +
//                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci");
//
//        // Patient Medical History
//        // ✅ FIXED: id→history_id, chronic_conditions→chronic_diseases, updated_at→last_updated
//        // ✅ FIXED: added blood_pressure, smoking/alcohol as ENUM, removed blood_group/notes/current_medications
//        clinicJdbc.execute("CREATE TABLE IF NOT EXISTS patient_medical_history (" +
//                "history_id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
//                "patient_id BIGINT NOT NULL UNIQUE, " +
//                "allergies TEXT, chronic_diseases TEXT, " +
//                "past_surgeries TEXT, family_history TEXT, " +
//                "smoking_status ENUM('NEVER','FORMER','CURRENT') DEFAULT 'NEVER', " +
//                "alcohol_consumption ENUM('NEVER','OCCASIONAL','REGULAR') DEFAULT 'NEVER', " +
//                "blood_pressure VARCHAR(20), " +
//                "height_cm DECIMAL(5,2), weight_kg DECIMAL(5,2), bmi DECIMAL(5,2), " +
//                "last_updated DATETIME" +
//                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci");
//
//        // Doctors table
//        clinicJdbc.execute("CREATE TABLE IF NOT EXISTS doctors (" +
//                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
//                "doctor_id VARCHAR(20) UNIQUE NOT NULL, " +
//                "user_id BIGINT, " +
//                "first_name VARCHAR(100) NOT NULL, last_name VARCHAR(100), " +
//                "specialization VARCHAR(100) NOT NULL, qualification VARCHAR(255), " +
//                "experience_years INT, department VARCHAR(100), " +
//                "contact_number VARCHAR(15) NOT NULL, email VARCHAR(100), " +
//                "license_number VARCHAR(50) UNIQUE, registration_number VARCHAR(50), " +
//                "consultation_fee DECIMAL(10,2) NOT NULL, follow_up_fee DECIMAL(10,2), " +
//                "status ENUM('AVAILABLE','ON_LEAVE','BUSY','INACTIVE') NOT NULL DEFAULT 'AVAILABLE', " +
//                "available_for_opd TINYINT(1) DEFAULT 1, available_for_emergency TINYINT(1) DEFAULT 0, " +
//                "photo_url TEXT, bio TEXT, languages_spoken VARCHAR(255), room_number VARCHAR(20), " +
//                "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
//                "updated_at DATETIME, created_by VARCHAR(100)" +
//                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci");
//
//        // Doctor Schedules
//        // ✅ FIXED: id→schedule_id, max_patients→max_patients_per_slot, added break times, removed day_of_week/created_at/updated_at
//        clinicJdbc.execute("CREATE TABLE IF NOT EXISTS doctor_schedules (" +
//                "schedule_id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
//                "doctor_id VARCHAR(20) NOT NULL, " +
//                "schedule_date DATE NOT NULL, " +
//                "start_time TIME NOT NULL, end_time TIME NOT NULL, " +
//                "slot_duration_minutes INT DEFAULT 15, " +
//                "max_patients_per_slot INT DEFAULT 1, " +
//                "is_active TINYINT(1) DEFAULT 1, " +
//                "break_start_time TIME NULL, break_end_time TIME NULL, " +
//                "UNIQUE KEY uk_doctor_date (doctor_id, schedule_date), " +
//                "INDEX idx_doctor_date (doctor_id, schedule_date)" +
//                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci");
//
//        // Case Visit Records
//        clinicJdbc.execute("CREATE TABLE IF NOT EXISTS case_visit_records (" +
//                "cvr_id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
//                "cvr_number VARCHAR(20) UNIQUE NOT NULL, " +
//                "patient_id BIGINT NOT NULL, pin_number VARCHAR(20) NOT NULL, " +
//                "appointment_id VARCHAR(20), " +
//                "appointment_date DATE, appointment_time TIME, " +
//                "visit_date DATE, visit_time TIME, " +
//                "visit_type ENUM('OPD','IPD','EMERGENCY','FOLLOW_UP') NOT NULL DEFAULT 'OPD', " +
//                "department VARCHAR(100), doctor_id VARCHAR(20), " +
//                "chief_complaint TEXT NOT NULL, symptoms TEXT, " +
//                "status ENUM('REGISTERED','APPOINTMENT_SCHEDULED','CHECKED_IN','CONSULTING','COMPLETED','CANCELLED') NOT NULL DEFAULT 'REGISTERED', " +
//                "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
//                "created_by VARCHAR(100), " +
//                "checked_in_at DATETIME, consultation_started_at DATETIME, consultation_completed_at DATETIME, " +
//                "is_billed TINYINT(1) DEFAULT 0, billing_id VARCHAR(20)" +
//                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci");
//
//        // CVR Vitals
//        clinicJdbc.execute("CREATE TABLE IF NOT EXISTS cvr_vitals (" +
//                "vital_id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
//                "cvr_id BIGINT NOT NULL, " +
//                "temperature_f DECIMAL(4,2), " +
//                "blood_pressure_systolic INT, blood_pressure_diastolic INT, " +
//                "pulse_rate INT, respiratory_rate INT, " +
//                "spo2_percentage INT, " +
//                "height_cm DECIMAL(5,2), weight_kg DECIMAL(5,2), bmi DECIMAL(5,2), " +
//                "blood_sugar DECIMAL(6,2), notes TEXT, " +
//                "recorded_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
//                "recorded_by VARCHAR(100)" +
//                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci");
//
//        // Appointments
//        clinicJdbc.execute("CREATE TABLE IF NOT EXISTS appointments (" +
//                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
//                "appointment_id VARCHAR(20) UNIQUE NOT NULL, " +
//                "cvr_id BIGINT, cvr_number VARCHAR(20), " +
//                "patient_id BIGINT NOT NULL, pin_number VARCHAR(20) NOT NULL, " +
//                "doctor_id VARCHAR(20) NOT NULL, " +
//                "appointment_date DATE NOT NULL, appointment_time TIME NOT NULL, " +
//                "slot_id BIGINT, token_number INT, " +
//                "appointment_type ENUM('NEW','FOLLOW_UP','EMERGENCY','CONSULTATION') DEFAULT 'NEW', " +
//                "status ENUM('SCHEDULED','CHECKED_IN','CONSULTING','COMPLETED','CANCELLED','NO_SHOW','RESCHEDULED') NOT NULL DEFAULT 'SCHEDULED', " +
//                "symptoms TEXT, notes TEXT, " +
//                "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
//                "created_by VARCHAR(100), " +
//                "checked_in_at DATETIME, consultation_started_at DATETIME, consultation_ended_at DATETIME, " +
//                "cancelled_at DATETIME, cancellation_reason TEXT, cancelled_by VARCHAR(100)" +
//                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci");
//
//        // Appointment Slots
//        // ✅ FIXED: id→slot_id, start_time/end_time→slot_time, added schedule_id/max_patients/booked_count, removed is_booked/created_at
//        clinicJdbc.execute("CREATE TABLE IF NOT EXISTS appointment_slots (" +
//                "slot_id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
//                "schedule_id BIGINT, " +
//                "doctor_id VARCHAR(20) NOT NULL, " +
//                "slot_date DATE NOT NULL, " +
//                "slot_time TIME NOT NULL, " +
//                "is_available TINYINT(1) DEFAULT 1, " +
//                "max_patients INT DEFAULT 1, " +
//                "booked_count INT DEFAULT 0, " +
//                "appointment_id VARCHAR(20), " +
//                "UNIQUE KEY uk_doctor_date_time (doctor_id, slot_date, slot_time), " +
//                "INDEX idx_doctor_date (doctor_id, slot_date)" +
//                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci");
//
//        // Consultations
//        clinicJdbc.execute("CREATE TABLE IF NOT EXISTS consultations (" +
//                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
//                "consultation_id VARCHAR(20) UNIQUE NOT NULL, " +
//                "appointment_id VARCHAR(20), cvr_number VARCHAR(20), " +
//                "patient_id BIGINT NOT NULL, pin_number VARCHAR(20) NOT NULL, " +
//                "doctor_id VARCHAR(20) NOT NULL, " +
//                "consultation_date DATETIME NOT NULL, " +
//                "subjective TEXT, objective TEXT, assessment TEXT, plan TEXT, " +
//                "chief_complaint TEXT NOT NULL, present_illness TEXT, " +
//                "examination_findings TEXT, " +
//                "diagnosis TEXT NOT NULL, treatment_plan TEXT, " +
//                "vitals_recorded JSON, " +
//                "follow_up_required TINYINT(1) DEFAULT 0, follow_up_date DATE, follow_up_instructions TEXT, " +
//                "status ENUM('IN_PROGRESS','COMPLETED') NOT NULL DEFAULT 'IN_PROGRESS', " +
//                "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
//                "updated_at DATETIME, completed_at DATETIME" +
//                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci");
//
//        // OPD Queue
//        // ✅ FIXED: id→queue_id BIGINT PK, status/priority ENUMs updated, added called_at/consultation times/waiting mins
//        //           removed notes/created_at/start_time/end_time, queue_id VARCHAR UNIQUE removed
//        clinicJdbc.execute("CREATE TABLE IF NOT EXISTS opd_queue (" +
//                "queue_id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
//                "appointment_id VARCHAR(20), cvr_number VARCHAR(20), " +
//                "patient_id BIGINT NOT NULL, pin_number VARCHAR(20) NOT NULL, " +
//                "doctor_id VARCHAR(20) NOT NULL, " +
//                "token_number INT NOT NULL, " +
//                "queue_date DATE NOT NULL, " +
//                "status ENUM('WAITING','SKIPPED','IN_CONSULTATION','COMPLETED','CANCELLED') NOT NULL DEFAULT 'WAITING', " +
//                "priority ENUM('NORMAL','URGENT','EMERGENCY') DEFAULT 'NORMAL', " +
//                "check_in_time DATETIME NOT NULL, " +
//                "called_at DATETIME, " +
//                "consultation_start_time DATETIME, " +
//                "consultation_end_time DATETIME, " +
//                "waiting_time_minutes INT, " +
//                "consultation_duration_minutes INT, " +
//                "INDEX idx_doctor_date (doctor_id, queue_date), " +
//                "INDEX idx_queue_date (queue_date)" +
//                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci");
//
//        // Prescriptions
//        // ✅ FIXED: added consultation_number/validity_days/expiry_date/instructions, status ENUM updated
//        //           removed diagnosis/notes/follow_up_date/updated_at
//        clinicJdbc.execute("CREATE TABLE IF NOT EXISTS prescriptions (" +
//                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
//                "prescription_id VARCHAR(20) UNIQUE NOT NULL, " +
//                "consultation_id VARCHAR(20) NOT NULL, " +
//                "consultation_number VARCHAR(20), " +
//                "patient_id BIGINT NOT NULL, pin_number VARCHAR(20) NOT NULL, " +
//                "doctor_id VARCHAR(20) NOT NULL, " +
//                "prescription_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
//                "validity_days INT DEFAULT 30, " +
//                "expiry_date DATE, " +
//                "instructions TEXT, " +
//                "status ENUM('ACTIVE','DISPENSED','EXPIRED','CANCELLED') NOT NULL DEFAULT 'ACTIVE', " +
//                "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
//                "INDEX idx_patient (patient_id), INDEX idx_doctor (doctor_id)" +
//                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci");
//
//        // Prescription Items
//        // ✅ FIXED: id→item_id, added morning/afternoon/evening/night/before_food/after_food
//        //           removed route/is_generic/created_at, FK constraint added
//        clinicJdbc.execute("CREATE TABLE IF NOT EXISTS prescription_items (" +
//                "item_id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
//                "prescription_id BIGINT NOT NULL, " +
//                "medicine_name VARCHAR(255) NOT NULL, " +
//                "dosage VARCHAR(100) NOT NULL, " +
//                "frequency VARCHAR(100) NOT NULL, " +
//                "duration VARCHAR(50) NOT NULL, " +
//                "quantity INT NOT NULL, " +
//                "instructions TEXT, " +
//                "morning TINYINT(1) DEFAULT 0, " +
//                "afternoon TINYINT(1) DEFAULT 0, " +
//                "evening TINYINT(1) DEFAULT 0, " +
//                "night TINYINT(1) DEFAULT 0, " +
//                "before_food TINYINT(1) DEFAULT 0, " +
//                "after_food TINYINT(1) DEFAULT 1, " +
//                "FOREIGN KEY (prescription_id) REFERENCES prescriptions(id) ON DELETE CASCADE" +
//                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci");
//
//        // Invoices
//        clinicJdbc.execute("CREATE TABLE IF NOT EXISTS invoices (" +
//                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
//                "invoice_number VARCHAR(20) UNIQUE NOT NULL, " +
//                "appointment_id VARCHAR(20), cvr_number VARCHAR(20), " +
//                "pin_number VARCHAR(20) NOT NULL, patient_id BIGINT NOT NULL, " +
//                "doctor_id VARCHAR(20), " +
//                "invoice_type ENUM('OPD','IPD','PHARMACY','LAB','EMERGENCY') NOT NULL DEFAULT 'OPD', " +
//                "invoice_date DATE NOT NULL, due_date DATE, " +
//                "sub_total DECIMAL(10,2) NOT NULL DEFAULT 0.00, " +
//                "discount_amount DECIMAL(10,2) DEFAULT 0.00, discount_percentage DECIMAL(5,2) DEFAULT 0.00, " +
//                "tax_amount DECIMAL(10,2) DEFAULT 0.00, tax_percentage DECIMAL(5,2) DEFAULT 0.00, " +
//                "total_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00, " +
//                "paid_amount DECIMAL(10,2) DEFAULT 0.00, outstanding_amount DECIMAL(10,2) DEFAULT 0.00, " +
//                "is_insurance_claim TINYINT(1) DEFAULT 0, insurance_provider VARCHAR(100), insurance_claim_amount DECIMAL(10,2) DEFAULT 0.00, " +
//                "payment_status ENUM('PENDING','PARTIAL','PAID','CANCELLED','REFUNDED') NOT NULL DEFAULT 'PENDING', " +
//                "notes TEXT, " +
//                "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
//                "created_by VARCHAR(100), paid_at DATETIME" +
//                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci");
//
//        // Invoice Items
//        // ✅ FIXED: item_type VARCHAR→ENUM(CONSULTATION,SERVICE,MEDICINE,LAB_TEST,PROCEDURE,ROOM_CHARGE,OTHER)
//        //           removed created_at, FK constraint added
//        clinicJdbc.execute("CREATE TABLE IF NOT EXISTS invoice_items (" +
//                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
//                "invoice_id BIGINT NOT NULL, " +
//                "item_name VARCHAR(255) NOT NULL, " +
//                "description TEXT, " +
//                "quantity INT NOT NULL DEFAULT 1, " +
//                "unit_price DECIMAL(10,2) NOT NULL DEFAULT 0.00, " +
//                "amount DECIMAL(10,2) NOT NULL DEFAULT 0.00, " +
//                "item_type ENUM('CONSULTATION','SERVICE','MEDICINE','LAB_TEST','PROCEDURE','ROOM_CHARGE','OTHER') DEFAULT 'SERVICE', " +
//                "FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE" +
//                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci");
//
//        // Payments
//        // ✅ FIXED: payment_method→payment_mode, ENUM updated (NET_BANKING added, ONLINE removed)
//        //           added payment_status/invoice_number/remarks, removed notes/created_at, FK added
//        clinicJdbc.execute("CREATE TABLE IF NOT EXISTS payments (" +
//                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
//                "payment_id VARCHAR(20) UNIQUE NOT NULL, " +
//                "invoice_id BIGINT NOT NULL, " +
//                "invoice_number VARCHAR(20), " +
//                "amount DECIMAL(10,2) NOT NULL, " +
//                "payment_mode ENUM('CASH','CARD','UPI','NET_BANKING','CHEQUE','INSURANCE') NOT NULL, " +
//                "payment_status ENUM('SUCCESS','PENDING','FAILED','REFUNDED') NOT NULL DEFAULT 'SUCCESS', " +
//                "transaction_id VARCHAR(100), " +
//                "payment_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
//                "received_by VARCHAR(100), " +
//                "remarks TEXT, " +
//                "FOREIGN KEY (invoice_id) REFERENCES invoices(id)" +
//                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci");
//
//        log.info("✅ All 16 tables created in database: {}", dbName);
//    }
    private void createClinicTables(String dbName) {
    JdbcTemplate clinicJdbc = createClinicJdbcTemplate(dbName);

    clinicJdbc.execute("CREATE TABLE IF NOT EXISTS users (" +
            "user_id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
            "username VARCHAR(50) NOT NULL, " +
            "password VARCHAR(255) NOT NULL, " +
            "email VARCHAR(100) NOT NULL, " +
            "role VARCHAR(255) NOT NULL, " +
            "status VARCHAR(255) NOT NULL, " +
            "first_name VARCHAR(100) DEFAULT NULL, " +
            "last_name VARCHAR(100) DEFAULT NULL, " +
            "contact_number VARCHAR(15) DEFAULT NULL, " +
            "last_login DATETIME DEFAULT NULL, " +
            "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated_at DATETIME DEFAULT NULL, " +
            "created_by VARCHAR(100) DEFAULT NULL, " +
            "UNIQUE KEY UK_users_username (username), " +
            "UNIQUE KEY UK_users_email (email)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci");

    clinicJdbc.execute("CREATE TABLE IF NOT EXISTS patients (" +
            "patient_id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
            "pin_number VARCHAR(20) NOT NULL, " +
            "first_name VARCHAR(100) NOT NULL, " +
            "last_name VARCHAR(100) DEFAULT NULL, " +
            "date_of_birth DATE NOT NULL, " +
            "age INT DEFAULT NULL, " +
            "gender VARCHAR(255) NOT NULL, " +
            "blood_group VARCHAR(5) DEFAULT NULL, " +
            "contact_number VARCHAR(15) NOT NULL, " +
            "alternate_contact VARCHAR(15) DEFAULT NULL, " +
            "email VARCHAR(100) DEFAULT NULL, " +
            "aadhar_number VARCHAR(12) DEFAULT NULL, " +
            "address_line1 VARCHAR(255) DEFAULT NULL, " +
            "address_line2 VARCHAR(255) DEFAULT NULL, " +
            "city VARCHAR(100) DEFAULT NULL, " +
            "state VARCHAR(100) DEFAULT NULL, " +
            "pincode VARCHAR(10) DEFAULT NULL, " +
            "emergency_contact_name VARCHAR(100) DEFAULT NULL, " +
            "emergency_contact_number VARCHAR(15) DEFAULT NULL, " +
            "emergency_contact_relation VARCHAR(50) DEFAULT NULL, " +
            "insurance_provider VARCHAR(100) DEFAULT NULL, " +
            "insurance_id VARCHAR(50) DEFAULT NULL, " +
            "insurance_expiry_date DATE DEFAULT NULL, " +
            "registration_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "registered_by VARCHAR(100) DEFAULT NULL, " +
            "status VARCHAR(255) NOT NULL, " +
            "photo_url VARCHAR(255) DEFAULT NULL, " +
            "remarks TEXT, " +
            "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated_at DATETIME DEFAULT NULL, " +
            "UNIQUE KEY UK_patients_pin (pin_number), " +
            "UNIQUE KEY UK_patients_aadhar (aadhar_number)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci");

    clinicJdbc.execute("CREATE TABLE IF NOT EXISTS patient_medical_history (" +
            "history_id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
            "patient_id BIGINT NOT NULL, " +
            "allergies TEXT, " +
            "chronic_diseases TEXT, " +
            "past_surgeries TEXT, " +
            "family_history TEXT, " +
            "smoking_status VARCHAR(255) DEFAULT NULL, " +
            "alcohol_consumption VARCHAR(255) DEFAULT NULL, " +
            "blood_pressure VARCHAR(20) DEFAULT NULL, " +
            "height_cm DECIMAL(5,2) DEFAULT NULL, " +
            "weight_kg DECIMAL(5,2) DEFAULT NULL, " +
            "bmi DECIMAL(5,2) DEFAULT NULL, " +
            "last_updated DATETIME DEFAULT NULL, " +
            "UNIQUE KEY UK_pmh_patient_id (patient_id), " +
            "CONSTRAINT FK_pmh_patient FOREIGN KEY (patient_id) REFERENCES patients(patient_id)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci");

    clinicJdbc.execute("CREATE TABLE IF NOT EXISTS doctors (" +
            "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
            "doctor_id VARCHAR(20) NOT NULL, " +
            "user_id BIGINT DEFAULT NULL, " +
            "first_name VARCHAR(100) NOT NULL, " +
            "last_name VARCHAR(100) DEFAULT NULL, " +
            "specialization VARCHAR(100) NOT NULL, " +
            "qualification VARCHAR(255) DEFAULT NULL, " +
            "experience_years INT DEFAULT NULL, " +
            "department VARCHAR(100) DEFAULT NULL, " +
            "contact_number VARCHAR(15) NOT NULL, " +
            "email VARCHAR(100) DEFAULT NULL, " +
            "license_number VARCHAR(50) DEFAULT NULL, " +
            "registration_number VARCHAR(50) DEFAULT NULL, " +
            "consultation_fee DECIMAL(10,2) NOT NULL, " +
            "follow_up_fee DECIMAL(10,2) DEFAULT NULL, " +
            "status VARCHAR(255) NOT NULL, " +
            "available_for_opd TINYINT(1) DEFAULT NULL, " +
            "available_for_emergency TINYINT(1) DEFAULT NULL, " +
            "photo_url VARCHAR(255) DEFAULT NULL, " +
            "bio TEXT, " +
            "languages_spoken VARCHAR(255) DEFAULT NULL, " +
            "room_number VARCHAR(20) DEFAULT NULL, " +
            "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated_at DATETIME DEFAULT NULL, " +
            "created_by VARCHAR(100) DEFAULT NULL, " +
            "UNIQUE KEY UK_doctors_doctor_id (doctor_id), " +
            "UNIQUE KEY UK_doctors_license (license_number)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci");

    clinicJdbc.execute("CREATE TABLE IF NOT EXISTS doctor_schedules (" +
            "schedule_id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
            "doctor_id BIGINT NOT NULL, " +
            "schedule_date DATE NOT NULL, " +
            "start_time TIME NOT NULL, " +
            "end_time TIME NOT NULL, " +
            "slot_duration_minutes INT DEFAULT NULL, " +
            "max_patients_per_slot INT DEFAULT NULL, " +
            "is_active TINYINT(1) DEFAULT NULL, " +
            "break_start_time TIME DEFAULT NULL, " +
            "break_end_time TIME DEFAULT NULL, " +
            "CONSTRAINT FK_ds_doctor FOREIGN KEY (doctor_id) REFERENCES doctors(id)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci");

    clinicJdbc.execute("CREATE TABLE IF NOT EXISTS case_visit_records (" +
            "cvr_id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
            "cvr_number VARCHAR(20) NOT NULL, " +
            "op_case_number VARCHAR(20) DEFAULT NULL, " +
            "patient_id BIGINT NOT NULL, " +
            "pin_number VARCHAR(20) NOT NULL, " +
            "appointment_id VARCHAR(20) DEFAULT NULL, " +
            "appointment_date DATE DEFAULT NULL, " +
            "appointment_time TIME DEFAULT NULL, " +
            "visit_date DATE DEFAULT NULL, " +
            "visit_time TIME DEFAULT NULL, " +
            "visit_type VARCHAR(255) NOT NULL, " +
            "department VARCHAR(100) DEFAULT NULL, " +
            "doctor_id VARCHAR(20) DEFAULT NULL, " +
            "chief_complaint TEXT NOT NULL, " +
            "symptoms TEXT, " +
            "status VARCHAR(255) NOT NULL, " +
            "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "created_by VARCHAR(100) DEFAULT NULL, " +
            "checked_in_at DATETIME DEFAULT NULL, " +
            "consultation_started_at DATETIME DEFAULT NULL, " +
            "consultation_completed_at DATETIME DEFAULT NULL, " +
            "is_billed TINYINT(1) DEFAULT NULL, " +
            "billing_id VARCHAR(20) DEFAULT NULL, " +
            "UNIQUE KEY UK_cvr_number (cvr_number)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci");

    clinicJdbc.execute("CREATE TABLE IF NOT EXISTS cvr_vitals (" +
        "vital_id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
        "cvr_id BIGINT NOT NULL, " +
        "temperature_f DECIMAL(4,2) DEFAULT NULL, " +
        "blood_pressure_systolic INT DEFAULT NULL, " +
        "blood_pressure_diastolic INT DEFAULT NULL, " +
        "pulse_rate INT DEFAULT NULL, " +
        "respiratory_rate INT DEFAULT NULL, " +
        "spo2_percentage INT DEFAULT NULL, " +
        "weight_kg DECIMAL(5,2) DEFAULT NULL, " +
        "height_cm DECIMAL(5,2) DEFAULT NULL, " +
        "bmi DECIMAL(5,2) DEFAULT NULL, " +
        "recorded_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
        "recorded_by VARCHAR(100) DEFAULT NULL, " +
        "CONSTRAINT FK_cvr_vitals_cvr FOREIGN KEY (cvr_id) REFERENCES case_visit_records(cvr_id)" +
        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci");

    clinicJdbc.execute("CREATE TABLE IF NOT EXISTS appointments (" +
            "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
            "appointment_id VARCHAR(20) NOT NULL, " +
            "cvr_id BIGINT DEFAULT NULL, " +
            "cvr_number VARCHAR(20) DEFAULT NULL, " +
            "patient_id BIGINT NOT NULL, " +
            "pin_number VARCHAR(20) NOT NULL, " +
            "doctor_id VARCHAR(20) NOT NULL, " +
            "appointment_date DATE NOT NULL, " +
            "appointment_time TIME NOT NULL, " +
            "slot_id BIGINT DEFAULT NULL, " +
            "token_number INT DEFAULT NULL, " +
            "appointment_type VARCHAR(255) DEFAULT NULL, " +
            "status VARCHAR(255) NOT NULL, " +
            "symptoms TEXT, " +
            "notes TEXT, " +
            "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "created_by VARCHAR(100) DEFAULT NULL, " +
            "checked_in_at DATETIME DEFAULT NULL, " +
            "consultation_started_at DATETIME DEFAULT NULL, " +
            "consultation_ended_at DATETIME DEFAULT NULL, " +
            "cancelled_at DATETIME DEFAULT NULL, " +
            "cancellation_reason TEXT, " +
            "cancelled_by VARCHAR(100) DEFAULT NULL, " +
            "UNIQUE KEY UK_appointments_id (appointment_id)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci");

    clinicJdbc.execute("CREATE TABLE IF NOT EXISTS appointment_slots (" +
            "slot_id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
            "schedule_id BIGINT DEFAULT NULL, " +
            "doctor_id VARCHAR(20) NOT NULL, " +
            "slot_date DATE NOT NULL, " +
            "slot_time TIME NOT NULL, " +
            "is_available TINYINT(1) DEFAULT NULL, " +
            "max_patients INT DEFAULT NULL, " +
            "booked_count INT DEFAULT NULL, " +
            "appointment_id VARCHAR(20) DEFAULT NULL, " +
            "UNIQUE KEY UK_slots_doctor_date_time (doctor_id, slot_date, slot_time)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci");

    clinicJdbc.execute("CREATE TABLE IF NOT EXISTS consultations (" +
            "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
            "consultation_id VARCHAR(20) NOT NULL, " +
            "appointment_id VARCHAR(20) DEFAULT NULL, " +
            "cvr_number VARCHAR(20) DEFAULT NULL, " +
            "patient_id BIGINT NOT NULL, " +
            "pin_number VARCHAR(20) NOT NULL, " +
            "doctor_id VARCHAR(20) NOT NULL, " +
            "consultation_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "subjective TEXT, " +
            "objective TEXT, " +
            "assessment TEXT, " +
            "plan TEXT, " +
            "chief_complaint TEXT NOT NULL, " +
            "present_illness TEXT, " +
            "examination_findings TEXT, " +
            "diagnosis TEXT NOT NULL, " +
            "treatment_plan TEXT, " +
            "vitals_recorded JSON DEFAULT NULL, " +
            "follow_up_required TINYINT(1) DEFAULT NULL, " +
            "follow_up_date DATE DEFAULT NULL, " +
            "follow_up_instructions TEXT, " +
            "status VARCHAR(255) NOT NULL, " +
            "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated_at DATETIME DEFAULT NULL, " +
            "completed_at DATETIME DEFAULT NULL, " +
            "UNIQUE KEY UK_consultations_id (consultation_id)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci");

    clinicJdbc.execute("CREATE TABLE IF NOT EXISTS opd_queue (" +
            "queue_id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
            "appointment_id VARCHAR(20) DEFAULT NULL, " +
            "cvr_number VARCHAR(20) DEFAULT NULL, " +
            "patient_id BIGINT NOT NULL, " +
            "pin_number VARCHAR(20) NOT NULL, " +
            "doctor_id VARCHAR(20) NOT NULL, " +
            "token_number INT NOT NULL, " +
            "queue_date DATE NOT NULL, " +
            "status VARCHAR(255) NOT NULL, " +
            "priority VARCHAR(255) DEFAULT NULL, " +
            "check_in_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "called_at DATETIME DEFAULT NULL, " +
            "consultation_start_time DATETIME DEFAULT NULL, " +
            "consultation_end_time DATETIME DEFAULT NULL, " +
            "waiting_time_minutes INT DEFAULT NULL, " +
            "consultation_duration_minutes INT DEFAULT NULL" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci");

    clinicJdbc.execute("CREATE TABLE IF NOT EXISTS prescriptions (" +
            "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
            "prescription_id VARCHAR(20) NOT NULL, " +
            "consultation_id VARCHAR(255) NOT NULL, " +
            "consultation_number VARCHAR(20) DEFAULT NULL, " +
            "patient_id BIGINT NOT NULL, " +
            "pin_number VARCHAR(20) NOT NULL, " +
            "doctor_id VARCHAR(20) NOT NULL, " +
            "prescription_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "validity_days INT DEFAULT NULL, " +
            "expiry_date DATE DEFAULT NULL, " +
            "instructions TEXT, " +
            "status VARCHAR(255) NOT NULL, " +
            "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "created_by VARCHAR(100) DEFAULT NULL, " +
            "modified_by VARCHAR(100) DEFAULT NULL, " +
            "UNIQUE KEY UK_prescriptions_id (prescription_id), " +
            "KEY idx_prescriptions_patient (patient_id), " +
            "KEY idx_prescriptions_doctor (doctor_id)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci");

    clinicJdbc.execute("CREATE TABLE IF NOT EXISTS prescription_items (" +
            "item_id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
            "prescription_id_fk BIGINT NOT NULL, " +
            "prescription_id VARCHAR(20) DEFAULT NULL, " +
            "pin_number VARCHAR(20) DEFAULT NULL, " +
            "cvr_number VARCHAR(20) DEFAULT NULL, " +
            "cvr_date DATE DEFAULT NULL, " +
            "cvr_time TIME DEFAULT NULL, " +
            "medicine_name VARCHAR(255) NOT NULL, " +
            "dosage VARCHAR(100) NOT NULL, " +
            "frequency VARCHAR(100) NOT NULL, " +
            "duration VARCHAR(50) NOT NULL, " +
            "quantity INT NOT NULL, " +
            "instructions TEXT, " +
            "morning TINYINT(1) DEFAULT NULL, " +
            "afternoon TINYINT(1) DEFAULT NULL, " +
            "evening TINYINT(1) DEFAULT NULL, " +
            "night TINYINT(1) DEFAULT NULL, " +
            "before_food TINYINT(1) DEFAULT NULL, " +
            "after_food TINYINT(1) DEFAULT NULL, " +
            "created_by VARCHAR(100) DEFAULT NULL, " +
            "created_on DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            "modify_by VARCHAR(100) DEFAULT NULL, " +
            "modify_on DATETIME DEFAULT NULL, " +
            "CONSTRAINT FK_pi_prescription FOREIGN KEY (prescription_id_fk) REFERENCES prescriptions(id) ON DELETE CASCADE" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci");

    clinicJdbc.execute("CREATE TABLE IF NOT EXISTS invoices (" +
            "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
            "invoice_number VARCHAR(20) NOT NULL, " +
            "appointment_id VARCHAR(20) DEFAULT NULL, " +
            "cvr_number VARCHAR(20) DEFAULT NULL, " +
            "pin_number VARCHAR(20) NOT NULL, " +
            "patient_id BIGINT NOT NULL, " +
            "doctor_id VARCHAR(20) DEFAULT NULL, " +
            "invoice_type VARCHAR(255) NOT NULL, " +
            "invoice_date DATE NOT NULL, " +
            "due_date DATE DEFAULT NULL, " +
            "sub_total DECIMAL(10,2) NOT NULL, " +
            "discount_amount DECIMAL(10,2) DEFAULT NULL, " +
            "discount_percentage DECIMAL(5,2) DEFAULT NULL, " +
            "tax_amount DECIMAL(10,2) DEFAULT NULL, " +
            "tax_percentage DECIMAL(5,2) DEFAULT NULL, " +
            "total_amount DECIMAL(10,2) NOT NULL, " +
            "paid_amount DECIMAL(10,2) DEFAULT NULL, " +
            "outstanding_amount DECIMAL(10,2) DEFAULT NULL, " +
            "is_insurance_claim TINYINT(1) DEFAULT NULL, " +
            "insurance_provider VARCHAR(255) DEFAULT NULL, " +
            "insurance_claim_amount DECIMAL(10,2) DEFAULT NULL, " +
            "payment_status VARCHAR(255) NOT NULL, " +
            "notes TEXT, " +
            "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "created_by VARCHAR(255) DEFAULT NULL, " +
            "paid_at DATETIME DEFAULT NULL, " +
            "UNIQUE KEY UK_invoices_number (invoice_number)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci");

    clinicJdbc.execute("CREATE TABLE IF NOT EXISTS invoice_items (" +
            "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
            "invoice_id BIGINT NOT NULL, " +
            "item_name VARCHAR(255) NOT NULL, " +
            "description VARCHAR(255) DEFAULT NULL, " +
            "quantity INT NOT NULL, " +
            "unit_price DECIMAL(10,2) NOT NULL, " +
            "amount DECIMAL(10,2) NOT NULL, " +
            "item_type VARCHAR(255) DEFAULT NULL, " +
            "CONSTRAINT FK_inv_items_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(id)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci");

    clinicJdbc.execute("CREATE TABLE IF NOT EXISTS payments (" +
            "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
            "payment_id VARCHAR(20) NOT NULL, " +
            "invoice_id BIGINT NOT NULL, " +
            "invoice_number VARCHAR(20) DEFAULT NULL, " +
            "amount DECIMAL(10,2) NOT NULL, " +
            "payment_mode VARCHAR(255) NOT NULL, " +
            "payment_status VARCHAR(255) NOT NULL, " +
            "transaction_id VARCHAR(100) DEFAULT NULL, " +
            "payment_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "received_by VARCHAR(255) DEFAULT NULL, " +
            "remarks VARCHAR(255) DEFAULT NULL, " +
            "UNIQUE KEY UK_payments_id (payment_id), " +
            "CONSTRAINT FK_payments_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(id)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci");

    // Medicine Master Table
    clinicJdbc.execute("CREATE TABLE IF NOT EXISTS medicines (" +
            "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
            "medicine_name VARCHAR(255) NOT NULL, " +
            "generic_name VARCHAR(255), " +
            "brand_name VARCHAR(255), " +
            "composition VARCHAR(255), " +
            "medicine_type VARCHAR(50), " +
            "strength VARCHAR(50), " +
            "unit VARCHAR(20), " +
            "packaging VARCHAR(100), " +
            "manufacturer VARCHAR(255), " +
            "description TEXT, " +
            "is_active TINYINT(1) DEFAULT 1, " +
            "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
            "created_by VARCHAR(100), " +
            "INDEX idx_medicine_name (medicine_name), " +
            "INDEX idx_brand_name (brand_name)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci");

    log.info("✅ All 17 tables created in database: {}", dbName);
}
    // =====================================================================
    // HELPER: Create admin user in clinic database
    // =====================================================================
    private void createClinicAdmin(String dbName, ClinicDTO clinicDTO) {
        JdbcTemplate clinicJdbc = createClinicJdbcTemplate(dbName);

        String username = clinicDTO.getAdminUsername();

        // ⭐ Check if username already exists
        Integer existingCount = clinicJdbc.queryForObject(
                "SELECT COUNT(*) FROM users WHERE username = ?", Integer.class, username);

        String encodedPassword = passwordEncoder.encode(
                clinicDTO.getAdminPassword() != null ? clinicDTO.getAdminPassword() : "admin123");

        if (existingCount != null && existingCount > 0) {
            // Update existing user's password and ensure ADMIN role
            clinicJdbc.update(
                    "UPDATE users SET password = ?, role = 'ADMIN', status = 'ACTIVE', " +
                            "first_name = COALESCE(?, first_name), last_name = COALESCE(?, last_name), " +
                            "updated_at = NOW() WHERE username = ?",
                    encodedPassword,
                    clinicDTO.getAdminFirstName(),
                    clinicDTO.getAdminLastName(),
                    username);
            log.info("✅ Existing user '{}' updated to ADMIN in database: {}", username, dbName);
        } else {
            // Insert new admin user
            clinicJdbc.update(
                    "INSERT INTO users (username, password, email, role, status, first_name, last_name, created_by) " +
                            "VALUES (?, ?, ?, 'ADMIN', 'ACTIVE', ?, ?, 'SUPER_ADMIN')",
                    username,
                    encodedPassword,
                    clinicDTO.getAdminEmail() != null ? clinicDTO.getAdminEmail()
                            : username + "@" + clinicDTO.getClinicCode() + ".com",
                    clinicDTO.getAdminFirstName() != null ? clinicDTO.getAdminFirstName() : "Admin",
                    clinicDTO.getAdminLastName() != null ? clinicDTO.getAdminLastName() : clinicDTO.getClinicName());
            log.info("✅ New admin '{}' created in database: {}", username, dbName);
        }
    }

    // =====================================================================
    // 10. RBAC: GET ALL MODULES
    // =====================================================================
    public List<Map<String, Object>> getAllModules() {
        return masterJdbcTemplate.queryForList("SELECT * FROM modules ORDER BY module_id ASC");
    }

    // =====================================================================
    // 11. RBAC: GET PERMISSIONS FOR A ROLE
    // =====================================================================
    public List<String> getRolePermissions(String roleName) {
        String sql = "SELECT module_code FROM role_module_mapping WHERE role_name = ?";
        return masterJdbcTemplate.queryForList(sql, String.class, roleName.toUpperCase());
    }

    // =====================================================================
    // 12. RBAC: UPDATE ROLE PERMISSIONS
    // =====================================================================
    @Transactional
    public void updateRolePermissions(String roleName, List<String> moduleCodes) {
        log.info("🔐 Updating permissions for role: {} | Modules: {}", roleName, moduleCodes);
        
        // 1. Delete existing mappings
        masterJdbcTemplate.update("DELETE FROM role_module_mapping WHERE role_name = ?", roleName.toUpperCase());
        
        // 2. Insert new mappings
        if (moduleCodes != null && !moduleCodes.isEmpty()) {
            for (String code : moduleCodes) {
                masterJdbcTemplate.update("INSERT INTO role_module_mapping (role_name, module_code) VALUES (?, ?)",
                        roleName.toUpperCase(), code);
            }
        }
        log.info("✅ Permissions updated for role: {}", roleName);
    }

    // =====================================================================
    // 13. GET ALL ROLES
    // =====================================================================
    public List<String> getAllRoles() {
        return Arrays.stream(User.UserRole.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    // =====================================================================
    // HELPER: Create JdbcTemplate for a specific clinic database
    // =====================================================================
    private JdbcTemplate createClinicJdbcTemplate(String dbName) {
        com.zaxxer.hikari.HikariDataSource ds = new com.zaxxer.hikari.HikariDataSource();
        ds.setJdbcUrl("jdbc:mysql://localhost:3306/" + dbName
                + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
        ds.setUsername("root");
        ds.setPassword("Pass@123");
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setMaximumPoolSize(2);
        return new JdbcTemplate(ds);
    }

    // =====================================================================
    // 14. RBAC: GET USERS FOR A CLINIC
    // =====================================================================
    public List<Map<String, Object>> getClinicUsers(String tenantId) {
        log.info("👥 Fetching users for clinic: {}", tenantId);
        
        // 1. Get database name
        ClinicDTO clinic = getClinicByTenantId(tenantId);
        String dbName = clinic.getDbName();
        
        // 2. Query users from clinic DB using schema prefix
        String sql = "SELECT user_id, username, email, role, first_name, last_name, status FROM `" + dbName + "`.users";
        
        try {
            return masterJdbcTemplate.queryForList(sql);
        } catch (Exception e) {
            log.error("❌ Failed to fetch users for clinic {}: {}", tenantId, e.getMessage());
            return new ArrayList<>();
        }
    }

    // =====================================================================
    // 15. RBAC: GET PERMISSIONS FOR A SPECIFIC USER
    // =====================================================================
    public List<String> getUserPermissions(String tenantId, Long userId) {
        log.info("🔍 Fetching permissions for User ID {} in Clinic {}", userId, tenantId);
        String sql = "SELECT module_code FROM user_module_mapping WHERE tenant_id = ? AND user_id = ?";
        return masterJdbcTemplate.queryForList(sql, String.class, tenantId, userId);
    }

    // =====================================================================
    // 16. RBAC: UPDATE USER PERMISSIONS
    // =====================================================================
    @Transactional
    public void updateUserPermissions(String tenantId, Long userId, List<String> moduleCodes) {
        log.info("📝 Updating permissions for User ID {} in Clinic {}", userId, tenantId);
        
        // 1. Clear existing
        masterJdbcTemplate.update("DELETE FROM user_module_mapping WHERE tenant_id = ? AND user_id = ?", 
                                 tenantId, userId);
        
        // 2. Insert new
        if (moduleCodes != null && !moduleCodes.isEmpty()) {
            for (String code : moduleCodes) {
                masterJdbcTemplate.update("INSERT INTO user_module_mapping (tenant_id, user_id, module_code) VALUES (?, ?, ?)",
                        tenantId, userId, code);
            }
        }
    }
}
