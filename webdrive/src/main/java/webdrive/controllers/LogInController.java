package webdrive.controllers; // Para leer los controladores el paquete debe ser un subpaquete del paquete principal

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
	
	@GetMapping("") 
	public String index() {
		return "index";
	}
	
	@PostMapping("") 
	public String logIn(@RequestParam("username") String username, @RequestParam("password") String password, Model model, RedirectAttributes attributes) {
		User user = new User(username,password);
		if(userService.logIn(user)) {
			attributes.addFlashAttribute("username",user.getUsername());
			attributes.addAttribute("usuario",user.getUsername());
			return "redirect:/home";
		}
		else if(user.getUsername().equals("") || user.getPassword().equals("")) {
			model.addAttribute("err","Los campos no pueden estar vacios");
			return "index";
		}
		else {
			model.addAttribute("err","El usuario no existe");
			return "index";
		}
		
		
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
	
	@GetMapping("/home") 
	public String loadHomePage(@ModelAttribute("username") String username, Model model) {
		model.addAttribute("username",username);
		return "homepage";
	}
	
	
}
