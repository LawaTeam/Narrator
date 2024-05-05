package me.sakuratao.narrator.spigot.utils;

import lombok.experimental.UtilityClass;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class StringUtil {

    /**
     * 提取字符串中尖括号<>内的内容。
     *
     * @param input 输入的字符串
     * @return 尖括号内的内容，如果没有找到则返回null
     */
    public String getInBrackets(String input) {
        int start = input.indexOf('<');
        int end = input.lastIndexOf('>');

        if (start != -1 && end != -1 && start < end) {
            return input.substring(start + 1, end);
        } else {
            // 如果没有找到匹配的尖括号，则返回空字符串或抛出异常，根据需求调整
            return "";
        }
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

    /**
     * 根据给定的分隔符和忽略的分隔符数组，分割输入字符串，并返回结果数组。
     * 当遇到普通分隔符时，如果没有任何忽略的分隔符处于活动状态（即没有未关闭的忽略分隔符），则进行分割。
     * 活动的忽略分隔符是指已开始但未结束的忽略分隔符对。
     * 当 ignoredDelimiters 为 null 时，则直接输出 delimiter 分割后的内容
     *
     * @param input 待分割的输入字符串。
     * @param delimiter 作为分隔标准的字符。
     * @param ignoredDelimiters 一个分隔符对数组，表示应忽略的分隔符。每个分隔符对由开始和结束分隔符组成。
     * @return 分割后的字符串数组。
     */
    public static String[] splitRespectingIgnoredDelimiters(String input, String delimiter, String[] ignoredDelimiters) {
        ArrayList<String> result = new ArrayList<>(); // 存储分割后的字符串结果
        StringBuilder currentSegment = new StringBuilder(); // 当前正在构建的字符串片段
        Deque<String> openDelimiters = new ArrayDeque<>(); // 存储未结束的忽略分隔符

        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);
            boolean isDelimiter = currentChar == delimiter.charAt(0);

            if (isDelimiter && openDelimiters.isEmpty()) {
                // 如果当前字符是分隔符且没有活动的忽略分隔符，则将当前片段添加到结果中，并重置当前片段
                result.add(currentSegment.toString().trim());
                currentSegment.setLength(0);
            } else {
                currentSegment.append(currentChar); // 将字符添加到当前片段
            }

            if (ignoredDelimiters != null){
                // 处理忽略的分隔符
                for (int j = 0; j < ignoredDelimiters.length; j += 2) {
                    if (ignoredDelimiters[j].charAt(0) == currentChar) {
                        openDelimiters.push(ignoredDelimiters[j]); // 如果当前字符是开始忽略分隔符，则将其入栈
                    } else if (ignoredDelimiters[j + 1].charAt(0) == currentChar && !openDelimiters.isEmpty()
                            && openDelimiters.peek().equals(ignoredDelimiters[j])) {
                        openDelimiters.pop(); // 如果当前字符是结束忽略分隔符且栈非空且与栈顶开始分隔符匹配，则将其出栈
                    }
                }
            } else openDelimiters.pop();

        }

        // 分割完成后，将最后一个片段（如果存在）添加到结果中
        if (!currentSegment.isEmpty()) {
            result.add(currentSegment.toString().trim());
        }

        return result.toArray(new String[0]); // 将结果列表转换为数组并返回
    }


}
