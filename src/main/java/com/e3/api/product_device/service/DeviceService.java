package com.e3.api.product_device.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.e3.api.product_device.dto.BrandResponse;
import com.e3.api.product_device.dto.DeviceAPIResponseWrapper;
import com.e3.api.product_device.dto.DeviceListResponse;
import com.e3.api.product_device.dto.DeviceRequest;
import com.e3.api.product_device.dto.DeviceResponse;
import com.e3.api.product_device.model.Brand;
import com.e3.api.product_device.model.Device;
import com.e3.api.product_device.repository.BrandRepository;
import com.e3.api.product_device.repository.DeviceRepository;

@Service
public class DeviceService {

    Logger logger = LoggerFactory.getLogger(DeviceService.class);

    private final String deviceDataUrl;
    private final RestTemplate restTemplate;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    public DeviceService(@Value("${device.datasource.url}") String deviceDataUrl, RestTemplate restTemplate) {
        this.deviceDataUrl = deviceDataUrl;
        this.restTemplate = restTemplate;
    }

    public Page<Brand> getBrands(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return brandRepository.findAll(pageable);
    }

    public List<Brand> getBrandsFromSource() {
        List<Brand> brands = new ArrayList<>();

        String url = UriComponentsBuilder.fromHttpUrl(deviceDataUrl)
            .queryParam("route", "brand-list")
            .toUriString();
        ResponseEntity<DeviceAPIResponseWrapper<List<BrandResponse>>> responseEntity =
            restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                null,
                new ParameterizedTypeReference<DeviceAPIResponseWrapper<List<BrandResponse>>>() {});
        
        List<BrandResponse> brandResponses = responseEntity.getBody().getData();

        for(BrandResponse brandResponse : brandResponses) {
            if(StringUtils.hasLength(brandResponse.getBrandName()) 
                && StringUtils.hasLength(brandResponse.getKey())) {
                Brand brand = new Brand();
                brand.setId(brandResponse.getBrandId());
                brand.setBrandName(brandResponse.getBrandName());
                brand.setKey(brandResponse.getKey());
                brand.setCreatedBy((long) 1);
                brand.setUpdatedBy((long) 1);
                brands.add(brand);
            }
        }

        return brands;
    }

    public List<Brand> updateBrandsFromSource() {
        List<Brand> brands = new ArrayList<>();
        brands = getBrandsFromSource();

        brandRepository.saveAll(brands);

        return brands;
    }

    public Page<Device> getDevices(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return deviceRepository.findAll(pageable);
    }

    public List<Device> getDevicesFromSource() {
        List<Device> devices = new ArrayList<>();
        
        String url = UriComponentsBuilder.fromHttpUrl(deviceDataUrl)
            .queryParam("route", "device-list")
            .toUriString();
        ResponseEntity<DeviceAPIResponseWrapper<List<BrandResponse>>> responseEntity =
            restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                null,
                new ParameterizedTypeReference<DeviceAPIResponseWrapper<List<BrandResponse>>>() {});
        
        List<BrandResponse> brandResponses = responseEntity.getBody().getData();

        for(BrandResponse brandResponse : brandResponses) {
            Brand brand = brandRepository.findById(brandResponse.getBrandId()).orElse(null);
            
            if(brand != null){
                List<DeviceResponse> deviceResponses = brandResponse.getDeviceList();

                for(DeviceResponse deviceResponse : deviceResponses) {
                    if(StringUtils.hasLength(deviceResponse.getDeviceName()) 
                        && StringUtils.hasLength(deviceResponse.getKey())) {
                        Device device = new Device();
                        device.setId(deviceResponse.getDeviceId());
                        device.setDeviceName(deviceResponse.getDeviceName());
                        device.setDeviceType(deviceResponse.getDeviceType());
                        device.setDeviceImageUrl(deviceResponse.getDeviceImage());
                        device.setKey(deviceResponse.getKey());
                        device.setCreatedBy((long) 1);
                        device.setUpdatedBy((long) 1);
                        device.setBrand(brand);
                        devices.add(device);
                    }
                }
            }
        }
        return devices;
    }

    public List<Device> updateDevicesFromSource() {
        List<Device> devices = new ArrayList<>();
        devices = getDevicesFromSource();
        deviceRepository.saveAll(devices);
        return devices;
    }

    public List<Device> searchDevices(DeviceRequest deviceRequest) {
        List<Device> devices = new ArrayList<>();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<DeviceRequest> requestEntity = new HttpEntity<>(deviceRequest, headers);

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                deviceDataUrl, 
                HttpMethod.POST, 
                requestEntity, 
                String.class);
            // this API call will always redirect so we need to follow it
            if(responseEntity.getStatusCode().is3xxRedirection() 
                && responseEntity.getHeaders().getLocation() != null){
                URI redirectUrl = responseEntity.getHeaders().getLocation();
                ResponseEntity<DeviceAPIResponseWrapper<DeviceListResponse>> redirectResponseEntity =
                    restTemplate.exchange(
                        redirectUrl, 
                        HttpMethod.GET, 
                        new HttpEntity<>(headers),
                        new ParameterizedTypeReference<DeviceAPIResponseWrapper<DeviceListResponse>>() {});
                
                DeviceAPIResponseWrapper<DeviceListResponse> responseBody = redirectResponseEntity.getBody();
                if (responseBody == null || redirectResponseEntity.getStatusCode() != HttpStatus.OK) {
                    throw new RuntimeException("Failed to search devices");
                }
                if (responseBody.getStatus() != 200) {
                    throw new RuntimeException("Error from third-party service");
                }

                DeviceListResponse deviceListResponse = responseBody.getData();
                List<DeviceResponse> deviceResponses = deviceListResponse.getDeviceList();
                devices = deviceResponses.stream()
                        .map(deviceResponse -> {
                            Device device = new Device();
                            device.setDeviceName(deviceResponse.getDeviceName());
                            device.setDeviceImageUrl(deviceResponse.getDeviceImage());
                            device.setKey(deviceResponse.getKey());
                            return device;
                        })
                        .collect(Collectors.toList());
            }
            return devices;
        } catch(Exception e) {
            logger.error("Error during API call: ", e);
            throw new RuntimeException("Failed to search devices");
        }
    }

}
