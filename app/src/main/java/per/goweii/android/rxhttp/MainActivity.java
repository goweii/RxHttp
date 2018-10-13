package per.goweii.android.rxhttp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

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
                return "http://api.apiopen.top/";
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
