package com.nextstep.server.auth;

public record CheckCredsRequest(String email, String password)
	{
		// Turning missing fields into empty strings so they fail as "Bad credentials" instead of a 500
		public CheckCredsRequest
			{
				email = email == null ? "" : email.trim();
				password = password == null ? "" : password;
			}
	}
