package at.gv.egiz.pdfas.lib.impl.stamping.pdfbox;

import java.io.InputStream;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible.PDFTemplateBuilder;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible.PDFTemplateStructure;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible.PDVisibleSigBuilder;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible.PDVisibleSignDesigner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.lib.impl.stamping.IPDFStamper;
import at.gv.egiz.pdfas.lib.impl.stamping.IPDFVisualObject;
import at.gv.egiz.pdfas.lib.impl.status.PDFObject;
import at.knowcenter.wag.egov.egiz.pdf.PositioningInstruction;
import at.knowcenter.wag.egov.egiz.table.Table;

public class PdfBoxStamper implements IPDFStamper {

	private static final Logger logger = LoggerFactory.getLogger(PdfBoxStamper.class);
	
	private PDFTemplateBuilder pdfBuilder;
	
	public PdfBoxStamper() {
		this.pdfBuilder = new PDVisibleSigBuilder();
	}
	
	/*
	private InputStream renderTable(Table abstractTable) throws PdfAsException
    {
		logger.info("pdf building has been started");
        PDFTemplateStructure pdfStructure = pdfBuilder.getStructure();
        //pdfStructure.setIm
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
        AffineTransform transform = pdfStructure.getAffineTransform();
       
        // rectangle, formatter, image. /AcroForm/DR/XObject contains that form
        this.pdfBuilder.createSignatureRectangle(pdSignatureField, properties);
        this.pdfBuilder.createFormaterRectangle(properties.getFormaterRectangleParams());
        PDRectangle formater = pdfStructure.getFormaterRectangle();
        this.pdfBuilder.createSignatureImage(template, properties.getImage());

        // create form stream, form and  resource. 
        this.pdfBuilder.createHolderFormStream(template);
        PDStream holderFormStream = pdfStructure.getHolderFormStream();
        this.pdfBuilder.createHolderFormResources();
        PDResources holderFormResources = pdfStructure.getHolderFormResources();
        this.pdfBuilder.createHolderForm(holderFormResources, holderFormStream, formater);
        
        // that is /AP entry the appearance dictionary.
        this.pdfBuilder.createAppearanceDictionary(pdfStructure.getHolderForm(), pdSignatureField);
        
        // inner formstream, form and resource (hlder form containts inner form)
        this.pdfBuilder.createInnerFormStream(template);
        this.pdfBuilder.createInnerFormResource();
        PDResources innerFormResource = pdfStructure.getInnerFormResources();
        this.pdfBuilder.createInnerForm(innerFormResource, pdfStructure.getInnterFormStream(), formater);
        PDFormXObject innerForm = pdfStructure.getInnerForm();
       
        // inner form must be in the holder form as we wrote
        this.pdfBuilder.insertInnerFormToHolerResources(innerForm, holderFormResources);
        
        //  Image form is in this structure: /AcroForm/DR/FRM0/Resources/XObject/n0
        this.pdfBuilder.createImageFormStream(template);
        PDStream imageFormStream = pdfStructure.getImageFormStream();
        this.pdfBuilder.createImageFormResources();
        PDResources imageFormResources = pdfStructure.getImageFormResources();
        this.pdfBuilder.createImageForm(imageFormResources, innerFormResource, imageFormStream, formater, transform,
                pdfStructure.getImage());
       
        // now inject procSetArray
        this.pdfBuilder.injectProcSetArray(innerForm, page, innerFormResource, imageFormResources, holderFormResources,
                pdfStructure.getProcSet());

        String imgFormName = pdfStructure.getImageFormName();
        String imgName = pdfStructure.getImageName();
        String innerFormName = pdfStructure.getInnerFormName();

        // now create Streams of AP
        this.pdfBuilder.injectAppearanceStreams(holderFormStream, imageFormStream, imageFormStream, imgFormName,
                imgName, innerFormName, properties);
        this.pdfBuilder.createVisualSignature(template);
        this.pdfBuilder.createWidgetDictionary(pdSignatureField, holderFormResources);
        
        ByteArrayInputStream in = pdfStructure.getTemplateAppearanceStream();
        logger.info("stream returning started, size= " + in.available());
        
        // we must close the document
        template.close();
        
        // return result of the stream 
        return in;
    }
	*/
	
	public IPDFVisualObject createVisualPDFObject(PDFObject pdf, Table table) {
		
		return null;
	}

	public byte[] writeVisualObject(IPDFVisualObject visualObject,
			PositioningInstruction positioningInstruction, byte[] pdfData,
			String placeholderName) throws PdfAsException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setSettings(ISettings settings) {
		// TODO Auto-generated method stub
		
	}

}
