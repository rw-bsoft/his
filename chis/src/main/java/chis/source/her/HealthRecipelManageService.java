/**
 * @(#)HealthRecipelManageService.java Created on 2013-6-9 下午4:09:01
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.her;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.InputStreamUtils;
import chis.source.util.SchemaUtil;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class HealthRecipelManageService extends AbstractActionService implements
		DAOSupportable {

	private static Logger logger = LoggerFactory
			.getLogger(HealthRecipelManageService.class);

	/**
	 * 
	 * @Description:保存健康教育处方更新
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2013-6-9 下午4:44:26
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doSaveHealthRecipel(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		String recipelContent = StringUtils.trimToEmpty((String) reqBodyMap
				.get("recipelContent"));
		try {
			byte[] rc = InputStreamUtils.InputStreamTOByte(InputStreamUtils
					.StringTOInputStream(recipelContent));
			reqBodyMap.put("recipelContent", rc);
		} catch (IOException e) {
			logger.error("Failed to InputStream transition to byte.", e);
			throw new ServiceException(
					Constants.CODE_INPUTSTREAM_TO_BYTE_ERROR,
					"将InputStream转换成byte数组失败！", e);
		} catch (Exception e) {
			logger.error("Failed to String transition to InputStream.", e);
			throw new ServiceException(
					Constants.CODE_STRING_TO_INPUTSTREAM_ERROR,
					"将String转换成InputStream失败！", e);
		}
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		HealthRecipelManageModel hrmModel = new HealthRecipelManageModel(dao);
		try {
			resBodyMap.putAll(hrmModel.saveHealthRecipel(op, reqBodyMap, true));
		} catch (ModelDataOperationException e) {
			logger.error("Failed to save health recipel.", e);
			throw new ServiceException(e);
		}
		res.put("body", resBodyMap);
	}

	/**
	 * 
	 * @Description:获取健康处方
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2013-6-9 下午5:39:14
	 * @Modify:
	 */
	public void doGetHealthRecipel(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String pkey = (String) req.get("pkey");
		HealthRecipelManageModel hrmModel = new HealthRecipelManageModel(dao);
		Map<String, Object> rsMap = null;
		try {
			rsMap = hrmModel.getHealthRecipel(pkey);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get health recipel by pkey!", e);
			throw new ServiceException(e);
		}
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		if (rsMap != null && rsMap.size() > 0) {
			byte[] rcByte = (byte[]) rsMap.get("recipelContent");
			try {
				if (rcByte != null) {
					String recipelContent = InputStreamUtils
							.byteTOString(rcByte);
					rsMap.put("recipelContent", recipelContent);
				}
				resBodyMap = SchemaUtil.setDictionaryMessageForForm(rsMap,
						HER_RecipelRecord);
			} catch (Exception e) {
				logger.error("byte[] transition String failed.", e);
				throw new ServiceException(e);
			}
		}
		res.put("body", resBodyMap);
	}

	/**
	 * 
	 * @Description:引用健康处方公用页面查询处方（Blob字段转换）
	 * @param req
	 * @param res
	 * @param dao
	 * @param cxt
	 * @throws ServiceException
	 * @author ChenXianRui 2013-6-13 上午10:45:27
	 * @Modify:
	 */
	public void doQueryHealthRecipel(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context cxt)
			throws ServiceException {
		int pageNo = (Integer) req.get("pageNo");
		int pageSize = (Integer) req.get("pageSize");
		String queryCndsType = (String) req.get("queryCndsType");
		List<?> cnd = (List<?>) req.get("cnd");
		String entryName = (String) req.get("schema");
		HealthRecipelManageModel hrmModel = new HealthRecipelManageModel(dao);
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		try {
			resBodyMap = hrmModel.queryHealthRecipel(cnd, null, entryName,
					pageNo, pageSize, queryCndsType);
		} catch (ModelDataOperationException e) {
			logger.error(
					"Failed to query health recipel by cnd " + cnd.toString(),
					e);
			throw new ServiceException(e);
		}
		resBodyMap.put("pageNo", pageNo);
		resBodyMap.put("pageSize", pageSize);
		resBodyMap.put("queryCndsType", queryCndsType);
		res.putAll(resBodyMap);
	}

	public void doListHealthRecipel(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context cxt)
			throws ServiceException {
		HealthRecipelManageModel hrmModel = new HealthRecipelManageModel(dao);
		String entryName = (String)req.get("schema");
		List queryCnd = null;
		if(req.containsKey("cnd")){
			queryCnd = (List)req.get("cnd");
		}
		String queryCndsType = null;
		if(req.containsKey("queryCndsType")){
			queryCndsType = (String)req.get("queryCndsType");
		}
		String sortInfo = "a.inputDate desc";
		if(req.containsKey("sortInfo")){
			sortInfo = (String)req.get("sortInfo");
		}
		int pageSize= 25;
		if(req.containsKey("pageSize")){
			pageSize = (Integer)req.get("pageSize");
		}
		int pageNo = 1;
		if(req.containsKey("pageNo")){
			pageNo = (Integer)req.get("pageNo");
		}
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		try {
			resBodyMap = hrmModel.listHealthRecipel(queryCnd, sortInfo, entryName,
					pageNo, pageSize, queryCndsType);
		} catch (ModelDataOperationException e) {
			logger.error(
					"Failed to query health recipel by cnd " + queryCnd.toString(),
					e);
			throw new ServiceException(e);
		}
		resBodyMap.put("pageNo", pageNo);
		resBodyMap.put("pageSize", pageSize);
		resBodyMap.put("queryCndsType", queryCndsType);
		res.putAll(resBodyMap);
	}
	public static Map<String, Object> rCtx=new HashMap<String, Object>();
	public void doSaveHealthRecipelToCache(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String recordId=(String) req.get("recordId");
		Map<String, Object> body=(Map<String, Object>) req.get("body");
		rCtx.put(recordId, body);
	}
	
}
