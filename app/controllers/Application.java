package controllers;

import play.*;
import play.mvc.*;
import views.html.*;
import model.User;
import play.data.*;

public class Application extends Controller {

	final static Form<User> loginForm = Form.form(User.class);
	
    public static Result index() {
        return ok(index.render("Your new application is ready."));
    }
    
    public static Result login() {
    	Form<User> userForm = loginForm.fill(new User("Max Mustermann"));
    	return ok(login.render(userForm));
    }
    
    public static Result startseite() {
    	Form<User> userForm = loginForm.bindFromRequest();
    	if(userForm.hasErrors()){
    		return badRequest(login.render(userForm));
    	}else{
    		User user = userForm.get();
    		session().clear();
    		session("User1", user.username);
    		return ok(startseite.render(user));
    	}
    }
    
    public static Result start() {
    	String user = session("User1");
    	if(user != null){
    		return ok(startseite.render(new User(user)));
    	}else{
    		return ok(login.render(loginForm));
    	}
    	
    }
   
   
}	
