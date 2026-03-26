package com.ecom.salezone;

import com.ecom.salezone.enities.Role;
import com.ecom.salezone.repository.RoleRepository;
import com.ecom.salezone.services.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@EnableCaching
@SpringBootApplication
public class SalezoneApplication implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    private static final Logger log = LoggerFactory.getLogger(SalezoneApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SalezoneApplication.class, args);
    }


    @Override
    public void run(String... args) {

            // ADMIN ROLE
            roleRepository.findById("ROLE_ADMIN")
                    .ifPresentOrElse(
                            role -> log.info("Admin Role Already Exists"),
                            () -> {
                                Role role = new Role("ROLE_ADMIN", "ADMIN");
                                roleRepository.save(role);
                                log.info("Admin Role Created");
                            }
                    );

            // USER ROLE
            roleRepository.findById("ROLE_USER")
                    .ifPresentOrElse(
                            role -> log.info("User Role Already Exists"),
                            () -> {
                                Role role = new Role("ROLE_USER", "USER");
                                roleRepository.save(role);
                                log.info("User Role Created");
                            }
                    );
        }
}