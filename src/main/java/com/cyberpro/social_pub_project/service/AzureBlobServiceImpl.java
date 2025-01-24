package com.cyberpro.social_pub_project.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import java.time.OffsetDateTime;

@Service
public class AzureBlobServiceImpl implements AzureBlobService {
    private static final Logger logger = LoggerFactory.getLogger(AzureBlobServiceImpl.class);
    private final BlobServiceClient blobServiceClient;

    @Value("${azure.storage.sas-token-validity-hours:1}")
    private int sasTokenValidityHours;

    private final String qrCodesContainer = "qrcodes";
    private final String profilePicsContainer = "profile-pictures";

    @Autowired
    public AzureBlobServiceImpl(BlobServiceClient blobServiceClient) {
        this.blobServiceClient = blobServiceClient;
    }

    @PostConstruct
    public void initialize() {
        try {
            BlobContainerClient qrContainerClient = blobServiceClient.getBlobContainerClient(qrCodesContainer);
            if (!qrContainerClient.exists()) {
                logger.info("Creating new blob container: {}", qrCodesContainer);
                qrContainerClient.create();
            }
            qrContainerClient.setAccessPolicy(null, null);
            logger.info("Set private access for QR codes container");

            BlobContainerClient profileContainerClient = blobServiceClient.getBlobContainerClient(profilePicsContainer);
            if (!profileContainerClient.exists()) {
                logger.info("Creating new blob container: {}", profilePicsContainer);
                profileContainerClient.create();
            }
            profileContainerClient.setAccessPolicy(null, null);
            logger.info("Set private access for profile pictures container");

            logger.info("Successfully initialized blob containers");
        } catch (Exception e) {
            logger.error("Failed to initialize blob containers: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize blob storage", e);
        }
    }

    private String generateSasToken(BlobClient blobClient) {
        OffsetDateTime expiryTime = OffsetDateTime.now().plusHours(sasTokenValidityHours);

        BlobSasPermission permission = new BlobSasPermission()
                .setReadPermission(true);

        BlobServiceSasSignatureValues values = new BlobServiceSasSignatureValues(
                expiryTime,
                permission)
                .setStartTime(OffsetDateTime.now());

        return blobClient.generateSas(values);
    }

    public String getProfilePictureUrl(String fileName) {
        try {
            BlobClient blobClient = blobServiceClient
                    .getBlobContainerClient(profilePicsContainer)
                    .getBlobClient(fileName);

            String sasToken = generateSasToken(blobClient);
            return blobClient.getBlobUrl() + "?" + sasToken;
        } catch (Exception e) {
            logger.error("Failed to generate profile picture URL: {}", e.getMessage(), e);
            return null;
        }
    }

    public String getQrCodeUrl(String fileName) {
        try {
            BlobClient blobClient = blobServiceClient
                    .getBlobContainerClient(qrCodesContainer)
                    .getBlobClient(fileName);

            String sasToken = generateSasToken(blobClient);
            return blobClient.getBlobUrl() + "?" + sasToken;
        } catch (Exception e) {
            logger.error("Failed to generate QR code URL: {}", e.getMessage(), e);
            return null;
        }
    }

    public String uploadQRCode(byte[] qrCodeBytes, String fileName) {
        if (qrCodeBytes == null || qrCodeBytes.length == 0) {
            throw new IllegalArgumentException("QR code bytes cannot be null or empty");
        }

        BlobClient blobClient = blobServiceClient.getBlobContainerClient(qrCodesContainer)
                .getBlobClient(fileName);

        try (InputStream inputStream = new ByteArrayInputStream(qrCodeBytes)) {
            logger.info("Starting upload for file: {}", fileName);

            blobClient.upload(inputStream, qrCodeBytes.length, true);

            BlobHttpHeaders headers = new BlobHttpHeaders()
                    .setContentType("image/png");
            blobClient.setHttpHeaders(headers);

            logger.info("Successfully uploaded file: {}", fileName);
            return fileName;
        } catch (IOException e) {
            logger.error("Failed to upload QR code {}: {}", fileName, e.getMessage(), e);
            throw new RuntimeException("Failed to upload QR code: " + e.getMessage(), e);
        }
    }

    public String uploadProfilePicture(byte[] imageBytes, String fileName) {
        if (imageBytes == null || imageBytes.length == 0) {
            throw new IllegalArgumentException("Image bytes cannot be null or empty");
        }

        BlobClient blobClient = blobServiceClient.getBlobContainerClient(profilePicsContainer)
                .getBlobClient(fileName);

        try (InputStream inputStream = new ByteArrayInputStream(imageBytes)) {
            logger.info("Starting upload for profile picture: {}", fileName);

            blobClient.upload(inputStream, imageBytes.length, true);

            BlobHttpHeaders headers = new BlobHttpHeaders()
                    .setContentType("image/jpeg");
            blobClient.setHttpHeaders(headers);

            logger.info("Successfully uploaded profile picture: {}", fileName);
            return fileName;
        } catch (IOException e) {
            logger.error("Failed to upload profile picture {}: {}", fileName, e.getMessage(), e);
            throw new RuntimeException("Failed to upload profile picture: " + e.getMessage(), e);
        }
    }
}








