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
import org.apache.log4j.pattern.LogEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import atc.otn.liferay.DM.DocumentManager;

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
	 * @param liferayID
	 */
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
	
	
	public String addUserDataset(long liferayID, JSONObject dataset){
		
		//********************** Variables **********************
		
		CloseableHttpClient httpclient = HttpClients.createDefault();;
		
		String ckanAPIKey = "";

		HttpPost httppost;

		StringEntity dataSetEntity;
		
		String datasetURL ="";
		
		CloseableHttpResponse response = null;

		JSONObject jsonResp;

		//************************ Action *************************  

		try{
			
			//get the user's api key
			ckanAPIKey = getUserAPIKey(liferayID);
			
	//		logger.info("ckan url:"+CKAN_BASEURL+ "package_create");
			
			httppost = new HttpPost(CKAN_BASEURL+ "package_create");
			httppost.setHeader("Content-Type", "application/json");
			httppost.setHeader("Accept", "application/json");
			httppost.setHeader("Authorization", ckanAPIKey);

			dataSetEntity = new StringEntity(dataset.toString(), StandardCharsets.UTF_8);
			
			//logger.info(dataSetEntity);
			httppost.setEntity(dataSetEntity);
			
			response = httpclient.execute(httppost);
			
			
			//get the response and convert it to json
			jsonResp = new JSONObject(EntityUtils.toString(response.getEntity()));
			logger.info("resp:"+jsonResp);
						
			if(jsonResp.has("result"))
			{
				JSONObject resour = (JSONObject) jsonResp.getJSONObject("result").getJSONArray("resources").get(0);
				datasetURL = resour.getString("url");				
				
				logger.info("created url: "+datasetURL);
			}
			else {
				JSONObject errorObj = jsonResp.getJSONObject("error");
				if(errorObj.has("message"))
				{
					datasetURL = errorObj.getString("message");
				}
				else if(errorObj.has("name"))
				{
					
					datasetURL = (String) errorObj.getJSONArray("name").get(0);
				}
				else{
					datasetURL = "Error storing the dataset";
				}
				
			}
			
		}catch(Exception e){
			
			e.printStackTrace();
			
		} finally {
			
			try {
				
				response.close();
				
				httpclient.close();
				
				return datasetURL;
			
			} catch (IOException e) {

				e.printStackTrace();
			}
			
		}
		return datasetURL;

	}//end addUserDataset()
	
public String addOrganizationDataset(long liferayID, JSONObject dataset){
		
		//********************** Variables **********************
		
		CloseableHttpClient httpclient = HttpClients.createDefault();;
		
		String ckanAPIKey = "";

		HttpPost httppost;

		StringEntity dataSetEntity;
		
		String datasetURL ="";
		
		CloseableHttpResponse response = null;

		JSONObject jsonResp;

		//************************ Action *************************  

		try{
			
			//get the user's api key
			ckanAPIKey = CKAN_ADMIN_APIKEY;
			
	//		logger.info("ckan url:"+CKAN_BASEURL+ "package_create");
			
			httppost = new HttpPost(CKAN_BASEURL+ "package_create");
			httppost.setHeader("Content-Type", "application/json");
			httppost.setHeader("Accept", "application/json");
			httppost.setHeader("Authorization", ckanAPIKey);

			dataSetEntity = new StringEntity(dataset.toString(), StandardCharsets.UTF_8);
			httppost.setEntity(dataSetEntity);
			
			response = httpclient.execute(httppost);
			//get the response and convert it to json
			jsonResp = new JSONObject(EntityUtils.toString(response.getEntity()));
			logger.info("resp:"+jsonResp);
			
			if(jsonResp.has("result"))
			{
				JSONObject resour = (JSONObject) jsonResp.getJSONObject("result").getJSONArray("resources").get(0);
				datasetURL = resour.getString("url");				
				
				logger.info("created url: "+datasetURL);
			}
			else {
				JSONObject errorObj = jsonResp.getJSONObject("error");
				if(errorObj.has("message"))
				{
					datasetURL = errorObj.getString("message");
				}
				else if(errorObj.has("name"))
				{
					
					datasetURL = (String) errorObj.getJSONArray("name").get(0);
				}
				else{
					datasetURL = "Error storing the dataset";
				}
				
			}
			
		}catch(Exception e){
			
			e.printStackTrace();
			
		} finally {
			
			try {
				
				response.close();
				
				httpclient.close();
				
				return datasetURL;
			
			} catch (IOException e) {

				e.printStackTrace();
			}
			
		}
		return datasetURL;

	}//end addOrganizationDataset()
	
	
	/***
	 * 
	 */
	public JSONArray getLicenses(){

		//********************** Variables **********************
		
		CloseableHttpClient httpclient = HttpClients.createDefault();;
		
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
	
	public JSONArray getExtent(String dTankDatasetURL){
		CloseableHttpClient httpclient = HttpClients.createDefault();;
		
		HttpGet httpGet;
		
		CloseableHttpResponse response = null;
		
		JSONArray extras = new JSONArray();
		
		//************************ Action *************************  
		
		try{
			httpGet = new HttpGet(dTankDatasetURL+ ".geojson");
			httpGet.setHeader("Content-Type", "application/json");
			httpGet.setHeader("Accept", "application/json");

			//call the service
			response = httpclient.execute(httpGet);
			if(response.getStatusLine().getStatusCode() == 200)
			{
				
				JSONObject jsonRespExtras;
				jsonRespExtras = new JSONObject(EntityUtils.toString(response.getEntity()));
				
				if(jsonRespExtras.has("bbox"))
				{
					extras = (JSONArray) jsonRespExtras.getJSONArray("bbox");
				}
			}
						
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
		return extras;
	}//end getExtent()
	
public String addToDataTank(JSONObject dataset,String format, String dTankTitle){
		
		//********************** Variables **********************
		
		CloseableHttpClient httpclient = HttpClients.createDefault();;
		
		String ckanAPIKey = "";

		HttpPost httppost;

		StringEntity dataSetEntity;
		
		CloseableHttpResponse response = null;

		JSONObject jsonResp;
		
		String responsedTank ="";
		
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
			httpput = new HttpPut(DATATANK_BASEURL+ "api/definitions/" + format +"/" + dTankTitle);
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
				responsedTank = DATATANK_BASEURL + format + "/" + dTankTitle;
			}
			else{
				responsedTank = "error";
				JSONObject dTankespo = new JSONObject(EntityUtils.toString(response.getEntity()));
				logger.info("Datatank JSONObject Response:" + dTankespo.toString());
				if(dTankespo.has("error"))
				{
					responsedTank = dTankespo.getJSONObject("error").getString("message");
				}				
			}
			logger.info("Datatank Raw Response:" + response.toString());
			
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
		return responsedTank;

	}//end addToDataTank()

/***
 * 
 * @param dTankDatasetURL
 */
public boolean getDatatankTransform(String dTankDatasetURL){
	
	//********************** Variables **********************
	
	CloseableHttpClient httpclient = HttpClients.createDefault();;
	
	HttpGet httpGet;
	
	CloseableHttpResponse response = null;
	
	
	//************************ Action *************************  
	
	try{
		httpGet = new HttpGet(dTankDatasetURL+ ".geojson");
		httpGet.setHeader("Content-Type", "application/json");
		httpGet.setHeader("Accept", "application/json");

		//call the service
		response = httpclient.execute(httpGet);
		logger.info("Datatank Trnsformation Effort for: " + dTankDatasetURL + " produced "+ response.toString());
		if(response.getStatusLine().getStatusCode() == 200)
		{
			return true;
		}
					
	}catch(Exception e){		
		logger.error(e.getMessage());
		return false;
	
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
	return false;
}//end getUserAPIKey()
	
}//end class
