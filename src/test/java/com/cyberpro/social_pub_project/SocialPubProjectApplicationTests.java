package com.cyberpro.social_pub_project;

import com.cyberpro.social_pub_project.service.AzureBlobService;
import com.cyberpro.social_pub_project.service.QRCodeServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class SocialPubProjectApplicationTests {

	@MockitoBean
	private AzureBlobService azureBlobService; // Mock the AzureBlobService
	@MockitoBean
	private QRCodeServiceImpl qrCodeService;

	@Test
	void contextLoads() {
	}

}
