<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<jsp:include page = "/WEB-INF/jspf/header.jsp"/>

<script src="<c:url value="/js/message.js"/>"></script>
<script type="text/javascript">
	$(document).ready(function() {
		// Con esto empezamos a recibir los mensajes nuevos
		startFecthingMsgs("<c:url value="/user/${userName}" />");
	});
</script>
		<c:if test="${userLoginName == userName}">
			<div>
				<form id="profileForm" style="display:none;" class ="modal pure-form" action="<c:url value="/upload" />" enctype="MULTIPART/FORM-DATA" method="post">
				    <fieldset class="pure-group">
						<input  type="file" name="file" accept="image/*" />					
						<input class="pure-button pure-button-primary" type="submit" value="Upload" />
					</fieldset>
				</form>
			</div>
		</c:if>
	<div class="pure-g">
	<div class="pure-u-1-4"></div>
	
	<div id="main-view" class="pure-u-1-2">

		<div class="userInfo" class="pure-g">
			<p><span style="font-weight:bold"><c:out value="${userAlias}"/></span> @<c:out value="${userName}"/></p>
			<img style="margin: 0px auto" class="profileImage" src="<c:url value="/ProfileImages/${userPhoto}"/>" />

			<c:if test="${userLoginName == userName}">
			    <script type="text/javascript">
					var wrapper = $('<div/>').css({height:0,width:0,'overflow':'hidden'});
					var fileInput = $(':file').wrap(wrapper);
					// Estilizar el input de subida de archivo
					$( document ).ready(function() {
					
						fileInput.change(function(){
							$this = $(this);
							$('#profileForm').submit();
						})

						$('#file').bind("click", function(event) {
							fileInput.click();
						}).show()
					
					});
				</script>
				<br style="margin: 20px;"/>
				<div class="pure-button" id="file">Editar foto</div>
				<form id="profileForm" style="display:none;" action="<c:url value="/upload" />" enctype="MULTIPART/FORM-DATA" method="post">
					<input style="display:none;" type="file" name="file" />
				</form>
			</c:if>

			<div class="userStats pure-g">
				<div class="pure-u-1-3"><h2>Mensajes</h2><p><c:out value="${numMessages}"/></p></div>
				<div class="pure-u-1-3"><h2>Siguiendo</h2><p><c:out value="${numFollowing}"/></p></div>
				<div class="pure-u-1-3"><h2>Seguidores</h2><p><c:out value="${numFollowers}"/></p></div>
			</div>
		</div>
		
		<a id="newMsgsButton" href="#"/>
		<div id="timeline">
			 <c:forEach items="${profileMsgs}" var="msg">
			 <c:set var="msg" scope="request" value="${msg}"/>
			  	<jsp:include page="/WEB-INF/jspf/message.jsp"/>
			 </c:forEach>
		</div>

	</div>
	<div class = "pure-u-1-4" ></div>
</div>


<jsp:include page = "/WEB-INF/jspf/footer.jspf"/>