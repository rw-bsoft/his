/**
 * @(#)PlanTypeConfigControllor.java 创建于 2011-1-7 下午06:25:18
 * 
 * 版权：版本所有 bsoft.com.cn 保留所有权力。
 * 
 */
package chis.source.conf.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import chis.source.BSCHISEntryNames;
import chis.source.Constants;
import chis.source.PersistentDataOperationException;
import chis.source.dic.BusinessType;
import chis.source.util.ApplicationUtil;
import ctd.app.Application;
import ctd.controller.exception.ControllerException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class PlanTypeConfigControllor extends AbstractConfigControllor {

	// ** 有计划类型存储在Application.xml文件的计划方案
	public final static List<String> instanceType = new ArrayList<String>();

	static {
		instanceType.add(BusinessType.LNR);
		instanceType.add(BusinessType.CD_IQ);
		instanceType.add(BusinessType.CD_CU);
		instanceType.add(BusinessType.CD_DC);
		instanceType.add(BusinessType.MATERNAL);
	}

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
	 * @see com.bsoft.hzehr.biz.conf.control.ConfigControllor#isReadOnly()
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> isReadOnly(Session session, Context ctx)
			throws PersistentDataOperationException, ControllerException {
		String hql = new StringBuffer("select distinct planTypeCode from ")
				.append(BSCHISEntryNames.PUB_PlanInstance).toString();
		List<String> list = null;
		try {
			Query query = session.createQuery(hql);
			list = query.list();
		} catch (HibernateException e) {
			throw new PersistentDataOperationException(e);
		}
		Map<String, Object> subMap = new HashMap<String, Object>();
		for (String code : list) {
			subMap.put(code + "_readOnly", true);
		}
		
			Application	app = ApplicationUtil.getApplication(Constants.UTIL_APP_ID);
		for (Iterator<String> iterator = instanceType.iterator(); iterator
				.hasNext();) {
			String type = (String) iterator.next();
			String code = (String) app.getProperty(type + "_planTypeCode");
			if (code == null || code.equals("")) {
				continue;
			}
			String codeKey = code + "_readOnly";
			if (subMap.get(codeKey) == null) {
				subMap.put(codeKey, true);
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(moduleId + "_readOnly", subMap);
		return map;
	}

}
