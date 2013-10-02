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
package at.gv.egiz.pdfas.api.analyze;

/**
 * Encapsulates information about non textual objects in a pdf document.
 * 
 * @author dferbas
 *
 */
public class NonTextObjectInfo {
   public static final String TYPE_IMAGE = "image";
   public static final String TYPE_ANNOTATION = "annotation";
   
   private String objectType;
   private String subType;
   private String name;
   private int pageNr;
   private double width;
   private double height;
   
   public String getObjectType() {
      return this.objectType;
   }
   
   public void setObjectType(String objectType) {
      this.objectType = objectType;
   }
   
   public String getName() {
      return this.name;
   }
   
   public void setName(String name) {
      this.name = name;
   }
   
   public int getPageNr() {
      return this.pageNr;
   }
   
   public void setPageNr(int pageNr) {
      this.pageNr = pageNr;
   }
   
   public double getWidth() {
      return this.width;
   }
   
   public void setWidth(double width) {
      this.width = width;
   }
   
   public double getHeight() {
      return this.height;
   }
   
   public void setHeight(double height) {
      this.height = height;
   }
   
   public String getSubType() {
      return this.subType;
   }

   public void setSubType(String subType) {
      this.subType = subType;
   }
   
   

   public String toString() {
      return "NonTextObjectInfo [height=" + this.height + ", name=" + this.name + ", objectType="
            + this.objectType + ", pageNr=" + this.pageNr + ", subType=" + this.subType
            + ", width=" + this.width + "]";
   }  

}
