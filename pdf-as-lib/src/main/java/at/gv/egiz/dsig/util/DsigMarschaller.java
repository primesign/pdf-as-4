package at.gv.egiz.dsig.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

public class DsigMarschaller {
	private static Marshaller marshaller = null;
	private static Unmarshaller unmarshaller = null;
	
	public final static QName X509DataTypeX509Certificate_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", "X509Certificate");
	
	static {
		try {
			JAXBContext context = JAXBContext.newInstance("at.gv.egiz.dsig");
			marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

			unmarshaller = context.createUnmarshaller();
		} catch (JAXBException e) {
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
		return unmarshaller.unmarshal(is);
	}
	
	public static Object unmarshalFromString(String message) throws JAXBException {
		StringReader sr = new StringReader(message);
		return unmarshaller.unmarshal(sr);
	}
}
