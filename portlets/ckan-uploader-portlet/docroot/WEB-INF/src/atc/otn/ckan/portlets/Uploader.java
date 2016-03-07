package atc.otn.ckan.portlets;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.ProcessAction;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import atc.otn.ckan.client.CKANClient;
import atc.otn.liferay.DM.DocumentManager;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.model.DLFolderConstants;
import com.liferay.portlet.documentlibrary.service.DLAppServiceUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.liferay.util.portlet.PortletProps;

/**
 * Portlet implementation class Uploader
 */
public class Uploader extends MVCPortlet {
 
	private static String ROOT_FOLDER_NAME = PortletProps.get("fileupload.folder.name"); 	 
	private static long PARENT_FOLDER_ID = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;
	
	 private static final Log logger = LogFactory.getLog(Uploader.class);

	 @Override
	 public void doView(RenderRequest renderRequest,RenderResponse renderResponse){
		 
		//********************** Variables **********************
				 		
		//json array holding the user receivers with their roles and units
		JSONArray licenses = new JSONArray();
		
		CKANClient client;
		ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY); 
		String url = "";
		
		//********************** Action **********************
		 
		try {
			
				url = PortalUtil.getLayoutURL(themeDisplay.getLayout(),themeDisplay);
				String city = "";
				
				if (url.contains("/web/antwerp")){
					city = "antwerp";
				}else if (url.contains("/web/birmingham")){
					city = "birmingham";
				}else if (url.contains("/web/issy")){
					city = "issy";
				}else if (url.contains("/web/liberec")){
					city = "liberec";
				}else{
					city = "guest";
				}
					
				renderRequest.setAttribute("city",city );
			
			//init CKAN client
			client = new CKANClient();
			
			licenses = client.getLicenses();
			
			//return list to jsp
			renderRequest.setAttribute("licenses", licenses);
			
			super.doView(renderRequest, renderResponse);

		} catch (Exception e) {
			
			e.printStackTrace();
		} 
		 
		 
	 }//end doView()

	 
	@ProcessAction(name = "uploadDocument")
	public void uploadDocument(ActionRequest actionRequest,ActionResponse actionResponse) throws IOException,
										PortletException, PortalException, SystemException{ 
		
		//********************** Variables **********************
		
		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY); 
		User user = themeDisplay.getUser();
		
		DocumentManager dm = new DocumentManager();
		
		Folder folder;
		
		UploadPortletRequest uploadPortletRequest = PortalUtil.getUploadPortletRequest(actionRequest); 

		File file;
		
		String fileName;
		
		Folder parentFolder;		

		long repositoryId;
		
		String mimeType;
		
		String fileURL = "";
		
		CKANClient client;
		
		JSONObject datasetData = new JSONObject(), resource = new JSONObject(), dTankresource = new JSONObject();
		
		JSONArray resources = new JSONArray(), extras = new JSONArray(), tags = new JSONArray(), dTankresources = new JSONArray();
		
		String[] tagsStr;
				
		String datasetURL = "";
		
		String dTankDatasetURL = "";
		
		//get the repository id
		repositoryId = themeDisplay.getScopeGroupId();			
		
		//get the CKAN folder
		parentFolder = DLAppServiceUtil.getFolder(repositoryId, PARENT_FOLDER_ID, ROOT_FOLDER_NAME);
		
		//init CKAN client
		client = new CKANClient();
		
		long now = new Date().getTime();
		
		//********************** Action **********************
	
		try{
		
//			logger.info("uploading document....");
			
			//get the file uploaded
			file = uploadPortletRequest.getFile("fileInput"); 
			
			//if file was given after all
			if(uploadPortletRequest.getFile("fileInput") != null && file.exists()){
			
				fileName = uploadPortletRequest.getFileName("fileInput");	
		 		
				mimeType = uploadPortletRequest.getContentType("fileInput");	

				//folder = dm.createUserFolder(actionRequest, themeDisplay); 
			
				fileURL = dm.uploadFile(themeDisplay, actionRequest, parentFolder, file, fileName, mimeType);  
		
				file.delete();
				
			}else if(ParamUtil.getString(uploadPortletRequest,"link") != null){
				
				fileURL = ParamUtil.getString(uploadPortletRequest,"link");
				
			}
					
			tagsStr = ParamUtil.getString(uploadPortletRequest,"tags").split(",");
			
			logger.info(ParamUtil.getString(uploadPortletRequest,"tags"));
			
			//create json objects as ckan requires them
			resource.put("url", fileURL);
			
			/* Give the resource the same name as the package */
			resource.put("name", ParamUtil.getString(uploadPortletRequest,"dstitle"));
			//resource.put("name", ParamUtil.getString(uploadPortletRequest,"dname"));
			//resource.put("description", ParamUtil.getString(uploadPortletRequest,"description"));
			resource.put("format", ParamUtil.getString(uploadPortletRequest,"format"));
			
			resources.put(resource);
			
			/* Don't upload to CKAN xlxs,xls and csv files */
			if(!resource.getString("format").equals("XLSX") && !resource.getString("format").equals("XLS") &&
				!resource.getString("format").equals("CSV"))
			{
			
			for(String tag : tagsStr){
				
				if(!tag.isEmpty()){
					tags.put(new JSONObject().put("name", tag));
				}
			}
			
			//convert title of dataset to ascii characters for the url to be valid
			String stringName = ParamUtil.getString(uploadPortletRequest, "dstitle").toLowerCase().replace(" ", "-");
			String nameForUrl = java.net.URLEncoder.encode(stringName.toString(), "ascii").replace("%", "-").toLowerCase();
			
			datasetData.put("title", ParamUtil.getString(uploadPortletRequest, "dstitle"));
			datasetData.put("name", nameForUrl+now);
			datasetData.put("owner_org", user.getUserId()+"_org");			
			datasetData.put("notes", ParamUtil.getString(uploadPortletRequest, "notes"));
			datasetData.put("url", ParamUtil.getString(uploadPortletRequest, "source"));
			datasetData.put("version", ParamUtil.getString(uploadPortletRequest, "version"));
			datasetData.put("author", ParamUtil.getString(uploadPortletRequest, "author"));
			datasetData.put("license_id", ParamUtil.getString(uploadPortletRequest, "license"));
			
			datasetData.put("resources", resources);
						
			if(tags.length()>0){
				datasetData.put("tags", tags);
			}				
			
			
			//Store dataset in CKAN
			 datasetURL = client.addUserDataset(user.getUserId(), datasetData);
			 
			 
			 if(!datasetURL.startsWith("http"))
			 {				
				 actionRequest.setAttribute("ckanerror", datasetURL);
				 SessionErrors.add(actionRequest, "ckanerror");				
			 }
			 else{
				 SessionMessages.add(actionRequest,"ckansuccess");
				 System.out.println("uploaded to CKAN " + datasetURL);
			 }
			 
			}// End CKAN UPload
			else
			{// Upload to Datatank
			 
			 if(!fileURL.equals("")){
				JSONObject dtankData = new JSONObject();
				
				/*Remove everything after the .xls*/
				int ext = 0;
				if((ext = fileURL.indexOf(".xls/")) > 0)
				{
					fileURL = fileURL.substring(0,ext + 4);
				}
				else if((ext = fileURL.indexOf(".xlsx/")) > 0)
				{
					fileURL = fileURL.substring(0,ext + 5);
				}
				else if((ext = fileURL.indexOf(".csv")) > 0)
				{
					fileURL = fileURL.substring(0,ext + 4);
				}				
				System.out.println("Datatank resource url: " + fileURL);
				
				String dTankformat = ParamUtil.getString(uploadPortletRequest,"format").toLowerCase();
				if(dTankformat.equals("xlsx"))
				{
					dTankformat = "xls";
				}
				dtankData.put("description", ParamUtil.getString(uploadPortletRequest, "notes"));
				dtankData.put("uri", fileURL);
				dtankData.put("type", dTankformat);
				dtankData.put("title", ParamUtil.getString(uploadPortletRequest, "dstitle"));
				
				dTankDatasetURL = client.addToDataTank(dtankData,dTankformat,ParamUtil.getString(uploadPortletRequest, "dstitle").toLowerCase().replace(" ", "_") + now);
				
				if(dTankDatasetURL.startsWith("http:"))
				{		
				
					logger.info("uploaded to Datatank: " + dTankDatasetURL);
					if(client.getDatatankTransform(dTankDatasetURL))				
					{				 
					 /*Now store the file back in CKAN*/
						dTankresource.put("url", dTankDatasetURL + ".geojson");
						/* Give the resource the same name as the package */
						dTankresource.put("name", ParamUtil.getString(uploadPortletRequest,"dstitle"));
						//resource.put("name", ParamUtil.getString(uploadPortletRequest,"dname"));
						//resource.put("description", ParamUtil.getString(uploadPortletRequest,"description"));
						dTankresource.put("format", "geojson");
						
						dTankresources.put(dTankresource);
						
					 for(String tag : tagsStr){
							
							if(!tag.isEmpty()){
								tags.put(new JSONObject().put("name", tag));
							}
						}						
						datasetData.put("title", ParamUtil.getString(uploadPortletRequest, "dstitle"));
						datasetData.put("name", ParamUtil.getString(uploadPortletRequest, "dstitle").toLowerCase().replace(" ", "-") + now);
						datasetData.put("owner_org", user.getUserId()+"_org");			
						datasetData.put("notes", ParamUtil.getString(uploadPortletRequest, "notes"));
						datasetData.put("url", ParamUtil.getString(uploadPortletRequest, "source"));
						datasetData.put("version", ParamUtil.getString(uploadPortletRequest, "version"));
						datasetData.put("author", ParamUtil.getString(uploadPortletRequest, "author"));
						datasetData.put("license_id", ParamUtil.getString(uploadPortletRequest, "license"));

						//if uploaded file format is xls or csv
						if (ParamUtil.getString(uploadPortletRequest,"format").toLowerCase().equals("csv")|| ParamUtil.getString(uploadPortletRequest,"format").toLowerCase().equals("xls")|| ParamUtil.getString(uploadPortletRequest,"format").toLowerCase().equals("xlsx"))	{

							client = new CKANClient();
							//get the bbox value from geojson coming from Datatank
							extras = client.getExtent(dTankDatasetURL);
							
							//prepare the jsonArray from service to insert it as a custom field in CKAN
							String extrStr = extras.toString().replaceAll("[\\[\\]]", "");						
							List<HashMap<String, String>> extraDictionaries = new ArrayList<HashMap<String,String>>();						
							HashMap<String, String> extraDictionary = new HashMap<String, String>();
							extraDictionary.put("value", extrStr);
							extraDictionary.put("key", "extent");						
							extraDictionaries.add(extraDictionary);
							
							datasetData.put("extras", extraDictionaries);
						}
						
						datasetData.put("resources", dTankresources);
									
						if(tags.length()>0){
							datasetData.put("tags", tags);
						}				
						
						
						//Store dataset in CKAN
						 datasetURL = client.addUserDataset(user.getUserId(), datasetData);
						 
						 if(!datasetURL.startsWith("http"))
						 {				
							 actionRequest.setAttribute("ckanerror", datasetURL);
							 SessionErrors.add(actionRequest, "ckanerror");				
						 }
						 //actionRequest.setAttribute("dTanksuccess", dTankDatasetURL +".geojson");
						 actionRequest.setAttribute("dTanksuccess", "Your dataset is succefully transformed into geojson.<br /> Go to <a href='/my-datasets'>My Datasets</a> to view it!");
						 SessionMessages.add(actionRequest,"dTanksuccess");
						 logger.info("Dtank transofrmed succesfully stored in CKAN: " + datasetURL);
					}
					else{
						actionRequest.setAttribute("dTankerror", "Your file could not be transformed into geojson format.<br /> Please check if it has column headers and if the records contain coordinates. For excel files, the system will try to transform data found in the first sheet which should be named 'sheet1'");
						 SessionErrors.add(actionRequest, "dTankerror");	
					}	
				}
				else{
					actionRequest.setAttribute("dTankerror", "Your file could not be transformed into geojson format");
					 SessionErrors.add(actionRequest, "dTankerror");	
				}	
			 }
			}
			
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}//end uploadDocument()

}//end class
