package com.lza.pad.helper;

import com.lza.pad.app.base.MainApplication;
import com.lza.pad.db.model.pad.PadDeviceInfo;
import com.lza.pad.support.utils.UniversalUtility;

import java.util.HashMap;
import java.util.Map;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/1/22.
 */
public class UrlHelper {

    public static String generateUrl(Map<String, String> par) {
        String param = UniversalUtility.encodeUrl(par);
        StringBuilder builder = new StringBuilder();
        String defaultUrl = MainApplication.getInstance().getUrl();
        builder.append(defaultUrl).append(param);
        return builder.toString();
    }

    public static Map<String, String> getDevicePar(String macAddress) {
        Map<String, String> par = new HashMap<String, String>();
        par.put("control", "get_pad_model");
        par.put("mac", macAddress);
        return par;
    }

    public static Map<String, String> getLayoutPar(PadDeviceInfo deviceInfo) {
        Map<String, String> par = new HashMap<String, String>();
        par.put("control", "get_device_layout");
        par.put("layout_id", deviceInfo.getModule_ids());
        return par;
    }

}
