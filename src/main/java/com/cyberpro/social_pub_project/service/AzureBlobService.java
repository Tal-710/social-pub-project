package com.cyberpro.social_pub_project.service;

public interface AzureBlobService {
    String uploadQRCode(byte[] qrCodeBytes, String fileName);
    String uploadProfilePicture(byte[] imageBytes, String fileName);
}
