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

import at.gv.egiz.pdfas.lib.api.Configuration;
import at.gv.egiz.pdfas.lib.api.DataSink;
import at.gv.egiz.pdfas.lib.api.DataSource;
import at.gv.egiz.pdfas.lib.api.sign.IPlainSigner;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;

public class SignParameterImpl extends PdfAsParameterImpl implements SignParameter {
	protected String signatureProfileId = null;
	protected String signaturePosition = null;
	protected DataSink output = null;
	protected IPlainSigner signer = null;
	
	public SignParameterImpl(Configuration configuration, 
			DataSource dataSource) {
		super(configuration, dataSource);
	}

	// ========================================================================
	
	public String getSignatureProfileId() {
		return signatureProfileId;
	}

	public void setSignatureProfileId(String signatureProfileId) {
		this.signatureProfileId = signatureProfileId;
	}

	public String getSignaturePosition() {
		return signaturePosition;
	}

	public void setSignaturePosition(String signaturePosition) {
		this.signaturePosition = signaturePosition;
	}

	public void setOutput(DataSink output) {
		this.output = output;
	}

	public DataSink getOutput() {
		return this.output;
	}

	public void setPlainSigner(IPlainSigner signer) {
		this.signer = signer;
	}
	
	public IPlainSigner getPlainSigner() {
		return this.signer;
	}
	
}
