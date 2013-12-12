package at.gv.egiz.pdfas.wrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import at.gv.egiz.pdfas.api.io.DataSink;

public class ByteArrayDataSink_OLD implements DataSink {

	private ByteArrayOutputStream baos = new ByteArrayOutputStream();
	
	public ByteArrayOutputStream getBAOS() {
		return baos;
	}
	
	public OutputStream createOutputStream(String mimeType) throws IOException {
		return baos;
	}

	public OutputStream createOutputStream(String mimeType,
			String characterEncoding) throws IOException {
		return baos;
	}

	public String getMimeType() {
		return "application/pdf";
	}

	public String getCharacterEncoding() {
		return "UTF-8";
	}

}
