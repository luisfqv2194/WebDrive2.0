package webdrive.business;

public class User {
	
	private String username;
    private String password;
    private long space;
    // private Drive myDrive;
    
    public User(String username, String password, long space) {
		this.username = username;
		this.password = password;
		this.space = space;
	}
    
    
   
	public User(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}



	public User() {
	}



	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public long getSpace() {
		return space;
	}
	public void setSpace(long space) {
		this.space = space;
	}
    
    

}
