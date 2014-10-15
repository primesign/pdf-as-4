package at.gv.egiz.pdfas.lib.api;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

public class ByteArrayDataSource implements DataSource {

	private byte[] data;
	
	public ByteArrayDataSource(byte[] data) {
		this.data = data;
	}
	
	@Override
	public String getContentType() {
		return "application/pdf";
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(data);
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		throw new IOException("Not supported!");
	}

}
