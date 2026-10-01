package com.nextstep.server.auth;

public record LoginRequest(String email, String password, String twofactor)
	{
		// Missing fields become empty strings so they fail as "Bad credentials", not a 500
		public LoginRequest
			{
				email = email == null ? "" : email.trim();
				password = password == null ? "" : password;
				twofactor = twofactor == null ? "" : twofactor.trim();
			}
	}
