package at.gv.egiz.pdfas.lib.impl.stamping;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.settings.ISettings;

public class StamperFactory {

	public static final String DEFAULT_STAMPER_CLASS = "at.gv.egiz.pdfas.stmp.itext.ITextStamper";

	public static IPDFStamper createDefaultStamper(ISettings settings) throws PdfAsException {
		try {
			Class<? extends IPDFStamper> cls = (Class<? extends IPDFStamper>) 
					Class.forName(DEFAULT_STAMPER_CLASS);
			IPDFStamper stamper = cls.newInstance();
			stamper.setSettings(settings);
			return stamper;
		} catch (Throwable e) {
			throw new PdfAsException("error.pdf.stamp.10", e);
		}
	}
}
