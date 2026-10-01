package com.nextstep.server.auth;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginEventRepository extends JpaRepository<LoginEvent, Long>
	{
	}
