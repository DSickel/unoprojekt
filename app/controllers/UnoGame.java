package controllers;

import play.*;
import play.mvc.*;
import views.html.*;
import model.User;
import play.data.*;

public class UnoGame extends Controller {
	
	
	public static Result spiel_erstellen() {
		return ok(spiel_erstellen.render());
	}
	
	public static Result spiel_beitreten() {
		return TODO;
	}
	
	public static Result regelwerk() {
		return ok(regelwerk.render());
	}
}
