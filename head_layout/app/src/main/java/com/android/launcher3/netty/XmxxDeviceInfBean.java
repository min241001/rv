package com.android.launcher3.netty;

import com.android.launcher3.App;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.NetworkUtils;
import com.android.launcher3.moudle.launcher.util.WatchAccountUtils;
import com.baehug.lib.nettyclient.interfaces.NettyParameter;

public class XmxxDeviceInfBean implements NettyParameter {

    private int heatBeatTime = 300;

    /**
     * 设备id
     */
    private String waAcctId;
    private String partnerAbbr;
    private String nettyService;
    private int nettyPort;

    /**
     * 若修改该值，需要重启netty才生效
     *
     * @param nettyService 服务地址
     * @param nettyPort    端口号
     * @param partnerAbbr  协议前缀
     * @param heatBeatTime 心跳时间
     */
    public XmxxDeviceInfBean(String nettyService, int nettyPort, String partnerAbbr, int heatBeatTime) {
        this.nettyService = nettyService;
        this.nettyPort = nettyPort;
        this.partnerAbbr = partnerAbbr;
        this.heatBeatTime = heatBeatTime;
    }

    @Override
    public String getNettyService() {
        return nettyService;
    }

    @Override
    public int getNettyPort() {
        return nettyPort;
    }

    @Override
    public String getWaAcctId() {
        waAcctId = WatchAccountUtils.getInstance().getWaAcctId();
        return waAcctId;
    }

    @Override
    public String getPartnerAbbr() {
        return partnerAbbr;
    }

    @Override
    public boolean isNetworkAvailable() {
        boolean result = NetworkUtils.isNetworkAvailable(App.getInstance());
        if (result) {
            LogUtil.d("netty" + "网络是通的", LogUtil.TYPE_RELEASE);
        } else {
            LogUtil.e("netty" + "网络不通");
        }
        return result;
    }

    @Override
    public int getHeatBeatTime() {

        return heatBeatTime;
    }

    public void setHeatBeatTime(int heatBeatTime) {
        this.heatBeatTime = heatBeatTime;
    }

}
