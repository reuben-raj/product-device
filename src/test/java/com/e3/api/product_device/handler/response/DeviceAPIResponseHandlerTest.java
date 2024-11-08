package com.e3.api.product_device.handler.response;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.e3.api.product_device.dto.Brand;
import com.e3.api.product_device.dto.BrandResponse;
import com.e3.api.product_device.dto.Device;
import com.e3.api.product_device.dto.DeviceAPIResponseWrapper;
import com.e3.api.product_device.dto.DeviceListResponse;
import com.e3.api.product_device.dto.DeviceResponse;

public class DeviceAPIResponseHandlerTest {

    @Test
    void testClassInstantiation() {
        new DeviceAPIResponseHandler();
    }

    @Test
    void testHandleBrandResponse() {
        BrandResponse brandResponse = new BrandResponse();
        brandResponse.setBrandId(1);
        brandResponse.setBrandName("Nokia");
        brandResponse.setKey("nokia");

        DeviceAPIResponseWrapper<List<BrandResponse>> responseBody = new DeviceAPIResponseWrapper<>();
        responseBody.setStatus(200);
        responseBody.setMessage("Success");
        responseBody.setData(Arrays.asList(brandResponse));

        ResponseEntity<DeviceAPIResponseWrapper<List<BrandResponse>>> responseEntity = 
            new ResponseEntity<>(responseBody, HttpStatus.OK);
        
        ResponseEntity<?> response = DeviceAPIResponseHandler.handleBrandResponse(responseEntity);
        List<Brand> brands = (List<Brand>) response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, brands.size());
        assertEquals("Nokia", brands.get(0).getBrandName());
        assertEquals("nokia", brands.get(0).getKey());
    }

    @Test
    void testHandleBrandResponseNullBody() {
        ResponseEntity<DeviceAPIResponseWrapper<List<BrandResponse>>> responseEntity = 
            new ResponseEntity<>(null, HttpStatus.OK);
        
        ResponseEntity<?> response = DeviceAPIResponseHandler.handleBrandResponse(responseEntity);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Failed to retrieve data", response.getBody());
    }

    @Test
    void testHandleBrandResponseNon200StatusCode() {
        DeviceAPIResponseWrapper<List<BrandResponse>> responseBody = new DeviceAPIResponseWrapper<>();
        ResponseEntity<DeviceAPIResponseWrapper<List<BrandResponse>>> responseEntity = 
            new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);

        ResponseEntity<?> response = DeviceAPIResponseHandler.handleBrandResponse(responseEntity);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to retrieve data", response.getBody());
    }

    @Test
    void testHandleBrandResponseNon200StatusInBody() {
        DeviceAPIResponseWrapper<List<BrandResponse>> responseBody = new DeviceAPIResponseWrapper<>();
        responseBody.setStatus(500);
        ResponseEntity<DeviceAPIResponseWrapper<List<BrandResponse>>> responseEntity = 
            new ResponseEntity<>(responseBody, HttpStatus.OK);

        ResponseEntity<?> response = DeviceAPIResponseHandler.handleBrandResponse(responseEntity);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error from third-party service", response.getBody());
    }

    @Test
    void testHandleDeviceResponse() {
        DeviceResponse deviceResponse = new DeviceResponse();
        deviceResponse.setDeviceName("iPhone 11");
        deviceResponse.setDeviceImage("https://fdn2.gsmarena.com/vv/bigpic/apple-iphone-11.jpg");
        deviceResponse.setKey("apple_iphone_11-9848");

        DeviceListResponse deviceListResponse = new DeviceListResponse();
        deviceListResponse.setDeviceList(Arrays.asList(deviceResponse));

        DeviceAPIResponseWrapper<DeviceListResponse> responseBody = new DeviceAPIResponseWrapper<>();
        responseBody.setStatus(200);
        responseBody.setMessage("Success");
        responseBody.setData(deviceListResponse);

        ResponseEntity<DeviceAPIResponseWrapper<DeviceListResponse>> responseEntity = 
            new ResponseEntity<>(responseBody, HttpStatus.OK);
        
        ResponseEntity<?> response = DeviceAPIResponseHandler.handleDeviceResponse(responseEntity);
        List<Device> devices = (List<Device>) response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, devices.size());
        assertEquals("iPhone 11", devices.get(0).getDeviceName());
        assertEquals("https://fdn2.gsmarena.com/vv/bigpic/apple-iphone-11.jpg", devices.get(0).getDeviceImage());
        assertEquals("apple_iphone_11-9848", devices.get(0).getKey());
    }

    @Test
    void testHandleDeviceResponseNullBody() {
        ResponseEntity<DeviceAPIResponseWrapper<DeviceListResponse>> responseEntity = 
            new ResponseEntity<>(null, HttpStatus.OK);
        
        ResponseEntity<?> response = DeviceAPIResponseHandler.handleDeviceResponse(responseEntity);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Failed to retrieve data", response.getBody());
    }

    @Test
    void testHandleDeviceResponseNon200StatusCode() {
        DeviceAPIResponseWrapper<DeviceListResponse> responseBody = new DeviceAPIResponseWrapper<>();
        ResponseEntity<DeviceAPIResponseWrapper<DeviceListResponse>> responseEntity = 
            new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
        
        ResponseEntity<?> response = DeviceAPIResponseHandler.handleDeviceResponse(responseEntity);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to retrieve data", response.getBody());
    }

    @Test
    void testHandleDeviceResponseNon200StatusInBody() {
        DeviceAPIResponseWrapper<DeviceListResponse> responseBody = new DeviceAPIResponseWrapper<>();
        responseBody.setStatus(500);
        ResponseEntity<DeviceAPIResponseWrapper<DeviceListResponse>> responseEntity = 
            new ResponseEntity<>(responseBody, HttpStatus.OK);
        
        ResponseEntity<?> response = DeviceAPIResponseHandler.handleDeviceResponse(responseEntity);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error from third-party service", response.getBody());
    }
}
