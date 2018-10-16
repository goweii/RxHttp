package per.goweii.rxhttp.download.exception;

/**
 * @author Cuizhen
 * @date 2018/10/12
 */
public class SaveFileWriteException extends RuntimeException {
    public SaveFileWriteException() {
        super("下载保存的文件写入失败");
    }
}
