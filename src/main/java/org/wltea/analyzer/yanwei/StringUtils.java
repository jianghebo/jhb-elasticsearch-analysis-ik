package org.wltea.analyzer.yanwei;

/**
 * @author jhb
 *         Date: 2019/4/9
 *         Time:11:55
 */
public class StringUtils {
    /**
     * 判断字符串是否为空 为空返回true 否则返回false
     *
     * @param str
     * @return
     */
    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断字符串是否不为空 为空返回false 否则返回true
     *
     * @param str
     * @return
     */
    public static boolean isNotBlank(String str) {
        return !StringUtils.isBlank(str);
    }
}
