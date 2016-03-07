package otn.beans;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.event.SelectEvent;

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
import otn.model.marketplace.*;

@ManagedBean(name = "myChallengesBean")
@ViewScoped
public class MyChallengesBean implements Serializable {

	// ************************* Variables **************************

	/** Application scoped bean that holds global information */
	@ManagedProperty(value = "#{otnDataBean}")
	private DataBean dataBean;

	/***/
	private IAuthenticator authenticator;

	/** The available challenges in the db. */
	private List<ChallengeMin> challengeList;

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
	private float reward;
	private String output;
	private String skills;
	private Long userLiferayId;

	private ChallengeMin selectedChallenge;
	List<String> skillList;

	/* http://localhost:9080/web/guest/ */
	private String redirectUrl;
	
	FacesContext context = FacesContext.getCurrentInstance();
	PortletRequest portletRequest = (PortletRequest) context.getExternalContext().getRequest();
	ThemeDisplay themeDisplay = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
	
	private String url = "";
	private String realUrl = "";
	private String tempUrl = "";


	// ************************* Functions **************************

	public MyChallengesBean() {

		System.out.println("initializing mychallenges bean..");

		FacesContext facesContext = FacesContext.getCurrentInstance();

		// initialize the authenticator
		authenticator = new LiferayAuthenticator();

		userLiferayId = authenticator.getUserID();

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
		selectedChallenge = new ChallengeMin();
		cities = dataBean.getCities();
		challengeCategories = dataBean.getChallengeCategories();

		// fetchChallengeCategories();
		// fetchCities();
		fetchChallengesByOwnerId(userLiferayId);

		redirectUrl = authenticator.getSiteUrl();
		
		HttpServletRequest request = PortalUtil
				.getHttpServletRequest((PortletRequest) FacesContext
						.getCurrentInstance().getExternalContext().getRequest());

		HttpServletRequest oriRequest = PortalUtil
				.getOriginalServletRequest(request);

		String param = oriRequest.getParameter("id");

		System.out.println("param = " + param);

		if (param != null) {
			selectedChallenge.setId(Long.parseLong(param));
		}
		
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

	/* fetches all my challenges from database */
	private void fetchChallengesByOwnerId(Long userLiferayId) {

		// ************************ Variables *************************

		OtnClient client = new OtnClient();

		ClientResponse response;

		ChallengesResponse challengeResp;

		// // Creates the json object which will manage the information received
		// GsonBuilder builder = new GsonBuilder();
		// // Register an adapter to manage the date types as long values
		// builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>()
		// {
		// public Date deserialize(JsonElement json, Type typeOfT,
		// JsonDeserializationContext context) throws JsonParseException
		// { return new Date(json.getAsJsonPrimitive().getAsLong()); } });
		// Gson gson = builder.create();

		// Gson gson = new
		// GsonBuilder().setDateFormat("dd-MM-yyyy").setLongSerializationPolicy(LongSerializationPolicy.STRING).create();

		GsonBuilder builder = new GsonBuilder();

		Gson gson;

		String jsonResp;

		// ************************ Action ****************************

		/*System.out
				.println(".........getChallengesByOwnerId was called with userLiferayId = "
						+ userLiferayId);*/

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

			response = client.getChallengesByOwnerId(ClientResponse.class,
					javax.ws.rs.core.MediaType.APPLICATION_JSON, userLiferayId);

			client.close();

			jsonResp = response.getEntity(String.class);

			//System.out.println("jsonResp:" + jsonResp);

			challengeResp = gson.fromJson(jsonResp,
					new TypeToken<ChallengesResponse>() {
					}.getType());

			if (challengeResp != null && challengeResp.getErrorMsg().isEmpty()) {

				challengeList = challengeResp.getChallenges();

				//System.out.println("challenges..");

				for (ChallengeMin challenge : challengeList) {

					/*System.out.println("challenge.getSkills() = "
							+ challenge.getSkills());*/

					ArrayList<String> skillList = new ArrayList(
							Arrays.asList(challenge.getSkills().split(
									"\\s*,\\s*")));
					challenge.setSkillList(skillList);

					//System.out.println(skillList.size());

					SimpleDateFormat dateformatJava = new SimpleDateFormat(
							"dd-MM-yyyy");
					challenge.setDateString(dateformatJava.format(challenge
							.getDate()));
				}

			} else {

				challengeList = new ArrayList<ChallengeMin>();
			}

		} catch (Exception e) {

			e.printStackTrace();

			challengeList = new ArrayList<ChallengeMin>();

		}// end catch
	}

	public void onCategorySelected(AjaxBehaviorEvent e) {

		//System.out.println("selectedCat:" + selectedCategory);

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

			//System.out.println("jsonResp:" + jsonResp);

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

			//System.out.println("jsonResp:" + jsonResp);

			challengeCategories = gson.fromJson(jsonResp,
					new TypeToken<List<ChallengeCategoryMin>>() {
					}.getType());

		} catch (Exception e) {

			e.printStackTrace();

			challengeCategories = new ArrayList<ChallengeCategoryMin>();
		}

	}// end fetchServiceCategories()

	/**
	 * On row selection, user is being redirect to the selected challenge
	 * details
	 */
	public void onRowSelect(SelectEvent event) {
		/*System.out
				.println("selectedChalenge id = " + selectedChallenge.getId());
*/
		LiferayFacesContext portletFacesContext = LiferayFacesContext
				.getInstance();
		
		try {
			
			if (url.contains("web/antwerp") || url.contains("web/issy") ||
					url.contains("web/birmingham") || url.contains("web/liberec")){
				portletFacesContext.getExternalContext().redirect(
						realUrl + "/challenge-details?id="
								+ selectedChallenge.getId());
			}else {
				portletFacesContext.getExternalContext().redirect(
						redirectUrl + "/challenge-details?id="
								+ selectedChallenge.getId());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Redirects user to challenge registration form in order user to update 
	 * challenge details in database
	 */
	public void updateChallenge(){
		
		/*System.out
		.println("updateChallenge was called..selectedChalenge id = ");
		*/
		LiferayFacesContext portletFacesContext = LiferayFacesContext
				.getInstance();
		
			try {
			
			if (url.contains("web/antwerp") || url.contains("web/issy") ||
					url.contains("web/birmingham") || url.contains("web/liberec")){
				portletFacesContext.getExternalContext().redirect(
						realUrl + "/my-challenges?id="
								+ selectedChallenge.getId());
			}else {
				portletFacesContext.getExternalContext().redirect(
						redirectUrl + "/my-challenges?id="
								+ selectedChallenge.getId());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * Deletes challenge from database,
	 * in reality deactivates challenge by setting isDeleted equals 1
	 * 
	 */
	public void deleteChallenge(){
		
		OtnClient client = new OtnClient();

		ClientResponse response;

		Gson gson = new Gson();

		String jsonResp;

		// ************************ Action ****************************

		try {

			response = client.deleteChallenge(ClientResponse.class,
					javax.ws.rs.core.MediaType.APPLICATION_JSON, selectedChallenge);

			client.close();

			jsonResp = response.getEntity(String.class);

			//System.out.println("delete jsonResp:" + jsonResp);
			
			if(jsonResp.equalsIgnoreCase("true")){
				//System.out.println("challengeList.size() = " + challengeList.size());
				challengeList.remove(selectedChallenge);
				//System.out.println("challengeList.size after remove = " + challengeList.size());
			}

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
			} catch (IOException e) {
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

	public float getReward() {
		return reward;
	}

	public void setReward(float reward) {
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

	public Long getUserLiferayId() {
		return userLiferayId;
	}

	public void setUserLiferayId(Long userLiferayId) {
		this.userLiferayId = userLiferayId;
	}

	public List<ChallengeMin> getChallengeList() {
		return challengeList;
	}

	public void setChallengeList(List<ChallengeMin> challengeList) {
		this.challengeList = challengeList;
	}

	public ChallengeMin getSelectedChallenge() {
		return selectedChallenge;
	}

	public void setSelectedChallenge(ChallengeMin selectedChallenge) {
		this.selectedChallenge = selectedChallenge;
	}

}// end class
