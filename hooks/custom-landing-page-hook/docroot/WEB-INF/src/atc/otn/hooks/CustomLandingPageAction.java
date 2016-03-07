package atc.otn.hooks;

import java.util.Arrays;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.struts.LastPath;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.security.permission.PermissionThreadLocal;
import com.liferay.portal.util.PortalUtil;

public class CustomLandingPageAction extends Action {
	private static Log _log = LogFactoryUtil.getLog(CustomLandingPageAction.class);
	 
	public void run(HttpServletRequest request, HttpServletResponse response) {
	 HttpSession session = request.getSession();
	 String path = null;
	 User user = null;
	 
	 if( session != null ){
	 try {
	     user = PortalUtil.getUser(request);
	    	     
	     PermissionChecker checker = PermissionCheckerFactoryUtil.create(user);
	     PermissionThreadLocal.setPermissionChecker(checker);
	     
	     path = user.getScreenName();
	         _log.debug("path = " + path);
	     } catch (PortalException e) {
	         _log.error("Error: " + e.getMessage());
	     } catch (SystemException e) {
	         _log.error("Error: " + e.getMessage());
	     } catch (Exception e) {
			e.printStackTrace();
	     }
	 }
	 
     String cityAttribute = Arrays.deepToString((Object[]) user.getExpandoBridge().getAttribute("City Hub"));
	 System.out.println("----------------------------------------------"); 
     
	 if (Validator.isNotNull(path) && user != null){    //login user with its default page
	     
		 if (cityAttribute.equals("[Antwerp]")){
			 LastPath lastPath = new LastPath(StringPool.BLANK, "/web/antwerp", new HashMap<String, String[]>());
		     _log.info("Use Default User Path: " + lastPath.toString());
		     session.setAttribute(WebKeys.LAST_PATH, lastPath);
		 }else if (cityAttribute.equals("[Birmingham]")){
			 LastPath lastPath = new LastPath(StringPool.BLANK, "/web/birmingham", new HashMap<String, String[]>());
		     _log.info("Use Default User Path: " + lastPath.toString());
		     session.setAttribute(WebKeys.LAST_PATH, lastPath);
		 }else if (cityAttribute.equals("[Issy-Les-Moulineaux]")){
			 LastPath lastPath = new LastPath(StringPool.BLANK, "/web/issy", new HashMap<String, String[]>());
		     _log.info("Use Default User Path: " + lastPath.toString());
		     session.setAttribute(WebKeys.LAST_PATH, lastPath);
		 }else if (cityAttribute.equals("[Liberec]")){
			 LastPath lastPath = new LastPath(StringPool.BLANK, "/web/liberec", new HashMap<String, String[]>());
		     _log.info("Use Default User Path: " + lastPath.toString());
		     session.setAttribute(WebKeys.LAST_PATH, lastPath);
		 }else if (cityAttribute.equals("[Central Hub]")){
			 LastPath lastPath = new LastPath(StringPool.BLANK, "/web/guest", new HashMap<String, String[]>());
		     _log.info("Use Default User Path: " + lastPath.toString());
		     session.setAttribute(WebKeys.LAST_PATH, lastPath);
		 }
	 }
	 else{    //use the default landing page from config.
	     _log.info("Using the default landing page from config");
	     setDefaultLandingPagePath(session);
	 }
	}
	 
	private void setDefaultLandingPagePath(HttpSession session) {
		String path=PropsKeys.DEFAULT_LANDING_PAGE_PATH;
			if (Validator.isNotNull(path)) {
				 LastPath lastPath = new LastPath(StringPool.BLANK, "/web/guest", new HashMap());
				 session.setAttribute(WebKeys.LAST_PATH, lastPath);
				 _log.debug("Use System Config LastPath URL: "+ lastPath.toString());
				 System.out.println("Use System Config LastPath URL: "+ lastPath.toString());
			 }
			else {
				_log.error("Default Landing Page is Null");
			}
		}


}
