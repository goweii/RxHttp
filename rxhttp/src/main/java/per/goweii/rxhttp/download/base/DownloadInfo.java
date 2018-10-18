package per.goweii.rxhttp.download.base;

/**
 * 描述：
 *
 * @author Cuizhen
 * @date 2018/10/15
 */
public class DownloadInfo {

    public String url;
    public String saveDirName;
    public String saveFileName;
    public long downloadLength;
    public long contentLength;
    public State state;

    private DownloadInfo(String url, String saveDirName, String saveFileName, long downloadLength) {
        this.url = url;
        this.saveDirName = saveDirName;
        this.saveFileName = saveFileName;
        this.downloadLength = downloadLength;
    }

    public static DownloadInfo create(String url){
        return create(url, null, null);
    }

    public static DownloadInfo create(String url, String saveDirName, String saveFileName){
        return create(url, saveDirName, saveFileName, 0);
    }

    public static DownloadInfo create(String url, String saveDirName, String saveFileName, long downloadLength){
        return new DownloadInfo(url, saveDirName, saveFileName, downloadLength);
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
