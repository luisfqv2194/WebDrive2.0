package webdrive.business;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


public class User {
	
	private String username;
    private String password;
    
    private Drive myDrive;
    
   
	public User(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}



	public User() {
	}



	public String getUsername() {
		return username;
	}



	public void setUsername(String username) {
		this.username = username;
	}



	public String getPassword() {
		return password;
	}



	public void setPassword(String password) {
		this.password = password;
	}



	public Drive getMyDrive() {
		return myDrive;
	}



	public void setMyDrive(Drive myDrive) {
		this.myDrive = myDrive;
	}
	
	
    
    

}
