/**
 * @(#)PublicService.java Created on 2012-1-5 上午11:40:38
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package phis.application.pub.source;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BSPHISEntryNames;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.Constants;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.message.Message;
import phis.source.message.MsgClient;
import phis.source.message.MsgManager;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import phis.source.utils.BCLUtil;
import phis.source.utils.BSHISUtil;
import phis.source.utils.CNDHelper;
import phis.source.utils.ParameterUtil;
import com.alibaba.fastjson.JSONException;
import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;
import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.util.JSONProtocol;
import ctd.util.annotation.RpcService;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;

/**
 * @description
 * 
 * @author <a href="mailto:huangpf@bsoft.com.cn">huangpf</a>
 */
public class PublicService extends AbstractActionService implements
		DAOSupportable {

	static Logger logger = LoggerFactory.getLogger(PublicService.class);

	/**
	 * 发送消息
	 * 
	 * @author YangL 2015-5-27 下午2:25:05
	 */
	@SuppressWarnings("unchecked")
	public void doSendMessage(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		Message msg = new Message();
		if(body.get("body") != null) {
			msg.setBody(body.get("body").toString());
		}
		if(body.get("businessId") != null) {
			msg.setBusinessId(body.get("businessId") + "");
		}
		if(body.get("title") != null) {
			msg.setTitle(body.get("title") + "");
		}
		if(body.get("remainMode") != null) {
			msg.setRemindMode(body.get("remainMode") + "");
		}
		if(body.get("keywords") != null) {
			msg.setKeywords((Map<String,Object>)body.get("keywords"));
		}
		MsgManager.instance().add(msg);
	}
	
	/**
	 * 消息监听 默认20秒长轮询 ,关闭客户端后,20秒移除
	 */
	public void doMessageListener(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) {
		UserRoleToken user = UserRoleToken.getCurrent();
		// String roleId = user.getRoleId();
		String qyxxxt = ParameterUtil.getParameter(
				ParameterUtil.getTopUnitId(), BSPHISSystemArgument.QYXXXT, ctx);
		if ("1".equals(qyxxxt)) {
			MsgManager msgMgmt = MsgManager.instance();
			// if (msgMgmt.hasSubscribe(roleId)) {
			HttpSession httpSession = ((HttpServletRequest) ContextUtils
					.get(Context.HTTP_REQUEST)).getSession(false);
			MsgClient client = new MsgClient();
			client.setUser(user);
			// client.setRole(msgMgmt.getSubScribe(roleId));
			msgMgmt.addClient(httpSession.getId(), client);
			// System.out.println("---------当前连接的客户端个数："
			// + msgMgmt.getClients().size());
			Message msg = client.getMsg();// 此处会持续等待最多20秒获取信息，若20秒无法获取信息，则返回空信息
			res.put("message", msg);
			msgMgmt.removeClient(httpSession.getId());
			client = null;
			res.put(JSONProtocol.CODE, 200);
			res.put(JSONProtocol.MSG, "success");
			// } else {
			// res.put(JSONProtocol.CODE, 501);
			// res.put(JSONProtocol.MSG, "None Subscribe!");
			// }
		} else {
			res.put(JSONProtocol.CODE, 502);
			res.put(JSONProtocol.MSG, "Feature is not enabled!");
		}
	}

	@SuppressWarnings("unchecked")
	/**
	 * 病历锁功能,支持单业务或者数组方式
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doBclLock(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> lockKeys = new HashMap<String, Object>();
		Object body = req.get("body");
		if (body == null)
			return;
		Session session = (Session) ctx.get(Context.DB_SESSION);
		try {
			session.beginTransaction();
			if (body instanceof List) {
				for (Map<String, Object> m : (List<Map<String, Object>>) body) {
					Map<String, Object> r = BCLUtil.lock(m, dao);
					if (r != null) {
						lockKeys.put(m.get("YWXH").toString(), r.get("JLXH"));
					}
				}
			} else {
				Map<String, Object> r = BCLUtil.lock(
						(Map<String, Object>) body, dao);
				if (r != null) {
					lockKeys.put(((Map<String, Object>) body).get("YWXH")
							.toString(), r.get("JLXH"));
				}
			}
			session.getTransaction().commit();
			res.put("body", lockKeys);
		} catch (ModelDataOperationException e) {
			session.getTransaction().rollback();
			throw new ServiceException(e);
		} catch (Exception e) {
			session.getTransaction().rollback();
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doBclUnlock(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Object body = req.get("body");
		if (body == null)
			return;
		try {
			if (body instanceof List) {
				for (Map<String, Object> m : (List<Map<String, Object>>) body) {
					BCLUtil.unlock(m, dao);
				}
			} else {
				BCLUtil.unlock((Map<String, Object>) body, dao);
			}
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doCheckBclLock(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> resBody = new HashMap<String, Object>();
		Object body = req.get("body");
		if (body == null)
			return;
		try {
			if (body instanceof List) {
				for (Map<String, Object> m : (List<Map<String, Object>>) body) {
					resBody.put(m.get("YWXH").toString(),
							BCLUtil.checkLock(m, dao));
				}
			} else {
				resBody.put(
						((Map<String, Object>) body).get("YWXH").toString(),
						BCLUtil.checkLock((Map<String, Object>) body, dao));

			}
			res.put("body", resBody);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取系统参数
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadSystemParams(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		Map<String, Object> retMap = new HashMap<String, Object>();
		List<String> commons = body.containsKey("commons") ? (List<String>) body
				.get("commons") : null;
		List<String> privates = body.containsKey("privates") ? (List<String>) body
				.get("privates") : null;
		List<String> personals = body.containsKey("personals") ? (List<String>) body
				.get("personals") : null;
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();
		if (commons != null) {
			for (String _CSMC : commons) {
				retMap.put(_CSMC, ParameterUtil.getParameter(
						ParameterUtil.getTopUnitId(), _CSMC, ctx));
			}
		}
		if (privates != null) {
			for (String _CSMC : privates) {
				retMap.put(_CSMC,
						ParameterUtil.getParameter(manageUnit, _CSMC, ctx));
			}
		}
		if (personals != null) {
			List<Object> cnd = CNDHelper.createSimpleCnd("eq", "YHBH", "s",
					user.getUserId());
			body = null;
			try {
				Map<String, Object> ysxg = dao.doLoad(cnd,
						BSPHISEntryNames.EMR_YSXG_GRCS);
				if (ysxg != null) {
					retMap.putAll(ysxg);
				}
			} catch (PersistentDataOperationException e) {
				throw new ServiceException("加载医生个人习惯失败!", e);
			}
		}
		res.put("body", retMap);
	}

	/**
	 * 保存系统参数分类信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doSaveSystemParamsType(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		// Map<String, Object> body = (Map<String, Object>) req.get("body");
		// PublicModel pm = new PublicModel();

		// pm.doSaveSystemParamsType(body,ctx);
	}

	/**
	 * 
	 */
	public List<String> getNoDBActions() {
		List<String> list = new ArrayList<String>();
		list.add("getCurrentDate");
		list.add("calculateAge");
		list.add("personAge");
		list.add("getServerDate");
		list.add("getServerDateTime");
		list.add("reloadDictionarys");
		return list;
	}

	/**
	 * 重载字典功能
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 */
	@SuppressWarnings("unchecked")
	public void doReloadDictionarys(Map<String, Object> req,
			Map<String, Object> res, Context ctx) {
		List<String> body = (List<String>) req.get("body");
		if (body == null || body.size() <= 0) {
			DictionaryController.instance().reloadAll();
		} else {
			for (String dicId : body) {
				DictionaryController.instance().reload(dicId);
			}
		}
	}

	/**
	 * 计算人员年龄
	 * 
	 * @param req
	 * @param res
	 * @param sc
	 * @param ctx
	 * @throws JSONException
	 */
	// 新模式:
	// 年龄大于等于3*12个月的，用岁表示；
	// 小于3*12个月而又大于等于1*12个月的，用岁月表示；
	// 小于12个月而又大于等于6个月的，用月表示；
	// 小于6个月而大于等于29天的，用月天表示；
	// 大于72小时小于29天的，用天表示；
	// 小于72小时的，用小时表示。

	public void doPersonAge(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> body = (HashMap<String, Object>) req
				.get("body");
		String birthdayStr = (String) body.get("birthday");
		// "2014-04-16T16:00:00.000Z"
		birthdayStr = birthdayStr.substring(0, 10) + " "
				+ birthdayStr.substring(11, 19);
		DateFormat f = new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT);
		Date birthday = null;
		try {
			birthday = f.parse(birthdayStr);
			birthday.setTime(birthday.getTime() + (1000 * 60 * 60 * 8));
		} catch (ParseException e) {
			logger.error("无法识别的时间格式:{}", new Object[] { birthdayStr, e });
			res.put(RES_CODE, Constants.CODE_INVALID_REQUEST);
			res.put(RES_MESSAGE, "无法识别的时间格式：" + birthdayStr);
			return;
		}
		PublicModel pm = new PublicModel();
		pm.doPersonAge(birthday, res);
	}

	// 小于3*12个月而又大于等于1*12个月的，用岁月表示；

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
		int age = BSHISUtil.calculateAge(birthday, null);
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
		return BSHISUtil.calculateAge(birthday, calculateDate);
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

	/**
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 */
	@RpcService
	public void doGetServerDate(Map<String, Object> req,
			Map<String, Object> res, Context ctx) {
		res.put("date", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
	}

	/**
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 */
	@RpcService
	public void doGetServerDateTime(Map<String, Object> req,
			Map<String, Object> res, Context ctx) {
		res.put("dateTime",
				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
	}

	/**
	 * 得到几天后的时间
	 * 
	 * @param d
	 * @param day
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public void doGetDateAfter(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		Calendar now = Calendar.getInstance();
		try {
			now.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(body
					.get("date") + ""));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		now.set(Calendar.DATE,
				now.get(Calendar.DATE) + Integer.parseInt(body.get("day") + ""));
		res.put("date",
				new SimpleDateFormat("yyyy-MM-dd").format(now.getTime()));
	}

	/**
	 * 得到几天后的时间 带时分秒 zhaojian 2017-11-14
	 * 
	 * @param day
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public void doGetDateTimeAfter(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		Calendar now = Calendar.getInstance();
		try {
			now.setTime(new Date());
		} catch (Exception e) {
			e.printStackTrace();
		}
		now.set(Calendar.DATE,
				now.get(Calendar.DATE) + Integer.parseInt(body.get("day") + ""));
		res.put("date",
				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now.getTime()));
	}

	/**
	 * 
	 * @Description:获取需要访问社区系统的诊断信息（疾病编码、病症报告卡）
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2013-12-12 下午4:04:07
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doGetDiagnosisInfoForCHIS(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String empiId = (String) reqBodyMap.get("empiId");
		String JZXH = (String) reqBodyMap.get("JZXH");
		String hql = new StringBuffer(
				"select c.JBPB as JBPB,c.JBBGK as JBBGK from ")
				.append(" GY_JBBM ")
				.append(" c where c.jbxh in (select b.ZDXH from ")
				.append(" MS_BRZD ")
				.append(" b where b.BRID in (select a.BRID from ")
				.append(" MS_BRDA ").append(" a where a.EMPIID=:empiId))")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		List<Map<String, Object>> GXYL = null;
		try {
			GXYL = dao.doSqlQuery(hql, parameters);
		} catch (PersistentDataOperationException e) {
			logger.error("Failed to select ", e);
			throw new ServiceException(e);
		}
		boolean GXY = false;
		boolean TLB = false;
		boolean BGK = false;
		List<String> thqmList = new ArrayList<String>();
		String THQM = "03,04,05,06,07,08";
		for (int i = 0; i < GXYL.size(); i++) {
			Map<String, Object> map = GXYL.get(i);
			if (map.get("JBPB") != null) {
				String JBPB = (String) map.get("JBPB");
				if (JBPB.indexOf("01") >= 0) {
					GXY = true;
				} else if (JBPB.indexOf("02") >= 0) {
					TLB = true;
				}
				if (THQM.indexOf(JBPB) >= 0) {
					thqmList.add(JBPB);
				}
			}
			if (map.get("JBBGK") != null
					&& (map.get("JBBGK") + "").indexOf("06") >= 0) {
				BGK = true;
			}
		}
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		resBodyMap.put("GXY", GXY);
		resBodyMap.put("TLB", TLB);
		resBodyMap.put("BGK", BGK);
		resBodyMap.put("thqmList", thqmList);
		if (JZXH != null && !"undefined".equals(JZXH)) {
			try {
				Map<String, Object> map = dao.doLoad(BSPHISEntryNames.MS_BCJL,
						Long.parseLong(JZXH));
				if (map != null) {
					resBodyMap.put("BMI", map.get("BMI"));
				}
			} catch (NumberFormatException e) {
				logger.error("Failed to select ", e);
				throw new ServiceException(e);
			} catch (PersistentDataOperationException e) {
				logger.error("Failed to select ", e);
				throw new ServiceException(e);
			}
		}
		// 取门诊病历中血压值
		// boolean HYSC = false;
		// String strJZXH = (String)reqBodyMap.get("JZXH");
		// if(StringUtils.isNotEmpty(strJZXH.trim())){
		// long JZXH = Long.parseLong(strJZXH);
		// Map<String, Object> rsMap = null;
		// try {
		// rsMap = dao.doLoad(MS_BCJL, JZXH);
		// } catch (PersistentDataOperationException e) {
		// logger.error("Failed to select MS_BCJL ", e);
		// throw new ServiceException(e);
		// }
		// if(rsMap != null && rsMap.size() > 0){
		// Integer objSSY = (Integer) rsMap.get("SSY");
		// Integer objSZY = (Integer) rsMap.get("SZY");
		// int ssy = objSSY == null ? 0 : objSSY.intValue();
		// int szy = objSZY == null ? 0 : objSZY.intValue();
		// if(ssy >= 140 || szy >= 90){
		// HYSC = true;
		// }
		// }
		// }
		// resBodyMap.put("HYSC", HYSC);
		res.put("body", resBodyMap);
	}
	
	public void doSelectlrjkbk(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		int ksdm = (Integer) req.get("ksdm");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ksdm", ksdm);
		try {
			List<Map<String, Object>> list = dao.doSqlQuery(
					" SELECT LRJKBK as LRJKBK from MS_GHKS where ksdm=:ksdm",
					parameters);
			res.put("LRJKBK", list.get(0).get("LRJKBK"));
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}
	
	public void doVisitCountLogForInterface(Map<String, Object> req,
					Map<String, Object> res, BaseDAO dao, Context ctx)
					throws ServiceException {
				Map<String, Object> body = (Map<String, Object>) req.get("body");
				PublicModel model = new PublicModel(dao);
				try {
					model.visitCountLogForInterface(body,res,ctx);
				} catch (ModelDataOperationException e) {
					throw new ServiceException("调用第三方接口实现次数统计失败！", e);
				}
			}

	/**
	 * 
	 * @Description:待诊病人双击后调用市健康档案浏览器调阅接口（统计次数）
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author zhaojian 2019-08-22
	 * @Modify:
	 */
	public void doVisitCountForCityInterface(Map<String, Object> req,
					Map<String, Object> res, BaseDAO dao, Context ctx)
					throws ServiceException {
				Map<String, Object> body = (Map<String, Object>) req.get("body");
				PublicModel model = new PublicModel(dao);
				try {
					model.doVisitCountForCityInterface(body,res,ctx);
				} catch (Exception e) {
					throw new ServiceException("待诊病人双击后调用市健康档案浏览器调阅接口（统计次数）失败！", e);
				}
			}
}
