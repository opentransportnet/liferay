package atc.otn.ckan.portlets;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.ProcessAction;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import atc.otn.ckan.client.CKANClient;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

public class OrganizationDataset extends MVCPortlet {
	 
	 public void doView(RenderRequest renderRequest,RenderResponse renderResponse){
		 
		//********************** Variables **********************
				 		
		//json array holding the user receivers with their roles and units
		JSONArray datasets = new JSONArray();
		
		CKANClient client;
		String orgId;
		
		ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY); 
		User user = themeDisplay.getUser();
		String organizationId;
		InputStream in = null;
		Properties p = new Properties();
		
		HttpServletRequest httpReq = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(renderRequest)); 
		String datasetId = httpReq.getParameter("datasetId");
		
		String url = "";
		
		//********************** Action **********************
		renderRequest.setAttribute("datasetId",datasetId );
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
			
			in = this.getClass().getResourceAsStream("/portlet.properties");
			p.load(in);
			
			organizationId = p.getProperty("CKAN.organizationID");
			
			//init CKAN client
			client = new CKANClient();
			
			//return list to jsp
			
			 if(renderRequest.getParameter("datasetId") != null)
			 {
				 JSONObject datasetDetails = new JSONObject();			
			     CKANClient clienta;
			     String licenseString = "no licence";
				
					try {
						
						clienta = new CKANClient();						
						datasetDetails = client.getDataset(renderRequest.getParameter("datasetId"));				
						
						
						if (datasetDetails.getString("license_title")== null){
							 System.out.println("empty license");
						 }else{
							 licenseString = datasetDetails.getString("license_id");
						 }
						
						renderRequest.setAttribute("datasetDetails",datasetDetails );
						renderRequest.setAttribute("licenseString", licenseString);				
						
					} catch (Exception e) {
						
						e.printStackTrace();
					} 
				 this.include("/html/mydatasets/viewdataset.jsp", renderRequest, renderResponse);
			 }
			 else
			{				
				datasets = client.getuserDatasets(organizationId);
				renderRequest.setAttribute("datasets", datasets);							
				super.doView(renderRequest, renderResponse);
			}			

		} catch (Exception e) {
			
			e.printStackTrace();
		} 
		 
		 
	 }//end doView()
	 
	 @ProcessAction(name = "viewDatasetUrl")
		public void viewDatasetUrl(ActionRequest actionRequest,ActionResponse actionResponse) throws IOException,
											PortletException, PortalException, SystemException{ 
			
			//********************** Variables **********************
			
			ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY); 
			User user = themeDisplay.getUser();
			
			String datasetId = ParamUtil.getString(actionRequest, "datasetId");
			
			JSONObject datasetDetails = new JSONObject();			
			CKANClient client;
			
			try {
				
				client = new CKANClient();	
				
				datasetDetails = client.getDataset(datasetId);				
				actionRequest.setAttribute("datasetDetails",datasetDetails );
				

			} catch (Exception e) {
				
				e.printStackTrace();
			} 
			
		}//end viewDatasetUrl()
}
