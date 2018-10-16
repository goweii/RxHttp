package per.goweii.rxhttp.download.exception;

/**
 * @author Cuizhen
 * @date 2018/10/15
 */
public class NullDownloadSettingException extends RuntimeException {
    public NullDownloadSettingException() {
        super("DownloadSetting未设置");
    }
}
