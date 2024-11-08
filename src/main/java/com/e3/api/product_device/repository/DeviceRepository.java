package com.e3.api.product_device.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.e3.api.product_device.model.Device;

public interface DeviceRepository extends JpaRepository<Device, Long> {

}
