package per.goweii.android.rxhttp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import per.goweii.rxhttp.download.DownloadInfo;
import per.goweii.rxhttp.download.RxDownload;
import per.goweii.rxhttp.download.utils.UnitFormatUtils;

public class TestDownloadActivity extends AppCompatActivity {

    public static final String url_1 = "https://imtt.dd.qq.com/16891/601BBD228F1F77DB1FB03FE38EF9BC93.apk?fsname=com.tencent.tmgp.sgame_1.41.2.4_41020401.apk&csr=1bbd";
    public static final String url = "https://imtt.dd.qq.com/16891/513D2C5324E6EBE77F94C85D7C76EBAE.apk?fsname=com.tencent.mobileqq_7.8.2_926.apk&csr=1bbd";
    private RxDownload mRxDownload;
    private boolean isStart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_download);

        EditText et_url = findViewById(R.id.et_url);
        ProgressBar pb_1 = findViewById(R.id.pb_1);
        pb_1.setMax(10000);
        TextView tv_download_length = findViewById(R.id.tv_download_length);
        TextView tv_content_length = findViewById(R.id.tv_content_length);
        TextView tv_speed = findViewById(R.id.tv_speed);
        TextView tv_start_stop = findViewById(R.id.tv_start_stop);
        TextView tv_cancel = findViewById(R.id.tv_cancel);
        TextView tv_clean = findViewById(R.id.tv_clean);

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
                isStart = false;
            }
        });

        tv_clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanDownloadInfo();
                tv_download_length.setText("");
                tv_content_length.setText("");
                tv_speed.setText("");
                pb_1.setProgress(0);
                tv_start_stop.setText("开始下载");
                tv_cancel.setText("取消下载");
            }
        });

        DownloadInfo downloadInfo = getDownloadInfo();
        if (downloadInfo == null) {
            mRxDownload = RxDownload.create(DownloadInfo.create(et_url.getText().toString()));
        } else {
            pb_1.setProgress((int) (((float)downloadInfo.downloadLength / (float)downloadInfo.contentLength) * 10000));
            tv_download_length.setText(UnitFormatUtils.formatBytesLength(downloadInfo.downloadLength));
            tv_content_length.setText(UnitFormatUtils.formatBytesLength(downloadInfo.contentLength));
            DownloadInfo info = DownloadInfo.create(downloadInfo.url,
                    downloadInfo.saveDirPath, downloadInfo.saveFileName,
                    downloadInfo.downloadLength, downloadInfo.contentLength);
            mRxDownload = RxDownload.create(info);
        }
        mRxDownload.setDownloadListener(new RxDownload.DownloadListener() {
                    @Override
                    public void onStarting(DownloadInfo info) {
                        tv_start_stop.setText("暂停下载");
                        tv_cancel.setText("取消下载");
                    }

                    @Override
                    public void onDownloading(DownloadInfo info) {
                        tv_start_stop.setText("暂停下载");
                    }

                    @Override
                    public void onError(DownloadInfo info, Throwable e) {
                        saveDownloadInfo();
                        tv_start_stop.setText("开始下载");
                        tv_speed.setText("");
                    }

                    @Override
                    public void onStopped(DownloadInfo info) {
                        saveDownloadInfo();
                        tv_start_stop.setText("开始下载");
                        tv_speed.setText("");
                    }

                    @Override
                    public void onCanceled(DownloadInfo info) {
                        saveDownloadInfo();
                        tv_start_stop.setText("开始下载");
                        tv_cancel.setText("已取消");
                        pb_1.setProgress(0);
                        tv_speed.setText("");
                        tv_download_length.setText("");
                        tv_content_length.setText("");
                    }

                    @Override
                    public void onCompletion(DownloadInfo info) {
                        saveDownloadInfo();
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (isStart) {
                    mRxDownload.stop();
                    isStart = false;
                    return false;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private DownloadInfo getDownloadInfo() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String url = sp.getString("url", "");
        String saveDirName = sp.getString("saveDirPath", "");
        String saveFileName = sp.getString("saveFileName", "");
        long downloadLength = sp.getLong("downloadLength", 0);
        long contentLength = sp.getLong("contentLength", 0);
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(saveDirName) || TextUtils.isEmpty(saveFileName)){
            return null;
        }
        if (downloadLength == 0){
            return null;
        }
        if (contentLength < downloadLength){
            return null;
        }
        return DownloadInfo.create(url, saveDirName, saveFileName, downloadLength, contentLength);
    }

    private void saveDownloadInfo() {
        DownloadInfo info = mRxDownload.getDownloadInfo();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("url", info.url);
        editor.putString("saveDirPath", info.saveDirPath);
        editor.putString("saveFileName", info.saveFileName);
        editor.putLong("downloadLength", info.downloadLength);
        editor.putLong("contentLength", info.contentLength);
        editor.apply();
    }

    private void cleanDownloadInfo() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().clear().apply();
    }
}
