<%@page import="at.gv.egiz.pdfas.web.config.WebConfiguration"%>
<%@page import="at.gv.egiz.pdfas.web.helper.PdfAsHelper"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<title>PDF-Signatur</title>
</head>
<body>

<form action="Sign" method="POST" enctype="multipart/form-data" >

		<input name="utf8" type="hidden" value="&#x2713;" />
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
		<img src="assets/img/localBKU.png" /> <button type="submit"
			value="bku" name="connector" id="bku">Lokale BKU
		</button>
<!--
		<label for="ab">SBP keyA</label>
		<input type="text" id="ab" name="sbp:keyA">
		<label for="abc">SBP keyB</label>
		<input type="text" id="abc" name="sbp:keyB">
		-->
		<%
			}
		%>
		<%
			if (WebConfiguration.getHandyBKUURL() != null) {
		%>
		<img src="assets/img/mobileBKU.png" />
		<button type="submit" value="mobilebku" name="connector" id="mobilebku">Handy</button>
		<label for="placeholder_web_id">Placeholder ID</label>
          <input type="text" id="placeholder_web_id" name="placeholder_web_id">
		<%
			}
		%>
		
		<%
			if (WebConfiguration.getSecurityLayer20URL() != null) {
		%>
		<button type="submit" value="sl20" name="connector" id="sl20backend">SL2.0 Interface</button>
		<label for="placeholder_web_id">Placeholder ID</label>
          <input type="text" id="placeholder_web_id" name="placeholder_web_id">
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