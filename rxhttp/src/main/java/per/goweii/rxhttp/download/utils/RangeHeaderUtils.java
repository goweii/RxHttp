package per.goweii.rxhttp.download.utils;

import per.goweii.rxhttp.download.exception.RangeLengthIsZeroException;

/**
 * 描述：
 *
 * @author Cuizhen
 * @date 2018/10/19
 */
public class RangeHeaderUtils {

    public static String getValue(long startBytes, long endBytes) throws RangeLengthIsZeroException {
        long start = startBytes > 0 ? startBytes : 0;
        long end = endBytes > 0 ? endBytes : 0;
        if (start > 0 && end > 0 && end <= start){
            throw new RangeLengthIsZeroException();
        }
        return "bytes=" + start + "-" + (end == 0 ? "" : end);
    }
}
