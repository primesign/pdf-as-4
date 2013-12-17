package at.gv.egiz.pdfas.common.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DNUtils {
	private static final Logger logger = LoggerFactory.getLogger(DNUtils.class);


    public static Map<String, String> dnToMap(String dn) throws InvalidNameException {
        Map<String, String> map = new HashMap<String, String>();

        LdapName ldapName = new LdapName(dn);

        Iterator<Rdn> rdnIterator = ldapName.getRdns().iterator();

        while(rdnIterator.hasNext()) {
            Rdn rdn = rdnIterator.next();

            logger.debug(rdn.getType() + " = " + rdn.getValue().toString());
            map.put(rdn.getType(), rdn.getValue().toString());
        }
        
        map.put("DN", dn);
        logger.debug("DN = " + dn);

        return map;
    }
}
