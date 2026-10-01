package com.nextstep.server.auth;

import com.nextstep.server.user.AppUser;
import com.nextstep.server.user.AppUserRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.util.UUID;
import org.springframework.stereotype.Service;

// Writes one login_event row per attempt, each in its own short transaction
@Service
public class LoginAuditService
	{
		private static final int MAX_EMAIL = 320;
		private static final int MAX_USER_AGENT = 512;

		private final LoginEventRepository events;
		private final AppUserRepository users;

		public LoginAuditService(LoginEventRepository events, AppUserRepository users)
			{
				this.events = events;
				this.users = users;
			}

		public void record(String email, LoginOutcome outcome, HttpServletRequest request)
			{
				// user_id stays NULL when the email matches no account
				UUID userId = users.findByEmailIgnoreCase(email).map(AppUser::getId).orElse(null);
				InetAddress ip = InetAddress.ofLiteral(request.getRemoteAddr());
				String userAgent = truncate(request.getHeader("User-Agent"), MAX_USER_AGENT);

				events.save(new LoginEvent(userId, truncate(email, MAX_EMAIL), ip, userAgent, outcome));
			}

		private static String truncate(String value, int max)
			{
				if(value == null || value.length() <= max)
					{
						return value;
					}

				return value.substring(0, max);
			}
	}
