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

import iaik.x509.X509Certificate;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.exceptions.SignatureException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageNode;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.color.PDOutputIntent;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDJpeg;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureOptions;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDSignatureField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.PDFASError;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.messages.MessageResolver;
import at.gv.egiz.pdfas.common.settings.SignatureProfileSettings;
import at.gv.egiz.pdfas.common.utils.StreamUtils;
import at.gv.egiz.pdfas.common.utils.TempFileHelper;
import at.gv.egiz.pdfas.lib.api.ByteArrayDataSource;
import at.gv.egiz.pdfas.lib.api.IConfigurationConstants;
import at.gv.egiz.pdfas.lib.api.sign.IPlainSigner;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;
import at.gv.egiz.pdfas.lib.impl.ErrorExtractor;
import at.gv.egiz.pdfas.lib.impl.SignaturePositionImpl;
import at.gv.egiz.pdfas.lib.impl.configuration.SignatureProfileConfiguration;
import at.gv.egiz.pdfas.lib.impl.pdfbox.PDFBOXObject;
import at.gv.egiz.pdfas.lib.impl.pdfbox.positioning.Positioning;
import at.gv.egiz.pdfas.lib.impl.pdfbox.utils.PdfBoxUtils;
import at.gv.egiz.pdfas.lib.impl.placeholder.PlaceholderFilter;
import at.gv.egiz.pdfas.lib.impl.placeholder.SignaturePlaceholderData;
import at.gv.egiz.pdfas.lib.impl.signing.IPdfSigner;
import at.gv.egiz.pdfas.lib.impl.signing.PDFASSignatureExtractor;
import at.gv.egiz.pdfas.lib.impl.signing.PDFASSignatureInterface;
import at.gv.egiz.pdfas.lib.impl.stamping.IPDFStamper;
import at.gv.egiz.pdfas.lib.impl.stamping.IPDFVisualObject;
import at.gv.egiz.pdfas.lib.impl.stamping.StamperFactory;
import at.gv.egiz.pdfas.lib.impl.stamping.TableFactory;
import at.gv.egiz.pdfas.lib.impl.stamping.ValueResolver;
import at.gv.egiz.pdfas.lib.impl.stamping.pdfbox.PDFAsVisualSignatureProperties;
import at.gv.egiz.pdfas.lib.impl.stamping.pdfbox.PdfBoxVisualObject;
import at.gv.egiz.pdfas.lib.impl.status.OperationStatus;
import at.gv.egiz.pdfas.lib.impl.status.PDFObject;
import at.gv.egiz.pdfas.lib.impl.status.RequestedSignature;
import at.knowcenter.wag.egov.egiz.pdf.PositioningInstruction;
import at.knowcenter.wag.egov.egiz.pdf.TablePos;
import at.knowcenter.wag.egov.egiz.table.Table;

public class PADESPDFBOXSigner implements IPdfSigner, IConfigurationConstants {

	private static final Logger logger = LoggerFactory
			.getLogger(PADESPDFBOXSigner.class);

	private static final float PDF_VERSION_1_6 = 1.6f;

	public void signPDF(PDFObject genericPdfObject,
			RequestedSignature requestedSignature,
			PDFASSignatureInterface genericSigner) throws PdfAsException {
		String fisTmpFile = null;

		int signatureCertificationLevel = requestedSignature
				.getSignatureCertificationLevel();

		if (!(genericPdfObject instanceof PDFBOXObject)) {
			// tODO:
			throw new PdfAsException();
		}

		PDFBOXObject pdfObject = (PDFBOXObject) genericPdfObject;

		if (!(genericSigner instanceof PDFASPDFBOXSignatureInterface)) {
			// tODO:
			throw new PdfAsException();
		}

		PDFASPDFBOXSignatureInterface signer = (PDFASPDFBOXSignatureInterface) genericSigner;

		TempFileHelper helper = pdfObject.getStatus().getTempFileHelper();
		PDDocument doc = null;
		try {
			fisTmpFile = helper.getStaticFilename();

			FileOutputStream tmpOutputStream = null;
			try {
				// write to temporary file
				tmpOutputStream = new FileOutputStream(new File(fisTmpFile));
				InputStream tmpis = null;
				try {
					tmpis = pdfObject.getOriginalDocument().getInputStream();
					IOUtils.copy(tmpis, tmpOutputStream);
					tmpis.close();
				} finally {
					IOUtils.closeQuietly(tmpis);
				}

				doc = pdfObject.getDocument();

				SignaturePlaceholderData signaturePlaceholderData = PlaceholderFilter
						.checkPlaceholderSignature(pdfObject.getStatus(),
								pdfObject.getStatus().getSettings());

				TablePos tablePos = null;

				if (signaturePlaceholderData != null) {
					// Placeholder found!
					logger.info("Placeholder data found.");
					if (signaturePlaceholderData.getProfile() != null) {
						logger.debug("Placeholder Profile set to: "
								+ signaturePlaceholderData.getProfile());
						requestedSignature
								.setSignatureProfileID(signaturePlaceholderData
										.getProfile());
					}

					tablePos = signaturePlaceholderData.getTablePos();
					if (tablePos != null) {
						logger.debug("Placeholder Position set to: "
								+ tablePos.toString());
					}
				}

				PDSignature signature = new PDSignature();
				signature.setFilter(COSName.getPDFName(signer.getPDFFilter())); // default
																				// filter
				signature.setSubFilter(COSName.getPDFName(signer
						.getPDFSubFilter()));

				SignatureProfileSettings signatureProfileSettings = TableFactory
						.createProfile(
								requestedSignature.getSignatureProfileID(),
								pdfObject.getStatus().getSettings());

				ValueResolver resolver = new ValueResolver(requestedSignature,
						pdfObject.getStatus());
				String signerName = resolver.resolve("SIG_SUBJECT",
						signatureProfileSettings.getValue("SIG_SUBJECT"),
						signatureProfileSettings);

				signature.setName(signerName);
				signature.setSignDate(Calendar.getInstance());
				String signerReason = signatureProfileSettings
						.getSigningReason();

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
				options.setPreferedSignatureSize(0x1000);

				// Is visible Signature
				if (requestedSignature.isVisual()) {
					logger.info("Creating visual siganture block");

					SignatureProfileConfiguration signatureProfileConfiguration = pdfObject
							.getStatus().getSignatureProfileConfiguration(
									requestedSignature.getSignatureProfileID());

					if (tablePos == null) {
						// ================================================================
						// PositioningStage (visual) -> find position or use
						// fixed
						// position

						String posString = pdfObject.getStatus()
								.getSignParamter().getSignaturePosition();

						TablePos signaturePos = null;

						String signaturePosString = signatureProfileConfiguration
								.getDefaultPositioning();

						if (signaturePosString != null) {
							logger.debug("using signature Positioning: "
									+ signaturePos);
							signaturePos = new TablePos(signaturePosString);
						}

						logger.debug("using Positioning: " + posString);

						if (posString != null) {
							// Merge Signature Position
							tablePos = new TablePos(posString, signaturePos);
						} else {
							// Fallback to signature Position!
							tablePos = signaturePos;
						}

						if (tablePos == null) {
							// Last Fallback default position
							tablePos = new TablePos();
						}
					}
					boolean legacy32Position = signatureProfileConfiguration
							.getLegacy32Positioning();

					// create Table describtion
					Table main = TableFactory.createSigTable(
							signatureProfileSettings, MAIN,
							pdfObject.getStatus(), requestedSignature);

					IPDFStamper stamper = StamperFactory
							.createDefaultStamper(pdfObject.getStatus()
									.getSettings());

					IPDFVisualObject visualObject = stamper
							.createVisualPDFObject(pdfObject, main);

					/*
					 * PDDocument originalDocument = PDDocument .load(new
					 * ByteArrayInputStream(pdfObject.getStatus()
					 * .getPdfObject().getOriginalDocument()));
					 */

					PositioningInstruction positioningInstruction = Positioning
							.determineTablePositioning(tablePos, "", doc,
									visualObject, legacy32Position);

					logger.debug("Positioning: {}",
							positioningInstruction.toString());

					if (positioningInstruction.isMakeNewPage()) {
						int last = doc.getNumberOfPages() - 1;
						PDDocumentCatalog root = doc.getDocumentCatalog();
						PDPageNode rootPages = root.getPages();
						List<PDPage> kids = new ArrayList<PDPage>();
						rootPages.getAllKids(kids);
						PDPage lastPage = kids.get(last);
						rootPages.getCOSObject().setNeedToBeUpdate(true);
						PDPage p = new PDPage(lastPage.findMediaBox());
						p.setResources(new PDResources());
						p.setRotation(lastPage.findRotation());
						doc.addPage(p);
					}

					// handle rotated page
					PDDocumentCatalog documentCatalog = doc
							.getDocumentCatalog();
					PDPageNode documentPages = documentCatalog.getPages();
					List<PDPage> documentPagesKids = new ArrayList<PDPage>();
					documentPages.getAllKids(documentPagesKids);
					int targetPageNumber = positioningInstruction.getPage();
					logger.debug("Target Page: " + targetPageNumber);
					// rootPages.getAllKids(kids);
					PDPage targetPage = documentPagesKids
							.get(targetPageNumber - 1);
					int rot = targetPage.findRotation();
					logger.debug("Page rotation: " + rot);
					// positioningInstruction.setRotation(positioningInstruction.getRotation()
					// + rot);
					logger.debug("resulting Sign rotation: "
							+ positioningInstruction.getRotation());

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

					/*
					 * ByteArrayOutputStream sigbos = new
					 * ByteArrayOutputStream();
					 * sigbos.write(StreamUtils.inputStreamToByteArray
					 * (properties .getVisibleSignature())); sigbos.close();
					 */

					if (signaturePlaceholderData != null) {
						// Placeholder found!
						// replace placeholder
						InputStream is = null;
						try {
							is = PADESPDFBOXSigner.class
									.getResourceAsStream("/placeholder/empty.jpg");
							PDJpeg img = new PDJpeg(doc, is);

							img.getCOSObject().setNeedToBeUpdate(true);

							PDDocumentCatalog root = doc.getDocumentCatalog();
							PDPageNode rootPages = root.getPages();
							List<PDPage> kids = new ArrayList<PDPage>();
							rootPages.getAllKids(kids);
							int pageNumber = positioningInstruction.getPage();
							// rootPages.getAllKids(kids);
							PDPage page = kids.get(pageNumber - 1);

							logger.info("Placeholder name: "
									+ signaturePlaceholderData
											.getPlaceholderName());
							COSDictionary xobjectsDictionary = (COSDictionary) page
									.findResources().getCOSDictionary()
									.getDictionaryObject(COSName.XOBJECT);
							xobjectsDictionary.setItem(signaturePlaceholderData
									.getPlaceholderName(), img);
							xobjectsDictionary.setNeedToBeUpdate(true);
							page.findResources().getCOSObject()
									.setNeedToBeUpdate(true);
							logger.info("Placeholder name: "
									+ signaturePlaceholderData
											.getPlaceholderName());
						} finally {
							IOUtils.closeQuietly(is);
						}
					}

					if (signatureProfileSettings.isPDFA()) {
						PDDocumentCatalog root = doc.getDocumentCatalog();
						COSBase base = root.getCOSDictionary().getItem(
								COSName.OUTPUT_INTENTS);
						if (base == null) {
							InputStream colorProfile = null;
							try {
								colorProfile = PDDocumentCatalog.class
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
									throw new PdfAsException(
											"Failed to add Output Intent", e);
								}
							} finally {
								IOUtils.closeQuietly(colorProfile);
							}
						}
					}

					// if (signatureProfileSettings.isPDFA()) { // Check for
					// PDF-UA
					// PDDocumentCatalog root = doc.getDocumentCatalog();
					// PDStructureTreeRoot treeRoot =
					// root.getStructureTreeRoot();
					// if (treeRoot != null) { // Handle as PDF-UA
					// logger.info("Tree Root: {}", treeRoot.toString());
					// PDStructureElement docElement = PDFBoxTaggingUtils
					// .getDocumentElement(treeRoot);
					// PDStructureElement sigBlock = new PDStructureElement(
					// "Table", docElement);
					// root.getCOSObject().setNeedToBeUpdate(true);
					// docElement.getCOSObject().setNeedToBeUpdate(true);
					// treeRoot.getCOSObject().setNeedToBeUpdate(true);
					// sigBlock.setTitle("Signature Table");
					// }
					// }

					options.setPage(positioningInstruction.getPage());
					options.setVisualSignature(properties.getVisibleSignature());
				}

				if (signatureCertificationLevel > 0) {

					COSDocument document = doc.getDocument();

					addDocMDP(signature.getDictionary(), document.getTrailer(),
							document.getVersion(), signatureCertificationLevel);

					// add DocMDP entry to root
					COSDictionary docmdp = new COSDictionary();
					docmdp.setItem(COSName.getPDFName("DocMDP"),
							signature.getDictionary());
					doc.getDocumentCatalog().getCOSDictionary()
							.setItem(COSName.getPDFName("Perms"), docmdp);
				}

				doc.addSignature(signature, signer, options);

				String sigFieldName = signatureProfileSettings
						.getSignFieldValue();

				if (sigFieldName == null) {
					sigFieldName = "PDF-AS Signatur";
				}

				int count = PdfBoxUtils.countSignatures(doc, sigFieldName);

				sigFieldName = sigFieldName + count;

				PDAcroForm acroFormm = doc.getDocumentCatalog().getAcroForm();
				if (acroFormm != null) {
					@SuppressWarnings("unchecked")
					List<PDField> fields = acroFormm.getFields();
					PDSignatureField signatureField = null;

					if (fields != null) {
						for (PDField pdField : fields) {
							if (pdField != null) {
								if (pdField instanceof PDSignatureField) {
									PDSignatureField tmpSigField = (PDSignatureField) pdField;
									if (tmpSigField.getSignature() != null
											&& tmpSigField.getSignature()
													.getDictionary() != null) {
										if (tmpSigField
												.getSignature()
												.getDictionary()
												.equals(signature
														.getDictionary())) {
											signatureField = (PDSignatureField) pdField;
										}
									}
								}
							}
						}
					} else {
						logger.warn("Failed to name Signature Field! [Cannot find Field list in acroForm!]");
					}

					if (signatureField != null) {
						signatureField.setPartialName(sigFieldName);
					}
				} else {
					logger.warn("Failed to name Signature Field! [Cannot find acroForm!]");
				}

				if (requestedSignature.isVisual()) {

					// if(requestedSignature.getSignaturePosition().)
					/*
					 * PDAcroForm acroForm =
					 * doc.getDocumentCatalog().getAcroForm(); if (acroForm !=
					 * null) {
					 * 
					 * @SuppressWarnings("unchecked") List<PDField> fields =
					 * acroForm.getFields(); PDSignatureField signatureField =
					 * null;
					 * 
					 * if (fields != null) { for (PDField pdField : fields) { if
					 * (pdField instanceof PDSignatureField) { if
					 * (((PDSignatureField) pdField).getSignature()
					 * .getDictionary() .equals(signature.getDictionary())) {
					 * signatureField = (PDSignatureField) pdField; } } } } else
					 * { logger.warn(
					 * "Failed to apply rotation! [Cannot find Field list in acroForm!]"
					 * ); }
					 * 
					 * if (signatureField != null) { if
					 * (signatureField.getWidget() != null) { if
					 * (signatureField.getWidget()
					 * .getAppearanceCharacteristics() == null) {
					 * PDAppearanceCharacteristicsDictionary dict = new
					 * PDAppearanceCharacteristicsDictionary( new
					 * COSDictionary()); signatureField.getWidget()
					 * .setAppearanceCharacteristics(dict); }
					 * 
					 * if (signatureField.getWidget()
					 * .getAppearanceCharacteristics() != null) {
					 * signatureField.getWidget()
					 * .getAppearanceCharacteristics() .setRotation(90); } } }
					 * else { logger.warn(
					 * "Failed to apply rotation! [Cannot find signature Field!]"
					 * ); } } else { logger.warn(
					 * "Failed to apply rotation! [Cannot find acroForm!]" ); }
					 */
				}

				FileInputStream tmpFileIs = null;

				try {
					tmpFileIs = new FileInputStream(new File(fisTmpFile));

					doc.saveIncremental(tmpFileIs, tmpOutputStream);
					tmpFileIs.close();
				} finally {
					IOUtils.closeQuietly(tmpFileIs);
				}
				tmpOutputStream.flush();
				tmpOutputStream.close();
			} finally {
				IOUtils.closeQuietly(tmpOutputStream);
			}

			FileInputStream readReadyFile = null;
			try {
				readReadyFile = new FileInputStream(new File(fisTmpFile));

				// write to resulting output stream
				// ByteArrayOutputStream bos = new ByteArrayOutputStream();
				// bos.write();
				// bos.close();

				pdfObject.setSignedDocument(StreamUtils
						.inputStreamToByteArray(readReadyFile));
				readReadyFile.close();
			} finally {
				IOUtils.closeQuietly(readReadyFile);
			}
			System.gc();
		} catch (IOException e) {
			logger.warn(MessageResolver.resolveMessage("error.pdf.sig.01"), e);
			throw new PdfAsException("error.pdf.sig.01", e);
		} catch (SignatureException e) {
			logger.warn(MessageResolver.resolveMessage("error.pdf.sig.01"), e);
			throw new PdfAsException("error.pdf.sig.01", e);
		} catch (COSVisitorException e) {
			logger.warn(MessageResolver.resolveMessage("error.pdf.sig.01"), e);
			throw new PdfAsException("error.pdf.sig.01", e);
		} finally {
			if (doc != null) {
				try {
					doc.close();
				} catch (IOException e) {
					logger.debug("Failed to close COS Doc!", e);
					// Ignore
				}
			}
			
			if(fisTmpFile != null) {
				helper.deleteFile(fisTmpFile);
			}
			logger.debug("Signature done!");

		}
	}

	@Override
	public PDFObject buildPDFObject(OperationStatus operationStatus) {
		return new PDFBOXObject(operationStatus);
	}

	@Override
	public PDFASSignatureInterface buildSignaturInterface(IPlainSigner signer,
			SignParameter parameters, RequestedSignature requestedSignature) {
		return new PdfboxSignerWrapper(signer, parameters, requestedSignature);
	}

	@Override
	public PDFASSignatureExtractor buildBlindSignaturInterface(
			X509Certificate certificate, String filter, String subfilter,
			Calendar date) {
		return new SignatureDataExtractor(certificate, filter, subfilter, date);
	}

	@Override
	public void checkPDFPermissions(PDFObject genericPdfObject)
			throws PdfAsException {
		if (!(genericPdfObject instanceof PDFBOXObject)) {
			// tODO:
			throw new PdfAsException();
		}

		PDFBOXObject pdfObject = (PDFBOXObject) genericPdfObject;
		PdfBoxUtils.checkPDFPermissions(pdfObject.getDocument());
	}

	@Override
	public byte[] rewritePlainSignature(byte[] plainSignature) {
		String signature = new COSString(plainSignature).getHexString();
		byte[] pdfSignature = signature.getBytes();
		return pdfSignature;
	}

	@Override
	public Image generateVisibleSignaturePreview(SignParameter parameter,
			java.security.cert.X509Certificate cert, int resolution,
			OperationStatus status, RequestedSignature requestedSignature)
			throws PDFASError {
		try {
			PDFBOXObject pdfObject = (PDFBOXObject) status.getPdfObject();

			PDDocument origDoc = new PDDocument();
			origDoc.addPage(new PDPage(PDPage.PAGE_SIZE_A4));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			origDoc.save(baos);
			baos.close();

			pdfObject.setOriginalDocument(new ByteArrayDataSource(baos
					.toByteArray()));

			SignatureProfileSettings signatureProfileSettings = TableFactory
					.createProfile(requestedSignature.getSignatureProfileID(),
							pdfObject.getStatus().getSettings());

			// create Table describtion
			Table main = TableFactory.createSigTable(signatureProfileSettings,
					MAIN, pdfObject.getStatus(), requestedSignature);

			IPDFStamper stamper = StamperFactory.createDefaultStamper(pdfObject
					.getStatus().getSettings());

			IPDFVisualObject visualObject = stamper.createVisualPDFObject(
					pdfObject, main);

			SignatureProfileConfiguration signatureProfileConfiguration = pdfObject
					.getStatus().getSignatureProfileConfiguration(
							requestedSignature.getSignatureProfileID());

			String signaturePosString = signatureProfileConfiguration
					.getDefaultPositioning();
			PositioningInstruction positioningInstruction = null;
			if (signaturePosString != null) {
				positioningInstruction = Positioning.determineTablePositioning(
						new TablePos(signaturePosString), "", origDoc,
						visualObject, false);
			} else {
				positioningInstruction = Positioning.determineTablePositioning(
						new TablePos(), "", origDoc, visualObject, false);
			}

			origDoc.close();

			SignaturePositionImpl position = new SignaturePositionImpl();
			position.setX(positioningInstruction.getX());
			position.setY(positioningInstruction.getY());
			position.setPage(positioningInstruction.getPage());
			position.setHeight(visualObject.getHeight());
			position.setWidth(visualObject.getWidth());

			requestedSignature.setSignaturePosition(position);

			PDFAsVisualSignatureProperties properties = new PDFAsVisualSignatureProperties(
					pdfObject.getStatus().getSettings(), pdfObject,
					(PdfBoxVisualObject) visualObject, positioningInstruction);

			properties.buildSignature();
			PDDocument visualDoc = PDDocument.load(properties
					.getVisibleSignature());
			// PDPageable pageable = new PDPageable(visualDoc);
			List<PDPage> pages = new ArrayList<PDPage>();
			visualDoc.getDocumentCatalog().getPages().getAllKids(pages);

			PDPage firstPage = pages.get(0);

			float stdRes = 72;
			float targetRes = resolution;
			float factor = targetRes / stdRes;

			BufferedImage outputImage = firstPage.convertToImage(
					BufferedImage.TYPE_4BYTE_ABGR, (int) targetRes);

			BufferedImage cutOut = new BufferedImage(
					(int) (position.getWidth() * factor),
					(int) (position.getHeight() * factor),
					BufferedImage.TYPE_4BYTE_ABGR);

			Graphics2D graphics = (Graphics2D) cutOut.getGraphics();

			graphics.drawImage(outputImage, 0, 0, cutOut.getWidth(), cutOut
					.getHeight(), (int) (1 * factor), (int) (outputImage
					.getHeight() - ((position.getHeight() + 1) * factor)),
					(int) ((1 + position.getWidth()) * factor),
					(int) (outputImage.getHeight()
							- ((position.getHeight() + 1) * factor) + (position
							.getHeight() * factor)), null);
			return cutOut;
		} catch (PdfAsException e) {
			logger.warn("PDF-AS  Exception", e);
			throw ErrorExtractor.searchPdfAsError(e, status);
		} catch (Throwable e) {
			logger.warn("Unexpected Throwable  Exception", e);
			throw ErrorExtractor.searchPdfAsError(e, status);
		}
	}

	/**
	 * Adds keys to the signature dictionary that define the certification level
	 * and the permissions.
	 *
	 * @param crypto
	 *            the signature dictionary
	 * @param trailer
	 *            the document trailer.
	 * @param pdfVersion
	 *            the pdf version of the document
	 * @param certificationLevel
	 *            the certification level to set
	 */
	private void addDocMDP(COSDictionary crypto, COSDictionary trailer,
			float pdfVersion, int certificationLevel) {

		logger.debug("Certification level: {}", certificationLevel);

		COSDictionary reference = new COSDictionary();
		COSDictionary transformParams = new COSDictionary();

		transformParams.setItem(COSName.P, COSInteger.get(certificationLevel));
		transformParams.setItem(COSName.V, COSName.getPDFName("1.2"));
		transformParams.setItem(COSName.TYPE,
				COSName.getPDFName("TransformParams"));
		reference.setItem(COSName.getPDFName("TransformMethod"),
				COSName.getPDFName("DocMDP"));
		reference.setItem(COSName.TYPE, COSName.getPDFName("SigRef"));

		reference.setName("TransformParams", "TransformParams");
		reference.setItem(COSName.getPDFName("TransformParams"),
				transformParams);

		if (pdfVersion < PDF_VERSION_1_6) {
			reference.setItem(COSName.getPDFName("DigestMethod"),
					COSName.getPDFName("SHA1"));
		}

		COSArray types = new COSArray();
		types.add(reference);
		crypto.setItem(COSName.getPDFName("Reference"), types);
	}

}
