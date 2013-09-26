package at.gv.egiz.pdfas.lib.api;

import java.io.OutputStream;

public interface DataSink {
	public OutputStream createOutputStream();
}
