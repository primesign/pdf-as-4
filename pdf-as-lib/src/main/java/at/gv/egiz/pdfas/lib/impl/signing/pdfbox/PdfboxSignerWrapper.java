package at.gv.egiz.pdfas.lib.impl.signing.pdfbox;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import org.apache.pdfbox.exceptions.SignatureException;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.exceptions.PdfAsWrappedIOException;
import at.gv.egiz.pdfas.common.utils.PDFUtils;
import at.gv.egiz.pdfas.common.utils.StreamUtils;
import at.gv.egiz.pdfas.common.utils.StringUtils;
import at.gv.egiz.pdfas.lib.api.sign.IPlainSigner;
import at.gv.egiz.pdfas.lib.impl.signing.sig_interface.PDFASSignatureInterface;

public class PdfboxSignerWrapper implements PDFASSignatureInterface {

	private static final Logger logger = LoggerFactory
			.getLogger(PdfboxSignerWrapper.class);

	private IPlainSigner signer;
	private PDSignature signature;
	private int[] byteRange;
	private Calendar date;

	public PdfboxSignerWrapper(IPlainSigner signer) {
		this.signer = signer;
		this.date = Calendar.getInstance();
	}

	public byte[] sign(InputStream inputStream) throws SignatureException,
			IOException {
		byte[] data = StreamUtils.inputStreamToByteArray(inputStream);
		byteRange = PDFUtils.extractSignatureByteRange(data);
		try {
			byte[] signature = signer.sign(data, byteRange);
			/*logger.debug("Signature Data: "
					+ iaik.utils.Util.toBase64String(signature));*/
			FileOutputStream fos = new FileOutputStream("/tmp/fos.bin");
			fos.write(signature);
			fos.close();
			return signature;
		} catch (PdfAsException e) {
			throw new PdfAsWrappedIOException(e);
		}
	}

	public int[] getByteRange() {
		return byteRange;
	}

	public String getPDFSubFilter() {
		return this.signer.getPDFSubFilter();
	}

	public String getPDFFilter() {
		return this.signer.getPDFFilter();
	}

	public void setPDSignature(PDSignature signature) {
		this.signature = signature;
	}

	public Calendar getSigningDate() {
		return this.date;
	}
}
