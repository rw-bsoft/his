package chis.source.print.instance;

import java.util.List;
import java.util.Map;

import chis.source.BSCHISEntryNames;
import chis.source.Constants;
import chis.source.PersistentDataOperationException;
import chis.source.print.base.BSCHISPrint;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class OldPeopleSelfCare extends BSCHISPrint implements IHandler {

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		String empiId = (String) request.get("empiId");
		List<Map<String, Object>> list = null;
		getDao(ctx);
		try {
			list = dao.doQuery(toListCnd("['eq',['$','a.empiId'],['s','" + empiId
					+ "']]"), "a.createDate desc",
					BSCHISEntryNames.MDC_OldPeopleSelfCare);
		} catch (PersistentDataOperationException e1) {
			throw new PrintException(Constants.CODE_EXP_ERROR,e1.getMessage());
		}
		records.addAll(list);
	}


}
