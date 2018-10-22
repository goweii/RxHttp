package per.goweii.rxhttp.request;

import android.support.annotation.NonNull;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import per.goweii.rxhttp.core.RxHttp;
import per.goweii.rxhttp.core.RxLife;
import per.goweii.rxhttp.request.base.BaseResponse;
import per.goweii.rxhttp.request.exception.ApiException;
import per.goweii.rxhttp.request.exception.ExceptionHandle;

/**
 * 描述：网络请求
 *
 * @author Cuizhen
 * @date 2018/9/9
 */
public class RxRequest<T, R extends BaseResponse<T>> {

    private final Observable<R> mObservable;
    private ResultCallback<T> mCallback = null;
    private RequestListener mListener = null;
    private RxLife mRxLife = null;

    private RxRequest(Observable<R> observable) {
        mObservable = observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public static <T, R extends BaseResponse<T>> RxRequest<T, R> create(@NonNull Observable<R> observable) {
        return new RxRequest<>(observable);
    }

    /**
     * 添加请求生命周期的监听
     */
    public RxRequest<T, R> listener(RequestListener listener) {
        mListener = listener;
        return this;
    }

    /**
     * 用于中断请求，管理请求生命周期
     *
     * @param rxLife 详见{@link RxLife}
     */
    public RxRequest<T, R> autoLife(RxLife rxLife) {
        mRxLife = rxLife;
        return this;
    }

    /**
     * 发起请求并设置成功回调
     *
     * @return Disposable 用于中断请求，管理请求生命周期
     * 详见{@link RxLife}
     */
    public Disposable request(@NonNull ResultCallback<T> callback) {
        mCallback = callback;
        Disposable disposable = mObservable.subscribe(new Consumer<BaseResponse<T>>() {
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
                } else {
                    if (mListener != null) {
                        ExceptionHandle handle = RxHttp.getRequestSetting().getExceptionHandle();
                        if (handle == null) {
                            handle = new ExceptionHandle();
                        }
                        handle.handle(e);
                        mListener.onError(handle);
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
            }
        });
        if (mRxLife != null) {
            mRxLife.add(disposable);
        }
        return disposable;
    }

    private boolean isSuccess(int code) {
        if (code == RxHttp.getRequestSetting().getSuccessCode()) {
            return true;
        }
        int[] codes = RxHttp.getRequestSetting().getMultiSuccessCode();
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

    public interface ResultCallback<E> {
        void onSuccess(int code, E data);

        void onFailed(int code, String msg);
    }

    public interface RequestListener {
        void onStart();

        void onError(ExceptionHandle handle);

        void onFinish();
    }
}
