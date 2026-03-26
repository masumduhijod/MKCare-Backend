package com.hospital.report.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI reportServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("HMIS Report Service API")
                        .description("Aggregated reports for Hospital Management Information System. " +
                                "Fetches data from Patient, CVR, Appointment, Doctor, OPD, and Billing services. " +
                                "No data is saved - reports are generated on-the-fly.")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("HMIS Team")
                                .email("support@hospital.com")))
                .servers(Arrays.asList(
                        new Server().url("http://localhost:8089").description("Local Report Service"),
                        new Server().url("http://localhost:8080/api").description("Via API Gateway")
                ));
    }
}
