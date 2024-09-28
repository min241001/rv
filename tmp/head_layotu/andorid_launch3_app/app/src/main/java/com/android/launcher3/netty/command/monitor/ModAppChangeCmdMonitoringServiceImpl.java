package com.android.launcher3.netty.command.monitor;

import android.util.Log;
import com.android.launcher3.App;
import com.android.launcher3.common.bean.WatchAppBean;
import com.android.launcher3.common.data.AppLocalData;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.StringUtils;
import com.android.launcher3.netty.constant.XmxxCmdConstant;
import com.android.launcher3.utils.AppSuperviseManager;
import com.baehug.lib.nettyclient.NettyTcpClient;
import com.baehug.lib.nettyclient.command.BaseCmdServiceImpl;
import java.util.List;

public class ModAppChangeCmdMonitoringServiceImpl extends BaseCmdServiceImpl {

    private static final String CMD_NAME = "修改手表应用列表状态";

    private static final String TAG = BaseCmdServiceImpl.class.getSimpleName() + "--->>>" + CMD_NAME + "--->>>";

    @Override
    public String getCommandType() {
        return XmxxCmdConstant.OVR_APP_LIST;
    }

    @Override
    public void receiveMsg(NettyTcpClient tcpClient, String msg) {
        Log.d(TAG, "receiveMsg: 收到netty服务器下发的指令 =======修改手表应用列表状态====" + msg);
        try {
            if (StringUtils.isBlank(msg)) {
                LogUtil.e(TAG + "收到netty服务器下发的指令错误：msg为空");
                return;
            }
            List<WatchAppBean> list = msgListContentToModel(msg, WatchAppBean.class, false);
            Log.d(TAG, "receiveMsg: netty服务器下发的应用列表集合数量为 = "+ list.size());
            WatchAppManager.saveAll(App.getInstance(), AppLocalData.getInstance().getWatchId(), list);
            sendMsg("");

            AppSuperviseManager.getInstance().launchHome();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public Boolean sendMsg(Object obj) {
        String response = null;
        try {
            String msg = modelToMsgContent("nothing");
            response = formatSendMsgContent(msg);
            LogUtil.d(TAG + "收到获取手表应用列表的指令，并且转发回复给netty服务器，内容为：" + response.toString(), LogUtil.TYPE_RELEASE);
            sendStrMsg(response);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}
