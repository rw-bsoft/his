/**
 * @(#)EmpiLoader.java Created on Mar 16, 2010 7:58:30 PM
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.ehrview;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.PersistentDataOperationException;
import chis.source.empi.EmpiModel;
import chis.source.pub.PublicModel;
import chis.source.util.SchemaUtil;

import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.service.dao.DBService;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class EmpiLoader extends DBService implements Service, BSCHISEntryNames {

	private static final Log logger = LogFactory.getLog(EmpiLoader.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see ctd.service.core.Service#execute(java.util.Map, java.util.Map,
	 * ctd.util.context.Context)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ServiceException {
		Map<String, Object> reqBody = (HashMap<String, Object>) req.get("body");
		String empiId = (String) reqBody.get("empiId");
		Map<String, Object> empiData;
		try {
			empiData = load(empiId, ctx);
			empiData = SchemaUtil.setDictionaryMessageForList(empiData,
					BSCHISEntryNames.MPI_DemographicInfo);
			res.put("body", empiData);
		} catch (PersistentDataOperationException e) {
			logger.error("Get empi data failed.", e);
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "获取个人基本信息失败！");
			throw new ServiceException(e);
		}
	}

	/**
	 * @param empiId
	 * @param session
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> load(String empiId, Context ctx)
			throws PersistentDataOperationException {
		try {
			BaseDAO baseDAO = new BaseDAO();
			EmpiModel model = new EmpiModel(baseDAO);
			Map<String, Object> empiData = model.getEmpiInfoByEmpiid(empiId);
			String idCard = (String) empiData.get("idCard");
			if (idCard == null) {
				empiData.put("idCard", "");
			}
			// @@ 设置这个属性对应到EHRView 头上Template的temp参数，以强制浏览器去刷新。
			empiData.put("temp", new Date().getTime());
			makeEmpiResponse(empiData, baseDAO);
			return empiData;
		} catch (Exception e) {
			throw new PersistentDataOperationException(e);
		}
	}

	/**
	 * @param result
	 * @param sc
	 * @param session
	 * @return
	 * @throws JSONException
	 */
	private void makeEmpiResponse(Map<String, Object> result, BaseDAO baseDAO) {
		Date birthday = (Date) result.get("birthday");
		if (birthday == null) {
			result.put("lifeCycle", "unknown");
			result.put("lifeCycle_text", "unknown");
			return;
		}

		try {
			PublicModel publicModel = new PublicModel(baseDAO);
			Map<String, Object> cycle = publicModel
					.getLifeCycle(birthday, null);
			if (cycle == null) {
				result.put("lifeCycle", "unknown");
				result.put("lifeCycle_text", "unknown");
			} else {
				result.put("lifeCycle", cycle.get("cycleId"));
				result.put("lifeCycle_text", cycle.get("cycleName"));
				result.put("age", cycle.get("age"));
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Get life cycle failed.", e);
			result.put("lifeCycle", "unknown");
			result.put("lifeCycle_text", "unknown");
		}
	}
}
