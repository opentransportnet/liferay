package atc.otn.liferay.DM;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.security.permission.ActionKeys;
import com.liferay.portal.service.ResourcePermissionServiceUtil;
import com.liferay.portal.service.RoleServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.NoSuchFolderException;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.model.DLFolderConstants;
import com.liferay.portlet.documentlibrary.service.DLAppServiceUtil;
import com.liferay.util.portlet.PortletProps;

public class DocumentManager {

	private static String ROOT_FOLDER_NAME = PortletProps.get("fileupload.folder.name"); 
	private static String ROOT_FOLDER_DESCRIPTION = PortletProps.get("fileupload.folder.description"); 
	private static long PARENT_FOLDER_ID = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;
	
	 private static final Log logger = LogFactory.getLog(DocumentManager.class);

	/**
	 * 
	 * @param actionRequest
	 * @param themeDisplay
	 * @return
	 */
	public Folder createUserFolder(ActionRequest actionRequest,ThemeDisplay themeDisplay) { 
		
		//********************** Variables **********************

		Folder parentFolder;
		
		Folder folder = null; 

		long repositoryId;
		
		ServiceContext serviceContext;
		
		User user;
		
		//********************** Action **********************
			
		try { 
						
			//get the repository id
			repositoryId = themeDisplay.getScopeGroupId();	

			//get the logged in user
			user = themeDisplay.getUser();

			//get the CKAN folder
			parentFolder = DLAppServiceUtil.getFolder(repositoryId, PARENT_FOLDER_ID, ROOT_FOLDER_NAME);
			
			try{
				
				//get the folder's user
				folder = DLAppServiceUtil.getFolder(repositoryId, parentFolder.getFolderId(), ""+user.getUserId()); 

			}catch(NoSuchFolderException fe){
				
				fe.printStackTrace();
				
				serviceContext = ServiceContextFactory.getInstance(DLFolder.class.getName(), actionRequest); 

				folder = DLAppServiceUtil.addFolder(repositoryId,parentFolder.getFolderId(), ""+user.getUserId() ,ROOT_FOLDER_DESCRIPTION, serviceContext); 
				
				/* TODO: not working */
				Role guest = RoleServiceUtil.getRole(folder.getCompanyId(), "Guest");
				Map <Long,String[]> perms = new HashMap<Long,String[]>();
				perms.put((long) 10261, new String[] { ActionKeys.VIEW });
				System.out.println(guest.getRoleId()+"");
				ResourcePermissionServiceUtil.setIndividualResourcePermissions(folder.getGroupId(), folder.getCompanyId(), folder.getClass().getName(), folder.getPrimaryKey()+"", perms);
			}
						
		} catch (Exception e) { 
	
			e.printStackTrace(); 
	
		} 
		
		return folder; 

	}//end createFolder() 
	
	/**
	 * 
	 * @param themeDisplay
	 * @param actionRequest
	 * @param folder
	 * @param file
	 * @param fileName
	 * @param mimeType
	 */
	public String uploadFile(ThemeDisplay themeDisplay,ActionRequest actionRequest, Folder folder, 
						   File file, String fileName, String mimeType) { 
		
		//********************** Variables **********************
		
		String title, description;	 
		
		InputStream is;
		
		ServiceContext serviceContext;
		
		long repositoryId;
		
		FileEntry fileEntry;
		
		String fileURL = "";
		
		HttpServletRequest httpReq;
		
		//********************** Action **********************
	
		try { 
			
			repositoryId = themeDisplay.getScopeGroupId(); 

			//get hppt request in order to access the full url of the portlet's page
			httpReq = PortalUtil.getHttpServletRequest(actionRequest);

			//get the file's title given by user
			title = ParamUtil.getString(PortalUtil.getUploadPortletRequest(actionRequest), "dname");
			
			logger.debug("title:"+title);
			
			//get the file's description given by user
			description = ParamUtil.getString(PortalUtil.getUploadPortletRequest(actionRequest), "description");
						
			//get the service context
			serviceContext = ServiceContextFactory.getInstance(DLFileEntry.class.getName(), actionRequest); 
			
			try{
			
				fileEntry = DLAppServiceUtil.getFileEntry(repositoryId, folder.getFolderId(), fileName);
			
				logger.debug("fileEntry:"+fileEntry);
				
			}catch(Exception e){
				
				e.printStackTrace();
				
				//open stream with the file
				is = new FileInputStream( file ); 
				
				long now = new Date().getTime();
				
				//add file to the liferay's document library
				fileEntry  = DLAppServiceUtil.addFileEntry(repositoryId, folder.getFolderId(), now + fileName, mimeType, now + fileName, description, "", is, file.getTotalSpace(), serviceContext); 
				
				//close stream
				is.close();

			}
									
			//build the liferay file url
			fileURL = PortalUtil.getPortalURL(actionRequest)+"/documents/" + fileEntry.getRepositoryId() + "/"
						+ fileEntry.getFolderId() + "/" + fileEntry.getTitle()
						+ "/" + fileEntry.getUuid();
			
			logger.debug("fileURL:"+fileURL);
			logger.debug("host:"+PortalUtil.getHost(actionRequest));
		
			
		} catch (Exception e) { 
			
			logger.error(e.getMessage());
			
		}
		
		return fileURL;
		
	}//end uploadFile() 
	
}//end class
