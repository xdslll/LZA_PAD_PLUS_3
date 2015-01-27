package com.lza.pad.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.lza.pad.R;
import com.lza.pad.app.base.BaseActivity;
import com.lza.pad.support.debug.AppLogger;
import com.lza.pad.support.utils.UniversalUtility;

import java.io.File;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/6/15.
 */
public class PluginInstallActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout._update);

        File dir = Environment.getExternalStorageDirectory();
        final File file = new File(dir, "Download/app-debug.apk");
        AppLogger.e(file.getAbsolutePath() + ":" + file.exists());

        findViewById(R.id.content_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UniversalUtility.showDialog(
                        PluginInstallActivity.this, "请确认", "是否开始更新？",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                install(file.getAbsolutePath());
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }
                );
            }
        });

    }

    public void install(String path) {
        Uri uri = Uri.fromFile(new File(path));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        startActivity(intent);
    }
}
