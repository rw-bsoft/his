/**
 * @(#)PublicService.java Created on 2012-1-5 上午11:40:38
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.pub;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.cdh.ChildrenHealthModel;
import chis.source.control.ControlRunner;
import chis.source.dic.BusinessType;
import chis.source.dic.PlanStatus;
import chis.source.fhr.TemplateModule;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.service.ServiceCode;
import chis.source.util.BSCHISUtil;
import chis.source.util.Base64;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import ctd.dictionary.Dictionary;
import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.spring.AppDomainContext;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;

/**
 * @description
 * 
 * @author <a href="mailto:huangpf@bsoft.com.cn">huangpf</a>
 */
public class PublicService extends AbstractActionService implements
		DAOSupportable {

	// 不需要做首诊测压也不用做高血压核实。
	public static final int DO_HYPERTENSION_NON = 0;
	// 需要做首诊测压
	public static final int DO_HYPERTENSION_FIRST = 1;
	// 需要做高血压核实
	public static final int DO_HYPERTENSION_CHECK = 2;
	// 需要建立高血压档案。
	public static final int DO_HYPERTENSION_CREATE = 3;

	static Logger logger = LoggerFactory.getLogger(PublicService.class);

	/**
	 * 
	 */
	public List<String> getNoDBActions() {
		List<String> list = new ArrayList<String>();
		list.add("getCurrentDate");
		list.add("calculateAge");
		list.add("getManageUnit");
		return list;
	}

	/**
	 * 计算年龄
	 * 
	 * @param req
	 * @param res
	 * @param sc
	 * @param ctx
	 * @throws JSONException
	 */
	public void doCalculateAge(Map<String, Object> req,
			Map<String, Object> res, Context ctx) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> body = (HashMap<String, Object>) req
				.get("body");
		String birthdayStr = (String) body.get("birthday");
		DateFormat f = new SimpleDateFormat(Constants.DEFAULT_SHORT_DATE_FORMAT);
		Date birthday = null;
		try {
			birthday = f.parse(birthdayStr);
		} catch (ParseException e) {
			logger.error("无法识别的时间格式:{}", new Object[] { birthdayStr, e });
			res.put(RES_CODE, Constants.CODE_INVALID_REQUEST);
			res.put(RES_MESSAGE, "无法识别的时间格式：" + birthdayStr);
			return;
		}
		int age = BSCHISUtil.calculateAge(birthday, null);
		HashMap<String, Object> resBody = new HashMap<String, Object>();
		resBody.put("age", age);
		res.put("body", resBody);
	}

	/**
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 */
	public void doGetCurrentDate(Map<String, Object> req,
			Map<String, Object> res, Context ctx) {
		res.put("date", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		Map<String, Object> body = new HashMap<String, Object>();
		SimpleDateFormat sdf = new SimpleDateFormat(
				Constants.DEFAULT_SHORT_DATE_FORMAT);
		body.put("currentDate", sdf.format(new Date()));
		res.put("body", body);
	}

	/**
	 * 根据EMPI 到基本信息表中查找到出生日期，算出周岁返回。
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param ctx
	 * @return
	 * @throws JSONException
	 * @throws PersistentDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public static int getAge(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws PersistentDataOperationException {
		Session session = (Session) ctx.get(Context.DB_SESSION);
		HashMap<String, Object> body = (HashMap<String, Object>) req
				.get("body");
		String empiId = (String) body.get("empiId");
		String occurDate = (String) body.get("occurDate");
		if (empiId == null || empiId.trim().length() == 0) {
			logger.error("empiID is missing!");
			res.put(Service.RES_CODE, Constants.CODE_INVALID_REQUEST);
			res.put(Service.RES_MESSAGE, "empi id is missing");
			return -1;
		}
		Map<String, Object> empiMap = getEmpi(empiId, session);
		if (empiMap == null) {
			res.put(Service.RES_CODE, Constants.CODE_EMPIID_NOT_EXISTS);
			res.put(Service.RES_MESSAGE, "无法在数据库中找到该人基本信息");
			return -1;
		}
		// 计算周岁
		Date birthday = (Date) empiMap.get("birthday");
		Date calculateDate = null;
		if (occurDate != null && !"".equals(occurDate)) {
			DateFormat f = new SimpleDateFormat(
					Constants.DEFAULT_SHORT_DATE_FORMAT);
			try {
				calculateDate = f.parse(occurDate);
			} catch (ParseException e) {
				logger.error("Date parse failed : " + occurDate, e);
				res.put(Service.RES_CODE, Constants.CODE_INVALID_REQUEST);
				res.put(Service.RES_MESSAGE, "无法识别格式的时间：" + occurDate);

			}
		}
		return BSCHISUtil.calculateAge(birthday, calculateDate);
	}

	/**
	 * @param empiId
	 * @param session
	 * @return 如果对应的个人信息不存在，返回-1.
	 * @throws PersistentDataOperationException
	 */
	public static int getAge(String empiId, Session session)
			throws PersistentDataOperationException {
		Date birthday = getBirthday(empiId, session);
		if (birthday == null) {
			return -1;
		}
		return BSCHISUtil.calculateAge(birthday, null);
	}

	/**
	 * @param empiId
	 * @param session
	 * @return
	 * @throws PersistentDataOperationException
	 */
	public static Date getBirthday(String empiId, Session session)
			throws PersistentDataOperationException {
		String hql = new StringBuffer("select birthday from ")
				.append(MPI_DemographicInfo).append(" where empiId = :empiId")
				.toString();
		Query query = session.createQuery(hql);
		query.setString("empiId", empiId);
		Date birthday = null;
		try {
			birthday = (Date) query.uniqueResult();
			session.flush();
		} catch (HibernateException e) {
			throw new PersistentDataOperationException(e);
		}
		return birthday;
	}

	/**
	 * 通过EMPIID获取个人信息
	 * 
	 * @param empiId
	 * @param session
	 * @return
	 * @throws PersistentDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getEmpi(String empiId, Session session)
			throws PersistentDataOperationException {
		String hql = new StringBuffer("select empiId as empiId, personName ")
				.append("as personName, phoneNumber as phoneNumber,")
				.append("photo as photo, idCard as idCard, zipCode as zipCode, ")
				.append("address as address, contact as contact, contactPhone as ")
				.append("contactPhone, sexCode as sexCode, registeredPermanent as ")
				.append("registeredPermanent, bloodTypeCode as bloodTypeCode, ")
				.append("homePlace as homePlace, mobileNumber as mobileNumber, ")
				.append("email as email, birthday as birthday, nationalityCode as ")
				.append("nationalityCode, nationCode as nationCode, rhBloodCode as ")
				.append("rhBloodCode, maritalStatusCode as maritalStatusCode,")
				.append("startWorkDate as startWorkDate, workCode as workCode,")
				.append("educationCode as educationCode, insuranceCode as insuranceCode,")
				.append("createUnit as createUnit, createUser as createUser, ")
				.append("createTime as createTime, lastModifyUnit as lastModifyUnit, ")
				.append("lastModifyTime as lastModifyTime, lastModifyUser as ")
				.append("lastModifyUser, status as status, workPlace as workPlace from ")
				.append(MPI_DemographicInfo)
				.append(" where empiId = :empiId and status = :status")
				.toString();
		Query query = session.createQuery(hql).setResultTransformer(
				Transformers.ALIAS_TO_ENTITY_MAP);
		query.setString("empiId", empiId);
		query.setString("status", String.valueOf(Constants.CODE_STATUS_NORMAL));
		Map<String, Object> map = null;
		try {
			map = (Map<String, Object>) query.uniqueResult();
		} catch (HibernateException e) {
			throw new PersistentDataOperationException(e);
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	public void doGetManageUnit(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException,
			PersistentDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String manaUnitId = (String) body.get("manaUnitId");

		Dictionary dic = DictionaryController.instance().getDic(
				"chis.@manageUnit");
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("key", manaUnitId);
		m.put("text", dic.getText(manaUnitId));
		res.put("manageUnit", m);
	}

	/**
	 * 返回经过Base64编码后的个人信息照片数据。
	 * 
	 * @param photoId
	 * @param session
	 * @return
	 * @throws PersistentDataOperationException
	 */
	public static String getPhoto(String photoId, Session session)
			throws PersistentDataOperationException {
		byte[] image = getPhotoData(photoId, session);
		if (image == null) {
			return null;
		}
		return Base64.encode(image);
	}

	/**
	 * @param photoId
	 * @param session
	 * @return
	 * @throws PersistentDataOperationException
	 */
	public static byte[] getPhotoData(String photoId, Session session)
			throws PersistentDataOperationException {
		String hql = new StringBuffer("select content from ")
				.append(FileResources).append(" where fileId = :fileId")
				.toString();
		Query query = session.createQuery(hql);
		query.setString(
				"fileId",
				photoId == null || photoId.length() == 0 ? Constants.DEFAULT_PHOTO_ID
						: photoId);
		byte[] image;
		try {
			image = (byte[]) query.uniqueResult();
			session.flush();
		} catch (HibernateException e) {
			throw new PersistentDataOperationException(e);
		}
		return image;
	}

	/**
	 * @param age
	 * @param session
	 * @return lifeCycle的code，以及其text。
	 * @throws PersistentDataOperationException
	 */
	public static String[] getLifeCycle(int age, Session session)
			throws PersistentDataOperationException {
		String hql = new StringBuffer("select cycleId, cycleName from ")
				.append(EHR_LifeCycle)
				.append(" where startAge <= :startAge and endAge >= :endAge")
				.toString();
		Query query = session.createQuery(hql);
		query.setInteger("startAge", age);
		query.setInteger("endAge", age);
		List<?> l;
		try {
			l = query.list();
			session.flush();
		} catch (HibernateException e) {
			throw new PersistentDataOperationException(e);
		}
		if (l.size() == 0) {
			return null;
		}
		String[] s = new String[2];
		Object obj = l.get(0);
		s[0] = (String) ((Object[]) obj)[0];
		s[1] = (String) ((Object[]) obj)[1];
		return s;
	}

	/**
	 * @param birthday
	 * @param calculateDate
	 * @param session
	 * @return lifeCycle的code，text以及年龄。
	 * @throws PersistentDataOperationException
	 */
	public static String[] getLifeCycle(Date birthday, Date calculateDate,
			Session session) throws PersistentDataOperationException {
		int age = BSCHISUtil.calculateAge(birthday, calculateDate);
		String hql = new StringBuffer("select cycleId, cycleName from ")
				.append(EHR_LifeCycle)
				.append(" where startAge <= :startAge and endAge >= :endAge")
				.toString();
		Query query = session.createQuery(hql);
		query.setInteger("startAge", age);
		query.setInteger("endAge", age);
		List<?> l;
		try {
			l = query.list();
			session.flush();
		} catch (HibernateException e) {
			throw new PersistentDataOperationException(e);
		}
		if (l.size() == 0) {
			return null;
		}
		String[] s = new String[3];
		Object[] obj = (Object[]) l.get(0);
		s[0] = (String) obj[0];
		s[1] = (String) obj[1];
		s[2] = String.valueOf(age);
		return s;
	}

	/**
	 * 查找健康档案编号。
	 * 
	 * @param empiId
	 * @return
	 * @throws PersistentDataOperationException
	 */
	public static String getPhrId(String empiId, Session session)
			throws PersistentDataOperationException {
		String hql = new StringBuffer("select phrId from ")
				.append(EHR_HealthRecord).append(" where empiId = :empiId")
				.toString();
		Query query = session.createQuery(hql);
		query.setString("empiId", empiId);
		String str;
		try {
			str = (String) query.uniqueResult();
			session.flush();
		} catch (HibernateException e) {
			throw new PersistentDataOperationException(e);
		}
		return str;
	}

	/**
	 * 判断个人健康档案是否有建立（不检查已注销的档案）。
	 * 
	 * @param empiId
	 * @param session
	 * @return 如果已建立返回true，否则返回false，如果档案已注销false。
	 */
	public static boolean isPersonalHealthRecordExists(String empiId,
			Session session) throws PersistentDataOperationException {
		return isRecordExists(EHR_HealthRecord, empiId, true, session);
	}

	/**
	 * 判断糖尿病档案是否有建立（不检查已注销的档案）。
	 * 
	 * @param empiId
	 * @param session
	 * @return 如果已建立返回true，否则返回false，如果档案已注销false。
	 */
	public static boolean isDiabetesRecordExists(String empiId, Session session)
			throws PersistentDataOperationException {
		return isRecordExists(MDC_DiabetesRecord, empiId, true, session);
	}
	
	/**
	 * 判断高血压档案是否有建立（不检查已注销的档案）。
	 * 
	 * @param empiId
	 * @param session
	 * @return 如果已建立返回true，否则返回false，如果档案已注销false。
	 */
	public static boolean isHypertensionRecordExists(String empiId, Session session)
			throws PersistentDataOperationException {
		return isRecordExists(MDC_HypertensionRecord, empiId, true, session);
	}

	/**
	 * @param empiId
	 * @param session
	 * @throws HibernateException
	 */
	public static final boolean isChildRecordExists(String empiId,
			Session session) throws PersistentDataOperationException {
		return isRecordExists(CDH_HealthCard, empiId, true, session);
	}

	/**
	 * @param empiId
	 * @param session
	 * @return
	 * @throws PersistentDataOperationException
	 */
	public static final boolean isDebilityChildrenRecordExists(String empiId,
			Session session) throws PersistentDataOperationException {
		return isRecordExists(CDH_DebilityChildren, empiId, true, session);
	}

	/**
	 * @param empiId
	 * @param sssion
	 * @return
	 */
	public static final boolean isPregnantRecordExists(String empiId,
			Session session) throws PersistentDataOperationException {
		return isRecordExists(MHC_PregnantRecord, empiId, true, session);
	}

	/**
	 * @param empiId
	 * @param session
	 * @return
	 * @throws PersistentDataOperationException
	 */
	public static final boolean isWomanRecordExists(String empiId,
			Session session) throws PersistentDataOperationException {
		return isRecordExists(MHC_WomanRecord, empiId, false, session);
	}

	/**
	 * 检查一个表里的对应一个empiId的记录是否已存在。
	 * 
	 * @param table
	 *            表名
	 * @param empiId
	 * @param session
	 * @return 如果存在返回true，否则返回false。
	 * @throws PersistentDataOperationException
	 * @throws HibernateExcpetion
	 */
	public static boolean isRecordExists(String table, String empiId,
			boolean checkStatus, Session session)
			throws PersistentDataOperationException {
		StringBuffer hql = new StringBuffer("select count(*) from ").append(
				table).append(" where empiId = :empiId");
		if (checkStatus) {
			hql.append(" and status != :status");
		}
		Query query = session.createQuery(hql.toString());
		query.setString("empiId", empiId);
		if (checkStatus) {
			query.setString("status",
					String.valueOf(Constants.CODE_STATUS_END_MANAGE));
		}
		Long c;
		try {
			c = (Long) query.uniqueResult();
			session.flush();
		} catch (HibernateException e) {
			throw new PersistentDataOperationException(e);
		}
		if (c == null || c.intValue() == 0) {
			return false;
		}
		return true;
	}

	/**
	 * 取主档管辖机构。
	 * 
	 * @param empiId
	 * @param session
	 * @return
	 * @throws PersistentDataOperationException
	 */
	public static String getManaUnit(String empiId, Session session)
			throws PersistentDataOperationException {
		String hql = new StringBuffer("select manaUnitId from ")
				.append(EHR_HealthRecord)
				.append(" where empiId=:empiId and status=:status").toString();
		Query query = session.createQuery(hql);
		query.setString("empiId", empiId);
		query.setString("status", String.valueOf(Constants.CODE_STATUS_NORMAL));
		String manaUnitId;
		try {
			manaUnitId = (String) query.uniqueResult();
			session.flush();
		} catch (HibernateException e) {
			throw new PersistentDataOperationException(e);
		}
		return manaUnitId;
	}

	/**
	 * 获取责任医生。
	 * 
	 * @param empiId
	 * @param session
	 * @return
	 * @throws PersistentDataOperationException
	 */
	public static String getManaDoctor(String empiId, String recordEntry,
			Session session) throws PersistentDataOperationException {
		String hql = new StringBuffer("select manaDoctorId from ")
				.append(recordEntry)
				.append(" where empiId = :empiId and status=:status")
				.toString();
		Query query = session.createQuery(hql);
		query.setString("empiId", empiId);
		query.setString("status", String.valueOf(Constants.CODE_STATUS_NORMAL));
		String manageDoctor;
		try {
			manageDoctor = (String) query.uniqueResult();
			session.flush();
		} catch (HibernateException e) {
			throw new PersistentDataOperationException(e);
		}
		return manageDoctor;
	}

	/**
	 * 是否需要做高血压随访。
	 * 
	 * @param empiId
	 * @param session
	 * @return
	 * @throws Exception
	 */
	public static boolean needHypertensionVisit(String empiId, Session session)
			throws PersistentDataOperationException {
		Calendar c = Calendar.getInstance();
		return needVisit(empiId, MDC_HypertensionRecord, BusinessType.GXY,
				c.getTime(), session);
	}

	/**
	 * 是否需要做糖尿病随访。
	 * 
	 * @param empiId
	 * @param session
	 * @return
	 */
	public static boolean needDiabetesVisit(String empiId, Session session)
			throws PersistentDataOperationException {
		Calendar c = Calendar.getInstance();
		return needVisit(empiId, MDC_DiabetesRecord, BusinessType.TNB,
				c.getTime(), session);
	}

	/**
	 * 是否需要做肿瘤随访。
	 * 
	 * @param empiId
	 * @param session
	 * @return
	 * @throws HibernateException
	 */
	public static boolean needTumourVisit(String empiId, Session session)
			throws PersistentDataOperationException {
		Calendar c = Calendar.getInstance();
		return needVisit(empiId, MDC_TumourRecord, BusinessType.ZL,
				c.getTime(), session);
	}

	/**
	 * 是否需要做老年人随访。
	 * 
	 * @param empiId
	 * @param session
	 * @return
	 * @throws HibernateException
	 */
	public static boolean needOldPeopleVisit(String empiId, Session session)
			throws PersistentDataOperationException {
		Calendar c = Calendar.getInstance();
		// c.add(Calendar.DAY_OF_YEAR, 10);
		return needVisit(empiId, EHR_HealthRecord, BusinessType.LNR,
				c.getTime(), session);
	}

	/**
	 * 判断是否需要做随访。
	 * 
	 * @param empiId
	 * @param recordTable
	 * @param planType
	 * @param session
	 * @return
	 * @throws PersistentDataOperationException
	 */
	public static boolean needVisit(String empiId, String recordTable,
			String planType, Date date, Session session)
			throws PersistentDataOperationException {
		String hql0 = new StringBuffer("select count(*) from ")
				.append(recordTable)
				.append(" where empiId = :empiId and status = :status")
				.toString();
		Query query0 = session.createQuery(hql0);
		query0.setString("empiId", empiId);
		query0.setString("status", String.valueOf(Constants.CODE_STATUS_NORMAL));
		Long c0;
		try {
			c0 = (Long) query0.uniqueResult();
			session.flush();
		} catch (HibernateException e) {
			throw new PersistentDataOperationException(e);
		}
		if (c0 == 0) {
			return false;
		}

		String hql = new StringBuffer("select count(*) from ")
				.append(PUB_VisitPlan)
				.append(" where empiId = :empiId and businessType = :planType ")
				.append("and planStatus=:planStatus and beginDate<:now")
				.toString();
		Query query = session.createQuery(hql);
		query.setString("empiId", empiId);
		query.setString("planType", planType);
		query.setString("planStatus", PlanStatus.NEED_VISIT);
		query.setDate("now", date);
		Long l;
		try {
			l = (Long) query.uniqueResult();
			session.flush();
		} catch (HibernateException e) {
			throw new PersistentDataOperationException(e);
		}

		if (l == 0) {
			return false;
		}
		return true;
	}

	/**
	 * 判断是否需要进行首诊测压、高血压核实。
	 * 
	 * @param empiId
	 * @param session
	 * @return
	 * @throws Exception
	 */
	public static int ifHypertensionFirst(String empiId, Session session)
			throws Exception {
		int age = getAge(empiId, session);
		if (age == -1) {
			logger.error("Could not found empi record by empiId:" + empiId
					+ " or birthday info is not registed.");
			throw new Exception("该个人编号无法找到个人信息或出生日期未填写。");
		}
		if (age < 35) {
			return DO_HYPERTENSION_NON;
		}
		// query hyertensionfisrt record
		Calendar startc = Calendar.getInstance();
		Calendar endc = Calendar.getInstance();
		startc.set(startc.get(Calendar.YEAR), 0, 1, 0, 0, 0);
		endc.set(endc.get(Calendar.YEAR), 11, 31, 23, 59, 59);

		String hql = new StringBuffer("select count(*) as c from ")
				.append(MDC_HypertensionRecord)
				.append(" where empiId=:empiId and status=:status").toString();
		Query query = session.createQuery(hql);
		query.setString("empiId", empiId);
		query.setString("status", String.valueOf(Constants.CODE_STATUS_NORMAL));
		Long l;
		try {
			l = (Long) query.uniqueResult();
			session.flush();
		} catch (HibernateException e) {
			throw new PersistentDataOperationException(e);
		}
		if (l > 0) {
			return DO_HYPERTENSION_NON;
		}

		String hypertensionFirstHql = new StringBuffer(
				"select diagnosisType from ").append(MDC_HypertensionFirst)
				.append(" where empiId = :empiId and hypertensionFirstDate>")
				.append(":startDate and hypertensionFirstDate<:endDate")
				.toString();
		Query hyertensionFirstQuery = session.createQuery(hypertensionFirstHql);
		hyertensionFirstQuery.setString("empiId", empiId);
		hyertensionFirstQuery.setDate("startDate", startc.getTime());
		hyertensionFirstQuery.setDate("endDate", endc.getTime());
		String diagnosisType;
		try {
			diagnosisType = (String) hyertensionFirstQuery.uniqueResult();
			session.flush();
		} catch (HibernateException e) {
			throw new PersistentDataOperationException(e);
		}
		if (diagnosisType == null) {
			return DO_HYPERTENSION_FIRST;
		}
		if ("3".equals(diagnosisType)) {
			return DO_HYPERTENSION_NON;
		}
		if ("1".equals(diagnosisType)) {
			return DO_HYPERTENSION_CREATE;
		}
		return DO_HYPERTENSION_CHECK;
	}

	/**
	 * 
	 * @param empiId
	 * @param session
	 * @return
	 * @throws Exception
	 */
	public static Object ifDiabetesSimilarity(String empiId, Session session)
			throws Exception {
		StringBuffer hql = new StringBuffer("select diagnosisType from ")
				.append(MDC_DiabetesSimilarity).append(
						" where empiId = :empiId");
		Query query = session.createQuery(hql.toString());
		query.setString("empiId", empiId);
		Object diagnosisType = query.uniqueResult();
		return diagnosisType;
	}

	/**
	 * @param icd10
	 * @param session
	 * @return
	 * @throws PersistentDataOperationException
	 */
	public static String getActionIdByIcd10(String icd10, Session session)
			throws PersistentDataOperationException {
		String hql = new StringBuffer("select actionId from ")
				.append(PUB_ICD10).append(" where icd10 = :icd10").toString();
		Query query = session.createQuery(hql);
		query.setString("icd10", icd10);
		List<?> rstLst;
		try {
			rstLst = query.list();
			session.flush();
		} catch (HibernateException e) {
			throw new PersistentDataOperationException(e);
		}
		if (rstLst.size() == 0) {
			return null;
		}
		return (String) rstLst.get(0);
	}

	/**
	 * 获取服务器的rpc访问地址
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doGetServiceRpcUrl(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) {
		String localUrl = "";
		localUrl = AppDomainContext.getRpcServerWorkUrl();
		localUrl = localUrl.replace("hessian", "http");
		localUrl = localUrl.substring(0, localUrl.length() - 1);
		res.put("localUrl", localUrl);
	}

	/**
	 * 查询档案列表数据
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doQueryRecordList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String schemaId = (String) req.get("schema");
		//chis.application.hr.schemas.EHR_HealthRecord_HMC
		schemaId = schemaId.replace("EHR_HealthRecord_HMC", "EHR_HealthRecord");
		List queryCnd = null;
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
//		String year = new Date().getYear() + "";
		Calendar date = Calendar.getInstance();
		String year = String.valueOf(date.get(Calendar.YEAR));
		if (req.containsKey("year")) {
			year = req.get("year") + "";
		}
		String checkType = "1";
		if (req.containsKey("checkType")) {
			checkType = (String) req.get("checkType");
		}
		PublicModel hrModel = new PublicModel(dao);
		Map<String, Object> resBody = new HashMap<String, Object>();
		resBody = hrModel.queryRecordList(schemaId, queryCnd, queryCndsType,
				sortInfo, pageSize, pageNo, year, checkType);
		res.putAll(resBody);
	}

	/**
	 * 查询表单按钮权限
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doGetActionControl(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		String empiId = (String) body.get("empiId");
		String schema = (String) body.get("schema");
		String needSchema = (String) body.get("needSchema");
		if (empiId == null || "".equals(empiId)) {
			return;
		}
		try {
			Map<String, Object> resBody = new HashMap<String, Object>();
			Map<String, Boolean> data = ControlRunner.run(schema, body, ctx);
			resBody.put("_actions", data);
			res.put("body", resBody);
		} catch (Exception e) {
			logger.error("check child dead record control error .", e);
			throw new ServiceException(e);
		}
	}
	//保存字典信息
	protected void doSaveDicRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		try {
			Map<String, Object> body= (HashMap<String, Object>) req.get("body");
			if(body.containsKey("familyTeamName")){
				body.put("pyCode", BSCHISUtil.getPYSZM(body.get("familyTeamName")+""));
			}
			dao.doSave(req.get("op")+"", req.get("schema")+"",body, false);
		} catch (PersistentDataOperationException e) {
			throw new ServiceException(e);
		}
		if(req.containsKey("dcid") && !"".equals(req.get("dcid")+"")){
			DictionaryController.instance().reload(req.get("dcid")+"");
		}
	}
	@SuppressWarnings("unchecked")
	//获取团队信息
	public void doGetFamilyTeam(Map<String, Object> req,Map<String, Object> res,BaseDAO dao,Context ctx)
			throws ModelDataOperationException,
			PersistentDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String FamilyDoctorId = (String) body.get("FamilyDoctorId");
		String sql="select b.FamilyTeamId as  key,b.familyTeamName  as text"+
				" from PUB_FamilyDoctor a ,PUB_FamilyTeam b " +
				" where a.FamilyTeamId=b.FamilyTeamId and a.status='0' and b.status='0' and a.familyDoctorId=:familyDoctorId ";
		Map<String, Object> p=new HashMap<String, Object>();
		p.put("familyDoctorId",FamilyDoctorId);
		Map<String, Object> m =dao.doSqlLoad(sql, p);
		p.remove("familyDoctorId");
		p.put("key", m.get("KEY"));
		p.put("text", m.get("TEXT"));
		res.put("familyTeam", p);
	}
	//获取医生信息
	public void doGetDoctorinfo(Map<String, Object> req,Map<String, Object> res,BaseDAO dao,Context ctx)
			throws ModelDataOperationException,
			PersistentDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String doctorId = (String) body.get("DoctorId");
		String sql="select a.organizCode as ORGANIZCODE,a.mobile as MOBILE,b.organizName as ORGANIZNAME "+
				" from SYS_Personnel a ,SYS_Organization b "+ 
				" where a.organizCode=b.organizCode and a.personId=:doctorId ";
		Map<String, Object> p=new HashMap<String, Object>();
		p.put("doctorId",doctorId);
		Map<String, Object> m =dao.doSqlLoad(sql, p);
		p.remove("familyDoctorId");
		Map<String, Object> unit=new HashMap<String, Object>();
		unit.put("key", m.get("ORGANIZCODE"));
		unit.put("text", m.get("ORGANIZNAME"));
		res.put("unit", unit);
		p.put("phone", m.get("MOBILE")==null?"":m.get("MOBILE")+"");
		res.put("phone", p);
	}
	public void doGetFamilyteamId(Map<String, Object> req,Map<String, Object> res,BaseDAO dao,Context ctx)
			throws ModelDataOperationException,
			PersistentDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageunitId=user.getManageUnitId();
		String sql="select count(1) as SL from PUB_FamilyTeam where manaunitId=:manaunitId ";
		Map<String,Object> p=new HashMap<String, Object>();
		p.put("manaunitId", manageunitId);
		Map<String,Object> slmap=dao.doSqlLoad(sql, p);
		Integer temp=0;
		if(slmap!=null && slmap.size() >0 ){
			temp=Integer.parseInt(slmap.get("SL")+"");
		}
		
		String text="";
		try {
			text=DictionaryController.instance().get("chis.dictionary.familyteamcoding").getText(manageunitId);
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		if(text.length()==11){
			temp++;
			if(temp<=9){
				res.put("familyteamId", text+"0"+temp);
			}else{
				res.put("familyteamId", text+temp);
			}
		}
	}
	public void doCheckfamilyTeamName(Map<String, Object> req,Map<String, Object> res,BaseDAO dao,Context ctx)
			throws ModelDataOperationException,
			PersistentDataOperationException {
		String familyTeamName=req.get("familyTeamName")+"";
		String familyTeamId=req.get("familyTeamId")+"";
		String sql="select count(1) as SL from PUB_FamilyTeam " +
				" where familyTeamName=:familyTeamName and familyTeamId<>:familyTeamId";
		Map<String,Object> p=new HashMap<String, Object>();
		p.put("familyTeamName", familyTeamName);
		p.put("familyTeamId", familyTeamId);
		Map<String,Object> slmap=dao.doSqlLoad(sql, p);
		Integer temp=0;
		if(slmap!=null && slmap.size() >0 ){
			temp=Integer.parseInt(slmap.get("SL")+"");
		}
		if(temp>0){
			res.put("code","400");
			res.put("msg","团队名称已存在！");
		}
	}
	
	/**
	 * 
	 * @description 健康档案退回
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveVerify(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		PublicModel pm = new PublicModel(dao);
		try {
			pm.doSaveVerify(body, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	public  static void httpURLPOSTCase( String body) {
		   String methodUrl = "http://192.168.10.178:8881/apiCallLog";
		   HttpURLConnection connection = null;
		   OutputStream dataout = null;
		   BufferedReader reader = null;
		   StringBuilder result = null;
		   String line = null;
		   try {
		       URL url = new URL(methodUrl);
		       connection = (HttpURLConnection) url.openConnection();// 根据URL生成HttpURLConnection
		      connection.setDoOutput(true);// 设置是否向connection输出，因为这个是post请求，参数要放在http正文内，因此需要设为true,默认情况下是false
		      connection.setDoInput(true); // 设置是否从connection读入，默认情况下是true;
		      connection.setRequestMethod("POST");// 设置请求方式为post,默认GET请求
		      //connection.setUseCaches(false);// post请求不能使用缓存设为false
		      //connection.setConnectTimeout(3000);// 连接主机的超时时间
		      //connection.setReadTimeout(3000);// 从主机读取数据的超时时间
		      //connection.setInstanceFollowRedirects(true);// 设置该HttpURLConnection实例是否自动执行重定向
		      //connection.setRequestProperty("connection", "Keep-Alive");// 连接复用
		      connection.setRequestProperty("charset", "utf-8");
		      
		      connection.setRequestProperty("Content-Type", "application/json");
//		           connection.setRequestProperty("Authorization", "Bearer 66cb225f1c3ff0ddfdae31rae2b57488aadfb8b5e7");
//		          connection.connect();// 建立TCP连接,getOutputStream会隐含的进行connect,所以此处可以不要

		      dataout = new DataOutputStream(connection.getOutputStream());// 创建输入输出流,用于往连接里面输出携带的参数
		 //      body = "[{\"orderNo\":\"44921902\",\"adviser\":\"张怡筠\"}]";
		     // dataout.write(body.getBytes());
		      ((DataOutputStream) dataout).writeChars(body);
		     // dataout.flush();
		     // dataout.close();

		      if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
		          reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));// 发送http请求
		           result = new StringBuilder();
		          // 循环读取流
		          while ((line = reader.readLine()) != null) {
		             result.append(line).append(System.getProperty("line.separator"));//
		          }
		          System.out.println(result.toString());
		      }
		     } catch (IOException e) {
		      e.printStackTrace();
		     } 
		}
		   public static String doOrganName(String OrganCode)
					throws ModelDataOperationException{
				String sql="select organizName from SYS_Organization where organizCode=:organizCode ";
				Map<String,Object> p=new HashMap<String, Object>();
				p.put("organizCode", OrganCode);
				String organizName=null;
				Map<String, Object> slmap;
				BaseDAO dao =new BaseDAO();
				try {
					slmap = dao.doSqlLoad(sql, p);
					if(slmap!=null && slmap.size() >0 ){
						organizName=(String) slmap.get("ORGANIZNAME");
					}	
				} catch (PersistentDataOperationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return organizName;
							
			
		   }
		   
		  

		   /**
		    * 根据网络接口获取IP地址
		    * @param ethNum 网络接口名，Linux下是eth0
		    * @return
		    */
		   public static String getIpByEthNum() {
		       try {
		           Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
		           InetAddress ip;
		           while (allNetInterfaces.hasMoreElements()) {
		               NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();      
		                   Enumeration addresses = netInterface.getInetAddresses();
		                   while (addresses.hasMoreElements()) {
		                       ip = (InetAddress) addresses.nextElement();
		                       if (ip !=null  && ip instanceof Inet4Address) {
		                           return ip.getHostAddress();
		                       }
		                   }               
		           }
		       } catch (SocketException e) {
		           logger.error(e.getMessage(), e);
		       }
		       return "获取服务器IP错误";
		   }
}
