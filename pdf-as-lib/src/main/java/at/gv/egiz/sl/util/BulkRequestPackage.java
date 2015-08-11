package at.gv.egiz.sl.util;

import java.util.LinkedList;
import java.util.List;

/**
 * Contains necessary data to generate a <code>BulkRequest</code>.
 * @author szoescher
 *
 */
public class BulkRequestPackage {

	private List<SignRequestPackage> signRequestPackages;

	
	
	public BulkRequestPackage() {
		signRequestPackages = new LinkedList<SignRequestPackage>();
	}

	public List<SignRequestPackage> getSignRequestPackages() {
		return signRequestPackages;
	}

	public void setSignRequestPackages(List<SignRequestPackage> signRequestPackages) {
		this.signRequestPackages = signRequestPackages;
	}
	
	

	public void add(SignRequestPackage createSignRequest) {
		signRequestPackages.add(createSignRequest);
		
	}


}
