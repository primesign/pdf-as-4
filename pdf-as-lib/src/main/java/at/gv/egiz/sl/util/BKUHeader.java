package at.gv.egiz.sl.util;

public class BKUHeader {
	private String name;
	private String value;
	
	public BKUHeader(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return getName() + " = " + getValue();
	}
}
