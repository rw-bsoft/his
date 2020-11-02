/**
 * @(#)DiabetesConfigControllor.java 创建于 2011-1-7 下午08:02:53
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
public class DiabetesConfigControllor extends AbstractConfigControllor {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bsoft.hzehr.biz.conf.control.AbstractConfigControllor#getEntryName()
	 */
	@Override
	public String getEntryName() {
		return BSCHISEntryNames.MDC_DiabetesRecord;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bsoft.hzehr.biz.conf.control.AbstractConfigControllor#getSchemaName()
	 */
	@Override
	public String getSchemaName() {
		return BSCHISEntryNames.ADMIN_DiabetesConfig;
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
		return hasPlan(session, BusinessType.TNB);
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
	 * 获取页面控件 endMonth的读写权限
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
	 * 获取页面控件 planType1的读写权限
	 * 
	 * @param session
	 * @param ctx
	 * @return
	 * @throws PersistentDataOperationException
	 */
	protected boolean planType1ReadOnly(Session session, Context ctx)
			throws PersistentDataOperationException {
		return hasPlan(session, BusinessType.TNB);
	}

	/**
	 * 获取页面控件 planType2的读写权限
	 * 
	 * @param session
	 * @param ctx
	 * @return
	 * @throws PersistentDataOperationException
	 */
	protected boolean planType2ReadOnly(Session session, Context ctx)
			throws PersistentDataOperationException {
		return hasPlan(session, BusinessType.DiabetesRisk);
	}

	/**
	 * 获取页面控件 planType3的读写权限
	 * 
	 * @param session
	 * @param ctx
	 * @return
	 * @throws PersistentDataOperationException
	 */
	protected boolean planType3ReadOnly(Session session, Context ctx)
			throws PersistentDataOperationException {
		return hasPlan(session, BusinessType.DiabetesRisk);
	}

	/**
	 * 获取页面控件 configType的读写权限
	 * 
	 * @param session
	 * @param ctx
	 * @return
	 * @throws PersistentDataOperationException
	 */
	protected boolean configTypeReadOnly(Session session, Context ctx)
			throws PersistentDataOperationException {
		return hasPlan(session, BusinessType.DiabetesRisk);
	}
	/**
	 * 获取页面控件 manageType的读写权限
	 * 
	 * @param session
	 * @param ctx
	 * @return
	 * @throws PersistentDataOperationException
	 */
	protected boolean manageTypeReadOnly(Session session, Context ctx)
			throws PersistentDataOperationException {
		return hasPlan(session, BusinessType.DiabetesRisk);
	}

	// /**
	// * 获取页面控件 planTypeCode的读写权限
	// *
	// * @param session
	// * @param ctx
	// * @return
	// * @throws PersistentDataOperationException
	// */
	// protected boolean planTypeCodeReadOnly(Session session, Context ctx)
	// throws PersistentDataOperationException {
	// return false;
	// }
	//
	//
	// /**
	// * 获取页面控件 planType3的读写权限
	// *
	// * @param session
	// * @param ctx
	// * @return
	// * @throws PersistentDataOperationException
	// */
	// protected boolean planType3ReadOnly(Session session, Context ctx)
	// throws PersistentDataOperationException {
	// return false;
	// }

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
		return hasPlan(session, BusinessType.TNB);
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
		return hasPlan(session, BusinessType.TNB);
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
		return hasPlan(session, BusinessType.TNB);
	}

}
