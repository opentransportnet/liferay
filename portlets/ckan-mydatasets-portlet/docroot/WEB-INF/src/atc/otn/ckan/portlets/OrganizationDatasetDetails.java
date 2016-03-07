package atc.otn.ckan.portlets;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.ProcessAction;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import atc.otn.ckan.client.CKANClient;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionThreadLocal;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.PortletURLFactoryUtil;

/**
 * Portlet implementation class DatasetDetails
 */
public class OrganizationDatasetDetails extends MVCPortlet {
	
	private static final Log logger = LogFactory.getLog(DatasetDetails.class);
	
	public void doView(RenderRequest renderRequest,RenderResponse renderResponse){
		 
		//********************** Variables **********************
				 			
		CKANClient client;	
		 

		HttpServletRequest httpReq = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(renderRequest)); 
		String datasetId = httpReq.getParameter("datasetId");
		
		//ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY); 
		//User user = themeDisplay.getUser();
		
		//********************** Action **********************
		 
		try {
			
			//init CKAN client
			client = new CKANClient();
			System.out.println(datasetId);
			 if(datasetId != null)
			 {
				 JSONObject datasetDetails = new JSONObject();			
				 datasetDetails = client.getDataset(datasetId);	
				 
				 String mode = httpReq.getParameter("mode");
				 if(mode != null && mode.equals("edit"))
				 {
					 /*String dataCreator = datasetDetails.getString("creator_user_id");
					 ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY); 
					  User user = themeDisplay.getUser();
					  String currentUserID = "" + user.getUserId();
					  
					  PermissionChecker permissionChecker = PermissionThreadLocal.getPermissionChecker();
					  
					  if(currentUserID.equals(dataCreator) || permissionChecker.isOmniadmin())
					  {*/
						  renderRequest.setAttribute("mode","edit");
						  JSONArray licenses = new JSONArray();
						  licenses = client.getLicenses();							
							//return list to jsp
							renderRequest.setAttribute("licenses", licenses);
					 /* }
					  else// user not owner of dataset
					  {
						  renderRequest.setAttribute("datasetDetails",null );
						  renderRequest.setAttribute("licenseString", "");
					  }*/
				 }
				 
				 //System.out.println(datasetDetails);
				 renderRequest.setAttribute("datasetDetails",datasetDetails );
			 }	
			 else{
				 renderRequest.setAttribute("datasetDetails",null );
				 renderRequest.setAttribute("licenseString", "");
			 }
			 super.doView(renderRequest, renderResponse);

		} catch (Exception e) {
			
			e.printStackTrace();
		} 		 
		 
	 }//end doView()
	
	@ProcessAction(name = "editDocument")
	public void editDocument(ActionRequest actionRequest,ActionResponse actionResponse) throws IOException,
										PortletException, PortalException, SystemException{ 
		
		//********************** Variables **********************
		
		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY); 
		User user = themeDisplay.getUser();		
					
		HttpServletRequest httpReq = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(actionRequest)); 
		//String datasetId = ParamUtil.getString(actionRequest, "datasetId");
		
		String mimeType;
		
		UploadPortletRequest uploadPortletRequest = PortalUtil.getUploadPortletRequest(actionRequest); 	
				
		CKANClient client;
		
		JSONObject datasetData = new JSONObject(), resource = new JSONObject();
		JSONObject datasetDetails = new JSONObject();   
		
		JSONArray resources = new JSONArray(), tags = new JSONArray();
		
		String[] tagsStr;
		
		
		
		
		//********************** Action **********************
	
		try{
			actionRequest.setCharacterEncoding("UTF-8");

//			logger.info("uploading document....");
			
			//get the file uploaded		
			
			tagsStr = ParamUtil.getString(uploadPortletRequest,"tags").split(",");
			
			logger.info("title given " + ParamUtil.getString(uploadPortletRequest,"dstitle"));
			
			
			datasetDetails = new JSONObject(URLDecoder.decode(ParamUtil.getString(uploadPortletRequest,"dataset"),"UTF-8"));
			
			logger.info("from dataset title " + datasetDetails.getString("title"));
			/* Give the resource the same name as the package */
			
		
			//resource.put("format", ParamUtil.getString(actionRequest,"format"));			
			//resources.put(resource);
			
			for(String tag : tagsStr){
				
				if(!tag.isEmpty()){
					tags.put(new JSONObject().put("name", tag));
				}
			}
			
			//datasetData.put("id", "1c49fa2c-c61f-4a1b-aef6-a450935e7eb5");
			datasetDetails.put("title", ParamUtil.getString(uploadPortletRequest, "dstitle"));			
			datasetDetails.put("notes", ParamUtil.getString(uploadPortletRequest, "notes"));
			datasetDetails.put("url", ParamUtil.getString(uploadPortletRequest, "source"));
			datasetDetails.put("version", ParamUtil.getString(uploadPortletRequest, "version"));
			datasetDetails.put("author", ParamUtil.getString(uploadPortletRequest, "author"));
			
			datasetDetails.put("license_id", ParamUtil.getString(uploadPortletRequest, "license"));
			
			String datasetId = ParamUtil.getString(uploadPortletRequest, "id");
			System.out.println("dataset id: "+datasetId);
			
			PortletURL actionUrl =  PortletURLFactoryUtil.create(actionRequest, themeDisplay.getPortletDisplay().getId(), themeDisplay.getPlid(), PortletRequest.ACTION_PHASE);
			System.out.println("url: "+actionUrl);
			
			String redirectUrl = actionUrl.toString().replaceAll("&mode=edit", "");
			
			//datasetData.put("resources", resources);
						
			if(tags.length()>0){
				datasetDetails.put("tags", tags);
			}
			
			//init CKAN client
			client = new CKANClient();
			
			//create dataset
			client.editOrganizationDataset(user.getUserId(), datasetDetails);
			
			System.out.println("updated dataset");
			actionResponse.sendRedirect(redirectUrl+"&datasetId="+datasetId);
			//redirect
			//actionResponse.sendRedirect("web/guest/dataset-details");
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}//end editDocument()

}
