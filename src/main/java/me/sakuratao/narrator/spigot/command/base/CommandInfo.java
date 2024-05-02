package me.sakuratao.narrator.spigot.command.base;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author SakuraTao
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandInfo {

    String name(); // 指令名

    String description(); // 介绍

    String permission(); // 权限

    boolean canConsoleUse(); // 能否后台使用

    String syntax(); // 语法

}
