<html>
<head>
<title>PDF-Signatur</title>
</head>
<body>
<form role="form" action="Sign" method="POST" enctype="multipart/form-data">
  <div class="form-group">
    <label for="exampleInputFile">Signieren: </label>
    <input type="file" name="pdfFile" id="pdfFile">
    <p class="help-block">Zu signierende PDF Datei</p>
  </div>
  <div class="form-group">
  	<button type="submit" value="jks" name="connector" class="btn btn-primary">JKS</button>
	<button type="submit" value="bku" name="connector" class="btn btn-primary">Lokale BKU</button>
	<button type="submit" value="onlinebku" name="connector" class="btn btn-primary">Online BKU</button>
	<button type="submit" value="mobilebku" name="connector" class="btn btn-primary">Handy</button>
	<button type="submit" value="moa" name="connector" class="btn btn-primary">MOA-SS</button>
   </div>
</form>
</body>
</html>