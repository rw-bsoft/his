/**
 * @(#)RevertRecordModel.java Created on 2012-3-20 下午02:39:41
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.phr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.CancellationReason;
import chis.source.dic.RecordStatus;
import chis.source.dic.RevertOrLogout;
import chis.source.service.ServiceCode;
import chis.source.util.CNDHelper;
import ctd.controller.exception.ControllerException;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;

/**
 * @description
 * 
 * @author <a href="mailto:tianj@bsoft.com.cn">田军</a>
 */
public class RevertRecordModel implements BSCHISEntryNames {
	BaseDAO dao = null;

	public RevertRecordModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 查询被注销的相关记录
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> loadAllLogoutedRecords(String empiId,
			int pageSize, int pageNo) throws ModelDataOperationException {

		Map<String, String> map = new HashMap<String, String>();
		map.put(RevertOrLogout.HEALTHRECORD, EHR_HealthRecord);
		map.put(RevertOrLogout.HYPERTENSION, MDC_HypertensionRecord);
		map.put(RevertOrLogout.DIABETES, MDC_DiabetesRecord);
		map.put(RevertOrLogout.PREGNANT, MHC_PregnantRecord);
		map.put(RevertOrLogout.CHILDREN, CDH_HealthCard);
		map.put(RevertOrLogout.DEBILITYCHILDREN, CDH_DebilityChildren);
		map.put(RevertOrLogout.PSYCHOSIS, PSY_PsychosisRecord);
		map.put(RevertOrLogout.OLDPEOPLE, MDC_OldPeopleRecord);
		map.put(RevertOrLogout.TUMOUR, MDC_TumourRecord); // 肿瘤
		map.put(RevertOrLogout.SCHISTOSPMA, SCH_SchistospmaRecord);// 血吸虫
		map.put(RevertOrLogout.RABIES, DC_RabiesRecord);// 狂犬病
		map.put(RevertOrLogout.LIMBDEFORMITY, DEF_LimbDeformityRecord);// 肢体残疾
		map.put(RevertOrLogout.BRAINDEFORMITY, DEF_BrainDeformityRecord);// 脑残
		map.put(RevertOrLogout.INTELLECTDEFORMITY, DEF_IntellectDeformityRecord);// 智力残疾
		map.put(RevertOrLogout.TUMOUR_HIGH_RISK, MDC_TumourHighRisk);// 肿瘤高危
		map.put(RevertOrLogout.TPRC, MDC_TumourPatientReportCard);// 肿瘤患者报告卡
		map.put(RevertOrLogout.LXGB, RVC_RetiredVeteranCadresRecord);// 离休干部
		map.put(RevertOrLogout.TC, MDC_TumourConfirmed);// 肿瘤确诊记录
		map.put(RevertOrLogout.TS, MDC_TumourScreening);// 肿瘤初筛记录
		map.put(RevertOrLogout.TQ, PHQ_GeneralCase);// 肿瘤问卷记录
		map.put(RevertOrLogout.HYPERTENSIONRISK, MDC_HypertensionRisk);// 高血压高危档案

		List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
		Schema sc = null;
		try {
			sc = SchemaController.instance().get(ADMIN_RecordRevert);
		} catch (ControllerException e1) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取schema失败！", e1);
		}

		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "a.empiId", "s", empiId);
		List<?> cnd2 = CNDHelper.createSimpleCnd("ne", "a.cancellationReason",
				"s", CancellationReason.CANCELLATION);
		List<?> cnd3 = CNDHelper.createSimpleCnd("eq", "a.cancellationReason",
				"$", "null");
		List<?> cnd4 = CNDHelper.createArrayCnd("or", cnd2, cnd3);
		List<?> cnd5 = CNDHelper.createArrayCnd("and", cnd1, cnd4);

		List<?> cnd6 = CNDHelper.createSimpleCnd("ne", "a.status", "s",
				RecordStatus.END);
		List<?> cnd = CNDHelper.createArrayCnd("and", cnd5, cnd6);

		for (int k = 1; k <= map.size(); k++) {
			// List<?> cnd6 = CNDHelper.createSimpleCnd("ne", "a.status", "s",
			// RecordStatus.END);
			// List<?> cnd = CNDHelper.createArrayCnd("and", cnd5, cnd6);
			// if (k == 6) {
			// cnd6 = CNDHelper.createSimpleCnd("ne", "a.closeFlag", "s",
			// YesNo.YES);
			// cnd = CNDHelper.createArrayCnd("and", cnd5, cnd6);
			// }
			if (k == 9) {
				continue;// 肿瘤业务没有
			}
			if(k == Integer.parseInt(RevertOrLogout.TS) || k==Integer.parseInt(RevertOrLogout.TQ)){
				cnd = CNDHelper.createArrayCnd("and", cnd1, cnd6);
			}
			List<Map<String, Object>> list;
			try {
				list = dao.doList(cnd, null, map.get("" + k));
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "查询档案失败！", e);
			}
			for (Map<String, Object> record : list) {
				Map<String, Object> rec = new HashMap<String, Object>();
				records.add(rec);
				record.put("type", "" + k);
				if(k==21){
					record.put("cancellationUnit", record.get("closeUnit"));
					record.put("cancellationUser", record.get("closeUser"));
					record.put("cancellationDate", record.get("closeDate"));
					record.put("createUnit", record.get("inputUnit"));
					record.put("createUser", record.get("inputUser"));
					record.put("createDate", record.get("inputDate"));
				}
				for (String key : record.keySet()) {
					if(k==16 && key.equals("reportUnit")){
						rec.put("manaUnitId", record.get("reportUnit"));
						rec.put("manaUnitId_text", record.get("reportUnit_text"));
					}else if((k==18 || k==19 || k==20) && key.equals("createUnit")){
						rec.put("manaUnitId", record.get("createUnit"));
						rec.put("manaUnitId_text", record.get("createUnit_text"));
					}else{
						rec.put(key, record.get(key));
						SchemaItem si = sc.getItem(key);
						if (si != null && si.isCodedValue()) {
							String value = (String) record.get(key);
							rec.put(key + "_text", si.toDisplayValue(value));
						}
					}
				}
			}
		}
		return records;
	}
}
