package chis.source.print.instance;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import ctd.print.IHandler;
import ctd.print.PrintException;
import chis.source.BSCHISEntryNames;
import chis.source.PersistentDataOperationException;
import chis.source.print.base.BSCHISPrint;
import ctd.util.S;
import ctd.util.context.Context;

/**
 * 精神病个人信息 报表打印
 * 
 * @author chenxr
 */
public class PsychosisRecordFile extends BSCHISPrint implements IHandler {

	public void getParameters(Map<String, Object> request,
			Map<String, Object> para, Context ctx) throws PrintException {
		String empiId = (String) request.get("empiId");
		getDao(ctx);
		String cnd = "['eq',['$','a.empiId'],['s','" + empiId + "']]";
		List<Map<String, Object>> list = null;
		List<Map<String, Object>> mpilist = null;
		List<Map<String, Object>> ehrlist = null;
		try {
			list = dao.doQuery(toListCnd(cnd), "a.phrId desc",
					BSCHISEntryNames.PSY_PsychosisRecord);
			mpilist = dao.doQuery(toListCnd("['eq',['$','a.empiId'],['s','"
					+ empiId + "']]"), "a.createDate desc",
					BSCHISEntryNames.EHR_HealthRecord);
			ehrlist = dao.doQuery(toListCnd("['eq',['$','a.empiId'],['s','"
					+ empiId + "']]"), "a.createTime desc",
					BSCHISEntryNames.MPI_DemographicInfo);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy 年 MM 月 dd 日");
		if (list != null && list.size() > 0) {
			Map<String, Object> psy = list.get(0);
			para.putAll(psy);
			para.putAll(mpilist.get(0));
			para.putAll(ehrlist.get(0));
			para.put("personName",
					para.get("personName") != null ? para.get("personName")
							: "");
			para.put("phrId", psy.get("phrId") != null ? psy.get("phrId") : "");
			para.put("guardianName",
					psy.get("guardianName") != null ? psy.get("guardianName")
							: "");
			para.put(
					"guardianRelation",
					psy.get("guardianRelation_text") != null ? psy
							.get("guardianRelation_text") : "");
			para.put(
					"guardianAddress",
					psy.get("guardianAddress_text") != null ? psy
							.get("guardianAddress_text") : "");
			para.put("guardianPhone",
					psy.get("guardianPhone") != null ? psy.get("guardianPhone")
							: "");
			para.put("icDate",
					psy.get("icDate") != null ? sdf.format(psy.get("icDate"))
							: "");
			para.put(
					"diseasedTime",
					psy.get("diseasedTime") != null ? sdf.format(psy
							.get("diseasedTime")) : "");
			String pastSymptom = (String) psy.get("pastSymptom");
			String[] psKeys = pastSymptom.split(",");
			for (int i = 0; i < psKeys.length; i++) {
				int key = Integer.parseInt(psKeys[i]);
				if (key == 99) {
					para.put("pastSymptom12", 12);
				} else {
					para.put("pastSymptom" + key, key);
				}
			}
			String pastClinic = (String) (psy.get("pastClinic") != null ? psy
					.get("pastClinic") : "");
			Date firstCureTime = (Date) psy.get("firstCureTime");
			String fCT = firstCureTime != null ? sdf.format(firstCureTime) : "";
			para.put("pastClinic", pastClinic);
			para.put("firstCureTime", fCT);
			para.put(
					"recentDiagnoseTime",
					psy.get("recentDiagnoseTime") != null ? sdf.format(psy
							.get("recentDiagnoseTime")) : "");
			para.put(
					"recentTreatResult",
					psy.get("recentTreatResult") != null ? psy
							.get("recentTreatResult") : "");
			String familySocialImpact = (String) psy.get("familySocialImpact");
			if(S.isNotEmpty(familySocialImpact)){
				String[] fses = familySocialImpact.split(",");
				for(int i=0,len=fses.length;i<len;i++){
					para.put("familySocialImpact"+fses[i], fses[i]);
				}
			}
			para.put("closeDoor",
					psy.get("closeDoor") != null ? psy.get("closeDoor") : "");
			para.put(
					"createDate",
					psy.get("createDate") != null ? sdf.format(psy
							.get("createDate")) : "");
			para.put(
					"manaDoctorId",
					psy.get("manaDoctorId_text") != null ? psy
							.get("manaDoctorId_text") : "");
			String VNCLinkMan = (String) (psy.get("VNCLinkMan") != null ? psy
					.get("VNCLinkMan") : "");
			String VNCLinkPhone = (String) (psy.get("VNCLinkPhone") != null ? psy
					.get("VNCLinkPhone") : "");
			para.put("VNCLink", "村(居)委会联系人: " + VNCLinkMan + "     联系电话:"
					+ VNCLinkPhone);
		}
		sqlDate2String(para);
	}

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {

	}

}
