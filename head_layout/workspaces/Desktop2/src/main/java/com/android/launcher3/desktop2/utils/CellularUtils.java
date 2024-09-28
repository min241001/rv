package com.android.launcher3.desktop2.utils;

public class CellularUtils {

    public static Integer[] generateNums(int num) {

        if (num < 10) {
            return generate10(num);
        } else if (num < 30) {
            return generate10To30(num);
        } else {
            return generate30To60(num);
        }
    }

    private static Integer[] generate10(int num) {

        Integer[] nums = null;
        switch (num) {
            case 4:
                nums = new Integer[] { 1, 2, 1 };
                break;
            case 5:
                nums = new Integer[] { 1, 3, 1 };
                break;
            case 6:
                nums = new Integer[] { 1, 4, 1 };
                break;
            case 7:
                nums = new Integer[] { 2, 3, 2 };
                break;
            case 8:
                nums = new Integer[] { 2, 4, 2 };
                break;
            case 9:
                nums = new Integer[] { 1, 2, 3, 2, 1 };
                break;
        }

        return nums;
    }

    private static Integer[] generate10To30(int num) {
        int sum = 9;
        Integer[] result = new Integer[] { 1, 2, 3, 2, 1 };
        int addNum = (num - sum) / 5;
        sum = 0;
        for (int i = 0; i < result.length; i++) {
            result[i] += addNum;
            sum += result[i];
        }

        int remainder = num - sum;

        if (remainder < 3) {
            result[2] += remainder;
        } else if (remainder == 3) {
            result[0]++;
            result[1]++;
            result[2]++;
        } else if (remainder == 4) {
            result[1]++;
            result[2] += 2;
            result[3]++;
        }
        return result;
    }

    private static Integer[] generate30To60(int num) {
        int sum = 16;
        Integer[] result = new Integer[] { 1, 2, 3, 4, 3, 2, 1 };
        int addNum = (num - sum) / result.length;

        sum = 0;
        for (int i = 0; i < result.length; i++) {
            result[i] += addNum;
            sum += result[i];
        }

        int remainder = num - sum;

        if (remainder < 4) {
            result[3] += remainder;
        } else if (remainder == 4) {
            result[0]++;
            result[1]++;
            result[2]++;
            result[3]++;
        } else if (remainder == 5) {
            result[1]++;
            result[2]++;
            result[3]++;
            result[4]++;
            result[5]++;
        } else if (remainder == 6) {
            result[1]++;
            result[2]++;
            result[3] += 2;
            result[4]++;
            result[5]++;
        }
        return result;
    }
}