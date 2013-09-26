package at.gv.egiz.pdfas.common.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


public class PDFUtils {

    private static final Logger logger = LoggerFactory.getLogger(PDFUtils.class);


    private static final byte[] signature_pattern = new byte[] {
            (byte) 0x0A, (byte) 0x2F, (byte) 0x43, (byte) 0x6F,   // ./Co
            (byte) 0x6E, (byte) 0x74, (byte) 0x65, (byte) 0x6E,   // nten
            (byte) 0x74, (byte) 0x73, (byte) 0x20, (byte) 0x0A,   // ts .
            (byte) 0x2F, (byte) 0x42, (byte) 0x79, (byte) 0x74,   // /Byt
            (byte) 0x65, (byte) 0x52, (byte) 0x61, (byte) 0x6E,   // eRan
            (byte) 0x67, (byte) 0x65, (byte) 0x20, (byte) 0x5B,   // ge [

    };

    private static final byte range_seperation = (byte) 0x20;
    private static final byte range_end = (byte) 0x5D;

    private static int extractASCIIInteger(byte[] data, int offset) {
       int nextsepp = nextSeperator(data, offset);

        if(nextsepp < offset) {
            return -1;
        }

        String asciiString = new String(data, offset, nextsepp - offset);

        logger.debug("Extracted " + asciiString);

        return Integer.parseInt(asciiString);
    }

    private static int nextSeperator(byte[] data, int offset) {
        for(int i = offset; i < data.length; i++) {
            if(data[i] == range_seperation) {
                return i;
            } else if(data[i] == range_end) {
                return i;
            }
        }
        return -2;
    }

    public static int[] extractSignatureByteRange(byte[] rawPdfData) {
        int i = 0;
        for(i = rawPdfData.length - 1; i >= 0; i--) {
            if(rawPdfData[i] == signature_pattern[0] &&
                    i+signature_pattern.length < rawPdfData.length) {
                boolean match = true;
                for(int j = 0; j < signature_pattern.length; j++) {

                    if(rawPdfData[i+j] != signature_pattern[j]) {
                        match = false;
                        break;
                    }
                }

                if(match) {

                    int offset = i + signature_pattern.length;
                    List<Integer> byteRange = new ArrayList<Integer>();
                    while(offset > 0) {
                        byteRange.add(extractASCIIInteger(rawPdfData, offset));
                        offset = nextSeperator(rawPdfData, offset);
                        if(rawPdfData[offset] == range_end) {
                            break;
                        }
                        offset++;
                    }
                    int[] range = new int[byteRange.size()];
                    for(int j = 0; j < byteRange.size(); j++) {
                        range[j] = byteRange.get(j);
                    }
                    return range;
                }
            }
        }
        return null;
    }

    public static void checkPDFPermissions(PDDocument doc) {
        // TODO: Check permission for document
    }
}