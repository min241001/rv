package com.android.launcher3.common.utils;

public class ArrayUtil {

    /**
     * 求和
     *
     * @param arr
     * @return
     */
    public static int sum(int[] arr) {
        int sum = 0;
        for (int val : arr) {
            sum += val;
        }
        return sum;
    }

    /**
     * 计算圆心 X
     *
     * @param radius
     * @param angle
     * @return
     */
    public static int getCircleX(double radius, double angle) {
        return (int) (radius * Math.cos(angle));
    }

    /**
     * 计算圆心 Y
     *
     * @param radius
     * @param angle
     * @return
     */
    public static int getCircleY(double radius, double angle) {
        return (int) (radius * Math.sin(angle));
    }

    /**
     * 勾股定理计算斜边
     *
     * @param side1
     * @param side2
     * @return
     */
    public static int calcThreeSide(int side1, int side2) {
        return (int) Math.sqrt(Math.pow(side1, 2) + Math.pow(side2, 2));
    }

    /**
     * 弧度转角度
     * @param radians 弧度
     * @return
     */
    public static int radiansToDegrees(double radians) {
        return (int) Math.round(Math.toDegrees(radians));
    }

    /**
     * 角度转弧度
     * @param degrees 角度
     * @return
     */
    public static int degreesToRadians(int degrees) {
        return (int) Math.round(Math.toRadians(degrees));
    }
}