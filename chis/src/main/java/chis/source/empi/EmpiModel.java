/**
 * EmpiModel.java Created on Dec 4, 2009 9:13:52 AM
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 * 
 */
package chis.source.empi;

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

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.service.ServiceCode;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import chis.source.util.SchemaUtil;
import chis.source.util.UserUtil;

import com.alibaba.fastjson.JSONException;

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
public class EmpiModel implements BSCHISEntryNames {

	protected BaseDAO dao;

	protected Logger logger = LoggerFactory.getLogger(EmpiModel.class);

	public EmpiModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 根据姓名+性别+出生日期获取基础信息
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
				cnd1 = CNDHelper
						.toListCnd("['isNull', ['s', 'is'], ['$', 'personName']]");
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
	 * 根据证件号、证件类型获取个人信息
	 * 
	 * @param cardNo
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Map<String, Object>> getEmpiInfoByCertificate(
			String certifiteNo, String certifiteType)
			throws ModelDataOperationException {
		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "certificateNo", "s",
				certifiteNo);
		List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "certificateTypeCode",
				"s", certifiteType);
		List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		List<Map<String, Object>> info;
		try {
			info = dao.doList(cnd, null, MPI_Certificate);
			if (info.size() == 0) {
				return new ArrayList();
			}
			Map<String, Object> certificate = info.get(0);
			String empiid = (String) certificate.get("empiId");
			List<?> empiCnd = CNDHelper.createSimpleCnd("eq", "empiId", "s",
					empiid);
			List<Map<String, Object>> empiInfoList = dao.doList(empiCnd, null,
					MPI_DemographicInfo);
			return empiInfoList;

		} catch (PersistentDataOperationException e) {
			logger.error("failed to get child base message.", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取基本信息失败。");
		}
	}

	/**
	 * 根据姓名+性别+出生日期获取基础信息
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
				cnd1 = CNDHelper
						.toListCnd("['isNull', ['s', 'is'], ['$', 'personName']]");
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
		} catch (Exception e) {
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
			Map<String, Object> res = dao.doLoad(MPI_DemographicInfo, empiId);
			return SchemaUtil.setDictionaryMessageForList(res,
					MPI_DemographicInfo);
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
		Date newBirthday = BSCHISUtil.toDate(newBirthdayStr);
		int result = BSCHISUtil.dateCompare(newBirthday, oldBirthday);
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
			EmpiInterfaceImpi empiInterfaceImpi, boolean isChild, Context ctx,
			Map<String, Object> jsonRes) throws ModelDataOperationException,
			ServiceException {
		// @@ 检查是否已存在。
		String empiId = (String) personInfo.get("empiId");
		if (empiId != null && isRecordExists(MPI_DemographicInfo, empiId, true)) {
			try {
				Map<String, Object> PIXData = EmpiUtil
						.changeToPIXFormat(personInfo);
				personInfo = EmpiUtil.updatePerson(dao, ctx, personInfo,
						PIXData, jsonRes);
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
			Date d = BSCHISUtil.toDate(((String) personMap.get("birthday"))
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
		int age = BSCHISUtil.calculateAge(birthday, calculateDate);

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
		return BSCHISUtil.calculateAge(birthday, null);
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
		StringBuffer sb = new StringBuffer(
				"select cardId as cardId from MPI_Card");
		sb.append(" where cardTypeCode = :cardTypeCode and cardNo = :cardNo and  status = :status");
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("cardTypeCode", cardTypeCode);
		param.put("cardNo", cardNo);
		param.put("status", "1");
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
				"select certificateId as certificateId from  MPI_Certificate");
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
			ServiceAdapter.invoke("platform.empiService", "execute", record,
					"empi");
		} catch (Exception e) {
			throw new ModelDataOperationException("保存个人基本信息失败。", e);
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
		String hql = new StringBuffer("delete from  MPI_Address").append(
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
		String hql = new StringBuffer("delete from MPI_Phone").append(
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
		String hql = new StringBuffer("delete from MPI_Certificate").append(
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
		String hql = new StringBuffer("delete from MPI_Extension").append(
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
		String hql = new StringBuffer("delete from MPI_Card").append(
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
		String userName = UserUtil.get(UserUtil.USER_ID);
		records.put("lastModifyUser", userName);
		String unit = UserUtil.get(UserUtil.MANAUNIT_ID);
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
				"select distinct empiId as empiId from ").append(MPI_Card)
				.append(" where cardNo=:cardNo");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("cardNo", cardNo);
		try {
			return dao.doQuery(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("根据cardNo获取empiId失败");
		}
	}

	/**
	 * 根据正常的CardNo获取不重复的empiId集合
	 * 
	 * @param cardNo
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getEmpiListInStatus(String cardNo)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer(
				"select distinct empiId as empiId from ").append(MPI_Card)
				.append(" where cardNo=:cardNo and status='0'");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("cardNo", cardNo);
		try {
			return dao.doQuery(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("根据cardNo获取empiId失败");
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
	 */
	public List<Map<String, Object>> getEmpiInfoBycertificateNoAndcertificateTypeCode(
			String certificateNo, String certificateTypeCode)
			throws ModelDataOperationException {
		List<Map<String, Object>> info;
		try {
			String hql = buildHql(certificateNo, certificateTypeCode);
			info = dao.doQuery(hql, null);
		} catch (Exception e) {
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
		sb.append(" from ").append(MPI_DemographicInfo).append(" a,");
		sb.append(MPI_Certificate)
				.append(" b where a.empiId =b.empiId and b.certificateNo ='")
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
	 * 
	 * @Description:依据empiId获取该人的卡信息
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-11-26 上午9:23:29
	 * @Modify:
	 */
	public List<Map<String, Object>> getCardsList(String empiId)
			throws ModelDataOperationException {
		List<Object> cnd = CNDHelper.createSimpleCnd("eq", "empiId", "s",
				empiId);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doList(cnd, "a.createTime desc", MPI_Card);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "依据empiId获取该人的卡信息失败！", e);
		}
		rsList = SchemaUtil.setDictionaryMessageForList(rsList, MPI_Card);
		return rsList;
	}

	/**
	 * 
	 * @Description:获取个人基本信息
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-11-26 上午11:10:45
	 * @Modify:
	 */
	public Map<String, Object> getDemographicInfo(String empiId)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(MPI_DemographicInfo, empiId);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取个人基本信息失败！", e);
		}
		return rsMap;
	}

	/**
	 * 更新卡状态
	 * 
	 * @Description:
	 * @param body
	 * @param ctx
	 * @throws ServiceException
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doChangeCardStatus(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException, ServiceException {
		try {
			String op = (String) body.get("op");
			Map<String, Object> card = (Map<String, Object>) body.get("data");
			if (op.equals("lockCard")) {
				card.put("status", "1");
				changeCardStatus(card);
				if (EmpiUtil.getIfNeedPix(dao, ctx)) {
					EmpiUtil.empiInterfaceImpi.lockCard(card
							.get("cardTypeCode").toString(), card.get("cardNo")
							.toString());
				}
			} else if (op.equals("unlockCard")) {
				card.put("status", "0");
				changeCardStatus(card);
				if (EmpiUtil.getIfNeedPix(dao, ctx)) {
					EmpiUtil.empiInterfaceImpi.unlockCard(
							card.get("cardTypeCode").toString(),
							card.get("cardNo").toString());
				}
			} else if (op.equals("writeOffCard")) {
				card.put("status", "2");
				changeCardStatus(card);
				if (EmpiUtil.getIfNeedPix(dao, ctx)) {
					EmpiUtil.empiInterfaceImpi.writeOffCard(
							card.get("cardTypeCode").toString(),
							card.get("cardNo").toString());
				}
			}

		} catch (ValidateException e) {
			throw new ModelDataOperationException("更新卡状态失败!", e);
		}

	}

	/**
	 * 修改卡状态
	 * 
	 * @Description:
	 * @param data
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 * @author YuBo 2015-5-15 上午9:23:55
	 * @Modify:
	 */
	public void changeCardStatus(Map<String, Object> data)
			throws ModelDataOperationException, ValidateException {
		try {
			ServiceAdapter.invoke("platform.empiService", "execute", data,
					"card");
		} catch (Exception e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存卡信息失败。", e);
		}
	}
}
