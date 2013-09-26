package at.gv.egiz.pdfas.lib.impl.positioning;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.lib.impl.stamping.IPDFVisualObject;
import at.gv.egiz.pdfas.common.utils.PDFUtils;
import at.knowcenter.wag.egov.egiz.pdf.PDFUtilities;
import at.knowcenter.wag.egov.egiz.pdf.PositioningInstruction;
import at.knowcenter.wag.egov.egiz.pdf.TablePos;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

/**
 * Created with IntelliJ IDEA.
 * User: afitzek
 * Date: 8/29/13
 * Time: 4:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class Positioning {

    /**
     * The left/right margin.
     */
    public static final float SIGNATURE_MARGIN_HORIZONTAL = 50f;

    /**
     * The top/bottom margin.
     */
    public static final float SIGNATURE_MARGIN_VERTICAL = 20f;

    /**
     * Evalutates absolute positioning and prepares the PositioningInstruction for
     * placing the table.
     *
     * @param pos
     *          The absolute positioning parameter. If null it is sought in the
     *          profile definition.
     * @param signature_type
     *          The profile definition of the table to be written.
     * @param pdfDataSource
     *          The pdf.
     * @param pdf_table
     *          The pdf table to be written.
     * @return Returns the PositioningInformation.
     * @throws PdfAsException
     *           F.e.
     */
    public static PositioningInstruction determineTablePositioning(TablePos pos, String signature_type,
            PDDocument pdfDataSource, IPDFVisualObject pdf_table) throws PdfAsException
    {
        boolean legacy32 = false;

        //TODO: settings reader ...

        /*
        if (pos == null)
        {
            String pos_string = SettingsReader.getInstance().getSetting(SignatureTypes.SIG_OBJ + signature_type + ".pos", null);
            if (pos_string != null)
            {
                pos = PdfAS.parsePositionFromPosString(pos_string);
            }
        }
        if (pos == null)
        {
            // The default algorithm. x,y,w =auto ,p=lastpage, f:ignored because
            // y:auto
            pos = new TablePos();
        }

        // afitzek
        // Allow legacy positioning (3.2) for BRZ Templates ...
        boolean legacy32 = false;
        String leg = SettingsReader.getInstance().getSetting(SignatureTypes.SIG_OBJ + signature_type + ".legacy.pos", "false");
        if (leg != null) {
            if ("true".equals(leg.trim())) {
                legacy32 = true;
            }
        }
        */
        // System.out.println("Tablepos="+pos);
        return adjustSignatureTableandCalculatePosition(pdfDataSource, pdf_table, pos, legacy32);
    }

    /**
     * Sets the width of the table according to the layout of the document and
     * calculates the y position where the PDFPTable should be placed.
     *
     * @param pdfDataSource
     *          The PDF document.
     * @param pdf_table
     *          The PDFPTable to be placed.
     * @return Returns the position where the PDFPTable should be placed.
     * @throws PdfAsException
     *           F.e.
     */
    public static PositioningInstruction adjustSignatureTableandCalculatePosition(final PDDocument pdfDataSource,
        IPDFVisualObject pdf_table, TablePos pos, boolean legacy32) throws PdfAsException
    {

        try {
            PDFUtils.checkPDFPermissions(pdfDataSource);
            // get pages of currentdocument

            int doc_pages = pdfDataSource.getNumberOfPages();
            int page = doc_pages;
            boolean make_new_page = pos.isNewPage();
            if (!(pos.isNewPage() || pos.isPauto()))
            {
                // we should posit signaturtable on this page

                page = pos.getPage();
                // System.out.println("XXXXPAGE="+page+" doc_pages="+doc_pages);
                if (page > doc_pages)
                {
                    make_new_page = true;
                    page = doc_pages;
                    // throw new PDFDocumentException(227, "Page number is to big(=" + page+
                    // ") cannot be parsed.");
                }
            }

            PDPage pdPage = (PDPage)pdfDataSource.getDocumentCatalog().getAllPages().get(page - 1);
            PDRectangle cropBox = pdPage.getCropBox();

            if(cropBox == null) {
                cropBox = pdPage.findCropBox();
            }


            if(cropBox == null) {
                cropBox = pdPage.findMediaBox();
            }

            //TODO: fallback to MediaBox if Cropbox not available!

            // getPagedimensions
            //Rectangle psize = reader.getPageSizeWithRotation(page);
            //int page_rotation = reader.getPageRotation(page);

            //Integer rotation = pdPage.getRotation();
            //int page_rotation = rotation.intValue();

            float page_width = cropBox.getWidth();
            float page_height = cropBox.getHeight();

            // now we can calculate x-position
            float pre_pos_x = SIGNATURE_MARGIN_HORIZONTAL;
            if (!pos.isXauto())
            {
                // we do have absolute x
                pre_pos_x = pos.getPosX();
            }
            // calculate width
            // center
            float pre_width = page_width - 2*pre_pos_x;
            if (!pos.isWauto())
            {
                // we do have absolute width
                pre_width = pos.getWidth();
                if (pos.isXauto())
                { // center x
                    pre_pos_x = (page_width - pre_width) / 2;
                }
            }
            final float pos_x = pre_pos_x;
            final float width = pre_width;
            // Signatur table dimensions are complete
            pdf_table.setWidth(width);
            pdf_table.fixWidth();
            //pdf_table.setTotalWidth(width);
            //pdf_table.setLockedWidth(true);

            final float table_height = pdf_table.getHeight();
            // now check pos_y
            float pos_y = pos.getPosY();

            // in case an absolute y position is already given OR
            // if the table is related to an invisible signature
            // there is no need for further calculations
            // (fixed adding new page in case of invisible signatures)
            if (!pos.isYauto() || table_height == 0)
            {
                // we do have y-position too --> all parameters but page ok
                if (make_new_page)
                {
                    page++;
                }
                return new PositioningInstruction(make_new_page, page, pos_x, pos_y);
            }
            // pos_y is auto
            if (make_new_page)
            {
                // ignore footer in new page
                page++;
                pos_y = page_height - SIGNATURE_MARGIN_VERTICAL;
                return new PositioningInstruction(make_new_page, page, pos_x, pos_y);
            }
            // up to here no checks have to be made if Tablesize and Pagesize are fit
            // Now we have to getfreespace in page and reguard footerline
            float footer_line = pos.getFooterLine();
            float pre_page_length = PDFUtilities.calculatePageLength(pdfDataSource, page - 1, page_height - footer_line, /*page_rotation,*/ legacy32);
            if (pre_page_length == Float.NEGATIVE_INFINITY)
            {
                // we do have an empty page or nothing in area above footerline
                pre_page_length = page_height;
                // no text --> SIGNATURE_BORDER
                pos_y = page_height - SIGNATURE_MARGIN_VERTICAL;
                if (pos_y - footer_line <= table_height)
                {
                    make_new_page = true;
                    if (!pos.isPauto())
                    {
                        // we have to correct pagenumber
                        page = pdfDataSource.getNumberOfPages();
                    }
                    page++;
                    // no text --> SIGNATURE_BORDER
                    pos_y = page_height - SIGNATURE_MARGIN_VERTICAL;
                }
                return new PositioningInstruction(make_new_page, page, pos_x, pos_y);
            }
            final float page_length = pre_page_length;
            // we do have text take SIGNATURE_MARGIN
            pos_y = page_height - page_length - SIGNATURE_MARGIN_VERTICAL;
            if (pos_y - footer_line <= table_height)
            {
                make_new_page = true;
                if (!pos.isPauto())
                {
                    // we have to correct pagenumber in case of absolute page and not enough
                    // space
                    page = pdfDataSource.getNumberOfPages();
                }
                page++;
                // no text --> SIGNATURE_BORDER
                pos_y = page_height - SIGNATURE_MARGIN_VERTICAL;
            }
            return new PositioningInstruction(make_new_page, page, pos_x, pos_y);
        } finally {
            if (pdfDataSource != null) {
                try {
                    pdfDataSource.close();
                } catch (Exception e) {
                }
            }
        }
    }

}