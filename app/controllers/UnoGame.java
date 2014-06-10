package controllers;

import play.*;
import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.mvc.*;
import views.html.*;
import model.User;
import play.data.*;

public class UnoGame extends Controller{
	
	
	
	public static WebSocket<String> websocket() {
		WebSocket<String> webSocket = null;
		webSocket = new WebSocket<String>() {
			
			//Called when the Websocket Handshake is done.
			public void onReady(WebSocket.In<String> input, WebSocket.Out<String> output) {
				
				//For each event received on the socket,
				input.onMessage(new Callback<String>() {
					public void invoke(String event) {
						
						//An dieser Stelle Methode aufrufen welche das Websocket handeln soll
						
						//Log events to the console
						System.out.println(event);
					}
				});
				input.onClose(new Callback0() {
					public void invoke() {
						System.out.println("Disconnected!");
					}
				});
				//Send a single 'Hello' message
				output.write("Websocket läuft");
			}
		};
		return webSocket;
	}
	
	public static Result spiel_beitreten(){
		return ok(spiel_beitreten.render());
	}
	
	public static Result gameplay(){
		
		return ok(gameboard.render("MESSAGE"));
	}
	
	
	
	
	
	
	
}
