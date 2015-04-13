package com.lza.pad.app2.base;

import com.lza.pad.db.model.pad.PadSceneModule;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 3/10/15.
 */
public interface IScene {

    /**
     * 解析所有模块
     */
    void parseModuleList();

    /**
     * 重置场景切换后的数据
     */
    void resetSceneData();

    /**
     * 启动模块
     *
     * @param module
     */
    void launchModule(PadSceneModule module);

}
