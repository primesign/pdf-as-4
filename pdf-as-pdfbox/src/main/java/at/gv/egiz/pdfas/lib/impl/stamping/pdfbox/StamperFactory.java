package at.gv.egiz.pdfas.lib.impl.stamping.pdfbox;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.lib.impl.stamping.IPDFStamper;

public class StamperFactory {

	//public static final String DEFAULT_STAMPER_CLASS = "at.gv.egiz.pdfas.stmp.itext.ITextStamper";
	public static final String DEFAULT_STAMPER_CLASS = "at.gv.egiz.pdfas.lib.impl.stamping.pdfbox.PdfBoxStamper";

	public static IPDFStamper createDefaultStamper(ISettings settings) throws PdfAsException {
		try {
			Class<?> cls = Class.forName(DEFAULT_STAMPER_CLASS);
			Object st = cls.newInstance();
			if (!(st instanceof IPDFStamper))
				throw new ClassCastException();
			IPDFStamper stamper = (IPDFStamper) st;
			stamper.setSettings(settings);
			return stamper;
		} catch (Throwable e) {
			throw new PdfAsException("error.pdf.stamp.10", e);
		}
	}
}