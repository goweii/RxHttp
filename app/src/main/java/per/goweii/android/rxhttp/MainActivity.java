package per.goweii.android.rxhttp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import per.goweii.android.rxhttp.http.Config;
import per.goweii.android.rxhttp.utils.ToastMaker;
import per.goweii.rxhttp.RxHttp;
import per.goweii.rxhttp.RxLife;
import per.goweii.rxhttp.setting.DefaultHttpSetting;

public class MainActivity extends AppCompatActivity {

    private RxLife mRxLife;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ToastMaker.init(getApplicationContext());
        RxHttp.init(this, new DefaultHttpSetting() {
            @NonNull
            @Override
            public String getBaseUrl() {
                return Config.BASE_URL;
            }

            @Nullable
            @Override
            public Map<String, String> getMultiBaseUrl() {
                Map<String, String> urls = new HashMap<>(1);
                urls.put(Config.BASE_URL_OTHER_NAME, Config.BASE_URL_OTHER);
                urls.put(Config.BASE_URL_ERROR_NAME, Config.BASE_URL_ERROR);
                return urls;
            }

            @Override
            public int getSuccessCode() {
                return 200;
            }
        });

        findViewById(R.id.tv_go_test_request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TestRequestActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
