package atc.otn.ckanclient;

import java.io.IOException;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class CKANClient {

	private static String CKAN_BASEURL;
	private static String CKAN_ADMIN_APIKEY;
	private static String PLATFORM_BASEURL;

	public CKANClient() {
		Properties p = new Properties();
		// initialize properties file
		try {
			p.load(this.getClass().getResourceAsStream("/settings.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CKAN_BASEURL = p.getProperty("CKAN.baseUrl");
		CKAN_ADMIN_APIKEY = p.getProperty("CKAN.adminapikey");
		PLATFORM_BASEURL = p.getProperty("otnWS.baseUrl");
	}

	/**
	 * Creates a CKAN user with the details of the liferay user
	 * 
	 * @param liferayUid
	 *            the user's liferay id
	 * @param liferayName
	 *            the user's liferay screenname
	 * @param liferayEmail
	 *            the user's liferay email
	 * @param liferayPassword
	 *            the user's liferay password
	 * @param liferayFullname
	 *            the user's liferay full name
	 * @return the CKAN API Key of the created user
	 */
	public final String createCKANUser(long liferayUid, String liferayName,
			String liferayEmail, String liferayPassword, String liferayFullname)
			throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		String userApikey = "";
		try {
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

			HttpPost httppost1 = new HttpPost(CKAN_BASEURL
					+ "/api/3/action/user_create");
			httppost1.setHeader("Authorization", CKAN_ADMIN_APIKEY);

			/*
			 * Create the user using the ckan_user_id = liferay-user-id naming
			 * convention
			 */
			JSONObject userJson = new JSONObject();
			userJson.put("name", liferayName);
			userJson.put("email", liferayEmail);
			userJson.put("fullname", liferayFullname);
			userJson.put("password", liferayPassword);
			userJson.put("id", liferayUid);

			StringEntity userString = new StringEntity(userJson.toString());
			httppost1.setEntity(userString);
			String responseBody = httpclient
					.execute(httppost1, responseHandler);

			/*
			 * expected response for new user { "help":
			 * "http://10.10.10.189/api/3/action/help_show?name=user_create",
			 * "success": true, "result": { "openid": null, "about": null,
			 * "apikey": "02980437-329c-438d-9c05-7f00bac94216", "display_name":
			 * "zxcvsdvafvdf", "name": "fvdfv", "created":
			 * "2015-08-18T16:28:52.796674", "email_hash":
			 * "bb0227d7c23d5bd6340e7d707729d6d4", "email": "fvfv@test.com",
			 * "sysadmin": false, "activity_streams_email_notifications": false,
			 * "state": "active", "number_of_edits": 0, "fullname":
			 * "zxcvsdvafvdf", "id": "11601", "number_created_packages": 0 } }
			 */

			JSONObject userResponse = new JSONObject(responseBody);
			JSONObject userResult = userResponse.getJSONObject("result");
			userApikey = userResult.getString("apikey");

			System.out.println("----------------------------------------");
			System.out.println("Create CKAN User for liferay user "
					+ liferayUid);
			System.out.println(responseBody);
			return userApikey;
		} finally {
			httpclient.close();
		}
	}

	/**
	 * Creates a CKAN organization for the given user. The user is admin of the
	 * created organization
	 * 
	 * @param liferayUid
	 *            the user's liferay id
	 */
	public final void createCKANOrg(long liferayUid) throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {

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
			HttpPost httppost2 = new HttpPost(CKAN_BASEURL
					+ "/api/3/action/organization_create");
			httppost2.setHeader("Authorization", CKAN_ADMIN_APIKEY);

			/*
			 * Create the organisation using the <liferay-id>_org naming
			 * convention
			 */
			JSONObject orgJson = new JSONObject();
			orgJson.put("name", liferayUid + "_org");
			orgJson.put("title", liferayUid + "_org");
			orgJson.put("id", liferayUid + "_org");

			/* Add the created user to the members of the new org */
			JSONArray orgUsers = new JSONArray();
			JSONObject orgUser = new JSONObject();
			orgUser.put("name", liferayUid);
			orgUser.put("capacity", "admin");
			orgUsers.put(orgUser);
			orgJson.put("users", orgUsers);

			StringEntity orgString = new StringEntity(orgJson.toString());
			httppost2.setEntity(orgString);

			String responseBody2 = httpclient.execute(httppost2,
					responseHandler);

			System.out.println("----------------------------------------");
			System.out
					.println("Create CKAN Org for liferay user " + liferayUid);
			System.out.println(responseBody2);
		} finally {
			httpclient.close();
		}
	}

	/**
	 * Updates a CKAN user. Called when the user/admin updates his profile in
	 * the liferay
	 * 
	 * @param liferayUid
	 *            the user's liferay id
	 * @param liferayName
	 *            the user's liferay screenname
	 * @param liferayEmail
	 *            the user's liferay email
	 * @param liferayPassword
	 *            the user's liferay password
	 */
	public final void updateCKANUser(long liferayUid, String liferayName,
			String liferayEmail, String liferayPassword) throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {

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

			HttpPost httppost = new HttpPost(CKAN_BASEURL
					+ "/api/3/action/user_update");
			httppost.setHeader("Authorization", CKAN_ADMIN_APIKEY);

			/*
			 * Create the organisation using the <liferay-id> naming convention
			 */
			JSONObject userJson = new JSONObject();
			userJson.put("id", "" + liferayUid);
			userJson.put("name", liferayName);
			userJson.put("email", liferayEmail);
			userJson.put("password", liferayPassword);

			StringEntity userString = new StringEntity(userJson.toString());
			httppost.setEntity(userString);
			String responseBody = httpclient.execute(httppost, responseHandler);
			System.out.println("----------------------------------------");
			System.out.println(responseBody);
		} finally {
			httpclient.close();
		}
	}

	/**
	 * Stores the liferay user and his associated CKAN API key
	 * 
	 * @param liferayUid
	 *            the user's liferay id
	 * @param userCKANapikey
	 *            the user's CKAN API key
	 */
	public final void storeUserApikey(long liferayUid, String userCKANapikey)
			throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {

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

			HttpPost httppost2 = new HttpPost(PLATFORM_BASEURL
					+ "/ckanservices");
			httppost2.setHeader("Content-Type", "application/json");
			/*
			 * Store the ckan api key with the associated liferay user id
			 */
			JSONObject ckanJson = new JSONObject();
			ckanJson.put("liferayUserid", "" + liferayUid);
			ckanJson.put("ckanApikey", userCKANapikey);

			StringEntity orgString = new StringEntity(ckanJson.toString());
			httppost2.setEntity(orgString);

			String responseBody2 = httpclient.execute(httppost2,
					responseHandler);

			System.out.println("----------------------------------------");
			System.out.println("Stored CKAN API key for liferay user "
					+ liferayUid);
			System.out.println(responseBody2);
		} finally {
			httpclient.close();
		}
	}
}
