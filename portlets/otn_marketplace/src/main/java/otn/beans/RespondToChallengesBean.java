package otn.beans;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
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
import javax.mail.internet.InternetAddress;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
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
import otn.model.ChallengesHolder;
import otn.model.Products;
import otn.model.ServicesHolder;
import otn.model.marketplace.*;

@ManagedBean(name = "respondToChallengesBean")
@ViewScoped
public class RespondToChallengesBean implements Serializable {

	// ************************* Variables **************************

	/** Application scoped bean that holds global information */
	@ManagedProperty(value = "#{otnDataBean}")
	private DataBean dataBean;

	/***/
	private IAuthenticator authenticator;

	/** The available challenges in the db. */
	private List<ChallengeMin> challengeList;
	
	/**
	 * List holding all the registered services grouped by category. This list
	 * is used for the default view of the marketplace.
	 */
	private List<ChallengesHolder> challengesList;

	public List<ChallengesHolder> getChallengesList() {
		return challengesList;
	}

	public void setChallengesList(List<ChallengesHolder> challengesList) {
		this.challengesList = challengesList;
	}

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

	/* this is defined in the settings.properties file */
	/* http://localhost:9080/web/guest/ */
	private String redirectUrl;

	/** defines if respond button is displayed or not */
	private Boolean renderRespondBtn = false;

	/* email body */
	private String text = "";
	
	FacesContext context = FacesContext.getCurrentInstance();

	PortletRequest portletRequest = (PortletRequest) context.getExternalContext().getRequest();
	ThemeDisplay themeDisplay = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
	private String url = "";
	private String realUrl = "";
	private String tempUrl = "";
	

	// ************************* Functions **************************

	public RespondToChallengesBean() {

		System.out.println("initializing mychallenges bean..");

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

		challenge = new ChallengeMin();
		selectedChallenge = new ChallengeMin();
		cities = dataBean.getCities();
		challengeCategories = dataBean.getChallengeCategories();

		redirectUrl = authenticator.getSiteUrl();
		
		try {
			url = PortalUtil.getLayoutURL(themeDisplay.getLayout(),themeDisplay);
			tempUrl = redirectUrl.replaceAll("/web/guest", url);
			realUrl = tempUrl.replaceAll("/challenges", "");
			
		} catch (PortalException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SystemException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		fetchChallenges();

	}// end constructor

	/* fetches all my challenges from database */
	private void fetchChallenges() {

		// ************************ Variables *************************

		OtnClient client = new OtnClient();

		ClientResponse response = null;

		ChallengesResponse challengeResp;

		GsonBuilder builder = new GsonBuilder();

		Gson gson;

		String jsonResp;

		// ************************ Action ****************************

		System.out.println(".........getChallenges was called");
		
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
			String city = "";
			
			//get challenges according to which hub we are into
			
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
			
				response = client.getChallenges(ClientResponse.class,
						javax.ws.rs.core.MediaType.APPLICATION_JSON, 0, "", 0, city);
			
			client.close();

			jsonResp = response.getEntity(String.class);

			System.out.println("jsonResp:" + jsonResp);

			challengeResp = gson.fromJson(jsonResp,
					new TypeToken<ChallengesResponse>() {
					}.getType());

			if (challengeResp != null && challengeResp.getErrorMsg().isEmpty()) {

				challengeList = challengeResp.getChallenges();

				System.out.println("challenges..");

				for (ChallengeMin challenge : challengeList) {
					
					/**
					 * Check if the signed in user is the owner of the challenge. if not
					 * respond button is displayed to him
					 * */
										
					if (userLiferayId != challenge.getOwner().getLiferayId()) {
						renderRespondBtn = true;
					}

					System.out.println("challenge.getSkills() = "
							+ challenge.getSkills());

					if (challenge.getSkills() != null) {
						ArrayList<String> skillList = new ArrayList(
								Arrays.asList(challenge.getSkills().split(
										"\\s*,\\s*")));
						challenge.setSkillList(skillList);

						System.out.println(skillList.size());
					}

					SimpleDateFormat dateformatJava = new SimpleDateFormat(
							"dd-MM-yyyy");
					if (challenge.getDate() != null) {
						challenge.setDateString(dateformatJava.format(challenge
								.getDate()));
					}
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

		System.out.println("selectedCat:" + selectedCategory);

	}// end onCategorySelected()

	/**
	 * On row selection, user is being redirect to the selected challenge
	 * details
	 */
	public String onRowSelect() {
		System.out
				.println("selectedChalenge id = " + selectedChallenge.getId());

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "success";
	}

	public void sendEmail() {

		System.out.println("sendEmail was called");

		com.liferay.portal.kernel.mail.MailMessage message = new com.liferay.portal.kernel.mail.MailMessage();
		InternetAddress to = new InternetAddress();
		to.setAddress(selectedChallenge.getOwner().getEmail());

		InternetAddress from = new InternetAddress();
		from.setAddress("opentransportnet.eu@gmail.com");

		message.setFrom(from);
		message.setTo(to);
		message.setSubject("OTN");

		String bodyMsg = "You have received a message from "
				+ authenticator.getUserName() + " ("
				+ authenticator.getUserEmail() + ") \n" + getText();

		message.setBody(bodyMsg);

		System.out.println("email message: " + bodyMsg);

		// "" +
		// "A new user was registered with user id "
		// + userLiferayId + " and screen name " + userScreenName
		// + ". The " + userScreenName + " wants to get the role "
		// + getRole());
		com.liferay.mail.service.MailServiceUtil.sendEmail(message);
		setText("");
	}

	// ******************** Getters - Setters ********************************

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

	public Boolean getRenderRespondBtn() {
		return renderRespondBtn;
	}

	public void setRenderRespondBtn(Boolean renderRespondBtn) {
		this.renderRespondBtn = renderRespondBtn;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}// end class
