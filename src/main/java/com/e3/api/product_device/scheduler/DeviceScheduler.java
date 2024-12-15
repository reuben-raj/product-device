package com.e3.api.product_device.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.e3.api.product_device.service.DeviceService;

@Component
@EnableScheduling
public class DeviceScheduler {

    Logger log = LoggerFactory.getLogger(DeviceScheduler.class);

    @Autowired
    private DeviceService deviceService;

    @Scheduled(cron = "${scheduler.brands.expression}")
    public void updateBrands() {
        deviceService.updateBrandsFromSource();
    }

    @Scheduled(cron = "${scheduler.devices.expression}")
    public void updateDevices() {
        deviceService.updateDevicesFromSource();
    }

}
