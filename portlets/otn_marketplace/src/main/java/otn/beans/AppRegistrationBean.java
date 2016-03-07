package otn.beans;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

import otn.model.marketplace.*;

@ManagedBean(name = "appRegistrationBean")
@ViewScoped
public class AppRegistrationBean implements Serializable {

	// ************************* Variables **************************

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Application scoped bean that holds global information */
	@ManagedProperty(value = "#{otnDataBean}")
	private DataBean dataBean;

	/***/
	private IAuthenticator authenticator;

	/** The available cities in the db. */
	private List<CityMin> cities;
	
	private List<AppMin> apps;

	private AppMin app;

	private String appTitle;

	private String appAbstract;

	private String appOtnUrl;

	private String appGoogleUrl;

	private String appAppleUrl;
	
	private String appWindowsUrl;
	
	private String appPictureUrl;
	
	private Integer selectedCity;

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

	public AppRegistrationBean() {

		System.out.println("initializing app registration bean..");

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

		// initialize the app
		app = new AppMin();

		cities = dataBean.getCities();
		
		redirectUrl = authenticator.getSiteUrl();
		
		try {
			url = PortalUtil.getLayoutURL(themeDisplay.getLayout(),themeDisplay);
			tempUrl = redirectUrl.replaceAll("/web/guest", url);
			realUrl = tempUrl.replaceAll("/add-app", "");
			
		} catch (PortalException e1) {
			e1.printStackTrace();
		} catch (SystemException e1) {
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
				app.setPictureUrl(path);

			} catch (DuplicateFileException e) {
				FacesMessage msg = new FacesMessage(
						"This picture already exists. Please upload a new one.");
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

	public void allApps() {
		LiferayFacesContext portletFacesContext = LiferayFacesContext
				.getInstance();
		
		try {
			
			if (url.contains("web/antwerp") || url.contains("web/issy") ||
					url.contains("web/birmingham") || url.contains("web/liberec")){
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
	}
	
	public void onSubmit() {

		// ************************ Variables *************************
		OtnClient client = new OtnClient();
		/** The response of a web service call */
		ClientResponse response;
		/** Object to transform a json reponse to java class and vice versa */
		Gson gson = new Gson();

		// ************************ Action ****************************
		System.out.println("app title = " + appTitle);
		app.setTitle(appTitle);
		app.setAppAbstract(appAbstract);
		app.setCityId(selectedCity);
		
		 if (appOtnUrl != null && !appOtnUrl.isEmpty()){
			 if (!appOtnUrl.startsWith("http://") && !appOtnUrl.startsWith("https://")){
        	   app.setOtnUrl("http://"+appOtnUrl);
           }
			 else{
			 app.setOtnUrl(appOtnUrl);
			 }
		 }else{
			 app.setOtnUrl(null);
		 }
        
        if (appAppleUrl != null && !appAppleUrl.isEmpty()){
        	if (!appAppleUrl.startsWith("http://") && !appAppleUrl.startsWith("https://")){
        		app.setAppleUrl("http://"+appAppleUrl);
        	}
        	else {
        		app.setAppleUrl(appAppleUrl);
        	}
        }else{
			 app.setAppleUrl(null);
        }
        
        if (appGoogleUrl != null && !appGoogleUrl.isEmpty()){
        	if (!appGoogleUrl.startsWith("http://") && !appGoogleUrl.startsWith("https://")){
        		app.setGoogleUrl("http://"+appGoogleUrl);
        	} else {
        	app.setGoogleUrl(appGoogleUrl);
        	}
        }else {
        	app.setGoogleUrl(null);
        }
        	
        if (appWindowsUrl != null && !appWindowsUrl.isEmpty()){
        	if (!appWindowsUrl.startsWith("http://") && !appWindowsUrl.startsWith("https://") ){
        		app.setWindowsUrl("http://"+appWindowsUrl);
        	} else{
        		app.setWindowsUrl(appWindowsUrl);
        	}
        }else{
    		app.setWindowsUrl(null);
    	}
        	
		/*
		 * checks if user has uploaded screenshots, if not a default screenshot
		 * stored in the document library is assigned to the newly added app
		 */

		System.out.println("liferayId = " + authenticator.getUserID());

		response = client.addApp(ClientResponse.class,
				javax.ws.rs.core.MediaType.APPLICATION_JSON,
				gson.toJson(app));

		client.close();

		System.out.println("app response = " + response);
		System.out.println("gson.toJson(app): " + gson.toJson(app));

		String appId = response.getEntity(String.class);
		System.out.println("app was added into db, appId = "
				+ appId);

		/* checks if app has been added successfully */
		if (appId != null) {

			FacesMessage msg = new FacesMessage(
					"Registration of app was completed successfully!");
			FacesContext.getCurrentInstance().addMessage(null, msg);

		}

		LiferayFacesContext portletFacesContext = LiferayFacesContext
				.getInstance();
		
		try {
			
			if (url.contains("web/antwerp") || url.contains("web/issy") ||
					url.contains("web/birmingham") || url.contains("web/liberec")){
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
		
	}

	public void onCitySelected(AjaxBehaviorEvent e) {

		System.out.println("selectedCity:" + selectedCity);

	}
	
	// **********************************************************************

	public DataBean getDataBean() {
		return dataBean;
	}

	public void setDataBean(DataBean dataBean) {
		this.dataBean = dataBean;
	}

	public IAuthenticator getAuthenticator() {
		return authenticator;
	}

	public void setAuthenticator(IAuthenticator authenticator) {
		this.authenticator = authenticator;
	}

	public List<AppMin> getApps() {
		return apps;
	}

	public void setApps(List<AppMin> apps) {
		this.apps = apps;
	}

	public AppMin getApp() {
		return app;
	}

	public void setApp(AppMin app) {
		this.app = app;
	}

	public String getAppTitle() {
		return appTitle;
	}

	public void setAppTitle(String appTitle) {
		this.appTitle = appTitle;
	}

	public String getAppAbstract() {
		return appAbstract;
	}

	public void setAppAbstract(String appAbstract) {
		this.appAbstract = appAbstract;
	}

	public String getAppOtnUrl() {
		return appOtnUrl;
	}

	public void setAppOtnUrl(String appOtnUrl) {
		this.appOtnUrl = appOtnUrl;
	}

	public String getAppGoogleUrl() {
		return appGoogleUrl;
	}

	public void setAppGoogleUrl(String appGoogleUrl) {
		this.appGoogleUrl = appGoogleUrl;
	}

	public String getAppAppleUrl() {
		return appAppleUrl;
	}

	public void setAppAppleUrl(String appAppleUrl) {
		this.appAppleUrl = appAppleUrl;
	}

	public String getAppWindowsUrl() {
		return appWindowsUrl;
	}

	public void setAppWindowsUrl(String appWindowsUrl) {
		this.appWindowsUrl = appWindowsUrl;
	}

	public String getAppPictureUrl() {
		return appPictureUrl;
	}

	public void setAppPictureUrl(String appPictureUrl) {
		this.appPictureUrl = appPictureUrl;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public FileEntry getFileEntry() {
		return fileEntry;
	}

	public void setFileEntry(FileEntry fileEntry) {
		this.fileEntry = fileEntry;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public Folder getFolder() {
		return folder;
	}

	public void setFolder(Folder folder) {
		this.folder = folder;
	}
	
	public List<CityMin> getCities() {
		return cities;
	}

	public void setCities(List<CityMin> cities) {
		this.cities = cities;
	}

	public Integer getSelectedCity() {
		return selectedCity;
	}

	public void setSelectedCity(Integer selectedCity) {
		this.selectedCity = selectedCity;
	}

}// end class
