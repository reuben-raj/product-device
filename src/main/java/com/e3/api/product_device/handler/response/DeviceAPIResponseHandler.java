package com.e3.api.product_device.handler.response;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.e3.api.product_device.dto.Brand;
import com.e3.api.product_device.dto.BrandResponse;
import com.e3.api.product_device.dto.Device;
import com.e3.api.product_device.dto.DeviceAPIResponseWrapper;
import com.e3.api.product_device.dto.DeviceListResponse;
import com.e3.api.product_device.dto.DeviceResponse;

public class DeviceAPIResponseHandler {
    
    static Logger logger = LoggerFactory.getLogger(DeviceAPIResponseHandler.class);

    public static ResponseEntity<?> handleBrandResponse(ResponseEntity<DeviceAPIResponseWrapper<List<BrandResponse>>> responseEntity) {
        DeviceAPIResponseWrapper<List<BrandResponse>> responseBody = responseEntity.getBody();
        if (responseBody == null || responseEntity.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.status(responseEntity.getStatusCode()).body("Failed to retrieve data");
        }

        if (responseBody.getStatus() != 200) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error from third-party service");
        }

        List<BrandResponse> brandResponses = responseBody.getData();
        List<Brand> brands = brandResponses.stream()
                .map(brandResponse -> {
                    Brand brand = new Brand();
                    brand.setBrandId(brandResponse.getBrandId());
                    brand.setBrandName(brandResponse.getBrandName());
                    brand.setKey(brandResponse.getKey());
                    return brand;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(brands);
    }

    public static ResponseEntity<?> handleDeviceResponse(ResponseEntity<DeviceAPIResponseWrapper<DeviceListResponse>> responseEntity) {
        DeviceAPIResponseWrapper<DeviceListResponse> responseBody = responseEntity.getBody();
        if (responseBody == null || responseEntity.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.status(responseEntity.getStatusCode()).body("Failed to retrieve data");
        }

        if (responseBody.getStatus() != 200) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error from third-party service");
        }

        DeviceListResponse deviceListResponse = responseBody.getData();
        List<DeviceResponse> deviceResponses = deviceListResponse.getDeviceList();
        List<Device> devices = deviceResponses.stream()
                .map(deviceResponse -> {
                    Device device = new Device();
                    device.setDeviceName(deviceResponse.getDeviceName());
                    device.setDeviceImage(deviceResponse.getDeviceImage());
                    device.setKey(deviceResponse.getKey());
                    return device;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(devices);
    }

}
