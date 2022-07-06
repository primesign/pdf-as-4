package at.gv.egiz.pdfas.sigs.pades;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsArrayWithSize.arrayWithSize;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.util.Date;

import org.junit.Test;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.lib.api.sign.ExternalSignatureInfo;
import at.gv.egiz.pdfas.lib.api.sign.IPlainSigner;
import iaik.asn1.ASN1;
import iaik.asn1.ASN1Object;
import iaik.asn1.CodingException;
import iaik.asn1.ObjectID;
import iaik.asn1.structures.AlgorithmID;
import iaik.cms.CMSParsingException;
import iaik.cms.ContentInfo;
import iaik.cms.DigestInfo;
import iaik.cms.SignedData;
import iaik.cms.SignerInfo;
import iaik.x509.X509Certificate;

public class PAdESExternalSignerTest {
	
	private final IPlainSigner cut = new PAdESExternalSigner();
	
	private final X509Certificate signingCertificate;
	private final PrivateKey signingKey;

	public PAdESExternalSignerTest() throws IOException, CertificateEncodingException, CertificateException, KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException {
		
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		try (InputStream in = PAdESExternalSignerTest.class.getResourceAsStream("/test.p12")) {
			keyStore.load(in, "123456".toCharArray());
		}
		
		signingCertificate = new X509Certificate(keyStore.getCertificate("pdf").getEncoded());
		signingKey = (PrivateKey) keyStore.getKey("pdf", "123456".toCharArray());
		
	}
	
	@Test
	public void test_determineExternalSignatureInfo() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, PdfAsException, UnrecoverableKeyException, CodingException, CMSParsingException {

		Date signingTime = new Date();
		
		ExternalSignatureInfo externalSignatureInfo = cut.determineExternalSignatureInfo("pdf-byte-range-content".getBytes(), signingCertificate, signingTime, true);

		assertNotNull(externalSignatureInfo);
		assertThat(externalSignatureInfo.getDigestAlgorithm(), is(AlgorithmID.sha256));
		assertNotNull(externalSignatureInfo.getDigestValue());
		assertThat(externalSignatureInfo.getSignatureAlgorithm(), is(AlgorithmID.sha256WithRSAEncryption));
		assertNotNull(externalSignatureInfo.getSignatureObject());
		
		// validate ASN.1 object
		ASN1 asn1 = new ASN1(externalSignatureInfo.getSignatureObject());
		ASN1Object asn1Object = asn1.toASN1Object();
		ContentInfo contentInfo = new ContentInfo(asn1Object);
		// expect ContentInfo with SignedData
		assertThat(contentInfo.getContentType(), is(ObjectID.cms_signedData));
		// validate SignedData -> SignerInfo
		SignedData signedData = (SignedData) contentInfo.getContent();
		SignerInfo[] signerInfos = signedData.getSignerInfos();
		assertThat(signerInfos, arrayWithSize(1));
		
	}
	
	@Test
	public void test_applyPlainExternalSignatureValue() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, PdfAsException, UnrecoverableKeyException, CodingException, CMSParsingException, InvalidKeyException, SignatureException {

		Date signingTime = new Date();

		// ** determine infos required for external signature device
		ExternalSignatureInfo externalSignatureInfo = cut.determineExternalSignatureInfo("pdf-byte-range-content".getBytes(), signingCertificate, signingTime, true);
		
		// ** create external signature
		Signature signature = Signature.getInstance("NONEwithRSA");
		signature.initSign(signingKey);
		// keep in mind that RSA signature values must be wrapped in DigestInfo
		signature.update(new DigestInfo(externalSignatureInfo.getDigestAlgorithm(), externalSignatureInfo.getDigestValue()).toByteArray());
		byte[] externalSignatureValue = signature.sign();
		
		// ** insert external signature value into signature object
		byte[] encodedFinalSignatureObject = cut.applyPlainExternalSignatureValue(externalSignatureValue, externalSignatureInfo.getSignatureObject());
		
		// ** verify signature
		ASN1 asn1 = new ASN1(encodedFinalSignatureObject);
		ASN1Object asn1Object = asn1.toASN1Object();
		ContentInfo contentInfo = new ContentInfo(asn1Object);
		SignedData signedData = (SignedData) contentInfo.getContent();
		// since SignedData is EXPLICT we need to provide the content (or the digest)
		signedData.setContent("pdf-byte-range-content".getBytes());
		// verify
		signedData.verify(signingCertificate);
		
	}
	
}
