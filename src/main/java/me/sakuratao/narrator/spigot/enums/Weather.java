package me.sakuratao.narrator.spigot.enums;

import lombok.Getter;

@Getter
public enum Weather {

    RAINING("RAINING"),
    THUNDER("THUNDER"),
    CLEAR("CLEAR")
    ;

    private final String weather;

    Weather(String weather){
        this.weather = weather;
    }

}
