package webdrive.business;

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
	
	

	

}
