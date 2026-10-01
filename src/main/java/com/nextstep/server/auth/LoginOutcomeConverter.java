package com.nextstep.server.auth;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;

@Converter(autoApply = true)
public class LoginOutcomeConverter implements AttributeConverter<LoginOutcome, String>
	{
		@Override
		public String convertToDatabaseColumn(LoginOutcome outcome)
			{
				return outcome == null ? null : outcome.dbValue();
			}

		@Override
		public LoginOutcome convertToEntityAttribute(String value)
			{
				return Arrays.stream(LoginOutcome.values())
					.filter(outcome -> outcome.dbValue().equals(value))
					.findFirst()
					.orElse(null);
			}
	}
