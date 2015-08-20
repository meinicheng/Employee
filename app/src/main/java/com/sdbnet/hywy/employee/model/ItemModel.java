package com.sdbnet.hywy.employee.model;

/**
 * Created by Administrator on 2015/8/11.
 */
public class ItemModel {
    //      [
//        {"pname":"张宇","pid":"8891","itemid":"88","cmpname":"南华西集团","itemname":"南华西项目","cmpid":"NHXG"}
//        ,{"pname":"张宇","pid":"9001","itemid":"88","cmpname":"深圳市上丁网络科技有限公司","itemname":"上丁网络演示项目","cmpid":"8888"}]

    public String pname;
    public String pid;
    public String itemid;
    public String cmpname;
    public String itemName;
    public String cmpid;

    @Override
    public String toString() {
        return "ItemModel{" +
                "pname='" + pname + '\'' +
                ", pid='" + pid + '\'' +
                ", itemid='" + itemid + '\'' +
                ", cmpname='" + cmpname + '\'' +
                ", itemName='" + itemName + '\'' +
                ", cmpid='" + cmpid + '\'' +
                '}';
    }
}
