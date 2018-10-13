package per.goweii.android.rxhttp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import per.goweii.android.rxhttp.bean.RecommendPoetryBean;
import per.goweii.android.rxhttp.bean.SinglePoetryBean;
import per.goweii.rxhttp.RxHttp;
import per.goweii.rxhttp.RxLife;
import per.goweii.rxhttp.RxRequest;

public class TestRequestActivity extends AppCompatActivity {
    private static final String TAG = "TestRequestActivity";

    private RxLife mRxLife;
    private TextView tv_log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_request);
        mRxLife = RxLife.create();

        tv_log = findViewById(R.id.tv_log);
        findViewById(R.id.tv_get_singlePoetry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTime();
            }
        });findViewById(R.id.tv_get_recommendPoetry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHome();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRxLife.destroy();
    }

    private void getTime() {
        mRxLife.add(RxRequest.create(RxHttp.getApi(Api.class).singlePoetry()).listener(new RxRequest.RequestListener() {
            @Override
            public void onStart() {
                Log.d(TAG, "RxRequest:onStart");
                log(null);
                log("onStart");
            }

            @Override
            public void onNoNet() {
                Log.d(TAG, "RxRequest:onNoNet");
                log("onNoNet");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "RxRequest:onError");
                log("onError");
            }

            @Override
            public void onFinish() {
                Log.d(TAG, "RxRequest:onFinish");
                log("onFinish");
            }
        }).request(new RxRequest.RequestCallback<SinglePoetryBean>() {
            @Override
            public void onSuccess(int code, SinglePoetryBean data) {
                Log.d(TAG, "RxRequest:onSuccess(code=" + code + ",data=" + data.toJson() + ")");
                log("onSuccess(code=" + code + ",data=" + data.toJson() + ")");
            }

            @Override
            public void onFailed(int code, String msg) {
                Log.d(TAG, "RxRequest:onFailed(code=" + code + ",msg=" + msg + ")");
                log("onFailed(code=" + code + ",msg=" + msg + ")");
            }
        }));
    }

    private void getHome() {
        mRxLife.add(RxRequest.create(RxHttp.getApi(Api.class).recommendPoetry()).listener(new RxRequest.RequestListener() {
            @Override
            public void onStart() {
                Log.d(TAG, "RxRequest:onStart");
                log(null);
                log("onStart");
            }

            @Override
            public void onNoNet() {
                Log.d(TAG, "RxRequest:onNoNet");
                log("onNoNet");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "RxRequest:onError");
                log("onError");
            }

            @Override
            public void onFinish() {
                Log.d(TAG, "RxRequest:onFinish");
                log("onFinish");
            }
        }).request(new RxRequest.RequestCallback<RecommendPoetryBean>() {
            @Override
            public void onSuccess(int code, RecommendPoetryBean data) {
                Log.d(TAG, "RxRequest:onSuccess(code=" + code + ",data=" + data.toJson() + ")");
                log("onSuccess(code=" + code + ",data=" + data.toJson() + ")");
            }

            @Override
            public void onFailed(int code, String msg) {
                Log.d(TAG, "RxRequest:onFailed(code=" + code + ",msg=" + msg + ")");
                log("onFailed(code=" + code + ",msg=" + msg + ")");
            }
        }));
    }

    private void log(String text){
        if (text == null) {
            tv_log.setText("");
        } else {
            tv_log.setText(tv_log.getText().toString() + "\n" + text);
        }
    }
}
