package per.goweii.rxhttp.download;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import per.goweii.rxhttp.core.RxHttp;
import per.goweii.rxhttp.core.utils.SDCardUtils;
import per.goweii.rxhttp.download.base.DownloadInfo;
import per.goweii.rxhttp.download.exception.SaveFileDirMakeException;
import per.goweii.rxhttp.download.exception.SaveFileWriteException;
import per.goweii.rxhttp.download.listener.ProgressListener;
import per.goweii.rxhttp.download.listener.RealNameListener;

/**
 * 描述：网络请求
 *
 * @author Cuizhen
 * @date 2018/9/9
 */
public class RxDownload implements ProgressListener, RealNameListener {

    private DownloadListener mListener = null;
    private Disposable mDisposable = null;
    private final DownloadInfo mDownloadInfo;

    private RxDownload(String url) {
        mDownloadInfo = new DownloadInfo();
        mDownloadInfo.url = url;
    }

    public static RxDownload create(@NonNull String url) {
        return new RxDownload(url);
    }

    public RxDownload saveTo(@Nullable String dirName, @Nullable String fileName) {
        mDownloadInfo.saveDirName = dirName;
        mDownloadInfo.saveFileName = fileName;
        return this;
    }

    public RxDownload listener(@NonNull DownloadListener listener) {
        mListener = listener;
        return this;
    }

    public void start() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            return;
        }
        mDisposable = DownloadClientManager.getService(mDownloadInfo.downloadLength, mDownloadInfo.contentLength,
                this, this).download(mDownloadInfo.url)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        mDownloadInfo.contentLength = responseBody.contentLength();
                        checkSaveFilePath(mDownloadInfo);
                        File file = createSaveFile(mDownloadInfo);
                        write(responseBody.byteStream(), file);
                        mDownloadInfo.state = DownloadInfo.State.DOWNLOADING;
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
                        if (mListener != null) {
                            mListener.onError(e);
                        }
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        mDownloadInfo.state = DownloadInfo.State.FINISH;
                        if (mListener != null) {
                            mListener.onFinish();
                        }
                    }
                }, new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mDownloadInfo.state = DownloadInfo.State.STARTING;
                        if (mListener != null) {
                            mListener.onStart();
                        }
                    }
                });
    }

    public void stop() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
            mDisposable = null;
        }
    }

    public void cancel() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
            mDisposable = null;
            deleteSaveFile(mDownloadInfo);
        }
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

    private void deleteSaveFile(DownloadInfo info) throws SaveFileDirMakeException {
        if (new File(info.saveDirName, info.saveFileName).delete()) {
            mDownloadInfo.downloadLength = 0;
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

    @SuppressLint("CheckResult")
    @Override
    public void onUpdate(long readBytes, long totalBytes, boolean isDown) {
        if (isDown) {
            return;
        }
        mDownloadInfo.downloadLength = readBytes;
        if (mListener != null) {
            float progress = (float) readBytes / (float) totalBytes;
            Observable.just(progress)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Float>() {
                        @Override
                        public void accept(Float progress) throws Exception {
                            mListener.onProgress(progress);
                        }
                    });
        }
    }

    @Override
    public void getRealName(String realName) {
        if (TextUtils.isEmpty(mDownloadInfo.saveFileName)) {
            mDownloadInfo.saveFileName = realName;
        }
    }

    public interface DownloadListener {
        void onStart();

        void onProgress(float progress);

        void onError(Throwable e);

        void onFinish();
    }
}
