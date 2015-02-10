package com.lza.pad.helper;

import android.text.TextUtils;

import com.lza.pad.app.base.MainApplication;
import com.lza.pad.db.model.pad.PadDeviceInfo;
import com.lza.pad.db.model.pad.PadLayoutModule;
import com.lza.pad.support.utils.Consts;
import com.lza.pad.support.utils.UniversalUtility;

import java.util.HashMap;
import java.util.Map;

/**
 * 业务类，用于生成Url和Url参数
 *
 * @author xiads
 * @Date 15/1/22.
 */
public class UrlHelper implements Consts {

    public static final String PAR_CONTROL = "control";

    public static final String PAR_MAC_ADDRESS = "mac";

    public static final String PAR_LAYOUT_ID = "layout_id";

    public static final String PAR_MODULE_ID = "model_id";

    public static final String PAR_UPDATE_TAG = "update_tag";

    public static final String PAR_DEVICE_ID = "bh";



    public static String generateUrl(Map<String, String> par) {
        String param = UniversalUtility.encodeUrl(par);
        StringBuilder builder = new StringBuilder();
        String defaultUrl = MainApplication.getInstance().getUrl();
        builder.append(defaultUrl).append(param);
        return builder.toString();
    }

    public static Map<String, String> getDevicePar(String macAddress) {
        Map<String, String> par = new HashMap<String, String>();
        par.put(PAR_CONTROL, CONTROL_GET_DEVICE_INFO);
        par.put(PAR_MAC_ADDRESS, macAddress);
        return par;
    }

    /**
     * 生成获取设备信息的Url
     *
     * @param macAddress    设备的mac地址
     * @return
     */
    public static String getDeviceUrl(String macAddress) {
        return generateUrl(getDevicePar(macAddress));
    }

    public static Map<String, String> getLayoutModulePar(PadDeviceInfo deviceInfo) {
        Map<String, String> par = new HashMap<String, String>();
        par.put(PAR_CONTROL, CONTROL_GET_LAYOUT_MODULE);
        par.put(PAR_LAYOUT_ID, deviceInfo.getModule_ids());
        par.put(PAR_MAC_ADDRESS, deviceInfo.getMac_add());
        return par;
    }

    /**
     * 生成布局对应模块的Url
     *
     * @param deviceInfo    设备信息
     * @return
     */
    public static String getLayoutModuleUrl(PadDeviceInfo deviceInfo) {
        return generateUrl(getLayoutModulePar(deviceInfo));
    }

    public static Map<String, String> getModuleControlPar(PadDeviceInfo deviceInfo, PadLayoutModule deviceLayout) {
        Map<String, String> par = new HashMap<String, String>();
        par.put(PAR_CONTROL, CONTROL_GET_MODULE_CONTROL);
        par.put(PAR_MODULE_ID, deviceLayout.getModule_id());
        par.put(PAR_MAC_ADDRESS, deviceInfo.getMac_add());
        return par;
    }

    /**
     * 生成模块对应控件的Url
     *
     * @param deviceInfo    设备信息
     * @param deviceLayout  设备布局
     * @return
     */
    public static String getModuleControlUrl(PadDeviceInfo deviceInfo, PadLayoutModule deviceLayout) {
        return generateUrl(getModuleControlPar(deviceInfo, deviceLayout));
    }

    public static Map<String, String> updateDeviceInfoPar(PadDeviceInfo deviceInfo) {
        Map<String, String> par = new HashMap<String, String>();
        par.put(PAR_CONTROL, CONTROL_UPDATE_DEVICE_INFO);
        par.put(PAR_UPDATE_TAG, deviceInfo.getUpdate_tag());
        par.put(PAR_MAC_ADDRESS, deviceInfo.getMac_add());
        par.put(PAR_DEVICE_ID, deviceInfo.getBh());
        return par;
    }

    public static String updateDeviceInfoUrl(PadDeviceInfo deviceInfo) {
        return generateUrl(updateDeviceInfoPar(deviceInfo));
    }

    /**
     * 解析Url中的control参数
     *
     * @param url
     * @return
     */
    public static String parseControl(String url) {
        String control = "";
        if (TextUtils.isEmpty(url)) return control;
        int index = url.indexOf("control=");
        if (index > 0) {
            control = url.split("control=")[1];
            if (!TextUtils.isEmpty(control))
                control = control.split("&")[0];
        }
        return control;
    }

}
