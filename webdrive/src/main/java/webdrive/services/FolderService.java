package webdrive.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
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
		jsonParent.put("name", (userFolder.getParent() == null ? "nil" : userFolder.getParent().getName()));
		jsonParent.put("path", (userFolder.getParent() == null ? "nil" : userFolder.getParent().getPath()));
		
		jsonMe.put("username", username);
		jsonMe.put("name", userFolder.getName());
		jsonMe.put("secondUsername", (userFolder.getSecondUsername() == null ? "nil" : userFolder.getSecondUsername()));
		jsonMe.put("parent", jsonParent);
		jsonMe.put("path", userFolder.getPath());
		
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

	public Folder getUserFolders(String username) {
		
		Folder root = new Folder(null,"root");
		// Load root files TODO code
		
		// Load root folders
		JSONObject jsonObject = readFolders();
		JSONArray arrayFoldersJSON = (JSONArray) jsonObject.get("arrayFolders");
		JSONArray arrayFolders = new JSONArray();
		Iterator<JSONObject> folderIterator = arrayFoldersJSON.iterator();
		while(folderIterator.hasNext()) {
		    JSONObject folderRecord = folderIterator.next();
		    JSONObject parentFolder = (JSONObject) folderRecord.get("parent");
		    
		    if(folderRecord.get("username").equals(username) && !folderRecord.get("name").equals(root.getName())  
		    		&& folderRecord.get("path").equals(root.getPath() + "/")) {
		    	
		    	Folder folder = new Folder(root,String.valueOf(folderRecord.get("name")));
		    	folder.setPath(String.valueOf(folderRecord.get("path")));
		    	
		    	root.getChilds().add(folder);
		        
		    }
		    else if (folderRecord.get("username").equals(username) &&  !parentFolder.get("name").equals("root") && !parentFolder.get("name").equals("nil")){
		    	
		    	arrayFolders.add(folderRecord);
		    }
		    
		}
		
		for(int i = 0;i<root.getChilds().size();i++) {
			
			root.getChilds().set(i,getFolders(root.getChilds().get(i),username,arrayFolders));
			
		}
		
	
		return root;
	}

	private Folder getFolders(Folder pFolderRecord, String username, JSONArray pArrayFolders) {
		
		if(pArrayFolders.size() == 0) {
			return pFolderRecord;
		}
		
		else {
			JSONArray arrayFolders = new JSONArray();
			Iterator<JSONObject> folderIterator = pArrayFolders.iterator();
			while(folderIterator.hasNext()) {
			    JSONObject folderRecord = folderIterator.next();
			    JSONObject parentFolder = (JSONObject) folderRecord.get("parent");
			    
			    if(folderRecord.get("username").equals(username) && folderRecord.get("path").equals(pFolderRecord.getPath() + "/")) {
			    	Folder folder = new Folder(pFolderRecord,String.valueOf(folderRecord.get("name")));
			    	folder.setPath(String.valueOf(folderRecord.get("path")));
			    	
			    	pFolderRecord.getChilds().add(folder);
			        
			    }
			    else if (folderRecord.get("username").equals(username)){
			    	arrayFolders.add(folderRecord);
			    }
			    
			}
			
			for(int i = 0;i<pFolderRecord.getChilds().size();i++) {
				pFolderRecord.getChilds().set(i,getFolders(pFolderRecord.getChilds().get(i),username,arrayFolders));
				
			}
			
			return pFolderRecord;
		}
	}

	

	
	
	
}
