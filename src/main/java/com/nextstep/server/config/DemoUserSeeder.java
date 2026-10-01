package com.nextstep.server.config;

import com.nextstep.server.user.AppUser;
import com.nextstep.server.user.AppUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// Makes sure the demo account exists, with the password from config, whenever demo mode is on
@Component
public class DemoUserSeeder implements ApplicationRunner
	{
		private static final Logger log = LoggerFactory.getLogger(DemoUserSeeder.class);

		private final DemoProperties demo;
		private final AppUserRepository users;
		private final PasswordEncoder passwordEncoder;

		public DemoUserSeeder(DemoProperties demo, AppUserRepository users, PasswordEncoder passwordEncoder)
			{
				this.demo = demo;
				this.users = users;
				this.passwordEncoder = passwordEncoder;
			}

		@Override
		@Transactional
		public void run(ApplicationArguments args)
			{
				if(!demo.enabled())
					{
						return;
					}

				users.findByEmailIgnoreCase(demo.email()).ifPresentOrElse(user ->
					{
						// Password changed in config since last start: re-hash it
						if(!passwordEncoder.matches(demo.password(), user.getPasswordHash()))
							{
								user.setPasswordHash(passwordEncoder.encode(demo.password()));
								log.info("Demo user {} password updated", demo.email());
							}
					}, () ->
					{
						users.save(new AppUser(demo.email(), passwordEncoder.encode(demo.password()), "Demo", "User"));
						log.info("Demo user {} created", demo.email());
					});
			}
	}
