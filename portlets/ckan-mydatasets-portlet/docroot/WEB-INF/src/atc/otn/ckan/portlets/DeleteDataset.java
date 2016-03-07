package atc.otn.ckan.portlets;

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

import org.json.JSONArray;
import org.json.JSONObject;

import atc.otn.ckan.client.CKANClient;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.PortletURLFactoryUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

public class DeleteDataset extends MVCPortlet {
	
	public void doView(RenderRequest renderRequest,RenderResponse renderResponse){
		 
		//********************** Variables **********************
				 			
		CKANClient client;	
		 

		HttpServletRequest httpReq = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(renderRequest)); 
		String datasetId = httpReq.getParameter("datasetId");
		
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
					 String dataCreator = datasetDetails.getString("creator_user_id");
					 ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY); 
					  User user = themeDisplay.getUser();
					  String currentUserID = "" + user.getUserId();
					  if(currentUserID.equals(dataCreator))
					  {
						  renderRequest.setAttribute("mode","edit");
						  JSONArray licenses = new JSONArray();
						  licenses = client.getLicenses();							
							//return list to jsp
							renderRequest.setAttribute("licenses", licenses);
					  }
					  else// user not owner of dataset
					  {
						  renderRequest.setAttribute("datasetDetails",null );
					  }
				 }
				 
				 //System.out.println(datasetDetails);
				 renderRequest.setAttribute("datasetDetails",datasetDetails );
				 				
			 }	
			 else{
				 renderRequest.setAttribute("datasetDetails",null );
			 }
			 super.doView(renderRequest, renderResponse);

		} catch (Exception e) {
			
			e.printStackTrace();
		} 		 
		 
	 }//end doView()
	
	@ProcessAction(name = "deleteDocument")
	public void deleteDocument(ActionRequest actionRequest,ActionResponse actionResponse) throws IOException,
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
						
		//********************** Action **********************
	
		try{
			PortletURL actionUrl =  PortletURLFactoryUtil.create(actionRequest, themeDisplay.getPortletDisplay().getId(), themeDisplay.getPlid(), PortletRequest.ACTION_PHASE);
//			System.out.println("url: "+actionUrl);
			
			datasetDetails = new JSONObject(URLDecoder.decode(ParamUtil.getString(uploadPortletRequest,"dataset"),"UTF-8"));
			
			datasetDetails.put("title", ParamUtil.getString(uploadPortletRequest, "dstitle"));			
			
			String datasetId = ParamUtil.getString(uploadPortletRequest, "id");
			System.out.println("dataset id: "+datasetId);			
			
			String redirectUrl = actionUrl.toString().substring(0, actionUrl.toString().indexOf("delete-dataset"));
//			System.out.println("url redirect: "+redirectUrl);
						
			//init CKAN client
			client = new CKANClient();
			
			//create dataset
			client.deleteUserDataset(user.getUserId(), datasetId, datasetDetails);
			
			System.out.println("dataset deleted");
			actionResponse.sendRedirect(redirectUrl+"my-datasets");
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}//end editDocument()

}
