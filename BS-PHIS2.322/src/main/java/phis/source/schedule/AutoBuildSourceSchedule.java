package phis.source.schedule;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.SimpleFormatter;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSHISUtil;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.ParameterUtil;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.service.configure.DicConfig;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class AutoBuildSourceSchedule extends AbstractJobSchedule {
	private static final Logger logger = LoggerFactory.getLogger(AutoBuildSourceSchedule.class);
	@Override
	public void doJob(BaseDAO dao,Context ctx) throws ModelDataOperationException {
//		if(1==1)
//		return;
		DictionaryController d=DictionaryController.instance();
		String tbjg="";
		try{
			Dictionary fs=d.get("phis.dictionary.fsyypzxx");
			tbjg=fs.getItem("tbjg").getText();
		}catch(ControllerException e3){
			e3.printStackTrace();
		}
		
		Calendar cal=Calendar.getInstance();
		cal.setTime(new Date());
		int weekday=cal.get(Calendar.DAY_OF_WEEK);
		DateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//自动生成号源业务
		int start=1;
		if(weekday<7){
			start=weekday+1;
		}
		//生成本周之后号源
		for(int i=start;i<=7;i++){
			cal.add(Calendar.DATE,1);
			int year=cal.get(Calendar.YEAR);
			int month=cal.get(Calendar.MONTH)+1;
			int date=cal.get(Calendar.DATE);
			//1上午，2下午
			for(int j=1;j<=2;j++){
				String sql="select GHKS as GHKS,JGID as JGID,YYXE as YYXE,"+
				" YYKSSJ as YYKSSJ,YYJSSJ as YYJSSJ,YYJG as YYJG from MS_KSPB where YYXE>0 " +
				" and GHRQ='"+i+"' and ZBLB='"+j+"' and JGID in("+tbjg+") ";
				try{
					List<Map<String,Object>>lm=dao.doQuery(sql,null);
					for(Map<String,Object> m:lm){
						String GHKS=m.get("GHKS")+"";
						String JGID=m.get("JGID")+"";
						StringBuffer fysql=new StringBuffer();
						fysql.append("select a.GHF as GHF,a.ZLF as ZLF from MS_GHKS a ")
						.append(" where a.JGID='"+JGID+"' and a.KSDM="+GHKS);
						Map<String,Object> fymap=dao.doSqlLoad(fysql.toString(),null);
						double ghf=Double.parseDouble(fymap.get("GHF")+"");
						double zlf=Double.parseDouble(fymap.get("ZLF")+"");
						//预约开始时间
						String YYKSSJ=m.get("YYKSSJ")==null?"":m.get("YYKSSJ")+":00";
						//预约结束时间
						String YYJSSJ=m.get("YYJSSJ")==null?"":m.get("YYJSSJ")+":00";
						//预约限额
						Integer YYXE=Integer.parseInt(m.get("YYXE")+"");
						//预约间隔
						String YYJG=m.get("YYJG")==null?"":m.get("YYJG")+"";
						if(YYKSSJ.length()==0 || YYJSSJ.length()==0 || YYJG.length()==0){
							continue;
						}
						if(j==1){
							if(!(YYKSSJ.compareTo("06:00")>0 && YYKSSJ.compareTo("12:01")<0
							&&YYJSSJ.compareTo("06:00")>0 && YYKSSJ.compareTo("12:01")<0
							&& YYKSSJ.compareTo(YYJSSJ)<0)){
								continue;
							}
						}else{
							if(!(YYKSSJ.compareTo("12:00")>0 && YYKSSJ.compareTo("20:01")<0
							&&YYJSSJ.compareTo("12:00")>0 && YYKSSJ.compareTo("20:01")<0
							&& YYKSSJ.compareTo(YYJSSJ)<0)){
								continue;
							}
						}
						String ds=year+"-";
						if((month+"").length()==2){
							ds=ds+month;
						}else{
							ds=ds+"0"+month;
						}
						if((date+"").length()==2){
							ds=ds+"-"+date;
						}else{
							ds=ds+"-0"+date;
						}
						//已生成号源数
						StringBuffer countWhere=new StringBuffer();
						countWhere.append("HYKS="+GHKS+" and HYJG='"+JGID+"'")
						.append(" and to_char(HYSJ,'yyyy-MM-dd')='"+ds+"' and ZBLB='"+j+"'");
						System.out.println("countWhere:"+countWhere.toString());
						Long yschys=dao.doCount("MS_YYGHHY",countWhere.toString(),null);
						if(YYXE-yschys>0){
							String yssql="select a.YSDM as YSDM,a.YYKSSJ as YYKSSJ,a.YYJSSJ as YYJSSJ,a.YYJG as YYJG," +
									"a.YYXE as YYXE from MS_YSPB a where to_char(a.GZRQ,'yyyy-MM-dd')='"+ds+"'" +
									" and a.KSDM='"+GHKS+"' and a.ZBLB='"+j+"' and a.JGID='"+JGID+"' and a.YYXE>0 ";
							List<Map<String,Object>>lmys=dao.doQuery(yssql,null);
							Long hycount=0L;
							Date yyksdate=new Date();
							Date yyjsdate=new Date();
							Date nextdate=yyksdate;
							try{
								yyksdate=df.parse(year+"-"+month+"-"+date+" "+YYKSSJ);
								yyjsdate=df.parse(year+"-"+month+"-"+date+" "+YYJSSJ);
								nextdate=yyksdate;
							}catch(ParseException e2){
								e2.printStackTrace();
							}
							//已生成号源数
							
							//如果有医生排班
							if(lmys.size()>0 ){
								Long ljyschys=0L;
								for(Map<String,Object> ysm:lmys){
									Integer YS_YYXE=Integer.parseInt(ysm.get("YYXE")+"");
									Long ys_yschys=dao.doCount("MS_YYGHHY","HYKS="+GHKS+" and HYJG='"+JGID+"'" +
									" and to_char(HYSJ,'yyyy-MM-dd')='"+ds+"' and ZBLB='"+j+"'" +
									" and HYYS='"+ysm.get("YSDM")+"' ",null);
									ljyschys=ljyschys+YS_YYXE;
									if(ys_yschys<YS_YYXE){
										hycount=hycount+ys_yschys;
										//应生成数
										Long yscs=YS_YYXE-ys_yschys;
										hycount=hycount+yscs;
										//预约开始时间
										String YS_YYKSSJ=ysm.get("YYKSSJ")==null?"":ysm.get("YYKSSJ")+":00";
										if(ys_yschys>0){
											Map<String,Object>kssjmap=dao.doSqlLoad("select max(to_char(hysj,'HH:mi') )" +
													" as YYKSSJ from MS_YYGHHY where HYKS="+GHKS+" and HYJG='"+JGID+"'" +
											" and to_char(HYSJ,'yyyy-MM-dd')='"+ds+"' and ZBLB='"+j+"'" +
											" and HYYS='"+ysm.get("YSDM")+"' ",null);
											YS_YYKSSJ=kssjmap.get("YYKSSJ")+":00";
										}
										//预约结束时间
										String YS_YYJSSJ=ysm.get("YYJSSJ")==null?"":ysm.get("YYJSSJ")+":00";
										//预约间隔
										String YS_YYJG=ysm.get("YYJG")==null?"":ysm.get("YYJG")+"";
										if(j==1){
											if(!(YS_YYKSSJ.compareTo("06:00")>0 && YS_YYKSSJ.compareTo("12:01")<0
											&&YS_YYJSSJ.compareTo("06:00")>0 && YS_YYJSSJ.compareTo("12:01")<0
											&& YYKSSJ.compareTo(YS_YYJSSJ)<0)){
												continue;
											}
										}else{
											if(!(YS_YYKSSJ.compareTo("12:00")>0 && YS_YYKSSJ.compareTo("20:01")<0
											&&YS_YYJSSJ.compareTo("12:00")>0 && YS_YYJSSJ.compareTo("20:01")<0
											&& YS_YYKSSJ.compareTo(YS_YYJSSJ)<0)){
												continue;
											}
										}
										try{
											Date ys_yyksdate=df.parse(year+"-"+month+"-"+date+" "+YS_YYKSSJ);
											Date ys_yyjsdate=df.parse(year+"-"+month+"-"+date+" "+YS_YYJSSJ);
											Date ys_nextdate=ys_yyksdate;
											for(int k=1;k<=yscs 
												&&df.format(ys_nextdate).compareTo(df.format(ys_yyjsdate))<=0;k++){
												Map<String,Object> o=new HashMap<String,Object>();
												if(ys_yschys>0){
													Calendar cl=Calendar.getInstance();
													cl.setTime(ys_yyksdate);
													cl.add(Calendar.MINUTE,Integer.parseInt(YS_YYJG));
													ys_nextdate=cl.getTime();
												}
												o.put("HYKS",Long.parseLong(GHKS));
												o.put("HYJG",JGID);
												o.put("HYYS",ysm.get("YSDM"));
												o.put("HYZT","0");
												o.put("HYSJ",ys_nextdate);
												o.put("ZBLB",j);
												o.put("GHF",ghf);
												o.put("ZLF",zlf);
												Calendar ca=Calendar.getInstance();
												ca.setTime(ys_nextdate);
												ca.add(Calendar.MINUTE,Integer.parseInt(YS_YYJG));
												ys_nextdate=ca.getTime();
												nextdate=ys_nextdate;
												try{
													dao.doSave("create",MS_YYGHHY,o,false);
													dao.doCount("MS_YYGHHY","1=2",null);
												}catch(ValidateException e){
													e.printStackTrace();
												}
											}
										}catch(ParseException e1){
											e1.printStackTrace();
										}
									}else{
										hycount=hycount+ys_yschys;
									}
								}
								if(YYXE>hycount){
									//剩余号数
									Long syhs=YYXE-hycount;
									String KS_YYKSSJ=YYKSSJ;
									Map<String,Object>kssjmap=dao.doSqlLoad("select max(to_char(hysj,'HH:mi') )" +
									" as YYKSSJ from MS_YYGHHY where HYKS="+GHKS+" and HYJG='"+JGID+"'" +
									" and to_char(HYSJ,'yyyy-MM-dd')='"+ds+"' and ZBLB='"+j+"'",null);
									KS_YYKSSJ=kssjmap.get("YYKSSJ")+":00";
									try{
										nextdate=df.parse(year+"-"+month+"-"+date+" "+KS_YYKSSJ);
									}catch(ParseException e1){
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
									Calendar cat=Calendar.getInstance();
									cat.setTime(nextdate);
									cat.add(Calendar.MINUTE,Integer.parseInt(YYJG));
									nextdate=cat.getTime();
									for(int h=1;h<=syhs&&df.format(nextdate).compareTo(df.format(yyjsdate))<=0;h++){
										Map<String,Object> o=new HashMap<String,Object>();
										o.put("HYKS",Long.parseLong(GHKS));
										o.put("HYJG",JGID);
										o.put("HYZT","0");
										o.put("HYSJ",nextdate);
										o.put("ZBLB",j);
										o.put("GHF",ghf);
										o.put("ZLF",zlf);
										Calendar ca=Calendar.getInstance();
										ca.setTime(nextdate);
										ca.add(Calendar.MINUTE,Integer.parseInt(YYJG));
										nextdate=ca.getTime();
										try{
											dao.doSave("create",MS_YYGHHY,o,false);
											dao.doCount("MS_YYGHHY","1=2",null);
										}catch(ValidateException e){
											e.printStackTrace();
										}
									}
								}
							}
							//无医生排班
							else{
								Long ks_yschys=dao.doCount("MS_YYGHHY","HYKS="+GHKS+" and HYJG='"+JGID+"'" +
								" and to_char(HYSJ,'yyyy-MM-dd')='"+ds+"' and ZBLB='"+j+"'",null);
								String KS_YYKSSJ=YYKSSJ;
								if(ks_yschys>0){
									Map<String,Object>kssjmap=dao.doSqlLoad("select max(to_char(hysj,'HH:mi') )" +
											" as YYKSSJ from MS_YYGHHY where HYKS="+GHKS+" and HYJG='"+JGID+"'" +
									" and to_char(HYSJ,'yyyy-MM-dd')='"+ds+"' and ZBLB='"+j+"'",null);
									KS_YYKSSJ=kssjmap.get("YYKSSJ")+":00";
								}
								try{
									nextdate=df.parse(year+"-"+month+"-"+date+" "+KS_YYKSSJ);
								}catch(ParseException e1){
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								for(int h=1;h<=YYXE-ks_yschys&&df.format(nextdate).compareTo(df.format(yyjsdate))<=0;h++){
									Map<String,Object> o=new HashMap<String,Object>();
									if(ks_yschys>0){
										Calendar ca=Calendar.getInstance();
										ca.setTime(nextdate);
										ca.add(Calendar.MINUTE,Integer.parseInt(YYJG));
										nextdate=ca.getTime();
									}
									o.put("HYKS",Long.parseLong(GHKS));
									o.put("HYJG",JGID);
									o.put("HYZT","0");
									o.put("HYSJ",nextdate);
									o.put("ZBLB",j);
									o.put("GHF",ghf);
									o.put("ZLF",zlf);
									Calendar ca=Calendar.getInstance();
									ca.setTime(nextdate);
									ca.add(Calendar.MINUTE,Integer.parseInt(YYJG));
									nextdate=ca.getTime();
									try{
										dao.doSave("create",MS_YYGHHY,o,false);
										dao.doCount("MS_YYGHHY","1=2",null);
									}catch(ValidateException e){
										e.printStackTrace();
									}
								}
							}
						}
					}
				}catch(PersistentDataOperationException e){
					e.printStackTrace();
				}
			}
			
		}
		//生成下周号源
		for(int i=1;i<start;i++){
			cal.add(Calendar.DATE,1);
			int year=cal.get(Calendar.YEAR);
			int month=cal.get(Calendar.MONTH)+1;
			int date=cal.get(Calendar.DATE);
			//1上午，2下午
			for(int j=1;j<=2;j++){
				String sql="select GHKS as GHKS,JGID as JGID,YYXE as YYXE,"+
				" YYKSSJ as YYKSSJ,YYJSSJ as YYJSSJ,YYJG as YYJG from MS_KSPB where YYXE>0 " +
				" and GHRQ='"+i+"' and ZBLB='"+j+"' and JGID in("+tbjg+") ";
				try{
					List<Map<String,Object>>lm=dao.doQuery(sql,null);
					for(Map<String,Object> m:lm){
						String GHKS=m.get("GHKS")+"";
						String JGID=m.get("JGID")+"";
						StringBuffer fysql=new StringBuffer();
						fysql.append("select a.GHF as GHF,a.ZLF as ZLF from MS_GHKS a ")
						.append(" where a.JGID='"+JGID+"' and a.KSDM="+GHKS);
						Map<String,Object> fymap=dao.doSqlLoad(fysql.toString(),null);
						double ghf=Double.parseDouble(fymap.get("GHF")+"");
						double zlf=Double.parseDouble(fymap.get("ZLF")+"");
						//预约开始时间
						String YYKSSJ=m.get("YYKSSJ")==null?"":m.get("YYKSSJ")+":00";
						//预约结束时间
						String YYJSSJ=m.get("YYJSSJ")==null?"":m.get("YYJSSJ")+":00";
						//预约限额
						Integer YYXE=Integer.parseInt(m.get("YYXE")+"");
						//预约间隔
						String YYJG=m.get("YYJG")==null?"":m.get("YYJG")+"";
						if(YYKSSJ.length()==0 || YYJSSJ.length()==0 || YYJG.length()==0){
							continue;
						}
						if(j==1){
							if(!(YYKSSJ.compareTo("06:00")>0 && YYKSSJ.compareTo("12:01")<0
							&&YYJSSJ.compareTo("06:00")>0 && YYKSSJ.compareTo("12:01")<0
							&& YYKSSJ.compareTo(YYJSSJ)<0)){
								continue;
							}
						}else{
							if(!(YYKSSJ.compareTo("12:00")>0 && YYKSSJ.compareTo("20:01")<0
							&&YYJSSJ.compareTo("12:00")>0 && YYKSSJ.compareTo("20:01")<0
							&& YYKSSJ.compareTo(YYJSSJ)<0)){
								continue;
							}
						}
						String ds=year+"-";
						if((month+"").length()==2){
							ds=ds+month;
						}else{
							ds=ds+"0"+month;
						}
						if((date+"").length()==2){
							ds=ds+"-"+date;
						}else{
							ds=ds+"-0"+date;
						}
						//已生成号源数
						StringBuffer countWhere=new StringBuffer();
						countWhere.append("HYKS="+GHKS+" and HYJG='"+JGID+"'")
						.append(" and to_char(HYSJ,'yyyy-MM-dd')='"+ds+"' and ZBLB='"+j+"'");
						System.out.println("countWhere:"+countWhere.toString());
						Long yschys=dao.doCount("MS_YYGHHY",countWhere.toString(),null);
						if(YYXE-yschys>0){
							String yssql="select a.YSDM as YSDM,a.YYKSSJ as YYKSSJ,a.YYJSSJ as YYJSSJ,a.YYJG as YYJG," +
									"a.YYXE as YYXE from MS_YSPB a where to_char(a.GZRQ,'yyyy-MM-dd')='"+ds+"'" +
									" and a.KSDM='"+GHKS+"' and a.ZBLB='"+j+"' and a.JGID='"+JGID+"' and a.YYXE>0 ";
							List<Map<String,Object>>lmys=dao.doQuery(yssql,null);
							Long hycount=0L;
							Date yyksdate=new Date();
							Date yyjsdate=new Date();
							Date nextdate=yyksdate;
							try{
								yyksdate=df.parse(year+"-"+month+"-"+date+" "+YYKSSJ);
								yyjsdate=df.parse(year+"-"+month+"-"+date+" "+YYJSSJ);
								nextdate=yyksdate;
							}catch(ParseException e2){
								e2.printStackTrace();
							}
							//已生成号源数
							
							//如果有医生排班
							if(lmys.size()>0 ){
								Long ljyschys=0L;
								for(Map<String,Object> ysm:lmys){
									Integer YS_YYXE=Integer.parseInt(ysm.get("YYXE")+"");
									Long ys_yschys=dao.doCount("MS_YYGHHY","HYKS="+GHKS+" and HYJG='"+JGID+"'" +
									" and to_char(HYSJ,'yyyy-MM-dd')='"+ds+"' and ZBLB='"+j+"'" +
									" and HYYS='"+ysm.get("YSDM")+"' ",null);
									ljyschys=ljyschys+YS_YYXE;
									if(ys_yschys<YS_YYXE){
										hycount=hycount+ys_yschys;
										//应生成数
										Long yscs=YS_YYXE-ys_yschys;
										hycount=hycount+yscs;
										//预约开始时间
										String YS_YYKSSJ=ysm.get("YYKSSJ")==null?"":ysm.get("YYKSSJ")+":00";
										if(ys_yschys>0){
											Map<String,Object>kssjmap=dao.doSqlLoad("select max(to_char(hysj,'HH:mi') )" +
													" as YYKSSJ from MS_YYGHHY where HYKS="+GHKS+" and HYJG='"+JGID+"'" +
											" and to_char(HYSJ,'yyyy-MM-dd')='"+ds+"' and ZBLB='"+j+"'" +
											" and HYYS='"+ysm.get("YSDM")+"' ",null);
											YS_YYKSSJ=kssjmap.get("YYKSSJ")+":00";
										}
										//预约结束时间
										String YS_YYJSSJ=ysm.get("YYJSSJ")==null?"":ysm.get("YYJSSJ")+":00";
										//预约间隔
										String YS_YYJG=ysm.get("YYJG")==null?"":ysm.get("YYJG")+"";
										if(j==1){
											if(!(YS_YYKSSJ.compareTo("06:00")>0 && YS_YYKSSJ.compareTo("12:01")<0
											&&YS_YYJSSJ.compareTo("06:00")>0 && YS_YYJSSJ.compareTo("12:01")<0
											&& YYKSSJ.compareTo(YS_YYJSSJ)<0)){
												continue;
											}
										}else{
											if(!(YS_YYKSSJ.compareTo("12:00")>0 && YS_YYKSSJ.compareTo("20:01")<0
											&&YS_YYJSSJ.compareTo("12:00")>0 && YS_YYJSSJ.compareTo("20:01")<0
											&& YS_YYKSSJ.compareTo(YS_YYJSSJ)<0)){
												continue;
											}
										}
										try{
											Date ys_yyksdate=df.parse(year+"-"+month+"-"+date+" "+YS_YYKSSJ);
											Date ys_yyjsdate=df.parse(year+"-"+month+"-"+date+" "+YS_YYJSSJ);
											Date ys_nextdate=ys_yyksdate;
											for(int k=1;k<=yscs 
												&&df.format(ys_nextdate).compareTo(df.format(ys_yyjsdate))<=0;k++){
												Map<String,Object> o=new HashMap<String,Object>();
												if(ys_yschys>0){
													Calendar cl=Calendar.getInstance();
													cl.setTime(ys_yyksdate);
													cl.add(Calendar.MINUTE,Integer.parseInt(YS_YYJG));
													ys_nextdate=cl.getTime();
												}
												o.put("HYKS",Long.parseLong(GHKS));
												o.put("HYJG",JGID);
												o.put("HYYS",ysm.get("YSDM"));
												o.put("HYZT","0");
												o.put("HYSJ",ys_nextdate);
												o.put("ZBLB",j);
												o.put("GHF",ghf);
												o.put("ZLF",zlf);
												Calendar ca=Calendar.getInstance();
												ca.setTime(ys_nextdate);
												ca.add(Calendar.MINUTE,Integer.parseInt(YS_YYJG));
												ys_nextdate=ca.getTime();
												nextdate=ys_nextdate;
												try{
													dao.doSave("create",MS_YYGHHY,o,false);
													dao.doCount("MS_YYGHHY","1=2",null);
												}catch(ValidateException e){
													e.printStackTrace();
												}
											}
										}catch(ParseException e1){
											e1.printStackTrace();
										}
									}else{
										hycount=hycount+ys_yschys;
									}
								}
								if(YYXE>hycount){
									//剩余号数
									Long syhs=YYXE-hycount;
									String KS_YYKSSJ=YYKSSJ;
									Map<String,Object>kssjmap=dao.doSqlLoad("select max(to_char(hysj,'HH:mi') )" +
									" as YYKSSJ from MS_YYGHHY where HYKS="+GHKS+" and HYJG='"+JGID+"'" +
									" and to_char(HYSJ,'yyyy-MM-dd')='"+ds+"' and ZBLB='"+j+"'",null);
									KS_YYKSSJ=kssjmap.get("YYKSSJ")+":00";
									try{
										nextdate=df.parse(year+"-"+month+"-"+date+" "+KS_YYKSSJ);
									}catch(ParseException e1){
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
									Calendar cat=Calendar.getInstance();
									cat.setTime(nextdate);
									cat.add(Calendar.MINUTE,Integer.parseInt(YYJG));
									nextdate=cat.getTime();
									for(int h=1;h<=syhs&&df.format(nextdate).compareTo(df.format(yyjsdate))<=0;h++){
										Map<String,Object> o=new HashMap<String,Object>();
										o.put("HYKS",Long.parseLong(GHKS));
										o.put("HYJG",JGID);
										o.put("HYZT","0");
										o.put("HYSJ",nextdate);
										o.put("ZBLB",j);
										o.put("GHF",ghf);
										o.put("ZLF",zlf);
										Calendar ca=Calendar.getInstance();
										ca.setTime(nextdate);
										ca.add(Calendar.MINUTE,Integer.parseInt(YYJG));
										nextdate=ca.getTime();
										try{
											dao.doSave("create",MS_YYGHHY,o,false);
											dao.doCount("MS_YYGHHY","1=2",null);
										}catch(ValidateException e){
											e.printStackTrace();
										}
									}
								}
							}
							//无医生排班
							else{
								Long ks_yschys=dao.doCount("MS_YYGHHY","HYKS="+GHKS+" and HYJG='"+JGID+"'" +
								" and to_char(HYSJ,'yyyy-MM-dd')='"+ds+"' and ZBLB='"+j+"'",null);
								String KS_YYKSSJ=YYKSSJ;
								if(ks_yschys>0){
									Map<String,Object>kssjmap=dao.doSqlLoad("select max(to_char(hysj,'HH:mi') )" +
											" as YYKSSJ from MS_YYGHHY where HYKS="+GHKS+" and HYJG='"+JGID+"'" +
									" and to_char(HYSJ,'yyyy-MM-dd')='"+ds+"' and ZBLB='"+j+"'",null);
									KS_YYKSSJ=kssjmap.get("YYKSSJ")+":00";
								}
								try{
									nextdate=df.parse(year+"-"+month+"-"+date+" "+KS_YYKSSJ);
								}catch(ParseException e1){
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								for(int h=1;h<=YYXE-ks_yschys&&df.format(nextdate).compareTo(df.format(yyjsdate))<=0;h++){
									Map<String,Object> o=new HashMap<String,Object>();
									if(ks_yschys>0){
										Calendar ca=Calendar.getInstance();
										ca.setTime(nextdate);
										ca.add(Calendar.MINUTE,Integer.parseInt(YYJG));
										nextdate=ca.getTime();
									}
									o.put("HYKS",Long.parseLong(GHKS));
									o.put("HYJG",JGID);
									o.put("HYZT","0");
									o.put("HYSJ",nextdate);
									o.put("ZBLB",j);
									o.put("GHF",ghf);
									o.put("ZLF",zlf);
									Calendar ca=Calendar.getInstance();
									ca.setTime(nextdate);
									ca.add(Calendar.MINUTE,Integer.parseInt(YYJG));
									nextdate=ca.getTime();
									try{
										dao.doSave("create",MS_YYGHHY,o,false);
										dao.doCount("MS_YYGHHY","1=2",null);
									}catch(ValidateException e){
										e.printStackTrace();
									}
								}
							}
						}
					}
				}catch(PersistentDataOperationException e){
					e.printStackTrace();
				}
			}
			
		}
		//同步科室----同步预约限额大于零的挂号科室和其上级科室
		for(int t=1;t<3;t++){
			//1传科室，2传挂号科室
			StringBuffer ksxxsql=new StringBuffer();
			if(t==1){
				ksxxsql.append("select a.ORGANIZCODE as HOSPITALID,b.ORGANIZNAME as HOSPITALNAME,")
				.append("a.OFFICECODE as DEPTID,a.OFFICENAME as DEPTNAME,null as PARENTID,")
				.append("null as PARENTNAME,null as ADDRESS,a.PYCODE as KEYWORD")
				.append(" from SYS_OFFICE a,SYS_ORGANIZATION b")
				.append(" where a.ORGANIZCODE=b.ORGANIZCODE and a.LOGOFF='0'")
				.append(" and b.ORGANIZCODE in("+tbjg+")")
				.append(" and a.ID in (select distinct g.MZKS from MS_KSPB k,MS_GHKS g where k.GHKS=g.KSDM and k.YYXE>0)");
			}else{
				ksxxsql.append("select a.JGID as HOSPITALID,b.ORGANIZNAME as HOSPITALNAME,")
				.append("'gh'||a.KSDM as DEPTID,a.KSMC as DEPTNAME,c.OFFICECODE as PARENTID,")
				.append("c.OFFICENAME as PARENTNAME,a.DDXX as ADDRESS,a.PYDM as KEYWORD")
				.append(" from MS_GHKS a,SYS_ORGANIZATION b,SYS_OFFICE c ")
				.append(" where a.JGID=b.ORGANIZCODE and a.MZKS=c.ID and c.LOGOFF='0' ")
				.append(" and b.ORGANIZCODE in("+tbjg+")")
				.append(" and c.ID in (select distinct g.MZKS from MS_KSPB k,MS_GHKS g where k.GHKS=g.KSDM and k.YYXE>0)");
			}
			try{
				List<Map<String,Object>>ksxxl=dao.doSqlQuery(ksxxsql.toString(),null);
				StringBuffer s=new StringBuffer();
				s.append("<addDeptInfo>");
				for(Map<String,Object> o:ksxxl){
					s.append("<request><hospitalid>"+o.get("HOSPITALID")+"</hospitalid>")
					.append("<hospitalname>"+o.get("HOSPITALNAME")+"</hospitalname>")
					.append("<deptid>"+o.get("DEPTID")+"</deptid>")
					.append("<deptname>"+o.get("DEPTNAME")+"</deptname>");
					String PARENTID=o.get("PARENTID")==null?"":o.get("PARENTID")+"";
					s.append("<parentid>"+PARENTID+"</parentid>");
					String PARENTNAME=o.get("PARENTNAME")==null?"":o.get("PARENTNAME")+"";
					s.append("<parentname>"+PARENTNAME+"</parentname>");
					String ADDRESS=o.get("ADDRESS")==null?"":o.get("ADDRESS")+"";
					s.append("<telephoneno/><address>"+ADDRESS+"</address>")
					.append("<information/><keyword>"+o.get("KEYWORD")+"</keyword>")
					.append("<logourl/><pictureurl/><flag>1</flag><tag1/><tag2/></request>");
				}
				s.append("</addDeptInfo>");
//				System.out.println("ksxx:"+s.toString());
				//同步科室信息到app
				String re=postTowebService("hcnInterface.deptSynchronizer","addData",
				s.toString());
				try{
					Document document = DocumentHelper.parseText(re);
					Element element0 = document.getRootElement();
					Element code = element0.element("code");
					if(code.getTextTrim().equals("200")){
						logger.info(new Date()+"同步科室信息成功！");
					}else{
						logger.info(new Date()+"同步科室信息失败！");
					}
				}catch(DocumentException e){
					e.printStackTrace();
				}
			}catch(PersistentDataOperationException e2){
				e2.printStackTrace();
			}
		}
		
		//上传医生信息----只同步排班的医生
		String ysxxsql="select b.MANAGEUNITID as MANAGEUNITID,a.PERSONID as PERSONID,a.PERSONNAME as PERSONNAME," +
				" a.GENDER as GENDER,a.MAJORQUALIFY as MAJORQUALIFY,a.MOBILE as MOBILE,a.ISEXPERT as ISEXPERT," +
				" c.OFFICECODE as OFFICECODE,c.OFFICENAME as OFFICENAME,a.REMARK as REMARK," +
				" upper(a.PYCODE) as PYCODE,a.LOGOFF as LOGOFF"+
				" from SYS_PERSONNEL a join BASE_USERROLES b on  a.PERSONID=b.USERID"+
				" left join SYS_OFFICE c on a.OFFICECODE=c.ID"+
				" where b.ROLEID='phis.55' and b.LOGOFF='0' and a.OFFICECODE is not null" +
				" and a.ORGANIZCODE=b.MANAGEUNITID and b.MANAGEUNITID in("+tbjg+")"+
				" and a.PERSONID in(select distinct y.YSDM from MS_YSPB y where y.YYXE>0 and y.GZRQ>SYSDATE-30)";
		try{
			List<Map<String,Object>>ysli=dao.doSqlQuery(ysxxsql,null);
			if(ysli.size()>0){
				StringBuffer s=new StringBuffer();
				s.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><addDoctorInfo>");
				StringBuffer b=new StringBuffer();
				for(Map<String,Object> o:ysli){
					b.append("<request><hospitalid>"+o.get("MANAGEUNITID")+"</hospitalid>");
					b.append("<doctorid>"+o.get("PERSONID")+"</doctorid>");
					String PERSONNAME=o.get("PERSONNAME")+"";
					b.append("<doctorname>"+PERSONNAME+"</doctorname>");
					b.append("<doctorgender>"+o.get("GENDER")+"</doctorgender>");
					String MAJORQUALIFY=o.get("MAJORQUALIFY")+"";
					String majorname="";
					if(MAJORQUALIFY.equals("231")){
						majorname="主任医师";
					}else if(MAJORQUALIFY.equals("232")){
						majorname="副主任医师";
					}else if(MAJORQUALIFY.equals("233")){
						majorname="主治医师";
					}else if(MAJORQUALIFY.equals("234")){
						majorname="医师";
					}else if(MAJORQUALIFY.equals("235")){
						majorname="医士";
					}else if(MAJORQUALIFY.equals("236")){
						majorname="主管医师";
					}
					b.append("<doctorrank>"+majorname+"</doctorrank>");
					b.append("<telephoneno>"+o.get("MOBILE")+"</telephoneno>");
					String ISEXPERT=o.get("ISEXPERT")+"";
					if(ISEXPERT.equals("1")){
						b.append("<workrankid>3</workrankid>");
						b.append("<workrank>专家门诊</workrank>");
					}else{
						b.append("<workrankid>1</workrankid>");
						b.append("<workrank>普通门诊</workrank>");
					}
					String OFFICECODE=o.get("OFFICECODE")==null?"":o.get("OFFICECODE")+"";
					b.append("<deptid>"+OFFICECODE+"</deptid>");
					String OFFICENAME=o.get("OFFICENAME")==null?"":o.get("OFFICENAME")+"";
					b.append("<deptname>"+OFFICENAME+"</deptname>");
					String REMARK=o.get("REMARK")==null?"":o.get("REMARK")+"";
					b.append("<information>"+REMARK+"</information>");
					String PYCODE=o.get("PYCODE")==null?"":o.get("PYCODE")+"";
					b.append("<keyword>"+PYCODE+"</keyword>");
					b.append("<pictureurl/>");
					String flag=(o.get("LOGOFF")+"").equals("0")?"1":"2";
					b.append("<flag>"+flag+"</flag>");
					b.append("</request>");
					s.append(b);
					b=new StringBuffer();
				}
				s.append("</addDoctorInfo>");
				//同步医生信息到app
				System.out.println("ysxx:"+s.toString());
				String re=postTowebService("hcnInterface.doctorSynchronizer","addData",
				s.toString());
				try{
					Document document = DocumentHelper.parseText(re);
					Element element0 = document.getRootElement();
					Element code = element0.element("code");
					if(code.getTextTrim().equals("200")){
						logger.info(new Date()+"同步医生信息成功！");
					}else{
						logger.info(new Date()+"同步医生信息失败！");
					}
				}catch(DocumentException e){
					e.printStackTrace();
				}
			}
		}catch(PersistentDataOperationException e){
			e.printStackTrace();
		}
		//科室、医生排班信息同步
		for(int k=1;k<3;k++){
			DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
			String td=sdf.format(new Date());
			StringBuffer pbsql= new StringBuffer();
			if(k==1){
				Calendar pdd=Calendar.getInstance();
				pdd.setTime(new Date());
				for(int l=start;l<=7;l++){
					pdd.add(Calendar.DATE,1);
					pbsql=new StringBuffer();
					pbsql.append("select a.JGID as HOSPITALID,'"+sdf.format(pdd.getTime())+"' as WORKDATE,")
					.append("a.ZBLB as ZBLB,a.YYKSSJ as STARTTIME,'gh'||a.GHKS as DEPTID,")
					.append("a.YYJSSJ as ENDTIME,'' as DOCTORID,'' as DOCTORNAME,")
					.append("'' as GENDER,'' as MAJORQUALIFY,c.ZJMZ as ISEXPERT,")
					.append("c.KSMC as DEPTNAME,c.ZLF as PRICE,c.GHF as ORDERFEE,c.DDXX as ADMITADDRESS,")
					.append("a.YYXE as ORDERCOUNT,a.YYRS as YYRS")
					.append(" from MS_KSPB a join MS_GHKS c on a.GHKS=c.KSDM ")
					.append(" where a.YYXE>0 and a.YYKSSJ is not null and a.JGID in ("+tbjg+")")
					.append(" and a.GHRQ="+l);
					pbxxToApp(pbsql.toString(),dao);
				}
				for(int l=1;l<start;l++){
					pdd.add(Calendar.DATE,1);
					pbsql=new StringBuffer();
					pbsql.append("select a.JGID as HOSPITALID,'"+sdf.format(pdd.getTime())+"' as WORKDATE,")
					.append("a.ZBLB as ZBLB,a.YYKSSJ as STARTTIME,'gh'||a.GHKS as DEPTID,")
					.append("a.YYJSSJ as ENDTIME,'' as DOCTORID,'' as DOCTORNAME,")
					.append("'' as GENDER,'' as MAJORQUALIFY,c.ZJMZ as ISEXPERT,")
					.append("c.KSMC as DEPTNAME,c.ZLF as PRICE,c.GHF as ORDERFEE,c.DDXX as ADMITADDRESS,")
					.append("a.YYXE as ORDERCOUNT,a.YYRS as YYRS")
					.append(" from MS_KSPB a join MS_GHKS c on a.GHKS=c.KSDM ")
					.append(" where a.YYXE>0 and a.YYKSSJ is not null and a.JGID in ("+tbjg+")")
					.append(" and a.GHRQ="+l);
					pbxxToApp(pbsql.toString(),dao);
				}
			}else{
				pbsql=new StringBuffer();
				pbsql.append("select a.JGID as HOSPITALID,to_char(a.GZRQ,'yyyy-MM-dd') as WORKDATE,")
				.append("a.ZBLB as ZBLB,a.YYKSSJ as STARTTIME,'gh'||a.KSDM as DEPTID,")
				.append("a.YYJSSJ as ENDTIME,a.YSDM as DOCTORID,b.PERSONNAME as DOCTORNAME,")
				.append("b.GENDER as GENDER,b.MAJORQUALIFY as MAJORQUALIFY,b.ISEXPERT as ISEXPERT,")
				.append("c.KSMC as DEPTNAME,c.ZLF as PRICE,c.GHF as ORDERFEE,c.DDXX as ADMITADDRESS,")
				.append("a.YYXE as ORDERCOUNT,a.YYRS as YYRS")
				.append(" from MS_YSPB a join SYS_PERSONNEL b on a.YSDM=b.PERSONID")
				.append(" join MS_GHKS c on a.ksdm=c.ksdm where to_char(a.gzrq,'yyyy-MM-dd')>'"+td+"' ")
				.append(" and a.YYXE>0 and a.YYKSSJ is not null and a.JGID in ("+tbjg+")");
				pbxxToApp(pbsql.toString(),dao);
			}
		}
	}
	public static void pbxxToApp(String sql,BaseDAO dao){
		try{
			System.out.println("sql:"+sql);
			List<Map<String,Object>> pbl=dao.doSqlQuery(sql.toString(),null);
			StringBuffer sb=new StringBuffer();
			sb.append("<addWorkPbInfo>");
			for(Map<String,Object> o:pbl){
				sb.append("<request><hospitalid>"+o.get("HOSPITALID")+"</hospitalid>");
				String WORKDATE=o.get("WORKDATE")+"";
				String DOCTORID=o.get("DOCTORID")==null?"":o.get("DOCTORID")+"";
				String DEPTID=o.get("DEPTID")==null?"":o.get("DEPTID")+"";
				String wds=WORKDATE.replaceAll("-","");
				sb.append("<workid>"+wds+"_"+DEPTID+"_"+DOCTORID+"</workid>");
				sb.append("<workdate>"+WORKDATE+" 00:00:00"+"</workdate>");
				String periodcode="1";
				if((o.get("ZBLB")+"").equals("2")){
					periodcode="3";
				}
				sb.append("<periodcode>"+periodcode+"</periodcode>")
				.append("<starttime>"+o.get("STARTTIME")+"</starttime>")
				.append("<endtime>"+o.get("ENDTIME")+"</endtime>")
				.append("<doctorid>"+DOCTORID+"</doctorid>");
				String DOCTORNAME=o.get("DOCTORNAME")==null?"":o.get("DOCTORNAME")+"";
				sb.append("<doctorname>"+DOCTORNAME+"</doctorname>");
				String doctorgender="";
				if((o.get("GENDER")+"").equals("1")){
					doctorgender="男";
				}else if((o.get("GENDER")+"").equals("2")){
					doctorgender="女";
				}
				sb.append("<doctorgender>"+doctorgender+"</doctorgender>");
				String MAJORQUALIFY=o.get("MAJORQUALIFY")+"";
				String majorname="";
				if(MAJORQUALIFY.equals("231")){
					majorname="主任医师";
				}else if(MAJORQUALIFY.equals("232")){
					majorname="副主任医师";
				}else if(MAJORQUALIFY.equals("233")){
					majorname="主治医师";
				}else if(MAJORQUALIFY.equals("234")){
					majorname="医师";
				}else if(MAJORQUALIFY.equals("235")){
					majorname="医士";
				}else if(MAJORQUALIFY.equals("236")){
					majorname="主管医师";
				}
				sb.append("<doctorrank>"+majorname+"</doctorrank>");
				String ISEXPERT=o.get("ISEXPERT")+"";
				if(ISEXPERT.equals("1")){
					sb.append("<workrankid>3</workrankid>");
					sb.append("<workrank>专家门诊</workrank>");
				}else{
					sb.append("<workrankid>1</workrankid>");
					sb.append("<workrank>普通门诊</workrank>");
				}
				sb.append("<deptid>"+DEPTID+"</deptid>").append("<deptname>"+o.get("DEPTNAME")+"</deptname>");
				sb.append("<price>"+o.get("PRICE")+"</price><orderfee>"+o.get("PRICE")+"</orderfee>");
				String ADMITADDRESS=o.get("ADMITADDRESS")==null?"":o.get("ADMITADDRESS")+"";
				sb.append("<admitaddress>"+ADMITADDRESS+"</admitaddress>");
				int ORDERCOUNT=Integer.parseInt(o.get("ORDERCOUNT")+"");
				int YYRS=Integer.parseInt(o.get("YYRS")+"");
				int remain=ORDERCOUNT-YYRS;
				sb.append("<ordercount>"+ORDERCOUNT+"</ordercount><reason></reason>")
				.append("<suggestworkid></suggestworkid><flag>0</flag>")
				.append("<remain>"+remain+"</remain><tag1></tag1><tag2></tag2><ordernumbers></ordernumbers></request>");
			}
			sb.append("</addWorkPbInfo>");
			//科室、同步医生排班信息到app
			System.out.println("pbxx:"+sb.toString());
			String re=postTowebService("hcnInterface.regPlanService","addData",
			sb.toString());
			try{
				Document document = DocumentHelper.parseText(re);
				Element element0 = document.getRootElement();
				Element code = element0.element("code");
				if(code.getTextTrim().equals("200")){
					logger.info(new Date()+"同步排班信息信息成功！");
				}else{
					logger.info(new Date()+"同步排班信息失败！");
				}
			}catch(DocumentException e){
				e.printStackTrace();
			}
		}catch(PersistentDataOperationException e1){
			e1.printStackTrace();
		}
	}
	public static String postTowebService(String service,String method,String params){
		DictionaryController d=DictionaryController.instance();
		try{
			Dictionary fs=d.get("phis.dictionary.fsyypzxx");
			String wsUrl=fs.getItem("wsUrl").getText();
			String appId=fs.getItem("appId").getText();
			String pwd=fs.getItem("pwd").getText();
			try{
				URL wurl=new URL(wsUrl);
				try{
					HttpURLConnection conn=(HttpURLConnection)wurl.openConnection();
					conn.setDoInput(true);
			        conn.setDoOutput(true);
			        conn.setRequestMethod("POST");
			        conn.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
			        OutputStream os = conn.getOutputStream();
			        String soap = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:v2=\"http://www.bsoft.com/bs-hcn/api/v2.0\">"+
			        	"   <soapenv:Header/>"+
			        	"   <soapenv:Body>"+
			        	"      <v2:invoke>"+
			        	"         <appId>"+appId+"</appId>"+
			        	"         <pwd>"+pwd+"</pwd>"+
			        	"         <service>"+service+"</service>"+
			        	"         <method>"+method+"</method>"+
			        	"         <params>"+
			        	"            <item><![CDATA["+params+"]]></item>"+
			        	"         </params>"+
			        	"      </v2:invoke>"+
			        	"   </soapenv:Body>"+
			        	"</soapenv:Envelope>";
			        os.write(soap.getBytes("UTF-8"));
			        InputStream is = conn.getInputStream();
			        byte[] b = new byte[1024];
			        int len = 0;
			        String s = "";
			        while((len = is.read(b)) != -1){
			            String ss = new String(b,0,len,"UTF-8");
			            s += ss;
			        }
			        s=s.replaceAll("&lt;","<");
			        s=s.replaceAll("&gt;",">");
			        String re=s.substring(s.indexOf("<return>")+8,s.indexOf("</return>"));
			        is.close();
			        os.close();
			        conn.disconnect();
			        return re;
				}catch(IOException e){
					e.printStackTrace();
				}
			}catch(MalformedURLException e){
				e.printStackTrace();
			}
		}catch(ControllerException e){
			e.printStackTrace();
		}
		return "";
	}
public static void main(String[] args){
	Calendar cal=Calendar.getInstance();
	cal.setTime(new Date());
	System.out.println(cal.get(Calendar.DAY_OF_WEEK));
	String a="11:30";
	String b="12:30";
	System.out.println(a.compareTo(b));
	DateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	System.out.println(df.parse("2018-12-31 00:00:00"));
	try{
		cal.setTime(df.parse("2018-12-31 00:00:00"));
		cal.add(Calendar.DATE,1);
		System.out.println(df.format(cal.getTime()));
	}catch(ParseException e){
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
}
