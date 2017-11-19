package webdrive.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import webdrive.business.User;

@Service   //Debe ser un singleton
public class UserService {
	
	@Autowired
	ResourceLoader loader;
	
	
	// WebDrive2.0\webdrive\target\classes\static es la ruta de los archivos .json
	private static final String usersJsonPath = "classpath:static/users.json"; 
	
	

	public void addUser(User user) {
		JSONObject jsonObject = readUsers();
		JSONObject jsonUser = new JSONObject();
		JSONArray arrayUsers = (JSONArray) jsonObject.get("arrayUsers");
		Resource resource = loader.getResource(usersJsonPath);
		jsonUser.put("username", user.getUsername());
		jsonUser.put("password", user.getPassword());
		arrayUsers.add(jsonUser);
		jsonObject = new JSONObject();
		jsonObject.put("arrayUsers", arrayUsers);
		try {
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

	public boolean logIn(User user) {
		
		JSONObject jsonObject = readUsers();
		JSONArray arrayUsers = (JSONArray) jsonObject.get("arrayUsers");
		Iterator<JSONObject> userIterator = arrayUsers.iterator();
		while(userIterator.hasNext()) {
		    JSONObject userRecord = userIterator.next();
		    
		    if(userRecord.get("username").equals(user.getUsername()) 
		            && userRecord.get("password").equals(user.getPassword())) { 
		        
		        return true;
		    }
		    
		}
		
		return false;
		
		
	}
	
	public JSONObject readUsers() {
		JSONParser parser = new JSONParser();
		Resource resource = loader.getResource(usersJsonPath);
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
