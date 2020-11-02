package phis.prints.bean;

import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class FamilySickBedChargesSummaryFile implements IHandler {
	FamilySickBedChargesSummary cck = FamilySickBedChargesSummary.getInstance();

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		String saveSign = "";
		if (request.get("save") != null) {
			saveSign = request.get("save").toString();
		}
		if (!saveSign.equals("1")) {
			cck.doGetFields(request, records, ctx);
		}
	}

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		String saveSign = "";
		if (request.get("save") != null) {
			saveSign = request.get("save").toString();
		}
		if (saveSign.equals("2")) {// 查询
			cck.doInquiry(request, response, dao, ctx);
		} else {// 产生
			cck.doGetParameters(request, response, dao, ctx);
		}
	}
}
