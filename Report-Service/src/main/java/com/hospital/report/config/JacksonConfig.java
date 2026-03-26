/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.report.config;

/**
 *
 * @author mduhijod
 */




import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

@Configuration
public class JacksonConfig {

    /**
     * Handles all LocalDateTime formats from microservices:
     *   "2026-03-07T17:16:34"       (ISO with T)
     *   "2026-03-07T17:16:34.123"   (ISO with T + millis)
     *   "2026-03-07 17:16:34"       (space separator)
     *   "2026-03-07 17:16:34.123"   (space + millis)
     */
    public static class FlexibleLocalDateTimeDeserializer extends StdDeserializer<LocalDateTime> {

        private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd")
                .optionalStart().appendLiteral('T').optionalEnd()
                .optionalStart().appendLiteral(' ').optionalEnd()
                .appendPattern("HH:mm:ss")
                .optionalStart().appendPattern(".SSS").optionalEnd()
                .optionalStart().appendPattern(".SS").optionalEnd()
                .optionalStart().appendPattern(".S").optionalEnd()
                .toFormatter();

        public FlexibleLocalDateTimeDeserializer() { super(LocalDateTime.class); }

        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String text = p.getText();
            if (text == null || text.isEmpty()) return null;
            try {
                return LocalDateTime.parse(text, FORMATTER);
            } catch (Exception e) {
                return LocalDateTime.parse(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
        }
    }

    /**
     * Handles all LocalDate formats from microservices:
     *   "2025-11-04"                (pure date - correct)
     *   "2025-11-04 06:17:48"       (date + time with space - Patient Service sends this)
     *   "2025-11-04T06:17:48"       (date + time with T)
     * Strips the time part and returns only the date.
     */
    public static class FlexibleLocalDateDeserializer extends StdDeserializer<LocalDate> {

        public FlexibleLocalDateDeserializer() { super(LocalDate.class); }

        @Override
        public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String text = p.getText();
            if (text == null || text.isEmpty()) return null;
            // If it contains time part (space or T after date), strip it
            if (text.length() > 10) {
                text = text.substring(0, 10);
            }
            return LocalDate.parse(text, DateTimeFormatter.ISO_LOCAL_DATE);
        }
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        SimpleModule flexibleModule = new SimpleModule();
        flexibleModule.addDeserializer(LocalDateTime.class, new FlexibleLocalDateTimeDeserializer());
        flexibleModule.addDeserializer(LocalDate.class, new FlexibleLocalDateDeserializer());

        return builder -> builder
                .modules(new JavaTimeModule(), flexibleModule)
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}