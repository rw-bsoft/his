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
public class DispensingDetailsBRFile implements IHandler {

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameter = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnitId();// 用户的机构ID
		parameter.put("jgid", JGID);
		Long bq = 0L;
		if(request.get("bq") != null){
			bq = Long.parseLong(request.get("bq")+"");
		}else if (user.getProperty("wardId") != null
				&& user.getProperty("wardId") != "") {
			bq = Long.parseLong(user.getProperty("wardId") + "");
		}
		parameter.put("bqsb", bq);
		try {
			StringBuffer sqlString=new StringBuffer("select b.BFGG as YPGG,b.YFDW as YPDW,sum(a.YPSL) as YPSL,a.YFSB as YFSB,a.YPXH as YPXH,d.CDMC as YPCD,b.YPMC as YPMC,b.YPSX as YPSX,a.YPDJ as YPDJ,sum(a.LSJE) as LSJE,c.ZYHM as ZYHM,c.BRXM as BRXM from YF_ZYFYMX a,YK_TYPK b,ZY_BRRY c,YK_CDDZ d  where a.JGID=:jgid and a.LYBQ=:bqsb and a.YPXH=b.YPXH and a.ZYH=c.ZYH and a.JGID=c.JGID and a.YPCD=d.YPCD and a.ZYH=:ZYH and a.YPSL>0");
			parameter.put("ZYH", Long.parseLong(request.get("ZYH")+""));
			if (request.get("dateFrom") != null) {
				String datefrom = request.get("dateFrom")+"";
				sqlString.append(" and to_char(a.FYRQ,'yyyy-mm-dd hh24:mi:ss')>=:dateFrom ");
				parameter.put("dateFrom", datefrom.replace("_", " "));
			}
			if (request.get("dateTo") != null) {
				String dateto = request.get("dateTo")+"";
				sqlString.append(" and to_char(a.FYRQ,'yyyy-mm-dd hh24:mi:ss')<=:dateTo ");
				parameter.put("dateTo", dateto.replace("_", " "));
			}
			if (request.get("FYFS") != null&&MedicineUtils.parseLong(request.get("FYFS"))!=0) {
				sqlString.append(" and a.FYFS=:FYFS ");
				parameter.put("FYFS", MedicineUtils.parseLong(request.get("FYFS")));
			}
			if (request.get("YF") != null&&MedicineUtils.parseLong(request.get("YF"))!=0) {
				sqlString.append(" and a.YFSB=:YF ");
				parameter.put("YF", MedicineUtils.parseLong(request.get("YF")));
			}
			if (request.get("FYGH") != null && !request.get("FYGH").equals("")) {
				sqlString.append(" and a.QRGH=:QRGH ");
				parameter.put("QRGH", request.get("FYGH"));
			}
			sqlString.append(" group by a.YFSB,a.YPXH,a.YPCD,d.CDMC,b.YPMC,b.BFGG,b.YPSX,b.YFDW,a.YPDJ,c.ZYHM,c.BRXM");
			List<Map<String, Object>> rklist = dao.doQuery(sqlString.toString(), parameter);
			records.addAll(rklist);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	  }

	@SuppressWarnings("deprecation")
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		UserRoleToken user = UserRoleToken.getCurrent();
		String TITLE = user.getManageUnitName();
		long bq = 0;
		if(request.get("bq") != null){
			bq = Long.parseLong(request.get("bq")+"");
		}else if (user.getProperty("wardId") != null
				&& user.getProperty("wardId") != "") {
			bq = Long.parseLong(user.getProperty("wardId") + "");
		}
		try {
			if(null != request.get("dateFrom")){
				String datefrom = request.get("dateFrom")+"";
				response.put("dateForm",sdf.format(BSHISUtil.toDate(datefrom.replace("_", " "))));
			}
			if(null != request.get("dateTo")){
				String dateto = request.get("dateTo")+"";
				response.put("dateTo",sdf.format(BSHISUtil.toDate(dateto.replace("_", " "))));
			}
			response.put("TITLE",TITLE+"病区发药明细清单(按病人统计)");
			if (bq!= 0) {
				Map<String, Object> parameter = new HashMap<String, Object>();
				parameter.put("KSDM", Long.parseLong(request.get("bq")+""));
				String  sqlString = "SELECT officename as KSMC FROM sys_office WHERE id =:KSDM and hospitalArea = 1 ";
				List<Map<String, Object>> rklist = dao.doSqlQuery(sqlString, parameter);
				response.put("BQ",rklist.get(0).get("KSMC"));
			}
			if (request.get("YF") != null&&MedicineUtils.parseLong(request.get("YF"))!=0) {
				Map<String, Object> parameter2 = new HashMap<String, Object>();
				parameter2.put("YFSB", Long.parseLong(request.get("YF")+""));
				String  sqlString = "SELECT YFMC as YFMC FROM YF_YFLB WHERE YFSB =:YFSB ";
				List<Map<String, Object>> rklist = dao.doSqlQuery(sqlString, parameter2);
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
