package me.sakuratao.narrator.spigot.utils;

import net.sourceforge.jeval.Evaluator;

public class MathUtil {

    public static String calculateExpression(String expression) {
        Evaluator evaluator = new Evaluator();
        try {
            return evaluator.evaluate(expression);
        } catch (Exception e) {
            throw new RuntimeException("无法计算表达式: " + expression, e);
        }
    }

}

