package otn.beans;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import otn.clients.OtnClient;
import otn.model.catalog.WebServiceCategoryMin;
import otn.model.marketplace.ChallengeCategoryMin;
import otn.model.marketplace.CityMin;
import otn.model.marketplace.ServiceCategoryMin;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.ClientResponse;

@ManagedBean(name = "otnDataBean")
@ApplicationScoped
public class DataBean implements Serializable {

	/** The available service categories in the db. */
	private List<ServiceCategoryMin> serviceCategories;

	/** The available service categories in the db. */
	private List<ChallengeCategoryMin> challengeCategories;

	/** The list of all the available cities in the system. */
	private List<CityMin> cities;

	/** The portal page of the service details portlet. */
	private String serviceDetailsPage;

	/**
	 * The id of the folder we have created on the liferay's document library
	 * and where users' services images are uploaded
	 */
	private String folderId;

	/**
	 * The id of the default service thumbnail icon, saved in the liferay's
	 * document library
	 */
	private String fileEntryId;

	/**
	 * The id of the default service Screenshot, saved in the liferay's document
	 * library
	 */
	private String screenshotFileEntryId;
	
	public DataBean() {

		// file with necessary parameters
		Properties p = new Properties();
		InputStream in = null;

		// ************************ Action *************************

		try {

			in = this.getClass().getResourceAsStream("/settings.properties");
			p.load(in);

			serviceDetailsPage = p
					.getProperty("marketplace.serviceDetailsPage");
			folderId = p.getProperty("folderId");
			fileEntryId = p.getProperty("fileEntryId");
			screenshotFileEntryId = p.getProperty("screenshotFileEntryId");

			in.close();

		} catch (IOException e) {

			e.printStackTrace();

		}

		fetchServiceCategories();

		fetchChallengeCategories();

		fetchCities();

		// fetchWebServicesCategories();

	}// end constructor

	private void fetchServiceCategories() {

		// ************************ Variables *************************

		OtnClient client = new OtnClient();

		ClientResponse response;

		Gson gson = new Gson();

		// ************************ Action ****************************

		try {

			response = client.getServiceCategories(ClientResponse.class,
					javax.ws.rs.core.MediaType.APPLICATION_JSON);

			client.close();

			serviceCategories = gson.fromJson(response.getEntity(String.class),
					new TypeToken<List<ServiceCategoryMin>>() {
					}.getType());

		} catch (Exception e) {

			e.printStackTrace();

			serviceCategories = new ArrayList<ServiceCategoryMin>();

		}

		// System.out.println("serviceCat:"+serviceCategories);

	}// end getServiceCategories()

	private void fetchChallengeCategories() {

		// ************************ Variables *************************

		OtnClient client = new OtnClient();

		ClientResponse response;

		Gson gson = new Gson();

		// ************************ Action ****************************

		try {

			response = client.getChallengeCategories(ClientResponse.class,
					javax.ws.rs.core.MediaType.APPLICATION_JSON);

			client.close();

			challengeCategories = gson.fromJson(
					response.getEntity(String.class),
					new TypeToken<List<ChallengeCategoryMin>>() {
					}.getType());

		} catch (Exception e) {

			e.printStackTrace();

			challengeCategories = new ArrayList<ChallengeCategoryMin>();

		}

	}// end getChallengeCategories()

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

			// System.out.println("jsonResp:" + jsonResp);

			cities = gson.fromJson(jsonResp, new TypeToken<List<CityMin>>() {
			}.getType());

		} catch (Exception e) {

			e.printStackTrace();

			cities = new ArrayList<CityMin>();

		}

	}// end fetchCities()

	/*
	 * private void fetchWebServicesCategories(){
	 * 
	 * //************************ Variables *************************
	 * 
	 * OtnClient client = new OtnClient();
	 * 
	 * ClientResponse response;
	 * 
	 * Gson gson = new Gson();
	 * 
	 * //************************ Action ****************************
	 * 
	 * try{
	 * 
	 * response =
	 * client.getChallengeCategories(ClientResponse.class,javax.ws.rs.
	 * core.MediaType.APPLICATION_JSON);
	 * 
	 * client.close();
	 * 
	 * webServiceCategories = gson.fromJson(response.getEntity(String.class),
	 * new TypeToken<List<WebServiceCategoryMin>>(){}.getType());
	 * 
	 * }catch(Exception e){
	 * 
	 * e.printStackTrace();
	 * 
	 * webServiceCategories = new ArrayList<WebServiceCategoryMin>();
	 * 
	 * }
	 * 
	 * }//end getWebServicesCategories()
	 */

	public List<ServiceCategoryMin> getServiceCategories() {
		return serviceCategories;
	}

	public List<ChallengeCategoryMin> getChallengeCategories() {
		return challengeCategories;
	}

	public List<CityMin> getCities() {
		return cities;
	}

	public String getServiceDetailsPage() {
		return serviceDetailsPage;
	}

	public String getFolderId() {
		return folderId;
	}

	public String getFileEntryId() {
		return fileEntryId;
	}

	public void setFileEntryId(String fileEntryId) {
		this.fileEntryId = fileEntryId;
	}

	public String getScreenshotFileEntryId() {
		return screenshotFileEntryId;
	}

	public void setScreenshotFileEntryId(String screenshotFileEntryId) {
		this.screenshotFileEntryId = screenshotFileEntryId;
	}

}// end class
