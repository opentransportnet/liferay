package otn.authenticator;

import java.io.Serializable;

import javax.faces.context.FacesContext;

import otn.interfaces.IAuthenticator;

import com.liferay.faces.util.portal.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;

public class LiferayAuthenticator implements IAuthenticator, Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * It retrieves the userID of the current logged in user.
	 */
	public Long getUserID() {

		// ************************ Variables *************************

		FacesContext context = FacesContext.getCurrentInstance();

		ThemeDisplay themeDisplay = (ThemeDisplay) context.getExternalContext()
				.getRequestMap().get(WebKeys.THEME_DISPLAY);

		// ************************* Action **************************

		return themeDisplay.getUser().getUserId();

	}// end getUserID()
	
	
	/***
	 * It retrieves the full name of the currently logged in user. 
	 */
	public String getUserName() {

		// ************************ Variables *************************

		FacesContext context = FacesContext.getCurrentInstance();

		ThemeDisplay themeDisplay = (ThemeDisplay) context.getExternalContext()
				.getRequestMap().get(WebKeys.THEME_DISPLAY);

		// ************************* Action **************************

		return themeDisplay.getUser().getFullName();

	}// end getUserName()
	
	/**
	 * It retrieves the user email of the currently logged in user.
	 */
	public String getUserEmail() {

		// ************************ Variables *************************

		FacesContext context = FacesContext.getCurrentInstance();

		ThemeDisplay themeDisplay = (ThemeDisplay) context.getExternalContext()
				.getRequestMap().get(WebKeys.THEME_DISPLAY);

		// ************************* Action **************************

		return themeDisplay.getUser().getEmailAddress();

	}// end getUserEmail()
	
	/***
	 * It checks if the user is signed in.
	 * 
	 * @return : A boolean  indicating if the user is signed in.
	 */
	public boolean userIsSignedIn(){
		
		// ************************ Variables *************************

		FacesContext context = FacesContext.getCurrentInstance();

		ThemeDisplay themeDisplay = (ThemeDisplay) context.getExternalContext()
				.getRequestMap().get(WebKeys.THEME_DISPLAY);

		// ************************* Action **************************

		return themeDisplay.isSignedIn();
		
	}//end userIsSignedIn()
	
	/**
	 * It retrieves the name of the current site.
	 */
	public String getSiteName(){
		
		// ************************ Variables *************************

		FacesContext context = FacesContext.getCurrentInstance();

		ThemeDisplay themeDisplay = (ThemeDisplay) context.getExternalContext()
				.getRequestMap().get(WebKeys.THEME_DISPLAY);

		String siteName = "";
		
		// ************************* Action **************************
		
		try{
		
			siteName = themeDisplay.getCompany().getName();
		
		}catch(Exception e){
			
			e.printStackTrace();
			
		}
		
		return siteName;

	}//end getSiteName()
	
	/**
	 * It retrieves the url of the current site.
	 */
	public String getSiteUrl(){
		
		// ************************ Variables *************************

		FacesContext context = FacesContext.getCurrentInstance();

		ThemeDisplay themeDisplay = (ThemeDisplay) context.getExternalContext()
				.getRequestMap().get(WebKeys.THEME_DISPLAY);

		String siteUrl = "";
		
		// ************************* Action **************************
		
		try{
		
			System.out.println("portal url:"+themeDisplay.getPortalURL());
			System.out.println("url home:"+themeDisplay.getURLHome());
			System.out.println("company url home:"+themeDisplay.getCompany().getHomeURL());
			
			siteUrl = themeDisplay.getURLHome();
		
		}catch(Exception e){
			
			e.printStackTrace();
			
		}
		
		return siteUrl;

	}//end getSiteurl()
	
	/**
	 * It retrieves the base url of the portal.
	 */
	public String getPortalUrl(){
		
		// ************************ Variables *************************

		FacesContext context = FacesContext.getCurrentInstance();

		ThemeDisplay themeDisplay = (ThemeDisplay) context.getExternalContext()
				.getRequestMap().get(WebKeys.THEME_DISPLAY);

		String portalUrl = "";
		
		// ************************* Action **************************
		
		try{
		
//			System.out.println("portal url:"+themeDisplay.getPortalURL());
//			System.out.println("url home:"+themeDisplay.getURLHome());
//			System.out.println("company url home:"+themeDisplay.getCompany().getHomeURL());
			
			portalUrl = themeDisplay.getPortalURL();
		
		}catch(Exception e){
			
			e.printStackTrace();
			
		}
		
		return portalUrl;

	}//end getSiteurl()

}// end Authenticator
