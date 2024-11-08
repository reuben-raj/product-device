package com.e3.api.product_device;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("unit")
class ProductDeviceApplicationTests {

	// @Test
	// void contextLoads() {
	// }

	@Test
	void testMain() {
		System.setProperty("spring.profiles.active", "unit");

		ProductDeviceApplication.main(new String[] {});
	}

}
