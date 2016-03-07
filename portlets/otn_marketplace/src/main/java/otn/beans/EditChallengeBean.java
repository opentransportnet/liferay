package otn.beans;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
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
import otn.model.marketplace.*;

@ManagedBean(name = "editChallengeBean")
@ViewScoped
public class EditChallengeBean implements Serializable {

	// ************************* Variables **************************

	/** Application scoped bean that holds global information */
	@ManagedProperty(value = "#{otnDataBean}")
	private DataBean dataBean;

	/***/
	private IAuthenticator authenticator;

	/** The available challenge categories in the db. */
	private List<ChallengeCategoryMin> challengeCategories;

	/** The available cities in the db. */
	private List<CityMin> cities;

	private List<? extends Categories> categories;

	private List<? extends Products> products;

	private ChallengeMin challenge;
	
	private String skillsView;
	
	/* the original file's name */
	private String fileName;
	private String destination = "";
	private long fileSize;
	private FileEntry fileEntry;
	
	private String oldfileName;
	private Boolean renderUpdatePanel = false;
	private Boolean renderNoServicesMsg = false;
	
	private Boolean renderView = false;

	/** the liferay user id of the signed in user */
	private Long userLiferayId;
	

	//private UserMin owner;

	//private Integer selectedCategory;

	private ChallengeCategoryMin category;

//	private Integer selectedCity;
//
//	private String challengeTitle;
//
//	private String challengeDescription;
//
//	private float reward;
//
//	private String output;
//
//	private String skills;
	
	private Long challengeId;
	
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

	public EditChallengeBean() {

		System.out.println("initializing service registration bean..");

		authenticator = new LiferayAuthenticator();
		userLiferayId = authenticator.getUserID();				
		
		PermissionChecker permissionChecker = PermissionThreadLocal.getPermissionChecker();		
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		
//		if(dataBean == null){
//			dataBean = (DataBean)facesContext.getExternalContext().getApplicationMap().get("otnDataBean");
//		}

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
		
		String param = oriRequest.getParameter("id");

		System.out.println("param = " + param);
		
		redirectUrl = authenticator.getSiteUrl();

		if (param != null) {
			challengeId = Long.parseLong(param);
			renderUpdatePanel = true;
			fetchChallengeById(challengeId);
			
			cities = dataBean.getCities();	
		    challengeCategories = dataBean.getChallengeCategories();
		    
		
		    if (challenge.getDocumentUrl() != null && !challenge.getDocumentUrl().isEmpty()) {
			    String[] stringArray = challenge.getDocumentUrl().split("/");
			    oldfileName = stringArray[4];
		    }
		    
			if (challenge.getOwner() != null){
				if (userLiferayId.equals(challenge.getOwner().getLiferayId()) || permissionChecker.isOmniadmin()) 
					renderView = true;
				} 
		}else {
			renderNoServicesMsg = true;
			renderView = true;
		}

		

		//challenge = new ChallengeMin();
		
	    
//	    if (challenge != null) {
//		    if (userLiferayId.equals(challenge.getOwner().getLiferayId()) || permissionChecker.isOmniadmin()) 
//				renderView = true;
//		    }
//	    
		try {
			url = PortalUtil.getLayoutURL(themeDisplay.getLayout(),themeDisplay);
			tempUrl = redirectUrl.replaceAll("/web/guest", url);
			realUrl = tempUrl.replaceAll("/my-challenges", "");
			
		} catch (PortalException e1) {
			e1.printStackTrace();
		} catch (SystemException e1) {
			e1.printStackTrace();
		}

	}// end constructor

	
	
	/* fetches the selected challenge from database */
	private void fetchChallengeById(Long challengeId) {

		// ************************ Variables *************************

		OtnClient client = new OtnClient();

		ClientResponse response;

		ChallengeResponse challengeResp;

		GsonBuilder builder = new GsonBuilder();

		Gson gson;

		String jsonResp;

		// ************************ Action ****************************

		System.out
				.println(".........getChallengesById was called with challengeId = "
						+ challengeId);

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

			response = client.getChallengeById(ClientResponse.class,
					javax.ws.rs.core.MediaType.APPLICATION_JSON, challengeId);

			client.close();

			jsonResp = response.getEntity(String.class);

			System.out.println("jsonResp:" + jsonResp);

			challengeResp = gson.fromJson(jsonResp,
					new TypeToken<ChallengeResponse>() {
					}.getType());

			if (challengeResp != null && challengeResp.getErrorMsg().isEmpty()) {

				challenge = challengeResp.getChallenge();

				System.out.println("challenges..");

				// System.out.println(challenge.getSkillList().size());
				
				ArrayList<String> skillList = new ArrayList(
						Arrays.asList(challenge.getSkills().split("\\s*,\\s*")));
				
				challenge.setSkillList(skillList);

				System.out.println(skillList.size());

				SimpleDateFormat dateformatJava = new SimpleDateFormat(
						"dd-MM-yyyy");
				challenge.setDateString(dateformatJava.format(challenge
						.getDate()));

			} else {
				challenge = new ChallengeMin();
			}

		} catch (Exception e) {

			e.printStackTrace();

			challenge = new ChallengeMin();

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

				if(sourceFileName.endsWith(".doc")
						|| sourceFileName.endsWith(".docx")
						|| sourceFileName.endsWith(".pdf")
						|| sourceFileName.endsWith(".txt")
						|| sourceFileName.endsWith(".zip"))
					challenge.setDocumentUrl(path);

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
		ChallengeMin updateChallege = new ChallengeMin();
		
		updateChallege.setId(challenge.getId());
		updateChallege.setTitle(challenge.getTitle());
		updateChallege.setOutput(challenge.getOutput());
		updateChallege.setDescription(challenge.getDescription());
		updateChallege.setReward(challenge.getReward());
		updateChallege.setSkills(skillsView);
		updateChallege.setCityId(challenge.getCity().getId());
		updateChallege.setDocumentUrl(challenge.getDocumentUrl());
		updateChallege.setUrl(challenge.getUrl());

		category = new ChallengeCategoryMin();
		category.setId(challenge.getCategory().getId());
		updateChallege.setCategory(category);

		response = client.updateChallenge(ClientResponse.class,
				javax.ws.rs.core.MediaType.APPLICATION_JSON,
				gson.toJson(updateChallege));

		client.close();

		System.out.println("updateChallege response = " + response);
		System.out.println("gson.toJson(updateChallege): " + gson.toJson(updateChallege));

		String challengeId = response.getEntity(String.class);
		System.out.println("challenge was updated into db, challengeId = "
				+ challengeId);

		if (challengeId.equalsIgnoreCase("true")) {
			FacesMessage msg = new FacesMessage(
					"Challenge was updated successfully!");
			FacesContext.getCurrentInstance().addMessage(null, msg);
			
			LiferayFacesContext portletFacesContext = LiferayFacesContext
					.getInstance();
			
			try {
				
				if (url.contains("web/antwerp") || url.contains("web/issy") ||
						url.contains("web/birmingham") || url.contains("web/liberec")){
					portletFacesContext.getExternalContext().redirect(
							realUrl + "/challenge-details?id=" + challenge.getId());
				}else {
					portletFacesContext.getExternalContext().redirect(
							redirectUrl + "/challenge-details?id=" + challenge.getId());
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
						realUrl + "/my-challenges");
			}else {
				portletFacesContext.getExternalContext().redirect(
						redirectUrl + "/my-challenges");
		}
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

	public void onCategorySelected(AjaxBehaviorEvent e) {

		System.out.println("onCategorySelected, selectedCat:" + challenge.getCategory().getId());
	

	}// end onCategorySelected()

	public void onCitySelected(AjaxBehaviorEvent e) {

		System.out.println("onCitySelected, selectedCity:" + challenge.getCity().getId());

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

	/*public Integer getSelectedCategory() {
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
	}*/

	public List<ChallengeCategoryMin> getChallengeCategories() {
		return challengeCategories;
	}

	public void setChallengeCategories(
			List<ChallengeCategoryMin> challengeCategories) {
		this.challengeCategories = challengeCategories;
	}

	public ChallengeCategoryMin getCategory() {
		return category;
	}

	public void setCategory(ChallengeCategoryMin category) {
		this.category = category;
	}

	/*public String getChallengeTitle() {
		return challengeTitle;
	}

	public void setChallengeTitle(String challengeTitle) {
		this.challengeTitle = challengeTitle;
	}

	public String getChallengeDescription() {
		return challengeDescription;
	}

	public void setChallengeDescription(String challengeDescription) {
		this.challengeDescription = challengeDescription;
	}

	public float getReward() {
		return reward;
	}

	public void setReward(float reward) {
		this.reward = reward;
	}*/

	public ChallengeMin getChallenge() {
		return challenge;
	}

	public void setChallenge(ChallengeMin challenge) {
		this.challenge = challenge;
	}

	public String getSkillsView() {
		skillsView = challenge.getSkills().replaceAll("dummya|dummyb", "");
		return skillsView;
	}

	public void setSkillsView(String skillsView) {
		this.skillsView = skillsView;
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
	public Boolean getRenderView() {
		return renderView;
	}

	public void setRenderView(Boolean renderView) {
		this.renderView = renderView;
	}

	/*public UserMin getOwner() {
		return owner;
	}

	public void setOwner(UserMin owner) {
		this.owner = owner;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public String getSkills() {
		return skills;
	}

	public void setSkills(String skills) {
		this.skills = skills;
	}*/

}// end class
