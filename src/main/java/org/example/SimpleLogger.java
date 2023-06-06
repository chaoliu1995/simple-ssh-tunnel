package org.example;

import com.jcraft.jsch.Logger;

public class SimpleLogger implements Logger {

    public SimpleLogger(int level){
        this.level = level;
    }

    private int level;

    @Override
    public boolean isEnabled(int i) {
        return i == level;
    }

    @Override
    public void log(int i, String s) {
        Print.info(LoggerLevelEnum.getByLevel(i).getName() + " : " + s);
    }
}
