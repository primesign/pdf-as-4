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
import iaik.cms.Utils;
import iaik.pkcs.PKCSException;
import iaik.pkcs.pkcs7.Data;
import iaik.security.ecc.provider.ECCProvider;
import iaik.security.provider.IAIK;
import iaik.x509.X509Certificate;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Date;

import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.exceptions.PdfAsSignatureException;
import at.gv.egiz.pdfas.common.utils.StringUtils;
import at.gv.egiz.pdfas.lib.api.sign.IPlainSigner;

public class PKCS7DetachedSigner implements IPlainSigner {

	private static final Logger logger = LoggerFactory
			.getLogger(PKCS7DetachedSigner.class);
	
	PrivateKey privKey;
	X509Certificate cert;

	public PKCS7DetachedSigner(String file, String alias, String kspassword,
			String keypassword, String type) throws PdfAsException {
		try {
			IAIK.addAsProvider();
			ECCProvider.addAsProvider();
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

	class CMSProcessableInputStream implements CMSProcessable {

		InputStream in;

		public CMSProcessableInputStream(InputStream is) {
			in = is;
		}

		public Object getContent() {
			return null;
		}

		public void write(OutputStream out) throws IOException, CMSException {
			// read the content only one time
			byte[] buffer = new byte[8 * 1024];
			int read;
			while ((read = in.read(buffer)) != -1) {
				out.write(buffer, 0, read);
			}
			in.close();
		}
	}

	private static BouncyCastleProvider provider = new BouncyCastleProvider();

	/*
	 * public byte[] sign(byte[] input, int[] byteRange) throws PdfAsException {
	 * CMSProcessableInputStream content = new CMSProcessableInputStream(new
	 * ByteArrayInputStream(input)); CMSSignedDataGenerator gen = new
	 * CMSSignedDataGenerator(); // CertificateChain List<X509Certificate>
	 * certList = Arrays.asList(cert);
	 * 
	 * CertStore certStore = null; try { certStore =
	 * CertStore.getInstance("Collection", new
	 * CollectionCertStoreParameters(certList), provider);
	 * gen.addSigner(privKey, (X509Certificate)certList.get(0),
	 * CMSSignedGenerator.DIGEST_SHA256); gen.addCertificatesAndCRLs(certStore);
	 * CMSSignedData signedData = gen.generate(content, false, provider); return
	 * signedData.getEncoded(); } catch (Exception e) { // should be handled
	 * e.printStackTrace(); } throw new
	 * RuntimeException("Problem while preparing signature"); }
	 */

	public byte[] sign(byte[] input, int[] byteRange) throws PdfAsException {
		try {
			// SignedDataStream signed_data_stream = new SignedDataStream(
			// new ByteArrayInputStream(input), SignedDataStream.EXPLICIT);
			// ByteArrayOutputStream baos = new ByteArrayOutputStream();
			// signed_data_stream.addCertificates(new Certificate[] { cert });
			//
			// SubjectKeyID subjectKeyId = new SubjectKeyID(cert);
			// SignerInfo signer1 = new SignerInfo(subjectKeyId,
			// AlgorithmID.sha256, privKey);
			// signed_data_stream.addSignerInfo(signer1);
			// InputStream data_is = signed_data_stream.getInputStream();
			// if (signed_data_stream.getMode() == SignedDataStream.EXPLICIT) {
			// byte[] buf = new byte[1024];
			// int r;
			// while ((r = data_is.read(buf)) > 0) {
			// // do something useful
			// }
			// }
			// SubjectKeyID subjectKeyId = new SubjectKeyID(cert);
			IssuerAndSerialNumber issuer = new IssuerAndSerialNumber(cert);
			SignerInfo signer1 = new SignerInfo(issuer, AlgorithmID.sha256, 
					AlgorithmID.ecdsa_plain_With_SHA256, 
					privKey);

			SignedData si = new SignedData(input, SignedData.EXPLICIT);
			si.addCertificates(new Certificate[] { cert });
			Attribute signingTime = new Attribute(ObjectID.signingTime,
					new ASN1Object[] { new ChoiceOfTime(new Date())
							.toASN1Object() });
			Attribute contentType = new Attribute(ObjectID.contentType, new ASN1Object[] {
					new ObjectID("1.2.840.113549.1.7.1")
				});
			// Attribute signingCert = new
			// Attribute(ObjectID.signingCertificateV2,
			// new ASN1Object[] { cert.toASN1Object() });

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
