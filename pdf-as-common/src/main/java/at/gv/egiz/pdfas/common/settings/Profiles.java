package at.gv.egiz.pdfas.common.settings;

import java.util.*;

public class Profiles {

	private String name;
	private Profiles parent;
	private List<Profiles> augments;
	private boolean initialized;
	
	private static final String PARENT_CONFIG = ".parent";

	private static final String AUGMENTS_CONFIG = ".augments";
	
	public Profiles(String name) {
		this.name = name;
		this.initialized = false;
		this.parent = null;
		this.augments = new ArrayList<Profiles>();
	}
	
	public String getName() {
		return this.name;
	}
	
	public void findParent(Properties props, Map<String, Profiles> profiles) {
		String parentString = props.getProperty("sig_obj." + this.name + PARENT_CONFIG);
		if(parentString != null) {
			this.parent = profiles.get(parentString);
		}

		String augmentKeyPrefix = "sig_obj." + this.name + AUGMENTS_CONFIG;

		Enumeration enumeration = props.propertyNames();
		while(enumeration.hasMoreElements()) {
			String key = (String)enumeration.nextElement();
			if(key.startsWith(augmentKeyPrefix)) {
				String augmentProfile = props.getProperty(key);
				this.augments.add(profiles.get(augmentProfile));
			}
		}
	}
	
	public Profiles getParent() {
		return this.parent;
	}

	public List<Profiles> getAugments() {
		return this.augments;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}
}
