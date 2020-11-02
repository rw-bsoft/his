/**
 * @(#)HealthRecordService.java Created on 2012-1-5 上午11:37:25
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.phr;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.admin.AreaGridModel;
import chis.source.admin.SystemUserModel;
import chis.source.cdh.ChildrenBaseModel;
import chis.source.cdh.ChildrenHealthModel;
import chis.source.cdh.DebilityChildrenModel;
import chis.source.control.ControlRunner;
import chis.source.dc.RabiesRecordModel;
import chis.source.def.DefLimbModel;
import chis.source.dic.Gender;
import chis.source.dic.IsFamily;
import chis.source.dic.Maritals;
import chis.source.dic.RelatedCode;
import chis.source.dic.RolesList;
import chis.source.dic.YesNo;
import chis.source.empi.EmpiModel;
import chis.source.fhr.FamilyRecordModule;
import chis.source.idr.IdrReportModel;
import chis.source.log.VindicateLogService;
import chis.source.mdc.DiabetesRecordModel;
import chis.source.mdc.HypertensionModel;
import chis.source.mdc.HypertensionRiskModel;
import chis.source.mhc.PregnantRecordModel;
import chis.source.ohr.OldPeopleRecordModel;
import chis.source.psy.PsychosisRecordModel;
import chis.source.rvc.RetiredVeteranCadresModel;
import chis.source.sch.SchistospmaModel;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.tr.TumourConfirmedModel;
import chis.source.tr.TumourHighRiskModel;
import chis.source.tr.TumourPatientReportCardModel;
import chis.source.tr.TumourQuestionnaireModel;
import chis.source.tr.TumourScreeningModel;
import chis.source.util.SchemaUtil;
import chis.source.visitplan.VisitPlanModel;
import com.alibaba.fastjson.JSONException;
import ctd.account.UserRoleToken;
import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description 个人健康档案服务
 * 
 * @author <a href="mailto:tianj@bsoft.com.cn">tianj</a>
 */
public class HealthRecordService extends AbstractActionService implements
		DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(HealthRecordService.class);

	/**
	 * 根据姓名、出生日期查询健康档案列表。
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doQueryHealthRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {

		HashMap<String, Object> body = (HashMap<String, Object>) req
				.get("body");
		String name = (String) body.get("personName");
		String birthday = (String) body.get("birthday");
		String sexCode = (String) body.get("sexCode");
		String childEmpiId = (String) body.get("empiId");
		EmpiModel empiModel = new EmpiModel(dao);
		try {
			List<Map<String, Object>> empiList = empiModel.getEmpiInfoByBase(
					name, sexCode, birthday);
			List<String> matchedList = new ArrayList<String>();
			for (Map<String, Object> empiInfo : empiList) {
				String empiId = (String) empiInfo.get("empiId");
				if (empiId != null && !empiId.equals(childEmpiId)) {
					matchedList.add(empiId);
				}
			}

			if (matchedList.size() == 0) {
				res.put("body", new HashMap<String, Object>());
				return;
			}

			HealthRecordModel healthRecordModel = new HealthRecordModel(dao);
			List<Map<String, Object>> recs = healthRecordModel
					.getHealthRecordByEmpiId(matchedList);
			res.put("body", recs);

		} catch (ModelDataOperationException e) {
			logger.error("error query healthrecord.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 根据Empiid健康档案列表。
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doQueryHealthRecordByEmpiid(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {

		HashMap<String, Object> body = (HashMap<String, Object>) req
				.get("body");
		String empiId = (String) body.get("empiId");
		try {
			HealthRecordModel healthRecordModel = new HealthRecordModel(dao);
			List<String> empiIdList = new ArrayList<String>();
			empiIdList.add(empiId);
			List<Map<String, Object>> recs = healthRecordModel
					.getHealthRecordByEmpiId(empiIdList);
			res.put("body", recs != null && recs.size() == 1 ? recs.get(0) : "");
		} catch (ModelDataOperationException e) {
			logger.error("error query healthrecord.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 根据Empiid健康档案列表。
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doGetHealthRecordByEmpiid(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {

		HashMap<String, Object> body = (HashMap<String, Object>) req
				.get("body");
		String empiId = (String) body.get("empiId");
		try {
			HealthRecordModel healthRecordModel = new HealthRecordModel(dao);
			Map<String, Object> recs = healthRecordModel
					.getHealthRecordByEmpiId(empiId);
			res.put("body", recs);
		} catch (ModelDataOperationException e) {
			logger.error("error query healthrecord.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doUpdatePersonName(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String empiId = (String) body.get("empiId");
		String newName = (String) body.get("personName");
		List<Map<String, Object>> confList = new ArrayList<Map<String, Object>>();
		// 个人健康档案父亲姓名更新
		Map<String, Object> conf1 = new HashMap<String, Object>();
		conf1.put("schema", EHR_HealthRecord);
		conf1.put("empiIdField", "fatherId");
		conf1.put("nameField", "fatherName");
		confList.add(conf1);
		// 个人健康档案父母亲名更新
		Map<String, Object> conf2 = new HashMap<String, Object>();
		conf2.put("schema", EHR_HealthRecord);
		conf2.put("empiIdField", "motherId");
		conf2.put("nameField", "motherName");
		confList.add(conf2);
		// 个人健康档案配偶姓名更新
		Map<String, Object> conf3 = new HashMap<String, Object>();
		conf3.put("schema", EHR_HealthRecord);
		conf3.put("empiIdField", "partnerId");
		conf3.put("nameField", "partnerName");
		confList.add(conf3);
		// 死亡登记父亲姓名
		Map<String, Object> conf4 = new HashMap<String, Object>();
		conf4.put("schema", CDH_DeadRegister);
		conf4.put("empiIdField", "fatherEmpiId");
		conf4.put("nameField", "fatherName");
		confList.add(conf4);
		// 死亡登记父亲姓名
		Map<String, Object> conf5 = new HashMap<String, Object>();
		conf5.put("schema", CDH_DeadRegister);
		conf5.put("empiIdField", "motherEmpiId");
		conf5.put("nameField", "motherName");
		confList.add(conf5);
		// 新生儿访视
		Map<String, Object> conf6 = new HashMap<String, Object>();
		conf6.put("schema", MHC_BabyVisitInfo);
		conf6.put("empiIdField", "fatherEmpiId");
		conf6.put("nameField", "fatherName");
		confList.add(conf6);

		Map<String, Object> conf7 = new HashMap<String, Object>();
		conf7.put("schema", MHC_BabyVisitInfo);
		conf7.put("empiIdField", "empiId");
		conf7.put("nameField", "motherName");
		confList.add(conf7);
		try {
			for (int i = 0; i < confList.size(); i++) {
				Map<String, Object> conf = (Map<String, Object>) confList
						.get(i);
				StringBuffer sb = new StringBuffer("update ").append(conf
						.get("schema"));
				sb.append(" set ").append(conf.get("nameField"));
				sb.append("='").append(newName).append("'");
				sb.append(" where ").append(conf.get("empiIdField"))
						.append("='").append(empiId).append("'");
				dao.doUpdate(sb.toString(), null);
			}
			// 如果是户主，更新家庭档案中的户主姓名
			StringBuffer sql2 = new StringBuffer(
					"select familyId as familyId from EHR_HealthRecord where empiid ='")
					.append(empiId).append("' and status='0' and relacode=")
					.append(RelatedCode.MASTER);
			List<Map<String, Object>> result = dao.doQuery(sql2.toString(),
					null);
			if (result.size() > 0) {
				String familyId = (String) ((Map<String, Object>) result.get(0))
						.get("familyId");
				StringBuffer sql3 = new StringBuffer(
						"update EHR_FamilyRecord set ownerName='").append(
						newName).append("' ");
				sql3.append(" where familyId ='").append(familyId).append("'");
				dao.doUpdate(sql3.toString(), null);
			}
		} catch (PersistentDataOperationException e) {
			throw new ServiceException("个人姓名更新失败!", e);
		}
	}

	/**
	 * 保存个人健康档案。
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveHealthRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		body.remove("signFlag");
		String empiId = (String) body.get("empiId");
		String op = (String) req.get("op");
		String status = (String) body.get("status");
		String masterFlag = (String) body.get("masterFlag");
		String relaCode = (String) body.get("relaCode");
		String regionCode = (String) body.get("regionCode");
		String phrId = (String) body.get("phrId");
		String familyId = (String) body.get("familyId");
		String areaGridType = (String) body.get("areaGridType");
		Context c = new Context();
		c.put("regionCode", regionCode);
		ctx.put("codeCtx", c);
		Map<String, Object> resBody = null;
		try {
			HealthRecordModel healthRecordModel = new HealthRecordModel(dao);
			if (YesNo.YES.equals(masterFlag)
					|| RelatedCode.MASTER.equals(relaCode)) {
				if (StringUtils.isEmpty(familyId)
						&& !"part".equals(areaGridType)) {
					FamilyRecordModule familyModel = new FamilyRecordModule(dao);
					familyId = familyModel.getFamilyIdByRegionCode(regionCode);
				}
				if (StringUtils.isNotEmpty(familyId)) {
					Map<String, Object> masterRecords = healthRecordModel
							.getMasterRecordByFamilyId(familyId);
					if (masterRecords != null) {
						String masterPhrId = (String) masterRecords
								.get("phrId");
						if (masterPhrId != null && !masterPhrId.equals(phrId)) {
							if (!"part".equals(areaGridType)) {
								String message = "["
										+ (String) masterRecords
												.get("regionCode_text")
										+ "]地址已经存在户主,无法在同一家庭建立两个户主!";
								res.put(Service.RES_CODE,
										Constants.CODE_RECORD_EXSIT);
								res.put(Service.RES_MESSAGE, message);
								return;
							}
						}
					}
				}
			}

			// ****审核档案,设置状态为正常****
			body.put("status", Constants.CODE_STATUS_NORMAL);

			// 查找个人档案姓名
			EmpiModel empiModel = new EmpiModel(dao);
			Map<String, Object> empiInfo = empiModel
					.getEmpiInfoByEmpiid(empiId);
			String preferRelaCode = null;
			if ("update".equals(op)) {
				Map<String, Object> preferRecord = healthRecordModel
						.getHealthRecordByPhrId(phrId);
				preferRelaCode = (String) preferRecord.get("relaCode");
			}
			String personName = (String) empiInfo.get("personName");
			String sexCode = (String) empiInfo.get("sexCode");

			body.put("familyId", familyId);
			body.put("personName", personName);
			body.put("sexCode", sexCode);
			// 如果有家庭档案，判断是否进行户主更新。
			if (status != null && !status.equalsIgnoreCase("")
					&& familyId != null && !familyId.equalsIgnoreCase("")) {
				updateFamilyIfo(preferRelaCode, body, dao);
			}
			// 保存基本信息
			resBody = healthRecordModel.saveHealthRecord(op, body);
			if ("update".equals(op)) {
				if (resBody == null) {
					resBody = new HashMap<String, Object>();
				}
				resBody.put("phrId", phrId);
			}
			phrId = (String) resBody.get("phrId");
			vLogService.saveVindicateLog(EHR_HealthRecord, op, phrId, dao,
					empiId);

			// 保存个人档案
			vLogService.saveRecords("GRDA", op, dao, empiId);
			//打签约项目完成标识
			//this.finishSCServiceTask(empiId, JMJKDA_GLFW, null, dao);
			// -----------

			// 录入时更新家庭成员关系
			if ("create".equals(op)) {
				// 处理家庭成员关系
				List<Map<String, Object>> relatedRecords = healthRecordModel
						.getRelatedRecordByFamilyId(familyId);
				saveFamilyRelations(relatedRecords, dao);
			} else if ("update".equals(op)) {
				// 查询出修改前的与户主关系。
				String newRelaCode = (String) body.get("relaCode");
				if (newRelaCode != null && newRelaCode.length() > 0
						&& false == newRelaCode.equals(preferRelaCode)) {
					// 处理家庭成员关系
					List<Map<String, Object>> relatedRecords = healthRecordModel
							.getRelatedRecordByFamilyId(familyId);
					clearRelations((String) body.get("phrId"), preferRelaCode,
							relatedRecords, dao);
					saveFamilyRelations(relatedRecords, dao);
				}
			}

			// 判断用户是否死亡,若死亡则需注销其相关档案
			String deadFlag = (String) body.get("deadFlag");
			if (deadFlag.equals(YesNo.YES)) {
				HashMap<String, Object> deadReq = new HashMap<String, Object>(
						req);
				if ("create".equals(op)) {
					deadReq.put("body", resBody);
				}
				HashMap<String, Object> reqBody = (HashMap<String, Object>) deadReq
						.get("body");
				reqBody.put("cancellationReason", "1");
				reqBody.put("deadReason", body.get("deadReason"));
				reqBody.put("deadDate",
						((String) body.get("deadDate")).substring(0, 10));
				reqBody.put("cancellationUser", UserRoleToken.getCurrent()
						.getUserId());
				reqBody.put("cancellationDate", new SimpleDateFormat(
						"yyyy-MM-dd").format(new Date()));

				// 调用注销档案服务。
				this.doLogoutAllRecords(deadReq, resBody, dao, ctx);
				ControlRunner.run(EHR_HealthRecord, resBody, ctx,
						ControlRunner.UPDATE);
			}
			if ("create".equals(op)) {
				resBody.put("deadFlag", deadFlag);
			}
			res.put("body", resBody);
			res.put(Service.RES_CODE, Constants.CODE_OK);
			res.put(Service.RES_MESSAGE, "个人健康档案保存成功");
		} catch (Exception e) {
			e.printStackTrace();
			res.put(Service.RES_CODE, Constants.CODE_UNKNOWN_ERROR);
			res.put(Service.RES_MESSAGE, "保存个人健康档案相关操作失败");
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 责任医生审核居民档案(一级审核)。
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doVerifyHealthRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		
		try {
			HealthRecordModel healthRecordModel = new HealthRecordModel(dao);
			healthRecordModel.doverifyHealthRecord(body);
		} catch (ModelDataOperationException e) {
			logger.error("error query healthrecord.", e);
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 责任医生取消审核居民档案(一级审核)。
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doCancelverifyHealthRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		
		try {
			HealthRecordModel healthRecordModel = new HealthRecordModel(dao);
			healthRecordModel.doCancelverifyHealthRecord(body);
		} catch (ModelDataOperationException e) {
			logger.error("error query healthrecord.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 更新家庭成员之间的关系。
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param sc
	 * @param ctx
	 * @throws JSONException
	 * @throws PersistentDataOperationException
	 * @throws ServiceException
	 */
	private void saveFamilyRelations(List<Map<String, Object>> relatedRecords,
			BaseDAO dao) throws ServiceException {
		for (int i = 0; i < relatedRecords.size(); i++) {
			HashMap<String, Object> relatedRecord = (HashMap<String, Object>) relatedRecords
					.get(i);
			String relaCode = (String) relatedRecord.get("relaCode");
			if (relaCode == null || relaCode.trim().length() == 0)
				continue;
			if (RelatedCode.MASTER.equals(relaCode)) {// 处理户主关系
				dealMasterRelatoins(relatedRecords, relatedRecord, dao);
			} else if (RelatedCode.HUSBAND.equals(relaCode)
					|| RelatedCode.WIFE.equals(relaCode)) {// 处理户主子女关系
				dealMasterPartnerRelations(relatedRecords, relatedRecord, dao);
			} else if (RelatedCode.FATHER.equals(relaCode)) {// 处理户主父亲关系
				dealMasterFatherRelations(relatedRecords, relatedRecord, dao);
			} else if (RelatedCode.MOTHER.equals(relaCode)) {// 处理户母父亲关系
				dealMasterMotherRelations(relatedRecords, relatedRecord, dao);
			}
		}
		HealthRecordModel healthRecordModel = new HealthRecordModel(dao);
		// 执行更新
		for (int i = 0; i < relatedRecords.size(); i++) {
			HashMap<String, Object> healthRecord = (HashMap<String, Object>) relatedRecords
					.get(i);
			if ("1".equals(healthRecord.get("_doUpdate"))) {
				try {
					healthRecordModel.saveHealthRecord("update", healthRecord);
				} catch (ModelDataOperationException e) {
					e.printStackTrace();
					throw new ServiceException("更新健康档案失败.", e);
				}
			}
		}
	}

	/**
	 * 处理家庭档案，如果有则判断是否更新户主，无则创建家庭档案,并返回FAMILY ID。
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param sc
	 * @param ctx
	 * @throws JSONException
	 * @throws PersistentDataOperationException
	 * @throws ModelDataOperationException
	 */
	private void updateFamilyIfo(String preferRelaCode,
			Map<String, Object> personInfo, BaseDAO dao)
			throws PersistentDataOperationException,
			ModelDataOperationException {
		String familyId = (String) personInfo.get("familyId");
		String newRelaCode = (String) personInfo.get("relaCode");
		String personName = (String) personInfo.get("personName");
		// 关系谱改变 ， 并前以前或者现在是户主。
		if (!newRelaCode.equals(preferRelaCode)
				&& (RelatedCode.MASTER.equals(newRelaCode) || RelatedCode.MASTER
						.equals(preferRelaCode))) {
			FamilyRecordModule familyModel = new FamilyRecordModule(dao);
			if (RelatedCode.MASTER.equals(newRelaCode))// 现在关系是户主，更新户主姓名
				familyModel.updateOnwerName(familyId, personName);
			else {// 以前是户主，清空家庭中户主姓名
				familyModel.updateOnwerName(familyId, "");
			}
		}
	}

	/**
	 * 个人既往史录入
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void doSavePastHistory(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HealthRecordModel hrModel = new HealthRecordModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String empiId = (String) body.get("empiId");
		String op = (String) req.get("op");
		try {
			if ("create".equals(op)) {
				hrModel.addPersonalPastHistory(op, body, ctx, vLogService);
			} else {
				hrModel.updatePersonalPastHistory(op, body, ctx, vLogService);
			}
			Map mixMap = hrModel.collectRelatedRecord(empiId);
			if (mixMap == null) {
				return;
			}
			HypertensionModel hyModel = new HypertensionModel(dao);
			hyModel.updateHypertensionRecord((Map<String, Object>) mixMap
					.get("hypertensionRecordCnd"));
			ChildrenBaseModel cbModel = new ChildrenBaseModel(dao);
			cbModel.updateHealthCard((Map<String, Object>) mixMap
					.get("childrenHealthCardCnd"));
			ChildrenHealthModel chModel = new ChildrenHealthModel(dao);
			chModel.updateDisabilityMonitorByEmpiId((Map<String, Object>) mixMap
					.get("disabilityMonitorCnd"));
			PregnantRecordModel prModel = new PregnantRecordModel(dao);
			prModel.updatePregnantRecord((Map<String, Object>) mixMap
					.get("pregnantRecordCnd"));
		} catch (ModelDataOperationException e) {
			logger.error("save pasthHistory is fail");
			throw new ServiceException(e);
		}
	}

	/**
	 * 与医疗业务联动个人既往史的保存
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void doSavePastHistoryHis(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HealthRecordModel hrModel = new HealthRecordModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String empiId = (String) body.get("empiId");
		String op = (String) req.get("op");

		// 根据empiid diseaseCode diseaseText 查询是否存在既往史存在就不做插入操作，不存在就插入
		List<Map> records = (List<Map>) body.get("record");

		try {

			for (int i = 0; i < records.size(); i++) {

				Map record = records.get(i);

				Map<String, Object> param = new HashMap<String, Object>();
				String hql = "select pasthistoryid as PASTHISTORYID from EHR_PastHistory where empiId=:empiId and diseaseCode=:diseaseCode and diseaseText=:diseaseText";
				param.put("empiId", record.get("empiId"));
				param.put("diseaseCode", record.get("diseaseCode"));
				param.put("diseaseText", record.get("diseaseText"));
				List cl = dao.doSqlQuery(hql, param);
				// 不存在则插入
				if (cl.size() < 1) {
					Map saveData = new HashMap();
					List saveList = new ArrayList();
					saveList.add(record);
					saveData.put("empiId", body.get("empiId"));
					saveData.put("record", saveList);

					if ("create".equals(op)) {
						hrModel.addPersonalPastHistory(op, saveData, ctx,
								vLogService);
					} else {
						hrModel.updatePersonalPastHistory(op, saveData, ctx,
								vLogService);
					}
					Map mixMap = hrModel.collectRelatedRecord(empiId);
					if (mixMap == null) {
						return;
					}
					HypertensionModel hyModel = new HypertensionModel(dao);
					hyModel.updateHypertensionRecord((Map<String, Object>) mixMap
							.get("hypertensionRecordCnd"));
					ChildrenBaseModel cbModel = new ChildrenBaseModel(dao);
					cbModel.updateHealthCard((Map<String, Object>) mixMap
							.get("childrenHealthCardCnd"));
					ChildrenHealthModel chModel = new ChildrenHealthModel(dao);
					chModel.updateDisabilityMonitorByEmpiId((Map<String, Object>) mixMap
							.get("disabilityMonitorCnd"));
					PregnantRecordModel prModel = new PregnantRecordModel(dao);
					prModel.updatePregnantRecord((Map<String, Object>) mixMap
							.get("pregnantRecordCnd"));
				}
			}

		} catch (ModelDataOperationException e) {
			logger.error("save pasthHistory is fail");
			throw new ServiceException(e);
		} catch (PersistentDataOperationException e) {
			logger.error("save pasthHistory is fail");
			throw new ServiceException(e);
		}
	}

	/**
	 * 个人主要问题录入
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSavePersonProblem(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String op = (String) req.get("op");
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			HealthRecordModel hrh = new HealthRecordModel(dao);
			Map<String, Object> data = hrh.savePersonalAndFamilyProblem(op,
					body);
			String sickRecordId = (String) body.get("sickRecordId");
			if ("create".equals(op)) {
				sickRecordId = (String) data.get("sickRecordId");
			}
			String empiId = (String) body.get("empiId");
			vLogService.saveVindicateLog(EHR_PersonProblem, op, sickRecordId,
					dao, empiId);
			res.put("body", data);
		} catch (ModelDataOperationException e) {
			logger.error("save PersonProblem is fail");
			throw new ServiceException(e.getCode(), e.getMessage(), e);
		}
	}

	/**
	 * 删除个人既往史
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void doDeletePastHistory(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HealthRecordModel hrModel = new HealthRecordModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String pastHistoryId = (String) body.get("pastHistoryId");
		String empiId = (String) body.get("empiId");
		try {
			hrModel.deletePastHistory(pastHistoryId);
			vLogService.saveVindicateLog(EHR_PastHistory, "4", pastHistoryId,
					dao, empiId);
			Map mixMap = hrModel.collectRelatedRecord(empiId);
			if (mixMap == null) {
				return;
			}
			HypertensionModel hyModel = new HypertensionModel(dao);
			hyModel.updateHypertensionRecord((Map<String, Object>) mixMap
					.get("hypertensionRecordCnd"));
			ChildrenBaseModel cbModel = new ChildrenBaseModel(dao);
			cbModel.updateHealthCard((Map<String, Object>) mixMap
					.get("childrenHealthCardCnd"));
			ChildrenHealthModel chModel = new ChildrenHealthModel(dao);
			chModel.updateDisabilityMonitorByEmpiId((Map<String, Object>) mixMap
					.get("disabilityMonitorCnd"));
			PregnantRecordModel prModel = new PregnantRecordModel(dao);
			prModel.updatePregnantRecord((Map<String, Object>) mixMap
					.get("pregnantRecordCnd"));
		} catch (ModelDataOperationException e) {
			logger.error("delete pasthHistory is fail");
			throw new ServiceException(e);
		}
	}

	/**
	 * 处理户主父亲与其他成员的关系。
	 * 
	 * @param relatedRecords
	 * @param husband
	 * @param ctx
	 * @throws JSONException
	 * @throws PersistentDataOperationException
	 * @throws ServiceException
	 */
	private void dealMasterFatherRelations(
			List<Map<String, Object>> relatedRecords,
			HashMap<String, Object> father, BaseDAO dao)
			throws ServiceException {
		if (false == checkEmpiInfo(father, dao))
			return;
		// 为户主设置户主父亲信息。
		HashMap<String, Object> master = selectRecordByRelaCode(relatedRecords,
				RelatedCode.MASTER);
		if (master != null) {
			String fatherId = (String) master.get("fatherId");
			if (fatherId == null || fatherId.trim().length() == 0) {
				master.put("fatherId", father.get("empiId"));
				master.put("fatherName", father.get("personName"));
				master.put("_doUpdate", "1");

			}
		}
		// 更新户主母亲配偶信息
		// 1.户主父亲中填写了配偶信息，为户主设置母亲信息，为户主母亲设置配偶信息。
		String motherId = (String) father.get("partnerId");
		String motherName = (String) father.get("partnerName");
		if (motherId != null && motherId.trim().length() > 0) {
			if (master != null) {
				String masterMotherId = (String) master.get("motherId");
				if (masterMotherId == null
						|| masterMotherId.trim().length() == 0) {
					master.put("motherId", motherId);
					master.put("motherName", motherName);
					master.put("_doUpdate", "1");
				}
			}
			HashMap<String, Object> mother = this.selectRecordsByEmpiId(
					relatedRecords, motherId);
			if (mother != null) {
				String partnerId = (String) mother.get("partnerId");
				if (partnerId == null || partnerId.trim().length() == 0) {
					mother.put("partnerId", father.get("empiId"));
					mother.put("partnerName", father.get("personName"));
					String motherRelaCode = (String) mother.get("relaCode");
					// 更新户主母亲 与户主关系。
					if (motherRelaCode == null
							|| motherRelaCode.trim().length() == 0)
						mother.put("relaCode", RelatedCode.MOTHER);
					mother.put("_doUpdate", "1");
				}
			}
		}
		// 2.如果户主填写了母亲信息，为户主父亲设置配偶信息。以户主父亲中填写的为优先。
		if (motherId == null || motherId.trim().length() == 0) {
			if (master != null) {
				motherId = (String) master.get("motherId");
				motherName = (String) master.get("motherName");
				if (motherId != null && motherId.trim().length() > 0) {
					if (!motherId.equals(father.get("empiId"))) {
						father.put("partnerId", motherId);
						father.put("partnerName", motherName);
						father.put("_doUpdate", "1");
					}
				}
				HashMap<String, Object> mother = selectRecordsByEmpiId(
						relatedRecords, motherId);
				if (mother != null) {
					String motherPartnerId = (String) mother.get("partnerId");
					if (motherPartnerId == null
							|| motherPartnerId.trim().length() == 0) {
						mother.put("partnerId", father.get("empiId"));
						mother.put("partnerName", father.get("personName"));
						mother.put("_doUpdate", "1");
					}
					String motherRelaCode = (String) mother.get("relaCode");
					if (motherRelaCode == null
							|| motherRelaCode.trim().length() == 0) {
						mother.put("relaCode", RelatedCode.MOTHER);
						mother.put("_doUpdate", "1");
					}
				}
			}
		}
		// 3.如果都没有填写，则去列表查找是否有户主母亲的记录。并设置户主父母亲的配偶信息。
		if (motherId == null || motherId.trim().length() == 0) {
			HashMap<String, Object> mother = selectRecordByRelaCode(
					relatedRecords, RelatedCode.MOTHER);
			if (mother != null) {
				checkEmpiInfo(mother, dao);
				father.put("partnerId", mother.get("empiId"));
				father.put("partnerName", mother.get("personName"));
				father.put("_doUpdate", "1");
				if (master != null) {
					master.put("motherId", mother.get("empiId"));
					master.put("motherName", mother.get("personName"));
					master.put("_doUpdate", "1");
				}
				String motherPartnerId = (String) mother.get("partnerId");
				if (motherPartnerId == null
						|| motherPartnerId.trim().length() == 0) {
					mother.put("partnerId", father.get("empiId"));
					mother.put("partnerName", father.get("personName"));
					mother.put("_doUpdate", "1");
				}
			}
		}
	}

	/**
	 * 处理户主母亲与其他成员的关系。
	 * 
	 * @param relatedRecords
	 * @param husband
	 * @param ctx
	 * @throws JSONException
	 * @throws PersistentDataOperationException
	 * @throws ServiceException
	 */
	private void dealMasterMotherRelations(
			List<Map<String, Object>> relatedRecords,
			HashMap<String, Object> mother, BaseDAO dao)
			throws ServiceException {
		if (false == checkEmpiInfo(mother, dao))
			return;
		// 为户主设置户主母亲信息。
		HashMap<String, Object> master = selectRecordByRelaCode(relatedRecords,
				RelatedCode.MASTER);
		if (master != null) {
			String motherId = (String) master.get("motherId");
			if (motherId == null || motherId.trim().length() == 0) {
				if (!master.get("empiId").equals(motherId)) {
					master.put("motherId", mother.get("empiId"));
					master.put("motherName", mother.get("personName"));
					master.put("_doUpdate", "1");
				}
			}
		}
		// 更新户主母亲配偶信息
		// 1.户主母亲中填写了配偶信息，为户主设置父亲信息，为户主父亲设置配偶信息。
		String fatherId = (String) mother.get("partnerId");
		String fatherName = (String) mother.get("partnerName");
		if (fatherId != null && fatherId.trim().length() > 0) {
			if (master != null) {
				String masterMother = (String) master.get("motherId");
				if (masterMother == null || masterMother.trim().length() == 0) {
					if (!fatherId.equals(master.get("empiId"))) {
						master.put("fatherId", fatherId);
						master.put("fatherName", fatherName);
						master.put("_doUpdate", "1");
					}
				}
			}
			HashMap<String, Object> father = this.selectRecordsByEmpiId(
					relatedRecords, fatherId);
			if (father != null) {
				String partnerId = (String) father.get("partnerId");
				if (partnerId == null || partnerId.trim().length() == 0) {
					father.put("partnerId", mother.get("empiId"));
					father.put("partnerName", mother.get("personName"));
					String fatherRelaCode = (String) father.get("relaCode");
					// 更新户主母亲 与户主关系。
					if (fatherRelaCode == null
							|| fatherRelaCode.trim().length() == 0)
						father.put("relaCode", RelatedCode.FATHER);
					father.put("_doUpdate", "1");
				}
			}
		}
		// 2.如果户主填写了父亲信息，为户主母亲设置配偶信息。以户主母亲中填写的为优先。
		if (fatherId == null || fatherId.trim().length() == 0) {
			if (master != null) {
				fatherId = (String) master.get("fatherId");
				fatherName = (String) master.get("fatherName");
				if (fatherId != null && fatherId.trim().length() > 0) {
					if (!fatherId.equals(mother.get("empiId"))) {
						mother.put("partnerId", fatherId);
						mother.put("partnerName", fatherName);
						mother.put("_doUpdate", "1");
					}
				}
				HashMap<String, Object> father = selectRecordsByEmpiId(
						relatedRecords, fatherId);
				if (father != null) {
					String fatherPartnerId = (String) father.get("partnerId");
					if (fatherPartnerId == null
							|| fatherPartnerId.trim().length() == 0) {
						father.put("partnerId", mother.get("empiId"));
						father.put("partnerName", mother.get("personName"));
						father.put("_doUpdate", "1");
					}
					String fatherRelaCode = (String) father.get("relaCode");
					if (fatherRelaCode == null
							|| fatherRelaCode.trim().length() == 0) {
						father.put("relaCode", RelatedCode.FATHER);
						father.put("_doUpdate", "1");
					}
				}
			}
		}
		// 3.如果都没有填写，则去列表查找是否有户主父亲的记录。并设置户主父母亲的配偶信息。
		if (fatherId == null || fatherId.trim().length() == 0) {
			HashMap<String, Object> father = selectRecordByRelaCode(
					relatedRecords, RelatedCode.FATHER);
			if (father != null) {
				checkEmpiInfo(father, dao);
				mother.put("partnerId", father.get("empiId"));
				mother.put("partnerName", father.get("personName"));
				mother.put("_doUpdate", "1");
				if (master != null) {
					master.put("fatherId", father.get("empiId"));
					master.put("fatherName", father.get("personName"));
					master.put("_doUpdate", "1");
				}

				String fatherPartnerId = (String) father.get("partnerId");
				if (fatherPartnerId == null
						|| fatherPartnerId.trim().length() == 0) {
					father.put("partnerId", mother.get("empiId"));
					father.put("partnerName", mother.get("personName"));
					father.put("_doUpdate", "1");
				}
			}
		}
	}

	/**
	 * 处理户主配偶与其他家庭成员的关系。
	 * 
	 * @param relatedRecords
	 * @param master
	 * @param ctx
	 * @throws JSONException
	 * @throws PersistentDataOperationException
	 * @throws ServiceException
	 */
	private void dealMasterPartnerRelations(
			List<Map<String, Object>> relatedRecords,
			HashMap<String, Object> partner, BaseDAO dao)
			throws ServiceException {
		if (false == this.checkEmpiInfo(partner, dao))
			return;
		// 更新户主的配偶信息‘更新本身的配偶信息
		HashMap<String, Object> master = null;
		String masterId = (String) partner.get("partnerId");
		if (masterId != null && masterId.trim().length() > 0) {
			master = selectRecordsByEmpiId(relatedRecords, masterId);
			if (master != null) {
				String masterPartnerId = (String) master.get("partnerId");
				if (masterPartnerId == null
						|| masterPartnerId.trim().length() == 0) {
					if (master.get("empiId") != null
							&& !master.get("empiId").equals(masterPartnerId)) {
						master.put("partnerId", partner.get("empiId"));
						master.put("partnerName", partner.get("personName"));
						master.put("_doUpdate", "1");
					}
				}
			}
		} else {
			master = this.selectRecordByRelaCode(relatedRecords,
					RelatedCode.MASTER);
			if (master != null) {
				String partnerId = (String) partner.get("partnerId");
				if (partnerId != null && partnerId.trim().length() > 0) {
					String ppartnerId = (String) master.get("partnerId");
					if (ppartnerId == null || ppartnerId.trim().length() == 0) {
						if (master.get("empiId") != null
								&& !master.get("empiId").equals(ppartnerId))
							master.put("partnerId", partner.get("empiId"));
						master.put("partnerName", partner.get("personName"));
						master.put("_doUpdate", "1");
					}
				} else {
					checkEmpiInfo(master, dao);

					if (!master.get("empiId").equals(partner.get("empiId"))) {
						partner.put("partnerId", master.get("empiId"));
						partner.put("partnerName", master.get("personName"));
						partner.put("_doUpdate", "1");
						master.put("partnerId", partner.get("empiId"));
						master.put("partnerName", partner.get("personName"));
						master.put("_doUpdate", "1");
					}
				}
			}
		}

		HashMap<String, Object> mother = master;
		HashMap<String, Object> father = partner;

		String sexCode = (String) partner.get("sexCode");
		if (Gender.WOMEN.equals(sexCode)) {
			father = master;
			mother = partner;
		}

		// 更新子女母亲父亲
		List<Map<String, Object>> childrenRecords = selectRecordsByRelaCodes(
				relatedRecords, new String[] { RelatedCode.FIFTH_DAUGHTER,
						RelatedCode.FIFTH_SON, RelatedCode.FIRST_DAUGHTER,
						RelatedCode.FIRST_SON, RelatedCode.FOURTH_DAUGHTER,
						RelatedCode.FOURTH_SON, RelatedCode.ONLY_DAUGHTER,
						RelatedCode.ONLY_SON, RelatedCode.OTHER_DAUGHTER,
						RelatedCode.OTHER_SON, RelatedCode.SECOND_DAUGHTER,
						RelatedCode.SECOND_SON, RelatedCode.STEP_DAUGHTER,
						RelatedCode.STEP_SON, RelatedCode.THIRD_DAUGHTER,
						RelatedCode.THIRD_SON });
		for (int i = 0; i < childrenRecords.size(); i++) {
			HashMap<String, Object> childRecord = (HashMap<String, Object>) childrenRecords
					.get(i);
			if (mother != null) {
				String motherId = (String) childRecord.get("motherId");
				if (motherId == null || motherId.trim().length() == 0) {
					if (childRecord.get("empiId") != null
							&& !childRecord.get("empiId").equals(motherId)) {
						childRecord.put("motherId", mother.get("empiId"));
						childRecord.put("motherName", mother.get("personName"));
						childRecord.put("_doUpdate", "1");
					}
				}
			}
			if (father != null) {
				String fatherId = (String) childRecord.get("fatherId");
				if (fatherId == null || fatherId.trim().length() == 0) {
					if (childRecord.get("empiId") != null
							&& !childRecord.get("empiId").equals(fatherId)) {
						childRecord.put("fatherId", father.get("empiId"));
						childRecord.put("fatherName", father.get("personName"));
						childRecord.put("_doUpdate", "1");
					}
				}
			}
		}
	}

	/**
	 * 处理户主与其他家庭成员的关系。
	 * 
	 * @param relatedRecords
	 * @param master
	 * @param ctx
	 * @throws JSONException
	 * @throws PersistentDataOperationException
	 * @throws ServiceException
	 */
	private void dealMasterRelatoins(List<Map<String, Object>> relatedRecords,
			HashMap<String, Object> master, BaseDAO dao)
			throws ServiceException {
		if (false == checkEmpiInfo(master, dao))
			return;
		String sexCode = (String) master.get("sexCode");
		String partnerCode = RelatedCode.WIFE;
		if (Gender.WOMEN.equals(sexCode)) {
			partnerCode = RelatedCode.HUSBAND;
		}
		HashMap<String, Object> masterPartner = null;
		String partnerId = (String) master.get("partnerId");
		if (partnerId != null && partnerId.trim().length() > 0) {
			masterPartner = this.selectRecordsByEmpiId(relatedRecords,
					partnerId);
			if (masterPartner != null) {
				String ppartnerId = (String) masterPartner.get("partnerId");
				if (ppartnerId == null || ppartnerId.trim().length() == 0) {
					if (masterPartner.get("empiId") != null
							&& !masterPartner.get("empiId").equals(ppartnerId)) {
						masterPartner.put("partnerId", master.get("empiId"));
						masterPartner.put("partnerName",
								master.get("personName"));
						masterPartner.put("_doUpdate", "1");
					}
				}
				String ppartnerRelaCode = (String) masterPartner
						.get("relaCode");
				if (ppartnerRelaCode == null
						|| ppartnerRelaCode.trim().length() == 0) {
					masterPartner.put("relaCode", partnerCode);
					masterPartner.put("_doUpdate", "1");
				}
			}
		} else {
			masterPartner = selectRecordByRelaCode(relatedRecords, partnerCode);
			if (masterPartner != null) {// 存在户主配偶.
				checkEmpiInfo(masterPartner, dao);
				String ppartnerId = (String) masterPartner.get("partnerId");
				if (ppartnerId == null || ppartnerId.trim().length() == 0) {
					if (masterPartner.get("empiId") != null
							&& !masterPartner.get("empiId").equals(ppartnerId)) {
						masterPartner.put("partnerId", master.get("empiId"));
						masterPartner.put("partnerName",
								master.get("personName"));
						// partnerRec.put("relaCode",
						// partnerCode);//户主妻子。-----------
						masterPartner.put("_doUpdate", "1");// 此记录需要UPDATE
					}
				}
				// 更新户主配偶为partnerRec
				if (!master.get("empiId").equals(masterPartner.get("empiId"))) {
					master.put("partnerId", masterPartner.get("empiId"));
					master.put("partnerName", masterPartner.get("personName"));
					master.put("_doUpdate", "1");
				}
			}
		}

		HashMap<String, Object> father = null;
		HashMap<String, Object> mother = null;
		if (RelatedCode.HUSBAND.equals(sexCode)) {
			father = master;
			mother = masterPartner;
		} else {
			father = masterPartner;
			mother = master;
		}

		// /////更新子女父母信息。
		List<Map<String, Object>> childrenRecords = selectRecordsByRelaCodes(
				relatedRecords, new String[] { RelatedCode.FIFTH_DAUGHTER,
						RelatedCode.FIFTH_SON, RelatedCode.FIRST_DAUGHTER,
						RelatedCode.FIRST_SON, RelatedCode.FOURTH_DAUGHTER,
						RelatedCode.FOURTH_SON, RelatedCode.ONLY_DAUGHTER,
						RelatedCode.ONLY_SON, RelatedCode.OTHER_DAUGHTER,
						RelatedCode.OTHER_SON, RelatedCode.SECOND_DAUGHTER,
						RelatedCode.SECOND_SON, RelatedCode.STEP_DAUGHTER,
						RelatedCode.STEP_SON, RelatedCode.THIRD_DAUGHTER,
						RelatedCode.THIRD_SON });
		for (int i = 0; i < childrenRecords.size(); i++) {
			HashMap<String, Object> childRecord = (HashMap<String, Object>) childrenRecords
					.get(i);
			if (mother != null) {
				String motherId = (String) childRecord.get("motherId");
				if (motherId == null || motherId.trim().length() == 0) {
					if (childRecord.get("empiId") != null
							&& !childRecord.get("empiId").equals(motherId)) {
						childRecord.put("motherId", mother.get("empiId"));
						childRecord.put("motherName", mother.get("personName"));
						childRecord.put("_doUpdate", "1");
					}
				}
			}
			if (father != null) {
				String fatherId = (String) childRecord.get("fatherId");
				if (fatherId == null || fatherId.trim().length() == 0) {
					if (childRecord.get("empiId") != null
							&& !childRecord.get("empiId").equals(father)) {
						childRecord.put("fatherId", father.get("empiId"));
						childRecord.put("fatherName", father.get("personName"));
						childRecord.put("_doUpdate", "1");
					}
				}
			}
		}

		// //////更新父亲信息
		String fatherId = (String) master.get("fatherId");
		if (fatherId != null && fatherId.trim().length() > 0) {
			HashMap<String, Object> masterFather = selectRecordsByEmpiId(
					relatedRecords, fatherId);
			if (masterFather != null) {
				String fatherRelaCode = (String) masterFather.get("relaCode");
				if (fatherRelaCode == null
						|| fatherRelaCode.trim().length() == 0) {
					masterFather.put("relaCode", RelatedCode.FATHER);
					masterFather.put("_doUpdate", "1");
				}
			}
		} else {
			HashMap<String, Object> masterFather = selectRecordByRelaCode(
					relatedRecords, RelatedCode.FATHER);
			if (masterFather != null) {
				checkEmpiInfo(masterFather, dao);
				if (!masterFather.get("empiId").equals(master.get("empiId"))) {
					master.put("fatherId", masterFather.get("empiId"));
					master.put("fatherName", masterFather.get("personName"));
					master.put("_doUpdate", "1");
				}
			}
		}
		// //////更新母亲信息
		String motherId = (String) master.get("motherId");
		if (motherId != null && motherId.trim().length() > 0) {
			HashMap<String, Object> masterMather = selectRecordsByEmpiId(
					relatedRecords, motherId);
			if (masterMather != null) {
				String matherRelaCode = (String) masterMather.get("relaCode");
				if (matherRelaCode == null
						|| matherRelaCode.trim().length() == 0) {
					masterMather.put("relaCode", RelatedCode.MOTHER);
					masterMather.put("_doUpdate", "1");
				}
			}
		} else {
			HashMap<String, Object> masterMother = selectRecordByRelaCode(
					relatedRecords, RelatedCode.MOTHER);
			if (masterMother != null) {
				checkEmpiInfo(masterMother, dao);
				if (!masterMother.get("empiId").equals(master.get("empiId"))) {
					master.put("motherId", masterMother.get("empiId"));
					master.put("motherName", masterMother.get("personName"));
					master.put("_doUpdate", "1");
				}
			}
		}
	}

	/**
	 * copy 清空关系改变后相关成员的瓜葛。
	 * 
	 * @param phrId
	 * @param oldRelaCode
	 * @param relatedRecords
	 * @param session
	 * @param context
	 * @throws JSONException
	 * @throws PersistentDataOperationException
	 * @throws ServiceException
	 */
	@SuppressWarnings("static-access")
	private void clearRelations(String phrId, String oldRelaCode,
			List<Map<String, Object>> relatedRecords, BaseDAO dao)
			throws ServiceException {
		HashMap<String, Object> changedRecord = selectRecordByPhrId(
				relatedRecords, phrId);
		if (changedRecord == null)
			return;
		changedRecord.put("_doUpdate", "1");
		if (RelatedCode.HUSBAND.equals(oldRelaCode)) {// ////////////////////户主丈夫
			changedRecord.put("partnerId", "");
			changedRecord.put("partnerName", "");
			// 找出户主 清空配偶信息
			HashMap<String, Object> masterRec = this.selectRecordByRelaCode(
					relatedRecords, RelatedCode.MASTER);
			if (masterRec != null) {
				masterRec.put("partnerId", "");
				masterRec.put("partnerName", "");
				masterRec.put("_doUpdate", "1");
			}
			// 找出户主子女 清空父亲信息
			@SuppressWarnings("unchecked")
			HashMap<String, Object> records = (HashMap<String, Object>) this
					.selectRecordsByRelaCodes(relatedRecords, new String[] {
							RelatedCode.FIFTH_DAUGHTER, RelatedCode.FIFTH_SON,
							RelatedCode.FIRST_DAUGHTER, RelatedCode.FIRST_SON,
							RelatedCode.FOURTH_DAUGHTER,
							RelatedCode.FOURTH_SON, RelatedCode.ONLY_DAUGHTER,
							RelatedCode.ONLY_SON, RelatedCode.OTHER_DAUGHTER,
							RelatedCode.OTHER_SON, RelatedCode.SECOND_DAUGHTER,
							RelatedCode.SECOND_SON, RelatedCode.STEP_DAUGHTER,
							RelatedCode.STEP_SON, RelatedCode.THIRD_DAUGHTER,
							RelatedCode.THIRD_SON });
			for (int i = 0; i < records.size(); i++) {
				@SuppressWarnings("unchecked")
				HashMap<String, Object> record = (HashMap<String, Object>) records
						.get(i);
				record.put("fatherId", "");
				record.put("fatherName", "");
				record.put("_doUpdate", "1");
			}
		} else if (RelatedCode.WIFE.equals(oldRelaCode)) {// //////////////////户主妻子
			changedRecord.put("partnerId", "");
			changedRecord.put("partnerName", "");
			// 找出户主 清空配偶信息
			HashMap<String, Object> masterRec = this.selectRecordByRelaCode(
					relatedRecords, RelatedCode.MASTER);
			if (masterRec != null) {
				masterRec.put("partnerId", "");
				masterRec.put("partnerName", "");
				masterRec.put("_doUpdate", "1");
			}
			// 找出户主子女 清空母亲信息
			List<Map<String, Object>> records = this.selectRecordsByRelaCodes(
					relatedRecords, new String[] { RelatedCode.FIFTH_DAUGHTER,
							RelatedCode.FIFTH_SON, RelatedCode.FIRST_DAUGHTER,
							RelatedCode.FIRST_SON, RelatedCode.FOURTH_DAUGHTER,
							RelatedCode.FOURTH_SON, RelatedCode.ONLY_DAUGHTER,
							RelatedCode.ONLY_SON, RelatedCode.OTHER_DAUGHTER,
							RelatedCode.OTHER_SON, RelatedCode.SECOND_DAUGHTER,
							RelatedCode.SECOND_SON, RelatedCode.STEP_DAUGHTER,
							RelatedCode.STEP_SON, RelatedCode.THIRD_DAUGHTER,
							RelatedCode.THIRD_SON });
			for (int i = 0; i < records.size(); i++) {
				HashMap<String, Object> record = (HashMap<String, Object>) records
						.get(i);
				record.put("motherId", "");
				record.put("motherName", "");
				record.put("_doUpdate", "1");
			}
		} else if (oldRelaCode != null && !"".equals(oldRelaCode)
				&& "2".equals(oldRelaCode.substring(0, 1))
				&& !RelatedCode.SON_IN_LOW.equals(oldRelaCode)) {// /////////////////户主儿子
			changedRecord.put("fatherId", "");
			changedRecord.put("fatherName", "");
			changedRecord.put("motherId", "");
			changedRecord.put("motherName", "");
		} else if (oldRelaCode != null && !"".equals(oldRelaCode)
				&& "3".equals(oldRelaCode.substring(0, 1))
				&& !RelatedCode.STEP_DAUGHTER.equals(oldRelaCode)) {// /////////////户主女儿
			changedRecord.put("fatherId", "");
			changedRecord.put("fatherName", "");
			changedRecord.put("motherId", "");
			changedRecord.put("motherName", "");
		} else if (RelatedCode.FATHER.equals(oldRelaCode)) {// ///////////////////户主父亲
			changedRecord.put("partnerId", "");
			changedRecord.put("partnerName", "");
			// 找出户主母亲 清空配偶
			HashMap<String, Object> motherRec = this.selectRecordByRelaCode(
					relatedRecords, RelatedCode.MOTHER);
			if (motherRec != null) {
				motherRec.put("partnerId", "");
				motherRec.put("partnerName", "");
				motherRec.put("_doUpdate", "1");
			}
			// 找出户主 清空父亲
			HashMap<String, Object> masterRec = this.selectRecordByRelaCode(
					relatedRecords, RelatedCode.MASTER);
			if (masterRec != null) {
				masterRec.put("fatherId", "");
				masterRec.put("fatherName", "");
				masterRec.put("_doUpdate", "1");
			}

		} else if (RelatedCode.MOTHER.equals(oldRelaCode)) {// ////////////////////户主母亲
			changedRecord.put("partnerId", "");
			changedRecord.put("partnerName", "");
			// 找出户主父亲 清空配偶
			HashMap<String, Object> fatherRec = this.selectRecordByRelaCode(
					relatedRecords, RelatedCode.FATHER);
			if (fatherRec != null) {
				fatherRec.put("partnerId", "");
				fatherRec.put("partnerName", "");
				fatherRec.put("_doUpdate", "1");
			}
			// 找出户主 清空母亲
			HashMap<String, Object> masterRec = this.selectRecordByRelaCode(
					relatedRecords, RelatedCode.MASTER);
			if (masterRec != null) {
				masterRec.put("motherId", "");
				masterRec.put("motherName", "");
				masterRec.put("_doUpdate", "1");
			}

		} else if (RelatedCode.MASTER.equals(oldRelaCode)) {// /////////////////////户主
			changedRecord.put("fatherId", "");
			changedRecord.put("fatherName", "");
			changedRecord.put("motherId", "");
			changedRecord.put("motherName", "");
			changedRecord.put("partnerId", "");
			changedRecord.put("partnerName", "");

			checkEmpiInfo(changedRecord, dao);
			String sexCode = (String) changedRecord.get("sexCode");
			if (Gender.MAN.equals(sexCode)) {// 户主是男人
				// 找出户主妻子 清空配偶
				HashMap<String, Object> wifeRec = this.selectRecordByRelaCode(
						relatedRecords, RelatedCode.WIFE);
				if (wifeRec != null) {
					wifeRec.put("partnerId", "");
					wifeRec.put("partnerName", "");
					wifeRec.put("_doUpdate", "1");
				}
				// 找出户主子女 清空父亲
				List<Map<String, Object>> records = this
						.selectRecordsByRelaCodes(relatedRecords, new String[] {
								RelatedCode.FIFTH_DAUGHTER,
								RelatedCode.FIFTH_SON,
								RelatedCode.FIRST_DAUGHTER,
								RelatedCode.FIRST_SON,
								RelatedCode.FOURTH_DAUGHTER,
								RelatedCode.FOURTH_SON,
								RelatedCode.ONLY_DAUGHTER,
								RelatedCode.ONLY_SON,
								RelatedCode.OTHER_DAUGHTER,
								RelatedCode.OTHER_SON,
								RelatedCode.SECOND_DAUGHTER,
								RelatedCode.SECOND_SON,
								RelatedCode.STEP_DAUGHTER,
								RelatedCode.STEP_SON,
								RelatedCode.THIRD_DAUGHTER,
								RelatedCode.THIRD_SON });
				for (int i = 0; i < records.size(); i++) {
					HashMap<String, Object> record = (HashMap<String, Object>) records
							.get(i);
					record.put("fatherId", "");
					record.put("fatherName", "");
					record.put("_doUpdate", "1");
				}

			} else if (Gender.WOMEN.equals(sexCode)) {// 户主是女人
				// 找出户主丈夫 清空配偶
				HashMap<String, Object> husbandRec = this
						.selectRecordByRelaCode(relatedRecords,
								RelatedCode.HUSBAND);
				if (husbandRec != null) {
					husbandRec.put("partnerId", "");
					husbandRec.put("partnerName", "");
					husbandRec.put("_doUpdate", "1");
				}
				// 找出户主子女 清空母亲
				List<Map<String, Object>> records = this
						.selectRecordsByRelaCodes(relatedRecords, new String[] {
								RelatedCode.FIFTH_DAUGHTER,
								RelatedCode.FIFTH_SON,
								RelatedCode.FIRST_DAUGHTER,
								RelatedCode.FIRST_SON,
								RelatedCode.FOURTH_DAUGHTER,
								RelatedCode.FOURTH_SON,
								RelatedCode.ONLY_DAUGHTER,
								RelatedCode.ONLY_SON,
								RelatedCode.OTHER_DAUGHTER,
								RelatedCode.OTHER_SON,
								RelatedCode.SECOND_DAUGHTER,
								RelatedCode.SECOND_SON,
								RelatedCode.STEP_DAUGHTER,
								RelatedCode.STEP_SON,
								RelatedCode.THIRD_DAUGHTER,
								RelatedCode.THIRD_SON });
				for (int i = 0; i < records.size(); i++) {
					HashMap<String, Object> record = (HashMap<String, Object>) records
							.get(i);
					record.put("motherId", "");
					record.put("motherName", "");
					record.put("_doUpdate", "1");
				}
			}
		}
	}

	/**
	 * 保证healthRecord中包含了性别、名字信息。 并且判断记录的性别信息是否支持进行成员关系更新（性别不详无法更新）。
	 * 
	 * @param healthRecord
	 * @param ctx
	 * @return
	 * @throws ServiceException
	 * @throws JSONException
	 * @throws PersistentDataOperationException
	 * @throws ModelDataOperationException
	 */
	private boolean checkEmpiInfo(Map<String, Object> healthRecord, BaseDAO dao)
			throws ServiceException {
		String sexCode = (String) healthRecord.get("sexCode");
		if (sexCode == null || sexCode.trim().length() == 0) {
			String empiId = (String) healthRecord.get("empiId");
			EmpiModel empiModel = new EmpiModel(dao);
			Map<String, Object> empiInfo = null;
			try {
				empiInfo = empiModel.getEmpiInfoByEmpiid(empiId);
			} catch (ModelDataOperationException e) {
				e.printStackTrace();
				throw new ServiceException("获取个人基本信息失败.", e);
			}
			sexCode = (String) empiInfo.get("sexCode");
			if (sexCode == null || sexCode.trim().length() == 0) {
				return false;
			}
			if (!Gender.MAN.equals(sexCode) && !Gender.WOMEN.equals(sexCode)) {
				return false;
			}
			healthRecord.put("sexCode", sexCode);
			healthRecord.put("personName", empiInfo.get("personName"));
		}
		return true;
	}

	// 根据phrId从列表中找出相应的记录。
	private HashMap<String, Object> selectRecordByPhrId(
			List<Map<String, Object>> list, String phrId) {
		if (list == null || list.size() == 0) {
			return null;
		}

		for (int i = 0; i < list.size(); i++) {
			HashMap<String, Object> record = (HashMap<String, Object>) list
					.get(i);
			String rphrId = (String) record.get("phrId");
			if (phrId.equals(rphrId)) {
				return record;
			}
		}
		return null;
	}

	// 根据关系代码在列表中找出单一关系相应的记录。
	private HashMap<String, Object> selectRecordByRelaCode(
			List<Map<String, Object>> list, String code) {
		if (list == null || list.size() == 0) {
			return null;
		}

		List<Map<String, Object>> arr = selectRecordsByRelaCodes(list,
				new String[] { code });
		if (arr == null || arr.size() == 0) {
			return null;
		}
		return (HashMap<String, Object>) arr.get(0);
	}

	// 根据关系代码在列表中找出相应的记录。
	public static List<Map<String, Object>> selectRecordsByRelaCodes(
			List<Map<String, Object>> list, String[] codes) {
		if (list == null || list.size() == 0)
			return new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> arr = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < list.size(); i++) {
			HashMap<String, Object> record = (HashMap<String, Object>) list
					.get(i);
			for (int j = 0; j < codes.length; j++) {
				if (codes[j].equals(record.get("relaCode")))
					arr.add(record);
			}
		}
		return arr;
	}

	// 根据EMPIID在列表中找出相应的记录。
	private HashMap<String, Object> selectRecordsByEmpiId(
			List<Map<String, Object>> list, String empiId) {
		if (empiId == null || list == null || list.size() == 0) {
			return null;
		}

		for (int i = 0; i < list.size(); i++) {
			HashMap<String, Object> record = (HashMap<String, Object>) list
					.get(i);
			String rEmpiId = (String) record.get("empiId");
			if (empiId.equals(rEmpiId)) {
				return record;
			}
		}
		return null;
	}

	/**
	 * 主要完成以下功能：1、加载个人健康档案数据； 2、判断是否是户主，如果不是户主， 查找与户主关系； 3、档案中儿童父母亲信息与儿童档案不一致
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doLoadHealthRecordData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {

		HealthRecordModel hrModel = new HealthRecordModel(dao);
		ChildrenHealthModel chModel = new ChildrenHealthModel(dao);

		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String empiId = (String) body.get("empiId");
		String phrId = (String) body.get("phrId");

		try {
			Map<String, Object> healthRecord = hrModel
					.getHealthRecordByEmpiId(empiId);
			healthRecord = SchemaUtil.setDictionaryMessageForForm(healthRecord,
					EHR_HealthRecord);
			if (healthRecord == null) {
				res.put("body", body);
				return;
			}
			body.putAll(healthRecord);

			Map<String, String> regionCode = (Map<String, String>) body
					.get("regionCode");

			// 判断该网格地址是否为户这一级
			if (regionCode != null) {
				AreaGridModel agModel = new AreaGridModel(dao);
				Map<String, Object> areaGridMap = agModel
						.getAreaGridByRegionCode(regionCode.get("key"));
				if (areaGridMap != null) {
					String isFamily = (String) areaGridMap.get("isFamily");
					if (IsFamily.FAMILY.equals(isFamily)) {
						body.put("isFamily", true);
					}
				}
			}

			String familyId = (String) healthRecord.get("familyId");
			if (familyId != null && !familyId.equals("")) {
				Map<String, Object> familyRecord = hrModel
						.getFamilyRecordById(familyId);
				if (familyRecord != null) {
					String ownerName = (String) familyRecord.get("ownerName");
					body.put("ownerName", ownerName);

					Map<String, Object> data = hrModel.getMasterInfo(familyId,
							empiId);
					boolean isMaster = false;
					boolean hasMaster = false;
					if (data != null) {
						hasMaster = true;
						String masterPhrId = (String) data.get("phrId");
						if (masterPhrId.equals(phrId)) {
							isMaster = true;
						}
					}

					body.put("hasMaster", hasMaster);
					body.put("isMaster", isMaster);
				}
			}

			Map<String, Object> cdhMap = chModel
					.getChildHealthCardByPhrId(phrId);
			boolean isSame = checkChildParent(healthRecord, cdhMap);
			body.put("isSame", isSame);

			EmpiModel em = new EmpiModel(dao);
			Map<String, Object> perInfo = em.getEmpiInfoByEmpiid(empiId);
			if (perInfo != null) {
				body.put("maritalStatusCode", perInfo.get("maritalStatusCode"));
			}

			res.put("body", body);
		} catch (ModelDataOperationException e) {
			logger.error("Search MaritalStatus is fail");
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 检查儿童父母信息是否一致
	 * 
	 * @param ehrMap
	 * @param cdhMap
	 * @return
	 */
	protected boolean checkChildParent(Map<String, Object> ehrMap,
			Map<String, Object> cdhMap) {
		boolean isSame = true;
		if (cdhMap == null) {
			return isSame;
		}
		String ehrFather = (String) ehrMap.get("fatherId");
		String cdhFather = (String) cdhMap.get("fatherEmpiId");
		String ehrFatherId = ehrFather != null ? ehrFather : "";
		String cdhFatherId = cdhFather != null ? cdhFather : "";
		if (!ehrFatherId.equalsIgnoreCase(cdhFatherId)) {
			isSame = false;
		}

		String ehrMother = (String) ehrMap.get("motherId");
		String cdhMother = (String) cdhMap.get("motherEmpiId");
		String ehrMotherId = ehrMother != null ? ehrMother : "";
		String cdhMotherId = cdhMother != null ? cdhMother : "";
		if (!ehrMotherId.equalsIgnoreCase(cdhMotherId)) {
			isSame = false;
		}

		return isSame;
	}

	/**
	 * 检查与户主的关系，然后查询父亲、目前、配偶等匹配信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doDealFamilyRelations(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HealthRecordModel hrModel = new HealthRecordModel(dao);
		EmpiModel empiModel = new EmpiModel(dao);

		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String empiId = (String) body.get("empiId");
		String relaCode = (String) body.get("relaCode");
		String regionCode = (String) body.get("regionCode");
		String sexCode = (String) body.get("sexCode");
		String familyId = (String) body.get("familyId");
		res.put("body", body);

		try {
			List<Map<String, Object>> relatedRecords = null;
			if (familyId != null && familyId.trim().length() > 0) {
				relatedRecords = hrModel.getRelatedRecordByFamilyId(familyId);
			} else if (regionCode != null && regionCode.trim().length() > 0) {
				relatedRecords = hrModel.getRelatedRecordByRegionCode(regionCode);
			}
			Map<String, Object> relation = checkRelations(relaCode, sexCode,relatedRecords);
			body.putAll(relation);
			
			if (relatedRecords.isEmpty()) {
				return;
			}

			Map<String, Object> masterMap = findRecordByRelaCode(
					RelatedCode.MASTER, relatedRecords);
			if (masterMap != null) {
				checkEmpiInfo(masterMap, dao);
				body.put("master", masterMap);
			}

			if (RelatedCode.MASTER.equals(relaCode)) {
				Map<String, Object> fatherMap = findRecordByRelaCode(
						RelatedCode.FATHER, relatedRecords);
				if (fatherMap != null) {
					checkEmpiInfo(fatherMap, dao);
					body.put("father", fatherMap);
				}
				Map<String, Object> motherMap = findRecordByRelaCode(
						RelatedCode.MOTHER, relatedRecords);
				if (motherMap != null) {
					checkEmpiInfo(motherMap, dao);
					body.put("mother", motherMap);
				}
				if (masterMap == null) {
					masterMap = empiModel.getEmpiInfoByEmpiid(empiId);
				}
				String masterSexCode = (String) masterMap.get("sexCode");
				String partnerCode = RelatedCode.WIFE;
				if (Gender.WOMEN.equals(masterSexCode)) {
					partnerCode = RelatedCode.HUSBAND;
				}
				Map<String, Object> partnerMap = findRecordByRelaCode(
						partnerCode, relatedRecords);
				if (partnerMap != null) {
					checkEmpiInfo(partnerMap, dao);
					body.put("partner", partnerMap);
				}
				return;
			}

			if (RelatedCode.WIFE.equals(relaCode)
					|| RelatedCode.HUSBAND.equals(relaCode)) {
				if (masterMap != null) {
					body.put("partner", masterMap);
				}
				return;
			}
			if ((relaCode != null && !"".equals(relaCode)
					&& "2".equals(relaCode.substring(0, 1)) && !RelatedCode.SON_IN_LOW
						.equals(relaCode))
					|| (relaCode != null && !"".equals(relaCode)
							&& "3".equals(relaCode.substring(0, 1)) && !RelatedCode.STEP_DAUGHTER
								.equals(relaCode))) {
				Map<String, Object> fatherMap = null;
				Map<String, Object> motherMap = null;
				if (masterMap == null) {
					fatherMap = findRecordByRelaCode(RelatedCode.HUSBAND,
							relatedRecords);
					motherMap = findRecordByRelaCode(RelatedCode.WIFE,
							relatedRecords);
				} else {
					if (masterMap.get("sexCode") != null
							&& Gender.MAN.equals(masterMap.get("sexCode"))) {
						fatherMap = masterMap;
						motherMap = findRecordByRelaCode(RelatedCode.WIFE,
								relatedRecords);
					} else if (masterMap.get("sexCode") != null
							&& Gender.WOMEN.equals(masterMap.get("sexCode"))) {
						motherMap = masterMap;
						fatherMap = findRecordByRelaCode(RelatedCode.HUSBAND,
								relatedRecords);
					}
				}

				if (fatherMap != null) {
					checkEmpiInfo(fatherMap, dao);
					body.put("father", fatherMap);
				}
				if (motherMap != null) {
					checkEmpiInfo(motherMap, dao);
					body.put("mother", motherMap);
				}
				return;
			}

			if (RelatedCode.MOTHER.equals(relaCode)) {
				Map<String, Object> fatherMap = findRecordByRelaCode(
						RelatedCode.FATHER, relatedRecords);
				if (fatherMap != null) {
					checkEmpiInfo(fatherMap, dao);
					body.put("partner", fatherMap);
				}
				return;
			}

			if (Gender.WOMEN.equals(relaCode)) {
				Map<String, Object> motherMap = findRecordByRelaCode(
						RelatedCode.MOTHER, relatedRecords);
				if (motherMap != null) {
					checkEmpiInfo(motherMap, dao);
					body.put("partner", motherMap);
				}
				return;
			}
		} catch (ModelDataOperationException e) {
			logger.error("Search MaritalStatus is fail");
			throw new ServiceException(e);
		}
	}

	/**
	 * 查找有重复关系代码的数据
	 * 
	 * @param relaCode
	 * @param healthRecrodList
	 * @return
	 */
	protected Map<String, Object> findRecordByRelaCode(String relaCode,
			List<Map<String, Object>> healthRecrodList) {
		if (healthRecrodList.isEmpty()) {
			return null;
		}
		for (int i = 0; i < healthRecrodList.size(); i++) {
			String midRelaCode = (String) healthRecrodList.get(i).get(
					"relaCode");
			if (midRelaCode != null && midRelaCode.equalsIgnoreCase(relaCode)) {
				return healthRecrodList.get(i);
			}
		}
		return null;
	}

	/**
	 * 检查关系是否正确
	 * 
	 * @param relaCode
	 * @param sexCode
	 * @param healthRecrodList
	 * @return
	 */
	protected Map<String, Object> checkRelations(String relaCode,
			String sexCode, List<Map<String, Object>> healthRecrodList) {
		Map<String, Object> checkResult = new HashMap<String, Object>();
		String message = "";
		boolean isExsit = false;

		Map<String, Object> husbandMap = findRecordByRelaCode(
				RelatedCode.HUSBAND, healthRecrodList);
		Map<String, Object> wifeMap = findRecordByRelaCode(RelatedCode.WIFE,
				healthRecrodList);
		Map<String, Object> masterMap = findRecordByRelaCode(
				RelatedCode.MASTER, healthRecrodList);
		Map<String, Object> fatherMap = findRecordByRelaCode(
				RelatedCode.FATHER, healthRecrodList);
		Map<String, Object> motherMap = findRecordByRelaCode(
				RelatedCode.MOTHER, healthRecrodList);

		if (relaCode.equals(RelatedCode.HUSBAND)) {
			if (masterMap != null) {
				if (masterMap.get("sexCode").equals(Gender.MAN)) {
					message = "家庭中已经存在性别为男的户主,只允许存在户主妻子!";
					isExsit = true;
				}
			}
			if (husbandMap != null) {
				message = "家庭中已经存在户主丈夫:" + husbandMap.get("personName");
				isExsit = true;
			}
		} else if (relaCode.equals(RelatedCode.WIFE)) {
			if (masterMap != null) {
				if (masterMap.get("sexCode").equals(Gender.WOMEN)) {
					message = "家庭中已经存在性别为女的户主,只允许存在户主丈夫!";
					isExsit = true;
				}
			}
			if (wifeMap != null) {
				message = "家庭中已经存在户主妻子:" + wifeMap.get("personName");
				isExsit = true;
			}
		} else if (fatherMap != null && relaCode.equals(RelatedCode.FATHER)) {
			message = "家庭中已经存在户主父亲:" + fatherMap.get("personName");
		} else if (motherMap != null && relaCode.equals(RelatedCode.MOTHER)) {
			message = "家庭中已经存在户主母亲:" + motherMap.get("personName");
		} else if (relaCode.equals(RelatedCode.MASTER)) {
			if (masterMap != null) {
				message = "家庭中已经存在户主:" + masterMap.get("personName");
				isExsit = true;
			}
			if (husbandMap != null && sexCode.equals(Gender.MAN)) {
				message = "家庭中已经存在户主丈夫:" + husbandMap.get("personName")
						+ ",户主性别应该是女性!";
				isExsit = true;
			}
			if (wifeMap != null && sexCode.equals(Gender.WOMEN)) {
				message = "家庭中已经存在户主妻子:" + wifeMap.get("personName")
						+ ",户主性别应该是男性!";
				isExsit = true;
			}
		}
		checkResult.put("message", message);
		checkResult.put("isExsit", isExsit);

		return checkResult;
	}

	/**
	 * 根据网格地址编码查找对应网格地址数据、家庭ID、是否存在户主、户主姓名等信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doFindInfoByRegionCode(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HealthRecordModel hrModel = new HealthRecordModel(dao);
		AreaGridModel agModel = new AreaGridModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String regionCode = (String) body.get("regionCode");
		String empiId = (String) body.get("empiId");
		res.put("body", body);
		try {
			if (empiId != null) {
				EmpiModel empiModel = new EmpiModel(dao);
				Map<String, Object> empiData = empiModel
						.getEmpiInfoByEmpiid(empiId);
				if (empiData == null) {
					return;
				}
				String maritalStatusCode = (String) empiData
						.get("maritalStatusCode");
				body.put("maritalStatusCode", maritalStatusCode);
				if (!Maritals.SINGLE.equals(maritalStatusCode)) {
					Map<String, Object> parterEhrData = hrModel
							.getPartnerHealthRecordByEmpiId(empiId);
					if (parterEhrData != null) {
						String partnerEmpiId = (String) parterEhrData
								.get("empiId");
						body.put("partnerId", partnerEmpiId);
						Map<String, Object> partnerEmpiData = empiModel
								.getEmpiInfoByEmpiid(partnerEmpiId);

						if (partnerEmpiData != null) {
							String partnerName = (String) partnerEmpiData
									.get("personName");
							body.put("partnerName", partnerName);
						}
					}
				}
			}

			Map<String, Object> nodeMap = agModel.getNodeInfo(regionCode);
			if (nodeMap != null) {
				String manaDoctor = (String) nodeMap.get("manaDoctor");
				body.putAll(SchemaUtil.setDictionaryMessageForForm(nodeMap,
						EHR_AreaGridChild));
				if (manaDoctor != null) {
					SystemUserModel suModel = new SystemUserModel(dao);
					List<Map<String, Object>> manageUnits = suModel
							.getUserByLogonName(manaDoctor, RolesList.ZRYS,
									RolesList.TDZ);
					body.put("manageUnits", manageUnits);
				}
			}

			String familyId = hrModel.getFamilyIdByRegionCode(regionCode);
			if (familyId.isEmpty()) {
				body.put("familyId", "");
				body.put("hasMaster", false);
				body.put("ownerName", "");
				return;
			}

			List<Map<String, Object>> masterRecord = hrModel
					.getHealthRecordByFamilyId(familyId);
			boolean hasMaster = false;
			if (masterRecord != null && masterRecord.size() > 0) {
				hasMaster = true;
			}

			Map<String, Object> familyRecord = hrModel
					.getFamilyRecordById(familyId);
			String ownerName = "";
			if (familyRecord != null) {
				ownerName = (String) familyRecord.get("ownerName");
			}

			body.put("familyId", familyId);
			body.put("hasMaster", hasMaster);
			body.put("ownerName", ownerName);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 个人生活习惯录入
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveLifeStyle(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		LifeStyleModel lsModel = new LifeStyleModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		Map<String, Object> resBody = new HashMap<String, Object>();
		String lifeStyleId = (String) body.get("lifeStyleId");
		res.put("body", resBody);
		try {
			Map<String, Object> record = lsModel.saveLifeStyle(op, body);
			if ("create".equals(op)) {
				lifeStyleId = (String) record.get("lifeStyleId");
			}
			resBody.put("lifeStyleId", lifeStyleId);
			String empiId = (String) body.get("empiId");
			vLogService.saveVindicateLog(EHR_LifeStyle, op, lifeStyleId, dao,
					empiId);
		} catch (ModelDataOperationException e) {
			logger.error("dave lifeStyle is fail");
			throw new ServiceException(e);
		}
	}

	/**
	 * 加载个人生活习惯数据
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doLoadLifeStyleData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		LifeStyleModel lsModel = new LifeStyleModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String empiId = (String) body.get("empiId");
		try {
			body = lsModel.getLifeStyleByEmpiId(empiId);
			res.put("body", body);
		} catch (ModelDataOperationException e) {
			logger.error("dave lifeStyle is fail");
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取生命周期编号
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doQueryLifeCycleId(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HealthRecordModel hrModel = new HealthRecordModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			body = hrModel.getLifeCycleId(body);
			res.put("body", body);
		} catch (ModelDataOperationException e) {
			logger.error("dave lifeStyle is fail");
			throw new ServiceException(e);
		}
	}

	/**
	 * 注销个人健康档案和相关子档
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void logoutAllRecords(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx,
			VindicateLogService vLogService)
			throws ServiceException {
		this.vLogService=vLogService;
		this.doLogoutAllRecords(req, res, dao, ctx);
	}

	/**
	 * 注销个人健康档案和相关子档
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLogoutAllRecords(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String phrId = StringUtils.trimToEmpty((String) body.get("phrId"));
		String empiId = StringUtils.trimToEmpty((String) body.get("empiId"));
		String deadReason = StringUtils.trimToEmpty((String) body
				.get("deadReason"));
		String cancellationReason = StringUtils.trimToEmpty((String) body
				.get("cancellationReason"));
		String deadDate = StringUtils
				.trimToEmpty((String) body.get("deadDate"));
		String deadFlag = StringUtils
				.trimToEmpty((String) body.get("deadFlag"));
		VisitPlanModel vpModel = new VisitPlanModel(dao);
		try {
			// **注销个人健康档案
			HealthRecordModel hrModel = new HealthRecordModel(dao);
			hrModel.logoutHealthRecord(phrId, deadReason, cancellationReason,
					deadDate, deadFlag);
			vLogService.saveVindicateLog(EHR_HealthRecord, "3", phrId, dao,
					empiId);

			// ** 注销传染病档案
			IdrReportModel idrm = new IdrReportModel(dao);
			idrm.logOutIdrReport("empiId", empiId, cancellationReason,
					deadReason);
			vLogService.saveVindicateLog(IDR_Report, "3", phrId, dao, empiId);

			// **注销老年人档案
			OldPeopleRecordModel oprModel = new OldPeopleRecordModel(dao);
			oprModel.logoutOldPeopleRecord(phrId, cancellationReason,
					deadReason);
			vLogService.saveVindicateLog(MDC_OldPeopleRecord, "3", phrId, dao,
					empiId);

			// **注销高血压档案
			HypertensionModel hypModel = new HypertensionModel(dao);
			hypModel.logoutHypertensionRecord(phrId, cancellationReason,
					deadReason);
			vLogService.saveVindicateLog(MDC_HypertensionRecord, "3", phrId,
					dao, empiId);
			// **注销高血压高危档案
			HypertensionRiskModel hModel = new HypertensionRiskModel(dao);
			hModel.logoutHypertensionRisk(empiId, cancellationReason,
					deadReason, deadDate);

			// **注销糖尿病档案
			DiabetesRecordModel drModel = new DiabetesRecordModel(dao);
			drModel.logoutDiabetesRecord(phrId, cancellationReason, deadReason);
			vLogService.saveVindicateLog(MDC_DiabetesRecord, "3", phrId, dao,
					empiId);

			// **注销儿童档案
			ChildrenHealthModel chModel = new ChildrenHealthModel(dao);
			chModel.logOutHealthCardRecord("phrId", phrId, cancellationReason,
					deadReason);
			vLogService.saveVindicateLog(CDH_HealthCard, "3", phrId, dao,
					empiId);

			// ** 注销体弱儿童档案
			DebilityChildrenModel dcModel = new DebilityChildrenModel(dao);
			dcModel.logOutDebilityChildrenRecord("empiId", empiId,
					cancellationReason, deadReason);
			vLogService.saveVindicateLog(CDH_DebilityChildren, "3", phrId, dao,
					empiId);

			// ** 注销孕妇档案
			PregnantRecordModel prm = new PregnantRecordModel(dao);
			prm.logOutPregnantRecord("empiId", empiId, cancellationReason,
					deadReason);
			vLogService.saveVindicateLog(MHC_PregnantRecord, "3", phrId, dao,
					empiId);

			// ** 注销精神病档案
			PsychosisRecordModel prModel = new PsychosisRecordModel(dao);
			prModel.logoutPsychosisRecord(phrId, cancellationReason, deadReason);
			vLogService.saveVindicateLog(PSY_PsychosisRecord, "3", phrId, dao,
					empiId);

			// ** 注销血吸虫病档案
			SchistospmaModel sm = new SchistospmaModel(dao);
			sm.logoutSchistospmaRecord(phrId, cancellationReason, deadReason,
					true);
			vLogService.saveVindicateLog(SCH_SchistospmaRecord, "3", phrId,
					dao, empiId);

			// ** 注销狂犬病档案
			RabiesRecordModel rrm = new RabiesRecordModel(dao);
			rrm.logoutRabiesRecord(phrId, cancellationReason, deadReason);
			vLogService.saveVindicateLog(DC_RabiesRecord, "3", phrId, dao,
					empiId);

			// ** 注销残疾人档案
			DefLimbModel dlm = new DefLimbModel(dao);
			dlm.logoutDeformityRecord(body, cancellationReason, deadReason,
					DEF_LimbDeformityRecord);
			vLogService.saveVindicateLog(DEF_LimbDeformityRecord, "3", phrId,
					dao, empiId);
			dlm.logoutDeformityRecord(body, cancellationReason, deadReason,
					DEF_BrainDeformityRecord);
			vLogService.saveVindicateLog(DEF_BrainDeformityRecord, "3", phrId,
					dao, empiId);
			dlm.logoutDeformityRecord(body, cancellationReason, deadReason,
					DEF_IntellectDeformityRecord);
			vLogService.saveVindicateLog(DEF_IntellectDeformityRecord, "3",
					phrId, dao, empiId);

			// ** 注销离休干部档案
			RetiredVeteranCadresModel rvcModule = new RetiredVeteranCadresModel(
					dao);
			rvcModule.logoutRVCRecord(phrId, cancellationReason, deadReason);
			vLogService.saveVindicateLog(RVC_RetiredVeteranCadresRecord, "3",
					phrId, dao, empiId);

			// ** 注销肿瘤高危档案(该人员下全部记录[所有类别])
			TumourHighRiskModel thrModel = new TumourHighRiskModel(dao);
			thrModel.logoutAllTHR(empiId, cancellationReason);
			vLogService.saveVindicateLog(MDC_TumourHighRisk, "3", phrId, dao,
					empiId);
			// ** 注销该人员的全部肿瘤确诊记录
			TumourConfirmedModel tcModel = new TumourConfirmedModel(dao);
			tcModel.logoutAllTC(empiId, cancellationReason);
			vLogService.saveVindicateLog(MDC_TumourConfirmed, "3", phrId, dao,
					empiId);
			// ** 注销该人员的全部肿瘤现患报卡
			TumourPatientReportCardModel tprcModel = new TumourPatientReportCardModel(
					dao);
			tprcModel.logoutAllTPRCRecord(empiId, cancellationReason, deadFlag,
					deadDate);
			vLogService.saveVindicateLog(MDC_TumourPatientReportCard, "3",
					phrId, dao, empiId);
			// ** 迁出或死亡 注销该人员的全部肿瘤初筛
			TumourScreeningModel tsModel = new TumourScreeningModel(dao);
			tsModel.logoutMyAllTSR(empiId, cancellationReason);
			// ** 迁出或死亡 注销该人员的全部肿瘤问卷
			TumourQuestionnaireModel tqModel = new TumourQuestionnaireModel(dao);
			tqModel.logoutMyAllTHQ(empiId, cancellationReason);

			// **注销未执行过的随访计划
			vpModel.logOutVisitPlan(vpModel.EMPIID, empiId);

			// 保存个人档案
			vLogService.saveRecords("LOGOUT", "logoutAll", dao, empiId);
			// -----------

		} catch (ModelDataOperationException e) {
			logger.error("logout healthRecord failed");
			throw new ServiceException(e);
		}
	}

	public void doRefreshRefUserId(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		UserRoleToken user = UserRoleToken.getCurrent();
		user.setProperty("refUserId", req.get("refUserId"));
		user.setProperty("refRoleId", "chis.01");
	}

	/**
	 * 个人签约保存
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws ServiceException
	 */
	public void doSavePsersonnalContract(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException, ServiceException {
		List<Map<String, Object>> reqBodyList = (List<Map<String, Object>>) req.get("body");
		Map<String,Object> reqBody = reqBodyList.get(0);
		String pkey = (String) reqBody.get("FS_Id");
		String op = "create";
		FamilyRecordModule frModule = new FamilyRecordModule(dao);
		if (pkey != null && !pkey.equals(" ")) {
			op = "update";
		}
		String empiId = (String) reqBody.get("FS_EmpiId");
		try {
			boolean pFlag = false;
			HealthRecordModel hrModel = new HealthRecordModel(dao);
			if (reqBody.get("FC_Stop_Date") != null
					&& !"".equals(reqBody.get("FC_Stop_Date"))) {
				reqBody.put("FC_Sign_Flag", "2");
				pFlag = true;
			} else {
				reqBody.put("FC_Sign_Flag", "1");
			}
			Map<String, Object> info = hrModel.getHealthRecordByEmpiId(empiId);
			if (info == null) {
				return;
			}
		    String masterFlag = (String)info.get("masterFlag");
		    String familyId = "";
		    if(masterFlag!=null&&masterFlag.equals("y")){
		    	familyId = (String)info.get("familyId");
		    	if(familyId!=null&&!familyId.isEmpty()){
		    		reqBody.put("F_Id", familyId);
		    	}
		    }
			String userId = UserRoleToken.getCurrent().getUserId();
			String userManageUnitId = UserRoleToken.getCurrent().getManageUnitId();
			
			reqBody.put("FC_CreateUser", userId);
			reqBody.put("FC_Repre", empiId);
			reqBody.put("FC_CreateUnit", userManageUnitId);
			reqBody.put("FC_CreateDate", reqBody.get("FS_CreateDate"));
			reqBody.put("FC_Begin", reqBody.get("FS_CreateDate")==null?reqBody.get("FC_Begin"):reqBody.get("FS_CreateDate"));
			reqBody.put("FC_End", reqBody.get("FC_End"));
			Map<String, Object> contractRecord = frModule.saveFamilyContractBase(reqBody, op);
			if ("create".equals(op)) {
				pkey = (String) contractRecord.get("FC_Id");
			}
			String FC_Id = "";
			if(reqBody.get("FC_Id")!=null){
				FC_Id = (String)reqBody.get("FC_Id");
			}
			String phrId = (String) info.get("phrId");
			Map<String, Object> fcsRecord = frModule.getFamilyContract(empiId,FC_Id);
			String FS_PersonGroup = (String) reqBody.get("FS_PersonGroup");
			String FS_Kind = (String) reqBody.get("FS_Kind");
			String signFlag = (String) reqBody.get("signFlag");
			String oldFlag = (String) info.get("signFlag");
			if((reqBody.get("FC_Sign_Flag")+"").equals("1")){
				signFlag="y";
			}
			if (fcsRecord != null) {
				fcsRecord.put("FS_PersonGroup", FS_PersonGroup);
				fcsRecord.put("FS_Kind", FS_Kind);
				if (info != null) {
					if (pFlag|| (signFlag != null && !signFlag.equals(oldFlag))) {
						info.put("signFlag", signFlag);
						if (pFlag) {
							info.put("signFlag", "n");
						}
						hrModel.saveHealthRecord("update", info);
						if ("y".equals(signFlag)) {
							fcsRecord.put("FS_CreateDate", new Date());
						}
					}
				}
				frModule.saveFamilyContract(fcsRecord, "update");
				vLogService.saveVindicateLog(EHR_FamilyContractService, "2",pkey, dao);
			} else {
				reqBody.put("FS_CreateDate", new Date());
				reqBody.put("FC_Id", pkey);
				Map<String, Object> rsMap = frModule.saveFamilyContract(reqBody, "create");
				String pkey2 = (String) rsMap.get("FS_Id");
				vLogService.saveVindicateLog(EHR_FamilyContractService, "1",pkey2, dao);
				if (info != null) {
					info.put("signFlag", "y");
					if (pFlag) {
						info.put("signFlag", "n");
					}
					hrModel.saveHealthRecord("update", info);
					vLogService.saveVindicateLog(EHR_HealthRecord, "2", phrId,
							dao, empiId);

				}
			}
			// 如果签约标志是y则更新个人签约记录
			String grqyempiId = (String) reqBody.get("FS_EmpiId");
			if (reqBody.get("signFlag") != null) {
				String grqybz = (String) reqBody.get("signFlag");
				if (grqybz.equals("y")) {
					vLogService.saveRecords("GRQY", "create", dao, grqyempiId);
				} else {
					vLogService.saveRecords("GRQY", "logout", dao, grqyempiId);
				}
			} else {
				vLogService.saveRecords("GRQY", "logout", dao, grqyempiId);
			}
			Map<String, Object> body = new HashMap<String, Object>();
			body.put("pkey", pkey);
			res.put("body", body);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	protected void doRemove(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		String fcId = (String) req.get("FC_Id");
		String fsId = (String) req.get("pkey");
		String FS_EmpiId = (String) req.get("FS_EmpiId");
		FamilyRecordModule frModule = new FamilyRecordModule(dao);
		try {
			HealthRecordModel hrModel = new HealthRecordModel(dao);
				Map<String, Object> info = hrModel
						.getHealthRecordByEmpiId(FS_EmpiId);
				info.put("signFlag", "n");
				hrModel.saveHealthRecord("update", info);
			frModule.deleteFamilyContractBaseByFcId(fcId);
			frModule.deleteFamilyContractServiceByFcId(fcId);
		} catch (ModelDataOperationException e) {
			logger.error("doRemove is fail");
			throw new ServiceException(e);
		}
	}
	//通过身份证号获取签约信息
	protected void doGetjmqyinfobyidcard(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		String idcard = (String) req.get("idcard");
		EmpiModel empiModel = new EmpiModel(dao);
		List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
		Map<String, Object> data=new HashMap<String, Object>();
		try {
			list = empiModel.getEmpiInfoByIdcard(idcard);
		} catch (ModelDataOperationException e) {
			e.printStackTrace();
		}
		if (list!=null && list.size() == 0 ) {
			res.put("code", "402");
			res.put("msg", "未获取到档案信息，请核对身份证号！");
			return;
		}else if(list!=null && list.size() > 1 ){
			res.put("code", "402");
			res.put("msg", "存在多份个人基本信息，请联系管理员处理！");
			return;
		}
		empiModel=null;
		data = list.get(0);
		HealthRecordModel hr=new HealthRecordModel(dao);
		Map<String, Object> healthrecord=new HashMap<String, Object>();
		try {
			healthrecord=hr.getHealthRecordByIdCard(idcard,ctx);
		} catch (ModelDataOperationException e) {
			e.printStackTrace();
		}
		if(healthrecord!=null && healthrecord.size() > 0 ){
			data.putAll(healthrecord);
			data.put("F_Id", data.get("familyId")+"");
		}else{
			res.put("code", "402");
			res.put("msg", "该居民未创建个人健康档案，请创建！");
			return;
		}
		Map<String, Object> familycontract=new HashMap<String, Object>();
		try {
			familycontract=hr.getFamilyContractBasebyFC_Repre(data.get("empiId")+"",res);
		} catch (ModelDataOperationException e) {
			e.printStackTrace();
		}
		if(familycontract!=null && familycontract.size() > 0 ){
			data.putAll(familycontract);
		}else{
			data.put("FC_Repre", data.get("empiId")+"");
		}
		res.put("data", data);
	}
	
	/**
	 * 健康档案审核
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doHealthRecordVerify(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		try {
			HealthRecordModel healthRecordModel = new HealthRecordModel(dao);
			healthRecordModel.verifyHealthRecordByPhrIds(body,res);
		} catch (ModelDataOperationException e) {
			logger.error("error query healthrecord.", e);
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 健康档案弃审
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doHealthRecordCancelVerify(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		try {
			HealthRecordModel healthRecordModel = new HealthRecordModel(dao);
			healthRecordModel.cancelVerifyHealthRecordByPhrIds(body,res);
		} catch (ModelDataOperationException e) {
			logger.error("error query healthrecord.", e);
			throw new ServiceException(e);
		}
	}
	/**
	 * 2018-09-26 Wangjl 健康档案确认开放
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveHealthRecordOpen(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		try {
			HealthRecordModel healthRecordModel = new HealthRecordModel(dao);
			healthRecordModel.doSaveOpenHealthRecordByPhrIds(body,res);
		} catch (ModelDataOperationException e) {
			logger.error("error query healthrecord.", e);
			throw new ServiceException(e);
		}
	}
	/**
	 * 2018-09-26 Wangjl 健康档案取消开放
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doHealthRecordCancelOpen(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		try {
			HealthRecordModel healthRecordModel = new HealthRecordModel(dao);
			healthRecordModel.cancelOpenHealthRecordByPhrIds(body,res);
		} catch (ModelDataOperationException e) {
			logger.error("error query healthrecord.", e);
			throw new ServiceException(e);
		}
	}
	/**
	 * 健康档案标记
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
//	@SuppressWarnings("unchecked")
//	protected void doSaveHealthRecordRemark(Map<String, Object> req,
//			Map<String, Object> res, BaseDAO dao, Context ctx)
//			throws ServiceException {
//		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
//		try {
//			HealthRecordModel healthRecordModel = new HealthRecordModel(dao);
//			healthRecordModel.doSaveRemarkHealthRecordByPhrIds(body,res);
//		} catch (ModelDataOperationException e) {
//			logger.error("error query healthrecord.", e);
//			throw new ServiceException(e);
//		}
//	}
	/**
	 * 健康档案取消标记
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
//	@SuppressWarnings("unchecked")
//	protected void doHealthRecordCancelRemark(Map<String, Object> req,
//			Map<String, Object> res, BaseDAO dao, Context ctx)
//			throws ServiceException {
//		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
//		try {
//			HealthRecordModel healthRecordModel = new HealthRecordModel(dao);
//			healthRecordModel.cancelRemarkHealthRecordByPhrIds(body,res);
//		} catch (ModelDataOperationException e) {
//			logger.error("error query healthrecord.", e);
//			throw new ServiceException(e);
//		}
//	}
	
	/**
	 * 查询健康档案退回记录
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doQueryBackRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		try {
			HealthRecordModel healthRecordModel = new HealthRecordModel(dao);
			List<Map<String, Object>> backList = healthRecordModel.doQueryBackRecord(body,res);
			res.put("data", backList);
		} catch (ModelDataOperationException e) {
			logger.error("error query healthrecord.", e);
			throw new ServiceException(e);
		}
	}
}
