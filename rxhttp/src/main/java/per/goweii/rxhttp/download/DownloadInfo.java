package per.goweii.rxhttp.download;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import per.goweii.rxhttp.core.RxHttp;

/**
 * 描述：
 *
 * @author Cuizhen
 * @date 2018/10/15
 */
public class DownloadInfo {

    @NonNull
    public String url;
    @Nullable
    public String saveDirPath;
    @Nullable
    public String saveFileName;
    @IntRange(from = 0)
    public long downloadLength;
    @IntRange(from = 0)
    public long contentLength;
    @NonNull
    public State state;
    @NonNull
    public Mode mode;

    private DownloadInfo(@NonNull String url, @Nullable String saveDirPath, @Nullable String saveFileName,
                         @IntRange(from = 0) long downloadLength, @IntRange(from = 0) long contentLength) {
        this.url = url;
        this.saveDirPath = saveDirPath;
        this.saveFileName = saveFileName;
        this.downloadLength = downloadLength;
        this.contentLength = contentLength;
        this.state = State.STOPPED;
        this.mode = RxHttp.getDownloadSetting().getDefaultDownloadMode();
    }

    public static DownloadInfo create(@NonNull String url){
        return create(url, null, null);
    }

    public static DownloadInfo create(@NonNull String url,
                                      @Nullable String saveDirPath,
                                      @Nullable String saveFileName){
        return create(url, saveDirPath, saveFileName, 0, 0);
    }

    public static DownloadInfo create(@NonNull String url,
                                      @Nullable String saveDirPath,
                                      @Nullable String saveFileName,
                                      @IntRange(from = 0) long downloadLength,
                                      @IntRange(from = 0) long contentLength){
        return new DownloadInfo(url, saveDirPath, saveFileName, downloadLength, contentLength);
    }

    /**
     * 如果路径文件存在，但是断点续传时未传入已下载长度信息，此时的写入模式
     */
    public enum Mode{
        /**
         * 追加
         */
        APPEND,
        /**
         * 替换
         */
        REPLACE,
        /**
         * 重命名
         */
        RENAME
    }

    public enum State{
        /**
         * 正在开始
         */
        STARTING,
        /**
         * 正在下载
         */
        DOWNLOADING,
        /**
         * 已停止
         */
        STOPPED,
        /**
         * 下载出错
         */
        ERROR,
        /**
         * 下载完成
         */
        COMPLETION
    }
}
