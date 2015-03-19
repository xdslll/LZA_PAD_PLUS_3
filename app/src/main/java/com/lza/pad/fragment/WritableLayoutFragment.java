package com.lza.pad.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lza.pad.R;
import com.lza.pad.fragment.base._BaseFragment;
import com.lza.pad.widget.WritableView;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/1/22.
 */
public class WritableLayoutFragment extends _BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.writeable_layout, container, false);
        final WritableView testView = (WritableView) view.findViewById(R.id.writeable_layout_canvas);
        Button btnSave = (Button) view.findViewById(R.id.writeable_layout_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testView.save();
            }
        });

        Button btnClear = (Button) view.findViewById(R.id.writeable_layout_clear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testView.clear();
            }
        });

        Button btnRestore = (Button) view.findViewById(R.id.writeable_layout_restore);
        btnRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testView.restore();
            }
        });

        return view;
    }
}
