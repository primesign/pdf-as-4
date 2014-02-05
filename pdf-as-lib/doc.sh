 #!/bin/sh
 
# Set the JAVA_HOME variable correctly !!
#JAVA_HOME=/usr/local/java/j2se
         
#PATH=$JAVA_HOME/bin
             
DOCLET=com.tarsec.javadoc.pdfdoclet.PDFDoclet
JARS=./libs/pdfdoclet-1.0.3-all.jar
PACKAGES="at.gv.egiz.pdfas.lib.api at.gv.egiz.pdfas.lib.api.sign at.gv.egiz.pdfas.lib.api.verify"
PDF=../docs/PDF_AS_API.pdf
CFG=./libs/config_pdfas.properties
SRC=./src/main/java
                                
export DOCLET JARS PACKAGES PDF CFG SRC
                                         
javadoc -doclet $DOCLET -docletpath $JARS -pdf $PDF -config $CFG -sourcepath $SRC $PACKAGES
