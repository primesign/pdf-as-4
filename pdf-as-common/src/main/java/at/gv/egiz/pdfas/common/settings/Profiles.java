package at.gv.egiz.pdfas.common.settings;

import java.util.Map;
import java.util.Properties;

public class Profiles {

	private String name;
	private Profiles parent;
	private boolean initialized;
	
	private static final String PARENT_CONFIG = ".parent";
	
	public Profiles(String name) {
		this.name = name;
		this.initialized = false;
		this.parent = null;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void findParent(Properties props, Map<String, Profiles> profiles) {
		String parentString = props.getProperty("sig_obj." + this.name + PARENT_CONFIG);
		if(parentString != null) {
			this.parent = profiles.get(parentString);
		}
	}
	
	public Profiles getParent() {
		return this.parent;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}
}
