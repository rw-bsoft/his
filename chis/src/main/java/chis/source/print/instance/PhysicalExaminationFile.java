/**
 * @(#)ExaminationFile.java Created on May 13, 2010 2:31:24 PM
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.print.instance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.dictionary.DictionaryController;
import chis.source.BSCHISEntryNames;
import chis.source.PersistentDataOperationException;
import chis.source.print.base.BSCHISPrint;
import com.alibaba.fastjson.JSONException;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class PhysicalExaminationFile extends BSCHISPrint implements IHandler {

	/**
	 * @see chis.source.print.instance
	 *      ;.base.IPrint#getDataSource(java.util.HashMap,
	 *      net.sf.jasperreports.engine.design.JasperDesign, java.util.HashMap)
	 */
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		getDao(ctx);
		try {
			records.addAll(getDataList(request));
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		change2String(records);

	}

	/**
	 * @see chis.source.print.instance
	 *      ;.base.IPrint#getPrintMap(java.util.HashMap, java.util.HashMap)
	 */
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		sqlDate2String(response);
	}

	/**
	 * @param requestData
	 * @param sharedData
	 * @return
	 * @throws PrintException
	 * @throws ServiceException
	 * @throws JSONException
	 * @throws PersistentDataOperationException
	 */
	private List<Map<String, Object>> getDataList(
			Map<String, Object> requestData) throws PrintException,
			ServiceException {
		String checkupNo = (String) requestData.get("checkupNo");
		List<Map<String, Object>> list = getCheckupInfo(checkupNo);
		Map<String, Object> registerInfo = getCheckupRegisterInfo(requestData);
		Map<String, Object> dicItems = getDicItems();
		try {
			if (list.isEmpty()) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("checkupNo", registerInfo.get("checkupNo"));
				map.put("checkupId", registerInfo.get("checkupId"));
				map.put("personName", registerInfo.get("name"));
				map.put("community",
						DictionaryController
								.instance()
								.get("chis.@manageUnit")
								.getText(
										(String) registerInfo
												.get("checkupOrganization"))
								+ "\n体检报告");
				map.put("sex",
						DictionaryController.instance()
								.get("chis.dictionary.gender")
								.getText((String) registerInfo.get("sex")));
				map.put("age", registerInfo.get("age"));
				map.put("totalCheckUpDate",
						registerInfo.get("totalCheckupDate"));
				// TODO 部门工号未填
				map.put("agency",
						DictionaryController
								.instance()
								.get("chis.dictionary.perComboType")
								.getText((String) registerInfo.get("comboName")));
				List<Map<String, Object>> l = new ArrayList<Map<String, Object>>();
				l.add(map);
				return l;
			}
			List<Map<String, Object>> summaryList = getSummary(checkupNo);
			for (Map<String, Object> map : list) {
				Double referenceLower = (Double) map.get("referenceLower");
				Double referenceUpper = (Double) map.get("referenceUpper");
				String arrange = "";
				if (referenceLower != null) {
					arrange = referenceLower.toString() + "-";
				}
				if (referenceUpper != null) {
					arrange = arrange + referenceUpper.toString();
				}
				map.put("arrange", arrange);
				if (map.get("memo") != null) {
					map.put("memo1", map.get("memo"));
					map.remove("memo");
				}

				String checkupProjectId = map.get("checkupProjectId")
						.toString();
				if (dicItems.containsKey(checkupProjectId)) {
					String dicId = dicItems.get(checkupProjectId).toString();
					if (map.get("ifException") != null) {
						String ifException = map.get("ifException").toString();
						if (DictionaryController.instance()
								.get("chis.dictionary." + dicId)
								.getItem(ifException) != null) {
							String ifExceptionText = DictionaryController
									.instance().get("chis.dictionary." + dicId)
									.getItem(ifException).getText();
							map.put("ifException", ifExceptionText);
						}
					}

				}
				for (Map<String, Object> smap : summaryList) {
					if (smap.get("projectOfficeCode").equals(
							map.get("projectOfficeCode"))) {
						map.put("checkupSummary",
								smap.get("checkupSummary") == null ? "" : smap
										.get("checkupSummary"));
						map.put("summaryDate",
								smap.get("summaryDate") == null ? "" : smap
										.get("summaryDate"));
						map.put("summaryDoctor",
								smap.get("checkupDoctor") == null ? ""
										: DictionaryController
												.instance()
												.get("chis.dictionary.user01")
												.getText(
														(String) smap
																.get("checkupDoctor")));
						break;
					}
				}
				map.put("personName", registerInfo.get("name"));
				map.put("checkupNo", registerInfo.get("checkupNo"));
				map.put("checkupId", registerInfo.get("checkupId"));
				map.put("community",
						DictionaryController
								.instance()
								.get("chis.@manageUnit")
								.getText(
										(String) registerInfo
												.get("checkupOrganization"))
								+ "\n体检报告");
				map.put("sex",
						DictionaryController.instance()
								.get("chis.dictionary.gender")
								.getText((String) registerInfo.get("sex")));
				map.put("age", registerInfo.get("age"));
				map.put("totalCheckUpDate",
						registerInfo.get("totalCheckupDate"));
				// TODO 部门工号未填
				map.put("agency",
						DictionaryController
								.instance()
								.get("chis.dictionary.perComboType")
								.getText((String) registerInfo.get("comboName")));
			}
			Map<String, Object> map = list.get(list.size() - 1);
			map.put("memo", makeMemo());
			map.put("totalSummary", makeTotalSummary(registerInfo));
			map.put("advice", registerInfo.get("checkupAdvice"));
			map.put("totalCheckupDate", registerInfo.get("totalCheckupDate"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * @param registerInfo
	 * @return
	 */
	private String makeTotalSummary(Map<String, Object> registerInfo) {
		StringBuffer sb = new StringBuffer();
		if (registerInfo.get("checkupExce") != null) {
			sb.append("1、").append(registerInfo.get("checkupExce"))
					.append("\n");
		}
		if (registerInfo.get("checkupOutcome") != null) {
			sb.append("2、").append(registerInfo.get("checkupOutcome"))
					.append("\n");
		}
		return sb.toString();
	}

	/**
	 * @return
	 */
	private String makeMemo() {
		return new StringBuffer("说明：\n")
				.append("1.您过去患的疾病，因这次体检范围所限未能发现到的情况，仍按原诊断及治疗\n")
				.append("2.本次查出的疾病请及时治疗，异常项目请到医院复查\n")
				.append("3.体检结论仅根据本次所检项目所做，可能难以全面反映您的健康状况\n")
				.append("4.若有不适症状出现请到相应专科就诊\n").append("5.检验报告结果以原件为准")
				.toString();
	}

	/**
	 * @param checkupNo
	 * @return
	 * @throws PrintException
	 * @throws ServiceException
	 * @throws JSONException
	 */
	private List<Map<String, Object>> getCheckupInfo(String checkupNo)
			throws PrintException, ServiceException {
		List<Map<String, Object>> list = null;
		try {
			list = dao.doQuery(toListCnd("['eq', ['$', 'checkupNo'], ['s', '"
					+ checkupNo + "']]"), "projectOffice",
					BSCHISEntryNames.PER_CheckupDetail);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		if (list == null || list.size() == 0) {
			Map<String, Object> m = new HashMap<String, Object>();
			fillData(m, BSCHISEntryNames.PER_CheckupDetail);
			list.add(m);
		}
		return list;
	}

	/**
	 * @param checkupNo
	 * @return
	 * @throws PrintException
	 * @throws ServiceException
	 * @throws JSONException
	 */
	private List<Map<String, Object>> getSummary(String checkupNo)
			throws PrintException, ServiceException {
		List<Map<String, Object>> list = null;
		try {
			list = dao.doQuery(toListCnd("['eq', ['$', 'checkupNo'], ['s', '"
					+ checkupNo + "']]"), null,
					BSCHISEntryNames.PER_CheckupSummary);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		if (list == null || list.size() == 0) {
			Map<String, Object> m = new HashMap<String, Object>();
			fillData(m, BSCHISEntryNames.PER_CheckupSummary);
			list.add(m);
		}
		return list;
	}

	/**
	 * @param requestData
	 * @param sharedData
	 * @return
	 * @throws PrintException
	 * @throws ServiceException
	 * @throws JSONException
	 */
	private Map<String, Object> getCheckupRegisterInfo(
			Map<String, Object> requestData) throws PrintException,
			ServiceException {
		String checkupNo = (String) requestData.get("checkupNo");
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			data = getFirstRecord(dao.doQuery(
					toListCnd("['eq', ['$', 'checkupNo'], ['s', '" + checkupNo
							+ "']]"), "a.checkupNo desc",
					BSCHISEntryNames.PER_CheckupRegister));
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		return data;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> getDicItems() throws PrintException,
			ServiceException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		List<Map<String, Object>> list = null;
		try {
			list = dao.doQuery(
					toListCnd("['gt', ['$', 'length(checkupDic)'], ['i', 0]]"),
					"orderNo asc", BSCHISEntryNames.PER_CheckupDict);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			Map<String, Object> map = (Map<String, Object>) iterator.next();
			String checkupProjectId = map.get("checkupProjectId").toString();
			String checkupDic = map.get("checkupDic").toString();
			parameters.put(checkupProjectId, checkupDic);
		}
		return parameters;
	}

}
