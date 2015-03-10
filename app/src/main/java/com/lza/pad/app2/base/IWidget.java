package com.lza.pad.app2.base;

import android.content.Context;

import com.lza.pad.db.model.pad.PadResource;

import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 3/10/15.
 */
public interface IWidget {

    /**
     * 更新UI
     *
     * @param ctx
     */
    void updateUi(Context ctx);

    /**
     * 更新数据
     *
     * @param ctx
     */
    void updateData(Context ctx);

    /**
     * 获取数据
     *
     * @return
     */
    List<PadResource> getData();

}
