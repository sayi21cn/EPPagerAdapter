package com.alextam.eppageradapter.common;

import android.app.Application;
import android.content.SharedPreferences;

/**
 * Created by Alex Tam on 2015/4/17.
 */
public class MyApplication extends Application {
    private SharedPreferences cachePreferences;

    private String advertiseStr;

    /**焦点图的本地链接数据
     * <br>格式: 焦点图本地保存地址@@焦点图本地文件名@@焦点图下载链接&&&焦点图本地保存地址@@焦点图本地文件名@@焦点图下载链接...
     **/
    private  String advPathInfo;


    @Override
    public void onCreate()
    {
        super.onCreate();
        init();
    }

    private void init()
    {
        cachePreferences = getSharedPreferences("adv_preferences", MODE_PRIVATE);
        advertiseStr = cachePreferences.getString("adv_str", "");
        advPathInfo = cachePreferences.getString("advpath_info_str", "");

    }

    public String getAdvertiseStr() {
        return advertiseStr;
    }

    /**
     * 保存按既定格式拼接好的焦点图数据
     * @param advertiseStr
     */
    public void setAdvertiseStr(String advertiseStr) {
        this.advertiseStr = advertiseStr;
        cachePreferences.edit().putString("adv_str", this.advertiseStr).commit();
    }

    public String getAdvPathInfo() {
        return advPathInfo;
    }

    /**
     * 保存文件信息
     * @param advPathInfo
     */
    public void setAdvPathInfo(String advPathInfo) {
        this.advPathInfo = advPathInfo;
        cachePreferences.edit().putString("advpath_info_str", this.advPathInfo).commit();
    }

}
