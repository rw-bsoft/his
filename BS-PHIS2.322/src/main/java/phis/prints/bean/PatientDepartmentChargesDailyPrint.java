package phis.prints.bean;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.application.hos.source.HospitalizationCheckoutService;
import phis.source.BaseDAO;
import phis.source.utils.BSPHISUtil;
import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class PatientDepartmentChargesDailyPrint implements IHandler {
	HospitalizationCheckoutService cck = HospitalizationCheckoutService
			.getInstance();

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {

	}

	@SuppressWarnings("unchecked")
	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> respose, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> body = new HashMap<String, Object>();
		body.putAll(request);
		request.put("body", body);
		try {
			if ("1".equals(request.get("ty") + "")) {
				Map<String, Object> jzxx = cck.doQuery_ZY_JZXX(request,
						respose, dao, ctx);
				respose.putAll(jzxx);
				String[] lS_JZRQ = (request.get("adt_clrq_b") + "")
						.split("-| |:");
				String JZRQ = lS_JZRQ[0] + "年" + lS_JZRQ[1] + "月" + lS_JZRQ[2]
						+ "日";
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
				String ZBRQ = sdf.format(new java.util.Date());
				UserRoleToken user = UserRoleToken.getCurrent();
				String userid = (String) user.getUserId();
				// String jgid = user.get("manageUnit.id");
				String CZY = DictionaryController.instance().getDic("phis.dictionary.doctor")
						.getText(userid);
				String JGMC = user.getManageUnitName();
				String RMBDX = BSPHISUtil.changeMoneyUpper(jzxx.get("YSJE"));
				respose.put("CYSR", getDouble(jzxx.get("CYSR")));
				respose.put("YJJE", getDouble(jzxx.get("YJJE")));
				respose.put("TYJJ", getDouble(jzxx.get("TYJJ")));
				respose.put("YSJE", getDouble(jzxx.get("YSJE")));
				respose.put("YSXJ", getDouble(jzxx.get("YSXJ")));
				respose.put("SRJE", getDouble(jzxx.get("SRJE")));
				respose.put("YSQT", getDouble(jzxx.get("YSQT")));
				respose.put("RMBDX", RMBDX);
				respose.put("JZRQ", JZRQ);
				respose.put("ZBRQ", ZBRQ);
				respose.put("CZY", CZY);
				respose.put("JGMC", JGMC);
				respose.put("XX", "—— —— —— ——");
				respose.put("SYB", getDouble(jzxx.get("SYB")));

			}
			if ("2".equals(request.get("ty") + "")) {
				Map<String, Object> ret = cck.doCreate_jzrb_preview(request,
						respose, dao, ctx);
				ret = (Map<String, Object>) ret.get("jzxx");
				respose.put("qtysFb", ret.get("qtysFb") + "");
				respose.put("CYSR", getDouble(ret.get("CYSR")));
				respose.put("YJJE", getDouble(ret.get("YJJE")));
				respose.put("TYJJ", getDouble(ret.get("TYJJ")));
				respose.put("QZPJ", ret.get("QZPJ") + "");
				respose.put("QZSJ", ret.get("QZSJ") + "");
				respose.put("FPZS", ret.get("FPZS") + "");
				respose.put("SJZS", ret.get("SJZS") + "");
				respose.put("TJKS", ret.get("TJKS") + "");
				respose.put("JSFP", ret.get("JSFP") + "");
				respose.put("JKSJ", ret.get("JKSJ") + "");
				respose.put("YSJE", getDouble(ret.get("YSJE")));
				respose.put("YSXJ", getDouble(ret.get("YSXJ")));
				respose.put("SRJE", getDouble(ret.get("SRJE")));
				respose.put("YSQT", getDouble(ret.get("YSQT")));
				String[] lS_JZRQ = (request.get("jzrq") + "").split("-| |:");
				String JZRQ = lS_JZRQ[0] + "年" + lS_JZRQ[1] + "月" + lS_JZRQ[2]
						+ "日(" + lS_JZRQ[3] + ":" + lS_JZRQ[4] + ")";
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
				String ZBRQ = sdf.format(new java.util.Date());
				UserRoleToken user = UserRoleToken.getCurrent();
				String userid = user.getUserId();
				// String jgid = user.get("manageUnit.id");
				String CZY = DictionaryController.instance().getDic("phis.dictionary.doctor")
						.getText(userid);
				String JGMC = user.getManageUnitName();
				String RMBDX = BSPHISUtil.changeMoneyUpper(getDouble(ret
						.get("YSJE")));
				respose.put("RMBDX", RMBDX);
				respose.put("JZRQ", JZRQ);
				respose.put("ZBRQ", ZBRQ);
				respose.put("CZY", CZY);
				respose.put("JGMC", JGMC);
				respose.put("XX", "—— —— —— ——");
				respose.put("SYB", getDouble(ret.get("SYB")));
			}
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	/**
	 * double型数据取两位小数
	 * 
	 * @param ret
	 * @return
	 */
	private String getDouble(Object ret) {
		if (ret == null) {
			ret = new Double(0.00);
		}
		String ls_str = BSPHISUtil.getDouble(Double.parseDouble(ret + ""), 2)
				+ "";
		if (ls_str.substring(ls_str.indexOf(".")).length() == 2) {
			ls_str = ls_str + "0";
		}
		return ls_str;
	}

}
