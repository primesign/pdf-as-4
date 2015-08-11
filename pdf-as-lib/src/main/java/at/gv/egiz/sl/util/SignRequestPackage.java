package at.gv.egiz.sl.util;

public class SignRequestPackage {
	private RequestPackage cmsRequest;
	private String displayName;

	public SignRequestPackage() {
		super();
	}

	public RequestPackage getCmsRequest() {
		return cmsRequest;
	}

	public void setCmsRequest(RequestPackage cmsRequest) {
		this.cmsRequest = cmsRequest;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

}
