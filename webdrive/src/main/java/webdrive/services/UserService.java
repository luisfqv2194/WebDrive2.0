package webdrive.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import webdrive.business.User;

@Service   //Debe ser un singleton
public class UserService {
	
	private List<User> users = new ArrayList<> (Arrays.asList(
			new User("user1","pass1",1), 
			new User("user2","pass2",2), 
			new User("user3","pass1",3)));

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}
	
	public User getUser(String username) {
		return users.stream().filter(user -> user.getUsername().equals(username)).findFirst().get();
		
	}

	public void addUser(User user) {
		this.users.add(user);
		
	}

	public void updateUser(User user, String username) {
		for(int i = 0; i < users.size(); i++) {
			User u = users.get(i);
			if(u.getUsername().equals(username)) {
				users.set(i, user);
				return;
			}
		}
		
	}

	public void deleteUser(String username) {
		users.removeIf(user -> user.getUsername().equals(username));
		
	}
	
	

}
