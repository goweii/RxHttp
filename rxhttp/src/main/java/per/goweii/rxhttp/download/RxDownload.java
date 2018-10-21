package per.goweii.rxhttp.download;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import per.goweii.rxhttp.download.exception.RangeLengthIsZeroException;
import per.goweii.rxhttp.download.exception.SaveFileBrokenPointException;
import per.goweii.rxhttp.download.exception.SaveFileDirMakeException;
import per.goweii.rxhttp.download.exception.SaveFileWriteException;
import per.goweii.rxhttp.download.interceptor.DownloadResponseBody;
import per.goweii.rxhttp.download.utils.DownloadInfoChecker;
import per.goweii.rxhttp.download.utils.RxNotify;
import per.goweii.rxhttp.download.utils.UnitFormatUtils;

/**
 * 描述：网络请求
 *
 * @author Cuizhen
 * @date 2018/9/9
 */
public class RxDownload {

    private final DownloadInfo mInfo;
    private DownloadListener mDownloadListener = null;
    private ProgressListener mProgressListener = null;
    private SpeedListener mSpeedListener = null;
    private Disposable mDisposableDownload = null;
    private Disposable mDisposableSpeed = null;

    private RxDownload(DownloadInfo info) {
        mInfo = info;
    }

    public static RxDownload create(@NonNull DownloadInfo info) {
        return new RxDownload(info);
    }

    public RxDownload setDownloadListener(@NonNull DownloadListener listener) {
        mDownloadListener = listener;
        return this;
    }

    public RxDownload setProgressListener(@NonNull ProgressListener listener) {
        mProgressListener = listener;
        return this;
    }

    public RxDownload setSpeedListener(@NonNull SpeedListener listener) {
        mSpeedListener = listener;
        return this;
    }

    public DownloadInfo getDownloadInfo() {
        return mInfo;
    }

    public void start() {
        if (mDisposableDownload != null && !mDisposableDownload.isDisposed()) {
            return;
        }
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                DownloadInfoChecker.checkDownloadLength(mInfo);
                DownloadInfoChecker.checkContentLength(mInfo);
                emitter.onNext("bytes=" + mInfo.downloadLength + "-" + (mInfo.contentLength == 0 ? "" : mInfo.contentLength));
            }
        }).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).flatMap(new Function<String, ObservableSource<ResponseBody>>() {
            @Override
            public ObservableSource<ResponseBody> apply(String range) throws Exception {
                return DownloadClientManager.getService().download(range, mInfo.url);
            }
        }).doOnNext(new Consumer<ResponseBody>() {
            @Override
            public void accept(ResponseBody responseBody) throws Exception {
                if (mInfo.contentLength == 0) {
                    mInfo.contentLength = mInfo.downloadLength + responseBody.contentLength();
                } else if (mInfo.downloadLength + responseBody.contentLength() != mInfo.contentLength) {
                    throw new SaveFileBrokenPointException();
                }
                DownloadInfoChecker.checkDirPath(mInfo);
                if (TextUtils.isEmpty(mInfo.saveFileName)) {
                    Class clazz = responseBody.getClass();
                    Field field = clazz.getDeclaredField("delegate");
                    field.setAccessible(true);
                    DownloadResponseBody body = (DownloadResponseBody) field.get(responseBody);
                    mInfo.saveFileName = body.getRealName();
                }
                DownloadInfoChecker.checkFileName(mInfo);
                mInfo.state = DownloadInfo.State.DOWNLOADING;
                notifyDownloading();
                write(responseBody.byteStream(), createSaveFile(mInfo));
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnDispose(new Action() {
            @Override
            public void run() throws Exception {
                cancelSpeedObserver();
            }
        }).subscribe(new Observer<ResponseBody>() {
            @Override
            public void onSubscribe(Disposable d) {
                mDisposableDownload = d;
                mInfo.state = DownloadInfo.State.STARTING;
                if (mDownloadListener != null) {
                    mDownloadListener.onStarting(mInfo);
                }
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                mInfo.state = DownloadInfo.State.COMPLETION;
                if (mDownloadListener != null) {
                    mDownloadListener.onCompletion(mInfo);
                }
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof RangeLengthIsZeroException) {
                    mInfo.state = DownloadInfo.State.COMPLETION;
                    if (mDownloadListener != null) {
                        mDownloadListener.onCompletion(mInfo);
                    }
                } else {
                    mInfo.state = DownloadInfo.State.ERROR;
                    if (mDownloadListener != null) {
                        mDownloadListener.onError(mInfo, e);
                    }
                }
            }

            @Override
            public void onComplete() {
            }
        });
    }

    public void stop() {
        if (mDisposableDownload != null && !mDisposableDownload.isDisposed()) {
            mDisposableDownload.dispose();
            mDisposableDownload = null;
        }
        mInfo.state = DownloadInfo.State.STOPPED;
        if (mDownloadListener != null) {
            mDownloadListener.onStopped(mInfo);
        }
    }

    public void cancel() {
        if (mDisposableDownload != null && !mDisposableDownload.isDisposed()) {
            mDisposableDownload.dispose();
            mDisposableDownload = null;
        }
        deleteSaveFile(mInfo);
        mInfo.state = DownloadInfo.State.STOPPED;
        if (mDownloadListener != null) {
            mDownloadListener.onCanceled(mInfo);
        }
    }

    private File createSaveFile(DownloadInfo info) throws SaveFileDirMakeException {
        File file = new File(info.saveDirPath, info.saveFileName);
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                throw new SaveFileDirMakeException();
            }
        }
        return file;
    }

    private void deleteSaveFile(DownloadInfo info) {
        try {
            if (new File(info.saveDirPath, info.saveFileName).delete()) {
                mInfo.downloadLength = 0;
            }
        } catch (Exception ignore) {
        }
    }

    private void write(InputStream is, File file) throws SaveFileWriteException {
        createSpeedObserver();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, true);
            byte[] buffer = new byte[2048];
            int len;
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                mInfo.downloadLength += len;
                notifyProgress();
            }
            fos.flush();
        } catch (IOException e) {
            throw new SaveFileWriteException();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void notifyDownloading() {
        if (mDownloadListener != null) {
            RxNotify.runOnUiThread(new RxNotify.Action() {
                @Override
                public void run() {
                    mDownloadListener.onDownloading(mInfo);
                }
            });
        }
    }

    private void notifyProgress() {
        if (mProgressListener != null) {
            RxNotify.runOnUiThread(new RxNotify.Action() {
                @Override
                public void run() {
                    float progress = (float) mInfo.downloadLength / (float) mInfo.contentLength;
                    mProgressListener.onProgress(progress, mInfo.downloadLength, mInfo.contentLength);
                }
            });
        }
    }

    private void createSpeedObserver() {
        if (mDisposableSpeed != null && !mDisposableSpeed.isDisposed()) {
            return;
        }
        mDisposableSpeed = Observable.interval(1, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.computation())
                .map(new Function<Long, Float>() {
                    private long lastDownloadLength = 0;

                    @Override
                    public Float apply(Long ms) throws Exception {
                        float bytesPerSecond = UnitFormatUtils.calculateSpeed(mInfo.downloadLength - lastDownloadLength, 1);
                        lastDownloadLength = mInfo.downloadLength;
                        return bytesPerSecond;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Float>() {
                    @Override
                    public void accept(Float speedPerSecond) throws Exception {
                        if (mSpeedListener != null) {
                            mSpeedListener.onSpeedChange(speedPerSecond, UnitFormatUtils.formatSpeedPerSecond(speedPerSecond));
                        }
                    }
                });
    }

    private void cancelSpeedObserver() {
        if (mDisposableSpeed != null && !mDisposableSpeed.isDisposed()) {
            mDisposableSpeed.dispose();
        }
        mDisposableSpeed = null;
    }

    public interface DownloadListener {
        void onStarting(DownloadInfo info);

        void onDownloading(DownloadInfo info);

        void onStopped(DownloadInfo info);

        void onCanceled(DownloadInfo info);

        void onCompletion(DownloadInfo info);

        void onError(DownloadInfo info, Throwable e);
    }

    public interface ProgressListener {
        void onProgress(float progress, long downloadLength, long contentLength);
    }

    public interface SpeedListener {
        void onSpeedChange(float bytesPerSecond, String speedFormat);
    }
}
