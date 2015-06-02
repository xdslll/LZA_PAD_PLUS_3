package com.lza.pad.app2.service;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/3/18.
 */
public enum ServiceMode {

    /**
     * 重置切换服务
     */
    MODE_RESET_SERVICE,

    /**
     * 终止场景服务
     */
    MODE_STOP_SCENE_SERVICE,

    MODE_STOP_MODULE_SERVICE,

    MODE_SWITCH_SCENE,

    MODE_SWITCH_MODULE,

    /**
     * 打开引导模块
     */
    MODE_START_GUIDE_MODULE,

    /**
     * 打开首页模块
     */
    MODE_START_HOME_MODULE,

    /**
     * 打开二级页面
     */
    MODE_START_SUBPAGE,

    /**
     * 关闭首页
     */
    MODE_FINISH_HOME_MODULE,

    /**
     * 关闭二级页面
     */
    MODE_FINISH_SUBPAGE_MODULE,

    /**
     * 关闭当前模块
     */
    MODE_FINISH_CURRENT_MODULE,

    /**
     * 开始更新版本
     */
    MODE_UPDATE_VERSION,

    /**
     * 开始更新界面
     */
    MODE_UPDATE_UI;

    int index;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }



}
