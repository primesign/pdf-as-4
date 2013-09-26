package at.gv.egiz.pdfas.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: afitzek
 * Date: 8/29/13
 * Time: 9:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class StreamUtils {

    public static byte[] inputStreamToByteArray(InputStream stream) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int readBytes = 0;

        while((readBytes = stream.read(buffer)) != -1) {
            bos.write(buffer, 0, readBytes);
        }
        stream.close();
        bos.close();
        return bos.toByteArray();
    }
}
