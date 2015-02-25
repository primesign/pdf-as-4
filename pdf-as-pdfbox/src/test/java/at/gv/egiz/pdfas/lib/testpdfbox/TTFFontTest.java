package at.gv.egiz.pdfas.lib.testpdfbox;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.pdmodel.PDDocument;

public class TTFFontTest {

	public static void main(String[] args) {
		try {
			PDDocument doc = PDDocument.load(new File("/home/afitzek/Downloads/pdf_groesse/willenserklaerung_signedByUser.pdf"));
		
			List<COSObject> cosObjects = doc.getDocument().getObjectsByType(COSName.FONT);
		
			Iterator<COSObject> cosObjectIt = cosObjects.iterator();
			
			while(cosObjectIt.hasNext()) {
				COSObject cosObject = cosObjectIt.next();
				COSBase subType = cosObject.getItem(COSName.SUBTYPE);
				COSBase baseFont = cosObject.getItem(COSName.BASE_FONT);
				COSBase aTest = cosObject.getItem(COSName.A);
				
				System.out.println(aTest);
				
				if(subType.equals(COSName.TRUE_TYPE)) {
					System.out.println("Object Number: " + cosObject.getObjectNumber().intValue() + 
							subType.toString());
					System.out.println("    BaseFont: " + baseFont.toString());
				}
				
				
			}
			
			
		} catch(Throwable e) {
			e.printStackTrace();
		}
	}

}
