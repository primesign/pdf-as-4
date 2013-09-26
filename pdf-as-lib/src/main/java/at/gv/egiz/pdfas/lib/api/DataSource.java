package at.gv.egiz.pdfas.lib.api;

public interface DataSource {
	public String getMIMEType();
    public byte[] getByteData();
}
