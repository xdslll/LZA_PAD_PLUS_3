package com.lza.pad.helper;

import com.google.gson.reflect.TypeToken;
import com.lza.pad.db.model.ResponseData;
import com.lza.pad.db.model.douban.DoubanBook;
import com.lza.pad.db.model.douban.DoubanReview;
import com.lza.pad.db.model.pad.PadAuthority;
import com.lza.pad.db.model.pad.PadDeviceInfo;
import com.lza.pad.db.model.pad.PadModuleSwitching;
import com.lza.pad.db.model.pad.PadModuleWidget;
import com.lza.pad.db.model.pad.PadSceneModule;
import com.lza.pad.db.model.pad._old.PadImageCollection;
import com.lza.pad.db.model.pad._old.PadLayoutModule;
import com.lza.pad.db.model.pad._old.PadModuleControl;
import com.lza.pad.db.model.pad.PadResource;
import com.lza.pad.db.model.pad.PadScene;
import com.lza.pad.db.model.pad.PadSceneSwitching;
import com.lza.pad.db.model.pad.PadSchool;
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

    public static ResponseData<PadDeviceInfo> parseDeviceInfoResponse(String json) {
        try {
            Type type = new TypeToken<ResponseData<PadDeviceInfo>>() {}.getType();
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

    public static ResponseData<PadScene> parseDeviceSceneResponse(String json) {
        try {
            Type type = new TypeToken<ResponseData<PadScene>>() {}.getType();
            return GsonHelper.instance().fromJson(json, type);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static ResponseData<PadSchool> parsePadSchoolResponse(String json) {
        try {
            Type type = new TypeToken<ResponseData<PadSchool>>() {}.getType();
            return GsonHelper.instance().fromJson(json, type);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static ResponseData<PadAuthority> parsePadAuthorityResponse(String json) {
        try {
            Type type = new TypeToken<ResponseData<PadAuthority>>() {}.getType();
            return GsonHelper.instance().fromJson(json, type);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static ResponseData<PadSceneSwitching> parsePadSceneSwitchingResponse(String json) {
        try {
            Type type = new TypeToken<ResponseData<PadSceneSwitching>>() {}.getType();
            return GsonHelper.instance().fromJson(json, type);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static ResponseData<PadSceneModule> parseSceneModulesResponse(String json) {
        try {
            Type type = new TypeToken<ResponseData<PadSceneModule>>() {}.getType();
            return GsonHelper.instance().fromJson(json, type);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static ResponseData<PadModuleWidget> parseModuleWidgetsResponse(String json) {
        try {
            Type type = new TypeToken<ResponseData<PadModuleWidget>>() {}.getType();
            return GsonHelper.instance().fromJson(json, type);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static ResponseData<PadModuleSwitching> parsePadModuleSwitchingResponse(String json) {
        try {
            Type type = new TypeToken<ResponseData<PadModuleSwitching>>() {}.getType();
            return GsonHelper.instance().fromJson(json, type);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
