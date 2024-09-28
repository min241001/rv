package com.android.launcher3.netty.bean;

/**
 * @Author: jamesfeng
 * @Description：
 * @CreateDate：2024/2/7 14:28
 * @UpdateUser: jamesfeng
 */
public class TimeBean {

    /**
     * 发送时间
     */
    private Long s1SendTime;
    /**
     * 服务器时间
     */
    private Long s2ServiceTime;

    public Long getS1SendTime() {
        return s1SendTime;
    }

    public void setS1SendTime(Long s1SendTime) {
        this.s1SendTime = s1SendTime;
    }

    public Long getS2ServiceTime() {
        return s2ServiceTime;
    }

    public void setS2ServiceTime(Long s2ServiceTime) {
        this.s2ServiceTime = s2ServiceTime;
    }
}
