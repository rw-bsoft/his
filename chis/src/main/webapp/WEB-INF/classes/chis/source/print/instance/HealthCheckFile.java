package chis.source.print.instance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ctd.print.IHandler;
import ctd.print.PrintException;
import chis.source.BSCHISEntryNames;
import chis.source.PersistentDataOperationException;
import chis.source.print.base.BSCHISPrint;
import ctd.service.core.ServiceException;
import ctd.util.S;
import ctd.util.context.Context;

public class HealthCheckFile extends BSCHISPrint implements IHandler {

	protected String empiId;

	protected String healthCheck;

	protected Context ctx;

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		empiId = (String) request.get("empiId");
		healthCheck = (String) request.get("healthCheck");
		getDao(ctx);
		String cnd = "";
		if (healthCheck != null) {
			cnd = "['eq',['$','a.healthCheck'],['s','" + healthCheck + "']]";
		} else {
			cnd = "['eq',['$','a.empiId'],['s','" + empiId + "']]";
		}
		List<Map<String, Object>> list = null;
		try {
			list = dao.doQuery(toListCnd(cnd), null,
					BSCHISEntryNames.HC_HealthCheck);
		} catch (PersistentDataOperationException e1) {
			e1.printStackTrace();
		}
		Map<String, Object> data = new HashMap<String, Object>();
		if (list.size() > 0) {
			data = list.get(0);
			if (healthCheck == null) {
				healthCheck = (String) data.get("healthCheck");
			}
			String cerebrovascularDiseases = (String) data.get("cerebrovascularDiseases") ;
			if (S.isNotEmpty(cerebrovascularDiseases)) {
				String[] cds = cerebrovascularDiseases.split(",");
				for(int i=0,len=cds.length;i<len;i++){
					data.put("cerebrovascularDiseases"+cds[i], cds[i]);
				}
			}
			String kidneyDiseases = (String) data.get("kidneyDiseases");
			if(S.isNotEmpty(kidneyDiseases)){
				String[] kds = kidneyDiseases.split(",");
				for(int i=0,len=kds.length;i<len;i++){
					data.put("kidneyDiseases"+kds[i], kds[i]);
				}
			}
			String heartDisease = (String) data.get("heartDisease");
			if(S.isNotEmpty(heartDisease)){
				String[] hds = heartDisease.split(",");
				for(int i=0,len=hds.length;i<len;i++){
					data.put("heartDisease"+hds[i], hds[i]);
				}
			}
			String VascularDisease = (String) data.get("VascularDisease");
			if(S.isNotEmpty(VascularDisease)){
				String[] vds = VascularDisease.split(",");
				for(int i=0,len=vds.length;i<len;i++){
					data.put("VascularDisease"+vds[i], vds[i]);
				}
			}
			String eyeDiseases = (String) data.get("eyeDiseases");
			if(S.isNotEmpty(eyeDiseases)){
				String[] eds = eyeDiseases.split(",");
				for(int i=0,len=eds.length;i<len;i++){
					data.put("eyeDiseases"+eds[i], eds[i]);
				}
			}
			response.putAll(data);
			response.put("manaDoctorId", data.get("manaDoctorId_text"));
		}
		// 根据empiId获得姓名 身份证 责任医生
		List<Map<String, Object>> info = null;
		try {
			info = getBaseInfo(MPI_DemographicInfo, request, ctx);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		if (info.size() > 0) {
			Map<String, Object> person = info.get(0);
			response.put("personName", person.get("personName"));
			response.put("idCard", person.get("idCard"));
		}
		// //获得查体数据
		String ctcnd = "['eq',['$','healthCheck'],['s','" + healthCheck + "']]";
		List<Map<String, Object>> ctlist = null;
		try {
			ctlist = dao.doQuery(toListCnd(ctcnd), null,
					BSCHISEntryNames.HC_Examination);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		Map<String, Object> ctresponse = new HashMap<String, Object>();
		if (ctlist.size() > 0) {
			ctresponse = ctlist.get(0);
		}
		response.putAll(ctresponse);
		//
		// 获得生活方式数据
		String fscnd = "['eq',['$','healthCheck'],['s','" + healthCheck + "']]";
		List<Map<String, Object>> fslist = null;
		try {
			fslist = dao.doQuery(toListCnd(fscnd), null,
					BSCHISEntryNames.HC_LifestySituation);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		Map<String, Object> fsresponse = new HashMap<String, Object>();
		if (fslist.size() > 0) {
			fsresponse = fslist.get(0);
		}
		String dietaryHabit = (String) fsresponse.get("dietaryHabit");
		if(S.isNotEmpty(dietaryHabit)){
			String[] dhes = dietaryHabit.split(",");
			for(int i=0,len=dhes.length;i<len;i++){
				fsresponse.put("dietaryHabit"+dhes[i], dhes[i]);
			}
		}
		String mainDrinkingVvarieties = (String) fsresponse.get("mainDrinkingVvarieties");
		if(S.isNotEmpty(mainDrinkingVvarieties)){
			String[] mdves = mainDrinkingVvarieties.split(",");
			for(int i=0,len=mdves.length;i<len;i++){
				fsresponse.put("mainDrinkingVvarieties"+mdves[i], mdves[i]);
			}
		}
		if (fsresponse.get("mainDrinkingVvarieties") != null) {
			String mdv = (String) fsresponse.get("mainDrinkingVvarieties");
			String md = "";
			if (mdv.contains("1") || mdv.contains("2")) {
				md += "1 ";
			}
			if (mdv.contains("3")) {
				md += "2 ";
			}
			if (mdv.contains("5")) {
				md += "3 ";
			}
			if (mdv.contains("4")) {
				md += "4 ";
			}
			if (mdv.contains("9")) {
				md += "9 ";
			}
			fsresponse.put("mainDrinkingVvarieties", md);
		}
		response.putAll(fsresponse);
		// 辅助检查
		String fzcnd = "['eq',['$','healthCheck'],['s','" + healthCheck + "']]";
		List<Map<String, Object>> fzlist = null;
		try {
			fzlist = dao.doQuery(toListCnd(fzcnd), null,
					BSCHISEntryNames.HC_AccessoryExamination);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		Map<String, Object> fzresponse = new HashMap<String, Object>();
		if (fzlist.size() > 0) {
			fzresponse = fzlist.get(0);
		}
		String denture = (String) fzresponse.get("denture");
		if(S.isNotEmpty(denture)){
			if("2".equals(denture)){
				fzresponse.put("QC_leftUp", fzresponse.get("leftUp"));
				fzresponse.put("QC_leftDown", fzresponse.get("leftDown"));
				fzresponse.put("QC_rightUp", fzresponse.get("rightUp"));
				fzresponse.put("QC_rightDown", fzresponse.get("rightDown"));
			}else if("3".equals(denture)){
				fzresponse.put("QuC_leftUp", fzresponse.get("leftUp"));
				fzresponse.put("QuC_leftDown", fzresponse.get("leftDown"));
				fzresponse.put("QuC_rightUp", fzresponse.get("rightUp"));
				fzresponse.put("QuC_rightDown", fzresponse.get("rightDown"));
			}else if("4".equals(denture)){
				fzresponse.put("YC_leftUp", fzresponse.get("leftUp"));
				fzresponse.put("YC_leftDown", fzresponse.get("leftDown"));
				fzresponse.put("YC_rightUp", fzresponse.get("rightUp"));
				fzresponse.put("YC_rightDown", fzresponse.get("rightDown"));
			}
		}
		response.putAll(fzresponse);
		// 健康评价
		String pjcnd = "['eq',['$','healthCheck'],['s','" + healthCheck + "']]";
		List<Map<String, Object>> pjlist = null;
		try {
			pjlist = dao.doQuery(toListCnd(pjcnd), null,
					BSCHISEntryNames.HC_HealthAssessment);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		Map<String, Object> pjresponse = new HashMap<String, Object>();
		if (pjlist.size() > 0) {
			pjresponse = pjlist.get(0);
		}
		String recognize = (String) pjresponse.get("recognize");
		if (recognize != null) {
			String[] recArr = recognize.split(",");
			for (int i = 0; i < recArr.length; i++) {
				pjresponse.put("recognize" + recArr[i].substring(0, 1),
						recArr[i].substring(1, 2));
			}
		}
		String mana = (String) pjresponse.get("mana");
		if(S.isNotEmpty(mana)){
			String[] manaArray = mana.split(",");
			for(int i=0,len=manaArray.length;i<len;i++){
				pjresponse.put("mana"+manaArray[i], manaArray[i]);
			}
		}
		String riskfactorsControl = (String) pjresponse.get("riskfactorsControl");
		if(S.isNotEmpty(riskfactorsControl)){
			String[] riskfactorsControlArray = riskfactorsControl.split(",");
			for(int i=0,len=riskfactorsControlArray.length;i<len;i++){
				pjresponse.put("riskfactorsControl"+riskfactorsControlArray[i], riskfactorsControlArray[i]);
			}
		}
		response.putAll(pjresponse);
		// 住院情况
		cnd = "['eq',['$','healthCheck'],['s','" + healthCheck + "']]";
		List<Map<String, Object>> hospital = null;
		try {
			hospital = dao.doQuery(toListCnd(cnd), "situationId",
					BSCHISEntryNames.HC_InhospitalSituation);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		int k = 1, j = 1;
		for (int i = 0; i < hospital.size(); i++) {
			Map<String, Object> m = hospital.get(i);
			if (((String) m.get("type")).equals("1")) {
				for (String key : m.keySet()) {
					response.put(key + k, m.get(key));
				}
				k++;
			} else {
				for (String key : m.keySet()) {
					response.put((key + 2) + j, m.get(key));
				}
				j++;
			}
		}
		// 用药情况
		cnd = "['eq',['$','healthCheck'],['s','" + healthCheck + "']]";
		List<Map<String, Object>> medic = null;
		try {
			medic = dao.doQuery(toListCnd(cnd), null,
					BSCHISEntryNames.HC_MedicineSituation);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		// int f = 1;
		for (int i = 0; i < medic.size(); i++) {
			Map<String, Object> m = medic.get(i);
			// if (((String) m.get("isInsulin")).equals("0")){
			for (String key : m.keySet()) {
				response.put(key + (i + 1), m.get(key));
			}
			// f++;
			// }else {
			// response.put("isInsulin", m.get("descr"));
			// }
		}
		// 计划免疫
		cnd = "['eq',['$','healthCheck'],['s','" + healthCheck + "']]";
		List<Map<String, Object>> noni = null;
		try {
			noni = dao.doQuery(toListCnd(cnd), null,
					BSCHISEntryNames.HC_NonimmuneInoculation);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		for (int ii = 0; ii < noni.size(); ii++) {
			Map<String, Object> m = noni.get(ii);
			for (String key : m.keySet()) {
				response.put(key + ii, m.get(key));
			}
		}
		change2String(response);
		sqlDate2String(response);
	}

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {

	}
}
