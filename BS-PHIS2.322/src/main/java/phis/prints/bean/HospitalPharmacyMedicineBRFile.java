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
public class HospitalPharmacyMedicineBRFile implements IHandler {

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();// 用户的机构ID
		String bqsb = Long.parseLong(user.getProperty("wardId") + "")+"";
		String ZYH = Long.parseLong(request.get("ZYH")+"")+"";
		try {
		StringBuffer sqlString=new StringBuffer("select a.YPGG as YPGG,a.YFDW as YPDW,sum(a.YPSL) as YPSL,a.YFSB as YFSB,a.YPXH as YPXH,a.YPCD as YPCD,d.CDMC as CDMC,b.YPMC as YPMC,b.YPSX as YPSX," +
				" a.YPDJ as YPDJ,sum(a.LSJE) as LSJE,c.ZYHM as ZYHM,c.BRXM as BRXM,e.XMMC as YPYF from YF_ZYFYMX a,YK_TYPK b,ZY_BRRY c,YK_CDDZ d,ZY_YPYF e  " +
				" where a.JGID='"+jgid+"' and a.LYBQ='"+bqsb+"' and a.YPXH=b.YPXH and a.ZYH=c.ZYH and a.JGID=c.JGID and a.YPCD=d.YPCD and a.ZYH='"+ZYH+"' and a.YPSL>0 and b.GYFF=e.YPYF ");
		if (request.get("dateFrom") != null) {
			String dateFrom = request.get("dateFrom") + " 00:00:00";
			sqlString.append(" and to_char(a.JFRQ,'yyyy-mm-dd hh24:mi:ss')>='"+dateFrom+"' ");
		}
		if (request.get("dateTo") != null) {
			String dateTo = request.get("dateTo") + " 23:59:59";
			sqlString.append(" and to_char(a.JFRQ,'yyyy-mm-dd hh24:mi:ss')<='"+dateTo+"' ");
		}
		if (request.get("FYFS") != null&&MedicineUtils.parseLong(request.get("FYFS"))!=0) {
			sqlString.append(" and a.FYFS='"+request.get("FYFS")+ "' " );
		}
		if (request.get("YF") != null&&MedicineUtils.parseLong(request.get("YF"))!=0) {
			sqlString.append(" and a.YFSB='"+request.get("YF")+"' ");
		}
		if (request.get("YPYF") != null&&MedicineUtils.parseLong(request.get("YPYF"))!=0) {
			sqlString.append(" and e.YPYF='"+request.get("YPYF")+"' ");
		}
		sqlString.append(" group by a.YFSB,a.YPXH,a.YPCD,d.CDMC,b.YPMC,a.YPGG,b.YPSX,a.YFDW,a.YPDJ,c.ZYHM,c.BRXM,e.XMMC");
		List<Map<String, Object>> rklist = dao.doSqlQuery(sqlString.toString(), null);
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
				response.put("dateForm",sdf.format(BSHISUtil.toDate(request.get("dateFrom")+" 00:00:00")));
			}
			if(null != request.get("dateTo")){
				response.put("dateTo",sdf.format(BSHISUtil.toDate(request.get("dateTo")+" 23:59:59")));
			}
			response.put("TITLE",TITLE+"病区发药明细清单(按病人统计)");
			if (bq!= 0) {
				String KSDM = Long.parseLong(request.get("bq")+"")+"";
				String  sqlString = "SELECT officename as KSMC FROM sys_office WHERE id ='"+KSDM+"' and hospitalArea = 1 ";
				List<Map<String, Object>> rklist = dao.doSqlQuery(sqlString, null);
				response.put("BQ",rklist.get(0).get("KSMC"));
			}
			if (request.get("YF") != null&&MedicineUtils.parseLong(request.get("YF"))!=0) {
				String YFSB = Long.parseLong(request.get("YF")+"")+"";
				String  sqlString = "SELECT YFMC as YFMC FROM YF_YFLB WHERE YFSB ='"+YFSB+"' ";
				List<Map<String, Object>> rklist = dao.doSqlQuery(sqlString, null);
				response.put("YFMC",rklist.get(0).get("YFMC"));
			}else{
				response.put("YFMC","全部药房");
			}
			if (request.get("FYFS") != null&&MedicineUtils.parseLong(request.get("FYFS"))!=0) {
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
