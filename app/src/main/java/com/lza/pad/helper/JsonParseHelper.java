package com.lza.pad.helper;

import com.google.gson.reflect.TypeToken;
import com.lza.pad.db.model.ResponseData;
import com.lza.pad.db.model.pad.PadDeviceInfo;

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

    public static ResponseData<PadDeviceInfo> parseDeviceResponse(String json) {
        Type type = new TypeToken<ResponseData<PadDeviceInfo>>(){}.getType();
        return GsonHelper.instance().fromJson(json, type);
    }
}
