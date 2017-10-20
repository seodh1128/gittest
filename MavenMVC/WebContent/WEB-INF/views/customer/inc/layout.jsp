<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<title><tiles:getAsString name="title" /></title>
		<link href="../css/customer.css" type="text/css" rel="stylesheet" />
	</head>
	<body>
		<!-- 헤더부분 -->
		<tiles:insertAttribute name="header" />
		<!-- visual 부분 - 모든 부분에서 공통되지는 않음 -->
		<tiles:insertAttribute name="visual" />
		<div id="main">
			<div class="top-wrapper clear">
				<!-- contents 부분 - 제거함 -->
				<tiles:insertAttribute name="content" />
				<!-- navi 부분 - customer부분에서만 공통 사용됨 -->
				<tiles:insertAttribute name="aside" />
			</div>
		</div>
		<!-- footer 부분 -->
		<tiles:insertAttribute name="footer"></tiles:insertAttribute>
	</body>
</html>
