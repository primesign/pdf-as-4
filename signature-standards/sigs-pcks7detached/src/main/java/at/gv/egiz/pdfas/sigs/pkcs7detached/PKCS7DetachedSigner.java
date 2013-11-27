package at.gv.egiz.pdfas.sigs.pkcs7detached;

import iaik.asn1.structures.AlgorithmID;
import iaik.cms.SignedDataStream;
import iaik.cms.SignerInfo;
import iaik.cms.SubjectKeyID;
import iaik.security.ecc.provider.ECCProvider;
import iaik.security.provider.IAIK;
import iaik.x509.X509Certificate;
import iaik.x509.X509ExtensionException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;

import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;

import at.gv.egiz.pdfas.common.exceptions.PDFIOException;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.exceptions.PdfAsSignatureException;
import at.gv.egiz.pdfas.lib.api.sign.IPlainSigner;

public class PKCS7DetachedSigner implements IPlainSigner {

	PrivateKey privKey;
	X509Certificate cert;

	public PKCS7DetachedSigner(String file, String alias, String kspassword,
			String keypassword, String type) throws PdfAsException {
		try {
			IAIK.getInstance();
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

	public byte[] sign(byte[] input, int[] byteRange) throws PdfAsException {
		try {
			SignedDataStream signed_data_stream = new SignedDataStream(
					new ByteArrayInputStream(input), SignedDataStream.EXPLICIT);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			signed_data_stream.addCertificates(new Certificate[] { cert });

			SubjectKeyID subjectKeyId = new SubjectKeyID(cert);
			SignerInfo signer1 = new SignerInfo(subjectKeyId,
					AlgorithmID.sha256, privKey);
			signed_data_stream.addSignerInfo(signer1);
			InputStream data_is = signed_data_stream.getInputStream();
			if (signed_data_stream.getMode() == SignedDataStream.EXPLICIT) {
				byte[] buf = new byte[1024];
				int r;
				while ((r = data_is.read(buf)) > 0) {
					// do something useful
				}
			}
			signed_data_stream.writeTo(baos);
			return baos.toByteArray();
		} catch (NoSuchAlgorithmException e) {
			throw new PdfAsSignatureException("", e);
		} catch (X509ExtensionException e) {
			throw new PdfAsSignatureException("", e);
		}  catch (IOException e) {
			throw new PDFIOException("", e);
		}
	}

	public String getPDFSubFilter() {
		return PDSignature.SUBFILTER_ADBE_PKCS7_DETACHED.getName();
	}
	
	public String getPDFFilter() {
		return PDSignature.FILTER_ADOBE_PPKLITE.getName();
	}

}
