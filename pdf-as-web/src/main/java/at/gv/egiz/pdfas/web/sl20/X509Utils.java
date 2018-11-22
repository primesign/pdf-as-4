package at.gv.egiz.pdfas.web.sl20;

import java.security.cert.X509Certificate;
import java.util.List;

import javax.security.auth.x500.X500Principal;

public class X509Utils {

	  /**
	   * Sorts the Certificate Chain by IssuerDN and SubjectDN. The [0]-Element should be the Hostname,
	   * the last Element should be the Root Certificate.
	   * 
	   * @param certs
	   *          The first element must be the correct one.
	   * @return sorted Certificate Chain
	   */
	  public static List<X509Certificate> sortCertificates(
		      List<X509Certificate> certs)
		  {
		    int length = certs.size();
		    if (certs.size() <= 1)
		    {
		      return certs;
		    }

		    for (X509Certificate cert : certs)
		    {
		      if (cert == null)
		      {
		        throw new NullPointerException();
		      }
		    }

		    for (int i = 0; i < length; i++)
		    {
		      boolean found = false;
		      X500Principal issuer = certs.get(i).getIssuerX500Principal();
		      for (int j = i + 1; j < length; j++)
		      {
		        X500Principal subject = certs.get(j).getSubjectX500Principal();
		        if (issuer.equals(subject))
		        {
		          // sorting necessary?
		          if (i + 1 != j)
		          {
		            X509Certificate tmp = certs.get(i + 1);
		            certs.set(i + 1, certs.get(j));
		            certs.set(j, tmp);
		          }
		          found = true;
		        }
		      }
		      if (!found)
		      {
		        break;
		      }
		    }

		    return certs;
		}
}
