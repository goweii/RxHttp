package per.goweii.rxhttp.download.exception;

/**
 * @author Cuizhen
 * @date 2018/10/12
 */
public class SaveFileBrokenPointException extends RuntimeException {
    public SaveFileBrokenPointException() {
        super("文件已下载部分与断点续传不符");
    }
}
