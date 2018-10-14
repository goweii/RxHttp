package per.goweii.rxhttp;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import per.goweii.rxhttp.download.DownloadApi;

/**
 * 描述：网络请求
 *
 * @author Cuizhen
 * @date 2018/9/9
 */
public class RxDownload {

    private final Observable<InputStream> mObservable;
    private DownloadListener mListener;

    private RxDownload(String url) {
        mObservable = RxHttp.getApi(DownloadApi.class)
                .download("", url)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .map(new Function<ResponseBody, InputStream>() {
                    @Override
                    public InputStream apply(ResponseBody responseBody) throws Exception {
                        return responseBody.byteStream();
                    }
                })
                .observeOn(Schedulers.computation())
                .doOnNext(new Consumer<InputStream>() {
                    @Override
                    public void accept(InputStream inputStream) throws Exception {
                        writeFile(inputStream, null);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static RxDownload create(@NonNull String url) {
        return new RxDownload(url);
    }

    public void download(@NonNull DownloadListener listener) {
    }

    private void writeFile(InputStream inputString, String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            byte[] b = new byte[1024];
            int len;
            while ((len = inputString.read(b)) != -1) {
                fos.write(b,0,len);
            }
            inputString.close();
            fos.close();
        } catch (FileNotFoundException e) {
            mListener.onError(e);
        } catch (IOException e) {
            mListener.onError(e);
        }
    }

    public interface DownloadListener {
        void onStart();
        void onProgress(float progress);
        void onError(Exception e);
        void onFinish();
    }
}
