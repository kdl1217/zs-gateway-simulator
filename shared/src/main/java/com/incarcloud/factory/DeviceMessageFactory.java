package com.incarcloud.factory;

import com.github.io.protocol.core.ProtocolEngine;
import com.incarcloud.boar.datapack.DataPackPosition;
import com.incarcloud.boar.datapack.ic.model.*;
import com.incarcloud.boar.datapack.ic.utils.IcDataPackUtils;
import com.incarcloud.share.Constants;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * TBox数据包创建工厂
 *
 * @since 0.0.1-SNAPSHOT
 */
public final class DeviceMessageFactory {


    /**
     * 协议解析引擎
     */
    private ProtocolEngine engine;

    /**
     * 私有构造函数
     *
     * @param engine
     */
    private DeviceMessageFactory(ProtocolEngine engine) {
        this.engine = engine;
    }

    /**
     * 单例模式
     */
    private static DeviceMessageFactory instance = new DeviceMessageFactory(new ProtocolEngine());

    static class Point {

        private double x;
        private double y;

        Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }
    }

    /**
     * 轨迹
     */
    private static List<Point> geoPoints = new ArrayList<Point>() {
        {
            add(new Point(114.4036630000D, 30.4757560000D));
            add(new Point(114.4035880000D, 30.4754320000D));
            add(new Point(114.4035120000D, 30.4750990000D));
            add(new Point(114.4034590000D, 30.4748500000D));
            add(new Point(114.4032120000D, 30.4749700000D));
            add(new Point(114.4027180000D, 30.4751730000D));
            add(new Point(114.4027720000D, 30.4755060000D));
            add(new Point(114.4028360000D, 30.4762640000D));
            add(new Point(114.4029970000D, 30.4772900000D));
            add(new Point(114.4030510000D, 30.4782240000D));
            add(new Point(114.4031690000D, 30.4791860000D));
            add(new Point(114.4032650000D, 30.4798420000D));
            add(new Point(114.4042740000D, 30.4797310000D));
            add(new Point(114.4052610000D, 30.4797400000D));
            add(new Point(114.4062590000D, 30.4795830000D));
            add(new Point(114.4073960000D, 30.4794440000D));
            add(new Point(114.4088980000D, 30.4793330000D));
            add(new Point(114.4097240000D, 30.4786120000D));
            add(new Point(114.4097880000D, 30.4780670000D));
            add(new Point(114.4096600000D, 30.4772900000D));
            add(new Point(114.4095310000D, 30.4770500000D));
            add(new Point(114.4086830000D, 30.4770220000D));
            add(new Point(114.4079110000D, 30.4771420000D));
            add(new Point(114.4078360000D, 30.4768920000D));
        }
    };

    /**
     * 静态工厂方法
     */
    public static DeviceMessageFactory getInstance() {
        return instance;
    }

    /**
     * 生成上线校验报文
     * @param deviceCode            设备号
     * @param serialNumber          流水号
     * @return      报文
     * @throws Exception        异常
     */
    public byte[] generateCheckByte(String deviceCode, Long serialNumber) throws Exception {
        CheckData checkData = new CheckData();
        checkData.setSoftwareVersionNo(new byte[]{0x03, 0x01, 0x07});
        checkData.setVinLength(0);
        checkData.setTelephoneLength(0);
        checkData.setBluetoothNameLength(0);
        checkData.setBluetoothMacLength(0);
        checkData.setVehicleCode(new byte[]{-117, 3, 37, 0});
        checkData.setGatherTime(IcDataPackUtils.date2buf(Calendar.getInstance().getTimeInMillis())); // 时间戳

        byte[] byteData = this.engine.encode(checkData);
        IcPackage icPackage = new IcPackage();

        int length = byteData.length + 12 + deviceCode.length();
        Header header = getHeader(deviceCode, Constants.CommandId.CHECK_CMD_FLAG, serialNumber, length);

        Tail tail = new Tail();
        tail.setSideWord(13);

        icPackage.setHeader(header);
        icPackage.setBodyBuffer(byteData);
        icPackage.setTail(tail);

        byte[] bytes = this.engine.encode(icPackage);

        return IcDataPackUtils.addCheck(bytes, Constants.KEY);
    }

    /**
     * 生成运行数据
     * @param deviceCode            设备号
     * @param serialNumber          流水号
     * @return      报文
     * @throws Exception        异常
     */
    public byte[] generateRunByte(String deviceCode, int serialNumber, int index, int commandId) throws Exception {

        // 对象数据
        OverviewData overviewData = new OverviewData();
        overviewData.setMember(12345678); // 会员号

        // 对象数据，车辆状态 - 不用变
        overviewData.setVehicleConfigData(new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF});

        // 表2 状态位定义
        VehicleStatus1 vehicleStatus1 = new VehicleStatus1();
        vehicleStatus1.setIsValidate(DataPackPosition.POSITION_MODE_GPS);           // 0：未定位；1：定位
        vehicleStatus1.setLatType(0);                                               // 0:北纬；1:南纬
        vehicleStatus1.setLngType(0);                                               // 0:东经；1:西经
        vehicleStatus1.setLeftFrontDoorStatus(2);   // 左前门状态    0：未检测；1：不支持；2：关；  3：开
        vehicleStatus1.setRightFrontDoorStatus(2);  // 右前门状态  0：未检测；1：不支持；2：关；  3：开
        vehicleStatus1.setLeftBackDoorStatus(2);    // 左后门状态      0：未检测；1：不支持；2：关；  3：开
        vehicleStatus1.setRightBackDoorStatus(2);   // 右后门状态    0：未检测；1：不支持；2：关；  3：开
        vehicleStatus1.setTrunkStatus(2);           // 后备箱状态        0：未检测；1：不支持；2：关；  3：开
        vehicleStatus1.setLockStatus(2);            // 车锁状态       0：未检测；1：不支持；2：关；  3：开
        vehicleStatus1.setWindowStatus(2);          // 车窗状态   0：未检测；1：不支持；2：关；  3：开
        vehicleStatus1.setAccStatus(2);             // ACC状态    0：未检测；1：不支持；2：关；  3：开
        vehicleStatus1.setHandBrakeStatus(3);           // 手刹状态     0：未检测；1：不支持；2：关；  3：开
        // 档位 档位  0：未检测；1：不支持；2：P；  3：R；4：N；5：D；6：S；7：L；8：E；9：B； 10：1；11：2；12：3；13：4；14：5；15：6；16：7；17：8；18：9； 19~31其他
        vehicleStatus1.setGearStatus(2);
        vehicleStatus1.setChargeStatus(0);              // 充电状态       0：未检测；1：不支持；2：未充电；3：充电中
        vehicleStatus1.setEvChargerStatus(0);           // 充电枪状态        0：未检测；1：不支持；2：未插充电枪；3：已插充电枪
        byte[] bytes1 = vehicleStatus1.encoder();
        overviewData.setVehicleStatus1(bytes1);

        // 表3 状态位定义
        VehicleStatus2 vehicleStatus2 = new VehicleStatus2();
        vehicleStatus2.setLeftFrontWindowStatus(2); // 左前窗状态        0：未检测；1：不支持；2：关；  3：开
        vehicleStatus2.setRightFrontWindowStatus(2); // 右前窗状态      0：未检测；1：不支持；2：关；  3：开
        vehicleStatus2.setLeftBackWindowStatus(2); // 左后窗状态      0：未检测；1：不支持；2：关；  3：开
        vehicleStatus2.setRightBackWindowStatus(2); // 右后窗状态        0：未检测；1：不支持；2：关；  3：开
        vehicleStatus2.setSkylightStatus(2); // 天窗状态       0：未检测；1：不支持；2：关；  3：开
        vehicleStatus2.setRemoteControlLockStatus(2); // 遥控锁状态        0：未检测；1：不支持；2：关；  3：开
        vehicleStatus2.setPkeStatus(2); // PKE状态        0：未检测；1：不支持；2：关；  3：开
        vehicleStatus2.setStartStatus(2); // 启动状态     0：未检测；1：不支持；2：关；  3：开
        vehicleStatus2.setHeadlightStatus(2); // 大灯状态     0：未检测；1：不支持；2：关；  3：开
        vehicleStatus2.setFootBrakeStatus(2); // 脚刹状态     0：未检测；1：不支持；2：关；  3：开
        vehicleStatus2.setMainSeatBeltsStatus(2); // 主驾安全带        0：未检测；1：不支持；2：关；  3：开
        vehicleStatus2.setPassengerSeatBeltsStatus(2); // 副驾安全带      0：未检测；1：不支持；2：关；  3：开
        byte[] bytes2 = vehicleStatus2.encoder();
        overviewData.setVehicleStatus2(bytes2);

        overviewData.setGatherTime(IcDataPackUtils.date2buf(Calendar.getInstance().getTimeInMillis())); // 时间戳
        overviewData.setLongitude(geoPoints.get(index).getX()); //经度 114.40241
        overviewData.setLatitude(geoPoints.get(index).getY()); // 纬度 30.477125
        overviewData.setDirection(56); // 方向
        overviewData.setAltitude(156); // 海拔高度
        overviewData.setGpsSpeed(25); // GPS速度

        overviewData.setSoc(20); // SOC
        overviewData.setSpeed(50); // 行驶记录速度 km/h
        overviewData.setMileage(new byte[]{0, 20, 25}); // 车辆行驶里程
        overviewData.setBatteryVoltage(12); // 蓄电池电压
        overviewData.setPowerCellVoltage(122); // 动力电池电压
        overviewData.setGnssSatelliteNumber(1); // GNSS卫星数量
        overviewData.setGnssChannelNumber(1); // GNSS通道数量
        overviewData.setFaultLenth(4);
        overviewData.setFault("Kong"); // 故障信息，多个以逗号分隔
        overviewData.setAirConditionerStatus(1); // 空调状态
        overviewData.setAirConditionerFanStatus(1); // 空调风扇状态
        overviewData.setRechargeMileage(new byte[]{0, 12, 25}); // 续航里程 【0.1KM】
        overviewData.setFuelQuantity(80); // 剩余油量 【单位：%】
        overviewData.setLeftFrontTirePressure(280); // 左前轮胎压 【单位：kPa】
        overviewData.setRightFrontTirePressure(280); // 右前轮胎压【单位：kPa】
        overviewData.setLeftRearTirePressure(280); // 左后轮胎压【单位：kPa】
        overviewData.setRightRearTirePressure(280); // 右后轮胎压【单位：kPa】
        overviewData.setInsideTemperature(25.5); // 车内温度【单位：℃】
        overviewData.setDynamicalFuel(15.5D); // 瞬时油耗【单位：0.1L/100KM】
        overviewData.setAvgOilUsed(8.5D); // 平均油耗【单位：0.1L/100KM】


        byte[] byteData = this.engine.encode(overviewData);
        IcPackage icPackage = new IcPackage();

        int length = byteData.length + 12 + deviceCode.length();
        Header header = getHeader(deviceCode, commandId, serialNumber, length);

        Tail tail = new Tail();
        tail.setSideWord(13);

        icPackage.setHeader(header);
        icPackage.setBodyBuffer(byteData);
        icPackage.setTail(tail);

        byte[] bytes = this.engine.encode(icPackage);

        return IcDataPackUtils.addCheck(bytes, Constants.KEY);
    }

    /**
     * 生成通用应答报文
     * @param signalFlag            命令包
     * @param deviceCode            设备编号
     * @param serialNumber          流水号
     * @param status                返回状态
     * @return      报文
     * @throws Exception        异常
     */
    public byte[] generateCommon(int signalFlag, String deviceCode, long serialNumber, int status) throws Exception {

        CommonRespData resp = new CommonRespData();
        resp.setSignalFlag(signalFlag);
        resp.setResult(status);                             // 成功标志 0 成功（存在）  1 失败（不存在）
        resp.setError(new byte[]{0x00, 0x00, 0x00, 0x00});  // 错误码
        resp.setDate(IcDataPackUtils.date2buf(Calendar.getInstance().getTimeInMillis())); // 时间

        byte[] byteData = this.engine.encode(resp);
        IcPackage icPackage = new IcPackage();

        int length = byteData.length + 12 + deviceCode.length();

        Header header = getHeader(deviceCode, 0xEE, serialNumber, length);

        Tail tail = new Tail();
        tail.setSideWord(13);

        icPackage.setHeader(header);
        icPackage.setBodyBuffer(byteData);
        icPackage.setTail(tail);

        byte[] bytes = this.engine.encode(icPackage);
        return IcDataPackUtils.addCheck(bytes, Constants.KEY);
    }

    /**
     * 生成报警数据报文
     * @param deviceCode            设备号
     * @param serialNumber          流水号
     * @return      报文
     * @throws Exception        异常
     */
    public byte[] generateAlarm(String deviceCode, int serialNumber, byte[] alarmBytes) throws Exception {
        // 对象数据
        AlarmData alarmData = new AlarmData();

        PositionData positionData = new PositionData();
        positionData.setIsValidate(1); // 定位方式
        positionData.setLngType(0); // 0:东经；1:西经
        positionData.setLatType(0); // 0:北纬；1:南纬
        positionData.setLongitude(114.40241); //经度 114.40241
        positionData.setLatitude(30.477125); // 纬度 30.477125
        positionData.setDirection(56); // 方向
        positionData.setAltitude(156); // 海拔高度
        positionData.setGpsSpeed(25); // GPS速度

        alarmData.setPositionData(positionData);

        alarmData.setAlarmByte(alarmBytes);

        alarmData.setGatherTime(IcDataPackUtils.date2buf(Calendar.getInstance().getTimeInMillis()));

        byte[] byteData = this.engine.encode(alarmData);
        IcPackage icPackage = new IcPackage();

        int length = byteData.length + 12 + deviceCode.length();
        Header header = getHeader(deviceCode, Constants.CommandId.ALARM_CMD_FLAG, serialNumber, length);

        Tail tail = new Tail();
        tail.setSideWord(13);

        icPackage.setHeader(header);
        icPackage.setBodyBuffer(byteData);
        icPackage.setTail(tail);

        byte[] bytes = this.engine.encode(icPackage);

        return IcDataPackUtils.addCheck(bytes, Constants.KEY);
    }

    /**
     *  生成平台设置
     * @param deviceCode            设备号
     * @param serialNumber          流水号
     * @param smartSecret           433密钥
     * @return      报文
     * @throws Exception        异常
     */
    public byte[] generatePlatformSet(String deviceCode, long serialNumber, String smartSecret) throws Exception {
        // 对象数据
        PlatformSetData platformSetData = new PlatformSetData();
        // 手机号码
        List<String> numbers = Collections.singletonList("1064899103098");
        int length = 1; //手机号码长度 组数 1 字节
        for (String s : numbers) {
            length += 1; // 号码长度
            length += s.length();
        }
        byte[] numberByte = new byte[length];
        numberByte[0] = (byte) numbers.size();
        int index = 1;
        for (String number : numbers) {
            numberByte[index] = (byte) number.length();
            index++;
            System.arraycopy(number.getBytes(), 0, numberByte, index, number.length());
            index += number.length();
        }

        platformSetData.setFreeFrequency(120); // 空闲上报频率
        platformSetData.setWorkFrequency(30); // 工作上报频率
        platformSetData.setAlarmFrequency(10); // 告警上报频率
        String ip = "tbox.zs.incarcloud.com";
        platformSetData.setIpLength(ip.length());
        platformSetData.setIp(ip.getBytes()); // IP
        platformSetData.setPort(6666); // 端口号
        platformSetData.setStandbyIpLength(0);

        // 默认密钥
        if (StringUtils.isNotBlank(smartSecret)) {
            platformSetData.setSmartSecretLength(smartSecret.length());
            platformSetData.setSmartSecret(smartSecret.getBytes());
        } else {
            platformSetData.setSmartSecretLength(0);
        }

        byte[] byteData = this.engine.encode(platformSetData);

        byte[] bytes = new byte[numberByte.length + byteData.length];
        System.arraycopy(numberByte, 0, bytes, 0, numberByte.length);
        System.arraycopy(byteData, 0, bytes, numberByte.length, byteData.length);

        IcPackage icPackage = new IcPackage();

        int headerLength = bytes.length + 12 + deviceCode.length();
        Header header = getHeader(deviceCode, Constants.CommandId.PLAT_SET_FLAG, serialNumber, headerLength);

        Tail tail = new Tail();
        tail.setSideWord(13);

        icPackage.setHeader(header);
        icPackage.setBodyBuffer(bytes);
        icPackage.setTail(tail);

        byte[] responseByte = this.engine.encode(icPackage);

        return IcDataPackUtils.addCheck(responseByte, Constants.KEY);
    }

    private Header getHeader(String deviceCode, int cmdFlag, long serialNumber, int length) {
        Header header = new Header();
        header.setCmdFlag(cmdFlag); // 主信令
        header.setLength(length);  // 数据单元长度
        header.setBoxFlagLength(deviceCode.length()); //TBOX标识长度
        header.setBoxFlag(deviceCode); // TBOX标识
        header.setCommandId(serialNumber); // 指令ID
        return header;
    }

}
