package me.sakuratao.narrator.spigot.enums;

public enum FunctionType {

    ENTITY("ENTITY"), // fixme 预计删除
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
