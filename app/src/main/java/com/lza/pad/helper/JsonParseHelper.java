package com.lza.pad.helper;

import com.google.gson.reflect.TypeToken;
import com.lza.pad.db.model.ResponseData;
import com.lza.pad.db.model.douban.DoubanBook;
import com.lza.pad.db.model.douban.DoubanReview;
import com.lza.pad.db.model.pad._PadDeviceInfo;
import com.lza.pad.db.model.pad.PadImageCollection;
import com.lza.pad.db.model.pad.PadLayoutModule;
import com.lza.pad.db.model.pad.PadModuleControl;
import com.lza.pad.db.model.pad.PadResource;
import com.lza.pad.db.model.pad.PadVersionInfo;

import java.lang.reflect.Type;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 11/4/14.
 */
public class JsonParseHelper {

    /**
     * Sample Code
     */
    public static ResponseData parseSimpleResponse(String json) {
        try {
            Type type = new TypeToken<ResponseData>() {}.getType();
            return GsonHelper.instance().fromJson(json, type);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static ResponseData<_PadDeviceInfo> parseDeviceInfoResponse(String json) {
        try {
            Type type = new TypeToken<ResponseData<_PadDeviceInfo>>() {}.getType();
            return GsonHelper.instance().fromJson(json, type);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static ResponseData<PadLayoutModule> parseDeviceLayoutResponse(String json) {
        try {
            Type type = new TypeToken<ResponseData<PadLayoutModule>>() {}.getType();
            return GsonHelper.instance().fromJson(json, type);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static ResponseData<PadModuleControl> parseModuleControlResponse(String json) {
        try {
            Type type = new TypeToken<ResponseData<PadModuleControl>>() {}.getType();
            return GsonHelper.instance().fromJson(json, type);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static ResponseData<PadResource> parseResourceResponse(String json) {
        try {
            Type type = new TypeToken<ResponseData<PadResource>>() {}.getType();
            return GsonHelper.instance().fromJson(json, type);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static DoubanBook parseDoubanBook(String json) {
        try {
            Type type = new TypeToken<DoubanBook>() {}.getType();
            return GsonHelper.instance().fromJson(json, type);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static DoubanReview parseDoubanReviews(String json) {
        try {
            Type type = new TypeToken<DoubanReview>() {}.getType();
            return GsonHelper.instance().fromJson(json, type);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static ResponseData<PadImageCollection> parseImageCollectionResponse(String json) {
        try {
            Type type = new TypeToken<ResponseData<PadImageCollection>>() {}.getType();
            return GsonHelper.instance().fromJson(json, type);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static ResponseData<PadVersionInfo> pareseVersionInfo(String json) {
        try {
            Type type = new TypeToken<ResponseData<PadVersionInfo>>() {}.getType();
            return GsonHelper.instance().fromJson(json, type);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
