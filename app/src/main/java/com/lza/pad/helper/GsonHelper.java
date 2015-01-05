package com.lza.pad.helper;

import com.google.gson.Gson;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 11/14/14.
 */
public class GsonHelper {

    private static Gson gson = new Gson();

    private GsonHelper(){}

    public static Gson instance() {
        return gson;
    }
}
