package com.gayrimenkul.system.service;

import com.gayrimenkul.system.entity.Role;
import com.gayrimenkul.system.entity.User;
import com.gayrimenkul.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User createUser(User user) {
        return createUser(user, null);
    }

    @Transactional
    public User createUser(User user, String requestedRole) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Bu kullanici adi zaten alinmis!");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Bu email adresi ile zaten kayit olunmus!");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.getRoles().clear();

        try {
            Role role = roleService.getRoleByName(resolveRegistrationRole(requestedRole));
            user.getRoles().add(role);
        } catch (RuntimeException e) {
            log.warn("Kayit rolu bulunamadi, kullanici rolsuz kaydediliyor.");
        }

        return userRepository.save(user);
    }

    private String resolveRegistrationRole(String requestedRole) {
        String normalized = requestedRole == null
                ? "USER"
                : requestedRole.trim().replace("ROLE_", "").toUpperCase(Locale.ROOT);
        return "AGENT".equals(normalized) ? "AGENT" : "USER";
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kullanici bulunamadi! ID: " + id));
    }

    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Kullanici bulunamadi! Username: " + username));
    }

    @Transactional(readOnly = true)
    public User getUserByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new RuntimeException("Kullanici bulunamadi!"));
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User updateUser(Long id, User userDetails) {
        User existingUser = getUserById(id);
        existingUser.setFullName(userDetails.getFullName());
        existingUser.setPhone(userDetails.getPhone());
        existingUser.setEmail(userDetails.getEmail());
        return userRepository.save(existingUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }
}
