package at.gv.egiz.pdfas.lib.impl.stamping;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import at.gv.egiz.pdfas.common.exceptions.PdfAsSettingsException;
import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.common.settings.SignatureProfileSettings;
import at.gv.egiz.pdfas.lib.impl.status.OperationStatus;
import at.knowcenter.wag.egov.egiz.table.Entry;
import at.knowcenter.wag.egov.egiz.table.Table;
import iaik.asn1.ObjectID;
import iaik.asn1.structures.Name;
import iaik.x509.X509Certificate;

/**
 * Testing creation of signature block table together with transliteration of values using ICU4J.
 *
 * @author Thomas Knall, PrimeSign GmbH
 *
 */
public class TableFactoryTest {

	private class TestSettings implements ISettings {

		private final Map<String, String> data;

		/**
		 * Simulates PDF-AS Properties using simple map.
		 *
		 * @param data The configuration map (required; must not be {@code null} but may be empty).
		 */
		public TestSettings(Map<String, String> data) {
			// copy values
			this.data = new HashMap<>(Objects.requireNonNull(data));
		}

		@Override
		public String getValue(String key) {
			return data.get(key);
		}

		@Override
		public boolean hasValue(String key) {
			return data.containsKey(key);
		}

		@Override
		public boolean hasPrefix(String prefix) {
			return data.keySet().stream().anyMatch(s -> s.startsWith(prefix));
		}

		@Override
		public Map<String, String> getValuesPrefix(String prefix) {
			return data.entrySet().stream()
				.filter(e -> e.getKey().startsWith(prefix))                      // select only elements starting with prefix
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));  // create new map
		}

		@Override
		public Vector<String> getFirstLevelKeys(String prefix) {
			// make sure prefix ends with "."
			String prefixWithPeriod = StringUtils.appendIfMissing(Objects.requireNonNull(prefix), ".", ".");
			return data.keySet().stream()
				.filter(k -> k.startsWith(prefixWithPeriod)) // select only elements starting with prefixWithPeriod
				// given "prefix.firstlevel.secondlevel.thirdlevel"
				// -> #1 first take remaining key after prefixWithPeriod: "firstlevel.secondlevel.thirdlevel"
				// -> #2 then take string before first ".": "firstlevel" (when no "." follows, take the complete remaining string)
				// -> return #1 + #2: "prefix.firstlevel"
				.map(t -> prefixWithPeriod + StringUtils.substringBefore(StringUtils.substringAfter(t, prefixWithPeriod), "."))
				.distinct()  // avoid duplicates
				.collect(Collectors.toCollection(Vector::new));  // and return as Vector

		}

		@Override
		public String getWorkingDirectory() {
			return null;
		}

	}

	@Test
	public void testCreateSigTableWithoutCaptions_ICU4J() throws PdfAsSettingsException {

		// prepare minimal profile config setting a profile with two rows (single column)

		Map<String, String> config = new HashMap<>();

		// the first row contains the common name of the signer certificate's subject dn
		config.put("sig_obj.PROFILE.key.SIG_SUBJECT" ,"Signer");  // required despite not using captions (???)
		config.put("sig_obj.PROFILE.table.main.1" ,"SIG_SUBJECT-v");
		config.put("sig_obj.PROFILE.value.SIG_SUBJECT" ,"${subject.CN}");

		// the second row contains a note (provided with both lower and upper case letters)
		config.put("sig_obj.PROFILE.key.SIG_META" ,"Note"); // required despite not using captions (???)
		config.put("sig_obj.PROFILE.table.main.2" ,"SIG_META-v");
		config.put("sig_obj.PROFILE.value.SIG_META", "A Signature Note");

		// Define a transliteration for ALL values within the signature block (just transforming all letters to LOWER case)
		config.put("sig_obj.PROFILE.transformPattern" ,"Any-Lower");

		// Define a specific transliteration for the signer's name (transforming letters to UPPER case)
		config.put("sig_obj.PROFILE.transformPattern-SIG_SUBJECT" ,"Any-Upper");

		TestSettings settings = new TestSettings(config);
		SignatureProfileSettings profileSettings = new SignatureProfileSettings("PROFILE", settings);
		OperationStatus operationStatus = new OperationStatus(settings, null, null);

		// we need to create a minimal certificate for the certificate provider / value resolver
		X509Certificate testCert = new X509Certificate();
		testCert.setSerialNumber(new BigInteger("1234567890"));
		Name n = new Name();
		n.addRDN(ObjectID.country, "AT");
		n.addRDN(ObjectID.commonName ,"A Common Name"); // using both lower case and upper case letters
		testCert.setIssuerDN(n);
		testCert.setSubjectDN(n);

		// let pdf-as create the table model
		Table sigTable = TableFactory.createSigTable(profileSettings, "main", operationStatus, () -> testCert);

		ArrayList<ArrayList<Entry>> rows = sigTable.getRows();
		// expecting two rows (signer and note)
		assertThat(rows.size(), is(2));

		// signer
		ArrayList<Entry> signerRow = rows.get(0);
		// expect one column only (since no caption, but value)
		assertThat(signerRow.size(), is(1));
		// expect SIG_SUBJECT (from certificate cn) to be transformed to UPPER case
		assertThat(signerRow.get(0).getValue(), is("A COMMON NAME"));

		// note
		ArrayList<Entry> noteRow = rows.get(1);
		// expect one column only (since no caption, but value)
		assertThat(noteRow.size(), is(1));
		// expect SIG_NOTE (static text from configuration) to be transformed to LOWER case
		assertThat(noteRow.get(0).getValue(), is("a signature note"));

	}

	@Test
	public void testCreateSigTableWithCaptions_ICU4J() throws PdfAsSettingsException {

		// prepare minimal profile config setting a profile with two rows and two columns

		Map<String, String> config = new HashMap<>();

		// the first row contains the columns "Signer" and the common name of the signer certificate's subject dn
		config.put("sig_obj.PROFILE.key.SIG_SUBJECT" ,"Signer");
		config.put("sig_obj.PROFILE.table.main.1" ,"SIG_SUBJECT-cv");
		config.put("sig_obj.PROFILE.value.SIG_SUBJECT" ,"${subject.CN}");

		// the second row contains the columns "Note" and note (provided with both lower and upper case letters)
		config.put("sig_obj.PROFILE.key.SIG_META" ,"Note"); // required despite not using captions (???)
		config.put("sig_obj.PROFILE.table.main.2" ,"SIG_META-cv");
		config.put("sig_obj.PROFILE.value.SIG_META", "A Signature Note");

		// Define a transliteration for ALL values within the signature block (just transforming all letters to LOWER case)
		config.put("sig_obj.PROFILE.transformPattern" ,"Any-Lower");

		// Define a specific transliteration for the signer's name (transforming letters to UPPER case)
		config.put("sig_obj.PROFILE.transformPattern-SIG_SUBJECT" ,"Any-Upper");

		TestSettings settings = new TestSettings(config);
		SignatureProfileSettings profileSettings = new SignatureProfileSettings("PROFILE", settings);
		OperationStatus operationStatus = new OperationStatus(settings, null, null);

		// we need to create a minimal certificate for the certificate provider / value resolver
		X509Certificate testCert = new X509Certificate();
		testCert.setSerialNumber(new BigInteger("1234567890"));
		Name n = new Name();
		n.addRDN(ObjectID.country, "AT");
		n.addRDN(ObjectID.commonName ,"A Common Name"); // using both lower case and upper case letters
		testCert.setIssuerDN(n);
		testCert.setSubjectDN(n);

		// let pdf-as create the table model
		Table sigTable = TableFactory.createSigTable(profileSettings, "main", operationStatus, () -> testCert);

		ArrayList<ArrayList<Entry>> rows = sigTable.getRows();
		// expecting two rows (signer and note)
		assertThat(rows.size(), is(2));

		// signer
		ArrayList<Entry> signerRow = rows.get(0);
		// expect two columns (caption, value)
		assertThat(signerRow.size(), is(2));
		// expect unmodified caption
		assertThat(signerRow.get(0).getValue(), is("Signer"));
		// expect SIG_SUBJECT (from certificate cn) to be transformed to UPPER case
		assertThat(signerRow.get(1).getValue(), is("A COMMON NAME"));

		// note
		ArrayList<Entry> noteRow = rows.get(1);
		// expect two columns (caption, value)
		assertThat(noteRow.size(), is(2));
		// expect unmodified caption
		assertThat(noteRow.get(0).getValue(), is("Note"));
		// expect SIG_NOTE (static text from configuration) to be transformed to LOWER case
		assertThat(noteRow.get(1).getValue(), is("a signature note"));

	}

}
