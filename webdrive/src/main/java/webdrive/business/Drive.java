package webdrive.business;

public class Drive {
	
	private long space;
	private Folder root;

	public Drive(long space) {
		this.space = space;
		root = new Folder();
		root.setName("root");
		root.getChilds().add(new Folder(root,"Shared"));
	}
	
	public Drive() {
		
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
