package org.example;

import com.jcraft.jsch.Session;

public class SessionWatcherRunnable implements Runnable {

    private final Session session;
    /**
     * Session重试次数
     */
    private Integer retryCount = 0;
    private Integer maxRetryCount = 3;
    private boolean programIsExit = false;
    /**
     * 是否退出
     */
    private boolean isExit = false;
    public SessionWatcherRunnable(Session session){
        this.session = session;
    }

    @Override
    public void run() {
        Print.info("SessionWatcher run");
        while (!programIsExit){
            if(session.isConnected()){
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {
                    //throw new RuntimeException(e);
                    Print.error("isConnected Sleep Exception");
                    break;
                }
            }else{
                //重试次数+1
                retryCount = retryCount + 1;
                try {
                    session.connect();
                    //连接成功后，重试次数置0
                    retryCount = 0;
                } catch (Exception e) {
                    Print.error("Reconnect Exception：" + e.getMessage());
                    e.printStackTrace();
                    //throw new RuntimeException(e);
                    if(retryCount >= maxRetryCount){
                        break;
                    }
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ex) {
                        Print.error("Reconnect Sleep Exception");
                        //throw new RuntimeException(ex);
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
