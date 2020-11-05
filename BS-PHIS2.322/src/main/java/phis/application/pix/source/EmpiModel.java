/**
 * @()EMPIUtil.java Created on Dec 4, 2009 9:13:52 AM
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.application.pix.source;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.pub.source.PublicModel;
import phis.source.BSPHISEntryNames;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.Constants;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.BSHISUtil;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.CNDHelper;
import phis.source.utils.ParameterUtil;

import com.alibaba.fastjson.JSONException;

import ctd.account.UserRoleToken;
import ctd.account.organ.ManageUnit;
import ctd.controller.exception.ControllerException;
import ctd.net.rpc.util.ServiceAdapter;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

/**
 * @description 个人基本信息相关的数据库操作
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class EmpiModel implements BSPHISEntryNames {

	protected BaseDAO dao;

	protected Logger logger = LoggerFactory.getLogger(EmpiModel.class);

	public EmpiModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 根据姓名+性別+出生日期获取基础信息
	 * 
	 * @param idCard
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getEmpiInfoByBase(String personName,
			String sexCode, String birthday) throws ModelDataOperationException {
		if (birthday.indexOf("T") > 0) {
			birthday = birthday.substring(0, birthday.indexOf("T"));
		}
		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "personName", "s",
				personName);
		if (personName == "") {
			try {
				cnd1 = CNDHelper.toListCnd("['isNull', ['$', 'personName']]");
			} catch (Exception e) {
				throw new ModelDataOperationException("错误的查询条件.", e);
			}
		}

		List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "sexCode", "s", sexCode);
		List<?> cnd3 = CNDHelper.createSimpleCnd("eq", "birthday", "$",
				"date('" + birthday + "')");
		List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		cnd = CNDHelper.createArrayCnd("and", cnd, cnd3);
		List<Map<String, Object>> info;
		try {
			info = dao.doList(cnd, null, MPI_DemographicInfo);
		} catch (PersistentDataOperationException e) {
			logger.error("failed to get child base message.", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取基本信息失败。");
		}
		return info;
	}

	/**
	 * 根据姓名+性別+出生日期获取基础信息
	 * 
	 * @param idCard
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getEmpiInfoByBase(String personName,
			String birthday) throws ModelDataOperationException {
		if (birthday.indexOf("T") > 0) {
			birthday = birthday.substring(0, birthday.indexOf("T"));
		}
		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "personName", "s",
				personName);
		if (personName == "") {
			try {
				cnd1 = CNDHelper.toListCnd("['isNull',['$', 'personName']]");
			} catch (Exception e) {
				throw new ModelDataOperationException("错误的查询条件.", e);
			}
		}

		List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "birthday", "$",
				"date('" + birthday + "')");
		List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		List<Map<String, Object>> info;
		try {
			info = dao.doList(cnd, null, MPI_DemographicInfo);
		} catch (PersistentDataOperationException e) {
			logger.error("failed to get child base message.", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取基本信息失败。");
		}
		return info;
	}

	/**
	 * 根据卡号，卡类型查询个人信息
	 * 
	 * @param cardNo
	 * @param cardTypeCode
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getEmpiInfoByCardNoAndCardTypeCode(
			String cardNo, String cardTypeCode)
			throws ModelDataOperationException {
		List<Map<String, Object>> info;

		try {
			String hql = buildDemoInfoHql(cardNo, cardTypeCode);
			info = dao.doQuery(hql, null);
		} catch (PersistentDataOperationException e) {
			logger.error("failed to get child base message.", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取基本信息失败。");
		} catch (ControllerException e) {
			logger.error("failed to get child base message.", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取基本信息失败。");
		}
		return info;
	}

	private String buildDemoInfoHql(String cardNo, String cardTypeCode)
			throws ControllerException {
		StringBuffer sb = new StringBuffer("select ");
		Schema sc = SchemaController.instance().get(MPI_DemographicInfo);
		List<SchemaItem> items = sc.getItems();
		for (int i = 0; i < items.size(); i++) {
			SchemaItem si = items.get(i);
			if (si.isVirtual() || si.isRef()) {
				continue;
			}
			sb.append("a.").append(si.getId()).append(" as ")
					.append(si.getId()).append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(" from ").append(MPI_DemographicInfo).append(" a,");
		sb.append(MPI_Card)
				.append(" b where a.empiId =b.empiId and b.cardNo ='")
				.append(cardNo).append("' and cardTypeCode = '")
				.append(cardTypeCode).append("'");
		return sb.toString();
	}

	/**
	 * 根据卡号获取基本信息
	 * 
	 * @param cardNo
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getEmpiInfoByIdcard(String idCard)
			throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "idCard", "s", idCard);
		List<Map<String, Object>> info;
		try {
			info = dao.doList(cnd, null, MPI_DemographicInfo);
		} catch (PersistentDataOperationException e) {
			logger.error("failed to get child base message.", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取基本信息失败。");
		}
		return info;
	}

	/**
	 * 根据empiid获取个人信息
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getEmpiInfoByEmpiid(String empiId)
			throws ModelDataOperationException {
		try {
			Map<String, Object> empiData = dao.doLoad(MPI_DemographicInfo,
					empiId);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("empiId", empiId);
			Map<String, Object> empiInfo = new HashMap<String, Object>();
			empiInfo = dao
					.doLoad("select BRXZ as BRXZ,MZHM as MZHM,BRID as BRID from MS_BRDA where empiId=:empiId",
							params);
			if (empiInfo != null && !empiInfo.isEmpty()) {
				empiData.putAll(empiInfo);
			}
			return empiData;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("获取个人信息失败,", e);
		}
	}

	/**
	 * 查询个人信息中的单独字段。
	 * 
	 * @param empiId
	 * @param fieldName
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Object getSingleEmpiField(String empiId, String fieldName)
			throws ModelDataOperationException {
		Map<String, Object> empiInfo = getEmpiInfoByEmpiid(empiId);
		return empiInfo.get(fieldName);
	}

	/**
	 * 判断生日是否发生了变化
	 * 
	 * @param empiId
	 * @param newBirthday
	 * @return
	 * @throws ModelDataOperationException
	 */
	public boolean ifBirthdayChanged(String empiId, String newBirthdayStr)
			throws ModelDataOperationException {
		Date oldBirthday = (Date) getSingleEmpiField(empiId, "birthday");
		Date newBirthday = BSHISUtil.toDate(newBirthdayStr);
		int result = BSHISUtil.dateCompare(newBirthday, oldBirthday);
		if (result == 0)
			return false;
		return true;
	}

	/**
	 * @param jsonReq
	 * @param jsonRes
	 * @param sc
	 * @param ctx
	 * @throws PersistentDataOperationException
	 * @throws JSONException
	 */
	public List<Map<String, Object>> getRelative(String idCard)
			throws ModelDataOperationException {
		String hql = new StringBuffer(
				"select centerCode as centerCode, xm as xm, xb as xb,csrq as csrq,")
				.append(" sfzh as sfzh, hh as hh, yhzgx as yhzgx from ")
				.append(GA_Info).append(" a where hh=(select hh from ")
				.append(GA_Info)
				.append(" b where b.sfzh=:sfzh) and a.sfzh<>:sfzh ").toString();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sfzh", idCard);
		try {
			return dao.doQuery(hql, map);
		} catch (PersistentDataOperationException e) {
			logger.error("query ga_info list failed.", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询直系亲属信息失败.");
		}
	}

	/**
	 * @param idCard
	 * @param session
	 * @return
	 * @throws PersistentDataOperationException
	 */
	public Map<String, Object> queryPersonFromGAByIdcard(String idCard)
			throws ModelDataOperationException {
		String hql = new StringBuffer(
				"select xm as name, xxdz as address, xb as sex,")
				.append("mz as nation, csrq as birthday, whcd as education,")
				.append(" hyzk as marital, centerCode as centerCode from ")
				.append(ZX_SMK_GA).append(" where sfzh = :sfzh").toString();

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("sfzh", idCard);
		try {
			List<Map<String, Object>> list = dao.doQuery(hql, parameters);
			if (list.size() == 0)
				return null;
			return list.get(0);
		} catch (PersistentDataOperationException e) {
			logger.error("query failed from zx_smk_ga.", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "个人信息查询失败.");
		}
	}

	/**
	 * 根据市民卡查询基本信息，需要先到市民卡表中查询出中心内码 再由该内码到公安信息表中查询人口学信息。
	 * 
	 * @param CitizenCard
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryPersonFromGAByCenterCode(String centerCode)
			throws ModelDataOperationException {
		String hql = new StringBuffer(
				"select xm as name, xxdz as address, xb as sex, mz as nation,")
				.append("csrq as birthday, whcd as education,")
				.append(" hyzk as marital, sfzh as idCard from ")
				.append(ZX_SMK_GA).append(" where centerCode = :centerCode")
				.toString();

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("centerCode", centerCode);
		try {
			List<Map<String, Object>> list = dao.doQuery(hql, parameters);
			if (list.size() == 0)
				return null;
			return list.get(0);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "个人信息查询失败。");
		}
	}

	/**
	 * 
	 * @param centerCode
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryIdCardFromGAByCenterCode(
			String centerCode) throws ModelDataOperationException {
		String hql = new StringBuffer(
				"select cardNumber as cardNumber, sendDate as sendDate, validity as validity from ")
				.append(ZX_CARD_BASEINFO)
				.append(" where centerCode = :centerCode").toString();

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("centerCode", centerCode);

		try {
			return dao.doQuery(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询卡信息失败。");
		}
	}

	/**
	 * @param citizenCard
	 * @param session
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> queryIdCardFromGAByCardNo(String citizenCard)
			throws ModelDataOperationException {
		String hql = new StringBuffer(
				"select cardNumber as cardNumber, sendDate as sendDate, ")
				.append("validity as validity, centerCode as centerCode from ")
				.append(ZX_CARD_BASEINFO)
				.append(" where cardNumber = :cardNumber").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("cardNumber", citizenCard);
		List<?> lst;
		try {
			lst = dao.doQuery(hql, parameters);
			if (lst.size() == 0)
				return null;
			return (Map<String, Object>) lst.get(0);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询卡信息失败。");
		}
	}

	/**
	 * 将传入的个人信息保存到EMPI并返回empiId，如果已保存过将直接返回empiId。
	 * 
	 * @param personInfo
	 * @param session
	 * @param isChild
	 *            是否为儿童基本信息
	 * @param empiInterfaceImpi
	 * @return
	 * @throws ModelDataOperationException
	 * @throws ServiceException
	 * @throws PersistentDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public String saveEmpiInfo(Map<String, Object> personInfo,
			EmpiInterfaceImpi empiInterfaceImpi, boolean isChild, Context ctx)
			throws ModelDataOperationException, ServiceException {
		// @@ 检查是否已存在。
		String empiId = (String) personInfo.get("empiId");
		if (empiId != null && isRecordExists(MPI_DemographicInfo, empiId, true)) {
			try {
				Map<String, Object> PIXData = EmpiUtil
						.changeToPIXFormat(personInfo);
				personInfo = EmpiUtil.updatePerson(dao, ctx, personInfo,
						PIXData);
				if (personInfo.get("idCard") != null
						&& ((String) personInfo.get("idCard")).length() > 0) {
					List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "empiId",
							"s", empiId);
					List<?> cnd2 = CNDHelper.createSimpleCnd("eq",
							"certificateTypeCode", "s", "01");
					List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
					List<Map<String, Object>> list = dao.doList(cnd, null,
							MPI_Certificate);
					if (list.size() > 0) {
						Map<String, Object> cardInfo = list.get(0);
						cardInfo.put("certificateNo", personInfo.get("idCard"));
						dao.doSave("update", MPI_Certificate, cardInfo, false);
					} else {
						Map<String, Object> cardInfo = new HashMap<String, Object>();
						cardInfo.put("empiId", empiId);
						cardInfo.put("certificateTypeCode", "01");
						cardInfo.put("certificateNo", personInfo.get("idCard"));
						dao.doInsert(MPI_Certificate, cardInfo, false);
					}
				}
				return empiId;
			} catch (PersistentDataOperationException e) {
				logger.error("update empi message failed.", e);
				throw new ModelDataOperationException(
						Constants.CODE_UNKNOWN_ERROR, "更新基本信息失败。");
			}
		}
		// @@ 如果没有注册过，新建一个。
		Map<String, Object> PIXData = EmpiUtil.changeToPIXFormat(personInfo);
		personInfo = EmpiUtil.submitPerson(dao, ctx, personInfo, PIXData);
		Map<String, Object> personMap = (Map<String, Object>) ((HashMap<String, Object>) personInfo)
				.clone();
		// personMap.put("empiId", empiId);
		if (personMap.get("birthday") != null) {
			Date d = BSHISUtil.toDate(((String) personMap.get("birthday"))
					.substring(0, 10));
			personMap.put("birthday", d);
		}
		personMap.put("status", String
				.valueOf(com.bsoft.pix.data.DataAccessor.PERSON_STATUS_NORMAL));
		personMap.put("createTime", new Date());
		personMap.put("createUser", personInfo.get("createUser"));
		personMap.put("createUnit", personInfo.get("createUnit"));
		boolean validate = true;
		// ** 如果为儿童基本信息保存，则不需要后台验证
		validate = isChild == true ? false : true;
		try {
			dao.doInsert(MPI_DemographicInfo, personMap, validate);
		} catch (PersistentDataOperationException e) {
			logger.error("save empi message failed.", e);
			throw new ModelDataOperationException(Constants.CODE_UNKNOWN_ERROR,
					"保存基本信息失败。");
		}
		if (personMap.get("idCard") != null) {
			Map<String, Object> cardMap = new HashMap<String, Object>();
			cardMap.put("certificateTypeCode",
					com.bsoft.pix.Constants.ID_CARD_DIC_CODE);
			cardMap.put("certificateNo", (String) personMap.get("idCard"));
			cardMap.put("empiId", empiId);
			String id = com.bsoft.pix.dao.IdMaker.makeId();
			cardMap.put("certificateId", id);
			try {
				dao.doInsert(MPI_Certificate, cardMap, validate);
			} catch (PersistentDataOperationException e) {
				logger.error("save idcard message failed.", e);
				throw new ModelDataOperationException(
						Constants.CODE_UNKNOWN_ERROR, "保存基本信息失败。");
			}
		}
		return empiId;
	}

	/**
	 * 检查一个表里的对应一个empiId的记录是否已存在。
	 * 
	 * @param table
	 *            表名
	 * @param empiId
	 * @param session
	 * @return 如果存在返回true，否则返回false。
	 * @throws ModelDataOperationException
	 */
	public boolean isRecordExists(String table, String empiId,
			boolean checkStatus) throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer("select count(*) as num from ")
				.append(table).append(" where empiId = :empiId");
		if (checkStatus) {
			hql.append(" and status != :status");
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		if (checkStatus) {
			parameters.put("status",
					String.valueOf(Constants.CODE_STATUS_END_MANAGE));
		}
		Long c;
		Map<String, Object> record;
		try {
			record = dao.doLoad(hql.toString(), parameters);
			c = (Long) record.get("num");
		} catch (PersistentDataOperationException e) {
			logger.error("check table {} message failed.", new Object[] {
					table, e });
			throw new ModelDataOperationException(Constants.CODE_UNKNOWN_ERROR,
					"检查信息出错.");
		}
		if (c == 0) {
			return false;
		}
		return true;
	}

	/**
	 * 查询个人基本信息的性别和出生日期
	 * 
	 * @author ChenXianRui
	 * @param empiId
	 * @return 第一个元素是性别代码（sexCode）， 第二个元素是出生日期（birthday）。 如果对应的个人信息不存在返回null。
	 * @throws PersistentDataOperationException
	 */
	public Map<String, Object> getSexAndBirthday(String empiId)
			throws ModelDataOperationException {
		String hql = new StringBuffer(
				"select sexCode as sexCode, birthday as birthday from ")
				.append(MPI_DemographicInfo).append(" where empiId = :empiId")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);

		Map<String, Object> obj = null;
		try {
			obj = dao.doLoad(hql, parameters);
		} catch (PersistentDataOperationException e) {
			logger.error(
					"select sex and birthday at MPI_DemographicInfo failed.", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询个人基本信息的性别和年龄失败！");
		}
		if (obj == null) {
			return null;
		}

		return obj;
	}

	/**
	 * 获取个人性别、年龄 <br/>
	 * --原executeGetAgeAndSex方法<br/>
	 * 
	 * @author ChenXianRui
	 * @param empiId
	 * @param occurDate
	 *            发生日期
	 * @return 第一个元素是性别代码（sexCode）<br/>
	 *         第二个元素是出生日期（birthday）,本应移除，可能有他用故没去<br/>
	 *         第三个元素是年龄(age)<br/>
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getAgeAndSex(String empiId, String occurDate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = getSexAndBirthday(empiId);

		if (rsMap == null) {
			throw new ModelDataOperationException(
					Constants.CODE_EMPIID_NOT_EXISTS, "无法在数据库中找到该人基本信息！");
		}

		// 计算周岁
		Date birthday = (Date) rsMap.get("birthday");
		Date calculateDate = null;
		if (occurDate != null && !"".equals(occurDate)) {
			DateFormat f = new SimpleDateFormat(
					Constants.DEFAULT_SHORT_DATE_FORMAT);
			try {
				calculateDate = f.parse(occurDate);
			} catch (ParseException e) {
				logger.error("Date parse error [" + occurDate + "].", e);
				throw new ModelDataOperationException(
						Constants.CODE_INVALID_REQUEST, "无法识别的时间格式："
								+ occurDate);
			}
		}
		int age = BSHISUtil.calculateAge(birthday, calculateDate);

		rsMap.put("age", age);

		return rsMap;
	}

	/**
	 * 根据健康档案编码（phrId）查询个人基本信息的性别和出生日期
	 * 
	 * @author ChenXianRui
	 * @param empiId
	 * @return 第一个元素是性别代码（sexCode）， 第二个元素是出生日期（birthday）。 如果对应的个人信息不存在返回null。
	 * @throws PersistentDataOperationException
	 */
	public Map<String, Object> getSexAndBirthdayByPhrId(String phrId)
			throws ModelDataOperationException {
		String hql = new StringBuffer(
				"select sexCode as sexCode, birthday as birthday from ")
				.append(MPI_DemographicInfo).append(" where empiId = ")
				.append("(select empiId from  ").append(EHR_HealthRecord)
				.append("  where phrId =:phrId)").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("phrId", phrId);

		Map<String, Object> obj = null;
		try {
			obj = dao.doLoad(hql, parameters);
		} catch (PersistentDataOperationException e) {
			logger.error(
					"select sex and birthday at MPI_DemographicInfo failed.", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询个人基本信息的性别和年龄失败！");
		}
		if (obj == null) {
			return null;
		}

		return obj;
	}

	/**
	 * 获取个人基本信息的出生日期
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Date getBirthday(String empiId) throws ModelDataOperationException {
		String hql = new StringBuffer("select birthday as birthday from ")
				.append(MPI_DemographicInfo).append(" where empiId = :empiId")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);

		Date birthday = null;
		try {
			Map<String, Object> obj = dao.doLoad(hql, parameters);
			if (obj != null && !obj.isEmpty()) {
				birthday = (Date) obj.get("birthday");
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Failed to get birthday from MPI_DemographicInfo .", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询个人基本信息的出生日期失败！");
		}
		return birthday;
	}

	/**
	 * 获取年龄
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public int getAge(String empiId) throws ModelDataOperationException {
		Date birthday;
		try {
			birthday = getBirthday(empiId);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get age.", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取年龄失败！");
		}
		if (birthday == null) {
			return -1;
		}
		return BSHISUtil.calculateAge(birthday, null);
	}

	/**
	 * 根据empiId获取 姓名
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public String getPersonName(String empiId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("select personName as personName from ")
				.append(MPI_DemographicInfo).append(" where empiId = :empiId")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);

		String personName = null;
		try {
			Map<String, Object> obj = dao.doLoad(hql, parameters);
			if (obj != null && !obj.isEmpty()) {
				personName = (String) obj.get("personName");
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Failed to get personName from MPI_DemographicInfo .",
					e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询个人基本信息的姓名失败！");
		}
		return personName;
	}

	/**
	 * 新增一条卡信息
	 * 
	 * @param data
	 * @param op
	 * @param res
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	public void saveCard(Map<String, Object> data)
			throws ModelDataOperationException, ValidateException {
		try {
			data.put("status", '0');
			// dao.doInsert(MPI_Card, data, false);
			ServiceAdapter.invoke("platform.empiService", "execute", data,
					"card");
		} catch (Exception e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存卡信息失败。", e);
		}
	}

	/**
	 * 检验卡信息是否存在
	 * 
	 * @param cardTypeCode
	 * @param cardNo
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Boolean checkCardExist(String cardTypeCode, String cardNo)
			throws ModelDataOperationException {
		StringBuffer sb = new StringBuffer("select cardId as cardId from ")
				.append(MPI_Card);
		sb.append(" where cardTypeCode = :cardTypeCode and cardNo = :cardNo and  status = :status");
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("cardTypeCode", cardTypeCode);
		param.put("cardNo", cardNo);
		param.put("status", "0");
		try {
			Map<String, Object> card = dao.doLoad(sb.toString(), param);
			if (card != null && card.size() > 0) {
				return true;
			} else {
				return false;
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取卡信息失败。", e);
		}
	}

	/**
	 * 检验证件信息是否存在
	 * 
	 * @param cardTypeCode
	 * @param cardNo
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Boolean checkCertificateExist(String certificateTypeCode,
			String certificateNo) throws ModelDataOperationException {
		StringBuffer sb = new StringBuffer(
				"select certificateId as certificateId from ")
				.append(MPI_Certificate);
		sb.append(" where certificateTypeCode = :certificateTypeCode and certificateNo = :certificateNo");
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("certificateTypeCode", certificateTypeCode);
		param.put("certificateNo", certificateNo);
		try {
			Map<String, Object> card = dao.doLoad(sb.toString(), param);
			if (card != null && card.size() > 0) {
				return true;
			} else {
				return false;
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取证件信息失败。", e);
		}
	}

	public long getIfMZHM(String MZHM) throws ValidateException,
			ModelDataOperationException {
		try {
			// dao.doSave(op, MPI_DemographicInfo, record, validate);
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("MZHM", MZHM);
			return dao.doCount("MS_BRDA", "MZHM=:MZHM", parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("查询门诊号码是否存在失败。", e);
		}
	}

	/**
	 * 保存个人基本信息
	 * 
	 * @param validate
	 * 
	 * @param body
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public void saveEmpiRecord(Map<String, Object> record, String op,
			boolean validate) throws ValidateException,
			ModelDataOperationException {
		try {
			// dao.doSave(op, MPI_DemographicInfo, record, validate);
			record.put("op", op);
			/***************** add by lizhi 20170724 健康卡读卡根据身份证号查询 *******************/
			if ("1000".equals(record.get("BRXZ") + "")
					&& record.get("idCard") != null
					&& !"".equals(record.get("idCard") + "")) {
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("idCard", record.get("idCard") + "");
				long count = dao.doCount("MPI_DemographicInfo",
						"idCard=:idCard", parameters);
				if (count > 0) {
					StringBuffer hql = new StringBuffer(
							"select distinct empiId as empiId from MPI_DemographicInfo where idCard=:idCard");
					List<Map<String, Object>> mpiList = dao.doQuery(
							hql.toString(), parameters);
					if (mpiList.size() > 0) {
						record.put("empiId", mpiList.get(0).get("empiId") + "");
						record.put("op", "update");
					}
				}
			}
			/***************** add by lizhi 20170724 健康卡读卡根据身份证号查询 *******************/
			ServiceAdapter.invoke("platform.empiService", "execute", record,
					"empi");
		} catch (Exception e) {
			throw new ModelDataOperationException("保存个人基本信息失败。", e);
		}
	}

	/**
	 * 保存BRDA
	 * 
	 * @param validate
	 * 
	 * @param body
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public void saveBRDA(Map<String, Object> body, Context ctx)
			throws ValidateException, ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String userId = (String) user.getUserId();
//		ManageUnit m=user.getManageUnit();
		String manaUnitId="";
		try{
			manaUnitId= user.getManageUnit().getId();
		}catch(Exception e){
			manaUnitId="320124";
		}
		
		String empiId = (String) body.get("empiId");
		String OutPatientNumber;
		String mzhm = (String) body.get("MZHM");
		String brxz = body.get("BRXZ") + "";
		String brxm = (String) body.get("personName");
		int brxb = Integer.parseInt(body.get("sexCode") + "");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("EMPIID", empiId);
		Map<String, Object> reBody = new HashMap<String, Object>();
		reBody.put("code", 200);
		reBody.put("msg", Constants.PIX_MSG.get(200));
		try {
			Map<String, Object> patient;
			patient = dao.doLoad(
					"select BRID as BRID from MS_BRDA  where EMPIID=:EMPIID",
					parameters);
			parameters.put("MZHM", mzhm);
			parameters.put("BRXZ", Long.parseLong(brxz));
			parameters.put("ZXBZ", 0);
			parameters.put("BRXM", brxm);
			parameters.put("BRXB", brxb);
			parameters.put("CSNY", body.get("birthday"));
			if ((body.get("idCard") + "").length() > 0) {
				parameters.put("SFZH", body.get("idCard"));
			}
			if ((body.get("nationalityCode") + "").length() > 0) {
				parameters.put("GJDM", body.get("nationalityCode"));
			}
			if ((body.get("nationCode") + "").length() > 0) {
				parameters.put("MZDM", body.get("nationCode"));
			}
			if ((body.get("maritalStatusCode") + "").length() > 0) {
				parameters.put("HYZK", body.get("maritalStatusCode"));
			}
			if ((body.get("workCode") + "").length() > 0) {
				parameters.put("ZYDM", body.get("workCode"));
			}
			if ((body.get("workPlace") + "").length() > 0) {
				parameters.put("DWMC", body.get("workPlace"));
			}
			if ((body.get("phoneNumber") + "").length() > 0) {
				parameters.put("JTDH", body.get("phoneNumber"));
			}
			if ((body.get("contact") + "").length() > 0) {
				parameters.put("LXRM", body.get("contact"));
			}
			if ((body.get("contactPhone") + "").length() > 0) {
				parameters.put("LXDH", body.get("contactPhone"));
			}
			if ((body.get("zipCode") + "").length() > 0) {
				parameters.put("HKYB", body.get("zipCode"));
			}
			if ((body.get("address") + "").length() > 0) {
				parameters.put("LXDZ", body.get("address"));
			}
			if (body.get("bloodTypeCode")!=null && (body.get("bloodTypeCode") + "").length() > 0) {
				parameters.put("XXDM",
						Integer.parseInt(body.get("bloodTypeCode") + ""));
			}
			if (body.containsKey("GRBH")) {
				parameters.put("GRBH", body.get("GRBH"));
			}
			if (body.containsKey("NHKH")) {
				parameters.put("NHKH", body.get("NHKH") + "");
			}// 病人性质更新
			if (body.containsKey("BRXZ")) {
				parameters.put("BRXZ", Long.parseLong(body.get("BRXZ") + ""));
			}
			if (body.containsKey("SHBZKH")) {
				parameters.put("SHBZKH", body.get("SHBZKH") + "");
			}
			if (patient != null) {
				parameters.put("MZHM", mzhm);
				parameters.put("BRID", patient.get("BRID"));
				parameters.put("XGSJ", new Date());
				dao.doSave("update", BSPHISEntryNames.MS_BRDA, parameters,
						false);
			} else {
				if(mzhm.equals(BSPHISUtil.getNotesNumberNotIncrement(
						userId, manaUnitId, 3, dao, ctx)))
					parameters.put("MZHM", mzhm);
				else
					parameters.put("MZHM", doOutPatientNumber(ctx));

				parameters.put("JDJG", manaUnitId);
				parameters.put("JDR", userId);
				parameters.put("JDSJ", new Date());
				parameters.put("XGSJ", new Date());
				dao.doSave("create", BSPHISEntryNames.MS_BRDA, parameters,
						false);
				OutPatientNumber = BSPHISUtil.getNotesNumberNotIncrement(
						userId, manaUnitId, 3, dao, ctx);
				if (mzhm.equals(OutPatientNumber)) {
					BSPHISUtil.getNotesNumber(userId, manaUnitId, 3, dao, ctx);
				}
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 保存地址信息
	 * 
	 * @param record
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public void saveAddress(Map<String, Object> record)
			throws ValidateException, ModelDataOperationException {
		try {
			ServiceAdapter.invoke("platform.empiService", "execute", record,
					"address");
			// dao.doSave("create", MPI_Address, record, false);
		} catch (Exception e) {
			throw new ModelDataOperationException("保存地址信息失败。", e);
		}
	}

	/**
	 * 保存电话信息
	 * 
	 * @param record
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public void savePhone(Map<String, Object> record) throws ValidateException,
			ModelDataOperationException {
		try {
			ServiceAdapter.invoke("platform.empiService", "execute", record,
					"phone");
			// dao.doSave("create", MPI_Phone, record, false);
		} catch (Exception e) {
			throw new ModelDataOperationException("保存电话信息失败。", e);
		}
	}

	/**
	 * 保存证件信息
	 * 
	 * @param cerReq
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public void saveCertificate(Map<String, Object> record, String op)
			throws ValidateException, ModelDataOperationException {
		try {
			// dao.doSave(op, MPI_Certificate, record, false);
			ServiceAdapter.invoke("platform.empiService", "execute", record,
					"certificate");
		} catch (Exception e) {
			throw new ModelDataOperationException("保存证件信息失败。", e);
		}
	}

	/**
	 * 保存其他信息
	 * 
	 * @param extReq
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public void saveExtension(Map<String, Object> record)
			throws ValidateException, ModelDataOperationException {
		try {
			// dao.doSave("create", MPI_Extension, record, false);
			ServiceAdapter.invoke("platform.empiService", "execute", record,
					"extension");
		} catch (Exception e) {
			throw new ModelDataOperationException("保存其他信息失败。", e);
		}
	}

	/**
	 * 根据empiId删除地址信息
	 * 
	 * @param empiId
	 * @throws ModelDataOperationException
	 */
	public void deleteAddressByEmpiId(String empiId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("delete from MPI_Address ").append(
				" where empiId=:empiId").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("根据empiId删除地址信息失败。", e);
		}
	}

	/**
	 * 根据empiId删除电话信息
	 * 
	 * @param empiId
	 * @throws ModelDataOperationException
	 */
	public void deletePhoneByEmpiId(String empiId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("delete from MPI_Phone ").append(
				" where empiId=:empiId").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("根据empiId删除电话信息失败。", e);
		}
	}

	/**
	 * 根据empiId删除证件信息
	 * 
	 * @param empiId
	 * @throws ModelDataOperationException
	 */
	public void deleteCertificateByEmpiId(String empiId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("delete from MPI_Certificate ").append(
				" where empiId=:empiId").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("根据empiId删除证件信息失败。", e);
		}
	}

	/**
	 * 根据empiId删除其他属性
	 * 
	 * @param empiId
	 * @throws ModelDataOperationException
	 */
	public void deleteExtensionByEmpiId(String empiId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("delete from MPI_Extension ").append(
				" where empiId=:empiId").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("根据empiId删除其他属性失败。", e);
		}
	}

	/**
	 * 根据empiId删除卡信息
	 * 
	 * @param empiId
	 * @throws ModelDataOperationException
	 */
	public void deleteCardByEmpiId(String empiId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("delete from MPI_Card ").append(
				" where empiId=:empiId").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("根据empiId删除卡信息失败。", e);
		}
	}

	/**
	 * 放入最后修改人和机构字段值
	 * 
	 * @param records
	 * @param dao
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void putLastModifyUserAndUnit(Map<String, Object> records,
			BaseDAO dao) {
		UserRoleToken user = UserRoleToken.getCurrent();
		String userName = (String) user.getUserId();
		records.put("lastModifyUser", userName);
		String unit = user.getManageUnit().getId();
		records.put("lastModifyUnit", unit);
		Collection<Object> c = records.values();
		Iterator it = c.iterator();
		while (it.hasNext()) {
			Object o = it.next();
			if (o instanceof List) {
				for (int j = 0; j < ((List) o).size(); j++) {
					Map<String, Object> record = (Map<String, Object>) ((List) o)
							.get(j);
					record.put("lastModifyUser", userName);
					record.put("lastModifyUnit", unit);
					if (record.get("createUser") == null) {
						record.put("createUser", userName);
					}
					if (record.get("createUnit") == null) {
						record.put("createUnit", unit);
					}
				}
			}
		}
	}

	/**
	 * 根据CardNo获取不重复的empiId集合
	 * 
	 * @param cardNo
	 * @return
	 * @throws ModelDataOperationException
	 */
	private List<Map<String, Object>> getEmpiList(String cardNo)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer(
				"select distinct empiId as empiId from MPI_Card where cardNo=:cardNo");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("cardNo", cardNo);
		try {
			return dao.doQuery(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("根据cardNo获取empiId失败");
		}
	}

	/**
	 * @param MZHM
	 * @return
	 */
	public List<Map<String, Object>> getEmpiInfoByMZHM(String MZHM)
			throws ModelDataOperationException {
		try {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("MZHM", MZHM);
			List<Map<String, Object>> empiList = dao.doQuery(
					"select EMPIID as empiId,BRID as BRID from MS_BRDA"
							+ " where MZHM=:MZHM", parameters);
			for (Map<String, Object> map : empiList) {
				String empiId = (String) map.get("empiId");
				String BRID = (String) map.get("BRID");
				Map<String, Object> info = getEmpiInfoByEmpiid(empiId);
				info.put("BRID", BRID);
				list.add(info);
			}
			return list;
		} catch (PersistentDataOperationException e) {
			logger.error("failed to get child base message.", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取基本信息失败。");
		}
	}

	/**
	 * 根据CardNo获取基本信息
	 * 
	 * @param cardNo
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getEmpiInfoByCardNo(String cardNo)
			throws ModelDataOperationException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> empiList = getEmpiList(cardNo);
		for (Map<String, Object> map : empiList) {
			String empiId = (String) map.get("empiId");
			Map<String, Object> info = getEmpiInfoByEmpiid(empiId);
			list.add(info);
		}
		return list;
	}

	/**
	 * 根据certificateNo，certificateTypeCode获取基本信息
	 * 
	 * @param certificateNo
	 * @param certificateTypeCode
	 * @return
	 * @throws ModelDataOperationException
	 * @throws ControllerException
	 */
	public List<Map<String, Object>> getEmpiInfoBycertificateNoAndcertificateTypeCode(
			String certificateNo, String certificateTypeCode)
			throws ModelDataOperationException, ControllerException {
		List<Map<String, Object>> info;
		String hql = buildHql(certificateNo, certificateTypeCode);
		try {
			info = dao.doQuery(hql, null);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR,
					"根据certificateNo，certificateTypeCode获取基本信息失败。");
		}
		return info;
	}

	private String buildHql(String certificateNo, String certificateTypeCode)
			throws ControllerException {
		StringBuffer sb = new StringBuffer("select ");
		Schema sc = SchemaController.instance().get(MPI_DemographicInfo);
		List<SchemaItem> items = sc.getItems();
		for (int i = 0; i < items.size(); i++) {
			SchemaItem si = items.get(i);
			if (si.isVirtual() || si.isRef()) {
				continue;
			}
			sb.append("a.").append(si.getId()).append(" as ")
					.append(si.getId()).append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(
				" from MPI_DemographicInfo a,MPI_Certificate b where a.empiId =b.empiId and b.certificateNo ='")
				.append(certificateNo).append("' and certificateTypeCode = '")
				.append(certificateTypeCode).append("'");
		return sb.toString();
	}

	public List<Map<String, Object>> getCertificatesByEmpiId(String empiId)
			throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "empiId", "s", empiId);
		try {
			List<Map<String, Object>> list = dao.doList(cnd,
					"certificateTypeCode", MPI_Certificate);
			if (list == null || list.size() < 1) {
				return null;
			}
			return list;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("获取证件信息失败。", e);
		}
	}

	/**
	 * 根据empiId和证件类型获得证件信息
	 * 
	 * @param empiId
	 * @param certificateTypeCode
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryCertificateInfoByEmpiIdAndTypeCode(
			String empiId, String certificateTypeCode)
			throws ModelDataOperationException {
		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "empiId", "s", empiId);
		List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "certificateTypeCode",
				"s", certificateTypeCode);
		List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		try {
			return dao.doList(cnd, null, MPI_Certificate);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("");
		}
	}

	/**
	 * @param fYGB
	 * @param cnds
	 * @param parameters
	 * @param ctx
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> doPersonLoad(String pkey, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", pkey);
		try {
			Map<String, Object> body = EmpiUtil.queryWithPIX(pkey, dao, ctx);
			if (body != null && body.size() > 0) {
				body.put("empiId", body.get("mpiId"));
				if (body.containsKey("certificates")) {
					List<Map<String, Object>> certificates = (List<Map<String, Object>>) body
							.get("certificates");
					for (int i = 0; i < certificates.size(); i++) {
						if ("01".equals(certificates.get(i).get(
								"certificateTypeCode")
								+ "")) {
							body.put("idCard",
									certificates.get(i).get("certificateNo"));
							break;
						}
					}
				}
				if (body.containsKey("contactWays")) {
					List<Map<String, Object>> contactWays = (List<Map<String, Object>>) body
							.get("contactWays");
					for (int i = 0; i < contactWays.size(); i++) {
						if ("01".equals(contactWays.get(i).get(
								"contactTypeCode")
								+ "")) {
							body.put("mobileNumber",
									contactWays.get(i).get("contactNo"));
						}
						if ("03".equals(contactWays.get(i).get(
								"contactTypeCode")
								+ "")) {
							body.put("contactPhone",
									contactWays.get(i).get("contactNo"));
						}
						if ("04".equals(contactWays.get(i).get(
								"contactTypeCode")
								+ "")) {
							body.put("phoneNumber",
									contactWays.get(i).get("contactNo"));
						}
						if ("07".equals(contactWays.get(i).get(
								"contactTypeCode")
								+ "")) {
							body.put("email",
									contactWays.get(i).get("contactNo"));
						}
					}
				}
				if (body.containsKey("contactName")) {
					body.put("contact", body.get("contactName"));
				}
				if (body.containsKey("postalCode")
						&& ((String) body.get("postalCode")).trim().length() > 0) {
					body.put("zipCode",
							((String) body.get("postalCode")).trim());
				}
				if (body.containsKey("startWorkDate")
						&& body.get("startWorkDate") != null) {
					SimpleDateFormat sdfd = new SimpleDateFormat("yyyy-MM-dd");
					body.put("startWorkDate",
							sdfd.format((Date) body.get("startWorkDate")));
				}

				if (body.containsKey("addresses")) {
					List<Map<String, Object>> addresses = (List<Map<String, Object>>) body
							.get("addresses");
					for (int i = 0; i < addresses.size(); i++) {
						if ("01".equals(addresses.get(i).get("addressTypeCode")
								+ "")) {
							if (addresses.get(i).containsKey("address")) {
								body.put("address",
										addresses.get(i).get("address"));
							}
							if (addresses.get(i).containsKey("postalCode")) {
								body.put("zipCode",
										addresses.get(i).get("postalCode"));
							}
							break;
						}
					}
				}
			} else {
				body = dao
						.doLoad("select a.empiId as empiId,a.personName as personName,a.idCard as idCard,a.sexCode as sexCode,a.birthday as birthday,a.workPlace as workPlace,a.mobileNumber as mobileNumber,a.phoneNumber as phoneNumber,a.contact as contact,a.contactPhone as contactPhone,a.registeredPermanent as registeredPermanent,a.nationCode as nationCode,a.bloodTypeCode as bloodTypeCode,a.rhBloodCode as rhBloodCode,a.educationCode as educationCode,a.workCode as workCode,a.maritalStatusCode as maritalStatusCode,a.homePlace as homePlace,a.zipCode as zipCode,a.address as address,a.email as email,a.nationalityCode as nationalityCode,a.startWorkDate as startWorkDate,a.createUnit as createUnit,a.createUser as createUser,a.createTime as createTime,a.lastModifyUnit as lastModifyUnit,a.lastModifyTime as lastModifyTime,a.lastModifyUser as lastModifyUser,a.status as status,a.photo as photo from MPI_DemographicInfo a where a.empiId=:empiId",
								parameters);
			}
			Map<String, Object> BRDAbody = dao
					.doLoad("select a.MZHM as MZHM,a.BRXZ as BRXZ,a.BRID as BRID from MS_BRDA a where a.EMPIID=:empiId",
							parameters);
			if (BRDAbody != null) {
				body.putAll(BRDAbody);
			}

			PublicModel pm = new PublicModel();
			Date birthday = (Date) body.get("birthday");
			pm.doPersonAge(birthday, body);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			body.put("birthday", sdf.format((Date) body.get("birthday")));
			body.put("age",
					((Map<String, Object>) body.get("body")).get("ages"));
			body.remove("body");
			return body;
		} catch (PersistentDataOperationException e) {
			logger.error("Failed to get personName from MPI_DemographicInfo .",
					e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询个人基本信息的姓名失败！");
		} catch (ServiceException e) {
			logger.error("Failed to get personName from MPI_DemographicInfo .",
					e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询个人基本信息的姓名失败！");
		}
	}

	/**
	 * 获取门诊号码
	 * 
	 */
	public String doOutPatientNumber(Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String userId = (String) user.getUserId();
		String manaUnitId = user.getManageUnit().getId();// 用户的机构ID
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("JDJG", manaUnitId);
		String zdcsmzh = BSPHISUtil.getNotesNumberNotIncrement(userId,
				manaUnitId, 3, dao, ctx);
		String CARDORMZHM = ParameterUtil.getParameter(manaUnitId,
				BSPHISSystemArgument.CARDORMZHM, ctx);
		Map<String, Object> parameters_MPI_Card = new HashMap<String, Object>();
		parameters_MPI_Card.put("cardNo", zdcsmzh);
		try {
			long count_MZHM = dao.doCount("MS_BRDA", "MZHM=:cardNo",
					parameters_MPI_Card);
			if (count_MZHM > 0) {
				BSPHISUtil.getNotesNumber(userId, manaUnitId, 3, dao, ctx);
				return doOutPatientNumber(ctx);
			} else if ("2".equals(CARDORMZHM)) {
				long count = dao.doCount("MPI_Card", "cardNo=:cardNo",
						parameters_MPI_Card);
				if (count > 0) {
					BSPHISUtil.getNotesNumber(userId, manaUnitId, 3, dao, ctx);
					return doOutPatientNumber(ctx);
				}
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取门诊号码失败:查询卡号或门诊号码失败!");
		}
		if (zdcsmzh == null) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取门诊号码失败:门诊号码未维护或已用完!");
		}
		return zdcsmzh;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2012-11-22
	 * @description 新增时查询系统参数里面的病人性质
	 * @updateInfo
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryNature(Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		Map<String, Object> ret = new HashMap<String, Object>();
		long brxz = Long.parseLong((ParameterUtil.getParameter(jgid,
				BSPHISSystemArgument.MRXZ, ctx)));
		try {
			if (brxz == 0) {
				return ret;
			}
			Map<String, Object> map_brxz = dao.doLoad(BSPHISEntryNames.GY_BRXZ,
					brxz);
			if (map_brxz == null) {
				return ret;
			}
			ret.put("text", map_brxz.get("XZMC"));
			ret.put("key", brxz);
		} catch (PersistentDataOperationException e) {
			logger.error("Failed to query default patient nature", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询默认病人性质失败");
		}

		return ret;
	}
}
