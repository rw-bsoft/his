/**
 * @(#)HypertensionRecord1File.java Created on Mar 10, 2010 2:46:21 PM
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.print.instance;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import ctd.print.IHandler;
import ctd.print.PrintException;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.web.context.WebApplicationContext;

import chis.source.BSCHISEntryNames;
import chis.source.Constants;
import chis.source.PersistentDataOperationException;
import chis.source.print.base.BSCHISPrint;
import chis.source.util.BSCHISUtil;
import chis.source.util.ManageYearUtil;
import com.alibaba.fastjson.JSONException;
import ctd.service.core.ServiceException;
import ctd.util.AppContextHolder;
import ctd.util.context.Context;

/**
 * @description 随访记录打印。
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class HypertensionRecordFile extends BSCHISPrint implements IHandler {

	public static final String EMPI_INFO_TABLE = "chis.application.mpi.schema.MPI_DemographicInfo";
	public static final String HYPERTENSION_RECORD_TABLE = "chis.application.hy.schemas.MDC_HypertensionRecord";
	public static final String HYPERTENSION_VISIT_TABLE = "chis.application.hy.schemas.MDC_HypertensionVisit";
	public static final String HYPERTENSION_MEDICINE_TABLE = "chis.application.hy.schemas.MDC_HypertensionMedicine";

	private String empiId;

	private Context ctx;

	/**
	 * @throws PrintException
	 * @see chis.source.print.instance
	 *      ;.base.IPrint#getDataSource(java.util.HashMap,
	 *      net.sf.jasperreports.engine.design.JasperDesign, java.util.HashMap)
	 */
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> response, Context ctx)
			throws PrintException {
		try {
			getDao(ctx);
			getSampleHypertensionRecord1Map(request, response, ctx);
		} catch (ServiceException e) {
			throw new PrintException(Constants.CODE_EXP_ERROR,e.getMessage());
		}
		sqlDate2String(response);
		change2String(response);
	}

	/**
	 * @throws PrintException
	 * @see chis.source.print.instance
	 *      ;.base.IPrint#getPrintMap(java.util.HashMap, java.util.HashMap)
	 */
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx)
			throws PrintException {
		empiId = (String) request.get("empiId");
		this.ctx = ctx;
		getDao(ctx);
		Map<String, Object> eim = null;
		try {
			eim = getEmpiBaseInfo(request, ctx);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		request.put("personName", eim.get("personName"));
		request.put("phrId", getPhrId(empiId));
		response.put("personName", eim.get("personName"));
		response.put("phrId", getPhrId(empiId));
		response.putAll(eim);
		sqlDate2String(response);
	}

	/**
	 * @param map
	 * @param sharedDate
	 * @return
	 * @throws PrintException
	 * @throws ServiceException 
	 * @throws JSONException
	 */
	private void getSampleHypertensionRecord1Map(Map<String, Object> map,
			List<Map<String, Object>> response, Context ctx)
			throws PrintException, ServiceException {
		String beginDate = (String) map.get("beginDate");
		String endDate = (String) map.get("endDate");
		// ------------------
		ManageYearUtil myu = new ManageYearUtil(new Date());
		beginDate = BSCHISUtil.toString(myu.getHypertensionCurYearStartDate(),
				"yyyy-MM-dd");
		endDate = BSCHISUtil.toString(myu.getHypertensionCurYearEndDate(),
				"yyyy-MM-dd");
		// --------------------
		List<Map<String, Object>> hvl = getVisitRecord(beginDate, endDate);
		for (Iterator<Map<String, Object>> it = hvl.iterator(); it
				.hasNext();) {
			Map<String, Object> m = it.next();
			m.put("targetTrain", getStringFromInt((Integer) m.get("targetTrainTimesWeek"))+"次/周"+"  "+ getStringFromInt((Integer) m.get("targetTrainMinute"))+"分钟/次");
			m.put("train",  getStringFromInt((Integer) m.get("trainTimesWeek"))+"次/周"+"  "+getStringFromInt((Integer) m.get("trainMinute"))+"分钟/次");
			Map<String, Object> bean = new HashMap<String, Object>();
			for (int i = 1; i < 5; i++) {
				setVisitMap(bean, m, i);
				if (it.hasNext()) {
					m = it.next();
				} else {
					break;
				}
			}
			response.add(bean);
		}
		if(hvl.size()==0){
			Map<String, Object> m=null;
			fillData(m, HYPERTENSION_VISIT_TABLE);
			response.add(m);
		}
	}

	/**
	 * 
	 * 
	 * @param bean
	 * @param m
	 * @param i
	 * @throws PrintException
	 * @throws ServiceException 
	 * @throws JSONException
	 */
	private void setVisitMap(final Map<String, Object> bean,
			Map<String, Object> m, int i) throws PrintException, ServiceException {
		for (String key : m.keySet()) {
			if (m.containsKey(key + "_text")) {
				bean.put(key + i, m.get(key + "_text"));
			} else {
				bean.put(key + i, m.get(key));
			}
		}
		bean.put("bloodPressure" + i,
				m.get("constriction") + "/" + m.get("diastolic"));
		List<Map<String, Object>> ml = getMedicine((String) m
				.get("visitId"));
		for (int j = 1; j < 5; j++) {
			if (ml.size() >= j) {
				Map<String, Object> mm = ml.get(j - 1);
				bean.put("medicine" + i + "_" + j, mm.get("medicineName"));
				StringBuffer sb = new StringBuffer("每日")
						.append(mm.get("medicineFrequency")).append("次,每次")
						.append(mm.get("medicineDosage"))
						.append(mm.get("medicineUnit_text"));
				bean.put("usage" + i + "_" + j, sb.toString());
			}
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
	private Map<String, Object> getEmpiBaseInfo(
			Map<String, Object> map, Context ctx) throws PrintException, ServiceException {
		Map<String, Object> sharedData = (Map<String, Object>) ctx
				.get("sharedData");
		if (sharedData != null && sharedData.containsKey(MPI_DemographicInfo)) {
			return ((List<Map<String, Object>>) sharedData
					.get(MPI_DemographicInfo)).get(0);
		}
		List<Map<String, Object>> list = getBaseInfo(MPI_DemographicInfo,
				map, ctx);
		sharedData = new HashMap<String, Object>();
		sharedData.put(String.valueOf(MPI_DemographicInfo), list);
		return getFirstRecord(list);
	}

	/**
	 * @param empiId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String getPhrId(String empiId) {
		WebApplicationContext wac = (WebApplicationContext) ctx
				.get(Context.APP_CONTEXT);
		HibernateTemplate ht = new HibernateTemplate((AppContextHolder.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY,SessionFactory.class)));
		
		List<Map<String, Object>> list = ht.find(
				new StringBuffer("from ").append(HYPERTENSION_RECORD_TABLE)
						.append(" where empiId=? and status='0'").toString(),
				empiId);
		if (list == null || list.size() == 0) {
			return null;
		}
		return (String) list.get(0).get("phrId");
	}

	/**
	 * 查询某一个人在某个时间段内的随访记录。
	 * 
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws PrintException
	 * @throws ServiceException 
	 * @throws JSONException
	 */
	private List<Map<String, Object>> getVisitRecord(String beginDate,
			String endDate) throws PrintException, ServiceException {
		List<Map<String, Object>> list = null;
		try {
			list = dao.doQuery(toListCnd("['and', ['eq', ['$', 'a.empiId'], ['s', '" + empiId
					+ "']], ['and', ['ge', ['$', "
					+ "\"str(visitDate, 'yyyy-MM-dd')\"], ['date', '"
					+ beginDate + "']], ['le', ['$', \"str(visitDate, "
					+ "'yyyy-MM-dd')\"], ['date', '" + endDate + "']]]]"), "a.empiId desc",
					BSCHISEntryNames.MDC_HypertensionVisit);
		} catch (PersistentDataOperationException e) {
			throw new PrintException(Constants.CODE_EXP_ERROR,e.getMessage());
		}
		return list;
	}

	/**
	 * 查询某次随访的服药情况。
	 * 
	 * @param visitId
	 * @return
	 * @throws PrintException
	 * @throws ServiceException 
	 * @throws JSONException
	 */
	private List<Map<String, Object>> getMedicine(String visitId)
			throws PrintException, ServiceException {
		List<Map<String, Object>> list = null;
		try {
			list = dao.doQuery(toListCnd("['eq', ['$', 'visitId'], ['s', '"
					+ visitId + "']]"),null,
					BSCHISEntryNames.MDC_HypertensionMedicine);
		} catch (PersistentDataOperationException e) {
			throw new PrintException(Constants.CODE_EXP_ERROR,e.getMessage());
		}
		return list;
	}

}
