package at.gv.egiz.pdfas.web.config;

public class WebConfiguration {

	public static String getPublicURL() {
		return null;
	}

	public static String getLocalBKUURL() {
		// TODO: Read URL from config
		return "http://127.0.0.1:3495/http-security-layer-request";
	}

	public static String getOnlineBKUURL() {
		// TODO: Read URL from config
		return "http://abyss.iaik.tugraz.at/bkuonline/http-security-layer-request";
	}

	public static String getHandyBKUURL() {
		// TODO: Read URL from config
		return "http://127.0.0.1:3495/http-security-layer-request";
	}

}
