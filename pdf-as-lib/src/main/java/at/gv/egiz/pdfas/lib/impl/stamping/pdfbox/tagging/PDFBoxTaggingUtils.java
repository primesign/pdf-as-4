package at.gv.egiz.pdfas.lib.impl.stamping.pdfbox.tagging;

import java.util.Iterator;
import java.util.List;

import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDMarkedContentReference;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureElement;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureNode;
import org.apache.pdfbox.pdmodel.documentinterchange.markedcontent.PDMarkedContent;

public class PDFBoxTaggingUtils {

	public static String DOCUMENT = "Document";
	
	public static void beginMarkedContent(PDMarkedContentReference reference) {
		PDMarkedContentReference ref;
		PDMarkedContent mc;
	}
	
	public static PDStructureElement getDocumentElement(PDStructureNode structElem) {
		List<Object> kids = structElem.getKids();
		Iterator<Object> kidsit = kids.iterator();
		while (kidsit.hasNext()) {
			Object kid = kidsit.next();
			if (kid instanceof PDStructureElement) {
				PDStructureElement elem = (PDStructureElement) kid;
				if(elem.getStructureType().equals(DOCUMENT)) {
					return elem;
				}
			} 
		}
		return null;
	}
	
}
