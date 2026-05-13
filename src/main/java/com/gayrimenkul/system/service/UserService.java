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
import java.util.HashSet;
import java.util.Set;

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
    public User createUserForRegistration(User user, boolean wantsToPostListing) {
        return createUser(user, wantsToPostListing ? "AGENT" : "USER");
    }

    @Transactional
    public User createUser(User user, String requestedRole) {
        validateRequired(user.getUsername(), "Kullanici adi zorunludur.");
        validateRequired(user.getEmail(), "Email zorunludur.");
        validateRequired(user.getPassword(), "Sifre zorunludur.");

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Bu kullanici adi zaten alinmis!");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Bu email adresi ile zaten kayit olunmus!");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getActive() == null) {
            user.setActive(true);
        }
        Set<Role> requestedRoles = user.getRoles() == null ? new HashSet<>() : new HashSet<>(user.getRoles());
        user.getRoles().clear();

        try {
            if (requestedRole != null) {
                user.getRoles().add(roleService.getRoleByName(resolveRegistrationRole(requestedRole)));
            } else if (requestedRoles != null && !requestedRoles.isEmpty()) {
                for (Role requested : requestedRoles) {
                    Role role = requested.getId() != null
                            ? roleService.getRoleById(requested.getId())
                            : roleService.getRoleByName(normalizeRoleName(requested.getName()));
                    user.getRoles().add(role);
                }
            } else {
                user.getRoles().add(roleService.getRoleByName("USER"));
            }
        } catch (RuntimeException e) {
            log.warn("Kayit rolu bulunamadi, kullanici rolsuz kaydediliyor.");
        }

        return userRepository.save(user);
    }

    private String resolveRegistrationRole(String requestedRole) {
        String normalized = requestedRole == null
                ? "USER"
                : requestedRole.trim().replace("ROLE_", "").toUpperCase();
        return "AGENT".equals(normalized) ? "AGENT" : "USER";
    }

    private void validateRequired(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new RuntimeException(message);
        }
    }

    private String normalizeRoleName(String roleName) {
        if (roleName == null || roleName.trim().isEmpty()) {
            return "USER";
        }
        return roleName.trim().replace("ROLE_", "").toUpperCase();
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
        if (userDetails.getUsername() != null && !userDetails.getUsername().trim().isEmpty()
                && !userDetails.getUsername().equals(existingUser.getUsername())) {
            if (userRepository.existsByUsername(userDetails.getUsername())) {
                throw new RuntimeException("Bu kullanici adi zaten alinmis!");
            }
            existingUser.setUsername(userDetails.getUsername());
        }
        if (userDetails.getEmail() != null && !userDetails.getEmail().trim().isEmpty()
                && !userDetails.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmail(userDetails.getEmail())) {
                throw new RuntimeException("Bu email adresi ile zaten kayit olunmus!");
            }
            existingUser.setEmail(userDetails.getEmail());
        }
        existingUser.setFullName(userDetails.getFullName());
        existingUser.setPhone(userDetails.getPhone());
        if (userDetails.getActive() != null) {
            existingUser.setActive(userDetails.getActive());
        }
        if (userDetails.getPassword() != null && !userDetails.getPassword().trim().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        if (userDetails.getRoles() != null && !userDetails.getRoles().isEmpty()) {
            existingUser.getRoles().clear();
            for (Role requested : userDetails.getRoles()) {
                Role role = requested.getId() != null
                        ? roleService.getRoleById(requested.getId())
                        : roleService.getRoleByName(normalizeRoleName(requested.getName()));
                existingUser.getRoles().add(role);
            }
        }
        return userRepository.save(existingUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

@Transactional
public void changeUserPassword(String username, String oldPassword, String newPassword) {
    User user = getUserByUsername(username);

    // Eski şifre doğru mu kontrol et
    if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
        throw new RuntimeException("Eski şifreniz hatalı!");
    }

    // Yeni şifreyi encode edip kaydet
    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);
}
}
