package at.gv.egiz.pdfas.lib.impl.verify.pdfbox;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.PDFASError;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.lib.api.verify.VerifyParameter;
import at.gv.egiz.pdfas.lib.api.verify.VerifyResult;
import at.gv.egiz.pdfas.lib.impl.ErrorExtractor;
import at.gv.egiz.pdfas.lib.impl.verify.IVerifier;
import at.gv.egiz.pdfas.lib.impl.verify.IVerifyFilter;
import at.gv.egiz.pdfas.lib.impl.verify.VerifierDispatcher;
import at.gv.egiz.pdfas.lib.impl.verify.VerifyBackend;

public class PDFBOXVerifier implements VerifyBackend {

	private static final Logger logger = LoggerFactory.getLogger(PDFBOXVerifier.class);

	@Override
	public List<VerifyResult> verify(VerifyParameter parameter) throws PDFASError {
		int signatureToVerify = parameter.getWhichSignature();
		int currentSignature = 0;
		PDDocument doc = null;
		try {
			List<VerifyResult> result = new ArrayList<VerifyResult>();
			ISettings settings = (ISettings) parameter.getConfiguration();
			VerifierDispatcher verifier = new VerifierDispatcher(settings);
			doc = PDDocument.load(parameter.getDataSource().getInputStream());

			COSDictionary trailer = doc.getDocument().getTrailer();
			if (trailer == null) {
				// No signatures ...
				return result;
			}
			COSDictionary root = (COSDictionary) trailer.getDictionaryObject(COSName.ROOT);
			if (root == null) {
				// No signatures ...
				return result;
			}
			COSDictionary acroForm = (COSDictionary) root.getDictionaryObject(COSName.ACRO_FORM);
			if (acroForm == null) {
				// No signatures ...
				return result;
			}
			COSArray fields = (COSArray) acroForm.getDictionaryObject(COSName.FIELDS);
			if (fields == null) {
				// No signatures ...
				return result;
			}

			int lastSig = -1;
			for (int i = 0; i < fields.size(); i++) {
				COSDictionary field = (COSDictionary) fields.getObject(i);
				String type = field.getNameAsString("FT");
				if ("Sig".equals(type)) {
					lastSig = i;
				}
			}

			byte[] inputData = IOUtils.toByteArray(parameter.getDataSource().getInputStream());

			for (int i = 0; i < fields.size(); i++) {
				COSDictionary field = (COSDictionary) fields.getObject(i);
				String type = field.getNameAsString("FT");
				if ("Sig".equals(type)) {
					boolean verifyThis = true;

					if (signatureToVerify >= 0) {
						// verify only specific siganture!
						verifyThis = signatureToVerify == currentSignature;
					}

					if (signatureToVerify == -2) {
						verifyThis = i == lastSig;
					}

					if (verifyThis) {
						logger.trace("Found Signature: ");
						COSBase base = field.getDictionaryObject("V");
						COSDictionary dict = (COSDictionary) base;

						logger.debug("Signer: " + dict.getNameAsString("Name"));
						logger.debug("SubFilter: " + dict.getNameAsString("SubFilter"));
						logger.debug("Filter: " + dict.getNameAsString("Filter"));
						logger.debug("Modified: " + dict.getNameAsString("M"));
						COSArray byteRange = (COSArray) dict.getDictionaryObject("ByteRange");

						StringBuilder sb = new StringBuilder();
						int[] bytes = new int[byteRange.size()];
						for (int j = 0; j < byteRange.size(); j++) {
							bytes[j] = byteRange.getInt(j);
							sb.append(" " + bytes[j]);
						}

						logger.debug("ByteRange" + sb.toString());

						COSString content = (COSString) dict.getDictionaryObject("Contents");

						ByteArrayOutputStream contentData = new ByteArrayOutputStream();
						for (int j = 0; j < bytes.length; j = j + 2) {
							int offset = bytes[j];
							int length = bytes[j + 1];

							contentData.write(inputData, offset, length);
						}
						contentData.close();

						IVerifyFilter verifyFilter = verifier.getVerifier(dict.getNameAsString("Filter"),
								dict.getNameAsString("SubFilter"));

						IVerifier lvlVerifier = verifier.getVerifierByLevel(parameter.getSignatureVerificationLevel());
						synchronized (lvlVerifier) {
							lvlVerifier.setConfiguration(parameter.getConfiguration());
							if (verifyFilter != null) {
								List<VerifyResult> results = verifyFilter.verify(contentData.toByteArray(),
										content.getBytes(), parameter.getVerificationTime(), bytes, lvlVerifier);
								if (results != null && !results.isEmpty()) {
									result.addAll(results);
								}
							}
						}
					}
					currentSignature++;
				}
			}
			return result;
		} catch (IOException e) {
			logger.warn("Failed to verify document", e);
			throw ErrorExtractor.searchPdfAsError(e, null);
		} catch (PdfAsException e) {
			logger.warn("Failed to verify document", e);
			throw ErrorExtractor.searchPdfAsError(e, null);
		} finally {
			if (doc != null) {
				try {
					doc.close();
				} catch (IOException e) {
					logger.info("Failed to close doc");
				}
			}
		}
	}

}
