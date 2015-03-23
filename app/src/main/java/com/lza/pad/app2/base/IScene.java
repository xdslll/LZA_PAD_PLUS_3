package com.lza.pad.app2.base;

import com.lza.pad.db.model.pad.PadScene;
import com.lza.pad.db.model.pad.PadSceneModule;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 3/10/15.
 */
public interface IScene {

    /**
     * 获取某一场景下的所有模块
     */
    void getSceneModules(PadScene scene);

    /**
     * 启动模块
     *
     * @param module
     */
    void launchModule(PadSceneModule module);

}
