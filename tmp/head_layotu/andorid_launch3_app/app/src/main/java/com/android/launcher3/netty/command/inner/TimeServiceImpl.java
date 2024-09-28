package com.android.launcher3.netty.command.inner;

import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.netty.bean.TimeBean;
import com.android.launcher3.netty.constant.XmxxCmdConstant;
import com.baehug.lib.nettyclient.NettyTcpClient;
import com.baehug.lib.nettyclient.command.BaseCmdServiceImpl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @Author: jamesfeng
 * @Description：
 * @CreateDate：2024/2/7 14:10
 * @UpdateUser: jamesfeng
 */
public class TimeServiceImpl extends BaseCmdServiceImpl {
    private static final String CMD_NAME = "同步系统时间";
    private static final String TAG = BaseCmdServiceImpl.class.getSimpleName() + "--->>>" + CMD_NAME + "--->>>";
    /**
     * 上次发送同步服务时间的时间
     */
    private Long lastSendSyncTime = -1L;
    /**
     * 上次成功同步服务时间的时间
     */
    private Long lastSyncSuccessTime = -1L;

    @Override
    public String getCommandType() {
        return XmxxCmdConstant.TIME;
    }

    @Override
    public void receiveMsg(NettyTcpClient tcpClient, String msg) {
        LogUtil.d(TAG + "收到netty服务器下发的指令：" + msg, LogUtil.TYPE_RELEASE);
        try {
            TimeBean timeBean = (TimeBean) msgContentToModel(msg, TimeBean.class, false);
            if (timeBean != null) {
                // 若当前时间减去发送时间小于0，说明已调整过了或数据有误
                if (System.currentTimeMillis() - timeBean.getS1SendTime() > 0) {
                    Date date = new Date(timeBean.getS2ServiceTime() + (System.currentTimeMillis() - timeBean.getS1SendTime()) - 100000000L);
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    String dateTimeString = dateFormat.format(date);
                    // 设置系统时间
                    // TODO: 待调用系统方法实现
                    //Settings.System.putString(App.getInstance().getContentResolver(), Settings.System.DATE_FORMAT, dateTimeString);
                    // 成功时间
                    lastSyncSuccessTime = System.currentTimeMillis();
                }


            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Boolean sendMsg(Object obj) {
        LogUtil.d(TAG, "sendMsg: ", LogUtil.TYPE_RELEASE);
        // 若没有同步过时间 或离上次发送超过1分钟
        if (lastSendSyncTime < 0 || ((System.currentTimeMillis() - lastSendSyncTime) > 60 * 1000L)) {
            if (lastSyncSuccessTime < 0 || (System.currentTimeMillis() - lastSyncSuccessTime) > 24 * 3600 * 1000L) {
                lastSendSyncTime = System.currentTimeMillis();
                String response = formatSendMsgContent(String.valueOf(System.currentTimeMillis()));
                return sendStrMsg(response);
            }
        }
        return false;
    }
}
