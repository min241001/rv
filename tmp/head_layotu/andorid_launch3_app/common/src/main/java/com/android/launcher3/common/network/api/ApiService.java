package com.android.launcher3.common.network.api;

import com.android.launcher3.common.bean.Course;
import com.android.launcher3.common.network.resp.AcctResidueResp;
import com.android.launcher3.common.network.resp.AddFriendCountResp;
import com.android.launcher3.common.network.resp.AliPayCheckResp;
import com.android.launcher3.common.network.resp.BaseReponse;
import com.android.launcher3.common.network.resp.ConfigResp;
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

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * @Author: shensl
 * @Description：主题相关的接口定义
 * @CreateDate：2023/12/6 15:53
 * @UpdateUser: shensl
 */
public interface ApiService {

    /**
     * 热门推荐首页
     *
     * @param pageNum  页码
     * @param pageSize 条数
     * @return
     */
    @GET("theme/hot/list")
    Observable<BaseReponse<PageResponse<HotTopicsResp>>> getHotTopics(@Query("pageNum") int pageNum, @Query("pageSize") int pageSize);

    /**
     * 主题详情
     *
     * @param themeId  主题id
     * @param waAcctId 手表id
     * @return
     */
    @GET("theme/detail")
    Observable<BaseReponse<ThemeDetailsResp>> getThemeDetails(@Query("themeId") int themeId, @Query("waAcctId") int waAcctId);

    /**
     * 主题下载
     *
     * @param requestBody 请求体
     * @return
     */
    @POST("theme/to/download")
    Observable<BaseReponse<ThemeDownloadResp>> getThemeDownload(@Body RequestBody requestBody);

    /**
     * 账户余额
     *
     * @param waAcctId 手表id
     * @return
     */
    @GET("theme/acct/residue")
    Observable<BaseReponse<AcctResidueResp>> getAcctResidue(@Query("waAcctId") int waAcctId);

    /**
     * 手机h5充值页
     *
     * @param waAcctId 手表id
     * @return
     */
    @GET("theme/to/wap/residue/page")
    Observable<BaseReponse<String>> getAcctResidueBuyPage(@Query("waAcctId") int waAcctId);


    /**
     * 获取sos列表
     *
     * @param waAcctId
     * @return
     */
    @GET("watch/user/sos/list")
    Observable<BaseReponse<SosListResp>> getSosList(@Query("waAcctId") int waAcctId);

    /**
     * 获取获取及配置信息
     *
     * @param requestBody 请求体
     * @return 账号及配置信息
     */
    @POST("watch/acct/config")
    Observable<BaseReponse<ConfigResp>> getAcctAndConfig(@Body RequestBody requestBody);

    /**
     * 获取账户余额
     *
     * @param waAcctId
     * @return
     */
    @GET("watch/acct/wallet/acct/residue")
    Observable<BaseReponse<ResidueResp>> getResidue(@Query("waAcctId") String waAcctId);

    /**
     * com.android.launcher3最新版本查询
     *
     * @param waAcctId
     * @param versionNo
     * @return
     */
    @GET("watch/inner/app/launch3/upgrade")
    Observable<BaseReponse<OtaResp>> upgrade(@Query("waAcctId") String waAcctId, @Query("versionNo") String versionNo);

    /**
     * 下载文件
     *
     * @param url 文件请求地址
     * @return
     */
    @Streaming
    @GET
    Observable<Response<ResponseBody>> downloadFile(@Url String url);

    @GET("watch/user/course/list")
    Observable<BaseReponse<Course>> getCourse(@Query("waAcctId") String waAcctId);

    /**
     * 搜索好友
     */
    @GET("pp/search/user")
    Observable<BaseReponse<SearchFriendResp>> getUser(@Query("tel") String tel, @Query("waAcctId") String waAcctId, @Query("pageNum") String pageNum, @Query("pageSize") String pageSize);

    /**
     * 最新补丁详情
     */
    @GET("watch/app/patch/new/upgrade/detail")
    Observable<BaseReponse<UpgradeDetailResp>> checkNewPatch(@Query("patchSid") String patchSid, @Query("waAcctId") String waAcctId);

    /**
     * 补丁升级下载数量+1
     */
    @POST("watch/app/patch/new/upgrade/download")
    Observable<BaseReponse<String>> downloadNumber(@Body RequestBody requestBody);

    /**
     * 碰碰-附近用户列表
     */
    @POST("pp/location/request/user")
    Observable<BaseReponse<FriendResp>> nearbyUsers(@Body RequestBody requestBody);

    /**
     * 请求添加好友
     */
    @POST("pp/apply/add/friend")
    Observable<BaseReponse<Void>> addFriend(@Body RequestBody requestBody);

    /**
     * 好友申请数量
     */
    @GET("pp/apply/friend/count")
    Observable<BaseReponse<AddFriendCountResp>> addFriendCount(@Query("waAcctId") String waAcctId);

    /**
     * 好友申请数量
     */
    @GET("pp/apply/friend/list")
    Observable<BaseReponse<FriendApplyResp>> friendApply(@Query("waAcctId") String waAcctId, @Query("pageNum") int pageNum, @Query("pageSize") int pageSize);

    /**
     * 处理添加好友申请
     */
    @POST("pp/process/apply/friend")
    Observable<BaseReponse<DealFriendApplyResp>> dealFriendApply(@Body RequestBody requestBody);

    /**
     * 检测支付宝激活状态
     */
    @POST("apply/activation/alipay/check")
    Observable<BaseReponse<AliPayCheckResp>> alipayCheck(@Body RequestBody requestBody);

    /**
     * 添加激活支付宝申请
     */
    @POST("apply/activation/alipay/save")
    Observable<BaseReponse<Void>> addAlipayRegister(@Body RequestBody requestBody);

    /**
     * OTA升级下载数量+1
     */
    @POST("watch/inner/app/add/download/record")
    Observable<BaseReponse<String>> otaDownloadNumberIncrease(@Body RequestBody requestBody);

}