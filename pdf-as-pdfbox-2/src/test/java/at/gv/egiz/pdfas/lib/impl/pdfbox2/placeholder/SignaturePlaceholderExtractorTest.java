package at.gv.egiz.pdfas.lib.impl.pdfbox2.placeholder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.Test;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.lib.impl.placeholder.SignaturePlaceholderData;
import at.knowcenter.wag.egov.egiz.pdf.TablePos;

/**
 * Tests extraction of multiple placeholders from a document.
 * @author tknall
 */
public class SignaturePlaceholderExtractorTest {
	
	/**
	 * Assures that {@link SignaturePlaceholderData} implements {@link Serializable}.
	 */
	@Test
	public void testSignaturePlaceHolderDataSerializable() {
		assertTrue(new SignaturePlaceholderData(null, null, null, null) instanceof Serializable);
	}

	/**
	 * Tests extraction of all placeholders of a certain file. The candidate file contains five placeholders with identifier
	 * "PDF-AS-POS" and two non-placeholder images, for false-positive testing purposes. The tests expects five placeholders
	 * with certain positions and names.
	 * 
	 * @throws IOException    Thrown in case of I/O issue.
	 * @throws PdfAsException Thrown in case of pdf as specific issue.
	 */
	@Test
	public void testExtractAllPlaceholders() throws IOException, PdfAsException {
		final String CANDIDATE_RESOURCE_URI = "blindtext_mit_fuenf_platzhaltern_und_2_bildern.pdf";

		PDDocument doc = null;
		try (InputStream in = getClass().getResourceAsStream(CANDIDATE_RESOURCE_URI)) {

			doc = PDDocument.load(in);
			List<SignaturePlaceholderData> placeholders = SignaturePlaceholderExtractor.extract(doc);

			assertNotNull(placeholders);
			assertEquals(5, placeholders.size());
			
			for (SignaturePlaceholderData placeholder : placeholders) {
				TablePos t = placeholder.getTablePos();
				System.out.println("Found placeholder:  p:" + t.getPage() + ";x:" + t.getPosX() + ";y:" + t.getPosY()
				+ ";w:" + t.getWidth() + ";h:" + t.getHeight() + ", based on image '" + placeholder.getPlaceholderName() + "'.");
			}

			assertEquals("Image7", placeholders.get(0).getPlaceholderName());
			assertEquals(0, CompareToBuilder.reflectionCompare(new TablePos("p:1;x:70.8;y:513.95996;w:452.76").setHeight(155.16f), placeholders.get(0).getTablePos()));

			assertEquals("Image13", placeholders.get(1).getPlaceholderName());
			assertEquals(0, CompareToBuilder.reflectionCompare(new TablePos("p:3;x:70.8;y:287.76;w:452.76").setHeight(155.16f), placeholders.get(1).getTablePos()));

			assertEquals("Image16", placeholders.get(2).getPlaceholderName());
			assertEquals(0, CompareToBuilder.reflectionCompare(new TablePos("p:4;x:70.8;y:272.4;w:452.76").setHeight(155.28f), placeholders.get(2).getTablePos()));

			assertEquals("Image19", placeholders.get(3).getPlaceholderName());
			assertEquals(0, CompareToBuilder.reflectionCompare(new TablePos("p:5;x:70.8;y:544.92004;w:452.76").setHeight(155.28f), placeholders.get(3).getTablePos()));

			assertEquals("Image20", placeholders.get(4).getPlaceholderName());
			assertEquals(0, CompareToBuilder.reflectionCompare(new TablePos("p:5;x:70.8;y:305.16;w:453.84").setHeight(123.84f), placeholders.get(4).getTablePos()));

		} finally {
			if (doc != null) {
				doc.close();
			}
		}

	}
}
