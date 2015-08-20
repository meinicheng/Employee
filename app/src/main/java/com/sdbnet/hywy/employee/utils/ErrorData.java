package com.sdbnet.hywy.employee.utils;

import android.text.TextUtils;
import android.util.SparseArray;

import com.baidu.location.BDLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/3.
 */
public class ErrorData {

    public ErrorData() {
        initBDExecptionMsg();
        initAmapExecptionMsg();
    }


    private SparseArray<String> mBDExecptionMap;

    private void initBDExecptionMsg() {


        mBDExecptionMap = new SparseArray<String>();

        mBDExecptionMap.put(BDLocation.TypeGpsLocation, "GPS定位结果 bd");
//        TypeGpsLocation 定位结果描述：GPS定位结果 ，GPS定位结果需要手机打开GPS开关或者设置手机为高精度定位模式，GPS定位结果需要一定的搜星时间才能获得， 连接网络的情况下会加速GPS定位速度
//        61 ： GPS定位结果，GPS定位成功。

        mBDExecptionMap.put(BDLocation.TypeNetWorkLocation, "网络定位结果 bd");
//      TypeNetWorkLocation  定位结果描述：网络定位结果 ，表 示网络定位成功，属于有效的定位结果
//        161： 网络定位结果，网络定位定位成功

        mBDExecptionMap.put(BDLocation.TypeOffLineLocation, "离线定位成功结果 bd");
//      TypeOffLineLocation  定位结果描述：离线定位成功结果 ，一般由于手机网络不通，会请求定位SDK内部的离线定位策略，这种定位也属于有效的定位结果
//        66 ： 离线定位结果。通过requestOfflineLocaiton调用时对应的返回结果。

        mBDExecptionMap.put(BDLocation.TypeNetWorkException, "网络连接失败 bd");
//       TypeNetWorkException 定位结果描述：网络连接失败，一般由于手机无有效网络连接导致，请检查手机是否能够正常上网
//        63 ： 网络异常，没有成功向服务器发起请求，请确认当前测试手机网络是否通畅，尝试重新请求定位。

        mBDExecptionMap.put(BDLocation.TypeOffLineLocationFail, "离线定位失败结果 bd");
//      TypeOffLineLocationFail  定位结果描述：离线定位失败结果 ，一般由于手机网络不通，会请求定位SDK内部的离线定位策略但失败了，这种定位也属于无效的定位结果
//        67 ： 离线定位失败。通过requestOfflineLocaiton调用时对应的返回结果。

        mBDExecptionMap.put(BDLocation.TypeNone, "无效定位结果 bd");
//       TypeNone 定位结果描述：无效定位结果，一般由于定位SDK内部逻辑异常时出现

        mBDExecptionMap.put(BDLocation.TypeCacheLocation, "缓存定位结果,取消 bd");
//        TypeCacheLocation 定位结果描述：缓存定位结果，目前该功能已经取消，由离线定位来代替
//        65 ： 定位缓存的结果。

        mBDExecptionMap.put(BDLocation.TypeCriteriaException, "无法定位结果 bd");
//        TypeCriteriaException 定位结果描述：无法定位结果，一般由于定位SDK内部检测到没有有效的定位依据，比如在飞行模式下就会返回该定位类型， 一般关闭飞行模式或者打开wifi就可以再次定位成功
        //        62 ： 无法获取有效定位依据，定位失败，请检查运营商网络或者wifi网络是否正常开启，尝试重新请求定位


        mBDExecptionMap.put(BDLocation.TypeOffLineLocationNetworkFail, "离线定位成功结果,已取消 bd");
//       TypeOffLineLocationNetworkFail 定位结果描述：离线定位成功结果 ，已取消
//        68 ： 网络连接失败时，查找本地离线定位时对应的返回结果。

        mBDExecptionMap.put(BDLocation.TypeServerError, "server定位失败 bd");
//    TypeServerError    定位结果描述：server定位失败，没有对应的位置信息
//        167： 服务端定位失败，请您检查是否禁用获取位置信息权限，尝试重新请求定位。

        mBDExecptionMap.put(162, "请求串密文解析失败 bd");
        //        162： 请求串密文解析失败。
        mBDExecptionMap.put(502, "key参数错误 bd");
//        502： key参数错误，请按照说明文档重新申请KEY。
        mBDExecptionMap.put(505, " key不存在或者非法 bd");
//        505： key不存在或者非法，请按照说明文档重新申请KEY。
        mBDExecptionMap.put(601, " key服务被开发者自己禁用 bd");
//        601： key服务被开发者自己禁用，请按照说明文档重新申请KEY。
        mBDExecptionMap.put(602, "key mcode不匹配 bd");
//        602： key mcode不匹配，您的ak配置过程中安全码设置有问题，请确保：sha1正确，“;”分号是英文状态；且包名是您当前运行应用的包名，请按照说明文档重新申请KEY。
//        mBDExecptionMap.put(62, "server定位失败 bd");
//        501～700：key验证失败，请按照说明文档重新申请KEY。
//        如果不能定位，请记住这个返回值，并到百度LBS开放平台论坛Andriod定位SDK版块中进行交流 http://bbs.lbsyun.baidu.com/forum.php?mod=forumdisplay&fid=10 。若返回值是162~167，请将错误码、imei和定位时间反馈至loc-bugs@baidu.com，以便我们跟进追查问题。


    }

    public String getBDExecptionMsg(int key) {
        String msg = mBDExecptionMap.get(key);
        if (TextUtils.isEmpty(msg)) {
            return "key="+key+"";
        }
        return msg+""+key;
    }

    private SparseArray<String> mAmapExecptionMap;

    private void initAmapExecptionMsg() {
        mAmapExecptionMap = new SparseArray<String>();

        mAmapExecptionMap.put(0, "正常 amp");
        // 0 正常
        mAmapExecptionMap.put(21, "IO 操作异常 amp");
        // 21 IO 操作异常
        mAmapExecptionMap.put(22, "连接异常 amp");
        // 22 连接异常
        mAmapExecptionMap.put(23, "连接超时 amp");
        // 23 连接超时
        mAmapExecptionMap.put(24, "无效的参数 amp");
        // 24 无效的参数
        mAmapExecptionMap.put(25, "空指针异常 amp");
        // 25 空指针异常
        mAmapExecptionMap.put(26, "url 异常 amp");
        // 26 url 异常
        mAmapExecptionMap.put(27, "未知主机 amp");
        // 27 未知主机
        mAmapExecptionMap.put(28, "服务器连接失败 amp");
        // 28 服务器连接失败
        mAmapExecptionMap.put(29, "协议解析错误 amp");
        // 29 协议解析错误
        mAmapExecptionMap.put(30, "http 连接失败 amp");
        // 30 http 连接失败
        mAmapExecptionMap.put(31, "未知的错误 amp");
        // 31 未知的错误
        mAmapExecptionMap.put(32, "key 鉴权失败 amp");
        // 32 key 鉴权失败
        mAmapExecptionMap.put(33, "获取基站/WiFi信息为空或失败 amp");
        // 33 获取基站/WiFi信息为空或失败
        mAmapExecptionMap.put(34, "定位失败无法获取城市信息 amp");
        // 34 定位失败无法获取城市信息

    }
    public String getAmapExecptionMsg(int key){
        String msg=mAmapExecptionMap.get(key);
        if(TextUtils.isEmpty(msg)){
            return "key="+key;
        }
        return msg+""+key;
    }

}
