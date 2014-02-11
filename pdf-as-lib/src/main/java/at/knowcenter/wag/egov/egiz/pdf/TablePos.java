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
 * $Id: TablePos.java,v 1.1 2006/08/25 17:10:08 wprinz Exp $
 */
package at.knowcenter.wag.egov.egiz.pdf;

import java.io.Serializable;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;

/**
 * Class that holds the exact position where the table should be written to the
 * document.
 * 
 * @author wprinz
 * @author mruhmer
 */
public class TablePos implements Serializable
{

  /**
   * SVUID.
   */
  private static final long serialVersionUID = -5299027706623518059L;

  /**
   * The page on which the block should be displayed.
   * 
   */
  private int page = 0;

  /**
   * The x position.
   */
  private float pos_x = 0.0f;

  /**
   * The y position.
   */
  private float pos_y = 0.0f;

  /**
   * The width of the block.
   */
  private float width = 0.0f;
  /**
   * The top y position of the footer line.
   */
  public float footer_line = 0.0f;
  
  /**
   * The rotation of the signature block
   */
  public float rotation = 0.0f;

  /**
   * The y position.
   */
  public String myposstring = "";
  
  private boolean newpage = false;
  private boolean autoX = true; 
  private boolean autoY = true;
  private boolean autoW = true;
  private boolean autoP = true;
  
  public boolean isXauto()
  {
	return this.autoX;
  }
  public boolean isYauto()
  {
	return this.autoY;
  } 
  public boolean isWauto()
  {
	return this.autoW;
  }
  public boolean isPauto()
  {
	return this.autoP;
  }
  public boolean isNewPage()
  {
	return this.newpage;
  }
  public int getPage()
  {
	return this.page;  
  }
  public float getFooterLine()
  {
	//ignore if newpage and y is not auto
	if (!this.autoY || this.newpage) 
	{
	  return 0.0f;
	}
	return this.footer_line;  
  } 
  public float getPosX()
  {
	return this.pos_x;  
  }  
  public float getPosY()
  {
	return this.pos_y;  
  } 
  public float getWidth()
  {
	return this.width;  
  }  
  public TablePos()
  {
    //nothing to do --> default
  } 
  
  /**
   * Constructor.
   * 
   * @param pos_string The pos instruction.
   *        format : [x:x_algo];[y:y_algo];[w:w_algo][p:p_algo];[f:f_algo];[r:r_algo]
   *        x_algo:='auto'     ... automatic positioning x
   *                floatvalue ... absolute x
   *        y_algo:='auto'     ... automatic positioning y
   *                floatvalue ... absolute y
   *        w_algo:='auto'     ... automatic width
   *                floatvalue ... absolute width    
   *        p_algo:='auto'     ... automatic last page
   *                'new'      ... new page  
   *                intvalue   ... pagenumber
   *        f_algo  floatvalue ... consider footerline (only if y_algo is auto and p_algo is not 'new')
   *        r_algo  floatvalue ... rotate the table arround the lower left corner anti clockwise in degree
   * @throws PdfAsException
   */
  public TablePos(String pos_string) throws PdfAsException
  {
    //parse posstring and throw exception
	//[x:x_algo];[y:y_algo];[w:w_algo][p:p_algo];[f:f_algo]
	
	String[] strs = pos_string.split(";");
	try
	{
	  for (int cmds = 0;cmds<strs.length;cmds++)
	  {
		 
		 String cmd_kvstring = strs[cmds];
		 String[] cmd_kv = cmd_kvstring.split(":");
		 if (cmd_kv.length != 2)
		 {
			 throw new PdfAsException("Pos string (=" + pos_string + ") is invalid.");
		 }
		 String cmdstr =  cmd_kv[0];
		 if (cmdstr.length() != 1)
		 {
			 throw new PdfAsException("Pos string (=" + pos_string + ") is invalid.");
		 }		 
		 char command = cmdstr.charAt(0);
	     String commandval= cmd_kv[1];
	     switch (command)
	     {
	     	case 'x': {
	     		         if (!commandval.equalsIgnoreCase("auto"))
	     		         {  
	     		        	float xval= Float.parseFloat(commandval);
	     		            if (xval<0)
	     		            {
	     		            	throw new PdfAsException("Pos string (x:" + xval + ") is invalid.");
	     		            }	     		          
	     		        	this.pos_x = xval;
	     		        	this.autoX = false; 
	     		         }	     		         
	     		         break;
	     			  }	
	     	case 'y': {
		         		if (!commandval.equalsIgnoreCase("auto"))
		         		{
		         			float yval= Float.parseFloat(commandval);
	     		            if (yval<0)
	     		            {
	     		            	throw new PdfAsException("Pos string (y:" + yval + ") is invalid.");
	     		            }			         			
		         			this.pos_y = yval;
		         			this.autoY = false; 
		         		}	     		         
		         		break;
	     			  }		
	     	case 'w': { 
         				if (!commandval.equalsIgnoreCase("auto"))
         				{    
		         			float wval= Float.parseFloat(commandval);
	     		            if (wval<=0)
	     		            {
	     		            	throw new PdfAsException("pos.width (w:" + wval + ") must not be lower or equal 0.");
	     		            }        					
         					this.width = wval;
         					this.autoW = false; 
         				}	     		         
         				break;
      				  }
	     	case 'p': {
 						if (!commandval.equalsIgnoreCase("auto"))
 						{ 
 							if (commandval.equalsIgnoreCase("new"))
 							{ 								
 								this.newpage = true;
 							}
 							else
 							{
 								int pval = Integer.parseInt(commandval);
 								if (pval<1)
 								{
 									throw new PdfAsException("Page (p:" + pval + ") must not be lower than 1.");
 								}
 								this.page = pval;
 								this.autoP = false;
 							}
 						}						     		       
 						break;
      				  }
	     	case 'f': {
	     		        float flval=Float.parseFloat(commandval);
     		            if (flval<0)
     		            {
     		            	throw new PdfAsException("Pos string (=" + pos_string + ") is invalid.");
     		            } 	     		        
	     				this.footer_line = flval;
	     				break;
	     			  }
	     	case 'r': {
 		        		float flval=Float.parseFloat(commandval);
 		        		if (flval<0)
 		        		{
 		        			throw new PdfAsException("Pos string (=" + pos_string + ") is invalid.");
 		        		} 	     		        
 		        		this.rotation = flval;
 		        		break;
 			  }
	     	default : {
		                throw new PdfAsException("Pos string (=" + pos_string + ") is invalid.");
	                  }
	     }
	  }
	  this.myposstring=pos_string;
    }
    catch (NumberFormatException e)
    {
      throw new PdfAsException("Pos string (=" + pos_string + ") cannot be parsed.");
    }
  }
  public String toString()
  {  
	 String thatsme = "cmd:"+this.myposstring+" pos_x:"+this.pos_x+" pos_y:"+this.pos_y+" page:"+this.page+" width:"+this.width+" footer:"+this.footer_line+" rotation:"+this.rotation+"\n "+" autoX:"+this.autoX+" autoY:"+this.autoY+" autoW:"+this.autoW+" Newpage:"+this.newpage+" autoP:"+this.autoP; 
	 return thatsme;
  }
}
