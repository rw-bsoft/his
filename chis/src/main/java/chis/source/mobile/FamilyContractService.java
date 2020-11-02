/**
 * @(#)FamilyContractModel.java Created on 2012-11-19 下午03:01:03
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.mobile;

import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.empi.EmpiModel;
import chis.source.fhr.FamilyRecordModule;
import chis.source.mhc.PregnantRecordModel;
import chis.source.mpm.MasterplateMaintainModel;
import chis.source.phr.HealthRecordModel;
import chis.source.psy.PsychosisRecordModel;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.SchemaUtil;
import chis.source.util.UserUtil;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;

/**
 * @description
 * 
 * @author <a href="mailto:yub@bsoft.com.cn">俞波</a>
 */
public class FamilyContractService extends AbstractActionService implements
		DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(FamilyContractService.class);
	private List<String> list = new ArrayList<String>();

	@SuppressWarnings("unchecked")
	public void doQueryContractContent(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) jsonReq.get("body");
		String familyId = (String) body.get("familyId");
		FamilyRecordModule frModel = new FamilyRecordModule(dao);
		try {
			Map<String, Object> familyData = frModel
					.getFamilyRecordById(familyId);
			if (familyData != null) {
				Map<String, Object> reqBody = new HashMap<String, Object>();
				reqBody.put("FC_Repre", familyData.get("ownerName"));
				reqBody.put("FC_Phone", familyData.get("familyHome"));
				reqBody.put("FC_Party2", familyData.get("ownerName"));
				reqBody.put("FC_Party", UserUtil.get(UserUtil.USER_NAME));
				jsonRes.put("body", reqBody);
			}
		} catch (ModelDataOperationException e) {
			logger.error("Fail to query contract content.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 保存家庭签约基本信息记录
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveContractBase(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
			throws ServiceException {
		// 保存签约信息
		Map<String, Object> formData = (Map<String, Object>) jsonReq
				.get("body");
		String op = (String) jsonReq.get("op");
		FamilyContractModel fcModel = new FamilyContractModel(dao);
		try {
			Map<String, Object> map = fcModel.saveContractBase(formData, op);
			if ("create".equals(op)) {
				formData.put("FC_Id", map.get("FC_Id"));
			} else {
				String flag = (String) formData.get("FC_Sign_Flag");
				if ("2".equals(flag)) {
					String F_Id = (String) formData.get("F_Id");
					List<Map<String, Object>> list = fcModel
							.getHealthRecordByFamilyId(F_Id);
					HealthRecordModel hrModel = new HealthRecordModel(dao);
					for (Map<String, Object> r : list) {
						String empiId = (String) r.get("empiId");
						r.put("signFlag", "n");
						hrModel.saveHealthRecord("update", r);
						fcModel.updateMasterplateDataByEmpiId(empiId);
						fcModel.updateFamilyContractService(empiId);
					}
				}
			}
			jsonRes.put("body", formData);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}

	}

	/**
	 * 保存服务项目相关信息
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveProjects(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> record = (Map<String, Object>) jsonReq.get("body");
		String signFlag = (String) jsonReq.get("signFlag");
		String op = (String) jsonReq.get("op");
		String empiId = (String) record.get("FS_EmpiId");
		FamilyContractModel fcModel = new FamilyContractModel(dao);
		// 保存服务项目相关信息
		try {
			HealthRecordModel hrModel = new HealthRecordModel(dao);
			Map<String, Object> info = hrModel.getHealthRecordByEmpiId(empiId);
			if ("create".equals(op)) {
				Map<String, Object> r = fcModel.saveContractProject(record, op);
				if (info != null) {
					info.put("signFlag", "y");
					hrModel.saveHealthRecord("update", info);
				}
				record.put("FS_Id", r.get("FS_Id"));
			} else {
				if ("y".equals(signFlag)) {
					fcModel.saveContractProject(record, op);
				} else {
					fcModel.deleteFamilyContractById((String) record
							.get("FS_Id"));
					record = new HashMap<String, Object>();
				}
				info.put("signFlag", "n");
				hrModel.saveHealthRecord("update", info);
			}
			jsonRes.put("body", record);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}

	}

	/**
	 * 删除家庭签约基本信息记录
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ServerException
	 */
	@SuppressWarnings("unchecked")
	public void doRemoveFamilyContractBase(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
			throws ServiceException, ServerException {
		Map<String, Object> body = (Map<String, Object>) jsonReq.get("body");
		String fcId = (String) body.get("FC_Id");
		FamilyContractModel fcModel = new FamilyContractModel(dao);
		HealthRecordModel hrModel = new HealthRecordModel(dao);
		try {
			// 获取家庭签约基本信息
			List<Map<String, Object>> records = fcModel
					.getFamilyBaseDataByFCId(fcId);
			Map<String, Object> baseData = null;
			if (list != null && list.size() > 0) {
				baseData = records.get(0);
			} else {
				throw new ServerException("获取家庭签约基本信息失败！");
			}
			String familyId = (String) baseData.get("F_Id");// 家庭档案编号
			List<Map<String, Object>> persons = hrModel
					.getHealthRecordByFamilyId(familyId);
			for (Map<String, Object> person : persons) {
				updateHealthRecord((String) person.get("empiId"), "2", dao);
			}
			fcModel.deleteDataByFCId(EHR_FamilyContractBase, fcId);
			fcModel.deleteDataByFCId(EHR_FamilyContractService, fcId);
			List<Map<String, Object>> FC_IdFields = fcModel
					.getFieldsByCodeAndValue("FC_Id", fcId);
			for (Map<String, Object> field : FC_IdFields) {
				String frId = (String) field.get("recordId");
				fcModel.remove(frId);
			}

			FamilyRecordModule frModule = new FamilyRecordModule(dao);
			Map<String, Object> familyData = frModule
					.getFamilyRecordById(familyId);
			familyData.put("cy_ContractDate", null);
			familyData.put("cy_isContract", "0");
			fcModel.updateFamilyRecord(familyData);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	public void updateHealthRecord(String empiId, String signFlag, BaseDAO dao)
			throws ServiceException {
		HealthRecordModel hrModel = new HealthRecordModel(dao);
		try {
			Map<String, Object> data = hrModel.getHealthRecordByEmpiId(empiId);
			if (data != null) {
				data.put("signFlag", signFlag);
				hrModel.saveHealthRecord("update", data);
			}
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 查询家医服务项目
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doFamilyQuery(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) jsonReq.get("body");
		String familyId = (String) body.get("familyId");
		String FC_Id = (String) body.get("FC_Id");
		FamilyContractModel fcModel = new FamilyContractModel(dao);
		PsychosisRecordModel psychosisRecordModel = new PsychosisRecordModel(
				dao);
		PregnantRecordModel pregnantRecordModel = new PregnantRecordModel(dao);
		try {
			List<Map<String, Object>> list = fcModel
					.getHealthRecordByFamilyId(familyId);
			List<Map<String, Object>> reqBody = new ArrayList<Map<String, Object>>();
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Map<String, Object> map = list.get(i);
					String empiId = (String) map.get("empiId");
					List<Map<String, Object>> list2 = fcModel
							.getFamilyContractByFCIdAndEmpiId(empiId, FC_Id);
					Map<String, Object> psychosisRecord = psychosisRecordModel
							.getPsychosisRecordByEmpiId(empiId);
					Map<String, Object> row = new HashMap<String, Object>();
					if (psychosisRecord != null && psychosisRecord.size() > 0) {
						row.put("isPSY", "y");

					} else {
						row.put("isPSY", "n");
					}
					List<Map<String, Object>> pregnantRecord = pregnantRecordModel
							.getNormalPregnantRecord(empiId);
					if (pregnantRecord != null && pregnantRecord.size() > 0) {
						row.put("isPregnant", "y");

					} else {
						row.put("isPregnant", "n");
					}

					if (list2.size() > 0) {
						Map<String, Object> map2 = list2.get(0);
						row.put("FS_SexCode", map2.get("sexCode"));
						row.put("FS_SexCode_text", map2.get("sexCode_text"));
						row.put("FS_PersonGroup", map2.get("FS_PersonGroup"));
						row.put("FS_EmpiId", empiId);
						row.put("FS_EmpiId_text", map2.get("personName"));
						row.put("FS_Kind", map2.get("FS_Kind"));
						row.put("FS_CreateDate", map2.get("FS_CreateDate"));
						row.put("birthday", map2.get("birthday"));
						row.put("signFlag", map2.get("signFlag"));
						row.put("FS_Id", map2.get("FS_Id"));
						row.put("isDiabetes", map.get("isDiabetes"));
						row.put("isHypertension", map.get("isHypertension"));
						row.put("masterFlag", map.get("masterFlag"));
						reqBody.add(row);
					} else {
						row.put("FS_SexCode", map.get("sexCode"));
						row.put("FS_SexCode_text", map.get("sexCode_text"));
						row.put("FS_EmpiId", empiId);
						row.put("FS_EmpiId_text", map.get("personName"));
						row.put("birthday", map.get("birthday"));
						row.put("signFlag", map.get("signFlag"));
						row.put("isDiabetes", map.get("isDiabetes"));
						row.put("isHypertension", map.get("isHypertension"));
						row.put("masterFlag", map.get("masterFlag"));
						reqBody.add(row);
					}
				}
			}
			reqBody = SchemaUtil.setDictionaryMessageForList(reqBody,
					EHR_FamilyContractService);
			jsonRes.put("body", reqBody);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 保存家庭签约服务记录
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveFamilyContractRecord(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) jsonReq.get("body");
		String op = (String) jsonReq.get("op");
		FamilyContractModel fcModel = new FamilyContractModel(dao);
		try {
			Map<String, Object> res = fcModel.saveBody(op, body);
			jsonRes.put("body", res);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 删除家庭签约服务记录
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doRemoveFamilyContractRecord(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) jsonReq.get("body");
		String recordId = (String) body.get("recordId");
		FamilyContractModel fcModel = new FamilyContractModel(dao);
		try {
			fcModel.remove(recordId);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doGetContractRecords(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) jsonReq.get("body");
		String masterplateName = (String) body.get("masterplateName");
		String empiId = (String) body.get("empiId");
		FamilyContractModel fcModel = new FamilyContractModel(dao);
		try {
			List<Map<String, Object>> list = fcModel.getAllRecord(empiId,
					masterplateName);
			list = DictionaryUtil.setDictionaryMessageForList(list, dao);
			jsonRes.put("body", list);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doGetOneRecord(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) jsonReq.get("body");
		String recordId = (String) body.get("recordId");
		FamilyContractModel fcModel = new FamilyContractModel(dao);
		try {
			Map<String, Object> record = fcModel.getOneRecord(recordId);
			record = DictionaryUtil.setDictionaryMessageForForm(record, dao);
			if (record == null) {
				record = new HashMap<String, Object>();
			}
			jsonRes.put("body", record);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 查询具体某一个服务记录模板
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void doGetOnePlate(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
			throws ServiceException {
		List resPlate = new ArrayList();
		Map<String, Object> res = new HashMap<String, Object>();
		Map<String, Object> body = (Map<String, Object>) jsonReq.get("body");
		List queryCnd = (List) body.get("cnd");
		int pageNo = (Integer) body.get("pageNo");
		int pageSize = (Integer) body.get("pageSize");
		MasterplateMaintainModel maintainModel = new MasterplateMaintainModel(
				dao);
		try {
			res = maintainModel.listFieldByRelation(queryCnd, pageNo, pageSize,
					"1", null);
			if (res != null && res.size() > 0) {
				resPlate = DictionaryUtil.setDictionaryOnePlate(res, dao);
			}

		} catch (ExpException e) {
			throw new ServiceException(e);
		}
		if (resPlate == null) {
			resPlate = new ArrayList();
		}
		resPlate.add(res);
		jsonRes.put("body", resPlate);
	}

	/**
	 * 查询具体某一个健康问卷模板
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void doGetOneSurveyPlate(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
			throws ServiceException {
		List resPlate = new ArrayList();
		Map<String, Object> res = new HashMap<String, Object>();
		Map<String, Object> body = (Map<String, Object>) jsonReq.get("body");
		List queryCnd = (List) body.get("cnd");
		int pageNo = (Integer) body.get("pageNo");
		int pageSize = (Integer) body.get("pageSize");
		MasterplateMaintainModel maintainModel = new MasterplateMaintainModel(
				dao);
		try {
			res = maintainModel.listFieldByRelation(queryCnd, pageNo, pageSize,
					"1", null);
			if (res != null && res.size() > 0) {
				resPlate = DictionaryUtil
						.setDictionarySurveryOnePlate(res, dao);
			}

		} catch (ExpException e) {
			throw new ServiceException(e);
		}
		if (resPlate == null) {
			resPlate = new ArrayList();
		}
		resPlate.add(res);
		jsonRes.put("body", resPlate);
	}

	/**
	 * 其他模块往家医跳转 1、根据empiId查询基本信息和健康档案, 2、再根据健康档案的familyId查询家庭档案和家庭成员
	 * 3、查询家医签约相关情况
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doGetFamilyRecordByEmpiId(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) jsonReq.get("body");
		Map<String, Object> res = new HashMap<String, Object>();
		String empiId = null;
		if (body != null && body.size() > 0) {
			empiId = (String) body.get("empiId");
		}

		HealthRecordModel healthRecordModel = new HealthRecordModel(dao);
		FamilyContractModel fcModel = new FamilyContractModel(dao);
		EmpiModel empiModel = new EmpiModel(dao);
		try {
			// 健康档案
			Map<String, Object> healthRecord = healthRecordModel
					.getHealthRecordByEmpiId(empiId);
			res.put("healthRecord", healthRecord);
			// 基本信息
			Map<String, Object> empiInfo = empiModel
					.getEmpiInfoByEmpiid(empiId);
			res.put("empiInfo", empiInfo);
			if (healthRecord != null && healthRecord.size() > 0) {

				Object familyId = healthRecord.get("familyId");
				if (familyId != null && !"".equals(familyId)) {
					// 家庭档案
					Map<String, Object> familyRecord = fcModel
							.getFamilyRecordByFamilyId(familyId.toString());
					res.put("familyRecord", familyRecord);
					// 家庭成员健康档案
					List<Map<String, Object>> memberHealthRecord = fcModel
							.getHealthRecordByFamilyId(familyId.toString());
					res.put("memberInfo", memberHealthRecord);
					// 家庭签约信息
					Map<String, Object> contractBase = fcModel
							.getFamilyContractBaseByFamilyIdAndFCSignFlag(familyId
									.toString());
					res.put("contractBase", contractBase);

				}
			}
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
		jsonRes.put("body", res);
	}

	/**
	 * 点击家庭档案列表
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doGetFamilyRecordByFamilyId(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) jsonReq.get("body");
		Map<String, Object> res = new HashMap<String, Object>();
		String familyId = body.get("familyId").toString();
		FamilyContractModel fcModel = new FamilyContractModel(dao);
		EmpiModel empiModel = new EmpiModel(dao);
		try {
			Map<String, Object> masterInfo = fcModel
					.getHealthRecordByFamilyIdAndMaster(familyId);
			res.put("healthRecord", masterInfo);
			if (null != masterInfo && masterInfo.size() > 0) {
				String empiId = masterInfo.get("empiId").toString();
				Map<String, Object> empiInfo = empiModel
						.getEmpiInfoByEmpiid(empiId);
				res.put("empiInfo", empiInfo);
			}
			Map<String, Object> contractBase = fcModel
					.getFamilyContractBaseByFamilyIdAndFCSignFlag(familyId);
			res.put("contractBase", contractBase);
			List<Map<String, Object>> healthRecord = fcModel
					.getHealthRecordByFamilyId(familyId);
			res.put("memberInfo", healthRecord);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
		jsonRes.put("body", res);
	}
}
