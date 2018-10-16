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
    public float speed;
    public State state;

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
         * 正在停止
         */
        STOPPING,
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
        FINISH
    }
}
