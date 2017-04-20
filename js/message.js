
// Id del ultimo mensaje recibido
var lastMsgId = 0;
// Mensajes sin leer
var newMsgsCount = 0;
var newMsgsArray = [];
// Cada cuantos milisegundos pedimos mensajes nuevos al servidor
var updateIntervalMs = 5000;
// Url a la que pedir nuevos mensajes
var fetchUrl = "";

var windowTitle;

// Configuración de la librearía timeago
jQuery.timeago.settings.strings = {
	prefixAgo: "hace",
	prefixFromNow: "dentro de",
	suffixAgo: "",
	suffixFromNow: "",
	seconds: "menos de un minuto",
	minute: "un minuto",
	minutes: "unos %d minutos",
	hour: "una hora",
	hours: "%d horas",
	day: "un día",
	days: "%d días",
	month: "un mes",
	months: "%d meses",
	year: "un año",
	years: "%d años"
};

// Llamar a la funcion "timeago()" de la librería para convertir las fechas a texto de intervalos de tiempo
$(document).ready(function() {
	windowTitle = $(document).attr('title');
	$( ".timeago" ).timeago();

});

// Reenviar el mensaje (solo si no ha sido reenviado ya)
$(document).on('click','.fwdButton:not(.forwarded)', {} ,function(event) {
	var msgId = event.target.id;
	$(this).addClass('forwarded');
	doForward($(event.target), msgId);
    event.stopPropagation();

	return;
});

// Envíar el formulario con la respuesta al mensaje
/*
$(document).on('click', '.rplyButton', {}, function(e){
	$(this).clearQueue();
	$(this).closest( ".messageView" ).find(".replyView").stop().toggle("fast"); 
	e.preventDefault();
	return;
});
*/
// Desplegar las opciones al hacer click en un menaje
$(document).on('click', '.messageWrap', {}, function(event){
	$(this).clearQueue();
	$(this).closest( ".messageView" ).find(".replyView").stop().toggle("fast"); 
    event.stopPropagation();

});

// Mostar los mensajes nuevos pendientes
$(document).on('click', '#newMsgsButton', {}, function(e){
	var newhtml = "";

	for (var i = 0; i < newMsgsArray.length; i++) { 
		//var newMsgElement = $(newMsgsArray[i].html);
		//newMsgElement.css("display", "hidden");
		//newhtml += newMsgsArray[i].html;
		$(newMsgsArray[i].html).hide().prependTo("#timeline").fadeIn("slow");
	}
	// Vaciar los mensajes entrantes
	newMsgsCount = 0;
	newMsgsArray.length = 0;
	newMsgsArray = [];

	// Reestablecer el titulo original de la ventana
	$(document).attr('title', windowTitle);

	$( ".timeago" ).timeago();
	$("#newMsgsButton").hide();
});

// Esta función desencadena la actualización periódica.
// Se llama desde el JSP que corresponda ya que es este JSP el que tiene la URL a la que pedir mensajes
function startFecthingMsgs(url) {
	lastMsgId = $('.messageView').first().attr('id');
	fetchUrl = url;
	setTimeout(fetchNewMsgs, updateIntervalMs);
}

// Envíar por ajax el ID del mensaje que estamos "reenviando"
function doForward(button, msgID) {
	$.ajax({
		type: "POST",
		url: appRoot + 'forward',
		data: {"fwdMsgId": msgID},
		dataType: "json",
		success: function(response) {
			if (response.redirect) 
			{
				window.location.href = appRoot + response.redirect;
			}
			if (response.success)
			{
				button.css("color","green");
				button.prepend("<img style='width: 16px; height: 16px;' src='" + appRoot + "/img/fwd.jpg'/>");
			}
		}
	});
} 

// Pedir mensajes nuevos al servidor y guardarlos en el array "newMsgsArray"
// Esta función usa "setTimeOut" para llamarse a si misma periodicamente
function fetchNewMsgs() {
	$.ajax({
		type: "GET",
		url: fetchUrl,
		dataType: "json",
		data: {"lastMsgId": lastMsgId},
		success: function(response) {

			if(response.msgCount > 0) {
				newMsgsArray = newMsgsArray.concat(response.msgs);
				lastMsgId = response.msgs[0].id;

				newMsgsCount += response.msgCount;
				$("#newMsgsButton").text(newMsgsCount + " mensajes nuevos");
				$("#newMsgsButton").show();

				$(document).attr('title', '(' + newMsgsCount + ') ' + windowTitle);
			}

		},
		error: function(response)
		{
			alert("Algo salió mal recuperando mensajes, por favor recarga la página.");
		}
	});
	setTimeout(fetchNewMsgs, updateIntervalMs);
}