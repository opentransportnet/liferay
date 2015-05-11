package com.ibm.g4i.service;

import java.util.Date;
import java.util.List;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.model.UserGroup;
import com.liferay.portal.service.UserLocalServiceUtil;

public class Token {

	private Date createDate;
	private long companyId;
	private long userId;
	private List<Role> roles;
	private List<UserGroup> groups;
	private long updateTime;

	public Token(long companyId, long userId) throws SystemException,
			PortalException {
		this.createDate = new Date();
		this.companyId = companyId;
		this.userId = userId;
		this.setUpdateTime(this.getUser().getModifiedDate().getTime());
		this.setRoles(this.getUser().getRoles());
		this.setGroups(this.getUser().getUserGroups());
	}

	public Date getCreateDate() {
		return createDate;
	}

	public User getUser() throws PortalException, SystemException {
		return UserLocalServiceUtil.getUserById(companyId, userId);
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public List<UserGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<UserGroup> groups) {
		this.groups = groups;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	public void updateToken() throws SystemException, PortalException {
		this.setRoles(this.getUser().getRoles());
		this.setGroups(this.getUser().getUserGroups());
		this.setUpdateTime(this.getUser().getModifiedDate().getTime());
	}
}
