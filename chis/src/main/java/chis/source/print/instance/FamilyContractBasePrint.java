package chis.source.print.instance;

import java.util.List;
import java.util.Map;

import chis.source.ModelDataOperationException;
import chis.source.empi.EmpiModel;
import chis.source.fhr.FamilyRecordModule;
import chis.source.phr.HealthRecordModel;
import chis.source.print.base.BSCHISPrint;
import chis.source.util.SchemaUtil;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class FamilyContractBasePrint extends BSCHISPrint implements IHandler {

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		String fcId = (String) request.get("recordId");
		getDao(ctx);
		FamilyRecordModule frModule = new FamilyRecordModule(dao);
		try {
			Map<String, Object> contract = frModule
					.getFamilyContractBaseByPkey(fcId);
			String F_Id = (String) contract.get("F_Id");
			Map<String, Object> familyRecord = frModule
					.getFamilyRecordById(F_Id);
			contract = SchemaUtil.setDictionaryMessageForList(contract,
					"chis.application.fhr.schemas.EHR_FamilyContractBase");
			contract.put("familyAddr", familyRecord.get("familyAddr"));
			contract.put("familyDoc", contract.get("FC_Party") + "医生为乙方的家庭医生");
			response.putAll(contract);
			change2String(response);
			sqlDate2String(response);
		} catch (ModelDataOperationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		String fcId = (String) request.get("recordId");
		getDao(ctx);
		FamilyRecordModule frModule = new FamilyRecordModule(dao);

		try {
			List<Map<String, Object>> list = frModule
					.getFamilyContractByFcId(fcId);
			if (list == null || list.size() == 0) {
				return;
			}
			EmpiModel em = new EmpiModel(dao);
			HealthRecordModel hm = new HealthRecordModel(dao);
			for (int j = 0; j < list.size(); j++) {
				Map<String, Object> map = list.get(j);
				String FS_EmpiId = (String) map.get("FS_EmpiId");
				Map<String, Object> personHR = hm
						.getHealthRecordByEmpiId(FS_EmpiId);
				String signFlag = (String) personHR.get("signFlag");
				if (!"y".equals(signFlag)) {
					list.remove(map);
					j--;
					continue;
				}
				Map<String, Object> personInfo = em
						.getEmpiInfoByEmpiid(FS_EmpiId);
				map.put("personName", personInfo.get("personName"));
				String FS_Kind = (String) map.get("FS_Kind");
				int n = FS_Kind.indexOf(":");
				String kind1 = "";
				if (n > 0) {
					kind1 = FS_Kind.substring(0, FS_Kind.indexOf(":"));
				} else {
					kind1 = FS_Kind;
				}
				String kindList[] = kind1.split(",");
				String kind = "";
				for (int i = 0; i < kindList.length; i++) {
					String k = kindList[i];
					if ("5.1 1次/年".equals(k) || "5.2 2次/年".equals(k)
							|| "5.3 4次/年".equals(k)) {
						kind = kind + "5.主动的分类健康咨询和指导(" + k + ")";
					} else {
						kind = kind + k;
					}
					if (i < kindList.length - 1) {
						kind = kind + ",   ";
					}
				}
				if (n > 0) {
					String kind2 = FS_Kind.substring(FS_Kind.indexOf(":") + 1);
//					Dictionary dic = DictionaryController.instance().get(
//							"chis.dictionary.otherServiceSelect");
//					String other[] = kind2.split(",");
//					String otherKind = "";
//					for (int i = 0; i < other.length; i++) {
//						otherKind = otherKind + dic.getText(other[i]);
//						if (i < other.length - 1) {
//							otherKind = otherKind + ",";
//						}
//					}
					kind = kind + ":" + kind2;//otherKind;
				}
				map.put("FS_Kind", kind);
			}
			records.addAll(list);
			change2String(records);
			sqlDate2String(records);
		} catch (ModelDataOperationException e) {
			e.printStackTrace();
		}

	}
}
