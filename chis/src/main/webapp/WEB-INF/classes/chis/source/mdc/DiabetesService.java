/**
 * @(#)DiabetesService.java Created on 2012-1-18 上午9:57:37
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.mdc;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.BusinessType;
import chis.source.dic.PlanStatus;
import chis.source.dic.VisitEffect;
import chis.source.phr.HealthRecordService;
import chis.source.pub.PublicModel;
import chis.source.util.SchemaUtil;
import chis.source.visitplan.VisitPlanCreator;
import chis.source.visitplan.VisitPlanModel;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class DiabetesService extends MDCService {
	private static final Logger logger = LoggerFactory
			.getLogger(DiabetesService.class);

	private VisitPlanCreator visitPlanCreator;

	/**
	 * 获得visitPlanCreator
	 * 
	 * @return the visitPlanCreator
	 */
	public VisitPlanCreator getVisitPlanCreator() {
		return visitPlanCreator;
	}

	/**
	 * 设置visitPlanCreator
	 * 
	 * @param visitPlanCreator
	 *            the visitPlanCreator to set
	 */
	public void setVisitPlanCreator(VisitPlanCreator visitPlanCreator) {
		this.visitPlanCreator = visitPlanCreator;
	}

	public void doGetDiabetesInquire(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException,
			PersistentDataOperationException {
		String empiId = StringUtils.trimToEmpty((String) req.get("empiId"));
		DiabetesModel dm = new DiabetesModel(dao);
		List<Map<String, Object>> l = dm.getDiabetesInquire(empiId);
		res.put("body", l);
	}

	public void doGetDiabetesRepeatVisit(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException,
			PersistentDataOperationException {
		String empiId = StringUtils.trimToEmpty((String) req.get("empiId"));
		String visitId = StringUtils.trimToEmpty((String) req.get("visitId"));
		DiabetesModel dm = new DiabetesModel(dao);
		List<Map<String, Object>> l = dm
				.getDiabetesRepeatVisit(empiId, visitId);
		res.put("body", l);
	}

	@SuppressWarnings("unchecked")
	public void doSaveDiabetesInquire(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException,
			PersistentDataOperationException, ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		String inquireId = (String) body.get("inquireId");
		String empiId = (String) body.get("empiId");
		DiabetesModel dm = new DiabetesModel(dao);
		Map<String, Object> map = dm.saveDiabetesInquire(op, body, true);
		map.putAll(SchemaUtil.setDictionaryMessageForList(body,
				MDC_DiabetesInquire));
		if ("create".equals(op)) {
			inquireId = (String) map.get("inquireId");
		}
		vLogService.saveVindicateLog(MDC_DiabetesInquire, op, inquireId, dao,
				empiId);
		res.put("body", map);
	}
	
	/**
	 * 查询糖尿病记录 --糖尿病档案管理主页list add by zhangwei 2015-07-02
	 * 
	 * @param req
	 *            ['schema':'','cnd':'','pageNo':'','pageSize':'']
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doListDiabetesRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		DiabetesModel hm = new DiabetesModel(dao);
		Map<String, Object> resultMap = null;
		String schemaId = (String) req.get("schema");
		List queryCnd = null;
		//yx列表导出时判断要不要查随访计划，原先随访计划条数多了会报错，而且不用去查什么随访计划,值从PrintLoaderCHIS传过来
		Boolean print=false;
		if(req.containsKey("print")){
			if(req.get("print").toString().equals("true")){
				print=true;
			}
		}
		if (req.containsKey("cnd")) {
			queryCnd = (List) req.get("cnd");
		}
		String queryCndsType = null;
		if (req.containsKey("queryCndsType")) {
			queryCndsType = (String) req.get("queryCndsType");
		}
		String sortInfo = null;
		if (req.containsKey("sortInfo")) {
			sortInfo = (String) req.get("sortInfo");
		}
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = 1;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo");
		}
		String year = new Date().getYear() + "";
		if (req.containsKey("year")) {
			year = req.get("year") + "";
		}
		String checkType = "1";
		if (req.containsKey("checkType")) {
			checkType = (String) req.get("checkType");
		}
		PublicModel hrModel = new PublicModel(dao);
		resultMap = hrModel.queryRecordList(schemaId, queryCnd, queryCndsType,
				sortInfo, pageSize, pageNo, year, checkType);
		List<Map<String, Object>> resBody = (List<Map<String, Object>>) resultMap
				.get("body");
		
		if(print){
			res.put("body", resBody);
			return;
		}
		// 取出记录中empiId的值放到empiIdList中
		List<String> empiIdList = new ArrayList<String>();
		if (resBody != null) {
			for (int i = 0; i < resBody.size(); i++) {
				HashMap<String, Object> rec = (HashMap<String, Object>) resBody
						.get(i);
				String empiId = (String) rec.get("empiId");
				empiIdList.add(empiId);
			}
		}
		if (empiIdList.size() == 0) {
			return;
		}
		// 取出糖尿病随访未做的随访记录
		List<Map<String, Object>> list = null;
		try {
			list = hm.checkHasVisitUndo(empiIdList,BusinessType.TNB,"糖尿病");
		} catch (ModelDataOperationException e) {
			logger.error(
					"Failed to find record of hypertension visit plan. message:",
					e);
			throw new ServiceException(e);
		}
		// 将未做随访的糖尿病记录标识为true,(标识属性名为needDoVisit)
		if (list != null) {
			for (int i = 0; i < resBody.size(); i++) {
				HashMap<String, Object> rec = (HashMap<String, Object>) resBody
						.get(i);
				String empiId = (String) rec.get("empiId");
				for (Iterator<Map<String, Object>> it = list.iterator(); it
						.hasNext();) {
					Map<String, Object> map = it.next();
					if (empiId.equals(map.get("empiId"))) {
						if ((Long) map.get("count") > 0) {
							rec.put("needDoVisit", true);
						}
						it.remove();
						break;
					}
				}
			}
		}
		res.put("body", resBody);
		res.put("pageSize", resultMap.get("pageSize"));
		res.put("pageNo", resultMap.get("pageNo"));
		res.put("totalCount", resultMap.get("totalCount"));
	}

	@SuppressWarnings("unchecked")
	public void doSaveDiabetesRepeatVisit(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException,
			PersistentDataOperationException, ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		String recordId = (String) body.get("recordId");
		String empiId = (String) body.get("empiId");
		DiabetesModel dm = new DiabetesModel(dao);
		Map<String, Object> map = dm.saveDiabetesRepeatVisit(op, body, true);
		map.putAll(SchemaUtil.setDictionaryMessageForList(body,
				MDC_DiabetesRepeatVisit));
		if ("create".equals(op)) {
			recordId = (String) map.get("recordId");
		}
		vLogService.saveVindicateLog(MDC_DiabetesRepeatVisit, op, recordId,
				dao, empiId);
		res.put("body", map);
	}

	@SuppressWarnings("unchecked")
	public void doSaveDiabetesEndCheck(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException,
			PersistentDataOperationException, ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		DiabetesRecordModel drm = new DiabetesRecordModel(dao);
		HealthRecordService hrs = new HealthRecordService();
		VisitPlanModel vpm = new VisitPlanModel(dao);
		String visitEffect = (String) body.get("visitEffect");
		try {
			String cancellationReason = (String) body.get("cancellationReason");
			if (VisitEffect.CONTINUE.equals(visitEffect)) {
				body.put("endCheck", "1");
				body.put("noVisitReason", null);
				drm.saveDiabetesRecordEnd("update", body, true);
				drm.revertDiabetesRecord((String) body.get("phrId"));
				vpm.setVisitedPlanStatus((String) body.get("phrId"),
						BusinessType.TNB, PlanStatus.VISITED);
				vpm.revertVisitPlan("empiId", (String) body.get("empiId"),
						BusinessType.TNB);
				drm.setLastVisitEffect((String) body.get("phrId"),
						VisitEffect.CONTINUE, null);
			} else if (VisitEffect.LOST.equals(visitEffect)) {
				body.put("endCheck", "1");
				body.put("noVisitReason", "3");
				drm.saveDiabetesRecordEnd("update", body, true);
				drm.revertDiabetesRecord((String) body.get("phrId"));
				vpm.revertVisitPlan("empiId", (String) body.get("empiId"),
						BusinessType.TNB);
				vpm.setVisitedPlanStatus((String) body.get("phrId"),
						BusinessType.TNB, PlanStatus.VISITED);
				Map<String, Object> visitPlan = new HashMap<String, Object>();
				visitPlan.put("businessType", BusinessType.GXY);
				visitPlan.put("empiId", body.get("empiId"));
				visitPlan.put("recordId", body.get("phrId"));
				vpm.updateLastVisitedPlanStatus(visitPlan, PlanStatus.LOST,
						PlanStatus.VISITED);
				drm.setLastVisitEffect((String) body.get("phrId"),
						VisitEffect.LOST, "3");
			} else if (VisitEffect.END.equals(visitEffect)) {
				body.put("endCheck", "2");
				body.put("noVisitReason", cancellationReason);
				drm.saveDiabetesRecordEnd("update", body, true);
				if ("1".equals(cancellationReason)
						|| "2".equals(cancellationReason)) {
					hrs.logoutAllRecords(req, res, dao, ctx, vLogService);
				}
			}
		} catch (ModelDataOperationException e) {
			logger.error("do Save Hypertension End Check error .", e);
			throw new ServiceException(e);
		}
	}

	// protected void doSaveSimilarity(Map<String, Object> jsonReq,
	// Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
	// throws PersistentDataOperationException, ValidateException {
	// Schema sc = SchemaController.instance().getSchema(
	// MDC_DiabetesSimilarity);
	// HashMap<String, Object> body = (HashMap<String, Object>) jsonReq
	// .get("body");
	// Map<String, Object> result = new HashMap<String, Object>();
	// String diagnosisType = (String) body.get("diagnosisType");
	// String similarityId = (String) body.get("similarityId");
	// if (diagnosisType != null && diagnosisType.equals("3")) {
	// if (similarityId == null || similarityId.equals("")) {
	// return;
	// } else {
	// try {
	// dao.doRemove(similarityId, MDC_DiabetesSimilarity);
	// jsonRes.put("body", body);
	// } catch (PersistentDataOperationException e) {
	// jsonRes.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
	// jsonRes.put(RES_MESSAGE, "删除糖尿病疑似记录失败。");
	// throw new PersistentDataOperationException(e);
	// }
	// return;
	// }
	// } else if (diagnosisType != null && diagnosisType.equals("1")) {
	// result.put("createRecord", true);
	// jsonRes.put("body", result);
	// }
	// if (similarityId == null || similarityId.equals("")) {
	// // similarityId = KeyManager.getKeyByName(sc.getId(), ctx);
	// try {
	// similarityId = (String) ExpRunner.run("['idGen','" + sc.getId()
	// + "']", ctx);
	// } catch (ExpException e) {
	// // 临时处理
	// e.printStackTrace();
	// return;
	// }
	// }
	// body.put("similarityId", similarityId);
	// try {
	// Map resMap = dao.doSave((String) jsonReq.get("op"),
	// MDC_DiabetesSimilarity, body, false);
	// resMap.putAll(result);
	// jsonRes.put("body", resMap);
	// } catch (PersistentDataOperationException e) {
	// jsonRes.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
	// jsonRes.put(RES_MESSAGE, "保存糖尿病疑似记录失败。");
	// throw new PersistentDataOperationException(e);
	// }
	//
	// }

}
