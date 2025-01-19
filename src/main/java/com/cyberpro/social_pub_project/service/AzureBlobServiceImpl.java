package com.cyberpro.social_pub_project.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.models.PublicAccessType;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AzureBlobServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(AzureBlobServiceImpl.class);
    private final BlobServiceClient blobServiceClient;
    private final String containerName = "qrcodes";

    @Autowired
    public AzureBlobServiceImpl(BlobServiceClient blobServiceClient) {
        this.blobServiceClient = blobServiceClient;
    }

    @PostConstruct
    public void initialize() {
        try {
            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
            if (!containerClient.exists()) {
                logger.info("Creating new blob container: {}", containerName);
                containerClient.create();
                // Make container public
                containerClient.setAccessPolicy(PublicAccessType.BLOB, null);
            }
            logger.info("Successfully initialized blob container");
        } catch (Exception e) {
            logger.error("Failed to initialize blob container: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize blob storage", e);
        }
    }

    public String uploadQRCode(byte[] qrCodeBytes, String fileName) {
        if (qrCodeBytes == null || qrCodeBytes.length == 0) {
            throw new IllegalArgumentException("QR code bytes cannot be null or empty");
        }

        BlobClient blobClient = blobServiceClient.getBlobContainerClient(containerName)
                .getBlobClient(fileName);

        try (InputStream inputStream = new ByteArrayInputStream(qrCodeBytes)) {
            logger.info("Starting upload for file: {}", fileName);

            blobClient.upload(inputStream, qrCodeBytes.length, true);

            BlobHttpHeaders headers = new BlobHttpHeaders()
                    .setContentType("image/png");
            blobClient.setHttpHeaders(headers);

            String url = blobClient.getBlobUrl();
            logger.info("Successfully uploaded file: {} to URL: {}", fileName, url);
            return url;
        } catch (IOException e) {
            logger.error("Failed to upload QR code {}: {}", fileName, e.getMessage(), e);
            throw new RuntimeException("Failed to upload QR code: " + e.getMessage(), e);
        }
    }
}