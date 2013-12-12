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
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Date;

import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
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
			throw new PdfAsException("Failed to get KeyStore", e);
		}
	}

	public X509Certificate getCertificate() {
		return cert;
	}

	public byte[] sign(byte[] input, int[] byteRange) throws PdfAsException {
		try {
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
			int r;
			while ((r = dataIs.read(buf)) > 0)
				; // skip data
			ContentInfo ci = new ContentInfo(si);

			return ci.getEncoded();
		} catch (NoSuchAlgorithmException e) {
			throw new PdfAsSignatureException("", e);
		} catch (iaik.cms.CMSException e) {
			throw new PdfAsSignatureException("", e);
		} catch (IOException e) {
			throw new PdfAsSignatureException("", e);
		} 
	}

	public String getPDFSubFilter() {
		return PDSignature.SUBFILTER_ADBE_PKCS7_DETACHED.getName();
	}

	public String getPDFFilter() {
		return PDSignature.FILTER_ADOBE_PPKLITE.getName();
	}

}
