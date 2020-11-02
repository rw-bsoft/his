package phis.prints.bean;

import java.util.List;
import java.util.Map;

import phis.application.ivc.source.ChargesProduce;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSHISUtil;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class ChargesSummaryFile implements IHandler {
	ChargesProduce cck = ChargesProduce.getInstance();

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
		if ("1".equals(saveSign)) {// 结账
			try {
				if (request.get("hzrq") != null) {
					request.put("hzrq",BSHISUtil.toDate(request.get("hzrq").toString()));
				}
				cck.doCheckout(request, response, dao, ctx);
				if (response.get("hzrq") != null) {
//					request.put("hzrq",
//							BSHISUtil.toDate(response.get("hzrq").toString()));
					request.put("save", 2);
					cck.doInquiry(request, response, dao, ctx);
				}
			} catch (ServiceException e) {
				e.printStackTrace();
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
			}
		} else if (saveSign.equals("2")) {
			if (request.get("hzrq") != null) {
				request.put("hzrq",
						BSHISUtil.toDate(request.get("hzrq").toString()));
			}
			cck.doInquiry(request, response, dao, ctx);
		} else {// 产生
			cck.doGetParameters(request, response, dao, ctx);
		}
	}
}
