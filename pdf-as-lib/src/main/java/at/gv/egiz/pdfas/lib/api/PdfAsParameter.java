package at.gv.egiz.pdfas.lib.api;

public abstract class PdfAsParameter {

	protected Configuration configuration;
	
	protected IDataSource dataSource;
	
	public PdfAsParameter(Configuration configuration, 
			IDataSource dataSource) {
		this.configuration = configuration;
		this.dataSource = dataSource;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public IDataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(IDataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	
}
