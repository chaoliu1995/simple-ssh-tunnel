package org.example;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class SessionWatcher implements Runnable {

    private Session session;
    /**
     * Session重连次数
     */
    private Integer retryCount = 0;
    /**
     * 最大重连次数
     */
    private Integer maxRetryCount = 3;
    /**
     * 主程序是否退出
     */
    private boolean programIsExit = false;
    /**
     * SessionWatcher是否退出
     */
    private boolean isExit = false;

    private final JSch jsch;
    private final Config config;
    public SessionWatcher(Session session, JSch jsch, Config config){
        this.session = session;
        this.jsch = jsch;
        this.config = config;
    }

    @Override
    public void run() {
        Print.info("SessionWatcher Run");
        while (!programIsExit){
            if(session.isConnected()){
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {
                    Print.error("isConnected Sleep Exception");
                    break;
                }
            }else{
                //重试次数+1
                retryCount = retryCount + 1;
                try {
                    SessionUtils.disconnect(session);
                    session = SessionUtils.getSession(this.jsch, this.config);
                    Print.info("Connect");
                    session.connect();
                    Print.info("Port Forwarding Local");
                    session.setPortForwardingL(config.getLocalPort(),config.getRemoteHost(),config.getRemotePort());
                    Print.info("Reset RetryCount");
                    //连接成功后，重试次数置0
                    retryCount = 0;
                } catch (Exception e) {
                    Print.error("Reconnect Exception：" + e.getMessage());
                    e.printStackTrace();
                    SessionUtils.disconnect(session);
                    if(retryCount >= maxRetryCount){
                        break;
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ex) {
                        Print.error("Reconnect Sleep Exception");
                        break;
                    }
                }
            }
        }
        this.isExit = true;
        Print.info("SessionWatcher Exit");
    }

    public void setMaxRetryCount(Integer maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    public void setProgramIsExit(boolean programIsExit) {
        this.programIsExit = programIsExit;
    }

    public boolean isExit() {
        return isExit;
    }
}
