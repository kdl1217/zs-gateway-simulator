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
         * 平台设置-报警
         */
        int PLAT_SET_FLAG = 0x0C;
    }


    /**
     * 密钥
     */
    byte[] KEY = Base64.getDecoder().decode("MDEyMzQ1Njc4OWFiY2RlZg==");

}
