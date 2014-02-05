package at.gv.egiz.pdfas.lib.api;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

/**
 * A simple byte array data sink
 */
public class ByteArrayDataSink implements DataSink {

	protected ByteArrayOutputStream bos = new ByteArrayOutputStream();
	
	public OutputStream createOutputStream() {
		bos = new ByteArrayOutputStream();
		return bos;
	}

	/**
	 * Returns the output data
	 * @return the output data
	 */
	public byte[] getData() {
		return bos.toByteArray();
	}
}
