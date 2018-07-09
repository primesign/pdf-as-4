/*
 * Copyright 2003 Federal Chancellery Austria
 * MOA-ID has been developed in a cooperation between BRZ, the Federal
 * Chancellery Austria - ICT staff unit, and Graz University of Technology.
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


package at.gv.egiz.pdfas.web.sl20;

import iaik.x509.X509Certificate;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;

/**
 * Utility for creating and loading key stores.
 * 
 * @author Paul Ivancsics
 * @version $Id$
 */
public class KeyStoreUtils {
	
	/**
	 * JAVA KeyStore
	 */
	private static final String KEYSTORE_TYPE_JKS = "JKS";
	
	/**
	 * PKCS12 KeyStore
	 */
	private static final String KEYSTORE_TYPE_PKCS12 = "PKCS12";
	
	

  /**
   * Loads a key store from file.
   * 
   * @param keystoreType key store type
   * @param urlString URL of key store
   * @param password password protecting the key store
   * @return key store loaded
   * @throws IOException thrown while reading the key store from file
   * @throws GeneralSecurityException thrown while creating the key store
   */
  public static KeyStore loadKeyStore(
    String keystoreType,
    String urlString,
    String password)
    throws IOException, GeneralSecurityException {

    URL keystoreURL = new URL(urlString);
    InputStream in = keystoreURL.openStream();
    return loadKeyStore(keystoreType, in, password);
  }
  /**
   * Loads a key store from an <code>InputStream</code>, and
   * closes the <code>InputStream</code>.
   * 
   * @param keystoreType key store type
   * @param in input stream
   * @param password password protecting the key store
   * @return key store loaded
   * @throws IOException thrown while reading the key store from the stream
   * @throws GeneralSecurityException thrown while creating the key store
   */
  public static KeyStore loadKeyStore(
    String keystoreType,
    InputStream in,
    String password)
    throws IOException, GeneralSecurityException {

    char[] chPassword = null;
    if (password != null)
      chPassword = password.toCharArray();
    KeyStore ks = KeyStore.getInstance(keystoreType);
    ks.load(in, chPassword);
    in.close();
    return ks;
  }
  /**
   * Creates a key store from X509 certificate files, aliasing them with
   * the index in the <code>String[]</code>, starting with <code>"0"</code>.
   * 
   * @param keyStoreType key store type
   * @param certFilenames certificate filenames
   * @return key store created
   * @throws IOException thrown while reading the certificates from file
   * @throws GeneralSecurityException thrown while creating the key store
   */
  public static KeyStore createKeyStore(
    String keyStoreType,
    String[] certFilenames)
    throws IOException, GeneralSecurityException {

    KeyStore ks = KeyStore.getInstance(keyStoreType);
    ks.load(null, null);
    for (int i = 0; i < certFilenames.length; i++) {
      Certificate cert = loadCertificate(certFilenames[i]);
      ks.setCertificateEntry("" + i, cert);
    }
    return ks;
  }
//  /**
//   * Creates a key store from a directory containg X509 certificate files, 
//   * aliasing them with the index in the <code>String[]</code>, starting with <code>"0"</code>.
//   * All the files in the directory are considered to be certificates.
//   * 
//   * @param keyStoreType key store type
//   * @param certDirURLString file URL of directory containing certificate filenames
//   * @return key store created
//   * @throws IOException thrown while reading the certificates from file
//   * @throws GeneralSecurityException thrown while creating the key store
//   */
//  public static KeyStore createKeyStoreFromCertificateDirectory(
//    String keyStoreType,
//    String certDirURLString)
//    throws IOException, GeneralSecurityException {
//
//    URL certDirURL = new URL(certDirURLString);
//    String certDirname = certDirURL.getFile();
//    File certDir = new File(certDirname);
//    String[] certFilenames = certDir.list();
//    String separator =
//      (certDirname.endsWith(File.separator) ? "" : File.separator);
//    for (int i = 0; i < certFilenames.length; i++) {
//      certFilenames[i] = certDirname + separator + certFilenames[i];
//    }
//    return createKeyStore(keyStoreType, certFilenames);
//  }

  /**
   * Loads an X509 certificate from file.
   * @param certFilename filename
   * @return the certificate loaded
   * @throws IOException thrown while reading the certificate from file
   * @throws GeneralSecurityException thrown while creating the certificate
   */
  private static Certificate loadCertificate(String certFilename)
    throws IOException, GeneralSecurityException {

    FileInputStream in = new FileInputStream(certFilename);
    Certificate cert = new X509Certificate(in);
    in.close();
    return cert;
  }
  
 
	/**
	 * Loads a keyStore without knowing the keyStore type
	 * @param keyStorePath URL to the keyStore
	 * @param password Password protecting the keyStore
	 * @return keyStore loaded
	 * @throws KeyStoreException thrown if keyStore cannot be loaded
	 * @throws FileNotFoundException 
	 * @throws IOException 
	 */
  public static KeyStore loadKeyStore(String keyStorePath, String password) throws KeyStoreException, IOException{
		
		//InputStream is = new FileInputStream(keyStorePath);
	  	URL keystoreURL = new URL(keyStorePath);
	    InputStream in = keystoreURL.openStream();
		InputStream isBuffered = new BufferedInputStream(in);				
		return loadKeyStore(isBuffered, password);
		
	}
	
	/**
	 * Loads a keyStore without knowing the keyStore type
	 * @param in input stream
	 * @param password Password protecting the keyStore
	 * @return keyStore loaded
	 * @throws KeyStoreException thrown if keyStore cannot be loaded
	 * @throws FileNotFoundException 
	 * @throws IOException 
	 */
public static KeyStore loadKeyStore(InputStream is, String password) throws KeyStoreException, IOException{		
		is.mark(1024*1024);
		KeyStore ks = null;
		try {
			try {				
				ks = loadKeyStore(KEYSTORE_TYPE_PKCS12, is, password);
			} catch (IOException e2) {
				is.reset();				
				ks = loadKeyStore(KEYSTORE_TYPE_JKS, is, password);
			}
		} catch(Exception e) {			
			e.printStackTrace();
			//throw new KeyStoreException(e);
		}
		return ks;	
						
	}
  
	


}
