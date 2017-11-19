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
	
	
	@PostMapping("/singup")
	public String addUser(@RequestParam("username") String username, @RequestParam("password") String password, @RequestParam("space") String space) {
		User user = new User(username,password,Long.valueOf(space));
		userService.addUser(user);
		return "redirect:";
	}
	
	@GetMapping("/singup")  
	public String showRegisterForm() {
		return "register";
	}
	
	@GetMapping("") 
	public String index() {
		return "index";
	}
	
	@PostMapping("") 
	public String logIn(@RequestParam("username") String username, @RequestParam("password") String password, Model model, RedirectAttributes attributes) {
		User user = new User(username,password);
		if(userService.logIn(user)) {
			// Paso variables a HomeController
			attributes.addFlashAttribute("username",user.getUsername());
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
	
	
	
	
	
	
}
