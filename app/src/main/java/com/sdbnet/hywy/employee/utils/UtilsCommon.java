package com.sdbnet.hywy.employee.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.sdbnet.hywy.employee.MainApplication;
import com.sdbnet.hywy.employee.R;
import com.sdbnet.hywy.employee.db.db.DBManager;
import com.sdbnet.hywy.employee.db.domin.LocateLog;
import com.sdbnet.hywy.employee.db.domin.OperateLog;
import com.sdbnet.hywy.employee.net.AsyncHttpService;

public class UtilsCommon {
    private static final String TAG = UtilsCommon.class.getSimpleName();

    public static boolean checkAccount() {
        String company = PreferencesUtil
                .getValue(PreferencesUtil.KEY_COMPANY_ID);
        String itemId = PreferencesUtil.getValue(PreferencesUtil.KEY_ITEM_ID);
        String userId = PreferencesUtil.getValue(PreferencesUtil.KEY_USER_ID);
        String userTel = PreferencesUtil.getValue(PreferencesUtil.KEY_USER_TEL);

        if (TextUtils.isEmpty(company) || TextUtils.isEmpty(itemId)
                || TextUtils.isEmpty(userTel) || TextUtils.isEmpty(userId)) {
            // PreferencesUtil.initStoreData();
            return false;
        } else {
            PreferencesUtil.user_company = company;
            PreferencesUtil.item_id = itemId;
            PreferencesUtil.user_id = userId;
            PreferencesUtil.user_tel = userTel;
            return true;
        }
    }

    public static boolean checkLocateLog(LocateLog locate) {
        if (TextUtils.isEmpty(locate.getCmpid())
                || TextUtils.isEmpty(locate.getPid())
                || TextUtils.isEmpty(locate.getItemid())
                || TextUtils.isEmpty(locate.getLoctel())) {
            return false;
        } else {
            return true;
        }
    }

    // public static String busiScanActions() {
    // List<Map<?, ?>> list = new ArrayList<Map<?, ?>>();
    // Map<String, String> map = new HashMap<String, String>();
    // map.put("actidx", "1");
    // map.put("action", "A0");
    // map.put("actname", "A0");
    // map.put("btnname", "A0");
    // map.put("actmemo", "A0");
    // map.put("lineno", "1");
    // map.put("iscall", "1");
    // map.put("islocate", "1");
    // map.put("isscan", "1");
    // map.put("linename", "车辆追踪");
    // map.put("sign", "0");
    // map.put("startnode", "0");
    // map.put("workflow", "1,3");
    // map.put("acttype", "0");
    // list.add(map);
    //
    // map = new HashMap<String, String>();
    // map.put("actidx", "1");
    // map.put("action", "A001");
    // map.put("actname", "A001");
    // map.put("btnname", "A001");
    // map.put("actmemo", "A001");
    // map.put("lineno", "1");
    // map.put("iscall", "1");
    // map.put("islocate", "1");
    // map.put("isscan", "1");
    // map.put("linename", "车辆追踪");
    // map.put("sign", "0");
    // map.put("startnode", "0");
    // map.put("workflow", "1,3");
    // map.put("acttype", "0");
    // list.add(map);
    //
    // map = new HashMap<String, String>();
    // map.put("actidx", "1");
    // map.put("action", "A002");
    // map.put("actname", "A002");
    // map.put("btnname", "A002");
    // map.put("actmemo", "A002");
    // map.put("lineno", "1");
    // map.put("iscall", "1");
    // map.put("islocate", "1");
    // map.put("isscan", "1");
    // map.put("linename", "车辆追踪");
    // map.put("sign", "0");
    // map.put("startnode", "0");
    // map.put("workflow", "1,3");
    // map.put("acttype", "0");
    // list.add(map);
    //
    // map = new HashMap<String, String>();
    // map.put("actidx", "1");
    // map.put("action", "A003");
    // map.put("actname", "A003");
    // map.put("btnname", "A003");
    // map.put("actmemo", "A003");
    // map.put("lineno", "1");
    // map.put("iscall", "1");
    // map.put("islocate", "1");
    // map.put("isscan", "1");
    // map.put("linename", "车辆追踪");
    // map.put("sign", "0");
    // map.put("startnode", "0");
    // map.put("workflow", "1,3");
    // map.put("acttype", "0");
    // list.add(map);
    //
    // map = new HashMap<String, String>();
    // map.put("actidx", "2");
    // map.put("action", "B0");
    // map.put("actname", "B0");
    // map.put("btnname", "B0");
    // map.put("actmemo", "B0");
    // map.put("lineno", "1");
    // map.put("iscall", "1");
    // map.put("islocate", "1");
    // map.put("isscan", "1");
    // map.put("linename", "车辆追踪");
    // map.put("sign", "0");
    // map.put("startnode", "0");
    // map.put("workflow", "1,3");
    // map.put("acttype", "0");
    // list.add(map);
    //
    // map = new HashMap<String, String>();
    // map.put("actidx", "2");
    // map.put("action", "E0");
    // map.put("actname", "B001");
    // map.put("btnname", "B001");
    // map.put("actmemo", "B001");
    // map.put("lineno", "1");
    // map.put("iscall", "1");
    // map.put("islocate", "1");
    // map.put("isscan", "1");
    // map.put("linename", "车辆追踪");
    // map.put("sign", "0");
    // map.put("startnode", "0");
    // map.put("workflow", "1,3");
    // map.put("acttype", "0");
    // list.add(map);
    //
    // map = new HashMap<String, String>();
    // map.put("actidx", "2");
    // map.put("action", "F0");
    // map.put("actname", "B002");
    // map.put("btnname", "B002");
    // map.put("actmemo", "B002");
    // map.put("lineno", "1");
    // map.put("iscall", "1");
    // map.put("islocate", "1");
    // map.put("isscan", "1");
    // map.put("linename", "车辆追踪");
    // map.put("sign", "0");
    // map.put("startnode", "0");
    // map.put("workflow", "1,3");
    // map.put("acttype", "0");
    // list.add(map);
    //
    // map = new HashMap<String, String>();
    // map.put("actidx", "3");
    // map.put("action", "C0");
    // map.put("actname", "C0");
    // map.put("btnname", "C0");
    // map.put("actmemo", "C0");
    // map.put("lineno", "1");
    // map.put("iscall", "1");
    // map.put("islocate", "1");
    // map.put("isscan", "1");
    // map.put("linename", "车辆追踪");
    // map.put("sign", "0");
    // map.put("startnode", "0");
    // map.put("workflow", "1,3");
    // map.put("acttype", "0");
    // list.add(map);
    //
    // map = new HashMap<String, String>();
    // map.put("actidx", "4");
    // map.put("action", "G0");
    // map.put("actname", "C001");
    // map.put("btnname", "C001");
    // map.put("actmemo", "C001");
    // map.put("lineno", "1");
    // map.put("iscall", "1");
    // map.put("islocate", "1");
    // map.put("isscan", "1");
    // map.put("linename", "车辆追踪");
    // map.put("sign", "0");
    // map.put("startnode", "0");
    // map.put("workflow", "1,3");
    // map.put("acttype", "0");
    // list.add(map);
    //
    // map = new HashMap<String, String>();
    // map.put("actidx", "5");
    // map.put("action", "H0");
    // map.put("actname", "C010");
    // map.put("btnname", "C010");
    // map.put("actmemo", "C010");
    // map.put("lineno", "1");
    // map.put("iscall", "1");
    // map.put("islocate", "1");
    // map.put("isscan", "1");
    // map.put("linename", "车辆追踪");
    // map.put("sign", "0");
    // map.put("startnode", "0");
    // map.put("workflow", "1,3");
    // map.put("acttype", "0");
    // list.add(map);
    //
    // map = new HashMap<String, String>();
    // map.put("actidx", "7");
    // map.put("action", "I0");
    // map.put("actname", "C030");
    // map.put("btnname", "C030");
    // map.put("actmemo", "C030");
    // map.put("lineno", "1");
    // map.put("iscall", "1");
    // map.put("islocate", "1");
    // map.put("isscan", "1");
    // map.put("linename", "车辆追踪");
    // map.put("sign", "0");
    // map.put("startnode", "0");
    // map.put("workflow", "1,3");
    // map.put("acttype", "0");
    // list.add(map);
    //
    // map = new HashMap<String, String>();
    // map.put("actidx", "7");
    // map.put("action", "C03001");
    // map.put("actname", "C03001");
    // map.put("btnname", "C03001");
    // map.put("actmemo", "C03001");
    // map.put("lineno", "1");
    // map.put("iscall", "1");
    // map.put("islocate", "1");
    // map.put("isscan", "1");
    // map.put("linename", "车辆追踪");
    // map.put("sign", "0");
    // map.put("startnode", "0");
    // map.put("workflow", "1,3");
    // map.put("acttype", "0");
    // list.add(map);
    //
    // map = new HashMap<String, String>();
    // map.put("actidx", "7");
    // map.put("action", "C03002");
    // map.put("actname", "C03002");
    // map.put("btnname", "C03002");
    // map.put("actmemo", "C03002");
    // map.put("lineno", "1");
    // map.put("iscall", "1");
    // map.put("islocate", "1");
    // map.put("isscan", "1");
    // map.put("linename", "车辆追踪");
    // map.put("sign", "0");
    // map.put("startnode", "0");
    // map.put("workflow", "1,3");
    // map.put("acttype", "0");
    // list.add(map);
    //
    // map = new HashMap<String, String>();
    // map.put("actidx", "7");
    // map.put("action", "C03003");
    // map.put("actname", "C03003");
    // map.put("btnname", "C03003");
    // map.put("actmemo", "C03003");
    // map.put("lineno", "1");
    // map.put("iscall", "1");
    // map.put("islocate", "1");
    // map.put("isscan", "1");
    // map.put("linename", "车辆追踪");
    // map.put("sign", "0");
    // map.put("startnode", "0");
    // map.put("workflow", "1,3");
    // map.put("acttype", "0");
    // list.add(map);
    //
    // map = new HashMap<String, String>();
    // map.put("actidx", "8");
    // map.put("action", "D0");
    // map.put("actname", "D0");
    // map.put("btnname", "D0");
    // map.put("actmemo", "D0");
    // map.put("lineno", "1");
    // map.put("iscall", "1");
    // map.put("islocate", "1");
    // map.put("isscan", "1");
    // map.put("linename", "车辆追踪");
    // map.put("sign", "0");
    // map.put("startnode", "0");
    // map.put("workflow", "1,3");
    // map.put("acttype", "0");
    // list.add(map);
    //
    // map = new HashMap<String, String>();
    // map.put("actidx", "9");
    // map.put("action", "D001");
    // map.put("actname", "D001");
    // map.put("btnname", "D001");
    // map.put("actmemo", "D001");
    // map.put("lineno", "1");
    // map.put("iscall", "1");
    // map.put("islocate", "1");
    // map.put("isscan", "1");
    // map.put("linename", "车辆追踪");
    // map.put("sign", "0");
    // map.put("startnode", "0");
    // map.put("workflow", "1,3");
    // map.put("acttype", "0");
    // list.add(map);
    //
    // map = new HashMap<String, String>();
    // map.put("actidx", "10");
    // map.put("action", "D010");
    // map.put("actname", "D010");
    // map.put("btnname", "D010");
    // map.put("actmemo", "D010");
    // map.put("lineno", "1");
    // map.put("iscall", "1");
    // map.put("islocate", "1");
    // map.put("isscan", "1");
    // map.put("linename", "车辆追踪");
    // map.put("sign", "0");
    // map.put("startnode", "0");
    // map.put("workflow", "1,3");
    // map.put("acttype", "0");
    // list.add(map);
    //
    // map = new HashMap<String, String>();
    // map.put("actidx", "10");
    // map.put("action", "D01001");
    // map.put("actname", "D01001");
    // map.put("btnname", "D01001");
    // map.put("actmemo", "D01001");
    // map.put("lineno", "1");
    // map.put("iscall", "1");
    // map.put("islocate", "1");
    // map.put("isscan", "1");
    // map.put("linename", "车辆追踪");
    // map.put("sign", "0");
    // map.put("startnode", "0");
    // map.put("workflow", "1,3");
    // map.put("acttype", "0");
    // list.add(map);
    //
    // map = new HashMap<String, String>();
    // map.put("actidx", "10");
    // map.put("action", "D0100101");
    // map.put("actname", "D0100101");
    // map.put("btnname", "D0100101");
    // map.put("actmemo", "D0100101");
    // map.put("lineno", "1");
    // map.put("iscall", "1");
    // map.put("islocate", "1");
    // map.put("isscan", "1");
    // map.put("linename", "车辆追踪");
    // map.put("sign", "0");
    // map.put("startnode", "0");
    // map.put("workflow", "1,3");
    // map.put("acttype", "0");
    // list.add(map);
    //
    // try {
    // return simpleListToJsonStr(list);
    // } catch (Exception e) {
    // e.printStackTrace();
    // return "";
    // }
    // }
    //
    // public static String busiOrderItems() throws Exception {
    // List<Map<?, ?>> list = new ArrayList<Map<?, ?>>();
    // Map<String, String> map = new HashMap<String, String>();
    // map.put("hboId", "179939539643");
    // map.put("hboReceName", "王小虎");
    // map.put("hboReceTel", "15825495456");
    // map.put("hboStatus", "签收");
    // list.add(map);
    //
    // map = new HashMap<String, String>();
    // map.put("hboId", "151032194303");
    // map.put("hboReceName", "李华然");
    // map.put("hboReceTel", "15762084234");
    // map.put("hboStatus", "签收");
    // list.add(map);
    //
    // map = new HashMap<String, String>();
    // map.put("hboId", "146721675703");
    // map.put("hboReceName", "王学斌");
    // map.put("hboReceTel", "15762083235");
    // map.put("hboStatus", "拒签");
    // list.add(map);
    //
    // map = new HashMap<String, String>();
    // map.put("hboId", "145704191523");
    // map.put("hboReceName", "张永强");
    // map.put("hboReceTel", "15763208323");
    // map.put("hboStatus", "签收");
    // list.add(map);
    //
    // map = new HashMap<String, String>();
    // map.put("hboId", "143056016513");
    // map.put("hboReceName", "王飞");
    // map.put("hboReceTel", "15662960236");
    // map.put("hboStatus", "二次配送");
    // list.add(map);
    //
    // return simpleListToJsonStr(list);
    // }
    //
    // public static String busiTraces() throws Exception {
    // List<Map<?, ?>> list = new ArrayList<Map<?, ?>>();
    // Map<String, String> map = new HashMap<String, String>();
    // map.put("hoiOrderId", "179939539643917");
    // map.put("hdiName", "王小虎");
    // map.put("hboRecePlace", "快件由官渡一区 已发往 昆明中转");
    // map.put("hboReceTime", "2014-11-30 01:11");
    // map.put("hdiPlate", "滇C52678");
    // list.add(map);
    //
    // map = new HashMap<String, String>();
    // map.put("hoiOrderId", "151032194303910");
    // map.put("hdiName", "李华然");
    // map.put("hboRecePlace", "快件由昆明中转 已发往 东莞中心");
    // map.put("hboReceTime", "2014-12-01 09:22");
    // map.put("hdiPlate", "滇B288851");
    // list.add(map);
    //
    // map = new HashMap<String, String>();
    // map.put("hoiOrderId", "146721675703917");
    // map.put("hdiName", "王学斌");
    // map.put("hboRecePlace", "快件由东莞中心 已发往 深圳中心");
    // map.put("hboReceTime", "2014-12-02 02:12");
    // map.put("hdiPlate", "粤B62345");
    // list.add(map);
    //
    // map = new HashMap<String, String>();
    // map.put("hoiOrderId", "145704191523911");
    // map.put("hdiName", "张永强");
    // map.put("hboRecePlace", "快件由深圳中心 已发往 深圳宝安");
    // map.put("hboReceTime", "2014-12-02 05:31");
    // map.put("hdiPlate", "粤E12345");
    // list.add(map);
    //
    // map = new HashMap<String, String>();
    // map.put("hoiOrderId", "143056016513909");
    // map.put("hdiName", "王飞");
    // map.put("hboRecePlace", "深圳宝安区创业花园淘景大厦");
    // map.put("hboReceTime", "2014-12-03 11:51");
    // map.put("hdiPlate", "粤C77865");
    // list.add(map);
    //
    // return simpleListToJsonStr(list);
    // }
    //
    // public static String busiOrders() throws Exception {
    // List<Map<?, ?>> list = new ArrayList<Map<?, ?>>();
    // Map<String, String> map = new HashMap<String, String>();
    // map.put("hboId", "879939539643917");
    // map.put("hboReceName", "王小虎");
    // map.put("hboReceTel", "18325495456");
    // list.add(map);
    //
    // map = new HashMap<String, String>();
    // map.put("hboId", "876568240093911");
    // map.put("hboReceName", "张有才");
    // map.put("hboReceTel", "15762084234");
    // list.add(map);
    //
    // map = new HashMap<String, String>();
    // map.put("hboId", "851032194303910");
    // map.put("hboReceName", "李华然");
    // map.put("hboReceTel", "15762083235");
    // list.add(map);
    //
    // map = new HashMap<String, String>();
    // map.put("hboId", "846728675703917");
    // map.put("hboReceName", "王学斌");
    // map.put("hboReceTel", "15763208323");
    // list.add(map);
    //
    // map = new HashMap<String, String>();
    // map.put("hboId", "845704191523918");
    // map.put("hboReceName", "张永强");
    // map.put("hboReceTel", "15662960236");
    // list.add(map);
    //
    // map = new HashMap<String, String>();
    // map.put("hboId", "843056086583909");
    // map.put("hboReceName", "王飞");
    // map.put("hboReceTel", "13853963379");
    // list.add(map);
    //
    // map = new HashMap<String, String>();
    // map.put("hboId", "845704191523918");
    // map.put("hboReceName", "梁建");
    // map.put("hboReceTel", "13853990238");
    // list.add(map);
    //
    // map = new HashMap<String, String>();
    // map.put("hboId", "839745373783911");
    // map.put("hboReceName", "刘彻");
    // map.put("hboReceTel", "13853919575");
    // list.add(map);
    //
    // return simpleListToJsonStr(list);
    // }

    /**
     * 将JAVA的MAP转换成JSON字符串， 只转换第一层数据
     *
     * @param map
     * @return
     */
    public static String simpleMapToJsonStr(Map<?, ?> map) {
        if (map == null || map.isEmpty()) {
            return "null";
        }
        String jsonStr = "{";
        Set<?> keySet = map.keySet();
        for (Object key : keySet) {
            jsonStr += "\"" + key + "\":\"" + map.get(key) + "\",";
        }
        jsonStr = jsonStr.substring(0, jsonStr.length() - 1);
        jsonStr += "}";
        return jsonStr;
    }

    /**
     * 将JAVA的LIST转换成JSON字符串
     *
     * @param list
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    @SuppressWarnings("rawtypes")
    public static String simpleListToJsonStr(List<Map<?, ?>> list)
            throws IllegalArgumentException, IllegalAccessException {
        if (list == null || list.size() == 0) {
            return "[]";
        }
        String jsonStr = "[";
        for (Map<?, ?> map : list) {
            jsonStr += simpleMapToJsonStr(map) + ",";
        }
        jsonStr = jsonStr.substring(0, jsonStr.length() - 1);
        jsonStr += "]";
        return jsonStr;
    }

    /**
     * 将订单执行的相关信息转换为json字符串，以便保存到草稿箱
     *
     * @param strs
     * @return
     */

    public static ArrayList<String> strs2List(String strs) {
        ArrayList<String> list = new ArrayList<String>();
        try {
            if (!TextUtils.isEmpty(strs)) {
                String[] array = strs.split(",");
                for (String img : array) {
                    list.add(img);
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

        return list;
    }

    public static String list2Strs(List<String> list) {
        if (list == null || list.size() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String str : list) {
            sb.append(str).append(",");
        }
        String orders = sb.toString();
        orders = orders.substring(0, orders.length() - 1);
        Log.e(TAG, "list2Strs=" + orders);
        return orders;
    }

    /**
     * @author i-zqluo 解析从网上获取的天气信息的工具类
     */
    public static final class WeaterInfoParser {

        /**
         * 通过解析content，得到一个一维为城市编号，二维为城市名的二维数组 解析的字符串的形式为:
         * <code>编号|城市名,编号|城市名,.....</code>
         *
         * @param content 需要解析的字符串
         * @return 封装有城市编码与名称的二维数组
         */
        public static String[][] parseCity(String content) {
            // 判断content不为空
            if (content != null && content.trim().length() != 0) {
                StringTokenizer st = new StringTokenizer(content, ",");
                int count = st.countTokens();
                String[][] citys = new String[count][2];
                int i = 0, index = 0;
                while (st.hasMoreTokens()) {
                    String city = st.nextToken();
                    index = city.indexOf('|');
                    citys[i][0] = city.substring(0, index);
                    citys[i][1] = city.substring(index + 1);
                    i = i + 1;
                }
                return citys;
            }
            return null;
        }
    }

    /**
     * 根据给定的url地址访问网络，得到响应内容(这里为GET方式访问)
     *
     * @param urlStr 指定的url地址
     * @return web服务器响应的内容
     */
    public static String getWebContent(String urlStr) {
        URL url = null;
        StringBuilder sb = new StringBuilder();
        try {
            try {
                url = new URL(urlStr);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            BufferedReader r = null;
            try {
                URLConnection con = url.openConnection();
                r = new BufferedReader(new InputStreamReader(
                        con.getInputStream(), "UTF-8"));
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            String str = "";
            do {
                try {
                    if (r == null) {
                        return null;
                    }
                    str = r.readLine();
                    sb.append(str);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } while (str != null);
            try {
                r.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
        return sb.toString();
    }

    public static void start_activity(Activity activity, Class<?> cls,
                                      BasicNameValuePair... name) {
        Intent intent = new Intent();
        intent.setClass(activity, cls);
        for (int i = 0; i < name.length; i++) {
            intent.putExtra(name[i].getName(), name[i].getValue());
        }
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.push_left_in,
                R.anim.push_left_out);
    }

    public static void start_activityForResult(Activity activity, Class<?> cls,
                                               int requestCode, BasicNameValuePair... name) {
        Intent intent = new Intent();
        intent.setClass(activity, cls);
        for (int i = 0; i < name.length; i++) {
            intent.putExtra(name[i].getName(), name[i].getValue());
        }
        activity.startActivityForResult(intent, requestCode);
        activity.overridePendingTransition(R.anim.push_left_in,
                R.anim.push_left_out);
    }

    public static void operateLogUpload(final Context context) {
        if (!UtilsAndroid.Set.checkNetState(context))
            return;
        DBManager manager = new DBManager(context);
        List<OperateLog> list = manager.getAllOpreate();
        manager.putLockOpreate();
        manager.closeDatabase();
        Log.i(TAG, list.toString());
        if (null == list || list.size() == 0) {
            return;
        }
        JSONArray jsonArray = getJsonArrayOperate(context, list);
        // Log.d(TAG, jsonArray.toString());
        StringEntity stringEntity = null;
        try {
            stringEntity = new StringEntity(jsonArray.toString(), "utf-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(context, AsyncHttpService.BASE_URL + "/upBatchOpeLog",
                stringEntity, "application/json",
                new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable,
                                errorResponse);
                        DBManager manager = new DBManager(context);
                        manager.clearLockOpreate();
                        manager.closeDatabase();
                    }

                    @Override
                    public void onCancel() {
                        super.onCancel();
                        DBManager manager = new DBManager(context);
                        manager.clearLockOpreate();
                        manager.closeDatabase();

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        LogUtil.d(response.toString());
                        DBManager manager = new DBManager(context);
                        try {

                            int errCode = response
                                    .getInt(Constants.Feild.KEY_ERROR_CODE);
                            if (errCode == 0) {
                                manager.deleteAllOpreate();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            manager.clearLockOpreate();
                            manager.closeDatabase();
                        }

                    }
                });
    }

    private static JSONArray getJsonArrayOperate(Context context,
                                                 List<OperateLog> list) {
        JSONArray jsonArray = new JSONArray();
        DBManager manager = new DBManager(context);
        try {
            for (OperateLog log : list) {
                if (log.getCmpid().length() > 4) {
                    Log.e(TAG, log.getCmpid().length() + ">>" + log.getCmpid());
                    // 解决有单引号的BUG --'8888'
                    log.setCmpid(log.getCmpid().replace("'", "").trim());
                    if (log.getCmpid().length() > 4) {
                        manager.deleteLocateById(log.getId());
                        continue;
                    }
                }
                JSONObject jsonObject = new JSONObject();
                // private String id;

                jsonObject.put("id", log.getId());
                // private String cmpid;
                jsonObject.put("cmpid", log.getCmpid());
                // private String itemid;
                jsonObject.put("itemid", log.getItemid());
                // private String pid;
                jsonObject.put("pid", log.getPid());
                // private String loctel;
                jsonObject.put("loctel", log.getLoctel());
                // private String opecont;
                jsonObject.put("opecont", log.getOpecont());
                // private String opecont;
                jsonObject.put("opetime", log.getOpetime());
                // private Integer isworking;
                jsonObject.put("isworking", log.getIsworking());
                // private Integer gpsstatus;
                jsonObject.put("gpsstatus", log.getGpsstatus());
                // private Integer gprsstatus;
                jsonObject.put("gprsstatus", log.getGprsstatus());
                // private Integer wifistatus;
                jsonObject.put("wifistatus", log.getWifistatus());
                // private Integer electricity;
                jsonObject.put("electricity", log.getElectricity());

                jsonArray.put(jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            manager.closeDatabase();
        }
        return jsonArray;
    }

    // private static Dialog mLoadDialog;
    //
    // /**
    // * check update
    // *
    // * @param context
    // */
    // public static void checkUpdate(final Context context) {
    // mLoadDialog = DialogUtil.createLoadingDialog(context, context
    // .getString(R.string.downloading_update_please_later_ellipsis),
    // null);
    // final boolean forceUpdate = false;
    // MobclickAgent.updateOnlineConfig(context);
    // UmengUpdateAgent.setUpdateOnlyWifi(false);
    // UmengUpdateAgent.setUpdateAutoPopup(false);
    // // 从友盟读取在线参数配置信息
    // String update_mode = MobclickAgent.getConfigParams(context,
    // "upgrade_mode");
    // LogUtil.d(TAG, "upgrade_mode:" + update_mode);
    //
    // if (!TextUtils.isEmpty(update_mode)) {
    // String[] params = update_mode.split(",");
    // if (params.length == 2 && "F".equalsIgnoreCase(params[1])) {
    // // F 表示强制更新
    // forceUpdate = true;
    // }
    // }
    //
    // UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
    //
    // @Override
    // public void onUpdateReturned(int updateStatus,
    // UpdateResponse updateInfo) {
    // if (context == null) {
    // return;
    // }
    // switch (updateStatus) {
    // case UpdateStatus.Yes: // has update
    // if ("v1.0.1".equals(updateInfo.version)
    // && mLoadDialog != null) {
    // mLoadDialog.show();
    // UmengUpdateAgent.startDownload(context, updateInfo);
    // } else {
    // UmengUpdateAgent.showUpdateDialog(context, updateInfo);
    // }
    // break;
    // case UpdateStatus.No: // has no update
    // Toast.makeText(context,
    // context.getString(R.string.updated_latest_version),
    // Toast.LENGTH_SHORT).show();
    //
    // break;
    // default:
    // Toast.makeText(context,
    // context.getString(R.string.updated_latest_version),
    // Toast.LENGTH_SHORT).show();
    // break;
    // }
    // }
    //
    // });
    //
    // // 对话框监听
    // UmengUpdateAgent.setDialogListener(new UmengDialogButtonListener() {
    // @Override
    // public void onClick(int status) {
    // switch (status) {
    // case UpdateStatus.Update:
    // if (null != mLoadDialog)
    // mLoadDialog.show();
    // break;
    // default:
    // if (forceUpdate) {
    // Toast.makeText(
    // context,
    // context.getString(R.string.umeng_update_tip_msg),
    // Toast.LENGTH_SHORT).show();
    // ActivityStackManager.getStackManager()
    // .popAllActivitys();
    //
    // } else {
    //
    // }
    // break;
    // }
    // }
    // });
    //
    // // 设置下载监听
    // UmengUpdateAgent.setDownloadListener(new UmengDownloadListener() {
    //
    // @Override
    // public void OnDownloadUpdate(int arg0) {
    //
    // }
    //
    // @Override
    // public void OnDownloadStart() {
    //
    // }
    //
    // @Override
    // public void OnDownloadEnd(int arg0, String arg1) {
    // switch (arg0) {
    // case UpdateStatus.DOWNLOAD_COMPLETE_FAIL:
    // showLongToast(R.string.download_fail);
    // mLoadDialog.dismiss();
    // break;
    // case UpdateStatus.DOWNLOAD_COMPLETE_SUCCESS:
    // showLongToast(R.string.download_success);
    // UmengUpdateAgent.startInstall(SystemSettingActivity.this,
    // new File(arg1));
    // // System.exit(0);
    // ActivityStackManager.getStackManager().popAllActivitys();
    // break;
    // case UpdateStatus.DOWNLOAD_NEED_RESTART:
    //
    // break;
    // default:
    // break;
    // }
    // }
    // });
    // UmengUpdateAgent.update(this);
    // }

    // //////////
    public static boolean isTestAccount() {
        String tel = PreferencesUtil.getValue(PreferencesUtil.KEY_USER_TEL);
        return MainApplication.mAccounts.contains(tel);
    }

}
