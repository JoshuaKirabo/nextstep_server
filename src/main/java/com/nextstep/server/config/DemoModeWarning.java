package com.nextstep.server.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class DemoModeWarning
	{
		private static final Logger log = LoggerFactory.getLogger(DemoModeWarning.class);

		private final DemoProperties demo;

		public DemoModeWarning(DemoProperties demo)
			{
				this.demo = demo;
			}

		@EventListener(ApplicationReadyEvent.class)
		public void warnIfEnabled()
			{
				if(demo.enabled())
					{
						log.warn("DEMO MODE ENABLED: user {} can log in without a 2FA code", demo.email());
					}
			}
	}
