package com.nextstep.server.auth;

import com.nextstep.server.config.DemoProperties;
import com.nextstep.server.user.AppUserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController
	{
		// This only fills in the 2FA box for you, the server still decides if the code is right
		private static final String DEMO_CODE = "123456";

		private final DemoProperties demo;
		private final AuthenticationManager authenticationManager;
		private final AppUserRepository users;
		private final LoginAuditService audit;
		private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

		public AuthController(DemoProperties demo, AuthenticationManager authenticationManager, AppUserRepository users, LoginAuditService audit)
			{
				this.demo = demo;
				this.authenticationManager = authenticationManager;
				this.users = users;
				this.audit = audit;
			}

		// Just reading the CSRF token is enough to get Spring to set the XSRF-TOKEN cookie when the login page loads
		@GetMapping("/config")
		public AuthConfigResponse config(CsrfToken csrfToken)
			{
				csrfToken.getToken();

				if(!demo.enabled()) return AuthConfigResponse.disabled();

				return AuthConfigResponse.demo(demo.email(), demo.password(), DEMO_CODE);
			}

		@PostMapping("/check-creds")
		public CheckCredsResponse checkCreds(@RequestBody CheckCredsRequest body, HttpServletRequest request)
			{
				authenticate(body.email(), body.password(), request);

				// Every account goes through 2FA, the TOTP setup comes later with real users
				return new CheckCredsResponse(true);
			}

		@PostMapping("/login")
		public UserResponse login(@RequestBody LoginRequest body, HttpServletRequest request, HttpServletResponse response)
			{
				// Checking the password again since nothing gets saved between check-creds and login
				Authentication auth = authenticate(body.email(), body.password(), request);

				// Only the demo user gets to skip the code check, real TOTP checking comes later with real users
				if(!demo.isDemoUser(body.email()))
					{
						audit.record(body.email(), LoginOutcome.BAD_TOTP, request);
						throw new BadCredentialsException("Bad credentials");
					}

				startSession(auth, request, response);
				audit.record(body.email(), LoginOutcome.SUCCESS, request);

				return currentUser(auth.getName());
			}

		@GetMapping("/me")
		public UserResponse me(Authentication auth)
			{
				return currentUser(auth.getName());
			}

		// Every failure looks exactly the same to the client
		@ExceptionHandler(AuthenticationException.class)
		public ResponseEntity<Map<String, String>> badCredentials()
			{
				return ResponseEntity.badRequest().body(Map.of("error", "Bad credentials"));
			}

		private Authentication authenticate(String email, String password, HttpServletRequest request)
			{
				try
					{
						return authenticationManager.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(email, password));
					}
				catch(AuthenticationException e)
					{
						audit.record(email, LoginOutcome.BAD_CREDENTIALS, request);
						throw e;
					}
			}

		private void startSession(Authentication auth, HttpServletRequest request, HttpServletResponse response)
			{
				// Getting a new session id on login so nobody can hijack the old one (session fixation)
				if(request.getSession(false) != null) request.changeSessionId();

				SecurityContext context = SecurityContextHolder.createEmptyContext();
				context.setAuthentication(auth);
				SecurityContextHolder.setContext(context);
				securityContextRepository.saveContext(context, request, response);
			}

		private UserResponse currentUser(String email)
			{
				return users.findByEmailIgnoreCase(email).map(user -> new UserResponse(user.getEmail(), user.getFirstName() + " " + user.getLastName())).orElseThrow(() -> new BadCredentialsException("Bad credentials"));
			}
	}
