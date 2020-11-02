/**
 * @(#)IncompleteRecordField.java Created on 2014-8-18 下午3:00:04
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.print.instance;

import java.util.List;
import java.util.Map;

import chis.source.ModelDataOperationException;
import chis.source.empi.EmpiModel;
import chis.source.empi.EmpiUtil;
import chis.source.inc.IncompleteRecordModel;
import chis.source.print.base.BSCHISPrint;
import chis.source.util.SchemaUtil;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yub@bsoft.com.cn">YuBo</a>
 */
public class IncompleteRecordField extends BSCHISPrint implements IHandler {

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		String recordId = (String) request.get("recordId");
		getDao(ctx);
		IncompleteRecordModel irm = new IncompleteRecordModel(dao);
		EmpiModel em = new EmpiModel(dao);
		try {
			Map<String, Object> record = irm.getIncompleteRecordByPkey(recordId);
			record = SchemaUtil.setDictionaryMessageForList(record,
					"chis.application.inc.schemas.INC_IncompleteRecord");
			String doctor = (String) record.get("doctor_text");
			if (doctor.indexOf("-")!=-1) {
				doctor = doctor.substring(0, doctor.indexOf("-"));
				record.put("doctor_text", doctor);
			}
			String empiId = (String) record.get("empiId");
			Map<String, Object> personInfo = em.getEmpiInfoByEmpiid(empiId);
			String personName = (String) personInfo.get("personName");
			record.put("personName", personName);
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
		// TODO Auto-generated method stub
		
	}

}
