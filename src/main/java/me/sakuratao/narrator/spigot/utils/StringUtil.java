package me.sakuratao.narrator.spigot.utils;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class StringUtil {

    /**
     * 提取字符串中尖括号<>内的内容。
     * 注意：此方法只提取第一个匹配项。
     *
     * @param text 输入的字符串
     * @return 尖括号内的内容，如果没有找到则返回null
     */
    public String getInBrackets(String text) {
        Pattern pattern = Pattern.compile("<(.*?)>");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1);  // group(1) 获取第一个括号内匹配的内容
        }
        return null;
    }

    /**
     * 判断字符串是否全为数字
     * @param str 待检查的字符串
     * @return 如果字符串全为数字则返回true，否则返回false
     */
    public static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        for (char c : str.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }
}
