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
package at.gv.egiz.pdfas.lib.impl.signing.pdfbox2;

import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;

import at.gv.egiz.pdfas.common.utils.StreamUtils;

public class SignatureDataExtractor implements PDFASPDFBOXExtractorInterface {

	private byte[] signatureData;
	
	private String pdfSubFilter;
	private String pdfFilter;
	private PDSignature signature;
	private int[] byteRange;
	
	public SignatureDataExtractor(String filter, String subfilter) {
		this.pdfFilter = filter;
		this.pdfSubFilter = subfilter;
	}
	
	@Override
	public String getPDFSubFilter() {
		return this.pdfSubFilter;
	}

	@Override
	public String getPDFFilter() {
		return this.pdfFilter;
	}

	@Override
	public byte[] getSignatureData() {
		return this.signatureData;
	}

	@Override
	public byte[] sign(InputStream content) throws IOException {
		signatureData = StreamUtils.inputStreamToByteArray(content);
		byteRange = this.signature.getByteRange();
		return new byte[] { 0 };
	}

	@Override
	public void setPDSignature(PDSignature signature) {
		this.signature = signature;
	}

	@Override
	public int[] getByteRange() {
		return byteRange;
	}
	
}
