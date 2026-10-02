package com.nextstep.server.user;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// How Spring Security checks passwords against app_user. "username" here is really the email.
@Service
public class AppUserDetailsService implements UserDetailsService
	{
		private final AppUserRepository users;

		public AppUserDetailsService(AppUserRepository users)
			{
				this.users = users;
			}

		@Override
		public UserDetails loadUserByUsername(String email)
			{
				return users.findByEmailIgnoreCase(email).map(user -> User.withUsername(user.getEmail()).password(user.getPasswordHash()).roles("USER").build()).orElseThrow(() -> new UsernameNotFoundException("Bad credentials"));
			}
	}
