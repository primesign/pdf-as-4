package at.gv.egiz.pdfas.lib.impl;

import at.gv.egiz.pdfas.lib.api.Configuration;
import at.gv.egiz.pdfas.lib.api.DataSource;
import at.gv.egiz.pdfas.lib.api.PdfAsParameter;

public class PdfAsParameterImpl implements PdfAsParameter {
protected Configuration configuration;
	
	protected DataSource dataSource;
	
	public PdfAsParameterImpl(Configuration configuration, 
			DataSource dataSource) {
		this.configuration = configuration;
		this.dataSource = dataSource;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
}
