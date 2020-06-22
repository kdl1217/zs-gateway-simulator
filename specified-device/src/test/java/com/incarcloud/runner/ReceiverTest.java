package com.incarcloud.runner;

import com.incarcloud.boar.cmd.CommandType;
import com.incarcloud.boar.datapack.DataPackDownlinkControl;
import com.incarcloud.boar.datapack.IcCommandFactory;
import com.incarcloud.entity.DeviceInfo;
import com.incarcloud.share.Constants;
import io.netty.buffer.ByteBuf;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;

/**
 * note
 *
 * @author Kong, created on 2020-06-22T08:37.
 * @since 1.0.0-SNAPSHOT
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ReceiverTest {

    @Autowired
    private Receiver receiver;

    @Test
    @Ignore
    public void test0() throws Exception {
        receiver.deviceManager.init();

        DeviceInfo deviceInfo = receiver.deviceManager.getDeviceMap().get("YK001912D4");

        DataPackDownlinkControl control = new DataPackDownlinkControl();
        control.setKey(Constants.KEY);
        control.setBoxFlag(deviceInfo.getDeviceCode());
        control.setCommandId(Instant.now().getEpochSecond());
        IcCommandFactory icCommandFactory = new IcCommandFactory();
        // 创建远程指令
        ByteBuf byteBuf = icCommandFactory.createCommand(CommandType.QUERY_PLATFORM_SET_PARAMS, control);

        receiver.sendCommand(deviceInfo, byteBuf.array());

    }

}
