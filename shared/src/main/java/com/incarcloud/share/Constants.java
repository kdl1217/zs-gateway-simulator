package com.incarcloud.share;


import java.util.Base64;

/**
 * 常量类
 *
 * @author Kong, created on 2018-04-18T13:36.
 * @since 0.0.1-SNAPSHOT
 */
public interface Constants {

    /**
     * 命令标识
     */
    interface CommandId {
        /**
         * 命令标志-上线校验
         */
        int CHECK_CMD_FLAG = 0x33;
        /**
         * 命令标志-运行
         */
        int RUN_CMD_FLAG = 0x32;
        /**
         * 命令标志-运行 - 回馈
         */
        int RUN_BACK_CMD_FLAG = 0x41;
        /**
         * 命令标志-报警
         */
        int ALARM_CMD_FLAG = 0x34;
        /**
         * 获取平台设置
         */
        int PLAT_SET_FLAG = 0x64;
    }


    /**
     * 密钥
     */
    byte[] KEY = Base64.getDecoder().decode("MDEyMzQ1Njc4OWFiY2RlZg==");


    interface AlarmType {
        /**
         * 关闭所有报警
         */
        byte[] CLOSE_ALL = new byte[]{0x00, 0x00};
        /**
         * 碰撞
         */
        byte[] CRASH = new byte[]{0x00, 0x01};
        /**
         * 震动
         */
        byte[] SHAKE = new byte[]{0x00, 0x02};
        /**
         * 空闲时有速度
         */
        byte[] IDLE_SPEED = new byte[]{0x00, 0x04};
        /**
         * gnss天线断开
         */
        byte[] GNSS_DISCONNECT = new byte[]{0x00, 0x08};
        /**
         * 蓄电池欠压
         */
        byte[] BATTERY_UNDERVOLTAGE = new byte[]{0x00, 0x10};
        /**
         * 蓄电池过压
         */
        byte[] BATTERY_OVERVOLTAGE = new byte[]{0x00, 0x20};
    }
}
