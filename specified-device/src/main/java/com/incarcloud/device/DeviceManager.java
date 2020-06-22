package com.incarcloud.device;

import com.github.io.protocol.utils.HexStringUtil;
import com.incarcloud.entity.DeviceInfo;
import com.incarcloud.factory.DeviceMessageFactory;
import com.incarcloud.share.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 设备管理者
 *
 * @author Kong, created on 2020-06-18T14:08.
 * @since 1.0.0-SNAPSHOT
 */
@Component
public class DeviceManager {

    private Logger log = LoggerFactory.getLogger(DeviceManager.class);
    /**
     * 网关服务地址
     */
    @Value("${gateway.server-address}")
    String serverAddress;
    /**
     * 网关服务端口
     */
    @Value("${gateway.server-port}")
    Integer serverPort;

    /**
     * 初始化设备信息
     *      可设置多个设备
     */
    public void init() {
        log.info("init device information ...");
        deviceMap.put("GPSSN000050",new DeviceInfo("GPSSN000050", "KEYTEST050", "TEST0000000000050")) ;

//        deviceMap.put("YK001912D4", new DeviceInfo("863576043319974", "YK001912D4", "LVGEN56A8JG257045"));
    }

    /**
     * 初始化TBox数据包创建工厂
     */
    private DeviceMessageFactory deviceMessageFactory = DeviceMessageFactory.getInstance();

    /**
     * 设备集合信息
     */
    private Map<String, DeviceInfo> deviceMap = new ConcurrentHashMap<>();

    public Map<String, DeviceInfo> getDeviceMap() {
        return deviceMap;
    }

    /**
     * 通道集合
     */
    private Map<String, Socket> socketMap = new ConcurrentHashMap<>();

    public Map<String, Socket> getSocketMap() {
        return socketMap;
    }

    /**
     * 设备发送次数
     */
    private Map<String, Integer> indexMap = new ConcurrentHashMap<>();

    public Map<String, Integer> getIndexMap() {
        return indexMap;
    }

    /**
     * 下发的433密钥
     */
    private Map<String, String> secretMap = new ConcurrentHashMap<>();

    public Map<String, String> getSecretMap() {
        return secretMap;
    }

    /**
     * Socket通道发送数据
     * @param deviceId 设备ID （IMEI）
     * @param bytes     发送字节流
     */
    public void sendMsg(String deviceId, byte[] bytes) {
        try {
            Socket socket = socketMap.get(deviceId);
            if (null == socket || !socket.isConnected()) {
                socket = new Socket(serverAddress, serverPort);
                socketMap.put(deviceId, socket);
            }
            OutputStream mSocketOut = socket.getOutputStream();
            mSocketOut.write(bytes);
            mSocketOut.flush();
            log.info("send deviceId : {} -->  {}", deviceId, HexStringUtil.toHexString(bytes));
        } catch (Exception e) {
            log.info("retry connect : {}", deviceId);
            retrySendMsg(deviceId, bytes, 0);
        }
    }

    /**
     * 重试发送数据
     * @param deviceId 设备ID （IMEI）
     * @param bytes     发送字节流
     * @param count     尝试次数
     */
    public void retrySendMsg(String deviceId, byte[] bytes, int count) {
        count++;
        log.info("retry connect : {}, count - > {}", deviceId, count);
        try {
            Socket socket = socketMap.get(deviceId);
            if (null == socket || !socket.isConnected()) {
                socket = new Socket(serverAddress, serverPort);
                socketMap.put(deviceId, socket);
            }
            OutputStream mSocketOut = socket.getOutputStream();
            mSocketOut.write(bytes);
            mSocketOut.flush();
            log.info("send deviceId : {} -->  {}", deviceId, HexStringUtil.toHexString(bytes));
        } catch (Exception e) {
            log.info("retry connect : {}", deviceId);
            retrySendMsg(deviceId, bytes, count);
        }
    }


    /**
     * 发送校验数据
     * @param deviceInfo        设备信息
     * @param serialNumber      流水号
     */
    public void sendCheckData(DeviceInfo deviceInfo, long serialNumber) {
        try {
            byte[] checkByte = deviceMessageFactory.generateCheckByte(deviceInfo.getDeviceCode(), serialNumber);
            sendMsg(deviceInfo.getDeviceId(), checkByte);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送运行数据
     * @param deviceInfo        设备信息
     * @param index             经纬度标记
     */
    public void sendRunData(DeviceInfo deviceInfo, int index) {
        try {
            byte[] runByte = deviceMessageFactory.generateRunByte(deviceInfo.getDeviceCode(), 1, index, Constants.CommandId.RUN_CMD_FLAG);
            sendMsg(deviceInfo.getDeviceId(), runByte);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送运行数据 - 回馈
     * @param deviceInfo        设备信息
     * @param index             经纬度标记
     */
    public void sendBackRunData(DeviceInfo deviceInfo, int index) {
        try {
            byte[] runByte = deviceMessageFactory.generateRunByte(deviceInfo.getDeviceCode(), 1, index, Constants.CommandId.RUN_BACK_CMD_FLAG);
            sendMsg(deviceInfo.getDeviceId(), runByte);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送报警数据
     * @param deviceInfo        设备信息
     * @param alarmBytes        报警字节流
     */
    public void sendAlarmData(DeviceInfo deviceInfo, byte[] alarmBytes) {
        try {
            byte[] alarmByte = deviceMessageFactory.generateAlarm(deviceInfo.getDeviceCode(), 1, alarmBytes);
            sendMsg(deviceInfo.getDeviceId(), alarmByte);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送通用数据
     * @param signalFlag        应答命令
     * @param deviceInfo        设备信息
     * @param serialNumber      流水号
     * @param status   成功标志 0 成功（存在）  1 失败（不存在）
     */
    public void sendCommon(int signalFlag, DeviceInfo deviceInfo, long serialNumber, int status) {
        try {
            byte[] commonByte = deviceMessageFactory.generateCommon(signalFlag, deviceInfo.getDeviceCode(), serialNumber, status);
            sendMsg(deviceInfo.getDeviceId(), commonByte);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送平台设置数据
     * @param deviceInfo        设备信息
     * @param serialNumber      流水号
     */
    public void sendPlatFormData(DeviceInfo deviceInfo, long serialNumber) {
        try {
            byte[] commonByte = deviceMessageFactory.generatePlatformSet(deviceInfo.getDeviceCode(),
                    serialNumber, secretMap.get(deviceInfo.getDeviceId()));
            sendMsg(deviceInfo.getDeviceId(), commonByte);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
