package com.ganesha.accounting.util;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.ganesha.accounting.minimarket.model.Company;
import com.ganesha.core.exception.AppException;
import com.ganesha.hibernate.HibernateUtil;

public class CompanyConsistencyChecker {

	private Company company;

	public CompanyConsistencyChecker() {
	}

	public void check() throws AppException {
		Session session = HibernateUtil.getSession();
		try {
			Query query = session.createQuery("from Company");

			@SuppressWarnings("unchecked")
			List<Company> companies = query.list();

			if (companies.size() == 1) {
				company = companies.get(0);
			} else {
				throw new AppException("Inconsistent COMPANY in DB");
			}
		} finally {
			session.close();
		}
	}

	public Company getCompany() throws AppException {
		if (company == null) {
			check();
		}
		return company;
	}
}
