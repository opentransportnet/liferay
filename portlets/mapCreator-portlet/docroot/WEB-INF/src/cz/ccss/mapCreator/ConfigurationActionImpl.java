package cz.ccss.mapCreator;

import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portlet.PortletPreferencesFactoryUtil;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

public class ConfigurationActionImpl implements ConfigurationAction {
    public void processAction(PortletConfig portletConfig,
            ActionRequest actionRequest, ActionResponse actionResponse)
            throws Exception {

        String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

        if (!cmd.equals(Constants.UPDATE)) {
            return;
        }

        String caturl = ParamUtil.getString(actionRequest, "caturl");
        String centerX = ParamUtil.getString(actionRequest, "centerX");
        String centerY = ParamUtil.getString(actionRequest, "centerY");
        String zoom = ParamUtil.getString(actionRequest, "zoom");
        String dsPaging = ParamUtil.getString(actionRequest, "dsPaging");

        String portletResource = ParamUtil.getString(actionRequest,
                "portletResource");
        PortletPreferences preferences = PortletPreferencesFactoryUtil
                .getPortletSetup(actionRequest, portletResource);

        preferences.setValue("caturl", caturl);
        preferences.setValue("centerX", centerX);
        preferences.setValue("centerY", centerY);
        preferences.setValue("zoom", zoom);
        preferences.setValue("dsPaging", dsPaging);

        preferences.store();

        SessionMessages.add(actionRequest, portletConfig.getPortletName()
                + ".doConfigure");
    }

    public String render(PortletConfig portletConfig,
            RenderRequest renderRequest, RenderResponse renderResponse)
            throws Exception {

        return "/html/config.jsp";
    }
}
