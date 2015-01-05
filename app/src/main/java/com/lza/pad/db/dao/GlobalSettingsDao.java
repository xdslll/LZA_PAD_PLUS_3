package com.lza.pad.db.dao;

import android.content.Context;

import com.lza.pad.db.model.GlobalSettings;

import java.sql.SQLException;
import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 9/30/14.
 */
public class GlobalSettingsDao extends BaseDao<GlobalSettings, Integer> {

    public GlobalSettingsDao(Context context) {
        super(GlobalSettings.class, context);
    }

    public void init() {
        /*String[] types = mContext.getResources().getStringArray(R.array.global_settings_type);
        String[] names = mContext.getResources().getStringArray(R.array.global_settings_name);
        String[] cnNames = mContext.getResources().getStringArray(R.array.global_settings_cnname);
        String[] values = mContext.getResources().getStringArray(R.array.global_settings_value);

        for (int i = 0; i < types.length; i++) {
            GlobalSettings settings = new GlobalSettings();
            settings.setType(types[i]);
            settings.setName(names[i]);
            settings.setCnName(cnNames[i]);
            settings.setValue(values[i]);
            if (!checkIfDuplication(settings)) {
                super.createNewData(settings);
            }
        }*/
    }

    public List<GlobalSettings> queryByTypeLikeName(String type, String name) {
        createQueryAndWhere();
        try {
            mWhere.eq(GlobalSettings.COL_TYPE, type).and()
                    .like(GlobalSettings.COL_NAME, name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return queryForCondition();
    }

    public List<GlobalSettings> queryByType(String type) {
        createQueryAndWhere();
        try {
            mWhere.eq(GlobalSettings.COL_TYPE, type);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return queryForCondition();
    }

    public GlobalSettings queryByTypeAndName(String type, String name) {
        createQueryAndWhere();
        try {
            mWhere.eq(GlobalSettings.COL_TYPE, type).and()
                    .eq(GlobalSettings.COL_NAME, name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return queryForFirst();
    }

    public GlobalSettings queryByValue(String value) {
        createQueryAndWhere();
        try {
            mWhere.eq(GlobalSettings.COL_VALUE, value);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return queryForFirst();
    }

    public String queryValueByTypeAndName(String type, String name) {
        createQueryAndWhere();
        try {
            mWhere.eq(GlobalSettings.COL_TYPE, type).and()
                    .eq(GlobalSettings.COL_NAME, name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        GlobalSettings settings = queryForFirst();
        if (settings != null) {
            return settings.getValue();
        } else {
            return "";
        }
    }

    @Override
    public boolean checkIfDuplication(GlobalSettings settings) {
        boolean flag;
        createQueryAndWhere();
        try {
            mWhere.eq(GlobalSettings.COL_TYPE, settings.getType())
                    .and().eq(GlobalSettings.COL_NAME, settings.getName())
                    .and().eq(GlobalSettings.COL_VALUE, settings.getValue());
            GlobalSettings newSettings = queryForFirst();
            if (newSettings != null) {
                flag = newSettings.equals(settings);
            } else {
                flag = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            flag = false;
        }
        return flag;
    }
}
