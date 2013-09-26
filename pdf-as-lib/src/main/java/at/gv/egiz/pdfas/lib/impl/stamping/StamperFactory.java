package at.gv.egiz.pdfas.lib.impl.stamping;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;

public class StamperFactory {

	public static final String DEFAULT_STAMPER_CLASS = "at.gv.egiz.pdfas.stmp.itext.ITextStamper";

	public static IPDFStamper createDefaultStamper() throws PdfAsException {
		try {
			Class<? extends IPDFStamper> cls = (Class<? extends IPDFStamper>) 
					Class.forName(DEFAULT_STAMPER_CLASS);
			return cls.newInstance();
		} catch (Throwable e) {
			throw new PdfAsException("NO STAMPER!", e);
		}
	}
}
