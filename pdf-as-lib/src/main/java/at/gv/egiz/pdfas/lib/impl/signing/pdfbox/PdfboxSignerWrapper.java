package at.gv.egiz.pdfas.lib.impl.signing.pdfbox;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.exceptions.SignatureException;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.utils.StreamUtils;
import at.gv.egiz.pdfas.common.utils.StringUtils;
import at.gv.egiz.pdfas.lib.api.sign.IPlainSigner;

public class PdfboxSignerWrapper implements SignatureInterface {

    private static final Logger logger = LoggerFactory.getLogger(PdfboxSignerWrapper.class);

    private IPlainSigner signer;
    private PDSignature signature;

    public PdfboxSignerWrapper(IPlainSigner signer, PDSignature signature) {
        this.signer = signer;
        this.signature = signature;
    }

    public byte[] sign(InputStream inputStream) throws SignatureException, IOException {
        byte[] signature =  signer.sign(StreamUtils.inputStreamToByteArray(inputStream));
        logger.debug("Signature Data: " + StringUtils.bytesToHexString(signature));
        FileOutputStream fos = new FileOutputStream("/tmp/fos.bin");
        fos.write(signature);
        fos.close();
        return signature;
    }
}
