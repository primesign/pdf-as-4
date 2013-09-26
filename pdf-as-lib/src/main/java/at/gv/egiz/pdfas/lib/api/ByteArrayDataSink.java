package at.gv.egiz.pdfas.lib.api;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public class ByteArrayDataSink implements DataSink {

	protected ByteArrayOutputStream bos = new ByteArrayOutputStream();
	
	public OutputStream createOutputStream() {
		bos = new ByteArrayOutputStream();
		return bos;
	}

	public byte[] getData() {
		return bos.toByteArray();
	}
}
