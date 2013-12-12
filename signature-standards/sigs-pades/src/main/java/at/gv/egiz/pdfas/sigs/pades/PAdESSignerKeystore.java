package at.gv.egiz.pdfas.sigs.pades;

import iaik.asn1.ASN1Object;
import iaik.asn1.CodingException;
import iaik.asn1.ObjectID;
import iaik.asn1.SEQUENCE;
import iaik.asn1.UTF8String;
import iaik.asn1.structures.AlgorithmID;
import iaik.asn1.structures.Attribute;
import iaik.asn1.structures.ChoiceOfTime;
import iaik.cms.ContentInfo;
import iaik.cms.IssuerAndSerialNumber;
import iaik.cms.SignedData;
import iaik.cms.SignerInfo;
import iaik.smime.ess.ESSCertID;
import iaik.smime.ess.ESSCertIDv2;
import iaik.x509.X509Certificate;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.exceptions.PdfAsSignatureException;
import at.gv.egiz.pdfas.lib.api.sign.IPlainSigner;
import at.gv.egiz.pdfas.lib.util.CertificateUtils;

public class PAdESSignerKeystore implements IPlainSigner {

	private static final Logger logger = LoggerFactory
			.getLogger(PAdESSignerKeystore.class);

	PrivateKey privKey;
	X509Certificate cert;

	public PAdESSignerKeystore(String file, String alias, String kspassword,
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
	
	private void setMimeTypeAttrib(List<Attribute> attributes, String mimeType) {
	    String oidStr = "0.4.0.1733.2.1";
	    String name = "mime-type";
	    ObjectID mimeTypeOID = new ObjectID(oidStr, name);

	    Attribute mimeTypeAtt = new Attribute(mimeTypeOID, new ASN1Object[] {new UTF8String(mimeType)});
	    attributes.add(mimeTypeAtt);
	  }

	  private void setContentTypeAttrib(List<Attribute> attributes) {
	    Attribute contentType = new Attribute(ObjectID.contentType, new ASN1Object[] {ObjectID.cms_data});
	    attributes.add(contentType);
	  }

	  private void setSigningCertificateAttrib(List<Attribute> attributes, X509Certificate signingCertificate) throws CertificateException, NoSuchAlgorithmException, CodingException {
	    ObjectID id;
	    ASN1Object value = new SEQUENCE();
	    AlgorithmID[] algorithms = CertificateUtils.getAlgorithmIDs(signingCertificate);
	    if (algorithms[1].equals(AlgorithmID.sha1)) {
	      id = ObjectID.signingCertificate;
	      value.addComponent(new ESSCertID(signingCertificate, true).toASN1Object());
	    }
	    else {
	      id = ObjectID.signingCertificateV2;
	      value.addComponent(new ESSCertIDv2(algorithms[1], signingCertificate, true).toASN1Object());
	    }
	    ASN1Object signingCert = new SEQUENCE();
	    signingCert.addComponent(value);
	    Attribute signingCertificateAttrib = new Attribute(id, new ASN1Object[] {signingCert});
	    attributes.add(signingCertificateAttrib);
	  }

	  private void setSigningTimeAttrib(List<Attribute> attributes, Date date) {
	    Attribute signingTime = new Attribute(ObjectID.signingTime, new ASN1Object[] {new ChoiceOfTime(date).toASN1Object()});
	    attributes.add(signingTime);
	  }

	private void setAttributes(String mimeType, X509Certificate signingCertificate, Date signingTime, 
			SignerInfo signerInfo) throws CertificateException, NoSuchAlgorithmException, CodingException {
	    List<Attribute> attributes = new ArrayList<Attribute>();
	    setMimeTypeAttrib(attributes, mimeType);
	    setContentTypeAttrib(attributes);
	    setSigningCertificateAttrib(attributes, signingCertificate);
	    setSigningTimeAttrib(attributes, signingTime);
	    Attribute[] attributeArray = attributes.toArray(new Attribute[attributes.size()]);
	    signerInfo.setSignedAttributes(attributeArray);
	  }
	
	public byte[] sign(byte[] input, int[] byteRange) throws PdfAsException {
		try {
			IssuerAndSerialNumber issuer = new IssuerAndSerialNumber(cert);
			
			AlgorithmID[] algorithms = CertificateUtils.getAlgorithmIDs(cert);
			
			SignerInfo signer1 = new SignerInfo(issuer, algorithms[1],
					algorithms[0], privKey);

			SignedData si = new SignedData(input, SignedData.EXPLICIT);
			si.addCertificates(new Certificate[] { cert });
			setAttributes("application/pdf", cert, new Date(), signer1);
			si.addSignerInfo(signer1);
			InputStream dataIs = si.getInputStream();
			byte[] buf = new byte[1024];
			@SuppressWarnings("unused")
			int r;
			while ((r = dataIs.read(buf)) > 0)
				; // skip data
			ContentInfo ci = new ContentInfo(si);

			return ci.getEncoded();
		} catch (NoSuchAlgorithmException e) {
			throw new PdfAsSignatureException("error.pdf.sig.01", e);
		} catch (iaik.cms.CMSException e) {
			throw new PdfAsSignatureException("error.pdf.sig.01", e);
		} catch (IOException e) {
			throw new PdfAsSignatureException("error.pdf.sig.01", e);
		} catch (CertificateException e) {
			throw new PdfAsSignatureException("error.pdf.sig.01", e);
		} catch (CodingException e) {
			throw new PdfAsSignatureException("error.pdf.sig.01", e);
		}
	}

	public String getPDFSubFilter() {
		return PDSignature.SUBFILTER_ETSI_CADES_DETACHED.getName();
	}

	public String getPDFFilter() {
		return PDSignature.FILTER_ADOBE_PPKLITE.getName();
	}

}