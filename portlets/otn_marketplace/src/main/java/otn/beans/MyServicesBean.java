package otn.beans;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.ClientResponse;

import com.liferay.faces.portal.context.LiferayFacesContext;
import com.liferay.faces.util.portal.WebKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;

import otn.authenticator.LiferayAuthenticator;
import otn.clients.OtnClient;
import otn.interfaces.IAuthenticator;

import otn.model.Categories;
import otn.model.Products;
import otn.model.Response;
import otn.model.marketplace.*;

@ManagedBean(name = "myServicesBean")
@ViewScoped
public class MyServicesBean implements Serializable {

	// ************************* Variables **************************

	/** Application scoped bean that holds global information */
	@ManagedProperty(value = "#{otnDataBean}")
	private DataBean dataBean;

	/***/
	private IAuthenticator authenticator;

	/**
	 * The available services a user has provided (saved into the "service" data
	 * table in the db)
	 */
	private List<ServiceMin> serviceList;

	/**
	 * The available services a user has purchased and are available into the
	 * "purchase" in the db.
	 */
	private List<ServiceMin> purchasedServiceList;

	/** The available service categories in the db. */
	private List<ServiceCategoryMin> serviceCategories;

	/** The available cities in the db. */
	private List<CityMin> cities;

	private List<? extends Categories> categories;
	private List<? extends Products> products;

	/* this is defined in the settings.properties file */
	/* http://localhost:9080/web/guest/ */
	private String redirectUrl;

	private Long userLiferayId;

	/** The selected provided service */
	private ServiceMin selectedService;

	/** The selected purchased service */
	private ServiceMin selectedPurchasedService;
	
	FacesContext context = FacesContext.getCurrentInstance();
	PortletRequest portletRequest = (PortletRequest) context.getExternalContext().getRequest();
	ThemeDisplay themeDisplay = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
	
	private String url = "";
	private String realUrl = "";
	private String tempUrl = "";

	// ************************* Functions **************************

	public MyServicesBean() {

		System.out.println("initializing myServicesBean bean..");

		FacesContext facesContext = FacesContext.getCurrentInstance();

		// initialize the authenticator
		authenticator = new LiferayAuthenticator();

		userLiferayId = authenticator.getUserID();

		// initialize the DataBean in case this is not done automatically
		if (facesContext.getExternalContext().getApplicationMap()
				.get("otnDataBean") == null) {

			dataBean = new DataBean();
			facesContext.getExternalContext().getApplicationMap()
					.put("otnDataBean", dataBean);

		} else {
			dataBean = (DataBean) facesContext.getExternalContext()
					.getApplicationMap().get("otnDataBean");
		}

		selectedService = new ServiceMin();
		selectedPurchasedService = new ServiceMin();

		serviceList = new ArrayList<ServiceMin>();
		purchasedServiceList = new ArrayList<ServiceMin>();

		cities = dataBean.getCities();
		serviceCategories = dataBean.getServiceCategories();

		fetchProvidedServices(userLiferayId);
		fetchPurchasedServices(userLiferayId);

		System.out.println("serviceList size = " + serviceList.size());

		redirectUrl = authenticator.getSiteUrl();
		
		HttpServletRequest request = PortalUtil
				.getHttpServletRequest((PortletRequest) FacesContext
						.getCurrentInstance().getExternalContext().getRequest());

		HttpServletRequest oriRequest = PortalUtil
				.getOriginalServletRequest(request);

		String param = oriRequest.getParameter("id");

		System.out.println("param = " + param);

		if (param != null) {
			selectedService.setId(Long.parseLong(param));
		}

		try {
			url = PortalUtil.getLayoutURL(themeDisplay.getLayout(),themeDisplay);
			tempUrl = redirectUrl.replaceAll("/web/guest", url);
			realUrl = tempUrl.replaceAll("/my-services", "");
			
			
		} catch (PortalException e1) {
			e1.printStackTrace();
		} catch (SystemException e1) {
			e1.printStackTrace();
		}

	}// end constructor

	/* fetches all services a user has provided, from database */
	private void fetchProvidedServices(Long userLiferayId) {

		// ************************ Variables *************************

		OtnClient client = new OtnClient();

		ClientResponse response;

		ServicesResponse serviceResp;

		GsonBuilder builder = new GsonBuilder();

		Gson gson;

		String jsonResp;

		// ************************ Action ****************************

		System.out
				.println(".....fetchServicesByProviderId was called with userLiferayId = "
						+ userLiferayId);

		// Register an adapter to manage the date types as long values
		builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
			public Date deserialize(JsonElement json,
					java.lang.reflect.Type typeOfT,
					com.google.gson.JsonDeserializationContext context)
					throws JsonParseException {
				return new Date(json.getAsJsonPrimitive().getAsLong());
			}
		});

		gson = builder.create();

		try {

			response = client.getServicesByProviderId(ClientResponse.class,
					userLiferayId, javax.ws.rs.core.MediaType.APPLICATION_JSON);

			client.close();

			jsonResp = response.getEntity(String.class);

			System.out.println("jsonResp:" + jsonResp);

			serviceResp = gson.fromJson(jsonResp,
					new TypeToken<ServicesResponse>() {
					}.getType());

			if (serviceResp != null && serviceResp.getErrorMsg().isEmpty()) {

				serviceList = serviceResp.getServices();

				System.out.println("services.." + serviceList.size());

				for (ServiceMin service : serviceList) {

					if (service.getDate() != null) {

						SimpleDateFormat dateformatJava = new SimpleDateFormat(
								"dd-MM-yyyy");
						service.setDateString(dateformatJava.format(service
								.getDate()));
					}
				}

			} else {

				System.out.println("fetch stso else services.."
						+ serviceList.size());
				// serviceList = new ArrayList<ServiceMin>();
			}

		} catch (Exception e) {

			e.printStackTrace();

			// serviceList = new ArrayList<ServiceMin>();

		}// end catch
	}

	/* fetches all services a user has purchased, from database */
	private void fetchPurchasedServices(Long userLiferayId) {

		// ************************ Variables *************************

		OtnClient client = new OtnClient();

		ClientResponse response;

		ServicesResponse serviceResp;

		GsonBuilder builder = new GsonBuilder();

		Gson gson;

		Response<List<ServiceMin>> resp;

		String jsonResp;

		// ************************ Action ****************************

		System.out
				.println(".....fetchPurchasedServices was called with userLiferayId = "
						+ userLiferayId);

		// Register an adapter to manage the date types as long values
		builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
			public Date deserialize(JsonElement json,
					java.lang.reflect.Type typeOfT,
					com.google.gson.JsonDeserializationContext context)
					throws JsonParseException {
				return new Date(json.getAsJsonPrimitive().getAsLong());
			}
		});

		// gson = builder.create();
		//
		// try {
		//
		// response = client.getUserPurchases(ClientResponse.class,
		// javax.ws.rs.core.MediaType.APPLICATION_JSON, userLiferayId);
		//
		// client.close();
		//
		// jsonResp = response.getEntity(String.class);
		//
		// System.out.println("jsonResp:" + jsonResp);
		//
		// serviceResp = gson.fromJson(jsonResp,
		// new TypeToken<ServicesResponse>() {
		// }.getType());
		//
		// if (serviceResp != null && serviceResp.getErrorMsg().isEmpty()) {
		//
		// purchasedServiceList = new ArrayList<ServiceMin>();
		// purchasedServiceList = serviceResp.getServices();
		//
		//
		//
		// // System.out.println("purchased services.." +
		// purchasedServiceList.size());
		//
		// for (ServiceMin purchasedService : purchasedServiceList) {
		//
		// if (purchasedService.getDate() != null) {
		//
		// SimpleDateFormat dateformatJava = new SimpleDateFormat(
		// "dd-MM-yyyy");
		// purchasedService.setDateString(dateformatJava.format(purchasedService
		// .getDate()));
		// }
		// }
		//
		// }

		gson = builder.create();

		try {

			response = client.getUserPurchases(ClientResponse.class,
					javax.ws.rs.core.MediaType.APPLICATION_JSON, userLiferayId);

			client.close();

			jsonResp = response.getEntity(String.class);

			System.out.println("jsonResp:" + jsonResp);

			resp = gson.fromJson(jsonResp,
					new TypeToken<Response<List<ServiceMin>>>() {
					}.getType());

			if (resp != null && resp.getErrorMsg().isEmpty()) {

				purchasedServiceList = (List<ServiceMin>) resp.getResponse();

				System.out.println("purchased services.."
						+ purchasedServiceList.size());

				for (ServiceMin purchasedService : purchasedServiceList) {

					if (purchasedService.getDate() != null) {

						SimpleDateFormat dateformatJava = new SimpleDateFormat(
								"dd-MM-yyyy");
						purchasedService.setDateString(dateformatJava
								.format(purchasedService.getDate()));
					}
				}

			} else {

				System.out
						.println("inside fetchPurchasedServices, inside else.."
								+ purchasedServiceList.size());
				// serviceList = new ArrayList<ServiceMin>();
			}

		} catch (Exception e) {

			e.printStackTrace();

			// serviceList = new ArrayList<ServiceMin>();

		}// end catch
	}

	public void onTabChange(TabChangeEvent event) {
		// FacesMessage msg = new FacesMessage("Tab Changed", "Active Tab" +
		// event.getTab.getId());
		//
		// FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	/**
	 * On row selection, user is being redirect to the selected challenge
	 * details
	 */
	public void onRowSelect(SelectEvent event) {
		System.out.println("selectedService id = " + selectedService.getId());

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
		
	}
	
	public void onPurchasedRowSelect(SelectEvent event) {
		System.out.println("selectedService id = " + selectedService.getId());

		LiferayFacesContext portletFacesContext = LiferayFacesContext
				.getInstance();
		
		realUrl = tempUrl.replaceAll("/my-purchased-services", "");
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
		
	}
	
	public String purchasedServices() {

		LiferayFacesContext portletFacesContext = LiferayFacesContext
				.getInstance();

		try {
			
			if (url.contains("web/antwerp") || url.contains("web/issy") ||
					url.contains("web/birmingham") || url.contains("web/liberec")){
				portletFacesContext.getExternalContext().redirect(
						realUrl + "/my-purchased-services");
			}else {
				portletFacesContext.getExternalContext().redirect(
						redirectUrl + "/my-purchased-services");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "success";
	}
	
	public String providedServices() {

		LiferayFacesContext portletFacesContext = LiferayFacesContext
				.getInstance();
		realUrl = tempUrl.replaceAll("/my-purchased-services", "");
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
		
		return "success";
	}

	/**
	 * Redirects user to service registration form in order user to update
	 * service details in database
	 */
	public void updateService() {

		System.out.println("updateService was called..selectedService id = ");

		LiferayFacesContext portletFacesContext = LiferayFacesContext
				.getInstance();
		try {
			
			if (url.contains("web/antwerp") || url.contains("web/issy") ||
					url.contains("web/birmingham") || url.contains("web/liberec")){
				portletFacesContext.getExternalContext().redirect(
						realUrl + "/my-services?id="
								+ selectedService.getId());
			}else {
				portletFacesContext.getExternalContext().redirect(
						redirectUrl + "/my-services?id="
								+ selectedService.getId());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Deletes challenge from database, in reality deactivates challenge by
	 * setting isDeleted equals 1
	 * 
	 */
	public void deleteService() {

		OtnClient client = new OtnClient();

		ClientResponse response;

		Gson gson = new Gson();

		String jsonResp;

		// ************************ Action ****************************

		try {

			response = client.deleteService(ClientResponse.class,
					javax.ws.rs.core.MediaType.APPLICATION_JSON,
					selectedService);

			client.close();

			jsonResp = response.getEntity(String.class);

			System.out.println("delete jsonResp:" + jsonResp);

			if (jsonResp.equalsIgnoreCase("true")) {
				System.out
						.println("serviceList.size() = " + serviceList.size());
				serviceList.remove(selectedService);
				System.out.println("serviceList.size after remove = "
						+ serviceList.size());
			}
			
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
			

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	// **********************************************************************

	public DataBean getDataBean() {
		return dataBean;
	}

	public void setDataBean(DataBean dataBean) {
		this.dataBean = dataBean;
	}

	public List<? extends Products> getProducts() {
		return products;
	}

	public void setProducts(List<? extends Products> products) {
		this.products = products;
	}

	public IAuthenticator getAuthenticator() {
		return authenticator;
	}

	public void setAuthenticator(IAuthenticator authenticator) {
		this.authenticator = authenticator;
	}

	public List<? extends Categories> getCategories() {
		return categories;
	}

	public void setCategories(List<? extends Categories> categories) {
		this.categories = categories;
	}

	public List<CityMin> getCities() {
		return cities;
	}

	public void setCities(List<CityMin> cities) {
		this.cities = cities;
	}

	public Long getUserLiferayId() {
		return userLiferayId;
	}

	public void setUserLiferayId(Long userLiferayId) {
		this.userLiferayId = userLiferayId;
	}

	public ServiceMin getSelectedService() {
		return selectedService;
	}

	public void setSelectedService(ServiceMin selectedService) {
		this.selectedService = selectedService;
	}

	public List<ServiceMin> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<ServiceMin> serviceList) {
		this.serviceList = serviceList;
	}

	public List<ServiceCategoryMin> getServiceCategories() {
		return serviceCategories;
	}

	public void setServiceCategories(List<ServiceCategoryMin> serviceCategories) {
		this.serviceCategories = serviceCategories;
	}

	public ServiceMin getSelectedPurchasedService() {
		return selectedPurchasedService;
	}

	public void setSelectedPurchasedService(ServiceMin selectedPurchasedService) {
		this.selectedPurchasedService = selectedPurchasedService;
	}

	public List<ServiceMin> getPurchasedServiceList() {
		return purchasedServiceList;
	}

	public void setPurchasedServiceList(List<ServiceMin> purchasedServiceList) {
		this.purchasedServiceList = purchasedServiceList;
	}

}// end class
