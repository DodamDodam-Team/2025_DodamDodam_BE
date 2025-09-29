package com.dodamdodam.dodamdodam;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@OpenAPIDefinition(servers = {@Server(url = "/", description = "Default Server URL")})
class DodamdodamApplicationTests {

	@Test
	void contextLoads() {
	}

}
