package com.incarcloud.device;

import com.github.io.protocol.core.ProtocolEngine;
import com.github.io.protocol.utils.HexStringUtil;
import com.incarcloud.boar.datapack.ic.model.DeviceData;
import com.incarcloud.boar.datapack.ic.model.IcPackage;
import com.incarcloud.entity.DeviceInfo;
import com.incarcloud.entity.KeyboardValue;
import com.incarcloud.factory.DeviceMessageFactory;
import com.incarcloud.share.Constants;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.Socket;
import java.util.Base64;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 设备管理者
 *
 * @author Kong, created on 2020-06-18T14:08.
 * @since 1.0.0-SNAPSHOT
 */
@Log4j2
@Component
public class DeviceManager {

    /**
     * 网关服务地址
     */
    @Value("${gateway.read-file}")
    Boolean readFile;

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
    public void init() throws IOException {
        log.info("init device information ...");
        if (readFile) {
            readProperties();
        } else {
//            deviceMap.put("YK001912D4", new DeviceInfo("867852139722605", "YK001912D4", "CS123456789012345"));
            // 25
//            deviceMap.put("YK001912D4", new DeviceInfo("863576043319974", "YK001912D4", "LVGEN56A8JG257045"));
            // 开发环境
            deviceMap.put("CTTTEST01", new DeviceInfo("66666666666666666", "CTTTEST01", "TESTCT00000000001"));

        }

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
     * 初始化集合, 是否第一次发送
     */
    private Map<String, Integer> initMap = new ConcurrentHashMap<>();

    public Map<String, Integer> getInitMap() {
        return initMap;
    }

    /**
     * 车型编码集合
     */
    private Map<String, byte[]> vehicleCodeMap = new ConcurrentHashMap<>();

    public Map<String, byte[]> getVehicleCodeMap() {
        return vehicleCodeMap;
    }

    /**
     * 下发的433密钥
     */
    private Map<String, String> secretMap = new ConcurrentHashMap<>();

    public Map<String, String> getSecretMap() {
        return secretMap;
    }

    private Map<String, KeyboardValue> keyboardValueMap = new ConcurrentHashMap<>();

    public Map<String, KeyboardValue> getKeyboardValueMap() {
        return keyboardValueMap;
    }

    /**
     * 读取配置文件
     * @throws IOException IO异常
     */
    private void readProperties() throws IOException {
        log.info("read smart properties ...");
        Properties properties = new Properties();
        // 使用InPutStream流读取properties文件
        BufferedReader bufferedReader = new BufferedReader(new FileReader("D:\\zs-simulator\\smart.properties"));
        properties.load(bufferedReader);

        this.serverAddress = properties.getProperty("server-address");
        this.serverPort = Integer.parseInt(properties.getProperty("server-port"));
        String deviceStr = properties.getProperty("devices");
        String[] devices = deviceStr.split(",");
        for (String s : devices) {
            if (StringUtils.isNotBlank(s)) {
                String[] device = s.split("[|]");
                if (device.length == 3) {
                    deviceMap.put(device[0], new DeviceInfo(device[1], device[0], device[2]));
                } else if (device.length == 4) {
                    byte[] key = Base64.getDecoder().decode(device[3]);
                    deviceMap.put(device[0], new DeviceInfo(device[1], device[0], device[2], key));
                }
            }
        }
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
            byte[] checkByte = deviceMessageFactory.generateCheckByte(deviceInfo.getDeviceCode(),
                    serialNumber, this.vehicleCodeMap.get(deviceInfo.getDeviceId()), deviceInfo.getKey());
            // 解析器
            ProtocolEngine engine = new ProtocolEngine();
            IcPackage icPackage = engine.decode(checkByte, IcPackage.class);
            // 数据单元
            byte[] dataBuffer = icPackage.getBodyBuffer();
            DeviceData deviceData = engine.decode(dataBuffer, DeviceData.class);
            this.vehicleCodeMap.put(deviceInfo.getDeviceId(), deviceData.getVehicleCode());
            this.initMap.put(deviceInfo.getDeviceId(), 0);
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
            byte[] runByte = deviceMessageFactory.generateRunByte(deviceInfo.getDeviceCode(), 1,
                    index, Constants.CommandId.RUN_CMD_FLAG, deviceInfo.getKey());
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
    public void sendBackRunData(DeviceInfo deviceInfo, long serialNumber, int index) {
        try {
            byte[] runByte = deviceMessageFactory.generateRunByte(deviceInfo.getDeviceCode(), serialNumber,
                    index, Constants.CommandId.RUN_BACK_CMD_FLAG, deviceInfo.getKey());
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
    public void sendAlarmData(DeviceInfo deviceInfo, byte[] alarmBytes, int index) {
        try {
            byte[] alarmByte = deviceMessageFactory.generateAlarm(deviceInfo.getDeviceCode(), 1,
                    alarmBytes, deviceInfo.getKey(), index);
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
            byte[] commonByte = deviceMessageFactory.generateCommon(signalFlag, deviceInfo.getDeviceCode(),
                    serialNumber, status, deviceInfo.getKey());
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
    public void sendDeviceData(DeviceInfo deviceInfo, long serialNumber) {
        try {
            byte[] commonByte = deviceMessageFactory.generateDeviceInfo(deviceInfo.getDeviceCode(),
                    serialNumber, this.vehicleCodeMap.get(deviceInfo.getDeviceId()), deviceInfo.getKey());
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
                    serialNumber, secretMap.get(deviceInfo.getDeviceId()), deviceInfo.getKey());
            sendMsg(deviceInfo.getDeviceId(), commonByte);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送按键设置数据
     * @param deviceInfo        设备信息
     * @param serialNumber      流水号
     */
    public void sendKeyboardData(DeviceInfo deviceInfo, long serialNumber) {
        try {
            byte[] commonByte = deviceMessageFactory.generateKeyboard(deviceInfo.getDeviceCode(),
                    keyboardValueMap.get(deviceInfo.getDeviceId()), serialNumber, deviceInfo.getKey());
            sendMsg(deviceInfo.getDeviceId(), commonByte);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
