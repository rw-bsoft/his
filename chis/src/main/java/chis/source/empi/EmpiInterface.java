/**
 * @(#)EmpiInterface.java Created on 2012-7-2 上午09:17:52
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.empi;

import java.util.List;
import java.util.Map;

import ctd.service.core.ServiceException;

/**
 * @description
 * 
 * @author <a href="mailto:yub@bsoft.com.cn">俞波</a>
 */
public interface EmpiInterface {

	/**
	 * 执行注册个人信息的请求。
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param sc
	 * @param ctx
	 * @return empiId
	 * @throws ServiceException
	 */
	Map<String, Object> submitPerson(Map<String, Object> info)
			throws ServiceException;

	/**
	 * 执行更新个人信息的请求。
	 * 
	 * @param req
	 * @param res
	 * @param sc
	 * @param ctx
	 * @throws ServiceException
	 * @throws ServiceException
	 */
	Map<String, Object> updatePerson(Map<String, Object> body)
			throws ServiceException, ServiceException;

	/**
	 * 执行个人信息高级查询请求。
	 * 
	 * @param req
	 * @param res
	 * @param sc
	 * @param ctx
	 * @throws ServiceException
	 * @throws ServiceException
	 */
	List<Map<String, Object>> advancedSearch(Map<String, Object> body)
			throws ServiceException, ServiceException;

	/**
	 * 用于判断卡或证件是否已存在的查询请求
	 * 
	 * @param body
	 * @return
	 * @throws ServiceException
	 * @throws ServiceException
	 */
	// List<String> getEmpiId(Map<String, Object> body) throws ServiceException,
	// ServiceException;

	/**
	 * 执行注册卡的请求。
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param sc
	 * @param ctx
	 * @throws ServiceException
	 */
	void registerCard(List<Map<String, Object>> cards) throws ServiceException;

	/**
	 * 执行挂失卡的请求。
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param sc
	 * @param ctx
	 * @throws ServiceException
	 */
	void lockCard(String cardType, String cardId) throws ServiceException;

	/**
	 * 执行卡解挂失请求。
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param sc
	 * @param ctx
	 * @throws ServiceException
	 */
	void unlockCard(String cardType, String cardId) throws ServiceException;

	/**
	 * 执行注销卡的请求。
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param sc
	 * @param ctx
	 * @throws ServiceException
	 */
	void writeOffCard(String cardType, String cardId) throws ServiceException;

}
