package com.android.launcher3.netty;

import android.os.Environment;
import android.util.Log;

import com.android.launcher3.App;
import com.android.launcher3.BuildConfig;
import com.android.launcher3.R;
import com.android.launcher3.common.data.AppLocalData;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.StringUtils;
import com.android.launcher3.moudle.launcher.util.WaAcctIdListener;
import com.android.launcher3.moudle.launcher.util.WatchAccountUtils;
import com.android.launcher3.utils.FileUtil;
import com.baehug.lib.nettyclient.NettyTcpClient;
import com.baehug.lib.nettyclient.command.CmdService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NettyManager {
    /**
     * netty的服务器地址 用于测试等会会删掉
     */
    public final static String SERVER_URL = BuildConfig.NETTY_HOST;
    /**
     * 亮屏心跳时间
     */
    public final static int SCREEN_ON_HB_TIME = 120;
    /**
     * 暗屏心跳时间
     */
    public final static int SCREEN_OFF_HB_TIME = 120;
    /**
     * 端口
     */
    public final static int PORT = BuildConfig.NETTY_PORT;
    private static final String TAG = "NettyManager";
    public static XmxxDeviceInfBean nettyParameter = new XmxxDeviceInfBean(SERVER_URL, PORT, "XM", SCREEN_ON_HB_TIME);
    private static WaAcctIdListener waAcctIdListener;

    private static boolean ifOpenNettyManager = true;

    /**
     * 是否启用本工具类
     *
     * @param ifOpen 是否启用
     */
    public static void ifOpenNettyManager(Boolean ifOpen) {
        ifOpenNettyManager = ifOpen;
    }

    private static String nettyFlag = "";

    /**
     * 检查netty是否正在，若不正常，重新连接
     */
    public static void checkNettyState() {
        if (ifCloseOpenNetty()) {
            nettyFlag = "没有启动nettyManager";
            return;
        }
        NettyTcpClient client = NettyTcpClient.getInstance();
        if (client.isActive()) {
            nettyFlag = "netty服务器活着，不需要再次启动！";
            LogUtil.d("netty客户端活着，不需要再次启动！", LogUtil.TYPE_RELEASE);
            return;
        }
        if (StringUtils.isBlank(AppLocalData.getInstance().getWatchId())) {
            nettyFlag = "无账号不尝试进行重连Netty";
            WatchAccountUtils.getInstance().checkAndGetAcctId();
            Log.d(TAG, "checkNettyState: 无账号不尝试进行重连");
        } else {
            nettyFlag = "有账号有没有开启超级省电，才进行重连连接Netty";
            // 有账号才进行重连连接
            NettyTcpClient.getInstance().tryReConnect(false);
        }
        if (BuildConfig.DEBUG) {
            // 在Debug环境下执行日志写入本地的代码
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTime = sdf.format(calendar.getTime());
            writeLogToFile("当前时间: "+currentTime+"\t\t当前手表的netty状态 === "+nettyFlag+"\n");
        }
    }

    //netty是否在线的日志写入本地文件
    public static void writeLogToFile(String logMessage) {
        File logFile = new File(Environment.getExternalStorageDirectory(), "nettyLogfile.txt");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true));
            writer.write(logMessage);
            writer.newLine();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭netty
     */
    public static void closeNetty() {
        NettyTcpClient.getInstance().close();
    }

    /**
     * netty 强制重置并重连连接
     */
    public static void forceResetAndConnect() {
        if (ifCloseOpenNetty()) {
            return;
        }
        if (!AppLocalData.getInstance().getPower()){
            return;
        }
        // 重新连接(若有参数，请在nettyParameter中修改，增加实现类，调用 addOrRecoverCmdService）
        NettyTcpClient.getInstance().resetAndConnect();
    }

    /**
     * 初始化netty 实现类及参数
     */
    public static void initNettyCmdServiceAndParameter() {
        if (ifCloseOpenNetty()) {
            return;
        }
        initWaAcctIdListener();
        NettyTcpClient.getInstance().initClient(nettyParameter, initNettyCmdServiceImpl());
    }

    /**
     * 初始监听账号变化
     */
    protected static void initWaAcctIdListener() {
        if (ifCloseOpenNetty()) {
            return;
        }
        if (waAcctIdListener != null) {
            WatchAccountUtils.getInstance().removeWaAcctIdListener(waAcctIdListener);
        }
        waAcctIdListener = new WaAcctIdListener() {
            @Override
            public void waAcctIdChange(String newAcctId, String oldAcctId) {
                forceResetAndConnect();
            }
        };
        WatchAccountUtils.getInstance().addWaAcctIdListener(waAcctIdListener);
    }

    /**
     * 获取所有的cmdService实现类
     *
     * @return 实现列表
     */
    private static List<CmdService> initNettyCmdServiceImpl() {
        String classNameStr = FileUtil.readTextFile(App.getInstance(), R.raw.netty_cmd_impl);
        String[] strings = classNameStr.split("\n");
        List<CmdService> cmdServiceList = new ArrayList<>(50);
        for (String string : strings) {
            try {
                Class filterClass = Class.forName(string);
                if (CmdService.class.isAssignableFrom(filterClass)) {
                    CmdService cmdService = (CmdService) filterClass.newInstance();
                    cmdServiceList.add(cmdService);
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        return cmdServiceList;
    }

    /**
     * 打印netty class（当netty 实现类有变动时，调用该类）
     * 1、打印已经实现的netty 实现类
     * 2、覆盖打印内容到res/raw/netty_cmd_impl.txt 文件中
     */
    private void printNettyClass() {
        String packageName = "com.android.launcher3.netty.command";
        // 获取当前应用的所有类
        StringBuilder stringBuilder = new StringBuilder(50);
        stringBuilder.append("\n");
        List<Class<?>> allClasses = FileUtil.getClassesByPacketName(packageName);
        for (Class<?> filterClass : allClasses) {
            if (CmdService.class.isAssignableFrom(filterClass)) {
                try {
                    CmdService cmdService = (CmdService) filterClass.newInstance();
                    stringBuilder.append(filterClass.getName());
                    stringBuilder.append("\n");
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        Log.d(TAG, "initNettyParameter: " + stringBuilder.toString());
    }

    /**
     * 判断能够关闭netty
     * @return
     */
    public static Boolean ifCloseOpenNetty(){
        if (AppLocalData.getInstance().getPower() || !ifOpenNettyManager){
            return true;
        }
        return false;
    }
}