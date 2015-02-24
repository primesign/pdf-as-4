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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.lib.impl.stamping.IPDFVisualObject;
import at.knowcenter.wag.egov.egiz.table.Table;

public class PdfBoxVisualObject implements IPDFVisualObject {

	private static final Logger logger = LoggerFactory
			.getLogger(PdfBoxVisualObject.class);
	
	private Table abstractTable;
	private PDFBoxTable table;
	private float width;
	private float x;
	private float y;
	private int page;
	private ISettings settings;
	private PDDocument originalDoc;

	public PdfBoxVisualObject(Table table, ISettings settings, PDDocument originalDoc)
			throws IOException, PdfAsException {
		this.abstractTable = table;
		this.originalDoc = originalDoc;
		this.table = new PDFBoxTable(table, null, settings, originalDoc);
		this.settings = settings;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public void fixWidth() {
		try {
			table = new PDFBoxTable(abstractTable, null, this.width,  settings, this.originalDoc);
		} catch (IOException e) {
			logger.warn("Failed to fix width of Table!", e);
		} catch (PdfAsException e) {
			logger.warn("Failed to fix width of Table!", e);
		}
	}

	public float getHeight() {
		return table.getHeight();
	}

	public float getWidth() {
		return table.getWidth();
	}

	public void setXPos(float x) {
		this.x = x;
	}

	public void setYPos(float y) {
		this.y = y;
	}
	
	public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public PDFBoxTable getTable() {
		return this.table;
	}
}
