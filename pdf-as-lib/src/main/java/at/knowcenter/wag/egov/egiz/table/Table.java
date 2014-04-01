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
 * $Id: Table.java,v 1.2 2006/08/25 17:08:19 wprinz Exp $
 */
package at.knowcenter.wag.egov.egiz.table;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * This class implements an abstract table definition. The table contains table
 * rows and the table rows contains the table entries. A table can be styled and
 * a relative column width can be set.
 * 
 * @author wlackner
 * @see Style
 * @see at.knowcenter.wag.egov.egiz.table.Entry
 */
public class Table implements Serializable
{

  /**
   * SVUID.
   */
  private static final long serialVersionUID = 8488947943674086618L;

  /**
   * The table column settings.
   */
  private float[] colsRelativeWith_ = null;

  /**
   * The row definitions.
   */
  private Map<String, ArrayList<Entry>> rows_ = new HashMap<String, ArrayList<Entry>>();

  /**
   * The table width.
   */
  private float width_ = 100;

  /**
   * The table style.
   */
  private Style style_ = null;

  /**
   * Number of columns that are defined for the current table.
   */
  private int maxCols_ = 0;

  /**
   * A table name.
   */
  private String name_ = null;

  /**
   * The table constructor init by a table name.
   * 
   * @param name
   *          the name for the table.
   */
  public Table(String name)
  {
    name_ = name;
  }

  /**
   * The width of the columns are relative to each other. This means the values
   * are summarized and divided into portions of columns used. <br>
   * Example: <code>[1,4]</code> means the second column is four times wider
   * than the first column.
   * 
   * @return Returns the relative width of the columns
   */
  public float[] getColsRelativeWith()
  {
    return colsRelativeWith_;
  }

  /**
   * The width of the columns are relative to each other. This means the values
   * are summarized and divided into portions of columns used. <br>
   * Example: <code>[10,90]</code> means the first colum consumes 10% and the
   * second column consumes 90% of the table width. <br>
   * The relative width of the columns to set.
   */
  public void setColsRelativeWith(float[] cols)
  {
    colsRelativeWith_ = cols;
  }

  /**
   * @return Returns the style.
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
   * @return Returns the width.
   */
  public float getWidth()
  {
    return width_;
  }

  /**
   * @param width
   *          The width to set.
   */
  public void setWidth(float width)
  {
    width_ = width;
  }

  /**
   * @return Returns the maxCols.
   */
  public int getMaxCols()
  {
    return maxCols_;
  }

  /**
   * @return Returns the name.
   */
  public String getName()
  {
    return name_;
  }

  /**
   * This method returns a sorted row list beginning with the row number 1. The
   * entrys in a row also stored in a <code>{@link java.util.ArrayList}</code>.
   * 
   * @return Returns the sorted (by row number) table rows.
   */
  public ArrayList<ArrayList<Entry>> getRows()
  {
    ArrayList<ArrayList<Entry>> rows = new ArrayList<ArrayList<Entry>>();
    for (int row_idx = 1; row_idx <= rows_.size(); row_idx++)
    {
      ArrayList<Entry> row = (ArrayList<Entry>) rows_.get("" + row_idx);
      rows.add(row);
    }
    return rows;
  }

  /**
   * Add a comlete table row to the current table. Be carefull usding the
   * correct row number because no check is done if a row with the given row
   * number does exist! In that case the stored row would be replaced!
   * 
   * @param rowNumber
   *          the row number to store the row entries
   * @param row
   *          the entry list to store
   */
  public void addRow(String rowNumber, ArrayList<Entry> row)
  {
    rows_.put(rowNumber, row);
    if (row.size() > maxCols_)
    {
      maxCols_ = row.size();
    }
  }

  /**
   * The toString method, used for tests or debugging.
   */
  public String toString()
  {
    String the_string = "\n#### TABLE " + name_ + " BEGIN #####";
    the_string += " Width:" + width_ + " max cols:" + maxCols_ + " cols:" + colsRelativeWith_;
    the_string += "\nStyle:" + style_;
    ArrayList<ArrayList<Entry>> rows = getRows();
    for (int row_idx = 0; row_idx < rows.size(); row_idx++)
    {
      ArrayList<Entry> row = rows.get(row_idx);
      String row_prefix = "\n ++ ROW " + row_idx + " ++ ";
      for (int entry_idx = 0; entry_idx < row.size(); entry_idx++)
      {
        the_string += row_prefix + ((Entry) row.get(entry_idx)).toString();
      }
    }
    the_string += "\n#### TABLE " + name_ + " END #####";
    return the_string;
  }
}
