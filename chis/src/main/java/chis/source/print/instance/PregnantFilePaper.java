package chis.source.print.instance;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

import chis.source.BSCHISEntryNames;
import chis.source.PersistentDataOperationException;
import chis.source.util.BSCHISUtil;
import com.alibaba.fastjson.JSONException;

import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.DictionaryItem;
import ctd.print.PrintException;
import ctd.service.core.ServiceException;
import ctd.util.AppContextHolder;
import ctd.util.context.Context;

public class PregnantFilePaper extends PersonalFile {
	private String empiId;

	private String pregnantId;

	private Context ctx;
	protected HibernateTemplate ht;

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		pregnantId = (String) request.get("MHC_PregnantRecord.pregnantId");
		String jrxml = (String) request.get("jrxml");
		getDao(ctx);
		try {
			if (jrxml.contains("postpartum")) {
				records.addAll(getPostnatalVisitDataSource(request));
			} else if (jrxml.contains("antepartum")) {
				records.addAll(getVisitDataSource(request));
			} else if (jrxml.contains("born")) {
				records.addAll(getBornDataSource(request));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		sqlDate2String(records);
	}

	public void getParameters(Map<String, Object> request,
			Map<String, Object> parameters, Context ctx) throws PrintException {
		empiId = (String) request.get("empiId");
		pregnantId = (String) request.get("MHC_PregnantRecord.pregnantId");
		getDao(ctx);
		try {
			if (pregnantId == null) {
				pregnantId = getRecordIdByEmpiId(empiId, "pregnantId",
						BSCHISEntryNames.MHC_PregnantRecord, ctx);
				request.put("MHC_PregnantRecord.pregnantId", pregnantId);
			}
			this.ctx = ctx;
			ht = new HibernateTemplate((AppContextHolder.getBean(
					AppContextHolder.DEFAULT_SESSION_FACTORY,
					SessionFactory.class)));
			String jrxml = (String) request.get("jrxml");
			if (jrxml.contains("pregnant")) {
				parameters.putAll(getSamplePregnantMap(request));
			} else if (jrxml.contains("first")) {
				parameters.putAll(getPregnantFirstVisitMap(request));
			} else if (jrxml.contains("antepartum")) {
				parameters.putAll(getPregnantMap(request));
			} else if (jrxml.contains("postpartum42")) {
				parameters.putAll(getPregnant42Map(request));
			} else if (jrxml.contains("postpartum")) {
				parameters.putAll(getPregnantMap(request));
			} else if (jrxml.contains("born")) {
				parameters.putAll(getPregnantMap(request));
			} else if (jrxml.contains("secondfifth")) {
				parameters.putAll(getSecondfifthMap(request));
				this.doChange(parameters, 2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		sqlDate2String(parameters);
	}

	private void doChange(Map<String, Object> response, int type)
			throws PrintException, ServiceException {
		int s[]={ 2, 3, 4, 5 };
		for (int i = 0; i < s.length; i++) {
			
			if(response.get("referral"+s[i])!=null&&response.get("referral"+s[i]).equals("y"))
				response.put("referral"+s[i],"2");
			if(response.get("referral"+s[i])!=null&&response.get("referral"+s[i]).equals("n"))
				response.put("referral"+s[i],"1");
			

			if (response.get("guide" + s[i]) != null) {
				String guideX = (String) response.get("guide" + s[i]);
				String guideXs[] = guideX.split(",");

				for (int k = 0; k < guideXs.length; k++) {
						response.put("guide" + s[i] + "X" + guideXs[k], "√");

				}

			}

		}
	}

	private Map<String, Object> getSecondfifthMap(Map<String, Object> request)
			throws PrintException, ServiceException, ControllerException {
		Map<String, Object> parameters = getPregnantMap(request);
		List<Map<String, Object>> list = getBaseInfo(MHC_VisitRecord, request,
				this.ctx);
		changeHaveOrNot(list, BSCHISEntryNames.MHC_VisitRecord);
		for (int i = 2; i < list.size() + 2; i++) {
			if (i == 6) {
				break;
			}
			Map<String, Object> map = list.get(i - 2);
			Map<String, Object> m = new HashMap<String, Object>();
			Set<String> entry = map.keySet();
			Iterator<String> it = entry.iterator();
			while (it.hasNext()) {
				String kv = it.next();
				if (kv.equals("referralReason")) {
					//m.put(kv + i, "原因:" + String.valueOf(map.get(kv)));
				} else if (kv.equals("referralUnit")) {
					//m.put(kv + i, "机构及科室:" + String.valueOf(map.get(kv)));
				} else {
					m.put(kv + i, String.valueOf(map.get(kv)));
				}
				if (map.get(kv) == null) {
					m.put(kv + i, "");
				}
			}
			parameters.putAll(m);
		}
		change2String(parameters);
		return parameters;
	}

	// 孕产妇基本情况
	@SuppressWarnings("unchecked")
	private Map<String, Object> getSamplePregnantMap(Map<String, Object> map)
			throws PrintException, ServiceException {

		// 孕妇个人基本信息
		Map<String, Object> parameters = getFirstRecord(getBaseInfo(
				MPI_DemographicInfo, map, ctx));

		// 孕妇档案信息
		Map<String, Object> preMap = getFirstRecord(getBaseInfo(
				MHC_PregnantRecord, map, ctx));
		parameters.putAll(preMap);
		Integer parity1 = (Integer) parameters.get("vaginalDelivery");
		Integer parity2 = (Integer) parameters.get("abdominalDelivery");
		String parity = "";
		if (parity1 == null && parity2 == null) {
			parity = "";
		} else if (parity1 == null) {
			parity = parity2 + "";
		} else if (parity2 == null) {
			parity = parity1 + "";
		} else {
			parity = (parity1 + parity2) + "";
		}

		parameters.put("parity", parity + " 次");
		// 孕妇丈夫信息
		if (parameters.get("husbandPhrId") != null) {
			String husbandPhrId = parameters.get("husbandPhrId").toString();
			List<Map<String, Object>> phrList = ht.find(
					"from EHR_HealthRecord where phrId = ?", husbandPhrId);
			if (phrList.size() > 0) {
				Map<String, Object> phrData = phrList.get(0);
				String husbandEmpi = phrData.get("empiId").toString();
				List<Map<String, Object>> list = null;
				try {
					list = dao.doQuery(
							toListCnd("['eq',['$','a.empiId'],['s','"
									+ husbandEmpi + "']]"), "createTime desc",
							BSCHISEntryNames.MPI_DemographicInfo);
				} catch (PersistentDataOperationException e) {
					e.printStackTrace();
				}
				if (list.size() > 0) {
					Map<String, Object> data = list.get(0);
					parameters.put("husbandPersonName", data.get("personName"));
					parameters.put("husbandBirthday", data.get("birthday"));
					parameters.put("husbandIdCard", data.get("idCard"));
					if (data.get("idCard") != null) {
						parameters.put("husHasIdCard", "身份证");
					} else {
						parameters.put("husHasIdCard", "");
					}
					parameters.put("husbandWorkCode_text",
							data.get("workCode_text"));
					parameters.put("husbandEducationCode_text",
							data.get("educationCode_text"));
					parameters.put("husbandNationCode_text",
							data.get("nationCode_text"));
					parameters.put("husbandMobileNumber",
							data.get("mobileNumber"));
					parameters.put("husbandWorkPlace", data.get("workPlace"));
				}
			}
		}
		if (parameters.get("idCard") != null) {
			parameters.put("hasIdCard", "身份证");
		} else {
			parameters.put("hasIdCard", "");
		}
		change2String(parameters);
		return parameters;
	}

	// 首次随访
	@SuppressWarnings("unchecked")
	private Map<String, Object> getPregnantFirstVisitMap(Map<String, Object> map)
			throws PrintException, ServiceException, ControllerException {
		pregnantId = (String) map.get("MHC_PregnantRecord.pregnantId");
		Map<String, Object> parameters = getFirstRecord(getBaseInfo(
				MPI_DemographicInfo, map, ctx));
		parameters.putAll(getFirstRecord(getBaseInfo(MHC_PregnantRecord, map,
				ctx)));
		parameters.putAll(getFirstRecord(getBaseInfo(MHC_FirstVisitRecord, map,
				ctx)));
		changeHaveOrNot(parameters, BSCHISEntryNames.MHC_FirstVisitRecord);
		if (parameters.get("birthday") != null) {
			int age = BSCHISUtil.calculateAge(
					(Date) parameters.get("birthday"), new Date());
			parameters.put("age", String.valueOf(age));
		}
		if (parameters.get("gynecologyOPS") != null) {
			parameters.put("gynecologyOPS_key", "2");
		} else {
			parameters.put("gynecologyOPS_key", "1");
		}
		Integer vaginalDelivery = null;
		Integer abdominalDelivery = null;
		if (parameters.get("vaginalDelivery") != null) {
			vaginalDelivery = (Integer) parameters.get("vaginalDelivery");
		}
		if (parameters.get("abdominalDelivery") != null) {
			abdominalDelivery = (Integer) parameters.get("abdominalDelivery");
		}
		parameters.put("parity", "阴道分娩 " + vaginalDelivery == null ? ""
				: vaginalDelivery + " 次  剖宫产 " + abdominalDelivery == null ? ""
						: abdominalDelivery + " 次");
		if (parameters.get("lastMenstrualPeriod") == null) {
			parameters.put("lastMenstrualPeriod", "不详");
		}
		if (parameters.get("husbandPhrId") != null) {
			String husbandPhrId = parameters.get("husbandPhrId").toString();
			List<Map<String, Object>> phrList = ht.find(
					"from EHR_HealthRecord where phrId = ?", husbandPhrId);
			if (phrList.size() > 0) {
				Map<String, Object> phrData = phrList.get(0);
				String husbandEmpi = phrData.get("empiId").toString();
				List<Map<String, Object>> list = null;
				try {
					list = dao.doQuery(
							toListCnd("['eq',['$','a.empiId'],['s','"
									+ husbandEmpi + "']]"), "createTime desc",
							BSCHISEntryNames.MPI_DemographicInfo);
				} catch (PersistentDataOperationException e) {
					e.printStackTrace();
				}
				if (list.size() > 0) {
					Map<String, Object> data = list.get(0);
					parameters.put("husbandName", data.get("personName"));
					Date birthday = (Date) data.get("birthday");
					Date today = new Date();
					int age = BSCHISUtil.calculateAge(birthday, today);
					String a = String.valueOf(age);
					parameters.put("husbandAge", a);
					parameters.put("husbandPhone", data.get("mobileNumber"));
				}
			}
		}
		// 流产合并
		String trafficFlow = "";
		try {
			trafficFlow = parameters.get("trafficFlow").toString();// 人流
		} catch (NullPointerException e) {
			trafficFlow = "0";
		}

		String naturalAbortion = "";
		try {
			naturalAbortion = parameters.get("naturalAbortion").toString();// 自然流
		} catch (NullPointerException e) {
			naturalAbortion = "0";
		}

		String qweTimes = "";
		try {
			qweTimes = parameters.get("qweTimes").toString();// 药流
		} catch (NullPointerException e) {
			qweTimes = "0";
		}
		int t = Integer.valueOf(trafficFlow).intValue();
		int n = Integer.valueOf(naturalAbortion).intValue();
		int q = Integer.valueOf(qweTimes).intValue();
		int abortion = t + n + q;
		parameters.put("abortion", abortion);

		Dictionary index = DictionaryController.instance().get(
				"chis.dictionary.pregnantIndex");
		List<DictionaryItem> l = index.itemsList();
		List<Map<String, Object>> ls = getBaseInfo(MHC_PregnantWomanIndex, map,
				ctx);
		for (Iterator<DictionaryItem> dit = l.iterator(); dit.hasNext();) {
			DictionaryItem i = dit.next();
			parameters.put(i.getKey(), "");
			for (Iterator<Map<String, Object>> it = ls.iterator(); it.hasNext();) {
				Map<String, Object> d = it.next();
				if (d.get("indexName") == null
						|| !i.getText().endsWith((String) d.get("indexName")))
					continue;
				String ex = "", ex_o = "";
				String code = (String) d.get("indexCode");
				if (d.get("ifException") != null) {
					if (d.get("ifException").equals("2") || code.equals("508")) {
						ex = "2";
						if (d.get("exceptionDesc") != null) {
							ex_o = d.get("exceptionDesc").toString();
						}
					} else if (d.get("ifException").equals("1")) {
						ex = "1";
					}
				}
				String[] codes = { "50501", "50502", "50503", "51001", "51002",
						"51003", "51004", "51005" };
				String indexValue = "";
				if (d.get("indexValue") != null) {
					indexValue = (String) d.get("indexValue");
					for (String s : codes) {
						if (s.equals(code)) {
							indexValue = (String) d.get("indexValue_text");
							break;
						}
					}
				}
				parameters.put(i.getKey() + "_ex", ex);
				parameters.put(i.getKey() + "_ex_o", ex_o);
				parameters.put(i.getKey(), indexValue);
				break;
			}
		}
		if (parameters.get("512") != null && !parameters.get("512").equals("1")) {
			parameters.put("512", "2");
		}
		if (parameters.get("511") != null && !parameters.get("511").equals("1")) {
			parameters.put("511", "2");
		}
		change2String(parameters);
		return parameters;
	}

	// 产后42天健康检查记录
	private Map<String, Object> getPregnant42Map(Map<String, Object> map)
			throws PrintException, ServiceException {
		pregnantId = (String) map.get("MHC_PregnantRecord.pregnantId");
		empiId = (String) map.get("empiId");
		Map<String, Object> parameters = new HashMap<String, Object>();
		if (pregnantId != null) {
			List<Map<String, Object>> list = null;
			try {
				list = dao.doList(toListCnd("['eq',['$','a.empiId'],['s','"
						+ empiId + "']]"), "a.checkDate desc",
						BSCHISEntryNames.MHC_Postnatal42dayRecord);
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
			}
			if (list.size() > 0) {
				parameters.putAll(list.get(0));
			}
		}

		// 随访日期 进行年月日特殊处理
		if (parameters.get("checkDate") != null) {
			Date checkDate = BSCHISUtil.toDate(parameters.get("checkDate")
					.toString());
			Calendar now = Calendar.getInstance();
			if (checkDate != null) {
				now.setTime(checkDate);
			}
			parameters.put("checkDate_year", now.get(Calendar.YEAR));
			parameters.put("checkDate_month", now.get(Calendar.MONTH) + 1);
			parameters.put("checkDate_day", now.get(Calendar.DATE));
		}
		change2String(parameters);
		return parameters;
	}

	/**
	 * 孕妇信息
	 * 
	 * @param map
	 * @param sharedData
	 * @return
	 * @throws PrintException
	 * @throws ServiceException
	 * @throws JSONException
	 */
	protected Map<String, Object> getPregnantMap(Map<String, Object> map)
			throws PrintException, ServiceException {

		// 孕妇个人基本信息
		Map<String, Object> parameters = getFirstRecord(getBaseInfo(
				MPI_DemographicInfo, map, ctx));

		// 孕妇档案信息
		Map<String, Object> preMap = getFirstRecord(getBaseInfo(
				MHC_PregnantRecord, map, ctx));
		parameters.putAll(preMap);

		return parameters;
	}

	/**
	 * 孕妇产后信息记录
	 * 
	 * @param map
	 * @param design
	 * @param sharedData
	 * @return
	 * @throws PrintException
	 * @throws ServiceException
	 * @throws ControllerException
	 * @throws JSONException
	 */
	public List<Map<String, Object>> getPostnatalVisitDataSource(
			Map<String, Object> map) throws PrintException, ServiceException,
			ControllerException {
		List<Map<String, Object>> list = getBaseInfo(MHC_PostnatalVisitInfo,
				map, ctx);
		changeHaveOrNot(list, BSCHISEntryNames.MHC_PostnatalVisitInfo);
		for (Map<String, Object> m : list) {
			if (m != null) {
				if (m.get("referral") != null && m.get("referral").equals("y")) {
					m.put("referral", "2");
				} else if (m.get("referral") != null
						&& m.get("referral").equals("n")) {
					m.put("referral", "1");
				}
			}
		}
		if (list.size() == 0) {
			Map<String, Object> m = null;
			fillData(m, BSCHISEntryNames.MHC_PostnatalVisitInfo);
			list.add(m);
		}
		// List<Map<String, Object>> dataList = new ArrayList<Map<String,
		// Object>>();
		// int dataSize = list.size();
		// int listSize = dataSize % 4 == 0 ? dataSize / 4 : (int) Math
		// .floor(dataSize / 4) + 1;
		// for (int i = 0; i < listSize; i++) {
		// int count = 0;
		// Map<String, Object> subMap = new HashMap<String, Object>();
		// for (int j = i * 4 + 1; j < i * 4 + 5; j++) {
		// count++;
		// if (j <= dataSize) {
		// Map<String, Object> record = list.get(j - 1);
		// Iterator<?> it = record.keySet().iterator();
		// while (it.hasNext()) {
		// String s = (String) it.next();
		// if (s.equalsIgnoreCase("infectant")) {
		// String value = record.get(s).toString();
		// if (value.equals("1")) {
		// subMap.put("ifException" + count, "异常");
		// } else
		// subMap.put("ifException" + count, "正常");
		// }
		// subMap.put(s + count, record.get(s));
		// }
		// }
		// }
		// dataList.add(subMap);
		// }

		return list;
	}

	/**
	 * 孕妇随访信息记录
	 * 
	 * @param map
	 * @param design
	 * @param sharedData
	 * @return
	 * @throws PrintException
	 * @throws ServiceException
	 * @throws JSONException
	 */
	public List<Map<String, Object>> getVisitDataSource(Map<String, Object> map)
			throws PrintException, ServiceException {
		List<Map<String, Object>> list = getBaseInfo(MHC_VisitRecord, map, ctx);
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> subMap = new HashMap<String, Object>();
			subMap.put("times", i + 1);
			Map<String, Object> record = list.get(i);
			Iterator<?> it = record.keySet().iterator();
			String sbp = "";
			String dbp = "";
			String doctor = "";
			while (it.hasNext()) {
				String s = (String) it.next();
				if (s.equalsIgnoreCase("sbp")) {
					sbp = record.get(s).toString();
				} else if (s.equalsIgnoreCase("dbp")) {
					dbp = record.get(s).toString();
				} else if (s.equalsIgnoreCase("doctorId_text")) {
					doctor = record.get(s).toString();
				}
				subMap.put(s, record.get(s));
			}
			subMap.put("bp", sbp + "/" + dbp);
			subMap.put("doctorName", doctor);
			dataList.add(subMap);
		}
		return dataList;
	}

	// 新生儿访视
	protected List<Map<String, Object>> getBornDataSource(
			Map<String, Object> requestData) throws PrintException,
			ServiceException, ControllerException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> visitData = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<String> babyList = getBabyId(requestData, pregnantId);// get babyId
		if (babyList.size() == 0) {
			fillData(parameters, BSCHISEntryNames.MHC_BabyVisitInfo);
			fillData(parameters, BSCHISEntryNames.MHC_BabyVisitRecord);
			list.add(parameters);
		} // from // MHC_BabyVisitInfo
		for (int i = 0; i < babyList.size(); i++) {
			String babyId = babyList.get(i);
			ctx.put("babyId", babyId);
			parameters = getFirstRecord(getBaseInfo(MHC_BabyVisitInfo,
					requestData, ctx));
			List<String> visitList = getVisitId(requestData, babyId);// get
																		// visitId
																		// from
																		// MHC_BabyVisitRecord
			for (int j = 0; j < visitList.size(); j++) {
				String visitId = visitList.get(j);
				ctx.put("visitId", visitId);
				requestData.put("visitId", visitId);

				visitData = getFirstRecord(getBaseInfo(MHC_BabyVisitRecord,
						requestData, ctx));
				if (visitData.size() > 0) {
					visitData.put("weight1", visitData.get("weight"));
					parameters.putAll(visitData);
				}
				changeDic(parameters);
				list.add(parameters);
			}
			if (visitList.size() == 0) {
				changeDic(parameters);
				fillData(parameters, BSCHISEntryNames.MHC_BabyVisitRecord);
				list.add(parameters);
			}
		}
		return list;
	}

	private void changeDic(Map<String, Object> parameters)
			throws ControllerException {
		String[] strs = { "eye", "ear", "mouse", "abdominal" };
		for (String s : strs) {
			if (parameters.containsKey(s) && parameters.get(s) != null) {
				if (!parameters.get(s).equals("1")) {
					parameters.put(s, "2");
				}
			}
		}
		if (parameters.containsKey("skin") && parameters.get("skin") != null) {
			if (parameters.get("skin").equals("07")) {
				parameters.put("skin", "2");
			} else if (parameters.get("skin").equals("08")) {
				parameters.put("skin", "3");
			} else if (parameters.get("skin").equals("99")) {
				parameters.put("skin", "4");
			} else if (parameters.get("skin").equals("01")) {
				parameters.put("skin", "1");
			} else {
				parameters.put("skin", "");
			}
		}
		if (parameters.containsKey("umbilical")
				&& parameters.get("umbilical") != null) {
			if (parameters.get("umbilical").equals("9")) {
				parameters.put("umbilical", "4");
			}
		}
		if (parameters.containsKey("jaundice")
				&& parameters.get("jaundice") != null) {
			if (parameters.get("jaundice").equals("9")
					|| parameters.get("jaundice").equals("5")) {
				parameters.put("jaundice", "");
			}
		}
		if (parameters.containsKey("birthStatus")
				&& parameters.get("birthStatus") != null) {
			if (parameters.get("birthStatus").equals("7")) {
				parameters.put("birthStatus", "");
			}
		}
		if (parameters.containsKey("vomit") && parameters.get("vomit") != null) {
			if (parameters.get("vomit").equals("1")) {
				parameters.put("vomit", "2");
			} else if (parameters.get("vomit").equals("2")) {
				parameters.put("vomit", "1");
			}
		}
		List<String> scs = new ArrayList<String>();
		scs.add(BSCHISEntryNames.MHC_BabyVisitInfo);
		scs.add(BSCHISEntryNames.MHC_BabyVisitRecord);
		changeHaveOrNot(parameters, scs);

	}

	// search babyId by pregnantId
	@SuppressWarnings("unchecked")
	public List<String> getBabyId(Map<String, Object> map, String pregnantId) {
		String sql = new StringBuffer(
				"select babyId from MHC_BabyVisitInfo where pregnantId = :pregnantId")
				.toString();
		Session session = AppContextHolder.getBean(
				AppContextHolder.DEFAULT_SESSION_FACTORY, SessionFactory.class)
				.openSession();
		Query query = session.createQuery(sql);
		query.setString("pregnantId", pregnantId);
		List<String> list = (List<String>) query.list();
		session.close();
		return list;
	}

	// search visitId by babyId
	@SuppressWarnings("unchecked")
	public List<String> getVisitId(Map<String, Object> map, String babyId) {
		String sql = new StringBuffer(
				"select visitId from MHC_BabyVisitRecord where babyId = :babyId")
				.toString();
		Session session = this.getSessionFactory(ctx).openSession();
		Query query = session.createQuery(sql);
		query.setString("babyId", babyId);
		List<String> list = (List<String>) query.list();
		session.close();
		return list;
	}
}