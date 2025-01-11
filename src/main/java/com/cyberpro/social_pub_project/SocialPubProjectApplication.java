package com.cyberpro.social_pub_project;


import de.codecentric.boot.admin.server.config.AdminServerHazelcastAutoConfiguration;
import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication
public class SocialPubProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocialPubProjectApplication.class, args);
	}

}
