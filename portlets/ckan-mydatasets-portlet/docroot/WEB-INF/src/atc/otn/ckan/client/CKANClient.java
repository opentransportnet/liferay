package atc.otn.ckan.client;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class CKANClient {
	private static String CKAN_BASEURL;
	private static String CKAN_ADMIN_APIKEY;
	private static String PLATFORM_BASEURL;
	private static String DATATANK_BASEURL;
	
	private static final Log logger = LogFactory.getLog(CKANClient.class);
	 
	public CKANClient() {
		
		//********************** Variables **********************

		Properties p = new Properties();
		InputStream in = null;

		//************************ Action *************************  

		try {
			
			in = this.getClass().getResourceAsStream("/portlet.properties");
			p.load(in);
			
			CKAN_BASEURL = p.getProperty("CKAN.baseUrl");
			CKAN_ADMIN_APIKEY = p.getProperty("CKAN.adminapikey");
			PLATFORM_BASEURL = p.getProperty("otnWS.baseUrl");
			DATATANK_BASEURL = p.getProperty("Datatank.baseurl");

			in.close();
						
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}
	
	}//end constructor
	
	/**
	 * 
	 * @return
	 */
	private ResponseHandler<String> getGenericResponseHandler(){
		
		// Create a custom response handler
		ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

			@Override
			public String handleResponse(final HttpResponse response)
					throws ClientProtocolException, IOException {
				int status = response.getStatusLine().getStatusCode();

				if (status >= 200 && status < 300) {
					HttpEntity entity = response.getEntity();
					return entity != null ? EntityUtils.toString(entity)
							: null;
				} else {
					throw new ClientProtocolException(
							"Unexpected response status: "
									+ status
									+ EntityUtils.toString(response
											.getEntity()));
				}
			}
		};

		return responseHandler;
		
	}//end getGenericResponseHandler()
	
	/***
	 * 
	 */
	public JSONArray getuserDatasets(String orgId){

		//********************** Variables **********************
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		HttpGet httpGet;
		
		CloseableHttpResponse response = null;
		
		JSONObject jsonResp;
		
		JSONArray datasets = new JSONArray();
		
		//************************ Action *************************  
	
		
		try{
			
			//prepare http post
			httpGet = new HttpGet(CKAN_BASEURL+ "organization_show?include_datasets=true&id=" + orgId);
			httpGet.setHeader("Content-Type", "application/json");
			httpGet.setHeader("Accept", "application/json");

			//call the service
			
			response = httpclient.execute(httpGet);
			
			//get the response and convert it to json
			
			jsonResp = new JSONObject(EntityUtils.toString(response.getEntity()));
			
			
			if(jsonResp.has("result"))
			{
				datasets = jsonResp.getJSONObject("result").getJSONArray("packages");
			}
			/* If no results is returned, the user is not found in CKAN 
			 * so we ask him to create himself*/
			else{
				JSONObject error = new JSONObject();
				error.put("ckan-user", "false");				
				datasets.put(error);
			}
			logger.info("Datasets:"+jsonResp);
						
		}
		catch(JSONException e){
			
			logger.error(e.getMessage());		
		}
		
		catch(Exception e){
			
			logger.error(e.getMessage());
		
		}finally{
			
			if(response != null){
				
				try {
					
					response.close();
					
					httpclient.close();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.error(e.getMessage());
				}
				
			}//end if
			
			
		}//end finally

		return datasets;
		
	}//end getuserDatasets()
	
	public JSONArray getDatasets(){

		//********************** Variables **********************
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		HttpGet httpGet;
		
		CloseableHttpResponse response = null;
		
		JSONObject jsonResp;
		
		JSONArray datasets = new JSONArray();
		
		//************************ Action *************************  
	
		
		try{
			
			//prepare http post
			httpGet = new HttpGet(CKAN_BASEURL+ "current_package_list_with_resources");
			httpGet.setHeader("Content-Type", "application/json");
			httpGet.setHeader("Accept", "application/json");

			//call the service
			
			response = httpclient.execute(httpGet);
			
			//get the response and convert it to json
			jsonResp = new JSONObject(EntityUtils.toString(response.getEntity()));
			
			
			if(jsonResp.has("result"))
			{
				datasets = jsonResp.getJSONArray("result");
			}
			/* If no results is returned, the user is not found in CKAN 
			 * so we ask him to create himself*/
			else{
				JSONObject error = new JSONObject();
				error.put("ckan-user", "false");				
				datasets.put(error);
			}
			logger.info("All Datasets:"+jsonResp);
						
		}
		catch(JSONException e){
			
			logger.error(e.getMessage());		
		}
		
		catch(Exception e){
			
			logger.error(e.getMessage());
		
		}finally{
			
			if(response != null){
				
				try {
					
					response.close();
					
					httpclient.close();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.error(e.getMessage());
				}
				
			}//end if
			
			
		}//end finally

		return datasets;
		
	}//end getuserDatasets()
	
	public JSONArray getServices(String city){

		//********************** Variables **********************
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		HttpGet httpGet;
		
		CloseableHttpResponse response = null;
		
		JSONObject jsonResp;
		
		JSONArray services = new JSONArray();
		
		//************************ Action *************************  
	
		
		try{
			
			//prepare http post
			httpGet = new HttpGet(PLATFORM_BASEURL+ "/marketplace/getAllServices?city="+city);
			httpGet.setHeader("Content-Type", "application/json");
			httpGet.setHeader("Accept", "application/json");

			logger.info(PLATFORM_BASEURL+ "/marketplace/getAllServices");
			
			//call the service
			
			response = httpclient.execute(httpGet);
			
			//get the response and convert it to json
			jsonResp = new JSONObject(EntityUtils.toString(response.getEntity()));
			
			
			if(jsonResp.has("services"))
			{
				services = jsonResp.getJSONArray("services");
			}
			/* If no results is returned, the user is not found in CKAN 
			 * so we ask him to create himself*/
			else{
				JSONObject error = new JSONObject();
				error.put("service", "false");				
				services.put(error);
			}
			logger.info("All Services:"+jsonResp);
						
		}
		catch(JSONException e){
			
			logger.error(e.getMessage());		
		}
		
		catch(Exception e){
			
			logger.error(e.getMessage());
		
		}finally{
			
			if(response != null){
				
				try {
					
					response.close();
					
					httpclient.close();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.error(e.getMessage());
				}
				
			}//end if
			
			
		}//end finally

		return services;
		
	}//end getServices()
	
	public JSONArray getApps(String city){

		//********************** Variables **********************
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		HttpGet httpGet;
		
		CloseableHttpResponse response = null;
		
		JSONObject jsonResp;
		
		JSONArray services = new JSONArray();
		
		//************************ Action *************************  
	
		
		try{
			
			//prepare http post
			httpGet = new HttpGet(PLATFORM_BASEURL+ "/marketplace/getApps?city="+city);
			httpGet.setHeader("Content-Type", "application/json");
			httpGet.setHeader("Accept", "application/json");

			logger.info(PLATFORM_BASEURL+ "/marketplace/getApps");
			
			//call the service
			
			response = httpclient.execute(httpGet);
			
			//get the response and convert it to json
			jsonResp = new JSONObject(EntityUtils.toString(response.getEntity()));
			
			
			if(jsonResp.has("apps"))
			{
				services = jsonResp.getJSONArray("apps");
			}
			/* If no results is returned, the user is not found in CKAN 
			 * so we ask him to create himself*/
			else{
				JSONObject error = new JSONObject();
				error.put("service", "false");				
				services.put(error);
			}
			logger.info("All Apps:"+jsonResp);
						
		}
		catch(JSONException e){
			
			logger.error(e.getMessage());		
		}
		
		catch(Exception e){
			
			logger.error(e.getMessage());
		
		}finally{
			
			if(response != null){
				
				try {
					
					response.close();
					
					httpclient.close();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.error(e.getMessage());
				}
				
			}//end if
			
			
		}//end finally

		return services;
		
	}//end getServices()
	
	/***
	 * 
	 */
	public JSONObject getDataset(String datasetId){

		//********************** Variables **********************
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		HttpGet httpGet;
		
		CloseableHttpResponse response = null;
		
		JSONObject jsonResp;
		JSONObject datasetDetails = new JSONObject();
		
		JSONArray datasets = new JSONArray();
		
		//************************ Action *************************  
	
		
		try{
			
			//prepare http post
			httpGet = new HttpGet(CKAN_BASEURL+ "package_show?id=" + datasetId);
			httpGet.setHeader("Content-Type", "application/json");
			httpGet.setHeader("Accept", "application/json");

			//call the service
			
			response = httpclient.execute(httpGet);
			
			//get the response and convert it to json
			jsonResp = new JSONObject(EntityUtils.toString(response.getEntity()));
			
			
			if(jsonResp.has("result"))
			{
				datasetDetails = jsonResp.getJSONObject("result");
			}
			/* If no results is returned, the user is not found in CKAN 
			 * so we ask him to create himself*/
			else{				
				datasetDetails.put("error", "false");		
				
			}
			logger.info("Asked for datasetDetails: "+jsonResp);
						
		}
		catch(Exception e){
			
			logger.error(e.getMessage());
		
		}finally{
			
			if(response != null){
				
				try {
					
					response.close();
					
					httpclient.close();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.error(e.getMessage());
				}
				
			}//end if			
			
		}//end finally

		return datasetDetails;
		
	}//end getuserDatasets()
	
	/***
	 * 
	 */
	public JSONArray getLicenses(){

		//********************** Variables **********************
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		HttpGet httpGet;
		
		CloseableHttpResponse response = null;
		
		JSONObject jsonResp;
		
		JSONArray licenses = new JSONArray();
		
		//************************ Action *************************  
	
		
		try{
			
			//prepare http post
			httpGet = new HttpGet(CKAN_BASEURL+ "license_list");
			httpGet.setHeader("Content-Type", "application/json");
			httpGet.setHeader("Accept", "application/json");

			//call the service
			response = httpclient.execute(httpGet);
			
			//get the response and convert it to json
			jsonResp = new JSONObject(EntityUtils.toString(response.getEntity()));
					
			licenses = jsonResp.getJSONArray("result");
			
			logger.info("licences:"+jsonResp);
						
		}catch(Exception e){
			
			logger.error(e.getMessage());
		
		}finally{
			
			if(response != null){
				
				try {
					
					response.close();
					
					httpclient.close();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.error(e.getMessage());
				}
				
			}//end if
			
			
		}//end finally

		return licenses;
		
	}//end getLicenses()
	
private String getUserAPIKey(long liferayID){
		
		//********************** Variables **********************
		
		CloseableHttpClient httpclient = HttpClients.createDefault();;
		
		HttpGet httpGet;
		
		CloseableHttpResponse response = null;
		
		JSONObject jsonResp;
		
		String ckanAPIKey = "";
		
		//************************ Action *************************  
		
		try{
									
			//prepare http post
			httpGet = new HttpGet(PLATFORM_BASEURL+ "/ckanservices/"+"apikeys/"+liferayID);
			httpGet.setHeader("Content-Type", "application/json");
			httpGet.setHeader("Accept", "application/json");

			//call the service
			response = httpclient.execute(httpGet);
			
			//get the response and convert it to json
			jsonResp = new JSONObject(EntityUtils.toString(response.getEntity()));
					
			//extract the user's api key
			ckanAPIKey = jsonResp.getString("ckanApikey");
//			logger.info("ckan api key: "+ckanAPIKey);			
		}catch(Exception e){
			
			logger.error(e.getMessage());
		
		}finally{
			
			if(response != null){
				
				try {
					
					response.close();
					
					httpclient.close();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.error(e.getMessage());
				}
				
			}//end if
			
			
		}//end finally
		
		return ckanAPIKey;
		
	}//end getUserAPIKey()
	
	
	public void editUserDataset(long liferayID, JSONObject dataset){
		
		//********************** Variables **********************
		
		CloseableHttpClient httpclient = HttpClients.createDefault();;
		
		String ckanAPIKey = "";

		HttpPost httppost;

		StringEntity dataSetEntity;
		
		CloseableHttpResponse response = null;

		JSONObject jsonResp;

		//************************ Action *************************  

		try{
			
			//get the user's api key
			ckanAPIKey = getUserAPIKey(liferayID);					


	//		logger.info("ckan url:"+CKAN_BASEURL+ "package_create");
			
			httppost = new HttpPost(CKAN_BASEURL+ "package_update");
			httppost.setHeader("Content-Type", "application/json");
			httppost.setHeader("Accept", "application/json");
			httppost.setHeader("Authorization", ckanAPIKey);

			
			dataSetEntity = new StringEntity(dataset.toString(), StandardCharsets.UTF_8);
			httppost.setEntity(dataSetEntity);
			
			response = httpclient.execute(httppost);
			
			//logger.info("Trying to update " + EntityUtils.toString(response.getEntity()));
			//get the response and convert it to json
			
			jsonResp = new JSONObject(dataSetEntity);

			
			logger.info("resp:"+jsonResp);
			
		}catch(Exception e){
			
			e.printStackTrace();
			
		} finally {
			
			try {
				
				response.close();
				
				httpclient.close();
			
			} catch (IOException e) {

				e.printStackTrace();
			}
			
		}

	}//end editUserDataset()
	
	
public String addToDataTank(JSONObject dataset, String dTankTitle){
		
		//********************** Variables **********************
		
		CloseableHttpClient httpclient = HttpClients.createDefault();;
		
		String ckanAPIKey = "";


		HttpPost httppost;

		StringEntity dataSetEntity;
		
		CloseableHttpResponse response = null;

		JSONObject jsonResp;
		
		HttpPut httpput;

		//************************ Action *************************  

		try{
			
			//get the user's api key
			/*ckanAPIKey = getUserAPIKey(liferayID);*/
			
	//		logger.info("ckan url:"+CKAN_BASEURL+ "package_create");
			
			/*httppost = new HttpPost(CKAN_BASEURL+ "package_create");
			httppost.setHeader("Content-Type", "application/json");
			httppost.setHeader("Accept", "application/json");
			httppost.setHeader("Authorization", ckanAPIKey);

			dataSetEntity = new StringEntity(dataset.toString());
			httppost.setEntity(dataSetEntity);
			
			response = httpclient.execute(httppost);
			
			//get the response and convert it to json
			jsonResp = new JSONObject(EntityUtils.toString(response.getEntity()));

			logger.info("resp:"+jsonResp);*/
			
			String credentials = "admin:admin";
			String encoding = org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(credentials.getBytes());
			httpput = new HttpPut(DATATANK_BASEURL+ "api/definitions/xls/" + dTankTitle);
			httpput.setHeader("Content-Type", "application/tdt.definition+json");
			//httpput.setHeader("Accept", "application/json");
			httpput.setHeader("Authorization", "Basic " + encoding);
			
			dataSetEntity = new StringEntity(dataset.toString(), StandardCharsets.UTF_8);
			httpput.setEntity(dataSetEntity);
			
			response = httpclient.execute(httpput);
			
			//get the response and convert it to json
			//jsonResp = new JSONObject(EntityUtils.toString(response.getEntity()));
			
			if(response.getStatusLine().getStatusCode() == 200)
			{
				return DATATANK_BASEURL + "/xls/" + dTankTitle;
			}

			String respo = EntityUtils.toString(response.getEntity());
			logger.info("Datatank Response:" + respo + " orig= " + response.toString());
			
		}catch(Exception e){			
			e.printStackTrace();			
		} finally {			
			try {				
				response.close();				
				httpclient.close();
			
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
		return "";

	}//end addToDataTank()


public void deleteUserDataset(long liferayID, String datasetId, JSONObject dataset){
		
		//********************** Variables **********************
		CloseableHttpClient httpclient = HttpClients.createDefault();;
		
		String ckanAPIKey = "";

		HttpPost httppost;

		StringEntity dataSetEntity;
		
		CloseableHttpResponse response = null;

		JSONObject jsonResp;

		//************************ Action *************************  

		try{
			
			//get the user's api key
			ckanAPIKey = getUserAPIKey(liferayID);
			logger.info("ckanAPIKey " + ckanAPIKey);
			
	//		logger.info("ckan url:"+CKAN_BASEURL+ "package_create");
			
			httppost = new HttpPost(CKAN_BASEURL+ "package_delete");
			httppost.setHeader("Content-Type", "application/json");
			httppost.setHeader("Accept", "application/json");
			httppost.setHeader("Authorization", ckanAPIKey);

			dataSetEntity = new StringEntity(dataset.toString(), StandardCharsets.UTF_8);
			httppost.setEntity(dataSetEntity);
			System.out.println(dataset.toString());
			
			response = httpclient.execute(httppost);
						
			//get the response and convert it to json
			jsonResp = new JSONObject(dataSetEntity);
			
			logger.info("resp:"+jsonResp);
			
		}catch(Exception e){
			
			e.printStackTrace();
			
		} finally {
			
			try {
				
				response.close();
				
				httpclient.close();
			
			} catch (IOException e) {

				e.printStackTrace();
			}
			
		}

	}//end deleteUserDataset()

public void editOrganizationDataset(long liferayID, JSONObject dataset){
	
	//********************** Variables **********************
	
	CloseableHttpClient httpclient = HttpClients.createDefault();;
	
	String ckanAPIKey = "";

	HttpPost httppost;

	StringEntity dataSetEntity;
	
	CloseableHttpResponse response = null;

	JSONObject jsonResp;

	//************************ Action *************************  

	try{
		
		//get the user's api key
//		ckanAPIKey = getUserAPIKey(liferayID);
		ckanAPIKey = CKAN_ADMIN_APIKEY;
		logger.info("ckanAPIKey " + ckanAPIKey);
		
//		logger.info("ckan url:"+CKAN_BASEURL+ "package_create");
		
		httppost = new HttpPost(CKAN_BASEURL+ "package_update");
		httppost.setHeader("Content-Type", "application/json");
		httppost.setHeader("Accept", "application/json");
		httppost.setHeader("Authorization", ckanAPIKey);

		
		dataSetEntity = new StringEntity(dataset.toString(), StandardCharsets.UTF_8);
		httppost.setEntity(dataSetEntity);
		
		response = httpclient.execute(httppost);
		
		//logger.info("Trying to update " + EntityUtils.toString(response.getEntity()));
		//get the response and convert it to json
		
		jsonResp = new JSONObject(dataSetEntity);

		
		logger.info("resp"+jsonResp);
		
	}catch(Exception e){
		
		e.printStackTrace();
		
	} finally {
		
		try {
			
			response.close();
			
			httpclient.close();
		
		} catch (IOException e) {

			e.printStackTrace();
		}
		
	}

}//end editUserDataset()

public void deleteOrganizationDataset(long liferayID, String datasetId, JSONObject dataset){
	
	//********************** Variables **********************
	CloseableHttpClient httpclient = HttpClients.createDefault();;
	
	String ckanAPIKey = "";

	HttpPost httppost;

	StringEntity dataSetEntity;
	
	CloseableHttpResponse response = null;

	JSONObject jsonResp;

	//************************ Action *************************  

	try{
		
		//get the user's api key
//		ckanAPIKey = getUserAPIKey(liferayID);
		ckanAPIKey = CKAN_ADMIN_APIKEY;
		logger.info("ckanAPIKey " + ckanAPIKey);
		
//		logger.info("ckan url:"+CKAN_BASEURL+ "package_create");
		
		httppost = new HttpPost(CKAN_BASEURL+ "package_delete");
		httppost.setHeader("Content-Type", "application/json");
		httppost.setHeader("Accept", "application/json");
		httppost.setHeader("Authorization", ckanAPIKey);

		dataSetEntity = new StringEntity(dataset.toString(), StandardCharsets.UTF_8);
		httppost.setEntity(dataSetEntity);
		System.out.println(dataset.toString());
		
		response = httpclient.execute(httppost);
					
		//get the response and convert it to json
		jsonResp = new JSONObject(dataSetEntity);
		
		logger.info("resp:"+jsonResp);
		
	}catch(Exception e){
		
		e.printStackTrace();
		
	} finally {
		
		try {
			
			response.close();
			
			httpclient.close();
		
		} catch (IOException e) {

			e.printStackTrace();
		}
		
	}

}//end deleteUserDataset()

}
