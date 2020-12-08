package phis.prints.bean; 
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import phis.source.BaseDAO;
import phis.source.utils.BSHISUtil;

import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context; 
public class HospitalPharmacyMedicineHZFile implements IHandler {

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();// 用户的机构ID
		String bqsb = request.get("bq")+"";
		try {
		StringBuffer sqlString=new StringBuffer("select a.YPGG as YPGG,a.YFDW as YPDW,sum(a.YPSL) as YPSL,a.YFSB as YFSB,a.YPXH as YPXH,a.YPCD as YPCD,d.CDMC as CDMC," +
				" b.YPMC as YPMC,b.YPSX as YPSX,a.YPDJ as YPDJ,sum(a.LSJE) as LSJE,e.XMMC as YPYF from YF_ZYFYMX a,YK_TYPK b,ZY_BRRY c,YK_CDDZ d,ZY_YPYF e  " +
				" where a.jgid='"+jgid+"' and a.LYBQ='"+bqsb+"' and a.YPXH=b.YPXH and a.ZYH=c.ZYH and a.JGID=c.JGID and a.YPCD=d.YPCD and a.YPSL>0 and b.GYFF=e.YPYF ");
		if (request.get("dateFrom") != null) {
			String dateFrom = request.get("dateFrom") + " 00:00:00";
			sqlString.append(" and to_char(a.JFRQ,'yyyy-mm-dd hh24:mi:ss')>='"+dateFrom+"' ");
		}
		if (request.get("dateTo") != null) {
			String dateTo = request.get("dateTo") + " 23:59:59";
			sqlString.append(" and to_char(a.JFRQ,'yyyy-mm-dd hh24:mi:ss')<='"+dateTo+"' ");
		}
		if (request.get("FYFS") != null&&!request.get("FYFS").equals("")) {
			sqlString.append(" and a.FYFS='"+request.get("FYFS")+"' ");
		}
		if (request.get("YF") != null&&!request.get("YF").equals("")) {
			sqlString.append(" and a.YFSB='"+request.get("YF")+"' ");
		}
		if (request.get("YPYF") != null&&!request.get("YPYF").equals("")) {
			sqlString.append(" and e.YPYF='"+request.get("YPYF")+"' ");
		}
		sqlString.append(" group by a.YFSB,a.YPXH,a.YPCD,d.CDMC,b.YPMC,a.YPGG,b.YPSX,a.YFDW,a.YPDJ,e.XMMC");
		List<Map<String, Object>> rklist = dao.doSqlQuery(sqlString.toString(), null);
		for(int i=0;i<rklist.size();i++){
			Map<String, Object> obj = rklist.get(i);
			if(obj.get("YPGG") == null || "null".equals(obj.get("YPGG"))){
				obj.put("YPGG", null);
			}
			if(obj.get("YPDW") == null || "null".equals(obj.get("YPDW"))){
				obj.put("YPDW", null);
			}
			rklist.set(i, obj);
		}
		records.addAll(rklist);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	  }

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		UserRoleToken user = UserRoleToken.getCurrent();
		String TITLE = user.getManageUnitName();
		int bq = 0;
		if (user.getProperty("wardId") != null
				&& user.getProperty("wardId") != "") {
			bq = Integer.parseInt(user.getProperty("wardId") + "");
		}
		try {
			if(null != request.get("dateFrom")){
				response.put("dateForm",sdf.format(BSHISUtil.toDate(request.get("dateFrom")+ " 00:00:00")));
			}
			if(null != request.get("dateTo")){
				response.put("dateTo",sdf.format(BSHISUtil.toDate(request.get("dateTo")+ " 23:59:59")));
			}
			response.put("TITLE",TITLE+"病区发药明细清单汇总");
			if (bq!= 0) {
				String KSDM = Long.parseLong(request.get("bq")+"")+"";
				String  sqlString = "SELECT officename as KSMC FROM sys_office WHERE id ='"+KSDM+"' and hospitalArea = 1 ";
				List<Map<String, Object>> rklist = dao.doSqlQuery(sqlString, null);
				response.put("BQ",rklist.get(0).get("KSMC"));
			}
			if (request.get("YF") != null&&!request.get("YF").equals("")) {
				String YFSB = Long.parseLong(request.get("YF")+"")+"";
				String  sqlString = "SELECT YFMC as YFMC FROM YF_YFLB WHERE YFSB ='"+YFSB+"' ";
				List<Map<String, Object>> rklist = dao.doSqlQuery(sqlString, null);
				response.put("YFMC",rklist.get(0).get("YFMC"));
			}else{
				response.put("YFMC","全部药房");
			}
			if (request.get("FYFS") != null&&!request.get("FYFS").equals("")) {
				String fyfs =DictionaryController.instance().getDic("phis.dictionary.hairMedicineWay").getText(request.get("FYFS").toString());
				response.put("FYFS",fyfs);
			}else{
				response.put("FYFS","全部发药方式");
			}
			if (request.get("YPYF") != null&&!request.get("YPYF").equals("")) {
				String ypyf =DictionaryController.instance().getDic("phis.dictionary.drugWay").getText(request.get("YPYF").toString());
				response.put("GYFS",ypyf);
			}else{
				response.put("GYFS","全部给药方式");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
