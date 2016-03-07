package atc.otn.ckan.portlets;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.ProcessAction;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import atc.otn.ckan.client.CKANClient;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.User;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.PortletURLFactoryUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class AllDatasets
 */
public class AllDatasets extends MVCPortlet {
	public void doView(RenderRequest renderRequest,
			RenderResponse renderResponse) {

		// ********************** Variables **********************

		// json array holding the user receivers with their roles and units
		JSONArray datasets = new JSONArray();

		CKANClient client;
		String orgId;

		ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest
				.getAttribute(WebKeys.THEME_DISPLAY);
		User user = themeDisplay.getUser();

		// ********************** Action **********************

		try {

			// init CKAN client
			client = new CKANClient();

			datasets = client.getDatasets();
			renderRequest.setAttribute("datasets", datasets);			
			
			if(PortalUtil.getCurrentURL(renderRequest).contains("all-datasets"))
			{
				renderRequest.setAttribute("mode", "full");
			}
			else{
				renderRequest.setAttribute("mode", "mini");
			}

			super.doView(renderRequest, renderResponse);

		} catch (Exception e) {

			e.printStackTrace();
		}

	}// end doView()

	@ProcessAction(name = "viewDatasetUrl")
	public void viewDatasetUrl(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException,
			PortletException, PortalException, SystemException {

		// ********************** Variables **********************

		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest
				.getAttribute(WebKeys.THEME_DISPLAY);
		User user = themeDisplay.getUser();

		String datasetId = ParamUtil.getString(actionRequest, "datasetId");

		JSONObject datasetDetails = new JSONObject();
		CKANClient client;

		try {
			client = new CKANClient();

			Layout layout = LayoutLocalServiceUtil.getFriendlyURLLayout(
					themeDisplay.getLayout().getGroupId(), false,
					"/my-datasets");

			PortletURL redirectURL = PortletURLFactoryUtil.create(
					PortalUtil.getHttpServletRequest(actionRequest),
					"mydatasets_WAR_ckanmydatasetsportlet", layout.getPlid(),
					PortletRequest.RENDER_PHASE);

			datasetDetails = client.getDataset(datasetId);
			redirectURL.setParameter("datasetId", datasetId);
			actionRequest.setAttribute("datasetDetails", datasetDetails);
			actionResponse.sendRedirect(redirectURL.toString());
		} catch (Exception e) {

			e.printStackTrace();
		}

	}// end viewDatasetUrl()

}
