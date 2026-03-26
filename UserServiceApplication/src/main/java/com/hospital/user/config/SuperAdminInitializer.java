package com.hospital.user.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Ensures super admin password is properly BCrypt-encoded on startup.
 * Runs once at application start.
 */
@Component
@Slf4j
public class SuperAdminInitializer implements ApplicationRunner {

    @Autowired
    @Qualifier("masterJdbcTemplate")
    private JdbcTemplate masterJdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        log.info("🔐 Checking super admin password encoding...");

        try {
            // Check if super_admins table exists
            Integer count = masterJdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM super_admins WHERE username = ?",
                    Integer.class, "mkadmin");

            if (count == null || count == 0) {
                // Insert default super admin with properly encoded password
                String encodedPassword = passwordEncoder.encode("@dmin123");
                masterJdbcTemplate.update(
                        "INSERT INTO super_admins (username, password, full_name, email) VALUES (?, ?, ?, ?)",
                        "mkadmin", encodedPassword, "Master Admin", "superadmin@hms.com");
                log.info("✅ Default super admin created: mkadmin / @dmin123");
            } else {
                // Check if existing password is properly BCrypt-encoded
                String storedPassword = masterJdbcTemplate.queryForObject(
                        "SELECT password FROM super_admins WHERE username = ?",
                        String.class, "mkadmin");

                if (storedPassword == null || !storedPassword.startsWith("$2a$") ||
                        !passwordEncoder.matches("@dmin123", storedPassword)) {
                    // Re-encode the password
                    String encodedPassword = passwordEncoder.encode("@dmin123");
                    masterJdbcTemplate.update(
                            "UPDATE super_admins SET password = ? WHERE username = ?",
                            encodedPassword, "mkadmin");
                    log.info("✅ Super admin password re-encoded successfully for: mkadmin");
                } else {
                    log.info("✅ Super admin password already properly encoded");
                }
            }
        } catch (Exception e) {
            log.warn("⚠️ Could not initialize super admin (table may not exist yet): {}", e.getMessage());
            log.warn("   Run 00_hms_master_NEW.sql first, then restart the service.");
        }
    }
}
