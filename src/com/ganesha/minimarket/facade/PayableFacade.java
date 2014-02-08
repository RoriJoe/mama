package com.ganesha.minimarket.facade;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.ganesha.core.exception.AppException;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.core.utils.GeneralConstants;
import com.ganesha.core.utils.GeneralConstants.AccountAction;
import com.ganesha.hibernate.HqlParameter;
import com.ganesha.minimarket.Main;
import com.ganesha.minimarket.model.PayableSummary;
import com.ganesha.minimarket.model.PayableTransaction;

public class PayableFacade {

	private static PayableFacade instance;

	public static PayableFacade getInstance() {
		if (instance == null) {
			instance = new PayableFacade();
		}
		return instance;
	}

	private PayableFacade() {
	}

	public void addSummary(int clientId, Session session) {
		PayableSummary payableSummary = new PayableSummary();
		payableSummary.setClientId(clientId);
		payableSummary.setRemainingAmount(BigDecimal.valueOf(0));
		payableSummary.setLastPayableTransactionId(-1);
		payableSummary.setDisabled(false);
		payableSummary.setDeleted(false);
		payableSummary.setLastUpdatedBy(Main.getUserLogin().getId());
		payableSummary.setLastUpdatedTimestamp(CommonUtils
				.getCurrentTimestamp());

		session.saveOrUpdate(payableSummary);
	}

	public void addTransaction(int clientId, AccountAction accountAction,
			Date maturityDate, BigDecimal amount, String description,
			Session session) throws AppException {

		boolean summaryExists = GlobalFacade.getInstance().isExists("clientId",
				clientId, PayableSummary.class, session);

		if (!summaryExists) {
			addSummary(clientId, session);
		}

		PayableSummary payableSummary = getSummary(clientId, session);

		PayableTransaction payableTransaction = new PayableTransaction();
		payableTransaction.setPayableSummary(payableSummary);
		payableTransaction
				.setReffNumber(GeneralConstants.PREFIX_TRX_NUMBER_PAYABLE
						+ CommonUtils.getTimestampInString());
		payableTransaction.setAccountAction(accountAction.name());
		payableTransaction
				.setActionTimestamp(CommonUtils.getCurrentTimestamp());
		payableTransaction.setMaturityDate(maturityDate);
		payableTransaction.setAmount(amount);
		payableTransaction.setDescription(description);
		payableTransaction.setLastUpdatedBy(Main.getUserLogin().getId());
		payableTransaction.setLastUpdatedTimestamp(CommonUtils
				.getCurrentTimestamp());

		session.saveOrUpdate(payableTransaction);

		if (accountAction == AccountAction.DECREASE) {

			BigDecimal lastRemainingAmount = payableSummary
					.getRemainingAmount();
			lastRemainingAmount = lastRemainingAmount
					.subtract(payableTransaction.getAmount());
			payableSummary.setRemainingAmount(lastRemainingAmount);

		} else if (accountAction == AccountAction.INCREASE) {

			BigDecimal lastRemainingAmount = payableSummary
					.getRemainingAmount();
			lastRemainingAmount = lastRemainingAmount.add(payableTransaction
					.getAmount());
			payableSummary.setRemainingAmount(lastRemainingAmount);

		} else {
			throw new AppException("Unknown AccountAction " + accountAction);
		}

		payableSummary.setLastPayableTransactionId(payableTransaction.getId());
		payableSummary.setLastUpdatedBy(Main.getUserLogin().getId());
		payableSummary.setLastUpdatedTimestamp(CommonUtils
				.getCurrentTimestamp());
		session.saveOrUpdate(payableSummary);
	}

	public PayableSummary getSummary(int clientId, Session session) {
		Criteria criteria = session.createCriteria(PayableSummary.class);
		criteria.add(Restrictions.eq("clientId", clientId));
		PayableSummary payableSummary = (PayableSummary) criteria
				.uniqueResult();
		return payableSummary;
	}

	public List<Map<String, Object>> search(String clientCode,
			String clientName, Session session) {

		String sqlString = "SELECT new Map("
				+ "supplier.code AS code"
				+ ", supplier.name AS name"
				+ ", summary.remainingAmount AS remainingAmount"
				+ ") FROM PayableSummary summary, Supplier supplier WHERE summary.clientId = supplier.id";

		if (!clientCode.trim().equals("")) {
			sqlString += " AND supplier.code LIKE :clientCode";
		}

		if (!clientName.trim().equals("")) {
			sqlString += " AND supplier.name LIKE :clientName";
		}

		Query query = session.createQuery(sqlString);
		HqlParameter parameter = new HqlParameter(query);
		parameter.put("clientCode", "%" + clientCode + "%");
		parameter.put("clientName", "%" + clientName + "%");
		parameter.validate();

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = query.list();

		return list;
	}
}