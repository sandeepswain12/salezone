package com.ecom.salezone;

import com.ecom.salezone.enities.Role;
import com.ecom.salezone.repository.RoleRepository;
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

    public static void main(String[] args) {
        SpringApplication.run(SalezoneApplication.class, args);
    }

    @Override
    public void run(String... args) {

        // ADMIN ROLE
        roleRepository.findById("ROLE_ADMIN")
                .ifPresentOrElse(
                        role -> System.out.println("Admin Role Already Exists"),
                        () -> {
                            Role role = new Role("ROLE_ADMIN", "ADMIN");
                            roleRepository.save(role);
                            System.out.println("Admin Role Created");
                        }
                );

        // USER ROLE
        roleRepository.findById("ROLE_USER")
                .ifPresentOrElse(
                        role -> System.out.println("User Role Already Exists"),
                        () -> {
                            Role role = new Role("ROLE_USER", "USER");
                            roleRepository.save(role);
                            System.out.println("User Role Created");
                        }
                );
    }
}