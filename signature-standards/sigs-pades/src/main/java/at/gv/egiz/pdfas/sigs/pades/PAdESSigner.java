package at.gv.egiz.pdfas.sigs.pades;

import iaik.x509.X509Certificate;

import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.lib.api.sign.IPlainSigner;
import at.gv.egiz.sl.util.ISignatureConnector;
import at.gv.egiz.sl.util.ISignatureConnectorSLWrapper;
import at.gv.egiz.sl.util.ISLConnector;

public class PAdESSigner implements IPlainSigner {

	private ISignatureConnector plainSigner;
	
	public PAdESSigner(ISLConnector connector) {
		this.plainSigner = new ISignatureConnectorSLWrapper(connector);
	}
	
	public PAdESSigner(ISignatureConnector signer) {
		this.plainSigner = signer;
	}

	public X509Certificate getCertificate() throws PdfAsException {
		return this.plainSigner.getCertificate();
	}

	public byte[] sign(byte[] input, int[] byteRange) throws PdfAsException {
		return this.plainSigner.sign(input, byteRange);
	}

	public String getPDFSubFilter() {
		return PDSignature.SUBFILTER_ETSI_CADES_DETACHED.getName();
	}

	public String getPDFFilter() {
		return PDSignature.FILTER_ADOBE_PPKLITE.getName();
	}

}
