package at.gv.egiz.pdfas.common.settings;

public class SignatureProfileEntry {
    private String key = null;
    private String caption = null;
    private String value = null;

    public SignatureProfileEntry() {
    }
    
    public SignatureProfileEntry(String key, String caption, String value) {
    	this.key = key;
    	this.caption = caption;
    	this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return getKey() + "[ " + getCaption() + " : " + getValue() + " ]";
    }
}
