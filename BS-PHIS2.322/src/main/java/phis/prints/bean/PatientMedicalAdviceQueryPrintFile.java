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
public class PatientMedicalAdviceQueryPrintFile implements IHandler {

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameter = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnitId();// 用户的机构ID
		long jgid = Long.parseLong( user.getManageUnitId()+"");
		long srks = Long.parseLong(request.get("SRKS")+"");
		String SRKS =(String) request.get("SRKS");
		
		parameter.put("JGID", JGID);
		parameter.put("SRKS", srks);
		
		try {
		String sqlString="select  t1.BRCH  as CH, t1.BRXM   as XM,to_char(t.KSSJ,'yyyy-mm-dd hh24:mi:ss')   as KZSJ,(SELECT t2.PERSONNAME  FROM SYS_Personnel t2 WHERE t2.PERSONID=t.YSGH   ) as KZYS,t.YZMC   as YZXM,t.YCSL   as SL,t.SYPC   as PC, (SELECT t3.XMMC FROM  ZY_YPYF t3   WHERE t3.YPYF=t.YPYF)   as TJ,to_char(t.TZSJ,'yyyy-mm-dd hh24:mi:ss')   as TZSJ,(SELECT t2.PERSONNAME  FROM SYS_Personnel t2 WHERE t2.PERSONID=t.TZYS) as TZYS,(SELECT t4.FSMC FROM ZY_FYFS t4 WHERE t4.FYFS= t.FYFS ) as FYFS1  from ZY_BQYZ t, ZY_BRRY t1 where t1.ZYH = t.ZYH ";

		if ((request.get("dateFrom") != null) && (request.get("dateTo") != null)){
			sqlString+=(" and (t.KSSJ >= to_date(:dateFrom, 'yyyy-mm-dd')  and t.KSSJ <= to_date( :dateTo, 'yyyy-mm-dd hh24:mi:ss') or   t.TZSJ >= to_date(:dateFrom, 'yyyy-mm-dd')  and  t.TZSJ <= to_date(:dateTo, 'yyyy-mm-dd hh24:mi:ss'))");
			parameter.put("dateFrom", request.get("dateFrom"));
			parameter.put("dateTo", request.get("dateTo") + " 23:59:59");
		}
		
		if (request.get("FYFS") != null&&!request.get("FYFS").equals("")) {
				sqlString+=(" and t.FYFS=:FYFS ");
			parameter.put("FYFS", MedicineUtils.parseLong(request.get("FYFS")));
		}
		if (request.get("XMLX") != null&&!request.get("XMLX").equals("")) {
			if(request.get("XMLX").equals("1")){
				sqlString+=(" and t.YPXH>0 and t.YPCD>0 ");
			}else if(request.get("XMLX").equals("2")){
				sqlString+=(" and t.YPXH>0 and t.YPCD=0 ");
			}else if(request.get("XMLX").equals("3")){
				sqlString+=(" and t.YPXH=0 and t.YPCD=0 ");
			}
		}
		if (request.get("YZLX") != null&&!request.get("YZLX").equals("")) {
			if(request.get("YZLX").equals("1")){
			}else if(request.get("YZLX").equals("2")){
				sqlString+=(" and t.KSSJ=t.TZSJ ");
			}else if(request.get("YZLX").equals("3")){
				sqlString+=(" and t.TZSJ is null");
			}else if(request.get("YZLX").equals("4")){
				sqlString+=("  and t.KSSJ<t.TZSJ");
			}
		}
		sqlString+=(" and t.SRKS = :SRKS  and t.JGID = :JGID and t.YPLX >= 0  and t.YZPB = 0  order by t.YZZH asc, t.YJZX desc, t.JLXH asc");
		List<Map<String, Object>> rklist = dao.doQuery(sqlString, parameter);
		  for (int i = 0; i < rklist.size(); i++) {
			  
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
		try {
			if(null != request.get("dateFrom")){
				response.put("dateForm",sdf.format(BSHISUtil.toDate(request.get("dateFrom")+ " 00:00:00")));
			}
			if(null != request.get("dateTo")){
				response.put("dateTo",sdf.format(BSHISUtil.toDate(request.get("dateTo")+ " 23:59:59")));
			}
			response.put("TITLE",TITLE+"变动医嘱查询");
			
			if (request.get("YZLX") != null&&!request.get("YZLX").equals("")) {
				if(request.get("YZLX").equals("1")){
					response.put("YZLX","当天所有变动医嘱");
				}else if(request.get("YZLX").equals("2")){
					response.put("YZLX","当天临时医嘱");
				}else if(request.get("YZLX").equals("3")){
					response.put("YZLX","当天开始长期医嘱");
				}else if(request.get("YZLX").equals("4")){
					response.put("YZLX","当天结束长期医嘱");
				}
			}else{
				response.put("YZLX","所有类型");
			}
			
			if (request.get("XMLX") != null&&!request.get("XMLX").equals("")) {
				if(request.get("XMLX").equals("1")){
					response.put("XMLX","药品");
				}else if(request.get("XMLX").equals("2")){
					response.put("XMLX","费用");
				}else if(request.get("XMLX").equals("3")){
					response.put("XMLX","文字");
				}
			}else{
				response.put("XMLX","项目类型");
			}
			if (request.get("FYFS") != null&&!request.get("FYFS").equals("")) {
				response.put("FYFS", DictionaryController.instance().getDic("phis.dictionary.hairMedicineWay").getText((String) request.get("FYFS")));
			}else{
				response.put("FYFS","全部");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
