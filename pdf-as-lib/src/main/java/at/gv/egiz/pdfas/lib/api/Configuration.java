package at.gv.egiz.pdfas.lib.api;

public interface Configuration {
	public String getValue(String key);
	public boolean hasValue(String key);
	public void setValue(String key, String value);
}
