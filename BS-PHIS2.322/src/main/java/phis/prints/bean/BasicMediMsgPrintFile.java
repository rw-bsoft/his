package phis.prints.bean; 
import java.util.HashMap;
import java.util.List;
import java.util.Map;  

import phis.source.BaseDAO;

import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context; 
public class BasicMediMsgPrintFile implements IHandler{
	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameter = new HashMap<String, Object>();
		StringBuffer where_sql =new StringBuffer();
		if(request.containsKey("VALUE")){
			String LB = request.get("LB")+"" ;
			String value = (request.get("VALUE")+"").toUpperCase();
			   where_sql.append(" and t."+LB+" like'"+value+"%'") ;
		}
		if(request.containsKey("ZBY")){
			 where_sql.append(" and t.ZFYP=1 ") ;
		}
		String sql ="select t.YPMC as YPMC,t.YPGG as GG,t.YPDW as DW,t.PYDM as PYM,t.WBDM as WBM,t.JXDM as JXM,t.TSYP as TSYP,t.ZFPB as ZX,t1.SXMC as JX from YK_TYPK t,YK_YPSX t1 where t.YPSX = t1.YPSX " +
		       where_sql +
			   "order by t.YPXH desc";
	try {
		List<Map<String, Object>> reslist = dao.doQuery(sql, parameter);
		int n = 0;
		for (int i = 0; i < reslist.size(); i++) {
			if(Integer.parseInt(reslist.get(i).get("ZX")+"") != 0){
				reslist.get(i).put("ZX","メ");
			}else{
				reslist.get(i).put("ZX","");
			}
			n = Integer.parseInt(reslist.get(i).get("TSYP")+"");
			if(n == 0){
				reslist.get(i).put("TSYP","");
			}
			//1——麻醉、2——精神、3——贵重
			if(n == 1){
				reslist.get(i).put("TSYP","麻醉");
			}
			if(n == 2){
				reslist.get(i).put("TSYP","精神");
			}
			if(n == 3){
				reslist.get(i).put("TSYP","贵重");
			}
			if(n!=1 && n!=2 && n!=3){
				reslist.get(i).put("TSYP","");
			}
			records.add(reslist.get(i));
		 }
	    } catch (Exception e) {
		   e.printStackTrace();
	   }
	}

	@Override
	public void getParameters(Map<String, Object> arg0,
			Map<String, Object> arg1, Context arg2) throws PrintException {
		
	}

}
