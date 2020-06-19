package com.incarcloud.entity;

import com.incarcloud.share.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备信息类
 *
 * @author Kong, created on 2020-06-18T13:59.
 * @since 1.0.0-SNAPSHOT
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
