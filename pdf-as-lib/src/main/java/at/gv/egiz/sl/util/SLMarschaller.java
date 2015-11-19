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
package at.gv.egiz.sl.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SLMarschaller {
	private static Marshaller marshaller = null;
	private static Unmarshaller unmarshaller = null;

	private static final Logger logger = LoggerFactory
			.getLogger(SLMarschaller.class);
	
	static {
		try {
			JAXBContext context = JAXBContext.newInstance("at.gv.egiz.sl.schema");
			marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

			unmarshaller = context.createUnmarshaller();
		} catch (JAXBException e) {
			logger.error("Error in creating JAXBContext", e);
			throw new RuntimeException(
					"There was a problem creating a JAXBContext object for formatting the object to XML.");
		}
	}

	public static void marshal(Object obj, OutputStream os) throws JAXBException {
		marshaller.marshal(obj, os);
	}

	public static String marshalToString(Object obj) throws JAXBException {
		StringWriter sw = new StringWriter();
		marshaller.marshal(obj, sw);
		return sw.toString();
	}
	
	public static Object unmarshal(InputStream is) throws JAXBException {
		XMLInputFactory xif = XMLInputFactory.newFactory();
        xif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        xif.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        XMLStreamReader xmlStreamReader;
		try {
			xmlStreamReader = xif.createXMLStreamReader(is);
			return unmarshaller.unmarshal(xmlStreamReader);
		} catch (XMLStreamException e) {
			throw new JAXBException(e);
		}
		
	}
	
	public static Object unmarshalFromString(String message) throws JAXBException {
		StringReader sr = new StringReader(message);
		XMLInputFactory xif = XMLInputFactory.newFactory();
        xif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        xif.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        XMLStreamReader xmlStreamReader;
		try {
			xmlStreamReader = xif.createXMLStreamReader(sr);
			return unmarshaller.unmarshal(xmlStreamReader);
		} catch (XMLStreamException e) {
			throw new JAXBException(e);
		}
	}
}
