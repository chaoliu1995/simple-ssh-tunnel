package org.example;

import com.jcraft.jsch.*;

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
    public SessionWatcher(JSch jsch, Config config) throws JSchException {
        this.jsch = jsch;
        this.config = config;
        this.session = SessionUtils.getSession(jsch, config);
        Print.info("Connect");
        session.connect();
        Print.info("Port Forwarding Local");
        //本地端口转发
        session.setPortForwardingL(config.getLocalPort(),config.getRemoteHost(),config.getRemotePort());
    }

    @Override
    public void run() {
        Print.info("SessionWatcher Run");
        while (!programIsExit){
            if(session.isConnected() && isConnected()){
                try {
                    Thread.sleep(10000);
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
        SessionUtils.disconnect(session);
        Print.info("SessionWatcher Exit");
        this.isExit = true;
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

    /**
     * 执行一个简单命令，测试连接是否中断
     * @return
     */
    private boolean isConnected(){
        ChannelExec channelExec = null;
        try{
            channelExec = (ChannelExec)session.openChannel("exec");
            channelExec.setCommand("ls ~/");
            channelExec.connect();
            if(channelExec.isConnected()){
                channelExec.disconnect();
            }
            return true;
        }catch (Exception e){
            Print.error("channelExec error: " + e.getMessage());
            if(channelExec != null && channelExec.isConnected()){
                channelExec.disconnect();
            }
            return false;
        }
    }
}
