package com.nextstep.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nextstep.demo")
public record DemoProperties(boolean enabled, String email, String password)
	{
		// Fail at startup rather than run a demo account with an empty password
		public DemoProperties
			{
				if(enabled && (password == null || password.isBlank()))
					{
						throw new IllegalStateException("Demo mode is on but NEXTSTEP_DEMO_PASSWORD is not set");
					}
			}

		public boolean isDemoUser(String candidateEmail)
			{
				return enabled && email.equalsIgnoreCase(candidateEmail);
			}
	}
