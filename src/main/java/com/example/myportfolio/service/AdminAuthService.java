package com.example.myportfolio.service;

import com.example.myportfolio.entity.AdminUser;
import com.example.myportfolio.repository.AdminUserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminAuthService {

    private final AdminUserRepository adminUserRepository;

    public AdminAuthService(AdminUserRepository adminUserRepository) {
        this.adminUserRepository = adminUserRepository;
    }

    @PostConstruct
    public void initDefaultAdmin() {
        if (adminUserRepository.count() == 0) {
            // For a real production app, passwords must be hashed (e.g. BCrypt)
            // Sticking to plain text here for simplicity within the scope of this portfolio
            AdminUser defaultAdmin = new AdminUser("admin", "admin123");
            adminUserRepository.save(defaultAdmin);
        }
    }

    public boolean authenticate(String username, String password) {
        Optional<AdminUser> userOpt = adminUserRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            return userOpt.get().getPassword().equals(password);
        }
        return false;
    }

    public boolean updatePassword(String username, String oldPassword, String newPassword) {
        Optional<AdminUser> userOpt = adminUserRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            AdminUser user = userOpt.get();
            if (user.getPassword().equals(oldPassword)) {
                user.setPassword(newPassword);
                adminUserRepository.save(user);
                return true;
            }
        }
        return false;
    }
}
