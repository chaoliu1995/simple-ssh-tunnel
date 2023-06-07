package org.example;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.util.Properties;

public class SessionUtils {

    private SessionUtils(){}

    public static Session getSession(JSch jsch, Config config) throws JSchException {
        Properties sessionConfig = new Properties();
        //SSH 公钥检查机制 no、ask、yes
        sessionConfig.put("StrictHostKeyChecking",config.getStrictHostKeyChecking());
        Session session = jsch.getSession(config.getServerUser(),config.getServerHost(),config.getServerPort());
        session.setConfig(sessionConfig);
        return session;
    }

    public static void disconnect(Session session){
        if(session != null && session.isConnected()){
            session.disconnect();
        }
    }
}
