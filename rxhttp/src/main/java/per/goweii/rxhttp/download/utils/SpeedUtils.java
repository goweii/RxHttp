package per.goweii.rxhttp.download.utils;

import android.support.annotation.NonNull;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

/**
 * 描述：
 *
 * @author Cuizhen
 * @date 2018/10/18
 */
public class SpeedUtils {

    private static class Format{
        private static final DecimalFormat SPEED_FORMAT =  new DecimalFormat("#.##");
    }

    public static float calculateSpeed(long increment, float duration) {
        return (float) increment / duration;
    }

    public static String formatSpeedPerSecond(float bytePerSecond) {
        return formatSpeed(bytePerSecond, TimeUnit.SECONDS);
    }

    public static String formatSpeed(float speedBytes, @NonNull TimeUnit timeUnit) {
        float speed;
        String unit1;
        if (speedBytes < 1024) {
            // 0B~1KB
            unit1 = "B";
            speed = speedBytes;
        } else if (speedBytes < 1024 * 1024) {
            // 1KB~1MB
            unit1 = "KB";
            speed = speedBytes / (1024);
        } else {
            // 1MB~
            unit1 = "MB";
            speed = speedBytes / (1024 * 1024);
        }
        String unit2 = getTimeUnit(timeUnit);
        return Format.SPEED_FORMAT.format(speed) + unit1 + "/" + unit2;
    }

    private static String getTimeUnit(TimeUnit timeUnit){
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
