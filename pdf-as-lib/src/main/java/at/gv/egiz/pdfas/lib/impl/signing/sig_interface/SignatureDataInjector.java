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
package at.gv.egiz.pdfas.lib.impl.signing.sig_interface;

import iaik.x509.X509Certificate;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import org.apache.pdfbox.exceptions.SignatureException;

import at.gv.egiz.pdfas.common.utils.StreamUtils;

public class SignatureDataInjector extends SignatureDataExtractor {

	protected byte[]  signature;
	protected byte[]  oldSignatureData;
	
	public SignatureDataInjector(X509Certificate certificate, String filter,
			String subfilter, Calendar date, byte[] signature, byte[] signatureData) {
		super(certificate, filter, subfilter, date);
		this.signature = signature;
		this.oldSignatureData = signatureData;
	}

	@Override
	public byte[] sign(InputStream content) throws SignatureException,
			IOException {
		byte[] signatureData = StreamUtils.inputStreamToByteArray(content);
		
		if(signatureData.length != this.oldSignatureData.length) {
			throw new SignatureException("Signature Data missmatch!");
		}
		
		for(int i = 0; i < signatureData.length; i++) {
			if(signatureData[i] != this.oldSignatureData[i]) {
				throw new SignatureException("Signature Data missmatch! " + i + " " + signatureData[i] + " vs " + this.oldSignatureData[i]);
			}
		}
		
		return signature;
	}

}
