package webdrive.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionAttributeStore;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import webdrive.business.Folder;
import webdrive.business.User;
import webdrive.services.DriveService;
import webdrive.services.FileService;
import webdrive.services.FolderService;
import webdrive.services.UserService;

@Controller
@RequestMapping("/home")
@SessionAttributes("userInSession")
public class HomeController {
	
	public static String username = "";
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private DriveService driveService;
	
	@Autowired
	private FolderService folderService;
	
	@Autowired
	private FileService fileService;
	
	@ModelAttribute("userInSession")
	public User getUserInSession( @ModelAttribute("username") String username) {
		User user = new User();
		user.setUsername(username);
		user.setMyDrive(driveService.getUserDrive(user.getUsername()));
		user.getMyDrive().setRoot(folderService.getUserFolders(user.getUsername()));
		System.out.println("En el controlador sale: " + user.getMyDrive().getRoot().getChilds().get(0).getPath());
		user.getMyDrive().setCurrentFolder(user.getMyDrive().getRoot());
		
		return user;
		
	}
	
	@GetMapping("/create") 
	public String loadCreatePage(Model model) {
		return "create";
	}
	
	@GetMapping("/create/folder") 
	public String loadCrreateFolderPage(Model model) {
		return "createFolder";
	}
	
	@PostMapping("/create/folder") 
	public String addNewFolder(HttpServletRequest request, @RequestParam("folderName") String folderName, @ModelAttribute("userInSession") User userInSession, Model model) {
		Folder newFolder = new Folder(userInSession.getMyDrive().getCurrentFolder(),folderName);
		System.out.println("Al agregar el nuevo folder tengo: " + newFolder.getPath());
		folderService.addFolder(newFolder, userInSession.getUsername());
		userInSession.setMyDrive(driveService.getUserDrive(userInSession.getUsername()));
		userInSession.getMyDrive().setRoot(folderService.getUserFolders(userInSession.getUsername()));
		System.out.println("En la lista de hijos en controller: " + userInSession.getMyDrive().getRoot().getChilds().get(2).getPath());
		userInSession.getMyDrive().setCurrentFolder(newFolder);
		request.getSession().setAttribute("userInSession", userInSession);
		return "redirect:/home";
	}
	
	@GetMapping("/create/file") 
	public String loadCreateFilePage(Model model) {
		return "createFile";
	}
	
	
	@GetMapping("") 
	public String loadHomePage(Model model) {
		
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
