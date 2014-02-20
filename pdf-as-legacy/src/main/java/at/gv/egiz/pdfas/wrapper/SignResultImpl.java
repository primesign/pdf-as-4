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
package at.gv.egiz.pdfas.wrapper;

import java.security.cert.X509Certificate;
import java.util.List;

import at.gv.egiz.pdfas.api.io.DataSink;
import at.gv.egiz.pdfas.api.sign.SignResult;
import at.gv.egiz.pdfas.api.sign.pos.SignaturePosition;

public class SignResultImpl implements SignResult {

	private DataSink sink;
	private X509Certificate certificate;
	private SignaturePosition position;
	
	public SignResultImpl(DataSink data, X509Certificate cert, SignaturePosition position) {
		this.certificate = cert;
		this.sink = data; 
		this.position = position;
	}
	
	public DataSink getOutputDocument() {
		return this.sink;
	}

	public X509Certificate getSignerCertificate() {
		return certificate;
	}

	public SignaturePosition getSignaturePosition() {
		return position;
	}

	public List getNonTextualObjects() {
		return null;
	}

	public boolean hasNonTextualObjects() {
		return false;
	}

}
