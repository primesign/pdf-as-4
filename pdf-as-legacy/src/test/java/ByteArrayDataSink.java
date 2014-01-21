import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import at.gv.egiz.pdfas.api.io.DataSink;


public class ByteArrayDataSink implements DataSink {

	private ByteArrayOutputStream bos = new ByteArrayOutputStream();
	
	public ByteArrayDataSink() {
	}
	
	public OutputStream createOutputStream(String mimeType) throws IOException {
		return createOutputStream(mimeType, "UTF-8");
	}

	public OutputStream createOutputStream(String mimeType,
			String characterEncoding) throws IOException {
		return bos;
	}

	public String getMimeType() {
		return "application/pdf";
	}

	public String getCharacterEncoding() {
		return "UTF-8";
	}
	
	public byte[] getBytes() {
		return this.bos.toByteArray();
	}

}
