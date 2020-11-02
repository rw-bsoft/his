/**
 * @(#)FamilyRecordService.java Created on 2012-2-20 下午04:25:55
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.fhr;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.util.JSON;

import chis.jsdwebservice.FamilyDoctorInterfaceService;
import chis.jsdwebservice.FamilyDoctorInterfaceServicePortType;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.admin.AreaGridModel;
import chis.source.admin.SystemUserModel;
import chis.source.cdh.ChildrenHealthModel;
import chis.source.control.ControlRunner;
import chis.source.def.DefModel;
import chis.source.dic.RelatedCode;
import chis.source.dic.RolesList;
import chis.source.dic.YesNo;
import chis.source.empi.EmpiModel;
import chis.source.mdc.DiabetesRecordModel;
import chis.source.mdc.HypertensionModel;
import chis.source.mhc.PregnantRecordModel;
import chis.source.mov.EHRMoveModule;
import chis.source.ohr.OldPeopleRecordModel;
import chis.source.phr.HealthRecordModel;
import chis.source.phr.HealthRecordService;
import chis.source.psy.PsychosisRecordModel;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.service.ServiceCode;
import chis.source.util.CNDHelper;
import chis.source.util.SchemaUtil;
import chis.source.util.UserUtil;
import ctd.account.UserRoleToken;
import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;

/**
 * @description
 * 
 * @author <a href="mailto:tianj@bsoft.com.cn">田军</a>
 */
public class FamilyRecordService extends AbstractActionService implements
		DAOSupportable {

	private static final Logger logger = LoggerFactory
			.getLogger(FamilyRecordService.class);

	/**
	 * 加载个人健康并判断户主是否存在
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doLoadFamilyRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		@SuppressWarnings("unchecked")
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		String pkey = (String) body.get("pkey");
		res.put("body", body);
		try {
			if (pkey == null || pkey.equals("")) {
				res.put(RES_CODE, Constants.CODE_RECORD_NOT_FOUND);
				res.put(RES_MESSAGE, "主键为空，不能加载数据！");
				return;
			}
			FamilyRecordModule frModel = new FamilyRecordModule(dao);
			Map<String, Object> familyRecord = frModel
					.getFamilyRecordById(pkey);
			if (familyRecord == null) {
				return;
			}
			body.putAll(SchemaUtil.setDictionaryMessageForForm(familyRecord,
					EHR_FamilyRecord));
			HealthRecordModel hrModel = new HealthRecordModel(dao);
			Map<String, Object> masterRecord = hrModel
					.getMasterRecordByFamilyId(pkey);
			if (masterRecord != null) {
				body.put("isExist", true);
			}
		} catch (ModelDataOperationException e) {
			logger.error("error query healthrecord.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 为EHRVIEW页面获取家庭档案信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doGetFamilyForEHRView(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		String pkey = (String) body.get("pkey");
		res.put("body", body);
		FamilyRecordModule frModel = new FamilyRecordModule(dao);
		Map<String, Object> familyRecord;
		try {
			familyRecord = frModel.getFamilyRecordById(pkey);
		} catch (ModelDataOperationException e) {
			logger.error("failed to get family record !", e);
			throw new ServiceException(e);
		}
		if (familyRecord == null || familyRecord.size() < 1) {
			return;
		}
		body.putAll(SchemaUtil.setDictionaryMessageForList(familyRecord,
				EHR_FamilyRecord));
	}

	/**
	 * 查询需要初始化与网格地址有关的数据
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doLoadRegionCodeRelatedRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		String regionCode = (String) body.get("regionCode");
		String op = (String) body.get("op");
		res.put("body", body);
		try {
			FamilyRecordModule frModel = new FamilyRecordModule(dao);
			if ("create".equals(op)) {
				Map<String, Object> familyRecord = frModel
						.getFamilyRecordByRegionCode(regionCode);
				familyRecord = SchemaUtil.setDictionaryMessageForForm(
						familyRecord, EHR_FamilyRecord);
				if (familyRecord != null) {
					ControlRunner.run(EHR_FamilyRecord, familyRecord, ctx,
							ControlRunner.UPDATE);
					body.put("familyRecord", familyRecord);
					return;
				}
			}

			HealthRecordModel hrModel = new HealthRecordModel(dao);
			Map<String, Object> masterRecord = hrModel
					.getMasterRecordByRegionCode(regionCode);
			body.put("masterRecord", masterRecord);
			if (masterRecord != null) {
				String empiId = (String) masterRecord.get("empiId");
				EmpiModel empiModel = new EmpiModel(dao);
				Map<String, Object> empiInfo = empiModel
						.getEmpiInfoByEmpiid(empiId);
				body.put("empiInfo", empiInfo);
			} else {// 没有户主时加载网格中的其他人到下拉列表上.
				List<Map<String, Object>> members = hrModel
						.getRelatedRecordByRegionCode(regionCode);
				body.put("member", members);
			}

			AreaGridModel agModel = new AreaGridModel(dao);
			Map<String, Object> nodeMap = agModel.getNodeInfo(regionCode);
			body.put("nodeMap", nodeMap);
			if (nodeMap == null) {
				return;
			}
			String manaDoctor = (String) nodeMap.get("manaDoctor");
			body.putAll(SchemaUtil.setDictionaryMessageForForm(nodeMap,
					EHR_AreaGridChild));
			if (manaDoctor == null) {
				return;
			}

			SystemUserModel suModel = new SystemUserModel(dao);
			List<Map<String, Object>> manageUnits = suModel.getUserByLogonName(
					manaDoctor, RolesList.ZRYS, RolesList.TDZ);
			body.put("manageUnits", manageUnits);
		} catch (ModelDataOperationException e) {
			logger.error("error query regionCodeRelatedRecord.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 批量匹配家庭成员
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ExpException
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	protected void doBatchMatchFamilyNumbers(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ExpException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String nowFamilyId = (String) body.get("familyId");
		try {
			UserRoleToken ur = UserRoleToken.getCurrent();
			String userId = ur.getUserId();
			// String uid = ((HttpSession) ctx.get(Context.WEB_SESSION))
			// .getAttribute("uid").toString();
			if (body == null) {
				return;
			}
			List<Map<String, Object>> records = (List<Map<String, Object>>) body
					.get("records");
			if (records == null || nowFamilyId == null) {
				return;
			}
			EmpiModel empiModel = new EmpiModel(dao);
			FamilyRecordModule frModel = new FamilyRecordModule(dao);
			HealthRecordModel hrModel = new HealthRecordModel(dao);
			for (int i = 0; i < records.size(); i++) {
				Map<String, Object> record = records.get(i);
				String preFamilyId = (String) record.get("familyId");
				String empiId = (String) record.get("empiId");
				if (nowFamilyId.equalsIgnoreCase(preFamilyId)) {
					continue;
				}
				String masterFlag = (String) record.get("masterFlag");
				if (YesNo.YES.equals(masterFlag)) {
					Map<String, Object> empiInfo = empiModel
							.getEmpiInfoByEmpiid(empiId);
					String ownerName = (String) empiInfo.get("personName");
					frModel.updateFamilyRecord(ownerName, nowFamilyId, userId);
				}
				String phrId = (String) record.get("phrId");
				hrModel.updateHealthRecordByPhrId(nowFamilyId, userId, phrId);
				// 增加家庭成员变动记录
				Map<String, Object> fmrMap = new HashMap<String, Object>();
				fmrMap.put("personName", record.get("personName"));
				fmrMap.put("sexCode", record.get("sexCode"));
				fmrMap.put("moveType", "1");
				fmrMap.put("phrId", record.get("phrId"));
				fmrMap.put("newFamilyId", preFamilyId);
				fmrMap.put("newFamilyId", nowFamilyId);
				fmrMap.put("manaDoctorId", record.get("manaDoctorId"));
				fmrMap.put("regionCode", record.get("regionCode"));
				fmrMap.put("regionCode_text", record.get("regionCode_text"));
				fmrMap.put("manaUnitId", record.get("manaUnitId"));
				fmrMap.put("applyUnit", UserUtil.get(UserUtil.USER_ID));
				fmrMap.put("applyUser", UserUtil.get(UserUtil.MANAUNIT_ID));
				fmrMap.put("applyDate", Calendar.getInstance().getTime());
				frModel.saveFMChangeRecord("create", fmrMap, false);
			}
		} catch (ModelDataOperationException e) {
			logger.error("error query healthrecord.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 保存家庭健康档案
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveFamilyRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		String familyId = (String) body.get("familyId");
		String ownerName = (String) body.get("ownerName");
		String phoneNumber = (String) body.get("familyHome");
		String regionCode = (String) body.get("regionCode");
		Context c = new Context();
		c.put("regionCode", regionCode);
		ctx.putCtx("codeCtx", c);
		Map<String, Object> resBody = new HashMap<String, Object>();
		res.put("body", resBody);
		try {
			FamilyRecordModule frModel = new FamilyRecordModule(dao);
			Map<String, Object> record = frModel.saveFamilyRecord(op, body);
			if (familyId != null && !familyId.equals("")) {
				frModel.updateFamilyPhone(familyId, phoneNumber);
			}
			HealthRecordModel hrModel = new HealthRecordModel(dao);
			if ("create".equals(op)) {
				familyId = (String) record.get("familyId");
				// Map<String, Object> healthRecord = hrModel
				// .getMasterRecordByRegionCode(regionCode);
				// if (healthRecord != null) {
				// String familyId1 = (String) healthRecord.get("familyId");
				// if (familyId1 == null || "".equals(familyId1)) {
				hrModel.updateHealthRecordByRegionCode(familyId, regionCode);
				// }
				// }
			}
			resBody.put("familyId", familyId);
			// 记录维护日志 add by ChenXR 2014-06-03
			vLogService.saveVindicateLog(EHR_FamilyRecord, op, familyId, dao);
			// 根据户主姓名更新健康档案。
			if (ownerName != null && ownerName.trim().length() > 0) {
				List<Map<String, Object>> healthRecList = hrModel
						.getRelatedRecordByRegionCode(regionCode);
				List<Map<String, Object>> onwerRec = HealthRecordService
						.selectRecordsByRelaCodes(healthRecList,
								new String[] { RelatedCode.MASTER });
				if (onwerRec.size() > 0) {
					return;
				}
				List<Map<String, Object>> matcedOwnerNameRecords = selectRecordByPersonName(
						healthRecList, ownerName);
				if (matcedOwnerNameRecords.size() > 1) {
					res.put(Service.RES_CODE, Constants.CODE_UNKNOWN_ERROR);
					res.put(Service.RES_MESSAGE, "家庭档案创建成功，但该家庭中存在两个姓名为'"
							+ ownerName + "'的人，系统无法自动判断户主，请到健康档案进行维护.");
					return;
				}
				if (matcedOwnerNameRecords.size() > 0) {
					Map<String, Object> ownerRec = matcedOwnerNameRecords
							.get(0);
					if (ownerRec != null) {
						hrModel.setMaster((String) ownerRec.get("empiId"));
					}
				}
			}

		} catch (ModelDataOperationException e) {
			logger.error("save FamilyRecord is fail");
			throw new ServiceException(e);
		}
	}

	private static List<Map<String, Object>> selectRecordByPersonName(
			List<Map<String, Object>> list, String name) {
		if (list == null || list.size() == 0)
			return new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> arr = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < list.size(); i++) {
			HashMap<String, Object> record = (HashMap<String, Object>) list
					.get(i);
			if (name.equals(record.get("personName")))
				arr.add(record);
		}
		return arr;
	}

	/**
	 * 保存家庭主要问题
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveFamilyProblem(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		String familyProblemId = (String) body.get("familyProblemId");
		Map<String, Object> resBody = new HashMap<String, Object>();
		res.put("body", resBody);
		try {
			FamilyRecordModule frModel = new FamilyRecordModule(dao);
			Map<String, Object> record = frModel.saveFamilyProblem(op, body);
			if ("create".equals(op)) {
				familyProblemId = (String) record.get("familyProblemId");
			}
			resBody.put("familyProblemId", familyProblemId);
			// 记录维护日志 add by ChenXR 2014-06-03
			vLogService.saveVindicateLog(EHR_FamilyProblem, op,
					familyProblemId, dao);
		} catch (ModelDataOperationException e) {
			logger.error("save FamilyProblem is fail");
			throw new ServiceException(e);
		}
	}

	/**
	 * 删除家庭档案
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doRemoveFamilyRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String preFamilyId = (String) body.get("pkey");
		String empiId = (String) body.get("empiId");
		try {
			FamilyRecordModule frModel = new FamilyRecordModule(dao);
			frModel.deleteFamilyProblem("familyId", preFamilyId);
			frModel.deleteFamilyRecord(preFamilyId);
			frModel.deleteFamilyMidleByEmpiId(empiId);
			HealthRecordModel hrModel = new HealthRecordModel(dao);
			UserRoleToken ur = UserRoleToken.getCurrent();
			String userId = ur.getUserId();
			hrModel.updateHealthRecordByFamilyId(userId, preFamilyId);
			// 记录维护日志 add by ChenXR 2014-06-03
			vLogService.saveVindicateLog(EHR_FamilyRecord, "4", preFamilyId,
					dao);
		} catch (ModelDataOperationException e) {
			logger.error("remove FamilyRecord is fail");
			throw new ServiceException(e);
		}
	}

	/**
	 * 解除该成员与家庭的关系
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doRemoveFamilyNumber(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String phrId = (String) body.get("pkey");
		HealthRecordModel hrModel = new HealthRecordModel(dao);
		try {
			UserRoleToken ur = UserRoleToken.getCurrent();
			String userId = ur.getUserId();
			hrModel.updateHealthRecordByPhrId(null, userId, phrId);
		} catch (ModelDataOperationException e) {
			logger.error("update familyId of healthrecord is fail");
			throw new ServiceException(e);
		}
		// 增加家庭成员变动记录
		Map<String, Object> hrMap = null;
		try {
			hrMap = hrModel.getHealthRecordByPhrId(phrId);
		} catch (ModelDataOperationException e) {
			logger.error("Get health record by phrId failure.", e);
			throw new ServiceException(e);
		}
		if (hrMap != null && hrMap.size() > 0) {
			// 增加家庭成员变动记录
			Map<String, Object> fmrMap = new HashMap<String, Object>();
			fmrMap.put("personName", hrMap.get("personName"));
			fmrMap.put("sexCode", hrMap.get("sexCode"));
			fmrMap.put("moveType", "4");
			fmrMap.put("phrId", hrMap.get("phrId"));
			fmrMap.put("newFamilyId", hrMap.get("familyId"));
			fmrMap.put("manaDoctorId", hrMap.get("manaDoctorId"));
			fmrMap.put("regionCode", hrMap.get("regionCode"));
			fmrMap.put("regionCode_text", hrMap.get("regionCode_text"));
			fmrMap.put("manaUnitId", hrMap.get("manaUnitId"));
			fmrMap.put("applyUnit", UserUtil.get(UserUtil.USER_ID));
			fmrMap.put("applyUser", UserUtil.get(UserUtil.MANAUNIT_ID));
			fmrMap.put("applyDate", Calendar.getInstance().getTime());
			FamilyRecordModule frModel = new FamilyRecordModule(dao);
			try {
				frModel.saveFMChangeRecord("create", fmrMap, false);
			} catch (ModelDataOperationException e) {
				logger.error("Save family member change record failure.", e);
				throw new ServiceException(e);
			}
		}
	}

	/**
	 * 控制按钮权限
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadControl(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		try {
			Map<String, Object> data = (Map<String, Object>) req.get("body");
			System.out.println("test....." + data);
			Map<String, Boolean> update = ControlRunner.run(EHR_FamilyRecord,
					data, ctx, ControlRunner.UPDATE);
			res.put("body", update);
		} catch (Exception e) {
			logger.error("Save hospitalization message error!", e);
			throw new ServiceException(e);
		}
	}
	/**
	 * 查询家医签约居民个人信息。
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws Getjsdqyjcxx
	 */
	protected void doSaveHealthRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		String empiid = body.get("empiid") + "";
		//Map<String, Object> res = new HashMap<String, Object>();
		String sql="select regionCode as REGIONCODE from EHR_HealthRecord where empiId=:empiId ";
		Map<String, Object> p=new HashMap<String, Object>();
		p.put("empiId", empiid);
		try {
			Map<String, Object> regionCodemap=dao.doSqlLoad(sql, p);
			String regionCode=regionCodemap.get("REGIONCODE")==null?"":regionCodemap.get("REGIONCODE")+"";
			if(regionCode!=null && regionCode.length() >0 ){
				Map<String, Object> r=new HashMap<String, Object>();
				Map<String, Object> data=new HashMap<String, Object>();
				String hql="select a.regionName as REGIONNAME from EHR_AreaGrid a where a.regionCode=:regionCode";
				if(regionCode.length() >2){
					r.put("regionCode", regionCode.substring(0,2));
					data=dao.doSqlLoad(hql, r);
					res.put("signer_province", data.get("REGIONNAME")==null?"":data.get("REGIONNAME")+"");
					res.put("signer_provincecode", regionCode.substring(0,2)+"");
				}
				if(regionCode.length() >4){
					r.put("regionCode", regionCode.substring(0,4));
					data=dao.doSqlLoad(hql, r);
					res.put("signer_city", data.get("REGIONNAME")==null?"":data.get("REGIONNAME")+"");
					res.put("signer_citycode", regionCode.substring(0,4)+"");
				}
				if(regionCode.length() >6){
					r.put("regionCode", regionCode.substring(0,6));
					data=dao.doSqlLoad(hql, r);
					res.put("signer_district", data.get("REGIONNAME")==null?"":data.get("REGIONNAME")+"");
					res.put("signer_districtcode",regionCode.substring(0,6)+"");
				}
				if(regionCode.length() >9){
					r.put("regionCode", regionCode.substring(0,9));
					data=dao.doSqlLoad(hql, r);
					res.put("signer_street", data.get("REGIONNAME")==null?"":data.get("REGIONNAME")+"");
					res.put("signer_streetcode",regionCode.substring(0,9)+"");
				}
			}
			UserRoleToken user = UserRoleToken.getCurrent();
			String uid=user.getUserId();
			res.put("userIdcode",user.getUserId());
		}catch (Exception e){
			logger.error("family Query is fail!", e);
			throw new ServiceException(e);
		}
	}
	@SuppressWarnings("unchecked")
	public void doLoadContractControl(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		try {
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			String familyId = (String) body.get("familyId");
			FamilyRecordModule fm = new FamilyRecordModule(dao);
			Map<String, Object> data = fm.getFamilyRecordById(familyId);
			System.out.println("test....." + data);
			Map<String, Boolean> update = ControlRunner.run(EHR_FamilyRecord,
					data, ctx, ControlRunner.UPDATE);
			res.put("body", update);
		} catch (Exception e) {
			logger.error("Save hospitalization message error!", e);
			throw new ServiceException(e);
		}
	}

	public void doQueryContractContent(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String familyId = (String) req.get("familyId");
		FamilyRecordModule frModule = new FamilyRecordModule(dao);
		try {
			Map<String, Object> record = frModule.getFamilyRecordById(familyId);
			String ownerName = frModule.getOwnerName(familyId);
			if (record != null) {
				Map<String, Object> body = new HashMap<String, Object>();
				String sql="select personName as personName from MPI_DemographicInfo where empiId=:empiId";
				Map<String, Object> p=new HashMap<String, Object>();
				p.put("empiId", ownerName);
				try {
					Map<String, Object> Repremap=dao.doSqlLoad(sql, p);
					body.put("FC_RepreName", Repremap.get("PERSONNAME")+"");
				} catch (PersistentDataOperationException e) {
					e.printStackTrace();
				}
				body.put("FC_Repre", ownerName);
				body.put("FC_Phone", record.get("familyHome"));
				body.put("FC_Party2", ownerName);
				String userName = UserUtil.get(UserUtil.USER_NAME);
				body.put("FC_Party", userName);
//				body = SchemaUtil.setDictionaryMessageForForm(body,
//						EHR_FamilyContractBase);
				
				res.put("body", body);
			}
		} catch (ModelDataOperationException e) {
			logger.error("query Contract Content is fail!", e);
			throw new ServiceException(e);
		}
	}

	public void doFamilyQuery(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		String familyId = (String) req.get("initDataId");
		String FC_Id = (String) req.get("FC_Id");
		HealthRecordModel hrModel = new HealthRecordModel(dao);
		try {
			List<Map<String, Object>> list = hrModel
					.getHealthRecordByFamilyId2(familyId);
			FamilyRecordModule frModule = new FamilyRecordModule(dao);
			List<Map<String, Object>> body = new ArrayList<Map<String, Object>>();
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Map<String, Object> map = list.get(i);
					String empiId = (String) map.get("empiId");
					Map<String, Object> record = frModule.getFamilyContract(
							empiId, FC_Id);
					if (record == null) {
						EmpiModel em = new EmpiModel(dao);
						Map<String, Object> personInfo = em
								.getEmpiInfoByEmpiid(empiId);
						personInfo.put("FC_Id", FC_Id);
						personInfo.put("FS_EmpiId", empiId);
						personInfo = SchemaUtil.setDictionaryMessageForList(
								personInfo, EHR_FamilyContractService);
						body.add(personInfo);
					} else {
						// String FS_Kind = (String) record.get("FS_Kind");
						// String FS_Kind_other =
						// FS_Kind.substring(FS_Kind.indexOf(":")+1);
						// record.put("FS_Kind_other", FS_Kind_other);
						// record =
						// SchemaUtil.setDictionaryMessageForList(record,
						// EHR_FamilyContractService);
						body.add(record);
					}
				}
			}
			res.put("body", body);
		} catch (ModelDataOperationException e) {
			logger.error("family Query is fail!", e);
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doSaveContractBase(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws JSONException, ServiceException {
		Map<String, Object> formData = (Map<String, Object>) req
				.get("formData");
		List<Map<String, Object>> gridData = (List<Map<String, Object>>) req
				.get("gridData");
		String pkey = (String) req.get("pkey");
		String familyId = (String) req.get("familyId");
		String op = "create";
		if (!pkey.equals("") && null != pkey) {
			op = "update";
		}
		FamilyRecordModule frModule = new FamilyRecordModule(dao);
		try {
			// Date FC_Stop_Date = null;
			// SimpleDateFormat sdf = new
			// SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			boolean jyFlag = false;
			if (formData.get("FC_Stop_Date") != null
					&& !"".equals(formData.get("FC_Stop_Date"))) {
				// String stop = (String) formData.get("FC_Stop_Date");
				// stop = stop.replace("T", " ");
				// FC_Stop_Date = sdf.parse(stop);
				formData.put("FC_Sign_Flag", "2");
				jyFlag = true;
			}
			String begin = (String) formData.get("FC_Begin");
			begin = begin.replace("T", " ");
			formData.put("F_Id", familyId);
			Map<String, Object> contractRecord = frModule
					.saveFamilyContractBase(formData, op);
			if ("create".equals(op)) {
				pkey = (String) contractRecord.get("FC_Id");
			}
			vLogService.saveVindicateLog(EHR_FamilyContractBase, op, pkey, dao);
			// Date FC_Begin = sdf.parse(begin);
			// Map<String, Object> familyRecord = frModule
			// .getFamilyRecordById(familyId);
			// if (FC_Stop_Date != null) {
			// familyRecord.put("cy_ContractDate", null);
			// familyRecord.put("cy_isContract", "0");
			// } else {
			// familyRecord.put("cy_ContractDate", FC_Begin);
			// familyRecord.put("cy_isContract", "1");
			// }
			// frModule.saveFamilyRecord("update", familyRecord);
			HealthRecordModel hrModel = new HealthRecordModel(dao);
			for (int i = 0; i < gridData.size(); i++) {
				Map<String, Object> gridDataBody = gridData.get(i);
				gridDataBody.remove("FS_EmpiId_text");
				String FS_EmpiId = (String) gridDataBody.get("FS_EmpiId");
				Map<String, Object> info = hrModel
						.getHealthRecordByEmpiId(FS_EmpiId);
				if (info == null) {
					continue;
				}
				String phrId = (String) info.get("phrId");
				Map<String, Object> fcsRecord = frModule.getFamilyContract(FS_EmpiId, pkey);
				String FS_PersonGroup = (String) gridDataBody.get("FS_PersonGroup");
				String FS_Kind = (String) gridDataBody.get("FS_Kind");
				String signFlag = (String) gridDataBody.get("signFlag");
				if (fcsRecord != null) {
					fcsRecord.put("FS_PersonGroup", FS_PersonGroup);
					fcsRecord.put("FS_Kind", FS_Kind);
					String oldFlag = (String) info.get("signFlag");
					if(gridDataBody.get("FS_CreateDate")==null 
							|| "".equals(""+gridDataBody.get("FS_CreateDate"))){
						fcsRecord.put("FS_CreateDate", new Date());
					}else{
						fcsRecord.put("FS_CreateDate", gridDataBody.get("FS_CreateDate"));
					}
					if (info != null) {
						if (jyFlag|| (signFlag != null && !signFlag.equals(oldFlag))) {
							info.put("signFlag", signFlag);
							if (jyFlag) {
								info.put("signFlag", "n");
							}
							hrModel.saveHealthRecord("update", info);
							vLogService.saveVindicateLog(EHR_HealthRecord, "2",phrId, dao, FS_EmpiId);
						}
					}
					frModule.saveFamilyContract(fcsRecord, "update");
					vLogService.saveVindicateLog(EHR_FamilyContractService,
							"2", pkey, dao);
				} else if (FS_PersonGroup != null && !"".equals(FS_PersonGroup)
						||( FS_Kind != null && !"".equals(FS_Kind))) {
					if(gridDataBody.get("FS_CreateDate")==null 
							|| "".equals(""+gridDataBody.get("FS_CreateDate"))){
						gridDataBody.put("FS_CreateDate", new Date());
					}
					gridDataBody.put("FC_Id", pkey);
					Map<String, Object> rsMap = frModule.saveFamilyContract(gridDataBody, "create");
					String pkey2 = (String) rsMap.get("FS_Id");
					vLogService.saveVindicateLog(EHR_FamilyContractService,
							"1", pkey2, dao);
					if (info != null) {
						info.put("signFlag", "y");
						if (jyFlag) {
							info.put("signFlag", "n");
						}
						hrModel.saveHealthRecord("update", info);
						vLogService.saveVindicateLog(EHR_HealthRecord, "2",
								phrId, dao, FS_EmpiId);
					}
				}

				//如果签约标志是y则更新个人签约记录
				String grqyempiId=(String)gridDataBody.get("FS_EmpiId");
				if(gridDataBody.get("signFlag")!=null)
				{
					String grqybz=(String)gridDataBody.get("signFlag");
					if(grqybz.equals("y"))
					{
						vLogService.saveRecords("GRQY","create", dao,grqyempiId);	
					}else
					{
						vLogService.saveRecords("GRQY","logout", dao,grqyempiId);	
					}
				}else
				{
						vLogService.saveRecords("GRQY","logout", dao,grqyempiId);
				}
				
			}
			Map<String, Object> body = new HashMap<String, Object>();
			body.put("pkey", pkey);
			res.put("body", body);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doCheckUpCreate(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws JSONException, ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String empiId = (String) body.get("empiId");
		String familyId = (String) body.get("familyId");
		String manageUnitId = UserRoleToken.getCurrent().getManageUnitId();
		if (manageUnitId.length() > 9) {
			manageUnitId = manageUnitId.substring(0, 9);
		} else {
			manageUnitId = manageUnitId.substring(0, manageUnitId.length());
		}
		FamilyRecordModule frModule = new FamilyRecordModule(dao);
		TemplateModule tm = new TemplateModule(dao);
		try {
			Map<String, Object> familyRecord = frModule
					.getFamilyRecordById(familyId);
			String manaDoctorId = (String) familyRecord.get("manaDoctorId");
			String uid = UserRoleToken.getCurrent().getUserId();
			if (!manaDoctorId.equals(uid)) {
				res.put("canCreate", "6");
			}
			List<Map<String, Object>> list = tm
					.getTemplateCountByManageUnitId(manageUnitId);
			int count = 0;
			if (list != null) {
				count = list.size();
			}
			if (count == 0) {
				res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
				res.put(Service.RES_MESSAGE, "本机构还未维护服务记录模板，请维护好模板后再使用该业务。");
			}
			boolean selectTemplate = true;
			if (count == 1) {
				selectTemplate = false;
			}
			List<Map<String, Object>> baseList = frModule
					.getFamilyContractBaseByFId(familyId);
			String canCreate = "2";
			if (baseList != null && baseList.size() != 0) {
				Map<String, Object> baseRecord = null;
				for (int i = 0; i < baseList.size(); i++) {
					baseRecord = baseList.get(i);
					String FC_Sign_Flag = (String) baseRecord
							.get("FC_Sign_Flag");
					if ("1".equals(FC_Sign_Flag)) {
						canCreate = "1";
						break;
					}
				}
				if ("1".equals(canCreate)) {
					Date FC_End = (Date) baseRecord.get("FC_End");
					Calendar end = Calendar.getInstance();
					end.setTime(FC_End);
					Calendar now = Calendar.getInstance();
					now.setTime(new Date());
					if (end.getTimeInMillis() < now.getTimeInMillis()) {
						canCreate = "3";
					} else {
						HealthRecordModel hrModel = new HealthRecordModel(dao);
						Map<String, Object> person = hrModel
								.getHealthRecordByEmpiId(empiId);
						String signFlag = (String) person.get("signFlag");
						if ("y".equals(signFlag)) {
							canCreate = "5";
						} else {
							canCreate = "4";
						}
					}
				}
			}
			res.put("canCreate", canCreate);
			res.put("selectTemplate", selectTemplate);
			res.put("templates", list);
		} catch (ModelDataOperationException e) {
			logger.error("doCheckUpCreate is fail");
			throw new ServiceException(e);
		}
	}

	protected void doRemove(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		String fcId = (String) req.get("pkey");
		FamilyRecordModule frModule = new FamilyRecordModule(dao);
		try {
			List<Map<String, Object>> list = frModule
					.getFamilyContractByFcId(fcId);
			HealthRecordModel hrModel = new HealthRecordModel(dao);
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map = list.get(i);
				String FS_EmpiId = (String) map.get("FS_EmpiId");
				Map<String, Object> info = hrModel
						.getHealthRecordByEmpiId(FS_EmpiId);
				info.put("signFlag", "n");
				hrModel.saveHealthRecord("update", info);
			}
			frModule.deleteFamilyContractBaseByFcId(fcId);
			frModule.deleteFamilyContractServiceByFcId(fcId);
			vLogService
					.saveVindicateLog(EHR_FamilyContractBase, "4", fcId, dao);
			vLogService.saveVindicateLog(EHR_FamilyContractService, "4", fcId,
					dao);
			String familyId = (String) req.get("familyId");
			Map<String, Object> fr = frModule.getFamilyRecordById(familyId);
			fr.put("cy_ContractDate", null);
			fr.put("cy_isContract", "0");
			frModule.saveFamilyRecord("update", fr);
		} catch (ModelDataOperationException e) {
			logger.error("doRemove is fail");
			throw new ServiceException(e);
		}
	}

	protected void doGetPersonGroup(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String empiId = (String) req.get("empiId");
		Map<String, Object> lnr = null;
		Map<String, Object> ycf = null;
		Map<String, Object> et = null;
		Map<String, Object> jsb = null;
		Map<String, Object> mb = null;
		Map<String, Object> mbgxy = null;
		Map<String, Object> mbtnb = null;
		Map<String, Object> cjr = null;
		try {
			OldPeopleRecordModel om = new OldPeopleRecordModel(dao);
			lnr = om.getOldPeopleRecordByEmpiId(empiId);
			if (lnr == null) {
				PregnantRecordModel prm = new PregnantRecordModel(dao);
				List<Map<String, Object>> list = prm
						.getNormalPregnantRecord(empiId);
				if (list != null && list.size() > 0) {
					ycf = list.get(0);
				}
				if (ycf == null) {
					ChildrenHealthModel cm = new ChildrenHealthModel(dao);
					et = cm.getManageChildHealthCardByEmpiId(empiId);
				}
			}
			PsychosisRecordModel psm = new PsychosisRecordModel(dao);
			jsb = psm.getPsychosisRecordByEmpiId(empiId);
			DefModel dm = new DefModel(dao);
			List<Map<String, Object>> cjrList = null;
			cjrList = dm.getDefRecordByEmpiId(empiId);
			if (cjrList != null && cjrList.size() > 0) {
				cjr = cjrList.get(0);
			} else {
				cjrList = dm.getDefIntellectRecordByEmpiId(empiId);
				if (cjrList != null && cjrList.size() > 0) {
					cjr = cjrList.get(0);
				} else {
					cjrList = dm.getDefBrainRecordByEmpiId(empiId);
					if (cjrList != null && cjrList.size() > 0) {
						cjr = cjrList.get(0);
					}
				}
			}
			HypertensionModel hm = new HypertensionModel(dao);
			mb = hm.getHypertensionByEmpiId(empiId);
			mbgxy=mb;
			if (mb == null) {
				DiabetesRecordModel drm = new DiabetesRecordModel(dao);
				mb = drm.getDiabetesByEmpiId(empiId);
				mbtnb=mb;
			}
			String group = "";
			if (lnr != null) {
				group = "大于或等于65岁老人";
			} else if (ycf != null) {
				group = "孕产妇";
			} else if (et != null) {
				group = "0-6岁儿童";
			}
			if (mb != null) {
				if (group.length() > 0) {
					group += ",";
				}
				group += "慢性病患者";
			}
			if (cjr != null) {
				if (group.length() > 0) {
					group += ",";
				}
				group += "残疾人群";
			}
			if (jsb != null) {
				if (group.length() > 0) {
					group += ",";
				}
				group += "重性精神病患者";
			}if (mbgxy != null) {
				if (group.length() > 0) {
					group += ",";
				}
				group += "高血压患者";
			}if (mbtnb != null) {
				if (group.length() > 0) {
					group += ",";
				}
				group += "糖尿病患者";
			}
			if (group.length() == 0) {
				group += "普通人群";
			}
			res.put("personGroup", group);
		} catch (ModelDataOperationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 判断是否有家庭成员
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	protected void doIsFamilyNumber(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, PersistentDataOperationException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		FamilyRecordModule model = new FamilyRecordModule(dao);
		String code = "";
		try {
			code = model.updateFamilyStatus(body.get("familyId") + "");
			if ("n".equals(code)) {
				res.put(Service.RES_CODE, Constants.CODE_RECORD_EXSIT);
				res.put(Service.RES_MESSAGE, "请先解除家庭成员，才能执行该操作");
				return;
			} else {
				res.put(Service.RES_CODE, Constants.CODE_OK);
				return;
			}

		} catch (ModelDataOperationException e) {

			throw new ServiceException(e);
		}

	}

	/**
	 * 修改家庭的状态,当没有家庭成员的时候，状态改为1，
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	protected void doRemoveFamilyInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, PersistentDataOperationException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		FamilyRecordModule model = new FamilyRecordModule(dao);
		String empiId = (String) body.get("empiId");
		try {
			model.removeFamilyByFimlyId(body.get("familyId") + "");
			//model.deleteFamilyMidleByEmpiId(empiId);
		} catch (ModelDataOperationException e) {
			logger.error("doRemove is fail");
			throw new ServiceException(e);
		}

	}

	/**
	 * 删除家庭成员
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	protected void doRemoveFamily(Map<String, Object> req,

	Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException,
			ModelDataOperationException, PersistentDataOperationException,
			ParseException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		FamilyRecordModule model = new FamilyRecordModule(dao);

		model.removeFamilyByid(body.get("phrId") + "");

	}

	/**
	 * 判断一个家庭档案是否有户主
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	protected void doIsOwnerName(Map<String, Object> req,

	Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException,
			ModelDataOperationException, PersistentDataOperationException,
			ParseException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		Map<String, Object> healthMap = new HashMap<String, Object>();
		FamilyRecordModule model = new FamilyRecordModule(dao);
		String familyId = (String) body.get("familyId");
		boolean flag = false;
		if (familyId != null && familyId.length() > 0) {
			flag = model.HaveOwnerName(familyId);
		}
		if (flag) {
			healthMap = model.SelectOwnerName(familyId);
			res.put(Service.RES_CODE, Constants.CODE_OK);
			res.put("body", healthMap);
			return;
		} else {
			res.put(Service.RES_CODE, Constants.CODE_RECORD_EXSIT);
			res.put(Service.RES_MESSAGE, "该家庭档案没有户主！");
			return;
		}

	}

	/**
	 * 根据个人健康档案主键查询数据
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	protected void doLoadInfo(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException,
			ModelDataOperationException, PersistentDataOperationException,
			ParseException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		Map<String, Object> map = new HashMap<String, Object>();
		FamilyRecordModule model = new FamilyRecordModule(dao);
		String phrId = body.get("pkey") + "";
		map.put("phrId", phrId);
		Map<String, Object> mapInfo = model.selectHealthInfo(map);
		mapInfo = SchemaUtil.setDictionaryMessageForForm(mapInfo,
				EHR_HealthRecord);
		// 获取该名字
		map.clear();
		map.put("empiId", mapInfo.get("empiId"));
		Map<String, Object> m = model.getValueByParameters(MPI_DemographicInfo,
				"personName", map);
		mapInfo.putAll(m);
		Map<String, Object> m1 = model.getValueByParameters(
				MPI_DemographicInfo, "sexCode", map);
		mapInfo.putAll(m1);
		// 组装数据，
		mapInfo.put("sourceArea", mapInfo.get("regionCode"));
		Map<String, Object> targetArea = new HashMap<String, Object>();
		targetArea.put("key", "");
		targetArea.put("text", "");
		mapInfo.put("targetArea", targetArea);
		res.put("body", mapInfo);

	}

	/**
	 * 家庭成员迁移的记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveInfo(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException,
			ModelDataOperationException, PersistentDataOperationException,
			ParseException {
		FamilyRecordModule model = new FamilyRecordModule(dao);
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		Map<String, Object> data = new HashMap<String, Object>();
		Map<String, Object> dataValue = new HashMap<String, Object>();
		data.put("phrId", body.get("phrId"));
		// String recordMoveId =(String) body.get("recordMoveId");
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		try {
			Map<String, Object> mapData = model.getValueByParameters(
					EHR_HealthRecord, "masterFlag", data);
			body.put("familyId", body.get("familyId"));
			if (mapData != null && mapData.size() > 0) {
				if ("y".equals(mapData.get("masterFlag") + "")) {// 户主迁出
					model.updateOnwerName(body.get("familyId") + "", "");
					model.updateMasterFlagByEmpiId(body.get("phrId") + "", "n","");
				} else {// 成员迁出
					model.updateMasterFlagByEmpiId(body.get("phrId") + "", "n","");
				}
			}
			// 记录成员变动记录
			dataValue = dao.doSave("create", EHR_Record, body, false);
			body.put("recordMoveId", dataValue.get("recordMoveId"));
			resBodyMap = SchemaUtil.setDictionaryMessageForForm(body, EHR_Record);
		} catch (ModelDataOperationException e) {
			logger.error("家庭成员迁移失败!");
			throw new ServiceException(e);
		}
		//增加个档案迁移申请记录
		Map<String, Object> movEHRMap = new HashMap<String, Object>();
		movEHRMap.put("archiveType", "1");//档案类别 为 个人档案
		movEHRMap.put("moveType", "2");//迁移类别 为 申请迁出
		movEHRMap.put("archiveId", body.get("phrId"));//档案编号
		movEHRMap.put("personName", body.get("personName"));//姓名
		movEHRMap.put("status", "1");//迁移状态 待确认
		//movEHRMap.put("affirmType", "");//确认处理 
		movEHRMap.put("sourceDoctor", body.get("manaDoctorId"));//确认处理 
		movEHRMap.put("sourceArea", body.get("regionCode"));//原网格地址 编码
		movEHRMap.put("sourceArea_text", body.get("regionCode_text"));//原网格地址
		movEHRMap.put("sourceUnit", body.get("manaUnitId"));//原管辖机构
		movEHRMap.put("targetArea", body.get("targetArea"));//现网格地址 编码
		movEHRMap.put("targetArea_text", body.get("targetArea_text"));//现网格地址
		movEHRMap.put("targetDoctor", body.get("targetDoctor"));//现责任医生
		movEHRMap.put("targetUnit", body.get("targetUnit"));//现管辖机构
		movEHRMap.put("applyReason", body.get("applyReason"));//申请原因
		movEHRMap.put("movesub", "y");//申请原因
		String curUserId = UserUtil.get(UserUtil.USER_ID);
		String curUnitId = UserUtil.get(UserUtil.MANAUNIT_ID);
		Date curDate = Calendar.getInstance().getTime();
		movEHRMap.put("applyUser", curUserId);//申请人
		movEHRMap.put("applyUnit", curUnitId);//申请机构
		movEHRMap.put("applyDate", curDate);//申请日期
		movEHRMap.put("lastModifyUnit", curUnitId);//最后修改单位
		movEHRMap.put("lastModifyUser", curUserId);//最后修改人
		movEHRMap.put("lastModifyDate", curDate);//最后修改日期
		EHRMoveModule ehrMoveModule = new EHRMoveModule(dao);
		ehrMoveModule.saveEHRMoveApply("create", movEHRMap, true);
		res.put("body", resBodyMap);
	}
	protected void doCheckpeople(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException,
			ModelDataOperationException, PersistentDataOperationException,
			ParseException {
		Map<String,Object> jymap=new HashMap<String, Object>();
		jymap.put("mobileNumber","本人电话");
		jymap.put("contact","联系人姓名");
		jymap.put("contactPhone","联系人电话");
		jymap.put("registeredPermanent","常住类型");
		jymap.put("nationCode","民族");
		jymap.put("bloodTypeCode","血型");
		jymap.put("rhBloodCode","RH血型");
		jymap.put("educationCode","文化程度");
		jymap.put("workCode","职业类别");
		jymap.put("maritalStatusCode","婚姻状况");
		jymap.put("insuranceCode","医疗支付方式");
		jymap.put("isAgrRegister","是否农业户籍");
		jymap.put("knowFlag","居民知晓");
		Map<String,Object> jwsmap=new HashMap<String, Object>();
		jwsmap.put("01", "过敏史");
		jwsmap.put("02", "疾病史");
		jwsmap.put("03", "手术史");
		jwsmap.put("06", "外伤史");
		jwsmap.put("04", "输血史");
		jwsmap.put("07", "家族疾病史-父亲");
		jwsmap.put("08", "家族疾病史-母亲");
		jwsmap.put("09", "家族疾病史-兄弟姐妹");
		jwsmap.put("10", "家族疾病史-子女");
		jwsmap.put("11", "残疾状况");
		String familyId=req.get("familyId")+"";
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "familyId", "s", familyId);
		List<Map<String,Object>>hrlist=dao.doQuery(cnd, "", EHR_HealthRecord);
		String tsmsg="";//提示字符串
		if(hrlist!=null && hrlist.size() >0){
			for(int i=0;i<hrlist.size();i++){
				String tempmsg="";
				Map<String,Object> one=hrlist.get(i);
				List<?> mpicnd = CNDHelper.createSimpleCnd("eq", "empiId", "s", one.get("empiId")+"");
				Map<String,Object> mpimap=dao.doLoad(mpicnd, MPI_DemographicInfo);
				for(String key:jymap.keySet()){
					if(one.containsKey(key)){
						if(one.get(key)==null || (one.get(key)+"").length()==0){
							tempmsg+=jymap.get(key)+"、";
						}
					}
					if(mpimap.containsKey(key)){
						if(mpimap.get(key)==null || (mpimap.get(key)+"").length()==0){
							tempmsg+=jymap.get(key)+"、";
						}
					}
				};
				List<Map<String,Object>> jwslist=dao.doList(mpicnd, "", EHR_PastHistory);
				Map<String,Object> jws=new HashMap<String, Object>();
				for(Map<String,Object> temp:jwslist){
					jws.put(temp.get("pastHisTypeCode")+"", temp.get("diseaseText")+"");
				}
				for(String key:jwsmap.keySet()){
					if(jws.containsKey(key)){
						
					}else{
						tempmsg+=jwsmap.get(key)+"、";
					}
				}
				if(tempmsg.length() >0){
					tsmsg+="成员【"+mpimap.get("personName")+"】的档案信息中 "+tempmsg.substring(0,tempmsg.length()-1)+" 填写不规范;";
				}
			}
		}
		if(tsmsg.length() >1)
		res.put("tsmsg", tsmsg.substring(0, tsmsg.length()-1)+"。");
	}
	protected String getjsdurl(String manageUnit){
		String urls="http://12.43.53.4:7080/FamilyDoctorInterface/services/FamilyDoctorInterfaceService?wsdl";
		if(manageUnit.indexOf("320111")==0){
			urls="http://32.26.3.95:7080/FamilyDoctorInterface/services/FamilyDoctorInterfaceService?wsdl";
		}
		return urls;
	}
	//同步到捷士达
	protected void doSendtojsd(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException,
			ModelDataOperationException, PersistentDataOperationException,
			ParseException {
		UserRoleToken ur = UserRoleToken.getCurrent();
		String manageUnit=ur.getManageUnitId();
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		FamilyRecordModule model = new FamilyRecordModule(dao);
		ObjectMapper mapper=new ObjectMapper();
		Map<String, Object> data=new HashMap<String, Object>();
		model.Sendtojsd(body, data);
		try {
			URL url=new URL(getjsdurl(manageUnit));
			FamilyDoctorInterfaceService fd=new FamilyDoctorInterfaceService(url);
			FamilyDoctorInterfaceServicePortType jk=fd.getFamilyDoctorInterfaceServiceHttpPort();
			String re=jk.uploadSignInfo(mapper.writeValueAsString(data));
			try {
				JSONObject reo=new JSONObject(re);
				if(reo.getString("success").equals("false")){
					res.put("code", "400");
					res.put("msg",reo.getString("message"));
				};
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//下载捷士达签约信息
	protected void doLoadjsdqyxx(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException,
			ModelDataOperationException, PersistentDataOperationException,
			ParseException {
		UserRoleToken ur = UserRoleToken.getCurrent();
		String manageUnit=ur.getManageUnitId();
		String idcard=req.get("idcard")+"";
		String mpisql="select a.personname as PERSONNAME from mpi_demographicinfo a where a.idcard=:idcard";
		Map<String, Object> p=new HashMap<String, Object>();
		p.put("idcard", idcard);
		Map<String, Object> mpimap=dao.doSqlLoad(mpisql, p);
		if(mpimap==null && mpimap.size()<=0){
			res.put("code",400);
			res.put("msg","本系统未找到输入身份证号的基本信息，请核实！");
			return;
		}
		p=null;
		try {
			URL url=new URL(getjsdurl(manageUnit));
			FamilyDoctorInterfaceService fd=new FamilyDoctorInterfaceService(url);
			FamilyDoctorInterfaceServicePortType jk=fd.getFamilyDoctorInterfaceServiceHttpPort();
			JSONObject json=new JSONObject();
			try {
				json.put("signerIden", idcard);
				String re=jk.getSignInfo(json.toString());
				JSONObject reo=new JSONObject(re);
				if(reo.getString("success").equals("false")){
					res.put("code", "400");
					res.put("msg",reo.getString("message"));
				}else{
					Map<String, Object> data=new HashMap<String, Object>();
					data=(Map<String,Object>)JSON.parse(reo.getString("resultData"));
					System.out.println(reo.getString("resultData"));
					data.put("idcard", idcard);
					data.put("personname", mpimap.get("PERSONNAME")+"");
					res.put("data", data);
				};
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
}
