package otn.beans;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.event.FileUploadEvent;
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
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionThreadLocal;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.DuplicateFileException;
import com.liferay.portlet.documentlibrary.model.DLFolderConstants;
import com.liferay.portlet.documentlibrary.service.DLAppServiceUtil;
import com.sun.jersey.api.client.ClientResponse;
import otn.authenticator.LiferayAuthenticator;
import otn.clients.OtnClient;
import otn.interfaces.IAuthenticator;

import otn.model.Categories;
import otn.model.Products;
import otn.model.Response;
import otn.model.marketplace.*;

@ManagedBean(name = "editServiceBean")
@ViewScoped
public class EditServiceBean implements Serializable {

	// ************************* Variables **************************

	/** Application scoped bean that holds global information */
	@ManagedProperty(value = "#{otnDataBean}")
	private DataBean dataBean;

	/***/
	private IAuthenticator authenticator;

	/** The available service categories in the db. */
	private List<ServiceCategoryMin> serviceCategories;

	/** The available cities in the db. */
	private List<CityMin> cities;

	private List<? extends Categories> categories;

	private List<? extends Products> products;

	private ServiceMin service;

	private ServiceCategoryMin category;

	private String thumbnail;

	private Long serviceId;
	
	private Boolean renderUpdatePanel = false;
	
	private Boolean renderNoServicesMsg = false;

	/* this is defined in the settings.properties file */
	/* http://localhost:9080/web/guest/ */
	private String redirectUrl;

	/* the original file's name */
	private String fileName;
	private String destination = "";
	private long fileSize;
	private FileEntry fileEntry;
	private Boolean renderView = false;
	
	FacesContext context = FacesContext.getCurrentInstance();
	PortletRequest portletRequest = (PortletRequest) context.getExternalContext().getRequest();
	ThemeDisplay themeDisplay = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
	
	private String url = "";
	private String realUrl = "";
	private String tempUrl = "";

	/** the liferay user id of the signed in user */
	private Long userLiferayId;

	// ************************* Functions **************************

	public EditServiceBean() {

		System.out.println("initializing edit service bean..");

		FacesContext facesContext = FacesContext.getCurrentInstance();
		
		
		// initialize the authenticator
		authenticator = new LiferayAuthenticator();
		
		userLiferayId = authenticator.getUserID();				
		
		redirectUrl = authenticator.getSiteUrl(); 
		
		PermissionChecker permissionChecker = PermissionThreadLocal.getPermissionChecker();


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

		} else {

			dataBean = (DataBean) facesContext.getExternalContext()
					.getApplicationMap().get("otnDataBean");

		}

		HttpServletRequest request = PortalUtil
				.getHttpServletRequest((PortletRequest) FacesContext
						.getCurrentInstance().getExternalContext().getRequest());

		HttpServletRequest oriRequest = PortalUtil
				.getOriginalServletRequest(request);

		String param = oriRequest.getParameter("id");

		System.out.println("param = " + param);

		if (param != null) {
			serviceId = Long.parseLong(param);
			renderUpdatePanel = true;
			
			service = new ServiceMin();
			cities = dataBean.getCities();
			System.out.println("cities.size = " + cities.size());
			serviceCategories = dataBean.getServiceCategories();
			
			fetchServiceById(serviceId);
			
			if (service.getProvider() != null){
				if ( userLiferayId.equals(service.getProvider().getLiferayId()) || permissionChecker.isOmniadmin())
					renderView = true;
				}

			}else {
				renderNoServicesMsg = true;
				renderView = true;
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

	  	}
	
		
	/* fetches the selected service from database */
	private void fetchServiceById(Long serviceId) {

		// ************************ Variables *************************

		OtnClient client = new OtnClient();

		ClientResponse response;

		Response<ServiceMin> serviceResp;

		GsonBuilder builder = new GsonBuilder();

		Gson gson;

		String jsonResp;

		// ************************ Action ****************************

		System.out
				.println(".........getServicesById was called with serviceId = "
						+ serviceId);

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

			response = client.getServiceDetails(ClientResponse.class,
					javax.ws.rs.core.MediaType.APPLICATION_JSON, serviceId);

			client.close();

			jsonResp = response.getEntity(String.class);

			System.out.println("jsonResp:" + jsonResp);

			serviceResp = gson.fromJson(jsonResp,
					new TypeToken<Response<ServiceMin>>() {
					}.getType());

			if (serviceResp != null && serviceResp.getErrorMsg().isEmpty()) {

				service = (ServiceMin) serviceResp.getResponse();

				if (service.getDate() != null) {

					SimpleDateFormat dateformatJava = new SimpleDateFormat(
							"dd-MM-yyyy");
					service.setDateString(dateformatJava.format(service
							.getDate()));
				}
					
				String pathImage = authenticator.getPortalUrl()
						+ service.getThumbnail();

				System.out.println("pathImage = " + pathImage);

				service.setThumbnail(pathImage);

			} else {

				service = new ServiceMin();
			}

		} catch (Exception e) {

			e.printStackTrace();

			service = new ServiceMin();
		}

	}

	public void upload(FileUploadEvent event) throws IOException {

		System.out.println("upload was called...");

		FacesMessage msg = new FacesMessage("Success! ", event.getFile()
				.getFileName() + " is uploaded.");

		FacesContext.getCurrentInstance().addMessage(null, msg);

		PortletRequest req = (PortletRequest) FacesContext.getCurrentInstance()
				.getExternalContext().getRequest();
		// String path=req.getContextPath();

		PortletContext pc = req.getPortletSession().getPortletContext();
		System.out.println("realPath = " + pc.getRealPath("/"));
		// C:\Users\SVasileiou\Documents\liferay-portal-6.1.1-ce-ga2\tomcat-7.0.27\webapps\configBuilder-portlet

		destination = pc.getRealPath("/");
		System.out.println("destination = " + destination);

		fileName = event.getFile().getFileName().toString();
		System.out.println("filename = " + fileName);

		copyFile(fileName, event.getFile().getInputstream());
	}

	public void copyFile(String fileName, InputStream in) {

		try {
			System.out.println("destination = " + destination);

			File file = new File(destination + fileName);

			double fileInBytes = file.length();
			fileSize = (long) (fileInBytes / 1024);

			// write the inputStream to a FileOutputStream
			OutputStream out = new FileOutputStream(file);

			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = in.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}

			FacesContext context = FacesContext.getCurrentInstance();

			ThemeDisplay themeDisplay = (ThemeDisplay) context
					.getExternalContext().getRequestMap()
					.get(WebKeys.THEME_DISPLAY);

			// long folderId = 11801;
			long folderId = Long.parseLong(dataBean.getFolderId());
			long repositoryId = themeDisplay.getScopeGroupId();
			long parentFolderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;

			String sourceFileName = fileName;

			System.out.println("---------------------");

			System.out.println("repositoryId = " + repositoryId
					+ " parentFolderId = " + parentFolderId);
			System.out.println("---------------------");

			// mimetype
			String mimeType = "";
			// for title
			String title = fileName;
			// for description
			String description = fileName;
			// changeLog
			String changeLog = "";

			// service Context Object
			String uuid = UUID.randomUUID().toString();

			Date date = new Date();
			ServiceContext serviceContext = new ServiceContext();
			serviceContext.setUuid(uuid);
			serviceContext.setCreateDate(date);
			serviceContext.setModifiedDate(date);

			try {
				fileEntry = DLAppServiceUtil.addFileEntry(repositoryId,
						folderId, sourceFileName, mimeType, title, description,
						changeLog, in, fileSize, serviceContext);

				System.out.println("RepositoryId = "
						+ fileEntry.getRepositoryId());
				System.out.println("FolderId = " + fileEntry.getFolderId());
				System.out.println("Title = " + fileEntry.getTitle());
				System.out.println("getUuid = " + fileEntry.getUuid());

				// the path used by DL is
				// /documents/repositoryId/folderID/fileName/fileEntryID
				String path = "/documents/" + fileEntry.getRepositoryId() + "/"
						+ fileEntry.getFolderId() + "/" + fileEntry.getTitle()
						+ "/" + fileEntry.getUuid();

				System.out.println("path = " + path);

				service.setThumbnail(path);

			} catch (DuplicateFileException e) {
				FacesMessage msg = new FacesMessage(
						"This thumbnail already exists. Please upload a new one.");
				FacesContext.getCurrentInstance().addMessage(null, msg);

			} catch (PortalException e) {
				System.out.println("----------------portalexception");
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SystemException e) {
				System.out.println("----------------SystemException");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			in.close();
			out.flush();
			out.close();

			// System.out.println("New file created!");
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public void onSubmit() {

		// ************************ Variables *************************
		OtnClient client = new OtnClient();
		/** The response of a web service call */
		ClientResponse response;
		/** Object to transform a json reponse to java class and vice versa */
		Gson gson = new Gson();

		// ************************ Action ****************************
		ServiceMin updateService = new ServiceMin();

		updateService.setId(service.getId());
		updateService.setName(service.getName());
		updateService.setDescription(service.getDescription());
		updateService.setFormat(service.getFormat());
		updateService.setPrice(service.getPrice());
		updateService.setServiceUrl(service.getServiceUrl());
		
		if (service.getThumbnail().equalsIgnoreCase("null")){
			updateService.setThumbnail("/otn_marketplace/images/default-icon-service.jpg");
		}else{
			updateService.setThumbnail(service.getThumbnail());
		}

		updateService.setCityId(service.getCity().getId());

		category = new ServiceCategoryMin();
		category.setId(service.getCategory().getId());
		updateService.setCategory(category);

		response = client.updateService(ClientResponse.class,
				javax.ws.rs.core.MediaType.APPLICATION_JSON,
				gson.toJson(updateService));

		client.close();

		System.out.println("updateService response = " + response);
		System.out.println("gson.toJson(updateService): "
				+ gson.toJson(updateService));

		String serviceId = response.getEntity(String.class);
		System.out.println("service was updated into db, serviceId = "
				+ serviceId);

		if (serviceId.equalsIgnoreCase("true")) {
			FacesMessage msg = new FacesMessage(
					"Service was updated successfully!");
			FacesContext.getCurrentInstance().addMessage(null, msg);

			LiferayFacesContext portletFacesContext = LiferayFacesContext
					.getInstance();
			
			try {
				
				if (url.contains("web/antwerp") || url.contains("web/issy") ||
						url.contains("web/birmingham") || url.contains("web/liberec")){
					portletFacesContext.getExternalContext().redirect(
							realUrl + "/service-details?id="
									+ service.getId());
				}else {
					portletFacesContext.getExternalContext().redirect(
							redirectUrl + "/service-details?id="
									+ service.getId());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else {

		}

	}

	public void onCancel() {

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

	public void onCategorySelected(AjaxBehaviorEvent e) {

		System.out.println("onCategorySelected, selectedCat:"
				+ service.getCategory().getId());

	}// end onCategorySelected()

	public void onCitySelected(AjaxBehaviorEvent e) {

		System.out.println("onCitySelected, selectedCity:"
				+ service.getCity().getId());

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

	public List<ServiceCategoryMin> getServiceCategories() {
		return serviceCategories;
	}

	public void setServiceCategories(List<ServiceCategoryMin> serviceCategories) {
		this.serviceCategories = serviceCategories;
	}

	public ServiceCategoryMin getCategory() {
		return category;
	}

	public void setCategory(ServiceCategoryMin category) {
		this.category = category;
	}

	public ServiceMin getService() {
		return service;
	}

	public void setService(ServiceMin service) {
		this.service = service;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public Boolean getRenderUpdatePanel() {
		return renderUpdatePanel;
	}

	public void setRenderUpdatePanel(Boolean renderUpdatePanel) {
		this.renderUpdatePanel = renderUpdatePanel;
	}
	
	public Boolean getRenderNoServicesMsg() {
		return renderNoServicesMsg;
	}

	public void setRenderNoServicesMsg(Boolean renderNoServicesMsg) {
		this.renderNoServicesMsg = renderNoServicesMsg;
	}
	
	public Boolean getRenderView() {
		return renderView;
	}

	public void setRenderView(Boolean renderView) {
		this.renderView = renderView;
	}
}// end class
