package com.nextstep.server.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

// Lines up with app_user in V1__auth.sql. Leaving tier out for now so the column default just kicks in.
@Entity
@Table(name = "app_user")
public class AppUser
	{
		@Id
		@UuidGenerator(style = UuidGenerator.Style.VERSION_7)
		private UUID id;

		@Column(nullable = false)
		private String email;

		@Column(name = "password_hash", nullable = false)
		private String passwordHash;

		@Column(name = "first_name", nullable = false)
		private String firstName;

		@Column(name = "last_name", nullable = false)
		private String lastName;

		@CreationTimestamp
		@Column(name = "created_at", nullable = false, updatable = false)
		private Instant createdAt;

		@UpdateTimestamp
		@Column(name = "updated_at", nullable = false)
		private Instant updatedAt;

		protected AppUser()
			{
			}

		public AppUser(String email, String passwordHash, String firstName, String lastName)
			{
				this.email = email;
				this.passwordHash = passwordHash;
				this.firstName = firstName;
				this.lastName = lastName;
			}

		public UUID getId()
			{
				return id;
			}

		public String getEmail()
			{
				return email;
			}

		public String getPasswordHash()
			{
				return passwordHash;
			}

		public void setPasswordHash(String passwordHash)
			{
				this.passwordHash = passwordHash;
			}

		public String getFirstName()
			{
				return firstName;
			}

		public String getLastName()
			{
				return lastName;
			}
	}
