package com.incarcloud;

import com.incarcloud.device.DeviceManager;
import com.incarcloud.runner.Receiver;
import com.incarcloud.runner.Sender;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot Launcher.
 *
 * @author Aaric, created on 2018-04-18T10:31.
 * @since 0.0.1-SNAPSHOT
 */
@Log4j2
@SpringBootApplication
public class App implements CommandLineRunner {


    @Autowired
    private DeviceManager deviceManager;

    @Autowired
    private Sender sender;

    @Autowired
    private Receiver receiver;

    /**
     * Main
     *
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... args) {
        // 初始化 设备信息
        deviceManager.init();
        // 启动发送者
        sender.start();
        // 启动接收者
        receiver.start();
    }
}
