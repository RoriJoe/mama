package com.ganesha.model;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

public class UserRoleLinkPK implements TableEntity {
	private static final long serialVersionUID = 274018399212050879L;

	@ManyToOne
	@JoinColumn(name = "USER_ID", nullable = false)
	private User user;

	@ManyToOne
	@JoinColumn(name = "ROLE_ID", nullable = false)
	private Role role;

	public Role getRole() {
		return role;
	}

	public User getUser() {
		return user;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
