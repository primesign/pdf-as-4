package at.gv.egiz.pdfas.lib.impl;

import at.gv.egiz.pdfas.lib.api.Configuration;
import at.gv.egiz.pdfas.lib.api.DataSource;
import at.gv.egiz.pdfas.lib.api.verify.VerifyParameter;

public class VerifyParameterImpl extends PdfAsParameterImpl implements VerifyParameter {
	public VerifyParameterImpl(Configuration configuration,
			DataSource dataSource) {
		super(configuration, dataSource);
	}
}
