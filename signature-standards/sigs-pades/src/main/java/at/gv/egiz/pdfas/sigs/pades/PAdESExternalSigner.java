package at.gv.egiz.pdfas.sigs.pades;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;
import at.gv.egiz.pdfas.lib.impl.status.RequestedSignature;
import iaik.x509.X509Certificate;

/**
 * This PAdES signer implementation can only be used together with "external" signatures with non Security Layer
 * signature creation devices creating plain signatures. Since all functionality is covered by the abstract
 * {@link PAdESSignerBase} this class does contain further code.
 * 
 * @author Thomas Knall, PrimeSign GmbH
 *
 */
public class PAdESExternalSigner extends PAdESSignerBase {

	@Override
	public X509Certificate getCertificate(SignParameter parameter) throws PdfAsException {
		throw new IllegalStateException(
				PAdESExternalSigner.class.getSimpleName() + " can only be used together with *ExternalSignature(...) api methods.");
	}

	@Override
	public byte[] sign(byte[] input, int[] byteRange, SignParameter parameter, RequestedSignature requestedSignature) throws PdfAsException {
		throw new IllegalStateException(
				PAdESExternalSigner.class.getSimpleName() + " can only be used together with *ExternalSignature(...) api methods.");
	}

}
