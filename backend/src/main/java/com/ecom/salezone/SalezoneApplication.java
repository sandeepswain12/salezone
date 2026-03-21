package com.ecom.salezone;

import com.ecom.salezone.enities.Role;
import com.ecom.salezone.repository.RoleRepository;
import com.ecom.salezone.services.EmailService;
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

//    @Autowired
//    private EmailService emailService;

    public static void main(String[] args) {
        SpringApplication.run(SalezoneApplication.class, args);
    }

//    public String getWelcomeEmailTemplate(String name) {
//        return """
//        <div style="font-family: Arial, sans-serif; padding: 20px;">
//            <h2 style="color: #4CAF50;">Welcome to SaleZone 🎉</h2>
//
//            <p>Hi %s,</p>
//
//            <p>We’re excited to have you onboard 🚀</p>
//
//            <p>
//                You can now explore products, place orders, and enjoy a seamless shopping experience.
//            </p>
//
//            <hr>
//
//            <p style="font-size: 12px; color: gray;">
//                If you didn’t create this account, please ignore this email.
//            </p>
//
//            <p>Thanks,<br><b>SaleZone Team</b></p>
//        </div>
//    """.formatted(name);
//    }

    @Override
    public void run(String... args) {

//        emailService.sendEmail(
//                "patisubham206@gmail.com",
//                "Welcome to SaleZone 🎉",
//                getWelcomeEmailTemplate("Sandeep")
//        );



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