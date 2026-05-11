package com.gayrimenkul.system.security;

import com.gayrimenkul.system.entity.Role;
import com.gayrimenkul.system.entity.User;
import com.gayrimenkul.system.repository.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public void run(String... args) {
        Role adminRole = ensureRole("ADMIN");
        ensureRole("AGENT");
        ensureRole("USER");

        if (!userRepository.existsByUsername("admin")) {
            log.info("Sistemde admin bulunamadi, otomatik olusturuluyor...");

            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setFullName("Sistem Yoneticisi");
            adminUser.setEmail("admin@gayrimenkul.com");
            adminUser.getRoles().add(adminRole);

            userRepository.save(adminUser);
            log.info("Admin kullanicisi olusturuldu. Kullanici adi: admin | Sifre: admin123");
        } else {
            log.info("Admin kullanicisi zaten sistemde mevcut.");
        }
    }

    private Role ensureRole(String name) {
        try {
            return entityManager.createQuery("SELECT r FROM Role r WHERE r.name = :name", Role.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (Exception e) {
            Role role = new Role();
            role.setName(name);
            entityManager.persist(role);
            return role;
        }
    }
}
