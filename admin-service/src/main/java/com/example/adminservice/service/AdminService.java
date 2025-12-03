package com.example.adminservice.service;

import com.example.adminservice.model.Admin;
import com.example.adminservice.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    public Admin createAdmin(Admin admin) {
        return adminRepository.save(admin);
    }

    public Optional<Admin> getAdminById(Long id) {
        return adminRepository.findById(id);
    }

    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    public Admin updateAdmin(Long id, Admin adminDetails) {
        Optional<Admin> admin = adminRepository.findById(id);
        if (admin.isPresent()) {
            Admin existingAdmin = admin.get();
            existingAdmin.setName(adminDetails.getName());
            existingAdmin.setEmail(adminDetails.getEmail());
            existingAdmin.setRole(adminDetails.getRole());
            return adminRepository.save(existingAdmin);
        }
        return null;
    }

    public void deleteAdmin(Long id) {
        adminRepository.deleteById(id);
    }

}
