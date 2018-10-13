package per.goweii.rxhttp;

import android.support.annotation.NonNull;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import per.goweii.rxhttp.base.BaseResponse;
import per.goweii.rxhttp.exception.ApiException;
import per.goweii.rxhttp.exception.NetConnectException;
import per.goweii.rxhttp.utils.NetUtils;

/**
 * 描述：网络请求
 *
 * @author Cuizhen
 * @date 2018/9/9
 */
public class RxRequest<T, R extends BaseResponse<T>> {

    private final Observable<R> mObservable;
    private RequestCallback<T> mCallback;
    private RequestListener mListener;

    private RxRequest(Observable<R> observable) {
        mObservable = observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public static <T, R extends BaseResponse<T>> RxRequest<T, R> create(@NonNull Observable<R> observable) {
        return new RxRequest<>(observable);
    }

    public RxRequest<T, R> listener(RequestListener listener){
        mListener = listener;
        return this;
    }

    public Disposable request(@NonNull RequestCallback<T> callback) {
        mCallback = callback;
        return mObservable.subscribe(new Consumer<BaseResponse<T>>() {
            @Override
            public void accept(BaseResponse<T> bean) throws Exception {
                if (!isSuccess(bean.getCode())) {
                    throw new ApiException(bean.getCode(), bean.getMsg());
                }
                mCallback.onSuccess(bean.getCode(), bean.getData());
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable e) throws Exception {
                if (e instanceof ApiException) {
                    ApiException apiException = (ApiException) e;
                    mCallback.onFailed(apiException.getCode(), apiException.getMsg());
                } else if (e instanceof NetConnectException) {
                    if (mListener != null) {
                        mListener.onNoNet();
                    }
                }
                /*else if (e instanceof SocketTimeoutException){
                } else if (e instanceof HttpException){
                } else if (e instanceof ConnectException || e instanceof UnknownHostException){
                } else if (e instanceof InterruptedIOException){
                } else if (e instanceof JsonParseException || e instanceof ParseException || e instanceof JSONException){
                }*/
                else {
                    if (mListener != null) {
                        mListener.onError(e);
                    }
                }
                if (mListener != null) {
                    mListener.onFinish();
                }
            }
        }, new Action() {
            @Override
            public void run() throws Exception {
                if (mListener != null) {
                    mListener.onFinish();
                }
            }
        }, new Consumer<Disposable>() {
            @Override
            public void accept(Disposable d) throws Exception {
                if (mListener != null) {
                    mListener.onStart();
                }
                if (!NetUtils.isConnected()) {
                    throw new NetConnectException();
                }
            }
        });
    }

    private boolean isSuccess(int code) {
        if (code == RxHttp.getSetting().getSuccessCode()) {
            return true;
        }
        int[] codes = RxHttp.getSetting().getOtherSuccessCode();
        if (codes == null || codes.length == 0) {
            return false;
        }
        for (int i : codes) {
            if (code == i) {
                return true;
            }
        }
        return false;
    }

    public interface RequestCallback<E> {
        void onSuccess(int code, E data);
        void onFailed(int code, String msg);
    }

    public interface RequestListener {
        void onStart();
        void onNoNet();
        void onError(Throwable e);
        void onFinish();
    }
}
