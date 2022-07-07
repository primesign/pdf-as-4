package at.gv.egiz.pdfas.lib.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.activation.DataSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.NullOutputStream;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Test;

import at.gv.egiz.pdfas.common.exceptions.PDFASError;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.lib.api.ByteArrayDataSource;
import at.gv.egiz.pdfas.lib.api.PdfAs;
import at.gv.egiz.pdfas.lib.api.PdfAsFactory;
import at.gv.egiz.pdfas.lib.api.StatusRequest;
import at.gv.egiz.pdfas.lib.api.sign.ExternalSignatureContext;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;
import at.gv.egiz.pdfas.lib.api.sign.SignResult;
import at.gv.egiz.pdfas.lib.api.sign.SigningTimeSource;
import at.gv.egiz.pdfas.lib.impl.status.OperationStatus;
import at.gv.egiz.pdfas.lib.impl.status.RequestedSignature;
import at.gv.egiz.pdfas.sigs.pades.PAdESExternalSigner;
import at.gv.egiz.pdfas.sigs.pades.PAdESSignerKeystore;
import iaik.asn1.ObjectID;
import iaik.asn1.structures.AlgorithmID;
import iaik.cms.InvalidSignatureValueException;

public class PdfAsImplTest {

	private final File pdfasConfigFolder = new File(PdfAsImplTest.class.getResource("/pdfas-config").getFile());
	private final PdfAs pdfas = PdfAsFactory.createPdfAs(pdfasConfigFolder);
	
	private final IMocksControl ctrl = EasyMock.createControl();
	
	private final PAdESSignerKeystore signer;
	
	private final DataSource inputDataSource;
	
	private final X509Certificate signingCertificate;
	
	private final PrivateKey signingKey;
	
	public PdfAsImplTest() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, PDFASError, UnrecoverableKeyException {
		
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		try (InputStream in = PdfAsImplTest.class.getResourceAsStream("/test.p12")) {
			keyStore.load(in, "123456".toCharArray());
		}
		signer = new PAdESSignerKeystore(keyStore, "ecc_test", "123456");
		
		try (InputStream in = getClass().getResourceAsStream("/rotate_090.pdf")) {
			inputDataSource = new ByteArrayDataSource(IOUtils.toByteArray(in));
		}
		
		signingCertificate = (X509Certificate) keyStore.getCertificate("ecc_test");
		signingKey = (PrivateKey) keyStore.getKey("ecc_test", "123456".toCharArray());
		
	}
	
	@Test
	public void test_sign_useOfSigningTimeSource() throws PDFASError, NoSuchAlgorithmException, CertificateException, IOException, KeyStoreException {
		
		SignParameter signParameter = PdfAsFactory.createSignParameter(pdfas.getConfiguration(), inputDataSource, NullOutputStream.NULL_OUTPUT_STREAM);
		signParameter.setPlainSigner(signer);
		
		ctrl.reset();
		
		SigningTimeSource signingTimeSource = ctrl.createMock(SigningTimeSource.class);
		signParameter.setSigningTimeSource(signingTimeSource);
		
		Calendar otherDay = Calendar.getInstance();
		otherDay.setTime(Date.from(Instant.parse("2007-12-03T10:15:30.00Z")));
		
		expect(signingTimeSource.getSigningTime(anyObject(RequestedSignature.class))).andReturn(otherDay);
		
		ctrl.replay();
		
		SignResult result = pdfas.sign(signParameter);
		
		ctrl.verify();
		
		assertThat(result.getSigningDate(), is(otherDay));

	}
	
	@Test
	public void test_sign_resultProvidesSigningTime() throws PDFASError, NoSuchAlgorithmException, CertificateException, IOException, KeyStoreException {
		
		SignParameter signParameter = PdfAsFactory.createSignParameter(pdfas.getConfiguration(), inputDataSource, NullOutputStream.NULL_OUTPUT_STREAM);
		signParameter.setPlainSigner(signer);
		
		SignResult result = pdfas.sign(signParameter);
		
		assertThat(result.getSigningDate().getTime()).isInSameMinuteWindowAs(new Date());
		
	}
	
	@Test
	public void test_stepwise_sign_resultProvidesSigningTime() throws PDFASError, NoSuchAlgorithmException, CertificateException, IOException, KeyStoreException, PdfAsException {
		
		SignParameter signParameter = PdfAsFactory.createSignParameter(pdfas.getConfiguration(), inputDataSource, NullOutputStream.NULL_OUTPUT_STREAM);
		signParameter.setPlainSigner(signer);
		
		StatusRequest statusRequest = pdfas.startSign(signParameter);
		
		statusRequest.setCertificate(signingCertificate.getEncoded());
		statusRequest = pdfas.process(statusRequest);

		// since we need a valid signature value we perform a signature (outside the startSign -> process -> process -> finishSign path).

		ctrl.reset();
		
		RequestedSignature requestedSignature = ctrl.createMock(RequestedSignature.class);
		OperationStatus operationStatus = ctrl.createMock(OperationStatus.class);
		expect(requestedSignature.getStatus()).andReturn(operationStatus);
		expect(operationStatus.getMetaInformations()).andReturn(new HashMap<>());

		ctrl.replay();
		
		byte[] signature = signer.sign(statusRequest.getSignatureData(), statusRequest.getSignatureDataByteRange(), signParameter, requestedSignature);
		
		// now we have a signature value for the next process step
		
		statusRequest.setSigature(signature);
		statusRequest = pdfas.process(statusRequest);
		
		SignResult result = pdfas.finishSign(statusRequest);
		
		assertThat(result.getSigningDate().getTime()).isInSameMinuteWindowAs(new Date());

	}
	
	@Test
	public void test_stepwise_sign_useOfSigningTimeSource() throws PDFASError, NoSuchAlgorithmException, CertificateException, IOException, KeyStoreException, PdfAsException {

		SignParameter signParameter = PdfAsFactory.createSignParameter(pdfas.getConfiguration(), inputDataSource, NullOutputStream.NULL_OUTPUT_STREAM);
		signParameter.setPlainSigner(signer);
		
		StatusRequest statusRequest = pdfas.startSign(signParameter);
		
		ctrl.reset();
		
		SigningTimeSource signingTimeSource = ctrl.createMock(SigningTimeSource.class);
		signParameter.setSigningTimeSource(signingTimeSource);
		
		Calendar otherDay = Calendar.getInstance();
		otherDay.setTime(Date.from(Instant.parse("2007-12-03T10:15:30.00Z")));
		
		expect(signingTimeSource.getSigningTime(anyObject(RequestedSignature.class))).andReturn(otherDay);
		
		ctrl.replay();
		
		statusRequest.setCertificate(signingCertificate.getEncoded());
		statusRequest = pdfas.process(statusRequest);
		
		ctrl.verify();
		
		
		// since we need a valid signature value we perform a signature (outside the startSign -> process -> process -> finishSign path).

		ctrl.reset();
		
		RequestedSignature requestedSignature = ctrl.createMock(RequestedSignature.class);
		OperationStatus operationStatus = ctrl.createMock(OperationStatus.class);
		expect(requestedSignature.getStatus()).andReturn(operationStatus);
		expect(operationStatus.getMetaInformations()).andReturn(new HashMap<>());

		ctrl.replay();
		
		byte[] signature = signer.sign(statusRequest.getSignatureData(), statusRequest.getSignatureDataByteRange(), signParameter, requestedSignature);
		
		// now we have a signature value for the next process step
		
		statusRequest.setSigature(signature);
		statusRequest = pdfas.process(statusRequest);
		
		SignResult result = pdfas.finishSign(statusRequest);
		
		assertThat(result.getSigningDate(), is(otherDay));

	}
	
	private static Calendar toCalendar(String isoDateString) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(Date.from(Instant.parse(isoDateString)));
		return calendar;
	}
	
	@Test
	public void test_startExternalSignature() throws PDFASError {
		
		SignParameter signParameter = PdfAsFactory.createSignParameter(pdfas.getConfiguration(), inputDataSource, NullOutputStream.NULL_OUTPUT_STREAM);
		signParameter.setPlainSigner(new PAdESExternalSigner());
		
		ctrl.reset();
		
		SigningTimeSource signingTimeSource = ctrl.createMock(SigningTimeSource.class);
		signParameter.setSigningTimeSource(signingTimeSource);
		
		Calendar signingTime = toCalendar("2007-12-03T10:15:30.00Z");
		
		expect(signingTimeSource.getSigningTime(anyObject(RequestedSignature.class))).andReturn(signingTime);
		
		ctrl.replay();
		
		ExternalSignatureContext ctx = new ExternalSignatureContext();
		
		pdfas.startExternalSignature(signParameter, signingCertificate, ctx);
		
		ctrl.verify();
		
		assertThat(ctx.getDigestAlgorithmOid(), is(AlgorithmID.sha256.getAlgorithm().getID())); // "2.16.840.1.101.3.4.2.1"
		assertNotNull(ctx.getDigestValue());
		assertNotNull(ctx.getPreparedDocument());
		assertThat(ctx.getSignatureAlgorithmOid(), is(AlgorithmID.ecdsa_With_SHA256.getAlgorithm().getID())); // "1.2.840.10045.4.3.2"
		assertNotNull(ctx.getSignatureByteRange());
		assertNotNull(ctx.getSignatureObject());
		assertThat(ctx.getSigningCertificate(), is(signingCertificate));
		assertThat(ctx.getSigningTime(), is(signingTime));
		
	}
	
	@Test
	public void test_startExternalSignature_finishExternalSignature() throws Exception {

		String signedFileName = getClass().getSimpleName() + "-test_finishExternalSignature-" + System.currentTimeMillis() + ".pdf";
		File signedFile = new File(FileUtils.getTempDirectory(), signedFileName);
		
		try (OutputStream out = new FileOutputStream(signedFile)) {
			
			SignParameter signParameter = PdfAsFactory.createSignParameter(pdfas.getConfiguration(), inputDataSource, out);
			signParameter.setPlainSigner(new PAdESExternalSigner());
			
			ctrl.reset();
			
			SigningTimeSource signingTimeSource = ctrl.createMock(SigningTimeSource.class);
			signParameter.setSigningTimeSource(signingTimeSource);
			
			Calendar signingTime = toCalendar("2007-12-03T10:15:30.00Z");
			
			expect(signingTimeSource.getSigningTime(anyObject(RequestedSignature.class))).andReturn(signingTime);
			
			ctrl.replay();
			
			ExternalSignatureContext ctx = new ExternalSignatureContext();
			
			pdfas.startExternalSignature(signParameter, signingCertificate, ctx);
			
			// ** create external signature
			Signature signature = Signature.getInstance("NONEwithECDSA");
			signature.initSign(signingKey);
			signature.update(ctx.getDigestValue());
			byte[] externalSignatureValue = signature.sign();
			
			SignResult signResult = pdfas.finishExternalSignature(signParameter, externalSignatureValue, ctx);
			
			ctrl.verify();
			
			assertNotNull(signResult);
			assertThat(signResult.getSignerCertificate(), is(signingCertificate));
			assertThat(signResult.getSigningDate(), is(signingTime));
			
			// TODO[PDFAS-114]: Update test once signature position is returned
			// TODO[PDFAS-114]: Update test once processInformations(sic!) is returned

		} catch (Exception e) {
			signedFile.delete();
			throw e;
		}
		
		System.out.println("Signed file: " + signedFile.getAbsolutePath());

	}
	
	@Test
	public void test_finishExternalSignature_invalidSignatureValue() throws Exception {

		SignParameter signParameter = PdfAsFactory.createSignParameter(pdfas.getConfiguration(), inputDataSource, NullOutputStream.NULL_OUTPUT_STREAM);
		signParameter.setPlainSigner(new PAdESExternalSigner());
		
		ctrl.reset();
		
		ctrl.replay();
		
		ExternalSignatureContext ctx = new ExternalSignatureContext();
		
		pdfas.startExternalSignature(signParameter, signingCertificate, ctx);
		
		// ** create external signature (from content != inputDataSource)
		Signature signature = Signature.getInstance("NONEwithECDSA");
		signature.initSign(signingKey);
		
		// deliberately calculate digest from wrong content
		byte[] digestInputData = "otherContent".getBytes();
		byte[] digest = new AlgorithmID(new ObjectID(ctx.getDigestAlgorithmOid())).getMessageDigestInstance().digest(digestInputData);
		
		signature.update(digest);
		byte[] externalSignatureValue = signature.sign();
		
		// expect that finishSignature detects invalid signature (value)
		
		PDFASError ex = assertThrows(PDFASError.class, () -> pdfas.finishExternalSignature(signParameter, externalSignatureValue, ctx));
		assertThat(ex.getCode(), is(11008L));  // at.gv.egiz.pdfas.common.exceptions.ErrorConstants.ERROR_SIG_INVALID_BKU_SIG
		assertThat(ex.getCause(), is(instanceOf(InvalidSignatureValueException.class)));
		
		ctrl.verify();

	}

}
