package com.nextstep.server.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.net.InetAddress;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

// Maps login_event from V1__auth.sql. Append-only: rows are inserted, never updated.
@Entity
@Table(name = "login_event")
public class LoginEvent
	{
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Long id;

		@Column(name = "user_id")
		private UUID userId;

		@Column(name = "email_attempted", nullable = false)
		private String emailAttempted;

		@JdbcTypeCode(SqlTypes.INET)
		@Column(nullable = false)
		private InetAddress ip;

		@Column(name = "user_agent")
		private String userAgent;

		@Column(nullable = false)
		private LoginOutcome outcome;

		@CreationTimestamp
		@Column(name = "occurred_at", nullable = false, updatable = false)
		private Instant occurredAt;

		protected LoginEvent()
			{
			}

		public LoginEvent(UUID userId, String emailAttempted, InetAddress ip, String userAgent, LoginOutcome outcome)
			{
				this.userId = userId;
				this.emailAttempted = emailAttempted;
				this.ip = ip;
				this.userAgent = userAgent;
				this.outcome = outcome;
			}
	}
