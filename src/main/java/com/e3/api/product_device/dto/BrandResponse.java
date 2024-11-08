package com.e3.api.product_device.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BrandResponse {
    @JsonProperty("brand_id")
    private int brandId;

    @JsonProperty("brand_name")
    private String brandName;

    @JsonProperty("key")
    private String key;

    @JsonProperty("device_list")
    private List<DeviceResponse> deviceList;

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<DeviceResponse> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<DeviceResponse> deviceList) {
        this.deviceList = deviceList;
    }

}
