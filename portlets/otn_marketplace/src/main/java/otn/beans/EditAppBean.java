package otn.beans;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
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

import otn.model.marketplace.*;

@ManagedBean(name = "editAppBean")
@ViewScoped
public class EditAppBean implements Serializable {

	// ************************* Variables **************************

	private static final long serialVersionUID = 1L;

	/** Application scoped bean that holds global information */
	@ManagedProperty(value = "#{otnDataBean}")
	private DataBean dataBean;

	/***/
	private IAuthenticator authenticator;
	
	/** The available cities in the db. */
	private List<CityMin> cities;

	private AppMin app;
		
	/* the original file's name */
	private String fileName;
	private String destination = "";
	private long fileSize;
	private FileEntry fileEntry;
	
	private String oldfileName;
	private Boolean renderUpdatePanel = false;
	private Boolean renderNoServicesMsg = false;
	
	private Long appId;
	private Boolean renderView = false;
	
	/* this is defined in the settings.properties file */
	/* http://localhost:9080/web/guest/ */
	private String redirectUrl;
	
	FacesContext context = FacesContext.getCurrentInstance();
	PortletRequest portletRequest = (PortletRequest) context.getExternalContext().getRequest();
	ThemeDisplay themeDisplay = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
	
	private String url = "";
	private String realUrl = "";
	private String tempUrl = "";

	// ************************* Functions **************************

	public EditAppBean() {

		System.out.println("initializing service registration bean..");

		FacesContext facesContext = FacesContext.getCurrentInstance();
		
		authenticator = new LiferayAuthenticator();
		//initialize the authenticator
		authenticator = new LiferayAuthenticator();
		

		PermissionChecker permissionChecker = PermissionThreadLocal.getPermissionChecker();

		//initialize the DataBean in case this is not done automatically
		if(facesContext.getExternalContext().getApplicationMap().get("otnDataBean") == null){
			
			dataBean = new DataBean();
			facesContext.getExternalContext().getApplicationMap().put("otnDataBean",dataBean);
			
			
		}else{

			dataBean = (DataBean)facesContext.getExternalContext().getApplicationMap().get("otnDataBean");

		}
		
		HttpServletRequest request = PortalUtil
				.getHttpServletRequest((PortletRequest) FacesContext
						.getCurrentInstance().getExternalContext().getRequest());

		HttpServletRequest oriRequest = PortalUtil
				.getOriginalServletRequest(request);
		
		try {
			request.setCharacterEncoding("UTF-8");
			oriRequest.setCharacterEncoding("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String param = oriRequest.getParameter("id");

		System.out.println("param = " + param);
		
		redirectUrl = authenticator.getSiteUrl();

		if (param != null) {
			appId = Long.parseLong(param);
			
			fetchAppById(appId);
		    
		    if (app.getPictureUrl() != null && !app.getPictureUrl().isEmpty()) 
		    {
			    String[] stringArray = app.getPictureUrl().split("/");
			    oldfileName = stringArray[4];
		    }
		
			cities = dataBean.getCities();

			renderUpdatePanel = true;
			
			if (permissionChecker.isOmniadmin()) 
				renderView = true;
			}
			if (app == null || app.getId() == null){
			renderUpdatePanel = false;
			renderView = false;
			}
		
			
//			else {
//			renderNoServicesMsg = true;
//			renderView = true;
//		}

			try {
				url = PortalUtil.getLayoutURL(themeDisplay.getLayout(),themeDisplay);
				tempUrl = redirectUrl.replaceAll("/web/guest", url);
				realUrl = tempUrl.replaceAll("/edit-app", "");
				
			} catch (PortalException e1) {
				e1.printStackTrace();
			} catch (SystemException e1) {
				e1.printStackTrace();
			}
		
	    
		
	}// end constructor

	
	
	/* fetches the selected challenge from database */
	private void fetchAppById(Long appId) {

		// ************************ Variables *************************

		OtnClient client = new OtnClient();

		ClientResponse response;

		AppResponse appResp;

		GsonBuilder builder = new GsonBuilder();

		Gson gson;

		String jsonResp;

		// ************************ Action ****************************

		System.out
				.println(".........getAppById was called with appId = "
						+ appId);

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

			response = client.getAppById(ClientResponse.class,
					javax.ws.rs.core.MediaType.APPLICATION_JSON, appId);

			client.close();

			jsonResp = response.getEntity(String.class);

			System.out.println("jsonResp:" + jsonResp);

			appResp = gson.fromJson(jsonResp,
					new TypeToken<AppResponse>() {
					}.getType());

			if (appResp != null && appResp.getErrorMsg().isEmpty()) {

				app = appResp.getApp();

				System.out.println("apps..");

			} else {
				app = new AppMin();
			}

		} catch (Exception e) {

			e.printStackTrace();

			app = new AppMin();

		}// end catch
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

					app.setPictureUrl(path);

			} catch (DuplicateFileException e) {
				FacesMessage msg = new FacesMessage(
						"This file already exists. Please upload a new one.");
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
		AppMin updateApp = new AppMin();
		
		updateApp.setId(app.getId());
		updateApp.setTitle(app.getTitle());
		updateApp.setAppAbstract(app.getAppAbstract());
		updateApp.setPictureUrl(app.getPictureUrl());
		
		updateApp.setCityId(app.getCity().getId());
		
	 if (app.getOtnUrl() != null && !app.getOtnUrl().isEmpty() ){
		 if (!app.getOtnUrl().startsWith("http://") && !app.getOtnUrl().startsWith("https://")){
			 updateApp.setOtnUrl("http://"+app.getOtnUrl());
		 }else {
			 updateApp.setOtnUrl(app.getOtnUrl());
		 }
	 }
	 else {
		 updateApp.setOtnUrl(null);
	 }
     
     if (app.getAppleUrl() != null && !app.getAppleUrl().isEmpty()){
    	 if(!app.getAppleUrl().startsWith("http://") && !app.getAppleUrl().startsWith("https://")){
    	 updateApp.setAppleUrl("http://"+app.getAppleUrl());
    	 } else {
    		 updateApp.setAppleUrl(app.getAppleUrl());
    	 }
     }
     else {
    	 updateApp.setAppleUrl(null);
     }
     
     if (app.getGoogleUrl() != null && !app.getGoogleUrl().isEmpty()){
    	 if(!app.getGoogleUrl().startsWith("http://") && !app.getGoogleUrl().startsWith("https://")){
    		 updateApp.setGoogleUrl("http://"+app.getGoogleUrl());
    	 } else {
    		 updateApp.setGoogleUrl(app.getGoogleUrl());
    	 }
     }
     else {
    	 updateApp.setGoogleUrl(null);
     }
     
     if (app.getWindowsUrl() != null && !app.getWindowsUrl().isEmpty()){
    	 if(!app.getWindowsUrl().startsWith("http://") && !app.getWindowsUrl().startsWith("https://")){
    	 updateApp.setWindowsUrl("http://"+app.getWindowsUrl());
    	 }
    	  else {
    		 updateApp.setWindowsUrl(app.getWindowsUrl());
    	 }
     }
     else {
    	 updateApp.setWindowsUrl(null);
     }
		

		response = client.updateApp(ClientResponse.class,
				javax.ws.rs.core.MediaType.APPLICATION_JSON,
				gson.toJson(updateApp));

		client.close();

		System.out.println("updateApp response = " + response);
		System.out.println("gson.toJson(updateChallege): " + gson.toJson(updateApp));

		String appId = response.getEntity(String.class);
		System.out.println("app was updated into db, appId = "
				+ appId);

		if (appId.equalsIgnoreCase("true")) {
			FacesMessage msg = new FacesMessage(
					"App was updated successfully!");
			FacesContext.getCurrentInstance().addMessage(null, msg);
			
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
	
	public void onDelete(){
		
		OtnClient client = new OtnClient();

		ClientResponse response;

		Gson gson = new Gson();

		String jsonResp;

		// ************************ Action ****************************

		try {

			response = client.deleteApp(ClientResponse.class,
					javax.ws.rs.core.MediaType.APPLICATION_JSON, app);

			client.close();

			jsonResp = response.getEntity(String.class);

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
			
		}
		
	
	public void onCitySelected(AjaxBehaviorEvent e) {

		System.out.println("onCitySelected, selectedCity:"
				+ app.getCity().getId());

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
	public String getOldfileName() {
		return oldfileName;
	}

	public void setOldfileName(String oldfileName) {
		this.oldfileName = oldfileName;
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



	public AppMin getApp() {
		return app;
	}



	public void setApp(AppMin app) {
		this.app = app;
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



	public Long getAppId() {
		return appId;
	}



	public void setAppId(Long appId) {
		this.appId = appId;
	}
	
	public List<CityMin> getCities() {
		return cities;
	}

	public void setCities(List<CityMin> cities) {
		this.cities = cities;
	}

	public Boolean getRenderView() {
		return renderView;
	}

	public void setRenderView(Boolean renderView) {
		this.renderView = renderView;
	}
}// end class
