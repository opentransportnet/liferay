package otn.beans;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.mail.internet.InternetAddress;
import javax.portlet.PortletRequest;

import otn.authenticator.LiferayAuthenticator;
import otn.interfaces.IAuthenticator;

import com.liferay.faces.portal.context.LiferayFacesContext;
import com.liferay.faces.util.portal.WebKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.security.auth.Authenticator;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;

@ManagedBean(name = "cityRegistrationBean")
@ViewScoped
public class CityRegistrationBean implements Serializable{

	
	private static final long serialVersionUID = 1L;

	// ************************* Variables **************************
	private String cityName;

	private String country;
	
	private String cityEmail;
	
	private String cityWebsite;
	
	private String name;
	
	private String email;
	
	private String telephone;
	
	private String function;
	
	private String receiver;
	
	private String redirectUrl;
		
	/** The user authenticator object. */
	private IAuthenticator authenticator;
	
	/* email body */
	private String text = "";
	
	FacesContext context = FacesContext.getCurrentInstance();

	PortletRequest portletRequest = (PortletRequest) context.getExternalContext().getRequest();
	ThemeDisplay themeDisplay = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
	private String url = "";
	private String realUrl = "";
	private String tempUrl = "";

	// ************************* Functions **************************

	public CityRegistrationBean() {
		
		authenticator = new LiferayAuthenticator();
		
		Properties p = new Properties();
		InputStream in = null;
		
		in = this.getClass().getResourceAsStream("/settings.properties");
		try {
			p.load(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
				receiver = p.getProperty("emailForRegister");
				
				redirectUrl = authenticator.getSiteUrl();
				
				try {
					url = PortalUtil.getLayoutURL(themeDisplay.getLayout(),themeDisplay);
					tempUrl = redirectUrl.replaceAll("/web/guest", url);
					realUrl = tempUrl.replaceAll("/become-otn-partner-city", "");
					
				} catch (PortalException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (SystemException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}		
	}// end constructor

	public void sendEmail() {

		System.out.println("sendEmail was called");
		
		System.out.println("receiver = " + receiver);

		try {
		
		com.liferay.portal.kernel.mail.MailMessage message = new com.liferay.portal.kernel.mail.MailMessage();
		InternetAddress to = new InternetAddress();
		to.setAddress(receiver);

		InternetAddress from = new InternetAddress();
		from.setAddress(cityEmail);

		message.setFrom(from);
		message.setTo(to);
		message.setSubject("OTN City Registration");
		
		setText("Please add " +cityName+ " as an OTN City hub. \n City name: " +cityName+ "\n Country: " +country+ "\n City E-mail: " +
				" "+cityEmail+ "\n City website: " +cityWebsite+ "\n Person Name: " +name+ "\n Person Email: " +email+ "" +
						"\n Person Telephone: " +telephone+ "\n Person Function: " +function);
	
		
		String bodyMsg = "You have received a message from " + cityName + " (" + cityEmail + ") \n" + getText();
		
		
		message.setBody(bodyMsg);
		
		System.out.println("email message: " + bodyMsg);
				
		com.liferay.mail.service.MailServiceUtil.sendEmail(message);
		
		FacesMessage msg = new FacesMessage(
				"Your registration as an OTN hub is being completed."," Please wait confirmation from administrator");
		FacesContext.getCurrentInstance().addMessage(null, msg);
		
		} catch (Exception e) {
			FacesMessage msg = new FacesMessage(
					"Something went wrong."," Please try again later");
			FacesContext.getCurrentInstance().addMessage(null, msg);
			System.out.println(e);
		}
		
		}
	
	public void hubs() {
		LiferayFacesContext portletFacesContext = LiferayFacesContext
				.getInstance();
		
		try {
			
			if (url.contains("web/antwerp") || url.contains("web/issy") ||
					url.contains("web/birmingham") || url.contains("web/liberec")){
				portletFacesContext.getExternalContext().redirect(
						realUrl + "/city-hubs?");
			}else {
				portletFacesContext.getExternalContext().redirect(
						redirectUrl + "/city-hubs");
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// ************************ Getters-Setters ****************************
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	
	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCityEmail() {
		return cityEmail;
	}

	public void setCityEmail(String cityEmail) {
		this.cityEmail = cityEmail;
	}

	public String getCityWebsite() {
		return cityWebsite;
	}

	public void setCityWebsite(String cityWebsite) {
		this.cityWebsite = cityWebsite;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

}// end class
