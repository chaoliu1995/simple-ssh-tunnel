package org.example;

public enum LoggerLevelEnum {
    DEBUG(0,"DEBUG"),
    INFO(1,"INFO"),
    WARN(2,"WARN"),
    ERROR(3,"ERROR"),
    FATAL(4,"FATAL");
    private int level;
    private String name;

    LoggerLevelEnum(int level, String name){
        this.level = level;
        this.name = name;
    }

    public static LoggerLevelEnum getByLevel(int level){
        for(LoggerLevelEnum item : values()){
            if(item.level == level){
                return item;
            }
        }
        return null;
    }

    public int getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }
}
