package atc.otn.ckan.portlets;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.json.JSONArray;

import atc.otn.ckan.client.CKANClient;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionThreadLocal;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class Apps2
 */
public class Apps2 extends MVCPortlet {
 
	public void doView(RenderRequest renderRequest, RenderResponse renderResponse) {

		// ********************** Variables **********************

		// json array holding all apps
		JSONArray apps = new JSONArray();

		CKANClient client;
		String url = "";
		Boolean isAdmin = false;
		String renderButtons = "style='display:none'";

		ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest
				.getAttribute(WebKeys.THEME_DISPLAY);

		// ********************** Action **********************

		try {
			//get url to find out the hub the user is in
			url = PortalUtil.getLayoutURL(themeDisplay.getLayout(),themeDisplay);

			String city = "";
			String cityQuery = "";
			if (url.contains("/web/antwerp")){
				city = "antwerp";
				cityQuery = "Antwerp";
				
			}else if (url.contains("/web/birmingham")){
				city = "birmingham";
				cityQuery = "Birmingham";
				
			}else if (url.contains("/web/issy")){
				city = "issy";
				cityQuery = "Issy-Les-Moulineaux";
				
			}else if (url.contains("/web/liberec")){
				city = "liberec";
				cityQuery = "Liberec";
				
			}else{
				city = "guest";
				cityQuery = "main";
			}
				
			PermissionChecker permissionChecker = PermissionThreadLocal.getPermissionChecker();
			isAdmin = permissionChecker.isOmniadmin();
			if (isAdmin == true) {
				renderButtons = "style='display:inline-block'";
			}
			
			renderRequest.setAttribute("city",city );
			renderRequest.setAttribute("renderButtons",renderButtons );
			
			// init CKAN client
			client = new CKANClient();

			apps = client.getApps(cityQuery);
			renderRequest.setAttribute("apps", apps);			
			

			super.doView(renderRequest, renderResponse);

		} catch (Exception e) {

			e.printStackTrace();
		}
	}
}
