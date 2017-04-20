<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<%@ page import="Entities.*" %>



<div id="<c:out value="${msg.id}" />" class="messageView">
	<div class="pure-g messageWrap">
		<div class="pure-u-1-8" style="margin:9px 0px 0px 0px;">
			<a href="<c:url value="/user?name=${msg.user.name}" />">
				<img src="<c:url value="/ProfileImages/${msg.user.photo}"/>">
			</a>
		</div>
		<div style="margin-top:4px;" class="pure-u-7-8">
			<span class="username">
				<a href="<c:url value="/user?name=${msg.user.name}" />">
					<c:out value="${msg.user.alias}"/>
				</a>
			</span>
			<span style="font-style:italic"> @<c:out value="${msg.user.name}"/></span>
			<time class="timeago" datetime="<c:out value="${msg.publishDate}"/>"><c:out value="${msg.publishDate}"/></time>
			<p style="line-height: 17px; margin:auto; font-size: 14px; color:black">
			<% 

				String path2 = getServletContext().getRealPath("");
	   			
	   			String[]modified;
	   			modified = path2.split("/");
	   
			    for(int i = 0; i<modified.length;i++)
	   			{
	    			if(modified[i].equals("webapps"))
	    			{
	     				path2 = modified[i+1];
	    			}
	   			}


				MessagesEntity m = (MessagesEntity)request.getAttribute("msg"); 
			%>

			<%= m.getTextHtml(path2) %>

			<c:if test="${msg.forwarderAlias != null}">
			<p>
				Forward por: 
				<span class="username">
					<a href="<c:url value="/user?name=${msg.forwarderName}" />">
						<c:out value="${msg.forwarderAlias}"/>
					</a>
				</span>
			</p>
			</c:if>

			<c:if test="${msg.originalId != 0}">
			<p>
				<a href="<c:url value="/conversation?msgId=${msg.originalId}" />" >
					Ver conversación
				</a>
			</p>
			</c:if>
			
			<div style="text-align: right; width: 100%;">
				<c:if test="${userLoginName != null}">
					<ul class="messageActions">
						<c:if test="${msg.forwarderName == userLoginName}">
							<li>
								<img style="width: 16px; height: 16px;" src="<c:url value="/img/fwd.jpg" />"/>
								<a class="fwdButton forwarded" href="#" id="<c:out value="${msg.id}"/>">Fwd</a> | 
							</li>
						</c:if>
						<c:if test="${msg.forwarderName != userLoginName}">
							<li><a class="fwdButton" href="#" id="<c:out value="${msg.id}"/>">Fwd</a> | </li>
						</c:if>
						<li><a class="rplyButton" href="#">Responder</a></li>
					</ul>
				</c:if>
			</div>
		</div>
	</div>

	<div  style="display:none" class="replyView">
		<c:if test="${userLoginName != null}">
			<form id="publishForm" method="POST" class="pure-form" action="<c:url value="/reply" />" >
				<textarea rows="3" name="message" placeholder="Responder a @<c:out value="${msg.user.name}"/>"></textarea>
				<input type="hidden" name="prevMsgId" value="<c:out value="${msg.id}"/>"/>
				<input type="hidden" name="origMsgId" value="<c:out value="${msg.originalId}"/>"/>
				<button class="pure-button" type="submit">Responder</button>
			</form>
		</c:if>
	</div>
</div>