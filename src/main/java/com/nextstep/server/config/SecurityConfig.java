package com.nextstep.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

@Configuration
public class SecurityConfig
	{
		@Bean
		public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
			{
				http
					.authorizeHttpRequests(auth -> auth
						// /error stays public so rejected requests keep their real status (e.g. 403) instead of becoming 401
						.requestMatchers("/error", "/api/auth/config", "/api/auth/check-creds", "/api/auth/login").permitAll()
						.anyRequest().authenticated())
					// JSON API: no login page or browser popup, just a 401
					.formLogin(AbstractHttpConfigurer::disable)
					.httpBasic(AbstractHttpConfigurer::disable)
					.exceptionHandling(ex -> ex
						.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
					// XSRF-TOKEN cookie; the frontend sends it back in the X-XSRF-TOKEN header on every POST
					.csrf(csrf -> csrf.spa())
					.logout(logout -> logout
						.logoutUrl("/api/auth/logout")
						.deleteCookies("NEXTSTEP_SESSION")
						.logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT)));

				return http.build();
			}

		// BCrypt by default; hashes are stored as {bcrypt}... so the algorithm can be upgraded later
		@Bean
		public PasswordEncoder passwordEncoder()
			{
				return PasswordEncoderFactories.createDelegatingPasswordEncoder();
			}

		// Checks email + password against app_user. Unknown emails still pay the BCrypt cost,
		// so response time does not reveal which accounts exist.
		@Bean
		public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder)
			{
				DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
				provider.setPasswordEncoder(passwordEncoder);

				return new ProviderManager(provider);
			}
	}
