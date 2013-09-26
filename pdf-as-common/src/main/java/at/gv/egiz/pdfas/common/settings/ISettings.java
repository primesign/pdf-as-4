package at.gv.egiz.pdfas.common.settings;

import java.util.Map;
import java.util.Vector;

public interface ISettings {
	public String getValue(String key);
	public boolean hasValue(String key);
	public boolean hasPrefix(String prefix);
	public Map<String, String> getValuesPrefix(String prefix);
	public Vector<String> getFirstLevelKeys(String prefix);
	public String getWorkingDirectory();
}
