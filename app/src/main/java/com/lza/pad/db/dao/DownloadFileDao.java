package com.lza.pad.db.dao;

import android.content.Context;

import com.lza.pad.db.model.DownloadFile;

import java.sql.SQLException;
import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 11/26/14.
 */
public class DownloadFileDao extends BaseDao<DownloadFile, Integer> {

    public DownloadFileDao(Context context) {
        super(DownloadFile.class, context);
    }

    @Override
    public int create(DownloadFile data) {
        if (!checkIfDuplication(data)) {
            return super.create(data);
        } else {
            return 0;
        }
    }

    public List<DownloadFile> queryByType(int type) {
        createQueryAndWhere();
        try {
            mWhere.eq("type", type);
            return queryForCondition();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public DownloadFile queryByArtId(String artId) {
        createQueryAndWhere();
        try {
            mWhere.eq("artId", artId);
            return queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public DownloadFile queryJournalById(String id) {
        createQueryAndWhere();
        try {
            mWhere.eq("fileName", id).and().eq("type", 0);
            return queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected boolean checkIfDuplication(DownloadFile data) {
        boolean flag;
        createQueryAndWhere();
        try {
            mWhere.eq("fileName", data.getFileName());
            DownloadFile oldData = queryForFirst();
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
