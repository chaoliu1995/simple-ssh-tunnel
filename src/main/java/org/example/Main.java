package org.example;

import com.jcraft.jsch.JSch;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Main {

    public static void main(String[] args) throws Exception {
        if(args.length < 1){
            Print.info("请输入配置文件路径");
            System.exit(1);
        }
        Config config = readConfig(args[0]);
        JSch jsch = new JSch();
        JSch.setLogger(new SimpleLogger(config.getLogLevel()));
        //添加私钥
        jsch.addIdentity(config.getPrivateKeyPath(),config.getPassphrase());

        try{
            SessionWatcher sessionWatcher = new SessionWatcher(jsch, config);
            if(config.getMaxRetryCount() != null && config.getMaxRetryCount() > 0){
                sessionWatcher.setMaxRetryCount(config.getMaxRetryCount());
            }
            Print.info("Start SessionWatcher");
            new Thread(sessionWatcher,"SessionWatcher").start();
            doShutDownWork(sessionWatcher);
            Print.info("running...");
        }catch (Exception e){
            Print.error(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 读取配置文件
     * @param path
     * @return
     * @throws IOException
     */
    private static Config readConfig(String path) throws IOException {
        Properties properties = new Properties();
        FileInputStream fis = new FileInputStream(path);
        properties.load(fis);
        Config config = new Config();
        config.setStrictHostKeyChecking(properties.getProperty("strictHostKeyChecking"));
        config.setPrivateKeyPath(properties.getProperty("privateKeyPath"));
        config.setPassphrase(properties.getProperty("passphrase"));
        config.setServerUser(properties.getProperty("serverUser"));
        config.setServerHost(properties.getProperty("serverHost"));
        config.setServerPort(Integer.valueOf(properties.getProperty("serverPort")));
        config.setLocalPort(Integer.valueOf(properties.getProperty("localPort")));
        config.setRemoteHost(properties.getProperty("remoteHost"));
        config.setRemotePort(Integer.valueOf(properties.getProperty("remotePort")));
        config.setLogLevel(Integer.valueOf(properties.getProperty("logLevel")));
        config.setMaxRetryCount(Integer.valueOf(properties.getProperty("maxRetryCount")));
        return config;
    }

    /**
     * 程序退出时清理资源 1.退出用于重连的线程 2.断开Session连接
     * @param sessionWatcher
     */
    private static void doShutDownWork(final SessionWatcher sessionWatcher) {
        //当前 Java 应用程序相关的运行时对象
        Runtime run = Runtime.getRuntime();
        //注册新的虚拟机来关闭钩子
        run.addShutdownHook(new Thread(() -> {
            //程序结束时进行的操作
            Print.info("Program Exit");
            Print.info("SessionWatcher Set isExit=true");
            sessionWatcher.setProgramIsExit(true);
            for(int i = 0; i < 5; i++){
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {
                    Print.error(e.getMessage());
                }
                if(sessionWatcher.isExit()){
                    break;
                }
            }
            Print.info("ShutdownHook Exit");
        }));
    }
}