/**
 * @(#)OldPeopleConfigControllor.java 创建于 2011-1-7 下午07:52:53
 * 
 * 版权：版本所有 bsoft.com.cn 保留所有权力。
 * 
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
 * @author <a href="mailto:yaozh@bsoft.com.cn">yaozh</a>
 */
public class OldPeopleConfigControllor extends AbstractConfigControllor {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bsoft.hzehr.biz.conf.control.AbstractConfigControllor#getEntryName()
	 */
	@Override
	public String getEntryName() {
		return BSCHISEntryNames.MDC_OldPeopleRecord;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bsoft.hzehr.biz.conf.control.AbstractConfigControllor#getSchemaName()
	 */
	@Override
	public String getSchemaName() {
		return BSCHISEntryNames.ADMIN_OldPeopleFormConfig;
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
		return hasPlan(session, BusinessType.LNR);
	}

	/**
	 * 获取页面控件 oldPeopleStartMonth的读写权限
	 * 
	 * @param session
	 * @param ctx
	 * @return
	 * @throws PersistentDataOperationException
	 */
	protected boolean oldPeopleStartMonthReadOnly(Session session, Context ctx)
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
	protected boolean oldPeopleEndMonthReadOnly(Session session, Context ctx)
			throws PersistentDataOperationException {
		return false;
	}

	/**
	 * 获取页面控件 oldPeoplePlanType的读写权限
	 * 
	 * @param session
	 * @param ctx
	 * @return
	 * @throws PersistentDataOperationException
	 */
	protected boolean oldPeoplePlanTypeReadOnly(Session session, Context ctx)
			throws PersistentDataOperationException {
		return hasPlan(session, BusinessType.LNR);
	}

	/**
	 * 获取页面控件 oldPeopleAge的读写权限
	 * 
	 * @param session
	 * @param ctx
	 * @return
	 * @throws PersistentDataOperationException
	 */
	protected boolean oldPeopleAgeReadOnly(Session session, Context ctx)
			throws PersistentDataOperationException {
		return hasRecord(null,session);
	}

	/**
	 * 获取页面控件 oldPeopleVisit的读写权限
	 * 
	 * @param session
	 * @param ctx
	 * @return
	 * @throws PersistentDataOperationException
	 */
	protected boolean oldPeopleVisitIntervalSameReadOnly(Session session,
			Context ctx) throws PersistentDataOperationException {
		return hasPlan(session, BusinessType.LNR);
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
		return hasPlan(session, BusinessType.LNR);
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
		return hasPlan(session, BusinessType.LNR);
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
		return hasPlan(session, BusinessType.LNR);
	}
}
