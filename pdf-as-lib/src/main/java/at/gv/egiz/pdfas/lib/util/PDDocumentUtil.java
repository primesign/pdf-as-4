package at.gv.egiz.pdfas.lib.util;/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.*;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.PDEncryption;
import org.apache.pdfbox.pdmodel.encryption.ProtectionPolicy;
import org.apache.pdfbox.pdmodel.encryption.SecurityHandler;
import org.slf4j.Logger;
import sun.rmi.runtime.Log;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * This is the in-memory representation of the PDF document.
 * The #close() method must be called once the document is no longer needed.
 *
 * @author Ben Litchfield
 */
public class PDDocumentUtil extends PDDocument
{
    private static final org.apache.commons.logging.Log logger = LogFactory.getLog(PDDocumentUtil.class);

    // the encryption will be cached here. When the document is decrypted then
    // the COSDocument will not have an "Encrypt" dictionary anymore and this object must be used
    private PDEncryption encryption;

    // holds a flag which tells us if we should remove all security from this documents.
    private boolean allSecurityToBeRemoved;


    // the access permissions of the document
    private AccessPermission accessPermission;

    private  COSDocument document=null;
    // the pdf to be read
    private  RandomAccessRead pdfSource=null;



    public PDDocumentUtil()
    {
    }

    /**
     * Constructor that uses an existing document. The COSDocument that is passed in must be valid.
     *
     * @param doc The COSDocument that this document wraps.
     * @param source the parser which is used to read the pdf
     * @param permission he access permissions of the pdf
     *
     */
    public PDDocumentUtil(COSDocument doc, RandomAccessRead source, AccessPermission permission)
    {
        document = doc;
        pdfSource = source;
        accessPermission = permission;
    }

    public static PDDocument load(File file) throws IOException
    {
        return load(file, "", MemoryUsageSetting.setupMainMemoryOnly());
    }

    /**
     * Parses a PDF.
     *
     * @param file file to be loaded
     * @param memUsageSetting defines how memory is used for buffering PDF streams
     *
     * @return loaded document
     *
     * @throws IOException in case of a file reading or parsing error
     */
    public static PDDocument load(File file, MemoryUsageSetting memUsageSetting) throws IOException
    {
        return load(file, "", null, null, memUsageSetting);
    }

    /**
     * Parses a PDF. Unrestricted main memory will be used for buffering PDF streams.
     *
     * @param file file to be loaded
     * @param password password to be used for decryption
     *
     * @return loaded document
     *
     * @throws IOException in case of a file reading or parsing error
     */
    public static PDDocument load(File file, String password) throws IOException
    {
        return load(file, password, null, null, MemoryUsageSetting.setupMainMemoryOnly());
    }

    /**
     * Parses a PDF.
     *
     * @param file file to be loaded
     * @param password password to be used for decryption
     * @param memUsageSetting defines how memory is used for buffering PDF streams
     *
     * @return loaded document
     *
     * @throws IOException in case of a file reading or parsing error
     */
    public static PDDocument load(File file, String password, MemoryUsageSetting memUsageSetting) throws IOException
    {
        return load(file, password, null, null, memUsageSetting);
    }

    /**
     * Parses a PDF. Unrestricted main memory will be used for buffering PDF streams.
     *
     * @param file file to be loaded
     * @param password password to be used for decryption
     * @param keyStore key store to be used for decryption when using public key security
     * @param alias alias to be used for decryption when using public key security
     *
     * @return loaded document
     *
     * @throws IOException in case of a file reading or parsing error
     */
    public static PDDocument load(File file, String password, InputStream keyStore, String alias)
            throws IOException
    {
        return load(file, password, keyStore, alias, MemoryUsageSetting.setupMainMemoryOnly());
    }

    /**
     * Parses a PDF.
     *
     * @param file file to be loaded
     * @param password password to be used for decryption
     * @param keyStore key store to be used for decryption when using public key security
     * @param alias alias to be used for decryption when using public key security
     * @param memUsageSetting defines how memory is used for buffering PDF streams
     *
     * @return loaded document
     *
     * @throws IOException in case of a file reading or parsing error
     */
    public static PDDocument load(File file, String password, InputStream keyStore, String alias,
                                  MemoryUsageSetting memUsageSetting) throws IOException
    {
        RandomAccessBufferedFileInputStream raFile = new RandomAccessBufferedFileInputStream(file);
        try
        {
            ScratchFile scratchFile = new ScratchFile(memUsageSetting);
            try
            {
                PDFParser parser = new PDFParser(raFile, password, keyStore, alias, scratchFile);
                parser.parse();
                return parser.getPDDocument();
            }
            catch (IOException ioe)
            {
                IOUtils.closeQuietly(scratchFile);
                throw ioe;
            }
        }
        catch (IOException ioe)
        {
            IOUtils.closeQuietly(raFile);
            throw ioe;
        }
    }




    /**
     * Protects the document with a protection policy. The document content will be really
     * encrypted when it will be saved. This method only marks the document for encryption. It also
     * calls {@link #setAllSecurityToBeRemoved(boolean)} with a false argument if it was set to true
     * previously and logs a warning.
     *
     * @see org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy
     * @see org.apache.pdfbox.pdmodel.encryption.PublicKeyProtectionPolicy
     *
     * @param policy The protection policy.
     * @throws IOException if there isn't any suitable security handler.
     */
    public void protect(ProtectionPolicy policy) throws IOException
    {
        if (isAllSecurityToBeRemoved())
        {
            logger.warn("do not call setAllSecurityToBeRemoved(true) before calling protect(), "
                    + "as protect() implies setAllSecurityToBeRemoved(false)");
            setAllSecurityToBeRemoved(false);
        }

        if (!isEncrypted())
        {
            encryption = new PDEncryption();
        }

        SecurityHandler securityHandler = DefaultSecHandlerFactory.INSTANCE.newSecurityHandlerForPolicy(policy);
       if (securityHandler == null)
        {
          throw new IOException("No security handler for policy " + policy);
        }

        try {
            getEncryption().setSecurityHandler(securityHandler);
        } catch (NullPointerException nle){logger.warn("Could not set Encryption type");}
    }

    /**
     * Returns the access permissions granted when the document was decrypted. If the document was not decrypted this
     * method returns the access permission for a document owner (ie can do everything). The returned object is in read
     * only mode so that permissions cannot be changed. Methods providing access to content should rely on this object
     * to verify if the current user is allowed to proceed.
     *
     * @return the access permissions for the current user on the document.
     */
    public AccessPermission getCurrentAccessPermission()
    {
        if (accessPermission == null)
        {
            accessPermission = AccessPermission.getOwnerAccessPermission();
        }
        return accessPermission;
    }

    /**
     * Indicates if all security is removed or not when writing the pdf.
     *
     * @return returns true if all security shall be removed otherwise false
     */
    public boolean isAllSecurityToBeRemoved()
    {
        return allSecurityToBeRemoved;
    }

    /**
     * Activates/Deactivates the removal of all security when writing the pdf.
     *
     * @param removeAllSecurity remove all security if set to true
     */
    public void setAllSecurityToBeRemoved(boolean removeAllSecurity)
    {
        allSecurityToBeRemoved = removeAllSecurity;
    }

    }

