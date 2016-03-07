package atc.otn.ckan.portlets;

import java.io.IOException;

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

/**
 * Portlet implementation class MyDatasets
 */
public class MyDatasets extends MVCPortlet {
 
	 public void doView(RenderRequest renderRequest,RenderResponse renderResponse){
		 
		//********************** Variables **********************
				 		
		//json array holding the user receivers with their roles and units
		JSONArray datasets = new JSONArray();
		
		CKANClient client;
		String orgId;
		
		ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY); 
		User user = themeDisplay.getUser();
		
		
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
			
			//init CKAN client
			client = new CKANClient();
			
			/*HttpServletRequest request = PortalUtil.getHttpServletRequest((PortletRequest) FacesContext
				      .getCurrentInstance().getExternalContext().getRequest());

				  HttpServletRequest oriRequest = PortalUtil
				    .getOriginalServletRequest(request);

				  value = oriRequest.getParameter("id");
				  ParamUtil.getString(renderRequest,paramName);	 */ 
			
			
			
			//return list to jsp
			
			 if(renderRequest.getParameter("datasetId") != null)
			 {
				 
				 JSONObject datasetDetails = new JSONObject();			
			     CKANClient clienta;
					try {
						
						clienta = new CKANClient();						
						datasetDetails = client.getDataset(renderRequest.getParameter("datasetId"));				
						renderRequest.setAttribute("datasetDetails",datasetDetails );
						//actionResponse.sendRedirect(redirectURL.toString());				
						//renderRequest.setRenderParameter("mvcPath","/html/mydatasets/viewdataset.jsp");
						//actionResponse.sendRedirect("web/guest/my-datasets");
						
					} catch (Exception e) {
						
						e.printStackTrace();
					} 
				 this.include("/html/mydatasets/viewdataset.jsp", renderRequest, renderResponse);
			 }
			//if(PortalUtil.getCurrentURL(renderRequest).contains("my-datasets"))
			 else
			{
				orgId = user.getUserId() + "_org";				
				datasets = client.getuserDatasets(orgId);
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
				
				/*LayoutTypePortlet layoutTypePortlet = LayoutTypePortletFactoryUtil.create(LayoutLocalServiceUtil.getFriendlyURLLayout(10179, false, "/my-datasets"));
				
				PortletURL redirectURL = PortletURLFactoryUtil.create(PortalUtil.getHttpServletRequest(actionRequest),
						 "/my-datasets", Long.parseLong(layoutTypePortlet.getPortletIds().get(0).toString()), PortletRequest.RENDER_PHASE);
				
				
				redirectURL.setParameter("jspPage", "/html/mydatasets/viewdataset.jsp");*/
				datasetDetails = client.getDataset(datasetId);				
				actionRequest.setAttribute("datasetDetails",datasetDetails );
				
				System.out.println(datasetId);
				//actionResponse.sendRedirect(redirectURL.toString());				
				actionResponse.setRenderParameter("mvcPath","/html/mydatasets/viewdataset.jsp");
				//actionResponse.sendRedirect("web/guest/my-datasets");
				
				
				
			} catch (Exception e) {
				
				e.printStackTrace();
			} 
			
		
			
			
		}//end viewDatasetUrl()
}
