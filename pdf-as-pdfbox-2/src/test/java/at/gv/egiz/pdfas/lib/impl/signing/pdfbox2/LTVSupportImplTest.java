package at.gv.egiz.pdfas.lib.impl.signing.pdfbox2;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createControl;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.newCapture;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.security.cert.CRLException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;

import javax.annotation.Nonnull;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.assertj.core.util.Lists;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IMockBuilder;
import org.easymock.IMocksControl;
import org.junit.Test;

import at.gv.egiz.pdfas.lib.pki.spi.CertificateVerificationData;

public class LTVSupportImplTest {
	
	private final LTVSupportImpl cut = new LTVSupportImpl();
	
	/**
	 * Validates a provided extensions dictionary.
	 * <p>
	 * According to ETSI TS 102 778-4 v1.1.2, 4.4 and ISO 32000-1:2008, 7.12.2 the following dictionary is expected:
	 * </p>
	 * 
	 * <pre>
	 * <</Extensions
	 *   <</ADBE
	 *     <</BaseVersion/1.7
	 *       /ExtensionLevel 5
	 *     >>
	 *   >>
	 * >>
	 * </pre>
	 * 
	 * Further entries are allowed (and ignored).
	 * 
	 * @param extDictionary     The provided extensions dictionary. (required; must not be {@code null})
	 * @param expectDirtyExtDic {@code true} if modifications are expected within the /Extensions dictionary, {@code false}
	 *                          otherwise.
	 * @param expectDirtyExtDic {@code true} if modifications are expected within the /ADBE dictionary, {@code false}
	 *                          otherwise.
	 */
	private void validateExtensionsDictionary(@Nonnull COSDictionary extDictionary, boolean expectDirtyExtDic, boolean expectDirtyAdbeDic) {

		// expect direct objects (ISO 32000-1:2008, 7.12.1)
		assertTrue(extDictionary.isDirect());
		
		// when the extensions dictionary is expected to be modified, it should be marked dirty
		assertEquals(expectDirtyExtDic, extDictionary.isNeedToBeUpdated());
		
		COSDictionary adbeDic = (COSDictionary) extDictionary.getDictionaryObject("ADBE");
		assertNotNull(adbeDic);
		
		// spec requires direct object (ISO 32000-1:2008, 7.12.1)
		assertTrue(adbeDic.isDirect());
		
		// when the adbe dictionary is expected to be modified, it should be marked dirty
		assertEquals(expectDirtyAdbeDic, adbeDic.isNeedToBeUpdated());
		
		// expect /BaseVersion/1.7/ExtensionLevel 5 (ETSI TS 102 778-4 v1.1.2, 4.4)
		// with "1.7" being a COS name (ISO 32000-1:2008, 7.12.2)
		assertThat(adbeDic.getCOSName(COSName.getPDFName("BaseVersion")), is(COSName.getPDFName("1.7")));
		assertThat(adbeDic.getInt("ExtensionLevel"), is(5));
		
	}
	
	@Test
	public void test_addOrUpdateADBEExtension() {
		
		COSDictionary extDictionary = new COSDictionary();
		extDictionary.setDirect(true);

		/**
		 * <</Extensions
		 * >>
		 */
		
		cut.addOrUpdateADBEExtension(extDictionary);

		validateExtensionsDictionary(extDictionary, true, true);
		
	}

	@Test
	public void test_addOrUpdateADBEExtension_dictionary_already_exists() {
		
		COSDictionary adbeDic = new COSDictionary();
		adbeDic.setDirect(true);
		adbeDic.setItem("BaseVersion", COSName.getPDFName("1.7"));
		adbeDic.setInt("ExtensionLevel", 5);
		
		COSDictionary extDictionary = new COSDictionary();
		extDictionary.setItem("ADBE", adbeDic);
		extDictionary.setDirect(true);
		// add some other developer extension ("MYDEVEXT")
		COSDictionary devDic = new COSDictionary();
		extDictionary.setItem("MYDEVEXT", devDic);
		devDic.setDirect(true);
		devDic.setItem("BaseVersion", COSName.getPDFName("1.7"));
		devDic.setInt("ExtensionLevel", 42);
		
		/**
		 * <</Extensions
		 *   <</ADBE
		 *     <</BaseVersion/1.7
		 *       /ExtensionLevel 5
		 *     >>
		 *   >>
		 *   <</MYDEVEXT
		 *     <</BaseVersion/1.7
		 *       /ExtensionLevel 42
		 *     >>
		 *   >>
		 * >>
		 */
		
		cut.addOrUpdateADBEExtension(extDictionary);
		
		// expect that nothing has changed (false, false) since we already had appropriate extensions
		validateExtensionsDictionary(extDictionary, false, false);
		
		// expect that existing dictionary is used
		COSDictionary newAdbeDic = (COSDictionary)extDictionary.getDictionaryObject("ADBE");
		assertSame(newAdbeDic, adbeDic);
		COSDictionary newDevDic = (COSDictionary)extDictionary.getDictionaryObject("MYDEVEXT");
		assertSame(newDevDic, devDic);
		
		// expect no change at all
		assertThat(extDictionary.entrySet(), hasSize(2));
		assertThat(newDevDic.getCOSName(COSName.getPDFName("BaseVersion")), is(COSName.getPDFName("1.7")));
		assertThat(newDevDic.getInt("ExtensionLevel"), is(42));
		assertFalse(newDevDic.isNeedToBeUpdated());
	}


	@Test
	public void test_addOrUpdateADBEExtension_dictionary_already_exists_with_non_suitable_values() {
		
		COSDictionary adbeDic = new COSDictionary();
		adbeDic.setDirect(true);
		adbeDic.setItem("BaseVersion", COSName.getPDFName("1.5"));
		adbeDic.setInt("ExtensionLevel", 42);
		
		COSDictionary extDictionary = new COSDictionary();
		extDictionary.setItem("ADBE", adbeDic);
		extDictionary.setDirect(true);
		
		/**
		 * <</Extensions
		 *   <</ADBE
		 *     <</BaseVersion/1.5
		 *       /ExtensionLevel 42
		 *     >>
		 * >>
		 */
		
		cut.addOrUpdateADBEExtension(extDictionary);
		
		validateExtensionsDictionary(extDictionary, true, true);

	}
	
	@Test
	public void test_addOrUpdateExtensions_noExtensionsYet() {
		
		IMocksControl ctrl = createControl();
		
		// mock only abstract method
		// @formatter:off
		IMockBuilder<LTVSupportImpl> mockBuilder = EasyMock.partialMockBuilder(LTVSupportImpl.class)
				.withConstructor()
				.addMockedMethod("addOrUpdateADBEExtension", COSDictionary.class);
		// @formatter:on
		
		LTVSupportImpl cut = mockBuilder.createMock(ctrl);

		// test only aspect of addOrUpdateExtensions, skip test for addOrUpdateExtensions(extDictionary), just make sure method gets called
		
		ctrl.reset();

		PDDocument pdDocument = emptyDocument();
		
		// document has no extensions dictionary
		COSDictionary rootDictionary = pdDocument.getDocumentCatalog().getCOSObject();
		assertNull(rootDictionary.getDictionaryObject("Extensions"));
		// document is not dirty
		assertFalse(rootDictionary.isNeedToBeUpdated());

		Capture<COSDictionary> extDictionary = newCapture();
		
		cut.addOrUpdateADBEExtension(capture(extDictionary));
		
		ctrl.replay();
		
		// cat cut with document that does not have an extension dictionary
		cut.addOrUpdateExtensions(pdDocument);
		
		ctrl.verify();
		
		COSDictionary extDictionaryFromDocument = (COSDictionary) rootDictionary.getDictionaryObject("Extensions");
		// expect that Extentions dictionary has been created 
		assertNotNull(extDictionaryFromDocument);
		
		// make sure mocked method gets called with dictionary
		assertSame(extDictionaryFromDocument, extDictionary.getValue());

		// root dictionary should also be marked dirty
		assertTrue(rootDictionary.isNeedToBeUpdated());
		
	}

	
	@Test
	public void test_addOrUpdateExtensions_with_existing_extensions() {
		
		// test only aspect of addOrUpdateExtensions, skip test for addOrUpdateExtensions(extDictionary), just make sure method gets called
		
		IMocksControl ctrl = createControl();
		
		// mock only abstract method
		// @formatter:off
		IMockBuilder<LTVSupportImpl> mockBuilder = EasyMock.partialMockBuilder(LTVSupportImpl.class)
				.withConstructor()
				.addMockedMethod("addOrUpdateADBEExtension", COSDictionary.class);
		// @formatter:on
		
		LTVSupportImpl cut = mockBuilder.createMock(ctrl);

		PDDocument pdDocument = emptyDocument();
		
		// add extensions to document
		COSDictionary rootDictionary = pdDocument.getDocumentCatalog().getCOSObject();
		COSDictionary extDictionary = new COSDictionary();
		rootDictionary.setItem("Extensions", extDictionary);
		// document is not dirty
		assertFalse(rootDictionary.isNeedToBeUpdated());

		ctrl.reset();
		
		Capture<COSDictionary> capturedExtDictionary = newCapture();
		cut.addOrUpdateADBEExtension(capture(capturedExtDictionary));
		
		ctrl.replay();
		
		// cat cut with document that does not have an extension dictionary
		cut.addOrUpdateExtensions(pdDocument);
		
		ctrl.verify();
		
		COSDictionary extDictionaryFromDocument = (COSDictionary) rootDictionary.getDictionaryObject("Extensions");
		// expect "our" Extensions dictionary 
		assertSame(extDictionaryFromDocument, extDictionary);
		
		// make sure mocked method gets called with dictionary
		assertSame(extDictionaryFromDocument, capturedExtDictionary.getValue());

		// root dictionary should also be marked dirty
		assertTrue(rootDictionary.isNeedToBeUpdated());
		
	}
	
	@Test
	public void test_addLTVInfo_noChainCerts() throws CertificateEncodingException, CRLException, IOException {
		
		IMocksControl ctrl = createControl();
		
		ctrl.reset();
		
		CertificateVerificationData certificateVerificationData = ctrl.createMock(CertificateVerificationData.class);
		expect(certificateVerificationData.getChainCerts()).andReturn(null);
		
		ctrl.replay();
		
		assertThrows(IllegalStateException.class, () -> cut.addLTVInfo(emptyDocument(), certificateVerificationData));
		
		ctrl.verify();
		
	}
	
	@Test
	public void test_addLTVInfo_emptyChainCerts() throws CertificateEncodingException, CRLException, IOException {
		
		IMocksControl ctrl = createControl();
		
		ctrl.reset();
		
		CertificateVerificationData certificateVerificationData = ctrl.createMock(CertificateVerificationData.class);
		expect(certificateVerificationData.getChainCerts()).andReturn(Collections.emptyList());
		
		ctrl.replay();
		
		assertThrows(IllegalStateException.class, () -> cut.addLTVInfo(emptyDocument(), certificateVerificationData));
		
		ctrl.verify();
		
	}
	
	@Test
	public void test_addLTVInfo() throws CertificateEncodingException, CRLException, IOException {
		
		// test only aspect of addLTVInfo, skip tests for addOrUpdateDSS(PDDocument, CertificateVerificationData),
		// ensurePdf17(PDDocument) and addOrUpdateExtensions(PDDocument), just make sure methods get called
		
		IMocksControl ctrl = createControl();
		
		// mock only abstract method
		// @formatter:off
		IMockBuilder<LTVSupportImpl> mockBuilder = EasyMock.partialMockBuilder(LTVSupportImpl.class)
				.withConstructor()
				.addMockedMethod("addOrUpdateDSS", PDDocument.class, CertificateVerificationData.class)
				.addMockedMethod("ensurePdf17", PDDocument.class)
				.addMockedMethod("addOrUpdateExtensions", PDDocument.class);
		// @formatter:on
		
		LTVSupportImpl cut = mockBuilder.createMock(ctrl);

		PDDocument pdDocument = emptyDocument();
		
		ctrl.reset();
		
		CertificateVerificationData certificateVerificationData = ctrl.createMock(CertificateVerificationData.class);
		expect(certificateVerificationData.getChainCerts()).andReturn(Lists.newArrayList(ctrl.createMock(X509Certificate.class)));
		cut.addOrUpdateDSS(pdDocument, certificateVerificationData);
		cut.ensurePdf17(pdDocument);
		cut.addOrUpdateExtensions(pdDocument);
		expect(certificateVerificationData.getCRLs()).andReturn(Collections.emptyList());
		expect(certificateVerificationData.getEncodedOCSPResponses()).andReturn(Collections.emptyList());
		
		ctrl.replay();
		
		cut.addLTVInfo(pdDocument, certificateVerificationData);
		
		ctrl.verify();
		
	}
	
	@Test
	public void test_ensurePdf17_withHeaderVersion14_noCatalogVersion() {
		
		PDDocument pdDocument = emptyDocument();
		
		// (default) header version 1.4
		pdDocument.getDocument().setVersion(1.4f);
		// no catalog version
		
		cut.ensurePdf17(pdDocument);
		
		assertThat(pdDocument.getVersion(), is(1.7f));
		
	}
	
	@Test
	public void test_ensurePdf17_withHeaderVersion14_withCatalogVersion16() {
		
		PDDocument pdDocument = emptyDocument();
		
		// (default) header version 1.4
		pdDocument.getDocument().setVersion(1.4f);
		// catalog version 1.6
		pdDocument.getDocumentCatalog().setVersion("1.6");
		
		cut.ensurePdf17(pdDocument);
		
		assertThat(pdDocument.getVersion(), is(1.7f));
		
	}
	
	@Test
	public void test_ensurePdf17_withHeaderVersion14_withCatalogVersion17() {
		
		PDDocument pdDocument = emptyDocument();
		
		// (default) header version 1.4
		pdDocument.getDocument().setVersion(1.4f);
		// catalog version 1.6
		pdDocument.getDocumentCatalog().setVersion("1.7");
		
		cut.ensurePdf17(pdDocument);
		
		assertThat(pdDocument.getVersion(), is(1.7f));
		
	}
	
	@Test
	public void test_ensurePdf17_withHeaderVersion17_noCatalogVersion() {
		
		PDDocument pdDocument = emptyDocument();
		
		// (default) header version 1.7
		pdDocument.getDocument().setVersion(1.7f);
		// no catalog version
		
		cut.ensurePdf17(pdDocument);
		
		assertThat(pdDocument.getVersion(), is(1.7f));
		
	}
	
	@Test
	public void test_addOrUpdateDSS_noExistingDSS() throws CertificateEncodingException, CRLException, IOException {
		
		// test only aspect of addOrUpdateDSS, skip tests for
		//    addDSSCerts(PDDocument, COSDictionary, Iterable<X509Certificate>)
		//    addDSSOCSPs(PDDocument, COSDictionary, Iterable<byte[]>)
		//    addDSSCRLs(PDDocument, COSDictionary, Iterable<X509CRL>)
		// just make sure methods get called
		
		IMocksControl ctrl = createControl();
		
		// mock only abstract method
		// @formatter:off
		IMockBuilder<LTVSupportImpl> mockBuilder = EasyMock.partialMockBuilder(LTVSupportImpl.class)
				.withConstructor()
				.addMockedMethod("addDSSCerts", PDDocument.class, COSDictionary.class, Iterable.class)
				.addMockedMethod("addDSSOCSPs", PDDocument.class, COSDictionary.class, Iterable.class)
				.addMockedMethod("addDSSCRLs", PDDocument.class, COSDictionary.class, Iterable.class);
		// @formatter:on
		
		LTVSupportImpl cut = mockBuilder.createMock(ctrl);

		PDDocument pdDocument = emptyDocument();
		
		ctrl.reset();
		
		CertificateVerificationData certificateVerificationData = ctrl.createMock(CertificateVerificationData.class);

		// chaincerts
		
		ArrayList<X509Certificate> chainCerts = Lists.newArrayList(ctrl.createMock(X509Certificate.class));
		expect(certificateVerificationData.getChainCerts()).andReturn(chainCerts);

		Capture<COSDictionary> dssDictionary1 = newCapture();
		cut.addDSSCerts(eq(pdDocument), capture(dssDictionary1), eq(chainCerts));
		
		// ocsp responses
		
		ArrayList<byte[]> encodedOCSPResponses = Lists.newArrayList(new byte[] { 1, 2, 3 });
		expect(certificateVerificationData.getEncodedOCSPResponses()).andReturn(encodedOCSPResponses).atLeastOnce();
		
		Capture<COSDictionary> dssDictionary2 = newCapture();
		cut.addDSSOCSPs(eq(pdDocument), capture(dssDictionary2), eq(encodedOCSPResponses));
		
		// clrs
		ArrayList<X509CRL> crls = Lists.newArrayList(ctrl.createMock(X509CRL.class));
		expect(certificateVerificationData.getCRLs()).andReturn(crls).atLeastOnce();
		
		Capture<COSDictionary> dssDictionary3 = newCapture();
		cut.addDSSCRLs(eq(pdDocument), capture(dssDictionary3), eq(crls));
		
		ctrl.replay();
		
		cut.addOrUpdateDSS(pdDocument, certificateVerificationData);
		
		ctrl.verify();
		
		// expect that dss dictionary has been created
		COSDictionary dssDictionaryFromDocument = (COSDictionary) pdDocument.getDocumentCatalog().getCOSObject().getDictionaryObject("DSS");
		assertNotNull(dssDictionaryFromDocument);
		
		assertSame(dssDictionaryFromDocument, dssDictionary1.getValue());
		assertSame(dssDictionaryFromDocument, dssDictionary2.getValue());
		assertSame(dssDictionaryFromDocument, dssDictionary3.getValue());
		
		// expect that path from dss up to the root is dirty
		assertTrue(dssDictionaryFromDocument.isNeedToBeUpdated());
		assertTrue(pdDocument.getDocumentCatalog().getCOSObject().isNeedToBeUpdated());
		
	}
	
	@Test
	public void test_addOrUpdateDSS_existingDSS() throws CertificateEncodingException, CRLException, IOException {
		
		// test only aspect of addOrUpdateDSS, skip tests for
		//    addDSSCerts(PDDocument, COSDictionary, Iterable<X509Certificate>)
		//    addDSSOCSPs(PDDocument, COSDictionary, Iterable<byte[]>)
		//    addDSSCRLs(PDDocument, COSDictionary, Iterable<X509CRL>)
		// just make sure methods get called
		
		IMocksControl ctrl = createControl();
		
		// mock only abstract method
		// @formatter:off
		IMockBuilder<LTVSupportImpl> mockBuilder = EasyMock.partialMockBuilder(LTVSupportImpl.class)
				.withConstructor()
				.addMockedMethod("addDSSCerts", PDDocument.class, COSDictionary.class, Iterable.class)
				.addMockedMethod("addDSSOCSPs", PDDocument.class, COSDictionary.class, Iterable.class)
				.addMockedMethod("addDSSCRLs", PDDocument.class, COSDictionary.class, Iterable.class);
		// @formatter:on
		
		LTVSupportImpl cut = mockBuilder.createMock(ctrl);

		PDDocument pdDocument = emptyDocument();
		COSDictionary existingDssDictionary = new COSDictionary();
		pdDocument.getDocumentCatalog().getCOSObject().setItem("DSS", existingDssDictionary);
		
		ctrl.reset();
		
		CertificateVerificationData certificateVerificationData = ctrl.createMock(CertificateVerificationData.class);

		// chaincerts
		
		ArrayList<X509Certificate> chainCerts = Lists.newArrayList(ctrl.createMock(X509Certificate.class));
		expect(certificateVerificationData.getChainCerts()).andReturn(chainCerts);

		cut.addDSSCerts(pdDocument, existingDssDictionary, chainCerts);
		
		// ocsp responses
		
		ArrayList<byte[]> encodedOCSPResponses = Lists.newArrayList(new byte[] { 1, 2, 3 });
		expect(certificateVerificationData.getEncodedOCSPResponses()).andReturn(encodedOCSPResponses).atLeastOnce();
		
		cut.addDSSOCSPs(pdDocument, existingDssDictionary, encodedOCSPResponses);
		
		// clrs
		ArrayList<X509CRL> crls = Lists.newArrayList(ctrl.createMock(X509CRL.class));
		expect(certificateVerificationData.getCRLs()).andReturn(crls).atLeastOnce();
		
		cut.addDSSCRLs(pdDocument, existingDssDictionary, crls);
		
		ctrl.replay();
		
		cut.addOrUpdateDSS(pdDocument, certificateVerificationData);
		
		ctrl.verify();
		
		// expect that existing dss dictionary has been used
		COSDictionary dssDictionaryFromDocument = (COSDictionary) pdDocument.getDocumentCatalog().getCOSObject().getDictionaryObject("DSS");
		assertNotNull(dssDictionaryFromDocument);
		
		assertSame(existingDssDictionary, dssDictionaryFromDocument);
		
		// expect that path from dss up to the root is dirty
		assertTrue(existingDssDictionary.isNeedToBeUpdated());
		assertTrue(pdDocument.getDocumentCatalog().getCOSObject().isNeedToBeUpdated());
		
	}
	
	@Test
	public void test_addOrUpdateDSS_noOCSP_noCRL_existingDSS() throws CertificateEncodingException, CRLException, IOException {
		
		// test only aspect of addOrUpdateDSS, skip tests for
		//    addDSSCerts(PDDocument, COSDictionary, Iterable<X509Certificate>)
		//    addDSSOCSPs(PDDocument, COSDictionary, Iterable<byte[]>)
		//    addDSSCRLs(PDDocument, COSDictionary, Iterable<X509CRL>)
		// just make sure methods get called
		
		IMocksControl ctrl = createControl();
		
		// mock only abstract method
		// @formatter:off
		IMockBuilder<LTVSupportImpl> mockBuilder = EasyMock.partialMockBuilder(LTVSupportImpl.class)
				.withConstructor()
				.addMockedMethod("addDSSCerts", PDDocument.class, COSDictionary.class, Iterable.class)
				.addMockedMethod("addDSSOCSPs", PDDocument.class, COSDictionary.class, Iterable.class)
				.addMockedMethod("addDSSCRLs", PDDocument.class, COSDictionary.class, Iterable.class);
		// @formatter:on
		
		LTVSupportImpl cut = mockBuilder.createMock(ctrl);

		PDDocument pdDocument = emptyDocument();
		COSDictionary existingDssDictionary = new COSDictionary();
		pdDocument.getDocumentCatalog().getCOSObject().setItem("DSS", existingDssDictionary);
		
		ctrl.reset();
		
		CertificateVerificationData certificateVerificationData = ctrl.createMock(CertificateVerificationData.class);

		// chaincerts
		
		ArrayList<X509Certificate> chainCerts = Lists.newArrayList(ctrl.createMock(X509Certificate.class));
		expect(certificateVerificationData.getChainCerts()).andReturn(chainCerts);

		cut.addDSSCerts(pdDocument, existingDssDictionary, chainCerts);
		
		// ocsp responses
		
		expect(certificateVerificationData.getEncodedOCSPResponses()).andReturn(Collections.emptyList()).atLeastOnce();
		// expect that cut.addDSSOCSPs(...) not being invoked
		
		// clrs
		expect(certificateVerificationData.getCRLs()).andReturn(Collections.emptyList()).atLeastOnce();
		// expect cut.addDSSCRLs(...) not being invoked
		
		ctrl.replay();
		
		cut.addOrUpdateDSS(pdDocument, certificateVerificationData);
		
		ctrl.verify();
		
		// expect that existing dss dictionary has been used
		COSDictionary dssDictionaryFromDocument = (COSDictionary) pdDocument.getDocumentCatalog().getCOSObject().getDictionaryObject("DSS");
		assertNotNull(dssDictionaryFromDocument);
		
		assertSame(existingDssDictionary, dssDictionaryFromDocument);
		
		// expect that path from dss up to the root is dirty
		assertTrue(existingDssDictionary.isNeedToBeUpdated());
		assertTrue(pdDocument.getDocumentCatalog().getCOSObject().isNeedToBeUpdated());
		
	}

	@Nonnull
	private PDDocument emptyDocument() {
		PDDocument pdDocument = new PDDocument();
		// You need to add at least one page for the document to be valid.
		PDPage page = new PDPage();
		pdDocument.addPage(page);
		return pdDocument;
	}
	
}
