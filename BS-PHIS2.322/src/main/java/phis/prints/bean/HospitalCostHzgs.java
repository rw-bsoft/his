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
public class HospitalCostHzgs implements IHandler {
	List<Map<String, Object>> ret_list = new ArrayList<Map<String, Object>>();
	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		ret_list.clear();
		String json=(String)request.get("param");
		Gson gson=new Gson();
		Map param=gson.fromJson(json, Map.class);
		Map record=(Map)param.get("record");
		String startDate=(String)param.get("startDate");
		String endDate=(String)param.get("endDate");
		if(!"".equals(endDate)){
			//endDate=((String)param.get("endDate")).substring(0,10)+" 23:59:59";
			endDate=((String)param.get("endDate"));
		}
		
		List<Object> cnd = (List<Object>) param.get("cnd");
	
		
		Long ZYH=((Double)record.get("ZYH")).longValue();
		
		UserRoleToken user = UserRoleToken.getCurrent();
		String title = user.getManageUnit().getName();
		
		BaseDAO dao = new BaseDAO(ctx);
		try {
			response.put("title", title+"病人住院费用清单");
			response.put("startDate", startDate);
			response.put("endDate",endDate);
			if(record.get("BRXM")!=null)
			{
				response.put("BRXM",new String(record.get("BRXM").toString().getBytes("iso8859_1"), "UTF-8"));
			}
			if(record.get("BRXZ_text")!=null)
			{
				response.put("BRXZ",new String(record.get("BRXZ_text").toString().getBytes("iso8859_1"), "UTF-8"));
			}
			if(record.get("BRKS_text")!=null)
			{
				response.put("BRKS",new String(record.get("BRKS_text").toString().getBytes("iso8859_1"), "UTF-8"));
			}
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
				response.put("DAYS",BSPHISUtil.daysBetween(record.get("CYRQ").toString(), record.get("RYRQ").toString())
						);
				
			}else
			{
				response.put("DAYS",record.get("CYRQ").toString());
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
				Date now=new Date();
				response.put("DAYS",BSPHISUtil.daysBetween(record.get("RYRQ").toString(),sdf.format(now))
						);
			}
			if(record.get("BRCH")!=null)
			{
				response.put("BRCH",record.get("BRCH").toString());
			}
			
			//费用合计计算
			Map<String, Object> parameterFyhj= new HashMap<String, Object>();
			parameterFyhj.put("ZYH",ZYH);
			StringBuilder sqlfyhj=new StringBuilder("select sum(ZJJE) AS FYHJ,sum(ZFJE) AS ZFHJ ");
			sqlfyhj.append(" from ZY_FYMX a where ");
			if (cnd != null) {
				if (cnd.size() > 0) {
					String where =  ExpRunner.toString(cnd, ctx);
					sqlfyhj.append(where+" and ");
				}
			}
			sqlfyhj.append(" ZYH=:ZYH");
			if(startDate.length()>0){
				parameterFyhj.put("startDate",startDate);
				sqlfyhj.append(" and FYRQ>=to_date(:startDate,'yyyy-mm-dd HH24:mi:ss') ");
			}else{
				response.put("startDate", record.get("RYRQ").toString().substring(0, 10));
				
			}
			if(endDate.length()>0){
				parameterFyhj.put("endDate",endDate);
				sqlfyhj.append(" and FYRQ<to_date(:endDate,'yyyy-mm-dd HH24:mi:ss') ");
			}else{
				if(record.get("CYRQ").toString().length()>0){
					response.put("endDate",record.get("CYRQ").toString().substring(0, 10));
				}else{
					SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  
					response.put("endDate",sdf.format(new Date()));
				}
			}
			Map<String, Object> fyhjMap=dao.doLoad(sqlfyhj.toString(),parameterFyhj);
			
			
			Map<String, Object> parameterFylj= new HashMap<String, Object>();
			parameterFylj.put("ZYH",ZYH);
			StringBuilder sqlfylj=new StringBuilder("select sum(ZJJE) AS FYLJ,sum(ZFJE) AS ZFLJ ");
			sqlfylj.append(" from ZY_FYMX a where ");
			if (cnd != null) {
				if (cnd.size() > 0) {
					String where =  ExpRunner.toString(cnd, ctx);
					if(where.indexOf("a.YPLX in")>=0){
						where = where.substring(0,where.indexOf("a.YPLX in"))+where.substring(where.indexOf("a.YPLX in")+27);
					}else if(where.indexOf("a.YPLX = 0")>=0){
						where = where.substring(0,where.indexOf("a.YPLX = 0"))+where.substring(where.indexOf("a.YPLX = 0")+14);
					}
					sqlfylj.append(where+" and ");
				}
			}
			sqlfylj.append(" ZYH=:ZYH ");
			Map<String, Object> fyljMap=dao.doLoad(sqlfylj.toString(),parameterFylj);
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
		String startDate=(String)param.get("startDate");
		String endDate=(String)param.get("endDate");
		
		List<Object> cnd = (List<Object>) param.get("cnd");
		
		Long ZYH=((Double)record.get("ZYH")).longValue();
		
		BaseDAO dao = new BaseDAO(ctx);
		
		//根据住院号码查询明细清单
		Map<String, Object> parameterFyhj= new HashMap<String, Object>();
		parameterFyhj.put("ZYH",ZYH);
		StringBuilder sqlfyhj=new StringBuilder("select to_char(a.FYRQ,'yyyy-mm-dd') as FYRQ,a.FYMC as FYMC,sum(a.FYSL) as FYSL,a.FYDJ as FYDJ,sum(a.ZJJE) as FYJE,a.ZFBL as ZFBL,sum(a.ZFJE) as ZFJE,a.FYKS as FYKS");
		sqlfyhj.append(" from ZY_FYMX a  WHERE ");
		try {
		if (cnd != null) {
			if (cnd.size() > 0) {
				String where =  ExpRunner.toString(cnd, ctx);
				sqlfyhj.append(where+" and ");
			}
		}
		sqlfyhj.append(" ZYH=:ZYH");
		if(startDate.length()>0){
			parameterFyhj.put("startDate",startDate);
			sqlfyhj.append(" and FYRQ>=to_date(:startDate,'yyyy-mm-dd HH24:mi:ss') ");
		}
		if(endDate.length()>0){
			sqlfyhj.append("  and a.FYRQ<to_date(:endDate,'yyyy-mm-dd HH24:mi:ss') ");
			parameterFyhj.put("endDate",endDate);
		}
		sqlfyhj.append("  group by to_char(FYRQ,'yyyy-mm-dd'),FYXM,FYMC,FYDJ,ZFBL,FYKS  ");
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
					m.put("FYKS",dic.getDic("phis.dictionary.department").getText(m.get("FYKS") + ""));
					records.add(m);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
