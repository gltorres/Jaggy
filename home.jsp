<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<jsp:include page="/WEB-INF/jspf/header.jsp"/>

<script src="<c:url value="/js/message.js"/>"></script>
<script type="text/javascript">
	$(document).ready(function() {
		// Con esto empezamos a recibir los mensajes nuevos
		startFecthingMsgs("<c:url value="/home" />");
	});
</script>
<div class="pure-g">
	<div class="pure-u-1-4">
		<div class="userInfo" class="pure-g">
			<p><span style="font-weight:bold"><c:out value="${userLoginAlias}"/></span> @<c:out value="${userLoginName}"/></p>
			<img style="margin: 0px auto" class="profileImage" src="<c:url value="/ProfileImages/${userLoginPhoto}"/>" />
			<div class="userStats pure-g">
				<div class="pure-u-1-3"><h2>Mensajes</h2><p><c:out value="${numMessages}"/></p></div>
				<div class="pure-u-1-3"><h2>Siguiendo</h2><p><c:out value="${numFollowing}"/></p></div>
				<div class="pure-u-1-3"><h2>Seguidores</h2><p><c:out value="${numFollowers}"/></p></div>
			</div>
		</div>
	</div>
	
	<div id="main-view" class="pure-u-1-2">

		<div>
			<form id="publishForm" method="POST" class="pure-form" action="publish" accept-encoding="UTF-8">
				<textarea rows="3" name="message" placeholder="Cuéntanos más..."></textarea>
				<button type="submit" class="button-green pure-button">Publicar</button>
			</form>
		</div>
		
		<a id="newMsgsButton" href="#"/>
		<div id="timeline">
			 <c:forEach items="${timelineMsgs}" var="msg">
			 <c:set var="msg" scope="request" value="${msg}"/>
			  	<jsp:include page="/WEB-INF/jspf/message.jsp"/>
			 </c:forEach>
		</div>

	</div>
	<div class = "pure-u-1-4" >

	</div>
</div>

<jsp:include page = "/WEB-INF/jspf/footer.jspf"/>
