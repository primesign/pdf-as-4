/**
 * <copyright> Copyright 2006 by Know-Center, Graz, Austria </copyright>
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
 */
package at.gv.egiz.pdfas.api.sign.pos;

import java.io.Serializable;
import java.util.StringTokenizer;

import at.gv.egiz.pdfas.api.exceptions.ErrorCode;
import at.gv.egiz.pdfas.api.exceptions.PdfAsException;
import at.gv.egiz.pdfas.api.sign.pos.axis.AbsoluteAxisAlgorithm;
import at.gv.egiz.pdfas.api.sign.pos.axis.AutoAxisAlgorithm;
import at.gv.egiz.pdfas.api.sign.pos.axis.AxisAlgorithm;
import at.gv.egiz.pdfas.api.sign.pos.page.AbsolutePageAlgorithm;
import at.gv.egiz.pdfas.api.sign.pos.page.AutoPageAlgorithm;
import at.gv.egiz.pdfas.api.sign.pos.page.NewPageAlgorithm;
import at.gv.egiz.pdfas.api.sign.pos.page.PageAlgorithm;

/**
 * Defines how the signature positioning is to be performed.
 * 
 * <p>
 * This positioning allows to select the location where the signature block is
 * placed in the document.
 * </p>
 * 
 * @author wprinz
 */
public class SignaturePositioning implements Serializable
{
  
  /**
    * 
    */
  private static final long serialVersionUID = 1L;

  /**
   * The x axis algorithm.
   * 
   * <p>
   * May be {@link AutoAxisAlgorithm} or {@link AbsoluteAxisAlgorithm}
   * </p>
   */
  protected AxisAlgorithm xAlgorithm = new AutoAxisAlgorithm();

  /**
   * The y axis algorithm.
   * 
   * <p>
   * May be {@link AutoAxisAlgorithm} or {@link AbsoluteAxisAlgorithm}
   * </p>
   */
  protected AxisAlgorithm yAlgorithm = new AutoAxisAlgorithm();

  /**
   * The width algorithm.
   * 
   * <p>
   * May be {@link AutoAxisAlgorithm} or {@link AbsoluteAxisAlgorithm}
   * </p>
   */
  protected AxisAlgorithm widthAlgorithm = new AutoAxisAlgorithm();

  /**
   * The page algorithm.
   * 
   * <p>
   * May be {@link AutoPageAlgorithm}, {@link AbsolutePageAlgorithm} or
   * {@link NewPageAlgorithm}
   * </p>
   */
  protected PageAlgorithm pageAlgorithm = new AutoPageAlgorithm();

  /**
   * Provides the position of the footline.
   * 
   * <p>
   * Only used if the pageAlgorithm is {@link AutoPageAlgorithm} and the
   * yAlgorithm is {@link AutoAxisAlgorithm}
   * </p>
   */
  protected float footerLine = 0.0f;

  protected void checkAxisAlgorithm(AxisAlgorithm algorithm)
  {
    if (algorithm == null)
    {
      throw new IllegalArgumentException("The algorithm must not be null.");
    }
    if (!(algorithm instanceof AutoAxisAlgorithm) && !(algorithm instanceof AbsoluteAxisAlgorithm))
    {
      throw new IllegalArgumentException("The algorithm must be either Auto or Absolute.");
    }
  }

  protected void checkPageAlgorithm(PageAlgorithm algorithm)
  {
    if (algorithm == null)
    {
      throw new IllegalArgumentException("The algorithm must not be null.");
    }
    if (!(algorithm instanceof AutoPageAlgorithm) && !(algorithm instanceof AbsolutePageAlgorithm) && !(algorithm instanceof NewPageAlgorithm))
    {
      throw new IllegalArgumentException("The algorithm must be either Auto or Absolute.");
    }

  }

  /**
   * @return the xAlgorithm
   */
  public AxisAlgorithm getXAlgorithm()
  {
    return this.xAlgorithm;
  }

  /**
   * @param algorithm
   *          the xAlgorithm to set
   */
  public void setXAlgorithm(AxisAlgorithm algorithm)
  {
    checkAxisAlgorithm(algorithm);
    xAlgorithm = algorithm;
  }

  /**
   * @return the yAlgorithm
   */
  public AxisAlgorithm getYAlgorithm()
  {
    return this.yAlgorithm;
  }

  /**
   * @param algorithm
   *          the yAlgorithm to set
   */
  public void setYAlgorithm(AxisAlgorithm algorithm)
  {
    checkAxisAlgorithm(algorithm);

    yAlgorithm = algorithm;
  }

  /**
   * @return the widthAlgorithm
   */
  public AxisAlgorithm getWidthAlgorithm()
  {
    return this.widthAlgorithm;
  }

  /**
   * @param widthAlgorithm
   *          the widthAlgorithm to set
   */
  public void setWidthAlgorithm(AxisAlgorithm widthAlgorithm)
  {
    checkAxisAlgorithm(widthAlgorithm);

    this.widthAlgorithm = widthAlgorithm;
  }

  /**
   * @return the pageAlgorithm
   */
  public PageAlgorithm getPageAlgorithm()
  {
    return this.pageAlgorithm;
  }

  /**
   * @param pageAlgorithm
   *          the pageAlgorithm to set
   */
  public void setPageAlgorithm(PageAlgorithm pageAlgorithm)
  {
    checkPageAlgorithm(pageAlgorithm);
    this.pageAlgorithm = pageAlgorithm;
  }

  /**
   * @return the footerLine
   */
  public float getFooterLine()
  {
    return this.footerLine;
  }

  /**
   * @param footerLine
   *          the footerLine to set
   */
  public void setFooterLine(float footerLine)
  {
    this.footerLine = footerLine;
  }

   public SignaturePositioning() {
   }
   
   public SignaturePositioning(String position) throws PdfAsException {
      if (position != null) {
         StringTokenizer tokenizer = new StringTokenizer(position, ";");
         while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().replaceAll(" ", "");
            String[] sToken = token.split(":");
            if (sToken == null || sToken.length != 2 || sToken[0].length() != 1) {
               throw new PdfAsException(ErrorCode.INVALID_SIGNATURE_POSITION, "Invalid signature position element: " + token);               
            }
            char cmd = sToken[0].toLowerCase().charAt(0);
            String value = sToken[1];
            switch (cmd) {
            case 'x':
               if ("auto".equalsIgnoreCase(value)) {
                  this.setXAlgorithm(new AutoAxisAlgorithm());
               } else {
                  try {
                     this.setXAlgorithm(new AbsoluteAxisAlgorithm(Float.parseFloat(value)));
                  } catch (NumberFormatException e) {
                     throw new PdfAsException(ErrorCode.INVALID_SIGNATURE_POSITION, "Invalid signature position element: " + token);               
                  }
               }
               break;
            case 'y':
               if ("auto".equalsIgnoreCase(value)) {
                  this.setYAlgorithm(new AutoAxisAlgorithm());
               } else {
                  try {
                     this.setYAlgorithm(new AbsoluteAxisAlgorithm(Float.parseFloat(value)));
                  } catch (NumberFormatException e) {
                     throw new PdfAsException(ErrorCode.INVALID_SIGNATURE_POSITION, "Invalid signature position element: " + token);               
                  }
               }
               break;
            case 'w':
               if ("auto".equalsIgnoreCase(value)) {
                  this.setWidthAlgorithm(new AutoAxisAlgorithm());
               } else {
                  try {
                     this.setWidthAlgorithm(new AbsoluteAxisAlgorithm(Float.parseFloat(value)));
                  } catch (NumberFormatException e) {
                     throw new PdfAsException(ErrorCode.INVALID_SIGNATURE_POSITION, "Invalid signature position element: " + token);               
                  }
               }
               break;
            case 'p':
               if ("auto".equalsIgnoreCase(value)) {
                  this.setPageAlgorithm(new AutoPageAlgorithm());
               } else if ("new".equalsIgnoreCase(value)) {
                     this.setPageAlgorithm(new NewPageAlgorithm());
               } else {
                  try {
                     this.setPageAlgorithm(new AbsolutePageAlgorithm(Integer.parseInt(value)));
                  } catch (NumberFormatException e) {
                     throw new PdfAsException(ErrorCode.INVALID_SIGNATURE_POSITION, "Invalid signature position element: " + token);               
                  }
               }
               break;
            case 'f':
               try {
                  this.setFooterLine(Float.parseFloat(value));
               } catch (NumberFormatException e) {
                  throw new PdfAsException(ErrorCode.INVALID_SIGNATURE_POSITION, "Invalid signature position element: " + token);               
               }
               break;
            default:
               throw new PdfAsException(ErrorCode.INVALID_SIGNATURE_POSITION, "Invalid signature position element: " + token);               
            }
         }
      }
   }
   
   public String getPositionString() {
	   StringBuilder sb = new StringBuilder();
	   AxisAlgorithm xAlgo = getXAlgorithm();
	   
	   if(xAlgo instanceof AutoAxisAlgorithm) {
		   sb.append("x:auto;");
	   } else if(xAlgo instanceof AbsoluteAxisAlgorithm) {
		   sb.append("x:" + ((AbsoluteAxisAlgorithm)xAlgo).getAbsoluteValue() + ";");
	   }
	   
	   AxisAlgorithm yAlgo = getXAlgorithm();
	   
	   if(yAlgo instanceof AutoAxisAlgorithm) {
		   sb.append("y:auto;");
	   } else if(yAlgo instanceof AbsoluteAxisAlgorithm) {
		   sb.append("y:" + ((AbsoluteAxisAlgorithm)yAlgo).getAbsoluteValue() + ";");
	   }
	   
	   AxisAlgorithm wAlgo = getWidthAlgorithm();
	   
	   if(wAlgo instanceof AutoAxisAlgorithm) {
		   sb.append("w:auto;");
	   } else if(wAlgo instanceof AbsoluteAxisAlgorithm) {
		   sb.append("w:" + ((AbsoluteAxisAlgorithm)wAlgo).getAbsoluteValue() + ";");
	   }
	   
	   PageAlgorithm pAlgo = getPageAlgorithm();
	   
	   if(pAlgo instanceof AutoPageAlgorithm) {
		   sb.append("p:auto;");
	   } else if(pAlgo instanceof NewPageAlgorithm) {
		   sb.append("p:new;");
	   }
	   
	   float footerLine = getFooterLine();
	   
	   sb.append("f:" + + footerLine);
	   
	   return sb.toString();
   }
  

}
