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
 *
 * $Id: Style.java,v 1.3 2006/08/25 17:08:19 wprinz Exp $
 */
package at.knowcenter.wag.egov.egiz.table;

import java.awt.Color;
import java.io.Serializable;

/**
 * This class implements an abstract style definiton used in tables or table entrys. Predefined
 * values exists for valign and halign. Color definitions uses the native awt color declarations.
 * <br>
 * The predefined keys are used in the setting definition file to style tables and table entries.
 * <br>
 * It provides an static method to inherit style informations from a given style object.
 * {@link Style#doInherit}
 * 
 * 
 * @author wlackner
 * @see java.awt.Color
 */
public class Style implements Serializable {

// 03.11.2010 changed by exthex - added valuevalign and valuehalign to allow separate layout for value and non-value cells.
// Also the hardcoded default values for halign and valign were removed to allow proper inheritment.
// 04.11.2010 changed by exthex - added imagevalign and imagehalign analog to valuevalign/valuehalign

  /**
   * SVUID.
   */
  private static final long serialVersionUID = 5855722896712428387L;
  
  /**
   * valign statement key top
   */
  public final static String TOP = "top";
  /**
   * valign statement key middle
   */
  public final static String MIDDLE = "middle";
  /**
   * valign statement key bottom
   */
  public final static String BOTTOM = "bottom";
  /**
   * halign statement key left
   */
  public final static String LEFT = "left";
  /**
   * halign statement key center
   */
  public final static String CENTER = "center";
  /**
   * halign statement key right
   */
  public final static String RIGHT = "right";

  /**
   * bgcolor key
   */
  public final static String BGCOLOR = "bgcolor";
  /**
   * halign key
   */
  public final static String HALIGN = "halign";
  /**
   * valign key
   */
  public final static String VALIGN = "valign";

  /**
   * value halign key
   */
  public final static String VALUEHALIGN = "valuehalign";
  /**
   * value valign key
   */
  public final static String VALUEVALIGN = "valuevalign";

  /**
   * image halign key
   */
  public final static String IMAGEHALIGN = "imagehalign";
  /**
   * image valign key
   */
  public final static String IMAGEVALIGN = "imagevalign";
  /**
   * padding key, default padding = 1
   */
  public final static String PADDING = "padding";
  /**
   * border key, default border = 1;<br>
   * The border value is one value for all border lines of an entry or table! <br>
   * No separte definitions for top, right, bottom or left are possible.
   */
  public final static String BORDER = "border";
  
  /**
   * Font key
   */
  public final static String FONT = "font";
  
  /**
   * The value font key.
   */
  public final static String VALUEFONT = "valuefont";
  
  /**
   * The imageScaleToFit key.
   */
  public final static String IMAGE_SCALE_TO_FIT = "imagescaletofit";
  
  /**
   * Font name HELVETICA
   */
  public final static String HELVETICA = "HELVETICA";
  /**
   * Font name TIMES_ROMAN
   */
  public final static String TIMES_ROMAN = "TIMES_ROMAN";
  /**
   * Font name COURIER
   */
  public final static String COURIER = "COURIER";
  /**
   * Font type NORMAL
   */
  public final static String NORMAL = "NORMAL";
  /**
   * Font type BOLD
   */
  public final static String BOLD = "BOLD";
  /**
   * Font type ITALIC
   */
  public final static String ITALIC = "ITALIC";
  /**
   * Font type BOLDITALIC
   */
  public final static String BOLDITALIC = "BOLDITALIC";
  /**
   * Font type UNDERLINE
   */
  public final static String UNDERLINE = "UNDERLINE";
  /**
   * Font type STRIKETHRU
   */
  public final static String STRIKETHRU = "STRIKETHRU";
  

  /**
   * all paddings initialized with the default padding value (1)
   */
  private static final float DEFAULT_PADDING = 1;
  /**
   * all borders initialized with the default border value (1)
   */
  private static final float DEFAULT_BORDER = 1;
  /**
   * The background color definition.
   */
  private Color bgColor_ = null;
  /**
   * The current padding value -> initialized with the default padding value
   */
  private float padding_ = DEFAULT_PADDING;
  /**
   * The current halign value
   */
  private String hAlign_ = null;
  /**
   * The current valign value
   */
  private String vAlign_ = null;
  /**
   * The current valuehalign value
   */
  private String valueHAlign_ = null;
  /**
   * The current valuevalign value
   */
  private String valueVAlign_ = null;
  /**
   * The current imagehalign value
   */
  private String imageHAlign_ = null;
  /**
   * The current imagevalign value
   */
  private String imageVAlign_ = null;
  /**
   * The current border value -> initialized with the default border value
   */
  private float border_ = DEFAULT_BORDER;
  /**
   * The font string of the style definition
   */
  private String font_ = null;
  /**
   * The font string of the value font.
   */
  private String valuefont_ = null;
  /**
   * The scaleToFit dimensions to be applied for image-cells.
   */
  private ImageScaleToFit imageScaleToFit_ = null;

  /**
   * The empty constructor.
   */
  public Style() {
  }

  /**
   * Set a style attribute. The style attribute must be one of the public definitions
   * 
   * @param id the style attribute to set
   * @param value the style value to set for the given attribute
   */
  public void setStyle(String id, String value) {
    if (BGCOLOR.equals(id)) {
      String[] col_strg = value.split(" ");
      if (col_strg.length == 3) {
        int r = Integer.parseInt(col_strg[0]);
        int g = Integer.parseInt(col_strg[1]);
        int b = Integer.parseInt(col_strg[2]);
        if (r < 256 && g < 256 && b < 256 && r >= 0 && g >= 0 && b >= 0) {
          bgColor_ = new Color(r, g, b);
        }
      }
    }
    if (HALIGN.equals(id)) {
      if (LEFT.equals(value) || CENTER.equals(value) || RIGHT.equals(value)) {
        hAlign_ = value;
      }
    }
    if (VALIGN.equals(id)) {
      if (TOP.equals(value) || MIDDLE.equals(value) || BOTTOM.equals(value)) {
        vAlign_ = value;
      }
    }
    if (VALUEHALIGN.equals(id)) {
      if (LEFT.equals(value) || CENTER.equals(value) || RIGHT.equals(value)) {
        valueHAlign_ = value;
      }
    }
    if (VALUEVALIGN.equals(id)) {
      if (TOP.equals(value) || MIDDLE.equals(value) || BOTTOM.equals(value)) {
        valueVAlign_ = value;
      }
    }
    if (IMAGEHALIGN.equals(id)) {
      if (LEFT.equals(value) || CENTER.equals(value) || RIGHT.equals(value)) {
        imageHAlign_ = value;
      }
    }
    if (IMAGEVALIGN.equals(id)) {
      if (TOP.equals(value) || MIDDLE.equals(value) || BOTTOM.equals(value)) {
        imageVAlign_ = value;
      }
    }
    if (PADDING.equals(id)) {
      padding_ = Float.parseFloat(value);
    }
    if (BORDER.equals(id)) {
      border_ = Float.parseFloat(value);
    }
    if (FONT.equals(id)) {
      font_ = value;
    }
    if (VALUEFONT.equals(id)) {
      valuefont_ = value;
    }
    if (IMAGE_SCALE_TO_FIT.equals(id))
    {
      imageScaleToFit_ = parseImageScaleToFit(value);
    }
  }

  /**
   * @return Returns the bgColor.
   */
  public Color getBgColor() {
    return bgColor_;
  }

  /**
   * @param bgColor The bgColor to set.
   */
  public void setBgColor(Color bgColor) {
    bgColor_ = bgColor;
  }

  /**
   * @return Returns the hAlign.
   */
  public String getHAlign() {
    return hAlign_;
  }

  /**
   * @param align The hAlign to set.
   */
  public void setHAlign(String align) {
    hAlign_ = align;
  }

  /**
   * @return Returns the padding.
   */
  public float getPadding() {
    return padding_;
  }

  /**
   * @param padding The padding to set.
   */
  public void setPadding(float padding) {
    padding_ = padding;
  }

  /**
   * @return Returns the vAlign.
   */
  public String getVAlign() {
    return vAlign_;
  }

  /**
   * @param align The vAlign to set.
   */
  public void setVAlign(String align) {
    vAlign_ = align;
  }

  /**
   * @return Returns the border.
   */
  public float getBorder() {
    return border_;
  }

  /**
   * @param border The border to set.
   */
  public void setBorder(float border) {
    border_ = border;
  }

  
  /**
   * @return Returns the font.
   */
  public String getFont() {
    return font_;
  }
  
  /**
   * @param font The font to set.
   */
  public void setFont(String font) {
    font_ = font;
  }
  
  
  /**
   * Returns the value font.
   * @return Returns the value font.
   */
  public String getValueFont()
  {
    return valuefont_;
  }

  /**
   * Sets the value font.
   * @param valuefont The value font to be set.
   */
  public void setValueFont(String valuefont)
  {
    this.valuefont_ = valuefont;
  }
  
  /**
   * @param align The valueHAlign to set.
   */
  public void setValueHAlign(String align) {
    valueHAlign_ = align;
  }

  /**
   * Returns the value halign
   * @return Returns the value halign
   */
  public String getValueHAlign() {
    return valueHAlign_;
  }

  /**
   * @param align The valueVAlign to set.
   */
  public void setValueVAlign(String align) {
    valueVAlign_ = align;
  }

  /**
   * Returns the value valign
   * @return Returns the value valign
   */
  public String getValueVAlign() {
    return valueVAlign_;
  }
  
  /**
   * @param align The imageHAlign to set.
   */
  public void setImageHAlign(String align) {
    imageHAlign_ = align;
  }

  /**
   * Returns the image halign
   * @return Returns the image halign
   */
  public String getImageHAlign() {
    return imageHAlign_;
  }

  /**
   * @param align The imageVAlign to set.
   */
  public void setImageVAlign(String align) {
    imageVAlign_ = align;
  }

  /**
   * Returns the image valign
   * @return Returns the image valign
   */
  public String getImageVAlign() {
    return imageVAlign_;
  }

  /**
   * Returns the scaleToFit dimensions to be applied for image-cells.
   * @return Returns the scaleToFit dimensions to be applied for image-cells.
   */
  public ImageScaleToFit getImageScaleToFit()
  {
    return this.imageScaleToFit_;
  }

  /**
   * Sets the scaleToFit dimensions to be applied for image-cells.
   * @param imageScaleToFit_ The scaleToFit dimensions to be applied for image-cells.
   */
  public void setImageScaleToFit(ImageScaleToFit imageScaleToFit)
  {
    this.imageScaleToFit_ = imageScaleToFit;
  }

  /**
   * The toString method, used for tests or debugging.
   */
  public String toString() {
    return "bgcolor:" + getBgColor() + " halign:" + getHAlign() + " valign:" + getVAlign() + " padding:" + getPadding() + " border:" + getBorder() + " font:" + getFont() + " valuefont:" + getValueFont() + " imageScaleToFit:" + getImageScaleToFit();
  }

  /**
   * This method inherits all style attributes (values) from a given style object.
   * 
   * <p>
   * A new style object is created that receives the properly inherited styles.
   * </p>
   * <p>
   * If a value is not defined in the <code>baseStyle</code> object it would be inhert from the <code>inheritStyle</code> object.
   * </p>
   * 
   * @param baseStyle the style object that serves as a primary style source.
   * @param inheritStyle the style object that serves as a secondary style source in case a style attribute is not defined on the primary style source. 
   * @param isValue 
   * @return Returns a new Style object being fully equipped with styles.
   */
  public static Style doInherit(Style baseStyle, Style inheritStyle) {
    Style newStyle = new Style();
    
    if (baseStyle != null)
    {
      newStyle.setBgColor(baseStyle.getBgColor());
      newStyle.setBorder(baseStyle.getBorder());
      newStyle.setFont(baseStyle.getFont());
      newStyle.setHAlign(baseStyle.getHAlign());
      newStyle.setImageHAlign(baseStyle.getImageHAlign());
      newStyle.setImageVAlign(baseStyle.getImageVAlign());
      newStyle.setPadding(baseStyle.getPadding());
      newStyle.setVAlign(baseStyle.getVAlign());
      newStyle.setValueFont(baseStyle.getValueFont());
      newStyle.setValueHAlign(baseStyle.getValueHAlign());
      newStyle.setValueVAlign(baseStyle.getValueVAlign());
      newStyle.setImageScaleToFit(baseStyle.getImageScaleToFit());
    }
    
    if (inheritStyle != null)
    {
      if (newStyle.getBgColor() == null) { newStyle.setBgColor(inheritStyle.getBgColor()); }
      if (newStyle.getBorder() == DEFAULT_BORDER) { newStyle.setBorder(inheritStyle.getBorder()); }
      if (newStyle.getFont() == null) { newStyle.setFont(inheritStyle.getFont()); }
      if (newStyle.getHAlign() == null) { newStyle.setHAlign(inheritStyle.getHAlign()); }
      if (newStyle.getImageHAlign() == null) { newStyle.setImageHAlign(inheritStyle.getImageHAlign()); }
      if (newStyle.getImageVAlign() == null) { newStyle.setImageVAlign(inheritStyle.getImageVAlign()); }
      if (newStyle.getPadding() == DEFAULT_PADDING) { newStyle.setPadding(inheritStyle.getPadding()); }
      if (newStyle.getVAlign() == null) { newStyle.setVAlign(inheritStyle.getVAlign()); }
      if (newStyle.getValueFont() == null) { newStyle.setValueFont(inheritStyle.getValueFont()); }
      if (newStyle.getValueHAlign() == null) { newStyle.setValueHAlign(inheritStyle.getValueHAlign()); }
      if (newStyle.getValueVAlign() == null) { newStyle.setValueVAlign(inheritStyle.getValueVAlign()); }
      if (newStyle.getImageScaleToFit() == null) { newStyle.setImageScaleToFit(inheritStyle.getImageScaleToFit()); }
   }
    
   return newStyle;
  }
  
  protected static ImageScaleToFit parseImageScaleToFit (String imageScaleToFit)
  {
    if (imageScaleToFit == null || imageScaleToFit.length() == 0 || imageScaleToFit.trim().length() == 0)
    {
      return null;
    }
    
    String [] dimensions = imageScaleToFit.split(";");
    if (dimensions.length != 2)
    {
      return null;
    }
    
    float width = Float.parseFloat(dimensions[0]);
    float height = Float.parseFloat(dimensions[0]);
    
    return new ImageScaleToFit(width, height);
  }
  
  /**
   * Holds the width and the height an image can be scaled to fit.
   * 
   * @author wprinz
   */
  public static class ImageScaleToFit
  {
    /**
     * The width.
     */
    protected float width;
    
    /**
     * The height.
     */
    protected float height;

    /**
     * Constructor.
     * 
     * @param width The width.
     * @param height The height.
     */
    public ImageScaleToFit(float width, float height)
    {
      this.width = width;
      this.height = height;
    }

    /**
     * Returns the width.
     * @return Returns the width.
     */
    public float getWidth()
    {
      return this.width;
    }

    /**
     * Sets the width.
     * @param width The width to set.
     */
    public void setWidth(float width)
    {
      this.width = width;
    }

    /**
     * Returns the height.
     * @return Returns the height.
     */
    public float getHeight()
    {
      return this.height;
    }

    /**
     * Sets the height.
     * @param height The height to set.
     */
    public void setHeight(float height)
    {
      this.height = height;
    }
     
  }
}