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
package at.gv.egiz.pdfas.lib.impl.stamping.pdfbox;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible.PDVisibleSigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.exceptions.PdfAsWrappedIOException;
import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.lib.impl.pdfbox.PDFBOXObject;
import at.knowcenter.wag.egov.egiz.pdf.PositioningInstruction;

public class PDFAsVisualSignatureProperties extends PDVisibleSigProperties {

	private static final Logger logger = LoggerFactory.getLogger(PDFAsVisualSignatureProperties.class);
	
	private ISettings settings;

	private PDFBoxTable main;
	
	private PDFAsVisualSignatureDesigner designer;
	
	private float rotationAngle = 0;

	public PDFAsVisualSignatureProperties(ISettings settings, PDFBOXObject object, 
			PdfBoxVisualObject visObj, PositioningInstruction pos) {
		this.settings = settings;
		try {
			main = visObj.getTable();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		this.rotationAngle = pos.getRotation();
		try {
			PDDocument origDoc = object.getDocument();

			designer = new PDFAsVisualSignatureDesigner(origDoc, pos.getPage(), this, pos.isMakeNewPage());
			float posy = designer.getPageHeight() - pos.getY();
			designer.coordinates(pos.getX(), posy);
			float[] form_rect = new float[] {0,0, main.getWidth() + 2, main.getHeight() + 2};
			logger.debug("AP Rect: {} {} {} {}", form_rect[0], form_rect[1], form_rect[2], form_rect[3]);
			designer.formaterRectangleParams(form_rect);
			//this.setPdVisibleSignature(designer);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	public void buildSignature() throws IOException {
		PDFAsVisualSignatureBuilder builder = new PDFAsVisualSignatureBuilder(this, this.settings, designer);
		PDFAsTemplateCreator creator = new PDFAsTemplateCreator(builder);
		try {
			setVisibleSignature(creator.buildPDF(designer));
		} catch (PdfAsException e) {
			throw new PdfAsWrappedIOException(e);
		}
	}

	public PDFBoxTable getMainTable() {
		return main;
	}
	
	
	public float getRotation() {
		return this.rotationAngle;
	}

	public PDFAsVisualSignatureDesigner getDesigner() {
		return designer;
	}
	
}