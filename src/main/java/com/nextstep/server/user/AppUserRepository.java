package com.nextstep.server.user;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, UUID>
	{
		// Spring turns this into lower(email) = lower(?), which hits the app_user_email_key index
		Optional<AppUser> findByEmailIgnoreCase(String email);
	}
