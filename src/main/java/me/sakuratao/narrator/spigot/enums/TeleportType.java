package me.sakuratao.narrator.spigot.enums;

public enum TeleportType {

    ENTITY("ENTITY"),
    PLAYER("PLAYER"),
    NPC("NPC"), // todo
    LOC("LOC")
    ;

    private final String type;

    TeleportType(String type) {
        this.type = type;
    }

}
