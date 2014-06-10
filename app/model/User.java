package model;

import java.util.ArrayList;
import java.util.List;

import play.data.validation.Constraints.Required;
import play.data.validation.ValidationError;

/**
 * Handles the User Login + "Validation"
 * @author Dominic
 *
 */
public class User {
	@Required
	public String username;
	
	
	public User(){
		//Default constructor
	}
	
	public User(String username) {
		this.username = username;
	}

	public List<ValidationError> validate() {
		List<ValidationError> error = new ArrayList<>();
		if(username == null || username.length() == 0){
			error.add(new ValidationError("username", "Username is required"));
		}
		// Nothing in "error" return null, else return error
		return error.isEmpty() ? null : error;
	}
}
