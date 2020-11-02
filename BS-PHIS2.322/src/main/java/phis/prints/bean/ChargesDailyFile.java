package phis.prints.bean;

import java.util.List;
import java.util.Map;

import phis.application.ivc.source.ChargesCheckout;
import phis.source.BaseDAO;
import phis.source.utils.BSHISUtil;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class ChargesDailyFile implements IHandler {
	ChargesCheckout cck =  new ChargesCheckout();

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		String saveSign = "";
		BaseDAO dao = new BaseDAO(ctx);
		if (request.get("save") != null) {
			saveSign = request.get("save").toString();
		}
		if (saveSign.equals("2")) {
			cck.doQueryFields(request, records, dao, ctx);
		} else {
			cck.doGetFields(request, records, dao, ctx);
		}
	}

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		String saveSign = "";
		if (request.get("save") != null) {
			saveSign = request.get("save").toString();
		}
		BaseDAO dao = new BaseDAO(ctx);
		if (saveSign.equals("2")) {
			if (request.get("jzrq") != null) {
				request.put("jzrq",BSHISUtil.toDate(request.get("jzrq").toString()));
			}
			cck.doQuery(request, response, dao, ctx);
		} else {
			cck.doGetParameters(request, response ,dao, ctx);
		}
	}
}
