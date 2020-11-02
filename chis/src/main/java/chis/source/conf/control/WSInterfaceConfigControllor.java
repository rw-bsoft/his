/**
 * @(#)WSInterfaceConfigControllor.java 创建于 2011-1-7 下午06:23:42
 * 
 * 版权：版本所有 bsoft.com.cn 保留所有权力。
 * 
 */
package chis.source.conf.control;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Session;

import chis.source.PersistentDataOperationException;

import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class WSInterfaceConfigControllor extends AbstractConfigControllor {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bsoft.hzehr.biz.conf.control.AbstractConfigControllor#getEntryName()
	 */
	@Override
	public String getEntryName() {
		return null;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bsoft.hzehr.biz.conf.control.ConfigControllor#isReadOnly(org.hibernate
	 * .Session, ctd.util.context.Context)
	 */
	public Map<String, Object> isReadOnly(Session session, Context ctx)
			throws PersistentDataOperationException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(moduleId + "_readOnly", false);
		return map;
	}

}
