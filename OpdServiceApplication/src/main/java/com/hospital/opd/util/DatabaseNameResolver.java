/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.opd.util;

/**
 *
 * @author mduhijod
 */
import com.hospital.opd.config.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Resolves tenant-specific database name.
 * ✅ FIXED: Updated for Single-DB architecture.
 * Previously used separate opd_db / appointments_db columns (old multi-db design).
 * Now uses single db_name column from hms_master.tenants table.
 */
@Component
public class DatabaseNameResolver {

    @Autowired
    private JdbcTemplate masterJdbcTemplate;

    // Cache: tenantId -> db_name
    private static final Map<String, String> tenantDatabases = new HashMap<>();

    /**
     * Get database name for current tenant.
     * ✅ FIXED: Both OPD and Appointments are in the same single DB per clinic.
     */
    public static String getDb() {
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            return "clinic_hms"; // Default fallback
        }
        String db = tenantDatabases.get(tenantId);
        return db != null ? db : "clinic_hms";
    }

    /**
     * @deprecated Use getDb() instead. Kept for backward compatibility.
     * Both OPD and Appointments now share the same single DB per clinic.
     */
    @Deprecated
    public static String getOpdDb() {
        return getDb();
    }

    /**
     * @deprecated Use getDb() instead. Kept for backward compatibility.
     * Both OPD and Appointments now share the same single DB per clinic.
     */
    @Deprecated
    public static String getAppointmentsDb() {
        return getDb();
    }

    /**
     * Load tenant database name from master DB.
     * ✅ FIXED: Now reads single db_name column instead of opd_db / appointments_db.
     */
    public void loadTenantDatabases(String tenantId) {
        if (tenantDatabases.containsKey(tenantId)) {
            return; // Already cached
        }

        // ✅ FIXED: Single db_name column (was: "SELECT opd_db, appointments_db FROM tenants WHERE tenant_id = ?")
        String sql = "SELECT db_name FROM tenants WHERE tenant_id = ?";

        try {
            Map<String, Object> result = masterJdbcTemplate.queryForMap(sql, tenantId);
            String dbName = (String) result.get("db_name");

            tenantDatabases.put(tenantId, dbName);

            System.out.println("✅ [DatabaseResolver] Loaded DB for " + tenantId + ": " + dbName);
        } catch (Exception e) {
            System.err.println("❌ [DatabaseResolver] Failed to load DB for: " + tenantId);
            e.printStackTrace();
        }
    }

    /**
     * Clear cache (useful for testing or tenant refresh)
     */
    public static void clearCache() {
        tenantDatabases.clear();
    }
}