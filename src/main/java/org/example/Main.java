package org.example;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

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
        //添加私钥
        jsch.addIdentity(config.getPrivateKeyPath(),config.getPassphrase());
        Properties sessionConfig = new Properties();
        //SSH 公钥检查机制 no、ask、yes
        sessionConfig.put("StrictHostKeyChecking",config.getStrictHostKeyChecking());
        Session session = null;
        try{
            Print.info("get session");
            session = jsch.getSession(config.getServerUser(),config.getServerHost(),config.getServerPort());
            session.setConfig(sessionConfig);
            Print.info("connect...");
            session.connect();
            //本地端口转发
            //localPort, remoteHost, remotePort
            Print.info("port forwarding...");
            session.setPortForwardingL(config.getLocalPort(),config.getRemoteHost(),config.getRemotePort());
            doShutDownWork(session);
            Print.info("running...");
        }catch (Exception e){
            e.printStackTrace();
            if(session != null){
                Print.info("disconnect...");
                session.disconnect();
                Print.info("disconnect end");
            }
        }
    }

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
        return config;
    }

    private static void doShutDownWork(final Session session) {
        //当前 Java 应用程序相关的运行时对象
        Runtime run=Runtime.getRuntime();
        //注册新的虚拟机来关闭钩子
        run.addShutdownHook(new Thread(() -> {
            //程序结束时进行的操作
            Print.info("disconnect...");
            session.disconnect();
            Print.info("disconnect end");
        }));
    }
}