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
package at.gv.egiz.pdfas.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.PDFIOException;

public class PDFUtils {

	private static final Logger logger = LoggerFactory
			.getLogger(PDFUtils.class);

	private static final byte[] signature_pattern = new byte[] { (byte) 0x0A,
			(byte) 0x2F, (byte) 0x43, (byte) 0x6F, // ./Co
			(byte) 0x6E, (byte) 0x74, (byte) 0x65, (byte) 0x6E, // nten
			(byte) 0x74, (byte) 0x73, (byte) 0x20, (byte) 0x0A, // ts .
			(byte) 0x2F, (byte) 0x42, (byte) 0x79, (byte) 0x74, // /Byt
			(byte) 0x65, (byte) 0x52, (byte) 0x61, (byte) 0x6E, // eRan
			(byte) 0x67, (byte) 0x65, (byte) 0x20, (byte) 0x5B, // ge [

	};

	private static final byte range_seperation = (byte) 0x20;
	private static final byte range_end = (byte) 0x5D;

	public static int[] buildExcludeRange(int[] byteRange) throws PDFIOException {
		
		if(byteRange.length % 2 != 0) {
			throw new PDFIOException("error.pdf.io.09");
		}
	
		int[] exclude_range = new int[byteRange.length-2];
		
		for(int i = 0; i < byteRange.length; i = i + 2) {
			int offset = byteRange[i];
			int size = byteRange[i+1];
			if(i + 2 < byteRange.length) {
				exclude_range[i] = offset + size; // exclude start
				exclude_range[i+1] = byteRange[i+2] - 1; // exclude end
			}
		}
		return exclude_range;
	}
	
	public static byte[] blackOutSignature(byte[] signatureData, int[] byteRange) throws PDFIOException {
		if(byteRange.length % 2 != 0) {
			throw new PDFIOException("error.pdf.io.09");
		}
		
		int lastOffset = byteRange[byteRange.length - 2];
		int lastSize = byteRange[byteRange.length - 1];
		
		int dataSize = lastOffset + lastSize;
		
		byte[] data = new byte[dataSize];
		int currentdataOff = 0;
		
		Arrays.fill(data, (byte)0x0);
		
		for(int i = 0; i < byteRange.length; i = i + 2) {
			int offset = byteRange[i];
			int size = byteRange[i+1];
			
			for(int j = 0; j < size; j++) {
				data[offset + j] = signatureData[currentdataOff];
				currentdataOff++;
			}
		}
		return data;
	}
	
	public static byte[] extractSignatureData(byte[] signatureData, int[] byteRange) throws PDFIOException {
		if(byteRange.length % 2 != 0) {
			throw new PDFIOException("error.pdf.io.09");
		}
		
		int lastOffset = byteRange[byteRange.length - 2];
		int lastSize = byteRange[byteRange.length - 1];
		
		int dataSize = lastOffset + lastSize;
		
		byte[] data = new byte[dataSize];
		int currentdataOff = 0;
		
		Arrays.fill(data, (byte)0x0);
		
		for(int i = 0; i < byteRange.length; i = i + 2) {
			int offset = byteRange[i];
			int size = byteRange[i+1];
			
			for(int j = 0; j < size; j++) {
				data[offset + j] = signatureData[currentdataOff];
				currentdataOff++;
			}
		}
		return data;
	}
	
	private static int extractASCIIInteger(byte[] data, int offset) {
		int nextsepp = nextSeperator(data, offset);

		if (nextsepp < offset) {
			return -1;
		}

		String asciiString = new String(data, offset, nextsepp - offset);

		logger.debug("Extracted " + asciiString);

		return Integer.parseInt(asciiString);
	}

	private static int nextSeperator(byte[] data, int offset) {
		for (int i = offset; i < data.length; i++) {
			if (data[i] == range_seperation) {
				return i;
			} else if (data[i] == range_end) {
				return i;
			}
		}
		return -2;
	}

	public static int[] extractSignatureByteRange(byte[] rawPdfData) {
		int i = 0;
		for (i = rawPdfData.length - 1; i >= 0; i--) {
			if (rawPdfData[i] == signature_pattern[0]
					&& i + signature_pattern.length < rawPdfData.length) {
				boolean match = true;
				for (int j = 0; j < signature_pattern.length; j++) {

					if (rawPdfData[i + j] != signature_pattern[j]) {
						match = false;
						break;
					}
				}

				if (match) {

					int offset = i + signature_pattern.length;
					List<Integer> byteRange = new ArrayList<Integer>();
					while (offset > 0) {
						byteRange.add(extractASCIIInteger(rawPdfData, offset));
						offset = nextSeperator(rawPdfData, offset);
						if (rawPdfData[offset] == range_end) {
							break;
						}
						offset++;
					}
					int[] range = new int[byteRange.size()];
					for (int j = 0; j < byteRange.size(); j++) {
						range[j] = byteRange.get(j);
					}
					return range;
				}
			}
		}
		return null;
	}
}
