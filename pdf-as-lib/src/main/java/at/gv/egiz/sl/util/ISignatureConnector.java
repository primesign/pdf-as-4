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
package at.gv.egiz.sl.util;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;
import at.gv.egiz.pdfas.lib.impl.status.RequestedSignature;
import iaik.x509.X509Certificate;

public interface ISignatureConnector {

	X509Certificate getCertificate(SignParameter parameter) throws PdfAsException;

	/**
	 * Creates a CMS signature using the provided data to be signed as well es the provides byte range.
	 * 
	 * @param input              The data to be signed. (required; must not be {@code null})
	 * @param byteRange          The byte range to be signed. (never {@code null}, tuples of [offset, length] expected)
	 * @param parameter          The sign parameters. (required; must not be {@code null})
	 * @param requestedSignature The signature context. (required; must not be {@code null})
	 * @return The CMS signature (in the form of encoded CMS ContentInfo). (never {@code null})
	 * @throws PdfAsException Thrown in case of error.
	 */
	byte[] sign(byte[] input, int[] byteRange, SignParameter parameter, RequestedSignature requestedSignature) throws PdfAsException;

}
