package com.incarcloud.runner;

import com.incarcloud.device.DeviceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.incarcloud.share.Constants;
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
@Component
public class Sender {

    private Logger log = LoggerFactory.getLogger(Receiver.class);

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

            if (0 == index) {
                deviceManager.sendCheckData(deviceInfo, System.currentTimeMillis());
            } else {
                deviceManager.sendRunData(deviceInfo, 0);
                switch (index) {
                    case 1:
                        // 关闭报警
                        deviceManager.sendAlarmData(deviceInfo, Constants.AlarmType.CLOSE_ALL);
                        log.info("关闭报警");
                        break;
                    case 2:
                        // 发送碰撞报警
                        deviceManager.sendAlarmData(deviceInfo, Constants.AlarmType.CRASH);
                        log.info("发送碰撞报警");
                        break;
                    case 3:
                        // 发送震动报警
                        deviceManager.sendAlarmData(deviceInfo, Constants.AlarmType.SHAKE);
                        log.info("发送震动报警");
                        break;
                    case 4:
                        // 车辆在空闲状态下有速度
                        deviceManager.sendAlarmData(deviceInfo, Constants.AlarmType.IDLE_SPEED);
                        log.info("车辆在空闲状态下有速度");
                        break;
                    case 5:
                        // GNSS天线未接或被剪断
                        deviceManager.sendAlarmData(deviceInfo, Constants.AlarmType.GNSS_DISCONNECT);
                        log.info("GNSS天线未接或被剪断");
                        break;
                    case 6:
                        // 蓄电池欠压
                        deviceManager.sendAlarmData(deviceInfo, Constants.AlarmType.BATTERY_UNDERVOLTAGE);
                        log.info("蓄电池欠压");
                        break;
                    case 7:
                        // 蓄电池过压
                        deviceManager.sendAlarmData(deviceInfo, Constants.AlarmType.BATTERY_OVERVOLTAGE);
                        log.info("蓄电池过压");
                        break;
                    default:
                }

            }
            index++;
            deviceManager.getIndexMap().put(deviceId, index);
        }), period, period, TimeUnit.SECONDS);
    }
}
