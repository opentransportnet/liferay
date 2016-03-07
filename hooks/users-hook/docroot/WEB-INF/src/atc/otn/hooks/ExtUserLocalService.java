package atc.otn.hooks;

import atc.otn.ckanclient.CKANClient;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceWrapper;
import com.liferay.portal.service.UserLocalService;

public class ExtUserLocalService extends UserLocalServiceWrapper {
	/*
	 * (non-Java-doc)
	 * 
	 * @see
	 * com.liferay.portal.service.UserLocalServiceWrapper#UserLocalServiceWrapper
	 * (UserLocalService userLocalService)
	 */
	public ExtUserLocalService(UserLocalService userLocalService) {

		super(userLocalService);

	}

	public User addUser(long creatorUserId, long companyId,
			boolean autoPassword, String password1, String password2,
			boolean autoScreenName, String screenName, String emailAddress,
			long facebookId, String openId, java.util.Locale locale,
			String firstName, String middleName, String lastName, int prefixId,
			int suffixId, boolean male, int birthdayMonth, int birthdayDay,
			int birthdayYear, String jobTitle, long[] groupIds,
			long[] organizationIds, long[] roleIds, long[] userGroupIds,
			boolean sendEmail, ServiceContext serviceContext)
			throws PortalException, SystemException {

		User user = super.addUser(creatorUserId, companyId, autoPassword,
				password1, password2, autoScreenName, screenName, emailAddress,
				facebookId, openId, locale, firstName, middleName, lastName,
				prefixId, suffixId, male, birthdayMonth, birthdayDay,
				birthdayYear, jobTitle, groupIds, organizationIds, roleIds,
				userGroupIds, sendEmail, serviceContext);

		System.out.println("inside the hook.. adding user:"
				+ user.getFirstName() + ", pass:" + user.getPassword()
				+ ", id:" + user.getUserId());

		// CKAN Client call for adding user
				CKANClient ckan= new CKANClient();
				String apikey = "";
				try {
					apikey = ckan.createCKANUser(user.getUserId(), user.getScreenName(),
							user.getEmailAddress(), "123456", user.getFirstName()
									+ " " + user.getLastName());			
					ckan.createCKANOrg(user.getUserId());
					
					if(!apikey.isEmpty())
					{	
						ckan.storeUserApikey(user.getUserId(),apikey);
					}
				  
				} catch (Exception ex) {
					System.out.println("error creating ckan user and org " + ex);
				}

		return user;

	}// end addUser()

	public User addUserWithWorkflow(long creatorUserId, long companyId,
			boolean autoPassword, String password1, java.lang.String password2,
			boolean autoScreenName, String screenName, String emailAddress,
			long facebookId, String openId, java.util.Locale locale,
			String firstName, String middleName, String lastName, int prefixId,
			int suffixId, boolean male, int birthdayMonth, int birthdayDay,
			int birthdayYear, String jobTitle, long[] groupIds,
			long[] organizationIds, long[] roleIds, long[] userGroupIds,
			boolean sendEmail, ServiceContext serviceContext)
			throws PortalException, SystemException {

		User user = super.addUserWithWorkflow(creatorUserId, companyId,
				autoPassword, password1, password2, autoScreenName, screenName,
				emailAddress, facebookId, openId, locale, firstName,
				middleName, lastName, prefixId, suffixId, male, birthdayMonth,
				birthdayDay, birthdayYear, jobTitle, groupIds, organizationIds,
				roleIds, userGroupIds, sendEmail, serviceContext);

		System.out.println("inside the hook.. adding user with workflow:"
				+ user.getFirstName() + ", pass:" + user.getPassword()
				+ ", id:" + user.getUserId());

		// CKAN Client call for adding user
		CKANClient ckan = new CKANClient();
		String apikey = "";
		try {
			apikey = ckan.createCKANUser(user.getUserId(),
					user.getScreenName(), user.getEmailAddress(), "123456",
					user.getFirstName() + " " + user.getLastName());
			ckan.createCKANOrg(user.getUserId());

			if (!apikey.isEmpty()) {
				ckan.storeUserApikey(user.getUserId(), apikey);
			}

		} catch (Exception ex) {
			System.out.println("error creating ckan user and org " + ex);
		}
				
		return user;

	}// end addUserWithWorkflow()

	/**
	 * Completes the user's registration by generating a password and sending
	 * the confirmation email.
	 * 
	 * @param user
	 *            the user
	 * @param serviceContext
	 *            the user's service context. Can set whether a password should
	 *            be generated (with the <code>autoPassword</code> attribute)
	 *            and whether the confirmation email should be sent (with the
	 *            <code>sendEmail</code> attribute) for the user.
	 * @throws PortalException
	 *             if a portal exception occurred
	 * @throws SystemException
	 *             if a system exception occurred
	 */
	public void completeUserRegistration(User user,
			ServiceContext serviceContext) throws PortalException,
			SystemException {

		super.completeUserRegistration(user, serviceContext);

		// CKAN Client call for updating user password
		System.out.println("inside the hook.. setting password:"
				+ user.getFirstName() + ", pass:" + user.getPassword()
				+ ", id:" + user.getUserId());	
		

	}// end completeUserRegistration()

	public User updatePassword(long userId, String password1, String password2,
			boolean passwordReset) throws PortalException, SystemException {

		User user = super.updatePassword(userId, password1, password2,
				passwordReset);

		System.out.println("inside the hook.. updating password:"
				+ user.getFirstName() + ", pass:" + user.getPassword()
				+ ", id:" + user.getUserId() + ", pass:" + password1);

		// CKAN Client call for adding user
		CKANClient ckan= new CKANClient();
		try {
			ckan.updateCKANUser(user.getUserId(), user.getScreenName(),user.getEmailAddress(), password1);			
		} catch (Exception ex) {
			System.out.println("error updating ckan user password");
		}

		return user;

	}// end updatePassword()

}// end class