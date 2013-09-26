package at.gv.egiz.pdfas.lib.api;

public interface IDataSource {
	public String getMIMEType();
    public byte[] getByteData();
}
