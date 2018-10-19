package per.goweii.rxhttp.download.utils;

import android.text.TextUtils;

import java.io.File;

import per.goweii.rxhttp.core.RxHttp;
import per.goweii.rxhttp.core.utils.SDCardUtils;
import per.goweii.rxhttp.download.DownloadInfo;
import per.goweii.rxhttp.download.exception.SaveFileBrokenPointException;

/**
 * 描述：
 *
 * @author Cuizhen
 * @date 2018/10/19
 */
public class DownloadInfoChecker {

    public static void checkDownloadLength(DownloadInfo info){
        if (info.downloadLength <= 0){
            info.downloadLength = 0;
            File file = createFile(info.saveDirPath, info.saveFileName);
            if (file != null && file.exists()) {
                if (info.mode == DownloadInfo.Mode.APPEND) {
                    info.downloadLength = file.length();
                } else if (info.mode == DownloadInfo.Mode.REPLACE) {
                    file.delete();
                } else {
                    info.saveFileName = renameFileName(info.saveFileName);
                }
            }
        } else {
            File file = createFile(info.saveDirPath, info.saveFileName);
            if (file != null && file.exists()) {
                if (info.downloadLength != file.length()) {
                    throw new SaveFileBrokenPointException();
                }
            } else {
                info.downloadLength = 0;
            }
        }
    }
    
    private static String renameFileName(String fileName){
        String nameLeft;
        String nameDivide;
        String nameRight;
        int index = fileName.lastIndexOf(".");
        if (index >= 0) {
            nameLeft = fileName.substring(0, index);
            nameDivide = ".";
            nameRight = fileName.substring(index + 1, fileName.length());
        } else {
            nameLeft = fileName;
            nameDivide = "";
            nameRight = "";
        }
        return nameLeft + "_new" + nameDivide + nameRight;
    }

    private static File createFile(String dirPath, String fileName){
        if (TextUtils.isEmpty(dirPath) || TextUtils.isEmpty(fileName)) {
            return null;
        }
        return new File(dirPath, fileName);
    }

    public static void checkFilePath(DownloadInfo info){
        if (TextUtils.isEmpty(info.saveDirPath)) {
            info.saveDirPath = RxHttp.getDownloadSetting().getSaveDirPath();
        }
        if (TextUtils.isEmpty(info.saveDirPath)) {
            info.saveDirPath = SDCardUtils.getDownloadCacheDir();
        }
        if (TextUtils.isEmpty(info.saveFileName)) {
            info.saveFileName = System.currentTimeMillis() + ".rxdownload";
        }
    }

}
