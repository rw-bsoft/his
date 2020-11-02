/**
 * @(#)DebilityChildrenConfigControllor.java 创建于 2011-1-7 下午08:11:24
 * 
 * 版权：版本所有 bsoft.com.cn 保留所有权力。
 * 
 */
package chis.source.conf.control;

import java.util.Map;
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
public class DebilityChildrenConfigControllor extends AbstractConfigControllor {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bsoft.hzehr.biz.conf.control.AbstractConfigControllor#getEntryName()
	 */
	@Override
	public String getEntryName() {
		return BSCHISEntryNames.CDH_DebilityChildren;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bsoft.hzehr.biz.conf.control.AbstractConfigControllor#getSchemaName()
	 */
	@Override
	public String getSchemaName() {
		return null;
	}

	/**
	 * 获取页面控件的只读权限
	 * 
	 * @param map
	 * @param session
	 * @param ctx
	 * @return
	 */
	protected Map<String, Object> fieldReadOnly(Session session, Context ctx) {
		return null;
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
		return hasPlan(session, BusinessType.CD_DC);
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
		return hasPlan(session, BusinessType.CD_DC);
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
		return hasPlan(session,  BusinessType.CD_DC);
	}

}
