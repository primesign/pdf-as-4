<%@page import="at.gv.egiz.pdfas.web.config.WebConfiguration"%>
<html>
<head>
<title>PDF-Signatur</title>
</head>
<body>
	<form role="form" action="Sign" method="POST"
		enctype="multipart/form-data">
		<input type="hidden" name="source" id="source" value="internal" /> <input
			type="file" name="pdfFile" id="pdfFile" accept="application/pdf">
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
		<img src="assets/img/onlineBKU.png" /> <input type="submit"
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
			if (WebConfiguration.getKeystoreEnabled()) {
		%>
			<button type="submit" value="jks" name="connector"
				id="jks">Server Keystore</button>
		<%
			}
		%>
	</form>
</body>
</html>