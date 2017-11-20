package webdrive.business;

public class File {
	
	private String name;
	private long size;
	private long last_modified;
	private String secondUsername;
	
	public File(String name, long size, long last_modified) {
		this.name = name;
		this.size = size;
		this.last_modified = last_modified;
	}

	public File() {
		
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
