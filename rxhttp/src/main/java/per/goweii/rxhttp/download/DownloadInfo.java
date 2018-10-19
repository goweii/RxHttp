package per.goweii.rxhttp.download;

import per.goweii.rxhttp.core.RxHttp;

/**
 * 描述：
 *
 * @author Cuizhen
 * @date 2018/10/15
 */
public class DownloadInfo {

    public String url;
    public String saveDirPath;
    public String saveFileName;
    public long downloadLength;
    public long contentLength;
    public State state;
    public Mode mode;

    private DownloadInfo(String url, String saveDirPath, String saveFileName, long downloadLength, long contentLength) {
        this.url = url;
        this.saveDirPath = saveDirPath;
        this.saveFileName = saveFileName;
        this.downloadLength = downloadLength;
        this.contentLength = contentLength;
        this.state = State.STOPPED;
        this.mode = RxHttp.getDownloadSetting().getDefaultDownloadMode();
    }

    public static DownloadInfo create(String url){
        return create(url, null, null);
    }

    public static DownloadInfo create(String url, String saveDirPath, String saveFileName){
        return create(url, saveDirPath, saveFileName, 0, 0);
    }

    public static DownloadInfo create(String url, String saveDirPath, String saveFileName, long downloadLength, long contentLength){
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
