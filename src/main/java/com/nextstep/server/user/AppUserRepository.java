package com.nextstep.server.user;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, UUID>
	{
		// Generates lower(email) = lower(?), which uses the app_user_email_key index
		Optional<AppUser> findByEmailIgnoreCase(String email);
	}
