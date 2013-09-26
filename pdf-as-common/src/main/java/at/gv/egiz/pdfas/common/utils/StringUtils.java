package at.gv.egiz.pdfas.common.utils;

import java.util.Formatter;

/**
 * Created with IntelliJ IDEA.
 * User: afitzek
 * Date: 8/28/13
 * Time: 12:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class StringUtils {

    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);

        Formatter formatter = new Formatter(sb);
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return sb.toString();
    }

    public static String extractLastID(String id) {
        int lastIDX = id.lastIndexOf('.');
        String result = id;
        if(lastIDX > 0) {
            result = id.substring(lastIDX+1);
        }
        return result;
    }
}
