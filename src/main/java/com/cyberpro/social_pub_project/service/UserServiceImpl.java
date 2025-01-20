package com.cyberpro.social_pub_project.service;

import com.cyberpro.social_pub_project.entity.Role;
import com.cyberpro.social_pub_project.entity.User;
import com.cyberpro.social_pub_project.repository.RoleRepository;
import com.cyberpro.social_pub_project.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final QRCodeServiceImpl qrCodeService;
    private final IdEncryptionService idEncryptionService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           BCryptPasswordEncoder passwordEncoder,
                           QRCodeServiceImpl qrCodeService,
                           AzureBlobServiceImpl azureBlobService, IdEncryptionService idEncryptionService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.qrCodeService = qrCodeService;
        this.idEncryptionService = idEncryptionService;
    }

    @Override
    @Transactional
    public void registerUser(String username, String password, String firstName,
                             String lastName, int age, int idNumber, String profilePictureUrl) {
        try {
            String idNumberStr = String.format("%09d", idNumber);

            if (idNumberStr.length() != 9 || !idNumberStr.matches("\\d{9}")) {
                throw new IllegalArgumentException("ID Number must be a 9-digit number (000000001 to 999999999)");
            }

            String encryptedId = idEncryptionService.encryptId(idNumber);

            if (userRepository.findByEncryptedIdNumber(encryptedId).isPresent()) {
                throw new RuntimeException("ID Number already exists");
            }

            String hashedPassword = passwordEncoder.encode(password);

            User user = new User();
            user.setUsername(username);
            user.setPassword(hashedPassword);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setAge(age);
            user.setIdNumber(idNumber);
            user.setEncryptedIdNumber(encryptedId);
            user.setEnabled(0);
            user.setProfilePicture(profilePictureUrl);

            Role userRole = roleRepository.findByRoleName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Default role not found"));
            user.getRoles().add(userRole);

            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Registration failed: " + e.getMessage(), e);
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
        Optional<User> user = userRepository.findById(id);
        user.ifPresent(u -> {
            if (u.getEncryptedIdNumber() != null) {
                Integer decryptedIdNumber = idEncryptionService.decryptId(u.getEncryptedIdNumber());
                u.setIdNumber(decryptedIdNumber);
            }
        });
        return user;
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
        String encryptedId = idEncryptionService.encryptId(idNumber);
        Optional<User> user = userRepository.findByEncryptedIdNumber(encryptedId);
        user.ifPresent(u -> u.setIdNumber(idNumber));
        return user;
    }

    @Override
    public User saveWithQRCode(User user) {
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
    public User save(User user) {
        if (user.getId() != null) {
            Optional<User> existingUser = userRepository.findById(user.getId());
            existingUser.ifPresent(value -> user.setEncryptedIdNumber(value.getEncryptedIdNumber()));
        }

        return userRepository.save(user);
    }

    @Override
    public void deleteById(int id) {
        userRepository.deleteById(id);
    }
}
