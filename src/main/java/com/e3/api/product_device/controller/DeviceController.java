package com.e3.api.product_device.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.e3.api.product_device.dto.DeviceRequest;
import com.e3.api.product_device.model.Brand;
import com.e3.api.product_device.model.Device;
import com.e3.api.product_device.service.DeviceService;

@RestController
@RequestMapping("/device")
public class DeviceController {

    Logger logger = LoggerFactory.getLogger(DeviceController.class);

    @Autowired
    private DeviceService deviceService;

    @GetMapping("/brands")
    public ResponseEntity<Page<Brand>> getAllBrands(
        @RequestParam(defaultValue = "0") int page, 
        @RequestParam(defaultValue = "10") int size) {
        Page<Brand> brands = deviceService.getBrands(page, size);
        return ResponseEntity.ok(brands);
    }

    @GetMapping("/devices")
    public ResponseEntity<Page<Device>> getAllDevices(
        @RequestParam(defaultValue = "0") int page, 
        @RequestParam(defaultValue = "10") int size) {
        Page<Device> devices = deviceService.getDevices(page, size);
        return ResponseEntity.ok(devices);
    }

    @PostMapping("/search")
    public ResponseEntity<?> searchDevices(@RequestBody DeviceRequest deviceRequest) {
        try {
            List<Device> devices = deviceService.searchDevices(deviceRequest);
            logger.info("devices size :"+devices.size());
            if(devices.size() < 1) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(devices);
        } catch(Exception e) {
            logger.error("Internal server error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

}

