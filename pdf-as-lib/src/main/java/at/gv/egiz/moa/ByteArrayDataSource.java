package at.gv.egiz.moa;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

public class ByteArrayDataSource implements DataSource {

	private byte[] data;
	private String mimeType;
	
	public ByteArrayDataSource(byte[] data, String mime) {
		this.data = data;
		this.mimeType = mime;
	}
	
	public String getContentType() {
		return this.mimeType;
	}

	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(data);
	}

	public String getName() {
		return null;
	}

	public OutputStream getOutputStream() throws IOException {
		return null;
	}

}
