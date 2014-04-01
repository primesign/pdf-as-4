/*******************************************************************************
 * <copyright> Copyright 2014 by E-Government Innovation Center EGIZ, Graz, Austria </copyright>
 * PDF-AS has been contracted by the E-Government Innovation Center EGIZ, a
 * joint initiative of the Federal Chancellery Austria and Graz University of
 * Technology.
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * http://www.osor.eu/eupl/
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * 
 * This product combines work with different licenses. See the "NOTICE" text
 * file for details on the various modules and licenses.
 * The "NOTICE" text file is part of the distribution. Any derivative works
 * that you distribute must include a readable copy of the "NOTICE" text file.
 ******************************************************************************/
package at.gv.egiz.pdfas.lib.impl.signing.pdfbox;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.exceptions.SignatureException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageNode;
import org.apache.pdfbox.pdmodel.graphics.color.PDOutputIntent;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDJpeg;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.messages.MessageResolver;
import at.gv.egiz.pdfas.common.settings.SignatureProfileSettings;
import at.gv.egiz.pdfas.common.utils.StreamUtils;
import at.gv.egiz.pdfas.common.utils.TempFileHelper;
import at.gv.egiz.pdfas.lib.api.IConfigurationConstants;
import at.gv.egiz.pdfas.lib.impl.SignaturePositionImpl;
import at.gv.egiz.pdfas.lib.impl.configuration.SignatureProfileConfiguration;
import at.gv.egiz.pdfas.lib.impl.placeholder.PlaceholderFilter;
import at.gv.egiz.pdfas.lib.impl.placeholder.SignaturePlaceholderData;
import at.gv.egiz.pdfas.lib.impl.positioning.Positioning;
import at.gv.egiz.pdfas.lib.impl.signing.IPdfSigner;
import at.gv.egiz.pdfas.lib.impl.signing.sig_interface.PDFASSignatureInterface;
import at.gv.egiz.pdfas.lib.impl.stamping.IPDFStamper;
import at.gv.egiz.pdfas.lib.impl.stamping.IPDFVisualObject;
import at.gv.egiz.pdfas.lib.impl.stamping.StamperFactory;
import at.gv.egiz.pdfas.lib.impl.stamping.TableFactory;
import at.gv.egiz.pdfas.lib.impl.stamping.ValueResolver;
import at.gv.egiz.pdfas.lib.impl.stamping.pdfbox.PDFAsVisualSignatureProperties;
import at.gv.egiz.pdfas.lib.impl.stamping.pdfbox.PdfBoxVisualObject;
import at.gv.egiz.pdfas.lib.impl.status.PDFObject;
import at.gv.egiz.pdfas.lib.impl.status.RequestedSignature;
import at.knowcenter.wag.egov.egiz.pdf.PositioningInstruction;
import at.knowcenter.wag.egov.egiz.pdf.TablePos;
import at.knowcenter.wag.egov.egiz.table.Table;

public class PADESPDFBOXSigner implements IPdfSigner, IConfigurationConstants {

	private static final Logger logger = LoggerFactory
			.getLogger(PADESPDFBOXSigner.class);

	public void signPDF(PDFObject pdfObject,
			RequestedSignature requestedSignature,
			PDFASSignatureInterface signer) throws PdfAsException {
		String fisTmpFile = null;

		TempFileHelper helper = pdfObject.getStatus().getTempFileHelper();

		try {
			fisTmpFile = helper.getStaticFilename();

			// write to temporary file
			FileOutputStream fos = new FileOutputStream(new File(fisTmpFile));
			fos.write(pdfObject.getOriginalDocument());

			FileInputStream fis = new FileInputStream(new File(fisTmpFile));

			PDDocument doc = PDDocument.load(new ByteArrayInputStream(pdfObject
					.getOriginalDocument()));

			PDSignature signature = new PDSignature();
			signature.setFilter(COSName.getPDFName(signer.getPDFFilter())); // default
																			// filter
			signature
					.setSubFilter(COSName.getPDFName(signer.getPDFSubFilter()));

			SignatureProfileSettings signatureProfileSettings = TableFactory
					.createProfile(requestedSignature.getSignatureProfileID(),
							pdfObject.getStatus().getSettings());

			ValueResolver resolver = new ValueResolver();
			String signerName = resolver.resolve("SIG_SUBJECT",
					signatureProfileSettings.getValue("SIG_SUBJECT"),
					signatureProfileSettings, requestedSignature);

			signature.setName(signerName);
			signature.setSignDate(Calendar.getInstance());
			String signerReason = signatureProfileSettings.getSigningReason();

			if (signerReason == null) {
				signerReason = "PAdES Signature";
			}

			signature.setReason(signerReason);
			logger.debug("Signing reason: " + signerReason);

			logger.debug("Signing @ "
					+ signer.getSigningDate().getTime().toString());
			// the signing date, needed for valid signature
			// signature.setSignDate(signer.getSigningDate());

			signer.setPDSignature(signature);
			SignatureOptions options = new SignatureOptions();

			// Is visible Signature
			if (requestedSignature.isVisual()) {
				logger.info("Creating visual siganture block");

				SignatureProfileConfiguration signatureProfileConfiguration = pdfObject
						.getStatus().getSignatureProfileConfiguration(
								requestedSignature.getSignatureProfileID());

				SignaturePlaceholderData signaturePlaceholderData = PlaceholderFilter
						.checkPlaceholderSignature(pdfObject.getStatus(),
								pdfObject.getStatus().getSettings());

				TablePos tablePos = null;

				if (signaturePlaceholderData != null) {
					// Placeholder found!

					if (signaturePlaceholderData.getProfile() != null) {
						requestedSignature
								.setSignatureProfileID(signaturePlaceholderData
										.getProfile());
					}

					tablePos = signaturePlaceholderData.getTablePos();
				}

				if (tablePos == null) {
					// ================================================================
					// PositioningStage (visual) -> find position or use fixed
					// position

					String posString = pdfObject.getStatus().getSignParamter()
							.getSignaturePosition();

					if (posString == null) {
						posString = signatureProfileConfiguration
								.getDefaultPositioning();
					}

					logger.debug("using Positioning: " + posString);

					if (posString == null) {
						tablePos = new TablePos();
					} else {
						tablePos = new TablePos(posString);
					}
				}
				boolean legacy32Position = signatureProfileConfiguration
						.getLegacy32Positioning();

				// create Table describtion
				Table main = TableFactory.createSigTable(
						signatureProfileSettings, MAIN, pdfObject.getStatus()
								.getSettings(), requestedSignature);

				IPDFStamper stamper = StamperFactory
						.createDefaultStamper(pdfObject.getStatus()
								.getSettings());

				IPDFVisualObject visualObject = stamper.createVisualPDFObject(
						pdfObject, main);

				PDDocument originalDocument = PDDocument
						.load(new ByteArrayInputStream(pdfObject.getStatus()
								.getPdfObject().getOriginalDocument()));

				PositioningInstruction positioningInstruction = Positioning
						.determineTablePositioning(tablePos, "",
								originalDocument, visualObject,
								legacy32Position);

				SignaturePositionImpl position = new SignaturePositionImpl();
				position.setX(positioningInstruction.getX());
				position.setY(positioningInstruction.getY());
				position.setPage(positioningInstruction.getPage());
				position.setHeight(visualObject.getHeight());
				position.setWidth(visualObject.getWidth());

				requestedSignature.setSignaturePosition(position);

				PDFAsVisualSignatureProperties properties = new PDFAsVisualSignatureProperties(
						pdfObject.getStatus().getSettings(), pdfObject,
						(PdfBoxVisualObject) visualObject,
						positioningInstruction);

				properties.buildSignature();

				ByteArrayOutputStream sigbos = new ByteArrayOutputStream();
				sigbos.write(StreamUtils.inputStreamToByteArray(properties
						.getVisibleSignature()));
				sigbos.close();

				FileOutputStream fos2 = new FileOutputStream("/tmp/apsig.pdf");
				fos2.write(sigbos.toByteArray());
				fos2.close();

				if (signaturePlaceholderData != null) {
					// Placeholder found!
					// replace placeholder
					InputStream is = PADESPDFBOXSigner.class
							.getResourceAsStream("/placeholder/empty.jpg");
					PDJpeg img = new PDJpeg(doc, is);
					img.getCOSObject().setNeedToBeUpdate(true);

					PDDocumentCatalog root = doc.getDocumentCatalog();
					PDPageNode rootPages = root.getPages();
					List<PDPage> kids = new ArrayList<PDPage>();
					rootPages.getAllKids(kids);
					int pageNumber = positioningInstruction.getPage();
					rootPages.getAllKids(kids);
					PDPage page = kids.get(pageNumber);
					
					logger.info("Placeholder name: " + signaturePlaceholderData.getPlaceholderName());
					COSDictionary xobjectsDictionary = (COSDictionary) page.findResources().getCOSDictionary()
							.getDictionaryObject(COSName.XOBJECT);
					xobjectsDictionary.setItem(signaturePlaceholderData.getPlaceholderName(), img);
					xobjectsDictionary.setNeedToBeUpdate(true);
					page.findResources().getCOSObject().setNeedToBeUpdate(true);
					logger.info("Placeholder name: " + signaturePlaceholderData.getPlaceholderName());
				}

				if (positioningInstruction.isMakeNewPage()) {
					int last = doc.getNumberOfPages() - 1;
					PDDocumentCatalog root = doc.getDocumentCatalog();
					PDPageNode rootPages = root.getPages();
					List<PDPage> kids = new ArrayList<PDPage>();
					rootPages.getAllKids(kids);
					PDPage lastPage = kids.get(last);
					rootPages.getCOSObject().setNeedToBeUpdate(true);
					PDPage p = new PDPage(lastPage.findMediaBox());

					doc.addPage(p);
				}

				if (signatureProfileSettings.isPDFA()) {
					PDDocumentCatalog root = doc.getDocumentCatalog();
					InputStream colorProfile = PDDocumentCatalog.class
							.getResourceAsStream("/icm/sRGB Color Space Profile.icm");
					try {
						PDOutputIntent oi = new PDOutputIntent(doc,
								colorProfile);
						oi.setInfo("sRGB IEC61966-2.1");
						oi.setOutputCondition("sRGB IEC61966-2.1");
						oi.setOutputConditionIdentifier("sRGB IEC61966-2.1");
						oi.setRegistryName("http://www.color.org");

						root.addOutputIntent(oi);
						root.getCOSObject().setNeedToBeUpdate(true);
						logger.info("added Output Intent");
					} catch (Throwable e) {
						e.printStackTrace();
						throw new PdfAsException("Failed to add Output Intent",
								e);
					}
				}

				options.setPreferedSignatureSize(0x1000);
				options.setPage(positioningInstruction.getPage());
				options.setVisualSignature(new ByteArrayInputStream(sigbos
						.toByteArray()));
			}

			doc.addSignature(signature, signer, options);

			// pdfbox patched (FIS -> IS)
			doc.saveIncremental(fis, fos);
			fis.close();
			fos.close();

			fis = new FileInputStream(new File(fisTmpFile));

			// write to resulting output stream
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bos.write(StreamUtils.inputStreamToByteArray(fis));
			fis.close();
			bos.close();

			pdfObject.setSignedDocument(bos.toByteArray());

			helper.deleteFile(fisTmpFile);

		} catch (IOException e) {
			logger.error(MessageResolver.resolveMessage("error.pdf.sig.01"), e);
			throw new PdfAsException("error.pdf.sig.01", e);
		} catch (SignatureException e) {
			logger.error(MessageResolver.resolveMessage("error.pdf.sig.01"), e);
			throw new PdfAsException("error.pdf.sig.01", e);
		} catch (COSVisitorException e) {
			logger.error(MessageResolver.resolveMessage("error.pdf.sig.01"), e);
			throw new PdfAsException("error.pdf.sig.01", e);
		}
	}
}
