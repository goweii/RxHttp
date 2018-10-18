package per.goweii.rxhttp.download;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import per.goweii.rxhttp.core.RxHttp;
import per.goweii.rxhttp.core.utils.SDCardUtils;
import per.goweii.rxhttp.download.base.DownloadInfo;
import per.goweii.rxhttp.download.exception.SaveFileBrokenPointException;
import per.goweii.rxhttp.download.exception.SaveFileDirMakeException;
import per.goweii.rxhttp.download.exception.SaveFileWriteException;
import per.goweii.rxhttp.download.interceptor.RealNameInterceptor;
import per.goweii.rxhttp.download.utils.UnitFormatUtils;

/**
 * 描述：网络请求
 *
 * @author Cuizhen
 * @date 2018/9/9
 */
public class RxDownload implements RealNameInterceptor.RealNameCallback {

    private final DownloadInfo mInfo;
    private DownloadListener mDownloadListener = null;
    private ProgressListener mProgressListener = null;
    private SpeedListener mSpeedListener = null;
    private Disposable mDisposableDownload = null;
    private Disposable mDisposableSpeed = null;

    private RxDownload(DownloadInfo info) {
        mInfo = info;
    }

    public static RxDownload create(@NonNull String url) {
        return new RxDownload(DownloadInfo.create(url));
    }

    public static RxDownload create(@NonNull String url, String saveDirName, String saveFileName) {
        return new RxDownload(DownloadInfo.create(url, saveDirName, saveFileName));
    }

    public static RxDownload create(@NonNull String url, String saveDirName, String saveFileName, @IntRange(from = 0) long downloadLength) {
        return new RxDownload(DownloadInfo.create(url, saveDirName, saveFileName, downloadLength));
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

    public void start() {
        if (mDisposableDownload != null && !mDisposableDownload.isDisposed()) {
            return;
        }
        mDisposableDownload = DownloadClientManager.getService(mInfo.downloadLength, mInfo.contentLength, this).download(mInfo.url)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        if (mInfo.contentLength == 0) {
                            mInfo.contentLength = responseBody.contentLength();
                        } else if (mInfo.downloadLength + responseBody.contentLength() != mInfo.contentLength) {
                            throw new SaveFileBrokenPointException();
                        }
                        checkSaveFilePath(mInfo);
                        File file = createSaveFile(mInfo);
                        if (mInfo.downloadLength != file.length()) {
                            throw new SaveFileBrokenPointException();
                        }
                        mInfo.state = DownloadInfo.State.DOWNLOADING;
                        notifyDownloading();
                        createSpeedObserver();
                        write(responseBody.byteStream(), file);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable e) throws Exception {
                        mInfo.state = DownloadInfo.State.ERROR;
                        if (mDownloadListener != null) {
                            mDownloadListener.onError(e);
                        }
                        cancelSpeedObserver();
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        mInfo.state = DownloadInfo.State.COMPLETION;
                        if (mDownloadListener != null) {
                            mDownloadListener.onCompletion(mInfo);
                        }
                        cancelSpeedObserver();
                    }
                }, new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mInfo.state = DownloadInfo.State.STARTING;
                        if (mDownloadListener != null) {
                            mDownloadListener.onStarting();
                        }
                    }
                });
    }

    public void stop() {
        if (mDisposableDownload != null && !mDisposableDownload.isDisposed()) {
            mDisposableDownload.dispose();
        }
        mDisposableDownload = null;
        if (mDownloadListener != null) {
            mDownloadListener.onStopped();
        }
        cancelSpeedObserver();
    }

    public void cancel() {
        if (mDisposableDownload != null && !mDisposableDownload.isDisposed()) {
            mDisposableDownload.dispose();
        }
        mDisposableDownload = null;
        deleteSaveFile(mInfo);
        if (mDownloadListener != null) {
            mDownloadListener.onCanceled();
        }
        cancelSpeedObserver();
    }

    private void checkSaveFilePath(DownloadInfo info) {
        if (TextUtils.isEmpty(info.saveDirName)) {
            info.saveDirName = RxHttp.getDownloadSetting().getSaveDirName();
            if (TextUtils.isEmpty(info.saveDirName)) {
                info.saveDirName = SDCardUtils.getDownloadCacheDir();
            }
        }
        if (TextUtils.isEmpty(info.saveFileName)) {
            info.saveFileName = System.currentTimeMillis() + ".rxdownload";
        }
    }

    private File createSaveFile(DownloadInfo info) throws SaveFileDirMakeException {
        File file = new File(info.saveDirName, info.saveFileName);
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                throw new SaveFileDirMakeException();
            }
        }
        return file;
    }

    private void deleteSaveFile(DownloadInfo info) {
        try {
            if (new File(info.saveDirName, info.saveFileName).delete()) {
                mInfo.downloadLength = 0;
            }
        } catch (Exception ignore) {
        }
    }

    private void write(InputStream is, File file) throws SaveFileWriteException {
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
            Observable.empty()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Object>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(Object o) {
                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onComplete() {
                            mDownloadListener.onDownloading();
                        }
                    });
        }
    }

    private void notifyProgress() {
        if (mProgressListener != null) {
            Observable.empty()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Object>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(Object o) {
                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onComplete() {
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

    @Override
    public void onRealName(@NonNull String realName) {
        mInfo.saveFileName = realName;
    }

    public interface DownloadListener {
        void onStarting();

        void onDownloading();

        void onStopped();

        void onCanceled();

        void onCompletion(DownloadInfo info);

        void onError(Throwable e);
    }

    public interface ProgressListener {
        void onProgress(float progress, long downloadLength, long contentLength);
    }

    public interface SpeedListener {
        void onSpeedChange(float bytesPerSecond, String speedFormat);
    }
}
