package com.lza.pad.helper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 11/14/14.
 */
public class GsonHelper {

    private static Gson gson = new Gson();
    private static GsonBuilder builder = new GsonBuilder();

    private GsonHelper(){}

    public static Gson instance() {
        return gson;
    }

    public static GsonBuilder builder() {
        return builder;
    }

    public static Gson buildExpose() {
        return builder.excludeFieldsWithoutExposeAnnotation().create();
    }
}
