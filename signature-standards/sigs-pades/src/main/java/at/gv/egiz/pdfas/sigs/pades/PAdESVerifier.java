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
package at.gv.egiz.pdfas.sigs.pades;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.utils.PDFUtils;
import at.gv.egiz.pdfas.lib.api.Configuration;
import at.gv.egiz.pdfas.lib.api.verify.VerifyResult;
import at.gv.egiz.pdfas.lib.impl.verify.FilterEntry;
import at.gv.egiz.pdfas.lib.impl.verify.IVerifier;
import at.gv.egiz.pdfas.lib.impl.verify.IVerifyFilter;
import at.gv.egiz.pdfas.lib.impl.verify.VerifyResultImpl;

public class PAdESVerifier implements IVerifyFilter, PAdESConstants {

	private static final Logger logger = LoggerFactory
			.getLogger(PAdESVerifier.class);

	private static final String MOA_VERIFY_URL = "moa.verify.url";
	private static final String MOA_VERIFY_TRUSTPROFILE = "moa.verify.TrustProfileID";

	private String moaEndpoint;
	private String moaTrustProfile;

	public PAdESVerifier() {
	}

	@SuppressWarnings("rawtypes")
	public List<VerifyResult> verify(byte[] contentData,
			byte[] signatureContent, Date verificationTime, int[] byteRange, IVerifier verifier)
			throws PdfAsException {
		
		byte[] data = contentData;
		byte[] signature = signatureContent;
		
		List<VerifyResult> verifieResults = verifier.verify(signature, data, verificationTime);
		for(int i =0; i < verifieResults.size();i++) {
			VerifyResultImpl result = (VerifyResultImpl)verifieResults.get(i);
			result.setSignatureData(PDFUtils.blackOutSignature(data, byteRange));
		}
		
		return verifieResults;
	}

	public List<FilterEntry> getFiters() {
		List<FilterEntry> result = new ArrayList<FilterEntry>();
		result.add(new FilterEntry(FILTER_ADOBE_PPKLITE,
				SUBFILTER_ETSI_CADES_DETACHED));
		return result;
	}

	public void setConfiguration(Configuration config) {
		this.moaEndpoint = config.getValue(MOA_VERIFY_URL);
		this.moaTrustProfile = config.getValue(MOA_VERIFY_TRUSTPROFILE);
	}

}
