package com.lza.pad.db.dao;

import android.content.Context;

import com.lza.pad.db.model.ApiParam;

import java.sql.SQLException;
import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 9/30/14.
 */
public class ApiParamDao extends BaseDao<ApiParam, Integer> {

    public ApiParamDao(Context context) {
        super(ApiParam.class, context);
    }

    public void init() {
        /*String[] types = mContext.getResources().getStringArray(R.array.api_param_type);
        String[] keys = mContext.getResources().getStringArray(R.array.api_param_key);
        String[] values = mContext.getResources().getStringArray(R.array.api_param_value);

        for (int i = 0; i < types.length; i++) {
            ApiParam data = new ApiParam();
            data.setType(types[i]);
            data.setKey(keys[i]);
            data.setValue(values[i]);
            if (!checkIfDuplication(data)) {
                super.createNewData(data);
            }
        }*/
    }

    public ApiParam queryByTypeAndKey(String type, String key) {
        createQueryAndWhere();
        try {
            mWhere.eq(ApiParam.COL_TYPE, type).and()
                    .eq(ApiParam.COL_KEY, key);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return queryForFirst();
    }

    public List<ApiParam> queryByType(String type) {
        createQueryAndWhere();
        try {
            mWhere.eq(ApiParam.COL_TYPE, type);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return queryForCondition();
    }

    @Override
    public boolean checkIfDuplication(ApiParam data) {
        boolean flag;
        createQueryAndWhere();
        try {
            mWhere.eq(ApiParam.COL_TYPE, data.getType())
                    .and().eq(ApiParam.COL_KEY, data.getKey())
                    .and().eq(ApiParam.COL_VALUE, data.getValue());
            ApiParam oldData = queryForFirst();
            if (oldData != null) {
                flag = oldData.equals(data);
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
