package com.nextstep.server.auth;

// Values match the CHECK constraint on login_event.outcome
public enum LoginOutcome
	{
		SUCCESS("success"),
		BAD_CREDENTIALS("bad_credentials"),
		BAD_TOTP("bad_totp"),
		RATE_LIMITED("rate_limited");

		private final String dbValue;

		LoginOutcome(String dbValue)
			{
				this.dbValue = dbValue;
			}

		public String dbValue()
			{
				return dbValue;
			}
	}
