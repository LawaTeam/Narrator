package me.sakuratao.narrator.spigot.handlers;

import me.sakuratao.narrator.spigot.data.player.PlayerData;
import me.sakuratao.narrator.spigot.manager.PlayerManager;
import me.sakuratao.narrator.spigot.utils.MathUtil;
import me.sakuratao.narrator.spigot.utils.StringUtil;
import org.bukkit.entity.Player;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Wire;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@PieComponent
public class ConditionHandler {

    @Wire private FunctionHandler functionHandler;
    @Wire private PlayerManager playerManager;
    @Wire private ContentHandler contentHandler;

    public void handle(Player player, String content) {

        if (content == null || content.isEmpty()) {
            return;
        }

        String[] tempParts = content.split(",(?=(?:[^<>]*<[^<>]*>)*[^<>]*$)");
        // 确保 tempParts 至少有两个元素
        if (tempParts.length < 2) return;

        int THE_LAST_ELEMENT = tempParts.length - 1;
        List<String> executeContents = new ArrayList<>(
                Arrays.asList(tempParts[THE_LAST_ELEMENT].split("::"))
        );
        if (executeContents.isEmpty()) return;

        List<Boolean> results = new ArrayList<>();
        for (int i = 0; i < THE_LAST_ELEMENT; i++) {
            String[] splitByLogic = splitLogic(tempParts[i]);
            if (splitByLogic != null) {
                boolean a = handle(player, content, splitByLogic[0]);
                boolean b = handle(player, content, splitByLogic[1]);
                results.add(handleLogic(parseLogic(tempParts[i]), a, b));
                continue;
            }
            results.add(handle(player, content, tempParts[i]));
        }

        if (results.isEmpty()) return;

        for (boolean result : results) {
            if (!result) return;
        }
        for (String executeContent : executeContents) {
            executeContent(player, executeContent);
        }
    }

    private boolean handle(Player player, String content, String condition){
        // 解析条件
        String[] splitByCondition = splitCondition(condition);
        if (splitByCondition == null) return false;
        if (splitByCondition.length < 1) {
            return false;
        }

        List<Object> evaluatedObjects = new ArrayList<>();
        for (String part : splitByCondition) {
            Object evaluatedObject = processPart(part);
            if (evaluatedObject != null) {
                evaluatedObjects.add(evaluatedObject);
            }
        }

        String conditionType = parseCondition(condition);
        if (conditionType == null) return false;
        switch (conditionType) {
            case "==":
                // 执行逻辑判断和操作
                if (evaluatedObjects.size() >= 2) {
                    return evaluatedObjects.get(0).equals(evaluatedObjects.get(1));
                }
                return false;
            case "!=":
                // 执行逻辑判断和操作
                if (evaluatedObjects.size() >= 2) {
                    return !evaluatedObjects.get(0).equals(evaluatedObjects.get(1));
                }
                return false;
            case "<=":
                for (Object object : evaluatedObjects) {
                    if (!StringUtil.isNumeric(String.valueOf(object))) return false;
                }
                if (evaluatedObjects.size() >= 2) {
                    return Double.parseDouble(String.valueOf(evaluatedObjects.get(0))) <= Double.parseDouble(String.valueOf(evaluatedObjects.get(1)));
                }
                return false;
            case ">=":
                for (Object object : evaluatedObjects) {
                    if (!StringUtil.isNumeric(String.valueOf(object))) return false;
                }
                if (evaluatedObjects.size() >= 2) {
                    return Double.parseDouble(String.valueOf(evaluatedObjects.get(0))) >= Double.parseDouble(String.valueOf(evaluatedObjects.get(1)));
                }
                return false;
            case "<":
                for (Object object : evaluatedObjects) {
                    if (!StringUtil.isNumeric(String.valueOf(object))) return false;
                }
                if (evaluatedObjects.size() >= 2) {
                    return Double.parseDouble(String.valueOf(evaluatedObjects.get(0))) < Double.parseDouble(String.valueOf(evaluatedObjects.get(1)));
                }
                return false;
            case ">":
                for (Object object : evaluatedObjects) {
                    if (!StringUtil.isNumeric(String.valueOf(object))) return true;
                }
                if (evaluatedObjects.size() >= 2) {
                    return Double.parseDouble(String.valueOf(evaluatedObjects.get(0))) > Double.parseDouble(String.valueOf(evaluatedObjects.get(1)));
                }
                return false;
        }
        return false;

    }

    private boolean handleLogic(String logic, boolean a, boolean b){
        if (logic == null) return  false;
        return switch (logic) {
            case "&" -> a & b;
            case "|" -> a | b;
            default -> false;
        };
    }

    private String parseLogic(String part) {
        if (part.contains("&")) {
            return "&";
        }
        if (part.contains("|")) {
            return "|";
        }
        return null;
    }

    private String[] splitLogic(String part) {
        if (part.contains("&")) {
            return part.split("&");
        }
        if (part.contains("|")) {
            return part.split("\\|");
        }
        return null;
    }

    private String parseCondition(String part) {
        if (part.contains("==")) {
            return "==";
        }
        if (part.contains("!=")) {
            return "!=";
        }
        if (part.contains("<=")) {
            return "<=";
        }
        if (part.contains(">=")) {
            return ">=";
        }
        if (part.contains("<")) {
            return "<";
        }
        if (part.contains(">")) {
            return ">";
        }
        return null; // 表示没有识别的条件
    }

    private String[] splitCondition(String part) {
        if (part.contains("==")) {
            return part.split("==");
        }
        if (part.contains("!=")) {
            return part.split("!=");
        }
        if (part.contains("<=")) {
            return part.split("<=");
        }
        if (part.contains(">=")) {
            return part.split(">=");
        }
        if (part.contains("<")) {
            return part.split("<");
        }
        if (part.contains(">")) {
            return part.split(">");
        }
        return null; // 表示没有识别的条件
    }

    /**
     * 处理部分数据，首先尝试使用functionHandler处理，如果失败则尝试评估数学表达式。
     * @param part 待处理的字符串部分。
     * @return 处理结果，如果都无法处理则返回null。
     */
    private Object processPart(String part) {
        // 验证输入，确保不处理空或只含空白字符的输入
        if (part == null || part.trim().isEmpty()) {
            return null;
        }

        try {
            // 尝试使用functionHandler处理
            Object result = functionHandler.handleType(part.trim());
            if (result != null) {
                return result;
            }
        } catch (Exception ignored) {
        }

        // 尝试评估数学表达式
        try {
            return MathUtil.calculateExpression(part.trim());
        } catch (Exception ignored) {
        }

        // 如果都处理失败，返回 字符串
        return part;
    }

    private void executeContent(Player player, String content) {
        PlayerData playerData = playerManager.getByPlayer(player);
        contentHandler.execute(player, playerData.getPlayingChapterData(), content, playerData.getContentTask());
    }


}
