package controllers;

import play.*;
import play.mvc.*;
import views.html.*;
import model.User;
import play.data.*;

public class GetStarted extends Controller {
	
	
	
	public static Result regelwerk() {
		return ok(regelwerk.render());
	}
}
