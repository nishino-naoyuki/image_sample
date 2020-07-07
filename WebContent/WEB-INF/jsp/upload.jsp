<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>画像のアップロード</title>
</head>
<body>
<form action="UploadServlet" method="POST" enctype="multipart/form-data" >
<input type="text" name="title" placeholder="画像の名前を適当に入れてください"><br>
<input type="file" name="imgFile"/><br>
<input type="submit" value="アップロード" />
</form>
</body>
</html>