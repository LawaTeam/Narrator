package me.sakuratao.narrator.spigot.handlers;

import me.sakuratao.narrator.spigot.data.player.PlayerData;
import me.sakuratao.narrator.spigot.manager.ManagerHandler;
import me.sakuratao.narrator.spigot.manager.PlayerManager;
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
    @Wire private DebugHandler debugHandler;
    @Wire private ManagerHandler managerHandler;

    /**
     * 处理玩家传入的内容，根据一系列条件判断是否执行特定操作。
     *
     * @param player 玩家对象，代表执行操作的玩家。
     * @param content 需要处理的字符串内容，包含了条件和要执行的操作。
     */
    public boolean handle(Player player, String content) {

        // 如果内容为空，则直接返回不做处理
        if (content == null || content.isEmpty()) {
            return false;
        }
        List<String> debugDetails = new ArrayList<>();

        // 使用指定的分隔符拆分字符串，忽略特定的 delimiter
        String[] tempParts = StringUtil.splitRespectingIgnoredDelimiters(content, ",", new String[]{"{", "}"});
        // 如果分割后的内容不包含至少两个元素，则返回
        if (tempParts.length < 2) return false;

        int THE_LAST_ELEMENT = tempParts.length - 1;
        // 将最后一个元素根据 "::" 进行再次分割，用于存储最终需要执行的操作
        List<String> executeContents = new ArrayList<>(
                Arrays.asList(tempParts[THE_LAST_ELEMENT].split("::"))
        );
        // 如果执行内容为空，则返回
        if (executeContents.isEmpty()) return false;

        // 存储每个条件的处理结果
        List<Boolean> results = new ArrayList<>();
        for (int i = 0; i < THE_LAST_ELEMENT; i++) {
            // 分割条件字符串，支持逻辑运算符
            String[] splitByLogic = splitLogic(tempParts[i].trim());
            if (splitByLogic != null) {
                // 分别处理两个条件，并根据逻辑运算符计算结果
                boolean a = handleCondition(splitByLogic[0].trim());
                boolean b = handleCondition(splitByLogic[1].trim());
                boolean tempResult = handleLogic(parseLogic(tempParts[i].trim()), a, b);
                results.add(tempResult);

                debugDetails.add("&7condition&8(&7" + parseLogic(tempParts[i]) + "&8): &f" + tempParts[i]);
                debugDetails.add("&7    |-result: " + (tempResult ? "&atrue" : "&cfalse"));

                continue;
            }
            // 处理不包含逻辑运算符的简单条件
            boolean tempResult = handleCondition(tempParts[i].trim());
            results.add(tempResult);
            debugDetails.add("&7condition: &f" + tempParts[i]);
            debugDetails.add("&7    |-result: " + (tempResult ? "&atrue" : "&cfalse"));
        }

        // 如果没有条件需要处理，则返回
        if (results.isEmpty()) return false;


        boolean finalResult = true;
        for (boolean result : results) {
            if (!result) {
                finalResult = false;
                break;
            }
        }

        debugDetails.add("&7finalResult: " + (finalResult ? "&atrue" : "&cfalse"));
        for (String executeContent : executeContents) {
            debugDetails.add("&7executeContent: &f" + executeContent);
        }

        PlayerData playerData = managerHandler.getPlayerManager().getByPlayer(player);
        debugHandler.debug(
                player,
                playerData.getPlayingChapterData(),
                playerData.getPlayingTaskData(),
                "Condition/C",
                debugDetails,
                false
        );

        // 检查所有条件是否满足，如果有不满足的，则返回
        if (!finalResult) return false;

        // 执行所有需要执行的操作
        for (String executeContent : executeContents) {
            executeContent(player, executeContent);
            return true;
        }

        return false;
    }

    private boolean handleCondition(String condition){
        // 解析条件
        String[] splitByCondition = splitCondition(condition);
        if (splitByCondition == null) return false;
        if (splitByCondition.length < 1) {
            return false;
        }

        List<Object> processedObjects = new ArrayList<>();
        for (String part : splitByCondition) {
            Object processedObject = processPart(part.trim());
            if (processedObject != null) {
                processedObjects.add(processedObject);
            }
        }

        String conditionType = parseCondition(condition);
        if (conditionType == null) return false;
        switch (conditionType) {
            case "==":
                // 执行逻辑判断和操作
                if (processedObjects.size() >= 2) {
                    return processedObjects.get(0).equals(processedObjects.get(1));
                }
                return false;
            case "!=":
                // 执行逻辑判断和操作
                if (processedObjects.size() >= 2) {
                    return !processedObjects.get(0).equals(processedObjects.get(1));
                }
                return false;
            case "<=":
                for (Object object : processedObjects) {
                    if (!StringUtil.isNumeric(String.valueOf(object))) return false;
                }
                if (processedObjects.size() >= 2) {
                    return Double.parseDouble(String.valueOf(processedObjects.get(0))) <= Double.parseDouble(String.valueOf(processedObjects.get(1)));
                }
                return false;
            case ">=":
                for (Object object : processedObjects) {
                    if (!StringUtil.isNumeric(String.valueOf(object))) return false;
                }
                if (processedObjects.size() >= 2) {
                    return Double.parseDouble(String.valueOf(processedObjects.get(0))) >= Double.parseDouble(String.valueOf(processedObjects.get(1)));
                }
                return false;
            case "<":
                for (Object object : processedObjects) {
                    if (!StringUtil.isNumeric(String.valueOf(object))) return false;
                }
                if (processedObjects.size() >= 2) {
                    return Double.parseDouble(String.valueOf(processedObjects.get(0))) < Double.parseDouble(String.valueOf(processedObjects.get(1)));
                }
                return false;
            case ">":
                for (Object object : processedObjects) {
                    if (!StringUtil.isNumeric(String.valueOf(object))) return true;
                }
                if (processedObjects.size() >= 2) {
                    return Double.parseDouble(String.valueOf(processedObjects.get(0))) > Double.parseDouble(String.valueOf(processedObjects.get(1)));
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
     * 处理部分数据，如果 functionHandler 可以解析则返回解析后的数据
     * 否则返回原字符串。
     * @param part 待处理的字符串部分。
     * @return 处理结果，如果都无法处理则返回 null。
     */
    private Object processPart(String part) {
        // 验证输入，确保不处理空或只含空白字符的输入
        if (part == null || part.trim().isEmpty()) {
            return null;
        }

        Object result = functionHandler.handleType(part.trim());
        if (result != null) {
            return result;
        }

        // 如果处理失败，返回 字符串
        return part;
    }

    private void executeContent(Player player, String content) {
        PlayerData playerData = playerManager.getByPlayer(player);
        contentHandler.handleContent(player, playerData.getPlayingChapterData(), content, playerData.getContentTask());
    }


}
