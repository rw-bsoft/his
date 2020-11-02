package chis.source.print.instance;

import java.util.List;
import java.util.Map;

import chis.source.ModelDataOperationException;
import chis.source.her.PlanExecutionModule;
import chis.source.print.base.BSCHISPrint;
import chis.source.util.SchemaUtil;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class HealthTeachReport extends BSCHISPrint implements IHandler {

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		String recordId = (String) request.get("recordId");
		getDao(ctx);
		PlanExecutionModule pm = new PlanExecutionModule(dao);
		try {
			List<Map<String, Object>> records = pm
					.queryRecordByRecordId(recordId);
			if (records != null && records.size() == 0) {
				return;
			}
			Map<String, Object> record = records.get(0);
			String saveMaterial=record.get("saveMaterial")+"";
			String[] saveMaterialList=saveMaterial.split(",");
			for (int i = 0; i < saveMaterialList.length; i++) {
				String val=saveMaterialList[i];
				response.put("saveMaterial"+val, "√");
			}
			record = SchemaUtil.setDictionaryMessageForList(record,
					"chis.application.her.schemas.HER_EducationRecord");
			response.putAll(record);
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

	}

}
