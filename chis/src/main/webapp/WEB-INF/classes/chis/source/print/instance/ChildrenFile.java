/**
 * @(#)ChildrenRecordReportFile.java Created on 10:10:01 AM
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.print.instance;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;

import chis.source.BSCHISEntryNames;
import chis.source.Constants;
import chis.source.PersistentDataOperationException;
import chis.source.dic.BusinessType;
import chis.source.print.base.BSCHISPrint;
import chis.source.util.BSCHISUtil;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yaozh@bsoft.com.cn">yao zhenhua</a>
 */
public class ChildrenFile extends BSCHISPrint implements IHandler {

	private String empiId;

	private Context ctx;

	private String phrId;

	private String accidentId;

	private String deadRegisterId;

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		empiId = (String) request.get("empiId");
		phrId = (String) request.get("phrId");
		getDao(ctx);
		this.ctx = ctx;
		String jrxml = (String) request.get("jrxml");
		try {
			if (jrxml.contains("checkupOne")) {
				response.putAll(this.getOneMap(phrId, request, ctx));
			} else if (jrxml.contains("checkupTwo")) {
				response.putAll(this.getTwoMap(phrId, request, ctx));
			} else if (jrxml.contains("checkupThree")) {
				response.putAll(this.getThreeMap(phrId, request, ctx));
			} else if (jrxml.contains("0-6")) {
				response.putAll(getSample06Map(request, ctx));
			} else if (jrxml.contains("onethreesummary")) {
				response.putAll(getSummaryMap(request, ctx));
			} else if (jrxml.contains("thick")) {
				response.putAll(getDeblilityMap(request, ctx));
			} else if (jrxml.contains("deadRegister")) {
				deadRegisterId = (String) request.get("deadRegisterId");
				response.putAll(getDeadRegisterMap(request, ctx));
			} else if (jrxml.contains("accident")) {
				accidentId = (String) request.get("accidentId");
				response.putAll(getAccidentMap(request, ctx));
			} else if (jrxml.contains("0-3DisabilityChildrenMonitor")) {
				response.putAll(get03DisabilityMonitorMap(request, ctx));
			}
		} catch (Exception e) {
			throw new PrintException(Constants.CODE_PERSISTENT_ERROR,
					e.getMessage());
		}
		sqlDate2String(response);
	}

	private Map<String, Object> getAccidentMap(Map<String, Object> map,
			Context ctx) throws PrintException, ServiceException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> accMap = getFirstRecord(getBaseInfo(CDH_Accident,
				map, ctx));
		parameters.putAll(accMap);
		if (parameters.get("phrId") != null) {
			String phrId = parameters.get("phrId").toString();
			List<Map<String, Object>> list = null;
			try {
				list = dao.doQuery(toListCnd("['eq',['$','a.phrId'],['s','"
						+ phrId + "']]"), "a.phrId desc",
						BSCHISEntryNames.EHR_HealthRecord);
				if (list.size() > 0) {
					Map<String, Object> maps = list.get(0);
					List<Map<String, Object>> mpilist = dao.doQuery(
							toListCnd("['eq',['$','a.empiId'],['s','"
									+ maps.get("empiId") + "']]"),
							"a.createTime desc",
							BSCHISEntryNames.MPI_DemographicInfo);
					parameters.putAll(mpilist.get(0));
					parameters.putAll(maps);
				}
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
			}
		}

		if (parameters.get("accidentDate") != null) {
			Date accidentDate = (Date) parameters.get("accidentDate");
			Date birth = (Date) parameters.get("birthday");
			long m = accidentDate.getTime() - birth.getTime();
			int time = (int) (m / (1000 * 60 * 60 * 24));
			int year = (int) Math.floor(time / 365);
			int moth = (int) Math.floor(time % 365 / 30);
			int day = (int) Math.floor(time % 365 % 30);
			parameters.put("accidentYear", year + "年" + moth + "月" + day + "天");
		}

		if (parameters.get("motherEmpiId") != null) {
			String motherEmpiId = parameters.get("motherEmpiId").toString();
			List<Map<String, Object>> list = null;
			try {
				list = dao.doQuery(toListCnd("['eq',['$','a.empiId'],['s','"
						+ motherEmpiId + "']]"), "a.createTime desc",
						BSCHISEntryNames.MPI_DemographicInfo);
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
			}

			if (list.size() > 0) {
				Map<String, Object> data = list.get(0);
				parameters.put("motherName", data.get("personName"));
			}
		}

		if (parameters.get("fatherEmpiId") != null) {
			String fatherEmpiId = parameters.get("fatherEmpiId").toString();
			List<Map<String, Object>> list = null;
			try {
				list = dao.doQuery(toListCnd("['eq',['$','a.empiId'],['s','"
						+ fatherEmpiId + "']]"), "a.createTime desc",
						BSCHISEntryNames.MPI_DemographicInfo);
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
			}
			if (list.size() > 0) {
				Map<String, Object> data = list.get(0);
				parameters.put("fatherName", data.get("personName"));
			}
		}
		return parameters;
	}

	private Map<String, Object> getDeadRegisterMap(Map<String, Object> map,
			Context ctx) throws PrintException, ServiceException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> deadMap = getFirstRecord(getBaseInfo(
				CDH_DeadRegister, map, ctx));
		parameters.putAll(deadMap);

		HashMap<String, Object> jsonReq = new HashMap<String, Object>();
		jsonReq.put("schema", BSCHISEntryNames.MPI_DemographicInfo);

		if (parameters.get("motherEmpiId") != null) {
			String motherEmpiId = parameters.get("motherEmpiId").toString();
			List<Map<String, Object>> list = null;
			try {
				list = dao.doQuery(toListCnd("['eq',['$','a.empiId'],['s','"
						+ motherEmpiId + "']]"), "a.createTime desc",
						BSCHISEntryNames.MPI_DemographicInfo);
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
			}
			if (list.size() > 0) {
				Map<String, Object> data = list.get(0);
				parameters.put("motherName", data.get("personName"));
			}
		}

		if (parameters.get("fatherEmpiId") != null) {
			String fatherEmpiId = parameters.get("fatherEmpiId").toString();
			List<Map<String, Object>> list = null;
			try {
				list = dao.doQuery(toListCnd("['eq',['$','a.empiId'],['s','"
						+ fatherEmpiId + "']]"), "a.createTime desc",
						BSCHISEntryNames.MPI_DemographicInfo);
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
			}
			if (list.size() > 0) {
				Map<String, Object> data = list.get(0);
				parameters.put("fatherName", data.get("personName"));
			}
		}
		return parameters;
	}

	private Map<String, Object> getOneMap(String phrId,
			Map<String, Object> map, Context ctx) throws PrintException,
			ServiceException {
		Map<String, Object> parameters = getFirstRecord(getBaseInfo(
				MPI_DemographicInfo, map, ctx));
		parameters.put("phrId", phrId);
		String exp = "['and',['eq',['$','a.phrId'],['s','"
				+ phrId
				+ "']],['or',['eq',['$','checkupStage'],['s','1']],['eq',['$','checkupStage'],['s','3']],['eq',['$','checkupStage'],['s','6']],['eq',['$','checkupStage'],['s','9']]]]";
		this.getCheckupMapValue(exp, parameters,
				BSCHISEntryNames.CDH_CheckupInOne);
		this.getInquireMapValue("1", "9", parameters);
		change2String(parameters);
		return parameters;
	}

	private Map<String, Object> getTwoMap(String phrId,
			Map<String, Object> map, Context ctx) throws PrintException,
			ServiceException {
		Map<String, Object> parameters = getFirstRecord(getBaseInfo(
				MPI_DemographicInfo, map, ctx));
		parameters.put("phrId", phrId);
		String exp = "['and',['eq',['$','a.phrId'],['s','"
				+ phrId
				+ "']],['or',['eq',['$','checkupStage'],['s','12']],['eq',['$','checkupStage'],['s','18']],['eq',['$','checkupStage'],['s','24']],['eq',['$','checkupStage'],['s','30']]]]";
		this.getCheckupMapValue(exp, parameters,
				BSCHISEntryNames.CDH_CheckupOneToTwo);
		this.getInquireMapValue("12", "30", parameters);
		change2String(parameters);
		return parameters;
	}

	private Map<String, Object> getThreeMap(String phrId,
			Map<String, Object> map, Context ctx) throws PrintException,
			ServiceException {
		Map<String, Object> parameters = getFirstRecord(getBaseInfo(
				MPI_DemographicInfo, map, ctx));
		parameters.put("phrId", phrId);
		String exp = "['and',['eq',['$','a.phrId'],['s','"
				+ phrId
				+ "']],['or',['eq',['$','checkupStage'],['s','36']],['eq',['$','checkupStage'],['s','48']],['eq',['$','checkupStage'],['s','60']],['eq',['$','checkupStage'],['s','72']]]]";
		this.getCheckupMapValue(exp, parameters,
				BSCHISEntryNames.CDH_CheckupThreeToSix);
		this.getInquireMapValue("36", "72", parameters);
		change2String(parameters);
		return parameters;
	}

	private void getCheckupMapValue(String exp, Map<String, Object> parameters,
			String schema) throws PrintException, ServiceException {
		List<Map<String, Object>> list = null;
		try {
			list = dao.doQuery(toListCnd(exp), null, schema);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		Iterator<Map<String, Object>> it = list.iterator();
		while (it.hasNext()) {
			Map<String, Object> map = it.next();
			if (map.containsKey("skin")) {
				String skin = (String) map.get("skin");
				if (skin != null && !skin.equals("01")) {
					map.put("skin_text", "异常");
				}
			}
			String checkupStage = (String) map.get("checkupStage");
			Iterator<?> i = map.keySet().iterator();
			while (i.hasNext()) {
				String s = (String) i.next();
				parameters.put(s + checkupStage, map.get(s));
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void getInquireMapValue(String startTime, String lastTime,
			Map<String, Object> parameters) throws PrintException,
			ServiceException {
		String exp = null;
		if ((startTime != null && !startTime.equals(""))
				&& (lastTime != null && !lastTime.equals(""))) {
			int sTime = Integer.parseInt(startTime);
			int lTime = Integer.parseInt(lastTime);
			exp = "['and',['and',['eq',['$','businessType'],['s','"
					+ BusinessType.CD_IQ + "']],['eq',['$','recordId'],['s','"
					+ phrId + "']]],['and',['ge',['$','extend1'],['i'," + sTime
					+ "]],['le',['$','extend1'],['i'," + lTime + "]]]]";

		} else if ((startTime != null && !startTime.equals(""))
				&& (lastTime == null || lastTime.equals(""))) {
			int sTime = Integer.parseInt(startTime);
			exp = "['and',['and',['eq',['$','businessType'],['s','"
					+ BusinessType.CD_IQ + "']],['eq',['$','recordId'],['s','"
					+ phrId + "']]],['eq',['$','extend1'],['i'," + sTime
					+ "]]]";
		}
		List<Map<String, Object>> list = null;
		try {
			list = dao.doQuery(toListCnd(exp), "beginDate",
					BSCHISEntryNames.PUB_VisitPlan);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			Map<String, Object> plan = (Map<String, Object>) iterator.next();
			String inquireTime = plan.get("extend1").toString();
			Object inquireId = plan.get("visitId");
			if (inquireId != null) {
				Map<String, Object> data = null;
				try {
					data = getFirstRecord(dao.doQuery(
							toListCnd("['eq',['$','a.inquireId'],['s','"
									+ inquireId + "']]"), null,
							BSCHISEntryNames.CDH_Inquire));
				} catch (PersistentDataOperationException e) {
					e.printStackTrace();
				}
				Iterator<String> key = data.keySet().iterator();
				while (key.hasNext()) {
					String s = (String) key.next();
					parameters.put(s + inquireTime, data.get(s));
				}
			}
		}
	}

	// 0~6岁儿童基本档案
	private Map<String, Object> getSample06Map(Map<String, Object> map,
			Context ctx) throws PrintException, ServiceException {
		Map<String, Object> parameters = getFirstRecord(getBaseInfo(
				MPI_DemographicInfo, map, ctx));
		Map<String, Object> childData = getFirstRecord(getBaseInfo(
				CDH_HealthCard, map, ctx));
		parameters.putAll(childData);
		List<Map<String, Object>> list = null;
		if (parameters.get("fatherEmpiId") != null) {
			String fatherEmpiId = parameters.get("fatherEmpiId").toString();
			map.put("empiId", fatherEmpiId);
			list = getBaseInfo(MPI_DemographicInfo, map, ctx);
			map.put("empiId", empiId);
			if (list.size() > 0) {
				Map<String, Object> data = list.get(0);
				parameters.put("fatherName", data.get("personName"));
				parameters.put("fatherBirthday", data.get("birthday"));
				parameters.put("fatherJob", data.get("workCode_text"));
				parameters.put("fatherEducation",
						data.get("educationCode_text"));
				// parameters.put("fatherHealth", faData.get(""));
			}
		}
		if (parameters.get("motherEmpiId") != null) {
			String motherEmpiId = parameters.get("motherEmpiId").toString();
			map.put("empiId", motherEmpiId);
			list = getBaseInfo(MPI_DemographicInfo, map, ctx);
			map.put("empiId", empiId);
			if (list.size() > 0) {
				Map<String, Object> data = list.get(0);
				parameters.put("motherName", data.get("personName"));
				parameters.put("motherBirthday", data.get("birthday"));
				parameters.put("motherJob", data.get("workCode_text"));
				parameters.put("motherEducation",
						data.get("educationCode_text"));
				// parameters.put("motherHealth", data.get(""));
			}
		}
		change2String(parameters);
		return parameters;
	}

	private Map<String, Object> getSummaryMap(Map<String, Object> map,
			Context ctx) throws PrintException, ServiceException {
		Map<String, Object> parameters = getFirstRecord(getBaseInfo(
				MPI_DemographicInfo, map, ctx));
		Map<String, Object> summaryData = getFirstRecord(getBaseInfo(
				CDH_OneYearSummary, map, ctx));
		parameters.putAll(summaryData);
		parameters.put("phrId", phrId);
		return parameters;
	}

	// @@ 疑似残疾儿童报告卡
	private Map<String, Object> get03DisabilityMonitorMap(
			Map<String, Object> map, Context ctx) throws PrintException,
			ServiceException {
		Map<String, Object> parameters = getFirstRecord(getBaseInfo(
				MPI_DemographicInfo, map, ctx));
		Date birthday = (Date) parameters.get("birthday");
		String realAge = "";
		int age = BSCHISUtil.calculateAge(birthday, null);

		if (age < 1) {
			age = BSCHISUtil.getMonths(birthday, new Date());
			if (age == 0) {
				age = BSCHISUtil.getPeriod(birthday, new Date());
				realAge = age + "天";
			} else {
				realAge = age + "个月";
			}
		} else {
			realAge = age + "岁";
		}

		String areaGrid = (String) ctx.get("server.manage.topUnit");
		Map<String, Object> topAreaGrid = null;
		try {
			topAreaGrid = getFirstRecord(dao.doQuery(
					toListCnd("['eq',['$','a.regionCode'],['s','" + areaGrid
							+ "']]"), "orderNo asc , regionCode asc",
					BSCHISEntryNames.EHR_AreaGrid));
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		parameters.put("regionName", topAreaGrid.get("regionName"));
		parameters.put("age", realAge);
		Map<String, Object> disabilityData = getFirstRecord(getBaseInfo(
				CDH_DisabilityMonitor, map, ctx));
		parameters.putAll(disabilityData);
		Map<String, Object> childrenCard = getFirstRecord(getBaseInfo(
				CDH_HealthCard, map, ctx));
		parameters.putAll(childrenCard);
		return parameters;
	}

	protected Map<String, Object> getDeblilityMap(Map<String, Object> map,
			Context ctx) throws PrintException, ServiceException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String recordId = (String) map.get("CDH_DebilityChildren.recordId");
		// 体弱儿基本信息
		Map<String, Object> parameters = getFirstRecord(getBaseInfo(
				MPI_DemographicInfo, map, ctx));

		// 体弱儿档案
		try {
			list = dao.doQuery(toListCnd("['eq',['$','a.recordId'],['s','"
					+ recordId + "']]"), "a.phrId desc,a.createDate desc",
					BSCHISEntryNames.CDH_DebilityChildren);

			Map<String, Object> debMap = getFirstRecord(list);
			parameters.putAll(debMap);

			// 获取父母姓名
			parameters.putAll(getFirstRecord(getBaseInfo(CDH_HealthCard, map,
					ctx)));
			if (parameters.get("fatherEmpiId") != null) {
				list = dao.doQuery(toListCnd("['eq',['$','a.empiId'],['s','"
						+ parameters.get("fatherEmpiId") + "']]"),
						"a.empiId desc", BSCHISEntryNames.MPI_DemographicInfo);
				if (list != null && list.size() > 0) {
					parameters.put("fatherName", list.get(0).get("personName"));
				}
			}
			if (parameters.get("motherEmpiId") != null) {
				list = dao.doQuery(toListCnd("['eq',['$','a.empiId'],['s','"
						+ parameters.get("motherEmpiId") + "']]"),
						"a.empiId desc", BSCHISEntryNames.MPI_DemographicInfo);
				if (list != null && list.size() > 0) {
					parameters.put("motherName", list.get(0).get("personName"));
				}
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		return parameters;
	}

	// search visitId by phrId
	@SuppressWarnings("unchecked")
	public List<String> getVisitId(Map<String, Object> requestData, Context ctx) {
		Session session = null;
		List<String> list = new ArrayList<String>();
		try {
			String sql = new StringBuffer(
					"select visitId from CDH_DebilityChildrenVisit where empiId = :empiId")
					.toString();
			session = this.getSessionFactory(ctx).openSession();
			Query query = session.createSQLQuery(sql);
			query.setString("empiId", empiId);
			list = (List<String>) query.list();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
		return list;

	}

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		getDao(ctx);
		try {
			records.addAll(getDeblilityDataSource(request, ctx));
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	private List<Map<String, Object>> getDeblilityDataSource(
			Map<String, Object> map, Context ctx2) throws PrintException,
			ServiceException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String recordId = (String) map.get("CDH_DebilityChildren.recordId");
		try {
			list = dao.doQuery(toListCnd("['eq',['$','a.recordId'],['s','"
					+ recordId + "']]"), null,
					BSCHISEntryNames.CDH_DebilityChildrenVisit);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		;
		return list;
	}
}
