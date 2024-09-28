package com.android.launcher3.netty.command.monitor;

import android.content.res.TypedArray;
import android.util.Log;

import com.android.launcher3.App;
import com.android.launcher3.common.bean.AppBean;
import com.android.launcher3.common.bean.WatchAppBean;
import com.android.launcher3.common.data.AppLocalData;
import com.android.launcher3.common.mode.AppMode;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.SharedPreferencesUtils;
import com.android.launcher3.netty.constant.XmxxCmdConstant;
import com.baehug.lib.nettyclient.NettyTcpClient;
import com.baehug.lib.nettyclient.command.BaseCmdServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * 应用监督实现类
 */
public class ModAppCmdMonitoringServiceImpl extends BaseCmdServiceImpl {

    private static final String CMD_NAME = "获取手表应用列表";

    private static final String TAG = BaseCmdServiceImpl.class.getSimpleName() + "--->>>" + CMD_NAME + "--->>>";


    @Override
    public String getCommandType() {
        return XmxxCmdConstant.APP_LIST;
    }

    @Override
    public void receiveMsg(NettyTcpClient tcpClient, String msg) {
        Log.d(TAG, "receiveMsg: 收到netty服务器下发的指令 =======" + msg);
        AppMode appMode = new AppMode(App.getInstance());
        List<AppBean> appBeans = appMode.loadDefaultData();
        Log.d(TAG, "receiveMsg: 获取手表应用列表应用数量========"+appBeans.size());
        List<WatchAppBean> appBeanList = new ArrayList<>();
        for (AppBean bean: appBeans) {
            WatchAppBean watchAppBean = new WatchAppBean(bean.getPackageName(),bean.getName(), bean.getType());
            appBeanList.add(watchAppBean);
        }
        //禁用app查询上传
        List<WatchAppBean> list = SharedPreferencesUtils.getList(App.getInstance(), AppLocalData.getInstance().getWatchId());
        if (list!=null && !list.isEmpty()){
            for (int i = 0; i < list.size() ; i++) {
                for (int j = 0; j < appBeanList.size(); j++) {
                    if (list.get(i).getS01PackageName().equals(appBeanList.get(j).getS01PackageName())){
                        appBeanList.get(j).setS03MType(list.get(i).getS03MType());
                    }
                }
            }
        }
        //读取应用监督是否显示的配置文件
        TypedArray appDefaultArray = App.getInstance().getResources().obtainTypedArray(com.android.launcher3.common.R.array._default_monitor_app);
        //读取应用监督是否显示的应用名称
        String[] appNameArray = App.getInstance().getResources().getStringArray(appDefaultArray.getResourceId(0, -1));
        if (appNameArray.length > 0){
            for (int i = 0; i < appNameArray.length; i++){
                for (int j = 0; j < appBeanList.size(); j++) {
                    if (appNameArray[i].equals(appBeanList.get(j).getS02appName())){
                        appBeanList.remove(j);
                    }
                }
            }
        }
        sendMsg(appBeanList);
    }

    @Override
    public Boolean sendMsg(Object obj) {
        String response = null;
        try {
            String listModelToMsgContent = listModelToMsgContent(obj);
            response = formatSendMsgContent(listModelToMsgContent);
            LogUtil.d(TAG + "收到获取手表应用列表的指令，并且转发回复给netty服务器，内容为：" + response.toString(), LogUtil.TYPE_RELEASE);
            sendStrMsg(response);
        } catch (IllegalAccessException e) {
            Log.d(TAG, "sendMsg: " + e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
