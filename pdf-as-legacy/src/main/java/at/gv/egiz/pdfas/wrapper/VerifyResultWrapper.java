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

import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;

import at.gv.egiz.pdfas.api.commons.Constants;
import at.gv.egiz.pdfas.api.exceptions.PdfAsException;
import at.gv.egiz.pdfas.api.exceptions.PdfAsWrappedException;
import at.gv.egiz.pdfas.api.io.DataSource;
import at.gv.egiz.pdfas.api.verify.SignatureCheck;
import at.gv.egiz.pdfas.api.verify.VerifyResult;
import at.gv.egiz.pdfas.api.xmldsig.XMLDsigData;

public class VerifyResultWrapper implements VerifyResult {

	private at.gv.egiz.pdfas.lib.api.verify.VerifyResult newResult;
	
	public VerifyResultWrapper(at.gv.egiz.pdfas.lib.api.verify.VerifyResult newResult) {
		this.newResult = newResult; 
	}

	public String getSignatureType() {
		return null;
	}

	public DataSource getSignedData() {
		return new ByteArrayDataSource_OLD(this.newResult.getSignatureData());
	}

	public X509Certificate getSignerCertificate() {
		return this.newResult.getSignerCertificate();
	}

	public Date getSigningTime() {
		return null;
	}

	public Object getInternalSignatureInformation() {
		return null;
	}

	public String getTimeStampValue() {
		return null;
	}

	public void setNonTextualObjects(List nonTextualObjects) {
	}

	public boolean isVerificationDone() {
		return this.newResult.isVerificationDone();
	}

	public PdfAsException getVerificationException() {
		return new PdfAsWrappedException(this.newResult.getVerificationException());
	}

	public SignatureCheck getCertificateCheck() {
		return new SignatureCheckWrapper(this.newResult.getCertificateCheck());
	}

	public SignatureCheck getValueCheckCode() {
		return new SignatureCheckWrapper(this.newResult.getValueCheckCode());
	}

	public SignatureCheck getManifestCheckCode() {
		return new SignatureCheckWrapper(this.newResult.getManifestCheckCode());
	}

	public boolean isQualifiedCertificate() {
		return this.newResult.isQualifiedCertificate();
	}

	public boolean isPublicAuthority() {
		return false;
	}

	public String getPublicAuthorityCode() {
		return null;
	}

	public List getPublicProperties() {
		return null;
	}

	public Date getVerificationTime() {
		return null;
	}

	public String getHashInputData() {
		return null;
	}

	public List getNonTextualObjects() {
		return null;
	}

	public boolean hasNonTextualObjects() {
		return false;
	}

	public XMLDsigData getReconstructedXMLDsig() {
		return null;
	}
	
	
	
}
