# RxHttp

对RxJava2+Retrofit2+OkHttp3的封装，优雅实现接口请求和文件下载

[GitHub主页](https://github.com/goweii/RxHttp)

[Demo下载](https://github.com/goweii/RxHttp/raw/master/app/release/app-release.apk)



# 功能简介

- 网络请求（RxRequest）
  - 支持监听请求声明周期，如开始结束和网络错误
  - 支持多BaseUrl，可针对不同请求重定向
  - 支持针对不同请求设置不同缓存策略，如无网强制获取缓存，有网缓存有效10秒
  - 支持添加公共请求参数
  - 支持自定义异常处理和异常提示消息
- 文件下载（RxDownload）
  - 支持断点续传
  - 支持下载进度回调
  - 支持下载速度回调
  - 支持下载过程状态监听
  - 支持在仅保存下载路径未保存进度时自动恢复断点续传
  - 支持自动获取真实文件名



# 集成方式

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
   	api 'com.github.goweii:RxHttp:最新版本号'
   }
   ```



# 发起请求之RxRequest

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

定义ResponseBean< E>继承BaseResponse< E>，定义成员变量并实现方法。

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

    public interface Code{
        int SUCCESS = 200;
    }

    public interface Config {
        String BASE_URL = "http://api.apiopen.top/";
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

- #### init(Context)

  初始化RxHttp，建议在自定义Application中进行

- #### initRequest(RequestSetting)

  初始化RxRequest，建议在自定义Application中进行

- #### initDownload(DownloadSetting)

  初始化RxDownload，建议在自定义Application中进行

- #### request(Observable< R> )

  发起一个请求，同RxRequest.request(Observable< R> )方法

- #### download(String)

  新建一个下载任务，同RxDownload.create(String)方法

### RxLife

用于管理请求的生命周期，防止内存泄露。

- #### RxLife create()

  在页面的onCreate方法调用，会返回一个RxLife实例

- #### destroy()

  在页面的onDestroy方法调用，终止所有未完成的请求

- #### add(Disposable)

  当调用RxHttp.request(Observable)或者RxRequest.create(Observable)方法发起一个请求时，会返回一个Disposable对象，调用该方法将其添加至管理队列

### RequestSetting/DefaultRequestSetting

RxRequest的配置参数

- #### String getBaseUrl()

- 默认的BaseUrl

- #### Map<String, String> getMultiBaseUrl()

  其他用于重定向的BaseUrl，Map的Key值为添加重定向Header的Value值，Map的Value值为BaseUrl

- #### int getSuccessCode()

  请求成功后服务器返回的成功Code值

- #### int[] getMultiSuccessCode()

  请求成功后服务器返回的其他成功Code值

- #### long getTimeout()

  默认超时时长，单位毫秒

- #### long getConnectTimeout()

  设置0则取getTimeout()，单位毫秒

- #### long getReadTimeout()

  设置0则取getTimeout()，单位毫秒

- #### long getWriteTimeout()

  设置0则取getTimeout()，单位毫秒

- #### String getCacheDirName()

  缓存文件夹名

- #### long getCacheSize()

  缓存大小

- #### Map<String, String> getStaticPublicQueryParameter()

  拼接在url后面的公共请求参数，静态字符串，如版本号等

- #### Map<String, ParameterGetter> getDynamicPublicQueryParameter()

  拼接在url后面的公共请求参数，需要动态获取的，如用户名等

- #### < E extends ExceptionHandle> E getExceptionHandle()

  获取自定义异常处理器

- #### Interceptor[] getInterceptors()

  添加自定义拦截器

- #### Interceptor[] getNetworkInterceptors()

  添加自定义拦截器

### BaseResponse< E>

服务器响应体数据结构，可自定义字段名

- #### int getCode();

- #### void setCode(int);

- #### E getData();

- #### void setData(E);

- #### String getMsg();

- #### void setMsg(Stringg);

### BaseBean

响应体Data的数据结构，建议继承自这个类，实现了Serializable接口，提供toJson方法

- #### toJson()

  转为Json字符串

- #### toFormatJson()

  转为格式化后的Json字符串，及花括号换行加缩进

### ExceptionHandle

处理请求过程中的异常，可通过继承自定义。

- #### onGetCode(Throwable)

  重写该方法去返回异常对应的错误码

- #### onGetMsg(int)

  重写该方法去返回错误码对应的错误信息

### Api

强烈建议创建Api实例的类继承自该类。可在其中定义内部类接口管理常量数据，如：

```java
public class FreeApi extends Api {
    
	// 定义静态无参方法创建ApiService实例
    public static Service api() {
        return Api.api(Service.class);
    }

    public interface Code{
        // 定义服务器返回的各种成功失败的状态码
    }

    public interface Config {
        // 定义请求的各种配置信息，如BASE_URL/TIMEOUT等
    }

    public interface Service {
        // 定义Retrofit的API声明接口
    }
}
```

- #### Header内部类

  - ##### BASE_URL_REDIRECT

    用于BaseUrl的重定向

  - ##### CACHE_ALIVE_SECOND

    指定一个int值用于设置缓存有效时长（秒）。配置后，在无网时强制使用缓存数据，有网时，如果小于等于0则强制联网获取，大于0则在该时长内使用缓存，过期后联网获取。

- #### api(Class< T>)静态方法

  创建Api接口实例

### RxRequest

用于发起请求

- #### create(Observable< R>)

  创建实例，传入参数为一个可观察对象，应该为Api接口返回

- #### listener(RequestListener)

  监听请求的生命周期

  - ##### onStart()

    请求开始

  - ##### onError(ExceptionHandle)

    请求出错，请见ExceptionHandle

  - ##### onFinish()

    请求结束

- #### request(ResultCallback< E>)

  请求成功

  - ##### onSuccess(int, E)

    服务器返回成功code

  - ##### onFailed(int, String)

    服务器返回失败code

### JsonFieldUtils

创建Json结构的数据

### ParameterUtils

构建Map<String, RequestBody>



# 文件下载之RxDownload

## 使用方法

### 初始化

初始化操作可在Application中也可在应用启动页中进行

```java
RxHttp.init(this);
// 可选，未配置设置将自动采用DefaultDownloadSetting
RxHttp.initDownload(new DefaultDownloadSetting() {
            @Override
            public long getTimeout() {
                return 60000;
            }
        }); 
```

### 调用

```java
RxDownload mRxDownload = RxDownload.create(et_url.getText().toString())
        .setDownloadListener(new RxDownload.DownloadListener() {
            @Override
            public void onStarting(DownloadInfo info) {
                tv_start.setText("正在开始...");
            }

            @Override
            public void onDownloading(DownloadInfo info) {
                tv_start.setText("正在下载");
            }

            @Override
            public void onError(DownloadInfo info, Throwable e) {
                tv_start.setText("下载失败");
            }

            @Override
            public void onStopped(DownloadInfo info) {
                tv_start.setText("已停止");
            }

            @Override
            public void onCanceled(DownloadInfo info) {
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
            public void onProgress(float progress, long downloadLength, long contentLength) {
                pb_1.setProgress((int) (progress * 10000));
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

## 常用类说明

### RxHttp

用于初始化和设置

### DownloadSetting/DefaultDownloadSetting

RxDownload的设置

- #### String getBaseUrl()

  指定默认BaseUrl，传入一个合法的就可以了

- #### long getTimeout()

  指定超时时间，建议长一点，如60秒

- #### long getConnectTimeout()

  设置0则取getTimeout()，单位毫秒

- #### long getReadTimeout()

  设置0则取getTimeout()，单位毫秒

- #### long getWriteTimeout()

  设置0则取getTimeout()，单位毫秒

- #### String getSaveDirPath()

  指定默认的下载文件夹路径

- #### DownloadInfo.Mode getDefaultDownloadMode()

  获取保存路径的文件已存在但未保存下载进度时的默认模式

### DownloadInfo

用于保存下载信息，如需断点续传，需要自己保存以下几个必传项

- #### String url

  下载文件的链接**（必传项）**

- #### String saveDirPath

  自定义下载文件的保存目录**（断点续传时必传项）**

- #### String saveFileName

  自定义下载文件的保存文件名，需带后缀名**（断点续传时必传项）**

- #### long downloadLength

  已下载文件的长度**（断点续传时必传项）**

- #### long contentLength

  下载文件的总长度

- #### State state

  当前下载状态

  - ##### STARTING

    正在开始

  - ##### DOWNLOADING

    正在下载

  - ##### STOPPED

    未开始/已停止

  - ##### ERROR

    下载出错

  - ##### COMPLETION

    下载完成

- #### Mode mode

  获取保存路径的文件已存在但未保存下载进度时的模式

  - ##### APPEND

    追加

  - ##### REPLACE

    替换

  - ##### RENAME

    重命名

- #### create(String)

  创建一个下载对象，参数为url

- #### create(String, String, String)

  创建一个下载对象，参数为url/保存目录/文件名

- #### create(String, String, String, long, long)

  创建一个下载对象，参数为url/保存目录/文件名/已下载长度/总长度

### RxDownload

- #### create(DownloadInfo)

  用于新建一个下载任务

- #### setDownloadListener(DownloadListener)

  设置下载状态监听

  - ##### onStarting()

    正在开始，正在连接服务器

  - ##### onDownloading()

    正在下载

  - ##### onStopped()

    已停止，不会删除已下载部分，支持断点续传

  - ##### onCanceled()

    已取消，会删除已下载的部分文件，再次开始会重新下载

  - ##### onCompletion(DownloadInfo)

    下载完成

  - ##### onError(Throwable)

    下载出错

- #### setProgressListener(ProgressListener)

  - ##### onProgress(float)

    下载进度回调（0~1）

- #### setSpeedListener(SpeedListener)

  - ##### onSpeedChange(float, String)

    下载速度回调，两个值分别为每秒下载比特数和格式化后速度（如：1.2KB/s，3.24MB/s）

- #### start()

  开始下载/继续下载

- #### stop()

  停止下载，不会删除已下载部分，支持断点续传

- #### cancel()

  取消下载，会删除已下载的部分文件，再次开始会重新下载

### UnitFormatUtils

单位格式化工具

- #### calculateSpeed(long, float)

  计算速度

- #### formatSpeedPerSecond(float)

  格式化速度（如：1.12MB/s，628KB/s）

- #### formatSpeed(float,TimeUnit)

  格式化速度（如：1.12MB/s，628KB/s）

- #### formatBytesLength(float)

  格式化比特值（如：12.1KB,，187.24MB，154GB）

- #### formatTimeUnit(TimeUnit)

  格式化时间单位（如：秒为s，毫秒为ms）