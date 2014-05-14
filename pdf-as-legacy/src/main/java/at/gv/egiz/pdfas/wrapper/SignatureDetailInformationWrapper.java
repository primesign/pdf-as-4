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
import java.util.Date;
import java.util.List;
import java.util.Map;

import at.gv.egiz.pdfas.api.io.DataSource;
import at.gv.egiz.pdfas.api.sign.SignatureDetailInformation;
import at.gv.egiz.pdfas.api.sign.pos.SignaturePosition;
import at.gv.egiz.pdfas.common.utils.DNUtils;
import at.gv.egiz.pdfas.lib.api.StatusRequest;

public class SignatureDetailInformationWrapper implements
		SignatureDetailInformation {

	public SignParameterWrapper wrapper;
	private StatusRequest status;
	private DataSource dataSource;
	private iaik.x509.X509Certificate certificate;

	public SignatureDetailInformationWrapper(iaik.x509.X509Certificate cert) {
		this.certificate = cert;
	}
	
	public StatusRequest getStatus() {
		return status;
	}

	public void setStatus(StatusRequest status) {
		this.status = status;
	}
	

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public DataSource getSignatureData() {
		return this.dataSource;
	}

	public SignaturePosition getSignaturePosition() {
		return null;
	}

	public List getNonTextualObjects() {
		return null;
	}

	public Date getSignDate() {
		return null;
	}

	public String getIssuer() {
		return this.certificate.getIssuerDN().getName();
	}

	public Map getIssuerDNMap() {
		try {
			return DNUtils.dnToMap(getIssuer());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getSubjectName() {
		return this.certificate.getSubjectDN().getName();
	}

	public String getSerialNumber() {
		return this.certificate.getSerialNumber().toString();
	}

	public String getSigAlgorithm() {
		return this.certificate.getSigAlgName();
	}

	public String getSigID() {
		return null;
	}

	public String getSigKZ() {
		return null;
	}

	public String getSignatureValue() {
		return null;
	}

	public String getSigTimeStamp() {
		return null;
	}

	public Map getSubjectDNMap() {
		try {
			return DNUtils.dnToMap(getSubjectName());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public X509Certificate getX509Certificate() {
		return this.certificate;
	}

	public boolean isTextual() {
		return false;
	}

	public boolean isBinary() {
		return true;
	}

}
