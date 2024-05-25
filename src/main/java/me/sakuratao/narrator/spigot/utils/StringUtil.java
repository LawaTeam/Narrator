package me.sakuratao.narrator.spigot.utils;

import lombok.experimental.UtilityClass;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class StringUtil {

    /**
     * 从输入字符串中提取括号内的内容。
     * 该方法假设输入字符串包含一对匹配的花括号（{}），并返回括号内的文本。
     * 如果找不到匹配的括号，或找到的括号不是正确的一对，将返回空字符串。
     *
     * @param input 输入字符串，期望包含一对匹配的花括号。
     * @return 括号内的文本，如果不存在合适的括号则返回空字符串。
     */
    public String getInBrackets(String input) {
        // 查找输入字符串中第一个花括号的位置
        int start = input.indexOf("{");
        // 查找输入字符串中最后一个花括号的位置
        int end = input.lastIndexOf("}");

        // 确保找到了一对匹配的花括号，并且它们的顺序是正确的
        if (start != -1 && end != -1 && start < end) {
            // 返回括号内的文本
            return input.substring(start + 1, end);
        } else {
            // 如果没有找到合适的一对括号，返回空字符串
            return "";
        }
    }


    /**
     * 从输入字符串中提取括号内的内容。
     * @param input 输入的字符串
     * @param startWith 指定的起始括号或标识
     * @param endWith 指定的结束括号或标识
     * @return 在输入字符串中找到的，由起始和结束标识括起来的字符串。如果找不到或不匹配，则返回空字符串。
     */
    public String getInBrackets(String input, String startWith, String endWith) {
        // 查找起始和结束标识的位置
        int start = input.indexOf(startWith);
        int end = input.lastIndexOf(endWith);

        // 确保找到了匹配的起始和结束标识，并且起始标识在结束标识的前面
        if (start != -1 && end != -1 && start < end) {
            // 提取并返回括号内的字符串
            return input.substring(start + 1, end);
        } else {
            // 如果没有找到匹配的标识或匹配不正确，返回空字符串
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
