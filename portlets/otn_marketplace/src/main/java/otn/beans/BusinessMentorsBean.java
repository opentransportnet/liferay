package otn.beans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.mail.internet.InternetAddress;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.liferay.faces.portal.context.LiferayFacesContext;
import com.liferay.faces.util.portal.WebKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.sun.jersey.api.client.ClientResponse;

import otn.authenticator.LiferayAuthenticator;
import otn.clients.OtnClient;
import otn.interfaces.IAuthenticator;
import otn.model.Response;
import otn.model.marketplace.ServiceMin;

@ManagedBean(name = "businessMentorsBean")
@ViewScoped
public class BusinessMentorsBean {

	// ************************* Variables **************************
	/** The user authenticator object. */
	private IAuthenticator authenticator;

	private Long serviceID;

	private String redirectUrl;

	/** the liferay user id of the signed in user */
	private Long userLiferayId;

	private String userName;

	private String userEmail;
	/* email body */
	private String text = "";
	private String receiver;
	
	FacesContext context = FacesContext.getCurrentInstance();
	PortletRequest portletRequest = (PortletRequest) context.getExternalContext().getRequest();
	ThemeDisplay themeDisplay = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
	
	private String url = "";
	private String realUrl = "";
	private String tempUrl = "";


	// ************************* Functions **************************
	
	public BusinessMentorsBean() {

		// ************************ Variables *************************

		FacesContext context = FacesContext.getCurrentInstance();
		//
		// // retrieve the requested serviceID from the portlet session
		// PortletRequest request1 = (PortletRequest)
		// context.getExternalContext()
		// .getRequest();
		// PortletSession session = request1.getPortletSession(false);
		// String value = (String) session.getAttribute("serviceID",
		// PortletSession.APPLICATION_SCOPE);
		//
		// Long serviceID = new Long(0);

		// ************************ Action ****************************
		// initialize the authenticator
		authenticator = new LiferayAuthenticator();

		// retrieve the requested serviceID from the query string parameter
		HttpServletRequest request = PortalUtil
				.getHttpServletRequest((PortletRequest) FacesContext
						.getCurrentInstance().getExternalContext().getRequest());

		HttpServletRequest oriRequest = PortalUtil
				.getOriginalServletRequest(request);

		String param = oriRequest.getParameter("id");

		System.out.println("param = " + param);

		// get the serviceID as long
		if (param == null) {
			serviceID = (long) 0;
		} else {
			serviceID = Long.parseLong(param);
		}

		// // get the serviceID as long
		// try {
		// serviceID = Long.parseLong(value);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		redirectUrl = authenticator.getSiteUrl();

		/* user credentials */
		userLiferayId = authenticator.getUserID();
		userName = authenticator.getUserName();
		userEmail = authenticator.getUserEmail();
		
		try {
			url = PortalUtil.getLayoutURL(themeDisplay.getLayout(),themeDisplay);
			tempUrl = redirectUrl.replaceAll("/web/guest", url);
			realUrl = tempUrl.replaceAll("/business-support", "");
			
		} catch (PortalException e1) {
			e1.printStackTrace();
		} catch (SystemException e1) {
			e1.printStackTrace();
		}

	}// end constructor

	public void sendEmail() {

		System.out.println("sendEmail was called");
		
		System.out.println("receiver = " + receiver);

		com.liferay.portal.kernel.mail.MailMessage message = new com.liferay.portal.kernel.mail.MailMessage();
		InternetAddress to = new InternetAddress();
		to.setAddress(receiver);

		InternetAddress from = new InternetAddress();
		from.setAddress("opentransportnet.eu@gmail.com");

		message.setFrom(from);
		message.setTo(to);
		message.setSubject("OTN");
		
		String bodyMsg = "You have received a message from " + userName + " (" + userEmail + ") \n" + getText();
		
		
		message.setBody(bodyMsg);
		
		System.out.println("email message: " + bodyMsg);
		
		// "" +
		// "A new user was registered with user id "
		// + userLiferayId + " and screen name " + userScreenName
		// + ". The " + userScreenName + " wants to get the role "
		// + getRole());
		com.liferay.mail.service.MailServiceUtil.sendEmail(message);
		setText("");
	}

	/**
	 * On become expert selection, user is being redirected to the become mentor page
	 */
	public String becomeExpert() {

		LiferayFacesContext portletFacesContext = LiferayFacesContext
				.getInstance();
		
		try {
			
			if (url.contains("web/antwerp") || url.contains("web/issy") ||
					url.contains("web/birmingham") || url.contains("web/liberec")){
				portletFacesContext.getExternalContext().redirect(
						realUrl + "/become-an-otn-expert");
			}else {
				portletFacesContext.getExternalContext().redirect(
						redirectUrl + "/become-an-otn-expert");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "success";
	}
	
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

	// ************************ Getters-Setters ****************************
	
	
	
}// end class
