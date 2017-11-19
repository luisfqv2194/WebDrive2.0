package webdrive.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import webdrive.business.User;
import webdrive.services.FileService;
import webdrive.services.UserService;

@Controller
@RequestMapping("/home")
@SessionAttributes("userInSession")
public class HomeController {
	
	public static String username = "";
	
	@Autowired
	private UserService userService;
	
	
	
	@Autowired
	private FileService fileService;
	
	@ModelAttribute("userInSession")
	public User getUserInSession(@ModelAttribute("username") String username) {
		User user = new User();
		user.setUsername(username);
		return user;
		
	}
	
	@GetMapping("") 
	public String loadHomePage(@ModelAttribute("message") String message, Model model) {
		
		model.addAttribute("message", message);
		return "homepage";
	}
	
	 @PostMapping("/upload") 
	    public String singleFileUpload(@RequestParam("file") MultipartFile file,
	                                   RedirectAttributes redirectAttributes) {
		 if (file.isEmpty()) {
	            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
	            return "redirect:/home";
	        }
		 
		 fileService.uploadFile(file);
		 
		 return "redirect:/home";
	 }
	
	
}
