package me.sakuratao.narrator.spigot.enums;

public enum Weather {

    SUNSHINE("SUNSHINE"),
    RAINING("RAINING"),
    THUNDER("THUNDER"),
    CLEAR("CLEAR")
    ;

    private final String weather;

    Weather(String weather){
        this.weather = weather;
    }

    public String getWeather() {
        return weather;
    }
}
