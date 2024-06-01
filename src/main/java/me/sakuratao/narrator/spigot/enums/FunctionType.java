package me.sakuratao.narrator.spigot.enums;

public enum FunctionType {

    MATH("@MATH"),
    CONTAIN("@CONTAIN"),

    PLAYER("@PLAYER"),
    NPC("@NPC"), // todo
    LOC("@LOC"),
    BLOCK("@BLOCK"),
    ITEM_STACK("@ITEM_STACK"),
    ITEM_META("@ITEM_META"),
    INV("@INV")
    ;

    private final String type;

    FunctionType(String type) {
        this.type = type;
    }

}
