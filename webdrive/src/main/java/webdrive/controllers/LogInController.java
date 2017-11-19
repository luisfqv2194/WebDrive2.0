package webdrive.controllers; // Para leer los controladores el paquete debe ser un subpaquete del paquete principal

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import webdrive.business.User;
import webdrive.services.UserService;

@Controller
public class LogInController {
	
	@Autowired
	private UserService userService;
	
	@RequestMapping("/login/{username}") // Todos los métodos
	@ResponseBody
	public User getUser(@PathVariable String username) {
		
		return userService.getUser(username);
	}
	
	@PostMapping("/login")  // Solo Post
	@ResponseBody
	public void addUser(@RequestBody User user) {
		userService.addUser(user);
	}
	
	@RequestMapping("") 
	public String index() {
		return "index";
	}
	
	@PutMapping("/login/{username}") // Solo Put (Es para hacer updates)
	@ResponseBody
	public void updateUser(@RequestBody User user,@PathVariable String username) {
		userService.updateUser(user,username);
	}
	
	@DeleteMapping("/login/{username}") // Solo Delete (Es para hacer borrados)
	@ResponseBody
	public void deleteUser(@PathVariable String username) {
		userService.deleteUser(username);
	}
	
	@RequestMapping("/list") // Todos los métodos
	@ResponseBody
	public List<User> sayList() {
		return userService.getUsers();
	}
	
	
}
