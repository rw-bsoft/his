package chis.source.print.instance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import ctd.dictionary.DictionaryController;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import chis.source.print.base.PrintImpl;
import ctd.dictionary.DictionaryItem;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class ChildrenDeathMonitor extends PrintImpl {

	public void getFields(Map<String, Object> requestData,
			List<Map<String, Object>> list,Context ctx){
		SessionFactory factory = getSessionFactory(ctx);
		Session ss = factory.openSession();
		SQLQuery q = null;
		String regionCode = (String) requestData.get("areaGrid");
		String sql = "select count(*) from MPI_DemographicInfo a,EHR_HealthRecord c";
		String B = " where str(a.birthday,'yyyy')="+requestData.get("year") + 
			" and " + getSeasonSql(requestData.get("season").toString(),"a.birthday") + " and a.empiId = c.empiId" +
			" and a.empiId not in (select empiId from CDH_DeadRegister)";		//出生
		String D = ",CDH_DeadRegister b where str(b.deathDate,'yyyy')="+requestData.get("year") + 
			" and " + getSeasonSql(requestData.get("season").toString(),"b.deathDate") + " and a.empiId = c.empiId and a.empiId = b.empiId";		//死亡
		String M = " and a.sexcode = 1";		//男
		String F = " and a.sexcode = 2";		//女
		String W = " and a.sexcode = 0";
		String BENDI = " and a.registeredPermanent = 1";			//本地
		String LIUDONG = " and a.registeredPermanent = 2";		//流动
		String LIUDONG1 = " and a.registeredPermanent = 3";		//非本地一年以上
		Transaction ta = ss.beginTransaction();
		try{
			List<DictionaryItem> dic = DictionaryController.instance().get("chis.dictionary.areaGrid").getSlice(regionCode, 3, "");
			Iterator<DictionaryItem> it = dic.iterator();
			while(it.hasNext()){
				DictionaryItem i = it.next();
				String key = i.getKey();
				Map<String, Object> r = new HashMap<String, Object>();
				r.put("point", i.getText());	
				//本地
				r.put("1", getCount(ss, q, sql+B+M+BENDI+" and c.regionCode like '"+key+"%'"));
				r.put("2", getCount(ss, q, sql+B+F+BENDI+" and c.regionCode like '"+key+"%'"));
				r.put("3", getCount(ss, q, sql+B+W+BENDI+" and c.regionCode like '"+key+"%'"));
				r.put("4", getCount(ss, q, sql+B+BENDI+" and c.regionCode like '"+key+"%'"));
				r.put("5", getCount(ss, q, sql+D+BENDI+" and c.regionCode like '"+key+"%'"+getDeadOldSql(0, 0)));
				r.put("6", getCount(ss, q, sql+D+BENDI+" and c.regionCode like '"+key+"%'"+" and (b.deathDate-a.birthday)<=28"));
				r.put("7", getCount(ss, q, sql+D+BENDI+" and c.regionCode like '"+key+"%'"+getDeadOldSql(1, 4)));
				r.put("8", getCount(ss, q, sql+D+BENDI+" and c.regionCode like '"+key+"%'"+getDeadOldSql(0, 4)));
				//流动
				r.put("9", getCount(ss, q, sql+B+M+LIUDONG+" and c.regionCode like '"+key+"%'"));
				r.put("10", getCount(ss, q, sql+B+F+LIUDONG+" and c.regionCode like '"+key+"%'"));
				r.put("11", getCount(ss, q, sql+B+W+LIUDONG+" and c.regionCode like '"+key+"%'"));
				r.put("12", getCount(ss, q, sql+B+LIUDONG+" and c.regionCode like '"+key+"%'"));
				r.put("13", getCount(ss, q, sql+D+LIUDONG+" and c.regionCode like '"+key+"%'"+getDeadOldSql(0, 0)));
				r.put("14", getCount(ss, q, sql+D+LIUDONG+" and c.regionCode like '"+key+"%'"+" and (b.deathDate-a.birthday)<=28"));
				r.put("15", getCount(ss, q, sql+D+LIUDONG+" and c.regionCode like '"+key+"%'"+getDeadOldSql(1, 4)));
				r.put("16", getCount(ss, q, sql+D+LIUDONG+" and c.regionCode like '"+key+"%'"+getDeadOldSql(0, 4)));
				//流动一年
				r.put("17", getCount(ss, q, sql+B+M+LIUDONG1+" and c.regionCode like '"+key+"%'"));
				r.put("18", getCount(ss, q, sql+B+F+LIUDONG1+" and c.regionCode like '"+key+"%'"));
				r.put("19", getCount(ss, q, sql+B+W+LIUDONG1+" and c.regionCode like '"+key+"%'"));
				r.put("20", getCount(ss, q, sql+B+LIUDONG1+" and c.regionCode like '"+key+"%'"));
				r.put("21", getCount(ss, q, sql+D+LIUDONG1+" and c.regionCode like '"+key+"%'"+getDeadOldSql(0, 0)));
				r.put("22", getCount(ss, q, sql+D+LIUDONG1+" and c.regionCode like '"+key+"%'"+" and (b.deathDate-a.birthday)<=28"));
				r.put("23", getCount(ss, q, sql+D+LIUDONG1+" and c.regionCode like '"+key+"%'"+getDeadOldSql(1, 4)));
				r.put("24", getCount(ss, q, sql+D+LIUDONG1+" and c.regionCode like '"+key+"%'"+getDeadOldSql(0, 4)));
				list.add(r);
			}
			ta.commit();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(ss != null && ss.isOpen()){
				ss.close();
			}
		}
	}

	public Map<String, Object> getPrintMap(
			Map<String, Object> requestData,
			Map<String, Object> sharedData) throws PrintException{
		Map<String, Object> pa = new HashMap<String, Object>();
		pa.put("year", requestData.get("year"));
		pa.put("season", getSeason(requestData));
		pa.put("reportDate", new Date());
		pa.put("areaGrid", dicKeyToText("chis.dictionary.areaGrid", requestData.get("areaGrid").toString()));
		return pa;
	}
	
	private String getDeadOldSql(int start,int end){
		String old = "months_between(b.deathDate, a.birthday)";
		int m = 12;		
		return " and "+old+">="+start*m+" and "+old+"<"+(end+1)*m;
	}
	
	@SuppressWarnings("static-access")
	private String getSeason(Map<String, Object> requestData){
		String year =  (String) requestData.get("year");
		String season = this.season.get((String) requestData.get("season"));
		return year+"年"+season;
	}
	
	private String getSeasonSql(String season,String date){
		String start = "0";
		String end = "0";
		if(season.equals("1")){
			start = "1";end = "3";
		}else if(season.equals("2")){
			start = "4";end = "6";
		}else if(season.equals("3")){
			start = "7";end = "9";
		}else if(season.equals("4")){
			start = "10";end = "12";
		}
		return "str("+date+",'mm')>="+start+" and str("+date+",'mm')<="+end;
	}
	
	@SuppressWarnings({"rawtypes" })
	private Integer getCount(Session ss,SQLQuery q,String queryString){
		Integer count = 0;
		q = ss.createSQLQuery(queryString);
		List list = q.list();
		if(list.size() != 0){
			count = ((BigDecimal) list.get(0)).intValue();
		}
		return count;
	}
	
	private static HashMap<String, String> season = new HashMap<String, String>();
	static{
		season.put("1", "一");
		season.put("2", "二");
		season.put("3", "三");
		season.put("4", "四");
	}
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx)
			throws PrintException {
		
	}
}
