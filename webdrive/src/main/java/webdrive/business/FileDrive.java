package webdrive.business;

public class FileDrive {

	private String name;
	private long size;
	private long last_modified;
	private String data;
	private String secondUsername = null;
	private Folder parent;
	private String path;
	
	

	public FileDrive(String name, long size, long last_modified, String data) {
		super();
		this.name = name;
		this.size = size;
		this.last_modified = last_modified;
		this.data = data;
	}
	
	
	
	

	public FileDrive(String name, long size, long last_modified, String data, Folder parent) {
		super();
		this.name = name;
		this.size = size;
		this.last_modified = last_modified;
		this.data = data;
		this.parent = parent;
		path = (parent.getParent() == null ? parent.getPath() + "/" : parent.getPath());
	}







	public FileDrive() {
		super();
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
			this.path = parent.getPath() + "/";
		}
		else {
			this.path = parent.getPath() + parent.getName() + "/";
		}
	}





	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getLast_modified() {
		return last_modified;
	}

	public void setLast_modified(long last_modified) {
		this.last_modified = last_modified;
	}

	public String getSecondUsername() {
		return secondUsername;
	}

	public void setSecondUsername(String secondUsername) {
		this.secondUsername = secondUsername;
	}
}
