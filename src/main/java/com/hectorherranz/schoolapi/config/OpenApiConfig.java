package com.hectorherranz.schoolapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI schoolApiOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("School Management API")
                .description(
                    """
                                A RESTful API for managing schools and students.

                                ## Features
                                - **Schools Management**: Create, read, update, and delete schools
                                - **Student Enrollment**: Manage student enrollment with capacity validation
                                - **Search Capabilities**: Search schools and students with pagination
                                - **Business Rules**: Enforce school capacity limits and unique names

                                ## Architecture
                                Built with Hexagonal Architecture (Ports & Adapters) and Domain-Driven Design principles.
                                """)
                .version("1.0.0")
                .contact(
                    new Contact()
                        .name("Hector Herranz")
                        .email("hector.herranz@example.com")
                        .url("https://github.com/hectorherranz/school-api"))
                .license(
                    new License().name("MIT License").url("https://opensource.org/licenses/MIT")))
        .servers(
            List.of(
                new Server().url("http://localhost:8080").description("Local Development Server"),
                new Server()
                    .url("https://api.school-management.com")
                    .description("Production Server")));
  }
}
