/**
 * @(#)ChildrenConfigControllor.java 创建于 2011-1-7 下午08:03:37
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
public class ChildrenConfigControllor extends AbstractConfigControllor {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bsoft.hzehr.biz.conf.control.AbstractConfigControllor#getEntryName()
	 */
	@Override
	public String getEntryName() {
		return BSCHISEntryNames.CDH_HealthCard;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bsoft.hzehr.biz.conf.control.AbstractConfigControllor#getSchemaName()
	 */
	@Override
	public String getSchemaName() {
		return BSCHISEntryNames.ADMIN_ChildrenConfig;
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
		return hasPlan(session, BusinessType.CD_IQ,
				BusinessType.CD_CU);
	}

	/**
	 * 获取页面childrenRegisterAge控件的读写权限
	 * 
	 * @param session
	 * @param ctx
	 * @return
	 * @throws PersistentDataOperationException
	 */
	protected boolean childrenRegisterAgeReadOnly(Session session, Context ctx)
			throws PersistentDataOperationException {
		return hasRecord(null,session);
	}

	/**
	 * 获取页面childrenRegisterAge控件的读写权限
	 * 
	 * @param session
	 * @param ctx
	 * @return
	 * @throws PersistentDataOperationException
	 */
	protected boolean childrenDieAgeReadOnly(Session session, Context ctx)
			throws PersistentDataOperationException {
		return hasRecord(BSCHISEntryNames.CDH_DeadRegister,session);
	}

	/**
	 * 获取页面childrenFirstVistDays控件的读写权限
	 * 
	 * @param session
	 * @param ctx
	 * @return
	 * @throws PersistentDataOperationException
	 */
	protected boolean childrenFirstVistDaysReadOnly(Session session, Context ctx)
			throws PersistentDataOperationException {
		return false;
	}

	/**
	 * 获取页面planTypeCode控件的读写权限
	 * 
	 * @param session
	 * @param ctx
	 * @return
	 * @throws PersistentDataOperationException
	 */
	protected boolean planTypeCodeReadOnly(Session session, Context ctx)
			throws PersistentDataOperationException {
		return hasPlan(session, BusinessType.CD_IQ,
				BusinessType.CD_CU);
	}

	/**
	 * 获取页面visitIntervalSame控件的读写权限
	 * 
	 * @param session
	 * @param ctx
	 * @return
	 * @throws PersistentDataOperationException
	 */
	protected boolean visitIntervalSameReadOnly(Session session, Context ctx)
			throws PersistentDataOperationException {
		return hasPlan(session, BusinessType.CD_IQ,
				BusinessType.CD_CU);
	}
	
	/**
	 * 提前天数读写权限
	 * @param session
	 * @param ctx
	 * @return
	 * @throws PersistentDataOperationException
	 */
	protected boolean precedeDaysReadOnly(Session session, Context ctx)
	throws PersistentDataOperationException {
		return hasPlan(session, BusinessType.CD_IQ,BusinessType.CD_CU);
	}
	
	/**
	 * 延后天数读写权限
	 * @param session
	 * @param ctx
	 * @return
	 * @throws PersistentDataOperationException
	 */
	protected boolean delayDaysReadOnly(Session session, Context ctx)
	throws PersistentDataOperationException {
		return hasPlan(session,  BusinessType.CD_IQ,BusinessType.CD_CU);
	}
}
