package chis.source.print.instance;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;

import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;
import chis.source.BSCHISEntryNames;
import chis.source.Constants;
import chis.source.PersistentDataOperationException;
import chis.source.print.base.BSCHISPrint;
import ctd.dictionary.Dictionary;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class PersonalFile extends BSCHISPrint implements IHandler {

	private String empiId;

	// 家族档案
	private Map<String, Object> getSampleFamilyMap(Map<String, Object> map,
			Map<String, Object> sharedData, Context ctx) throws PrintException,
			ServiceException {
		Map<String, Object> parameters = getFirstRecord(getBaseInfo(
				EHR_FamilyRecord, map, ctx));
		if (parameters.containsKey("familyId")) {
			List<Map<String, Object>> list = getBaseInfo(
					Family_EHR_HealthRecord, map, ctx);
			Iterator<Map<String, Object>> it = list.iterator();
			int count = 1;
			while (it.hasNext()) {
				if (count > 10) {
					break;
				}
				Map<String, Object> d = it.next();
				List<Map<String, Object>> mpilist = null;
				try {
					mpilist = dao.doQuery(
							toListCnd("['eq',['$','a.empiId'],['s','"
									+ d.get("empiId") + "']]"),
							"a.createTime desc",
							BSCHISEntryNames.MPI_DemographicInfo);
				} catch (PersistentDataOperationException e) {
					e.printStackTrace();
				}
				d.putAll(mpilist.get(0));
				parameters.put("personName" + count, d.get("personName"));
				parameters.put("sexCode_text" + count, d.get("sexCode_text"));
				parameters.put("birthday" + count, d.get("birthday"));
				parameters.put("idCard" + count, d.get("idCard"));
				parameters.put("relaCode_text" + count, d.get("relaCode_text"));
				count++;
			}
			list = getBaseInfo(EHR_FamilyProblem, map, ctx);
			it = list.iterator();
			StringBuilder familyProblem = new StringBuilder();
			while (it.hasNext()) {
				Map<String, Object> d = it.next();
				familyProblem.append(d.get("problemName"));
				familyProblem.append(":");
				familyProblem.append(d.get("description"));
				Object happenDate = d.get("happenDate");
				if (happenDate != null) {
					familyProblem.append("(");
					familyProblem.append(happenDate);
					familyProblem.append(")");
				}
				familyProblem.append("\r\n");
			}
			parameters.put("familyProblem", familyProblem.toString());
		}
		return parameters;
	}

	// 个人基本信息
	private Map<String, Object> getSampleInfoMap(Map<String, Object> map,
			Map<String, Object> sharedData, Context ctx) throws PrintException,
			ServiceException, PersistentDataOperationException {
		Map<String, Object> parameters = getFirstRecord(getBaseInfo(
				EHR_HealthRecord, map, ctx));
		parameters.putAll(getFirstRecord(getBaseInfo(MPI_DemographicInfo, map,
				ctx)));
		if ("y".equals(parameters.get("masterFlag"))) {
			parameters.putAll(getFirstRecord(getBaseInfo(EHR_FamilyRecord, map,
					ctx)));
		} else {
			List listCnd = toListCnd("['eq',['$','a.empiId'],['s','" + empiId
					+ "']]");
			List<Map<String, Object>> list = dao.doQuery(listCnd, "a.middleId",
					BSCHISEntryNames.EHR_FamilyMiddle);
			parameters.putAll(getFirstRecord(list));
		}
		if (parameters.containsKey("nationCode")) {
			if ("01".equals(parameters.get("nationCode"))) {
				parameters.put("nationCode", "1");
				parameters.put("nationCode_text", "");
			} else {
				parameters.put("nationCode", "2");
				parameters.put("nationCode_text",
						parameters.get("nationCode_text"));
			}
		}
		if (parameters.containsKey("fuelType")) {
			if ("9".equals(parameters.get("fuelType"))) {
				parameters.put("fuelType", "6");
			}
		}
		if (parameters.containsKey("waterSourceCode")) {
			if ("9".equals(parameters.get("waterSourceCode"))) {
				parameters.put("waterSourceCode", "6");
			}
		}
		if (parameters.containsKey("cookAirTool")) {
			if ("9".equals(parameters.get("cookAirTool"))) {
				parameters.put("cookAirTool", "");
			}
		}
		if (parameters.containsKey("workCode")) {
			if ("X".equals(parameters.get("workCode"))) {
				parameters.put("workCode", "7");
			} else if ("Y".equals(parameters.get("workCode"))) {
				parameters.put("workCode", "8");
			} else if ("0".equals(parameters.get("workCode"))) {
				parameters.put("workCode", "1");
			} else if ("1/2".equals(parameters.get("workCode"))) {
				parameters.put("workCode", "2");
			} else if ("9-9".equals(parameters.get("workCode"))) {
				parameters.put("workCode", "6");
			}
		}
		if (parameters.containsKey("educationCode")) {
			String educationCode = (String) parameters.get("educationCode");
			if (educationCode != null) {
				if ("91".equals(educationCode)) {
					parameters.put("educationCode", "6");
				} else {
					educationCode = educationCode.substring(0, 1);
					if (educationCode.equals("1") || educationCode.equals("2")
							|| educationCode.equals("3")) {
						parameters.put("educationCode", "5");
					} else if (educationCode.equals("4")
							|| educationCode.equals("6")) {
						parameters.put("educationCode", "4");
					} else if (educationCode.equals("7")) {
						parameters.put("educationCode", "3");
					} else if (educationCode.equals("8")) {
						parameters.put("educationCode", "2");
					} else {
						parameters.put("educationCode", "1");
					}
				}
			}
		}
		if (parameters.containsKey("maritalStatusCode")) {
			String maritalStatusCode = (String) parameters
					.get("maritalStatusCode");
			if (maritalStatusCode != null) {
				maritalStatusCode = maritalStatusCode.substring(0,1);
				if (maritalStatusCode.equals("9")) {
					maritalStatusCode = "5";
				}
				parameters.put("maritalStatusCode", maritalStatusCode);
			}
		}
		if (parameters.containsKey("insuranceCode")) {
			String insuranceCode = (String) parameters.get("insuranceCode");
			if (insuranceCode != null) {
				insuranceCode = insuranceCode.substring(1, 2);
				if (insuranceCode.equals("9")) {
					insuranceCode = "8";
				}
				parameters.put("insuranceCode", insuranceCode);
			}
		}
		List<Map<String, Object>> list = null;
		try {
			list = dao.doQuery(toListCnd("['eq',['$','a.empiId'],['s','"
					+ empiId + "']]"), "a.diseaseCode",
					BSCHISEntryNames.EHR_PastHistory);
		} catch (PersistentDataOperationException e1) {
			throw new PrintException(Constants.CODE_EXP_ERROR, e1.getMessage());
		}
		Iterator<Map<String, Object>> it = list.iterator();
		String allergy1 = "", allergy2 = "", allergy3 = "", allergy4 = "", allergy_text = "";
		String disease1 = "", disease2 = "", disease3 = "", disease4 = "";
		String disease5 = "", disease6 = "", operation = "", traumatism = "";
		String disease1Date = "", disease2Date = "", disease3Date = "", disease4Date = "", disease5Date = "", disease6Date = "";
		String transfuse = "", operation1 = "", traumatism1 = "", transfuse1 = "";
		String operation2 = "", traumatism2 = "", transfuse2 = "", father = "";
		String operation1Date = "", operation2Date = "", traumatism1Date = "", traumatism2Date = "", transfuse1Date = "", transfuse2Date = "";
		String mother = "", brother = "", son = "", genetic = "", disability_text = "";
		String disability1 = "", disability2 = "", disability3 = "", disability4 = "", disability5 = "", disability6 = "";
		String reveal1 = "", reveal2 = "", reveal3 = "";
		String cancer = "", occupational = "", other = "", geneticCode = "";
		String father1 = "", father2 = "", father3 = "", father4 = "", father5 = "", father6 = "";
		String mother1 = "", mother2 = "", mother3 = "", mother4 = "", mother5 = "", mother6 = "";
		String brother1 = "", brother2 = "", brother3 = "", brother4 = "", brother5 = "", brother6 = "";
		String son1 = "", son2 = "", son3 = "", son4 = "", son5 = "", son6 = "";
		int diseaseCount = 1;
		int operationCount = 1;
		int traumatismCount = 1;
		int transfuseCount = 1;
		while (it.hasNext()) {
			Map<String, Object> record = it.next();
			Object confirmDate1 = record.get("confirmDate");
			Object startDate1 = record.get("startDate");
			String confirmDate = "";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
			if (confirmDate1 != null) {
				confirmDate = sdf.format(confirmDate1);
			} else if (startDate1 != null) {
				confirmDate = sdf.format(startDate1);
			}
			boolean flag = record.get("diseaseCode").equals("0211")
					|| record.get("diseaseCode").equals("0213")
					|| record.get("diseaseCode").equals("0214");
			String familyCode = record.get("diseaseCode").toString()
					.substring(2);
			if (familyCode.equals("99")) {
				familyCode = "12";
			}
			if (familyCode.startsWith("0")) {
				familyCode = familyCode.substring(1);
			}

			if (record.get("pastHisTypeCode").equals("01")) { // 药物过敏史
				if ("".equals(allergy1)) {
					allergy1 = record.get("diseaseCode").toString()
							.substring(3);
					if (record.get("diseaseCode").toString().substring(2)
							.equals("09")) {
						allergy_text = record.get("diseaseText") + "";
						allergy1 = "5";
					}
				} else if ("".equals(allergy2)) {
					allergy2 = record.get("diseaseCode").toString()
							.substring(3);
					if (record.get("diseaseCode").toString().substring(2)
							.equals("09")) {
						allergy_text = record.get("diseaseText") + "";
						allergy2 = "5";
					}
				} else if ("".equals(allergy3)) {
					allergy3 = record.get("diseaseCode").toString()
							.substring(3);
					if (record.get("diseaseCode").toString().substring(2)
							.equals("09")) {
						allergy_text = record.get("diseaseText") + "";
						allergy3 = "5";
					}
				} else if ("".equals(allergy4)) {
					allergy4 = record.get("diseaseCode").toString()
							.substring(3);
					if (record.get("diseaseCode").toString().substring(2)
							.equals("09")) {
						allergy_text = record.get("diseaseText") + "";
						allergy4 = "5";
					}
				}

			} else if (record.get("pastHisTypeCode").equals("02")) { // 疾病
				if (flag == true) {
					continue;
				}
				String code = "";
				if (record.get("diseaseCode").toString().substring(2)
						.equals("99")) {
					code = "13";
				} else if (record.get("diseaseCode").toString().substring(2)
						.equals("98")) {
					code = "11";
				} else {
					code = record.get("diseaseCode").toString().substring(2);
					if (code.startsWith("0")) {
						code = record.get("diseaseCode").toString()
								.substring(3);
					}
				}

				if (diseaseCount == 1) {
					disease1 = code;
					disease1Date = (confirmDate == null ? "不详" : confirmDate);
				} else if (diseaseCount == 2) {
					disease2 = code;
					disease2Date = (confirmDate == null ? "不详" : confirmDate);
				} else if (diseaseCount == 3) {
					disease3 = code;
					disease3Date = (confirmDate == null ? "不详" : confirmDate);
				} else if (diseaseCount == 4) {
					disease4 = code;
					disease4Date = (confirmDate == null ? "不详" : confirmDate);
				} else if (diseaseCount == 5) {
					disease5 = code;
					disease5Date = (confirmDate == null ? "不详" : confirmDate);
				} else if (diseaseCount == 6) {
					disease6 = code;
					disease6Date = (confirmDate == null ? "不详" : confirmDate);
				}
				if (record.get("diseaseCode").equals("0206")) {
					cancer = (String) record.get("diseaseText");
				}
				if (record.get("diseaseCode").equals("0212")) {
					occupational = (String) record.get("diseaseText");
				}
				if (record.get("diseaseCode").equals("0299")) {
					other = (String) record.get("diseaseText");
				}
				diseaseCount++;
			} else if (record.get("pastHisTypeCode").equals("03")) { // 手术
				if (operationCount == 1 && record.get("diseaseText") != null) {
					operation1 = record.get("diseaseText") + "";
					operation1Date = (confirmDate == null ? "不详" : confirmDate);
				} else if (operationCount == 2
						&& record.get("diseaseText") != null) {
					operation2 = record.get("diseaseText") + "";
					operation2Date = (confirmDate == null ? "不详" : confirmDate);
				}
				operation += "  "
						+ record.get("diseaseCode").toString().substring(3);
				operationCount++;
			} else if (record.get("pastHisTypeCode").equals("06")) { // 外伤
				if (traumatismCount == 1 && record.get("diseaseText") != null) {
					traumatism1 = record.get("diseaseText") + "";
					traumatism1Date = (confirmDate == null ? "不详" : confirmDate);
				} else if (traumatismCount == 2
						&& record.get("diseaseText") != null) {
					traumatism2 = record.get("diseaseText") + "";
					traumatism2Date = (confirmDate == null ? "不详" : confirmDate);
				}
				traumatism += "  "
						+ record.get("diseaseCode").toString().substring(3);
				traumatismCount++;
			} else if (record.get("pastHisTypeCode").equals("04")) { // 输血
				if (transfuseCount == 1 && record.get("diseaseText") != null) {
					transfuse1 = record.get("diseaseText") + "";
					transfuse1Date = (confirmDate == null ? "不详" : confirmDate);
				} else if (transfuseCount == 2
						&& record.get("diseaseText") != null) {
					transfuse2 = record.get("diseaseText") + "";
					transfuse2Date = (confirmDate == null ? "不详" : confirmDate);
				}
				transfuse += "  "
						+ record.get("diseaseCode").toString().substring(3);
				transfuseCount++;
			} else if (record.get("pastHisTypeCode").equals("07")) { // 父亲
				if ("12".equals(familyCode)) {
					father = "" + record.get("diseaseText");
				}
				if ("".equals(father1)) {
					father1 = familyCode;
				} else if ("".equals(father2)) {
					father2 = familyCode;
				} else if ("".equals(father3)) {
					father3 = familyCode;
				} else if ("".equals(father4)) {
					father4 = familyCode;
				} else if ("".equals(father5)) {
					father5 = familyCode;
				} else if ("".equals(father6)) {
					father6 = familyCode;
				}
			} else if (record.get("pastHisTypeCode").equals("08")) { // 母亲
				if ("12".equals(familyCode)) {
					mother = "" + record.get("diseaseText");
				}
				if ("".equals(mother1)) {
					mother1 = familyCode;
				} else if ("".equals(mother2)) {
					mother2 = familyCode;
				} else if ("".equals(mother3)) {
					mother3 = familyCode;
				} else if ("".equals(mother4)) {
					mother4 = familyCode;
				} else if ("".equals(mother5)) {
					mother5 = familyCode;
				} else if ("".equals(mother6)) {
					mother6 = familyCode;
				}
			} else if (record.get("pastHisTypeCode").equals("09")) { // 兄弟姐妹
				if ("12".equals(familyCode)) {
					brother = "" + record.get("diseaseText");
				}
				if ("".equals(brother1)) {
					brother1 = familyCode;
				} else if ("".equals(brother2)) {
					brother2 = familyCode;
				} else if ("".equals(brother3)) {
					brother3 = familyCode;
				} else if ("".equals(brother4)) {
					brother4 = familyCode;
				} else if ("".equals(brother5)) {
					brother5 = familyCode;
				} else if ("".equals(brother6)) {
					brother6 = familyCode;
				}
			} else if (record.get("pastHisTypeCode").equals("10")) { // 子女
				if ("12".equals(familyCode)) {
					son = "" + record.get("diseaseText");
				}
				if ("".equals(son1)) {
					son1 = familyCode;
				} else if ("".equals(son2)) {
					son2 = familyCode;
				} else if ("".equals(son3)) {
					son3 = familyCode;
				} else if ("".equals(son4)) {
					son4 = familyCode;
				} else if ("".equals(son5)) {
					son5 = familyCode;
				} else if ("".equals(son6)) {
					son6 = familyCode;
				}
			} else if (record.get("pastHisTypeCode").equals("05")) { // 遗传病史
				genetic = record.get("diseaseText") + "";
				geneticCode = record.get("diseaseCode").toString().substring(3);
			} else if (record.get("pastHisTypeCode").equals("11")) { // 残疾
				String code = record.get("diseaseCode").toString().substring(2);
				if (code.equals("99")) {
					code = "8";
					disability_text = record.get("diseaseText") + "";
				} else if (code.equals("08") || code.equals("09")) {
					continue;
				} else {
					code = code.substring(1);
				}
				if ("".equals(disability1)) {
					disability1 = code;
				} else if ("".equals(disability2)) {
					disability2 = code;
				} else if ("".equals(disability3)) {
					disability3 = code;
				} else if ("".equals(disability4)) {
					disability4 = code;
				} else if ("".equals(disability5)) {
					disability5 = code;
				} else if ("".equals(disability6)) {
					disability6 = code;
				}
			} else if (record.get("pastHisTypeCode").equals("12")) {// 暴露史
				if ("".equals(reveal1)) {
					reveal1 = record.get("diseaseCode").toString().substring(3);
				} else if ("".equals(reveal2)) {
					reveal2 = record.get("diseaseCode").toString().substring(3);
				} else if ("".equals(reveal3)) {
					reveal3 = record.get("diseaseCode").toString().substring(3);
				}
			}

		}
		parameters.put("allergy1", allergy1);
		parameters.put("allergy2", allergy2);
		parameters.put("allergy3", allergy3);
		parameters.put("allergy4", allergy4);
		parameters.put("allergy_text", allergy_text);
		parameters.put("disease1", disease1);
		parameters.put("disease2", disease2);
		parameters.put("disease3", disease3);
		parameters.put("disease4", disease4);
		parameters.put("disease5", disease5);
		parameters.put("disease6", disease6);
		parameters.put("disease1Date", disease1Date);
		parameters.put("disease2Date", disease2Date);
		parameters.put("disease3Date", disease3Date);
		parameters.put("disease4Date", disease4Date);
		parameters.put("disease5Date", disease5Date);
		parameters.put("disease6Date", disease6Date);
		parameters.put("operation", operation);
		parameters.put("traumatism", traumatism);
		parameters.put("transfuse", transfuse);
		parameters.put("operation1", operation1);
		parameters.put("traumatism1", traumatism1);
		parameters.put("transfuse1", transfuse1);
		parameters.put("operation2", operation2);
		parameters.put("traumatism2", traumatism2);
		parameters.put("transfuse2", transfuse2);
		parameters.put("operation1Date", operation1Date);
		parameters.put("traumatism1Date", traumatism1Date);
		parameters.put("transfuse1Date", transfuse1Date);
		parameters.put("operation2Date", operation2Date);
		parameters.put("traumatism2Date", traumatism2Date);
		parameters.put("transfuse2Date", transfuse2Date);
		parameters.put("father", father);
		parameters.put("mother", mother);
		parameters.put("brother", brother);
		parameters.put("son", son);
		parameters.put("father1", father1);
		parameters.put("mother1", mother1);
		parameters.put("brother1", brother1);
		parameters.put("son1", son1);
		parameters.put("father2", father2);
		parameters.put("mother2", mother2);
		parameters.put("brother2", brother2);
		parameters.put("son2", son2);
		parameters.put("father3", father3);
		parameters.put("mother3", mother3);
		parameters.put("brother3", brother3);
		parameters.put("son3", son3);
		parameters.put("father4", father4);
		parameters.put("mother4", mother4);
		parameters.put("brother4", brother4);
		parameters.put("son4", son4);
		parameters.put("father5", father5);
		parameters.put("mother5", mother5);
		parameters.put("brother5", brother5);
		parameters.put("son5", son5);
		parameters.put("father6", father6);
		parameters.put("mother6", mother6);
		parameters.put("brother6", brother6);
		parameters.put("son6", son6);
		parameters.put("genetic", genetic);
		parameters.put("geneticCode", geneticCode);
		parameters.put("disability_text", disability_text);
		parameters.put("disability1", disability1);
		parameters.put("disability2", disability2);
		parameters.put("disability3", disability3);
		parameters.put("disability4", disability4);
		parameters.put("disability5", disability5);
		parameters.put("disability6", disability6);
		parameters.put("reveal1", reveal1);
		parameters.put("reveal2", reveal2);
		parameters.put("reveal3", reveal3);
		parameters.put("cancer", cancer);
		parameters.put("occupational", occupational);
		parameters.put("other", other);
		List<Map<String, Object>> list1 = null;
		try {
			list1 = dao.doQuery(toListCnd("['eq',['$','a.empiId'],['s','"
					+ empiId + "']]"), null, BSCHISEntryNames.EHR_LifeStyle);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		if (list1.size() > 0) {
			Map<String, Object> lifeCtyle = list1.get(0);
			parameters.putAll(lifeCtyle);
		}
		return parameters;
	}

	// 封面
	private Map<String, Object> getSampleCoverMap(Map<String, Object> map,
			Map<String, Object> sharedData, Context ctx) throws PrintException,
			ServiceException {
		Map<String, Object> parameters = getFirstRecord(getBaseInfo(
				MPI_DemographicInfo, map, ctx));
		parameters
				.putAll(getFirstRecord(getBaseInfo(EHR_HealthRecord, map, ctx)));
		Dictionary area;
		try {
			area = DictionaryController.instance().get(
					"chis.dictionary.areaGrid");
		} catch (ControllerException e) {
			throw new PrintException(Constants.CODE_EXP_ERROR, e.getMessage());
		}
		String empiId = (String) parameters.get("empiId");
		String sql="select  c.personname personname,case when length(createunit)>=9 then  (select organizname from sys_organization where organizcode=substr(createunit,0,9)) else '' end  createUnit1_text   from ehr_healthrecord a,sys_organization b,sys_personnel c where a.createuser=c.personid and a.createUnit=b.organizcode and a.empiId=:empiId";
		Session session = (Session) ctx.get(Context.DB_SESSION);
		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("empiId", parameters.get("empiId"));
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		Map<String, Object> l = (Map<String, Object>) query.uniqueResult();
		if (l != null) {
			parameters.put("createUnit1_text",l.get("CREATEUNIT1_TEXT")==null?"":l.get("CREATEUNIT1_TEXT").toString());
			parameters.put("personname",l.get("PERSONNAME")==null?"":l.get("PERSONNAME").toString());
		}
		String regionCode = (String) parameters.get("regionCode");
		if (regionCode != null) {
			if (regionCode.length() >= 2) {
			parameters.put("regionCode7_text", area.getText(regionCode
					.length() == 2 ? regionCode : regionCode
					.substring(0, 2)));
	    	}
			if (regionCode.length() >= 4) {
				parameters.put("regionCode8_text", area.getText(regionCode
						.length() == 4 ? regionCode : regionCode
						.substring(0, 4)));
			}
			if (regionCode.length() >= 6) {
				parameters.put("regionCode9_text", area.getText(regionCode
						.length() == 6 ? regionCode : regionCode
						.substring(0, 6)));
			}
			if (regionCode.length() >= 9) {
				parameters.put("regionCode4_text", area.getText(regionCode
						.length() == 9 ? regionCode : regionCode
						.substring(0, 9)));
			}
			if (regionCode.length() >= 12) {
				parameters.put("regionCode5_text", area.getText(regionCode
						.length() == 12 ? regionCode : regionCode.substring(0,
						12)));
			}
			if (regionCode.length() >= 19) {
				parameters.put("regionCode6_text", area.getText(regionCode
						.length() == 19 ? regionCode : regionCode.substring(0,
						19)));
			}
		}
		parameters.put("title",
				area.getText((String) area.getProperty("rootKey"))
						+ "居民健康档案");
		String str = area.getText((String) area.getProperty("rootKey"))
				+ "卫生局制";
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			sb.append("   ").append(str.charAt(i));
		}
		parameters.put("releaseUnit", sb.toString());
		return parameters;
	}

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		empiId = (String) request.get("empiId");
		getDao(ctx);
		String jrxml = (String) request.get("jrxml");
		try {
			if (jrxml.contains("cover")) {
				response.putAll(getSampleCoverMap(request, response, ctx));
			} else if (jrxml.contains("info")) {
				response.putAll(getSampleInfoMap(request, response, ctx));
			} else if (jrxml.contains("family")) {
				response.putAll(getSampleFamilyMap(request, response, ctx));
			} else if (jrxml.contains("card")) {
				response.putAll(getSampleCardMap(request, response, ctx));
			}
			sqlDate2String(response);
		} catch (Exception e) {
			throw new PrintException(Constants.CODE_PERSISTENT_ERROR,
					e.getMessage());
		}

	}

	private Map<String, Object> getSampleCardMap(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException,
			ServiceException {
		Map<String, Object> parameters = getFirstRecord(getBaseInfo(
				MPI_DemographicInfo, request, ctx));
		parameters.putAll(getFirstRecord(getBaseInfo(EHR_HealthRecord, request,
				ctx)));
		List<Map<String, Object>> list = getBaseInfo(EHR_PastHistory, request,
				ctx);
		String allergy = "", disease = "";
		List<String> ls = new ArrayList<String>();
		ls.add("01");
		ls.add("02");
		ls.add("03");
		ls.add("04");
		ls.add("07");
		ls.add("12");
		ls.add("99");
		for (Map<String, Object> map : list) {
			if (map.containsKey("pastHisTypeCode")
					&& map.get("pastHisTypeCode").equals("01")) {
				allergy += map.get("diseaseText") + "   ";
			}
			if (map.containsKey("pastHisTypeCode")
					&& map.get("pastHisTypeCode").equals("02")) {
				if (ls.contains(map.get("diseaseCode").toString().substring(2))) {
					disease += map.get("diseaseText") + "    ";
				}
			}
		}
		parameters.put("disease", disease);
		parameters.put("allergy", allergy);
		return parameters;
	}

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {

	}
}
