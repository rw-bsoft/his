package phis.prints.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSHISUtil;
import phis.source.utils.BSPHISUtil;
import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;
import ctd.util.exp.ExpRunner;

/**
 * 住院一日清单汇总格式
 * @author Bollen
 * 
 */
public class FamilySickBedCostHzgs implements IHandler {
	List<Map<String, Object>> ret_list = new ArrayList<Map<String, Object>>();
	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		ret_list.clear();
		String json=(String)request.get("param");
		Gson gson=new Gson();
		Map param=gson.fromJson(json, Map.class);
		Map record=(Map)param.get("record");
		int JSCS = 0;
		if(record.containsKey("JSCS")&&record.get("JSCS")!=null&&!"null".equals(record.get("JSCS"))){
			JSCS = (int) Double.parseDouble(record.get("JSCS").toString());
		}
		response.put("DAYS", record.get("DAYS"));
		String startDate=(String)param.get("startDate");
		String endDate=(String)param.get("endDate");
		
		
		List<Object> cnd = (List<Object>) param.get("cnd");
	
		
		Long ZYH=((Double)record.get("ZYH")).longValue();
		
		UserRoleToken user = UserRoleToken.getCurrent();
		String title = user.getManageUnit().getName();
		
		BaseDAO dao = new BaseDAO(ctx);
		try {
			response.put("title", title+"病人家床费用清单");
			response.put("startDate", startDate);
			Map<String, Object> JC_BRRY = dao.doLoad(BSPHISEntryNames.JC_BRRY, ZYH);
			response.put("endDate",endDate);
			if(JC_BRRY.get("CCJSRQ")!=null){
				response.put("endDate",JC_BRRY.get("CCJSRQ").toString());
			}
			if(record.get("BRXM")!=null)
			{
				response.put("BRXM",new String(record.get("BRXM").toString().getBytes("iso8859_1"), "UTF-8"));
			}
			if(record.get("BRXZ_text")!=null)
			{
				response.put("BRXZ",new String(record.get("BRXZ_text").toString().getBytes("iso8859_1"), "UTF-8"));
			}
//			if(record.get("BRKS_text")!=null)
//			{
//				response.put("BRKS",new String(record.get("BRKS_text").toString().getBytes("iso8859_1"), "UTF-8"));
//			}
			if(record.get("ZYHM")!=null)
			{
				response.put("ZYHM",record.get("ZYHM").toString());
			}
			if(record.get("RYRQ")!=null)
			{
				response.put("RYRQ",record.get("RYRQ").toString());
			}
			if(record.get("CYRQ")!=null&&!StringUtils.isEmpty(record.get("CYRQ").toString()))
			{
				//如果存在出院时间，则住院天数为出院日期-入院日期
				response.put("CYRQ",record.get("CYRQ").toString());
//				response.put("DAYS",BSPHISUtil.daysBetween(record.get("CYRQ").toString(), record.get("RYRQ").toString())
//						);
				
//			}else
//			{
//				response.put("DAYS",record.get("CYRQ").toString());
//				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
//				Date now=new Date();
//				response.put("DAYS",BSPHISUtil.daysBetween(record.get("RYRQ").toString(),sdf.format(now))
//						);
			}
//			if(record.get("BRCH")!=null)
//			{
//				response.put("BRCH",record.get("BRCH").toString());
//			}
			
			//费用合计计算
			Map<String, Object> parameterFyhj= new HashMap<String, Object>();
			parameterFyhj.put("ZYH",ZYH);
			parameterFyhj.put("startDate",startDate);
			parameterFyhj.put("endDate",endDate);
			StringBuilder sqlfyhj=new StringBuilder("select sum(ZJJE) AS FYHJ,sum(ZFJE) AS ZFHJ ");
			sqlfyhj.append(" from JC_FYMX a where ");
			
//			System.out.println(cnd);
			if (cnd != null) {
				if (cnd.size() > 0) {
					String where =  ExpRunner.toString(cnd, ctx);
					sqlfyhj.append(where+" and ");
				}
			}
			sqlfyhj.append(" ZYH=:ZYH and FYRQ>=to_date(:startDate,'yyyy-mm-dd HH24:mi:ss')  and FYRQ<to_date(:endDate,'yyyy-mm-dd HH24:mi:ss') ");
//			System.out.println(sqlfyhj.toString());
//			System.out.println(parameterFyhj);
		
			Map<String, Object> fyhjMap=dao.doLoad(sqlfyhj.toString(),parameterFyhj);
			
			
			Map<String, Object> parameterFylj= new HashMap<String, Object>();
			parameterFylj.put("ZYH",ZYH);
			StringBuilder sqlfylj=new StringBuilder("select sum(ZJJE) AS FYLJ,sum(ZFJE) AS ZFLJ ");
			sqlfylj.append(" from JC_FYMX a where ");
			if (cnd != null) {
				if (cnd.size() > 0) {
					String where =  ExpRunner.toString(cnd, ctx);
					sqlfylj.append(where+" and ");
				}
			}
			sqlfylj.append(" ZYH=:ZYH ");
			Map<String, Object> fyljMap=dao.doLoad(sqlfylj.toString(),parameterFylj);
			
//			System.out.println(fyhjMap);
//			System.out.println(fyljMap);
			
//			response.put("ZFHJ", fyhjMap.get("ZFHJ")==null?"0.00":fyhjMap.get("ZFHJ"));
			response.put("FYHJ", fyhjMap.get("FYHJ")==null?"0.00":fyhjMap.get("FYHJ"));
//			response.put("ZFLJ", fyljMap.get("ZFLJ")==null?"0.00":fyljMap.get("ZFLJ"));
			response.put("FYLJ", fyljMap.get("FYLJ")==null?"0.00":fyljMap.get("FYLJ"));

			response.put("ZFHJ", record.get("ZFHJ")==null?"0.00":record.get("ZFHJ"));
			response.put("ZFLJ", record.get("ZFLJ")==null?"0.00":record.get("ZFLJ"));
//			System.out.println(response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		String json=(String)request.get("param");
		Gson gson=new Gson();
		Map param=gson.fromJson(json, Map.class);
		Map record=(Map)param.get("record");
		int JSCS = 0;
		if(record.containsKey("JSCS")&&record.get("JSCS")!=null&&!"null".equals(record.get("JSCS"))){
			JSCS = (int) Double.parseDouble(record.get("JSCS").toString());
		}
//		String startDate=(String)param.get("startDate");
//		String endDate=(String)param.get("endDate");
		
		List<Object> cnd = (List<Object>) param.get("cnd");
		
		Long ZYH=((Double)record.get("ZYH")).longValue();
		
		BaseDAO dao = new BaseDAO(ctx);
		
		//根据住院号码查询明细清单
		Map<String, Object> parameterFyhj= new HashMap<String, Object>();
		//parameterFyhj.put("ZYH",ZYH);
		//parameterFyhj.put("JSCS",JSCS);
//		parameterFyhj.put("startDate",startDate);
//		parameterFyhj.put("endDate",endDate);
//		System.out.println(parameterFyhj);
		StringBuilder sqlfyhj=new StringBuilder("select to_char(a.FYRQ,'yyyy-mm-dd') as FYRQ,a.FYMC as FYMC,sum(a.FYSL) as FYSL,a.FYDJ as FYDJ,sum(a.ZJJE) as FYJE,a.ZFBL as ZFBL,sum(a.ZFJE) as ZFJE");
		sqlfyhj.append(" from JC_FYMX a  WHERE ");
		try {
		if (cnd != null) {
			if (cnd.size() > 0) {
				String where =  ExpRunner.toString(cnd, ctx);
				sqlfyhj.append(where);
			}
		}else{
			parameterFyhj.put("ZYH",ZYH);
			parameterFyhj.put("JSCS",JSCS);
			sqlfyhj.append(" a.ZYH=:ZYH and a.JSCS=:JSCS ");
		}
		
		sqlfyhj.append("  group by to_char(FYRQ,'yyyy-mm-dd'),FYXM,FYMC,FYDJ,ZFBL  ");
		sqlfyhj.append("  order by FYRQ desc");
		
		String tdate="";
		DictionaryController dic=DictionaryController.instance();
			List<Map<String, Object>> fyhjList=dao.doQuery(sqlfyhj.toString(),parameterFyhj);	
			for (int i = 0; i < fyhjList.size(); i++) {
				 	Map m=fyhjList.get(i);
					if(!m.get("FYRQ").equals(tdate))
					{
						tdate=m.get("FYRQ").toString();
					}else
					{
						m.put("FYRQ","");
					}
//					m.put("FYKS",dic.getDic("phis.dictionary.department").getText(m.get("FYKS") + ""));
					records.add(m);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
