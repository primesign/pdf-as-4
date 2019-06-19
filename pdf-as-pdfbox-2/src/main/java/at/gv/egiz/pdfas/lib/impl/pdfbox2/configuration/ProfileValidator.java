package at.gv.egiz.pdfas.lib.impl.pdfbox2.configuration;

import iaik.asn1.ObjectID;
import iaik.asn1.structures.Name;
import iaik.x509.X509Certificate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.PdfAsSettingsValidationException;
import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.common.settings.SignatureProfileSettings;
import at.gv.egiz.pdfas.lib.api.ByteArrayDataSource;
import at.gv.egiz.pdfas.lib.configuration.ConfigurationValidator;
import at.gv.egiz.pdfas.lib.impl.pdfbox2.PDFBOXObject;
import at.gv.egiz.pdfas.lib.impl.stamping.TableFactory;
import at.gv.egiz.pdfas.lib.impl.stamping.pdfbox2.PDFBoxTable;
import at.gv.egiz.pdfas.lib.impl.status.ICertificateProvider;
import at.gv.egiz.pdfas.lib.impl.status.OperationStatus;
import at.knowcenter.wag.egov.egiz.table.Table;

public class ProfileValidator implements ConfigurationValidator{

	private static final String NAME = "PDFBOX_2_PROFILE_VALIDATOR";

	private static final Logger logger = LoggerFactory
			.getLogger(ProfileValidator.class);

	@Override
	public void validate(ISettings settings) throws PdfAsSettingsValidationException{
		Set<String> profileIds = new HashSet<String>();

		Iterator<String> itKeys = settings.getFirstLevelKeys("sig_obj.types.")
				.iterator();
		while (itKeys.hasNext()) {
			String key = itKeys.next();
			String profile = key.substring("sig_obj.types.".length());

			if (settings.getValue(key).equals("on")) {
				profileIds.add(profile);
			}
		}
		logger.debug("Validating "+profileIds.size()+ " Profiles.");

		ArrayList<SignatureProfileSettings> profileSettings = new ArrayList<SignatureProfileSettings>();

		OperationStatus opState = new OperationStatus(settings, null, null);
		
		X509Certificate dummyCert = new X509Certificate();
		dummyCert.setSerialNumber(new BigInteger("123"));
		Name n = new Name();
		n.addRDN(ObjectID.country, "AT");
		n.addRDN(ObjectID.locality, "Graz");
		n.addRDN(ObjectID.organization ,"test");
		n.addRDN(ObjectID.organizationalUnit ,"test");
		n.addRDN(ObjectID.commonName ,"testca");
		dummyCert.setIssuerDN(n);
		dummyCert.setSubjectDN(n);

		ICertificateProvider certProvider = new DummyCertificateProvider(dummyCert);

		PDFBOXObject pdfBoxObject = new PDFBOXObject(opState);
		PDDocument origDoc = new PDDocument();
		origDoc.addPage(new PDPage(PDRectangle.A4));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {

			origDoc.save(baos);
			baos.close();
			origDoc.close();

			pdfBoxObject.setOriginalDocument(new ByteArrayDataSource(baos.toByteArray()));
		} catch (IOException e1) {
			logger.info("Configuration Validation failed!");
			throw new PdfAsSettingsValidationException("Configuration Validationfailed!", e1);
		}


		for(String id:profileIds){
			SignatureProfileSettings profileSetting = new SignatureProfileSettings(id, settings);
			profileSettings.add(profileSetting);
			if(profileSetting.getValue("isvisible")!=null){
				if(profileSetting.getValue("isvisible").equals("false")){
					continue;
				}
			}
			Table t;
			try {
				t = TableFactory.createSigTable(profileSetting, "main", opState, certProvider);
				new PDFBoxTable(t, null, settings, pdfBoxObject);
			} catch (Exception e) {
				logger.info("Configuration Validation for profile "+id+" failed!");
				throw new PdfAsSettingsValidationException("Configuration Validation for profile "+id+" failed!", e);
			}
		}
	}

	@Override
	public boolean usedAsDefault() {
		return true;
	}

	@Override
	public String getName() {
		return NAME;
	}
	
	private class DummyCertificateProvider implements ICertificateProvider {
		private X509Certificate cert;		
		public DummyCertificateProvider(X509Certificate cert) {
			this.cert = cert;
		}
		public X509Certificate getCertificate() {
			return cert;
		}

	}

}
