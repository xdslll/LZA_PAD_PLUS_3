package com.lza.pad.db.facade;

import android.content.Context;

import com.lza.pad.db.dao.DownloadFileDao;
import com.lza.pad.db.model.DownloadFile;

import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 11/26/14.
 */
public class DownloadFileFacade {

    private static DownloadFileDao mInstance;

    private static DownloadFileDao getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DownloadFileDao(context);
        }
        return mInstance;
    }

    public static int create(Context context, DownloadFile data) {
        return getInstance(context).create(data);
    }

    public static DownloadFile queryJournalById(Context context, String id) {
        return getInstance(context).queryJournalById(id);
    }

    public static DownloadFile queryByArtId(Context context, String artId) {
        return getInstance(context).queryByArtId(artId);
    }

    public static List<DownloadFile> queryEbookList(Context context) {
        return getInstance(context).queryByType(2);
    }

    public static List<DownloadFile> queryResourceList(Context context) {
        return getInstance(context).queryByType(1);
    }

    public static List<DownloadFile> queryJournalList(Context context) {
        return getInstance(context).queryByType(0);
    }

    public static long clear(Context context) {
        return getInstance(context).clear();
    }

    public static int delete(Context context, DownloadFile data) {
        return getInstance(context).delete(data);
    }

    public static List<DownloadFile> queryForAll(Context context) {
        return getInstance(context).queryForAll();
    }
}
