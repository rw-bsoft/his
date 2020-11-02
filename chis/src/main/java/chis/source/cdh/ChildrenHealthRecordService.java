/**
 * @(#)ChildrenHealthRecordService.java Created on 2011-12-27 下午4:34:42
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */

package chis.source.cdh;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.admin.AreaGridModel;
import chis.source.admin.SystemUserModel;
import chis.source.control.ControlRunner;
import chis.source.dic.BusinessType;
import chis.source.dic.CancellationReason;
import chis.source.dic.Education;
import chis.source.dic.Maritals;
import chis.source.dic.PastHistoryCode;
import chis.source.dic.PlanStatus;
import chis.source.dic.RolesList;
import chis.source.dic.YesNo;
import chis.source.empi.EmpiModel;
import chis.source.empi.EmpiUtil;
import chis.source.fhr.FamilyRecordModule;
import chis.source.mdc.DiabetesRecordModel;
import chis.source.mdc.HypertensionModel;
import chis.source.mhc.PregnantRecordModel;
import chis.source.ohr.OldPeopleRecordModel;
import chis.source.phr.HealthRecordModel;
import chis.source.phr.PastHistoryModel;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.service.ServiceCode;
import chis.source.util.ApplicationUtil;
import chis.source.util.BSCHISUtil;
import chis.source.util.SchemaUtil;
import chis.source.visitplan.VisitPlanCreator;
import chis.source.visitplan.VisitPlanModel;

import com.alibaba.fastjson.JSONException;

import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

/**
 * @description 儿保服务
 * 
 * @author <a href="mailto:chenhb@bsoft.com.cn">chenhuabin</a>
 */

public class ChildrenHealthRecordService extends AbstractActionService
		implements DAOSupportable {

	private static final Logger logger = LoggerFactory
			.getLogger(ChildrenHealthRecordService.class);

	private VisitPlanCreator visitPlanCreator;

	/**
	 * 
	 * @return the visitPlanCreator
	 */
	public VisitPlanCreator getVisitPlanCreator() {
		return visitPlanCreator;
	}

	/**
	 * 
	 * @param visitPlanCreator
	 *            the visitPlanCreator to set
	 */
	public void setVisitPlanCreator(VisitPlanCreator visitPlanCreator) {
		this.visitPlanCreator = visitPlanCreator;
	}

	/**
	 * 根据儿童身份证获取儿童基本信息 或者姓名+性别+出生日期获取儿童基本信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doGetChildBaseInfoByIdCard(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ChildrenBaseModel chm = new ChildrenBaseModel(dao);
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String idCard = (String) reqBody.get("idCard");
		List<Map<String, Object>> info = null;
		try {
			if (idCard != null && idCard.trim().length() > 0) {
				info = chm.getChildBaseInfoByIdCard(EmpiUtil
						.idCard15To18(idCard.toUpperCase()));
				if (info != null && info.size() > 0) {
					res.put("body", SchemaUtil.setDictionaryMessageForForm(
							info, MPI_ChildBaseInfo));
				} else {
					Map<String, Object> map = EmpiUtil.queryByIdCardAndName(
							dao, ctx, reqBody);
					if (map.get("dataSource") != null
							&& "pix".equals(map.get("dataSource"))) {
						info = (List<Map<String, Object>>) map.get("body");
						info = EmpiUtil.changeToBSInfo(info);
						info = SchemaUtil.setDictionaryMessageForForm(info,
								MPI_DemographicInfo);
						res.put("body", info);
						res.put("dataSource", "pix");
					} else {
						res.putAll(map);
					}
				}
			} else {
				Map<String, Object> map = EmpiUtil.queryByPersonInfo(dao, ctx,
						reqBody);
				info = (List<Map<String, Object>>) map.get("body");
				if (map.get("dataSource") != null
						&& "pix".equals(map.get("dataSource"))) {
					info = EmpiUtil.changeToBSInfo(info);
					info = SchemaUtil.setDictionaryMessageForForm(info,
							MPI_DemographicInfo);
					res.put("body", info);
					res.put("dataSource", "pix");
				} else {
					res.putAll(map);
				}
			}
		} catch (ModelDataOperationException e) {
			logger.error("Get children base infomation failed.", e);
			throw new ServiceException(e);
		}

	}

	/**
	 * 保存儿童基本信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveChildBaseMessage(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ChildrenBaseModel cbm = new ChildrenBaseModel(dao);
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String birthday = ((String) reqBody.get("birthday")).substring(0, 10);
		String op = (String) req.get("op");
		String empiId = (String) reqBody.get("empiId");
		String certificateNo = (String) reqBody.get("certificateNo");
		boolean isRepeat = false;
		try {
			isRepeat = cbm.checkCertificateNo(empiId, certificateNo);
		} catch (ModelDataOperationException e1) {
			logger.error("check  child  certificateNo  is repeat failed.", e1);
			throw new ServiceException(e1);
		}
		if (isRepeat) {
			res.put(RES_CODE, ServiceCode.CODE_TARGET_EXISTS);
			res.put(RES_MESSAGE, "儿童出生证明编号重复");
			return;
		}
		// @@ 如果是更新并且出生日期发生变化需要更新随访计划。
		if ("update".equals(op)) {
			Map<String, Object> healthCard;
			try {
				ChildrenHealthModel chm = new ChildrenHealthModel(dao);
				healthCard = chm.getNormalHealthCardByEmpiId(empiId);
			} catch (ModelDataOperationException e) {
				logger.error("get child normal record failed.", e);
				throw new ServiceException(e);
			}
			if (healthCard != null && healthCard.size() != 0) {
				try {
					reCreateVisitPlan(empiId, birthday, dao, ctx);
				} catch (Exception e) {
					logger.error("reCreate children plan failed.", e);
					throw new ServiceException(e);
				}
			}
		}
		// ****保存儿童基本信息****
		try {
			String idCard = StringUtils.trimToEmpty((String) reqBody
					.get("idCard"));
			if (idCard != null && !"".equals(idCard)) {
				reqBody.put("idCard", idCard.toUpperCase());
			}
			reqBody.put("educationCode", Education.OTHER);
			reqBody.put("maritalStatusCode", Maritals.SINGLE);
			empiId = EmpiUtil.saveEmpiInfo(reqBody, ctx, dao, res);
		} catch (Exception e) {
			logger.error("save child  base record failed.", e);
			res.put(RES_CODE, ServiceCode.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "保存儿童基本信息失败。");
			throw new ServiceException(e);

		}
		// ** 保存卡信息
		Map<String, Object> card = (Map<String, Object>) reqBody.get("card");
		if (card != null && card.size() > 0) {
			EmpiModel em = new EmpiModel(dao);
			try {
				String cardId = com.bsoft.pix.dao.IdMaker.makeId();
				card.put("empiId", empiId);
				card.put("cardId", cardId);
				em.saveCard(card);
			} catch (ModelDataOperationException e) {
				logger.error("save child  card  failed.", e);
				throw new ServiceException(e);
			}
		}

		try {
			// ****保存儿童直系亲属相关信息****
			reqBody.put("empiId", empiId);
			cbm.saveChildInfo(reqBody, op);

			// ****保存儿童个人健康****
			String regionCode = (String) reqBody.get("regionCode");
			FamilyRecordModule familyRecordModule = new FamilyRecordModule(dao);
			String familyId = familyRecordModule
					.getFamilyIdByRegionCode(regionCode);
			reqBody.put("familyId", familyId);
			cbm.saveChildrenHealthRecord(reqBody);
		} catch (ModelDataOperationException e) {
			logger.error("Save children base massage failed.", e);
			throw new ServiceException(e);
		}
		Map<String, Object> resBody = new HashMap<String, Object>(reqBody);
		resBody.put("empiId", empiId);
		resBody.put("birthday", birthday);
		res.put("body", resBody);
	}

	/**
	 * 如果儿童出生日期变更，重新生成计划
	 * 
	 * @param body
	 * @param businessType
	 *            计划类型
	 * @param ctx
	 * @throws ServiceException
	 * @throws ControllerException
	 * @throws NumberFormatException
	 */
	private void reCreateVisitPlan(String empiId, String nowBirth, BaseDAO dao,
			Context ctx) throws ServiceException, NumberFormatException,
			ControllerException {
		EmpiModel em = new EmpiModel(dao);
		Date oldBirthday = null;
		try {
			oldBirthday = (Date) em.getSingleEmpiField(empiId, "birthday");
		} catch (ModelDataOperationException e) {
			logger.error("get child old birthday failed .", e);

			throw new ServiceException(e);
		}
		Date nowBirthday = BSCHISUtil.toDate(nowBirth);
		int dateCompare = BSCHISUtil.dateCompare(oldBirthday, nowBirthday);
		if (dateCompare == 0) {
			return;
		}
		ChildrenHealthModel chm = new ChildrenHealthModel(dao);
		try {
			chm.changeChildPlan(nowBirth, empiId);
		} catch (ModelDataOperationException e) {
			logger.error("change child visit plan date failed .", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 根据儿童EMPIID获取儿童基本信息
	 * 
	 * @param req
	 * @param res
	 * @param session
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doGetChildBaseInfoByEmpiId(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ChildrenBaseModel chm = new ChildrenBaseModel(dao);
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String empiId = (String) reqBody.get("empiId");
		Map<String, Object> info = null;
		try {
			info = chm.getChildBaseInfoByEmpiId(empiId);
		} catch (ModelDataOperationException e) {
			logger.error("Get children base infomation by empiId failed.", e);
			throw new ServiceException(e);
		}
		res.put("body",
				SchemaUtil.setDictionaryMessageForForm(info, MPI_ChildBaseInfo));
	}

	/**
	 * 根据儿童PhrId获取儿童基本信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doGetChildBaseInfoByPhrId(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ChildrenBaseModel cbm = new ChildrenBaseModel(dao);
		HealthRecordModel hrm = new HealthRecordModel(dao);
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String phrId = (String) reqBody.get("phrId");
		Map<String, Object> info = null;
		try {
			String empiId = hrm.getEmpiId(phrId);
			if (empiId != null) {
				info = cbm.getChildBaseInfoByEmpiId(empiId);
			}
		} catch (ModelDataOperationException e) {
			logger.error("Get children base infomation by phrId failed.", e);
			throw new ServiceException(e);
		}
		res.put("body",
				SchemaUtil.setDictionaryMessageForForm(info, MPI_ChildBaseInfo));
	}

	/**
	 * 根据网格地址编码获取责任医生信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doGetManaDoctorInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String regionCode = (String) body.get("regionCode");
		res.put("body", body);
		try {
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
			logger.error("Get children base infomation by phrId failed.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 根据网格地址编码获取儿保医生信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doGetCdhDoctorInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String regionCode = (String) body.get("regionCode");
		res.put("body", body);
		try {
			AreaGridModel agModel = new AreaGridModel(dao);
			Map<String, Object> nodeMap = agModel.getNodeInfo(regionCode);
			body.put("nodeMap", nodeMap);
			if (nodeMap == null) {
				return;
			}
			String cdhDoctor = (String) nodeMap.get("cdhDoctor");
			body.putAll(SchemaUtil.setDictionaryMessageForForm(nodeMap,
					EHR_AreaGridChild));
			if (cdhDoctor == null) {
				return;
			}

			SystemUserModel suModel = new SystemUserModel(dao);
			List<Map<String, Object>> manageUnits = suModel.getUserByLogonName(
					cdhDoctor, RolesList.EBYS);
			body.put("manageUnits", manageUnits);
		} catch (ModelDataOperationException e) {
			logger.error("Get children base infomation by phrId failed.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 根据儿童卡号获取儿童基本信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doGetChildBaseInfoByCardNo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ChildrenBaseModel chm = new ChildrenBaseModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String cardNo = (String) body.get("cardNo");
		boolean isDeadRegist = (Boolean) body.get("isDeadRegist");
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		if (cardNo != null && cardNo.length() > 0) {
			try {
				lists = chm.getChildBaseInfoByCardNo(cardNo);
			} catch (ModelDataOperationException e) {
				logger.error("Get children base infomation by CardNo failed.",
						e);
				throw new ServiceException(e);
			}
		}
		if (lists == null || lists.size() == 0) {
			Map<String, Object> map = EmpiUtil.queryByCardNo(dao, ctx, body);
			List<Map<String, Object>> records = (List<Map<String, Object>>) map
					.get("body");
			if (records == null || records.size() == 0) {
				return;
			}
			if (map.get("dataSource") != null
					&& "pix".equals(map.get("dataSource"))) {
				lists = (List<Map<String, Object>>) map.get("body");
				lists = EmpiUtil.changeToBSInfo(lists);
				lists = SchemaUtil.setDictionaryMessageForForm(lists,
						MPI_DemographicInfo);
				res.put("body", lists);
				res.put("dataSource", "pix");
			} else {
				res.putAll(map);
			}
		}
		String childrenDieAge = null;
		String childrenRegisterAge = null;
		try {
			childrenDieAge = ApplicationUtil.getProperty(Constants.UTIL_APP_ID,
					"childrenDieAge");
			childrenRegisterAge = ApplicationUtil.getProperty(
					Constants.UTIL_APP_ID, "childrenRegisterAge");
		} catch (ControllerException e1) {
			throw new ServiceException(e1);
		}
		if (childrenDieAge == null || childrenDieAge.equals("")
				|| childrenRegisterAge == null
				|| childrenRegisterAge.equals("")) {
			res.put(RES_CODE, Constants.CODE_INVALID_REQUEST);
			res.put(RES_MESSAGE, "请先完成儿童模块相关参数配置,方能对儿童进行建档等操作！");
			return;
		}
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		boolean ageInvalid = false;
		if (lists != null && lists.size() > 0) {
			for (Map<String, Object> info : lists) {
				Date birth = (Date) info.get("birthday");
				int age = BSCHISUtil.calculateAge(birth, null);
				// **判断是否为儿童死亡登记
				if (isDeadRegist) {
					if (age > Integer.parseInt(childrenDieAge)) {
						ageInvalid = true;
						continue;
					}
				} else {
					if (age > Integer.parseInt(childrenRegisterAge)) {
						ageInvalid = true;
						continue;
					}
				}
				String empiId = (String) info.get("empiId");
				Map<String, Object> otherInfo;
				try {
					otherInfo = chm.getOtherChildBaseInfo(empiId);
				} catch (ModelDataOperationException e) {
					logger.error(
							"Get children health record infomation by empiid failed.",
							e);
					throw new ServiceException(e);
				}
				info.putAll(otherInfo);
				list.add(info);
			}
		}
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		if (list.size() == 1) {
			Map<String, Object> map = list.get(0);
			data.add(SchemaUtil.setDictionaryMessageForForm(map,
					MPI_ChildBaseInfo));
		} else {
			data = SchemaUtil.setDictionaryMessageForList(list,
					MPI_ChildBaseInfo);
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("data", data);
		if (data.size() < 1) {
			if (ageInvalid == true) {
				if (isDeadRegist) {
					result.put("ageInvalidMsg", "该用户已经超过" + childrenDieAge
							+ "周岁,无法进行死亡登记!");
				} else {
					result.put("ageInvalidMsg", "该用户已经超过" + childrenRegisterAge
							+ "周岁,无法建立档案!");
				}
			}
		}
		res.put("body", result);
	}

	/**
	 * 根据直系亲属身份证获取儿童基本信息列表
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doGetChildBaseInfoByRelative(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ChildrenBaseModel chm = new ChildrenBaseModel(dao);
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String idCard = (String) reqBody.get("idCard");
		boolean recordExists = (Boolean) reqBody.get("recordExists");
		List<Map<String, Object>> list = null;
		try {
			list = chm.getChildInfoByRelativeIdCard(idCard);
			if (list.size() > 0) {
				List<Map<String, Object>> array = new ArrayList<Map<String, Object>>();
				for (int i = 0; i < list.size(); i++) {
					Map<String, Object> childinfo = (Map<String, Object>) list
							.get(i);
					String empiId = (String) childinfo.get("childEmpiId");
					Map<String, Object> info = chm
							.getChildBaseInfoByEmpiId(empiId);
					if (info != null && info.size() > 1) {
						array.add(info);
					}
				}
				Map<String, Object> resBody = new HashMap<String, Object>();
				if (array.size() < 1) {
					resBody.put("hasChild", false);
				} else {
					resBody.put("hasChild", true);
					resBody.put("childData", SchemaUtil
							.setDictionaryMessageForList(array,
									MPI_ChildBaseInfo));
				}
				resBody.put(
						"relativeData",
						getRelativeMessageByIdCard(idCard, recordExists, dao,
								ctx));
				res.put("body", resBody);
			} else {
				Map<String, Object> resBody = new HashMap<String, Object>();
				resBody.put("hasChild", false);
				resBody.put(
						"relativeData",
						getRelativeMessageByIdCard(idCard, recordExists, dao,
								ctx));
				res.put("body", resBody);
			}
		} catch (ModelDataOperationException e) {
			logger.error("Get children base infomation by Relative failed.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 检验儿童档案是否存在
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doCheckHealthCardExists(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String empiId = (String) reqBody.get("empiId");
		ChildrenHealthModel chm = new ChildrenHealthModel(dao);
		boolean recordExists = false;
		try {
			Map<String, Object> data = chm.getNormalHealthCardByEmpiId(empiId);
			if (data == null || data.size() < 1) {
				recordExists = false;
			} else {
				recordExists = true;
			}
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("recordExists", recordExists);
			res.put("body", result);
		} catch (ModelDataOperationException e) {
			logger.error("Check existance of children record failed.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 1>根据请求中的pkey获取儿童档案信息
	 * 2>根据1>的结果如果存在fatherEmpiId,motherEmpiId则获取其对应的姓名fatherName,motherName
	 * 3>查询该儿童个人健康档案中的父母信息，跟儿童档案中的父母信息比对，返回是否一致(isParentSame)
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doGetChildHealthCard(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String pkey = (String) reqBody.get("pkey");
		ChildrenHealthModel chm = new ChildrenHealthModel(dao);
		EmpiModel empi = new EmpiModel(dao);
		Map<String, Object> childMap;
		try {
			childMap = chm.getChildHealthCardByPhrId(pkey);
			if (childMap == null) {
				return;
			}
			if (childMap.get("fatherEmpiId") != null) {
				String fatherEmpiId = (String) childMap.get("fatherEmpiId");
				String fatherName = empi.getPersonName(fatherEmpiId);
				childMap.put("fatherName", fatherName);
			}
			if (childMap.get("motherEmpiId") != null) {
				String motherEmpiId = (String) childMap.get("motherEmpiId");
				String motherName = empi.getPersonName(motherEmpiId);
				childMap.put("motherName", motherName);
			}
		} catch (ModelDataOperationException e) {
			logger.error("Get children HealthCard failed.", e);
			throw new ServiceException(e);
		}
		res.put("body", SchemaUtil.setDictionaryMessageForForm(childMap,
				CDH_HealthCard));
	}

	/**
	 * 获取儿童出生缺陷记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doGetChildDefectRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String pkey = (String) reqBody.get("phrId");
		ChildrenHealthModel chm = new ChildrenHealthModel(dao);
		EmpiModel empi = new EmpiModel(dao);
		try {
			Map<String, Object> defect = chm.getChildDefectRecord(pkey);
			if (defect == null || defect.size() < 1) {
				return;
			}
			if (defect.get("motherEmpiId") != null) {
				String motherEmpiId = (String) defect.get("motherEmpiId");
				String motherName = empi.getPersonName(motherEmpiId);
				defect.put("motherName", motherName);
			}
			res.put("body", SchemaUtil.setDictionaryMessageForForm(defect,
					CDH_DefectRegister));
		} catch (ModelDataOperationException e) {
			logger.error("Get children defect record  failed.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 根据直系亲属身份证（idCard）获取亲属的相关信息
	 * 
	 * @param idCard
	 * @param childExist
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	protected List<Map<String, Object>> getRelativeMessageByIdCard(
			String idCard, boolean childExist, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		HealthRecordModel healthrecordModule = new HealthRecordModel(dao);
		Map<String, Object> relative = healthrecordModule
				.getHealthRecordByIdCard(idCard, ctx);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if (relative != null) {
			Map<String, Object> record = new HashMap<String, Object>();
			record.put("relativeEmpiId", relative.get("empiId"));
			record.put("relativeName", relative.get("personName"));
			record.put("contact", relative.get("personName"));
			record.put("relativeIdCard", relative.get("idCard"));
			record.put("registeredPermanent",
					relative.get("registeredPermanent"));
			if (!childExist) {
				record.put("regionCode", relative.get("regionCode"));
				record.put("manaDoctorId", relative.get("manaDoctorId"));
				record.put("manaUnitId", relative.get("manaUnitId"));
				record.put("isAgrRegister", relative.get("isAgrRegister"));
			}
			list.add(SchemaUtil.setDictionaryMessageForForm(record,
					MPI_ChildBaseInfo));
		} else {
			logger.error("Not found child relative record .");
		}
		return list;
	}

	/**
	 * 获取儿童档案初始化信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doInitChildHealth(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PastHistoryModel phm = new PastHistoryModel(dao);
		ChildrenBaseModel chm = new ChildrenBaseModel(dao);
		HealthRecordModel healthrecordModule = new HealthRecordModel(dao);
		ChildrenHealthModel childModel = new ChildrenHealthModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String empiId = (String) body.get("empiId");
		Map<String, Object> resMap = new HashMap<String, Object>();
		Map<String, Object> resBody = new HashMap<String, Object>();
		try {
			// chb 获取健康档案中信息
			Map<String, Object> healthInfo = healthrecordModule
					.getHealthRecordByEmpiId(empiId);
			resMap.put("manaDoctorId", healthInfo.get("manaDoctorId"));
			resMap.put("homeAddress", healthInfo.get("regionCode"));
			resMap.put("homeAddress_text", healthInfo.get("regionCode_text"));
			// chb 获取直系亲属信息
			Map<String, Object> childInfo = chm.getChildInfoByEmpiId(empiId);
			// chb 根据childInfo中的 出生证号 获取新生儿访视信息
			if (childInfo != null && !childInfo.isEmpty()) {
				if (childInfo.get("certificateNo") != null) {
					String certificateNo = (String) childInfo
							.get("certificateNo");
					Map<String, Object> cNo = childModel
							.getBabyVisitInfoByCertificateNo(certificateNo);
					if (cNo != null && !cNo.isEmpty()) {
						resMap.put("gestation", cNo.get("gestation"));
						resMap.put("boneCondition", cNo.get("birthStatus"));
						resMap.put("otherBone", cNo.get("otherStatus"));
						resMap.put("birthWeight", cNo.get("weight"));
						resMap.put("birthHeight", cNo.get("length"));
						resMap.put("heardScreen", cNo.get("hearingTest"));
					}
				}
			}
			Map<String, Object> pastMess = new HashMap<String, Object>();
			// chb 残疾情况
			List<Map<String, Object>> pastHistoryList = childModel
					.getPastHistoryByEmpiIdAndTypeCode(empiId,
							PastHistoryCode.DEFORMITY);
			if (pastHistoryList != null) {
				if (pastHistoryList != null) {
					Map<String, Object> deformityMap = phm.buildPastHistoryDic(
							pastHistoryList, "deformity", "otherDeformity",
							PastHistoryCode.PASTHIS_DEFORMITY_OTHER);
					if (deformityMap != null && deformityMap.size() > 0) {
						pastMess.putAll(deformityMap);
					}
				}
			}

			// chb 药物过敏史
			pastHistoryList = childModel.getPastHistoryByEmpiIdAndTypeCode(
					empiId, PastHistoryCode.ALLERGIC);
			if (pastHistoryList != null) {
				Map<String, Object> allergicMap = phm.buildPastHistoryDic(
						pastHistoryList, "allergicHistory", "otherAllergyDrug",
						PastHistoryCode.PASTHIS_ALLERGIC_CODE);
				if (allergicMap != null && allergicMap.size() > 0) {
					pastMess.putAll(allergicMap);
				}
			}
			String cdhDoctorId = childModel.getCDHDoctor(empiId);
			if (cdhDoctorId != null && !"".equals(cdhDoctorId)) {
				resMap.put("cdhDoctorId", cdhDoctorId);
				resBody = SchemaUtil.setDictionaryMessageForForm(resMap,
						CDH_HealthCard);
				SystemUserModel suModel = new SystemUserModel(dao);
				List<Map<String, Object>> manageUnits = suModel
						.getUserByLogonName(cdhDoctorId, RolesList.EBYS);
				resBody.put("manageUnits", manageUnits);
			} else {
				resBody = SchemaUtil.setDictionaryMessageForForm(resMap,
						CDH_HealthCard);
			}
			resBody.putAll(pastMess);
		} catch (ModelDataOperationException e) {
			logger.error("Init children health massage failed.", e);
			throw new ServiceException(e);
		}
		res.put("body", resBody);
	}

	/**
	 * 获取产时记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doQueryDeliveryRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		String idcard = (String) body.get("idcard");
		String skey = (String) body.get("skey");
		PregnantRecordModel pm = new PregnantRecordModel(dao);
		try {
			List<Map<String, Object>> list = pm.getDeliveryRecordByIdCard(skey,
					idcard);
			res.put("body", list);
		} catch (ModelDataOperationException e) {
			logger.error("get delivery record  failed.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 保存儿童档案记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveChildrenRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PastHistoryModel phm = new PastHistoryModel(dao);
		ChildrenHealthModel chm = new ChildrenHealthModel(dao);
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		Map<String, Object> rebody = new HashMap<String, Object>();
		String ops = (String) req.get("op");
		String phrId = (String) body.get("phrId");
		String empiId = (String) body.get("empiId");
		String deformity = (String) body.get("deformity");
		try {
			if (deformity != null && !"".equals(deformity)) {
				Integer result = chm.updateDisabilityMonitor(phrId, deformity);
				if (result == 0) {
					if (!deformity
							.equals(PastHistoryCode.PASTHIS_NOTDEFORMITY_CODE)) {
						rebody.put("disabilityMonitor", true);
					}
				} else if (result == 1) {
					throw new ServiceException(
							"残疾情况不能为【无残疾】，疑似残疾儿童报告中有儿童疑似残疾信息");
				} else if (result == 3) {
					rebody.put("updateDisabilityMonitor", true);
				}
			}
		} catch (ModelDataOperationException e) {
			logger.error("faild to update  children disability monitor.", e);
			throw new ServiceException(e);
		}

		// chb 获取原档案的残疾情况和过敏信息
		String preDefo = "";
		String preAllHis = "";
		if (phrId != null && !phrId.equals("")) {
			Map<String, Object> oldHealthRecord;
			try {
				oldHealthRecord = chm.getChildHealthCardByPhrId(phrId);
			} catch (ModelDataOperationException e) {
				logger.error("get child pasthistory message failed.", e);
				throw new ServiceException(e);
			}
			if (oldHealthRecord != null && oldHealthRecord.size() > 0) {
				preDefo = StringUtils.trimToEmpty((String) oldHealthRecord
						.get("deformity"));
				preAllHis = StringUtils.trimToEmpty((String) oldHealthRecord
						.get("allergicHistory"));
			}
		}

		// chb 同步残疾情况
		try {
			if (!preDefo.equalsIgnoreCase(deformity)) {
				// chb 删除原有残疾情况记录
				phm.deletePastHistory(empiId, PastHistoryCode.DEFORMITY);
				if (deformity != null && !deformity.equals("")) {
					phm.updatePastHistory(body, "deformity", "otherDeformity",
							PastHistoryCode.DEFORMITY,
							PastHistoryCode.PASTHIS_DEFORMITY_OTHER);
				}
			}

			// chb 同步药物过敏史
			String allHis = (String) body.get("allergicHistory");
			if (!preAllHis.equalsIgnoreCase(allHis)) {
				phm.deletePastHistory(empiId, PastHistoryCode.ALLERGIC);
				if (allHis != null && !allHis.equals("")) {
					phm.updatePastHistory(body, "allergicHistory",
							"otherAllergyDrug", PastHistoryCode.ALLERGIC,
							PastHistoryCode.PASTHIS_ALLERGIC_CODE);
				}
			}

			updateChildDefect(body, dao);

			// chb 保存儿童档案
			chm.saveChildHealthCard(body, ops, res);
			Map<String, Object> resBody = (Map<String, Object>) res.get("body");
			if (ops.equalsIgnoreCase("create")) {
				body.put("phrId", resBody.get("phrId"));
				createVisitPlan(body, BusinessType.CD_IQ, ctx);
				createVisitPlan(body, BusinessType.CD_CU, ctx);
			}
			resBody.putAll(rebody);
		} catch (ModelDataOperationException e) {
			logger.error("Save children health record failed.", e);
			throw new ServiceException(e);
		}
		vLogService.saveVindicateLog(CDH_HealthCard, ops, phrId, dao, empiId);
		vLogService.saveRecords("ER", ops, dao, empiId);
		this.finishSCServiceTask(empiId, ETJD_ETFW, null, dao);
	}

	/**
	 * 初始化儿童询问记录首次随访信息 1> 根据empiid查询儿童档案，获取管辖机构(manaUnitId) 2>如果extend1 == "1"
	 * ，获取新生儿随访信息 3>根据inquireId获取前一条询问记录的断奶标志(weanFlag),如果weanFlag ==
	 * "1"，将weanFlag返回，不等于1不返回 4>查询同月龄的体格检查记录是否已经做过，做过默认体检日期为询问日期
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doInitChildInquire(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ChildrenBaseModel cbm = new ChildrenBaseModel(dao);
		ChildrenHealthModel chm = new ChildrenHealthModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String empiId = (String) body.get("empiId");
		Integer extend1 = (Integer) body.get("extend1");
		String inquireId = (String) body.get("inquireId");
		Map<String, Object> resBody = new HashMap<String, Object>();
		try {
			Map<String, Object> childHealthRecord = chm
					.getChildHealthCardByEmpiId(empiId);
			if (childHealthRecord != null && childHealthRecord.size() > 0) {
				String manaUnitId = (String) childHealthRecord
						.get("manaUnitId");
				resBody.put("manaUnitId", manaUnitId);
			}
			Map<String, Object> inquireRecord = chm
					.getChildInquireRecordByInquirId(inquireId);
			if (inquireRecord != null && !inquireRecord.isEmpty()) {
				String weanFlag = (String) inquireRecord.get("weanFlag");
				if (YesNo.YES.equals(weanFlag)) {
					resBody.put("weanFlag", weanFlag);
				}
			}
			Map<String, Object> plan = cbm.getPlanVisitDate(empiId, extend1,
					BusinessType.CD_CU, PlanStatus.VISITED);
			if (plan != null && plan.size() > 0) {
				resBody.put("inquireDate", plan.get("visitDate"));
			}
			if (1 == extend1) {
				Map<String, Object> childObject = cbm
						.getChildInfoByEmpiId(empiId);
				if (childObject == null || childObject.isEmpty()) {
					res.put("body", SchemaUtil.setDictionaryMessageForForm(
							resBody, CDH_Inquire));
					return;
				}
				String certificateNo = StringUtils
						.trimToEmpty((String) childObject.get("certificateNo"));
				if (certificateNo == null || "".equals(certificateNo)) {
					res.put("body", SchemaUtil.setDictionaryMessageForForm(
							resBody, CDH_Inquire));
					return;
				}

				Map<String, Object> visitRecord = null;
				// chb 获取新生儿访视记录的最后一条记录
				List<Map<String, Object>> visitRecordList = chm
						.getBabyVisitRecordByCertificateNoForInquire(certificateNo);
				if (visitRecordList.size() > 0) {
					visitRecord = (Map<String, Object>) visitRecordList
							.get(visitRecordList.size() - 1);
				}
				if (visitRecord == null) {
					visitRecord = new HashMap<String, Object>();
				}
				// chb 从新生儿随访基本信息中获取喂养方式
				Map<String, Object> visitInfo = chm
						.getBabyVisitInfoByCertificateNo(certificateNo);
				if (visitInfo != null && !visitInfo.isEmpty()) {
					visitRecord.put("feedWay", visitInfo.get("feedWay"));
				}
				resBody.putAll(visitRecord);
			}
			res.put("body", SchemaUtil.setDictionaryMessageForForm(resBody,
					CDH_Inquire));
		} catch (ModelDataOperationException e) {
			logger.error("Init children inquire information failed.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 1>查询儿童基本信息(MPI_ChildBaseInfo) certificateNo as birthNo, idCard as iD
	 * 2>查询儿童档案信息(CDH_HealthCard) manaUnitId as manaUnitId,gestation as
	 * gestation,birthWeight as birthWeight,litters as twins,defectsType as
	 * defectDiagnose,motherEmpiId as motherEmpiId
	 * 3>如果2>查询结果中motherEmpiId不为null，继续查询母亲信息,详见 executeDocCreateMotherInit
	 * 
	 * @param req
	 *            empiId
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doInitChildDefect(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String empiId = (String) body.get("empiId");
		ChildrenHealthModel chm = new ChildrenHealthModel(dao);
		Map<String, Object> rebody = new HashMap<String, Object>();
		try {
			Map<String, Object> healthCard = chm
					.getChildHealthCardByEmpiId(empiId);
			if (healthCard != null && healthCard.size() > 0) {
				rebody.put("manaUnitId", healthCard.get("manaUnitId"));
				rebody.put("gestation", healthCard.get("gestation"));
				rebody.put("birthWeight", healthCard.get("birthWeight"));
				rebody.put("twins", healthCard.get("litters"));
				rebody.put("defectDiagnose", healthCard.get("defectsType"));
				String motherEmpiId = (String) healthCard.get("motherEmpiId");
				rebody.put("motherEmpiId", motherEmpiId);
				if (motherEmpiId != null) {
					Map<String, Object> motherMap = chm
							.getChildMotherMess(motherEmpiId);
					rebody.putAll(motherMap);
				}
			}
		} catch (ModelDataOperationException e) {
			logger.error("Init children defect information failed.", e);
			throw new ServiceException(e);
		}
		res.put("body", SchemaUtil.setDictionaryMessageForForm(rebody,
				CDH_DefectRegister));
	}

	/**
	 * 儿童死亡监测初始化操作
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doInitChildDead(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HashMap<String, Object> reqBody = (HashMap<String, Object>) req
				.get("body");
		String empiId = StringUtils.trimToEmpty((String) reqBody.get("empiId"));
		ChildrenBaseModel cbm = new ChildrenBaseModel(dao);
		Map<String, Object> resBody = new HashMap<String, Object>();
		try {
			Map<String, Object> baseInfo = cbm.getChildBaseInfoByEmpiId(empiId);
			resBody.putAll(baseInfo);
			if (baseInfo == null || baseInfo.size() < 1) {
				return;
			}
			resBody.put("childName", StringUtils.trimToEmpty((String) baseInfo
					.get("personName")));
			resBody.put("childRegister", baseInfo.get("registeredPermanent"));
			resBody.put("childSex", baseInfo.get("sexCode"));
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get child base  message data.", e);
			throw new ServiceException(e);
		}
		ChildrenHealthModel chm = new ChildrenHealthModel(dao);
		try {
			Map<String, Object> cardInfo = chm
					.getNormalHealthCardByEmpiId(empiId);
			if (cardInfo == null || cardInfo.size() < 1) {
				return;
			}
			resBody.putAll(cardInfo);
			EmpiModel empi = new EmpiModel(dao);
			if (cardInfo.get("fatherEmpiId") != null) {
				String fatherEmpiId = (String) cardInfo.get("fatherEmpiId");
				String fatherName = empi.getPersonName(fatherEmpiId);
				resBody.put("fatherName", fatherName);
			}
			if (cardInfo.get("motherEmpiId") != null) {
				String motherEmpiId = (String) cardInfo.get("motherEmpiId");
				String motherName = empi.getPersonName(motherEmpiId);
				resBody.put("motherName", motherName);
			}
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get child record data.", e);
			throw new ServiceException(e);
		}
		res.put("body", SchemaUtil.setDictionaryMessageForForm(resBody,
				CDH_DeadRegister));
	}

	/**
	 * 初始化疑似残疾儿童报告卡
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doInitChildDisabilityMonitor(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HashMap<String, Object> reqBody = (HashMap<String, Object>) req
				.get("body");
		String phrId = (String) reqBody.get("phrId");
		ChildrenHealthModel chm = new ChildrenHealthModel(dao);
		try {
			Map<String, Object> record = chm.getChildHealthCardByPhrId(phrId);
			if (record == null || record.size() < 1) {
				return;
			}
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("phrId", record.get("phrId"));
			data.put("disabilityType", record.get("deformity"));
			res.put("body", SchemaUtil.setDictionaryMessageForForm(data,
					CDH_DisabilityMonitor));
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get child record data.", e);
			throw new ServiceException(e);
		}

	}

	/**
	 * 儿童询问记录保存
	 * 
	 * @param req
	 * @param res
	 * @param sc
	 * @param ctx
	 * @throws JSONException
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveInquireRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> data = (HashMap<String, Object>) req.get("body");
		String op = StringUtils.trimToEmpty((String) req.get("op"));
		String planId = StringUtils.trimToEmpty((String) data.get("planId"));
		String inquireDate = StringUtils.trimToEmpty((String) data
				.get("inquireDate"));
		if (planId == null || "".equals(planId)) {
			logger.error("Invalid request, plan is missing.");
			res.put(RES_CODE, ServiceCode.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "非法的计划，计划编号丢失!");
			throw new ServiceException("Invalid request, plan is missing.");
		}
		ChildrenHealthModel chm = new ChildrenHealthModel(dao);
		try {
			chm.saveChildInquireRecord(data, op, res);
		} catch (ModelDataOperationException e) {
			logger.error("Save children inquire  record error .", e);
			throw new ServiceException(e);
		}
		Map<String, Object> result = (HashMap<String, Object>) res.get("body");
		String inquireId = null;
		if (op.equalsIgnoreCase("create")) {
			inquireId = (String) result.get("inquireId");
		} else {
			inquireId = (String) data.get("inquireId");
		}
		vLogService.saveVindicateLog(CDH_Inquire, op, inquireId, dao);
		HashMap<String, Object> body = new HashMap<String, Object>();
		body.put("planId", planId);
		body.put("visitId", inquireId);
		body.put("planStatus", PlanStatus.VISITED);
		body.put("visitDate", inquireDate);
		try {
			VisitPlanModel vpm = new VisitPlanModel(dao);
			vpm.saveVisitPlan("update", body);
		} catch (Exception e) {
			logger.error("update children inquire plan  record error .", e);
			throw new ServiceException(e);
		}
		res.put("body", result);
	}

	/**
	 * 儿童死亡登记记录保存(有档案)
	 * 
	 * @param req
	 * @param res
	 * @param sc
	 * @param ctx
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveChildrenDeadRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ChildrenHealthModel chm = new ChildrenHealthModel(dao);
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		String phrId = StringUtils.trimToEmpty((String) body.get("phrId"));
		String empiId = StringUtils.trimToEmpty((String) body.get("empiId"));
		String op = StringUtils.trimToEmpty((String) req.get("op"));
		String deadDate = (String) body.get("deathDate");
		String deathReasonKey = (String) body.get("deathReason");
		String deadRegisterId = (String) body.get("deadRegisterId");
		Dictionary dic = null;
		try {
			dic = DictionaryController.instance().get(
					"chis.dictionary.deathReason");
		} catch (ControllerException e1) {
			throw new ServiceException(e1);
		}
		String deadReason = dic.getText(deathReasonKey);
		String cancellationReason = CancellationReason.PASS_AWAY;
		try {
			chm.saveChildrenDeadRecord(body, op, res);
			if (op.equals("update")) {
				// chb 更新个人健康档案的死亡信息
				HealthRecordModel hrm = new HealthRecordModel(dao);
				hrm.updateDeadMessage(phrId, deadDate, deadReason);
			} else {
				// **注销个人健康档案
				HealthRecordModel hrModel = new HealthRecordModel(dao);
				hrModel.logoutHealthRecord(phrId, deadReason,
						cancellationReason, deadDate, YesNo.YES);

				// **注销老年人档案
				OldPeopleRecordModel oprModel = new OldPeopleRecordModel(dao);
				oprModel.logoutOldPeopleRecord(phrId, cancellationReason,
						deadReason);

				// **注销高血压档案
				HypertensionModel hypModel = new HypertensionModel(dao);
				hypModel.logoutHypertensionRecord(phrId, cancellationReason,
						deadReason);

				// **注销糖尿病档案
				DiabetesRecordModel drModel = new DiabetesRecordModel(dao);
				drModel.logoutDiabetesRecord(phrId, cancellationReason,
						deadReason);

				// **注销儿童档案
				ChildrenHealthModel chModel = new ChildrenHealthModel(dao);
				chModel.logOutHealthCardRecord("phrId", phrId,
						cancellationReason, deadReason);

				// ** 注销体弱儿童档案
				DebilityChildrenModel dcModel = new DebilityChildrenModel(dao);
				dcModel.logOutDebilityChildrenRecord("empiId", empiId,
						cancellationReason, deadReason);

				// **注销未执行过的随访计划
				VisitPlanModel vpModel = new VisitPlanModel(dao);
				vpModel.logOutVisitPlan(vpModel.EMPIID, empiId);

			}
		} catch (ModelDataOperationException e) {
			logger.error("Save children dead record error .", e);
			throw new ServiceException(e);
		}
		if ("create".equals(op)) {
			Map<String, Object> resBody = (Map<String, Object>) res.get("body");
			deadRegisterId = (String) resBody.get("deadRegisterId");
		}
		vLogService.saveVindicateLog(CDH_DeadRegister, op, deadRegisterId, dao,
				empiId);
	}

	/**
	 * 儿童死亡登记记录保存(无档案)
	 * 
	 * @param req
	 * @param res
	 * @param sc
	 * @param ctx
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveChildrenDeadRecordNH(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ChildrenHealthModel chm = new ChildrenHealthModel(dao);
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		String deadRegisterId = (String) body.get("deadRegisterId");
		String op = StringUtils.trimToEmpty((String) req.get("op"));
		try {
			chm.saveChildrenDeadRecordNH(body, op, res);
		} catch (ModelDataOperationException e) {
			logger.error("Save children dead record error .", e);
			throw new ServiceException(e);
		}
		if ("create".equals(op)) {
			Map<String, Object> resBodyMap = (Map<String, Object>) res
					.get("body");
			deadRegisterId = (String) resBodyMap.get("deadRegisterId");
		}
		vLogService.saveVindicateLog(CDH_DeadRegister, op, deadRegisterId, dao);
	}

	/**
	 * 保存疑似残疾儿童报告卡信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveChildrenDisabilityMonitor(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String op = StringUtils.trimToEmpty((String) req.get("op"));
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		ChildrenHealthModel chm = new ChildrenHealthModel(dao);
		PastHistoryModel phm = new PastHistoryModel(dao);
		try {
			chm.saveDisabilityMonitorRecord(body, op, res);
			String phrId = (String) body.get("phrId");
			String disabilityType = (String) body.get("disabilityType");
			String disabilityType_text = (String) body
					.get("disabilityType_text");
			String empiId = (String) body.get("empiId");
			vLogService.saveVindicateLog(CDH_DisabilityMonitor, op, phrId, dao,
					empiId);
			Map<String, Object> record = dao.doLoad(CDH_HealthCard, phrId);
			if (record != null && record.size() > 0) {
				String deformity = (String) record.get("deformity");
				if (deformity != null && deformity.equals(disabilityType)) {
					return;
				}
				Map<String, Object> rebody = (Map<String, Object>) res
						.get("body");
				rebody.put("updateHealthCard", true);
				String hql = new StringBuffer("update ").append(CDH_HealthCard)
						.append(" set deformity =:deformity ")
						.append("where phrId =:phrId ").toString();
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("deformity", disabilityType);
				parameters.put("phrId", phrId);
				dao.doUpdate(hql, parameters);
				phm.deletePastHistory(empiId, PastHistoryCode.DEFORMITY);
				ArrayList<Map<String, Object>> deformityBody = phm
						.createPastHisBody(empiId, PastHistoryCode.DEFORMITY,
								disabilityType, disabilityType_text, body);
				for (Map<String, Object> data : deformityBody) {
					phm.savePastHistory(data, "create");
				}
			}
		} catch (Exception e) {
			logger.error("Save children disabilityMonitor record error .", e);
			res.put(RES_CODE, ServiceCode.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 删除儿童死亡登记记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doRemoveChildrenDeadRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ChildrenHealthModel chm = new ChildrenHealthModel(dao);
		String deadRegisterId = StringUtils.trimToEmpty((String) req
				.get("pkey"));
		Map<String, Object> bodyMap = (Map<String, Object>) req.get("body");
		String empiId = (String) bodyMap.get("empiId");
		try {
			chm.removeChildrenDeadRecord(deadRegisterId);
		} catch (ModelDataOperationException e) {
			logger.error("Remove children dead record error .", e);
			throw new ServiceException(e);
		}
		vLogService.saveVindicateLog(CDH_DeadRegister, "4", deadRegisterId,
				dao, empiId);
		// res.put(RES_CODE, ServiceCode.CODE_OK);
		// res.put(RES_MESSAGE, "记录删除成功");

	}

	/**
	 * 检测儿童死亡登记信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doCheckDeadRecordExistsByIdCard(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ChildrenHealthModel chm = new ChildrenHealthModel(dao);
		Map<String, Object> reqBody = (HashMap<String, Object>) req.get("body");
		String idCard = (String) reqBody.get("idCard");
		boolean isExists = false;
		try {
			isExists = chm.isDeadRecordExistsByIdCard(idCard);
		} catch (ModelDataOperationException e) {
			logger.error("Check children dead  record error .", e);
			throw new ServiceException(e);
		}
		Map<String, Object> resBody = new HashMap<String, Object>();
		resBody.put("recordExists", isExists);
		res.put("body", resBody);
	}

	/**
	 * 检测儿童死亡登记信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doCheckDeadRecordExistsByEmpiId(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ChildrenHealthModel chm = new ChildrenHealthModel(dao);
		Map<String, Object> reqBody = (HashMap<String, Object>) req.get("body");
		String empiId = (String) reqBody.get("empiId");
		Map<String, Object> resBody;
		try {
			resBody = chm.isDeadRecordExistsByEmpiId(empiId);
			res.put("body", resBody);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get child dead record .", e);
			throw new ServiceException(e);
		}

	}

	/**
	 * 初始化儿童意外情况信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doInitChildAccident(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ChildrenHealthModel chm = new ChildrenHealthModel(dao);
		Map<String, Object> reqBody = (HashMap<String, Object>) req.get("body");
		Map<String, Object> resBody = new HashMap<String, Object>();
		String phrId = (String) reqBody.get("phrId");
		Map<String, Object> childHealthCard;
		try {
			childHealthCard = chm.getChildHealthCardByPhrId(phrId);
			resBody.putAll(childHealthCard);
		} catch (ModelDataOperationException e) {
			logger.error("Init children Accident information failed .", e);
			throw new ServiceException(e);
		}
		res.put("body", SchemaUtil.setDictionaryMessageForForm(childHealthCard,
				CDH_HealthCard));
	}

	/**
	 * 儿童意外情况保存
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveAccidentRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String op = (String) req.get("op");
		Map<String, Object> data = (HashMap<String, Object>) req.get("body");
		String accidentId = (String) data.get("accidentId");
		ChildrenHealthModel chm = new ChildrenHealthModel(dao);
		try {
			chm.saveChildAccidentRecord(data, op, res);
		} catch (ModelDataOperationException e) {
			logger.error("Save children accident record error .", e);
			throw new ServiceException(e);
		}
		if ("create".equals(op)) {
			Map<String, Object> resBody = (Map<String, Object>) res.get("body");
			accidentId = (String) resBody.get("accidentId");
		}
		vLogService.saveVindicateLog(CDH_Accident, op, accidentId, dao);
	}

	/**
	 * 儿童缺陷监测记录删除
	 * 
	 * @param req
	 * @param res
	 * @param sc
	 * @param ctx
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	protected void doDeleteDefectChildrenRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws Exception {
		String pkey = StringUtils.trimToEmpty((String) req.get("pkey"));
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String empiId = (String) body.get("empiId");
		try {
			ChildrenHealthModel chm = new ChildrenHealthModel(dao);
			chm.deleteDefectChildrenRecord(pkey);
		} catch (ModelDataOperationException e) {
			logger.error("Delete children defect record error .", e);
			throw new ServiceException(e);
		}
		vLogService
				.saveVindicateLog(CDH_DefectRegister, "4", pkey, dao, empiId);
	}

	/**
	 * 儿童缺陷监测记录保存
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveDefectChildrenRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String op = (String) req.get("op");
		Map<String, Object> data = (HashMap<String, Object>) req.get("body");
		String phrId = (String) data.get("phrId");
		String empiId = (String) data.get("empiId");
		ChildrenHealthModel chm = new ChildrenHealthModel(dao);
		try {
			chm.saveDefectChildrenRecord(data, op, res);
		} catch (ModelDataOperationException e) {
			logger.error("Save children defect record error .", e);
			throw new ServiceException(e);
		}
		vLogService
				.saveVindicateLog(CDH_DefectRegister, op, phrId, dao, empiId);
	}

	/**
	 * 注销儿童档案
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doLogOutChildrenRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> data = (HashMap<String, Object>) req.get("body");
		String phrId = (String) data.get("phrId");
		String empiId = (String) data.get("empiId");
		if (phrId == null || phrId.equals("")) {
			return;
		}
		if (empiId == null || empiId.equals("")) {
			return;
		}
		String deadReason = StringUtils.trimToEmpty((String) data
				.get("deadReason"));
		String cancellationReason = StringUtils.trimToEmpty((String) data
				.get("cancellationReason"));
		ChildrenHealthModel chm = new ChildrenHealthModel(dao);
		VisitPlanModel vpm = new VisitPlanModel(dao);
		DebilityChildrenModel dcm = new DebilityChildrenModel(dao);
		try {
			chm.logOutHealthCardRecord("phrId", phrId, cancellationReason,
					deadReason);
			vpm.logOutVisitPlan(vpm.RECORDID, phrId, BusinessType.CD_CU);
			vpm.logOutVisitPlan(vpm.RECORDID, phrId, BusinessType.CD_IQ);
			dcm.logOutDebilityChildrenRecord("empiId", empiId,
					cancellationReason, deadReason);
			vpm.logOutVisitPlan(vpm.EMPIID, empiId, BusinessType.CD_DC);
		} catch (ModelDataOperationException e) {
			logger.error("logout children  record error .", e);
			throw new ServiceException(e);
		}
		vLogService.saveVindicateLog(CDH_HealthCard, "3", phrId, dao, empiId);
		vLogService.saveRecords("ER", "logout", dao, empiId);
	}

	/**
	 * 获取儿童死亡监测的操作权限
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doCheckDeadControl(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		String empiId = (String) body.get("empiId");
		if (empiId == null || "".equals(empiId)) {
			return;
		}
		try {
			ChildrenHealthModel chm = new ChildrenHealthModel(dao);
			Map<String, Object> record = chm.getChildHealthCardByEmpiId(empiId);
			Map<String, Object> resBody = new HashMap<String, Object>();
			if (record != null && record.size() > 0) {
				Map<String, Boolean> data = ControlRunner
						.run(CDH_DeadRegister, record, ctx,
								ControlRunner.CREATE, ControlRunner.UPDATE);
				resBody.put("_actions", data);
			}
			res.put("body", resBody);
		} catch (Exception e) {
			logger.error("check child dead record control error .", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 检查死亡编号是否重复
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doCheckDeadNo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		String deadNo = StringUtils.trimToEmpty((String) body.get("deadNo"));
		String empiId = StringUtils.trimToEmpty((String) body.get("empiId"));
		boolean isRepeat;
		try {
			ChildrenHealthModel chm = new ChildrenHealthModel(dao);
			isRepeat = chm.checkDeadNo(empiId, deadNo);
		} catch (ModelDataOperationException e) {
			logger.error("check  child deadNo  if exisit failed.", e);
			throw new ServiceException(e);
		}
		Map<String, Object> rebody = new HashMap<String, Object>();
		rebody.put("isRepeat", isRepeat);
		res.put("body", rebody);
	}

	/**
	 * 修改儿童出生缺陷记录
	 * 
	 * @param childRecord
	 * @param dao
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	private void updateChildDefect(Map<String, Object> childRecord, BaseDAO dao)
			throws ModelDataOperationException, ValidateException {
		String phrId = (String) childRecord.get("phrId");
		ChildrenHealthModel chm = new ChildrenHealthModel(dao);
		Map<String, Object> defect = chm.getChildDefectRecord(phrId);
		if (defect == null || defect.size() < 1) {
			return;
		}
		String motherEmpi = (String) defect.get("motherEmpiId");
		if (motherEmpi == null) {
			motherEmpi = "";
		}
		String childMother = (String) childRecord.get("motherEmpiId");
		if (childMother == null) {
			childMother = "";
		}
		if (motherEmpi == childMother || motherEmpi.equals(childMother)) {
			return;
		}
		Map<String, Object> motherMess = chm.getChildMotherMess(childMother);
		defect.putAll(motherMess);
		chm.saveDefectChildrenRecord(defect, "update");
	}

	/**
	 * 生成随访计划
	 * 
	 * @param body
	 * @param businessType
	 *            计划类型
	 * @param ctx
	 * @throws ServiceException
	 */
	private void createVisitPlan(Map<String, Object> body, String businessType,
			Context ctx) throws ServiceException {
		Map<String, Object> planReq = new HashMap<String, Object>();
		planReq.put("recordId", body.get("phrId"));
		planReq.put("businessType", businessType);
		planReq.put("empiId", body.get("empiId"));
		planReq.put("birthday", body.get("birthday"));
		try {
			getVisitPlanCreator().create(planReq, ctx);
		} catch (Exception e) {
			logger.error("create visit plan failed .", e);
			throw new ServiceException("生成计划失败!", e);
		}
	}

	/**
	 * 
	 * @Description:儿童档案结案
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2013-5-22 下午12:45:20
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	protected void doEndManageCHR(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String empiId = (String) body.get("empiId");
		String pkey = (String) req.get("pkey");
		ChildrenHealthModel chModel = new ChildrenHealthModel(dao);
		int upr = 0;
		try {
			upr = chModel.endManageCHR(pkey);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to end manager children health record.", e);
			throw new ServiceException(e);
		}
		vLogService.saveVindicateLog(CDH_HealthCard, "7", pkey, dao, empiId);
		boolean success = false;
		if (upr > 0) {
			success = true;
		}
		res.put("success", success);
	}

	/**
	 * 
	 * @Description:是否建立儿童档案
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	protected void doIsCreateHealthCard(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");

		ChildrenHealthModel chModel = new ChildrenHealthModel(dao);
		boolean flag = false;
		try {
			flag = chModel.isCreateHealthCard(body);
			if (flag) {
				res.put("success", flag);
			}
		} catch (ModelDataOperationException e) {
			logger.error("Failed to end manager healthCard record.", e);
			throw new ServiceException(e);
		}

	}

	/**
	 * 
	 * @Description:判断是否第一次建立新生儿随访基本信息,如果是，则返回随访基本西信息的数据，否就返回儿童健康档案的相关信息
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author
	 * @throws PersistentDataOperationException
	 * @throws ModelDataOperationException
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	protected void doIsCreatrFirstVisitRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, PersistentDataOperationException,
			ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ChildrenHealthModel chModel = new ChildrenHealthModel(dao);
		Map<String, Object> m = new HashMap<String, Object>();
		Map<String, Object> data = null;
		m.put("empiId", body.get("empiId"));
		data = chModel.getChildrenVisitInfo(m);
		if (data != null && data.size() > 0) {
			String apgar1 = (String) data.get("apgar1");
			String apgar5 = (String) data.get("apgar5");
			// 如果长度>1,说明是类似于12 23 要转化成 1,2 2,3
			String s = (String) data.get("birthStatus");
			if (s != null && s.length() > 0) {
				if (s.length() > 1) {
					StringBuffer ss = new StringBuffer();
					ss.append(s.substring(0, 1) + ",").append(s.substring(1));
					data.put("birthStatus", ss);
				}
			}
			s = null;// 清除引用，给jvm回收，因为是临时变量
			body.put("healthData", data);
			res.putAll(body);
			return;
		}

		// 获取healthCard表的信息
		data = chModel.getHealthCard(m);
		String fatherEmpiId = null;
		String motherEmpiId = null;
		if (data != null && data.size() > 0) {// 获取母亲和父亲的信息
			fatherEmpiId = (String) data.get("fatherEmpiId");
			motherEmpiId = (String) data.get("motherEmpiId");
			List<String> parametersFatherInfo = new ArrayList<String>();
			parametersFatherInfo.add("workCode");
			parametersFatherInfo.add("mobileNumber");
			parametersFatherInfo.add("personName");
			parametersFatherInfo.add("birthday");
			if (fatherEmpiId != null && fatherEmpiId.length() > 0) {

				Map<String, Object> mFatherEmpiId = new HashMap<String, Object>();

				mFatherEmpiId.put("empiId", fatherEmpiId);
				Map<String, Object> resultDataF = chModel
						.getValuesByParameters("MPI_DemographicInfo",
								parametersFatherInfo, mFatherEmpiId);
				if (resultDataF != null && resultDataF.size() > 0) {
					data.put("fatherJob", resultDataF.get("workCode"));
					data.put("fatherPhone", resultDataF.get("mobileNumber"));
					data.put("fatherName", resultDataF.get("personName"));
					data.put("fatherBirth", resultDataF.get("birthday"));
				}
			}
			if (motherEmpiId != null && motherEmpiId.length() > 0) {
				// 母亲的信息
				Map<String, Object> mMotherEmpiId = new HashMap<String, Object>();
				mMotherEmpiId.put("empiId", motherEmpiId);
				Map<String, Object> resultDataM = chModel
						.getValuesByParameters("MPI_DemographicInfo",
								parametersFatherInfo, mMotherEmpiId);
				if (resultDataM != null && resultDataM.size() > 0) {
					data.put("motherJob", resultDataM.get("workCode"));
					data.put("motherPhone", resultDataM.get("mobileNumber"));
					data.put("motherName", resultDataM.get("personName"));
					data.put("motherBirth", resultDataM.get("birthday"));

				}
			}
		}
		Map<String, Object> changeToText = new HashMap<String, Object>();
		changeToText.put("motherJob", data.get("motherJob"));
		changeToText.put("fatherJob", data.get("fatherJob"));
		data.putAll(SchemaUtil.setDictionaryMessageForForm(
				changeToText,CDH_ChildVisitInfo));
		
		body.put("healthData", data);

		res.putAll(body);

	}

	/**
	 * 
	 * @Description:保存或者更新新生儿随访的数据
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author
	 * @throws PersistentDataOperationException
	 * @throws ModelDataOperationException
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveNewChildrenVistRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, PersistentDataOperationException,
			ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ChildrenHealthModel chModel = new ChildrenHealthModel(dao);
		Map<String, Object> visitInfo = (Map<String, Object>) body.get("jbxx");
		Map<String, Object> visitRecord = (Map<String, Object>) body
				.get("visit");
		String babyId = null;
		String visitId = null;
		try {
			// 保存或者更新访视的基本信息
			String s = visitInfo.get("birthStatus").toString();
			Pattern p = Pattern.compile("[^0-9]");
			Matcher m = p.matcher(s);
			visitInfo.put("birthStatus", m.replaceAll("").trim());
			Map<String, Object> dataInfo = chModel
					.saveNewChildrenVistInfo(visitInfo);
			if (dataInfo != null && !dataInfo.isEmpty()) {
				babyId = (String) dataInfo.get("babyId");
			} else {
				return;
			}
			visitInfo.put("birthStatus", s);
			visitInfo.put("babyId", babyId);

			// 保存或者更新访视的记录
			Map<String, Object> dataRecord = chModel
					.saveNewChildrenVistRecord(visitRecord);
			if (dataRecord != null && !dataRecord.isEmpty()) {
				visitId = (String) dataRecord.get("visitId");
				visitRecord.put("visitId", visitId);
			} else {
				return;
			}
			// 更新随访计划表信息
			visitInfo.putAll(visitRecord);
			res.put("body", visitInfo);
		} catch (ModelDataOperationException e) {
			logger.error("保存或者更新新生儿家庭随访信息失败!", e);
			throw new ServiceException(e);
		}

	}

	/**
	 * 
	 * @Description:保存新生儿随访记录的信息
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author
	 * @throws PersistentDataOperationException
	 * @throws ModelDataOperationException
	 * @Modify:
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	protected void doSaveChildVistRecordAndInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, PersistentDataOperationException,
			ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		Map<String, Object> info = (Map<String, Object>) body.get("jbxx");// 基本信息
		Map<String, Object> fsjl = (Map<String, Object>) body.get("fsjl");// 访视记录
		ChildrenHealthModel chModel = new ChildrenHealthModel(dao);
		Map<String, Object> infoData = null;
		Map<String, Object> fsjlData = null;
		String visitId = (String) fsjl.get("visitId");
		String babyId = (String) info.get("babyId");
		String empiId = null;
		String visitDate = null;
		String birthStatus_s = info.get("birthStatus").toString();// 将list类型转换string类型
		String birthStatus_ss = birthStatus_s.substring(
				birthStatus_s.indexOf("[") + 1, birthStatus_s.indexOf("]"))
				.replace(" ", "");
		info.put("birthStatus", birthStatus_ss);
		// 保存新生儿访视基本信息
		if (babyId != null && babyId.length() > 0) {
			info.put("babyId", babyId);
			dao.doSave("update", CDH_ChildVisitInfo, info, false);
		} else {
			// // empiId 判断是否已经建立基本信息
			// Map<String, Object> boj = chModel.getBabyIdByEmpiId(empiId);
			// if (boj != null && boj.size() > 0) {
			// info.put("babyId", boj.get("babyId"));
			// infoData = dao
			// .doSave("update", CDH_ChildVisitInfo, info, false);
			// babyId = (String) boj.get("babyId");
			// } else {

			infoData = dao.doSave("create", CDH_ChildVisitInfo, info, false);
			if (infoData != null && infoData.size() > 0) {
				babyId = (String) infoData.get("babyId");
			}
			// }

		}
		fsjl.put("babyId", babyId);
		//标识 签约任务完成
		this.finishSCServiceTask(empiId, ETJD_ETFW, null, dao);
		// 保存新生儿访视记录
		if (visitId != null && visitId.length() > 0) {
			fsjl.put("visitId", visitId);
		}
		if (visitId != null && visitId.length() > 0) {
			fsjl.put("visitId", visitId);
			dao.doSave("update", CDH_ChildVisitRecord, fsjl, false);
		} else {
			// 判断是否有记录 根据 babyId和随访时间获取主键，如果有就更新，否则就创建
			visitDate = (String) fsjl.get("visitDate");
			Map<String, Object> ect = chModel.getVisitIdByIdAndDate(visitDate,
					babyId);
			if (ect != null && ect.size() > 0) {
				visitId = (String) ect.get("visitId");
				fsjl.put("visitId", visitId);
				fsjlData = dao.doSave("update", CDH_ChildVisitRecord, fsjl,
						false);
			} else {
				fsjlData = dao.doSave("create", CDH_ChildVisitRecord, fsjl,
						false);
				if (fsjlData != null && fsjlData.size() > 0) {
					visitId = (String) fsjlData.get("visitId");
				}
			}

		}
		info = SchemaUtil.setDictionaryMessageForForm(info, CDH_ChildVisitInfo);
		fsjl = SchemaUtil.setDictionaryMessageForForm(fsjl,
				CDH_ChildVisitRecord);
		//
		body.put("visitId", visitId);
		body.put("babyId", babyId);
		body.put("jbxx", info);
		body.put("fsjl", fsjl);
		res.put("body", body);

	}

	/**
	 * 
	 * @Description:获取新生儿的 出生证、 身份证 、母亲身份证
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	protected void doGetInfoNumber(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ChildrenHealthModel chModel = new ChildrenHealthModel(dao);
		try {
			Map<String, Object> map = chModel.getInfoNumber(body);
			if (map != null && map.size() > 0) {
				res.put("body", map);

			}
		} catch (ModelDataOperationException e) {

			throw new ServiceException(e);
		}

	}//

	/**
	 * 
	 * @Description:获取新生儿的 信息
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	protected void doGetFullRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ChildrenHealthModel chModel = new ChildrenHealthModel(dao);
		// String visitId = (String) body.get("visitId");
		try {

			Map<String, Object> map = chModel.getGetFullRecord(body);
			Map<String, Object> manaUnitId = new HashMap<String, Object>();
			manaUnitId.put("visitDoctor", (String) map.get("visitDoctor"));
			map.putAll(SchemaUtil.setDictionaryMessageForForm(manaUnitId,
					CDH_ChildVisitRecord));

			if (map != null && map.size() > 0) {
				res.put("body", map);

			}
		} catch (ModelDataOperationException e) {

			throw new ServiceException(e);
		}

	}

	//
	/**
	 * 
	 * @Description:获取新生儿的 出生证、 身份证 、母亲身份证
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	protected void doFindInfo(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		// ChildrenHealthModel chModel = new ChildrenHealthModel(dao);
		Map<String, Object> map = new HashMap<String, Object>();
		map.putAll(SchemaUtil.setDictionaryMessageForForm(body,
				CDH_ChildVisitRecord));
		res.put("body", map);

	}
}
