package at.gv.egiz.pdfas.lib.api;

/**
 * Data Source interface
 *
 * All data sources in PDF-AS implement this interface. Also custom data sources have to 
 * implement this interface to allow PDF-AS to use them.
 */
public interface DataSource {
	
	/**
	 * Gets the MIME Type of the contained data.
	 * @return MIME Type
	 */
	public String getMIMEType();
	
	/**
	 * Gets the contained data
	 * @return the contained data
	 */
    public byte[] getByteData();
}
