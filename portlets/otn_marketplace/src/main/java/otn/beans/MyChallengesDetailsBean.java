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

import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.LongSerializationPolicy;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.ClientResponse;
import otn.authenticator.LiferayAuthenticator;
import otn.clients.OtnClient;
import otn.interfaces.IAuthenticator;

import javax.mail.internet.InternetAddress;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import com.liferay.faces.portal.context.LiferayFacesContext;
import com.liferay.faces.util.portal.WebKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionThreadLocal;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;

import otn.model.Categories;
import otn.model.Products;
import otn.model.marketplace.*;

@ManagedBean(name = "myChallengesDetailsBean")
@ViewScoped
public class MyChallengesDetailsBean implements Serializable {

	// ************************* Variables **************************

	// /** Application scoped bean that holds global information */
	// @ManagedProperty(value = "#{otnDataBean}")
	// private DataBean dataBean;

	private IAuthenticator authenticator;

	/** The available challenges in the db. */
	private List<ChallengeMin> challengeList;

	private ChallengeMin challenge;
	
	ArrayList<String> skillsViewList;
	
	private String skillsView;

	private Long challengeId;
	
	private Boolean isnew = false;
	
	private Boolean renderFile = false ;
	
	private Boolean renderUrl = false ;
	
	private Boolean renderReward = false ;

	private String imageFileUrl;
	
	FacesContext context = FacesContext.getCurrentInstance();
	PortletRequest portletRequest = (PortletRequest) context.getExternalContext().getRequest();
	ThemeDisplay themeDisplay = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
	
	private String url = "";
	private String realUrl = "";
	private String tempUrl = "";
	
	// http://localhost:9080/web/guest/my-challenge-details?challengeId=6
	private String redirectUrl;

	/** defines if edit/delete buttons are displayed or not */
	private Boolean renderBtn = false;

	/** defines if respond button is displayed or not */
	private Boolean renderRespondBtn = false;

	/** the liferay user id of the signed in user */
	private Long userLiferayId;
	
	/* email body */
	private String text = "";

	// ************************* Functions **************************

	public MyChallengesDetailsBean() {

		System.out.println("initializing mychallenges details bean..");

		HttpServletRequest request = PortalUtil
				.getHttpServletRequest((PortletRequest) FacesContext
						.getCurrentInstance().getExternalContext().getRequest());

		HttpServletRequest oriRequest = PortalUtil
				.getOriginalServletRequest(request);

		String param = oriRequest.getParameter("id");

		System.out.println("param = " + param);

		if (param == null) {
			challengeId = (long) 0;
		} else {
			challengeId = Long.parseLong(param);
		}
		
		String newchallenge = oriRequest.getParameter("newchallenge");
		
		if (newchallenge!=null && newchallenge.equals("true")) {
			isnew = true;
		}

		fetchChallengeById(challengeId);

		// initialize the authenticator
		authenticator = new LiferayAuthenticator();

		redirectUrl = authenticator.getSiteUrl();

		userLiferayId = authenticator.getUserID();
        System.out.println("userliferayID= " + userLiferayId);
        System.out.println("" + challenge);
        System.out.println("challenge.getOwner().getLiferayId()= " + challenge.getOwner().getLiferayId());
		/**
		 * Check if the signed in user is the owner of the challenge. if yes
		 * edit/delete buttons are displayed to him
		 * */
		
		PermissionChecker permissionChecker = PermissionThreadLocal.getPermissionChecker();		
		if (userLiferayId == challenge.getOwner().getLiferayId() || permissionChecker.isOmniadmin()) {

			renderBtn = true;
		}

		/**
		 * Check if the signed in user is the owner of the challenge. if not
		 * respond button is displayed to him
		 * */
		if (!userLiferayId.equals(challenge.getOwner().getLiferayId())) {
			renderRespondBtn = true;
		}
		
		try {
			url = PortalUtil.getLayoutURL(themeDisplay.getLayout(),themeDisplay);
			tempUrl = redirectUrl.replaceAll("/web/guest", url);
			realUrl = tempUrl.replaceAll("/challenge-details", "");
			
		} catch (PortalException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SystemException e1) {
			// TODO Auto-generated catch block
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
				
				if (challenge.getDocumentUrl() != null && !challenge.getDocumentUrl().isEmpty() ) {
					renderFile = true;

					if (challenge.getDocumentUrl().contains(".pdf"))
						imageFileUrl = "/otn_marketplace/images/pdf.png";

					else if (challenge.getDocumentUrl().contains(".doc") || 
							challenge.getDocumentUrl().contains(".docx"))
						imageFileUrl = "/otn_marketplace/images/doc.png";
					else if (challenge.getDocumentUrl().contains(".txt"))
						imageFileUrl = "/otn_marketplace/images/txt.png";
					else if (challenge.getDocumentUrl().contains(".zip"))
						imageFileUrl = "/otn_marketplace/images/zip.jpg";
				}
				
				if (challenge.getUrl()!= null && !challenge.getUrl().isEmpty() ) 
					renderUrl = true;
				if (challenge.getReward() != null && !challenge.getReward().isEmpty()) 
					renderReward = true;

			} else {
				challenge = new ChallengeMin();
			}

		} catch (Exception e) {

			e.printStackTrace();

			challenge = new ChallengeMin();

		}// end catch
	}

	public void onEdit() {

		LiferayFacesContext portletFacesContext = LiferayFacesContext
				.getInstance();

		try {
			
			if (url.contains("web/antwerp") || url.contains("web/issy") ||
					url.contains("web/birmingham") || url.contains("web/liberec")){
				portletFacesContext.getExternalContext().redirect(
						realUrl + "/my-challenges?id="
							+ challenge.getId());
			}else {
				portletFacesContext.getExternalContext().redirect(
						redirectUrl + "/my-challenges?id="
							+ challenge.getId());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/** Redirects a user to all challenges */
	public void goToChallenges() {

		LiferayFacesContext portletFacesContext = LiferayFacesContext
				.getInstance();

try {
			
			if (url.contains("web/antwerp") || url.contains("web/issy") ||
					url.contains("web/birmingham") || url.contains("web/liberec")){
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

	/** Redirects a user to his challenges */
	public void goToMyChallenges() {

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void onDelete() {

		OtnClient client = new OtnClient();

		ClientResponse response;

		Gson gson = new Gson();

		String jsonResp;

		// ************************ Action ****************************

		try {

			response = client.deleteChallenge(ClientResponse.class,
					javax.ws.rs.core.MediaType.APPLICATION_JSON, challenge);

			client.close();

			jsonResp = response.getEntity(String.class);

			System.out.println("delete jsonResp:" + jsonResp);

		} catch (Exception e) {
			e.printStackTrace();
		}

		goToMyChallenges();

	}

	public void sendEmail() {

		System.out.println("sendEmail was called");

		com.liferay.portal.kernel.mail.MailMessage message = new com.liferay.portal.kernel.mail.MailMessage();
		InternetAddress to = new InternetAddress();
		to.setAddress(challenge.getOwner().getEmail());

		InternetAddress from = new InternetAddress();
		from.setAddress("opentransportnet.eu@gmail.com");

		message.setFrom(from);
		message.setTo(to);
		message.setSubject("OTN");
		
		String bodyMsg = "You have received a message from " + authenticator.getUserName() + " (" + authenticator.getUserEmail() + ") \n" + getText();
		
		
		message.setBody(bodyMsg);
		
		System.out.println("email message: " + bodyMsg);
	
		com.liferay.mail.service.MailServiceUtil.sendEmail(message);
		setText("");
	}

	
	
	// ********************* Getters-Setters ***************************

	public ChallengeMin getChallenge() {
		return challenge;
	}

	public void setChallenge(ChallengeMin challenge) {
		this.challenge = challenge;
	}

	public Boolean getRenderBtn() {
		return renderBtn;
	}

	public void setRenderBtn(Boolean renderBtn) {
		this.renderBtn = renderBtn;
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

	public boolean getIsnew() {
		return isnew;
	}
	
	public void setIsnew(Boolean isnew) {
		this.isnew = isnew;
	}
	
	public String getSkillsView() {
		skillsView = challenge.getSkills().replaceAll("dummya,|dummyb,", "");
		return skillsView;
	}

	public void setSkillsView(String skillsView) {
		this.skillsView = skillsView;
	}

	public boolean isRenderFile() {
		return renderFile;
	}

	public void setRenderFile(boolean renderFile) {
		this.renderFile = renderFile;
	}
	
	public String getImageFileUrl() {
		return imageFileUrl;
	}

	public void setImageFileUrl(String imageFileUrl) {
		this.imageFileUrl = imageFileUrl;
	}
	
	public Boolean getRenderUrl() {
		return renderUrl;
	}

	public void setRenderUrl(Boolean renderUrl) {
		this.renderUrl = renderUrl;
	}

	public Boolean getRenderReward() {
		return renderReward;
	}

	public void setRenderReward(Boolean renderReward) {
		this.renderReward = renderReward;
	}
	
}// end class
