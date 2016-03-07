package atc.otn.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ServiceClient {

	private static String COMPOSITION_SERVICE;
	private static String PORTAL_BASEURL;
	
	private static final Log logger = LogFactory.getLog(ServiceClient.class);
	 
	public ServiceClient() {
		
		//********************** Variables **********************

		Properties p = new Properties();
		InputStream in = null;

		//************************ Action *************************  

		try {
			
			in = this.getClass().getResourceAsStream("/portlet.properties");
			p.load(in);
			
			COMPOSITION_SERVICE = p.getProperty("MAP.compositionService");	
			PORTAL_BASEURL = p.getProperty("Portal.baseurl");

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
	public JSONArray getMapCompositions(String jsessionid){

		//********************** Variables **********************
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		HttpGet httpGet;
		
		CloseableHttpResponse response = null;
		
		JSONObject jsonResp;
		
		JSONArray mapCompositions = new JSONArray();
		
		//************************ Action *************************  
		System.out.println("Map Compositions: in here");
		
		try{
			
			BasicCookieStore cookieStore = new BasicCookieStore();
		    BasicClientCookie cookie = new BasicClientCookie("JSESSIONID", jsessionid);
		    cookie.setDomain(".intrasoft-intl.com");
		    cookie.setPath("/");
		    cookieStore.addCookie(cookie);
		    //HttpClient client = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).build();
		 		   		 
		 
		    HttpContext localContext = new BasicHttpContext();
		    localContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
		    
			//prepare http post
			httpGet = new HttpGet(COMPOSITION_SERVICE);
			httpGet.setHeader("Content-Type", "application/json");
			httpGet.setHeader("Accept", "application/json");
							

			//call the service
			
			response = httpclient.execute(httpGet,localContext);
			
			//get the response and convert it to json
			jsonResp = new JSONObject(EntityUtils.toString(response.getEntity()));
			
			
			mapCompositions = jsonResp.getJSONArray("results");
			System.out.println("Map Compositions:"+jsonResp);
		
			logger.info("Map Compositions:"+jsonResp);
						
		}
		catch(JSONException e){
			
			logger.error(e.getMessage());	
			System.out.println(e.getMessage());
		}
		
		catch(Exception e){
			
			logger.error(e.getMessage());
			System.out.println(e.getMessage());
		
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

		return mapCompositions;
		
	}//end getMapCompositions()
	
	public String getBaseUrl(){
		return PORTAL_BASEURL;
	}
}
