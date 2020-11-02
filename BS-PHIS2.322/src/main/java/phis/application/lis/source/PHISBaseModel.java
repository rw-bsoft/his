package phis.application.lis.source;

import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import ctd.dao.exception.DataAccessException;
import ctd.util.context.Context;

/**
 * @description Base Model
 * @author <a href="mailto:chengzx@bsoft.com.cn">chzhxiang</a>
 */
public abstract class PHISBaseModel {
	protected Context ctx;
	protected Session ss;
	protected Transaction trx;

	/**construct method*/
	public PHISBaseModel(){}
	
	public PHISBaseModel(Context ctx) {
		this.ctx = ctx;
		if(this.ss == null){
			if(ctx.has(Context.DB_SESSION)){
				this.ss = (Session) ctx.get(Context.DB_SESSION);
			}
		}
		this.trx = this.ss.getTransaction(); // init transaction but not begin
	}

	public void beginTransaction() throws DataAccessException {
		try {
			trx = ss.getTransaction();
			trx.begin();
		} catch (HibernateException e) {
			throw new DataAccessException("BeginTransactionFailed:"
					+ e.getMessage(), e);
		}
	}

	public void commitTransaction() throws DataAccessException {
		if (trx == null) {
			return;
		}
		try {
			trx.commit();
		} catch (HibernateException e) {
			throw new DataAccessException("CommitTransactionFailed:"
					+ e.getMessage(), e);
		}
	}

	public void rollbackTransaction() throws DataAccessException {
		if (trx == null) {
			return;
		}
		try {
			trx.rollback();
		} catch (HibernateException e) {
			throw new DataAccessException("RollbackTransactionFailed:"
					+ e.getMessage(), e);
		}
	}

	public void destroy() throws DataAccessException {
		if (ss == null || !ss.isOpen()) {
			return;
		}
		try {
			ss.close();
		} catch (HibernateException e) {
			throw new DataAccessException("SessionCloseFailed:"
					+ e.getMessage(), e);
		}
	}
	
	@SuppressWarnings("unchecked")
	protected List<?> getPatientInfoQueryCnd(String scName, Map<String, Object> rec) {
		List queryCnd = null;
		return null;
	}
}
