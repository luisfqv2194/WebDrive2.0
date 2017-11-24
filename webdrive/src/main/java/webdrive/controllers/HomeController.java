package webdrive.controllers;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
	
	@PostMapping("/file")
	public String loadFileContent(Model model, @ModelAttribute("userInSession") User userInSession, @RequestParam("fileName") String fileName) {
		FileDrive file = userInSession.getMyDrive().getCurrentFolder().getFile(fileName);
		
		model.addAttribute("fileName",file.getName());
		String[] parts = file.getName().split("\\.");
		model.addAttribute("extension",parts[1]);
		model.addAttribute("size",file.getSize());
		model.addAttribute("data",file.getData());
		model.addAttribute("last_modified",file.getLast_modified());
		model.addAttribute("secondUsername",file.getSecondUsername());
		model.addAttribute("filePath",file.getPath());
		return "file";
	}
	

	@PostMapping("/file/update") 
	public String updateFile(RedirectAttributes redirectAttributes, Model model, @ModelAttribute("userInSession") User userInSession, @RequestParam("fileName") String fileName,
			@RequestParam("data") String Filedata, @RequestParam("last_modified") String last_modified, @RequestParam("size") String fileSize, 
			@RequestParam("secondUsername") String secondUsername, @RequestParam("filePath") String filePath) {
		FileDrive newFile = new FileDrive(fileName, (long) Filedata.length(), new Date().getTime(), Filedata, userInSession.getMyDrive().getCurrentFolder());
		newFile.setSecondUsername((secondUsername.equals("") || secondUsername.equals("null")) ? null : secondUsername);
		newFile.setPath(filePath);
		Long realFreeSpace = userInSession.getMyDrive().getFreeSpace() + Long.valueOf(fileSize);
		if((realFreeSpace-newFile.getSize()) < 0) {
			
			redirectAttributes.addFlashAttribute("message","File is to big!");
			return "redirect:/home";
		}
		else {
			fileService.updateFile(newFile,userInSession.getUsername());
			String currentFolder = userInSession.getMyDrive().getCurrentFolder().getPath();
			userInSession.getMyDrive().setFreeSpace(realFreeSpace - newFile.getSize());
			driveService.updateDrive(userInSession.getMyDrive(), userInSession.getUsername());
			userInSession.setMyDrive(driveService.getUserDrive(userInSession.getUsername()));
			userInSession.getMyDrive().setRoot(folderService.getUserFolders(userInSession.getUsername()));
			userInSession.getMyDrive().setCurrentFolder(userInSession.getMyDrive().getRoot());
			redirectAttributes.addFlashAttribute("message","File updated!");
			return "redirect:/home";
		}
		
	}
	@GetMapping("/list") 
	public String loadListPage(Model model,  @ModelAttribute("userInSession") User userInSession) {
		return "list";
	}
	@GetMapping("/create") 
	public String loadCreatePage(Model model) {
		return "create";
	}
	@GetMapping("/copy") 
	public String loadCopyPage(Model model) {
		return "copy";
	}
	@RequestMapping("/copy/{type}") 
	public String loadCopyPage(Model model, @PathVariable("type") String copyType) {
		if(copyType.equals("rv")) {
			return "copyRV";
		}
		else if (copyType.equals("vv")){
			return "copyVV";
		}
		else {
			return "redirect:/home";
		}
	}
	@GetMapping("/delete") 
	public String loadDeletePage(Model model) {
		return "delete";
	}
	
	@GetMapping("/deletefolder") 
	public String loadDeleteFolderPage(Model model) {
		return "deletefolder";
	}
	
	@PostMapping("/deletefolder") 
	public String deleteFolder(RedirectAttributes redirectAttributes, @ModelAttribute("userInSession") User userInSession) {
		Folder currentFolder = userInSession.getMyDrive().getCurrentFolder();
		long spaceToFree = currentFolder.getTotalSize();
		
		userInSession.getMyDrive().setFreeSpace(userInSession.getMyDrive().getFreeSpace() + spaceToFree);
		folderService.deleteFolder(currentFolder,userInSession.getUsername());
		driveService.updateDrive(userInSession.getMyDrive(), userInSession.getUsername());
		userInSession.setMyDrive(driveService.getUserDrive(userInSession.getUsername()));
		userInSession.getMyDrive().setRoot(folderService.getUserFolders(userInSession.getUsername()));
		userInSession.getMyDrive().setCurrentFolder(userInSession.getMyDrive().getRoot());
		return "redirect:/home";
			
	}
	
	@GetMapping("/deletefile") 
	public String loadDeleteFilePage(Model model) {
		return "deletefile";
	}
	
	@PostMapping("/deletefile") 
	public String deleteFile(RedirectAttributes redirectAttributes, @ModelAttribute("userInSession") User userInSession,
			@RequestParam("fileName") String fileName) {
		Folder currentFolder = userInSession.getMyDrive().getCurrentFolder();
		FileDrive file = currentFolder.getFile(fileName);
		if(file == null) {
			redirectAttributes.addFlashAttribute("err","File doesn't exits!");
			userInSession.setMyDrive(driveService.getUserDrive(userInSession.getUsername()));
			userInSession.getMyDrive().setRoot(folderService.getUserFolders(userInSession.getUsername()));
			userInSession.getMyDrive().setCurrentFolder(currentFolder);
			return "redirect:/home/deletefile";
		}
		else {
				long spaceToFree = file.getSize();
				userInSession.getMyDrive().setFreeSpace(userInSession.getMyDrive().getFreeSpace() + spaceToFree);
				fileService.deleteFile(file, userInSession.getUsername());
				driveService.updateDrive(userInSession.getMyDrive(), userInSession.getUsername());
				userInSession.setMyDrive(driveService.getUserDrive(userInSession.getUsername()));
				userInSession.getMyDrive().setRoot(folderService.getUserFolders(userInSession.getUsername()));
				userInSession.getMyDrive().setCurrentFolder(userInSession.getMyDrive().getRoot());
				return "redirect:/home";
			}
	}
	
	@GetMapping("/move") 
	public String loadmovePage(Model model) {
		return "move";
	}
	
	@GetMapping("/movefile") 
	public String loadmoveFilePage(Model model) {
		return "moveFile";
	}
	
	@PostMapping("/movefile") 
	public String moveFile(RedirectAttributes redirectAttributes, @ModelAttribute("userInSession") User userInSession,
			Model model, @RequestParam("fileName") String fileName, @RequestParam("targetFolderPath") String folderPath) {
		Folder targetFolder = userInSession.getMyDrive().moveToChild(folderPath);
		Folder currentFolder = userInSession.getMyDrive().getCurrentFolder();
		FileDrive file = currentFolder.getFile(fileName);
		if(file == null) {
			redirectAttributes.addFlashAttribute("err","File doesn't exits!");
			userInSession.setMyDrive(driveService.getUserDrive(userInSession.getUsername()));
			userInSession.getMyDrive().setRoot(folderService.getUserFolders(userInSession.getUsername()));
			userInSession.getMyDrive().setCurrentFolder(currentFolder);
			return "redirect:/home/movefile";
		}
		else {
				String targetPath = targetFolder.getPath();
				FileDrive updatedFile = new FileDrive();
				updatedFile.setParent(targetFolder);
				updatedFile.setName(file.getName());
				updatedFile.setData(file.getData());
				updatedFile.setSecondUsername(file.getSecondUsername());
				updatedFile.setSize(file.getSize());
				updatedFile.setLast_modified(file.getLast_modified());
				fileService.moveFilePath(file, updatedFile, userInSession.getUsername());
				userInSession.setMyDrive(driveService.getUserDrive(userInSession.getUsername()));
				userInSession.getMyDrive().setRoot(folderService.getUserFolders(userInSession.getUsername()));
				userInSession.getMyDrive().setCurrentFolder(userInSession.getMyDrive().moveToChild(targetPath));
				return "redirect:/home";
			}
		
	}
	
	@GetMapping("/movefolder") 
	public String loadmoveFolderPage(Model model) {
		return "moveFolder";
	}
	
	@PostMapping("/movefolder") 
	public String moveFolder(RedirectAttributes redirectAttributes, @ModelAttribute("userInSession") User userInSession,
			@RequestParam("targetFolderPath") String folderPath) {
		Folder targetFolder = userInSession.getMyDrive().moveToChild(folderPath);
		Folder currentFolder = userInSession.getMyDrive().getCurrentFolder();
		
		if(targetFolder == null) {
			redirectAttributes.addFlashAttribute("err","Folder doesn't exits!");
			userInSession.setMyDrive(driveService.getUserDrive(userInSession.getUsername()));
			userInSession.getMyDrive().setRoot(folderService.getUserFolders(userInSession.getUsername()));
			userInSession.getMyDrive().setCurrentFolder(currentFolder);
			return "redirect:/home/movefolder";
		}
		else {
				String targetPath = targetFolder.getPath();
				folderService.copyVVOrMove(currentFolder,targetFolder,userInSession.getUsername(),2);
				userInSession.setMyDrive(driveService.getUserDrive(userInSession.getUsername()));
				userInSession.getMyDrive().setRoot(folderService.getUserFolders(userInSession.getUsername()));
				userInSession.getMyDrive().setCurrentFolder(userInSession.getMyDrive().moveToChild(targetPath));
				return "redirect:/home";
			}
	}
	
	@GetMapping("/vvcopyfile") 
	public String loadCopyVVFilePage(Model model) {
		return "copyvvFile";
	}
	@PostMapping("/vvcopyfile") 
	public String loadCopyVVFile(RedirectAttributes redirectAttributes, @ModelAttribute("userInSession") User userInSession,
			Model model, @RequestParam("fileName") String fileName, @RequestParam("targetFolderPath") String folderPath) {
		Folder targetFolder = userInSession.getMyDrive().moveToChild(folderPath);
		Folder currentFolder = userInSession.getMyDrive().getCurrentFolder();
		FileDrive file = currentFolder.getFile(fileName);
		if(file == null) {
			redirectAttributes.addFlashAttribute("err","File doesn't exits!");
			userInSession.setMyDrive(driveService.getUserDrive(userInSession.getUsername()));
			userInSession.getMyDrive().setRoot(folderService.getUserFolders(userInSession.getUsername()));
			userInSession.getMyDrive().setCurrentFolder(currentFolder);
			return "redirect:/home/vvcopyfile";
		}
		else {
			long freeSpace = userInSession.getMyDrive().getFreeSpace() - file.getSize();
			if(freeSpace < 0) {
				redirectAttributes.addFlashAttribute("err","Not enough space for copy operation!");
				userInSession.setMyDrive(driveService.getUserDrive(userInSession.getUsername()));
				userInSession.getMyDrive().setRoot(folderService.getUserFolders(userInSession.getUsername()));
				userInSession.getMyDrive().setCurrentFolder(currentFolder);
				return "redirect:/home/vvcopyfile";
			}
			else {
				String targetPath = targetFolder.getPath();
				file.setParent(targetFolder);
				fileService.addFile(file, userInSession.getUsername());
				userInSession.getMyDrive().setFreeSpace(freeSpace);
				driveService.updateDrive(userInSession.getMyDrive(), userInSession.getUsername());
				userInSession.setMyDrive(driveService.getUserDrive(userInSession.getUsername()));
				userInSession.getMyDrive().setRoot(folderService.getUserFolders(userInSession.getUsername()));
				userInSession.getMyDrive().setCurrentFolder(userInSession.getMyDrive().moveToChild(targetPath));
				return "redirect:/home";
			}
			
		}
		
	}
	@GetMapping("/vvcopyfolder") 
	public String loadCopyVVFolderPage(Model model) {
		return "copyvvFolder";
	}
	@PostMapping("/vvcopyfolder") 
	public String copyVVFolder(RedirectAttributes redirectAttributes, @ModelAttribute("userInSession") User userInSession, Model model, @RequestParam("targetFolderPath") String folderPath) {
		Folder targetFolder = userInSession.getMyDrive().moveToChild(folderPath);
		Folder currentFolder = userInSession.getMyDrive().getCurrentFolder();
		
		if(targetFolder == null) {
			redirectAttributes.addFlashAttribute("err","Folder doesn't exits!");
			userInSession.setMyDrive(driveService.getUserDrive(userInSession.getUsername()));
			userInSession.getMyDrive().setRoot(folderService.getUserFolders(userInSession.getUsername()));
			userInSession.getMyDrive().setCurrentFolder(currentFolder);
			return "redirect:/home/vvcopyfolder";
		}
		else {
			long folderSize = currentFolder.getTotalSize();
			if((userInSession.getMyDrive().getFreeSpace()-folderSize) < 0) {
				redirectAttributes.addFlashAttribute("err","Not enough space for copy operation!");
				userInSession.setMyDrive(driveService.getUserDrive(userInSession.getUsername()));
				userInSession.getMyDrive().setRoot(folderService.getUserFolders(userInSession.getUsername()));
				userInSession.getMyDrive().setCurrentFolder(currentFolder);
				return "redirect:/home/vvcopyfolder";
			}
			else {
				String targetPath = targetFolder.getPath();
				userInSession.getMyDrive().setFreeSpace(userInSession.getMyDrive().getFreeSpace() - folderSize);
				driveService.updateDrive(userInSession.getMyDrive(), userInSession.getUsername());
				folderService.copyVVOrMove(currentFolder,targetFolder,userInSession.getUsername(),1);
				userInSession.setMyDrive(driveService.getUserDrive(userInSession.getUsername()));
				userInSession.getMyDrive().setRoot(folderService.getUserFolders(userInSession.getUsername()));
				userInSession.getMyDrive().setCurrentFolder(userInSession.getMyDrive().moveToChild(targetPath));
				return "redirect:/home";
			}
			
			
		}
		
	}
	@GetMapping("/surf") 
	public String loadSurfDrivePage(Model model) {
		return "navigation";
	}
	@PostMapping("/surf") 
	public String surfToFolder(RedirectAttributes redirectAttributes, @ModelAttribute("userInSession") User userInSession, Model model, @RequestParam("folderPath") String folderPath) {
		
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
			System.out.println("No deberia entrar aqui!");
			return "redirect:/home/create/file";
		}
		else {
			String currentFolder = userInSession.getMyDrive().getCurrentFolder().getPath() + userInSession.getMyDrive().getCurrentFolder().getName() + "/";
			System.out.println("currentFolder TIENE el path: " + currentFolder);
			fileService.addFile(newFile, userInSession.getUsername());
			userInSession.getMyDrive().setFreeSpace(userInSession.getMyDrive().getFreeSpace() - newFile.getSize());
			
			// Update al json
			driveService.updateDrive(userInSession.getMyDrive(), userInSession.getUsername());
			userInSession.setMyDrive(driveService.getUserDrive(userInSession.getUsername()));
			userInSession.getMyDrive().setRoot(folderService.getUserFolders(userInSession.getUsername()));
			userInSession.getMyDrive().setCurrentFolder(userInSession.getMyDrive().getRoot());
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
		 
		 long fileSize = fileService.uploadFile(file, userInSession.getUsername(), userInSession.getMyDrive().getCurrentFolder(), userInSession.getMyDrive().getFreeSpace());
		 String currentFolder = userInSession.getMyDrive().getCurrentFolder().getPath();
		 if(fileSize != 0) {
			 userInSession.getMyDrive().setFreeSpace(userInSession.getMyDrive().getFreeSpace() - fileSize);
				driveService.updateDrive(userInSession.getMyDrive(), userInSession.getUsername());
				userInSession.setMyDrive(driveService.getUserDrive(userInSession.getUsername()));
				userInSession.getMyDrive().setRoot(folderService.getUserFolders(userInSession.getUsername()));
				userInSession.getMyDrive().setCurrentFolder(userInSession.getMyDrive().moveToChild(currentFolder));
				return "redirect:/home";
		 }
		 else {
			 userInSession.setMyDrive(driveService.getUserDrive(userInSession.getUsername()));
			 userInSession.getMyDrive().setRoot(folderService.getUserFolders(userInSession.getUsername()));
			 userInSession.getMyDrive().setCurrentFolder(userInSession.getMyDrive().moveToChild(currentFolder));
			 redirectAttributes.addFlashAttribute("message","File is too big!");
			 return "redirect:/home";
		 }
		
			
			
		 
	 }
	
	
}
