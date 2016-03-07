package otn.beans;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.RequestContext;

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

import otn.model.AppsHolder;
import otn.model.Categories;
import otn.model.Products;
import otn.model.ServicesHolder;
import otn.model.catalog.WebServiceCategoryMin;
import otn.model.marketplace.*;

@ManagedBean(name = "appsBean")
@ViewScoped
public class AppsBean implements Serializable {

	// ************************* Variables **************************

	private static final long serialVersionUID = 1L;

	/** the object that helps us access liferay specific attributes. */
	private LiferayAuthenticator authenticator;

	/** The list of the available services matching the search criteria. */
	private List<AppMin> apps;

	/**
	 * List holding all the registered apps. This list
	 * is used for the default view of the apps.
	 */
	private List<AppsHolder> appsList;

	/** It holds the text of the search field. */
	private String searchText = "";
	
	/** Indicates if the search panel on the ui should be visible or not. */
	private boolean renderSearch = false;

	private String redirectUrl;
	
	private AppMin selectedApp;
	
	private AppMin deletedApp;
	
	private Boolean isAdmin = false;
	
	private String url = "";
	
	FacesContext context = FacesContext.getCurrentInstance();

	PortletRequest portletRequest = (PortletRequest) context.getExternalContext().getRequest();
	ThemeDisplay themeDisplay = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
	private String realUrl = "";
	private String tempUrl = "";
	
	// ************************* Functions **************************

	public AppsBean() {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		String siteName;

		
		// initialize the authenticator
		authenticator = new LiferayAuthenticator();

		// initialize the service lists
		appsList = new ArrayList<AppsHolder>();
		apps = new ArrayList<AppMin>();
		
		redirectUrl = authenticator.getSiteUrl();

		// get the portal url
		// portalUrl = authenticator.getPortalUrl();

		// get the name of the current site (company name)
		siteName = authenticator.getSiteName();
		
		PermissionChecker permissionChecker = PermissionThreadLocal.getPermissionChecker();
		isAdmin = permissionChecker.isOmniadmin();

		System.out.println("site:" + authenticator.getSiteName() + ", url:"
				+ authenticator.getSiteUrl());

		selectedApp = new AppMin();
		// get the apps available
		
		try {
			url = PortalUtil.getLayoutURL(themeDisplay.getLayout(),themeDisplay);
			tempUrl = redirectUrl.replaceAll("/web/guest", url);
			realUrl = tempUrl.replaceAll("/apps", "");
			
		} catch (PortalException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SystemException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		fetchApps();
		
		HttpServletRequest request = PortalUtil
				.getHttpServletRequest((PortletRequest) FacesContext
						.getCurrentInstance().getExternalContext().getRequest());

		HttpServletRequest oriRequest = PortalUtil
				.getOriginalServletRequest(request);
		
		String param = oriRequest.getParameter("id");
		
		if (param != null) {
			selectedApp.setId(Long.parseLong(param));
		}

	}// end constructor

	/**
	 * It retrieves the available services (limit is 5) in the db for all
	 * service categories and it groups them based on the service category.
	 */
	public void fetchApps() {

		// ************************ Variables *************************

		// the rest client for the otn web services
		OtnClient client = new OtnClient();

		// the response of the web service call
		ClientResponse response = null;

		// objects to transform the json response to java classes
		GsonBuilder builder = new GsonBuilder();
		Gson gson;

		// the java response object
		AppsResponse appResp;

		// counter
		int i;

		// the json response of the web service call
		String jsonResp;

		// temp object holding the category name and the list of its services
		AppsHolder holder;

		
		// ************************ Action ****************************

		// initialize the gson object
		gson = builder.create();
		
			try {
				String city = "";
				//get apps according to which hub we are into
				if (url.contains("/web/antwerp")){
					city = "Antwerp";
				}
				else if (url.contains("/web/birmingham")) {
					city = "Birmingham";
				}
				
				else if (url.contains("/web/issy")) {
					city = "Issy-les-Moulineaux";
				}
				else if (url.contains("/web/liberec")) {
					city = "Liberec";
				}
				else{
					city = "main";
				}
								
				response = client.getApps(ClientResponse.class,
						javax.ws.rs.core.MediaType.APPLICATION_JSON, city);
				
				// release client
				client.close();

				// get the json response
				jsonResp = response.getEntity(String.class);

				// System.out.println("services resp:"+jsonResp);

				// convert json response to java response
				appResp = gson.fromJson(jsonResp,
						new TypeToken<AppsResponse>() {
						}.getType());

				// if we got a response and the error message is emtpy
				// (everything went ok)
				if (appResp != null && appResp.getErrorMsg().isEmpty()) {

					// get the list of services
					apps = appResp.getApps();

					// System.out.println("services..");

				} else {// else create an empty list

					apps = new ArrayList<AppMin>();

				}

			} catch (Exception e) {

				e.printStackTrace();

				apps = new ArrayList<AppMin>();

			}// end catch

			// create a new service holder object and add it to the services
			// list
			holder = new AppsHolder();
			
			holder.setApps(apps);

			appsList.add(holder);
			
			for (AppMin app : apps) {
				if (app.getPictureUrl() == null || app.getPictureUrl().isEmpty())
					app.setPictureUrl("/otn_marketplace/images/default-image.jpg");
			}
				

		}// end fetch apps
	
	public String onEdit() {
		LiferayFacesContext portletFacesContext = LiferayFacesContext
				.getInstance();
		try {
			if (url.contains("web/antwerp") || url.contains("web/issy") ||
				url.contains("web/birmingham") || url.contains("web/liberec")){
				portletFacesContext.getExternalContext().redirect(
						realUrl + "/edit-app?id="+selectedApp.getId());
			}else {
				portletFacesContext.getExternalContext().redirect(
						redirectUrl + "/edit-app?id="+selectedApp.getId());
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "success";
	}
	
	public void onDelete(){
		
	OtnClient client = new OtnClient();

	ClientResponse response;

	Gson gson = new Gson();

	String jsonResp;

	// ************************ Action ****************************

	try {

		response = client.deleteApp(ClientResponse.class,
				javax.ws.rs.core.MediaType.APPLICATION_JSON, selectedApp);

		client.close();

		jsonResp = response.getEntity(String.class);

		//System.out.println("delete jsonResp:" + jsonResp);
		
		if(jsonResp.equalsIgnoreCase("true")){
			//System.out.println("challengeList.size() = " + challengeList.size());
			appsList.remove(selectedApp);
			//System.out.println("challengeList.size after remove = " + challengeList.size());
		}

		LiferayFacesContext portletFacesContext = LiferayFacesContext
				.getInstance();
		try {
			if (!url.contains("/web/guest")){
				portletFacesContext.getExternalContext().redirect(
						realUrl + "/apps");
			}else {
				portletFacesContext.getExternalContext().redirect(
						redirectUrl + "/apps");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	} catch (Exception e) {

		e.printStackTrace();

		
	}
	

			}// end deleteService()

	// **********************************************************************

	public String getSearchText() {
		return searchText;
	}

	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

	
	public boolean isRenderSearch() {
		return renderSearch;
	}

	public void setRenderSearch(boolean renderSearch) {
		this.renderSearch = renderSearch;
	}

	public List<AppMin> getApps() {
		return apps;
	}

	public void setApps(List<AppMin> apps) {
		this.apps = apps;
	}

	public List<AppsHolder> getAppsList() {
		return appsList;
	}

	public void setAppsList(List<AppsHolder> appsList) {
		this.appsList = appsList;
	}

	public Boolean getIsAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(Boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public AppMin getSelectedApp() {
		return selectedApp;
	}

	public void setSelectedApp(AppMin selectedApp) {
		this.selectedApp = selectedApp;
	}

	public AppMin getDeletedApp() {
		return deletedApp;
	}

	public void setDeletedApp(AppMin deletedApp) {
		this.deletedApp = deletedApp;
	}
	
}// end class
