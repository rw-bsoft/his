/**
 * @(#)ConsultationRecordFile.java Created on 2014-8-19 上午10:50:08
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.print.instance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.ModelDataOperationException;
import chis.source.cons.ConsultationRecordModel;
import chis.source.empi.EmpiModel;
import chis.source.inc.IncompleteRecordModel;
import chis.source.phr.HealthRecordModel;
import chis.source.print.base.BSCHISPrint;
import chis.source.util.SchemaUtil;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yub@bsoft.com.cn">YuBo</a>
 */
public class ConsultationRecordFile extends BSCHISPrint implements IHandler {

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		String recordId = (String) request.get("recordId");
		getDao(ctx);
		ConsultationRecordModel cm = new ConsultationRecordModel(dao);
		EmpiModel em = new EmpiModel(dao);
		HealthRecordModel hm = new HealthRecordModel(dao);
		try {
			Map<String, Object> record = cm
					.getConsultationRecordByPkey(recordId);
			String empiId = (String) record.get("empiId");
			Map<String, Object> personInfo = em.getEmpiInfoByEmpiid(empiId);
			String personName = (String) personInfo.get("personName");
			record.put("personName", personName);
			Map<String, Object> healthRecord = hm
					.getHealthRecordByEmpiId(empiId);
			String manaDoctorId = (String) healthRecord.get("manaDoctorId");
			record.put("manaDoctorId", manaDoctorId);
			record = SchemaUtil.setDictionaryMessageForList(record,
					"chis.application.cons.schemas.CONS_ConsultationRecord");
			String manaDoctorId_text = (String) record.get("manaDoctorId_text");
			if (manaDoctorId_text.indexOf("-") != -1) {
				manaDoctorId_text = manaDoctorId_text.substring(0,
						manaDoctorId_text.indexOf("-"));
				record.put("manaDoctorId_text", manaDoctorId_text);
			}
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
		String recordId = (String) request.get("recordId");
		getDao(ctx);
		ConsultationRecordModel cm = new ConsultationRecordModel(dao);
		try {
			Map<String, Object> record = cm
					.getConsultationRecordByPkey(recordId);
			String doctor = (String) record.get("doctor");
			String[] doctors = doctor.split(",");
			Dictionary manageUtilDic = DictionaryController.instance().get(
					"chis.@manageUnit");
			Dictionary userDic = DictionaryController.instance().get(
					"chis.dictionary.userHER");
			for (int i = 0; i < doctors.length; i++) {
				String doc = doctors[i];
				Map<String, Object> map = new HashMap<String, Object>();
				String doctorName = userDic.getText(doc);
				if (doctorName.indexOf("-") != -1) {
					doctorName = doctorName.substring(0,
							doctorName.indexOf("-"));
				}
				map.put("doctorName", doctorName);
				String manageUnitId = doc.substring(0, doc.indexOf("@"));
				String manageUnitText = manageUtilDic.getText(manageUnitId);
				map.put("manageUnitText", manageUnitText);
				records.add(map);
			}
		} catch (ModelDataOperationException e) {
			e.printStackTrace();
		} catch (ControllerException e) {
			e.printStackTrace();
		}

	}

}
