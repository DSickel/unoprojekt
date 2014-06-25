/**
 * JavaScript WebSocket
 */
var webSocket;
	$(function() {
		var WS= window["MozWebSocket"] ? MozWebSocket : WebSocket;
		webSocket = new WS("@routes.Application.webSocket(username).webSocketURL(request)");
		
		webSocket.onmessage = receiveEvent;
	})
	
	function send(){
	var g = $("socket-input").val();
	webSocket.send(g);
	};
	
	function receiveEvent(event){
		$("#socket-output").html(event.data);
	};