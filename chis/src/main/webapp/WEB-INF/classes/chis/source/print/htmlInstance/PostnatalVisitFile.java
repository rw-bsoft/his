/**
 * @(#)PostnatalVisitFile.java Created on 2015-2-6 上午10:32:08
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.print.htmlInstance;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.BSCHISEntryNames;
import chis.source.PersistentDataOperationException;
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
public class PostnatalVisitFile extends BSCHISPrint implements IHandler {

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		String recordId = (String) request.get("recordId");
		String empiId = (String) request.get("empiId");
		getDao(ctx);
		Map<String, Object> resRecord = new HashMap<String, Object>();
		try {
			Map<String, Object> record = dao.doLoad(
					BSCHISEntryNames.MHC_PostnatalVisitInfo, recordId);
			Map<String, Object> personInfo = dao.doLoad(
					BSCHISEntryNames.MPI_DemographicInfo, empiId);
			Date visitDate = (Date) record.get("visitDate");
			Calendar c = Calendar.getInstance();
			c.setTime(visitDate);
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH) + 1;
			int day = c.get(Calendar.DAY_OF_MONTH);
			record.put("year", year);
			record.put("month", month);
			record.put("day", day);
			putSuggestionValue(record);
			putReferralValue(record);
			record = SchemaUtil.setDictionaryMessageForList(record,
					BSCHISEntryNames.MHC_PostnatalVisitInfo);
			resRecord.putAll(record);
			resRecord.put("personName", personInfo.get("personName"));
			response.putAll(resRecord);
			change2String(response);
			sqlDate2String(response);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	private void putReferralValue(Map<String, Object> record) {
		String referral = (String) record.get("referral");
		if ("y".equals(referral)) {
			record.put("referral", "2");
		} else if ("n".equals(referral)) {
			record.put("referral", "1");
		}
		
	}

	private void putSuggestionValue(Map<String, Object> record) {
		String suggestion = (String) record.get("suggestion");
		if (suggestion == null) {
			return;
		}
		String[] suggestions = suggestion.split(",");
		for (int i = 0; i < suggestions.length; i++) {
			int s = Integer.parseInt(suggestions[i]);
			String v = "";
			switch (s) {
			case 1:
				v = "1";
				break;
			case 2:
				v = "2";
				break;
			case 3:
				v = "3";
				break;
			case 9:
				v = "4";
				break;
			case 10:
				v = "5";
				break;
			case 99:
				v = "6";
				break;
			}
			if (!"".equals(v)) {
				record.put("suggestion_" + (i + 1), v);
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
