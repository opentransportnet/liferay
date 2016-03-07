package otn.beans;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

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

import otn.model.Categories;
import otn.model.Products;
import otn.model.ServicesHolder;
import otn.model.catalog.WebServiceCategoryMin;
import otn.model.marketplace.*;

@ManagedBean(name = "marketplaceBean")
@ViewScoped
public class MarketplaceBean implements Serializable {

	// ************************* Variables **************************

	/** Application scoped bean that holds global information */
	@ManagedProperty(value = "#{otnDataBean}")
	private DataBean dataBean;

	/** the object that helps us access liferay specific attributes. */
	private LiferayAuthenticator authenticator;

	/** The list of the available services matching the search criteria. */
	private List<ServiceMin> services;

	/**
	 * List holding all the registered services grouped by category. This list
	 * is used for the default view of the marketplace.
	 */
	private List<ServicesHolder> servicesList;

	/** It holds the text of the search field. */
	private String searchText = "";

	/** It holds the id of the category the user has selected from the ui list. */
	private Integer category = 0;

	/** Indicates if the search panel on the ui should be visible or not. */
	private boolean renderSearch = false;

	/** Indicates if the main service panel should be visible or not. */
	private boolean renderMain = true;

	/** The price category the user has selecte from the ui list (free, paid). */
	private Integer price;

	/**
	 * The id of the city that the user has selected (in case the portlet is at
	 * the central site) or that is matched to the site name (in case the
	 * portlet is at the city site)
	 */
	private Integer cityID = -1;

	/** The base portal url. */
	// private String portalUrl;

	/** The redirect url to the service details page. */
	private String detailsUrl;

	private boolean renderCities;

	private ServiceMin selectedService;
	private String redirectUrl;
	
	FacesContext context = FacesContext.getCurrentInstance();

	PortletRequest portletRequest = (PortletRequest) context.getExternalContext().getRequest();
	ThemeDisplay themeDisplay = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
	private String url = "";
	private String realUrl = "";
	private String tempUrl = "";
	
	// ************************* Functions **************************

	public MarketplaceBean() {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		String siteName;
		
		// initialize the authenticator
		authenticator = new LiferayAuthenticator();

		// if(dataBean == null){
		// dataBean =
		// (DataBean)facesContext.getExternalContext().getApplicationMap().get("otnDataBean");
		// }

		// initialize the DataBean in case this is not done automatically
		if (facesContext.getExternalContext().getApplicationMap()
				.get("otnDataBean") == null) {

			dataBean = new DataBean();
			facesContext.getExternalContext().getApplicationMap()
					.put("otnDataBean", dataBean);

			// System.out.println("data bean in.."+dataBean);

		} else {

			dataBean = (DataBean) facesContext.getExternalContext()
					.getApplicationMap().get("otnDataBean");

		}

		// initialize the service lists
		servicesList = new ArrayList<ServicesHolder>();
		services = new ArrayList<ServiceMin>();
		
		redirectUrl = authenticator.getSiteUrl();

		// get the portal url
		// portalUrl = authenticator.getPortalUrl();

		// get the name of the current site (company name)
		siteName = authenticator.getSiteName();

		System.out.println("site:" + authenticator.getSiteName() + ", url:"
				+ authenticator.getSiteUrl());

		// find the city id based on the site name
		if (siteName.equalsIgnoreCase("otn")) {

			cityID = 0;

			renderCities = true;

		} else {

			// for every available city
			for (CityMin city : dataBean.getCities()) {

				System.out.println("city name:" + city.getName()
						+ ", site name:" + siteName);

				// if the city name is the site name, get the city id
				if (city.getName().equalsIgnoreCase(siteName)) {

					System.out.println("matched!");

					cityID = city.getId();

					renderCities = false;

					break;

				}// end if

			}// end for

		}// end else

		if (cityID == -1) {
			cityID = 0;
			renderCities = true;
		}

		// initialize paid option (0-all)
		price = 0;

		// initialize redirect url
		detailsUrl = authenticator.getSiteUrl()
				+ dataBean.getServiceDetailsPage();

		// System.out.println("cityID:"+cityID);

		try {
			url = PortalUtil.getLayoutURL(themeDisplay.getLayout(),themeDisplay);
			tempUrl = redirectUrl.replaceAll("/web/guest", url);
			realUrl = tempUrl.replaceAll("/marketplace", "");
			
		} catch (PortalException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SystemException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// get the services available
		fetchServices();

	}// end constructor

	/**
	 * It retrieves the available services (limit is 5) in the db for all
	 * service categories and it groups them based on the service category.
	 */
	public void fetchServices() {

		// ************************ Variables *************************

		// the rest client for the otn web services
		OtnClient client = new OtnClient();

		// the response of the web service call
		ClientResponse response = null;

		// objects to transform the json response to java classes
		GsonBuilder builder = new GsonBuilder();
		Gson gson;

		// the java response object
		ServicesResponse serResp;

		// counter
		int i;

		// the service category object
		ServiceCategoryMin cat;

		// the list of the available services for a given category
		List<ServiceMin> services;

		// the json response of the web service call
		String jsonResp;

		// temp object holding the category name and the list of its services
		ServicesHolder holder;

		// the list of available service categories
		List<ServiceCategoryMin> serviceCategories;

		// ************************ Action ****************************
		
		// Register an adapter to manage the date types as long values
		builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
			public Date deserialize(JsonElement json,
					java.lang.reflect.Type typeOfT,
					com.google.gson.JsonDeserializationContext context)
					throws JsonParseException {
				return new Date(json.getAsJsonPrimitive().getAsLong());
			}
		});

		// initialize the gson object
		gson = builder.create();

		// get a reference to the service categories
		serviceCategories = dataBean.getServiceCategories();

		// fetch services for all categories
		for (i = 0; i < serviceCategories.size(); i++) {

			// System.out.println("calling services..");

			// get the next category
			cat = serviceCategories.get(i);

			try {
				String city = "";
				//get services according to which hub we are into
				
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
				
					response = client.getServices(ClientResponse.class,
							javax.ws.rs.core.MediaType.APPLICATION_JSON, 0, "",
							cat.getId(), 0, city);
				
				// release client
				client.close();

				// get the json response
				jsonResp = response.getEntity(String.class);

				// System.out.println("services resp:"+jsonResp);

				// convert json response to java response
				serResp = gson.fromJson(jsonResp,
						new TypeToken<ServicesResponse>() {
						}.getType());

				// if we got a response and the error message is emtpy
				// (everything went ok)
				if (serResp != null && serResp.getErrorMsg().isEmpty()) {

					// get the list of services
					services = serResp.getServices();

					// System.out.println("services..");

				} else {// else create an empty list

					services = new ArrayList<ServiceMin>();

				}

			} catch (Exception e) {

				e.printStackTrace();

				services = new ArrayList<ServiceMin>();

			}// end catch

			// create a new service holder object and add it to the services
			// list
			holder = new ServicesHolder();
			holder.setCategory(cat);
			holder.setServices(services);

			servicesList.add(holder);

		}// end for

		// System.out.println("servicesMap:"+servicesList.toString());

	}// end fetchServices()

	/***
	 * Function triggered when the user clicks on a link or photo in the
	 * marketplace service catalog. It redirects the user to the details page.
	 * 
	 * @return
	 */
	public String onServiceSelected() {

		// ************************ Variables *************************

		// LiferayFacesContext portletFacesContext =
		// LiferayFacesContext.getInstance();
		//
		// FacesContext facesContext = FacesContext.getCurrentInstance();
		//
		// PortletRequest request = (PortletRequest)
		// facesContext.getExternalContext().getRequest();
		// PortletSession session = request.getPortletSession(false);
		//
		// Map<String,String> params =
		// facesContext.getExternalContext().getRequestParameterMap();
		//
		// //************************ Action ****************************
		//
		// try{
		//
		// System.out.println("detailsUrl:"+detailsUrl);
		//
		// session.setAttribute("serviceID", params.get("serviceID"),
		// PortletSession.APPLICATION_SCOPE);
		//
		// portletFacesContext.getExternalContext().redirect(detailsUrl);
		//
		// }catch(Exception e){
		// e.printStackTrace();
		// }

		System.out
				.println("onServiceSelected, id = " + selectedService.getId());

		LiferayFacesContext portletFacesContext = LiferayFacesContext
				.getInstance();
		try {
			
			if (url.contains("web/antwerp") || url.contains("web/issy") ||
					url.contains("web/birmingham") || url.contains("web/liberec")){
				portletFacesContext.getExternalContext().redirect(
						realUrl + "/service-details?id="
								+ selectedService.getId());
			}else {
				portletFacesContext.getExternalContext().redirect(
						redirectUrl + "/service-details?id="
								+ selectedService.getId());
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}// end onProductSelected()

	/***
	 * It is triggered when the user clicks the "search" button. It gets the
	 * selected search parameters, calls the getService web service method and
	 * renders the appropriate panel.
	 * 
	 * @param event
	 */
//	public void search(ActionEvent event) {
//
//		System.out.println("search:" + category + ",price:" + price + ", city:"
//				+ cityID + "searchString:" + searchText);
//
//		// ************************ Variables *************************
//
//		OtnClient client = new OtnClient();
//
//		ClientResponse response;
//
//		GsonBuilder builder = new GsonBuilder();
//
//		Gson gson = new Gson();
//
//		ServicesResponse serResp;
//
//		String resp;
//
//		// ************************ Action ****************************
//
//		// Register an adapter to manage the date types as long values
//		builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
//			public Date deserialize(JsonElement json,
//					java.lang.reflect.Type typeOfT,
//					com.google.gson.JsonDeserializationContext context)
//					throws JsonParseException {
//				return new Date(json.getAsJsonPrimitive().getAsLong());
//			}
//		});
//
//		gson = builder.create();
//
//		// if we have selected 'All' in every filter
//		if (price == 0 && category == 0 && cityID == 0 && searchText.isEmpty()) {
//
//			renderSearch = false;
//			renderMain = true;
//
//		} else {
//
//			renderSearch = true;
//			renderMain = false;
//
//			try {
//
//				response = client.getServices(ClientResponse.class,
//						javax.ws.rs.core.MediaType.APPLICATION_JSON, 0,
//						searchText, category, price, cityID);
//
//				client.close();
//
//				resp = response.getEntity(String.class);
//
//				// System.out.println("resp:"+resp);
//
//				serResp = gson.fromJson(resp,
//						new TypeToken<ServicesResponse>() {
//						}.getType());
//
//				if (serResp != null && serResp.getErrorMsg().isEmpty()) {
//
//					services = serResp.getServices();
//
//				} else {
//
//					services = new ArrayList<ServiceMin>();
//
//				}
//
//			} catch (Exception e) {
//
//				e.printStackTrace();
//
//				services = new ArrayList<ServiceMin>();
//
//			}// end catch
//
//		}// end else
//
//	}// end search()

	// **********************************************************************

	public String getSearchText() {
		return searchText;
	}

	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

	public Integer getCategory() {
		return category;
	}

	public void setCategory(Integer category) {
		this.category = category;
	}

	public DataBean getDataBean() {
		return dataBean;
	}

	public void setDataBean(DataBean dataBean) {
		this.dataBean = dataBean;
	}

	public List<ServiceMin> getServices() {
		return services;
	}

	public void setServices(List<ServiceMin> services) {
		this.services = services;
	}

	public boolean isRenderSearch() {
		return renderSearch;
	}

	public void setRenderSearch(boolean renderSearch) {
		this.renderSearch = renderSearch;
	}

	public boolean isRenderMain() {
		return renderMain;
	}

	public void setRenderMain(boolean renderMain) {
		this.renderMain = renderMain;
	}

	public List<ServicesHolder> getServicesList() {
		return servicesList;
	}

	public void setServicesList(List<ServicesHolder> servicesList) {
		this.servicesList = servicesList;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public Integer getCityID() {
		return cityID;
	}

	public void setCityID(Integer cityID) {
		this.cityID = cityID;
	}

	public boolean isRenderCities() {
		return renderCities;
	}

	public void setRenderCities(boolean renderCities) {
		this.renderCities = renderCities;
	}

	public ServiceMin getSelectedService() {
		return selectedService;
	}

	public void setSelectedService(ServiceMin selectedService) {
		this.selectedService = selectedService;
	}

}// end class
