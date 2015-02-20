package com.lza.pad.support.utils;

import android.app.Activity;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 11/14/14.
 */
public interface Consts {

    /**
     * Url参数中的Control参数，必须和服务端的接口参数一一对应
     * 设备与模块的关系为：
     * 设备(1) -> 布局(1) -> 模块(n) -> 控件(n*n)
     * 一台设备对应一套布局，布局可以在管理平台随时更换，大的布局切换后，Pad的整体显示内容将变更
     * 一套布局对应若干模块，每个模块可以在管理平台编辑，模块表示向Pad提供的功能
     * 一个模块对应若干控件，每个控件可以在管理平台编辑，控件表示一个模块下具体展示哪些数据
     *
     * ----------       ----------       ----------
     * |        |       |        |       |        | -> control
     * |        |       |        |       | module | -> control
     * |        |       |        |       |        | -> control
     * |        |  ---\ |        | ---\  ----------
     * | device |  ---/ | layout | ---/
     * |        |       |        |       ----------
     * |        |       |        |       |        | -> control
     * |        |       |        |       | module | -> control
     * |        |       |        |       |        | -> control
     * ----------       ----------       ----------
 */

    /**
     * 获取设备信息
     */
    public static final String CONTROL_GET_DEVICE_INFO = "get_device_info";

    /**
     * 获取设备布局
     */
    public static final String CONTROL_GET_LAYOUT_MODULE = "get_layout_module";

    /**
     * 获取模块下的所有控件
     */
    public static final String CONTROL_GET_MODULE_CONTROL = "get_module_control";

    /**
     * 更新设备信息
     */
    public static final String CONTROL_UPDATE_DEVICE_INFO = "update_device_info";

    /**
     * 重要接口：获取数据源
     */
    public static final String CONTROL_GET_MESSAGE = "get_message";

    /**
     * 获取数据源的详细内容
     */
    public static final String CONTROL_OPAC_SEARCH_DETAIL = "opac_search_detail";

    /**
     * 获取图片
     */
    public static final String CONTROL_GET_IMAGE = "get_image";


    public static final String KEY_CURRENT_MODULE_INDEX = "key_current_module_index";
    public static final String KEY_FRAGMENT_INDEX = "key_fragment_index";
    public static final String KEY_DOUBAN_BOOK = "key_douban_book";
    public static final String KEY_PAD_RESOURCE_INFO = "key_pad_resource";
    public static final String KEY_PAD_CONTROL_INFO = "key_pad_control";
    public static final String KEY_PAD_MODULE_INFO = "key_pad_module";
    public static final String KEY_PAD_MODULE_INFOS = "key_pad_modules";
    public static final String KEY_PAD_SCHOOL = "key_pad_school";
    public static final String KEY_PAD_DEVICE_INFO = "key_pad_device";
    public static final String KEY_DEVICE_IS_UPDATING = "key_device_is_updating";
    public static final String KEY_UPDATE_DEVICE_IS_RUNNING = "key_update_device_is_running";

    public static final String ACTION_UPDATE_DEVICE_CALLBACK = "LZA_PAD_UPDATE_CALLBACK";
    public static final String ACTION_UPDATE_DEVICE_RECEIVER = "LZA_PAD_UPDATE_DEVICE_RECEIVER";
    public static final String ACTION_UPDATE_DEVICE_SERVICE = "LZA_PAD_UPDATE_DEVICE_SERIVCE";
    public static final String ACTION_MINA_SERVICE = "LZA_PAD_MINA_SERVICE";
    public static final String KEY_MINA_SERVER_ACTION = "key_mina_server_action";

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

    public static final String KEY_COOKIE = "cookie";
    public static final String KEY_RESPONSE_CODE = "response_code";
    public static final String KEY_COMMON_RESPONSE = "common_response";
    public static final String API_PARAM_TYPE_COMMON = "Common";

    public static final String CACHE_IMG_PATH = "/lza/pad";

    public static final long ONE_DAY = 24 * 60 * 60 * 1000;
    public static final long ONE_HOUR = 60 * 60 * 1000;
    public static final long ONE_MINUTE = 60 * 1000;
    public static final long ONE_SECOND = 1000;

    public static final String SP_NAME = "lza_pad_plus_pref";
    public static final String SP_UI_NAME = "lza_pad_plus_ui_pref";
    public static final int SP_MODE = Activity.MODE_PRIVATE;
    String ACTION_START_SERVER = "start_server";
    String ACTION_STOP_SERVER = "stop_server";

    public static final String TEMP_IMAGE_LOADER = "temp";

    public static final int MAX_DOUBAN_RATING = 5;

    public static final String IMG_JPEG = "jpg";
    public static final String IMG_PNG = "png";

    /**
     * 首页归属的模块编号为1
     */
    public static final int INDEX_HOME_MODULE = 1;
}
