package com.lza.pad.app.ebook;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.lza.pad.app.SubjectActivity;
import com.lza.pad.app.base.BaseModuleActivity;
import com.lza.pad.fragment.ebook.EbookFragment1;
import com.lza.pad.fragment.ebook.EbookFragment3;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/18/15.
 */
public class EbookActivity extends BaseModuleActivity {

    private static final int REQUEST_SUBJECT = 0x001;
    private int mCurrentSubject = 0;
    private String[] mData = {"全部分类", "A 马列主义、毛泽东思想、邓小平理论", "B 哲学、宗教",
            "C 社会科学总论", "D 政治、法律", "E 军事", "F 经济", "G 文化、科学、教育、体育", "H 语言、文学",
            "I 文学", "J 艺术", "K 历史、地理", "N 自然科学总论", "O 数理科学与化学", "P 天文学、地球科学",
            "Q 生物科学", "R 医药、卫生", "S 农业科学", "T 工业技术", "U 交通运输", "V 航空、航天",
            "X 环境科学，安全科学", "Z 综合性图书"};

    @Override
    protected String getModName() {
        return "电子书";
    }

    @Override
    protected void onDrawWindow(LinearLayout container, int w, int h) {
        int size = DEFAULT_SIZE;
        for (int i = 0; i < size; i++) {
            int id = (i + 1) << (i + 1);
            FrameLayout subContainer = new FrameLayout(this);
            subContainer.setLayoutParams(new ViewGroup.LayoutParams(w, h / size));
            subContainer.setId(id);
            container.addView(subContainer);

            if (i == 0) {
                EbookFragment1 fragment = new EbookFragment1();
                launchFragment(fragment, id, w, h / size, false);
            } else {
                EbookFragment3 fragment = new EbookFragment3();
                launchFragment(fragment, id, w, h / size, false);
            }
        }
    }

    @Override
    protected void onSubjectClick(View v) {
        Intent intent = new Intent(EbookActivity.this, SubjectActivity.class);
        intent.putExtra(KEY_CURRENT_SUBJECT, mCurrentSubject);
        intent.putExtra(KEY_SUBJECT_DATA, mData);
        startActivityForResult(intent, REQUEST_SUBJECT);
    }

    @Override
    protected void onSearch(View v) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SUBJECT) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    mCurrentSubject = data.getIntExtra(KEY_CURRENT_SUBJECT, 0);
                    setSubjectText(mData[mCurrentSubject]);
                }
                //ToastUtils.showShort(this, "OK!" + mCurrentSubject);
            } else {
                //ToastUtils.showShort(this, "CANCEL!");
            }
        }
    }
}
