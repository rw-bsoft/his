package phis.prints.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.application.mds.source.MedicineUtils;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;

import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class DischargedPatientsDividedDiseaseStatisticsFile implements IHandler{

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		UserRoleToken user = UserRoleToken.getCurrent();
		response.put("JGMC", user.getManageUnitName());
		response.put("TJRQ", "统计日期:"+request.get("dateFrom")+"---"+request.get("dateTo"));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid=user.getManageUnitId();
		SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date begin=sdf.parse(request.get("dateFrom")+" 00:00:00");
			Date end=sdf.parse(request.get("dateTo")+" 23:59:59");
			StringBuffer hql_cybr=new StringBuffer();//查询时间范围内所有出院病人
			hql_cybr.append("select b.JBMC as JBMC,b.ICD10 as ICD10,a.CYFS as CYFS,(a.CYRQ-a.RYRQ+1) as ZYTS,a.ZYH as ZYH from ZY_BRRY a,GY_JBBM b,ZY_RYZD c where a.ZYH=c.ZYH and c.ZDLB=3 and c.ZDXH=b.JBXH and a.JGID=:jgid and a.CYPB>=8 and CYRQ>=:begin and CYRQ<=:end");
			StringBuffer hql_jsje=new StringBuffer();//查询时间范围内出院病人的费用
			hql_jsje.append("select a.ZYH as ZYH,sum(b.FYHJ) as ZJJE from ZY_BRRY a,ZY_ZYJS b where a.ZYH=b.ZYH and a.JGID=:jgid and a.CYPB>=8 and a.CYRQ>=:begin and a.CYRQ<=:end and b.ZFPB=0 group by a.ZYH");
			Map<String,Object> map_par_cybr=new HashMap<String,Object>();
			map_par_cybr.put("jgid", jgid);
			map_par_cybr.put("begin", begin);
			map_par_cybr.put("end", end);
			List<Map<String,Object>> list_cybr=dao.doQuery(hql_cybr.toString(), map_par_cybr);
			if(list_cybr==null||list_cybr.size()==0){
				return;
			}
			//由于出院主诊断不是唯一的,这里特殊处理 一个病人随机取一条主诊断
			List<Map<String,Object>> list_br=new ArrayList<Map<String,Object>>();
			for(Map<String,Object> map_cybr:list_cybr){
				boolean tag=false;
				for(Map<String,Object> map_br:list_br){
					if(MedicineUtils.compareMaps(map_br, new String[]{"ZYH"}, map_cybr, new String[]{"ZYH"})){
						tag=true;
						break;
					}
				}
				if(!tag){
					list_br.add(map_cybr);
				}
			}
			List<Map<String,Object>> list_jsje=dao.doSqlQuery(hql_jsje.toString(), map_par_cybr);
			List<Map<String,Object>> list_ret=new ArrayList<Map<String,Object>>();//按病种统计结果
			for(Map<String,Object> map_cybr:list_br){
				boolean tag=false;//用于判断统计结果中是否有相同疾病的数据
				for(Map<String,Object> map_ret:list_ret){
					if(MedicineUtils.compareMaps(map_cybr, new String[]{"ICD10"}, map_ret, new String[]{"ICD10"})){
						tag=true;
						map_ret.put("CYRS", MedicineUtils.parseInt(map_ret.get("CYRS"))+1);//出院人数
						if(MedicineUtils.parseInt(map_cybr.get("CYFS"))==1){//治愈
							map_ret.put("ZY", MedicineUtils.parseInt(map_ret.get("ZY"))+1);
						}
						if(MedicineUtils.parseInt(map_cybr.get("CYFS"))==2){//好转
							map_ret.put("HZ", MedicineUtils.parseInt(map_ret.get("HZ"))+1);
						}
						if(MedicineUtils.parseInt(map_cybr.get("CYFS"))==3){//无效
							map_ret.put("WX", MedicineUtils.parseInt(map_ret.get("WX"))+1);
						}
						if(MedicineUtils.parseInt(map_cybr.get("CYFS"))==4){//未治
							map_ret.put("WZ", MedicineUtils.parseInt(map_ret.get("WZ"))+1);
						}
						if(MedicineUtils.parseInt(map_cybr.get("CYFS"))==5){//死亡
							map_ret.put("SW", MedicineUtils.parseInt(map_ret.get("SW"))+1);
						}
						if(MedicineUtils.parseInt(map_cybr.get("CYFS"))==6){//其他
							map_ret.put("QT", MedicineUtils.parseInt(map_ret.get("QT"))+1);
						}
						map_ret.put("ZYTS", MedicineUtils.parseInt(map_ret.get("ZYTS"))+Math.round(MedicineUtils.parseDouble(map_cybr.get("ZYTS"))) );//住院天数
						for(Map<String,Object> map_jsje:list_jsje){
							if(MedicineUtils.compareMaps(map_jsje, new String[]{"ZYH"}, map_cybr, new String[]{"ZYH"})){
								map_ret.put("ZYFY", MedicineUtils.parseDouble(map_ret.get("ZYFY"))+MedicineUtils.parseDouble(map_jsje.get("ZJJE")));
								break;
							}
						}
						break;
					}
				}
				if(!tag){
					Map<String,Object> map_ret=new HashMap<String,Object>();
					map_ret.putAll(map_cybr);
					map_ret.put("CYRS", 1);
					int zy=0;
					int hz=0;
					int wz=0;
					int sw=0;
					int qt=0;
					int wx=0;
					if(MedicineUtils.parseInt(map_cybr.get("CYFS"))==1){//治愈
						zy=1;
					}
					if(MedicineUtils.parseInt(map_cybr.get("CYFS"))==2){//好转
						hz=1;
					}
					if(MedicineUtils.parseInt(map_cybr.get("CYFS"))==3){//无效
						wx=1;
					}
					if(MedicineUtils.parseInt(map_cybr.get("CYFS"))==4){//未治
						wz=1;
					}
					if(MedicineUtils.parseInt(map_cybr.get("CYFS"))==5){//死亡
						sw=1;
					}
					if(MedicineUtils.parseInt(map_cybr.get("CYFS"))==6){//其他
						qt=1;
					}
					map_ret.put("ZY", zy);
					map_ret.put("HZ", hz);
					map_ret.put("WZ", wz);
					map_ret.put("SW", sw);
					map_ret.put("QT", qt);
					map_ret.put("WX", wx);
					map_ret.put("ZYTS",Math.round(MedicineUtils.parseDouble(map_cybr.get("ZYTS"))));
					for(Map<String,Object> map_jsje:list_jsje){
						if(MedicineUtils.compareMaps(map_jsje, new String[]{"ZYH"}, map_cybr, new String[]{"ZYH"})){
							map_ret.put("ZYFY",MedicineUtils.parseDouble(map_jsje.get("ZJJE")));
							break;
						}
					}
				list_ret.add(map_ret);
				}
			}
			//计算平均天数和平均金额
			for(Map<String,Object> map_ret:list_ret){
				map_ret.put("PJZYTS", MedicineUtils.parseInt(map_ret.get("ZYTS"))/MedicineUtils.parseInt(map_ret.get("CYRS")));
				map_ret.put("PJZYFY", MedicineUtils.formatDouble(4, MedicineUtils.parseDouble(map_ret.get("ZYFY"))/MedicineUtils.parseDouble(map_ret.get("CYRS"))));
			}
			records.addAll(list_ret);
			
			Collections.sort(records,new Comparator() {
				@Override
			      public int compare(Object o1, Object o2) {
			    	  return MedicineUtils.parseInt(((Map<String,Object>)o2).get("CYRS"))-MedicineUtils.parseInt(((Map<String,Object>)o1).get("CYRS"));
			      }});
		} catch (ParseException e) {
			throw new PrintException(9000,"出院病人分病种统计查询失败"+e.getMessage());
		} catch (PersistentDataOperationException e) {
			throw new PrintException(9000,"出院病人分病种统计查询失败"+e.getMessage());
		}
		
	}

}
