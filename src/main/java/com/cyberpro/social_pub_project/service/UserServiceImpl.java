package com.cyberpro.social_pub_project.service;

import com.cyberpro.social_pub_project.entity.Role;
import com.cyberpro.social_pub_project.entity.User;
import com.cyberpro.social_pub_project.repository.RoleRepository;
import com.cyberpro.social_pub_project.repository.UserRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final QRCodeServiceImpl qrCodeService;
    private final AzureBlobServiceImpl azureBlobService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           BCryptPasswordEncoder passwordEncoder,
                           QRCodeServiceImpl qrCodeService,
                           AzureBlobServiceImpl azureBlobService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.qrCodeService = qrCodeService;
        this.azureBlobService = azureBlobService;
    }

    @Override
    @Transactional
    public void registerUser(String username, String password, String firstName, String lastName, int age, int idNumber) {
        // First encrypt the password
        String hashedPassword = passwordEncoder.encode(password);

        User user = new User();
        user.setUsername(username);
        user.setPassword(hashedPassword);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setAge(age);
        user.setIdNumber(idNumber);
        user.setEnabled(0);  // Set enabled by default

        Role userRole = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Default role not found"));
        user.getRoles().add(userRole);

        // Save user first to get ID
        User savedUser = userRepository.save(user);

        // Generate and set QR code
        try {
            String localUrl = String.format("http://localhost:8080/users/%d", savedUser.getId());
            String qrCodeUrl = qrCodeService.generateAndUploadQRCode(savedUser);
            savedUser.setQrCode(qrCodeUrl);
            userRepository.save(savedUser);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }

    @Override
    public void addUserRole(User user, String roleName) {
        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
        user.getRoles().add(role);
        userRepository.save(user);
    }

    @Override
    public void removeUserRole(User user, String roleName) {
        user.getRoles().removeIf(role -> role.getRoleName().equals(roleName));
        userRepository.save(user);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(int id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public void addRoleUser(User user) {

    }

    @Override
    public Optional<User> findByIdNumber(Integer idNumber) {
        return userRepository.findByIdNumber(idNumber);
    }

    @Override
    public User save(User user) {
        User savedUser = userRepository.save(user);
        try {
            String qrCodeUrl = qrCodeService.generateAndUploadQRCode(savedUser);
            savedUser.setQrCode(qrCodeUrl);
            return userRepository.save(savedUser);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }

    @Override
    public void deleteById(int id) {
        userRepository.deleteById(id);
    }
}
