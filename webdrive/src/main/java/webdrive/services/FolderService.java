package webdrive.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import webdrive.business.Drive;
import webdrive.business.Folder;

@Service 
public class FolderService {

	@Autowired
	ResourceLoader loader;
	
	private static final String foldersJsonPath = "classpath:static/folders.json";

	public void addFolder(Folder userFolder, String username) {
		JSONObject jsonObject = readFolders();
		JSONObject jsonParent = new JSONObject();
		JSONObject jsonMe = new JSONObject();
		JSONArray arrayFolders = (JSONArray) jsonObject.get("arrayFolders");
		Resource resource = loader.getResource(foldersJsonPath);
		jsonParent.put("username", username);
		jsonParent.put("name", (userFolder.getParent() == null ? null : userFolder.getParent().getName()));
		
		jsonMe.put("username", username);
		jsonMe.put("name", userFolder.getName());
		jsonMe.put("parent", jsonParent);
		
		arrayFolders.add(jsonMe);
		jsonObject = new JSONObject();
		jsonObject.put("arrayFolders", arrayFolders);
		try {
			File file = resource.getFile();
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(jsonObject.toJSONString());
            bw.close();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}
	
	public JSONObject readFolders() {
		JSONParser parser = new JSONParser();
		Resource resource = loader.getResource(foldersJsonPath);
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
}
