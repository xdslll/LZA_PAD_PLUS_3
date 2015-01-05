package com.lza.pad.db.facade;

import android.content.Context;

import com.lza.pad.db.dao.GlobalSettingsDao;
import com.lza.pad.db.model.GlobalSettings;
import com.lza.pad.support.utils.Consts;

import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 10/22/14.
 */
public class GlobalSettingsFacade implements Consts {

    private static GlobalSettingsDao mInstance;

    private static GlobalSettingsDao getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new GlobalSettingsDao(context);
        }
        return mInstance;
    }

    public static void init() {
        mInstance.init();
    }

    /**
     * 查询所有学校的名称
     *
     * @param context
     * @return
     */
    public static List<GlobalSettings> loadAllSchool(Context context) {
        return getInstance(context).queryByType(GLOBAL_TYPE_SCHOOL);
    }

    /**
     * 获取当前选中的学校
     *
     * @param context
     * @return
     */
    public static GlobalSettings loadCurrentSchoolCode(Context context) {
        return getInstance(context).queryByTypeAndName(GLOBAL_TYPE_RUN_TIME, "school");
    }

    public static List<GlobalSettings> loadSchoolInfo(Context context, String schoolCode) {
        return getInstance(context).queryByType(schoolCode);
    }
}
