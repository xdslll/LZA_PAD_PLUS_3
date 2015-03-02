package com.lza.pad.fragment.journal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lza.pad.R;
import com.lza.pad.fragment.base.BaseFragment;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 2/28/15.
 */
public class JournalContentSummaryFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ebook_content_summary, container, false);
        TextView text = (TextView) view.findViewById(R.id.ebook_content_summary_text);
        String content = wrap(mPadResource.getContents(), "-");
        Spanned sp = Html.fromHtml(content);
        text.setText(sp);
        return view;
    }
}
