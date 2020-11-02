/*
 * @(#)AbstractService.java Created on 2011-12-15 下午2:51:43
 *
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.service;

import java.util.Observable;

import chis.source.BSCHISEntryNames;

import ctd.service.core.Service;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 * 
 */
public abstract class AbstractService extends Observable implements Service, BSCHISEntryNames {

	public static final String P_SERVICE_ID = "serviceId";
	public static final String P_SCHEMA = "schema";
}
