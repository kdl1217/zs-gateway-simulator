# 中升T-Box模拟器
 
 [![java](https://img.shields.io/badge/java-1.8-brightgreen.svg?style=flat&logo=java)](https://www.oracle.com/java/technologies/javase-downloads.html)
 [![gradle](https://img.shields.io/badge/gradle-6.3-brightgreen.svg?style=flat&logo=gradle)](https://docs.gradle.org/6.3/userguide/installation.html)
 [![release](https://img.shields.io/badge/release-1.0-blue.svg)](https://github.com/kdl1217/zs-gateway-simulator)
 
 > 中升集团 - T-Box模拟器。
>> 支持内容：
>> - 上线校验
>> - 车辆状态数据
>> - 车辆报警数据
>> - 设置433密钥
>> - 获取平台设置信息
>> - 控车（车锁、空调、后备箱等）
>> - 寻车


#### 后期持续升级...

-------

 ## 模拟器启动配置
 
 ````yaml
 # Gateway Setting
 ## 暂时不支持往多台网关发送数据
 gateway:
   server-address: 网关服务地址
   server-port: 网关服务端口
 ````
 
 ## 启动设备信息修改[![modify](https://img.shields.io/badge/修改代码位置-blue.svg)](https://github.com/kdl1217/zs-gateway-simulator/blob/master/specified-device/src/main/java/com/incarcloud/device/DeviceManager.java#L44)
 
 ````java
     public void init() {
         log.info("init device information ...");
         deviceMap.put("YK001912D4",new DeviceInfo("863576043319974", "YK001912D4", "LVGEN56A8JG257045")) ;
     }
 ````
