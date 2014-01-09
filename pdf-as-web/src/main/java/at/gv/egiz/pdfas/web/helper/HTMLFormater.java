package at.gv.egiz.pdfas.web.helper;

public class HTMLFormater {

	public static String formatStackTrace(StackTraceElement[] elements) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < elements.length; i++) {
			sb.append(elements[i].toString());
			sb.append("</br>");
		}
		return sb.toString();
	}
}
