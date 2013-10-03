package at.gv.egiz.pdfas.lib.impl.signing.sig_interface;

import java.util.Calendar;

import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureInterface;

public interface PDFASSignatureInterface extends SignatureInterface {
	public String getPDFSubFilter();
    public String getPDFFilter();
    public void setPDSignature(PDSignature signature);
    public Calendar getSigningDate();
}
