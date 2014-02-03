package com.ganesha.minimarket.facade;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.ganesha.model.Permission;

public class PermissionFacade {

	private static PermissionFacade instance;

	public static PermissionFacade getInstance() {
		if (instance == null) {
			instance = new PermissionFacade();
		}
		return instance;
	}

	private PermissionFacade() {
	}

	public List<Permission> getAll(Session session) {
		Query query = session.createQuery("from Permission");

		@SuppressWarnings("unchecked")
		List<Permission> permissions = query.list();

		return permissions;
	}
}
