package com.android.launcher3.desktop4.cellular.utils;

import java.util.Arrays;

public class Utils {

    public static int[] generateNums(int num, int type) {
        // type 1 - 棋盘风格  2 - 蜂窝风格  3 - 三四三四风格
        if (type == 1) {
            int lowestSquare = (int) Math.sqrt(num);
            int residue = num - lowestSquare * lowestSquare;
            int[] nums;
            if (residue > 0) {
                nums = new int[lowestSquare + 1];
                if (residue <= lowestSquare + 2) {
                    nums[0] = residue;
                    Arrays.fill(nums, 1, lowestSquare + 1, lowestSquare);
                } else {
                    nums[0] = lowestSquare + 2;
                    int surplus = residue - (lowestSquare + 2);
                    if (surplus > 0) {
                        int result = surplus / 2;
                        if (result > 0) {
                            Arrays.fill(nums, 1, result + 1, lowestSquare + 2);
                            int remainder = surplus - 2 * result;
                            nums[result + 1] = lowestSquare + remainder;
                        } else {
                            nums[1] = lowestSquare + surplus;
                        }
                        Arrays.fill(nums, result + 2, lowestSquare + 1, lowestSquare);
                    }
                }
            } else {
                nums = new int[lowestSquare];
                Arrays.fill(nums, lowestSquare);
            }
            return nums;
        } else if (type == 3) {
            int[] nums;
            int result = num / 7;
            int remind = num - result * 7;
            if (remind > 3) {
                nums = new int[result * 2 + 2];
            } else if (remind > 0 && remind <= 3) {
                nums = new int[result * 2 + 1];
            } else {
                nums = new int[result * 2];
            }
            for (int i = 0; i < result * 2; i++) {
                if (i % 2 == 0) {
                    nums[i] = 3;
                } else {
                    nums[i] = 4;
                }
            }
            if (remind > 3) {
                nums[result * 2] = 3;
                nums[result * 2 + 1] = remind - 3;
            } else if (remind > 0 && remind <= 3) {
                nums[result * 2] = remind;
            }
            return nums;
        } else {
            if (num < 10) {
                return generate10(num);
            } else if (num < 30) {
                return generate10To30(num);
            } else {
                return generate30To60(num);
            }
        }
        /**
         * 注释的代码别删！！！
         */
//        int[] nums = null;
//        //最大平方数
//        int lowestSquare = (int) Math.sqrt(num);
//        //余数
//        int residue = num - lowestSquare * lowestSquare;
//        if (residue > 0) {
//            nums = new int[lowestSquare + 1];
//            if (residue <= lowestSquare + 2) {
//                nums[0] = residue;
//                for (int i = 1; i <= lowestSquare; i++) {
//                    nums[i] = lowestSquare;
//                }
//            } else {
//                nums[0] = lowestSquare + 2;
//                int surplus = residue - (lowestSquare + 2);
//                if (surplus > 0) {
//                    int result = surplus / 2;
//                    if (result > 0) {
//                        for (int i = 1; i <= result; i++) {
//                            nums[i] = lowestSquare + 2;
//                        }
//                        int remainder = surplus - 2 * result;
//                        nums[result + 1] = lowestSquare + remainder;
//                        for (int i = result + 2; i <= lowestSquare; i++) {
//                            nums[i] = lowestSquare;
//                        }
//                    } else {
//                        nums[1] = lowestSquare + surplus;
//                        for (int i = 2; i <= lowestSquare; i++) {
//                            nums[i] = lowestSquare;
//                        }
//                    }
//                }
//            }
//        } else {
//            nums = new int[lowestSquare];
//            for (int i = 0; i < lowestSquare; i++) {
//                nums[i] = lowestSquare;
//            }
//        }
    }

    private static int[] generate10(int num) {

        int[] nums = null;
        switch (num) {
            case 4:
                nums = new int[]{1, 2, 1};
                break;
            case 5:
                nums = new int[]{1, 3, 1};
                break;
            case 6:
                nums = new int[]{1, 4, 1};
                break;
            case 7:
                nums = new int[]{2, 3, 2};
                break;
            case 8:
                nums = new int[]{2, 4, 2};
                break;
            case 9:
                nums = new int[]{1, 2, 3, 2, 1};
                break;
        }
        return nums;
    }

    private static int[] generate10To30(int num) {
        int sum = 9;
        int[] result = new int[]{1, 2, 3, 2, 1};
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

    private static int[] generate30To60(int num) {
        int sum = 16;
        int[] result = new int[]{1, 2, 3, 4, 3, 2, 1};
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
