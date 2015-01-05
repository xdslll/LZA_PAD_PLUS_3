package com.lza.pad.db.facade;

import android.content.Context;

import com.lza.pad.db.dao.CacheImageDao;
import com.lza.pad.db.model.CacheImage;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 10/22/14.
 */
public class CacheImageFacade {

    private static CacheImageDao mInstance;

    private static CacheImageDao getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new CacheImageDao(context);
        }
        return mInstance;
    }

    public static void createOrUpdateData(Context context, CacheImage data) {
        CacheImageDao dao = getInstance(context);
        dao.createOrUpdateData(data);
    }

    public static String queryByTypeAndKey(Context context, String type, String key) {
        CacheImageDao dao = getInstance(context);
        return dao.queryValueByTypeAndKey(type, key);
    }

}
