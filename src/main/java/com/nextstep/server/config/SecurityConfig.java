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
				// Leaving /error public so a blocked request keeps its real status (like 403) instead of turning into a 401
				http.authorizeHttpRequests(auth -> auth.requestMatchers("/error", "/api/auth/config", "/api/auth/check-creds", "/api/auth/login").permitAll().anyRequest().authenticated());

				// It's a JSON API so no login page or browser popup, just send back a 401
				http.formLogin(AbstractHttpConfigurer::disable);
				http.httpBasic(AbstractHttpConfigurer::disable);
				http.exceptionHandling(ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));

				// Puts the token in an XSRF-TOKEN cookie and the frontend sends it back in the X-XSRF-TOKEN header on every POST
				http.csrf(csrf -> csrf.spa());
				http.logout(logout -> logout.logoutUrl("/api/auth/logout").deleteCookies("NEXTSTEP_SESSION").logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT)));

				return http.build();
			}

		// Uses BCrypt, and hashes get saved as {bcrypt}... so we can switch the algorithm later if we need to
		@Bean
		public PasswordEncoder passwordEncoder()
			{
				return PasswordEncoderFactories.createDelegatingPasswordEncoder();
			}

		// Checks the email and password against app_user. Emails we don't know still go through BCrypt
		// so nobody can tell which accounts exist by how long the response takes.
		@Bean
		public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder)
			{
				DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
				provider.setPasswordEncoder(passwordEncoder);

				return new ProviderManager(provider);
			}
	}
