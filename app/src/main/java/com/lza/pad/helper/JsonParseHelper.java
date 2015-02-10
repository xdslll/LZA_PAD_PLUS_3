package com.lza.pad.helper;

import com.google.gson.reflect.TypeToken;
import com.lza.pad.db.model.ResponseData;
import com.lza.pad.db.model.pad.PadDeviceInfo;
import com.lza.pad.db.model.pad.PadLayoutModule;
import com.lza.pad.db.model.pad.PadModuleControl;

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
        Type type = new TypeToken<ResponseData>(){}.getType();
        return GsonHelper.instance().fromJson(json, type);
    }

    public static ResponseData<PadDeviceInfo> parseDeviceInfoResponse(String json) {
        Type type = new TypeToken<ResponseData<PadDeviceInfo>>(){}.getType();
        return GsonHelper.instance().fromJson(json, type);
    }

    public static ResponseData<PadLayoutModule> parseDeviceLayoutResponse(String json) {
        Type type = new TypeToken<ResponseData<PadLayoutModule>>(){}.getType();
        return GsonHelper.instance().fromJson(json, type);
    }

    public static ResponseData<PadModuleControl> parseModuleControlResponse(String json) {
        Type type = new TypeToken<ResponseData<PadModuleControl>>(){}.getType();
        return GsonHelper.instance().fromJson(json, type);
    }
}
