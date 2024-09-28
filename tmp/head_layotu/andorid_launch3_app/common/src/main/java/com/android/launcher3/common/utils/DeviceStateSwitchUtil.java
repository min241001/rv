package com.android.launcher3.common.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备状态值转tcp协议内容
 * @Author: jamesfeng
 * @Description：
 * @CreateDate：2024/1/25 13:57
 * @UpdateUser: jamesfeng
 */
public class DeviceStateSwitchUtil {

    /**
     * 获取二进制设备状态
     *
     * @param alarmValue  报警类型
     * @param statusValue 设备状态
     * @return 二进制字符串
     */
    public static String getBinaryString(int alarmValue, int statusValue) {
        if (statusValue < 0 && statusValue != -1) {
            statusValue = 0;
        }
        if (statusValue > 15) {
            statusValue = 15;
        }
        if (alarmValue < 16 && alarmValue != -1) {
            alarmValue = 16;
        }
        if (alarmValue > 31) {
            alarmValue = 31;
        }
        if(statusValue == -1){
            statusValue = 0;
        }else {
            statusValue = 1 << statusValue;
        }
        if(alarmValue == -1){
            alarmValue = 0;
        }else {
            alarmValue = 1 << alarmValue;
        }

        int result = alarmValue | statusValue;
        return String.format("%32s", Integer.toBinaryString(result)).replace(' ', '0');
    }

    /**
     * 获取16进制设备状态
     *
     * @param alarmValue  报警类型
     * @param statusValue 设备状态
     * @return 二进制字符串
     */
    public static String getHexString(int alarmValue, int statusValue) {
        if (statusValue < 0 && statusValue != -1) {
            statusValue = 0;
        }
        if (statusValue > 15) {
            statusValue = 15;
        }
        if (alarmValue < 16 && alarmValue != -1) {
            alarmValue = 16;
        }
        if (alarmValue > 31) {
            alarmValue = 31;
        }
        if(statusValue == -1){
            statusValue = 0;
        }else {
            statusValue = 1 << statusValue;
        }
        if(alarmValue == -1){
            alarmValue = 0;
        }else {
            alarmValue = 1 << alarmValue;
        }
        int result = alarmValue | statusValue;
        return String.format("%08X", result);
    }

    /**
     * 判断设备状态是否启用
     *
     * @param deviceState  设备状态
     * @param binaryString 二进制设备状态
     * @return 结果
     */
    public static Boolean checkDeviceStateByBinaryString(int deviceState, String binaryString) {
        if (binaryString == null) {
            binaryString = "00000000000000000000000000000000";
        }
        if (binaryString.length() != 32) {
            binaryString = "00000000000000000000000000000000";
        }
        if (deviceState < 0) {
            deviceState = 0;
        }
        if (deviceState > 31) {
            deviceState = 31;
        }
        int result = 0;
        try {
            result = Integer.parseInt(binaryString, 2);
        } catch (Exception exception) {

        }
        // String dd =String.format("%32s", Integer.toBinaryString(1 << deviceState)).replace(' ', '0');
        result = result & (1 << deviceState);
        return (result & (1 << deviceState)) != 0;
    }


    /**
     * 判断设备状态是否启用
     *
     * @param deviceState 设备状态
     * @param hexString   16进制设备状态
     * @return 结果
     */
    public static Boolean checkDeviceStateByHexString(int deviceState, String hexString) {
        String binaryString = String.format("%32s", Integer.toBinaryString(Integer.parseInt(hexString, 16))).replace(' ', '0');
        return checkDeviceStateByBinaryString(deviceState, binaryString);

    }

    /**
     * 获取所有已启用的设备状态
     *
     * @param binaryString 32位二进制设备状态
     * @return 列表
     */
    public static List<Integer> getAllDeviceStateListByBinaryString(String binaryString) {
        if (binaryString == null) {
            binaryString = "00000000000000000000000000000000";
        }
        if (binaryString.length() != 32) {
            binaryString = "00000000000000000000000000000000";
        }
        char[] stateChars = binaryString.toCharArray();
        List resultList = new ArrayList(32);
        int idx = 0;
        for (int i = stateChars.length - 1; i >= 0; i--) {
            if (stateChars[i] == '1') {
                resultList.add(idx);
            }
            idx++;
        }
        return resultList;
    }

    /**
     * 获取所有已启用的设备状态
     *
     * @param hexString 16进制设备状态
     * @return 列表
     */
    public static List<Integer> getAllDeviceStateListByHexString(String hexString) {
        int hex = Integer.parseInt(hexString, 16);
        String binaryString = String.format("%32s", Integer.toBinaryString(hex)).replace(' ', '0');
        return getAllDeviceStateListByBinaryString(binaryString);
    }

    public interface DeviceStateConstant {

        /**
         * 未知
         */
        int UNKNOWN = -1;
        /**
         * 低电量
         */
        int LOW_BATTERY = 0;
        /**
         * 出围栏状态
         */
        int OUT_OF_FENCE = 1;
        /**
         * 进围栏状态
         */
        int IN_FENCE = 2;
        /**
         * 手环戴上取下状态
         */
        int WEARABLE_ON_OFF = 3;
        /**
         * 手表运行静止状态
         */
        int WATCH_MOVING = 4;
        /**
         * sos 报警
         */
        int SOS_ALERT = 16;

        /**
         * 低电报警
         */
        int LOW_BATTERY_ALERT = 17;

        /**
         * 出围栏报警
         */
        int OUT_OF_FENCE_ALERT = 18;

        /**
         * 进围栏报警
         */
        int IN_FENCE_ALERT = 19;

        /**
         * 手环拆除报警
         */
        int REMOVE_WATCH_ALERT = 20;
        /**
         * 跌倒报警
         */
        int FALL_DOWN_ALERT = 21;
        /**
         * 震动报警
         */
        int VIBRATE_ALERT = 22;

        /**
         * 断电报警
         */
        int OUT_AGE_ALERT = 23;

        /**
         * 心率骤降报警
         */
        int SUDDEN_DROP_IN_HEART_RATE_ALERT = 24;

        /**
         * 车辆启动
         */
        int VEHICLE_START_ALERT = 25;

        /**
         * 低温报警
         */
        int LOW_TEMP_ALERT = 26;
        /**
         * 高温报警
         */
        int HIGH_TEMP_ALERT = 27;
        /**
         * 换 IMSI 报警
         */
        int CHANGE_IMSI_ALERT = 28;
    }

    public static void main(String[] args) {
        int idx = Integer.parseInt("00000000001000000000000000010000", 2);
        String str = String.format("%08X", idx);
        String test1 =  DeviceStateSwitchUtil.getBinaryString(DeviceStateConstant.UNKNOWN, DeviceStateConstant.UNKNOWN);
        String test3 =  DeviceStateSwitchUtil.getHexString(DeviceStateConstant.UNKNOWN, DeviceStateConstant.UNKNOWN);

        List list = DeviceStateSwitchUtil.getAllDeviceStateListByBinaryString(test1);
        String test2 = DeviceStateSwitchUtil.getHexString(DeviceStateConstant.FALL_DOWN_ALERT, DeviceStateConstant.WATCH_MOVING);
        Boolean ifFallDown = DeviceStateSwitchUtil.checkDeviceStateByHexString(DeviceStateConstant.FALL_DOWN_ALERT,test2);
        List list2 = DeviceStateSwitchUtil.getAllDeviceStateListByHexString(test2);
    }
}
