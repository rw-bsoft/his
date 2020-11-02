package phis.prints.bean; 
import java.util.HashMap;
import java.util.List;
import java.util.Map; 

import phis.source.BaseDAO;

import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context; 
public class MediPackageMsgPrintFile implements IHandler{
	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameter = new HashMap<String, Object>();
//		String LB = request.get("LB")+"" ;
//		String value = (request.get("VALUE")+"").toUpperCase();
		StringBuffer where_sql =new StringBuffer();
		if(request.containsKey("VALUE")&&request.containsKey("LB")){
			String LB = request.get("LB")+"" ;
			String value = (request.get("VALUE")+"").toUpperCase();
			 where_sql.append("WHERE t."+LB+" like'"+value+"%'") ;
		}
		if(request.containsKey("ZBY")){
			if(where_sql.length()>0){
				where_sql.append(" and t.ZFYP=1");
			}else{
				where_sql.append(" where t.ZFYP=1");
			}
		}
		String sql ="select t.YPXH as YPXH,t.YPMC as YPMC,t.YPGG as GG,t.YPDW as DW,t.ZXDW as ZXDW ,t.ZXBZ as ZXBZSL,t.YFDW as MZDW,t.YFBZ as MZBZSL,t.BFDW as BQDW,t.BFBZ as BQBZSL,t.PYDM as PYDM,t.JXDM as JXDM,t.QTDM as QTDM,t.ZFPB as ZX from YK_TYPK t " +
		       where_sql +
			   "order by t.YPXH desc";
	try {
		List<Map<String, Object>> reslist = dao.doQuery(sql, parameter);
		for (int i = 0; i < reslist.size(); i++) {
			if(Integer.parseInt(reslist.get(i).get("ZX")+"") != 0){
				reslist.get(i).put("ZX","メ");
			}else{
				reslist.get(i).put("ZX","");
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
