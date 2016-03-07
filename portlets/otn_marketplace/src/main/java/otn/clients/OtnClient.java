package otn.clients;

import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class OtnClient implements Serializable {

	private WebResource webResource;
	private Client client;
	private static String BASE_URI;

	public OtnClient() {

		// file with necessary parameters
		Properties p = new Properties();

		// initialize properties file
		try {
			p.load(this.getClass().getResourceAsStream("/settings.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		BASE_URI = p.getProperty("otnWS.baseUrl");

		ClientConfig config = new DefaultClientConfig();
		client = Client.create(config);

		webResource = client.resource(BASE_URI);// .path("marketplace");

	}// end EcimClient()

	public void close() {
		client.destroy();
	}

	public <T> T getWebServiceCategories(Class<T> responseType, String mediaType)
			throws UniformInterfaceException {

		WebResource resource = webResource;

		return resource.path("catalog").path("getWebServiceCategories")
				.type(mediaType).get(responseType);

	}// end getWebServiceCategories()

	public <T> T getServiceCategories(Class<T> responseType, String mediaType)
			throws UniformInterfaceException {

		WebResource resource = webResource;

		return resource.path("marketplace").path("getServiceCategories")
				.type(mediaType).get(responseType);

	}// end getServiceCategories()

	public <T> T getCities(Class<T> responseType, String mediaType)
			throws UniformInterfaceException {

		WebResource resource = webResource;

		return resource.path("marketplace").path("getCities").type(mediaType)
				.get(responseType);

	}// end getCities()

	public <T> T getChallengeCategories(Class<T> responseType, String mediaType)
			throws UniformInterfaceException {

		WebResource resource = webResource;

		return resource.path("marketplace").path("getChallengeCategories")
				.type(mediaType).get(responseType);

	}// end getChallengeCategories()

	public <T> T getWebServices(Class<T> responseType, String mediaType,
			int limit, String searchString, int categoryID)
			throws UniformInterfaceException {

		WebResource resource = webResource;

		resource = resource.queryParam("limit", "" + limit);
		resource = resource.queryParam("searchString", searchString);
		resource = resource.queryParam("categoryID", "" + categoryID);

		return resource.path("catalog").path("getWebServices").type(mediaType)
				.get(responseType);

	}// end getWebServices()

	public <T> T getServices(Class<T> responseType, String mediaType,
			int limit, String searchString, int categoryID, int free, String city)
			throws UniformInterfaceException {

		WebResource resource = webResource;

		resource = resource.queryParam("limit", "" + limit);
		resource = resource.queryParam("searchString", searchString);
		resource = resource.queryParam("categoryID", "" + categoryID);
		resource = resource.queryParam("free", "" + free);
		resource = resource.queryParam("city", "" + city);

		return resource.path("marketplace").path("getServices").type(mediaType)
				.get(responseType);

	}// end getServices()
	
	public <T> T getApps(Class<T> responseType, String mediaType, String city)
			throws UniformInterfaceException {

		WebResource resource = webResource;

		resource = resource.queryParam("city", "" + city);
		
		return resource.path("marketplace").path("getApps").type(mediaType)
				.get(responseType);

	}// end getApps()

	public <T> T getChallenges(Class<T> responseType, String mediaType,
			int limit, String searchString, int categoryID, String city)
			throws UniformInterfaceException {

		WebResource resource = webResource;

		resource = resource.queryParam("limit", "" + limit);
		resource = resource.queryParam("searchString", searchString);
		resource = resource.queryParam("categoryID", "" + categoryID);
		resource = resource.queryParam("city", "" + city);

		return resource.path("marketplace").path("getChallenges")
				.type(mediaType).get(responseType);

	}// end getChallenges()

	public <T> T addChallenge(Class<T> responseType, String mediaType,
			Object requestEntity) throws UniformInterfaceException {

		WebResource resource = webResource;
		return resource.path("marketplace").path("addChallenge")
				.type(mediaType).post(responseType, requestEntity);
	}// end addChallenge()

	public <T> T addService(Class<T> responseType, String mediaType,
			Object requestEntity) throws UniformInterfaceException {

		WebResource resource = webResource;
		return resource.path("marketplace").path("addService").type(mediaType)
				.post(responseType, requestEntity);
	}// end addService()
	
	public <T> T addApp(Class<T> responseType, String mediaType,
			Object requestEntity) throws UniformInterfaceException {

		WebResource resource = webResource;
		return resource.path("marketplace").path("addApp").type(mediaType)
				.post(responseType, requestEntity);
	}// end addApp()
	
	public <T> T addServiceScreenshot(Class<T> responseType, String mediaType,
			Object requestEntity) throws UniformInterfaceException {

		WebResource resource = webResource;
		return resource.path("marketplace").path("addServiceScreenshot").type(mediaType)
				.post(responseType, requestEntity);
	}// end addServiceScreenshot()

	public <T> T updateService(Class<T> responseType, String mediaType,
			Object requestEntity) throws UniformInterfaceException {

		WebResource resource = webResource;
		return resource.path("marketplace").path("updateService")
				.type(mediaType).put(responseType, requestEntity);
	}// end updateService()
	
	public <T> T updateApp(Class<T> responseType, String mediaType,
			Object requestEntity) throws UniformInterfaceException {

		WebResource resource = webResource;
		return resource.path("marketplace").path("updateApp")
				.type(mediaType).put(responseType, requestEntity);
	}// end updateApp()

	public <T> T deleteService(Class<T> responseType, String mediaType,
			Object requestEntity) throws UniformInterfaceException {

		WebResource resource = webResource;
		return resource.path("marketplace").path("deleteService")
				.type(mediaType).put(responseType, requestEntity);
	}// end deleteService()

	public <T> T getChallengeById(Class<T> responseType, String mediaType,
			Long id) throws UniformInterfaceException {

		WebResource resource = webResource;
		resource = resource.queryParam("id", "" + id);

		return resource.path("marketplace").path("getChallengesById")
				.type(mediaType).get(responseType);

	}// end getChallengesById
	
	public <T> T getAppById(Class<T> responseType, String mediaType,
			Long id) throws UniformInterfaceException {

		WebResource resource = webResource;
		resource = resource.queryParam("id", "" + id);

		return resource.path("marketplace").path("getAppById")
				.type(mediaType).get(responseType);

	}// end getChallengesById

	public <T> T getChallengesByOwnerId(Class<T> responseType,
			String mediaType, Long id) throws UniformInterfaceException {

		WebResource resource = webResource;
		resource = resource.queryParam("id", "" + id);

		return resource.path("marketplace").path("getChallengesByOwnerId")
				.type(mediaType).get(responseType);

	}// end getChallengesByOwnerId
	
	public <T> T updateChallenge(Class<T> responseType, String mediaType,
			Object requestEntity) throws UniformInterfaceException {

		WebResource resource = webResource;
		return resource.path("marketplace").path("updateChallenge")
				.type(mediaType).put(responseType, requestEntity);
	}// end updateChallenge()

	public <T> T deleteChallenge(Class<T> responseType, String mediaType,
			Object requestEntity) throws UniformInterfaceException {

		WebResource resource = webResource;
		return resource.path("marketplace").path("deleteChallenge")
				.type(mediaType).put(responseType, requestEntity);
	}// end deleteChallenge()
	
	public <T> T deleteApp(Class<T> responseType, String mediaType,
			Object requestEntity) throws UniformInterfaceException {

		WebResource resource = webResource;
		return resource.path("marketplace").path("deleteApp")
				.type(mediaType).put(responseType, requestEntity);
	}// end deleteChallenge()

	public <T> T getServicesByProviderId(Class<T> responseType, Long id,
			String mediaType) throws UniformInterfaceException {

		WebResource resource = webResource;

		resource = resource.queryParam("id", "" + id);

		return resource.path("marketplace").path("getServicesByProviderId")
				.type(mediaType).get(responseType);

	}// end getServicesByProviderId
	
	public <T> T buyService(Class<T> responseType, String mediaType,long userID, String userName, String userEmail, long serviceID) throws UniformInterfaceException {
        
    	WebResource resource = webResource;
    	
    	resource = resource.queryParam("userID", ""+userID);
    	resource = resource.queryParam("userName", ""+userName);
    	resource = resource.queryParam("userEmail", ""+userEmail);
    	resource = resource.queryParam("serviceID", ""+serviceID);

    	return resource.path("marketplace").path("buyService").type(mediaType).get(responseType);
    
}//end updateSubscription()

public <T> T getUserPurchases(Class<T> responseType, String mediaType, long userID) throws UniformInterfaceException {
        
        WebResource resource = webResource;
        
        resource = resource.queryParam("userID", ""+userID);
     	
        return resource.path("marketplace").path("getUserPurchases").type(mediaType).get(responseType);
    
}//end getUserPurchases()

public <T> T getServiceDetails(Class<T> responseType, String mediaType, long serviceID) throws UniformInterfaceException {
    
    WebResource resource = webResource;
    
    resource = resource.queryParam("serviceID", ""+serviceID);
 	
    return resource.path("marketplace").path("getServiceDetails").type(mediaType).get(responseType);

 }//end getServiceDetails()

}// end class
