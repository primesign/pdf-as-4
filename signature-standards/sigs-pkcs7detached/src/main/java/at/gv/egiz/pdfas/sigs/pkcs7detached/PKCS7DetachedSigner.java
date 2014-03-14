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
package at.gv.egiz.pdfas.sigs.pkcs7detached;

import iaik.asn1.ASN1Object;
import iaik.asn1.ObjectID;
import iaik.asn1.structures.AlgorithmID;
import iaik.asn1.structures.Attribute;
import iaik.asn1.structures.ChoiceOfTime;
import iaik.cms.ContentInfo;
import iaik.cms.IssuerAndSerialNumber;
import iaik.cms.SignedData;
import iaik.cms.SignerInfo;
import iaik.x509.X509Certificate;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Date;

import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.exceptions.PdfAsSignatureException;
import at.gv.egiz.pdfas.lib.api.sign.IPlainSigner;

/**
 * Creates a PKCS7 detached PDF signature
 *
 */
public class PKCS7DetachedSigner implements IPlainSigner {

	private static final Logger logger = LoggerFactory
			.getLogger(PKCS7DetachedSigner.class);
	
	PrivateKey privKey;
	X509Certificate cert;

	public PKCS7DetachedSigner(String file, String alias, String kspassword,
			String keypassword, String type) throws PdfAsException {
		try {
			KeyStore ks = KeyStore.getInstance(type);
			ks.load(new FileInputStream(file), kspassword.toCharArray());
			privKey = (PrivateKey) ks.getKey(alias, keypassword.toCharArray());
			cert = new X509Certificate(ks.getCertificate(alias).getEncoded());
		} catch (Throwable e) {
			throw new PdfAsException("error.pdf.sig.02", e);
		}
	}

	public X509Certificate getCertificate() {
		return cert;
	}

	public byte[] sign(byte[] input, int[] byteRange) throws PdfAsException {
		try {
			logger.info("Creating PKCS7 signature.");
			IssuerAndSerialNumber issuer = new IssuerAndSerialNumber(cert);
			SignerInfo signer1 = new SignerInfo(issuer, AlgorithmID.sha256, 
					AlgorithmID.ecdsa_With_SHA256, 
					privKey);

			SignedData si = new SignedData(input, SignedData.EXPLICIT);
			si.addCertificates(new Certificate[] { cert });
			Attribute signingTime = new Attribute(ObjectID.signingTime,
					new ASN1Object[] { new ChoiceOfTime(new Date())
							.toASN1Object() });
			Attribute contentType = new Attribute(ObjectID.contentType, new ASN1Object[] {
					new ObjectID("1.2.840.113549.1.7.1")
				});

			Attribute[] attributes = new Attribute[] { signingTime, contentType };
			signer1.setSignedAttributes(attributes);
			si.addSignerInfo(signer1);
			InputStream dataIs = si.getInputStream();
			byte[] buf = new byte[1024];
			while ((dataIs.read(buf)) > 0)
				; // skip data
			ContentInfo ci = new ContentInfo(si);
			logger.info("PKCS7 signature done.");
			return ci.getEncoded();
		} catch (NoSuchAlgorithmException e) {
			throw new PdfAsSignatureException("error.pdf.sig.01", e);
		} catch (iaik.cms.CMSException e) {
			throw new PdfAsSignatureException("error.pdf.sig.01", e);
		} catch (IOException e) {
			throw new PdfAsSignatureException("error.pdf.sig.01", e);
		} 
	}

	public String getPDFSubFilter() {
		return PDSignature.SUBFILTER_ADBE_PKCS7_DETACHED.getName();
	}

	public String getPDFFilter() {
		return PDSignature.FILTER_ADOBE_PPKLITE.getName();
	}

}
