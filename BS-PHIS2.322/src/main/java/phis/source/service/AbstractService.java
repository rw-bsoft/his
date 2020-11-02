/*
 * @(#)AbstractService.java Created on 2011-12-15 下午2:51:43
 *
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.source.service;

import phis.source.BSPHISEntryNames;
import ctd.service.dao.DBService;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 * 
 */
public abstract class AbstractService extends DBService implements
		BSPHISEntryNames {

	public static final String P_SERVICE_ID = "serviceId";
	public static final String P_SCHEMA = "schema";
}
