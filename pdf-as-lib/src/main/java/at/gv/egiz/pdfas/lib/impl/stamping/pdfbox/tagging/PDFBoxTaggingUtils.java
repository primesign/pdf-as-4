/*******************************************************************************
 * <copyright> Copyright 2014 by E-Government Innovation Center EGIZ, Graz, Austria </copyright>
 * PDF-AS has been contracted by the E-Government Innovation Center EGIZ, a
 * joint initiative of the Federal Chancellery Austria and Graz University of
 * Technology.
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * http://www.osor.eu/eupl/
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * 
 * This product combines work with different licenses. See the "NOTICE" text
 * file for details on the various modules and licenses.
 * The "NOTICE" text file is part of the distribution. Any derivative works
 * that you distribute must include a readable copy of the "NOTICE" text file.
 ******************************************************************************/
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
