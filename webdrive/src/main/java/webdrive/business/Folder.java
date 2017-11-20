package webdrive.business;

import java.util.ArrayList;

public class Folder {
	
	private Folder parent = null;
	private String name;
	private String secondUsername;
	private ArrayList<Folder> childs = new ArrayList();
	private ArrayList<File> files = new ArrayList();
	
	public Folder(Folder parent, String name) {
		this.parent = parent;
		this.name = name;
	}

	public Folder() {
		
	}

	

	public Folder getParent() {
		return parent;
	}

	public void setParent(Folder parent) {
		this.parent = parent;
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

	public ArrayList<File> getFiles() {
		return files;
	}

	public void setFiles(ArrayList<File> files) {
		this.files = files;
	}

	public String getSecondUsername() {
		return secondUsername;
	}

	public void setSecondUsername(String secondUsername) {
		this.secondUsername = secondUsername;
	}
	
	
	
	
	
	
}
