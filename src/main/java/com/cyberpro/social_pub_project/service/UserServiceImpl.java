package com.cyberpro.social_pub_project.service;

import com.cyberpro.social_pub_project.entity.Authority;
import com.cyberpro.social_pub_project.entity.User;
import com.cyberpro.social_pub_project.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void registerUser(String username, String password, String firstName, String lastName, int age, int idNumber) {
        String hashedPassword = passwordEncoder.encode(password);


        User user = new User(username, hashedPassword, firstName, lastName, age, idNumber);
        addRoleUser(user);
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
    @Transactional
    public void addRoleUser(User user) {
        Authority userAuthority = new Authority(user.getUsername(), "ROLE_USER", user);
        if (user.getAuthorities() == null) {
            user.setAuthorities(new ArrayList<>());
        }
        user.getAuthorities().add(userAuthority);
    }

    @Override
    public Optional<User> findByIdNumber(Integer idNumber) {
        return  userRepository.findByIdNumber(idNumber);
    }

    @Override

    public User save(User user) {
        return userRepository.save(user);
    }

    @Override

    public void deleteById(int id) {
        userRepository.deleteById(id);
    }
}
