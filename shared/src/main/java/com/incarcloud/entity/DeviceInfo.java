package com.incarcloud.entity;

import com.incarcloud.share.Constants;
/**
 * 设备信息类
 *
 * @author Kong, created on 2020-06-18T13:59.
 * @since 1.0.0-SNAPSHOT
 */
public class DeviceInfo {

    /**
     * 设备IMEI
     */
    private String deviceId;

    /**
     * 设备号
     */
    private String deviceCode;

    /**
     * 车辆VIN
     */
    private String vin;

    /**
     * 密钥Key
     */
    private byte[] key;

    public DeviceInfo(String deviceId, String deviceCode, String vin) {
        this.deviceId = deviceId;
        this.deviceCode = deviceCode;
        this.vin = vin;
        this.key = Constants.KEY;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }
}
