# Release notes

### Overview of necessary steps
1. Release on Joinup page
2. Push code to public git repo
3. Add release news to the website

#### 1. Release on Joinup page
To add contribution on Joinup page, both release folder and maven repository are necessary.

Create release folder by running:

`./gradle jar sourcesJar war distZip distTar releases`

With this command, a release folder is created. 

**Note**: The release version should be without snapshot!

Release folder needs to be uploaded to https://apps.egiz.gv.at/releases/pdf-as/release/ 

Create mvn repo folder by running:

`./gradle uploadArchives`

The generated mvn repo needs to be uploaded to https://apps.egiz.gv.at/maven/at/gv/egiz/pdfas/

After this, the release on Joinup page is necessary. 
For this purpose, make sure to have corresponding role in PDF-AS project. 
Make a new "release" on PDF-AS page on Joinup. The release version should contain a new version number, distribution links, and notes about the major changes in that version.
https://joinup.ec.europa.eu/collection/e-government-innovation-center-egiz/solution/pdf 


#### 2. Push source code to public git

Run git push with tag also to  https://git.egiz.gv.at/pdf-as-4/ (this is the public EGIZ GIT REPO) 

#### 3. Add release notes to EGIZ or ASIT website! 

# Setup


#### 1. Deployment of configuration 

arguments: 

-d

#### 2. DebugRun 

arguments: 

-p SIGNATURBLOCK_DE 

-c bku 
  
-m sign C:\Users\username\Desktop\testing\test.pdf

-pos "x:220;y:220;w:300;p:1" 

#### 3. PDF-AS-Web 

`-Dpdf-as-web.conf=$CATALINA_BASE/conf/pdf-as/pdf-as-web.properties \` 

#### 4. Intellij Config for testing:

* Main class: at.gv.egiz.pdfas.cli.Main
* Classpath: pdf-as-4-pdf-as-cli.pdfbox2
* Parameters: -p SIGNATURBLOCK_DE_NOTE_DYNAMIC -c bku -m sign /Users/user/Documents/pdf-as-4/unsigned.pdf
* Needs local BKU (e.g. Mocca)
* Uses configuration from /Users/user/.pdfas/




