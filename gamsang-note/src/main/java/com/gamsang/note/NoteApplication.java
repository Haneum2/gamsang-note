
package com.gamsang.note;

import java.util.Objects;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class NoteApplication {

    public static void main(String[] args) {
        SpringApplication.run(NoteApplication.class, args);
    }

    @Bean
    ApplicationRunner checkTursoSettings(DataSourceProperties props) {
        return args -> {
            String token = System.getenv("TURSO_AUTH_TOKEN");
            String override = System.getenv("SPRING_DATASOURCE_PASSWORD");
            String configured = props.getPassword();

            System.out.println("=== Turso Configuration Check ===");
            System.out.println("TOKEN exists: " +
                    (token != null && !token.isBlank()));
            System.out.println("SPRING PASSWORD exists: " +
                    (override != null && !override.isBlank()));
            System.out.println("Two environment values match: " +
                    (token != null && Objects.equals(token, override)));
            System.out.println("Spring password matches TOKEN: " +
                    (token != null && Objects.equals(configured, token)));
            System.out.println("Spring password is unresolved placeholder: " +
                    Objects.equals(configured, "${TURSO_AUTH_TOKEN}"));
            System.out.println("=================================");
        };
    }
}
