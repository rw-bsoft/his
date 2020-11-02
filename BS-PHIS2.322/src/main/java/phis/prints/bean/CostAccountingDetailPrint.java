package phis.prints.bean;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSPHISUtil;
import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class CostAccountingDetailPrint implements IHandler {
	private List<Map<String, Object>> rklist = null;

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		records.addAll(rklist);
	}

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameter = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();
		String beginDate = (String) request.get("FYRQFrom");
		String endDate = (String) request.get("FYRQTo");
		String ygdm = (String) request.get("SRGH");
		String ksdm = (String) request.get("FYKS");
		String ZYHM = (String) request.get("ZYHM");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		parameter.put("manaUnitId", manaUnitId);
		parameter.put("beginDate", beginDate);
		parameter.put("endDate", endDate);
		String sql = "SELECT b.ZYHM as ZYHM, b.BRXM as BRXM,a.JFRQ as JFRQ, to_char(a.FYRQ,'yyyy-mm-dd') as FYRQ,a.YPLX as YPLX,a.FYXH as FYXH,a.FYMC as FYMC,a.FYSL as FYSL,a.FYDJ as FYDJ,a.ZFBL as ZFBL,a.FYKS as FYKS,c.PERSONNAME as YGXM,a.JGID as JGID,a.ZJJE as ZJJE FROM ZY_FYMX  a,ZY_BRRY  b ,SYS_Personnel c WHERE (a.SRGH = c.PERSONID) AND ( a.ZYH = b.ZYH )  AND ( a.JGID = b.JGID ) AND  ( a.JGID = :manaUnitId ) AND  ( a.XMLX = 4) ";
		 sql += "and( to_char(a.JFRQ,'yyyy-mm-dd')>=:beginDate ) and(to_char(a.JFRQ ,'yyyy-mm-dd') <=:endDate )";
		if (ygdm != null && !"".equals(ygdm) && !"null".equals(ygdm)) {
			parameter.put("ygdm", ygdm);
			sql += "and( a.SRGH = :ygdm )";
		}
		if (ksdm != null && !"".equals(ksdm) && !"null".equals(ksdm)) {
			parameter.put("ksdm", Long.parseLong(ksdm));
			sql += "and( a.FYKS = :ksdm)";
		}
		if (ZYHM != null && !"".equals(ZYHM) && !"null".equals(ZYHM)) {
			parameter.put("zyh", ZYHM);
			sql += "and(b.ZYHM =:zyh)";
		}
		sql += " order by a.JFRQ desc";
		double total = 0;
		double zfje = 0;
		DecimalFormat df2 = new DecimalFormat("0.00");
		DecimalFormat df3 = new DecimalFormat("0.000");
		DecimalFormat df4 = new DecimalFormat("0.0000");
		try {
			rklist = dao.doQuery(sql, parameter);
			Map<String, Object> map ;
			String doub = "";
			for (int i = 0; i < rklist.size(); i++) {
				map = rklist.get(i);
				map.put("JFRQ",sdf.format(map.get("JFRQ")));
				doub = df2.format(map.get("FYSL"));
				map.put("FYSL",doub);
				
				doub = df4.format(map.get("FYDJ"));
				map.put("FYDJ",doub);
				String KSMC = DictionaryController.instance().getDic("phis.dictionary.department").getText(map.get("FYKS") + "");
				rklist.get(i).put("KSMC", KSMC);
				total += (Double)map.get("ZJJE");
				zfje += ((Double)map.get("ZJJE"))*((Double)rklist.get(i).get("ZFBL"));
				doub = df2.format(map.get("ZJJE"));
				map.put("ZJJE",doub);
				doub = df3.format(map.get("ZFBL"));
				map.put("ZFBL",doub);
			}
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		response.put("zj", BSPHISUtil.getDouble(total, 2) + "");
		response.put("zf", BSPHISUtil.getDouble(zfje, 2) + "");
	}

}
