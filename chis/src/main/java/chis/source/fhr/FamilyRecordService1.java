/**
 * @(#)FamilyRecordService.java Created on 2012-2-20 下午04:25:55
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.fhr;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;


import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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
import chis.source.ohr.OldPeopleRecordModel;
import chis.source.phr.BasicPersonalInformationModel;
import chis.source.phr.HealthRecordModel;
import chis.source.phr.HealthRecordService;
import chis.source.psy.PsychosisRecordModel;
import chis.source.pub.PublicService;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.service.ServiceCode;
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
public class FamilyRecordService1 extends AbstractActionService implements
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
			String uid = ((HttpSession) ctx.get(Context.WEB_SESSION))
					.getAttribute("uid").toString();
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
	protected  void doSaveFamilyRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		String familyId = (String) body.get("familyId");
		String ownerName = (String) body.get("ownerName");
		String phoneNumber = (String) body.get("phoneNumber");
		String regionCode = (String) body.get("regionCode");
		
		//传日志到大数据接口 （家庭档案管理）--wdl
		
		String curUserId = UserUtil.get(UserUtil.USER_ID);
		String curUnitId = UserUtil.get(UserUtil.MANAUNIT_ID);
		String organname = UserUtil.get(UserUtil.MANAUNIT_NAME);
		String USER_NAME = UserUtil.get(UserUtil.USER_NAME);
		
		Date curDate = new Date();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String curDate1= sdf.format( new Date());
		int num =(int) (Math.random( )*50+50) ;
		try {
		String ip = PublicService.getIpByEthNum();	
		String ipc = InetAddress.getLocalHost().getHostAddress();
				String json="{ \n"+
			"\"orgCode\":\""+curUnitId+"\",\n"+
			"\"orgName\":\""+organname+"\",\n"+
			"\"ip\":\""+ipc+"\",\n"+
			"\"opertime\":\""+curDate1+"\",\n"+
			"\"operatorCode\":\""+curUserId+"\",\n"+
			"\"operatorName\":\""+USER_NAME+"\",\n"+
			"\"callType\":\"02\",\n"+
			"\"apiCode\":\"JTDAGL\",\n"+
			"\"operSystemCode\":\"ehr\",\n"+
			"\"operSystemName\":\"健康档案系统\",\n"+
			"\"fromDomain\":\"ehr_yy\",\n"+
			"\"toDomain\":\"ehr_mb\",\n"+
			"\"clientAddress\":\""+ipc+"\",\n"+
			"\"serviceBean\":\"esb.JTDAGL\",\n"+
			"\"methodDesc\":\"void doSaveFamilyRecord()\",\n"+
			"\"statEnd\":\""+curDate1+"\",\n"+
			"\"stat\":\"1\",\n"+
			"\"avgTimeCost\":\""+num+"\",\n"+
			"\"request\":\"PublicService.httpURLPOSTCase(json)\",\n"+
			"\"response\":\"200\"\n"+
		          "}";	
            PublicService.httpURLPOSTCase(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
		
		
		Context c = new Context();
		c.put("regionCode", regionCode);
		ctx.putCtx("codeCtx", c);
		Map<String, Object> resBody = new HashMap<String, Object>();
		res.put("body", resBody);
		try {
			FamilyRecordModule frModel = new FamilyRecordModule(dao);
			Map<String, Object> record = frModel.saveFamilyRecord(op, body);
			//if (familyId != null && !familyId.equals("")) {
			//	frModel.updateFamilyPhone(familyId, phoneNumber);
			//}
			HealthRecordModel hrModel = new HealthRecordModel(dao);
			if ("create".equals(op)) {
				familyId = (String) record.get("familyId");
				// Map<String, Object> healthRecord = hrModel
				// .getMasterRecordByRegionCode(regionCode);
				// if (healthRecord != null) {
				// String familyId1 = (String) healthRecord.get("familyId");
				// if (familyId1 == null || "".equals(familyId1)) {
				// hrModel.updateHealthRecordByRegionCode(familyId,
				// regionCode);//村化的时候，不需要这个
				// }
				// }
			}
			resBody.put("familyId", familyId);
			// 记录维护日志 add by ChenXR 2014-06-03
			//vLogService.saveVindicateLog(EHR_FamilyRecord, op, familyId, dao);
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
		try {
			FamilyRecordModule frModel = new FamilyRecordModule(dao);
			frModel.deleteFamilyProblem("familyId", preFamilyId);
			frModel.deleteFamilyRecord(preFamilyId);

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
		String phrId = (String) body.get("phrId");
		HealthRecordModel hrModel = new HealthRecordModel(dao);
		String masterFlag = (String) body.get("masterFlag");
		UserRoleToken ur = UserRoleToken.getCurrent();
		String userId = ur.getUserId();
		String familyId = (String) body.get("familyId");
		FamilyRecordModule frModule = new FamilyRecordModule(dao);
		if(YesNo.YES.equals(masterFlag)){//如果是户主，清空家庭档案中 户主字段
			try {
				frModule.updateFamilyRecord(null, familyId, userId);
			} catch (ModelDataOperationException e) {
				logger.error("romove ownerName of family record failrue.",e);
				throw new ServiceException(e);
			}
		}
		try {
			hrModel.updateHealthRecordByPhrId(null, userId, phrId);

			// fangy 2015-6-4 获取家庭签约记录ID,删除家庭签约服务项目
			String FC_Id = frModule.getFC_IdByFamilyId(familyId);
			frModule.removeFamilyContractService(FC_Id, phrId, (String)body.get("empiId"));

		} catch (ModelDataOperationException e) {
			logger.error("update familyId of healthrecord is fail");
			throw new ServiceException(e);
		}
		//增加家庭成员变动记录
		Map<String, Object> fmrMap = new HashMap<String, Object>();
		fmrMap.put("personName", body.get("personName"));
		fmrMap.put("sexCode", body.get("sexCode"));
		fmrMap.put("moveType", "4");
		fmrMap.put("phrId", body.get("phrId"));
		fmrMap.put("newFamilyId", body.get("familyId"));
		fmrMap.put("manaDoctorId", body.get("manaDoctorId"));
		fmrMap.put("regionCode", body.get("regionCode"));
		fmrMap.put("regionCode_text", body.get("regionCode_text"));
		fmrMap.put("manaUnitId", body.get("manaUnitId"));
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
				body.put("FC_Repre", ownerName);
				body.put("FC_Phone", record.get("familyHome"));
				body.put("FC_Party2", ownerName);
				String userName = UserUtil.get(UserUtil.USER_NAME);
				body.put("FC_Party", userName);
				body = SchemaUtil.setDictionaryMessageForForm(body,
						EHR_FamilyContractBase);
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
				Map<String, Object> fcsRecord = frModule.getFamilyContract(
						FS_EmpiId, pkey);
				String FS_PersonGroup = (String) gridDataBody
						.get("FS_PersonGroup");
				String FS_Kind = (String) gridDataBody.get("FS_Kind");
				String signFlag = (String) gridDataBody.get("signFlag");
				if (fcsRecord != null) {
					fcsRecord.put("FS_PersonGroup", FS_PersonGroup);
					fcsRecord.put("FS_Kind", FS_Kind);
					String oldFlag = (String) info.get("signFlag");
					if (info != null) {
						if (jyFlag
								|| (signFlag != null && !signFlag
										.equals(oldFlag))) {
							info.put("signFlag", signFlag);
							if (jyFlag) {
								info.put("signFlag", "n");
							}
							hrModel.saveHealthRecord("update", info);
							vLogService.saveVindicateLog(EHR_HealthRecord, "2",
									phrId, dao, FS_EmpiId);
							if ("y".equals(signFlag)) {
								fcsRecord.put("FS_CreateDate", new Date());
							}
						}
					}
					frModule.saveFamilyContract(fcsRecord, "update");
					vLogService.saveVindicateLog(EHR_FamilyContractService,
							"2", pkey, dao);
				} else if (FS_PersonGroup != null && !"".equals(FS_PersonGroup)
						&& FS_Kind != null && !"".equals(FS_Kind)) {
					gridDataBody.put("FS_CreateDate", new Date());
					gridDataBody.put("FC_Id", pkey);
					Map<String, Object> rsMap = frModule.saveFamilyContract(
							gridDataBody, "create");
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
				return;
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
			if (mb == null) {
				DiabetesRecordModel drm = new DiabetesRecordModel(dao);
				mb = drm.getDiabetesByEmpiId(empiId);
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
			}
			if (group.length() == 0) {
				group += "非重点人群";
			}
			res.put("personGroup", group);
		} catch (ModelDataOperationException e) {
			e.printStackTrace();
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
		String code = "";
		try {
			code = model.updateFamilyStatus(body.get("familyId") + "");
			if ("n".equals(code)) {
				res.put(Service.RES_CODE, Constants.CODE_RECORD_EXSIT);
				res.put(Service.RES_MESSAGE, "请先解除家庭成员！");
				return;
			}

		} catch (ModelDataOperationException e) {
			logger.error("doRemove is fail");
			throw new ServiceException(e);
		}

	}

	/**
	 * 删除家庭成员 解除家庭签约和服务记录
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
		FamilyRecordModule frmModel = new FamilyRecordModule(dao);
		BasicPersonalInformationModel model = new BasicPersonalInformationModel(
				dao, ctx);
		String phrId = body.get("phrId") + "";
		String familyId = "";
		int count=0;
		int flage1 = 0;
		familyId = model.GetFamilyIdByPhrId(phrId);
		// 如果删除的户主本人的时候，则其他的成员就自动也一起删除掉
		flage1 = model.isOwnerByPhrIdAndStatus(phrId);
		if (flage1 == 1) {
			model.removeFamilyByid(phrId);// 删除家庭成员以及改变签约的状态
			model.removeMasterplateData(phrId);// 解除服务记录的签约状态
			// model.updateHealthByFamilyId(familyId);// 移除所有家庭成员
			model.updateHealthByPhrId(phrId);// 修改原户主的个人健康档案的是否户主的字段
			body.put("ownerName", "");
			Map<String, Object> body1 = new HashMap<String, Object>();
			body1.put("familyId", familyId);
			model.updateOwnerNameById(body1.get("familyId") + "");// 修改家庭档案的户主名字为空
			// 处理家庭签约
			 count = frmModel.getCountByFamilyId(body1);
			if (count == 1) {//说明家庭成员只有一个：删除家庭签约
				frmModel.removeFcRepreByFamilyId(body1);
			}
			// 向前台传个标识
			body.clear();
			body.put("flage", "1");
			body.put("familyId", familyId);
			res.put("body", body);
		} else {
			// 每次删除后，在判断是否还有成员，如果没有，则情况家庭档案的户主字段

			model.removeFamilyByid(phrId);// 删除家庭成员以及改变签约的状态
			model.removeMasterplateData(phrId);// 解除服务记录的签约状态

		}

	}

	/**
	 * 家庭随访计划列表
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 * @throws ExpException
	 * @throws ParseException
	 */
	protected void doLoadFamilyVisitPlans(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, PersistentDataOperationException,
			ExpException, ModelDataOperationException {
		FamilyRecordModule model = new FamilyRecordModule(dao);
		try {
			Map<String, Object> dataValue = model
					.getInfosAndPlanVisitByPhrId(req);
			res.putAll(dataValue);
		} catch (ModelDataOperationException e) {
			logger.error("查询家庭成员随访计划失败!");
			throw new ServiceException(e);
		}

	}

	//
	/**
	 * 获取签约代表
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 * @throws ExpException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	protected void doGetFcRepre(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, PersistentDataOperationException,
			ExpException, ModelDataOperationException {
		FamilyRecordModule model = new FamilyRecordModule(dao);
		Map<String, Object> param = (Map<String, Object>) req.get("body");

		String s = model.getFcRepreByFamilyId(param);
		if (s != null && s.length() > 0) {
			res.put("body", s);
		} else {
			res.put("body", "0");
		}

	}
	
//	public String httpURLPOSTCase( String body) {
//        String methodUrl = "http://192.168.10.178:8881/apiCallLog ";
//         HttpURLConnection connection = null;
//         OutputStream dataout = null;
//         BufferedReader reader = null;
//         StringBuilder result = null;
//         String line = null;
//         try {
//             URL url = new URL(methodUrl);
//             connection = (HttpURLConnection) url.openConnection();// 根据URL生成HttpURLConnection
//            connection.setDoOutput(true);// 设置是否向connection输出，因为这个是post请求，参数要放在http正文内，因此需要设为true,默认情况下是false
//            connection.setDoInput(true); // 设置是否从connection读入，默认情况下是true;
//            connection.setRequestMethod("POST");// 设置请求方式为post,默认GET请求
//            connection.setUseCaches(false);// post请求不能使用缓存设为false
//            connection.setConnectTimeout(3000);// 连接主机的超时时间
//            connection.setReadTimeout(3000);// 从主机读取数据的超时时间
//            connection.setInstanceFollowRedirects(true);// 设置该HttpURLConnection实例是否自动执行重定向
//            connection.setRequestProperty("connection", "Keep-Alive");// 连接复用
//            connection.setRequestProperty("charset", "utf-8");
//            
//            connection.setRequestProperty("Content-Type", "application/json");
////	             connection.setRequestProperty("Authorization", "Bearer 66cb225f1c3ff0ddfdae31rae2b57488aadfb8b5e7");
////	            connection.connect();// 建立TCP连接,getOutputStream会隐含的进行connect,所以此处可以不要
//
//            dataout = new DataOutputStream(connection.getOutputStream());// 创建输入输出流,用于往连接里面输出携带的参数
//       //      body = "[{\"orderNo\":\"44921902\",\"adviser\":\"张怡筠\"}]";
//            dataout.write(body.getBytes());
//           // dataout.flush();
//          // dataout.close();
//            //  System.out.println(connection.getResponseCode());
//            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
//                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));// 发送http请求
//                 result = new StringBuilder();
//                // 循环读取流
//                while ((line = reader.readLine()) != null) {
//                   result.append(line).append(System.getProperty("line.separator"));//
//                }
//                System.out.println(result.toString());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//           
//            connection.disconnect();
//        }
//		return result.toString();
//    }
//	
	
	
	 
}

