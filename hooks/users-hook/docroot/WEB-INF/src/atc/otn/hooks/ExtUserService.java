package atc.otn.hooks;

import atc.otn.ckanclient.CKANClient;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserServiceWrapper;
import com.liferay.portal.service.UserService;

public class ExtUserService extends UserServiceWrapper {
	/*
	 * (non-Java-doc)
	 * 
	 * @see
	 * com.liferay.portal.service.UserServiceWrapper#UserServiceWrapper(UserService
	 * userService)
	 */
	public ExtUserService(UserService userService) {
		super(userService);
	}

	public User addUser(long companyId, boolean autoPassword, String password1,
			String password2, boolean autoScreenName, String screenName,
			String emailAddress, long facebookId, String openId,
			java.util.Locale locale, String firstName, String middleName,
			String lastName, int prefixId, int suffixId, boolean male,
			int birthdayMonth, int birthdayDay, int birthdayYear,
			java.lang.String jobTitle, long[] groupIds, long[] organizationIds,
			long[] roleIds, long[] userGroupIds, boolean sendEmail,
			ServiceContext serviceContext) throws PortalException,
			SystemException {

		User user = super.addUser(companyId, autoPassword, password1,
				password2, autoScreenName, screenName, emailAddress,
				facebookId, openId, locale, firstName, middleName, lastName,
				prefixId, suffixId, male, birthdayMonth, birthdayDay,
				birthdayYear, jobTitle, groupIds, organizationIds, roleIds,
				userGroupIds, sendEmail, serviceContext);

		System.out.println("inside hook adding user in UserService:"
				+ user.getUserId());
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

	}// end addUser()

	public User updateUser(
			long userId,
			java.lang.String oldPassword,
			java.lang.String newPassword1,
			java.lang.String newPassword2,
			boolean passwordReset,
			java.lang.String reminderQueryQuestion,
			java.lang.String reminderQueryAnswer,
			java.lang.String screenName,
			java.lang.String emailAddress,
			long facebookId,
			java.lang.String openId,
			java.lang.String languageId,
			java.lang.String timeZoneId,
			java.lang.String greeting,
			java.lang.String comments,
			java.lang.String firstName,
			java.lang.String middleName,
			java.lang.String lastName,
			int prefixId,
			int suffixId,
			boolean male,
			int birthdayMonth,
			int birthdayDay,
			int birthdayYear,
			java.lang.String smsSn,
			java.lang.String aimSn,
			java.lang.String facebookSn,
			java.lang.String icqSn,
			java.lang.String jabberSn,
			java.lang.String msnSn,
			java.lang.String mySpaceSn,
			java.lang.String skypeSn,
			java.lang.String twitterSn,
			java.lang.String ymSn,
			java.lang.String jobTitle,
			long[] groupIds,
			long[] organizationIds,
			long[] roleIds,
			java.util.List<com.liferay.portal.model.UserGroupRole> userGroupRoles,
			long[] userGroupIds,
			java.util.List<com.liferay.portal.model.Address> addresses,
			java.util.List<com.liferay.portal.model.EmailAddress> emailAddresses,
			java.util.List<com.liferay.portal.model.Phone> phones,
			java.util.List<com.liferay.portal.model.Website> websites,
			java.util.List<com.liferay.portlet.announcements.model.AnnouncementsDelivery> announcementsDelivers,
			com.liferay.portal.service.ServiceContext serviceContext)
			throws PortalException, SystemException {

		User user = super.updateUser(userId, oldPassword, newPassword1,
				newPassword2, passwordReset, reminderQueryQuestion,
				reminderQueryAnswer, screenName, emailAddress, facebookId,
				openId, languageId, timeZoneId, greeting, comments, firstName,
				middleName, lastName, prefixId, suffixId, male, birthdayMonth,
				birthdayDay, birthdayYear, smsSn, aimSn, facebookSn, icqSn,
				jabberSn, msnSn, mySpaceSn, skypeSn, twitterSn, ymSn, jobTitle,
				groupIds, organizationIds, roleIds, userGroupRoles,
				userGroupIds, addresses, emailAddresses, phones, websites,
				announcementsDelivers, serviceContext);

		// CKAN client call for updating the user
		System.out.println("inside hook updating user in UserService:"
				+ user.getUserId());
		CKANClient ckan = new CKANClient();
		try {
			ckan.updateCKANUser(user.getUserId(), user.getScreenName(),
					user.getEmailAddress(), newPassword1);
		} catch (Exception ex) {
			System.out.println("error updating ckan user password");
		}

		return user;

	}// end updateUser()

	public void deleteUser(long userId) throws PortalException, SystemException {

		super.deleteUser(userId);

		// CKAN client call for deleting user
		System.out
				.println("inside hook deleting user in UserService:" + userId);

	}// end deleteUser()

}// end class