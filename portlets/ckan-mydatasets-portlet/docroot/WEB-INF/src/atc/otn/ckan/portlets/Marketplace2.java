package atc.otn.ckan.portlets;


import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import org.json.JSONArray;
import atc.otn.ckan.client.CKANClient;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class Marketplace2
 */
public class Marketplace2 extends MVCPortlet {
	public void doView(RenderRequest renderRequest, RenderResponse renderResponse) {

		// ********************** Variables **********************

		// json array holding all services of marketplace
		JSONArray services = new JSONArray();

		CKANClient client;
		String url = "";

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
				
			renderRequest.setAttribute("city",city );
			
			// init CKAN client
			client = new CKANClient();

			services = client.getServices(cityQuery);
			renderRequest.setAttribute("services", services);			
			

			super.doView(renderRequest, renderResponse);

		} catch (Exception e) {

			e.printStackTrace();
		}

	}// end doView()

}
