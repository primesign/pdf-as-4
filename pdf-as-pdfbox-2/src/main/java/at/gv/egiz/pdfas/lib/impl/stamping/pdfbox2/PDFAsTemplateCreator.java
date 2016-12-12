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
package at.gv.egiz.pdfas.lib.impl.stamping.pdfbox2;

import java.io.*;

import at.gv.egiz.pdfas.common.settings.SignatureProfileSettings;
import at.gv.egiz.pdfas.lib.impl.stamping.TableFactory;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible.PDFTemplateCreator;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible.PDFTemplateStructure;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDSignatureField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;

public class PDFAsTemplateCreator extends PDFTemplateCreator {

	PDFAsVisualSignatureBuilder pdfBuilder;
    private static final Logger logger = LoggerFactory.getLogger(PDFAsTemplateCreator.class);
	
	public PDFAsTemplateCreator(PDFAsVisualSignatureBuilder bookBuilder) {
		super(bookBuilder);
		this.pdfBuilder = bookBuilder;
	}

	
	public InputStream buildPDF(PDFAsVisualSignatureDesigner properties, PDDocument originalDocument)
			throws IOException, PdfAsException {
		logger.debug("pdf building has been started");
        PDFTemplateStructure pdfStructure = pdfBuilder.getStructure();

        // we create array of [Text, ImageB, ImageC, ImageI]
        this.pdfBuilder.createProcSetArray();
        
        //create page
        this.pdfBuilder.createPage(properties);
        PDPage page = pdfStructure.getPage();

        //create template
        this.pdfBuilder.createTemplate(page);
        PDDocument template = pdfStructure.getTemplate();
        
        //create /AcroForm
        this.pdfBuilder.createAcroForm(template);
        PDAcroForm acroForm = pdfStructure.getAcroForm();

        // AcroForm contains singature fields
        this.pdfBuilder.createSignatureField(acroForm);
        PDSignatureField pdSignatureField = pdfStructure.getSignatureField();
        
        // create signature
        this.pdfBuilder.createSignature(pdSignatureField, page, properties.getSignatureFieldName());
       
        // that is /AcroForm/DR entry
        this.pdfBuilder.createAcroFormDictionary(acroForm, pdSignatureField);
        
        // create AffineTransform
        this.pdfBuilder.createAffineTransform(properties.getAffineTransformParams());
        //AffineTransform transform = pdfStructure.getAffineTransform();
       
        // rectangle, formatter, image. /AcroForm/DR/XObject contains that form
        this.pdfBuilder.createSignatureRectangle(pdSignatureField, properties, properties.getRotation() + properties.getPageRotation());
        this.pdfBuilder.createFormaterRectangle(properties.getFormaterRectangleParams());
        PDRectangle formater = pdfStructure.getFormatterRectangle();
        
        //this.pdfBuilder.createSignatureImage(template, properties.getImageStream());

        // create form stream, form and  resource. 
        this.pdfBuilder.createHolderFormStream(template);
        PDStream holderFormStream = pdfStructure.getHolderFormStream();
        this.pdfBuilder.createHolderFormResources();
        PDResources holderFormResources = pdfStructure.getHolderFormResources();
        this.pdfBuilder.createHolderForm(holderFormResources, holderFormStream, formater);
        
        // that is /AP entry the appearance dictionary.
        this.pdfBuilder.createAppearanceDictionary(pdfStructure.getHolderForm(), pdSignatureField, 
        		properties.getRotation() + properties.getPageRotation());
        
        // inner formstream, form and resource (hlder form containts inner form)
        this.pdfBuilder.createInnerFormStreamPdfAs(template, originalDocument);
        this.pdfBuilder.createInnerFormResource();
        PDResources innerFormResource = pdfStructure.getInnerFormResources();
        this.pdfBuilder.createInnerForm(innerFormResource, pdfStructure.getInnerFormStream(), formater);
        PDFormXObject innerForm = pdfStructure.getInnerForm();
       
        // inner form must be in the holder form as we wrote
        this.pdfBuilder.insertInnerFormToHolerResources(innerForm, holderFormResources);
        
        //  Image form is in this structure: /AcroForm/DR/FRM0/Resources/XObject/n0
        //this.pdfBuilder.createImageFormStream(template);
        //PDStream imageFormStream = pdfStructure.getImageFormStream();
        //this.pdfBuilder.createImageFormResources();
        //PDResources imageFormResources = pdfStructure.getImageFormResources();
        //this.pdfBuilder.createImageForm(imageFormResources, innerFormResource, imageFormStream, formater, transform,
        //        pdfStructure.getJpedImage());
       
        // now inject procSetArray
        /*this.pdfBuilder.injectProcSetArray(innerForm, page, innerFormResource, imageFormResources, holderFormResources,
                pdfStructure.getProcSet());*/
        this.pdfBuilder.injectProcSetArray(innerForm, page, innerFormResource, null, holderFormResources,
                pdfStructure.getProcSet());
        

        /*String imgFormName = pdfStructure.getImageFormName();
        String imgName = pdfStructure.getImageName();*/
        String innerFormName = pdfStructure.getInnerFormName().getName();

        // now create Streams of AP
        /*this.pdfBuilder.injectAppearanceStreams(holderFormStream, imageFormStream, imageFormStream, imgFormName,
                imgName, innerFormName, properties);*/
        this.pdfBuilder.injectAppearanceStreams(holderFormStream, null, null, null,
        		null, innerFormName, properties);
        this.pdfBuilder.createVisualSignature(template);
        this.pdfBuilder.createWidgetDictionary(pdSignatureField, holderFormResources);



        ByteArrayInputStream in = null;
    
        	//COSDocument doc = pdfStructure.getVisualSignature();
        	//doc.
            //in = pdfStructure.getTemplateAppearanceStream();
        	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        	template.save(baos);
        	baos.close();

        SignatureProfileSettings signatureProfileSettings =
                this.pdfBuilder.signatureProfileSettings;

        boolean requirePDFA3 = signatureProfileSettings.isPDFA3();

        if(requirePDFA3) {

            //FileOutputStream fos = new FileOutputStream("/tmp/signature.pdf");
            //fos.write(baos.toByteArray());
            //fos.close();

            PDDocument cidSetRemoved = PDDocument.load(baos.toByteArray());
            try {
                this.pdfBuilder.removeCidSet(cidSetRemoved);
                baos.reset();
                baos = new ByteArrayOutputStream();
                cidSetRemoved.save(baos);
                baos.close();
            } finally {
                cidSetRemoved.close();
            }
        }

        in = new ByteArrayInputStream(baos.toByteArray());
        

        logger.debug("stream returning started, size= " + in.available());
        
        // we must close the document
        this.pdfBuilder.closeTemplate(template);
        
        // return result of the stream 
        return in;
	}
}
