package com.cyberpro.social_pub_project.entity;


import jakarta.persistence.*;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;

    @Column(name = "username" ,unique = true, nullable = false)
    private String username;

    @Column(name="password",nullable = false)
    private String password;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "age",nullable = false)
    private Integer age;

    @Column(name = "id_number", unique = true, nullable = false)
    private String idNumber;

    @Column(name = "profile_picture")
    private String profilePicture;

    @Column(name = "qr_code", unique = true, nullable = false)
    private String qrCode;

    @Column(name = "enabled")
    private int enabled = 0;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Authority> authorities;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public int getEnabled() {
        return enabled;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    public List<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<Authority> authorities) {
        this.authorities = authorities;
    }

    public User(){

    }

    public User(Integer id, String username, String password, Integer age, String firstName, String idNumber, String profilePicture, String qrCode, int enabled, List<Authority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.age = age;
        this.firstName = firstName;
        this.idNumber = idNumber;
        this.profilePicture = profilePicture;
        this.qrCode = qrCode;
        this.enabled = enabled;
        this.authorities = authorities;
    }
}
