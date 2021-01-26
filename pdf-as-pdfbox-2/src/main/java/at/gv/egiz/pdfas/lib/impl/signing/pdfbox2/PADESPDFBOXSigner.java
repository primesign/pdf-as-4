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
package at.gv.egiz.pdfas.lib.impl.signing.pdfbox2;

import at.gv.egiz.pdfas.common.exceptions.PDFASError;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.messages.MessageResolver;
import at.gv.egiz.pdfas.common.settings.SignatureProfileSettings;
import at.gv.egiz.pdfas.lib.api.ByteArrayDataSource;
import at.gv.egiz.pdfas.lib.api.IConfigurationConstants;
import at.gv.egiz.pdfas.lib.api.sign.IPlainSigner;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;
import at.gv.egiz.pdfas.lib.impl.ErrorExtractor;
import at.gv.egiz.pdfas.lib.impl.SignaturePositionImpl;
import at.gv.egiz.pdfas.lib.impl.configuration.PlaceholderWebConfiguration;
import at.gv.egiz.pdfas.lib.impl.configuration.SignatureProfileConfiguration;
import at.gv.egiz.pdfas.lib.impl.pdfbox2.PDFBOXObject;
import at.gv.egiz.pdfas.lib.impl.pdfbox2.placeholder.SignaturePlaceholderExtractor;
import at.gv.egiz.pdfas.lib.impl.pdfbox2.positioning.Positioning;
import at.gv.egiz.pdfas.lib.impl.pdfbox2.utils.PdfBoxUtils;
import at.gv.egiz.pdfas.lib.impl.placeholder.PlaceholderFilter;
import at.gv.egiz.pdfas.lib.impl.placeholder.SignaturePlaceholderData;
import at.gv.egiz.pdfas.lib.impl.signing.IPdfSigner;
import at.gv.egiz.pdfas.lib.impl.signing.PDFASSignatureExtractor;
import at.gv.egiz.pdfas.lib.impl.signing.PDFASSignatureInterface;
import at.gv.egiz.pdfas.lib.impl.stamping.IPDFStamper;
import at.gv.egiz.pdfas.lib.impl.stamping.IPDFVisualObject;
import at.gv.egiz.pdfas.lib.impl.stamping.TableFactory;
import at.gv.egiz.pdfas.lib.impl.stamping.ValueResolver;
import at.gv.egiz.pdfas.lib.impl.stamping.pdfbox2.PDFAsVisualSignatureProperties;
import at.gv.egiz.pdfas.lib.impl.stamping.pdfbox2.PdfBoxVisualObject;
import at.gv.egiz.pdfas.lib.impl.stamping.pdfbox2.StamperFactory;
import at.gv.egiz.pdfas.lib.impl.status.OperationStatus;
import at.gv.egiz.pdfas.lib.impl.status.PDFObject;
import at.gv.egiz.pdfas.lib.impl.status.RequestedSignature;
import at.knowcenter.wag.egov.egiz.pdf.PositioningInstruction;
import at.knowcenter.wag.egov.egiz.pdf.TablePos;
import at.knowcenter.wag.egov.egiz.table.Table;
import iaik.x509.X509Certificate;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.cos.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.common.PDNumberTreeNode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureElement;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureTreeRoot;
import org.apache.pdfbox.pdmodel.graphics.color.PDOutputIntent;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureOptions;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDSignatureField;
import org.apache.pdfbox.preflight.PreflightDocument;
import org.apache.pdfbox.preflight.ValidationResult;
import org.apache.pdfbox.preflight.exception.SyntaxValidationException;
import org.apache.pdfbox.preflight.exception.ValidationException;
import org.apache.pdfbox.preflight.parser.PreflightParser;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.PDFAIdentificationSchema;
import org.apache.xmpbox.xml.DomXmpParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataSource;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PADESPDFBOXSigner implements IPdfSigner, IConfigurationConstants {

	private static final Logger logger = LoggerFactory.getLogger(PADESPDFBOXSigner.class);
	private boolean isAdobeSigForm = false;

	public void signPDF(PDFObject genericPdfObject, RequestedSignature requestedSignature,
			PDFASSignatureInterface genericSigner) throws PdfAsException {

		PDFAsVisualSignatureProperties properties = null;
		List<SignaturePlaceholderData> placeholders;
		String placeholder_id = "";

		if(PlaceholderWebConfiguration.getValue(PLACEHOLDER_WEB_ID) != null  && !PlaceholderWebConfiguration.getValue(PLACEHOLDER_WEB_ID).equalsIgnoreCase("")){
			placeholder_id = PlaceholderWebConfiguration.getValue(PLACEHOLDER_WEB_ID);
		}


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

        String pdfaVersion = null;

		PDDocument doc = null;
		SignatureOptions options = new SignatureOptions();
		COSDocument visualSignatureDocumentGuard = null;
		try {

			doc = pdfObject.getDocument();
			//if signature already exists dont create new page
			List<PDSignatureField> pdSignatureFieldList = doc.getSignatureFields();
			PDSignature signature;

			// sign a PDF with an existing empty signature, as created by the CreateEmptySignatureForm example.
			String sigFieldName = pdfObject.getStatus().getSettings().getValue(SIGNATURE_FIELD_NAME);
			signature = findExistingSignature(doc, sigFieldName);
			if (signature == null) {
				// create signature dictionary
				signature = new PDSignature();
			}
			else {
				isAdobeSigForm = true;
			}

			signature.setFilter(COSName.getPDFName(signer.getPDFFilter()));
			signature.setSubFilter(COSName.getPDFName(signer.getPDFSubFilter()));
			SignaturePlaceholderData signaturePlaceholderDataInit = PlaceholderFilter.checkPlaceholderSignatureLocation(pdfObject.getStatus(), pdfObject.getStatus().getSettings(), placeholder_id);

            placeholders = SignaturePlaceholderExtractor.getPlaceholders();

			if(placeholder_id.equalsIgnoreCase("")){
				if(checkAvailablePlaceholders(placeholders,existingSignatureLocations(doc))!=null)
				{
					placeholder_id = (checkAvailablePlaceholders(placeholders, existingSignatureLocations(doc))).getId();
				};
			}

			SignaturePlaceholderData signaturePlaceholderData = PlaceholderFilter
					.checkPlaceholderSignatureLocation(pdfObject.getStatus(), pdfObject.getStatus().getSettings(),placeholder_id);

			TablePos tablePos = null;

			if(signaturePlaceholderData!=null)
			signature.setLocation(signaturePlaceholderData.getPlaceholderName());

			if (signaturePlaceholderData != null) {
				// Placeholder found!
                placeholders.clear();
                logger.info("Placeholder data found.");
				if (signaturePlaceholderData.getProfile() != null) {
					logger.debug("Placeholder Profile set to: " + signaturePlaceholderData.getProfile());
					requestedSignature.setSignatureProfileID(signaturePlaceholderData.getProfile());
				}

				tablePos = signaturePlaceholderData.getTablePos();
				if (tablePos != null) {

					SignatureProfileConfiguration signatureProfileConfiguration = pdfObject.getStatus()
							.getSignatureProfileConfiguration(requestedSignature.getSignatureProfileID());

					float minWidth = signatureProfileConfiguration.getMinWidth();

					if(minWidth > 0) {
						if (tablePos.getWidth() < minWidth) {
							tablePos.width = minWidth;
							logger.debug("Correcting placeholder with to minimum width {}", minWidth);
						}
					}
					logger.debug("Placeholder Position set to: " + tablePos.toString());
				}
			}
			SignatureProfileSettings signatureProfileSettings = TableFactory
					.createProfile(requestedSignature.getSignatureProfileID(), pdfObject.getStatus().getSettings());
            //Check if input document is PDF-A conform
            if (signatureProfileSettings.isPDFA()) {
                DataSource origDoc = pdfObject.getOriginalDocument();
                InputStream stream = origDoc.getInputStream();
                //Run PreflightParser for checking conformity//
                //runPDFAPreflight(origDoc);
            }

			ValueResolver resolver = new ValueResolver(requestedSignature, pdfObject.getStatus());
			String signerName = resolver.resolve("SIG_SUBJECT", signatureProfileSettings.getValue("SIG_SUBJECT"),
					signatureProfileSettings);

			signature.setName(signerName);
			signature.setSignDate(Calendar.getInstance());
			String signerReason = signatureProfileSettings.getSigningReason();

			if (signerReason == null) {
				signerReason = "PAdES Signature";
			}

			signature.setReason(signerReason);
			logger.debug("Signing reason: " + signerReason);

			logger.debug("Signing @ " + signer.getSigningDate().getTime().toString());
			// the signing date, needed for valid signature
			// signature.setSignDate(signer.getSigningDate());

			signer.setPDSignature(signature);

			int signatureSize = 0x1000;
			try {
				String reservedSignatureSizeString = signatureProfileSettings.getValue(SIG_RESERVED_SIZE);
				if (reservedSignatureSizeString != null) {
					signatureSize = Integer.parseInt(reservedSignatureSizeString);
				}
				logger.debug("Reserving {} bytes for signature", signatureSize);
			} catch (NumberFormatException e) {
				logger.warn("Invalid configuration value: {} should be a number using 0x1000", SIG_RESERVED_SIZE);
			}
			options.setPreferredSignatureSize(signatureSize);

            if(signatureProfileSettings.isPDFA() || signatureProfileSettings.isPDFA3()) {
                pdfaVersion = getPDFAVersion(doc);
                signatureProfileSettings.setPDFAVersion(pdfaVersion);
            }

			// Is visible Signature
			if (requestedSignature.isVisual()) {
				logger.info("Creating visual siganture block");

				SignatureProfileConfiguration signatureProfileConfiguration = pdfObject.getStatus()
						.getSignatureProfileConfiguration(requestedSignature.getSignatureProfileID());

				if (tablePos == null) {
					// ================================================================
					// PositioningStage (visual) -> find position or use
					// fixed
					// position

					String posString = pdfObject.getStatus().getSignParamter().getSignaturePosition();

					TablePos signaturePos = null;

					String signaturePosString = signatureProfileConfiguration.getDefaultPositioning();

					if (signaturePosString != null) {
						logger.debug("using signature Positioning: " + signaturePos);
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

				//Legacy Modes not supported with pdfbox2 anymore
//				boolean legacy32Position = signatureProfileConfiguration.getLegacy32Positioning();
//				boolean legacy40Position = signatureProfileConfiguration.getLegacy40Positioning();

				// create Table describtion


				Table main = TableFactory.createSigTable(signatureProfileSettings, MAIN, pdfObject.getStatus(),
						requestedSignature);

				IPDFStamper stamper = StamperFactory.createDefaultStamper(pdfObject.getStatus().getSettings());

				IPDFVisualObject visualObject = stamper.createVisualPDFObject(pdfObject, main);

				/*
				 * PDDocument originalDocument = PDDocument .load(new
				 * ByteArrayInputStream(pdfObject.getStatus()
				 * .getPdfObject().getOriginalDocument()));
				 */

				PositioningInstruction positioningInstruction = Positioning.determineTablePositioning(tablePos, "",
						doc, visualObject, pdfObject.getStatus().getSettings());

				logger.debug("Positioning: {}", positioningInstruction.toString());

				if(!isAdobeSigForm)  {
				if (positioningInstruction.isMakeNewPage()) {
					int last = doc.getNumberOfPages() - 1;
					PDDocumentCatalog root = doc.getDocumentCatalog();
					PDPage lastPage = root.getPages().get(last);
					root.getPages().getCOSObject().setNeedToBeUpdated(true);
					PDPage p = new PDPage(lastPage.getMediaBox());
					p.setResources(new PDResources());
					p.setRotation(lastPage.getRotation());
					doc.addPage(p);
				}

				// handle rotated page
				int targetPageNumber = positioningInstruction.getPage();
				logger.debug("Target Page: " + targetPageNumber);
				PDPage targetPage = doc.getPages().get(targetPageNumber - 1);
				int rot = targetPage.getRotation();
				logger.debug("Page rotation: " + rot);
				logger.debug("resulting Sign rotation: " + positioningInstruction.getRotation());

				SignaturePositionImpl position = new SignaturePositionImpl();
				position.setX(positioningInstruction.getX());
				position.setY(positioningInstruction.getY());
				position.setPage(positioningInstruction.getPage());
				position.setHeight(visualObject.getHeight());
				position.setWidth(visualObject.getWidth());
				requestedSignature.setSignaturePosition(position);
				}

				properties = new PDFAsVisualSignatureProperties(pdfObject.getStatus().getSettings(), pdfObject,
						(PdfBoxVisualObject) visualObject, positioningInstruction, signatureProfileSettings);

				properties.buildSignature();

				/*
				 * ByteArrayOutputStream sigbos = new
				 * ByteArrayOutputStream();
				 * sigbos.write(StreamUtils.inputStreamToByteArray
				 * (properties .getVisibleSignature())); sigbos.close();
				 */

				/*if (signaturePlaceholderData != null) {

					InputStream fis = PADESPDFBOXSigner.class.getResourceAsStream("/placeholder/empty.jpg");
					PDImageXObject img = JPEGFactory.createFromStream(doc, fis);

					img.getCOSObject().setNeedToBeUpdated(true);
					//							PDDocumentCatalog root = doc.getDocumentCatalog();
					//							PDPageNode rootPages = root.getPages();
					//							List<PDPage> kids = new ArrayList<PDPage>();
					//							rootPages.getAllKids(kids);
					int pageNumber = positioningInstruction.getPage();
					PDPage page = doc.getPages().get(pageNumber - 1);

					logger.info("Placeholder name: " + signaturePlaceholderData.getPlaceholderName());
					COSDictionary xobjectsDictionary = (COSDictionary) page.getResources().getCOSObject()
							.getDictionaryObject(COSName.XOBJECT);


					xobjectsDictionary.setItem(signaturePlaceholderData.getPlaceholderName(), img);
					xobjectsDictionary.setNeedToBeUpdated(true);
					page.getResources().getCOSObject().setNeedToBeUpdated(true);
					logger.info("Placeholder name: " + signaturePlaceholderData.getPlaceholderName());
				}*/

				if (signatureProfileSettings.isPDFA() || signatureProfileSettings.isPDFA3()) {
                    PDDocumentCatalog root = doc.getDocumentCatalog();
                    COSBase base = root.getCOSObject().getItem(COSName.OUTPUT_INTENTS);

                    InputStream colorProfile = null;
                    //colorProfile = this.getClass().getResourceAsStream("/icm/sRGB.icm");
					colorProfile = this.getClass().getResourceAsStream("/icm/sRGB Color Space Profile.icm");
                     //Set output intents for PDF/A conformity//
                            try {
                                PDOutputIntent intent = new PDOutputIntent(doc, colorProfile);

                                intent.setInfo("sRGB IEC61966-2.1");
                                intent.setOutputCondition("sRGB IEC61966-2.1");
                                intent.setOutputConditionIdentifier("sRGB IEC61966-2.1");
                                intent.setRegistryName("http://www.color.org");
                                List<PDOutputIntent> oi = new ArrayList<PDOutputIntent>();
                                oi.add(intent);
                                root.setOutputIntents(oi);
                                root.getCOSObject().setNeedToBeUpdated(true);

                                logger.info("added Output Intent");
                            } catch (Throwable e) {
                                e.printStackTrace();
                                throw new PdfAsException("Failed to add Output Intent", e);
                            }
                         finally {
                            IOUtils.closeQuietly(colorProfile);
                        }
                    }
                options.setPage(positioningInstruction.getPage()-1);
                options.setVisualSignature(properties.getVisibleSignature());
            }

			visualSignatureDocumentGuard = options.getVisualSignature();

			doc.addSignature(signature, signer, options);

			if (sigFieldName == null) {
				sigFieldName = "PDF-AS Signatur";
			}
			int count = PdfBoxUtils.countSignatures(doc, sigFieldName);

			sigFieldName = sigFieldName + count;

			PDAcroForm acroFormm = doc.getDocumentCatalog().getAcroForm();

			// PDStructureTreeRoot pdstRoot =
			// doc.getDocumentCatalog().getStructureTreeRoot();
			// COSDictionary dic =
			// doc.getDocumentCatalog().getCOSDictionary();
			// PDStructureElement el = new PDStructureElement("Widget",
			// pdstRoot);

			//this is not used for Adobe signature fields
			if(!isAdobeSigForm) {
				PDSignatureField signatureField = null;
			if (acroFormm != null) {
				@SuppressWarnings("unchecked")
				List<PDField> fields = acroFormm.getFields();

				if (fields != null) {
					for (PDField pdField : fields) {
						if (pdField != null) {
							if (pdField instanceof PDSignatureField) {
								PDSignatureField tmpSigField = (PDSignatureField) pdField;

								if (tmpSigField.getSignature() != null
										&& tmpSigField.getSignature().getCOSObject() != null) {
									if (tmpSigField.getSignature().getCOSObject()
											.equals(signature.getCOSObject())) {
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
				if (properties != null) {
					signatureField.setAlternateFieldName(properties.getAlternativeTableCaption());
				} else {
					signatureField.setAlternateFieldName(sigFieldName);
				}
			} else {
				logger.warn("Failed to name Signature Field! [Cannot find acroForm!]");
			}
			}

			PDSignatureField signatureField = null;
			PDAcroForm acroForm = doc.getDocumentCatalog().getAcroForm();
			if (acroForm != null) {
				signatureField = (PDSignatureField) acroForm.getField(sigFieldName);
			}

			// PDF-UA
			logger.info("Adding pdf/ua content.");
			try {
				PDDocumentCatalog root = doc.getDocumentCatalog();
				PDStructureTreeRoot structureTreeRoot = root.getStructureTreeRoot();
				if (structureTreeRoot != null) {
					logger.info("Tree Root: {}", structureTreeRoot.toString());
					List<Object> kids = structureTreeRoot.getKids();

					if (kids == null) {
						logger.info("No kid-elements in structure tree Root, maybe not PDF/UA document");
					}

					PDStructureElement docElement = null;
					for (Object k : kids) {
						if (k instanceof PDStructureElement) {
							docElement = (PDStructureElement) k;
							break;

						}
					}

					PDStructureElement sigBlock = new PDStructureElement("Form", docElement);

					// create object dictionary and add as child element
					COSDictionary objectDic = new COSDictionary();
					objectDic.setName("Type", "OBJR");
					objectDic.setItem("Pg", signatureField.getWidget().getPage());
					objectDic.setItem("Obj", signatureField.getWidget());

					List<Object> l = new ArrayList<Object>();
					l.add(objectDic);
					sigBlock.setKids(l);
					sigBlock.setPage(signatureField.getWidget().getPage());


					sigBlock.setTitle("Signature Table");
					sigBlock.setParent(docElement);
					docElement.appendKid(sigBlock);

					// Create and add Attribute dictionary to mitigate PAC
					// warning
					COSDictionary sigBlockDic = (COSDictionary) sigBlock.getCOSObject();
					COSDictionary sub = new COSDictionary();

					sub.setName("O", "Layout");
					sub.setName("Placement", "Block");
					sigBlockDic.setItem(COSName.A, sub);
					sigBlockDic.setNeedToBeUpdated(true);

					// Modify number tree
					PDNumberTreeNode ntn = structureTreeRoot.getParentTree();
					int parentTreeNextKey = structureTreeRoot.getParentTreeNextKey();
					if (ntn == null) {
						ntn = new PDNumberTreeNode(objectDic, null);
						logger.info("No number-tree-node found!");
					}

					COSArray ntnKids = (COSArray) ntn.getCOSObject().getDictionaryObject(COSName.KIDS);
					COSArray ntnNumbers = (COSArray) ntn.getCOSObject().getDictionaryObject(COSName.NUMS);

					if(ntnNumbers == null && ntnKids != null){//no number array, so continue with the kids array

						//create dictionary with limits and nums array
						COSDictionary pTreeEntry = new COSDictionary();
						COSArray limitsArray = new COSArray();
						//limits for exact one entry
						limitsArray.add(COSInteger.get(parentTreeNextKey));
						limitsArray.add(COSInteger.get(parentTreeNextKey));

						COSArray numsArray = new COSArray();
						numsArray.add(COSInteger.get(parentTreeNextKey));
						numsArray.add(sigBlock);

						pTreeEntry.setItem(COSName.NUMS, numsArray);
						pTreeEntry.setItem(COSName.LIMITS, limitsArray);

						PDNumberTreeNode newKidsElement = new PDNumberTreeNode(pTreeEntry, PDNumberTreeNode.class);

						ntnKids.add(newKidsElement);
						ntnKids.setNeedToBeUpdated(true);


					}else if(ntnNumbers != null && ntnKids == null){

						int arrindex = ntnNumbers.size();

						ntnNumbers.add(arrindex, COSInteger.get(parentTreeNextKey));
						ntnNumbers.add(arrindex + 1, sigBlock.getCOSObject());

						ntnNumbers.setNeedToBeUpdated(true);

						structureTreeRoot.setParentTree(ntn);

					}else if(ntnNumbers == null && ntnKids == null){
						//document is not pdfua conform before signature creation
						throw new PdfAsException("error.pdf.sig.pdfua.1");
					}else{
						//this is not allowed
						throw new PdfAsException("error.pdf.sig.pdfua.1");
					}

					// set StructureParent for signature field annotation
					signatureField.getWidget().setStructParent(parentTreeNextKey);

					//Increase the next Key value in the structure tree root
					structureTreeRoot.setParentTreeNextKey(parentTreeNextKey+1);

					// add the Tabs /S Element for Tabbing through annots
					PDPage p = signatureField.getWidget().getPage();
					p.getCOSObject().setName("Tabs", "S");
					p.getCOSObject().setNeedToBeUpdated(true);

					//check alternative signature field name
					if (signatureField != null) {
						if(signatureField.getAlternateFieldName().equals(""))
							signatureField.setAlternateFieldName(sigFieldName);
					}

					ntn.getCOSObject().setNeedToBeUpdated(true);
					sigBlock.getCOSObject().setNeedToBeUpdated(true);
					structureTreeRoot.getCOSObject().setNeedToBeUpdated(true);
					objectDic.setNeedToBeUpdated(true);
					docElement.getCOSObject().setNeedToBeUpdated(true);
				}
			} catch (Throwable e) {
				if (signatureProfileSettings.isPDFUA() == true) {
					logger.error("Could not create PDF-UA conform document!");
					throw new PdfAsException("error.pdf.sig.pdfua.1", e);
				} else {
					logger.info("Could not create PDF-UA conform signature");
				}
			}

            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        synchronized (doc) {
                    doc.saveIncremental(bos);
                            byte[] outputDocument = bos.toByteArray();
                            pdfObject.setSignedDocument(outputDocument);
                }
                        /* Check if resulting pdf is PDF-A conform */
                    if (signatureProfileSettings.isPDFA()) {
                        runPDFAPreflight(new ByteArrayDataSource(pdfObject.getSignedDocument()));
                    }

            } catch (IOException e1) {
                e1.printStackTrace();
            }

         finally {
                if (options != null) {
                    if (options.getVisualSignature() != null) {
                        options.getVisualSignature().close();
                    }
                }
            }

            System.gc();
        } catch (IOException e) {
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
			logger.debug("Signature done!");
		}
	}

    /**
     * Check via PreFlightParser if PDF-Document is a valid PDFA1
     * @param signedDocument: signed Document
     * @throws PdfAsException
     */
    private void runPDFAPreflight(final DataSource signedDocument) throws PdfAsException {
        PreflightDocument document = null;
        ValidationResult result = null;
        try {
            PreflightParser parser = new PreflightParser(signedDocument);
            //
            // parser.parse(Format.PDF_A1B);
            parser.parse();
            document = parser.getPreflightDocument();
            document.validate();

            document.close();
            result = document.getResult();
            logger.info("PDF-A Validation Result: " + result.isValid());

            if (result.getErrorsList().size() > 0) {
                logger.error("The following validation errors occured for PDF-A validation");
            }

            for (ValidationResult.ValidationError ve : result.getErrorsList()) {
                logger.error("\t" + ve.getErrorCode() + ": " + ve.getDetails());
            }

            if (!result.isValid()) {
                logger.info("The file is not a valid PDF-A document");
            }

        } catch (SyntaxValidationException e) {
            logger.error("The file is syntactically invalid.", e);
            throw new PdfAsException("Resulting PDF Document is syntactically invalid.");
        } catch (ValidationException e) {
            logger.error("The file is not a valid PDF-A document.", e);
        } catch (IOException e) {
            logger.error("An IOException (" + e.getMessage()
                    + ") occurred, while validating the PDF-A conformance", e);
            throw new PdfAsException("Failed validating PDF Document IOException.");
        } catch (RuntimeException e) {
            logger.debug("An RuntimeException (" + e.getMessage()
                    + ") occurred, while validating the PDF-A conformance", e);
            throw new PdfAsException("Failed validating PDF Document RuntimeException.");
        } finally {
            if (document != null) {
                IOUtils.closeQuietly(document);
            }
        }
    }

    @Override
    public PDFObject buildPDFObject(OperationStatus operationStatus) {
        return new PDFBOXObject(operationStatus);
    }

    @Override
    public PDFASSignatureInterface buildSignaturInterface(IPlainSigner signer, SignParameter parameters,
                                                          RequestedSignature requestedSignature) {
        return new PdfboxSignerWrapper(signer, parameters, requestedSignature);
    }

    @Override
    public PDFASSignatureExtractor buildBlindSignaturInterface(X509Certificate certificate, String filter, String subfilter, Calendar date) {
        return new SignatureDataExtractor(certificate, filter, subfilter, date);
    }

    @Override
    public void checkPDFPermissions(PDFObject genericPdfObject) throws PdfAsException {
        if (!(genericPdfObject instanceof PDFBOXObject)) {
            // tODO:
            throw new PdfAsException();
        }

        PDFBOXObject pdfObject = (PDFBOXObject) genericPdfObject;
        PdfBoxUtils.checkPDFPermissions(pdfObject.getDocument());
    }

    @Override
    public byte[] rewritePlainSignature(byte[] plainSignature) {
        String signature = new COSString(plainSignature).toHexString();
        byte[] pdfSignature = signature.getBytes();
        return pdfSignature;
    }

    @Override
    public Image generateVisibleSignaturePreview(SignParameter parameter, java.security.cert.X509Certificate cert,
                                                 int resolution, OperationStatus status, RequestedSignature requestedSignature) throws PDFASError {
        try {

            PDFBOXObject pdfObject = (PDFBOXObject) status.getPdfObject();
            PDDocument origDoc = new PDDocument();

            origDoc.addPage(new PDPage(PDRectangle.A4));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            origDoc.save(baos);
            baos.close();

            pdfObject.setOriginalDocument(new ByteArrayDataSource(baos.toByteArray()));

            SignatureProfileSettings signatureProfileSettings = TableFactory
                    .createProfile(requestedSignature.getSignatureProfileID(), pdfObject.getStatus().getSettings());

            // create Table describtion
            Table main = TableFactory.createSigTable(signatureProfileSettings, MAIN, pdfObject.getStatus(),
                    requestedSignature);

            IPDFStamper stamper = StamperFactory.createDefaultStamper(pdfObject.getStatus().getSettings());

            IPDFVisualObject visualObject = stamper.createVisualPDFObject(pdfObject, main);

            SignatureProfileConfiguration signatureProfileConfiguration = pdfObject.getStatus()
                    .getSignatureProfileConfiguration(requestedSignature.getSignatureProfileID());

            String signaturePosString = signatureProfileConfiguration.getDefaultPositioning();
            PositioningInstruction positioningInstruction;
            if (signaturePosString != null) {
                positioningInstruction = Positioning.determineTablePositioning(new TablePos(signaturePosString), "",
                        origDoc, visualObject, pdfObject.getStatus().getSettings());
            } else {
                positioningInstruction = Positioning.determineTablePositioning(new TablePos(), "", origDoc,
                        visualObject, pdfObject.getStatus().getSettings());
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
                    pdfObject.getStatus().getSettings(), pdfObject, (PdfBoxVisualObject) visualObject,
                    positioningInstruction, signatureProfileSettings);

            properties.buildSignature();
            PDDocument visualDoc;
            synchronized (PDDocument.class) {
                visualDoc = PDDocument.load(properties.getVisibleSignature());
            }
            // PDPageable pageable = new PDPageable(visualDoc);

            PDPage firstPage = visualDoc.getDocumentCatalog().getPages().get(0);

            float stdRes = 72;
            float targetRes = resolution;
            float factor = targetRes / stdRes;


            int targetPageNumber = 0;//TODO: is this always the case
            PDFRenderer pdfRenderer = new PDFRenderer(visualDoc);
            BufferedImage outputImage = pdfRenderer.renderImageWithDPI(targetPageNumber, targetRes, ImageType.ARGB);

            //BufferedImage outputImage = firstPage.convertToImage(BufferedImage.TYPE_4BYTE_ABGR, (int) targetRes);

            BufferedImage cutOut = new BufferedImage((int) (position.getWidth() * factor),
                    (int) (position.getHeight() * factor), BufferedImage.TYPE_4BYTE_ABGR);

            Graphics2D graphics = (Graphics2D) cutOut.getGraphics();

            graphics.drawImage(outputImage, 0, 0, cutOut.getWidth(), cutOut.getHeight(), (int) (1 * factor),
                    (int) (outputImage.getHeight() - ((position.getHeight() + 1) * factor)),
                    (int) ((1 + position.getWidth()) * factor), (int) (outputImage.getHeight()
                            - ((position.getHeight() + 1) * factor) + (position.getHeight() * factor)),
                    null);
            return cutOut;
        } catch (PdfAsException e) {
            logger.warn("PDF-AS  Exception", e);
            throw ErrorExtractor.searchPdfAsError(e, status);
        } catch (Throwable e) {
            logger.warn("Unexpected Throwable  Exception", e);
            throw ErrorExtractor.searchPdfAsError(e, status);
        }
    }

    private String getPDFAVersion(PDDocument doc) {
        try {
            PDDocumentCatalog cat = doc.getDocumentCatalog();
            PDMetadata metadata = cat.getMetadata();

            if (metadata != null) {
                DomXmpParser xmpParser = new DomXmpParser();
                XMPMetadata xmpMetadata = xmpParser.parse(metadata.exportXMPMetadata());
                if (xmpMetadata != null) {
                    PDFAIdentificationSchema pdfaIdentificationSchema = xmpMetadata.getPDFIdentificationSchema();
                    if (pdfaIdentificationSchema != null) {
                        Integer pdfaversion = pdfaIdentificationSchema.getPart();
                        String conformance = pdfaIdentificationSchema.getConformance();
                        logger.info("Detected PDF/A Version: {} - {}", pdfaversion, conformance);

                        if (pdfaversion != null) {
                            return String.valueOf(pdfaversion);
                        }
                    }
                }
            }
        } catch (Throwable e) {
            logger.warn("Failed to determine PDF/A Version!", e);
        }
        return null;
    }

	// Find an existing signature.
	private PDSignature findExistingSignature(PDDocument doc, String sigFieldName) {
		PDSignature signature = null;
		PDSignatureField signatureField;
		PDAcroForm acroForm = doc.getDocumentCatalog().getAcroForm();
		if (acroForm != null) {
			signatureField = (PDSignatureField) acroForm.getField(sigFieldName);
			if (signatureField != null) {
				// retrieve signature dictionary
				signature = signatureField.getSignature();
				if (signature == null) {
					signature = new PDSignature();
					signatureField.getCOSObject().setItem(COSName.V, signature);
				}
				else {
					throw new IllegalStateException("The signature field " + sigFieldName + " is already signed.");
				}
			}
		}
		return signature;
	}

	private List<String> existingSignatureLocations(PDDocument doc) {
		List<String> existingLocations = new ArrayList<>();
		try {
			List <PDSignature> pdSignatureList =  doc.getSignatureDictionaries();
			if(pdSignatureList.size() != 0) {
				for(PDSignature sig : pdSignatureList) {
					existingLocations.add(sig.getLocation());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return existingLocations;
	}

	//find first available placeholder
	public SignaturePlaceholderData checkAvailablePlaceholders(List<SignaturePlaceholderData> placeholders, List<String> existingPlaceholders) {
		SignaturePlaceholderData result = null;

		if(placeholders!=null) {
		for(int i = 0; i < placeholders.size(); ++i) {
            if(!existingPlaceholders.contains(placeholders.get(i).getPlaceholderName())) {
				result = placeholders.get(i);
				break;
			}
		}
	}
		return result;
	}
}