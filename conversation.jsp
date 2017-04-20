<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<jsp:include page = "/WEB-INF/jspf/header.jsp"/>

<script src="<c:url value="/js/message.js"/>"></script>

<div class="pure-g">
	<div class="pure-u-1-4">
		<div class="userInfo" class="pure-g">
		</div>
	</div>
	
	<div id="main-view" class="pure-u-1-2">
		<h1>Conversación</h1>

		<div id="timeline">
			<c:forEach items="${conversationMsgs}" var="msg">
			 <c:set var="msg" scope="request" value="${msg}"/>
			  	<jsp:include page="/WEB-INF/jspf/message.jsp"/>
			 </c:forEach>
		</div>

	</div>
	
	<div class = "pure-u-1-4" ></div>
</div>


<jsp:include page = "/WEB-INF/jspf/footer.jspf"/>