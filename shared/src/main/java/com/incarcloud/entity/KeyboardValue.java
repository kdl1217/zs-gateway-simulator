package com.incarcloud.entity;

import com.incarcloud.boar.datapack.ic.model.KeyboardData;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

/**
 * 按键值
 *
 * @author Kong, created on 2020-11-18T13:27.
 * @version 1.0.0-SNAPSHOT
 */
@Data
@NoArgsConstructor
public class KeyboardValue {

    /**
     * 单击落锁(0:无功能,1:落锁,2:落锁+升窗,3:落锁+关闭舒适进入功能)
     */
    private int clickPadlock;

    /**
     * 单击后备箱(0:无功能,7:开/关后备箱)
     */
    private int clickTrunk;

    /**
     * 单击解锁(0:无功能,4:解锁,5:解锁+降窗,6:解锁+打开舒适进入功能)
     */
    private int clickUnlock;

    /**
     * 双击落锁(0:无功能,1:落锁,2:落锁+升窗,3:落锁+关闭舒适进入功能)
     */
    private int doubleClickPadlock;

    /**
     * 双击后备箱(0:无功能,7:开/关后备箱)
     */
    private int doubleClickTrunk;

    /**
     * 双击解锁(0:无功能,4:解锁,5:解锁+降窗,6:解锁+打开舒适进入功能)
     */
    private int doubleClickUnlock;

    /**
     * 长按落锁(0:无功能,1:落锁,2:落锁+升窗,3:落锁+关闭舒适进入功能)
     */
    private int longPressPadlock;

    /**
     * 长按后备箱(0:无功能,7:开/关后备箱)
     */
    private int longPressTrunk;

    /**
     * 长按解锁(0:无功能,4:解锁,5:解锁+降窗,6:解锁+打开舒适进入功能)
     */
    private int longPressUnlock;

    public KeyboardValue(KeyboardData keyboardData) {
        BeanUtils.copyProperties(keyboardData, this);
    }

    public KeyboardValue init() {
        this.clickPadlock = 0;
        this.clickTrunk = 0;
        this.clickUnlock = 0;
        this.doubleClickPadlock = 0;
        this.doubleClickTrunk = 0;
        this.doubleClickUnlock = 0;
        this.longPressPadlock = 0;
        this.longPressTrunk = 0;
        this.longPressUnlock = 0;
        return this;
    }
}
