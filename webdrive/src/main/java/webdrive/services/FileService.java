package webdrive.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import webdrive.business.Folder;
import webdrive.business.FileDrive;



@Service 
public class FileService {

	@Autowired
	ResourceLoader loader;
	
	
	// WebDrive2.0\webdrive\target\classes\static es la ruta de los archivos .json
	private static final String filesJsonPath = "classpath:static/files.json";
	private static final String filesUploadFolder = "C:\\Users\\XPC\\Documents\\GitHub\\WebDrive2.0\\files\\";
	
	public File convert(MultipartFile file)
	{    
	    File convFile = new File(file.getOriginalFilename());
	    try {
			convFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(convFile); 
		    fos.write(file.getBytes());
		    fos.close(); 
		    
		} catch (java.io.IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	    return convFile;
	}
	
	public void writeFile(JSONObject jsonObject) {
		
        try {
        	Resource resource = loader.getResource(filesJsonPath);
    		File file = resource.getFile();
    		FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
			bw.write(jsonObject.toJSONString());
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
	}
	
	public long uploadFile(MultipartFile multipartfile, String username, Folder parent, long driveFreeSpace) {
		File convertedFile = convert(multipartfile);
		InputStream isFile;
		String dataTXT;
		JSONObject jsonObject = this.readFiles();
		JSONObject jsonParent = new JSONObject();
		jsonParent.put("username", username);
		jsonParent.put("name", (parent == null ? "nil" : parent.getName()));
		jsonParent.put("path", (parent == null ? "nil" : parent.getPath()));
		JSONArray arrayFiles = (JSONArray) jsonObject.get("arrayFiles");
		
		if(driveFreeSpace - convertedFile.length() < 0) {
			return 0;
		}
		long res = convertedFile.length();
		try {
			
			isFile = new FileInputStream(convertedFile);
			dataTXT = new BufferedReader(new InputStreamReader(isFile)).lines().collect(Collectors.joining("\n"));
			isFile.close();
			JSONObject newFileRecord = new JSONObject();
			newFileRecord.put("name", convertedFile.getName());
			newFileRecord.put("size", convertedFile.length());
			newFileRecord.put("last_modified", convertedFile.lastModified());
			newFileRecord.put("secondUsername", "nil");
			newFileRecord.put("data", dataTXT);
			newFileRecord.put("username", username);
			newFileRecord.put("parent", jsonParent);
			String filePath = parent.getPath() + parent.getName() + "/";
			newFileRecord.put("path", (parent.getParent() == null ? parent.getName() + "/" : filePath));
			arrayFiles.add(newFileRecord);
			jsonObject = new JSONObject();
			jsonObject.put("arrayFiles", arrayFiles);
			writeFile(jsonObject);
			
			
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
		 
	}
	
	public void addFile(FileDrive fileDrive, String username) {
		
		String dataTXT = fileDrive.getData();
		JSONObject jsonObject = this.readFiles();
		JSONObject jsonParent = new JSONObject();
		jsonParent.put("username", username);
		jsonParent.put("name", (fileDrive.getParent() == null ? "nil" : fileDrive.getParent().getName()));
		jsonParent.put("path", (fileDrive.getParent() == null ? "nil" : fileDrive.getParent().getPath()));
		JSONArray arrayFiles = (JSONArray) jsonObject.get("arrayFiles");
		JSONObject newFileRecord = new JSONObject();
		newFileRecord.put("name", fileDrive.getName());
		newFileRecord.put("size", fileDrive.getSize());
		newFileRecord.put("last_modified", fileDrive.getLast_modified());
		newFileRecord.put("secondUsername", "nil");
		newFileRecord.put("data", dataTXT);
		newFileRecord.put("username", username);
		newFileRecord.put("parent", jsonParent);
		String filePath = fileDrive.getParent().getPath() + fileDrive.getParent().getName() + "/";
		newFileRecord.put("path", (fileDrive.getParent().getParent() == null ? fileDrive.getParent().getName() + "/" : filePath));
		arrayFiles.add(newFileRecord);
		jsonObject = new JSONObject();
		jsonObject.put("arrayFiles", arrayFiles);
		writeFile(jsonObject);
		 
	}
	
	
	
	public JSONObject readFiles() {
		JSONParser parser = new JSONParser();
		Resource resource = loader.getResource(filesJsonPath);
		JSONObject jsonObject = new JSONObject();
		String result;
		try {
			result = new BufferedReader(new InputStreamReader(resource.getInputStream())).lines().collect(Collectors.joining("\n"));
			jsonObject = (JSONObject) parser.parse(result);
			return jsonObject;
		} catch (IOException e) {
			
			e.printStackTrace();
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		return jsonObject;
	}

	public ArrayList<FileDrive> getUserFiles(Folder parent, String username) {
		ArrayList<FileDrive> files = new ArrayList();
		JSONObject jsonObject = this.readFiles();
		JSONArray arrayFiles = (JSONArray) jsonObject.get("arrayFiles");
		Iterator<JSONObject> fileIterator = arrayFiles.iterator();
		while(fileIterator.hasNext()) {
			JSONObject fileRecord = fileIterator.next();
			if (parent.getParent() == null && parent.getName().equals("root")) {
				
				if(fileRecord.get("username").equals(username) && fileRecord.get("path").equals(parent.getPath() + "/")) {
					FileDrive newFile = new FileDrive(String.valueOf(fileRecord.get("name")),Long.valueOf(String.valueOf(fileRecord.get("size"))),
							Long.valueOf(String.valueOf(fileRecord.get("last_modified"))), String.valueOf(fileRecord.get("data")), parent);
					newFile.setPath(String.valueOf(fileRecord.get("path")));
					files.add(newFile);
				}
			}
			
			else {
				
				if(fileRecord.get("username").equals(username) && fileRecord.get("path").equals(parent.getPath() + parent.getName() + "/")) {
					FileDrive newFile = new FileDrive(String.valueOf(fileRecord.get("name")),Long.valueOf(String.valueOf(fileRecord.get("size"))),
							Long.valueOf(String.valueOf(fileRecord.get("last_modified"))), String.valueOf(fileRecord.get("data")), parent);
					newFile.setPath(String.valueOf(fileRecord.get("path")));
					files.add(newFile);
				}
			}
			
		}
		return files;
	}

	public void updateFile(FileDrive updatedFile, String username) {
		System.out.println("La data es: " + updatedFile.getData());
		JSONObject jsonObject = this.readFiles();
		JSONArray arrayFiles = (JSONArray) jsonObject.get("arrayFiles");
		JSONArray arrayFilesResult = new JSONArray();
		Iterator<JSONObject> fileIterator = arrayFiles.iterator();
		while(fileIterator.hasNext()) {
			JSONObject fileRecord = fileIterator.next();
			
			if(fileRecord.get("username").equals(username) && fileRecord.get("name").equals(updatedFile.getName()) 
					&& fileRecord.get("path").equals(updatedFile.getPath())) {
				JSONObject newFileRecord = new JSONObject();
				JSONObject jsonParent = new JSONObject();
				jsonParent.put("username", username);
				jsonParent.put("name", updatedFile.getParent().getName());
				jsonParent.put("path", updatedFile.getParent().getPath());
				newFileRecord.put("name", updatedFile.getName());
				newFileRecord.put("size", updatedFile.getSize());
				newFileRecord.put("last_modified", updatedFile.getLast_modified());
				newFileRecord.put("secondUsername", updatedFile.getSecondUsername());
				newFileRecord.put("data", updatedFile.getData());
				newFileRecord.put("username", username);
				newFileRecord.put("parent", jsonParent);
				newFileRecord.put("path", updatedFile.getPath());
				arrayFilesResult.add(newFileRecord);
				
			}
			else {
				arrayFilesResult.add(fileRecord);
			}
			
		}
		jsonObject = new JSONObject();
		jsonObject.put("arrayFiles", arrayFilesResult);
		writeFile(jsonObject);
		
	}

	public void updateFilePath(FileDrive oldFile, FileDrive updatedFile, String username) {
		
		JSONObject jsonObject = this.readFiles();
		JSONArray arrayFiles = (JSONArray) jsonObject.get("arrayFiles");
		JSONArray arrayFilesResult = new JSONArray();
		Iterator<JSONObject> fileIterator = arrayFiles.iterator();
		while(fileIterator.hasNext()) {
			JSONObject fileRecord = fileIterator.next();
			if(fileRecord.get("username").equals(username) && fileRecord.get("name").equals(oldFile.getName()) 
					&& fileRecord.get("path").equals(oldFile.getPath())) {
				JSONObject newFileRecord = new JSONObject();
				JSONObject jsonParent = new JSONObject();
				jsonParent.put("username", username);
				jsonParent.put("name", updatedFile.getParent().getName());
				jsonParent.put("path", updatedFile.getParent().getPath());
				newFileRecord.put("name", updatedFile.getName());
				newFileRecord.put("size", updatedFile.getSize());
				newFileRecord.put("last_modified", updatedFile.getLast_modified());
				newFileRecord.put("secondUsername", updatedFile.getSecondUsername());
				newFileRecord.put("data", updatedFile.getData());
				newFileRecord.put("username", username);
				newFileRecord.put("parent", jsonParent);
				newFileRecord.put("path", updatedFile.getPath());
				arrayFilesResult.add(newFileRecord);
				
			}
			else {
				arrayFilesResult.add(fileRecord);
			}
		}
		jsonObject = new JSONObject();
		jsonObject.put("arrayFiles", arrayFilesResult);
		writeFile(jsonObject);
		
		
			
		
	}
}
