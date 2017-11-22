package webdrive.controllers;

import java.util.Date;

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

import webdrive.business.FileDrive;
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
		user.getMyDrive().setCurrentFolder(user.getMyDrive().getRoot());
		return user;
		
	}
	
	@GetMapping("/create") 
	public String loadCreatePage(Model model) {
		return "create";
	}
	@GetMapping("/surf") 
	public String loadSurfDrivePage(Model model) {
		return "navigation";
	}
	@PostMapping("/surf") 
	public String surfToFolder(RedirectAttributes redirectAttributes, @ModelAttribute("userInSession") User userInSession, Model model, @RequestParam("folderPath") String folderPath) {
		System.out.println("El nuevo path es: " + folderPath);
		Folder oldCurrentFolder = userInSession.getMyDrive().getCurrentFolder();
		Folder newCurrentFolder = userInSession.getMyDrive().moveToChild(folderPath);
		if (newCurrentFolder == null) {
			redirectAttributes.addFlashAttribute("err","La carpeta no existe!");
			userInSession.setMyDrive(driveService.getUserDrive(userInSession.getUsername()));
			userInSession.getMyDrive().setRoot(folderService.getUserFolders(userInSession.getUsername()));
			userInSession.getMyDrive().setCurrentFolder(oldCurrentFolder);
			return "redirect:/home/surf";
		}
		else {
			userInSession.setMyDrive(driveService.getUserDrive(userInSession.getUsername()));
			userInSession.getMyDrive().setRoot(folderService.getUserFolders(userInSession.getUsername()));
			userInSession.getMyDrive().setCurrentFolder(newCurrentFolder);
			return "redirect:/home";
		}
		
	}
	
	@GetMapping("/create/folder") 
	public String loadCrreateFolderPage(Model model) {
		return "createFolder";
	}
	
	@PostMapping("/create/folder") 
	public String addNewFolder(HttpServletRequest request, @RequestParam("folderName") String folderName, @ModelAttribute("userInSession") User userInSession, Model model) {
		Folder newFolder = new Folder(userInSession.getMyDrive().getCurrentFolder(),folderName);
		
		folderService.addFolder(newFolder, userInSession.getUsername());
		userInSession.setMyDrive(driveService.getUserDrive(userInSession.getUsername()));
		userInSession.getMyDrive().setRoot(folderService.getUserFolders(userInSession.getUsername()));
		
		userInSession.getMyDrive().setCurrentFolder(newFolder);
		request.getSession().setAttribute("userInSession", userInSession);
		
		return "redirect:/home";
	}
	
	@GetMapping("/create/file") 
	public String loadCreateFilePage(Model model) {
		return "createFile";
	}
	
	@PostMapping("/create/file") 
	public String addNewFile(RedirectAttributes redirectAttributes, HttpServletRequest request, @RequestParam("data") String Filedata,
			@RequestParam("fileName") String fileName, @ModelAttribute("userInSession") User userInSession, Model model) {
		
		FileDrive newFile = new FileDrive(fileName, (long) Filedata.length(), new Date().getTime(), Filedata, userInSession.getMyDrive().getCurrentFolder());
		if ((userInSession.getMyDrive().getFreeSpace()-newFile.getSize()) < 0) {
			redirectAttributes.addFlashAttribute("err", "Not enough space!");
			return "redirect:/home/create/file";
		}
		else {
			Folder currentFolder = userInSession.getMyDrive().getCurrentFolder();
			fileService.addFile(newFile, userInSession.getUsername());
			userInSession.getMyDrive().setFreeSpace(userInSession.getMyDrive().getFreeSpace() - newFile.getSize());
			
			// Update al json
			driveService.updateDrive(userInSession.getMyDrive(), userInSession.getUsername());
			userInSession.setMyDrive(driveService.getUserDrive(userInSession.getUsername()));
			userInSession.getMyDrive().setRoot(folderService.getUserFolders(userInSession.getUsername()));
			userInSession.getMyDrive().setCurrentFolder(currentFolder);
			return "redirect:/home";
		}
	}
	
	
	

	@GetMapping("") 
	public String loadHomePage(Model model) {
		
		return "homepage";
	}
	
	 @PostMapping("/upload") 
	    public String singleFileUpload(@RequestParam("file") MultipartFile file,
	                                   RedirectAttributes redirectAttributes, @ModelAttribute("userInSession") User userInSession) {
		 if (file.isEmpty()) {
	            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
	            return "redirect:/home";
	        }
		 
		 fileService.uploadFile(file, userInSession.getUsername(), userInSession.getMyDrive().getCurrentFolder());
		 
		 return "redirect:/home";
	 }
	
	
}
