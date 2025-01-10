package com.cyberpro.social_pub_project.entity;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.PersistenceConstructor;

import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;


    @Column(name = "username", unique = true, nullable = false)
    @NotNull(message = "Username is required")
    private String username;

    @Column(name="password",nullable = false)
    private String password;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "age",nullable = false)
    @Min(value = 18, message = "Must be at least 18 years old")
    private Integer age;

    @NotNull(message = "ID Number is required")
    @Column( name = "id_number", nullable = false, unique = true)
    private Integer idNumber;

    @Column(name = "profile_picture")
    private String profilePicture;

    @Column(name = "qr_code", unique = false, nullable = true)
    private String qrCode;

    @Column(name = "enabled")
    private int enabled = 1;

    public User(){

    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Authority> authorities;

    @PersistenceConstructor
    public User(String username, String password, String firstName, String lastName, int age, int idNumber) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.idNumber = idNumber;
    }

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

    public String getLastName() {return lastName;}

    public void setLastName(String lastName) {this.lastName = lastName;}

    public Integer getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(Integer idNumber) {
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

//    public User(String lastName){
//
//        this.lastName = lastName;
//    }
//
//    public User(Integer id, String username, String password, Integer age, String firstName, String lastName, int idNumber, String profilePicture, String qrCode, int enabled, List<Authority> authorities) {
//        this.id = id;
//        this.username = username;
//        this.password = password;
//        this.age = age;
//        this.firstName = firstName;
//        this.lastName = lastName;
//        this.idNumber = idNumber;
//        this.profilePicture = profilePicture;
//        this.qrCode = qrCode;
//        this.enabled = enabled;
//        this.authorities = authorities;
//    }
}
