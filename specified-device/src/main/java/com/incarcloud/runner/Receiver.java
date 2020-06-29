package com.incarcloud.runner;

import com.github.io.protocol.core.ProtocolEngine;
import com.github.io.protocol.utils.HexStringUtil;
import com.incarcloud.boar.datapack.ic.model.Header;
import com.incarcloud.boar.datapack.ic.model.IcPackage;
import com.incarcloud.boar.datapack.ic.utils.IcDataPackUtils;
import com.incarcloud.device.DeviceManager;
import com.incarcloud.entity.DeviceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 * 接收者
 *
 * @author Kong, created on 2020-06-18T14:00.
 * @since 1.0.0-SNAPSHOT
 */
@Component
public class Receiver {

    private Logger log = LoggerFactory.getLogger(Receiver.class);

    @Autowired
    protected DeviceManager deviceManager;

    public void start() {
        log.info("simulator receiver running ...");

        //创建接收数据线程
        deviceManager.getDeviceMap().forEach((key, deviceInfo) -> {
            Thread thread = new Thread(() -> {
                while (true) {
                    Socket socket = deviceManager.getSocketMap().get(deviceInfo.getDeviceId());
                    if (null != socket) {
                        try {
                            InputStream inputStream = socket.getInputStream();
                            int bytesRcv;
                            byte[] recData = new byte[1024];
                            bytesRcv = inputStream.read(recData);
                            if (bytesRcv == -1) {
                                throw new SocketException("Connection closed prematurely");
                            }

                            byte[] buffer = new byte[bytesRcv];
                            System.arraycopy(recData, 0, buffer, 0, bytesRcv);

                            // 发送指令
                            sendCommand(deviceInfo, buffer);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
        });
    }

    /**
     * 发送指令
     * @param deviceInfo        设备信息
     * @param buffer            接收到的指令
     */
    public void sendCommand(DeviceInfo deviceInfo, byte[] buffer) throws Exception {

        if (buffer[2] == 0x07 || buffer[2] == 0x08 || buffer[2] == 0x03) {
            // 解析器
            ProtocolEngine engine = new ProtocolEngine();
            // 设备长度
            int deviceSnLength = buffer[5];
            // 获取协议头部信息
            byte[] headerBytes = IcDataPackUtils.getRange(buffer, 0, 10 + deviceSnLength);
            Header header = engine.decode(headerBytes, Header.class);   // 包头

            // 成功标志 0 成功（存在）  1 失败（不存在）
            int status = 0; // 默认
            if (buffer[2] == 0x03) {                // 设置 433 指令
                if (status == 0) {
                    // 解析下发的433指令
                    try {
                        IcPackage icPackage = engine.decode(buffer, IcPackage.class);
                        String secret = new String(icPackage.getBodyBuffer());
                        deviceManager.getSecretMap().put(deviceInfo.getDeviceId(), secret);
                    } catch (Exception e) {
                        log.error("decode error", e);
                    }
                }
                log.info("*******************Rcv 433M command****** deviceId : {} <--  {}",
                        deviceInfo.getDeviceId(), HexStringUtil.toHexString(buffer));
            } else {
                if (buffer[2] == 0x07) {           // 控车 指令
                    IcPackage icPackage = engine.decode(buffer, IcPackage.class);
                    String msg = "";
                    if (icPackage.getBodyBuffer()[0] == 1) { // 车锁动作 - 开
                        msg += "解锁,";
                    }
                    if (icPackage.getBodyBuffer()[0] == 2) { // 车锁动作 - 关
                        msg += "关锁,";
                    }
                    if (icPackage.getBodyBuffer()[1] == 1) { // 动力 - 开
                        msg += "开动力,";
                    }
                    if (icPackage.getBodyBuffer()[1] == 2) { // 动力 - 关
                        msg += "关动力,";
                    }
                    if (icPackage.getBodyBuffer()[2] == 1) { // 车窗 - 开
                        msg += "开窗,";
                    }
                    if (icPackage.getBodyBuffer()[2] == 2) { // 车窗 - 关
                        msg += "关窗,";
                    }
                    if (icPackage.getBodyBuffer()[3] == 1) { // 后备箱 - 开
                        msg += "开后备箱,";
                    }
                    if (icPackage.getBodyBuffer()[3] == 2) { // 后备箱 - 关
                        msg += "关后备箱,";
                    }
                    if (icPackage.getBodyBuffer()[4] == 1) { // 天窗 - 开
                        msg += "开天窗,";
                    }
                    if (icPackage.getBodyBuffer()[4] == 2) { // 天窗 - 关
                        msg += "关天窗,";
                    }
                    if (icPackage.getBodyBuffer()[5] == 1) { // 点火 - 开
                        msg += "开引擎,";
                    }
                    if (icPackage.getBodyBuffer()[5] == 2) { // 点火 - 关
                        msg += "关引擎,";
                    }
                    if (icPackage.getBodyBuffer()[6] == 1) { // 空调 - 开
                        msg += "开空调,";
                    }
                    if (icPackage.getBodyBuffer()[6] == 2) { // 空调 - 关
                        msg += "关空调,";
                    }

//                                        Thread.sleep(10000);

                    log.info("收到{}指令，远控返回成功。deviceId : {} <--  {}",
                            msg, deviceInfo.getDeviceId(), HexStringUtil.toHexString(buffer));
                } else {
                    log.info("收到寻车指令，远控返回成功。deviceId : {} <--  {}",
                            deviceInfo.getDeviceId(), HexStringUtil.toHexString(buffer));
                }
            }
            // 返回通用应答
            deviceManager.sendCommon(buffer[2], deviceInfo, header.getCommandId(), status);
        } else if (buffer[2] == 0x0A) {        // 请求车况数据
            log.info("收到请求车辆状态数据！！！");
            // 解析器
            ProtocolEngine engine = new ProtocolEngine();
            // 设备长度
            int deviceSnLength = buffer[5];
            // 获取协议头部信息
            byte[] headerBytes = IcDataPackUtils.getRange(buffer, 0, 10 + deviceSnLength);
            Header header = engine.decode(headerBytes, Header.class);   // 包头
            log.info("Rcv request vehicle data！！！");
            deviceManager.sendBackRunData(deviceInfo, header.getCommandId(), 0);
        } else if (buffer[2] == 0x16) {        // 收到请求平台设置
            // 解析器
            ProtocolEngine engine = new ProtocolEngine();
            // 设备长度
            int deviceSnLength = buffer[5];
            // 获取协议头部信息
            byte[] headerBytes = IcDataPackUtils.getRange(buffer, 0, 10 + deviceSnLength);
            Header header = engine.decode(headerBytes, Header.class);   // 包头
            log.info("Rcv request platform set data！！！");
            deviceManager.sendPlatFormData(deviceInfo, header.getCommandId());
        }
        log.info("send deviceId : {} <--  {}", deviceInfo.getDeviceId(), HexStringUtil.toHexString(buffer));
    }

}
