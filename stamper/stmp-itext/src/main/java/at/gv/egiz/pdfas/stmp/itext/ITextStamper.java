package at.gv.egiz.pdfas.stmp.itext;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.PDFIOException;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.settings.ISettings;
import at.gv.egiz.pdfas.lib.impl.stamping.IPDFStamper;
import at.gv.egiz.pdfas.lib.impl.stamping.IPDFVisualObject;
import at.gv.egiz.pdfas.lib.impl.status.PDFObject;
import at.knowcenter.wag.egov.egiz.pdf.PositioningInstruction;
import at.knowcenter.wag.egov.egiz.table.Entry;
import at.knowcenter.wag.egov.egiz.table.Style;
import at.knowcenter.wag.egov.egiz.table.Table;

import com.lowagie.text.BadElementException;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ITextStamperAccess;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

public class ITextStamper implements IPDFStamper {

    private static final Logger logger = LoggerFactory.getLogger(ITextStamper.class);

    /**
     * The default font definition
     */
    private static Font DEFAULT_FONT = new Font(Font.HELVETICA, 8, Font.NORMAL);

    private ISettings settings;
    
    /**
     * This method visualize an abstract table into a corresponding pdf table. The
     * new pdf table is redered and get the style information from the abstract
     * cell.
     *
     * @param abstractTable
     *          the abstract table definition
     * @return the new redererd pdf table cell
     * @throws PdfAsException
     *           ErrorCode:220, 221, 222, 223
     * @see com.lowagie.text.pdf.PdfPTable
     * @see at.knowcenter.wag.egov.egiz.table.Table
     */
    private PdfPTable renderTable(Table abstractTable) throws PdfAsException
    {
        if (abstractTable == null)
        {
            PdfAsException pde = new PdfAsException("error.pdf.stamp.03");
            throw pde;
        }
        PdfPTable pdf_table = null;
        float[] cols = abstractTable.getColsRelativeWith();
        int max_cols = abstractTable.getMaxCols();
        if (cols == null)
        {
            cols = new float[max_cols];
            // set the column ratio for all columns to 1
            for (int cols_idx = 0; cols_idx < cols.length; cols_idx++)
            {
                cols[cols_idx] = 1;
            }
        }
        pdf_table = new PdfPTable(cols);
        pdf_table.setWidthPercentage(abstractTable.getWidth());
        Style table_style = abstractTable.getStyle();
        setCellStyle(pdf_table.getDefaultCell(), table_style, Entry.TYPE_TABLE);

        @SuppressWarnings("rawtypes")
		ArrayList rows = abstractTable.getRows();
        for (int row_idx = 0; row_idx < rows.size(); row_idx++)
        {
            @SuppressWarnings("rawtypes")
			ArrayList row = (ArrayList) rows.get(row_idx);
            logger.debug("## Row:" + row_idx + " ## of table:" + abstractTable.getName());
            for (int entry_idx = 0; entry_idx < row.size(); entry_idx++)
            {
                Entry cell = (Entry) row.get(entry_idx);
                // 03.11.2010 changed by exthex - swapped the two params, was probably a bug
                Style inherit_style = Style.doInherit(table_style, cell.getStyle());
                cell.setStyle(inherit_style);
                logger.debug(cell.toString());
                PdfPCell pdf_cell = renderCell(cell);
                if (cell.getColSpan() > 1)
                {
                    pdf_cell.setColspan(cell.getColSpan());
                }
                if (cell.isNoWrap())
                {
                    pdf_cell.setNoWrap(true);
                }
                // System.err.println("valign:" + pdf_cell.getVerticalAlignment() + "
                // halign:" +
                // pdf_cell.getHorizontalAlignment());
                pdf_table.addCell(pdf_cell);
            }
            pdf_table.completeRow();
        }
        logger.debug("render table:" + abstractTable.getName());
        return pdf_table;
    }

    /**
     * Map the style align definitions to IText's align statements
     */
    private static HashMap<String, Integer> alignMap_ = new HashMap<String, Integer>();

    /**
     * Map the font definitions to IText's font statements
     */
    private static HashMap<String, Integer> fontStyleMap_ = new HashMap<String, Integer>();
    
    private static HashMap<String, Font> fontMap_ = new HashMap<String, Font>();

    static  {
        initStyleMaps();
    }

    /**
     * This method initialize the style maps. It maps the style style definitions
     * to IText styles.
     */
    private static void initStyleMaps()
    {
        alignMap_.put(Style.TOP, new Integer(Element.ALIGN_TOP));
        alignMap_.put(Style.MIDDLE, new Integer(Element.ALIGN_MIDDLE));
        alignMap_.put(Style.BOTTOM, new Integer(Element.ALIGN_BOTTOM));
        alignMap_.put(Style.LEFT, new Integer(Element.ALIGN_LEFT));
        alignMap_.put(Style.CENTER, new Integer(Element.ALIGN_CENTER));
        alignMap_.put(Style.RIGHT, new Integer(Element.ALIGN_RIGHT));
        
        //BaseFont.createFont()
        fontStyleMap_.put(Style.HELVETICA, new Integer(Font.HELVETICA));
        fontStyleMap_.put(Style.TIMES_ROMAN, new Integer(Font.TIMES_ROMAN));
        fontStyleMap_.put(Style.COURIER, new Integer(Font.COURIER));
        fontStyleMap_.put(Style.NORMAL, new Integer(Font.NORMAL));
        fontStyleMap_.put(Style.BOLD, new Integer(Font.BOLD));
        fontStyleMap_.put(Style.ITALIC, new Integer(Font.ITALIC));
        fontStyleMap_.put(Style.BOLDITALIC, new Integer(Font.BOLDITALIC));
        fontStyleMap_.put(Style.UNDERLINE, new Integer(Font.UNDERLINE));
        fontStyleMap_.put(Style.STRIKETHRU, new Integer(Font.STRIKETHRU));
    }

    /**
     * This method maps the table cell definitions to the pdfCell element.
     *
     * @param pdfCell
     *          the pdf cell to be styled
     * @param cellStyle
     *          the abstract style definition
     * @param type
     *          type of the cell to render - the appropriate style will be set
     * @see com.lowagie.text.pdf.PdfPCell
     * @see at.knowcenter.wag.egov.egiz.table.Style
     */
    private void setCellStyle(PdfPCell pdfCell, Style cellStyle, int type)
    {
        if (cellStyle != null)
        {
            if (cellStyle.getBgColor() != null)
            {
                pdfCell.setBackgroundColor(cellStyle.getBgColor());
            }
            pdfCell.setPadding(cellStyle.getPadding());
            //exthex - fix for not exactly vertically centered text
            pdfCell.setUseAscender(true);

            if (cellStyle.getBorder() > 0)
            {
                pdfCell.setBorderWidth(cellStyle.getBorder());
            }
            else
            {
                pdfCell.setBorder(0);
            }
            int align = -1;
            if (type == Entry.TYPE_VALUE && cellStyle.getValueVAlign() != null)
                align = ((Integer) alignMap_.get(cellStyle.getValueVAlign())).intValue();
                //Note: to change the default valign of images to those of values, change the if construct below
            else if (type == Entry.TYPE_IMAGE && cellStyle.getImageVAlign() != null)
                align = ((Integer) alignMap_.get(cellStyle.getImageVAlign())).intValue();
            else if (cellStyle.getVAlign() != null)

                if(alignMap_.get(cellStyle.getVAlign()) == null) {
                    align = -1;
                } else {
                    align = alignMap_.get(cellStyle.getVAlign()).intValue();
                }
            if (align != -1)
                pdfCell.setVerticalAlignment(align);

            align = -1;
            if (type == Entry.TYPE_VALUE && cellStyle.getValueHAlign() != null)
                align = ((Integer) alignMap_.get(cellStyle.getValueHAlign())).intValue();
                //Note: to change the default halign of images to those of values, change the if construct below
            else if (type == Entry.TYPE_IMAGE && cellStyle.getImageHAlign() != null)
                align = ((Integer) alignMap_.get(cellStyle.getImageHAlign())).intValue();
            else if (cellStyle.getHAlign() != null)
                align = ((Integer) alignMap_.get(cellStyle.getHAlign())).intValue();
            if (align != -1)
                pdfCell.setHorizontalAlignment(align);
        }
    }

    /**
     * Creates a custom
     * @param fontString
     * @return
     * @throws PdfAsException
     */
    private Font getCellTrueTypeFont(String fontString) throws PdfAsException {
        float fontSize=8;
        String fontName = fontString.replaceFirst("TTF:", "");
        String[] split = fontName.split(",");
        if(split.length>1)
        {
            fontName = split[0].trim();
            try
            {
                fontSize = Float.parseFloat(split[1].trim());
            }catch (NumberFormatException e)
            {
                logger.error("Unable to parse fontsize:"+fontString);
            }
        }
        logger.debug("TrueType Font detected:"+fontName +" ("+fontSize+")");

        try {
            Font font = (Font) fontMap_.get(fontString);

            // TODO: implement FONT resources via settings path!
            if (font == null) {
                logger.debug("Font \"" + fontString + "\" not in cache. Instantiating font.");
                String fontPath = this.settings.getWorkingDirectory()  + File.separator + "fonts" + File.separator + fontName;
                logger.debug("Instantiating \"" + fontPath + "\".");

                font = new Font(BaseFont.createFont(fontPath, BaseFont.WINANSI, true), fontSize);
                fontMap_.put(fontString, font);
            }
            return font;
       } catch (DocumentException e) {
           throw new PdfAsException("error.pdf.stamp.01", e);
       } catch (IOException e) {
           throw new PdfAsException("error.pdf.stamp.01", e);
       }
    }


    /**
     * This method maps the cell font definition to the iText Font Object
     *
     * @param fontString
     * @return the corresponding iText Font Object
     * @see com.lowagie.text.Font
     */
    private Font getCellFont(String fontString)
    {
        Font font = DEFAULT_FONT;
        if (fontString == null)
        {
            return font;
        }
        Object cache_font = fontMap_.get(fontString);
        if (cache_font != null)
        {
            return (Font) cache_font;
        }
        String[] font_arr = fontString.split(",");
        if (font_arr.length != 3)
        {
            return font;
        }
        Object font_face = fontStyleMap_.get(font_arr[0]);
        if (font_face == null)
        {
            return font;
        }
        Object font_weight = fontStyleMap_.get(font_arr[2]);
        if (font_weight == null)
        {
            return font;
        }
        int face = ((Integer) font_face).intValue();
        float height = Float.parseFloat(font_arr[1]);
        int weight = ((Integer) font_weight).intValue();

        font = new Font(face, height, weight);
        //fontMap_.put(fontString, font);
        return font;
    }

    /**
     * This method visualize an abstract table cell into a corresponding pdf table
     * cell. The new pdf table cell is redered and get the style information from
     * the abstract cell. Following types can be rendered:
     * <ul>
     * <li>text statements</li>
     * <li>images</li>
     * <li>tables</li>
     * </ul>
     *
     * @param abstractCell
     *          the abstract cell definition
     * @return the new redererd pdf table cell
     * @throws PdfAsException
     *           ErrorCode:220, 221, 222
     * @see com.lowagie.text.pdf.PdfPCell
     * @see at.knowcenter.wag.egov.egiz.table.Entry
     */
    private PdfPCell renderCell(Entry abstractCell) throws PdfAsException
    {
        // TODO: read if signature should be PDF/A compatible!!
        boolean pdfaValid = false;//PDFASUtils.isPdfAEnabled(sigObject_.getSignatureTypeDefinition().getType());

        PdfPCell pdf_cell = null;
        Style cell_style = abstractCell.getStyle();
        boolean isValue = true;
        switch (abstractCell.getType())
        {
            case Entry.TYPE_CAPTION:
                isValue = false;
            case Entry.TYPE_VALUE:
                String text = (String) abstractCell.getValue();
                if (text == null)
                {
                    text = "";
                }
                String font_string = cell_style.getFont();
                if (abstractCell.getType() == Entry.TYPE_VALUE && cell_style.getValueFont() != null)
                {
                    font_string = cell_style.getValueFont();
                }

                logger.trace("using cell font: "+font_string);

                Font cell_font;
                if(font_string.startsWith("TTF:"))
                {
                    cell_font = getCellTrueTypeFont(font_string);
                }
                else
                {
                    if (pdfaValid) {
                        throw new PdfAsException("error.pdf.stamp.02");
                    }
                    cell_font = getCellFont(font_string);

                }
                //TODO: check and maybe remove ...
                // exthex
                //if (pdfaValid && abstractCell.getType() == Entry.TYPE_VALUE) {
                //    SubsetLocal.addNonSubsetFont(cell_font.getBaseFont());
                //}
                Phrase text_phrase = new Phrase(text, cell_font);
                pdf_cell = new PdfPCell(text_phrase);
                setCellStyle(pdf_cell, cell_style, (isValue?Entry.TYPE_VALUE:Entry.TYPE_CAPTION));
                break;
            case Entry.TYPE_IMAGE:
                try
                {
                    String img_ref = (String) abstractCell.getValue();
                    // fixed by tknall start
                    File img_file = new File(img_ref);
                    if (!img_file.isAbsolute()) {
                        logger.debug("Image file declaration is relative. Prepending path of resources directory.");
                        img_file = new File(settings.getWorkingDirectory() + File.separator + img_ref);
                    } else {
                        logger.debug("Image file declaration is absolute. Skipping file relocation.");
                    }

                    if (!img_file.exists())
                    {
                        logger.debug("Image file \"" + img_file.getCanonicalPath() + "\" doesn't exist.");
                        throw new PdfAsException("error.pdf.stamp.04");
                    }
                    Image image = Image.getInstance(img_file.getCanonicalPath());
                    logger.debug("Using image file \"" + img_file.getCanonicalPath() + "\".");

                    image.scaleToFit(80.0f, 80.0f);
                    boolean fit = true;
                    Style.ImageScaleToFit istf = cell_style.getImageScaleToFit();
                    if (istf != null)
                    {
                        image.scaleToFit(istf.getWidth(), istf.getHeight());
                        fit = false;
                    }
                    pdf_cell = new PdfPCell(image, fit);
                    setCellStyle(pdf_cell, cell_style, Entry.TYPE_IMAGE);
                }
                catch (BadElementException e)
                {
                    logger.error("BadElementException:" + e.getMessage());
                    PdfAsException pde = new PdfAsException("error.pdf.stamp.05", e);
                    throw pde;
                }
                catch (MalformedURLException e)
                {
                    logger.error("MalformedURLException:" + e.getMessage());
                    PdfAsException pde = new PdfAsException("error.pdf.stamp.05", e);
                    throw pde;
                }
                catch (IOException e)
                {
                    logger.error("IOException:" + e.getMessage());
                    PdfAsException pde = new PdfAsException("error.pdf.stamp.05", e);
                    throw pde;
                }
                break;
            case Entry.TYPE_TABLE:
                Table table = (Table) abstractCell.getValue();
                // inherit the style from the parent table
                Style inherit_style = Style.doInherit(table.getStyle(), cell_style);
                table.setStyle(inherit_style);
                PdfPTable pdf_table = renderTable(table);
                pdf_cell = new PdfPCell(pdf_table);
                // The default new PdfPCell has a default border of 15.
                // For blocks without border and subtables this results
                // in a border to be drawn around the cell.
                // ==> no border on default
                pdf_cell.setBorder(0);
                break;
        }
        return pdf_cell;
    }

    public IPDFVisualObject createVisualPDFObject(PDFObject pdf, Table table) {

        // TODO: Adapt PDFSignatureObjectIText to render PDFPTable to iTextVisualObject from table
        try {
        PdfPTable pdfPTable = renderTable(table);

        ITextVisualObject iTextVisualObject = new ITextVisualObject(pdfPTable);

        return iTextVisualObject;
        } catch (PdfAsException e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] writeVisualObject(IPDFVisualObject visualObject, PositioningInstruction positioningInstruction,
                                    byte[] pdfData, String placeholderName) throws PdfAsException {
        try {

            ITextVisualObject object = null;
            if(visualObject instanceof  ITextVisualObject)      {
                object = (ITextVisualObject)visualObject;
            }

            if(object == null) {
                throw new PdfAsException("error.pdf.stamp.06");
            }

            PdfReader reader = new PdfReader(pdfData);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            PdfStamper stamper = new PdfStamper(reader, baos, reader.getPdfVersion(), true);

            int pages = reader.getNumberOfPages();
            int targetPage = positioningInstruction.getPage();

            // TODO: maybe add new page ...
            if(positioningInstruction.isMakeNewPage()) {
                Rectangle rect = reader.getPageSize(pages);
                stamper.insertPage(pages + 1, new Rectangle(rect));
                targetPage = pages + 1;
            }

            if (positioningInstruction.getPage() < 1 ||
                    positioningInstruction.getPage() > stamper.getReader().getNumberOfPages())
            {
            	logger.error("The provided page (=" +
                        positioningInstruction.getPage() + ") is out of range.");
                throw new PdfAsException("error.pdf.stamp.07");
            }
            
            if(placeholderName != null) {
            	ITextStamperAccess.replacePlaceholder(stamper, targetPage, placeholderName);
            }
            
            PdfContentByte content = stamper.getOverContent(targetPage);

            PdfPTable table = object.getTable();

            logger.info("Visual Object: " + visualObject.getWidth() + " x " + visualObject.getHeight());
            //PdfTemplate table_template = content.createTemplate(visualObject.getWidth(), visualObject.getHeight());
            
            table.writeSelectedRows(0, -1, positioningInstruction.getX(),
                    positioningInstruction.getY(), content);
            
            stamper.close();

            baos.close();

            return baos.toByteArray();

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new PDFIOException("error.pdf.stamp.08", e);
        } catch (DocumentException e) {
            logger.error(e.getMessage(), e);
            throw new PdfAsException("error.pdf.stamp.08", e);
        }
    }

	public void setSettings(ISettings settings) {
		this.settings = settings;
	}
}