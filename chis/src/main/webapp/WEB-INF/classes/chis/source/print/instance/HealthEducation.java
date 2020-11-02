/**
 * @(#)HealthEducation.java Created on 2012-7-19 上午09:50:16
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.print.instance;

import java.util.List;
import java.util.Map;
import ctd.print.IHandler;
import ctd.print.PrintException;
import chis.source.BSCHISEntryNames;
import chis.source.Constants;
import chis.source.PersistentDataOperationException;
import chis.source.print.base.BSCHISPrint;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class HealthEducation extends BSCHISPrint implements IHandler{

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx)
			throws PrintException {
		String recordId=(String) request.get("recordId");
		getDao(ctx);
		String exp="['eq',['$','recordId'],['s','"+recordId+"']]";
		Map<String, Object> map = null;
		try {
			map = getFirstRecord(dao.doQuery(toListCnd(exp), "a.recordId desc",
					BSCHISEntryNames.HER_EducationRecord));
		} catch (PersistentDataOperationException e) {
			throw new PrintException(Constants.CODE_EXP_ERROR, e.getMessage());
		}
		response.putAll(map);
		sqlDate2String(response);
	}


	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
	}

}
