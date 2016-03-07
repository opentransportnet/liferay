package otn.interfaces;

public interface IAuthenticator {

	public Long getUserID();
	
	public String getUserName();
	
	public String getUserEmail();
	
	public boolean userIsSignedIn();
	
	public String getSiteName();
	
	public String getSiteUrl();

	public String getPortalUrl();

	
}//end Authenticator
