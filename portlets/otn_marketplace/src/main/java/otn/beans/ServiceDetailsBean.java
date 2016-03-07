package otn.beans;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.io.Serializable;

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
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionThreadLocal;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.sun.jersey.api.client.ClientResponse;

import otn.authenticator.LiferayAuthenticator;
import otn.clients.OtnClient;
import otn.interfaces.IAuthenticator;
import otn.model.Response;
import otn.model.marketplace.ServiceMin;

@ManagedBean(name = "serviceDetailsBean")
@ViewScoped
public class ServiceDetailsBean implements Serializable {

	// ************************* Variables **************************

	/** Session scoped bean that holds session information */
	@ManagedProperty(value = "#{sessionDataBean}")
	private SessionDataBean sessionBean;

	/** The user authenticator object. */
	private IAuthenticator authenticator;

	/** The service the user has selected. */
	private ServiceMin service;

	/** The list of user's subscriptions. */
	private List<ServiceMin> subscriptionsList;

	/** A boolean indicating if the subscription button is visible or not. */
	private boolean subscribeBtn;

	private Long serviceID;

	private String redirectUrl;

	/** defines if edit/delete buttons are displayed or not */
	private Boolean renderBtn = false;

	/** the liferay user id of the signed in user */
	private Long userLiferayId;

	/** A boolean indicating if the current user is signed in. */
	private boolean userSignedIn;
	
	FacesContext context = FacesContext.getCurrentInstance();

	PortletRequest portletRequest = (PortletRequest) context.getExternalContext().getRequest();
	ThemeDisplay themeDisplay = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
	private String url = "";
	private String realUrl = "";
	private String tempUrl = "";
	
	// ************************* Functions **************************

	/**
	 * 
	 */
	public ServiceDetailsBean() {

		// ************************ Variables *************************

		FacesContext context = FacesContext.getCurrentInstance();
//
//		// retrieve the requested serviceID from the portlet session
//		PortletRequest request1 = (PortletRequest) context.getExternalContext()
//				.getRequest();
//		PortletSession session = request1.getPortletSession(false);
//		 String value = (String) session.getAttribute("serviceID",
//		 PortletSession.APPLICATION_SCOPE);
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
		
		try {
			url = PortalUtil.getLayoutURL(themeDisplay.getLayout(),themeDisplay);
			tempUrl = redirectUrl.replaceAll("/web/guest", url);
			realUrl = tempUrl.replaceAll("/service-details", "");
			
		} catch (PortalException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SystemException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// get the data of the requested service
		getServiceDetails(serviceID);

		// get the user's subscriptions
		fetchPurchases();

		// check if the user has subscribed in the requested service already
		checkSubscription(serviceID);

		

		userLiferayId = authenticator.getUserID();

		/**
		 * Check if the signed in user is the provider of the service or the site administrator.
		 *  If yes edit/delete buttons are displayed to him
		 * */
		
		PermissionChecker permissionChecker = PermissionThreadLocal.getPermissionChecker();
		
		
		if (userLiferayId == service.getProvider().getLiferayId() || permissionChecker.isOmniadmin()) {
			renderBtn = true;
		}
	
		userSignedIn = authenticator.userIsSignedIn();

	}// end constructor

	/**
	 * Retrieves the details of the requested service.
	 */
	private void getServiceDetails(Long serviceID) {

		// ************************ Variables *************************

		OtnClient client = new OtnClient();

		ClientResponse response;

		// objects to transform the json response to java classes
		GsonBuilder builder = new GsonBuilder();
		Gson gson;

		Response<ServiceMin> resp;

		String respString;

		// ************************ Action ****************************

		try {

			// System.out.println("in get service details.. "+serviceID);

			// Register an adapter to manage the date types as long values
			builder.registerTypeAdapter(Date.class,
					new JsonDeserializer<Date>() {
						public Date deserialize(
								JsonElement json,
								java.lang.reflect.Type typeOfT,
								com.google.gson.JsonDeserializationContext context)
								throws JsonParseException {
							return new Date(json.getAsJsonPrimitive()
									.getAsLong());
						}
					});

			// initialize the gson object
			gson = builder.create();

			response = client.getServiceDetails(ClientResponse.class,
					javax.ws.rs.core.MediaType.APPLICATION_JSON, serviceID);

			client.close();

			respString = response.getEntity(String.class);

			System.out.println("getServiceDetails, respString:" + respString);

			resp = gson.fromJson(respString,
					new TypeToken<Response<ServiceMin>>() {
					}.getType());

			if (resp != null && resp.getErrorMsg().isEmpty()) {

				service = (ServiceMin) resp.getResponse();
				
				SimpleDateFormat dateformatJava = new SimpleDateFormat(
						"dd-MM-yyyy");
				service.setDateString(dateformatJava.format(service
						.getDate()));
					
				System.out.println("service object: " + gson.toJson(service));
				
			} else {

				service = new ServiceMin();
			}

		} catch (Exception e) {

			e.printStackTrace();

			service = new ServiceMin();
		}

	}// end getServices()

	/***
	 * It sends the subscription data to the appropriate web service.
	 */
	public String buyService() {

		// ************************ Variables *************************

		FacesContext context = FacesContext.getCurrentInstance();

		LiferayAuthenticator authenticator = new LiferayAuthenticator();

		OtnClient client = new OtnClient();

		ClientResponse response;

		String respString;

		ResourceBundle bundle = ResourceBundle.getBundle(
				"appMessages/messages", context.getViewRoot().getLocale());

		// ************************ Action ****************************

		try {
			System.out.println("buying service " + service.getId());

			// subscribe user to the selected service, passing the userID,
			// userScreenName, the serviceID and as type of product "services"
			response = client.buyService(ClientResponse.class,
					javax.ws.rs.core.MediaType.APPLICATION_JSON,
					authenticator.getUserID(), authenticator.getUserName(),
					authenticator.getUserEmail(), service.getId());

			client.close();

			// get response
			respString = response.getEntity(String.class);

			System.out.println("respString:" + respString);

			// if response is false, something went wrong, so inform user
			if (Long.parseLong(respString) == 0) {

				//show deletion error message
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL,bundle.getString("subscription-error-title"), bundle.getString("subscription-service-error")));  

			} else {
			
				// show success message
//				context.addMessage(
//						null,
//						new FacesMessage(FacesMessage.SEVERITY_INFO, "Subscription completed", "Your purchase is done successfully"));
				context.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, bundle
								.getString("subscription"), bundle
								.getString("subscription-success-msg")));

				// hide the subscription button
				subscribeBtn = false;

			}// end else

		} catch (Exception e) {

			e.printStackTrace();

			// show error message
			context.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_FATAL, bundle
							.getString("subscription-error-title"), bundle
							.getString("subscription-service-error")));

		}

		return null;

	}// end subscribe()

	/***
	 * It retrieves the user's service subscriptions
	 */
	private void fetchPurchases() {

		// ************************ Variables *************************

		OtnClient client = new OtnClient();

		ClientResponse response;

		Response<List<ServiceMin>> serResp;

		// objects to transform the json response to java classes
		GsonBuilder builder = new GsonBuilder();
		Gson gson;

		String jsonResponse;

		// ************************ Action ****************************

		try {

			// Register an adapter to manage the date types as long values
			builder.registerTypeAdapter(Date.class,
					new JsonDeserializer<Date>() {
						public Date deserialize(
								JsonElement json,
								java.lang.reflect.Type typeOfT,
								com.google.gson.JsonDeserializationContext context)
								throws JsonParseException {
							return new Date(json.getAsJsonPrimitive()
									.getAsLong());
						}
					});

			// initialize the gson object
			gson = builder.create();

			System.out.println("userID:" + authenticator.getUserID());

			response = client.getUserPurchases(ClientResponse.class,
					javax.ws.rs.core.MediaType.APPLICATION_JSON,
					authenticator.getUserID());

			client.close();

			jsonResponse = response.getEntity(String.class);

			System.out.println("jsonResponse:" + jsonResponse);

			serResp = gson.fromJson(jsonResponse,
					new TypeToken<Response<List<ServiceMin>>>() {
					}.getType());

			if (serResp != null && serResp.getErrorMsg().isEmpty()) {

				subscriptionsList = serResp.getResponse();

			} else {

				subscriptionsList = new ArrayList<ServiceMin>();

			}

		} catch (Exception e) {

			e.printStackTrace();

			subscriptionsList = new ArrayList<ServiceMin>();

		}

	}// end fetchSubscriptions()

	/**
	 * It checks if the service is one of the user's subscriptions.
	 * 
	 * @param serviceID
	 *            : The service id to match with.
	 */
	private void checkSubscription(long serviceID) {

		subscribeBtn = true;

		for (ServiceMin s : subscriptionsList) {

			// System.out.println("type:"+s.getServiceType()+", id:"+s.getServiceID());

			if (s.getId() == serviceID) {

				subscribeBtn = false;

				break;
			}

		}// end for

		// System.out.println("subscribeBtn:"+subscribeBtn);

	}// end checkSubscription()

	public void onEdit() {

		LiferayFacesContext portletFacesContext = LiferayFacesContext
				.getInstance();

			try {
			
			if (url.contains("web/antwerp") || url.contains("web/issy") ||
					url.contains("web/birmingham") || url.contains("web/liberec")){
				portletFacesContext.getExternalContext().redirect(
						realUrl + "/my-services?id=" + service.getId());
			}else {
				portletFacesContext.getExternalContext().redirect(
						redirectUrl + "/my-services?id=" + service.getId());
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/** Redirects a user to all challenges */
	public void goToServices() {

		LiferayFacesContext portletFacesContext = LiferayFacesContext
				.getInstance();

			try {
			
			if (url.contains("web/antwerp") || url.contains("web/issy") ||
					url.contains("web/birmingham") || url.contains("web/liberec")){
				portletFacesContext.getExternalContext().redirect(
						realUrl + "/marketplace");
			}else {
				portletFacesContext.getExternalContext().redirect(
						redirectUrl + "/marketplace");
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/** Redirects a user to his challenges */
	public void goToMyServices() {

		LiferayFacesContext portletFacesContext = LiferayFacesContext
				.getInstance();

try {
			
			if (url.contains("web/antwerp") || url.contains("web/issy") ||
					url.contains("web/birmingham") || url.contains("web/liberec")){
				portletFacesContext.getExternalContext().redirect(
						realUrl + "/my-services");
			}else {
				portletFacesContext.getExternalContext().redirect(
						redirectUrl + "/my-services");
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onDelete() {

		OtnClient client = new OtnClient();

		ClientResponse response;

		String jsonResp;

		// ************************ Action ****************************

		try {

			SimpleDateFormat dateformatJava = new SimpleDateFormat(
					"dd-MM-yyyy");
			service.setDateString(dateformatJava.format(service
					.getDate()));
			response = client.deleteService(ClientResponse.class,
					javax.ws.rs.core.MediaType.APPLICATION_JSON, service);

			client.close();

			jsonResp = response.getEntity(String.class);

			System.out.println("delete jsonResp:" + jsonResp);

		} catch (Exception e) {
			e.printStackTrace();
		}

		goToMyServices();

	}

	// ************************ Getters-Setters ****************************

	public SessionDataBean getSessionBean() {
		return sessionBean;
	}

	public void setSessionBean(SessionDataBean sessionBean) {
		this.sessionBean = sessionBean;
	}

	public ServiceMin getService() {
		return service;
	}

	public void setService(ServiceMin service) {
		this.service = service;
	}

	public boolean isSubscribeBtn() {
		return subscribeBtn;
	}

	public void setSubscribeBtn(boolean subscribeBtn) {
		this.subscribeBtn = subscribeBtn;
	}

	public Boolean getRenderBtn() {
		return renderBtn;
	}

	public void setRenderBtn(Boolean renderBtn) {
		this.renderBtn = renderBtn;
	}

	public boolean isUserSignedIn() {
		return userSignedIn;
	}

	public void setUserSignedIn(boolean userSignedIn) {
		this.userSignedIn = userSignedIn;
	}
	
}// end class
