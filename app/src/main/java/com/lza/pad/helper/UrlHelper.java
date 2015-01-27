package com.lza.pad.helper;

import com.lza.pad.app.MainApplication;
import com.lza.pad.support.utils.UniversalUtility;

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

}
