package com.android.launcher3.common.network.api;


import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.android.launcher3.common.CommonApp;
import com.android.launcher3.common.bean.Course;
import com.android.launcher3.common.bean.UdBean;
import com.android.launcher3.common.network.DownloadFile;
import com.android.launcher3.common.network.listener.BaseListener;
import com.android.launcher3.common.network.listener.DownloadListener;
import com.android.launcher3.common.network.resp.AcctResidueResp;
import com.android.launcher3.common.network.resp.AddFriendCountResp;
import com.android.launcher3.common.network.resp.AliPayCheckResp;
import com.android.launcher3.common.network.resp.BaseReponse;
import com.android.launcher3.common.network.resp.ConfigResp;
import com.android.launcher3.common.network.resp.DataResp;
import com.android.launcher3.common.network.resp.DealFriendApplyResp;
import com.android.launcher3.common.network.resp.FriendApplyResp;
import com.android.launcher3.common.network.resp.FriendResp;
import com.android.launcher3.common.network.resp.HotTopicsResp;
import com.android.launcher3.common.network.resp.OtaResp;
import com.android.launcher3.common.network.resp.PageResponse;
import com.android.launcher3.common.network.resp.ResidueResp;
import com.android.launcher3.common.network.resp.SearchFriendResp;
import com.android.launcher3.common.network.resp.SosListResp;
import com.android.launcher3.common.network.resp.ThemeDetailsResp;
import com.android.launcher3.common.network.resp.ThemeDownloadResp;
import com.android.launcher3.common.network.resp.UpgradeDetailResp;
import com.android.launcher3.common.network.resp.WeatherResp;
import com.android.launcher3.common.utils.GsonUtil;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.common.utils.PhoneSIMCardUtil;
import com.android.launcher3.common.utils.ThreadPoolUtils;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.internal.observers.BlockingBaseObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Response;

/**
 * @Description：api请求类
 */
public class ApiHelper {

    /**
     * 封装线程管理和订阅的过程
     */
    static void ApiSubscribe(Observable observable, Observer observer) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public static void getWaAcctId(BaseListener<ConfigResp> listener, String imei, String iccid, String mac, String phoneNumber) {
        if (listener == null) {
            LogUtil.d("获取设备id出错了，原因为：listener为空", LogUtil.TYPE_RELEASE);
            return;
        }
        if (imei == null || imei.trim().length() == 0) {
            listener.onError(-1, "imei不可以为空");
            return;
        }
        if (iccid == null || iccid.trim().length() == 0) {
            listener.onError(-1, "iccid不可以为空");
            return;
        }
        getWaAcctIdAndConfig(imei, iccid, mac, phoneNumber, listener);
    }

    public static final MediaType MEDIA_TYPE_PNG = MediaType.parse("file");

    //日志上传文件
    public static void loggerUpload(BaseListener<Boolean> listener, String waAcctId, String filePath) {
        File file = new File(filePath);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(MEDIA_TYPE_PNG, file))
                .addFormDataPart("waAcctId", waAcctId)
                .build();
        Request request;
        if (isInDebug(CommonApp.getInstance())) {
            request = new Request.Builder()
                    .url("http://dev.ximengsz.cn/api/watch/v1/watch/sys/log/upload")
                    .post(requestBody)
                    .build();
        } else {
            request = new Request.Builder()
                    .url("https://ximengsz.com/api/watch/v1/watch/sys/log/upload")
                    .post(requestBody)
                    .build();
        }
        new Thread(() -> {
            try {
                okhttp3.Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    LogUtil.d("日志上传成功", LogUtil.TYPE_RELEASE);
                    listener.onSuccess(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
                listener.onError(-1, e.getMessage());
                LogUtil.d("日志上传失败 " + e.getMessage(), LogUtil.TYPE_RELEASE);
            }
        }).start();
    }


    public static boolean isInDebug(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * 热门推荐首页
     *
     * @param listener
     * @param pageNum  页码
     * @param pageSize 条数
     */
    public static void getHotTopics(BaseListener<PageResponse<HotTopicsResp>> listener, int pageNum, int pageSize) {
        ApiSubscribe(Api.getService().getHotTopics(pageNum, pageSize), new BlockingBaseObserver<BaseReponse<PageResponse<HotTopicsResp>>>() {
            @Override
            public void onNext(BaseReponse<PageResponse<HotTopicsResp>> reponse) {
                if (reponse != null && reponse.getData() != null) {
                    if (listener != null) {
                        listener.onSuccess(reponse.getData());
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                if (listener != null) {
                    listener.onError(-1, e.getMessage());
                }
            }
        });
    }

    /**
     * 主题详情
     *
     * @param listener
     * @param themeId  主题id
     * @param waAcctId 手表id
     */
    public static void getThemeDetails(BaseListener<ThemeDetailsResp> listener, int themeId, int waAcctId) {
        ApiSubscribe(Api.getService().getThemeDetails(themeId, waAcctId), new BlockingBaseObserver<BaseReponse<ThemeDetailsResp>>() {
            @Override
            public void onNext(BaseReponse<ThemeDetailsResp> reponse) {
                if (reponse != null && reponse.getData() != null) {
                    if (listener != null) {
                        listener.onSuccess(reponse.getData());
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                if (listener != null) {
                    listener.onError(-1, e.getMessage());
                }
            }
        });
    }

    /**
     * 主题下载
     *
     * @param listener
     * @param themeId  主题id
     * @param waAcctId 手表id
     */
    public static void getThemeDownload(BaseListener<ThemeDownloadResp> listener, int themeId, int waAcctId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("themeId", themeId);
        jsonObject.addProperty("waAcctId", waAcctId);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), GsonUtil.INSTANCE.toJson(jsonObject));
        ApiSubscribe(Api.getService().getThemeDownload(requestBody), new BlockingBaseObserver<BaseReponse<ThemeDownloadResp>>() {
            @Override
            public void onNext(BaseReponse<ThemeDownloadResp> reponse) {
                if (reponse != null && reponse.getData() != null) {
                    if (listener != null) {
                        listener.onSuccess(reponse.getData());
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                if (listener != null) {
                    listener.onError(-1, e.getMessage());
                }
            }
        });
    }

    /**
     * 账户余额
     *
     * @param listener
     * @param waAcctId 手表id
     */
    public static void getAcctResidue(BaseListener<AcctResidueResp> listener, int waAcctId) {
        ApiSubscribe(Api.getService().getAcctResidue(waAcctId), new BlockingBaseObserver<BaseReponse<AcctResidueResp>>() {
            @Override
            public void onNext(BaseReponse<AcctResidueResp> reponse) {
                if (reponse != null && reponse.getData() != null) {
                    if (listener != null) {
                        listener.onSuccess(reponse.getData());
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                if (listener != null) {
                    listener.onError(-1, e.getMessage());
                }
            }
        });
    }

    /**
     * 手机h5充值页
     *
     * @param listener
     * @param waAcctId 手表id
     */
    public static void getAcctResidueBuyPage(BaseListener<String> listener, int waAcctId) {
        ApiSubscribe(Api.getService().getAcctResidueBuyPage(waAcctId), new BlockingBaseObserver<BaseReponse<String>>() {
            @Override
            public void onNext(BaseReponse<String> reponse) {
                if (reponse != null && reponse.getData() != null) {
                    if (listener != null) {
                        listener.onSuccess(reponse.getData());
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                if (listener != null) {
                    listener.onError(-1, e.getMessage());
                }
            }
        });
    }

    /**
     * 下载apk文件
     *
     * @param listener
     * @param url      文件请求地址
     */
    public static void downloadFile(DownloadListener listener, String url) {
        ApiSubscribe(Api.getService().downloadFile(url), new BlockingBaseObserver<Response<ProgressResponseBody>>() {
            @Override
            public void onNext(Response<ProgressResponseBody> response) {
                ThreadPoolUtils.getExecutorService().execute(new Runnable() {
                    @Override
                    public void run() {
                        String fileName = ".launcher3" + "." + url.substring(url.lastIndexOf(".") + 1);
                        String path = ".launcher";
                        boolean result = DownloadFile.writeResponseBodyToDisk(response.body(), path, fileName, listener);
                        if (result) {
                            if (listener != null) {
                                listener.onSuccess(String.format("%s/%s", path, fileName));
                            }
                        } else {
                            if (listener != null) {
                                listener.onError(-1, "下载异常");
                            }
                        }
                    }
                });
            }

            @Override
            public void onError(Throwable e) {
                if (listener != null) {
                    listener.onError(-1, e.getMessage());
                }
            }
        });
    }

    /**
     * 下载OTAApk文件
     *
     * @param listener
     * @param url      文件请求地址
     */
    public static void downloadOTAFile(DownloadListener listener, String url, String packageName) {
        ApiSubscribe(Api.getService().downloadFile(url), new BlockingBaseObserver<Response<ProgressResponseBody>>() {
            @Override
            public void onNext(Response<ProgressResponseBody> response) {
                ThreadPoolUtils.getExecutorService().execute(new Runnable() {
                    @Override
                    public void run() {
                        String fileName = packageName + "." + url.substring(url.lastIndexOf(".") + 1);
                        String path = "OTAApks";
                        boolean result = DownloadFile.writeResponseBodyToDisk(response.body(), path, fileName, listener);
                        if (result) {
                            if (listener != null) {
                                listener.onSuccess(String.format("%s/%s", path, fileName));
                            }
                        } else {
                            if (listener != null) {
                                listener.onError(-1, "下载异常");
                            }
                        }
                    }
                });
            }

            @Override
            public void onError(Throwable e) {
                if (listener != null) {
                    listener.onError(-1, e.getMessage());
                }
            }
        });
    }

    public static void getConfig(BaseListener<ConfigResp> listener, String imei, String iccid, String mac, String phoneNumber) {
        getWaAcctIdAndConfig(imei, iccid, mac, phoneNumber, listener);
    }

    /**
     * 获取账号及配置信息
     *
     * @param imei        imei号
     * @param iccid       iccid
     * @param mac         mac地址
     * @param phoneNumber 手机号
     * @param listener    获取成功结果
     */
    public static void getWaAcctIdAndConfig(String imei, String iccid, String mac, String phoneNumber, BaseListener<ConfigResp> listener) {

        if (listener == null) {
            LogUtil.d("获取设备id出错了，原因为：listener为空", LogUtil.TYPE_RELEASE);
            return;
        }
        if (imei == null || imei.trim().length() == 0) {
            listener.onError(-1, "imei不可以为空");
            return;
        }

        if (iccid == null || iccid.trim().length() == 0) {
            listener.onError(-1, "iccid不可以为空");
            return;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("imei", imei);
        jsonObject.addProperty("iccid", iccid);
        jsonObject.addProperty("mac", mac);
        jsonObject.addProperty("tel", phoneNumber);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), GsonUtil.INSTANCE.toJson(jsonObject));
        ApiSubscribe(Api.getService().getAcctAndConfig(requestBody), new BlockingBaseObserver<BaseReponse<ConfigResp>>() {
            @Override
            public void onNext(BaseReponse<ConfigResp> reponse) {
                if (reponse.isSuccess()) {
                    if (listener != null) {
                        listener.onSuccess(reponse.getData());
                    }
                } else {
                    listener.onError(reponse.getCode(), reponse.getMsg());
                }
            }

            @Override
            public void onError(Throwable e) {
                if (listener != null) {
                    listener.onError(-1, e.getMessage());
                }
            }
        });
    }

    /**
     * 获取sos列表
     *
     * @param listener
     * @param waAcctId 手表id
     */
    public static void getSosList(BaseListener<SosListResp> listener, int waAcctId) {
        ApiSubscribe(Api.getService().getSosList(waAcctId), new BlockingBaseObserver<BaseReponse<SosListResp>>() {
            @Override
            public void onNext(BaseReponse<SosListResp> reponse) {
                if (reponse.isSuccess()) {
                    if (listener != null) {
                        listener.onSuccess(reponse.getData());
                    }
                } else {
                    listener.onError(reponse.getCode(), reponse.getMsg());
                }
            }

            @Override
            public void onError(Throwable e) {
                if (listener != null) {
                    listener.onError(-1, e.getMessage());
                }
            }
        });
    }


    /**
     * launcher3
     *
     * @param listener
     * @param waAcctId
     */
    public static void upgrade(BaseListener<OtaResp> listener, String waAcctId, String versionNo) {
        ApiSubscribe(Api.getService().upgrade(waAcctId, versionNo), new BlockingBaseObserver<BaseReponse<OtaResp>>() {
            @Override
            public void onNext(BaseReponse<OtaResp> reponse) {
                if (reponse.isSuccess()) {
                    if (listener != null) {
                        listener.onSuccess(reponse.getData());
                    }
                } else {
                    listener.onError(reponse.getCode(), reponse.getMsg());
                }
            }

            @Override
            public void onError(Throwable e) {
                if (listener != null) {
                    listener.onError(-1, e.getMessage());
                }
            }
        });
    }


    public static void getResidue(BaseListener<ResidueResp> listener, String waAcctId) {
        ApiSubscribe(Api.getService().getResidue(waAcctId), new BlockingBaseObserver<BaseReponse<ResidueResp>>() {
            @Override
            public void onNext(BaseReponse<ResidueResp> reponse) {
                if (reponse.isSuccess()) {
                    if (listener != null) {
                        listener.onSuccess(reponse.getData());
                    }
                } else {
                    listener.onError(reponse.getCode(), reponse.getMsg());
                }
            }

            @Override
            public void onError(Throwable e) {
                if (listener != null) {
                    listener.onError(-1, e.getMessage());
                }
            }
        });
    }

    public static void getCourse(BaseListener<Course> listener, String waAcctId) {
        ApiSubscribe(Api.getService().getCourse(waAcctId), new BlockingBaseObserver<BaseReponse<Course>>() {
            @Override
            public void onNext(BaseReponse<Course> response) {
                if (response.isSuccess()) {
                    if (listener != null) {
                        listener.onSuccess(response.getData());
                    }
                } else {
                    listener.onError(response.getCode(), response.getMsg());
                }
            }

            @Override
            public void onError(Throwable e) {
                if (listener != null) {
                    listener.onError(-1, e.getMessage());
                }
            }
        });
    }

    /**
     * 搜索好友接口
     */
    public static void getUser(BaseListener<SearchFriendResp> listener, String tel, String waAcctId, String pageNum, String pageSize) {
        LogUtil.d("tel：" + tel, LogUtil.TYPE_RELEASE);
        LogUtil.d("waAcctId：" + waAcctId, LogUtil.TYPE_RELEASE);
        ApiSubscribe(Api.getService().getUser(tel, waAcctId, pageNum, pageSize), new BlockingBaseObserver<BaseReponse<SearchFriendResp>>() {
            @Override
            public void onNext(BaseReponse<SearchFriendResp> response) {
                if (response.isSuccess()) {
                    if (listener != null) {
                        listener.onSuccess(response.getData());
                    }
                } else {
                    listener.onError(response.getCode(), response.getMsg());
                }
            }

            @Override
            public void onError(Throwable e) {
                if (listener != null) {
                    listener.onError(-1, e.getMessage());
                }
            }
        });
    }


    /**
     * 最新补丁详情
     */
    public static void checkNewPatch(BaseListener<UpgradeDetailResp> listener, String patchSid, String waAcctId) {
        ApiSubscribe(Api.getService().checkNewPatch(patchSid, waAcctId), new BlockingBaseObserver<BaseReponse<UpgradeDetailResp>>() {
            @Override
            public void onNext(BaseReponse<UpgradeDetailResp> reponse) {
                if (reponse.isSuccess()) {
                    if (listener != null) {
                        listener.onSuccess(reponse.getData());
                    }
                } else {
                    listener.onError(reponse.getCode(), reponse.getMsg());
                }
            }

            @Override
            public void onError(Throwable e) {
                if (listener != null) {
                    listener.onError(-1, e.getMessage());
                }
            }
        });
    }

    /**
     * 下载apk文件
     *
     * @param url 文件请求地址
     */
    public static void downloadApkFile(DownloadListener listener, String url, String applicationId) {
        ApiSubscribe(Api.getService().downloadFile(url), new BlockingBaseObserver<Response<ProgressResponseBody>>() {
            @Override
            public void onNext(Response<ProgressResponseBody> response) {
                ThreadPoolUtils.getExecutorService().execute(new Runnable() {
                    @Override
                    public void run() {
                        String fileName = applicationId + "." + url.substring(url.lastIndexOf(".") + 1);
                        String path = applicationId;
                        boolean result = DownloadFile.writeResponseBodyToDisk(response.body(), path, fileName, listener);
                        if (result) {
                            if (listener != null) {
                                listener.onSuccess(String.format("%s/%s", path, fileName));
                            }
                        } else {
                            if (listener != null) {
                                listener.onError(-1, "下载异常");
                            }
                        }
                    }
                });
            }

            @Override
            public void onError(Throwable e) {
                if (listener != null) {
                    listener.onError(-1, e.getMessage());
                }
            }
        });
    }

    /**
     * 补丁升级下载数量+1
     */
    public static void downloadNumber(BaseListener<DataResp> listener, String patchSid, Long waAcctId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("patchSid", patchSid);
        jsonObject.addProperty("waAcctId", waAcctId);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), GsonUtil.INSTANCE.toJson(jsonObject));
        ApiSubscribe(Api.getService().downloadNumber(requestBody), new BlockingBaseObserver<BaseReponse<DataResp>>() {
            @Override
            public void onNext(BaseReponse<DataResp> data) {
                if (data.isSuccess()) {
                    if (listener != null) {
                        listener.onSuccess(data.getData());
                    }
                } else {
                    listener.onError(data.getCode(), data.getMsg());
                }
            }

            @Override
            public void onError(Throwable e) {
                if (listener != null) {
                    listener.onError(-1, e.getMessage());
                }
            }
        });
    }

    /**
     * 碰碰-附近用户列表
     */
    public static void nearbyUsers(BaseListener<FriendResp> listener, long waAcctId, String response) {
        try {
            JsonObject jsonObject = new JsonObject();
            String[] dataArr = response.split(",", -1);
            UdBean result = new UdBean();
            result.setTimestamp(dataArr[0]);
            result.setValidGps(dataArr[1]);
            result.setLatitude(dataArr[2]);
            result.setLatitudeSign(dataArr[3]);
            result.setLongitude(dataArr[4]);
            result.setLongitudeSign(dataArr[5]);
            result.setPrecision(dataArr[6]);
            result.setSpeed(dataArr[7]);
            result.setDirection(dataArr[8]);
            result.setElevation(dataArr[9]);
            result.setSatelliteNumber(dataArr[10]);
            result.setGmsSignalStrength(dataArr[11]);
            result.setElectricQuantity(dataArr[12]);
            result.setStepCount(dataArr[13]);
            result.setTurnNumber(dataArr[14]);
            result.setTerminalState(dataArr[15]);
            result.setNetwork(dataArr[16]);
            result.setCdma(dataArr[17]);
            result.setBts(dataArr[18]);
            result.setNearbts(dataArr[19]);
            result.setMmac(dataArr[20]);
            result.setMacs(dataArr[21]);
            // 1.时间戳
            jsonObject.addProperty("timestamp", result.getTimestamp());
            // 2. A gps、V 基站
            jsonObject.addProperty("ifGps", result.getValidGps());
            // 3. 经度
            jsonObject.addProperty("longitude", result.getLongitude());
            // 4. 纬度
            jsonObject.addProperty("latitude", result.getLatitude());
            // 5.精度
            jsonObject.addProperty("precision", result.getPrecision());
            // 6.卫星个数
            jsonObject.addProperty("satelliteNumber", result.getSatelliteNumber());
            // 7.gsm信号强度
            jsonObject.addProperty("gmsSignalStrength", result.getGmsSignalStrength());
            // 8.无线网络类型 GSM/GPRS/EDGE/HSUPA/HSDPA/WCDMA (注意大写)
            jsonObject.addProperty("network", result.getNetwork());
            // 9.cdma 是否为cdma。 非cdma：0; cdma：1
            jsonObject.addProperty("cdma", result.getCdma());
            // 10.bts 基站信息，非CDMA格式为：mcc, mnc,lac,cellid,signal；其中lac，cellid必须填写，signal如无法获取请填写50，前两位mcc, mnc 如无法获取，请填写-1
            //CDMA格式为：sid,nid,bid,lon,lat,signal 其中lon,lat可为空，格式为：sid,nid,bid,,,signal
            //为保证定位效果，请尽量全部填写
            jsonObject.addProperty("bts", result.getBts());
            // 11.nearbts 周边基站信息 基站信息1|基站信息2|基站信息3….
            jsonObject.addProperty("nearbts", result.getNearbts());
            // 12.mmac 已连热点mac信息 mac,signal,ssid。 如：f0:7d:68:9e:7d:18,-41,TPLink 非必选，但强烈建议填写
            jsonObject.addProperty("mmac", result.getMmac());
            // 13.macs WI-FI列表中mac信息 单mac信息同mmac，mac之间使用“|”分隔。 必须填写 2 个及 2 个以上,30 个 以内的方可正常定位。
            //请不要包含移动WI-FI信息
            jsonObject.addProperty("macs", result.getMacs());
            jsonObject.addProperty("waAcctId", waAcctId);
            // 替换指定键的值中的^为,字符
            List<String> strings = new ArrayList<>();
            strings.add("bts");
            strings.add("nearbts");
            strings.add("mmac");
            strings.add("macs");
            for (String keyToReplace : strings) {
                String newValue = jsonObject.get(keyToReplace).getAsString().replace("^", ",");
                jsonObject.addProperty(keyToReplace, newValue);
                if (keyToReplace.equals("mmac")) {
                    String nValue = jsonObject.get(keyToReplace).getAsString().replace("\"", "");
                    jsonObject.addProperty(keyToReplace, nValue);
                }
            }
            strings.clear();
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), GsonUtil.INSTANCE.toJson(jsonObject));
            ApiSubscribe(Api.getService().nearbyUsers(requestBody), new BlockingBaseObserver<BaseReponse<FriendResp>>() {
                @Override
                public void onNext(BaseReponse<FriendResp> data) {
                    if (data.isSuccess()) {
                        if (listener != null) {
                            listener.onSuccess(data.getData());
                        }
                    } else {
                        listener.onError(data.getCode(), data.getMsg());
                    }
                }

                @Override
                public void onError(Throwable e) {
                    if (listener != null) {
                        listener.onError(-1, e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 请求添加好友
     */
    public static void addFriend(BaseListener<Void> listener, String fromWaAcctId, String toWaAcctId, String tel, boolean isFromSearch) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("fromWaAcctId", fromWaAcctId);
        jsonObject.addProperty("toWaAcctId", toWaAcctId);
        if (isFromSearch) {
            jsonObject.addProperty("tel", tel);
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), GsonUtil.INSTANCE.toJson(jsonObject));
        ApiSubscribe(Api.getService().addFriend(requestBody), new BlockingBaseObserver<BaseReponse<Void>>() {
            @Override
            public void onNext(BaseReponse<Void> data) {
                if (data.isSuccess()) {
                    if (listener != null) {
                        listener.onSuccess(data.getData());
                    }
                } else {
                    listener.onError(data.getCode(), data.getMsg());
                }
            }

            @Override
            public void onError(Throwable e) {
                if (listener != null) {
                    listener.onError(-1, e.getMessage());
                }
            }
        });
    }

    /**
     * 好友申请数量
     */
    public static void addFriendCount(BaseListener<AddFriendCountResp> listener, String waAcctId) {
        ApiSubscribe(Api.getService().addFriendCount(waAcctId), new BlockingBaseObserver<BaseReponse<AddFriendCountResp>>() {
            @Override
            public void onNext(BaseReponse<AddFriendCountResp> data) {
                if (data.isSuccess()) {
                    if (listener != null) {
                        listener.onSuccess(data.getData());
                    }
                } else {
                    listener.onError(data.getCode(), data.getMsg());
                }
            }

            @Override
            public void onError(Throwable e) {
                if (listener != null) {
                    listener.onError(-1, e.getMessage());
                }
            }
        });
    }

    /**
     * 好友申请列表
     */
    public static void friendApply(BaseListener<FriendApplyResp> listener, String waAcctId, int pageNum, int pageSize) {
        ApiSubscribe(Api.getService().friendApply(waAcctId, pageNum, pageSize), new BlockingBaseObserver<BaseReponse<FriendApplyResp>>() {
            @Override
            public void onNext(BaseReponse<FriendApplyResp> data) {
                if (data.isSuccess()) {
                    if (listener != null) {
                        listener.onSuccess(data.getData());
                    }
                } else {
                    listener.onError(data.getCode(), data.getMsg());
                }
            }

            @Override
            public void onError(Throwable e) {
                if (listener != null) {
                    listener.onError(-1, e.getMessage());
                }
            }
        });
    }

    /**
     * 处理添加好友申请
     */
    public static void dealFriendApply(BaseListener<DealFriendApplyResp> listener, int fromWaAcctId, int applyId, int ifAgree) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("fromWaAcctId", fromWaAcctId);
        jsonObject.addProperty("applyId", applyId);
        jsonObject.addProperty("ifAgree", ifAgree);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), GsonUtil.INSTANCE.toJson(jsonObject));
        ApiSubscribe(Api.getService().dealFriendApply(requestBody), new BlockingBaseObserver<BaseReponse<DealFriendApplyResp>>() {
            @Override
            public void onNext(BaseReponse<DealFriendApplyResp> data) {
                if (data.isSuccess()) {
                    if (listener != null) {
                        listener.onSuccess(data.getData());
                    }
                } else {
                    listener.onError(data.getCode(), data.getMsg());
                }
            }

            @Override
            public void onError(Throwable e) {
                if (listener != null) {
                    listener.onError(-1, e.getMessage());
                }
            }
        });
    }

    /**
     * 检测支付宝激活状态
     */
    public static void alipayCheck(BaseListener<AliPayCheckResp> listener, String imei) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("imei", imei);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), GsonUtil.INSTANCE.toJson(jsonObject));
        ApiSubscribe(Api.getService().alipayCheck(requestBody), new BlockingBaseObserver<BaseReponse<AliPayCheckResp>>() {
            @Override
            public void onNext(BaseReponse<AliPayCheckResp> data) {
                if (data.isSuccess()) {
                    if (listener != null) {
                        listener.onSuccess(data.getData());
                    }
                } else {
                    listener.onError(data.getCode(), data.getMsg());
                }
            }

            @Override
            public void onError(Throwable e) {
                if (listener != null) {
                    listener.onError(-1, e.getMessage());
                }
            }
        });
    }

    /**
     * 添加激活支付宝申请
     */
    public static void addAlipayRegister(BaseListener<Void> listener, String imei) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("imei", imei);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), GsonUtil.INSTANCE.toJson(jsonObject));
        LogUtil.d(GsonUtil.INSTANCE.toJson(jsonObject), LogUtil.TYPE_RELEASE);
        ApiSubscribe(Api.getService().addAlipayRegister(requestBody), new BlockingBaseObserver<BaseReponse<Void>>() {
            @Override
            public void onNext(BaseReponse<Void> data) {
                if (data.isSuccess()) {
                    if (listener != null) {
                        listener.onSuccess(data.getData());
                    }
                } else {
                    listener.onError(data.getCode(), data.getMsg());
                }
            }

            @Override
            public void onError(Throwable e) {
                if (listener != null) {
                    listener.onError(-1, e.getMessage());
                }
            }
        });
    }

    /**
     * OTA升级下载数量+1
     */
    public static void otaDownloadNumberIncrease(BaseListener<DataResp> listener, String appId, String versionId, Long waAcctId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("appId", appId);
        jsonObject.addProperty("versionId", versionId);
        jsonObject.addProperty("waAcctId", waAcctId);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), GsonUtil.INSTANCE.toJson(jsonObject));
        ApiSubscribe(Api.getService().otaDownloadNumberIncrease(requestBody), new BlockingBaseObserver<BaseReponse<DataResp>>() {
            @Override
            public void onNext(BaseReponse<DataResp> data) {
                if (data.isSuccess()) {
                    if (listener != null) {
                        listener.onSuccess(data.getData());
                    }
                } else {
                    listener.onError(data.getCode(), data.getMsg());
                }
            }

            @Override
            public void onError(Throwable e) {
                if (listener != null) {
                    listener.onError(-1, e.getMessage());
                }
            }
        });
    }

}
