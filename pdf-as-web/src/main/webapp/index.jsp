<%@page import="at.gv.egiz.pdfas.web.config.WebConfiguration"%>
<%@page import="at.gv.egiz.pdfas.web.helper.PdfAsHelper"%>
<html>
<head>
<title>PDF-Signatur</title>
</head>
<body>
	<form action="Sign" method="POST"
		enctype="multipart/form-data">
		<input type="hidden" name="source" id="source" value="internal" /> 
		<input type="file" name="pdf-file" id="pdf-file" accept="application/pdf">
		<%
			if (request.getAttribute("FILEERR") != null) {
		%>
		<p>Bitte die zu signierende PDF Datei angeben.</p>
		<%
			}
		%>


		<%
			if (WebConfiguration.getLocalBKUURL() != null) {
		%>
		<img src="assets/img/onlineBKU.png" /> <button type="submit"
			value="bku" name="connector" id="bku">Lokale BKU
		</button>
		<%
			}
		%>
		<%
			if (WebConfiguration.getOnlineBKUURL() != null) {
		%>
		<img src="assets/img/onlineBKU.png" />
		<button type="submit" value="onlinebku" name="connector"
			id="onlinebku">Online BKU</button>
		<%
			}
		%>
		<%
			if (WebConfiguration.getHandyBKUURL() != null) {
		%>
		<img src="assets/img/mobileBKU.png" />
		<button type="submit" value="mobilebku" name="connector" id="mobilebku">Handy</button>
		<%
			}
		%>
		<%
			if (WebConfiguration.getKeystoreDefaultEnabled()) {
		%>
			<button type="submit" value="jks" name="connector"
				id="jks">Server Keystore</button>
		<%
			}
		%>
		<%
			if (WebConfiguration.getMOASSEnabled()) {
		%>
			<button type="submit" value="moa" name="connector"
				id="moa">MOA-SS</button>
		<%
			}
		%>
		
		<select name="locale" id="locale" size="3">
      		<option>EN</option>
      		<option>DE</option>
    	</select>
		
	</form>
	
	<p><small>Version: <%= PdfAsHelper.getVersion() %> - <%= PdfAsHelper.getSCMRevision() %></small></p>
</body>
</html>