package com.e3.api.product_device.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class DeviceListResponse {
    @JsonProperty("device_list")
    private List<DeviceResponse> deviceList;

    @JsonProperty("total_page")
    private int totalPage;

    public List<DeviceResponse> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<DeviceResponse> deviceList) {
        this.deviceList = deviceList;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

}
