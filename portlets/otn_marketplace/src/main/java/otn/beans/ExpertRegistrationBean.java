package otn.beans;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.mail.internet.InternetAddress;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;

import org.primefaces.event.FileUploadEvent;

import otn.authenticator.LiferayAuthenticator;
import otn.interfaces.IAuthenticator;

import com.liferay.faces.portal.context.LiferayFacesContext;
import com.liferay.faces.util.portal.WebKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.DuplicateFileException;
import com.liferay.portlet.documentlibrary.model.DLFolderConstants;
import com.liferay.portlet.documentlibrary.service.DLAppServiceUtil;

@ManagedBean(name = "expertRegistrationBean")
@ViewScoped
public class ExpertRegistrationBean implements Serializable{

	
	private static final long serialVersionUID = 1L;

	// ************************* Variables **************************
	private String name;

	private String company;
	
	private String profession;
	
	private String email;
	
	private String telephone;
	
	private String pictureUrl;
	
	private String fileurl;
		
	private String receiver;
	
	private String folderId;
	
	private String redirectUrl;	
	
	/** The user authenticator object. */
	private IAuthenticator authenticator;
	
	/* email body */
	private String text = "";
	
	private String fileName;
	private String destination = "";
	private long fileSize;
	private FileEntry fileEntry;
	
	FacesContext context = FacesContext.getCurrentInstance();
	
	PortletRequest portletRequest = (PortletRequest) context.getExternalContext().getRequest();
	ThemeDisplay themeDisplay = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
	private String url = "";
	private String realUrl = "";
	private String tempUrl = "";

	// ************************* Functions **************************

	public ExpertRegistrationBean() {
				
		authenticator = new LiferayAuthenticator();
		
		Properties p = new Properties();
		InputStream in = null;
		
		in = this.getClass().getResourceAsStream("/settings.properties");
		try {
			p.load(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
				receiver = p.getProperty("emailForRegister");
								
				folderId = p.getProperty("folderId");
				
				redirectUrl = authenticator.getSiteUrl();
				
				try {
					url = PortalUtil.getLayoutURL(themeDisplay.getLayout(),themeDisplay);
					tempUrl = redirectUrl.replaceAll("/web/guest", url);
					realUrl = tempUrl.replaceAll("/become-an-otn-expert", "");
					
				} catch (PortalException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (SystemException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}		

	}// end constructor

	public void uploadPicture(FileUploadEvent event) throws IOException {

		System.out.println("upload was called...");

		String fileType = (String) event.getComponent().getAttributes()
				.get("fileType");

		System.out.println("fileType = " + fileType);

		FacesMessage msg = new FacesMessage("Success! ", event.getFile()
				.getFileName() + " is uploaded.");

		FacesContext.getCurrentInstance().addMessage(null, msg);

		PortletRequest req = (PortletRequest) FacesContext.getCurrentInstance()
				.getExternalContext().getRequest();
		// String path=req.getContextPath();

		PortletContext pc = req.getPortletSession().getPortletContext();
		System.out.println("realPath = " + pc.getRealPath("/"));
		// C:\Users\SVasileiou\Documents\liferay-portal-6.1.1-ce-ga2\tomcat-7.0.27\webapps\configBuilder-portlet

		destination = pc.getRealPath("/");
		System.out.println("destination = " + destination);

		fileName = event.getFile().getFileName().toString();
		System.out.println("filename = " + fileName);

		copyPicture(fileName, event.getFile().getInputstream(), fileType);
	}

	public void copyPicture(String fileName, InputStream in, String imageType) {

		try {
			System.out.println("destination = " + destination);

			File file = new File(destination + fileName);

			double fileInBytes = file.length();
			fileSize = (long) (fileInBytes / 1024);

			// write the inputStream to a FileOutputStream
			OutputStream out = new FileOutputStream(file);

			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = in.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}

			FacesContext context = FacesContext.getCurrentInstance();

			ThemeDisplay themeDisplay = (ThemeDisplay) context
					.getExternalContext().getRequestMap()
					.get(WebKeys.THEME_DISPLAY);

			// long folderId = 11801;
			long folderIdInput = Long.parseLong(folderId);
			long repositoryId = themeDisplay.getScopeGroupId();
			long parentFolderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;

			String sourceFileName = fileName;

			System.out.println("---------------------");

			System.out.println("repositoryId = " + repositoryId
					+ " parentFolderId = " + parentFolderId);
			System.out.println("---------------------");

			// mimetype
			String mimeType = "";
			// for title
			String title = fileName;
			// for description
			String description = fileName;
			// changeLog
			String changeLog = "";

			
			// service Context Object
			String uuid = UUID.randomUUID().toString();

			Date date = new Date();
			ServiceContext serviceContext = new ServiceContext();
			serviceContext.setUuid(uuid);
			serviceContext.setCreateDate(date);
			serviceContext.setModifiedDate(date);

			try {
				fileEntry = DLAppServiceUtil.addFileEntry(repositoryId,
						folderIdInput, sourceFileName, mimeType, title, description,
						changeLog, in, fileSize, serviceContext);

				System.out.println("RepositoryId = "
						+ fileEntry.getRepositoryId());
				System.out.println("FolderId = " + fileEntry.getFolderId());
				System.out.println("Title = " + fileEntry.getTitle());
				System.out.println("getUuid = " + fileEntry.getUuid());

				// the path used by DL is
				// /documents/repositoryId/folderID/fileName/fileEntryID
				String path = "/documents/" + fileEntry.getRepositoryId() + "/"
						+ fileEntry.getFolderId() + "/" + fileEntry.getTitle()
						+ "/" + fileEntry.getUuid();

				System.out.println("path = " + path);

				String siteLoggedIn = authenticator.getSiteUrl();
				String site = siteLoggedIn.replaceAll("/web/guest", "");
				
				setPictureUrl(site+path);
				

			} catch (DuplicateFileException e) {
				FacesMessage msg = new FacesMessage(
						"This picture already exists. Please upload a new one.");
				FacesContext.getCurrentInstance().addMessage(null, msg);

			} catch (PortalException e) {
				System.out.println("----------------portalexception");
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SystemException e) {
				System.out.println("----------------SystemException");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			in.close();
			out.flush();
			out.close();

			// System.out.println("New file created!");
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void uploadPicture(){
		System.out.println("upload pic was called");		
	}
	
	public void sendEmail() {

		System.out.println("sendEmail was called");
		
		System.out.println("receiver = " + receiver);

		try {
		
		com.liferay.portal.kernel.mail.MailMessage message = new com.liferay.portal.kernel.mail.MailMessage();
		InternetAddress to = new InternetAddress();
		to.setAddress(receiver);

		InternetAddress from = new InternetAddress();
		from.setAddress(email);

		message.setFrom(from);
		message.setTo(to);
		message.setSubject("OTN Expert Registration\n\n");
		
		setText("Please add as an OTN expert: \n\n Name: "+name+"\n Company: " +company+ 
				"\n Profession: " +profession+ "\n E-mail: " +
				" "+email+ "\n Picture url: " +getPictureUrl()+ 
				"\n File Url: " +getFileurl());
	
		
		String bodyMsg = "You have received a message from " + name + " (" + email + ") \n" + getText();
		
		
		message.setBody(bodyMsg);
		
		System.out.println("email message: " + bodyMsg);
				
		com.liferay.mail.service.MailServiceUtil.sendEmail(message);
		
		FacesMessage msg = new FacesMessage(
				"Your registration as an OTN expert is being completed.",
				" Please wait confirmation from administrator");
		FacesContext.getCurrentInstance().addMessage(null, msg);
		
		} catch (Exception e) {
			FacesMessage msg = new FacesMessage(
					"Something went wrong."," Please try again later");
			FacesContext.getCurrentInstance().addMessage(null, msg);
			System.out.println(e);
		}
		
		}
	
	public void upload(FileUploadEvent event) throws IOException {

		System.out.println("upload was called...");

		String imageType = (String) event.getComponent().getAttributes()
				.get("imageType");

		System.out.println("imageType = " + imageType);

		FacesMessage msg = new FacesMessage("Success! ", event.getFile()
				.getFileName() + " is uploaded.");

		FacesContext.getCurrentInstance().addMessage(null, msg);

		PortletRequest req = (PortletRequest) FacesContext.getCurrentInstance()
				.getExternalContext().getRequest();
		// String path=req.getContextPath();

		PortletContext pc = req.getPortletSession().getPortletContext();
		System.out.println("realPath = " + pc.getRealPath("/"));
		// C:\Users\SVasileiou\Documents\liferay-portal-6.1.1-ce-ga2\tomcat-7.0.27\webapps\configBuilder-portlet

		destination = pc.getRealPath("/");
		System.out.println("destination = " + destination);

		fileName = event.getFile().getFileName().toString();
		System.out.println("filename = " + fileName);

		copyFile(fileName, event.getFile().getInputstream(), imageType);
	}

	public void copyFile(String fileName, InputStream in, String imageType) {

		try {
			System.out.println("destination = " + destination);

			File file = new File(destination + fileName);

			double fileInBytes = file.length();
			fileSize = (long) (fileInBytes / 1024);

			// write the inputStream to a FileOutputStream
			OutputStream out = new FileOutputStream(file);

			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = in.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}

			FacesContext context = FacesContext.getCurrentInstance();

			ThemeDisplay themeDisplay = (ThemeDisplay) context
					.getExternalContext().getRequestMap()
					.get(WebKeys.THEME_DISPLAY);

			// long folderId = 11801;
			long folderIdInput = Long.parseLong(folderId);
			long repositoryId = themeDisplay.getScopeGroupId();
			long parentFolderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;

			String sourceFileName = fileName;

			System.out.println("---------------------");

			System.out.println("repositoryId = " + repositoryId
					+ " parentFolderId = " + parentFolderId);
			System.out.println("---------------------");

			// mimetype
			String mimeType = "";
			// for title
			String title = fileName;
			// for description
			String description = fileName;
			// changeLog
			String changeLog = "";

			// service Context Object
			String uuid = UUID.randomUUID().toString();

			Date date = new Date();
			ServiceContext serviceContext = new ServiceContext();
			serviceContext.setUuid(uuid);
			serviceContext.setCreateDate(date);
			serviceContext.setModifiedDate(date);

			try {
				fileEntry = DLAppServiceUtil.addFileEntry(repositoryId,
						folderIdInput, sourceFileName, mimeType, title, description,
						changeLog, in, fileSize, serviceContext);

				System.out.println("RepositoryId = "
						+ fileEntry.getRepositoryId());
				System.out.println("FolderId = " + fileEntry.getFolderId());
				System.out.println("Title = " + fileEntry.getTitle());
				System.out.println("getUuid = " + fileEntry.getUuid());

				// the path used by DL is
				// /documents/repositoryId/folderID/fileName/fileEntryID
				String path = "/documents/" + fileEntry.getRepositoryId() + "/"
						+ fileEntry.getFolderId() + "/" + fileEntry.getTitle()
						+ "/" + fileEntry.getUuid();

				System.out.println("path = " + path);

				/**/
				
				String siteLoggedIn = authenticator.getSiteUrl();
				String site = siteLoggedIn.replaceAll("/web/guest", "");
				
				setFileurl(site+path);
				

			} catch (DuplicateFileException e) {
				FacesMessage msg = new FacesMessage(
						"This file already exists. Please upload a new one.");
				FacesContext.getCurrentInstance().addMessage(null, msg);

			} catch (PortalException e) {
				System.out.println("----------------portalexception");
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SystemException e) {
				System.out.println("----------------SystemException");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			in.close();
			out.flush();
			out.close();

			// System.out.println("New file created!");
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public void experts() {
		LiferayFacesContext portletFacesContext = LiferayFacesContext
				.getInstance();
		
		try {
			
			if (url.contains("web/antwerp") || url.contains("web/issy") ||
					url.contains("web/birmingham") || url.contains("web/liberec")){
				portletFacesContext.getExternalContext().redirect(
						realUrl + "/business-support?");
			}else {
				portletFacesContext.getExternalContext().redirect(
						redirectUrl + "/business-support");
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	// ************************ Getters-Setters ****************************
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public String getPictureUrl() {
		return pictureUrl;
	}

	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}

	public String getFileurl() {
		return fileurl;
	}

	public void setFileurl(String fileurl) {
		this.fileurl = fileurl;
	}

}// end class
