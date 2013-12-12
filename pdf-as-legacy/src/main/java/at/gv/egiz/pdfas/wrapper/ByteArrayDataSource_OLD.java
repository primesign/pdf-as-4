package at.gv.egiz.pdfas.wrapper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import at.gv.egiz.pdfas.api.io.DataSource;

public class ByteArrayDataSource_OLD implements DataSource {

	private InputStream is;
	private int length;
	private byte[] data;
	
	public ByteArrayDataSource_OLD(byte[] data) {
		this.length = data.length;
		this.is = new ByteArrayInputStream(data);
		this.data = data;
	}
	
	public InputStream createInputStream() {
		return is;
	}

	public int getLength() {
		return length;
	}

	public byte[] getAsByteArray() {
		return this.data;
	}

	public String getMimeType() {
		return "application/pdf";
	}

	public String getCharacterEncoding() {
		return "UTF-8";
	}

}
