package com.nextstep.server.auth;

import com.fasterxml.jackson.annotation.JsonInclude;

// The demo fields don't show up in the JSON when demo mode is off
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AuthConfigResponse(boolean demo, String email, String password, String code)
	{
		public static AuthConfigResponse disabled()
			{
				return new AuthConfigResponse(false, null, null, null);
			}

		public static AuthConfigResponse demo(String email, String password, String code)
			{
				return new AuthConfigResponse(true, email, password, code);
			}
	}
