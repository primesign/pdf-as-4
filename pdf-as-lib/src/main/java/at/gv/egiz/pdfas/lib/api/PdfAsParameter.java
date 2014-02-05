package at.gv.egiz.pdfas.lib.api;

public interface PdfAsParameter {

	/**
	 * Gets the configuration associated with the parameter
	 * @return
	 */
	public Configuration getConfiguration() ;

	/**
	 * Sets the configuration associated with the parameter
	 * @param configuration
	 */
	public void setConfiguration(Configuration configuration);

	/**
	 * Gets the data source of the parameter
	 * @return
	 */
	public DataSource getDataSource();

	/**
	 * Sets the data source of the parameter
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource);
}
