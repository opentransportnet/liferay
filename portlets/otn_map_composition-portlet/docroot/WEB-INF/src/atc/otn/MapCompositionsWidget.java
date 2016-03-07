package atc.otn;

import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.Cookie;

import org.json.JSONArray;

import atc.otn.client.ServiceClient;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class MapCompositionsWidget
 */
public class MapCompositionsWidget extends MVCPortlet {
	public void doView(RenderRequest renderRequest,
			RenderResponse renderResponse) {

		// ********************** Variables **********************

		// json array holding the user receivers with their roles and units
		JSONArray mapCompositions = new JSONArray();

		ServiceClient client;
		
		ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY); 
		User user = themeDisplay.getUser();
		
		String jsessionid ="";
		
		for (Cookie c : renderRequest.getCookies()) {
			
            if (c.getName().equalsIgnoreCase("jsessionid")) {
            	System.out.println("Map Compositions cookie " + c.getName()  + " val= "  + c.getValue());
            	jsessionid  = c.getValue();
            }
        }

		// ********************** Action **********************

		try {

			// init CKAN client
			client = new ServiceClient();

			mapCompositions = client.getMapCompositions(jsessionid);			
			renderRequest.setAttribute("mapCompositions", mapCompositions);		
			renderRequest.setAttribute("baseurl", client.getBaseUrl());	
			
			/*if(PortalUtil.getCurrentURL(renderRequest).contains("all-datasets"))
			{
				renderRequest.setAttribute("mode", "full");
			}
			else{
				renderRequest.setAttribute("mode", "mini");
			}*/

			super.doView(renderRequest, renderResponse);

		} catch (Exception e) {

			e.printStackTrace();
		}

	}// end doView()

}
