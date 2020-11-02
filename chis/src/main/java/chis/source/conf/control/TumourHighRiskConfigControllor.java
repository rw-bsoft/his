/**
 * @(#)TumourHighRiskConfigControllor.java Created on 2014-4-11 上午9:25:43
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.conf.control;

import org.hibernate.Session;

import chis.source.BSCHISEntryNames;
import chis.source.PersistentDataOperationException;
import chis.source.dic.BusinessType;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class TumourHighRiskConfigControllor extends AbstractConfigControllor {

	/*
	 * (non-Javadoc)
	 * 
	 * @see chis.source.conf.control.AbstractConfigControllor#getEntryName()
	 */
	@Override
	public String getEntryName() {
		return BSCHISEntryNames.ADMIN_TumourHighRiskConfig;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see chis.source.conf.control.AbstractConfigControllor#getSchemaName()
	 */
	@Override
	public String getSchemaName() {
		return BSCHISEntryNames.ADMIN_TumourHighRiskConfig;
	}

	/**
	 * 获取页面按钮的只读权限
	 * 
	 * @param session
	 * @param ctx
	 * @return
	 * @throws PersistentDataOperationException
	 */
	protected boolean btnReadOnly(Session session, Context ctx)
			throws PersistentDataOperationException {
		return hasPlan(session, BusinessType.THR);
	}

	/**
	 * 获取页面控件 startMonth的读写权限
	 * 
	 * @param session
	 * @param ctx
	 * @return
	 * @throws PersistentDataOperationException
	 */
	protected boolean startMonthReadOnly(Session session, Context ctx)
			throws PersistentDataOperationException {
		return false;
	}

	/**
	 * 获取页面控件 oldPeopleEndMonth的读写权限
	 * 
	 * @param session
	 * @param ctx
	 * @return
	 * @throws PersistentDataOperationException
	 */
	protected boolean endMonthReadOnly(Session session, Context ctx)
			throws PersistentDataOperationException {
		return false;
	}

	/**
	 * 提前天数读写权限
	 * 
	 * @param session
	 * @param ctx
	 * @return
	 * @throws PersistentDataOperationException
	 */
	protected boolean precedeDaysReadOnly(Session session, Context ctx)
			throws PersistentDataOperationException {
		return hasPlan(session, BusinessType.THR);
	}

	/**
	 * 延后天数读写权限
	 * 
	 * @param session
	 * @param ctx
	 * @return
	 * @throws PersistentDataOperationException
	 */
	protected boolean delayDaysReadOnly(Session session, Context ctx)
			throws PersistentDataOperationException {
		return hasPlan(session, BusinessType.THR);
	}
	
	/**
	 * 生成方式读写权限
	 * 
	 * @param session
	 * @param ctx
	 * @return
	 * @throws PersistentDataOperationException
	 */
	protected boolean planModeReadOnly(Session session, Context ctx)
			throws PersistentDataOperationException {
		return hasPlan(session, BusinessType.THR);
	}
}
