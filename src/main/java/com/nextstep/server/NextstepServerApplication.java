package com.nextstep.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;

// Turning off Spring's built in test user since the accounts come from app_user
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
@ConfigurationPropertiesScan
public class NextstepServerApplication
	{
		public static void main(String[] args)
			{
				SpringApplication.run(NextstepServerApplication.class, args);
			}
	}
