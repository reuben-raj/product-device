package com.e3.api.product_device.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import com.e3.api.product_device.dto.BrandResponse;
import com.e3.api.product_device.dto.DeviceAPIResponseWrapper;
import com.e3.api.product_device.dto.DeviceListResponse;
import com.e3.api.product_device.dto.DeviceRequest;
import com.e3.api.product_device.dto.DeviceResponse;
import com.e3.api.product_device.model.Brand;
import com.e3.api.product_device.model.Device;
import com.e3.api.product_device.repository.BrandRepository;
import com.e3.api.product_device.repository.DeviceRepository;

@ActiveProfiles("unit")
public class DeviceServiceTest {

    private static final String DEVICE_DATA_URL = "https://script.google.com/macros/s/AKfycbxNu27V2Y2LuKUIQMK8lX1y0joB6YmG6hUwB1fNeVbgzEh22TcDGrOak03Fk3uBHmz-/exec";
    private static final String DEVICE_REDIRECT_URL = "https://redirect-url.com";

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private DeviceRepository deviceRepository;

    @Spy
    @InjectMocks
    private DeviceService deviceService;

    private List<Brand> brands;
    private List<Device> devices;

    private List<BrandResponse> brandsResponse;
    Brand brand1;

    @BeforeEach
    void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        deviceService = new DeviceService(DEVICE_DATA_URL, restTemplate);

        Field brandRepoField = DeviceService.class.getDeclaredField("brandRepository");
        brandRepoField.setAccessible(true);
        brandRepoField.set(deviceService, brandRepository);

        Field deviceRepoField = DeviceService.class.getDeclaredField("deviceRepository");
        deviceRepoField.setAccessible(true);
        deviceRepoField.set(deviceService, deviceRepository);

        brand1 = new Brand();
        brand1.setId(1);
        brand1.setBrandName("Nokia");
        brand1.setKey("nokia");

        Brand brand2 = new Brand();
        brand2.setId(2);
        brand2.setBrandName("iPhone");
        brand2.setKey("iphone");

        brands = List.of(brand1, brand2);

        Device device1 = new Device();
        device1.setId((long) 1);
        device1.setDeviceName("Nokia 5");
        device1.setDeviceType("nokia5");

        Device device2 = new Device();
        device2.setId((long) 2);
        device2.setDeviceName("iPhone 11");
        device2.setDeviceType("iphone-11");

        devices = List.of(device1, device2);

        DeviceResponse deviceResponse1 = new DeviceResponse();
        deviceResponse1.setDeviceId((long) 1);
        deviceResponse1.setDeviceName("Nokia 5");
        deviceResponse1.setDeviceType("nokia5");
        deviceResponse1.setDeviceImage("https://sample.com/nokia-5.jpg");
        deviceResponse1.setKey("nokia_5");

        DeviceResponse deviceResponse2 = new DeviceResponse();
        deviceResponse2.setDeviceId((long) 2);
        deviceResponse2.setDeviceName("Nokia 6");
        deviceResponse2.setDeviceType("nokia6");
        deviceResponse2.setDeviceImage("https://sample.com/nokia-6.jpg");
        deviceResponse2.setKey("nokia_6");

        BrandResponse brandResponse = new BrandResponse();
        brandResponse.setBrandId(1);
        brandResponse.setBrandName("Nokia");
        brandResponse.setDeviceList(List.of(deviceResponse1, deviceResponse2));
        brandResponse.setKey("1");

        brandsResponse = List.of(brandResponse);
    }

    @Test
    void testGetBrands() {
        when(brandRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(brands));

        Page<Brand> result = deviceService.getBrands(0, 10);

        assertEquals(2, result.getTotalElements());
        assertEquals("Nokia", result.getContent().get(0).getBrandName());
    }

    @Test
    void testGetBrandsFromSource() {
        DeviceAPIResponseWrapper<List<BrandResponse>> apiResponse = new DeviceAPIResponseWrapper<>();
        apiResponse.setData(brandsResponse);

        ResponseEntity<DeviceAPIResponseWrapper<List<BrandResponse>>> responseEntity = 
            new ResponseEntity<>(apiResponse, HttpStatus.OK);
        
        when(restTemplate.exchange(
            eq(DEVICE_DATA_URL + "?route=brand-list"), 
            eq(HttpMethod.GET), 
            eq(null), 
            eq(new ParameterizedTypeReference<DeviceAPIResponseWrapper<List<BrandResponse>>>() {})))
        .thenReturn(responseEntity);

        List<Brand> result = deviceService.getBrandsFromSource();

        assertEquals(1, result.size());
        assertEquals("Nokia", result.get(0).getBrandName());
        assertEquals(1, result.get(0).getId());
        assertEquals("1", result.get(0).getKey());
    }

    @Test
    void testGetBrandsFromSourceInvalidBrandName() {
        DeviceAPIResponseWrapper<List<BrandResponse>> apiResponse = new DeviceAPIResponseWrapper<>();
        brandsResponse.get(0).setBrandName("");
        apiResponse.setData(brandsResponse);

        ResponseEntity<DeviceAPIResponseWrapper<List<BrandResponse>>> responseEntity = 
            new ResponseEntity<>(apiResponse, HttpStatus.OK);
        
        when(restTemplate.exchange(
            eq(DEVICE_DATA_URL + "?route=brand-list"), 
            eq(HttpMethod.GET), 
            eq(null), 
            eq(new ParameterizedTypeReference<DeviceAPIResponseWrapper<List<BrandResponse>>>() {})))
        .thenReturn(responseEntity);

        List<Brand> result = deviceService.getBrandsFromSource();

        assertEquals(0, result.size());
    }

    @Test
    void testGetBrandsFromSourceInvalidKey() {
        DeviceAPIResponseWrapper<List<BrandResponse>> apiResponse = new DeviceAPIResponseWrapper<>();
        brandsResponse.get(0).setKey("");
        apiResponse.setData(brandsResponse);

        ResponseEntity<DeviceAPIResponseWrapper<List<BrandResponse>>> responseEntity = 
            new ResponseEntity<>(apiResponse, HttpStatus.OK);
        
        when(restTemplate.exchange(
            eq(DEVICE_DATA_URL + "?route=brand-list"), 
            eq(HttpMethod.GET), 
            eq(null), 
            eq(new ParameterizedTypeReference<DeviceAPIResponseWrapper<List<BrandResponse>>>() {})))
        .thenReturn(responseEntity);

        List<Brand> result = deviceService.getBrandsFromSource();

        assertEquals(0, result.size());
    }

    @Test
    void testUpdateBrandsFromSource() {
        DeviceAPIResponseWrapper<List<BrandResponse>> apiResponse = new DeviceAPIResponseWrapper<>();
        apiResponse.setData(brandsResponse);

        ResponseEntity<DeviceAPIResponseWrapper<List<BrandResponse>>> responseEntity = 
            new ResponseEntity<>(apiResponse, HttpStatus.OK);

        when(restTemplate.exchange(
            eq(DEVICE_DATA_URL + "?route=brand-list"), 
            eq(HttpMethod.GET), 
            eq(null), 
            eq(new ParameterizedTypeReference<DeviceAPIResponseWrapper<List<BrandResponse>>>() {})))
        .thenReturn(responseEntity);

        when(brandRepository.saveAll(brands)).thenReturn(brands);

        List<Brand> result = deviceService.updateBrandsFromSource();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Nokia", result.get(0).getBrandName());
        assertEquals(1, result.get(0).getId());
        assertEquals("1", result.get(0).getKey());
    }
    
    @Test
    void testGetDevices() {
        when(deviceRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(devices));

        Page<Device> result = deviceService.getDevices(0, 10);

        assertEquals(2, result.getTotalElements());
        assertEquals("Nokia 5", result.getContent().get(0).getDeviceName());
    }

    @Test
    void testGetDevicesFromSource() {
        DeviceAPIResponseWrapper<List<BrandResponse>> apiResponse = new DeviceAPIResponseWrapper<>();
        apiResponse.setData(brandsResponse);

        ResponseEntity<DeviceAPIResponseWrapper<List<BrandResponse>>> responseEntity = 
            new ResponseEntity<>(apiResponse, HttpStatus.OK);
        
        when(restTemplate.exchange(
            eq(DEVICE_DATA_URL + "?route=device-list"), 
            eq(HttpMethod.GET), 
            eq(null), 
            eq(new ParameterizedTypeReference<DeviceAPIResponseWrapper<List<BrandResponse>>>() {})))
        .thenReturn(responseEntity);

        when(brandRepository.findById(1)).thenReturn(Optional.of(brand1));

        List<Device> result = deviceService.getDevicesFromSource();

        assertEquals(2, result.size());
        assertEquals("Nokia 5", result.get(0).getDeviceName());
        assertEquals("nokia5", result.get(0).getDeviceType());
        assertEquals("https://sample.com/nokia-5.jpg", result.get(0).getDeviceImageUrl());
        assertEquals("nokia_5", result.get(0).getKey());
    }

    @Test
    void testGetDevicesFromSourceInvalidBrand() {
        DeviceAPIResponseWrapper<List<BrandResponse>> apiResponse = new DeviceAPIResponseWrapper<>();
        apiResponse.setData(brandsResponse);

        ResponseEntity<DeviceAPIResponseWrapper<List<BrandResponse>>> responseEntity = 
            new ResponseEntity<>(apiResponse, HttpStatus.OK);
        
        when(restTemplate.exchange(
            eq(DEVICE_DATA_URL + "?route=device-list"), 
            eq(HttpMethod.GET), 
            eq(null), 
            eq(new ParameterizedTypeReference<DeviceAPIResponseWrapper<List<BrandResponse>>>() {})))
        .thenReturn(responseEntity);

        List<Device> result = deviceService.getDevicesFromSource();

        assertEquals(0, result.size());
    }

    @Test
    void testGetDevicesFromSourceInvalidDeviceName() {
        DeviceAPIResponseWrapper<List<BrandResponse>> apiResponse = new DeviceAPIResponseWrapper<>();
        brandsResponse.get(0).getDeviceList().get(0).setDeviceName("");
        apiResponse.setData(brandsResponse);

        ResponseEntity<DeviceAPIResponseWrapper<List<BrandResponse>>> responseEntity = 
            new ResponseEntity<>(apiResponse, HttpStatus.OK);
        
        when(restTemplate.exchange(
            eq(DEVICE_DATA_URL + "?route=device-list"), 
            eq(HttpMethod.GET), 
            eq(null), 
            eq(new ParameterizedTypeReference<DeviceAPIResponseWrapper<List<BrandResponse>>>() {})))
        .thenReturn(responseEntity);

        when(brandRepository.findById(1)).thenReturn(Optional.of(brand1));

        List<Device> result = deviceService.getDevicesFromSource();

        assertEquals(1, result.size());
        assertEquals("Nokia 6", result.get(0).getDeviceName());
        assertEquals("nokia6", result.get(0).getDeviceType());
        assertEquals("https://sample.com/nokia-6.jpg", result.get(0).getDeviceImageUrl());
        assertEquals("nokia_6", result.get(0).getKey());
    }

    @Test
    void testGetDevicesFromSourceInvalidDeviceKey() {
        DeviceAPIResponseWrapper<List<BrandResponse>> apiResponse = new DeviceAPIResponseWrapper<>();
        brandsResponse.get(0).getDeviceList().get(0).setKey("");
        apiResponse.setData(brandsResponse);

        ResponseEntity<DeviceAPIResponseWrapper<List<BrandResponse>>> responseEntity = 
            new ResponseEntity<>(apiResponse, HttpStatus.OK);
        
        when(restTemplate.exchange(
            eq(DEVICE_DATA_URL + "?route=device-list"), 
            eq(HttpMethod.GET), 
            eq(null), 
            eq(new ParameterizedTypeReference<DeviceAPIResponseWrapper<List<BrandResponse>>>() {})))
        .thenReturn(responseEntity);

        when(brandRepository.findById(1)).thenReturn(Optional.of(brand1));

        List<Device> result = deviceService.getDevicesFromSource();

        assertEquals(1, result.size());
        assertEquals("Nokia 6", result.get(0).getDeviceName());
        assertEquals("nokia6", result.get(0).getDeviceType());
        assertEquals("https://sample.com/nokia-6.jpg", result.get(0).getDeviceImageUrl());
        assertEquals("nokia_6", result.get(0).getKey());
    }

    @Test
    void testUpdateDevicesFromSource() {
        DeviceAPIResponseWrapper<List<BrandResponse>> apiResponse = new DeviceAPIResponseWrapper<>();
        apiResponse.setData(brandsResponse);

        ResponseEntity<DeviceAPIResponseWrapper<List<BrandResponse>>> responseEntity = 
            new ResponseEntity<>(apiResponse, HttpStatus.OK);
        
        when(restTemplate.exchange(
            eq(DEVICE_DATA_URL + "?route=device-list"), 
            eq(HttpMethod.GET), 
            eq(null), 
            eq(new ParameterizedTypeReference<DeviceAPIResponseWrapper<List<BrandResponse>>>() {})))
        .thenReturn(responseEntity);

        when(brandRepository.findById(1)).thenReturn(Optional.of(brand1));

        when(deviceRepository.saveAll(devices)).thenReturn(devices);

        List<Device> result = deviceService.updateDevicesFromSource();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Nokia 5", result.get(0).getDeviceName());
        assertEquals("nokia5", result.get(0).getDeviceType());
        assertEquals("https://sample.com/nokia-5.jpg", result.get(0).getDeviceImageUrl());
        assertEquals("nokia_5", result.get(0).getKey());
    }

    @Test
    void testSearchDevices() {
        DeviceRequest deviceRequest = new DeviceRequest();
        deviceRequest.setBrand_id(1);
        deviceRequest.setRoute("device-list-by-brand");
        
        ResponseEntity<String> redirectResponseEntity = ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(DEVICE_REDIRECT_URL))
                .build();

        when(restTemplate.exchange(
            eq(DEVICE_DATA_URL), 
            eq(HttpMethod.POST), 
            any(HttpEntity.class), 
            eq(String.class)))
        .thenReturn(redirectResponseEntity);

        DeviceResponse deviceResponse = new DeviceResponse();
        deviceResponse.setDeviceName("iPhone 11");
        deviceResponse.setDeviceImage("https://fdn2.gsmarena.com/vv/bigpic/apple-iphone-11.jpg");
        deviceResponse.setKey("apple_iphone_11-9848");

        DeviceListResponse deviceListResponse = new DeviceListResponse();
        deviceListResponse.setDeviceList(Arrays.asList(deviceResponse));
        deviceListResponse.setTotalPage(1);

        DeviceAPIResponseWrapper<DeviceListResponse> redirectResponse = new DeviceAPIResponseWrapper<>();
        redirectResponse.setStatus(200);
        redirectResponse.setMessage("Success");
        redirectResponse.setData(deviceListResponse);

        ResponseEntity<DeviceAPIResponseWrapper<DeviceListResponse>> responseEntity = 
            new ResponseEntity<>(redirectResponse, HttpStatus.OK);
        
        when(restTemplate.exchange(
            eq(URI.create(DEVICE_REDIRECT_URL)), 
            eq(HttpMethod.GET), 
            any(HttpEntity.class), 
            eq(new ParameterizedTypeReference<DeviceAPIResponseWrapper<DeviceListResponse>>() {})))
        .thenReturn(responseEntity);
        
        List<Device> result = deviceService.searchDevices(deviceRequest);

        assertEquals(1, result.size());
        assertEquals("iPhone 11", result.get(0).getDeviceName());
        assertEquals("https://fdn2.gsmarena.com/vv/bigpic/apple-iphone-11.jpg", result.get(0).getDeviceImageUrl());
        assertEquals("apple_iphone_11-9848", result.get(0).getKey());
    }

    @Test
    void testSearchDevicesMissingRedirect() {
        DeviceRequest deviceRequest = new DeviceRequest();
        deviceRequest.setBrand_id(1);
        deviceRequest.setRoute("device-list-by-brand");
        
        ResponseEntity<String> redirectResponseEntity = ResponseEntity.status(HttpStatus.OK)
                .build();

        when(restTemplate.exchange(
            eq(DEVICE_DATA_URL), 
            eq(HttpMethod.POST), 
            any(HttpEntity.class), 
            eq(String.class)))
        .thenReturn(redirectResponseEntity);
        
        List<Device> result = deviceService.searchDevices(deviceRequest);

        assertEquals(0, result.size());
    }

    @Test
    void testSearchDevicesMissingHeaderLocation() {
        DeviceRequest deviceRequest = new DeviceRequest();
        deviceRequest.setBrand_id(1);
        deviceRequest.setRoute("device-list-by-brand");
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", null);
        ResponseEntity<String> redirectResponseEntity = ResponseEntity.status(HttpStatus.FOUND)
                .headers(headers)
                .build();

        when(restTemplate.exchange(
            eq(DEVICE_DATA_URL), 
            eq(HttpMethod.POST), 
            any(HttpEntity.class), 
            eq(String.class)))
        .thenReturn(redirectResponseEntity);
        
        List<Device> result = deviceService.searchDevices(deviceRequest);

        assertEquals(0, result.size());
    }

    @Test
    void testSearchDevicesEmptyResponseBody() {
        DeviceRequest deviceRequest = new DeviceRequest();
        deviceRequest.setBrand_id(1);
        deviceRequest.setRoute("device-list-by-brand");
        
        ResponseEntity<String> redirectResponseEntity = ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(DEVICE_REDIRECT_URL))
                .build();

        when(restTemplate.exchange(
            eq(DEVICE_DATA_URL), 
            eq(HttpMethod.POST), 
            any(HttpEntity.class), 
            eq(String.class)))
        .thenReturn(redirectResponseEntity);

        ResponseEntity<DeviceAPIResponseWrapper<DeviceListResponse>> responseEntity = 
            new ResponseEntity<>(null, HttpStatus.OK);
        
        when(restTemplate.exchange(
            eq(URI.create(DEVICE_REDIRECT_URL)), 
            eq(HttpMethod.GET), 
            any(HttpEntity.class), 
            eq(new ParameterizedTypeReference<DeviceAPIResponseWrapper<DeviceListResponse>>() {})))
        .thenReturn(responseEntity);

        assertThrows(RuntimeException.class, 
            () -> deviceService.searchDevices(deviceRequest),
            "Failed to search devices");
    }

    @Test
    void testSearchDevicesInvalidStatusCode() {
        DeviceRequest deviceRequest = new DeviceRequest();
        deviceRequest.setBrand_id(1);
        deviceRequest.setRoute("device-list-by-brand");
        
        ResponseEntity<String> redirectResponseEntity = ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(DEVICE_REDIRECT_URL))
                .build();

        when(restTemplate.exchange(
            eq(DEVICE_DATA_URL), 
            eq(HttpMethod.POST), 
            any(HttpEntity.class), 
            eq(String.class)))
        .thenReturn(redirectResponseEntity);

        DeviceAPIResponseWrapper<DeviceListResponse> redirectResponse = new DeviceAPIResponseWrapper<>();
        redirectResponse.setStatus(200);
        redirectResponse.setMessage("Success");
        redirectResponse.setData(null);

        ResponseEntity<DeviceAPIResponseWrapper<DeviceListResponse>> responseEntity = 
            new ResponseEntity<>(redirectResponse, HttpStatus.FOUND);
        
        when(restTemplate.exchange(
            eq(URI.create(DEVICE_REDIRECT_URL)), 
            eq(HttpMethod.GET), 
            any(HttpEntity.class), 
            eq(new ParameterizedTypeReference<DeviceAPIResponseWrapper<DeviceListResponse>>() {})))
        .thenReturn(responseEntity);

        assertThrows(RuntimeException.class, 
            () -> deviceService.searchDevices(deviceRequest),
            "Failed to search devices");
    }

    @Test
    void testSearchDevicesInvalidStatusCodeInBody() {
        DeviceRequest deviceRequest = new DeviceRequest();
        deviceRequest.setBrand_id(1);
        deviceRequest.setRoute("device-list-by-brand");
        
        ResponseEntity<String> redirectResponseEntity = ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(DEVICE_REDIRECT_URL))
                .build();

        when(restTemplate.exchange(
            eq(DEVICE_DATA_URL), 
            eq(HttpMethod.POST), 
            any(HttpEntity.class), 
            eq(String.class)))
        .thenReturn(redirectResponseEntity);

        DeviceAPIResponseWrapper<DeviceListResponse> redirectResponse = new DeviceAPIResponseWrapper<>();
        redirectResponse.setStatus(204);
        redirectResponse.setMessage("Success");
        redirectResponse.setData(null);

        ResponseEntity<DeviceAPIResponseWrapper<DeviceListResponse>> responseEntity = 
            new ResponseEntity<>(redirectResponse, HttpStatus.OK);
        
        when(restTemplate.exchange(
            eq(URI.create(DEVICE_REDIRECT_URL)), 
            eq(HttpMethod.GET), 
            any(HttpEntity.class), 
            eq(new ParameterizedTypeReference<DeviceAPIResponseWrapper<DeviceListResponse>>() {})))
        .thenReturn(responseEntity);

        assertThrows(RuntimeException.class, 
            () -> deviceService.searchDevices(deviceRequest),
            "Error from third-party service");
    }
}
