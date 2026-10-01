package com.nextstep.server.auth;

public record CheckCredsRequest(String email, String password)
	{
		// Missing fields become empty strings so they fail as "Bad credentials", not a 500
		public CheckCredsRequest
			{
				email = email == null ? "" : email.trim();
				password = password == null ? "" : password;
			}
	}
