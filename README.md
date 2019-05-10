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




# 发起请求之RxRequest

## 使用说明

### 一、初始化

1. 新建网络请求配置类继承RequestSetting或DefaultRequestSetting，并复写部分方法。

```java
public class RxHttpRequestSetting extends DefaultRequestSetting {

    @NonNull
    @Override
    public String getBaseUrl() {
        return Config.BASE_URL;
    }

    @Override
    public int getSuccessCode() {
        return 200;
    }
}
```

2. 在Application中初始化并传入配置类实例

```java
RxHttp.init(this);
RxHttp.initRequest(new RxHttpRequestSetting());
```

### 二、定义公共请求头拦截器

```java
public class PublicHeadersInterceptor implements Interceptor {
    private static String TIME = "";
    private static String TOKEN = "";

    public static void updateTime(String time) {
        PublicHeadersInterceptor.TIME = time;
    }

    public static void updateToken(String token) {
        PublicHeadersInterceptor.TOKEN = token;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        request = request.newBuilder()
                .header(Constant.PUBLIC_HEADER_TIME_NAME, TIME)
                .header(Constant.PUBLIC_HEADER_SIGN_NAME, getSign(request))
                .build();
        return chain.proceed(request);
    }

    private String getSign(Request request){
        return MD5Coder.encode(request.url().url().toString() + "?token=" + TOKEN);
    }
}
```

### 三、定义公共参数拦截器

```java
public class PublicParamsInterceptor implements Interceptor {
    private static final String GET = "GET";
    private static final String POST = "POST";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String method = request.method();
        if (TextUtils.equals(method, GET)) {
            request = addForGet(request);
        } else if (TextUtils.equals(method, POST)) {
            request = addForPost(request);
        }
        return chain.proceed(request);
    }

    private Request addForGet(Request request) {
        List<Param> params = getPublicParams();
        HttpUrl httpUrl = request.url();
        HttpUrl.Builder httpUrlBuilder = httpUrl.newBuilder();
        for (int i = 0; i < httpUrl.querySize(); i++) {
            String name = httpUrl.queryParameterName(i);
            String value = httpUrl.queryParameterValue(i);
            params.add(new Param(name, value));
            httpUrlBuilder.removeAllQueryParameters(name);
        }
        JsonObjUtils json = JsonObjUtils.create();
        for (Param param : params) {
            json.add(param.getKey(), param.getValue());
        }
        LogUtils.i("PublicParamsInterceptor", "data=" + json.toJson());
        httpUrlBuilder.setQueryParameter(Constant.PUBLIC_PARAM_KEY, json.toJson());
        return request.newBuilder()
                .url(httpUrlBuilder.build())
                .build();
    }

    private Request addForPost(Request request) {
        RequestBody requestBody = request.body();
        if (requestBody == null) {
            return request;
        } else if (requestBody instanceof FormBody) {
            List<Param> params = getPublicParams();
            FormBody formBody = (FormBody) requestBody;
            for (int i = 0; i < formBody.size(); i++) {
                params.add(new Param(formBody.name(i), formBody.value(i)));
            }
            JsonObjUtils json = JsonObjUtils.create();
            for (Param param : params) {
                json.add(param.getKey(), param.getValue());
            }
            LogUtils.i("PublicParamsInterceptor", "data=" + json.toJson());
            FormBody.Builder formBodyBuilder = new FormBody.Builder()
                    .add(Constant.PUBLIC_PARAM_KEY, json.toJson());
            return request.newBuilder()
                    .post(formBodyBuilder.build())
                    .build();
        } else if (requestBody instanceof MultipartBody) {
            return request;
        } else {
            try {
                if (requestBody.contentLength() == 0) {
                    List<Param> params = getPublicParams();
                    JsonObjUtils json = JsonObjUtils.create();
                    for (Param param : params) {
                        json.add(param.getKey(), param.getValue());
                    }
                    LogUtils.i("PublicParamsInterceptor", "data=" + json.toJson());
                    FormBody.Builder formBodyBuilder = new FormBody.Builder()
                            .add(Constant.PUBLIC_PARAM_KEY, json.toJson());
                    return request.newBuilder()
                            .post(formBodyBuilder.build())
                            .build();
                } else {
                    return request;
                }
            } catch (IOException e) {
                return request;
            }
        }
    }

    private List<Param> getPublicParams() {
        List<Param> params = new ArrayList<>();
        params.add(new Param(Constant.PUBLIC_PARAM_SYSTEM_KEY, Constant.PUBLIC_PARAM_SYSTEM_VALUE));
        params.add(new Param(Constant.PUBLIC_PARAM_VERSION_KEY, String.valueOf(AppInfoUtils.getVersionCode())));
        params.add(new Param(Constant.PUBLIC_PARAM_USER_ID_KEY, UserUtils.getInstance().getUserId()));
        params.add(new Param(Constant.PUBLIC_PARAM_USER_DEVICE_KEY, DeviceIdUtils.getId()));
        params.add(new Param(Constant.PUBLIC_PARAM_JPUSH_DEVICE_KEY, JPushHelper.getId()));
        return params;
    }
}
```

### 四、定义响应体结构

定义ResponseBean< E >继承BaseResponse< E >，定义成员变量并实现方法。

```java
public class ResponseBean<E> implements BaseResponse<E> {
    @SerializedName(value = "code"/*, alternate = {"status"}*/)
    private int code;
    @SerializedName(value = "data"/*, alternate = {"result"}*/)
    private E data;
    @SerializedName(value = "msg"/*, alternate = {"message"}*/)
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

### 五、定义接口数据结构

```java
public class TimeBean extends BaseBean {
    private String token;
    private String time;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
```

### 六、定义Api接口类

1. 新建子类继承自Api
2. 定义一个内部类Service声明请求
3. 定义静态无参方法返回Api.api(Service.class)创建Api实例

```java
public class FreeApi extends Api {

    public static Service api() {
        return Api.api(Service.class);
    }

    public interface Config {
        String BASE_URL = Config.HTTP_HOST + Config.HTTP_VERSION;
        long HTTP_TIMEOUT = 5000;
    }

    public interface Code{
        int TIME_OUT = 1000;                     // 请求延迟
        int REQUEST_ERROR = 1001;                // 请求方式错误
        int ILLEGAL_PARAMETER = 1002;            // 非法参数

        int SUCCESS = 2000;                      // 获取信息成功
        int SUCCESS_OLD = 104;                   // 获取信息成功 老版本的
        int SUCCESS_NO_DATA = 2001;              // 暂无相关数据

        int FAILED = 3000;                       // 获取信息失败
        int PHONE_EXIST = 3001;                  // 该手机号已注册过
        int PASSWARD_ERROR = 3002;               // 密码错误
        int PHONE_ILLEGAL = 3003;                // 手机号不合法
        int PHONE_NOT_BIND = 3004;               // 请绑定手机号
        int PHONE_NOT_REGIST = 3005;             // 该手机号未注册
        
        int ACCOUNT_NOT_EXIST = 4001;            // 账号不存在
        int ACCOUNT_EXCEPTION = 4002;            // 账号异常,请重新登录
        int ACCOUNT_FROZEN = 4003;               // 该账户已被冻结,请联系管理员
        int ACCOUNT_DELETED = 4004;              // 该账户已被管理员删除

        int ERROR = 5000;                        // 访问异常
        int ERROR_NET = 5001;                    // 网络异常
    }

    public interface Service {
        @GET("public/time")
        Observable<ResponseBean<TimeBean>> getTime();
    }
}
```

### 七、定义请求回调

````java
public interface RequestBackListener<T> {
    void onStart();
    void onSuccess(int code, T data);
    void onFailed(int code, String msg);
    void onNoNet();
    void onError(Throwable e);
    void onFinish();
}
````

### 八、封装BaseRequest基类

封装签名的获取和响应回调的处理逻辑。

在调用正式接口之前，我们可能需要先调用一个获取时间戳的接口，将时间戳接口返回的时间戳和签名字段添加到正式接口的公共参数或请求头中，才能发起正式请求。所以在该基类中进行封装，抽取公共代码。

如果时间戳接口返回了版本更新字段，需要版本号等判断请求的执行流程，如强制更新且不需要继续执行正式接口，可在此处自行处理。

```java
public class BaseRequest {
    protected static <T> Disposable requestWithSign(@NonNull RequestCallback<T> observable, @NonNull RequestBackListener<T> callback) {
        return request(ProjectApi.api().getTime()
                .flatMap(new Function<ResponseBean<TimeBean>, ObservableSource<ResponseBean<T>>>() {
                    @Override
                    public Observable<ResponseBean<T>> apply(ResponseBean<TimeBean> bean) {
                        PublicHeadersInterceptor.updateTime(bean.getData().getTime());
                        PublicHeadersInterceptor.updateToken(bean.getData().getToken());
                        return observable.request().subscribeOn(Schedulers.io());
                    }
                }), callback);
    }

    protected static <T> Disposable request(@NonNull Observable<ResponseBean<T>> observable, @NonNull RequestBackListener<T> callback) {
        return RxRequest.create(observable)
                .listener(new RxRequest.RequestListener() {
                    @Override
                    public void onStart() {
                        callback.onStart();
                    }

                    @Override
                    public void onError(ExceptionHandle handle) {
                        handle.getException().printStackTrace();
                        if (handle.getCode() == ExceptionHandle.Code.NET) {
                            ToastMaker.showShort(R.string.http_no_net);
                            callback.onNoNet();
                            callback.onFailed(ProjectApi.Code.ERROR_NET, ResUtils.getString(R.string.http_no_net));
                        } else {
                            callback.onError(handle.getException());
                            callback.onFailed(ProjectApi.Code.ERROR, ResUtils.getString(R.string.http_error));
                        }
                    }

                    @Override
                    public void onFinish() {
                        callback.onFinish();
                    }
                })
                .request(new RxRequest.ResultCallback<T>() {
                    @Override
                    public void onSuccess(int code, T data) {
                        callback.onSuccess(code, data);
                    }

                    @Override
                    public void onFailed(int code, String msg) {
                        if (code == ProjectApi.Code.ACCOUNT_NOT_EXIST ||
                                code == ProjectApi.Code.ACCOUNT_EXCEPTION ||
                                code == ProjectApi.Code.ACCOUNT_FROZEN ||
                                code == ProjectApi.Code.ACCOUNT_DELETED) {
                            ForceOfflineReceiver.send(code, msg);
                        }
                        callback.onFailed(code, msg);
                    }
                });
    }

    protected interface RequestCallback<T> {
        Observable<ResponseBean<T>> request();
    }
}
```

### 九、新建请求类

```java
public class PublicRequest extends BaseRequest {
    /**
     * 获取系统时间
     */
    public static Disposable getTime(final RequestBackListener<TimeBean> listener) {
        return request(ProjectApi.api().getTime(), listener);
    }

    /**
     * 获取反馈类型
     */
    public static Disposable other(final RequestBackListener<OtherBean> listener) {
        return requestWithSign(new RequestCallback<OtherAean>() {
            @Override
            public Observable<ResponseBean<OtherBean>> request() {
                return ProjectApi.api().otherApi();
            }
        }, listener);
    }
}
```

### 十、发起请求

你可以在Activity或者Fragment中发起请求，也可以在你的Presenter层中发起请求，只需要注意请求生命周期的管理。

使用时分为3步处理：

1. 在onCreate方法中（如果是Presenter中使用应该在其绑定到视图时）调用RxLife.create()方法，该方法会返回一个RxLife实例mRxLife。
2. 在onDestroy方法中（如果是Presenter中使用应该在其从视图解除绑定时）调用mRxLife.destroy()方法，该方法会自动中断所有未完成的请求，防止内存泄漏。
3. 发起一个请求，并调用mRxLife.add(Disposable)添加至管理队列。

**下面将以MVP模式进行举例说明。**

1. 在P层基类中添加RxLife的创建和销毁，并提供addToRxLife方法。

```java
public abstract class MvpPresenter<V extends MvpView> {
    protected Context context;
    private V baseView;
    private RxLife rxLife;

    void onCreate(V baseView) {
        this.baseView = baseView;
        context = baseView.getContext();
        rxLife = RxLife.create();
    }

    void onDestroy() {
        baseView = null;
        context = null;
        rxLife.destroy();
        rxLife = null;
    }

    public RxLife getRxLife() {
        return rxLife;
    }

    public void addToRxLife(Disposable disposable) {
        if (rxLife != null) {
            rxLife.add(disposable);
        }
    }

    public V getBaseView() {
        return baseView;
    }

    public boolean isAttachView() {
        return baseView != null;
    }

    public Context getContext() {
        return context;
    }
}
```

2. 新建P层，发起请求。

```java
public class OtherPresenter extends MvpPresenter<FeedbackView> {
    public void other() {
        addToRxLife(PublicRequest.other(new RequestBackListener<OtherBean>() {
            @Override
            public void onStart() {
                showLoading();
            }

            @Override
            public void onSuccess(int code, OtherBean data) {
                if (isAttachView()) {
                    getBaseView().otherSuccess(code, data);
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                if (isAttachView()) {
                    getBaseView().otherFail(code, msg);
                }
            }

            @Override
            public void onNoNet() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onFinish() {
                dismissLoading();
            }
        }));
    }
}
```

3. 新建V层接口，并在Activity或Fragment实现接口回调，进行数据展示。

```java
public interface OtherView extends MvpView {
    void otherSuccess(int code, OtherBean data);
    void otherFail(int code, String msg);
}
```



## API

### JsonObjUtils

创建JSONObject对象并生成Json字符串。

### RequestBodyUtils

创建RequestBody，针对POST请求。

如图片上传接口

```java
/**
 * 键		值
 * img		File
 * content	String
 */
@Multipart
@POST("public/img")
Observable<ResponseBean<UploadImgBean>> uploadImg(@PartMap Map<String, RequestBody> img);
```

发起请求如下

```java
public static Disposable uploadImg(String content, File imgFile, final RequestBackListener<UploadImgBean> listener) {
    return requestWithSign(new RequestCallback<UploadImgBean>() {
        @Override
        public Observable<ResponseBean<UploadImgBean>> request() {
            Map<String, RequestBody> map = RequestBodyUtils.builder()
                .add("content", content)
                .add("img", imgFile)
                .build();
            return ProjectApi.api().uploadImg(map);
        }
    }, listener);
}
```

### HttpsCompat

主要提供7个静态方法，用于实现证书忽略和开启Android4.4及以下对TLS1.2的支持。

```java
/**
 * 忽略证书的验证，这样请求就和HTTP一样，失去了安全保障，不建议使用
 */
public static void ignoreSSLForOkHttp(OkHttpClient.Builder builder)
    
/**
 * 开启HttpsURLConnection对TLS1.2的支持
 */
public static void enableTls12ForOkHttp(OkHttpClient.Builder builder)
    
/**
 * 忽略证书的验证，这样请求就和HTTP一样，失去了安全保障，不建议使用
 * 应在使用HttpsURLConnection之前调用，建议在application中
 */
public static void ignoreSSLForHttpsURLConnection()
    
/**
 * 开启HttpsURLConnection对TLS1.2的支持
 * 应在使用HttpsURLConnection之前调用，建议在application中
 */
public static void enableTls12ForHttpsURLConnection()
    
/**
 * 获取开启TLS1.2的SSLSocketFactory
 * 建议在android4.4及以下版本调用
 */
public static SSLSocketFactory getEnableTls12SSLSocketFactory()
    
/**
 * 获取忽略证书的HostnameVerifier
 * 与{@link #getIgnoreSSLSocketFactory()}同时配置使用
 */
public static HostnameVerifier getIgnoreHostnameVerifier()
    
/**
 * 获取忽略证书的SSLSocketFactory
 * 与{@link #getIgnoreHostnameVerifier()}同时配置使用
 */
public static SSLSocketFactory getIgnoreSSLSocketFactory()
```



## 常见问题

### 在Android9.0及以上系统HTTP请求无响应

官方资料在框架安全性变更提及，如果应用以 Android 9 或更高版本为目标平台则默认情况下启用网络传输层安全协议 (TLS)，即 isCleartextTrafficPermitted() 函数返回 false。 如果您的应用需要为特定域名启用明文，您必须在应用的网络安全性配置中针对这些域名将 cleartextTrafficPermitted 显式设置为 true。

因此解决办法有2种：

第一种，启用HTTP，允许明文传输（不建议采用）

1. 在资源文件夹res/xml下面创建network_security_config.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="true">
        <trust-anchors>
            <certificates src="system" />
        </trust-anchors>
    </base-config>
</network-security-config>
```

2. 在清单文件AndroidManifest.xml的application标签里面设置networkSecurityConfig属性引用。

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest ... >
    <application
        android:networkSecurityConfig="@xml/network_security_config">
    </application>
</manifest>
```

第二种，所有接口采用HTTPS协议（建议采用）

此方法需确保后台正确配置，如配置后仍有无法访问，且提示证书异常，请检查后台配置。

### HTTPS请求访问时提示证书异常

该情况一般为后台未正确配置证书。请检查后台配置。

在测试时，我们可以暂时选择忽略证书，这样请求就和HTTP一样，但会失去安全保障，不允许在正式发布时使用。

**可直接使用HttpsCompat工具类。**

实现代码如下：

```java
public static void ignoreSSLForOkHttp(OkHttpClient.Builder builder) {
    builder.hostnameVerifier(getIgnoreHostnameVerifier())
            .sslSocketFactory(getIgnoreSSLSocketFactory());
}

public static void ignoreSSLForHttpsURLConnection() {
    HttpsURLConnection.setDefaultHostnameVerifier(getIgnoreHostnameVerifier());
    HttpsURLConnection.setDefaultSSLSocketFactory(getIgnoreSSLSocketFactory());
}

/**
 * 获取忽略证书的HostnameVerifier
 * 与{@link #getIgnoreSSLSocketFactory()}同时配置使用
 */
private static HostnameVerifier getIgnoreHostnameVerifier() {
    return new HostnameVerifier() {
        @Override
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }
    };
}

/**
 * 获取忽略证书的SSLSocketFactory
 * 与{@link #getIgnoreHostnameVerifier()}同时配置使用
 */
private static SSLSocketFactory getIgnoreSSLSocketFactory() {
    try {
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, getTrustManager(), new SecureRandom());
        return sslContext.getSocketFactory();
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}

private static TrustManager[] getTrustManager() {
    return new TrustManager[]{
        new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }
        }
    };
}
```

### HTTPS请求在Android4.4及以下无法访问

服务器已正确配置SSL证书，且已打开TLS1.1和TLS1.2，但是在Android4.4及以下无法访问网络。是因为在Android4.4及以下版本默认不支持TLS1.2，需要开启对TLS1.2的支持。代码如下：

```java
public static void enableTls12ForHttpsURLConnection() {
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
        SSLSocketFactory ssl = getEnableTls12SSLSocketFactory();
        if (ssl != null) {
            HttpsURLConnection.setDefaultSSLSocketFactory(ssl);
        }
    }
}

public static void enableTls12ForOkHttp(OkHttpClient.Builder builder) {
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
        SSLSocketFactory ssl = HttpsCompat.getEnableTls12SSLSocketFactory();
        if (ssl != null) {
            builder.sslSocketFactory(ssl);
        }
    }
}

public static SSLSocketFactory getEnableTls12SSLSocketFactory() {
    try {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, null, null);
        return new Tls12SocketFactory(sslContext.getSocketFactory());
    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
}

private static class Tls12SocketFactory extends SSLSocketFactory {
    private static final String[] TLS_SUPPORT_VERSION = {"TLSv1.1", "TLSv1.2"};

    private final SSLSocketFactory delegate;

    private Tls12SocketFactory(SSLSocketFactory base) {
        this.delegate = base;
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return delegate.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return delegate.getSupportedCipherSuites();
    }

    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        return patch(delegate.createSocket(s, host, port, autoClose));
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        return patch(delegate.createSocket(host, port));
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
        return patch(delegate.createSocket(host, port, localHost, localPort));
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        return patch(delegate.createSocket(host, port));
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return patch(delegate.createSocket(address, port, localAddress, localPort));
    }

    private Socket patch(Socket s) {
        if (s instanceof SSLSocket) {
            ((SSLSocket) s).setEnabledProtocols(TLS_SUPPORT_VERSION);
        }
        return s;
    }
}
```

### Glide在Android4.4及以下图片加载失败

原因同上，需要自定义Glide的AppGlideModule，传入支持TLS1.2的OkHttpClient。

```java
@GlideModule
public class CustomAppGlideModule extends AppGlideModule {

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(getOkHttpClient()));
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }

    private static OkHttpClient getOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpsCompat.enableTls12ForOkHttp(builder);
        return builder.build();
    }
}
```



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
