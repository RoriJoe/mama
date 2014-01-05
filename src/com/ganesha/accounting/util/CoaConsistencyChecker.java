package com.ganesha.accounting.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;

import com.ganesha.accounting.constants.CoaCodeConstants;
import com.ganesha.accounting.model.Coa;
import com.ganesha.core.exception.AppException;
import com.ganesha.hibernate.HibernateUtil;

public class CoaConsistencyChecker {

	private Map<Integer, Coa> coaListFromConstants;
	private List<Coa> coaListFromDB;

	public CoaConsistencyChecker() {
	}

	public void check() throws AppException {
		loadListFromConstants();
		loadListFromDB();

		if (coaListFromConstants.size() != coaListFromDB.size()) {
			String additionalMessage = "Size in DB: " + coaListFromDB.size()
					+ " , size in Constants: " + coaListFromConstants.size();
			throwException(additionalMessage);
		}

		for (Coa coaDb : coaListFromDB) {
			int id = coaDb.getId();
			Coa coaConstant = coaListFromConstants.get(coaDb.getId());

			if (coaConstant == null) {
				String additionalMessage = "Coa " + id
						+ " in Constant is not set";
				throwException(additionalMessage);
			}
		}
	}

	private void loadListFromConstants() throws AppException {
		coaListFromConstants = new HashMap<>();
		try {
			Class<CoaCodeConstants> clazz = CoaCodeConstants.class;
			Field[] fields = clazz.getFields();

			for (Field field : fields) {
				String coaName = field.getName();
				int coaId = field.getInt(null);
				Coa coa = new Coa();
				coa.setId(coaId);
				coa.setName(coaName);
				coaListFromConstants.put(coaId, coa);
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new AppException(e);
		}
	}

	private void loadListFromDB() {
		Session session = HibernateUtil.getSession();
		try {
			Query query = session.createQuery("from Coa");

			@SuppressWarnings("unchecked")
			List<Coa> coaList = query.list();

			coaListFromDB = coaList;
		} finally {
			session.close();
		}
	}

	private void throwException(String additionalMessage) throws AppException {
		throw new AppException("Inconsistent COA in DB. " + additionalMessage);
	}
}
