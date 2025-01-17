package com.cyberpro.social_pub_project.service;

import com.cyberpro.social_pub_project.entity.User;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@Service
public class QRCodeServiceImpl implements QRCodeService {
    private final AzureBlobServiceImpl azureBlobService;
    private static final Logger logger = LoggerFactory.getLogger(QRCodeServiceImpl.class);

    @Autowired
    public QRCodeServiceImpl(AzureBlobServiceImpl azureBlobService) {
        this.azureBlobService = azureBlobService;
    }

    public String generateAndUploadQRCode(User user) {
        try {
            String qrContent = String.format("http://localhost:8080/users/%d", user.getId());
            logger.info("Generating QR code for user: {}", user.getId());

            // Create QR code
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 350, 350);

            // Convert to image
            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "PNG", baos);
            byte[] imageBytes = baos.toByteArray();

            // Upload to Azure Blob Storage
            String fileName = String.format("qr_code_%d.png", user.getId());
            logger.info("Uploading QR code to Azure Blob Storage: {}", fileName);

            String qrCodeUrl = azureBlobService.uploadQRCode(imageBytes, fileName);
            logger.info("Successfully uploaded QR code: {}", qrCodeUrl);

            return qrCodeUrl;

        } catch (Exception e) {
            logger.error("Failed to generate/upload QR code for user {}: {}", user.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to generate QR code: " + e.getMessage(), e);
        }
    }
}