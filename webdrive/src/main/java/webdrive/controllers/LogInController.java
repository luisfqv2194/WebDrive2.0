package webdrive.controllers; // Para leer los controladores el paquete debe ser un subpaquete del paquete principal

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import webdrive.business.User;
import webdrive.services.UserService;

@RestController
public class LogInController {
	
	@Autowired
	private UserService userService;
	
	@RequestMapping("/login/{username}") // Todos los métodos
	public User getUser(@PathVariable String username) {
		
		return userService.getUser(username);
	}
	
	@PostMapping("/login")  // Solo Post
	public void addUser(@RequestBody User user) {
		userService.addUser(user);
	}
	
	@GetMapping("/login") // Solo Get
	public String sayHi() {
		return "Hi";
	}
	
	@PutMapping("/login/{username}") // Solo Put (Es para hacer updates)
	public void updateUser(@RequestBody User user,@PathVariable String username) {
		userService.updateUser(user,username);
	}
	
	@DeleteMapping("/login/{username}") // Solo Delete (Es para hacer borrados)
	public void deleteUser(@PathVariable String username) {
		userService.deleteUser(username);
	}
	
	@RequestMapping("/list") // Todos los métodos
	public List<User> sayList() {
		return userService.getUsers();
	}
	
	
}
