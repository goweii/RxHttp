package per.goweii.android.rxhttp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import per.goweii.android.rxhttp.bean.RecommendPoetryBean;
import per.goweii.android.rxhttp.bean.SinglePoetryBean;
import per.goweii.android.rxhttp.bean.WeatherBean;
import per.goweii.android.rxhttp.http.Api;
import per.goweii.rxhttp.RxHttp;
import per.goweii.rxhttp.RxLife;
import per.goweii.rxhttp.RxRequest;
import per.goweii.rxhttp.base.BaseBean;

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
        });
        findViewById(R.id.tv_get_recommendPoetry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHome();
            }
        });
        findViewById(R.id.tv_get_weather).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_weather_city = findViewById(R.id.et_weather_city);
                getWeather(et_weather_city.getText().toString());
            }
        });
        findViewById(R.id.tv_http_host_error).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getErrorHost();
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
                log(null);
                log("onStart()");
            }

            @Override
            public void onNoNet() {
                log("onNoNet()");
            }

            @Override
            public void onError(Throwable e) {
                log("onError()");
            }

            @Override
            public void onFinish() {
                log("onFinish()");
            }
        }).request(new RxRequest.RequestCallback<SinglePoetryBean>() {
            @Override
            public void onSuccess(int code, SinglePoetryBean data) {
                log("onSuccess(code=" + code + ",data=" + data.toFormatJson() + ")");
            }

            @Override
            public void onFailed(int code, String msg) {
                log("onFailed(code=" + code + ",msg=" + msg + ")");
            }
        }));
    }

    private void getHome() {
        mRxLife.add(RxRequest.create(RxHttp.getApi(Api.class).recommendPoetry()).listener(new RxRequest.RequestListener() {
            @Override
            public void onStart() {
                log(null);
                log("onStart()");
            }

            @Override
            public void onNoNet() {
                log("onNoNet()");
            }

            @Override
            public void onError(Throwable e) {
                log("onError()");
            }

            @Override
            public void onFinish() {
                log("onFinish()");
            }
        }).request(new RxRequest.RequestCallback<RecommendPoetryBean>() {
            @Override
            public void onSuccess(int code, RecommendPoetryBean data) {
                log("onSuccess(code=" + code + ",data=" + data.toFormatJson() + ")");
            }

            @Override
            public void onFailed(int code, String msg) {
                log("onFailed(code=" + code + ",msg=" + msg + ")");
            }
        }));
    }

    private void getWeather(String city) {
        mRxLife.add(RxRequest.create(RxHttp.getApi(Api.class).weather(city)).listener(new RxRequest.RequestListener() {
            @Override
            public void onStart() {
                log(null);
                log("onStart()");
            }

            @Override
            public void onNoNet() {
                log("onNoNet()");
            }

            @Override
            public void onError(Throwable e) {
                log("onError()");
            }

            @Override
            public void onFinish() {
                log("onFinish()");
            }
        }).request(new RxRequest.RequestCallback<WeatherBean>() {
            @Override
            public void onSuccess(int code, WeatherBean data) {
                log("onSuccess(code=" + code + ",data=" + data.toFormatJson() + ")");
            }

            @Override
            public void onFailed(int code, String msg) {
                log("onFailed(code=" + code + ",msg=" + msg + ")");
            }
        }));
    }

    private void getErrorHost() {
        mRxLife.add(RxRequest.create(RxHttp.getApi(Api.class).errorHost()).listener(new RxRequest.RequestListener() {
            @Override
            public void onStart() {
                log(null);
                log("onStart()");
            }

            @Override
            public void onNoNet() {
                log("onNoNet()");
            }

            @Override
            public void onError(Throwable e) {
                log("onError()");
            }

            @Override
            public void onFinish() {
                log("onFinish()");
            }
        }).request(new RxRequest.RequestCallback<BaseBean>() {
            @Override
            public void onSuccess(int code, BaseBean data) {
                log("onSuccess(code=" + code + ",data=" + data.toFormatJson() + ")");
            }

            @Override
            public void onFailed(int code, String msg) {
                log("onFailed(code=" + code + ",msg=" + msg + ")");
            }
        }));
    }

    private void log(String text){
        if (text == null) {
            tv_log.setText("");
        } else {
            Log.d(TAG, text);
            tv_log.setText(tv_log.getText().toString() + "\n" + text);
        }
    }
}
