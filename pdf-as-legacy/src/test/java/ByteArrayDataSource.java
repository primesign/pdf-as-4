import java.io.ByteArrayInputStream;
import java.io.InputStream;

import at.gv.egiz.pdfas.api.io.DataSource;


public class ByteArrayDataSource implements DataSource {

	private byte[] data;
	
	public ByteArrayDataSource(byte[] data) {
		this.data = data;
	}
	
	public InputStream createInputStream() {
		return new ByteArrayInputStream(data);
	}

	public int getLength() {
		return data.length;
	}

	public byte[] getAsByteArray() {
		return data;
	}

	public String getMimeType() {
		return "application/pdf";
	}

	public String getCharacterEncoding() {
		return "UTF-8";
	}

}
