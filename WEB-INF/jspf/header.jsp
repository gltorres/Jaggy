<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>Jaggy</title>
		<link rel="shortcut icon" href="<c:url value="/img/favicon.ico" />"/>

	    <meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />

		<link rel="stylesheet" type="text/css" href="<c:url value="/css/reset.css" />"/>
		<link rel="stylesheet" type="text/css" href="<c:url value="/css/pure-min.css" />"/>
		<link rel="stylesheet" type="text/css" href="<c:url value="/css/main-style.css" />"/>
		<link rel="stylesheet" type="text/css" href="<c:url value="/css/jquery.modal.css"/>" media="screen" />

		<script type="text/javascript" src="<c:url value="/js/jquery_1.11.0.js"/>" charset="utf-8"></script>
		<script type="text/javascript" src="<c:url value="/js/timeago.js"/>" charset="utf-8"></script>
		<script type="text/javascript" src="<c:url value="/js/jquery-modal.js"/>" charset="utf-8"></script>
	</head>
	<script type="text/javascript">
		var appRoot = "<c:url value="/"/>";
	</script>
	<body>
		<div class="header pure-menu-fixed" style="background: rgb(28, 184, 65); height: 41px;">
		<div style= "background: rgb(28, 184, 65); margin:0px auto; max-width:1024px" class="main-nav pure-menu-open pure-menu pure-menu-horizontal">	

		<c:if test="${userLoginName != null}">
			<ul style="float:left">
				<li><a href="<c:url value="/home" />" >Inicio</a></li>
				<li>
					<a href="<c:url value="/user?name=${userLoginName}" />">
						Perfil
					</a>
				</li>
			</ul>
			
				<c:if test="${false}">
					<form id="searchBar" class="pure-form">
						<input placeholder="Buscar..." type="text" class="pure-input-rounded" style="padding-bottom: 5px; padding-top: 5px">
					</form>
				</c:if>	

				<a id ="logout" href="<c:url value="/session?action=logout" />">Salir</a>
		</c:if>	
		
		<c:if test="${userLoginName == null}">
			<ul style="float:left">
				<li><a href="<c:url value="/home" />" >Inicio</a></li>
			</ul>
			
				<c:if test="${false}">
					<form id="searchBar" class="pure-form">
						<input placeholder="Buscar..." type="text" class="pure-input-rounded" style="padding-bottom: 5px; padding-top: 5px">
					</form>			
				</c:if>	
		</c:if>
	
		</div>
		</div>
		
		<div class="content-wrapper">
		
		
