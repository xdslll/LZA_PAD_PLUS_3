package com.lza.pad.helper;

import android.text.TextUtils;

import com.lza.pad.app.base.MainApplication;
import com.lza.pad.db.model.pad.PadDeviceInfo;
import com.lza.pad.db.model.pad.PadLayoutModule;
import com.lza.pad.db.model.pad.PadResource;
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

    public static final String PAR_MODEL_ID = "model_id";

    public static final String PAR_UPDATE_TAG = "update_tag";

    public static final String PAR_DEVICE_CODE = "bh";

    public static final String PAR_SCHOOL_CODE = "school_bh";

    public static final String PAR_ID = "id";

    public static final String PAR_MODULE_ID = "module_id";

    public static final String PAR_MODULE_NAME = "module_name";

    public static final String PAR_PX = "px";

    public static final String PAR_KEYWORD = "keyword";

    public static final String PAR_SUBJECT = "subject";

    public static final String PAR_WIDGETS_ID = "widgets_id";

    public static final String PAR_SOURCE_TYPE = "source_type";

    public static final String PAR_TITLE = "title";

    public static final String PAR_CONTROL_TYPE = "control_type";

    public static final String PAR_CONTROL_INDEX = "control_index";

    public static final String PAR_CONTROL_HEIGHT = "control_height";

    public static final String PAR_CONTROL_NAME = "control_name";

    public static final String PAR_PAGE_SIZE = "pagesize";

    public static final String PAR_PAGE = "page";

    public static final String PAR_BH = "bh";


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
        par.put(PAR_MODEL_ID, deviceLayout.getModule_id());
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
        par.put(PAR_DEVICE_CODE, deviceInfo.getBh());
        return par;
    }

    public static String updateDeviceInfoUrl(PadDeviceInfo deviceInfo) {
        return generateUrl(updateDeviceInfoPar(deviceInfo));
    }

    public static String updateDeviceInfoUrl(PadDeviceInfo deviceInfo, String key, String value) {
        Map<String, String> par = new HashMap<String, String>();
        par.put(PAR_CONTROL, CONTROL_UPDATE_DEVICE_INFO);
        par.put(PAR_MAC_ADDRESS, deviceInfo.getMac_add());
        par.put(PAR_DEVICE_CODE, deviceInfo.getBh());
        par.put(key, value);
        return generateUrl(par);
    }

    public static String getResourcesUrl(PadDeviceInfo deviceInfo, String sourceType, int pageSize, int page) {
        Map<String, String> par = new HashMap<String, String>();
        par.put(PAR_CONTROL, CONTROL_GET_MESSAGE);
        par.put(PAR_MAC_ADDRESS, deviceInfo.getMac_add());
        par.put(PAR_PAGE, String.valueOf(page));
        par.put(PAR_PAGE_SIZE, String.valueOf(pageSize));
        par.put(PAR_SOURCE_TYPE, sourceType);
        return generateUrl(par);
    }

    public static String getResourceDetailUrl(PadDeviceInfo deviceInfo, PadResource resource) {
        Map<String, String> par = new HashMap<String, String>();
        par.put(PAR_CONTROL, CONTROL_OPAC_SEARCH_DETAIL);
        par.put(PAR_MAC_ADDRESS, deviceInfo.getMac_add());
        par.put(PAR_BH, resource.getBh());
        return generateUrl(par);
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

    /**
     * 豆瓣API
     */
    public static final String DOUBAN_URL_BOOK_BY_ISBN = "https://api.douban.com/v2/book/isbn/%s?apikey=022b1e3243fbff4a06815a96cbf3fdde";
    public static final String DOUBAN_URL_BOOK_BY_TAG = "https://api.douban.com/v2/book/search?%s&apikey=022b1e3243fbff4a06815a96cbf3fdde";
    public static final String DOUBAN_URL_BOOK_REVIEWS_BY_ISBN = "https://api.douban.com/v2/book/isbn/%s/reviews?apikey=022b1e3243fbff4a06815a96cbf3fdde";
    public static final String DOUBAN_URL_BOOK_TAGS_BY_ISBN = "https://api.douban.com/v2/book/isbn/%s/tags?apikey=022b1e3243fbff4a06815a96cbf3fdde";
    public static final String DOUBAN_EXCEPTION_BOOK_NOT_FOUND = "book_not_found";
    public static final String DOUBAN_EXCEPTION_REVIEW_NOT_FOUND = "review_not_found";
    public static final String DOUBAN_IMAGE = "Douban";

    /**
     * 豆瓣读书接口
     */
    public static String createDoubanBookByIsbnUrl(PadResource data) {
        String isbn = data.getIsbn();
        String url = String.format(DOUBAN_URL_BOOK_BY_ISBN, isbn);
        return url;
    }

    public static String createDoubanReviewsByIsbnUrl(PadResource data) {
        String isbn = data.getIsbn();
        String url = String.format(DOUBAN_URL_BOOK_REVIEWS_BY_ISBN, isbn);
        return url;
    }

    public static String createDoubanReviewsByIsbnUrl(PadResource data, String start, String count) {
        String isbn = data.getIsbn();
        String url = String.format(DOUBAN_URL_BOOK_REVIEWS_BY_ISBN, isbn);
        return new StringBuilder().append(url)
                .append("&start=").append(start)
                .append("&count=").append(count)
                .toString();
        //url = url + "&start=" + start + "&count=" + count;
        //return url;
    }

}
