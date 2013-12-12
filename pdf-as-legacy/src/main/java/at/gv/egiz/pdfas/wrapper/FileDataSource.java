package at.gv.egiz.pdfas.wrapper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.management.RuntimeErrorException;

import at.gv.egiz.pdfas.api.io.DataSource;
import at.gv.egiz.pdfas.common.utils.StreamUtils;

public class FileDataSource implements DataSource {

	private byte[] data;
	
	public FileDataSource(File file) throws FileNotFoundException, IOException {
		data = StreamUtils.inputStreamToByteArray(new FileInputStream(file));
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
