package com.noobug.nooblog.tools.utils;

public class ValidateUtil {

    /**
     * 验证字符串存在空格
     *
     * @param str 字符串
     * @return
     */
    public static Boolean existSpace(String str) {
        return str.contains(" ");
    }

    /**
     * 验证字符串长度区间
     *
     * @param str 字符串
     * @param min 最小值（包括）
     * @param max 最大值（包括）
     * @return
     */
    public static Boolean lengthBetween(String str, int min, int max) {
        int len = str.length();
        return len >= min && len <= max;
    }

    /**
     * 验证纯数字
     *
     * @param str 字符串
     * @return
     */
    public static Boolean allNumber(String str) {
        return str.matches("^\\d+$");
    }

    /**
     * 验证存在汉字
     *
     * @param str 字符串
     * @return
     */
    public static Boolean existChinese(String str) {
        return str.matches("^.*[\u4E00-\u9FA5].*$");
    }

    /**
     * 验证纯中文汉字
     *
     * @param str 字符串
     * @return
     */
    public static Boolean AllChinese(String str) {
        return str.matches("^[\u4E00-\u9FA5]+$");
    }

    /**
     * 验证空字符串
     *
     * @param str 字符串
     * @return
     */
    public static boolean emptyString(String str) {
        return str.equals("") || str.isEmpty();
    }

    /**
     * 验证整数大小范围
     *
     * @param num 整数
     * @param min 最小值（包括）
     * @param max 最大值（包括）
     * @return
     */
    public static boolean integerRange(Integer num, int min, int max) {
        return num >= min && num < max;
    }

    /**
     * 验证手机号格式
     *
     * @param phone 字符串
     * @return
     */
    public static boolean phoneNumber(String phone) {
        return phone.matches("^1([34578])\\d{9}$");
    }
}
