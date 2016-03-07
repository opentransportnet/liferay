package otn.beans;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
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

import org.primefaces.event.FileUploadEvent;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.liferay.faces.portal.context.LiferayFacesContext;
import com.liferay.faces.util.portal.WebKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
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
import otn.model.marketplace.*;

@ManagedBean(name = "serviceRegistrationBean")
@ViewScoped
public class ServiceRegistrationBean implements Serializable {

	// ************************* Variables **************************

	/** Application scoped bean that holds global information */
	@ManagedProperty(value = "#{otnDataBean}")
	private DataBean dataBean;

	/***/
	private IAuthenticator authenticator;

	/***/
	private List<ServiceMin> services;

	/** The available service categories in the db. */
	private List<ServiceCategoryMin> serviceCategories;

	/** The available cities in the db. */
	private List<CityMin> cities;

	// private Integer category = 0;

	private List<? extends Categories> categories;

	private List<? extends Products> products;

	private boolean renderSearch = false;

	private boolean renderMain = true;

	private ServiceMin service;

	private UserMin provider;

	private Integer selectedCategory;

	private ServiceCategoryMin category;

	private Integer selectedCity;

	private String serviceName;

	private String serviceDescription;

	private String serviceUrl;

	private float servicePrice;

	private String serviceFormat;

	private String serviceLicense;
	
	private String thumbnail = "null";

	/* the screenshots of the added service, that the users upload */
	private ServiceScreenshotMin screenshot;
	private List<ServiceScreenshotMin> screenshotList = new ArrayList<ServiceScreenshotMin>();

	/* the original file's name */
	private String fileName;
	private String destination = "";
	private long fileSize;
	private FileEntry fileEntry;

	private String redirectUrl;
	private Folder folder;
	
	FacesContext context = FacesContext.getCurrentInstance();

	PortletRequest portletRequest = (PortletRequest) context.getExternalContext().getRequest();
	ThemeDisplay themeDisplay = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
	private String url = "";
	private String realUrl = "";
	private String tempUrl = "";

	// ************************* Functions **************************

	public ServiceRegistrationBean() {

		System.out.println("initializing service registration bean..");

		FacesContext facesContext = FacesContext.getCurrentInstance();

		authenticator = new LiferayAuthenticator();

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

		// initialize the service
		service = new ServiceMin();

		cities = dataBean.getCities();
		serviceCategories = dataBean.getServiceCategories();

		redirectUrl = authenticator.getSiteUrl();
		
		try {
			url = PortalUtil.getLayoutURL(themeDisplay.getLayout(),themeDisplay);
			tempUrl = redirectUrl.replaceAll("/web/guest", url);
			realUrl = tempUrl.replaceAll("/marketplace-add-service", "");
			
		} catch (PortalException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SystemException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}// end constructor

	public void upload(FileUploadEvent event) throws IOException {

		System.out.println("upload was called...");

		String imageType = (String) event.getComponent().getAttributes()
				.get("imageType");

		System.out.println("imageType = " + imageType);

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

		copyFile(fileName, event.getFile().getInputstream(), imageType);
	}

	public void copyFile(String fileName, InputStream in, String imageType) {

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

			// // for InputStream
			// InputStream is = null;
			// try {
			// is = new FileInputStream(file);
			// } catch (FileNotFoundException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }

			// // for file size
			// double bytes = file.length();
			// long size = (long) (bytes / 1024);

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

				/**/
				if (imageType.equalsIgnoreCase("thumbnail")) {
					setThumbnail(path);
				} else if (imageType.equalsIgnoreCase("screenshot")) {
					System.out.println("screenshot " + fileEntry.getTitle()
							+ path);
					screenshot = new ServiceScreenshotMin();
					screenshot.setUrl(path);

					System.out.println("screenshot.getUrl = " + screenshot.getUrl());
					screenshotList.add(screenshot);
					
					System.out.println("screenshotList size = " + screenshotList.size());
				}

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
		System.out.println("serviceName = " + serviceName);
		service.setName(serviceName);
		service.setDescription(serviceDescription);
		service.setServiceUrl(serviceUrl);
		service.setPrice(servicePrice);

		System.out.println("getThumbnail() = " + getThumbnail());

		/*
		 * checks if user has uploaded a thumbnail, if not a default thumbnail
		 * icon stored in the documentent library is assigned to the newly added
		 * service
		 */
		if (getThumbnail().equalsIgnoreCase("null")) {

			try {
				/* the id of the default thumbnail icon */
				long fileEntryId = Long.parseLong(dataBean.getFileEntryId());
				System.out.println("inside fileEntryId = " + fileEntryId);

				fileEntry = DLAppServiceUtil.getFileEntry(fileEntryId);

				/*System.out.println("RepositoryId = "
						+ fileEntry.getRepositoryId());
				System.out.println("FolderId = " + fileEntry.getFolderId());
				System.out.println("Title = " + fileEntry.getTitle());
				System.out.println("getUuid = " + fileEntry.getUuid());*/

				String path = "/documents/" + fileEntry.getRepositoryId() + "/"
						+ fileEntry.getFolderId() + "/" + fileEntry.getTitle()
						+ "/" + fileEntry.getUuid();

				System.out.println("thumbnail path = " + path);

				service.setThumbnail(path);

			} catch (PortalException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// String defaultThumb =
			// "/documents/repositoryId/folderID/defaultServiceIcon/fileEntryID";

			// String defaultThumb =
			// "/otn_marketplace/images/defaultServiceIcon.png";
			// service.setThumbnail(defaultThumb);

		}

		/*
		 * checks if user has uploaded screenshots, if not a default screenshot
		 * stored in the document library is assigned to the newly added service
		 */

		if (getScreenshotList().size() == 0) {

			try {

				long screenshotFileEntryId = Long.parseLong(dataBean
						.getScreenshotFileEntryId());
				System.out.println("inside screenshotFileEntryId = "
						+ screenshotFileEntryId);

				fileEntry = DLAppServiceUtil
						.getFileEntry(screenshotFileEntryId);

				/*System.out.println("RepositoryId = "
						+ fileEntry.getRepositoryId());
				System.out.println("FolderId = " + fileEntry.getFolderId());
				System.out.println("Title = " + fileEntry.getTitle());
				System.out.println("getUuid = " + fileEntry.getUuid());*/

				String path = "/documents/" + fileEntry.getRepositoryId() + "/"
						+ fileEntry.getFolderId() + "/" + fileEntry.getTitle()
						+ "/" + fileEntry.getUuid();

				//System.out.println("path = " + path);
				
				screenshot = new ServiceScreenshotMin();
				screenshot.setUrl(path);

				System.out.println("screenshot.getUrl = " + screenshot.getUrl());
				screenshotList.add(screenshot);

				//service.setScreenshotList(screenshotList);

			} catch (PortalException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		/*else{
			service.setScreenshotList(screenshotList);
		}*/

		service.setTotalRating(0);
		service.setCityId(selectedCity);
		service.setFormat(serviceFormat);
		service.setLicense(serviceLicense);

		category = new ServiceCategoryMin();
		category.setId(selectedCategory);
		service.setCategory(category);

		provider = new UserMin();
		provider.setLiferayId(authenticator.getUserID());
		provider.setName(authenticator.getUserName());
		provider.setEmail(authenticator.getUserEmail());

		service.setProvider(provider);

		System.out.println("liferayId = " + authenticator.getUserID());

		response = client.addService(ClientResponse.class,
				javax.ws.rs.core.MediaType.APPLICATION_JSON,
				gson.toJson(service));

		client.close();

		System.out.println("service response = " + response);
		System.out.println("gson.toJson(service): " + gson.toJson(service));

		String serviceId = response.getEntity(String.class);
		System.out.println("service was added into db, serviceId = "
				+ serviceId);

		/* checks if service has been added successfully */
		if (serviceId != null) {

			FacesMessage msg = new FacesMessage(
					"Registration was completed successfully!");
			FacesContext.getCurrentInstance().addMessage(null, msg);

			/*
			 * after the user service has been added to the db, we checks if
			 * user has uploaded any screenshots, and we add them to the db
			 */
			if (screenshotList.size() > 0) {

				long servicePerId = Long.parseLong(serviceId);
				service.setId(servicePerId);

				for (ServiceScreenshotMin uploadedScreenshot : screenshotList) {

					System.out.println("service screenshot url = "
							+ uploadedScreenshot.getUrl());

					uploadedScreenshot.setService(service);

					response = client.addServiceScreenshot(
							ClientResponse.class,
							javax.ws.rs.core.MediaType.APPLICATION_JSON,
							gson.toJson(uploadedScreenshot));

					client.close();

					String uploadedScreenshotId = response
							.getEntity(String.class);
					System.out
							.println("service screenshot was added into db, uploadedScreenshotId = "
									+ uploadedScreenshotId);
				}
			}

		}

		LiferayFacesContext portletFacesContext = LiferayFacesContext
				.getInstance();
		
		try {
			
			if (url.contains("web/antwerp") || url.contains("web/issy") ||
					url.contains("web/birmingham") || url.contains("web/liberec")){
				portletFacesContext.getExternalContext().redirect(
						realUrl + "/service-details?id=" + serviceId);
			}else {
				portletFacesContext.getExternalContext().redirect(
						redirectUrl + "/service-details?id=" + serviceId);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void allServices () {
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
	
	public void onCategorySelected(AjaxBehaviorEvent e) {

		System.out.println("selectedCat:" + selectedCategory);

	}// end onCategorySelected()

	public void onCitySelected(AjaxBehaviorEvent e) {

		System.out.println("selectedCity:" + selectedCity);

	}// end onCategorySelected()

	private void addFileToDocumentLibrary() {

		FacesContext context = FacesContext.getCurrentInstance();

		ThemeDisplay themeDisplay = (ThemeDisplay) context.getExternalContext()
				.getRequestMap().get(WebKeys.THEME_DISPLAY);

		// long FOLDER_ID = 11801;
		long FOLDER_ID = Long.parseLong(dataBean.getFolderId());
		long repositoryId = themeDisplay.getScopeGroupId();
		long parentFolderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;

		System.out.println("repositoryId = " + repositoryId
				+ " parentFolderId = " + parentFolderId);

		// Long parentFolderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;
		// DLFolder folder = DLFolderLocalServiceUtil.getFolder(groupId,
		// parentFolderId, dirName);
		// ThemeDisplay themeDisplay = (ThemeDisplay)
		// renderRequest.getAttribute(WebKeys.THEME_DISPLAY);
		// List<DLFileEntry> fileEntryService = null;
		// try {
		// fileEntryService =
		// DLFileEntryLocalServiceUtil.getFileEntries(themeDisplay.getScopeGroupId(),folder.getFolderId(),-1,
		// -1, null); //12345 is folderId
		// } catch (SystemException e) {
		// e.printStackTrace();
		// }
		// for(DLFileEntry fileEntryObj : fileEntryService){
		// System.out.println(fileEntryObj.getTitle());
		// }
	}

	// **********************************************************************

	/**
	 * Returns all cities from db
	 */
	private void fetchCities() {

		// ************************ Variables *************************

		OtnClient client = new OtnClient();

		ClientResponse response;

		Gson gson = new Gson();

		String jsonResp;

		// ************************ Action ****************************

		try {

			response = client.getCities(ClientResponse.class,
					javax.ws.rs.core.MediaType.APPLICATION_JSON);

			client.close();

			jsonResp = response.getEntity(String.class);

			System.out.println("jsonResp:" + jsonResp);

			cities = gson.fromJson(jsonResp, new TypeToken<List<CityMin>>() {
			}.getType());

		} catch (Exception e) {

			e.printStackTrace();

			cities = new ArrayList<CityMin>();

		}

	}// end fetchCities()

	/**
	 * Returns all service categories from db
	 */
	private void fetchServiceCategories() {

		// ************************ Variables *************************

		OtnClient client = new OtnClient();

		ClientResponse response;

		Gson gson = new Gson();

		String jsonResp;

		// ************************ Action ****************************

		try {

			response = client.getServiceCategories(ClientResponse.class,
					javax.ws.rs.core.MediaType.APPLICATION_JSON);

			client.close();

			jsonResp = response.getEntity(String.class);

			System.out.println("jsonResp:" + jsonResp);

			serviceCategories = gson.fromJson(jsonResp,
					new TypeToken<List<ServiceCategoryMin>>() {
					}.getType());

		} catch (Exception e) {

			e.printStackTrace();

			serviceCategories = new ArrayList<ServiceCategoryMin>();
		}

	}// end fetchServiceCategories()

	// **********************************************************************

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

	public List<? extends Products> getProducts() {
		return products;
	}

	public void setProducts(List<? extends Products> products) {
		this.products = products;
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

	public ServiceMin getService() {
		return service;
	}

	public void setService(ServiceMin service) {
		this.service = service;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceDescription() {
		return serviceDescription;
	}

	public void setServiceDescription(String serviceDescription) {
		this.serviceDescription = serviceDescription;
	}

	public String getServiceUrl() {
		return serviceUrl;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	public List<ServiceCategoryMin> getServiceCategories() {
		return serviceCategories;
	}

	public void setServiceCategories(List<ServiceCategoryMin> serviceCategories) {
		this.serviceCategories = serviceCategories;
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

	public UserMin getProvider() {
		return provider;
	}

	public void setProvider(UserMin provider) {
		this.provider = provider;
	}

	public Integer getSelectedCategory() {
		return selectedCategory;
	}

	public void setSelectedCategory(Integer selectedCategory) {
		this.selectedCategory = selectedCategory;
	}

	public Integer getSelectedCity() {
		return selectedCity;
	}

	public void setSelectedCity(Integer selectedCity) {
		this.selectedCity = selectedCity;
	}

	public float getServicePrice() {
		return servicePrice;
	}

	public void setServicePrice(float servicePrice) {
		this.servicePrice = servicePrice;
	}

	public String getServiceFormat() {
		return serviceFormat;
	}

	public void setServiceFormat(String serviceFormat) {
		this.serviceFormat = serviceFormat;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getServiceLicense() {
		return serviceLicense;
	}

	public void setServiceLicense(String serviceLicense) {
		this.serviceLicense = serviceLicense;
	}

	public ServiceScreenshotMin getScreenshot() {
		return screenshot;
	}

	public void setScreenshot(ServiceScreenshotMin screenshot) {
		this.screenshot = screenshot;
	}

	public List<ServiceScreenshotMin> getScreenshotList() {
		return screenshotList;
	}

	public void setScreenshotList(List<ServiceScreenshotMin> screenshotList) {
		this.screenshotList = screenshotList;
	}

}// end class
