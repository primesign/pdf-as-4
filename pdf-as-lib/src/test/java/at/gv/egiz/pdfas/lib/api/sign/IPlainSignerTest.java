package at.gv.egiz.pdfas.lib.api.sign;

import static org.junit.Assert.assertThrows;

import java.util.Date;

import org.easymock.EasyMock;
import org.junit.Test;

import at.gv.egiz.pdfas.common.exceptions.PDFASError;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.lib.impl.status.RequestedSignature;
import at.gv.egiz.pdfas.lib.pki.spi.CertificateVerificationData;
import iaik.x509.X509Certificate;

/**
 * Tests default method implementations of interface IPlainSigner.
 * 
 * @author Thomas Knall, PrimeSign GmbH
 *
 */
public class IPlainSignerTest {

	private final IPlainSigner cut = new IPlainSigner() {

		@Override
		public X509Certificate getCertificate(SignParameter parameter) throws PdfAsException {
			return null;
		}

		@Override
		public byte[] sign(byte[] input, int[] byteRange, SignParameter parameter, RequestedSignature requestedSignature) throws PdfAsException {
			return null;
		}

		@Override
		public String getPDFSubFilter() {
			return null;
		}

		@Override
		public String getPDFFilter() {
			return null;
		}

		@Override
		public CertificateVerificationData getCertificateVerificationData(RequestedSignature requestedSignature) throws PDFASError {
			return null;
		}

	};

	@Test
	public void test_default_applyPlainExternalSignatureValue_expect_UnsupportedOperationExeption() {
		assertThrows(UnsupportedOperationException.class, () -> cut.applyPlainExternalSignatureValue(new byte[] {}, new byte[] {}));
	}

	@Test
	public void test_default_determineExternalSignatureInfo_expect_UnsupportedOperationExeption() {
		assertThrows(UnsupportedOperationException.class, () -> cut.determineExternalSignatureInfo(new byte[] {}, EasyMock.createMock(X509Certificate.class), new Date(), true));
	}

}
