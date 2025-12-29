package com.upad.utils;

public class Constants {

    public static final int Address = 1;
    public static final String Url_QA_Api = "http://gatewaycenter.xiezhu365.cn";
    public static final String Url_Online_Api = "http://gatewaycenter.xiezhuwang.com";

//    public static final String OnLineAddress = "msgbus.xiezhuwang.com";
//    public static final String QAAddress = "nsqd.xiezhu365.cn";
//
//    public static final int OnLinePort = 8162;
//    public static final int QAPort = 4150;

    public static final String OnLineAddress = "https://xz.nsqlookup.xiezhuwang.com";
    public static final String QAAddress = "http://nsqlookupd.xiezhu365.cn";

    public static final int OnLinePort = 20161;
    public static final int QAPort = 4161;

    public static final long ServiceTimeout = 20000;
    public static final long ScreenTimeout = 30000;

    /// 客房点餐
    public static final String UPAD_SERVICE_FOOD = "service.food";
    /// 房间打扫
    public static final String UPAD_SERVICE_CLEAN = "service.clean";
    /// 叫醒服务
    public static final String UPAD_SERVICE_WAKEUP = "service.wakeup";
    /// 洗衣服务
    public static final String UPAD_SERVICE_WASH = "service.wash";
    /// 预约叫车
    public static final String UPAD_SERVICE_CAR = "service.car";
    /// 预约发票
    public static final String UPAD_SERVICE_BILL = "service.bill";
    /// 呼叫前台
    public static final String UPAD_SERVICE_FRONTDESK = "service.frontdesk";
    /// 客房续住
    public static final String UPAD_SERVICE_STAY = "service.stay";
    /// 预约退房
    public static final String UPAD_SERVICE_CHECKOUT = "service.checkout";


    public static final String Method_login = "/auth/login";
    public static final String Method_GetHotelInfo = "/api/upad/GetHotelInfo";
    public static final String Method_GetAllRoomInfo = "/api/upad/GetAllRoomInfo";
    public static final String Method_GetRoomInfo = "/api/upad/GetRoomInfo";
    public static final String Method_Register = "/api/upad/Register";
    public static final String Method_InnerService = "/api/upad/InnerService";
    public static final String Method_ReportDeviceInfo = "/api/upad/ReportDeviceInfo";


    public static final String MSKey = "2019_XieZhu_Lock";
    public static final int HttpCode_success = 0;//返回成功
    public static final int HttpCode_fail_error = -1;//网络错误
    public static final int HttpCode_fail_token_invalid = 810015;//Token失效
    public static final int HttpCode_fail_token_error = 810000;//Token错误

    /**
     * 直播信息列表action
     */
    public static final String CMD_TVLIVE_NEXT_CHANNEL = "usay.tvlive.nextchannel";
    public static final String CMD_TVLIVE_PRE_CHANNEL = "usay.tvlive.prevchannel";
    public static final String CMD_TVLIVE_SELECT = "usay.tvlive.select";
    public static final String CMD_MEDIA_VIDEO_SEARCH = "media.video.search";
    public static final String NATIVE_TV_APPLIST = "usay.tv.applist";
}
