package com.e3.api.product_device.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class DeviceConfig {

    @Value("${device.datasource.url}")
    private String deviceDataUrl;

    @Bean
    public String deviceDataUrl() {
        return deviceDataUrl;
    }
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
