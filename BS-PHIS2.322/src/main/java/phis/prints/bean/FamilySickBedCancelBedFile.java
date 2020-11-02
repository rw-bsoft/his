package phis.prints.bean;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.utils.BSHISUtil;
import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class FamilySickBedCancelBedFile implements IHandler {

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
	}

	@SuppressWarnings("deprecation")
	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd");
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parameterscc = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnitId();
		response.put("TITLE", user.getManageUnitName());
		parameters.put("ZYH", Long.parseLong(request.get("ZYH").toString()));
		parameterscc.put("ZYH", Long.parseLong(request.get("ZYH").toString()));
		parameters.put("JGID", manageUnit);
		try {
			StringBuffer sql = new StringBuffer(
					" select a.JCBH as JCBH,a.ZYH as ZYH,a.ZYHM as ZYHM,a.JCHM as JCHM,a.BRXM as BRXM,a.BRXB as BRXB,a.BRXZ as BRXZ,b.XZMC as XZMC,a.LXDZ as LXDZ,a.LXGX as LXRM,a.LXGX as LXGX,a.LXDH as LXDH,a.JCLX as JCLX,a.SFZH as SFZH,a.RYNL as RYNL,a.RYQK as RYQK,a.CYPB as CYPB,a.KSRQ as KSRQ,a.JSRQ as JSRQ,a.CYFS as CYFS,a.KSRQ as KSRQ,a.JSRQ as JSRQ,a.CYRQ as CYRQ,a.ZRYS as ZRYS,a.ZRHS as ZRHS,a.CCQK as CCQK from ");
			sql.append("JC_BRRY a,GY_BRXZ b");
			sql.append(" where a.BRXZ=b.BRXZ and a.JGID=:JGID and a.ZYH=:ZYH");
			List<Map<String, Object>> list = dao.doSqlQuery(sql.toString(),
					parameters);
			long hlcs = dao.doCount("JC_HLJL", "ZYH=:ZYH and JGID=:JGID",
					parameters);
			long cccs = dao.doCount("JC_CCJL", "ZYH=:ZYH", parameterscc);
			if (list.size() > 0) {
				Map<String, Object> body = list.get(0);
				String hql = "select ZDMC as ZDMC,ICD10 as ICD10,ZDSJ as ZDSJ,ZDLB as ZDLB from JC_BRZD a where a.JGID=:JGID and a.ZYH=:ZYH";
				List<Map<String, Object>> brzdList = dao.doSqlQuery(hql,
						parameters);
				for (Map<String, Object> brzd : brzdList) {
					if ("1".equals(brzd.get("ZDLB").toString())) {
						body.put("JCZD", brzd.get("ZDMC"));
						body.put("ICD10_JC", brzd.get("ICD10"));
						body.put("ZDRQ", brzd.get("ZDSJ"));
					} else if ("2".equals(brzd.get("ZDLB").toString())) {
						body.put("CCZD", brzd.get("ZDMC"));
						body.put("ICD10_CC", brzd.get("ICD10"));
						body.put("CCRQ", brzd.get("ZDSJ"));
					}
				}
				response.put("ZYHM", body.get("ZYHM") + "");
				response.put("BRXM", body.get("BRXM") + "");
				if (body.get("BRXB") != null && body.get("BRXB") != "") {
					if (Integer.parseInt(body.get("BRXB") + "") == 0) {
						response.put("BRXB", "未知的性别");
					} else if (Integer.parseInt(body.get("BRXB") + "") == 1) {
						response.put("BRXB", "男");
					} else if (Integer.parseInt(body.get("BRXB") + "") == 2) {
						response.put("BRXB", "女");
					} else if (Integer.parseInt(body.get("BRXB") + "") == 9) {
						response.put("BRXB", "未说明的性别");
					}
				}
				response.put("RYNL", body.get("RYNL"));
				response.put("BRXZ", body.get("XZMC"));
				response.put("SFZH", body.get("SFZH"));
				response.put("LXDZ", body.get("LXDZ"));
				response.put("JCRQ", body.get("KSRQ"));
				response.put("CYRQ", body.get("CYRQ"));
				if(body.get("CYRQ")!=null){
					int day = BSHISUtil.getDifferDays(
							matter.parse(body.get("CYRQ") + ""),
							matter.parse(body.get("KSRQ") + ""));
					response.put("JCTS", day );
				}
				response.put(
						"CYFS",
						DictionaryController.instance()
								.getDic("phis.dictionary.outcomeSituation")
								.getText(body.get("CYFS") + ""));
				response.put(
						"ZRYS",
						DictionaryController.instance()
								.getDic("phis.dictionary.doctor")
								.getText(body.get("ZRYS") + ""));
				response.put(
						"ZRHS",
						DictionaryController.instance()
								.getDic("phis.dictionary.doctor")
								.getText(body.get("ZRHS") + ""));
				response.put("CCCS", cccs);
				response.put("HLCS", hlcs);
				response.put("JCZD", body.get("JCZD"));
				response.put("ICD10_JC_JCD1", body.get("ICD10_JC"));
				response.put("ZDRQ", body.get("ZDRQ"));
				response.put("CCZD", body.get("CCZD"));
				response.put("ICD10_CC", body.get("ICD10_CC"));
				response.put("CCRQ", body.get("CCRQ"));
				response.put("CCQK", body.get("CCQK"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
