package com.lza.pad.fragment.ebook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.lza.pad.db.model.pad._old.PadLayoutModule;
import com.lza.pad.db.model.pad.PadResource;
import com.lza.pad.fragment.base._BaseGridFragment;
import com.lza.pad.fragment.base.BaseResourceListFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 2/20/15.
 */
public class EbookFragment1 extends BaseResourceListFragment {

    private int mPageSize;
    private int mPage;

    /**
     * 点击更多按钮事件
     */
    @Override
    protected void onMoreButtonClick() {
        //gotoMoudule("");
    }

    /**
     * 根据目标模块ID，跳转到指定模块
     *
     * @param targetModuleId
     */
    private void gotoMoudule(String targetModuleId) {
        PadLayoutModule module = null;
        for (int i = 0; i < mPadModuleInfos.size(); i++) {
            if (mPadModuleInfos.get(i).getId().equals(targetModuleId)) {
                module = mPadModuleInfos.get(i);
                break;
            }
        }
        if (module == null) return;
        //拼接出代码所在的路径
        String javaCodeFile = getModuleJavaFileName(module);
        try {
            Class clazz = Class.forName(javaCodeFile);
            Intent intent = new Intent(mActivity, clazz);
            intent.putExtra(KEY_PAD_DEVICE_INFO, mPadDeviceInfo);
            intent.putExtra(KEY_PAD_MODULE_INFO, module);
            startActivity(intent);
        } catch (Exception ex) {

        }
    }



    @Override
    protected Fragment getFragment(int position) {
        //计算当前是第几页
        mPage = position + 1;
        //计算当前页共子界面的个数
        calcPageSize();
        //获取数据源
        int start = (mPage - 1) * getGridNumColumns();
        int end = start + mPageSize;
        List<PadResource> _data = getDataSource().subList(start, end);
        ArrayList<PadResource> data = new ArrayList<PadResource>(_data);
        //生成Fragment，填充ViewPager
        Fragment fragment = new _BaseGridFragment();
        fragment.setArguments(createArgument(data));
        return fragment;
    }

    private void calcPageSize() {
        mPageSize = mGridNumColumns;
        //如果达到最后一页
        if (mPage == getTotalPage()) {
            int left = getGridDataSize() % getGridNumColumns();
            if (left > 0) {
                mPageSize = left;
            }
        }
    }

    private Bundle createArgument(ArrayList<PadResource> data) {
        Bundle arg = new Bundle();
        arg.putParcelable(KEY_PAD_DEVICE_INFO, mPadDeviceInfo);
        arg.putParcelable(KEY_PAD_CONTROL_INFO, mPadControlInfo);
        arg.putInt(KEY_FRAGMENT_WIDTH, getBookAreaWidth());
        arg.putInt(KEY_FRAGMENT_HEIGHT, getBookAreaHeight());
        arg.putInt(KEY_TOTAL_PAGE, mTotalPageSize);
        arg.putInt(KEY_PAGE_SIZE, mPageSize);
        arg.putInt(KEY_CURRENT_PAGE, mPage);
        arg.putInt(KEY_DATA_SIZE, mDataSize);
        arg.putInt(KEY_GRID_NUM_COLUMNS, mGridNumColumns);
        arg.putParcelableArrayList(KEY_PAD_RESOURCE_INFOS, data);
        arg.putBoolean(KEY_IS_HOME, mIsHome);
        return arg;
    }
}
