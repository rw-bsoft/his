package chis.source.print.base;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.PersistentDataOperationException;
import chis.source.util.CNDHelper;
import com.alibaba.fastjson.JSONException;
import ctd.controller.exception.ControllerException;
import ctd.print.PrintException;
import ctd.schema.DictionaryIndicator;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;

public abstract class BSCHISPrint extends PrintImpl {
	public static BaseDAO dao = null;

	// sharedData's keys
	protected static final int MPI_DemographicInfo = 1; // EMPI个人基本信息
	protected static final int EHR_HealthRecord = 2; // 个人健康档案
	protected static final int EHR_LifeStyle = 3; // 个人生活习惯
	protected static final int EHR_PastHistory = 4; // 个人既往史
	protected static final int EHR_FamilyRecord = 5; // 家庭健康档案
	protected static final int EHR_FamilyProblem = 6; // 家庭主要问题
	protected static final int CDH_HealthCard = 7; // 儿童保健卡
	protected static final int Family_EHR_HealthRecord = 8; // 家人个人健康档案
	protected static final int MHC_PregnantRecord = 9; // 孕产妇基本情况
	protected static final int MDC_DiabetesRecord = 10; // 糖尿病档案
	protected static final int MDC_HypertensionRecord = 11; // @@ 高血压档案
	protected static final int MDC_HypertensionMedicine_rec = 12;// @@
																	// 高血压建档时的服药情况
	protected static final int MHC_FirstVisitRecord = 13; // 孕妇首次随访
	protected static final int MDC_TumourRecord = 14; // 肿瘤档案
	protected static final int CDH_Inquire = 15; // 儿童询问记录
	protected static final int CDH_Checkup = 16; // 儿童体格检查
	protected static final int CDH_OneYearSummary = 17; // 儿童三周岁小结
	protected static final int MHC_PregnantWomanIndex = 18; // 孕妇指标
	protected static final int MHC_PostnatalVisitInfo = 19; // 孕妇产后访视信息
	protected static final int MHC_VisitRecord = 20; // 孕妇随访信息
	protected static final int MHC_BabyVisitInfo = 21; // 新生儿访视基本信息
	protected static final int MHC_BabyVisitRecord = 22; // 新生儿访视记录
	protected static final int CDH_DebilityChildren = 23; // 体弱儿档案
	protected static final int CDH_DebilityChildrenVisit = 24; // 体弱儿童档案随访表
	protected static final int CDH_DeadRegister = 25; // 儿童死亡登记报告卡
	protected static final int CDH_Accident = 26; // 儿童意外情况
	protected static final int CVD_AssessRegister = 27; // 儿童意外情况
	protected static final int CDH_DisabilityMonitor = 28; // 疑似残疾儿童报告卡

	/**
	 * 获取相关的记录信息
	 * 
	 * @param infoType
	 *            信息类型，详见chis.source.print.instance;.base. AbstractPrint的静态int参数
	 * @param map
	 *            请求参数
	 * @param sharedData
	 *            一次请求中的共享数据
	 * @param ctx
	 *            请求的上下文
	 * @return
	 * @throws PrintException
	 * @throws ServiceException
	 * @throws JSONException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected List<Map<String, Object>> getBaseInfo(int infoType,
			Map<String, Object> map, Context ctx) throws PrintException,
			ServiceException {
		String empiId = (String) map.get("empiId");
		String phrId = (String) map.get("phrId");
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> sharedData = (Map<String, Object>) ctx
				.get("sharedData");
		String cnd = "";
		if (null == sharedData) {
			sharedData = new HashMap<String, Object>();
			ctx.put("sharedData", sharedData);
		}
		List listCnd = toListCnd("['eq',['$','a.empiId'],['s','" + empiId
				+ "']]");
		getDao(ctx);
		if (!sharedData.containsKey(infoType)) {
			try {
				switch (infoType) {
				case MPI_DemographicInfo:
					list = dao.doQuery(listCnd, "a.createTime desc",
							BSCHISEntryNames.MPI_DemographicInfo);
					break;
				case EHR_HealthRecord:
					list = dao.doQuery(listCnd, "a.phrId", BSCHISEntryNames.EHR_HealthRecord);
					break;
				case EHR_LifeStyle:
					list = dao.doQuery(listCnd, null, BSCHISEntryNames.EHR_LifeStyle);
					break;
				case EHR_FamilyRecord:
					if (getFirstRecord(getBaseInfo(EHR_HealthRecord, map, ctx))
							.containsKey("familyId")) {
						String familyId = (String) getFirstRecord(
								getBaseInfo(EHR_HealthRecord, map, ctx)).get(
								"familyId");
						list = dao.doQuery(
								toListCnd("['eq',['$','a.familyId'],['s','"
										+ familyId + "']]"), "a.familyId desc",
										BSCHISEntryNames.EHR_FamilyRecord);
					}
					break;
				case EHR_PastHistory:
					list = dao.doQuery(listCnd, "a.pastHisTypeCode desc",
							BSCHISEntryNames.EHR_PastHistory);
					break;
				case Family_EHR_HealthRecord:
					if (getFirstRecord(getBaseInfo(EHR_FamilyRecord, map, ctx))
							.containsKey("familyId")) {
						String familyId = (String) getFirstRecord(
								getBaseInfo(EHR_HealthRecord, map, ctx)).get(
								"familyId");
						list = dao.doQuery(
								toListCnd("['eq',['$','a.familyId'],['s','"
										+ familyId + "']]"), "a.phrId desc",
										BSCHISEntryNames.EHR_HealthRecord);
					}
					break;
				case EHR_FamilyProblem:
					if (getFirstRecord(getBaseInfo(EHR_FamilyRecord, map, ctx))
							.containsKey("familyId")) {
						String familyId = (String) getFirstRecord(
								getBaseInfo(EHR_HealthRecord, map, ctx)).get(
								"familyId");
						list = dao.doQuery(
								toListCnd("['eq',['$','a.familyId'],['s','"
										+ familyId + "']]"), null,
										BSCHISEntryNames.EHR_FamilyProblem);
					}
					break;
				case CDH_HealthCard:
					list = dao.doQuery(listCnd, "a.createDate desc",
							BSCHISEntryNames.CDH_HealthCard);
					break;
				case CDH_OneYearSummary:
					list = dao.doQuery(toListCnd("['eq',['$','a.phrId'],['s','"
							+ phrId + "']]"), null, BSCHISEntryNames.CDH_OneYearSummary);
					break;
				case CDH_DebilityChildren:
					list = dao.doQuery(listCnd,
							"a.phrId desc,a.createDate desc",
							BSCHISEntryNames.CDH_DebilityChildren);
					break;
				case CDH_DebilityChildrenVisit:
					list = dao.doQuery(listCnd, null,
							BSCHISEntryNames.CDH_DebilityChildrenVisit);
					break;
				case CDH_DeadRegister:
					String deadRegisterId = (String) map.get("deadRegisterId");
					list = dao.doQuery(
							toListCnd("['eq',['$','a.deadRegisterId'],['s','"
									+ deadRegisterId + "']]"),
							"a.deadRegisterId desc", BSCHISEntryNames.CDH_DeadRegister);
					break;
				case CDH_Accident:
					String accidentId = (String) map.get("accidentId");
					list = dao
							.doQuery(
									toListCnd("['eq',['$','a.accidentId'],['s','"
											+ accidentId + "']]"), null,
											BSCHISEntryNames.CDH_Accident);
					break;
				case CDH_DisabilityMonitor:
					list = dao.doQuery(toListCnd("['eq',['$','a.phrId'],['s','"
							+ phrId + "']]"), null, BSCHISEntryNames.CDH_DisabilityMonitor);
					break;
				case CDH_Inquire:
					String inquireId = (String) map.get("inquireId");
					list = dao.doQuery(
							toListCnd("['eq',['$','a.inquireId'],['s','"
									+ inquireId + "']]"), null, BSCHISEntryNames.CDH_Inquire);
					break;
				case MDC_DiabetesRecord:
					list = dao.doQuery(listCnd, "a.createDate desc",
							BSCHISEntryNames.MDC_DiabetesRecord);
					break;
				case MDC_HypertensionRecord:
					list = dao.doQuery(listCnd, "a.createDate desc",
							BSCHISEntryNames.MDC_HypertensionRecord);
					break;
				case CVD_AssessRegister:
					inquireId = (String) map.get("inquireId");
					list = dao.doQuery(
							toListCnd("['eq',['$','a.inquireId'],['s','"
									+ inquireId + "']]"), "a.inputDate desc",
									BSCHISEntryNames.CVD_AssessRegister);
					break;
				case MHC_PregnantRecord:
					String pregnantId = (String) map
							.get("MHC_PregnantRecord.pregnantId");
					list = dao.doQuery(
							toListCnd("['eq',['$','a.pregnantId'],['s','"
									+ pregnantId + "']]"), "a.createDate desc",
									BSCHISEntryNames.MHC_PregnantRecord);
					break;
				case MHC_FirstVisitRecord:
					pregnantId = (String) map
							.get("MHC_PregnantRecord.pregnantId");
					list = dao.doQuery(
							toListCnd("['eq',['$','a.pregnantId'],['s','"
									+ pregnantId + "']]"), null,
									BSCHISEntryNames.MHC_FirstVisitRecord);
					break;
				case MHC_PregnantWomanIndex:
					pregnantId = (String) map
							.get("MHC_PregnantRecord.pregnantId");
					list = dao.doQuery(
							toListCnd("['eq',['$','a.pregnantId'],['s','"
									+ pregnantId + "']]"), "a.indexCode desc",
									BSCHISEntryNames.MHC_PregnantWomanIndex);
					break;
				case MHC_PostnatalVisitInfo:
					pregnantId = (String) map
							.get("MHC_PregnantRecord.pregnantId");
					list = dao.doQuery(
							toListCnd("['eq',['$','a.pregnantId'],['s','"
									+ pregnantId + "']]"),
							"a.visitDate desc,a.visitId desc",
							BSCHISEntryNames.MHC_PostnatalVisitInfo);
					break;
				case MHC_VisitRecord:
					pregnantId = (String) map
							.get("MHC_PregnantRecord.pregnantId");
					cnd = "['eq',['$','a.pregnantId'],['s','" + pregnantId
							+ "']]";
					list = dao.doQuery(toListCnd(cnd), "a.empiId desc",
							BSCHISEntryNames.MHC_VisitRecord);
					break;
				case MHC_BabyVisitInfo:
					pregnantId = (String) map
							.get("MHC_PregnantRecord.pregnantId");
					String babyCnd = "['eq',['$','pregnantId'],['s','"
							+ pregnantId + "']]";
					list = dao.doQuery(toListCnd(babyCnd), null,
							BSCHISEntryNames.MHC_BabyVisitInfo);
					break;
				case MHC_BabyVisitRecord:
					String visitId = (String) map.get("visitId");
					String visitCnd = "['eq',['$','visitId'],['s','" + visitId
							+ "']]";
					list = dao.doQuery(toListCnd(visitCnd), null,
							BSCHISEntryNames.MHC_BabyVisitRecord);
					break;
				case MDC_TumourRecord:
					String tumourId = (String) map.get("tumourPk");
					cnd = "['eq',['$','tumourId'],['s','" + tumourId + "']]";
					list = dao
							.doQuery(toListCnd(cnd), null, BSCHISEntryNames.MDC_TumourRecord);
					break;
				default:
					break;
				}
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
			}
			sharedData.put(String.valueOf(infoType), list);
		} else {
			list = (List<Map<String, Object>>) sharedData.get(infoType);
		}
		return list;
	}

	public List<?> toListCnd(String cnd) throws PrintException {
		List<?> l;
		try {
			l = CNDHelper.toListCnd(cnd);
		} catch (ExpException e) {
			throw new PrintException(Constants.CODE_EXP_ERROR, e.getMessage());
		}

		return l;
	}

	protected Map<String, Object> getFirstRecord(List<Map<String, Object>> list) {
		Map<String, Object> data = new HashMap<String, Object>();
		if (list.size() > 0) {
			data = list.get(0);
		}
		return data;
	}

	@SuppressWarnings("rawtypes")
	protected void sqlDate2String(Map<String, Object> response) {
		Set set = response.keySet();
		Iterator it = set.iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			Object o = response.get(key);
			if (o instanceof Date) {
				response.put(key, new SimpleDateFormat("yyyy-MM-dd").format(o));
			} else if (o instanceof String) {
				String s = (String) o;
				if (s.length() >= 10) {
					s = s.substring(0, 10);
					if (s.matches("\\d{4}\\-\\d{2}\\-\\d{2}")) {
						response.put(key, s);
					}
				}
			}
		}
	}

	protected void sqlDate2String(List<Map<String, Object>> list) {
		if (list != null && list.size() > 0) {
			for (Map<String, Object> map : list) {
				if (map != null) {
					sqlDate2String(map);
				}
			}
		}
	}

	protected void change2String(Map<String, Object> response) {
		Set<String> set = response.keySet();
		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			Object o = response.get(key);
			if (!(o instanceof Date)) {
				response.put(key,
						String.valueOf(response.get(key) != null ? response
								.get(key) : ""));
			}
		}
	}

	protected void change2String(List<Map<String, Object>> list) {
		if (list != null && list.size() > 0) {
			for (Map<String, Object> map : list) {
				if (map != null) {
					change2String(map);
				}
			}
		}
	}

	protected void changeHaveOrNot(List<Map<String, Object>> list,
			List<String> schemaList) throws ControllerException {
		List<String> needChange = new ArrayList<String>();
		for (String schemaId : schemaList) {
			Schema sc = SchemaController.instance().get(schemaId);
			List<SchemaItem> scItems = sc.getItems();
			for (SchemaItem scItem : scItems) {
				DictionaryIndicator di = scItem.getDic();
				if (di != null) {
					String dicId = di.getId();
					if (dicId != null &&dicId.equals("chis.dictionary.haveOrNot")) {
						needChange.add(scItem.getId());
					}
				}
				// Element el = scItem.defineXML().element("dic");
				// if (el != null) {
				// String dicId = el.attributeValue("id", "");
				// if (dicId.equals("haveOrNot")) {
				// needChange.add(scItem.getId());
				// }
				// }
			}
		}
		for (Map<String, Object> map : list) {
			Iterator<String> it = map.keySet().iterator();
			while (it.hasNext()) {
				String id = it.next();
				if (needChange.contains(id) && map.get(id) != null) {
					if (map.get(id).equals("y")) {
						map.put(id, "2");
					} else if (map.get(id).equals("n")) {
						map.put(id, "1");
					} else {
						map.put(id, "");
					}
				}
			}
		}
	}

	protected void changeHaveOrNot(Map<String, Object> map,
			List<String> schemaList) throws ControllerException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list.add(map);
		changeHaveOrNot(list, schemaList);
	}

	protected void changeHaveOrNot(Map<String, Object> map, String schema)
			throws ControllerException {
		List<String> list = new ArrayList<String>();
		list.add(schema);
		changeHaveOrNot(map, list);
	}

	protected void changeHaveOrNot(List<Map<String, Object>> map, String schema)
			throws ControllerException {
		List<String> list = new ArrayList<String>();
		list.add(schema);
		changeHaveOrNot(map, list);
	}

	protected void fillData(Map<String, Object> map, String schema)
			throws PrintException {
		if (map == null) {
			map = new HashMap<String, Object>();
		}
		Schema sc = null;
		try {
			sc = SchemaController.instance().get(schema);
		} catch (ControllerException e) {
			throw new PrintException(Constants.CODE_EXP_ERROR, e.getMessage());
		}
		List<SchemaItem> scItems = sc.getItems();
		for (SchemaItem schemaItem : scItems) {
			String id = schemaItem.getId();
			if (schemaItem.getType().equals("string")) {
				map.put(id, "");
			}
		}
	}

	protected String getRecordIdByEmpiId(String empiId2, String recordId,
			String schema, Context ctx) throws PrintException, ServiceException {
		Map<String, Object> data = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String id = null;
		String cnd = "['eq',['$','a.empiId'],['s','" + empiId2 + "']]";
		try {
			list = dao.doQuery(toListCnd(cnd), null, schema);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		data = getFirstRecord(list);
		if (data != null) {
			id = (String) data.get(recordId);
		}
		return id;
	}

	protected String getStringFromInt(Object data) {
		String str = "";
		if (data != null) {
			str = String.valueOf(data);
		}
		return str;
	}

	protected Map<String, Object> changeCombobox(Map<String, Object> map,
			String schemaId) throws PrintException {
		if (map == null) {
			return null;
		}
		Map<String, Object> m = new HashMap<String, Object>();
		Schema sc = null;
		try {
			sc = SchemaController.instance().get(schemaId);
		} catch (ControllerException e) {
			throw new PrintException(Constants.CODE_EXP_ERROR, e.getMessage());
		}
		List<SchemaItem> scItems = sc.getItems();
		for (SchemaItem schemaItem : scItems) {
			boolean f = schemaItem.isCodedValue();
			String id = schemaItem.getId();
			String v = String.valueOf(map.get(id));
			if (f == true && v != null && !v.equals("")) {
				String[] vs = v.split(",");
				for (int i = 0; i < vs.length; i++) {
					String value = vs[i];
					m.put(id + value, "√");
				}
			}
		}
		m.putAll(map);
		return m;
	}

	public void getDao(Context ctx) {
		dao = new BaseDAO();
	}

}
