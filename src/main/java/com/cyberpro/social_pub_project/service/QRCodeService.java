package com.cyberpro.social_pub_project.service;

import com.cyberpro.social_pub_project.entity.User;

public interface QRCodeService {

    String generateAndUploadQRCode(User user);
}
