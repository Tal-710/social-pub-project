package com.cyberpro.social_pub_project.service;

public interface IdEncryptionService {

    public String encryptId(Integer idNumber);
    public Integer decryptId(String encryptedId);
}
