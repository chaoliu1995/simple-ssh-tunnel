package org.example;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.util.Properties;

public class SSHConnection {

    public void createSession() throws Exception {

        JSch jsch = new JSch();
        //添加私钥
        jsch.addIdentity();
        Properties sessionConfig = new Properties();
        //SSH 公钥检查机制 no、ask、yes
        sessionConfig.put("StrictHostKeyChecking","no");
        Session session = jsch.getSession("","",22);
        session.setConfig(sessionConfig);
        session.connect();
        //本地端口转发
        //localPort, remoteHost, remotePort
        session.setPortForwardingL();

        //关闭连接
        session.disconnect();
    }

}
