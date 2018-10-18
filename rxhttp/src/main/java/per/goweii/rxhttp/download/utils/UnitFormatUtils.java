package per.goweii.rxhttp.download.utils;

import android.support.annotation.NonNull;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

/**
 * 描述：单位格式化
 *
 * @author Cuizhen
 * @date 2018/10/18
 */
public class UnitFormatUtils {

    private static class Format{
        private static final DecimalFormat TWO =  new DecimalFormat("#.##");
    }

    public static float calculateSpeed(long increment, float duration) {
        return (float) increment / duration;
    }

    public static String formatSpeedPerSecond(float bytePerSecond) {
        return formatSpeed(bytePerSecond, TimeUnit.SECONDS);
    }

    public static String formatSpeed(float speedBytes, @NonNull TimeUnit timeUnit) {
        return formatBytesLength(speedBytes) + "/" + formatTimeUnit(timeUnit);
    }
    
    public static String formatBytesLength(float bytes){
        float length;
        String unit;
        if (bytes < 1024L) {
            // 0B~1KB
            unit = "B";
            length = bytes;
        } else if (bytes < 1024L * 1024L) {
            // 1KB~1MB
            unit = "KB";
            length = bytes / (1024L);
        } else if (bytes < 1024L * 1024L * 1024L){
            // 1MB~1GB
            unit = "MB";
            length = bytes / (1024L * 1024L);
        } else if (bytes < 1024L * 1024L * 1024L * 1024L){
            // 1GB~1TB
            unit = "GB";
            length = bytes / (1024L * 1024L * 1024L);
        } else {
            // 1TB~
            unit = "TB";
            length = bytes / (1024L * 1024L * 1024L * 1024L);
        }
        return Format.TWO.format(length) + unit;
    }

    public static String formatTimeUnit(TimeUnit timeUnit){
        if (timeUnit == null) {
            return "-";
        }
        if (timeUnit == TimeUnit.NANOSECONDS) {
            return "ns";
        } else if (timeUnit == TimeUnit.MICROSECONDS){
            return "us";
        } else if (timeUnit == TimeUnit.MILLISECONDS){
            return "ms";
        } else if (timeUnit == TimeUnit.SECONDS){
            return "s";
        } else if (timeUnit == TimeUnit.MINUTES){
            return "m";
        } else if (timeUnit == TimeUnit.HOURS){
            return "h";
        } else if (timeUnit == TimeUnit.DAYS){
            return "d";
        } else {
            return "-";
        }
    }
}
