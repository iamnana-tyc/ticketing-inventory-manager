package com.iamnana.bookingservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.test.context.SpringBootTest;

// Needs the docker-compose infrastructure (Postgres, Kafka, Keycloak), so it is opt-in:
// RUN_INTEGRATION_TESTS=true ./mvnw test
@EnabledIfEnvironmentVariable(named = "RUN_INTEGRATION_TESTS", matches = "true")
@SpringBootTest
class BookingserviceApplicationTests {

	@Test
	void contextLoads() {
	}

}
