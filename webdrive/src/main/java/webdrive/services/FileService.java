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
	
	public void uploadFile(MultipartFile multipartfile) {
		File convertedFile = convert(multipartfile);
		InputStream isFile;
		String dataTXT;
		JSONObject jsonObject = this.readFiles();
		JSONArray arrayFiles = (JSONArray) jsonObject.get("arrayFiles");
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
}
