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

@Service 
public class DriveService {
	
	@Autowired
	ResourceLoader loader;
	
	private static final String drivesJsonPath = "classpath:static/drives.json";

	public void addDrive(Drive userDrive, String username) {
		JSONObject jsonObject = readDrives();
		JSONObject jsonDrive = new JSONObject();
		JSONArray arrayDrives = (JSONArray) jsonObject.get("arrayDrives");
		Resource resource = loader.getResource(drivesJsonPath);
		jsonDrive.put("username", username);
		jsonDrive.put("size", userDrive.getSpace());
		jsonDrive.put("freesize", userDrive.getFreeSpace());
		arrayDrives.add(jsonDrive);
		jsonObject = new JSONObject();
		jsonObject.put("arrayDrives", arrayDrives);
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
	
	public JSONObject readDrives() {
		JSONParser parser = new JSONParser();
		Resource resource = loader.getResource(drivesJsonPath);
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

	public Drive getUserDrive(String username) {
		JSONObject jsonObject = readDrives();
		JSONArray arrayDrives = (JSONArray) jsonObject.get("arrayDrives");
		Iterator<JSONObject> driveIterator = arrayDrives.iterator();
		while(driveIterator.hasNext()) {
		    JSONObject driveRecord = driveIterator.next();
		    
		    if(driveRecord.get("username").equals(username)) {
		    	Drive drive = new Drive();
		    	drive.setSpace(Long.valueOf(String.valueOf((driveRecord.get("size")))));
		    	drive.setFreeSpace(Long.valueOf(String.valueOf((driveRecord.get("freesize")))));
		        return drive;
		    }
		    
		}
		return null;
	}
}
