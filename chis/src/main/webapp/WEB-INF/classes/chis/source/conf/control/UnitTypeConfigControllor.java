/**
 * @(#)UnitTypeConfigControllor.java Created on 2012-5-17 下午02:20:29
 * 
 * 版权：版权所有 bsoft 保留所有权力。
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
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class UnitTypeConfigControllor extends AbstractConfigControllor {

	public String getEntryName() {
		return null;
	}

	public String getSchemaName() {
		return null;
	}

	public Map<String, Object> isReadOnly(Session session, Context ctx)
			throws PersistentDataOperationException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(moduleId + "_readOnly", false);
		return map;
	}
}
