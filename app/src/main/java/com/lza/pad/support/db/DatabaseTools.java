package com.lza.pad.support.db;

import android.content.Context;

import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.lza.pad.support.utils.Utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;


/**
 * Created by xiads on 14-9-3.
 */
public class DatabaseTools {

    private static ArrayList<Class> sTables = new ArrayList<Class>();

    public static ArrayList<Class> getTableClasses(Context c, int resId) {
        if (sTables != null) {
            if (sTables.size() == 0) {
                loadTableClass(c, resId);
                return sTables;
            }
        }
        throw new NullPointerException("Table class is null or init error!");
    }

    /**
     * 读取所有表
     */
    private static void loadTableClass(Context c, int resId) {
        InputStream is = c.getResources().openRawResource(resId);
        BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
        String line;
        try {
            while ((line = buffer.readLine()) != null) {
                if (line.contains("dataClass")) {
                    int index = line.indexOf("=");
                    String tableClass = line.substring(index + 1);
                    Class clazz = Class.forName(tableClass);
                    sTables.add(clazz);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Utility.close(buffer);
        Utility.close(is);
    }

    public static boolean dropTables(Context context, int resId, ConnectionSource connectionSource) {
        loadTableClass(context, resId);
        for (Class dataClass : sTables) {
            try {
                TableUtils.dropTable(connectionSource, dataClass, true);
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        sTables.clear();
        return true;
    }

    public static boolean createTables(Context context, int resId, ConnectionSource connectionSource) {
        loadTableClass(context, resId);
        for (Class dataClass : sTables) {
            try {
                TableUtils.createTableIfNotExists(connectionSource, dataClass);
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        sTables.clear();
        return true;
    }


}
