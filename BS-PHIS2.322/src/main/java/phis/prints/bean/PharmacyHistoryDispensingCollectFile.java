package phis.prints.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.application.mds.source.MedicineUtils;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSHISUtil;
import phis.source.utils.CNDHelper;
import phis.source.utils.SchemaUtil;
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class PharmacyHistoryDispensingCollectFile implements IHandler {
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();
		// 获取表单参数
		String dateFrom = (String) request.get("dateFrom");
		String dateTo = (String) request.get("dateTo");
		int lx  = MedicineUtils.parseInt(request.get("lx"));
		long bq  = MedicineUtils.parseLong(request.get("bq"));
		long ypxh = MedicineUtils.parseLong(request.get("ypxh"));
		long ypcd = MedicineUtils.parseLong(request.get("ypcd"));
//		String ypgg = (String) request.get("ypgg");
//		double ypdj = MedicineUtils.parseDouble(request.get("ypdj"));
		long yfsb = MedicineUtils.parseLong(request.get("yfsb"));
		
		StringBuffer hql = new StringBuffer();
		hql.append("select c.ZYHM as ZYHM,c.BRXM as BRXM,a.LYBQ as LYBQ,c.BRCH as BRCH,to_char(a.FYRQ,'yyyy-mm-dd hh24:mi:ss') as FYRQ,b.YPMC as YPMC,a.YPGG as YPGG,a.YFDW as YFDW,a.YPSL as YPSL,a.LSJE as LSJE,d.CDMC as CDMC,e.PERSONNAME as PERSONNAME,a.YPDJ as YPDJ" +
				" from YF_ZYFYMX a,YK_TYPK b,ZY_BRRY c,YK_CDDZ d,SYS_Personnel e,YF_FYJL f " +
				" where a.YPXH = b.YPXH and a.ZYH = c.ZYH and a.JGID = c.JGID and a.YPCD = d.YPCD and a.QRGH = e.PERSONID and a.JLID = f.JLID and a.JGID = '"+jgid+"' ");
		if (dateFrom != null) {
			hql.append(" and to_char(a.JFRQ,'yyyy-mm-dd hh24:mi:ss')>='")
					.append(dateFrom.replace("_", " ")).append("'");
		}
		if (dateTo != null) {
			hql.append(" and to_char(a.JFRQ,'yyyy-mm-dd hh24:mi:ss')<='")
					.append(dateTo.replace("_", " ")).append("'");
		}
		if(lx>0){
			hql.append(" and f.FYLX = "+lx);
		}
		if(bq>0){
			hql.append(" and a.LYBQ = "+bq);
		}
		if(ypxh>0){
			hql.append(" and a.YPXH = "+ypxh);
		}
		if(ypcd>0){
			hql.append(" and a.YPCD = "+ypcd);
		}
//		if(ypgg!=null && !"".equals(ypgg)){
//			hql.append(" and a.YPGG = '"+ypgg+"'");
//		}
//		if(ypdj>0){
//			hql.append(" and a.YPDJ = "+ypdj);
//		}
		if(yfsb>0){
			hql.append(" and a.YFSB = :yfsb");
		}
		try {
			Map<String,Object> par = new HashMap<String, Object>();
			par.put("yfsb", yfsb);
			list = dao.doSqlQuery(hql.toString(), par);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> m = list.get(i);
				m.put("LYBQ", DictionaryController.instance().get("phis.dictionary.department_bq")
						.getText(m.get("LYBQ").toString()));
//				m.put("FYRQ", sdf.format(m.get("FYRQ")));
				m.put("LSJE", String.format("%1$.2f", m.get("LSJE")));
				m.put("YPDJ", String.format("%1$.2f", m.get("YPDJ")));
				records.add(m);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ControllerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
		UserRoleToken user = UserRoleToken.getCurrent();
		// 获取表单参数
		String title =user.getManageUnitName()+"病区发药汇总查询";
		String dateFrom = (String) request.get("dateFrom");
		String dateTo = (String) request.get("dateTo");
		
		response.put("TITLE", title);
		response.put("dateForm", dateFrom.replace("_", " "));
		response.put("dateTo", dateTo.replace("_", " "));
		
		
	}
}
