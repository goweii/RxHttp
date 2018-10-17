# RxHttp

对RxJava2+Retrofit2+Okhttp的封装，优雅实现接口请求和文件下载



# 功能简介

- 网络请求（RxRequest）
  - 监听请求声明周期，如开始结束和网络错误等
  - 支持多BaseUrl，可针对不同请求重定向
  - 支持无网强制获取缓存数据
  - 支持添加公共请求参数
  - 支持自定义异常处理和异常提示消息
- 文件下载（RxDownload）
  - 支持断点续传
  - 支持下载进度回调
  - 支持下载速度回调



# 集成方式

## 添加依赖

1. 在Project的**build.gradle**添加仓库地址

   ```java
   allprojects {
   	repositories {
   		...
   		maven { url 'https://www.jitpack.io' }
   	}
   }
   ```

2. 在Model:app的**build.gradle**添加框架依赖

   最新版本是多少，看下[Releases](https://github.com/goweii/RxHttp/releases)

   ```java
   dependencies {
   	implementation 'com.github.goweii:RxHttp:最新版本号'
   }
   ```



# RxRequest

## 使用方法

### 初始化

初始化操作可在Application中也可在应用启动页中进行

```java
RxHttp.init(this);
RxHttp.initRequest(new DefaultRequestSetting() {
            @Override
            public String getBaseUrl() {
                return Config.BASE_URL;
            }

            @Override
            public int getSuccessCode() {
                return 200;
            }
        });
```

### 定义响应体结构

定义ResponseBean<E>继承BaseResponse<E>，定义成员变量并实现方法。

```java
public class ResponseBean<E> implements BaseResponse<E> {
    @SerializedName(value = "code", alternate = {"status"})
    private int code;
    @SerializedName(value = "data", alternate = {"result"})
    private E data;
    @SerializedName(value = "msg", alternate = {"message"})
    private String msg;

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public E getData() {
        return data;
    }

    @Override
    public void setData(E data) {
        this.data = data;
    }

    @Override
    public String getMsg() {
        return msg;
    }

    @Override
    public void setMsg(String msg) {
        this.msg = msg;
    }
}
```

### 定义接口数据结构

```java
public class TimeBean extends BaseBean {
    private String time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
```

### 定义Api接口类

1. 新建子类继承自Api
2. 定义一个内部类Service声明请求（即Retrifit的CategoryService）
3. 定义静态无参方法返回Api.api(Service.class)创建Api实例

```java
public class FreeApi extends Api {

    public static Service api() {
        return Api.api(Service.class);
    }

    public interface Service {
        @GET("public/time")
        Observable<ResponseBean<TimeBean>> getTime();
    }
}
```

### 发起请求

你可以在Activity或者Fragment中发起请求，也可以在你的Presenter层中发起请求，只需要注意请求生命周期的管理。

使用时分为3步处理：

1. 在onCreate方法中（如果是Presenter中使用应该在其绑定到视图时）调用RxLife.create()方法，该方法会返回一个RxLife实例mRxLife。
2. 在onDestroy方法中（如果是Presenter中使用应该在其从视图解除绑定时）调用mRxLife.destroy()方法，该方法会自动中断所有未完成的请求，防止内存泄漏。
3. 调用RxHttp.request(Observable)或者RxRequest.create(Observable)方法发起一个请求，会返回一个Disposable对象，调用mRxLife.add(Disposable)添加至管理队列。

```java
private RxLife mRxLife;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_test_request);
    mRxLife = RxLife.create();
}

@Override
protected void onDestroy() {
    super.onDestroy();
    mRxLife.destroy();
}

private void getTime() {
    mRxLife.add(RxHttp.request(FreeApi.api().getTime()).listener(new RxRequest.RequestListener() {
        private long timeStart = 0;

        @Override
        public void onDownloading() {
            log(null);
            log("onDownloading()");
            timeStart = System.currentTimeMillis();
        }

        @Override
        public void onError(ExceptionHandle handle) {
            log("onError(" + handle.getMsg() + ")");
        }

        @Override
        public void onStopped() {
            long cast = System.currentTimeMillis() - timeStart;
            log("onStopped(cast=" + cast + ")");
        }
    }).request(new RxRequest.ResultCallback<TimeBean>() {
        @Override
        public void onSuccess(int code, TimeBean data) {
            log("onSuccess(code=" + code + ",data=" + data.toFormatJson() + ")");
        }

        @Override
        public void onFailed(int code, String msg) {
            log("onFailed(code=" + code + ",msg=" + msg + ")");
        }
    }));
}
```

## 常用类说明

### RxHttp

用于初始化和设置

### RequestSetting/DefaultRequestSetting

RxRequest的设置

### ExceptionHandle

处理请求过程中的异常，可通过继承自定义。

- onGetCode(Throwable)：重写该方法去返回异常对应的错误码
- onGetMsg(int)：重写该方法去返回错误码对应的错误信息

### Api

- 内部类Header
  - BASE_URL_REDIRECT：用于BaseUrl的重定向
  - CACHE_CONTROL_AGE：用于指定无网读取缓存
- 静态方法api(Class<T> clazz)：创建Api接口实例

### RxRequest

用于发起请求

- create(Observable<R>)：创建实例，传入参数为一个可观察对象，应该为Api接口返回
- listener(RequestListener)：监听请求的生命周期
  - onDownloading()：请求开始
  - onError(ExceptionHandle)：请求出错，请见ExceptionHandle
  - onStopped()：请求结束
- request(ResultCallback<E>)：请求成功
  - onSuccess(int, E)：服务器返回成功code
  - onFailed(int, String)：服务器返回失败code

### JsonFieldUtils

创建Json结构的数据

### ParameterUtils

构建Map<String, RequestBody>



# RxDownload

## 使用方法

### 初始化

初始化操作可在Application中也可在应用启动页中进行

```java
RxHttp.init(this);
RxHttp.initDownload(new DefaultDownloadSetting() {
            @Override
            public long getTimeout() {
                return 60000;
            }
        });
```

### 调用方式

```java
RxDownload mRxDownload = RxDownload.create(et_url.getText().toString())
        .setDownloadListener(new RxDownload.DownloadListener() {
            @Override
            public void onStarting() {
                tv_start.setText("正在开始...");
            }

            @Override
            public void onDownloading() {
                tv_start.setText("正在下载");
            }

            @Override
            public void onError(Throwable e) {
                tv_start.setText("下载失败");
            }

            @Override
            public void onStopped() {
                tv_start.setText("已停止");
            }

            @Override
            public void onCanceled() {
                tv_start.setText("已取消");
                pb_1.setProgress(0);
            }

            @Override
            public void onCompletion(DownloadInfo info) {
                tv_start.setText("下载成功");
            }
        })
        .setProgressListener(new RxDownload.ProgressListener() {
            @Override
            public void onProgress(float progress) {
                pb_1.setProgress((int) (progress * 100));
            }
        })
        .setSpeedListener(new RxDownload.SpeedListener() {
            @Override
            public void onSpeedChange(float bytePerSecond, String speedFormat) {
                tv_start.setText("正在下载(" + speedFormat + ")");
            }
        });

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
```