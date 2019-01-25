package per.goweii.android.rxhttp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;
import per.goweii.android.rxhttp.bean.RecommendPoetryBean;
import per.goweii.android.rxhttp.bean.SinglePoetryBean;
import per.goweii.android.rxhttp.bean.WeatherBean;
import per.goweii.android.rxhttp.http.FreeApi;
import per.goweii.rxhttp.core.RxHttp;
import per.goweii.rxhttp.core.RxLife;
import per.goweii.rxhttp.request.RxRequest;
import per.goweii.rxhttp.request.base.BaseBean;
import per.goweii.rxhttp.request.exception.ExceptionHandle;
import per.goweii.rxhttp.request.setting.DefaultRequestSetting;
import per.goweii.rxhttp.request.setting.ParameterGetter;

public class TestRequestActivity extends AppCompatActivity {
    private static final String TAG = "TestRequestActivity";

    private RxLife mRxLife;
    private TextView tvLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_request);
        RxHttp.initRequest(new DefaultRequestSetting() {
            @NonNull
            @Override
            public String getBaseUrl() {
                return FreeApi.Config.BASE_URL;
            }

            @Override
            public Map<String, String> getRedirectBaseUrl() {
                Map<String, String> urls = new HashMap<>(2);
                urls.put(FreeApi.Config.BASE_URL_OTHER_NAME, FreeApi.Config.BASE_URL_OTHER);
                urls.put(FreeApi.Config.BASE_URL_ERROR_NAME, FreeApi.Config.BASE_URL_ERROR);
                urls.put(FreeApi.Config.BASE_URL_HTTPS_NAME, FreeApi.Config.BASE_URL_HTTPS);
                return urls;
            }

            @Override
            public int getSuccessCode() {
                return FreeApi.Code.SUCCESS;
            }

            @Override
            public Map<String, String> getStaticPublicQueryParameter() {
                Map<String, String> parameters = new HashMap<>(2);
                parameters.put("system", "android");
                parameters.put("version_code", "1");
                parameters.put("device_num", "666");
                return parameters;
            }

            @Override
            public Map<String, ParameterGetter> getDynamicPublicQueryParameter() {
                Map<String, ParameterGetter> parameters = new HashMap<>(2);
                parameters.put("user_id", new ParameterGetter() {
                    @Override
                    public String get() {
                        return "100001";
                    }
                });
                return parameters;
            }

            @Override
            public void setOkHttpClient(OkHttpClient.Builder builder) {
                builder.hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
            }
        });
        mRxLife = RxLife.create();

        tvLog = findViewById(R.id.tv_log);
        findViewById(R.id.tv_get_singlePoetry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSinglePoetry();
            }
        });
        findViewById(R.id.tv_get_recommendPoetry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRecommendPoetry();
            }
        });
        findViewById(R.id.tv_get_weather).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etWeatherCity = findViewById(R.id.et_weather_city);
                getWeather(etWeatherCity.getText().toString());
            }
        });
        findViewById(R.id.tv_http_host_error).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getErrorHost();
            }
        });
        findViewById(R.id.tv_https).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHttps();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRxLife.destroy();
    }

    private void getSinglePoetry() {
        mRxLife.add(RxHttp.request(FreeApi.api().singlePoetry()).listener(new RxRequest.RequestListener() {
            private long timeStart = 0;

            @Override
            public void onStart() {
                log(null);
                log("onStart()");
                timeStart = System.currentTimeMillis();
            }

            @Override
            public void onError(ExceptionHandle handle) {
                log("onError(" + handle.getMsg() + ")");
            }

            @Override
            public void onFinish() {
                long cast = System.currentTimeMillis() - timeStart;
                log("onFinish(cast=" + cast + ")");
            }
        }).request(new RxRequest.ResultCallback<SinglePoetryBean>() {
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

    private void getRecommendPoetry() {
        mRxLife.add(RxRequest.create(FreeApi.api().recommendPoetry()).listener(new RxRequest.RequestListener() {
            private long timeStart = 0;

            @Override
            public void onStart() {
                log(null);
                log("onStart()");
                timeStart = System.currentTimeMillis();
            }

            @Override
            public void onError(ExceptionHandle handle) {
                log("onError(" + handle.getMsg() + ")");
            }

            @Override
            public void onFinish() {
                long cast = System.currentTimeMillis() - timeStart;
                log("onFinish(cast=" + cast + ")");
            }
        }).request(new RxRequest.ResultCallback<RecommendPoetryBean>() {
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
        mRxLife.add(RxRequest.create(FreeApi.api().weather(city)).listener(new RxRequest.RequestListener() {
            private long timeStart = 0;

            @Override
            public void onStart() {
                log(null);
                log("onStart()");
                timeStart = System.currentTimeMillis();
            }

            @Override
            public void onError(ExceptionHandle handle) {
                log("onError(" + handle.getMsg() + ")");
            }

            @Override
            public void onFinish() {
                long cast = System.currentTimeMillis() - timeStart;
                log("onFinish(cast=" + cast + ")");
            }
        }).request(new RxRequest.ResultCallback<WeatherBean>() {
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
        mRxLife.add(RxRequest.create(FreeApi.api().errorHost()).listener(new RxRequest.RequestListener() {
            private long timeStart = 0;

            @Override
            public void onStart() {
                log(null);
                log("onStart()");
                timeStart = System.currentTimeMillis();
            }

            @Override
            public void onError(ExceptionHandle handle) {
                log("onError(" + handle.getMsg() + ")");
            }

            @Override
            public void onFinish() {
                long cast = System.currentTimeMillis() - timeStart;
                log("onFinish(cast=" + cast + ")");
            }
        }).request(new RxRequest.ResultCallback<BaseBean>() {
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

    private void getHttps() {
        mRxLife.add(RxRequest.create(FreeApi.api().https("哈哈")).listener(new RxRequest.RequestListener() {
            private long timeStart = 0;

            @Override
            public void onStart() {
                log(null);
                log("onStart()");
                timeStart = System.currentTimeMillis();
            }

            @Override
            public void onError(ExceptionHandle handle) {
                log("onError(" + handle.getMsg() + ")");
            }

            @Override
            public void onFinish() {
                long cast = System.currentTimeMillis() - timeStart;
                log("onFinish(cast=" + cast + ")");
            }
        }).request(new RxRequest.ResultCallback<BaseBean>() {
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
            tvLog.setText("");
        } else {
            Log.d(TAG, text);
            String textOld = tvLog.getText().toString();
            if (TextUtils.isEmpty(textOld)) {
                tvLog.setText(text);
            } else {
                tvLog.setText(tvLog.getText().toString() + "\n" + text);
            }
        }
    }
}
