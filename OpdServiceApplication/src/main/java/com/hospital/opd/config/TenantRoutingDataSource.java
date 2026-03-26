/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.opd.config;

/**
 *
 * @author mduhijod
 */
import com.hospital.opd.util.DatabaseNameResolver;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Component
public class TenantRoutingDataSource extends AbstractRoutingDataSource {

    @Autowired
    private JdbcTemplate masterJdbcTemplate;

    private final Map<Object, Object> tenantDataSources = new HashMap<>();

    @PostConstruct
    public void init() {
        setTargetDataSources(tenantDataSources);
        afterPropertiesSet();
        System.out.println("✅ TenantRoutingDataSource initialized with empty tenant map");
    }

    @Override
    protected Object determineCurrentLookupKey() {
        String tenantId = TenantContext.getTenantId();
        // Don't log here - too noisy, only log in determineTargetDataSource
        return tenantId;
    }

    @Override
    protected DataSource determineTargetDataSource() {
        String tenantId = TenantContext.getTenantId();

        // ⭐ If no tenant context, return default (master) datasource
        // This happens during app startup/initialization
        if (tenantId == null || tenantId.isEmpty()) {
            System.out.println("⚠️  No tenant context - using default datasource (master)");
            return getResolvedDefaultDataSource();
        }

        System.out.println("🔍 Tenant context found: " + tenantId);

        synchronized (tenantDataSources) {
            if (!tenantDataSources.containsKey(tenantId)) {
                System.out.println("🔍 Creating new connection pool for tenant: " + tenantId);

                String sql = "SELECT tenant_id, clinic_code, clinic_name, organization_id, " +
                        "operational_id, db_name, address, phone, " +
                        "email, logo_path, is_active FROM tenants WHERE tenant_id = ? AND is_active = TRUE";

                TenantInfo tenant;
                try {
                    tenant = masterJdbcTemplate.queryForObject(
                            sql,
                            new BeanPropertyRowMapper<>(TenantInfo.class),
                            tenantId);
                } catch (Exception e) {
                    System.err.println("❌ Failed to fetch tenant info from master DB for: " + tenantId);
                    e.printStackTrace();
                    throw new IllegalStateException("❌ Tenant not found or inactive: " + tenantId, e);
                }

                if (tenant == null) {
                    throw new IllegalStateException("❌ Tenant not found: " + tenantId);
                }

                HikariDataSource dataSource = new HikariDataSource();
                dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/" + tenant.getDbName() +
                        "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
                dataSource.setUsername("root");
                dataSource.setPassword("Pass@123");
                dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
                dataSource.setMaximumPoolSize(10);
                dataSource.setMinimumIdle(2);
                dataSource.setConnectionTimeout(30000);
                dataSource.setIdleTimeout(600000);
                dataSource.setMaxLifetime(1800000);

                tenantDataSources.put(tenantId, dataSource);
                setTargetDataSources(tenantDataSources);
                afterPropertiesSet();

                System.out.println(
                        "✅ Connected to database: " + tenant.getDbName() + " for clinic: " + tenant.getClinicName());
                // ⭐ Load database names into resolver
                try {
                    DatabaseNameResolver resolver = new DatabaseNameResolver();
                    resolver.loadTenantDatabases(tenantId);
                } catch (Exception e) {
                    System.err.println("⚠️ Failed to load database resolver for: " + tenantId);
                }
            }
        }

        return (DataSource) tenantDataSources.get(tenantId);
    }
}