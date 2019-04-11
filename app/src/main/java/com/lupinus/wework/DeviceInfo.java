package com.lupinus.wework;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.DisplayMetrics;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by luyufa on 2019/4/10.
 */

public class DeviceInfo {

    public static String[] valueArr = new String[]{"getDeviceId", "getString", "getString2", "getLine1Number", "getSimSerialNumber", "getSubscriberId", "getSimCountryIso", "getSimOperator", "getSimOperatorName", "getNetworkCountryIso", "getNetworkOperator", "getNetworkOperatorName", "getNetworkType", "getPhoneType", "getSimState", "getMacAddress", "getSSID", "getBSSID", "RELEASE", "SDK", "ARCH", "getMetrics", "getRadioVersion", "BRAND", "MODEL", "PRODUCT", "MANUFACTURER", "getJiZhan", "getCpuName", "connectType", "getRadioVersion"};
    public static String[] valueArrName = new String[]{"序列号", "android_id", "android_id第二种方式", "手机号码", "手机卡序列号", "IMSI", "手机卡国家", "运营商", "运营商名字", "国家iso代码", "网络类型", "网络类型名", "网络类型", "手机类型", "手机卡状态", "mac地址", "无线路由器名", "无线路由器地址", "系统版本", "系统版本值", "系统架构", "屏幕分辨率", "固件版本", "品牌", "型号", "产品名", "制造商", "基站信息", "CPU名字", "联网方式", "固件版本1"};
    private static HashMap<String, Object> valueMap = new HashMap();


    public static String getDeviceInfo(Activity activity) {
        getAllData(activity);
        String data = "";
        for (Map.Entry<String, Object> item : valueMap.entrySet()) {
            String key = item.getKey();
            Object value = item.getValue();
            for (int i = 0; i < valueArr.length; i++) {
                if (key.equals(valueArr[i])) {
                    key = valueArrName[i];
                }
            }
            data = new StringBuilder(String.valueOf(data)).append(key).append(" : ").append(value).append("\n").toString();
        }

        return data;
    }


    @SuppressLint("MissingPermission")
    private static void getAllData(Activity activity) {
        Class cl;
        Object invoker;
        @SuppressLint("WrongConstant")
        TelephonyManager tele = (TelephonyManager) activity.getSystemService("phone");
        valueMap.put("getDeviceId", tele.getDeviceId());
        valueMap.put("getLine1Number", tele.getLine1Number());
        valueMap.put("getSimSerialNumber", tele.getSimSerialNumber());
        valueMap.put("getSubscriberId", tele.getSubscriberId());
        valueMap.put("getSimCountryIso", tele.getSimCountryIso());
        valueMap.put("getSimOperator", tele.getSimOperator());
        valueMap.put("getSimOperatorName", tele.getSimOperatorName());
        valueMap.put("getNetworkCountryIso", tele.getNetworkCountryIso());
        valueMap.put("getNetworkOperator", tele.getNetworkOperator());
        valueMap.put("getNetworkOperatorName", tele.getNetworkOperatorName());
        valueMap.put("getNetworkType", Integer.valueOf(tele.getNetworkType()));
        valueMap.put("getPhoneType", Integer.valueOf(tele.getPhoneType()));
        valueMap.put("getSimState", Integer.valueOf(tele.getSimState()));
        valueMap.put("getRadioVersion", Build.getRadioVersion());

        @SuppressLint("WrongConstant") WifiManager wifi = (WifiManager) activity.getSystemService("wifi");
        valueMap.put("getMacAddress", wifi.getConnectionInfo().getMacAddress());
        valueMap.put("getSSID", wifi.getConnectionInfo().getSSID());
        valueMap.put("getBSSID", wifi.getConnectionInfo().getBSSID());
        valueMap.put("getString", Settings.Secure.getString(activity.getContentResolver(), "android_id"));
        valueMap.put("getString2", Settings.System.getString(activity.getContentResolver(), "android_id"));
        try {
            cl = Class.forName("android.os.SystemProperties");
            invoker = cl.newInstance();
            valueMap.put("get", cl.getMethod("get", new Class[]{String.class, String.class}).invoke(invoker, new Object[]{"gsm.version.baseband", "no message"}));
        } catch (Exception e) {
        }
        try {
            cl = Class.forName("android.os.SystemProperties");
            invoker = cl.newInstance();
            valueMap.put("getRadioVersion1", cl.getMethod("get", new Class[]{String.class}).invoke(invoker, new Object[]{"gsm.version.baseband"}));
        } catch (Exception e2) {
        }
        CellLocation location1 = tele.getCellLocation();
        String locationStr = "";
        int lac;
        if (location1 instanceof GsmCellLocation) {
            GsmCellLocation location = (GsmCellLocation) location1;
            lac = location.getLac();
            locationStr = new StringBuilder(String.valueOf(lac)).append("_").append(location.getCid()).toString();
        } else if (location1 instanceof CdmaCellLocation) {
            CdmaCellLocation location2 = (CdmaCellLocation) tele.getCellLocation();
            lac = location2.getNetworkId();
            locationStr = new StringBuilder(String.valueOf(lac)).append("_").append(location2.getBaseStationId()).toString();
        }
        valueMap.put("getJiZhan", new StringBuilder(String.valueOf(locationStr)).append("_").append(tele.getCallState()).toString());
        valueMap.put("RELEASE", Build.VERSION.RELEASE);
        valueMap.put("SDK", Build.VERSION.SDK);
        valueMap.put("ARCH", Build.CPU_ABI + "_" + Build.CPU_ABI2);
        valueMap.put("BRAND", Build.BRAND);
        valueMap.put("MODEL", Build.MODEL);
        valueMap.put("PRODUCT", Build.PRODUCT);
        valueMap.put("MANUFACTURER", Build.MANUFACTURER);
        valueMap.put("DEVICE", Build.DEVICE);
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        valueMap.put("getMetrics", dm.widthPixels + "x" + dm.heightPixels + " density: " + dm.density + " densityDpi: " + dm.densityDpi + " scaledDensity: " + dm.scaledDensity + " xdpi: " + dm.xdpi + " ydpi: " + dm.ydpi);
        @SuppressLint("WrongConstant") NetworkInfo info = ((ConnectivityManager) activity.getSystemService("connectivity")).getActiveNetworkInfo();
        String str = "";
        if (info != null) {
            str = info.getTypeName();
        } else {
            str = "网络未连接";
        }
        try {
            cl = Class.forName("android.os.SystemProperties");
            invoker = cl.newInstance();
            str = new StringBuilder(String.valueOf(str)).append("_状态：").append(cl.getMethod("get", new Class[]{String.class}).invoke(invoker, new Object[]{"gsm.sim.state"})).toString();
        } catch (Exception e3) {
        }
        if (tele.hasIccCard()) {
            str = new StringBuilder(String.valueOf(str)).append("——有sim卡").toString();
        } else {
            str = new StringBuilder(String.valueOf(str)).append("——无sim卡").toString();
        }
        valueMap.put("connectType", str);
    }

}
