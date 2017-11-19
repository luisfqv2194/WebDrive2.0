package webdrive.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import webdrive.services.UserService;

@Controller
@RequestMapping("/home")
public class HomeController {
	
	public static String username = "";
	
	@Autowired
	private UserService userService;

	@GetMapping("") 
	public String loadHomePage(@ModelAttribute("username") String username, Model model) {
		HomeController.username = (HomeController.username.equals("") ? username : HomeController.username);
		model.addAttribute("username","Welcome back, " + username);
		return "homepage";
	}
	
	
}
