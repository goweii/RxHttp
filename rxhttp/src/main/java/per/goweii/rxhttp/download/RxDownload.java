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
import per.goweii.rxhttp.download.exception.SaveFileDirMakeException;
import per.goweii.rxhttp.download.exception.SaveFileWriteException;
import per.goweii.rxhttp.download.interceptor.RealNameInterceptor;

/**
 * 描述：网络请求
 *
 * @author Cuizhen
 * @date 2018/9/9
 */
public class RxDownload implements RealNameInterceptor.RealNameCallback {

    private final DownloadInfo mDownloadInfo;
    private DownloadListener mDownloadListener = null;
    private ProgressListener mProgressListener = null;
    private SpeedListener mSpeedListener = null;
    private Disposable mDisposableDownload = null;
    private Disposable mDisposableSpeed = null;

    private RxDownload(DownloadInfo downloadInfo) {
        mDownloadInfo = downloadInfo;
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
        mDisposableDownload = DownloadClientManager.getService(mDownloadInfo.downloadLength, mDownloadInfo.contentLength, this).download(mDownloadInfo.url)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        mDownloadInfo.contentLength = responseBody.contentLength();
                        checkSaveFilePath(mDownloadInfo);
                        File file = createSaveFile(mDownloadInfo);
                        if (mDownloadInfo.downloadLength != file.length()) {
                            file.delete();
                            file = createSaveFile(mDownloadInfo);
                        }
                        mDownloadInfo.state = DownloadInfo.State.DOWNLOADING;
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
                        mDownloadInfo.state = DownloadInfo.State.ERROR;
                        if (mDownloadListener != null) {
                            mDownloadListener.onError(e);
                        }
                        cancelSpeedObserver();
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        mDownloadInfo.state = DownloadInfo.State.COMPLETION;
                        if (mDownloadListener != null) {
                            mDownloadListener.onCompletion(mDownloadInfo);
                        }
                        cancelSpeedObserver();
                    }
                }, new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mDownloadInfo.state = DownloadInfo.State.STARTING;
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
            deleteSaveFile(mDownloadInfo);
        }
        mDisposableDownload = null;
        if (mDownloadListener != null) {
            mDownloadListener.onCanceled();
        }
        cancelSpeedObserver();
    }

    private void checkSaveFilePath(DownloadInfo info) throws SaveFileDirMakeException {
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
                mDownloadInfo.downloadLength = 0;
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
                mDownloadInfo.downloadLength += len;
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
                            float progress = (float) mDownloadInfo.downloadLength / (float) mDownloadInfo.contentLength;
                            mProgressListener.onProgress(progress);
                        }
                    });
        }
    }

    private void createSpeedObserver() {
        if (mDisposableSpeed != null && !mDisposableSpeed.isDisposed()) {
            return;
        }
        mDisposableSpeed = Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.computation())
                .map(new Function<Long, Float>() {
                    private long lastDownloadLength = 0;

                    @Override
                    public Float apply(Long aLong) throws Exception {
                        float speed = (float) mDownloadInfo.downloadLength - (float) lastDownloadLength;
                        lastDownloadLength = mDownloadInfo.downloadLength;
                        return speed;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Float>() {

                    @Override
                    public void accept(Float speed) throws Exception {
                        if (mSpeedListener != null) {
                            mSpeedListener.onSpeedChange(speed, formatSpeed(speed));
                        }
                    }
                });
    }

    private String formatSpeed(float bytePerSecond) {
        float speed;
        String unit;
        if (bytePerSecond < 1024) {
            // 0B~1KB
            unit = "B";
            speed = bytePerSecond;
        } else if (bytePerSecond < 1024 * 1024) {
            // 1KB~1MB
            unit = "KB";
            speed = bytePerSecond / (1024);
        } else {
            // 1MB~
            unit = "MB";
            speed = bytePerSecond / (1024 * 1024);
        }
        return String.format("%.2f" + unit + "/s", speed);
    }

    private void cancelSpeedObserver() {
        if (mDisposableSpeed != null && !mDisposableSpeed.isDisposed()) {
            mDisposableSpeed.dispose();
        }
        mDisposableSpeed = null;
    }

    @Override
    public void onRealName(@NonNull String realName) {
        mDownloadInfo.saveFileName = realName;
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
        void onProgress(float progress);
    }

    public interface SpeedListener {
        void onSpeedChange(float bytePerSecond, String speedFormat);
    }
}
