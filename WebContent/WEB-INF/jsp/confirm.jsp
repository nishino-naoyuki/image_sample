<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>アップロードしたファイルの表示</title>
</head>
<body>
<h1><%= request.getAttribute("title") %></h1>
<img src='DisplayImageServlet?name=<%= request.getAttribute("filename") %>'>
<%
/*
【説明】
画像の取得の為にサーブレットを介している
パラメータ名name にファイル名を渡して、DisplayImageServletにて
保存したフォルダから画像を読み込み返している
*/
%>
</body>
</html>