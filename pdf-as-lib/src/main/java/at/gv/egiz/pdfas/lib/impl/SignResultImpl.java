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
package at.gv.egiz.pdfas.lib.impl;

import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import at.gv.egiz.pdfas.lib.api.SignaturePosition;
import at.gv.egiz.pdfas.lib.api.sign.SignResult;

public class SignResultImpl implements SignResult {

	protected X509Certificate certificate;
	protected SignaturePosition position;
	protected Map<String, String> processInfo = new HashMap<>();
	private Calendar signingDate;
	
	public SignResultImpl() {
	}

	public X509Certificate getSignerCertificate() {
		return this.certificate;
	}

	public SignaturePosition getSignaturePosition() {
		return this.position;
	}

	public void setSignerCertificate(X509Certificate certificate) {
		this.certificate = certificate;
	}

	public void setSignaturePosition(SignaturePosition position) {
		this.position = position;
	}

	@Override
	public Map<String, String> getProcessInformations() {
		return processInfo;
	}

	@Override
	public Calendar getSigningDate() {
		return signingDate;
	}

	/**
	 * Sets the final signing date used for the signature.
	 * 
	 * @param signingDate The signing date. (optional; may be {@code null})
	 */
	public void setSigningDate(@Nullable Calendar signingDate) {
		this.signingDate = signingDate;
	}

}
