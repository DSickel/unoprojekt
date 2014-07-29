package model;

import java.util.ArrayList;
import java.util.List;


import play.data.validation.ValidationError;

/**
 * Handles the User Login + "Validation"
 * @author Dominic
 *
 */
public class User {
	
	public String userName;
	
	
	public User(){
		//Default constructor
	}
	
	public User(String userName) {
		this.userName = userName;
	}

	public List<ValidationError> validate() {
		List<ValidationError> error = new ArrayList<>();
		
		if(userName == null || userName.length() == 0){
			error.add(new ValidationError("userName", "This field is needed"));
		}
		// Nothing in "error" return null, else return error
		return error.isEmpty() ? null : error;
	}
}
