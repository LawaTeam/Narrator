package me.sakuratao.narrator.spigot.utils;

import net.sourceforge.jeval.Evaluator;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class MathUtil {

    // 将ScriptEngine作为静态变量复用，减少性能开销
    private static final ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");

    public static String calculateExpression(String expression) {
        Evaluator evaluator = new Evaluator();
        try {
            return evaluator.evaluate(expression);
        } catch (Exception e) {
            throw new RuntimeException("无法计算表达式: " + expression, e);
        }
    }

}

