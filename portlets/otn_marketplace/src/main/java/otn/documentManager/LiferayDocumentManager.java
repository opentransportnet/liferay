package otn.documentManager;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.model.Group;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portlet.documentlibrary.NoSuchFolderException;
import com.liferay.portlet.documentlibrary.model.DLFolderConstants;
import com.liferay.portlet.documentlibrary.service.DLAppLocalServiceUtil;

public class LiferayDocumentManager {

	public LiferayDocumentManager() {
	}

	private Folder getOrCreateFolder(final ServiceContext serviceContext,
		final long userId, final Group group, String folderName)
			throws PortalException, SystemException {
		final long parentFolderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;
		final long repositoryId = group.getGroupId();

		try {
			final Folder prev = DLAppLocalServiceUtil.getFolder(repositoryId,
					parentFolderId, folderName);
			return prev;
		} catch (final NoSuchFolderException e) {
			final Folder newFolder = DLAppLocalServiceUtil.addFolder(userId,
					repositoryId, parentFolderId, folderName,
					"My cool new folder", serviceContext);
			return newFolder;
		}
	}
	
//	private Folder getExistingOrCreateFolderFromDLApp(
//			  final ServiceContext serviceContext,
//			  final long userId, final Group group,
//			  final String folderName)
//			  throws PortalException, SystemException {
//			 
//			 final long parentFolderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;
//			 final long repositoryId = group.getGroupId();
//			 
//			 try {
//			  final Folder alreadyExistingFolder = DLAppLocalServiceUtil.getFolder(
//			   repositoryId, parentFolderId, folderName);
//			  return alreadyExistingFolder ;
//			 } catch (final NoSuchFolderException e) {
//			  final Folder newFolder = DLAppLocalServiceUtil.addFolder(userId,
//			   repositoryId, parentFolderId, articlesImagesPath,
//			   "Description of my new folder", serviceContext);
//			  return newFolder;
//			 }
//			}

}
