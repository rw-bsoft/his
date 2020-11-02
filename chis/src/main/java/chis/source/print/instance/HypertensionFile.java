/**
 * @(#)HypertensionFile.java Created on Dec 24, 2009 10:29:19 AM
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.print.instance;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import ctd.print.IHandler;
import ctd.print.PrintException;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

import chis.source.BSCHISEntryNames;
import chis.source.Constants;
import chis.source.PersistentDataOperationException;
import chis.source.print.base.BSCHISPrint;
import com.alibaba.fastjson.JSONException;
import ctd.service.core.ServiceException;
import ctd.util.AppContextHolder;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class HypertensionFile extends BSCHISPrint implements IHandler{

	public static final String EMPI_INFO_TABLE = "MPI_DemographicInfo";
	public static final String HYPERTENSION_RECORD_TABLE = "MDC_HypertensionRecord";
	public static final String PAST_HISTORY_TABLE = "EHR_PastHistory";
	public static final String LIFE_STYLE_TABLE = "EHR_LifeStyle";
	public static final String HYPERTENSION_VISIT_TABLE = "MDC_HypertensionVisit";
	public static final String HYPERTENSION_MEDICINE_TABLE = "MDC_HypertensionMedicine";

	private String empiId;

	private HibernateTemplate ht;

	/**
	 * @throws PrintException 
	 * @see chis.source.print.instance;.base.IPrint#getPrintMap(java.util.HashMap,
	 *      java.util.HashMap)
	 */
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		empiId = (String) request.get("empiId");
		getDao(ctx);
		ht = new HibernateTemplate(AppContextHolder.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY,SessionFactory.class));
		
		String jrxml = (String) request.get("jrxml");
		if (jrxml.contains("hypertensioncard")) {
			try {
				getSampleHypertensionCardMap(request, response, ctx);
			} catch (ServiceException e) {
				throw new PrintException(Constants.CODE_EXP_ERROR,e.getMessage());
			}
		}
		sqlDate2String(response);
	}

	/**
	 * @param map
	 * @param sharedData
	 * @return
	 * @throws PrintException 
	 * @throws ServiceException 
	 * @throws JSONException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void getSampleHypertensionCardMap(
			Map<String, Object> request, Map<String, Object> response,
			Context ctx) throws PrintException, ServiceException {
		Map<String, Object> eim = getEmpiBaseInfo(request,ctx);
		// @@ 基本信息。
		if (false == eim.isEmpty()) {
			response.put("personName", eim.get("personName"));
			response.put("sexCode", eim.get("sexCode_text"));
			response.put("birthday", eim.get("birthday"));
			response.put("workCode", eim.get("workCode_text"));
			response.put("phoneNumber", eim.get("phoneNumber"));
			response.put("address", eim.get("address"));
			response.put("zipCode", eim.get("zipCode"));
		}
		// @@ 家庭史。
		Map<String, Object> phm = getFamilyHistory(ctx);
		response.put("pastHisFather", phm.get("fatherHis"));
		response.put("pastHisMather", phm.get("matherHis"));
		response.put("pastHisBroSis", phm.get("brotherSisterHis"));
		response.put("pastHisChidren", phm.get("childrenHis"));
		// @@ 高血压档案信息。
		Map<String, Object> hrm = getHypertensionRecord(ctx,request);
		String phrId = null;
		if (hrm != null && false == hrm.isEmpty()) {
			response.put("smoke", hrm.get("smoke_text"));
			response.put("smokeCount", hrm.get("smokeCount"));
			response.put("drink", hrm.get("drink_text"));
			response.put("drinkCount", hrm.get("drinkCount"));
			response.put("drinkType", hrm.get("drinkTypeCode_text"));
			response.put("train", hrm.get("train_text"));
			response.put("eateHabit", hrm.get("eateHabit_text"));

			response.put("recordSource", hrm.get("recordSource_text"));
			Date confirmDate = (Date) hrm.get("confirmDate");
			DateFormat df = SimpleDateFormat.getDateInstance();
			response.put("confirmDate", df.format(confirmDate));
			response.put("diseaseAge", calculateDiseaseAge(confirmDate));
			response.put("createDate", df.format(hrm.get("createDate")));
			response.put("adminDoctor", hrm.get("manaDoctorId_text"));
			response.put("manaUnitId", hrm.get("manaUnitId_text"));
			response.put("clinicAddress", hrm.get("clinicAddress_text"));
			response.put("viability", hrm.get("viability_text"));
			double height = 0;
			if (hrm.get("height") != null) {
				height = (Double) hrm.get("height");
			}
			double weight = 0;
			if (hrm.get("weight") != null) {
				weight = (Double) hrm.get("weight");
			}
			response.put("height", height);
			response.put("weight", weight);
			double bmi = calculateBMI(height, weight);
			response.put("bmi", bmi == -1 ? null : bmi);
			StringBuffer sb = new StringBuffer()
					.append(hrm.get("constriction")).append("/")
					.append(hrm.get("diastolic"));
			response.put("bloodPressure", sb.toString());
			response.put("riskLevel", hrm.get("riskLevel_text"));
			phrId = (String) hrm.get("phrId");
			response.put("phrId", phrId);
			if (hrm.get("cancellationDate") != null) {
				response.put("cancellationDate", hrm.get("cancellationDate"));
			}
			if (hrm.get("cancellationReason") != null) {
				response.put("cancellationReason",
						hrm.get("cancellationReason_text"));
			}
		}

		// @@ 服药情况。
		List<Map<String, Object>> medicineList = null;
		try {
			medicineList = dao.doQuery(toListCnd("['and', ['eq',['$','phrId'],['s','" + phrId
					+ "']], ['eq', ['$', 'visitId'], ['s', '0000000000000000']]]"),null,
					BSCHISEntryNames.MDC_HypertensionMedicine);
		} catch (PersistentDataOperationException e) {
			throw new PrintException(Constants.CODE_EXP_ERROR,e.getMessage());
		}
		int i = 1;
		for (Iterator it = medicineList.iterator(); it.hasNext(); i++) {
			Map<String, Object> m = (Map<String, Object>) it.next();
			response.put("medicine" + i, m.get("medicineName"));
			response.put("dayCount" + i, m.get("medicineFrequency"));
			response.put("dosage" + i, m.get("medicineDosage"));
			response.put("unit" + i, m.get("medicineUnit_text"));
		}
	}

	/**
	 * @param map 
	 * @param sharedDate
	 * @param ctx
	 * @return
	 * @throws PrintException 
	 * @throws ServiceException 
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> getEmpiBaseInfo(Map<String, Object> map, Context ctx) throws PrintException, ServiceException {
		Map<String, Object> sharedData = ((Map<String, Object>) ctx
				.get("sharedData"));
		if (sharedData!=null&&sharedData.containsKey(MPI_DemographicInfo)) {
			return ((List<Map<String, Object>>) sharedData
					.get(MPI_DemographicInfo)).get(0);
		}
		List<Map<String, Object>> list = getBaseInfo(MPI_DemographicInfo, map, ctx);
		sharedData = ((Map<String, Object>) ctx
				.get("sharedData"));
		sharedData.put(String.valueOf(MPI_DemographicInfo), list);
		return getFirstRecord(list);
	}

	/**
	 * @param sharedData
	 * @param ctx
	 * @return
	 * @throws JSONException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map<String, Object> getFamilyHistory(Context ctx) {
		Map<String, Object> sharedData = (Map<String, Object>) ctx
				.get("sharedData");
		if (sharedData!=null&&sharedData.containsKey(EHR_PastHistory)) {
			return (Map<String, Object>) sharedData.get(EHR_PastHistory);
		}
		List<Map<String, Object>> list2 = ht.find(new StringBuffer("from ")
				.append(PAST_HISTORY_TABLE).append(" where empiId = ? and ")
				.append("pastHisTypeCode >= '08' and pastHisTypeCode <= '11'")
				.toString(), empiId);
		StringBuffer fatherSb = new StringBuffer();
		StringBuffer matherSb = new StringBuffer();
		StringBuffer bsSb = new StringBuffer();
		StringBuffer childSb = new StringBuffer();
		for (Iterator it = list2.iterator(); it.hasNext();) {
			Map m = (Map) it.next();
			String code = (String) m.get("pastHisTypeCode");
			String diseaseCode = (String) m.get("diseaseCode");
			if (diseaseCode == null || diseaseCode.equals("0301")
					|| diseaseCode.equals("1401")) {
				continue;
			}
			if (code.equals("08")) {// @@ 父亲
				fatherSb.append(m.get("diseaseText")).append(",");
			}
			if (code.equals("09")) {// @@ 母亲
				matherSb.append(m.get("diseaseText")).append(",");
			}
			if (code.equals("10")) {// @@ 兄弟姐妹
				bsSb.append(m.get("diseaseText")).append(",");
			}
			if (code.equals("11")) {// @@ 子女
				childSb.append(m.get("diseaseText")).append(",");
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		if (fatherSb.length() > 0) {
			map.put("fatherHis", fatherSb.substring(0, fatherSb.length()));
		}
		if (matherSb.length() > 0) {
			map.put("matherHis", matherSb.substring(0, matherSb.length()));
		}
		if (bsSb.length() > 0) {
			map.put("brotherSisterHis", bsSb.substring(0, bsSb.length()));
		}
		if (childSb.length() > 0) {
			map.put("childrenHis", childSb.substring(0, childSb.length()));
		}
		sharedData.put(String.valueOf(EHR_PastHistory), map);
		return map;
	}

	/**
	 * @param sharedData
	 * @param ctx
	 * @param map 
	 * @return
	 * @throws PrintException 
	 * @throws ServiceException 
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> getHypertensionRecord(Context ctx, Map<String, Object> map) throws PrintException, ServiceException {
		Map<String, Object> sharedData = (Map<String, Object>) ctx
				.get("sharedData");
		if (null == sharedData) {
			sharedData = new HashMap<String, Object>();
			ctx.put("sharedData", sharedData);
		}
		if (sharedData.containsKey(MDC_HypertensionRecord)) {
			List<Map<String, Object>> hrmLst = (List<Map<String, Object>>) sharedData
					.get(MDC_HypertensionRecord);
			if (hrmLst.size() > 0) {
				return hrmLst.get(0);
			} else
				return null;
		}
		List<Map<String, Object>> hrmLst = getBaseInfo(MDC_HypertensionRecord, map, ctx);
		sharedData.put(String.valueOf(MDC_HypertensionRecord), hrmLst);
		if (hrmLst.size() > 0) {
			return hrmLst.get(0);
		} else
			return null;
	}

	/**
	 * 计算病程。
	 * 
	 * @param diseaseDate
	 * @return
	 */
	private String calculateDiseaseAge(Date diseaseDate) {
		if (diseaseDate == null) {
			return "";
		}
		Calendar c = Calendar.getInstance();
		c.setTime(diseaseDate);

		Calendar now = Calendar.getInstance();
		int years = now.get(Calendar.YEAR) - c.get(Calendar.YEAR);
		c.set(Calendar.YEAR, now.get(Calendar.YEAR));
		int months = Math.abs(now.get(Calendar.MONTH) - c.get(Calendar.MONTH));
		if (years == 0 && months == 0) {
			return now.get(Calendar.DAY_OF_YEAR) - c.get(Calendar.DAY_OF_YEAR)
					+ "天";
		}
		StringBuffer sb = new StringBuffer();
		if (years != 0) {
			sb.append(years).append("年");
		}
		if (months != 0) {
			sb.append("零").append(months).append("月");
		}
		return sb.toString();
	}

	/**
	 * @param height
	 * @param weight
	 * @return
	 */
	private double calculateBMI(double height, double weight) {
		if (height == 0 || weight == 0) {
			return -1;
		}
		return weight / Math.pow(height / 100.0, 2);
	}

	/**
	 * @see chis.source.print.instance;.base.IPrint#getDataSource(java.util.HashMap,
	 *      net.sf.jasperreports.engine.design.JasperDesign, java.util.HashMap)
	 */

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {

	}
}
