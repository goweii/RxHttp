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
import per.goweii.rxhttp.download.base.DownloadInfo;
import per.goweii.rxhttp.download.setting.DefaultDownloadSetting;
import per.goweii.rxhttp.download.utils.UnitFormatUtils;

public class TestDownloadActivity extends AppCompatActivity {

    public static final String url = "https://imtt.dd.qq.com/16891/601BBD228F1F77DB1FB03FE38EF9BC93.apk?fsname=com.tencent.tmgp.sgame_1.41.2.4_41020401.apk&csr=1bbd";
    public static final String url_1 = "https://imtt.dd.qq.com/16891/513D2C5324E6EBE77F94C85D7C76EBAE.apk?fsname=com.tencent.mobileqq_7.8.2_926.apk&csr=1bbd";
    private RxDownload mRxDownload;
    private boolean isStart = false;

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
        pb_1.setMax(10000);
        TextView tv_download_length = findViewById(R.id.tv_download_length);
        TextView tv_content_length = findViewById(R.id.tv_content_length);
        TextView tv_speed = findViewById(R.id.tv_speed);
        TextView tv_start_stop = findViewById(R.id.tv_start_stop);
        TextView tv_cancel = findViewById(R.id.tv_cancel);

        et_url.setText(url);

        tv_start_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStart) {
                    mRxDownload.stop();
                    isStart = false;
                } else {
                    mRxDownload.start();
                    isStart = true;
                }
            }
        });

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRxDownload.cancel();
            }
        });

        mRxDownload = RxDownload.create(et_url.getText().toString())
                .setDownloadListener(new RxDownload.DownloadListener() {
                    @Override
                    public void onStarting() {
                        tv_start_stop.setText("暂停下载");
                        tv_cancel.setText("取消下载");
                    }

                    @Override
                    public void onDownloading() {
                        tv_start_stop.setText("暂停下载");
                    }

                    @Override
                    public void onError(Throwable e) {
                        tv_start_stop.setText("开始下载");
                        tv_speed.setText("");
                    }

                    @Override
                    public void onStopped() {
                        tv_start_stop.setText("开始下载");
                        tv_speed.setText("");
                    }

                    @Override
                    public void onCanceled() {
                        tv_start_stop.setText("开始下载");
                        tv_cancel.setText("已取消");
                        pb_1.setProgress(0);
                        tv_speed.setText("");
                    }

                    @Override
                    public void onCompletion(DownloadInfo info) {
                        tv_start_stop.setText("下载成功");
                        tv_speed.setText("");
                    }
                })
                .setProgressListener(new RxDownload.ProgressListener() {
                    @Override
                    public void onProgress(float progress, long downloadLength, long contentLength) {
                        pb_1.setProgress((int) (progress * 10000));
                        tv_download_length.setText(UnitFormatUtils.formatBytesLength(downloadLength));
                        tv_content_length.setText(UnitFormatUtils.formatBytesLength(contentLength));
                    }
                })
                .setSpeedListener(new RxDownload.SpeedListener() {
                    @Override
                    public void onSpeedChange(float bytePerSecond, String speedFormat) {
                        tv_speed.setText(speedFormat);
                    }
                });
    }
}
