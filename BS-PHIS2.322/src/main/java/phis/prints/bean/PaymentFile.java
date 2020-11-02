package phis.prints.bean;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSPHISUtil;
import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class PaymentFile implements IHandler {

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {

	}

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgname = user.getManageUnitName();
		BaseDAO dao = new BaseDAO(ctx);
		response.put("title", jgname);
		// String[] upint = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"
		// };
		Long jkxh = 0L;
		if (request.get("jkxh") != null) {
			jkxh = Long.parseLong(request.get("jkxh") + "");
		}
		parameters.put("JKXH", jkxh);
		try {
			List<Map<String, Object>> ZY_BRRY = dao
					.doSqlQuery(
							"select zt.SJHM as SJHM,zt.ZYH as ZYH,zb.ZYHM as ZYHM,zb.BRCH as BRCH,zb.BRXM as BRXM,zt.JKJE as JKJE,zt.JKFS as JKFS,zt.CZGH as CZGH,zt.ZPHM as ZPHM,zt.JKRQ as JKRQ,ks.OFFICENAME as KSMC,bq.OFFICENAME as BQMC from ZY_TBKK zt,ZY_BRRY zb left outer join SYS_Office ks on zb.BRKS=ks.ID left outer join SYS_Office bq on zb.BRBQ=bq.ID where zt.ZYH=zb.ZYH and zt.JKXH=:JKXH",
							parameters);
			if (ZY_BRRY.size() > 0) {
				if (ZY_BRRY.get(0).get("SJHM") != null) {
					response.put("PJHM", ZY_BRRY.get(0).get("SJHM") + "");
				}
				response.put("ZYHM", ZY_BRRY.get(0).get("ZYHM") + "");
				if (ZY_BRRY.get(0).get("BRCH") != null) {
					response.put("BRCH", ZY_BRRY.get(0).get("BRCH") + "");
				}
				if (ZY_BRRY.get(0).get("BRXM") != null) {
					response.put("BRXM", ZY_BRRY.get(0).get("BRXM") + "");
				}
				if (ZY_BRRY.get(0).get("KSMC") != null) {
					response.put("KSMC", ZY_BRRY.get(0).get("KSMC") + "");
				}
				if (ZY_BRRY.get(0).get("BQMC") != null) {
					response.put("BQMC", ZY_BRRY.get(0).get("BQMC") + "");
				}
				if (ZY_BRRY.get(0).get("JKJE") != null) {
					response.put("JKJE",
							String.format("%1$.2f", ZY_BRRY.get(0).get("JKJE")));
				}
				double jkje = Double.parseDouble(ZY_BRRY.get(0).get("JKJE")
						+ "");
				response.put("DXJE", BSPHISUtil.changeMoneyUpper(jkje));
				response.put("JKFS", DictionaryController.instance().getDic("phis.dictionary.payment")
						.getText(ZY_BRRY.get(0).get("JKFS") + ""));
				response.put("JSR", DictionaryController.instance().getDic("phis.dictionary.user")
						.getText(ZY_BRRY.get(0).get("CZGH") + ""));
				if (ZY_BRRY.get(0).get("ZPHM") != null) {
					response.put("PKHM", ZY_BRRY.get(0).get("ZPHM") + "");
				} else {
					response.put("PKHM", "");
				}
				response.put("JKRQ", sdf.format(ZY_BRRY.get(0).get("JKRQ")));
				Map<String, Object> BRXX = new HashMap<String, Object>();
				BRXX.put("JSLX", 0);
				BRXX.put("ZYH", ZY_BRRY.get(0).get("ZYH"));
				BRXX = BSPHISUtil.gf_zy_gxmk_getjkhj(BRXX, dao, ctx);
				double JKHJ = Double.parseDouble(BRXX.get("JKHJ") + "");
				response.put("JKHJ", String.format("%1$.2f", JKHJ));
				BRXX = BSPHISUtil.gf_zy_gxmk_getzfhj(BRXX, dao, ctx);
				double ZFHJ = Double.parseDouble(BRXX.get("ZFHJ") + "");
				response.put("ZFHJ", String.format("%1$.2f", ZFHJ));
				double SYHJ = BSPHISUtil.getDouble(JKHJ - ZFHJ, 2);
				response.put("SYHJ", String.format("%1$.2f", SYHJ));
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ModelDataOperationException e) {
			e.printStackTrace();
		}

	}
}
