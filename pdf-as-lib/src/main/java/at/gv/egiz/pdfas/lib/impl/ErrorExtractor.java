package at.gv.egiz.pdfas.lib.impl;

import at.gv.egiz.pdfas.common.exceptions.ErrorConstants;
import at.gv.egiz.pdfas.common.exceptions.PDFASError;
import at.gv.egiz.pdfas.common.exceptions.SLPdfAsException;

public class ErrorExtractor implements ErrorConstants {

	private static final int MAX_CAUSE_DEPTH = 30;

	private static PDFASError convertPdfAsError(Throwable e) {
		if (e instanceof SLPdfAsException) {
			SLPdfAsException ex = (SLPdfAsException) e;
			if (ex.getInfo() != null) {
				return new PDFASError(ex.getCode(), ex.getInfo(), e);
			} else {
				return new PDFASError(ex.getCode(), e);
			}
		} // TODO: Handle more exceptions

		return null;
	}

	public static PDFASError searchPdfAsError(Throwable e) {
		Throwable cur = e;
		PDFASError err = null;

		// Search PDFASError
		for (int i = 0; i < MAX_CAUSE_DEPTH; i++) {
			if (cur instanceof PDFASError) {
				err = (PDFASError) cur;
			}
			if (err != null) {
				break;
			}

			cur = cur.getCause();
			if (cur == null) {
				break;
			}
		}
		cur = e;
		// Search other reasons
		for (int i = 0; i < MAX_CAUSE_DEPTH; i++) {

			if (cur == null) {
				break;
			}

			err = convertPdfAsError(cur);

			if (err != null) {
				break;
			}

			cur = cur.getCause();
		}

		if (err != null) {
			return err;
		}

		return new PDFASError(ERROR_GENERIC, e);
	}
}
