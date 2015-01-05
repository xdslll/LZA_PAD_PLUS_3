package com.lza.pad.db.dao;

import android.content.Context;

import com.lza.pad.db.model.CacheImage;

import java.sql.SQLException;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 9/30/14.
 */
public class CacheImageDao extends BaseDao<CacheImage, Integer> {

    public CacheImageDao(Context context) {
        super(CacheImage.class, context);
    }

    public void createOrUpdateData(CacheImage data) {
        if (!checkIfDuplication(data)) {
            create(data);
        } else {
            updateData(data);
        }
    }

    public String queryValueByTypeAndKey(String type, String key) {
        createQueryAndWhere();
        try {
            mWhere.eq(CacheImage.COL_TYPE, type).and()
                    .eq(CacheImage.COL_KEY, key);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        CacheImage data = queryForFirst();
        if (data != null) {
            return data.getValue();
        } else {
            return "";
        }
    }

    @Override
    public boolean checkIfDuplication(CacheImage data) {
        boolean flag;
        createQueryAndWhere();
        try {
            mWhere.eq(CacheImage.COL_TYPE, data.getType())
                    .and().eq(CacheImage.COL_KEY, data.getKey())
                    .and().eq(CacheImage.COL_VALUE, data.getValue());
            CacheImage oldData = queryForFirst();
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
