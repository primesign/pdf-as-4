package at.gv.egiz.pdfas.lib.impl.signing.pdfbox2;

import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureInterface;

import at.gv.egiz.pdfas.lib.impl.signing.PDFASSignatureInterface;

public interface PDFASPDFBOXSignatureInterface extends PDFASSignatureInterface, SignatureInterface {
	public void setPDSignature(PDSignature signature);
}
