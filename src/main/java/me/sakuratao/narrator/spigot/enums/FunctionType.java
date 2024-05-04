package me.sakuratao.narrator.spigot.enums;

public enum FunctionType {

    ENTITY("ENTITY"),
    PLAYER("PLAYER"),
    NPC("NPC"), // todo
    LOC("LOC"),
    BLOCK("BLOCK"),
    ;

    private final String type;

    FunctionType(String type) {
        this.type = type;
    }

}
