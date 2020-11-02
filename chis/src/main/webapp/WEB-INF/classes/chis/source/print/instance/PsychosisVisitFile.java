package chis.source.print.instance;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import ctd.print.IHandler;
import ctd.print.PrintException;
import chis.source.BSCHISEntryNames;
import chis.source.Constants;
import chis.source.PersistentDataOperationException;
import chis.source.print.base.BSCHISPrint;
import ctd.service.core.ServiceException;
import ctd.util.S;
import ctd.util.context.Context;

/**
 * 重性精神疾病患者随访服务记录表 打印
 * 
 * @author chenxr
 */
public class PsychosisVisitFile extends BSCHISPrint implements IHandler {

	public void getParameters(Map<String, Object> request,
			Map<String, Object> para, Context ctx) throws PrintException {
		getDao(ctx);
		String empiId = (String) request.get("empiId");
		String cnd = "['eq',['$','a.empiId'],['s','" + empiId + "']]";
		List<Map<String, Object>> list = null;
		Map<String, Object> parameters = null;
		try {
			parameters = getFirstRecord(getBaseInfo(MPI_DemographicInfo,
					request, ctx));
			list = dao.doQuery(toListCnd(cnd), null,
					BSCHISEntryNames.PSY_PsychosisVisit);
		} catch (PersistentDataOperationException e1) {
			throw new PrintException(Constants.CODE_EXP_ERROR, e1.getMessage());
		} catch (ServiceException e) {
			throw new PrintException(Constants.CODE_EXP_ERROR, e.getMessage());
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy 年 MM 月 dd 日");
		para.put(
				"personName",
				parameters.get("personName") != null ? parameters
						.get("personName") : "");
		if (list.size() > 0) {
			Map<String, Object> psy = list.get(0);
			para.putAll(psy);

			para.put("phrId", psy.get("phrId") != null ? psy.get("phrId") : "");
			para.put(
					"visitDate",
					psy.get("visitDate") != null ? sdf.format(psy
							.get("visitDate")) : "");
			String familySocialImpact = (String) psy.get("familySocialImpact");
			if (S.isNotEmpty(familySocialImpact)) {
				String[] fses = familySocialImpact.split(",");
				for (int i = 0, len = fses.length; i < len; i++) {
					para.put("familySocialImpact" + fses[i], fses[i]);
				}
			}
			String symptom = (String) psy.get("symptom");
			String[] sKeys = symptom.split(",");
			for (int i = 0; i < sKeys.length; i++) {
				int key = Integer.parseInt(sKeys[i]);
				if (key == 99) {
					para.put("symptom12", 12);
				} else {
					para.put("symptom" + key, key);
				}
			}
			para.put("insight", psy.get("insight") != null ? psy.get("insight")
					: "");
			para.put("sleepQuality",
					psy.get("sleepQuality") != null ? psy.get("sleepQuality")
							: "");
			para.put("eatQuality",
					psy.get("eatQuality") != null ? psy.get("eatQuality") : "");
			para.put("liveQuality",
					psy.get("liveQuality") != null ? psy.get("liveQuality")
							: "");
			para.put("houseWork",
					psy.get("houseWork") != null ? psy.get("houseWork") : "");
			para.put(
					"productiveLabor",
					psy.get("productiveLabor") != null ? psy
							.get("productiveLabor") : "");
			para.put("study", psy.get("study") != null ? psy.get("study") : "");
			para.put("social", psy.get("social") != null ? psy.get("social")
					: "");
			para.put("medicine",
					psy.get("medicine") != null ? psy.get("medicine") : "");
			String adverseReactions = (String) (psy.get("adverseReactions") != null ? psy
					.get("adverseReactions") : "");
			if ("n".equals(adverseReactions)) {
				para.put("adverseReactionsKey", "1");
			} else if ("y".equals(adverseReactions)) {
				para.put("adverseReactionsKey", "2");
			} else {
				para.put("adverseReactionsKey", "");
			}
			para.put("treatment",
					psy.get("treatment") != null ? psy.get("treatment") : "");
			para.put("visitType",
					psy.get("visitType") != null ? psy.get("visitType") : "");
			String referral = (String) (psy.get("referral") != null ? psy
					.get("referral") : "");
			if (referral.equals("y")) {
				para.put("referral", "2");
			} else if (referral.equals("n")) {
				para.put("referral", "1");
			}
			String healing = (String) (psy.get("healing") != null ? psy
					.get("healing") : "");
			String[] hs = healing.split(",");
			for (int i = 0, len = hs.length; i < len; i++) {
				if (S.isNotEmpty(hs[i])) {
					int hInt = Integer.parseInt(hs[i]);
					para.put("healing" + hInt, hInt);
				}
			}
			para.put("riskFactor",
					psy.get("riskFactor") != null ? psy.get("riskFactor") : "");
			para.put(
					"lastHospitalizationTime",
					psy.get("lastHospitalizationTime") != null ? sdf.format(psy
							.get("lastHospitalizationTime")) : "");
			para.put(
					"nextDate",
					psy.get("nextDate") != null ? sdf.format(psy
							.get("nextDate")) : "");
			para.put(
					"visitDoctor",
					psy.get("visitDoctor_text") != null ? psy
							.get("visitDoctor_text") : "");
			para.put("labCheckup",
					psy.get("labCheckup") != null ? psy.get("labCheckup") : "");

			String hospitalization = (String) (psy.get("hospitalization") != null ? psy
					.get("hospitalization") : "");
			para.put("hospitalization", hospitalization);
			String visitId = psy.get("visitId").toString();
			List<Map<String, Object>> list2 = null;
			try {
				changeHaveOrNot(para, BSCHISEntryNames.PSY_PsychosisVisit);
				String cnd2 = "['eq',['$','a.visitId'],['s','" + visitId
						+ "']]";
				list2 = dao.doQuery(toListCnd(cnd2), null,
						BSCHISEntryNames.PSY_PsychosisVisitMedicine);
			} catch (Exception e) {
				throw new PrintException(Constants.CODE_EXP_ERROR,
						e.getMessage());
			}
			for (int i = 0; i < list2.size(); i++) {
				Map<String, Object> pvm = list2.get(i);
				if (i > 2) {
					break;
				}
				para.put("medicineName" + i, pvm.get("medicineName"));
				para.put(
						"medicineFrequency" + i,
						pvm.get("medicineFrequency") != null ? pvm
								.get("medicineFrequency") : "");
				para.put(
						"medicineDosage" + i,
						pvm.get("medicineDosage") != null ? pvm
								.get("medicineDosage") : "");
				para.put(
						"medicineUnit" + i,
						pvm.get("medicineUnit") != null ? pvm
								.get("medicineUnit") : "");

			}
		}
		sqlDate2String(para);
	}

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {

	}

}
