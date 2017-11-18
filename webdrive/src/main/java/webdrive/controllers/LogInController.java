package webdrive.controllers; // Para leer los controladores el paquete debe ser un subpaquete del paquete principal

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import webdrive.business.User;
import webdrive.services.UserService;

@RestController
public class LogInController {
	
	@Autowired
	private UserService userService;

	@RequestMapping("/login") // Todos los métodos
	public String sayHi() {
		return "hi";
	}
	
	@RequestMapping("/login/{username}") // Todos los métodos
	public User getUser(@PathVariable String username) {
		
		return userService.getUser(username);
	}
	
	@RequestMapping("/list") // Todos los métodos
	public List<User> sayList() {
		return userService.getUsers();
	}
	
	
}
