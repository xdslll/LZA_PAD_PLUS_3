package com.lza.pad.support.utils;

import android.app.Activity;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 11/14/14.
 */
public interface Consts {

    public static final String KEY_MAP_FUNC_TEXT = "key_map_func_title";
    public static final String KEY_MAP_TITLE = "key_map_title";
    public static final String KEY_MAP_INDEX = "key_map_index";
    public static final String KEY_URL = "key_url";

    public static final String KEY_CURRENT_SUBJECT = "key_current_subject";
    public static final String KEY_SUBJECT_DATA = "key_subject_data";
    public static final String KEY_EBOOK_NUM_COLUMNS = "key_ebook_num_columns";
    public static final String KEY_IF_HOME = "key_if_home";
    public static final String KEY_FRAGMENT_WIDTH = "fragment_width";
    public static final String KEY_FRAGMENT_HEIGHT = "fragment_height";

    public static final String GLOBAL_TYPE_SCHOOL = "School";
    public static final String GLOBAL_TYPE_RUN_TIME = "Runtime";

    public static final String INTENT_ACTION_RESPONSE_OK = "com.lza.pad.receiver.RESPONSE_OK";
    public static final String INTENT_ACTION_RESPONSE_EMPTY = "com.lza.pad.receiver.RESPONSE_EMPTY";
    public static final String INTENT_ACTION_RESPONSE_ERROR = "com.lza.pad.receiver.RESPONSE_ERROR";
    public static final String INTENT_ACTION_RESPONSE_RECEIVER = "com.lza.pad.receiver.RESPONSE_RECEIVER";
    public static final String INTENT_ACTION_API_SERVICE = "com.lza.pad.service.API_SERVICE";
    public static final String INTENT_ACTION_NEW_API_SERVICE = "com.lza.pad.service.NEW_API_SERVICE";
    public static final String INTENT_ACTION_BROWER = "android.intent.action.VIEW";

    public static final String KEY_RESPONSE_CODE = "response_code";
    public static final String KEY_COMMON_RESPONSE = "common_response";
    public static final String API_PARAM_TYPE_COMMON = "Common";

    public static final String CACHE_IMG_PATH = "/lza/weixin";

    public static final long ONE_DAY = 24 * 60 * 60 * 1000;
    public static final long ONE_HOUR = 60 * 60 * 1000;
    public static final long ONE_MINUTE = 60 * 1000;
    public static final long ONE_SECOND = 1000;

    public static final String SP_NAME = "lza_weixin_pref";
    public static final int SP_MODE = Activity.MODE_PRIVATE;
}
