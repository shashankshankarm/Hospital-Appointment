package com.hospitalappointment;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"app.security.jwt.secret=test-secret-key-with-at-least-thirty-two-chars",
		"app.bootstrap.admin-password="
})
class HospitalAppointmentApplicationTests {

	@Test
	void contextLoads() {
	}

}
