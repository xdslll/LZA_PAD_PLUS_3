package com.lza.pad.support.network;

import android.text.TextUtils;

import com.lza.pad.support.file.FileTools;
import com.lza.pad.support.utils.UniversalUtility;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * 生成url地址
 *
 * @author Sam
 * @Date 14-9-12
 */
public class BaseApiUrl {

    private URL mUrl;
    private Map<String, String> mParams;

    public BaseApiUrl(String url) {
        generateUrl(url);
    }

    public BaseApiUrl(String url, Map<String, String> params) {
        generateUrl(url, params);
    }

    public BaseApiUrl(String address, String authAndPath, Map<String, String> params) {
        generateUrl(address, authAndPath, params);
    }

    public BaseApiUrl(String protocol, String authority, String path, Map<String, String> params) {
        generateUrl(protocol, authority, path, params);
    }

    public String getUrl() {
        if (mUrl != null) {
            return mUrl.toExternalForm();
        }
        return null;
    }

    public String getParamValue(String key) {
        if (!UniversalUtility.isMapEmpty(mParams) && mParams.containsKey(key)) {
            return mParams.get(key);
        }
        return null;
    }

    private void generateUrl(String url) {
        try {
            mUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void generateUrl(String url, Map<String, String> params) {
        StringBuilder urlBuilder = new StringBuilder();
        if (!TextUtils.isEmpty(url)) {
            urlBuilder.append(url);
        }
        if (!UniversalUtility.isMapEmpty(params)) {
            if (!url.endsWith("?")) {
                urlBuilder.append("?");
            }
            urlBuilder.append(UniversalUtility.encodeUrl(params));
        }
        generateUrl(urlBuilder.toString());
        mParams = params;
    }

    private void generateUrl(String address, String authAndPath, Map<String, String> params) {
        StringBuilder urlBuilder = new StringBuilder();
        address = FileTools.appendDirSeparator(address);
        urlBuilder.append(address);
        authAndPath = FileTools.removeFirstSeparator(authAndPath);
        urlBuilder.append(authAndPath);

        generateUrl(urlBuilder.toString(), params);
    }

    private void generateUrl(String protocol, String authority, String path, Map<String, String> params) {
        StringBuilder urlBuilder = new StringBuilder();
        if (!TextUtils.isEmpty(protocol)) {
            urlBuilder.append(protocol);
            if (!protocol.endsWith("://")) {
                urlBuilder.append("://");
            }
        }
        if (!TextUtils.isEmpty(authority)) {
            authority = FileTools.appendDirSeparator(authority);
            urlBuilder.append(authority);
        }
        if (!TextUtils.isEmpty(path)) {
            path = FileTools.removeFirstSeparator(path);
            urlBuilder.append(path);
        }

        generateUrl(urlBuilder.toString(), params);
    }

}
