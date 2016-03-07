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

@ManagedBean(name = "challengeRegistrationBean")
@ViewScoped
public class ChallengeRegistrationBean implements Serializable {

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

	// private Integer category = 0;

	private List<? extends Categories> categories;

	private List<? extends Products> products;

	private boolean renderSearch = false;

	private boolean renderMain = true;

	private ChallengeMin challenge;

	private UserMin owner;

	private Integer selectedCategory;

	private ChallengeCategoryMin category;

	private Integer selectedCity;

	private String challengeTitle;

	private String challengeDescription;

	private String reward;

	private String output;

	private String skills;
		
	FacesContext context = FacesContext.getCurrentInstance();
	PortletRequest portletRequest = (PortletRequest) context.getExternalContext().getRequest();
	ThemeDisplay themeDisplay = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
	private String url;
	
	private String urlRedirect = "";
	private String realUrl = "";
	private String tempUrl = "";

	/* this is defined in the settings.properties file */
	/* http://localhost:9080/web/guest/ */
	private String redirectUrl;
	
	private String fileName;
	private String destination = "";
	private long fileSize;
	private FileEntry fileEntry;
	
	

	// ************************* Functions **************************

	public ChallengeRegistrationBean() {

		System.out.println("initializing service registration bean..");

		FacesContext facesContext = FacesContext.getCurrentInstance();

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

		} else {

			dataBean = (DataBean) facesContext.getExternalContext()
					.getApplicationMap().get("otnDataBean");

		}

		challenge = new ChallengeMin();
		cities = dataBean.getCities();
		challengeCategories = dataBean.getChallengeCategories();

		redirectUrl = authenticator.getSiteUrl();

		try {
			urlRedirect = PortalUtil.getLayoutURL(themeDisplay.getLayout(),themeDisplay);
			tempUrl = redirectUrl.replaceAll("/web/guest", urlRedirect);
			realUrl = tempUrl.replaceAll("/add-challenge", "");			
			
		} catch (PortalException e1) {
			e1.printStackTrace();
		} catch (SystemException e1) {
			e1.printStackTrace();
		}


	}// end constructor

	public void allChallenges() {
		LiferayFacesContext portletFacesContext = LiferayFacesContext
				.getInstance();
		try {
			
			if (urlRedirect.contains("web/antwerp") || urlRedirect.contains("web/issy") ||
					urlRedirect.contains("web/birmingham") || urlRedirect.contains("web/liberec")){
				portletFacesContext.getExternalContext().redirect(
						realUrl + "/challenges");
			}else {
				portletFacesContext.getExternalContext().redirect(
						redirectUrl + "/challenges");
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

		challenge.setTitle(challengeTitle);
		challenge.setOutput(output);
		challenge.setDescription(challengeDescription);
		challenge.setReward(reward);
		challenge.setSkills("dummya,dummyb,"+ skills);
		challenge.setCityId(selectedCity);
		challenge.setUrl(url);

		category = new ChallengeCategoryMin();
		category.setId(selectedCategory);
		challenge.setCategory(category);

		owner = new UserMin();
		owner.setLiferayId(authenticator.getUserID());
		owner.setName(authenticator.getUserName());
		owner.setEmail(authenticator.getUserEmail());

		challenge.setOwner(owner);

		System.out.println("liferayId = " + authenticator.getUserID());

		response = client.addChallenge(ClientResponse.class,
				javax.ws.rs.core.MediaType.APPLICATION_JSON,
				gson.toJson(challenge));

		client.close();

		System.out.println("service response = " + response);
		System.out.println("gson.toJson(service): " + gson.toJson(challenge));

		String challengeId = response.getEntity(String.class);
		System.out.println("challenge was added into db, challengeId = "
				+ challengeId);

		if (challengeId != null) {			

			LiferayFacesContext portletFacesContext = LiferayFacesContext
					.getInstance();
			FacesMessage msg = new FacesMessage(
					"Registration was completed successfully!");
			FacesContext.getCurrentInstance().addMessage(null, msg);
			FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
			
			try {
				
				if (urlRedirect.contains("web/antwerp") || urlRedirect.contains("web/issy") ||
						urlRedirect.contains("web/birmingham") || urlRedirect.contains("web/liberec")){
					portletFacesContext.getExternalContext().redirect(
							realUrl + "/challenge-details?id="
									+ challengeId + "&newchallenge=true");
				}else {
					portletFacesContext.getExternalContext().redirect(
							redirectUrl + "/challenge-details?id="
									+ challengeId + "&newchallenge=true");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else {

		}

	}

	
	public void onCategorySelected(AjaxBehaviorEvent e) {

		System.out.println("selectedCat:" + selectedCategory);

	}// end onCategorySelected()

	public void onCitySelected(AjaxBehaviorEvent e) {

		System.out.println("selectedCity:" + selectedCity);

	}// end onCategorySelected()

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
	 * Returns all service challenge categories from db
	 */
	private void fetchChallengeCategories() {

		// ************************ Variables *************************

		OtnClient client = new OtnClient();

		ClientResponse response;

		Gson gson = new Gson();

		String jsonResp;

		// ************************ Action ****************************

		try {

			response = client.getChallengeCategories(ClientResponse.class,
					javax.ws.rs.core.MediaType.APPLICATION_JSON);

			client.close();

			jsonResp = response.getEntity(String.class);

			System.out.println("jsonResp:" + jsonResp);

			challengeCategories = gson.fromJson(jsonResp,
					new TypeToken<List<ChallengeCategoryMin>>() {
					}.getType());

		} catch (Exception e) {

			e.printStackTrace();

			challengeCategories = new ArrayList<ChallengeCategoryMin>();
		}

	}// end fetchServiceCategories()
	
	public void upload(FileUploadEvent event) throws IOException {

		System.out.println("upload was called...");

		String fileType = (String) event.getComponent().getAttributes()
				.get("fileType");

		System.out.println("fileType = " + fileType);

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

		copyFile(fileName, event.getFile().getInputstream(), fileType);
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

	public String getChallengeTitle() {
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

	public String getReward() {
		return reward;
	}

	public void setReward(String reward) {
		this.reward = reward;
	}

	public ChallengeMin getChallenge() {
		return challenge;
	}

	public void setChallenge(ChallengeMin challenge) {
		this.challenge = challenge;
	}

	public UserMin getOwner() {
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
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}// end class
