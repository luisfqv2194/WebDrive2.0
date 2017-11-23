package webdrive.business;

import java.util.ArrayList;

public class Drive {
	
	private long space;
	private long freeSpace;
	private Folder root;
	private Folder currentFolder;

	public Drive(long space) {
		this.space = space;
		freeSpace = space;
		root = new Folder(null,"root");
		root.getChilds().add(new Folder(root,"Shared"));
	}
	
	public Drive() {
		
	}
	
	

	public Folder getCurrentFolder() {
		return currentFolder;
	}
	
	

	public long getFreeSpace() {
		return freeSpace;
	}

	public void setFreeSpace(long freeSpace) {
		this.freeSpace = freeSpace;
	}

	public void setCurrentFolder(Folder currentFolder) {
		this.currentFolder = currentFolder;
	}

	public long getSpace() {
		return space;
	}

	public void setSpace(long space) {
		this.space = space;
	}

	public Folder getRoot() {
		return root;
	}

	public void setRoot(Folder root) {
		this.root = root;
	}
	
	
	public Folder moveToChild(String path) {
		
		String[] pathParts = path.split("/");
		ArrayList<String> pathParts2 = new ArrayList();
		for(int i = 0; i < pathParts.length; i++) {
			pathParts2.add(pathParts[i]);
			
			
		}
		if (pathParts[0].equals("root")) {
			return searchForChild(pathParts2,this.root);
			
		}
		else {
			return null;
		}
		
	}

	private Folder searchForChild(ArrayList<String> pathParts,Folder folder) {
		if (pathParts.get(0).equals("root") && pathParts.size() == 1) {
			return root;
		}
		else {
			pathParts.remove(0);
			Folder currentFolder = folder.getChilds().get(0);
			
			
			folder.getChilds().remove(0);
			
			return searchForChildAux(pathParts,root.getChilds(),currentFolder);
		}
	}

	private Folder searchForChildAux(ArrayList<String> pathParts, ArrayList<Folder> siblings, Folder currentFolder) {
		for(int i = 0; i < siblings.size(); i++) {		
		}
		
		if(currentFolder.getName().equals(pathParts.get(0)) && pathParts.size() == 1) {
			return currentFolder;
		}
		else if(!currentFolder.getName().equals(pathParts.get(0)) && siblings.size() == 0) {
			return null;
		}
		
		else if(!currentFolder.getName().equals(pathParts.get(0)) && siblings.size() != 0) {
			currentFolder = siblings.get(0);
			
			siblings.remove(0);
			return searchForChildAux(pathParts,siblings,currentFolder);
		}
		else if(currentFolder.getName().equals(pathParts.get(0)) && currentFolder.getChilds().size() != 0) {
				pathParts.remove(0);
				Folder folder = currentFolder.getChilds().get(0);
				currentFolder.getChilds().remove(0);
				return searchForChildAux(pathParts,currentFolder.getChilds(),folder);
		}
			
		else {
			return null;
		}	
			
			
		
		
		
		
	}

	
	

}
