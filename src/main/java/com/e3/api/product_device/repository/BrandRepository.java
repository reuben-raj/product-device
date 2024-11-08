package com.e3.api.product_device.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.e3.api.product_device.model.Brand;

public interface BrandRepository extends JpaRepository<Brand, Integer> {

}
