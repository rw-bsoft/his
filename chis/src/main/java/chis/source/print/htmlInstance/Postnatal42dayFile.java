/**
 * @(#)Postnatal42dayFile.java Created on 2015-2-6 上午10:31:54
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.print.htmlInstance;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.BSCHISEntryNames;
import chis.source.PersistentDataOperationException;
import chis.source.print.base.BSCHISPrint;
import chis.source.util.CNDHelper;
import chis.source.util.SchemaUtil;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yub@bsoft.com.cn">YuBo</a>
 */
public class Postnatal42dayFile extends BSCHISPrint implements IHandler {

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		String empiId = (String) request.get("empiId");
		getDao(ctx);
		Map<String, Object> resRecord = new HashMap<String, Object>();
		try {
			List<Object> cnd = CNDHelper.createSimpleCnd("eq", "a.empiId", "s",
					empiId);
			Map<String, Object> record = dao.doLoad(cnd,
					BSCHISEntryNames.MHC_Postnatal42dayRecord);
			Map<String, Object> personInfo = dao.doLoad(
					BSCHISEntryNames.MPI_DemographicInfo, empiId);
			Date checkDate = (Date) record.get("checkDate");
			record.put("checkDate",
					new SimpleDateFormat("yyyy年MM月dd日").format(checkDate));
			putSuggestionValue(record);
			record = SchemaUtil.setDictionaryMessageForList(record,
					BSCHISEntryNames.MHC_Postnatal42dayRecord);
			resRecord.putAll(record);
			resRecord.put("personName", personInfo.get("personName"));
			response.putAll(resRecord);
			change2String(response);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}

	}

	private void putSuggestionValue(Map<String, Object> record) {
		String suggestion = (String) record.get("suggestion");
		if (suggestion != null) {
			String[] suggestions = suggestion.split(",");
			for (int i = 0; i < suggestions.length; i++) {
				int s = Integer.parseInt(suggestions[i]);
				String v = "";
				switch (s) {
				case 11:
					v = "1";
					break;
				case 12:
					v = "2";
					break;
				case 13:
					v = "3";
					break;
				case 99:
					v = "4";
					break;
				}
				if (!"".equals(v)) {
					record.put("suggestion_" + (i + 1), v);
				}
			}
		}
	}

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		// TODO Auto-generated method stub

	}

}
