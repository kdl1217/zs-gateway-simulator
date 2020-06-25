package com.incarcloud.runner;

import com.incarcloud.device.DeviceManager;
import com.incarcloud.share.Constants;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 发送者
 *
 * @author Kong, created on 2020-06-18T13:59.
 * @since 1.0.0-SNAPSHOT
 */
@Log4j2
@Component
public class Sender {

    @Autowired
    protected DeviceManager deviceManager;

    /**
     * 执行定时任务
     */
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    public void start() {

        log.info("simulator sender running ...");

        // 定时30S
        int period = 30;

        // 执行任务
        scheduledExecutorService.scheduleAtFixedRate(
                () -> deviceManager.getDeviceMap().forEach((key, deviceInfo) -> {

            // 设备号
            String deviceId = deviceInfo.getDeviceId();
            // 获取发送次数，用于发送不同报文
            Integer index = deviceManager.getIndexMap().get(deviceId);
            if (null == index) {
                index = 0;
            }
            // GPS 下标-固定
            int gpsIndex = 0;
            if (0 == index) {
                deviceManager.sendCheckData(deviceInfo, System.currentTimeMillis());
            } else {
                deviceManager.sendRunData(deviceInfo, gpsIndex);
                switch (index) {
                    case 1:
                        // 关闭报警
                        deviceManager.sendAlarmData(deviceInfo, Constants.AlarmType.CLOSE_ALL, gpsIndex);
                        break;
                    case 2:
                        // 发送碰撞报警
                        deviceManager.sendAlarmData(deviceInfo, Constants.AlarmType.CRASH, gpsIndex);
                        break;
                    case 3:
                        // 发送震动报警
                        deviceManager.sendAlarmData(deviceInfo, Constants.AlarmType.SHAKE, gpsIndex);
                        break;
                    case 4:
                        // 车辆在空闲状态下有速度
                        deviceManager.sendAlarmData(deviceInfo, Constants.AlarmType.IDLE_SPEED, gpsIndex);
                        break;
                    case 5:
                        // GNSS天线未接或被剪断
                        deviceManager.sendAlarmData(deviceInfo, Constants.AlarmType.GNSS_DISCONNECT, gpsIndex);
                        break;
                    case 6:
                        // 蓄电池欠压
                        deviceManager.sendAlarmData(deviceInfo, Constants.AlarmType.BATTERY_UNDERVOLTAGE, gpsIndex);
                        break;
                    case 7:
                        // 蓄电池过压
                        deviceManager.sendAlarmData(deviceInfo, Constants.AlarmType.BATTERY_OVERVOLTAGE, gpsIndex);
                        break;
                    default:
                }

            }
            index++;
            deviceManager.getIndexMap().put(deviceId, index);
        }), period, period, TimeUnit.SECONDS);
    }
}
