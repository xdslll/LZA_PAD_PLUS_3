package com.lza.pad.app.guide;

import android.widget.LinearLayout;

import com.lza.pad.app.base._BaseModuleActivity;
import com.lza.pad.fragment.guide.LibraryMapFragment;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/1/24.
 */
public class GuideActivity extends _BaseModuleActivity {

    @Override
    protected String getModName() {
        return "布局导航";
    }

    @Override
    protected void onDrawWindow(LinearLayout container, int w, int h) {
        launchFragment(new LibraryMapFragment(), container.getId(), w, h, false);
    }
}
