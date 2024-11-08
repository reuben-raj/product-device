package com.e3.api.product_device.scheduler;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.e3.api.product_device.service.DeviceService;

public class DeviceSchedulerTest {

    @Mock
    private DeviceService deviceService;

    @InjectMocks
    private DeviceScheduler deviceScheduler;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdateBrands() {
        deviceScheduler.updateBrands();

        verify(deviceService, times(1)).updateBrandsFromSource();
    }

    @Test
    void testUpdateDevices() {
        deviceScheduler.updateDevices();

        verify(deviceService, times(1)).updateDevicesFromSource();
    }
}
