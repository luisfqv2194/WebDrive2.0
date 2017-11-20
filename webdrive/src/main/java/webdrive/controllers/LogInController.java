package webdrive.controllers; // Para leer los controladores el paquete debe ser un subpaquete del paquete principal


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import webdrive.business.Drive;
import webdrive.business.User;
import webdrive.services.DriveService;
import webdrive.services.FolderService;
import webdrive.services.UserService;

@Controller
public class LogInController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private DriveService driveService;
	
	@Autowired
	private FolderService folderService;
	
	
	
	@PostMapping("/singup")
	public String addUser(@RequestParam("username") String username, @RequestParam("password") String password, @RequestParam("space") String space) {
		User user = new User(username,password);
		user.setMyDrive(new Drive(Long.valueOf(space)));
		// Creo un usuario en el json correspondiente
		userService.addUser(user);
		// Creo el drive del usuario en el json correspondiente
		driveService.addDrive(user.getMyDrive(), user.getUsername());
		//Creo carpeta "Raiz" del drive en el json correspondiente
		folderService.addFolder(user.getMyDrive().getRoot(), username);
		//Creo carpeta "Shared" dentro de "Raiz" en el json correspondiente
		folderService.addFolder(user.getMyDrive().getRoot().getChilds().get(0), username);
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
			attributes.addFlashAttribute("message","");
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
