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
package at.gv.egiz.pdfas.lib.impl.status;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.activation.DataSource;


public abstract class PDFObject {
	
	protected OperationStatus status;
	
	protected DataSource originalDocument;
	protected byte[] signedDocument;

	public PDFObject(OperationStatus operationStatus) {
		this.status = operationStatus;
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}

	public abstract void close();
	
	public DataSource getOriginalDocument() {
		return originalDocument;
	}

	public abstract void setOriginalDocument(DataSource originalDocument) throws IOException;
	
	public byte[] getSignedDocument() {
		return signedDocument;
	}

	public void setSignedDocument(byte[] signedDocument) {
		this.signedDocument = signedDocument;
	}

	public OperationStatus getStatus() {
		return status;
	}

	public void setStatus(OperationStatus status) {
		this.status = status;
	}
	
	public abstract String getPDFVersion();

	public Map<String, String> getRequestParameters() {
		return status.getSignParamter().getDynamicSignatureBlockArguments();
	}
}
