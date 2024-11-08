package com.e3.api.product_device;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ProductDeviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductDeviceApplication.class, args);
	}

}
