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
 * $Id: Entry.java,v 1.3 2006/08/25 17:08:19 wprinz Exp $
 */
package at.knowcenter.wag.egov.egiz.table;

import java.io.Serializable;

/**
 * This class implements a table entry for different types. A table entry can be
 * styled and setting there column dimensions. The default value for the column
 * dimension is 1. To declare the type of the entry use the public
 * <code>TYPE_</code> definitions.
 * 
 * @author wlackner
 */
public class Entry implements Serializable
{

  /**
   * SVUID.
   */
  private static final long serialVersionUID = -7952755200668528348L;

  /**
   * Type for a text entry.
   */
  public final static int TYPE_CAPTION = 0;

  /**
   * Type for a text entry.
   */
  public final static int TYPE_VALUE = 1;

  /**
   * Type for an image entry.
   */
  public final static int TYPE_IMAGE = 2;

  /**
   * Type for a table entry.
   */
  public final static int TYPE_TABLE = 3;

  /**
   * The type info holder, default value is 0!
   */
  private int type_ = 0;

  /**
   * The entry value.
   */
  private Object value_ = null;

  /**
   * The key value
   */
  private String key_ = null;

  /**
   * The entry style information.
   */
  private Style style_ = null;

  /**
   * The column dimension.
   */
  private int colSpan_ = 1;

  /**
   * Text wrap indicator, default is <code>false</code>.
   */
  private boolean noWrap_ = false;

  /**
   * The empty constructor.
   */
  public Entry()
  {
  }

  /**
   * A constructor setting the type and the value.
   * 
   * @param type
   *          the entry type to set
   * @param value
   *          the entry value to set
   */
  public Entry(int type, Object value, String key)
  {
    type_ = type;
    value_ = value;
    key_ = key;
  }

  /**
   * @return Returns the entry style.
   */
  public Style getStyle()
  {
    return style_;
  }

  /**
   * @param style
   *          The style to set.
   */
  public void setStyle(Style style)
  {
    style_ = style;
  }

  /**
   * @return Returns the entry type.
   */
  public int getType()
  {
    return type_;
  }

  /**
   * @param type
   *          The type to set.
   */
  public void setType(int type)
  {
    type_ = type;
  }

  /**
   * @return Returns the entry value.
   */
  public Object getValue()
  {
    return value_;
  }

  /**
   * @param value
   *          The value to set.
   */
  public void setValue(Object value)
  {
    value_ = value;
  }

  /**
   * @return Returns the key.
   */

  public String getKey()
  {
    return key_;
  }

  /**
   * @param key
   *          The key to set.
   */
  public void setKey(String key)
  {
    key_ = key;
  }

  /**
   * @return Returns the colSpan.
   */
  public int getColSpan()
  {
    return colSpan_;
  }

  /**
   * @param colSpan
   *          The colSpan to set.
   */
  public void setColSpan(int colSpan)
  {
    colSpan_ = colSpan;
  }

  /**
   * @return Returns the wrap indicator.
   */
  public boolean isNoWrap()
  {
    return noWrap_;
  }

  /**
   * @param noWrap
   *          The wrap indicator to set.
   */
  public void setNoWrap(boolean noWrap)
  {
    noWrap_ = noWrap;
  }

  /**
   * The toString method, used for tests or debugging.
   */
  public String toString()
  {
    Object obj = getValue();
    String value = null;
    if (obj != null)
    {
      value = obj.toString();
    }
    return "Type:" + getType() + " Value:" + value + " ColSpan:" + getColSpan();
  }

}