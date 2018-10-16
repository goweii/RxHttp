package per.goweii.rxhttp.download.listener;

/**
 * 描述：
 *
 * @author Cuizhen
 * @date 2018/10/16
 */
public interface ProgressListener {
    void onUpdate(long readBytes, long totalBytes, boolean isDown);
}
