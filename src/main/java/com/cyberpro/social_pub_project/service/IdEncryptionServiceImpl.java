package com.cyberpro.social_pub_project.service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Base64;

@Service
public class IdEncryptionServiceImpl implements IdEncryptionService {
    private final SecretKeySpec secretKey;
    private static final String ALGORITHM = "AES";

    public IdEncryptionServiceImpl(@Value("${encryption.secret-key}") String secretKey) {
        byte[] key = secretKey.getBytes();
        this.secretKey = new SecretKeySpec(key, ALGORITHM);
    }

    public String encryptId(Integer idNumber) {
        try {
            if (idNumber == null) return null;
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(idNumber.toString().getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting ID", e);
        }
    }

    public Integer decryptId(String encryptedId) {
        try {
            if (encryptedId == null) return null;
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedId));
            return Integer.valueOf(new String(decryptedBytes));
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting ID", e);
        }
    }
}