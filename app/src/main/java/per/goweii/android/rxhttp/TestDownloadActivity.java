package per.goweii.android.rxhttp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import per.goweii.rxhttp.core.RxHttp;
import per.goweii.rxhttp.download.RxDownload;
import per.goweii.rxhttp.download.setting.DefaultDownloadSetting;

public class TestDownloadActivity extends AppCompatActivity {

    public static final String url_1 = "https://imtt.dd.qq.com/16891/601BBD228F1F77DB1FB03FE38EF9BC93.apk?fsname=com.tencent.tmgp.sgame_1.41.2.4_41020401.apk&csr=1bbd";
    public static final String url = "https://imtt.dd.qq.com/16891/513D2C5324E6EBE77F94C85D7C76EBAE.apk?fsname=com.tencent.mobileqq_7.8.2_926.apk&csr=1bbd";
    private RxDownload mRxDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_download);

        RxHttp.initDownload(new DefaultDownloadSetting() {
            @Override
            public long getTimeout() {
                return 5000;
            }

            @Nullable
            @Override
            public String getSaveDirName() {
                return null;
            }
        });

        EditText et_url = findViewById(R.id.et_url);
        ProgressBar pb_1 = findViewById(R.id.pb_1);
        TextView tv_start = findViewById(R.id.tv_start);
        TextView tv_stop = findViewById(R.id.tv_stop);
        TextView tv_cancel = findViewById(R.id.tv_cancel);

        et_url.setText(url);

        tv_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRxDownload.start();
            }
        });

        tv_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRxDownload.stop();
            }
        });

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRxDownload.cancel();
            }
        });

        mRxDownload = RxDownload.create(et_url.getText().toString())
                .listener(new RxDownload.DownloadListener() {
                    @Override
                    public void onStart() {
                        tv_start.setText("正在下载...");
                    }

                    @Override
                    public void onProgress(float progress) {
                        pb_1.setProgress((int) (progress * 100));
                    }

                    @Override
                    public void onError(Throwable e) {
                        tv_start.setText("下载失败");
                    }

                    @Override
                    public void onFinish() {
                        tv_start.setText("下载成功");
                    }
                });
    }
}
