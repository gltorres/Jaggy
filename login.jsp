<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:include page = "/WEB-INF/jspf/header_default.jsp"/>

<script>
$( document ).ready(function() {
	$("#loginForm").submit(function() {
		$("#response").fadeOut("fast");
		var url = "session";
		$.ajax({
			type: "POST",
			url: url,
			data: $("#loginForm").serialize(),
			dataType: "json",
			success: function(response) {
				if (response.redirect) 
				{
					window.location.href = response.redirect;
				}
				if (response.error)
				{
					$("#response").html(response.error);
					$("#response").fadeIn("fast");
				}
			}
		});
		return false;
	});
});
</script>

<div style="margin: 0px auto;max-width: 600px;" class="pure-g">
	<div class="pure-u-1-2">
		<h2>¡Bienvenido a Jaggy!</h2>
		<p style="margin-right:2em">

		Bienvenido a Jaggy, una red social hecha para gente como tú, por gente como tú. <br/><br/>
		Comparte tus pensamientos, tus fotos, y tus momentos especiales con las personas 
		que te admiran, y sigue en vivo la actualidad de las personas que te interesan.<br/><br/>

		¡Es momento de Jaggear!

		</p>
	</div>

	
	<div class="pure-u-1-2">
		<form id="loginForm" class="pure-form pure-form-aligned" method="POST">

			<fieldset class="pure-group">
				<input name="username" class="pure-input-1" type="text" placeholder="Nombre de usuario">
				<input name="password" class="pure-input-1" type="password" placeholder="Contraseña">
			</fieldset>

			<button type="submit" class="button-secondary pure-input-1 pure-button">Entrar</button>
		</form>
		<div style="display:none" class="button-error" id="response"></div>
	</div>
</div>

<jsp:include page = "/WEB-INF/jspf/footer.jspf"/>

