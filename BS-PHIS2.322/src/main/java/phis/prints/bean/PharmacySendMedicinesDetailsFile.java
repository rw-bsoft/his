package phis.prints.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

public class PharmacySendMedicinesDetailsFile implements IHandler {
	@SuppressWarnings("unchecked")
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		try {
		Map<String,Object> body = new HashMap<String,Object>();
		body.put("VIRTUAL_FIELD", request.get("virtualField")+"");
		body.put("FYLB", request.get("FYLB")+"");
		body.put("TJFS", request.get("TJFS")+"");
		List<?>cnd1=CNDHelper.toListCnd("['and',['ne',['$','b.ZFYP'],['i',1]],['ge',['$', \"to_char(a.FYRQ,'yyyy-mm-dd hh24:mi:ss')\"],['s','"+request.get("KSSJ")+"' ]],['le',['$', \"to_char(a.FYRQ,'yyyy-mm-dd hh24:mi:ss')\"],['s', '"+request.get("JSSJ")+"' ]]]");
		List<?>cnd2=CNDHelper.toListCnd("['and',['ge',['$', \"to_char(a.JFRQ,'yyyy-mm-dd hh24:mi:ss')\"],['s','"+request.get("KSSJ")+"' ]],['le',['$', \"to_char(a.JFRQ,'yyyy-mm-dd hh24:mi:ss')\"],['s', '"+request.get("JSSJ")+"' ]]]");
		if(request.containsKey("PYDM")){
			cnd1=CNDHelper.createArrayCnd("and", cnd1, CNDHelper.toListCnd("['like', ['$', 'c.PYDM'], ['s', '"+request.get("PYDM")+"%']]"));
			cnd2=CNDHelper.createArrayCnd("and", cnd2, CNDHelper.toListCnd("['like', ['$', 'b.PYDM'], ['s', '"+request.get("PYDM")+"%']]"));
		}
		if(!"0".equals(request.get("YPLX"))){
			cnd1=CNDHelper.createArrayCnd("and", cnd1, CNDHelper.toListCnd("['eq', ['$', 'a.CFLX'], ['i', "+MedicineUtils.parseInt(request.get("YPLX"))+"]]"));
			cnd2=CNDHelper.createArrayCnd("and", cnd2, CNDHelper.toListCnd("['eq', ['$', 'a.YPLX'], ['i', "+MedicineUtils.parseInt(request.get("YPLX"))+"]]"));
		}
		body.put("cnd1",cnd1 );
		body.put("cnd2", cnd2 );
		//modify by zhaojian 2017-08-29 解决药品发药统计无法打印明细问题
		//body.put("TAG", 1);
			PharmacyStatisticalModel mmd = new PharmacyStatisticalModel(dao);
			List<Map<String, Object>> list =new ArrayList<Map<String,Object>>();	
			Map<String,Object> m=mmd.yffyDetailsQuery(body,request, ctx);
			if(m!=null){
				list=(List<Map<String, Object>>)m.get("body");
			}
			double jhhj = 0.00;
			double fyhj = 0.00;
			for(int i=0;i<list.size();i++){
				if(list.get(i).get("JHJE")!=null){
					jhhj += parseDouble(list.get(i).get("JHJE"));
				}
				if(list.get(i).get("FYJE")!=null){
					fyhj += parseDouble(list.get(i).get("FYJE"));
				}
//				if(list.get(i).get("JHJE")!=null){
//					list.get(i).put("JHJE", String.format("%1$.2f", parseDouble(list.get(i).get("JHJE"))));
//				}
//				if(list.get(i).get("JHJG")!=null){
//					list.get(i).put("JHJG", String.format("%1$.2f", parseDouble(list.get(i).get("JHJG"))));
//				}
				if(list.get(i).get("FYJE")!=null){
					list.get(i).put("FYJE", String.format("%1$.2f", parseDouble(list.get(i).get("FYJE"))));
				}
				if(i+1==list.size()){
					list.get(i).put("JHHJ", String.format("%1$.2f", jhhj));
					list.get(i).put("FYHJ", String.format("%1$.2f", fyhj));
				}
				list.get(i).put("XH",i+1);
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
		long YFSB=MedicineUtils.parseLong(user.getProperty("pharmacyId"));
		response.put("ZBR", user.getUserName()+"");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		response.put("ZBRQ", sdf.format(new Date()));
		String day = (request.get("KSSJ")+"").substring(0, 10)+"----"+(request.get("JSSJ")+"").substring(0, 10);
		//System.out.println(day);
		response.put("CXSJ", day);
		String yplx = request.get("YPLX")+"";
		String tjfs = request.get("TJFS")+"";
		String fylb = request.get("FYLB")+"";
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
		} else {
			response.put("TJFS", "<未知>");
		}
		if(fylb.equals("0")){
			response.put("FYLB", "全部");
		} else if(fylb.equals("1")){
			response.put("FYLB", "门诊");
		} else if(fylb.equals("2")){
			response.put("FYLB", "住院");
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
