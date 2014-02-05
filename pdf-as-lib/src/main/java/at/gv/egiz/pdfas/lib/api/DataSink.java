package at.gv.egiz.pdfas.lib.api;

import java.io.OutputStream;

/**
 * Data Sink interface.
 */
public interface DataSink {
	/**
	 * Creates an output stream to receive the data
	 * @return an output stream for the data
	 */
	public OutputStream createOutputStream();
}
