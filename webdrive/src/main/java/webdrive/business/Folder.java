package webdrive.business;

import java.util.ArrayList;
import java.util.Iterator;

public class Folder {
	
	private Folder parent = null;
	private String name;
	private String secondUsername = null;
	private ArrayList<Folder> childs = new ArrayList();
	private ArrayList<FileDrive> files = new ArrayList();
	private String path;
	
	
	public Folder(Folder parent, String name) {
		this.parent = parent;
		this.name = name;
		createPath();
	}

	public Folder() {
		
	}

	public void createPath() {
		if (parent == null) {
			path = name;
		}
		else {
			path = "";
			createPathAux(path,parent);
		}
	}

	private void createPathAux(String pPath, Folder pParent) {
		if(pParent == null) {
			path = pPath;
		}
		else {
			createPathAux(pParent.name + '/' + pPath, pParent.parent);
		}
		
	}
	
	public FileDrive getFile(String fileName) {
		Iterator<FileDrive> fileIterator = files.iterator();
		while(fileIterator.hasNext()) {
			FileDrive file = fileIterator.next();
			
			if(file.getName().equals(fileName)) {
				return file;
			}
		}
		return null;
	}
	
	public long getTotalSize() {
		Iterator<FileDrive> fileIterator = files.iterator();
		long resultado = 0;
		while(fileIterator.hasNext()) {
			FileDrive file = fileIterator.next();
			resultado += file.getSize();
		}
		Iterator<Folder> folderIterator = childs.iterator();
		while(folderIterator.hasNext()) {
			Folder child = folderIterator.next();
			resultado += child.getTotalSize();
		}
		
		return resultado;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Folder getParent() {
		return parent;
	}

	public void setParent(Folder parent) {
		this.parent = parent;
		
		if(parent.getPath().equals("root")) {
			this.path = parent.path + "/";
		}
		else {
			this.path = parent.path + parent.name + "/";
		}
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Folder> getChilds() {
		return childs;
	}

	public void setChilds(ArrayList<Folder> childs) {
		this.childs = childs;
	}

	public ArrayList<FileDrive> getFiles() {
		return files;
	}

	public void setFiles(ArrayList<FileDrive> files) {
		this.files = files;
	}

	public String getSecondUsername() {
		return secondUsername;
	}

	public void setSecondUsername(String secondUsername) {
		this.secondUsername = secondUsername;
	}

	
	
	
	
	
	
	
	
	
}
