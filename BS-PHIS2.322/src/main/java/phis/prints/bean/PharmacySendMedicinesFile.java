package phis.prints.bean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.application.mds.source.MedicineUtils;
import phis.application.pha.source.PharmacyStatisticalModel;
import phis.source.BaseDAO;
import phis.source.utils.CNDHelper;
import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class PharmacySendMedicinesFile implements IHandler {

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		try {
		Map<String,Object> body = new HashMap<String,Object>();
		body.put("FYLB", request.get("FYLB")+"");
		body.put("TJFS", request.get("TJFS")+"");
		List<?>cnd1=CNDHelper.toListCnd("['and',['ne',['$','b.ZFYP'],['i',1]],['ge',['$', \"to_char(a.FYRQ,'yyyy-mm-dd hh24:mi:ss')\"],['s','"+request.get("KSSJ")+"' ]],['le',['$', \"to_char(a.FYRQ,'yyyy-mm-dd hh24:mi:ss')\"],['s', '"+request.get("JSSJ")+"' ]]]");
		List<?>cnd2=CNDHelper.toListCnd("['and',['ge',['$', \"to_char(a.JFRQ,'yyyy-mm-dd hh24:mi:ss')\"],['s','"+request.get("KSSJ")+"' ]],['le',['$', \"to_char(a.JFRQ,'yyyy-mm-dd hh24:mi:ss')\"],['s', '"+request.get("JSSJ")+"' ]]]");
		if(!"0".equals(request.get("YPLX"))){
			cnd1=CNDHelper.createArrayCnd("and", cnd1, CNDHelper.toListCnd("['eq', ['$', 'a.CFLX'], ['i', "+MedicineUtils.parseInt(request.get("YPLX"))+"]]"));
			cnd2=CNDHelper.createArrayCnd("and", cnd2, CNDHelper.toListCnd("['eq', ['$', 'a.YPLX'], ['i', "+MedicineUtils.parseInt(request.get("YPLX"))+"]]"));
		}
		body.put("cnd1",cnd1 );
		body.put("cnd2", cnd2 );
		PharmacyStatisticalModel mmd = new PharmacyStatisticalModel(dao);
					
			List<Map<String, Object>> list = mmd.yffytjQuery(body, ctx);	
			for(int i=0;i<list.size();i++){
				if(list.get(i).get("PJCFE")!=null){
					list.get(i).put("PJCFE", String.format("%1$.2f", parseDouble(list.get(i).get("PJCFE")))); 
				}
			}
			records.addAll(list);			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnitId();
		Long YFSB = parseLong(request.get("YFSB") + "");
		response.put("ZBR", user.getUserName()+"");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		response.put("ZBRQ", sdf.format(new Date()));
		String day = (request.get("KSSJ")+"").substring(0, 10)+"----"+(request.get("JSSJ")+"").substring(0, 10);
		//System.out.println(day);
		response.put("CXSJ", day);
		String yplx = request.get("YPLX")+"";
		String tjfs = request.get("TJFS")+"";
		
		if(yplx.equals("0")){
			response.put("YPLX", "全部");
		} else if(yplx.equals("1")){
			response.put("YPLX", "西药");
		} else if(yplx.equals("2")){
			response.put("YPLX", "中成药");
		} else if(yplx.equals("3")){
			response.put("YPLX", "草药");
		} else {
			response.put("YPLX", "<未知>");
		}
		if(tjfs.equals("1")){
			response.put("TJFS", "按发药窗口");
		} else if(tjfs.equals("2")){
			response.put("TJFS", "按病人性质");
		} else if(tjfs.equals("3")){
			response.put("TJFS", "按特殊药品");
		} else if(tjfs.equals("4")){
			response.put("TJFS", "按开单科室");
		} else if(tjfs.equals("5")){
			response.put("TJFS", "按发药人");
		} else {
			response.put("TJFS", "<未知>");
		}
		//response.put("TITLE", jgname + "智慧医疗结算告知单");
		try {
			Map<String,Object> parameters = new HashMap<String,Object>();
			parameters.put("JGID", JGID);
			parameters.put("YFSB", YFSB);
			response.put("TITLE",(dao.doLoad("select YFMC as YFMC from YF_YFLB where JGID=:JGID and YFSB=:YFSB", parameters)).get("YFMC")+"发药统计报表");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public double parseDouble(Object o) {
		if (o == null) {
			return new Double(0);
		}
		return Double.parseDouble(o + "");
	}
	public long parseLong(Object o) {
		if (o == null) {
			return new Long(0);
		}
		return Long.parseLong(o + "");
	}
}
