package com.e3.api.product_device.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.e3.api.product_device.dto.DeviceRequest;
import com.e3.api.product_device.model.Brand;
import com.e3.api.product_device.model.Device;
import com.e3.api.product_device.service.DeviceService;

@WebMvcTest(DeviceController.class)
@ActiveProfiles("unit")
public class DeviceControllerTest {

    @MockBean
    private DeviceService deviceService;

    @Autowired
    private MockMvc mockMvc;

    private List<Brand> brands;
    private List<Device> devices;

    @BeforeEach
    void setup() {
        Brand brand1 = new Brand();
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
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testGetAllBrands() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Brand> page = new PageImpl<>(brands, pageable, 2);

        when(deviceService.getBrands(0, 10)).thenReturn(page);

        mockMvc.perform(get("/device/brands").contentType(MediaType.APPLICATION_JSON).with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].brandName").value("Nokia"))
            .andExpect(jsonPath("$.content[0].key").value("nokia"))
            .andExpect(jsonPath("$.content[1].brandName").value("iPhone"))
            .andExpect(jsonPath("$.content[1].key").value("iphone"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getAllDevices() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Device> page = new PageImpl<>(devices, pageable, 2);

        when(deviceService.getDevices(0, 10)).thenReturn(page);

        mockMvc.perform(get("/device/devices").contentType(MediaType.APPLICATION_JSON).with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].deviceName").value("Nokia 5"))
            .andExpect(jsonPath("$.content[0].deviceType").value("nokia5"))
            .andExpect(jsonPath("$.content[1].deviceName").value("iPhone 11"))
            .andExpect(jsonPath("$.content[1].deviceType").value("iphone-11"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testSearchDevices() throws Exception {
        DeviceRequest deviceRequest = new DeviceRequest();
        deviceRequest.setBrand_id(1);
        
        when(deviceService.searchDevices(any(DeviceRequest.class))).thenReturn(devices);

        mockMvc.perform(post("/device/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(deviceRequest))
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].deviceName").value("Nokia 5"))
            .andExpect(jsonPath("$[1].deviceName").value("iPhone 11"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testSearchDevicesNotFound() throws Exception {
        DeviceRequest deviceRequest = new DeviceRequest();
        deviceRequest.setBrand_name("Unknown");
        
        when(deviceService.searchDevices(any(DeviceRequest.class))).thenReturn(List.of());

        mockMvc.perform(post("/device/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(deviceRequest))
                .with(csrf()))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testSearchDevicesRestClientException() throws Exception {
        DeviceRequest deviceRequest = new DeviceRequest();
        deviceRequest.setBrand_id(1);

        when(deviceService.searchDevices(any(DeviceRequest.class)))
        .thenThrow(new RestClientException("Client Error"));

        mockMvc.perform(post("/device/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(deviceRequest))
                .with(csrf()))
        .andExpect(status().isInternalServerError());
    }
}
