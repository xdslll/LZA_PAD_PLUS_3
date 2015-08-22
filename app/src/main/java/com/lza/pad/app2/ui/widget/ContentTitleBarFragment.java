package com.lza.pad.app2.ui.widget;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.lza.pad.R;
import com.lza.pad.app2.ui.widget.base.BaseImageFragment;
import com.lza.pad.db.model.ResponseData;
import com.lza.pad.db.model.pad.PadMenuGroup;
import com.lza.pad.helper.JsonParseHelper;
import com.lza.pad.helper.SimpleRequestListener;
import com.lza.pad.helper.UrlHelper;

import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 3/31/15.
 */
public class ContentTitleBarFragment extends BaseImageFragment implements SubjectFragment.OnSubjectClick {

    TextView mTxtHome, mTxtModName, mTxtSubject, mTxtSearch, mTxtDivider;

    List<PadMenuGroup> mPadMenuGroups;

    private int mCurrentSubject = 0;
    private String[] mData = {"全部分类", "A 马列主义、毛泽东思想、邓小平理论", "B 哲学、宗教",
            "C 社会科学总论", "D 政治、法律", "E 军事", "F 经济", "G 文化、科学、教育、体育", "H 语言、文学",
            "I 文学", "J 艺术", "K 历史、地理", "N 自然科学总论", "O 数理科学与化学", "P 天文学、地球科学",
            "Q 生物科学", "R 医药、卫生", "S 农业科学", "T 工业技术", "U 交通运输", "V 航空、航天",
            "X 环境科学，安全科学", "Z 综合性图书"};

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.common_content_titlebar, container, false);
        mTxtHome = (TextView) view.findViewById(R.id.common_content_titlebar_back);
        mTxtModName = (TextView) view.findViewById(R.id.common_content_titlebar_mod_name);
        mTxtSubject = (TextView) view.findViewById(R.id.common_content_titlebar_subject);
        mTxtSearch = (TextView) view.findViewById(R.id.common_content_titlebar_search);
        mTxtDivider = (TextView) view.findViewById(R.id.common_content_titlebar_divider);

        mTxtHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                mActivity.finish();
            }
        });

        mTxtModName.setText(mPadModuleWidget.getLabel());

        mTxtSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubjectFragment subjectFragment = new SubjectFragment();
                Bundle bundle = new Bundle();
                bundle.putStringArray(KEY_SUBJECT_DATA, mData);
                bundle.putInt(KEY_CURRENT_SUBJECT, mCurrentSubject);
                subjectFragment.setArguments(bundle);
                subjectFragment.setOnSubjectClick(ContentTitleBarFragment.this);
                subjectFragment.show(getFragmentManager(), "subject");
            }
        });

        mTxtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText = new EditText(mActivity);
                AlertDialog dialog = new AlertDialog.Builder(mActivity)
                        .setTitle("请输入搜索关键字")
                        .setView(editText)
                        .setPositiveButton("搜索", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String keyword = editText.getText().toString();
                                log("搜索关键字:" + keyword);
                                Intent intent = new Intent();
                                intent.setAction(ACTION_SEARCH_RECEIVER);
                                intent.putExtra(KEY_KEYWORD, keyword);
                                mActivity.sendBroadcast(intent);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                dialog.show();
            }
        });

        mTxtSearch.setVisibility(View.GONE);
        mTxtSubject.setVisibility(View.GONE);
        mTxtDivider.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onSubjectClick(int position) {
        if (position >= 0 && position < mData.length) {
            mCurrentSubject = position;
            try {
                String subject = mData[position];
                mTxtSubject.setText(subject);
                String subjectType = subject.split(" ")[0];
                log("学科：" + subjectType);
                if (subjectType.equals(mData[0])) {
                    Intent intent = new Intent();
                    intent.setAction(ACTION_SUBJECT_RECEIVER);
                    intent.putExtra(KEY_SUBJECT, "ALL");
                    mActivity.sendBroadcast(intent);
                } else {
                    Intent intent = new Intent();
                    intent.setAction(ACTION_SUBJECT_RECEIVER);
                    intent.putExtra(KEY_SUBJECT, subjectType);
                    mActivity.sendBroadcast(intent);
                }
            } catch (Exception ex) {

            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (mPadSceneModule != null) {
            String menuGroupId = mPadSceneModule.getMenu_group_id();
            log("menuGroupId=" + menuGroupId);
            String url = UrlHelper.getModuleMenu(mPadDeviceInfo, mPadSceneModule);
            send(url, new GetPadModuleMenu());
        }
    }

    private class GetPadModuleMenu extends SimpleRequestListener<PadMenuGroup> {

        @Override
        public ResponseData<PadMenuGroup> parseJson(String json) {
            return JsonParseHelper.parsePadMenuGroup(json);
        }

        @Override
        public void handleRespone(List<PadMenuGroup> content) {
            mPadMenuGroups = content;
            if (mPadMenuGroups.get(0).getGroup().equals("1")) {
                mTxtSearch.setVisibility(View.VISIBLE);
                mTxtSubject.setVisibility(View.VISIBLE);
                mTxtDivider.setVisibility(View.VISIBLE);
            }
        }
    }
}
