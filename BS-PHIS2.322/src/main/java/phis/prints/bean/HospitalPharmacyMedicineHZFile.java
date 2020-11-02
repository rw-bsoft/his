package phis.prints.bean; 
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;  

import phis.application.mds.source.MedicineUtils;
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
		Map<String, Object> parameter = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnitId();// 用户的机构ID
		parameter.put("jgid", JGID);
		Long bq = 0L;
		if (user.getProperty("wardId") != null
				&& user.getProperty("wardId") != "") {
			bq = Long.parseLong(user.getProperty("wardId") + "");
		}
		parameter.put("bqsb", bq);
		try {
		StringBuffer sqlString=new StringBuffer("select a.YPGG as YPGG,a.YFDW as YPDW,sum(a.YPSL) as YPSL,a.YFSB as YFSB,a.YPXH as YPXH,a.YPCD as YPCD,d.CDMC as YPCD,b.YPMC as YPMC,b.YPSX as YPSX,a.YPDJ as YPDJ,sum(a.LSJE) as LSJE from YF_ZYFYMX a,YK_TYPK b,ZY_BRRY c,YK_CDDZ d  where a.JGID=:jgid and a.LYBQ=:bqsb and a.YPXH=b.YPXH and a.ZYH=c.ZYH and a.JGID=c.JGID and a.YPCD=d.YPCD and a.YPSL>0 ");
		if (request.get("dateFrom") != null) {
			sqlString.append(" and to_char(a.JFRQ,'yyyy-mm-dd hh24:mi:ss')>=:dateFrom ");
			parameter.put("dateFrom", request.get("dateFrom") + " 00:00:00");
		}
		if (request.get("dateTo") != null) {
			sqlString.append(" and to_char(a.JFRQ,'yyyy-mm-dd hh24:mi:ss')<=:dateTo ");
			parameter.put("dateTo", request.get("dateTo") + " 23:59:59");
		}
		if (request.get("FYFS") != null&&!request.get("FYFS").equals("")) {
			sqlString.append(" and a.FYFS=:FYFS ");
			parameter.put("FYFS", MedicineUtils.parseLong(request.get("FYFS")));
		}
		if (request.get("YF") != null&&!request.get("YF").equals("")) {
			sqlString.append(" and a.YFSB=:YF ");
			parameter.put("YF", MedicineUtils.parseLong(request.get("YF")));
		}
		sqlString.append(" group by a.YFSB,a.YPXH,a.YPCD,d.CDMC,b.YPMC,a.YPGG,b.YPSX,a.YFDW,a.YPDJ");
		List<Map<String, Object>> rklist = dao.doQuery(sqlString.toString(), parameter);
		for(int i=0;i<rklist.size();i++){
			Map<String, Object> obj = new HashMap<String, Object>();
			obj = rklist.get(i);
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
				Map<String, Object> parameter = new HashMap<String, Object>();
				parameter.put("KSDM", Long.parseLong(request.get("bq")+""));
				String  sqlString = "SELECT officename as KSMC FROM sys_office WHERE id =:KSDM and hospitalArea = 1 ";
				List<Map<String, Object>> rklist = dao.doSqlQuery(sqlString, parameter);
				response.put("BQ",rklist.get(0).get("KSMC"));
			}
			if (request.get("YF") != null&&!request.get("YF").equals("")) {
				Map<String, Object> parameter = new HashMap<String, Object>();
				parameter.put("YFSB", Long.parseLong(request.get("YF")+""));
				String  sqlString = "SELECT YFMC as YFMC FROM YF_YFLB WHERE YFSB =:YFSB ";
				List<Map<String, Object>> rklist = dao.doSqlQuery(sqlString, parameter);
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
