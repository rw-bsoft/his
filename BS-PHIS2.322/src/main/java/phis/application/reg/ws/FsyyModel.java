package phis.application.reg.ws;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ctd.account.AccountCenter;
import ctd.account.user.User;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;
import ctd.validator.ValidateException;
import phis.application.pix.source.EmpiModel;
import phis.application.pix.source.EmpiUtil;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.utils.CNDHelper;

public class FsyyModel {
	DateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	protected Logger logger = LoggerFactory.getLogger(FsyyModel.class);
	public static BaseDAO getDAO(Session session) {
		BaseDAO dao = new BaseDAO(ContextUtils.getContext(), session);
		return dao;
	}
	public String GetWorkInfoBy(String request,Session sen) {
		BaseDAO dao = getDAO(sen);
		String HospitalId="";
		String PeriodCode="";
		String DoctorId="";
		String workDate="";
		String DeptId="";
		try{
			Document document=DocumentHelper.parseText(request);
			Element Request = document.getRootElement();
			Element Parameter = Request.element("Parameter");
			HospitalId=Parameter.element("HospitalId").getTextTrim();
			PeriodCode=Parameter.element("PeriodCode").getTextTrim();
			DoctorId=Parameter.element("DoctorId").getTextTrim();
			workDate=Parameter.element("WorkDate").getTextTrim();
			DeptId=Parameter.element("DeptId").getTextTrim();
		}catch(DocumentException e1){
			e1.printStackTrace();
		}
		String zblb="1";
		if(PeriodCode.equals("3")){
			zblb="2";
		}
		Map<String,Object> p=new HashMap<String,Object>();
		p.put("HYJG",HospitalId);
		if(DoctorId.length()>0)
		p.put("HYYS",DoctorId);
		p.put("HYSJ",workDate);
		p.put("ZBLB",zblb);
		p.put("HYKS",Long.parseLong(DeptId.replaceAll("gh","")));
		String ResultCode="1";
		String ErrorMsg="";
		String DoctorName="";
		String DoctorGender="";
		String DoctorRank="";
		String WorkRankID="";
		String WorkRank="";
		String DeptName="";
		String Price="";
		String OrderFee="";
		String AdmitAddress="";
		Long Count=0L;//预约总数
		StringBuffer sb=new StringBuffer();
		try{
			if(DoctorId.length()>0){
				StringBuffer where=new StringBuffer();
				where.append("HYJG=:HYJG and HYYS=:HYYS ")
				.append(" and to_char(HYSJ,'yyyy-MM-dd')=:HYSJ and ZBLB=:ZBLB and HYKS=:HYKS");
				Count=dao.doCount("MS_YYGHHY",where.toString(),p);
				if(Count>0){
						StringBuffer sql=new StringBuffer();
						sql.append("select 'gh'||a.KSDM as DEPTID, ")
						.append("b.KSMC as DEPTNAME,b.ZLF as PRICE,b.GHF as ORDERFEE,b.DDXX as ADMITADDRESS")
						.append(" from MS_YSPB a,MS_GHKS b where a.JGID=:HYJG and to_char(a.GZRQ,'yyyy-MM-dd')=:HYSJ and a.ZBLB=:ZBLB")
						.append(" and a.KSDM=b.KSDM and a.YSDM=:HYYS and a.KSDM=:HYKS");
						Map<String,Object>pm=dao.doSqlLoad(sql.toString(),p);
						String ysxxsql="select PERSONNAME as PERSONNAME,GENDER as GENDER,MAJORQUALIFY as MAJORQUALIFY," +
								"ISEXPERT as ISEXPERT from SYS_PERSONNEL a where a.PERSONID='"+DoctorId+"'";
						Map<String,Object> ysxxmap=dao.doSqlLoad(ysxxsql,null);
						DoctorName=ysxxmap.get("PERSONNAME")+"";
						DoctorGender=(ysxxmap.get("GENDER")+"").equals("1")?"男":"女";
						String MAJORQUALIFY=ysxxmap.get("MAJORQUALIFY")==null?"":ysxxmap.get("MAJORQUALIFY")+"";
						if(MAJORQUALIFY.equals("231")){
							DoctorRank="主任医师";
						}else if(MAJORQUALIFY.equals("232")){
							DoctorRank="副主任医师";
						}else if(MAJORQUALIFY.equals("233")){
							DoctorRank="主治医师";
						}else if(MAJORQUALIFY.equals("234")){
							DoctorRank="医师";
						}else if(MAJORQUALIFY.equals("235")){
							DoctorRank="医士";
						}else if(MAJORQUALIFY.equals("236")){
							DoctorRank="主管医师";
						}
						if((ysxxmap.get("ISEXPERT")+"").equals("1")){
							WorkRankID="3";
							WorkRank="专家门诊";
						}else{
							WorkRankID="1";
							WorkRank="普通门诊";
						}
					DeptId=pm.get("DEPTID")+"";
					DeptName=pm.get("DEPTNAME")+"";
					Price=pm.get("PRICE")+"";
					OrderFee=pm.get("ORDERFEE")+"";
					AdmitAddress=pm.get("ADMITADDRESS")==null?"":pm.get("ADMITADDRESS")+"";
					String fsdsql="select SDHH as SDHH,SDMI as SDMI,HYZS as HYZS,KYYS as KYYS from (" +
							"select (to_char(HYSJ,'HH24') ) as SDHH, case when to_char(hysj,'mi') >='30'" +
							" then '30' else '00' end  as SDMI ,count(1) as HYZS," +
							" sum(case when HYZT='0' then 1 else 0 end) as KYYS from MS_YYGHHY"+
							" where HYJG=:HYJG and HYYS=:HYYS and to_char(HYSJ,'yyyy-MM-dd')=:HYSJ" +
							" and ZBLB=:ZBLB and HYKS=:HYKS group by (to_char(hysj,'HH24'))," +
							" case when to_char(HYSJ,'mi') >='30' then '30' else '00' end )" +
							" order by SDHH,SDMI ";
					List<Map<String,Object>>fsdl=dao.doSqlQuery(fsdsql,p);
					for(Map<String,Object> o:fsdl){
						String StartTime="";
						String SDHH=o.get("SDHH")+"";
						String SDMI=o.get("SDMI")+"";
						StartTime=SDHH+":"+SDMI;
						String EndTime="";
						if(SDMI.equals("00")){
							EndTime=SDHH+":30";
						}else{
							int h=Integer.parseInt(SDHH)+1;
							if((h+"").length()==1){
								EndTime="0"+h+":00";
							}else{
								EndTime=h+":00";
							}
						}
						String OrderCount=o.get("HYZS")+"";
						String AvailableCount=o.get("KYYS")+"";
						String WorkId=HospitalId+"_"+workDate.replaceAll("-","")+"_"+DoctorId+"_"+StartTime;
						sb.append("<Record><HospitalId>"+HospitalId+"</HospitalId><WorkId>"+WorkId+"</WorkId>")
						.append("<WorkDate>"+workDate+"</WorkDate><PeriodCode>"+PeriodCode+"</PeriodCode>")
						.append("<StartTime>"+StartTime+"</StartTime><EndTime>"+EndTime+"</EndTime>")
						.append("<DoctorId>"+DoctorId+"</DoctorId><DoctorName>"+DoctorName+"</DoctorName>")
						.append("<DoctorGender>"+DoctorGender+"</DoctorGender><DoctorRank>"+DoctorRank+"</DoctorRank>")
						.append("<WorkRankID>"+WorkRankID+"</WorkRankID><WorkRank>"+WorkRank+"</WorkRank>")
						.append("<DeptId>"+DeptId+"</DeptId><DeptName>"+DeptName+"</DeptName><Price>"+Price+"</Price>")
						.append("<OrderFee>"+OrderFee+"</OrderFee><AdmitAddress>"+AdmitAddress+"</AdmitAddress>")
						.append("<OrderCount>"+OrderCount+"</OrderCount><AvailableCount>"+AvailableCount+"</AvailableCount>")
						.append("<Flag>1</Flag></Record>");
					}
					ResultCode="0";
					ErrorMsg="操作成功";
				}else{
					ErrorMsg="未查询到排班信息";
				}
			}else{
				StringBuffer where=new StringBuffer();
				where.append("HYJG=:HYJG and HYYS is null ")
				.append(" and to_char(HYSJ,'yyyy-MM-dd')=:HYSJ and ZBLB=:ZBLB and HYKS=:HYKS");
				System.out.println("select count(1) from MS_YYGHHY where "+where.toString());
				Count=dao.doCount("MS_YYGHHY",where.toString(),p);
				if(Count>0){
					StringBuffer sql=new StringBuffer();
					sql.append("select HYKS as HYKS, SDHH as SDHH,SDMI as SDMI,HYZS as HYZS,KYYS as KYYS")
					.append(" from (select HYKS as HYKS,(to_char(HYSJ,'HH24')) as SDHH,")
					.append("case when to_char(hysj,'mi')>='30' then '30' else '00' end  as SDMI ,count(1) as HYZS,")
					.append("sum(case when HYZT='0' then 1 else 0 end) as KYYS from MS_YYGHHY")
					.append(" where HYJG=:HYJG and to_char(HYSJ,'yyyy-MM-dd')=:HYSJ")
					.append(" and ZBLB=:ZBLB and HYYS is null and HYKS=:HYKS group by HYKS,(to_char(hysj,'HH24')),")
					.append("case when to_char(HYSJ,'mi') >='30' then '30' else '00' end ) order by HYKS,SDHH,SDMI");
					List<Map<String,Object>>fsdl=dao.doSqlQuery(sql.toString(),p);
					StringBuffer ksxxsql=new StringBuffer();
					ksxxsql.append("select a.KSMC as KSMC,a.GHF as GHF,a.ZLF as ZLF from MS_GHKS a")
					.append(" where a.KSDM="+p.get("HYKS"));
					Map<String,Object> ksxxmap=dao.doSqlLoad(ksxxsql.toString(),null);
					for(Map<String,Object>o:fsdl){
						String StartTime="";
						String SDHH=o.get("SDHH")+"";
						String SDMI=o.get("SDMI")+"";
						StartTime=SDHH+":"+SDMI;
						String EndTime="";
						if(SDMI.equals("00")){
							EndTime=SDHH+":30";
						}else{
							int h=Integer.parseInt(SDHH)+1;
							if((h+"").length()==1){
								EndTime="0"+h+":00";
							}else{
								EndTime=h+":00";
							}
						}
						DeptId="gh"+o.get("HYKS");
						String OrderCount=o.get("HYZS")+"";
						String AvailableCount=o.get("KYYS")+"";
						String WorkId=HospitalId+"_"+workDate.replaceAll("-","")+"_"+DeptId+"_"+StartTime;
						sb.append("<Record><HospitalId>"+HospitalId+"</HospitalId><WorkId>"+WorkId+"</WorkId>")
						.append("<WorkDate>"+workDate+"</WorkDate><PeriodCode>"+PeriodCode+"</PeriodCode>")
						.append("<StartTime>"+StartTime+"</StartTime><EndTime>"+EndTime+"</EndTime>")
						.append("<DoctorId></DoctorId><DoctorName></DoctorName>")
						.append("<DoctorGender></DoctorGender><DoctorRank></DoctorRank>")
						.append("<WorkRankID>1</WorkRankID><WorkRank>普通门诊</WorkRank>")
						.append("<DeptId>"+DeptId+"</DeptId><DeptName>"+ksxxmap.get("KSMC")+"</DeptName><Price>"+ksxxmap.get("ZLF")+"</Price>")
						.append("<OrderFee>"+ksxxmap.get("GHF")+"</OrderFee><AdmitAddress>"+AdmitAddress+"</AdmitAddress>")
						.append("<OrderCount>"+OrderCount+"</OrderCount><AvailableCount>"+AvailableCount+"</AvailableCount>")
						.append("<Flag>1</Flag></Record>");
					}
				}
			}
		}catch(PersistentDataOperationException e){
			e.printStackTrace();
		}
		StringBuffer s=new StringBuffer();
		s.append("<Response><ResultCode>"+ResultCode+"</ResultCode><ErrorMsg>"+ErrorMsg+"</ErrorMsg>")
		.append("<Result>"+sb.toString()+"</Result></Response>");
		System.out.println(s.toString());
		return s.toString();
	}
	public String doSaveOrder(String request,Session sen) {
		BaseDAO dao = getDAO(sen);
		String HospitalId="";
		String Source="";
		String WorkId="";
		String CardId="";
		String CardType="";
		String LoginUserCardType="";
		String LoginUserIdCard="";
		String PatientName="";
		String PatientGender="";
		String PatientBirthday="";
		String TelePhoneNo="";
		String Address="";
		String deptid="";
		String deptname="";
		String doctors="";
		String doctorName="";
		String orderdate="";
		String time="";
		try{
			Document document=DocumentHelper.parseText(request);
			Element Request = document.getRootElement();
			Element Parameter = Request.element("Parameter");
			HospitalId=Parameter.element("HospitalId").getTextTrim();
			Source=Parameter.element("Source").getTextTrim();
			WorkId=Parameter.element("WorkId").getTextTrim();
			CardId=Parameter.element("CardId").getTextTrim();
			CardType=Parameter.element("CardType").getTextTrim();
			if(Parameter.element("LoginUserCardType")!=null)
				LoginUserCardType=Parameter.element("LoginUserCardType").getTextTrim();
			if(Parameter.element("LoginUserIdCard")!=null)
				LoginUserIdCard=Parameter.element("LoginUserIdCard").getTextTrim();
			PatientName=Parameter.element("PatientName").getTextTrim();
			PatientGender=Parameter.element("PatientGender").getTextTrim();
			PatientBirthday=Parameter.element("PatientBirthday").getTextTrim();
			TelePhoneNo=Parameter.element("TelePhoneNo").getTextTrim();
			Address=Parameter.element("Address").getTextTrim();
			deptid=Parameter.element("deptid").getTextTrim();
			deptname=Parameter.element("deptname").getTextTrim();
			if(Parameter.element("doctors")!=null)
				doctors=Parameter.element("doctors").getTextTrim();
			if(Parameter.element("doctorName")!=null)
				doctorName=Parameter.element("doctorName").getTextTrim();
			orderdate=Parameter.element("orderdate").getTextTrim();
			time=Parameter.element("time").getTextTrim();
		}catch(DocumentException e1){
			System.out.println("提交预约挂号信息传入的参数不对");
			return "<Response><ResultCode>1</ResultCode><ErrorMsg>传入参数错误</ErrorMsg></Response>";
		}
		DictionaryController d=DictionaryController.instance();
		String ygbm="";
		try{
			Dictionary yg=d.get("phis.dictionary.YYGHYG");
			ygbm=yg.getItem(HospitalId).getText();
		}catch(ControllerException e3){
			e3.printStackTrace();
		}
		if(ygbm.length()==0){
			return "<Response><ResultCode>1</ResultCode><ErrorMsg>请联系管理员维护默认的挂号员工</ErrorMsg></Response>";
		}
		String sdxh=Math.random()+"";
		StringBuffer upsql=new StringBuffer();
		String hyks=deptid.replaceAll("gh","");
		String[] ta=time.split("-");
		upsql.append("update MS_YYGHHY a set a.SDXH='"+sdxh+"',a.HYZT='2',a.SDSJ=sysdate,")
		.append("SOURCE="+Source+",CARDID='"+CardId+"',CARDTYPE='"+CardType+"',")
		.append("LOGINUSERCARDTYPE='"+LoginUserCardType+"',LOGINUSERIDCARD='"+LoginUserIdCard+"',")
		.append("PATIENTNAME='"+PatientName+"',PATIENTGENDER="+PatientGender+",")
		.append("PATIENTBIRTHDAY='"+PatientBirthday+"',TELEPHONENO='"+TelePhoneNo+"',")
		.append("ADDRESS='"+Address+"'")
		.append(" where a.HYJG='"+HospitalId+"'")
		.append(" and to_char(a.HYSJ,'yyyy-MM-dd')='"+orderdate+"'");
		if(doctors.length()>0){
			upsql.append(" and a.HYYS='"+doctors+"'");
		}else{
			upsql.append(" and a.HYYS is null");
		}
		upsql.append(" and a.HYKS="+hyks+" and to_char(a.HYSJ,'HH24:mi')>='"+ta[0]+"' ")
		.append(" and to_char(a.HYSJ,'HH24:mi')<'"+ta[1]+"' and a.HYZT='0' and ROWNUM=1");
		try{
			int uprow=dao.doSqlUpdate(upsql.toString(),null);
			if(uprow==1){
				StringBuffer getsql=new StringBuffer();
				getsql.append("select a.HYXH as HYXH,to_char(a.HYSJ,'yyyy-MM-dd HH:mi:ss') as HYSJ,a.ZBLB as ZBLB from MS_YYGHHY a ")
				.append(" where a.SDXH='"+sdxh+"' and a.HYJG='"+HospitalId+"' ")
				.append(" and to_char(a.HYSJ,'yyyy-mm-dd')='"+orderdate+"'");
				Map<String,Object> hymap=dao.doSqlLoad(getsql.toString(),null);
				Long hyxh=Long.parseLong(hymap.get("HYXH")+"");
				if(CardType.equals("01")){
					if(CardId.length()<18){
						return "<Response><ResultCode>1</ResultCode><ErrorMsg>身份证位数不正确</ErrorMsg></Response>";
					}
					Context ctx = ContextUtils.getContext();
					ctx.put(Context.DB_SESSION,sen);
					if (!ctx.containsKey(Context.USER_ROLE_TOKEN)) {
						User user;
						try{
							user=AccountCenter.getUser("0310581X");
							Object[] urs = user.getUserRoleTokens().toArray();
							ctx.put(Context.USER_ROLE_TOKEN, urs[0]);
						}catch(ControllerException e){
							e.printStackTrace();
						}
					}
					Map<String, Object> reqBody = new HashMap<String, Object>();
					reqBody.put("idCard",CardId);
					Map<String, Object> result=new HashMap<String,Object>();
					try{
						Map<String, Object> query=EmpiUtil.queryByIdCardAndName(dao,ctx,reqBody);
						List<Map<String, Object>> list=(List<Map<String, Object>>)query.get("body");
						if(list==null){
							Map<String, Object> body=new HashMap<String,Object>();
							body.put("personName",PatientName);
							body.put("idCard",CardId);
							body.put("sexCode",PatientGender);
							
							DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
							try{
								if(PatientBirthday.length()>6)
									body.put("birthday",sdf.parse(PatientBirthday));
							}catch(ParseException e){
								e.printStackTrace();
								return "<Response><ResultCode>1</ResultCode><ErrorMsg>出生日期转换出错</ErrorMsg></Response>";
							}
							body.put("mobileNumber",TelePhoneNo);
							body.put("contact",PatientName);
							body.put("contactPhone",TelePhoneNo);
							body.put("bloodTypeCode","5");
							body.put("maritalStatusCode","5");
							body.put("BRXZ","1000");
							double dd=Math.random();
							String mzhm="40"+(dd+"").substring(2,12);
							List<Map<String, Object>> cards= new ArrayList<Map<String,Object>>();
							Map<String, Object> card2=new HashMap<String, Object>();
							card2.put("cardNo", mzhm);
							body.put("MZHM", mzhm);
							card2.put("cardTypeCode","04");
							cards.add(card2);
							body.put("cards",cards);
							Map<String, Object> records = new HashMap<String, Object>();
							records.putAll(body);
							records = EmpiUtil.changeToPIXFormat(records);
							records.put("photo","");
							//保存人员信息
							 result=EmpiUtil.submitPerson(dao,ctx,body,records);
						}else if(list.size()>1){
							throw new ServiceException("同一身份证号在档案表有两份档案！请联系管理员处理！");
						}else{
							Map<String, Object> brxx=list.get(0);
							String empiid=brxx.get("empiId").toString();
							String uppersonsql="update MPI_DemographicInfo set personName='"+PatientName+"' where empiId=:empiId";
							Map<String, Object> pa=new HashMap<String, Object>();
							pa.put("empiId", empiid);
							try {
								dao.doUpdate(uppersonsql,pa);
							} catch (PersistentDataOperationException e2) {
								e2.printStackTrace();
							}
							result.put("empiId", empiid);
							List<?> cnd = CNDHelper.createSimpleCnd("eq", "EMPIID", "s", empiid);
							Map<String, Object> brda=new HashMap<String, Object>();
							try {
								brda=dao.doLoad(cnd, BSPHISEntryNames.MS_BRDA);
							} catch (PersistentDataOperationException e1) {
								e1.printStackTrace();
							}
							if(brda==null || brda.size()<=0){
								EmpiModel em=new EmpiModel(dao);
								String tempmzhm="";
								//判断是否存在门诊号
								String sql="select cardNo as cardNo from MPI_Card where empiId=:empiId " +
										" and cardTypeCode=:cardTypeCode";
								Map<String, Object> p=new HashMap<String, Object>();
								p.put("empiId", empiid);
								p.put("cardTypeCode","04");
								boolean cardinsertflag=true;
								try {
									Map<String, Object> cardxx=dao.doSqlLoad(sql, p);
									if(cardxx!=null && cardxx.size() >0){
										tempmzhm=cardxx.get("CARDNO")+"";
										cardinsertflag=false;
									}
								} catch (PersistentDataOperationException e1) {
									e1.printStackTrace();
								}
								if(cardinsertflag){
									double dd=Math.random();
									tempmzhm="40"+(dd+"").substring(2,12);
									Map<String, Object> card=new HashMap<String, Object>();
									card.put("cardNo", tempmzhm);
									card.put("cardTypeCode", "04");
									card.put("empiId", empiid);
									try {
										em.saveCard(card);
									} catch (ModelDataOperationException e) {
										e.printStackTrace();
									}
								}
								brxx.put("MZHM", tempmzhm);
								brxx.put("BRXZ", 1000);
								brxx.put("HKDZ",Address.length()==0?"未填":Address);
								brxx.put("BRXM",PatientName);
								brxx.put("sexCode",PatientGender);
								try {
									//保存病人档案
									em.saveBRDA(brxx,ctx);
								} catch (ModelDataOperationException e) {
									e.printStackTrace();
								}
							}
						}
						Map<String, Object> bridmap=dao.doSqlLoad("select BRID as BRID from MS_BRDA " +
								" where EMPIID='"+result.get("empiId")+"'",null);
						Long brid=Long.parseLong(bridmap.get("BRID")+"");
						Map<String, Object> ksxxmap=dao.doSqlLoad("select GHF as GHF,ZLF as ZLF from MS_GHKS " +
								" where KSDM="+deptid.replaceAll("gh",""),null);
						DateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						StringBuffer upyysql=new StringBuffer();
						upyysql.append("update MS_YYGHHY a set a.SFZH='"+CardId+"',a.BRID="+brid)
						.append(" where a.SDXH='"+sdxh+"' and a.HYJG='"+HospitalId+"' ")
						.append(" and to_char(a.HYSJ,'yyyy-mm-dd')='"+orderdate+"'");
						dao.doSqlUpdate(upyysql.toString(),null);
						StringBuffer sb=new StringBuffer();
						String dts=df.format(new Date());
						sb.append("<Response><Result><Record><yyid>"+hyxh+"</yyid>")
						.append("<HospitalId>"+HospitalId+"</HospitalId><Source>"+Source+"</Source>")
						.append("<WorkId>"+WorkId+"</WorkId><OrderTime>"+hymap.get("HYSJ")+"</OrderTime>")
						.append("<CardId>"+CardId+"</CardId><CardType>"+CardType+"</CardType>")
						.append("<PatientName>"+PatientName+"</PatientName><PatientGender>"+PatientGender+"</PatientGender>")
						.append("<TelePhoneNo>"+TelePhoneNo+"</TelePhoneNo><Address>"+Address+"</Address>")
						.append("<SeqCode></SeqCode><deptid>"+deptid+"</deptid>")
						.append("<creat__time>"+dts+"</creat__time><LoginUserCardType>"+LoginUserCardType+"</LoginUserCardType>")
						.append("<LoginUserIdCard>"+LoginUserIdCard+"</LoginUserIdCard><doctors>"+doctors+"</doctors>")
						.append("<doctorName>"+doctorName+"</doctorName><time>"+time+"</time>")
						.append("<deptname>"+deptname+"</deptname><orderdate class=\"sql-date\">"+orderdate+"</orderdate>");
						String zblb=hymap.get("ZBLB")+"";
						if(zblb.equals("2")){
							zblb="3";
						}
						sb.append("<PeriodCode>"+zblb+"</PeriodCode></Record></Result><ResultCode>0</ResultCode>")
						.append("<ErrorMsg>处理成功</ErrorMsg></Response>");
						System.out.println("re:"+sb.toString());
						return sb.toString();
					}catch(ServiceException e){
						e.printStackTrace();
					}
				}else{
					return "<Response><ResultCode>1</ResultCode><ErrorMsg>暂不支持卡类型不是01的预约</ErrorMsg></Response>";
				}
				
			}else{
				return "<Response><ResultCode>1</ResultCode><ErrorMsg>已没有可预约资源</ErrorMsg></Response>";
			}
		}catch(PersistentDataOperationException e){
			e.printStackTrace();
		}
		return "";
	}
	
	public String doSaveCancelOrder(String request,Session sen) {
		System.out.println("取消预约传入信息:"+request);
		BaseDAO dao = getDAO(sen);
		String HospitalId="";
		String Source="";
		String WorkId="";
		String SeqCode="";
		String CardId="";
		String CardType="";
		String PatientName="";
		String TelePhoneNo="";
		String yyid="";
		try{
			Document document=DocumentHelper.parseText(request);
			Element Request = document.getRootElement();
			Element Parameter = Request.element("Parameter");
			HospitalId=Parameter.element("HospitalId").getTextTrim();
			Source=Parameter.element("Source").getTextTrim();
			WorkId=Parameter.element("WorkId").getTextTrim();
			CardId=Parameter.element("CardId").getTextTrim();
			CardType=Parameter.element("CardType").getTextTrim();
			PatientName=Parameter.element("PatientName").getTextTrim();
			TelePhoneNo=Parameter.element("TelePhoneNo").getTextTrim();
			SeqCode=Parameter.element("SeqCode").getTextTrim();
			yyid=Parameter.element("yyid").getTextTrim();
		}catch(DocumentException e1){
			System.out.println("提交预约挂号信息传入的参数不对");
			return "<Response><ResultCode>1</ResultCode><ErrorMsg>传入参数错误</ErrorMsg></Response>";
		}
		try{
			if(dao.doCount("MS_GHMX","HYXH="+yyid+" and THBZ=0",null)==0){
				//处理未支付但锁定了号源的情况
				if(dao.doCount("MS_YYGHHY"," HYXH="+yyid+" and HYZT='2' ",null)>0){
					StringBuffer uphysql=new StringBuffer();
					uphysql.append("update MS_YYGHHY set HYZT='0',BRID=null,SFZH=null,SOURCE=null,")
					.append("CARDID=null,CARDTYPE=null,LOGINUSERCARDTYPE=null,LOGINUSERIDCARD=null,")
					.append("PATIENTNAME=null,PATIENTGENDER=null,")
					.append("PATIENTBIRTHDAY=null,ZFJE=0.00,GHXH=null,")
					.append("TELEPHONENO=null,ADDRESS=null,SDXH=null where HYXH="+yyid);
					dao.doSqlUpdate(uphysql.toString(),null);
					StringBuffer kssql=new StringBuffer();
					kssql.append("select HYKS as HYKS from MS_YYGHHY where HYXH="+yyid);
					String ksdm=dao.doSqlLoad(kssql.toString(),null).get("HYKS")+"";
					StringBuffer s=new StringBuffer();
					DateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					s.append("<Response><Result><Record><yyid>"+yyid+"</yyid><HospitalId>"+HospitalId+"</HospitalId>")
					.append("<Source>"+Source+"</Source><WorkId>"+WorkId+"</WorkId><OrderTime>"+df.format(new Date())+"</OrderTime>")
					.append("<CardId>"+CardId+"</CardId><CardType>"+CardType+"</CardType><PatientName>"+PatientName+"</PatientName>")
					.append("<TelePhoneNo>"+TelePhoneNo+"</TelePhoneNo><SeqCode>"+SeqCode+"</SeqCode><state>5</state>")
					.append("<deptid>"+"gh"+ksdm+"</deptid><creat__time>"+df.format(new Date())+"</creat__time>")
					.append("</Record></Result><ResultCode>0</ResultCode><ErrorMsg>处理成功</ErrorMsg></Response>");
					System.out.println("取消预约返回信息："+s.toString());
					return s.toString();
				}else{
					return "<Response><ResultCode>1</ResultCode><ErrorMsg>无预约序号为"+yyid+"的未退号的挂号信息</ErrorMsg></Response>";
				}
			}else{
				StringBuffer sql=new StringBuffer();
				sql.append("select SBXH as SBXH,to_char(GHSJ,'yyyy-mm-dd') as GHSJ from MS_GHMX where HYXH="+yyid+" and THBZ=0 and ROWNUM=1");
				Map<String,Object> sbxhmap=dao.doSqlLoad(sql.toString(),null);
				Calendar cal=Calendar.getInstance();
				cal.setTime(new Date());
				DateFormat dfd=new SimpleDateFormat("yyyy-MM-dd");
				if(dfd.format(cal.getTime()).equals(sbxhmap.get("GHSJ")+"")){
					return "<Response><ResultCode>1</ResultCode><ErrorMsg>卫计局业务规定当天的号不能退</ErrorMsg></Response>";
				}
				String sbxh=sbxhmap.get("SBXH")+"";
				dao.doSqlUpdate("update MS_GHMX set THBZ=1 where SBXH="+sbxh,null);
				StringBuffer ghxxsql=new StringBuffer();
				ghxxsql.append("select a.SBXH as SBXH,a.JGID as JGID,a.CZGH as CZGH,")
				.append("a.MZLB as MZLB,a.KSDM as KSDM from MS_GHMX a where SBXH="+sbxh);
				Map<String,Object> ghxxmap=dao.doSqlLoad(ghxxsql.toString(),null);
				ghxxmap.put("THRQ",new Date());
				Context ctx = ContextUtils.getContext();
				ctx.put(Context.DB_SESSION,sen);
				if (!ctx.containsKey(Context.USER_ROLE_TOKEN)) {
					User user;
					try{
						user=AccountCenter.getUser("0310581X");
						Object[] urs = user.getUserRoleTokens().toArray();
						ctx.put(Context.USER_ROLE_TOKEN, urs[0]);
					}catch(ControllerException e){
						e.printStackTrace();
					}
				}
				try{
					dao.doInsert(BSPHISEntryNames.MS_THMX,ghxxmap, false);
				}catch(ValidateException e){
					e.printStackTrace();
				}
				StringBuffer uphysql=new StringBuffer();
				uphysql.append("update MS_YYGHHY set HYZT='0',BRID=null,SFZH=null,SOURCE=null,")
				.append("CARDID=null,CARDTYPE=null,LOGINUSERCARDTYPE=null,LOGINUSERIDCARD=null,")
				.append("PATIENTNAME=null,PATIENTGENDER=null,")
				.append("PATIENTBIRTHDAY=null,ZFJE=0.00,GHXH=null,")
				.append("TELEPHONENO=null,ADDRESS=null,SDXH=null where HYXH="+yyid);
				dao.doSqlUpdate(uphysql.toString(),null);
				StringBuffer s=new StringBuffer();
				DateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				s.append("<Response><Result><Record><yyid>"+yyid+"</yyid><HospitalId>"+HospitalId+"</HospitalId>")
				.append("<Source>"+Source+"</Source><WorkId>"+WorkId+"</WorkId><OrderTime>"+df.format(new Date())+"</OrderTime>")
				.append("<CardId>"+CardId+"</CardId><CardType>"+CardType+"</CardType><PatientName>"+PatientName+"</PatientName>")
				.append("<TelePhoneNo>"+TelePhoneNo+"</TelePhoneNo><SeqCode>"+SeqCode+"</SeqCode><state>5</state>")
				.append("<deptid>"+"gh"+ghxxmap.get("KSDM")+"</deptid><creat__time>"+df.format(new Date())+"</creat__time>")
				.append("</Record></Result><ResultCode>0</ResultCode><ErrorMsg>处理成功</ErrorMsg></Response>");
				System.out.println("取消预约返回信息："+s.toString());
				return s.toString();
			}
		}catch(PersistentDataOperationException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	public String GetDoctorInformation(String request,Session sen) {
		BaseDAO dao = getDAO(sen);
		String HospitalId="";
		String DeptId="";
		String WorkDate="";
		Calendar cal=Calendar.getInstance();
		cal.setTime(new Date());
		DateFormat df=new SimpleDateFormat("yyyy-MM-dd");
		String ds=df.format(cal.getTime());
		try{
			Document document=DocumentHelper.parseText(request);
			Element Request = document.getRootElement();
			Element Parameter = Request.element("Parameter");
			HospitalId=Parameter.element("HospitalId").getTextTrim();
			DeptId=Parameter.element("DeptId").getTextTrim();
			WorkDate=Parameter.element("WorkDate").getTextTrim();
		}catch(DocumentException e1){
			e1.printStackTrace();
		}
		StringBuffer sql=new StringBuffer();
		sql.append("select nvl(a.HYYS,'-') as HYYS ,count(1) as HYSL from MS_YYGHHY a")
		.append(" where 'gh'||a.HYKS='"+DeptId+"' and a.HYJG='"+HospitalId+"'");
		if(WorkDate.length()==0){
			sql.append(" and to_char(a.HYSJ,'yyyy-mm-dd')>='"+df.format(new Date())+"'");
		}else{
			sql.append(" and to_char(a.HYSJ,'yyyy-mm-dd')='"+WorkDate+"'");
		}
		sql.append(" group by nvl(a.HYYS,'-')");
		try{
			System.out.println("sql:"+sql.toString());
			List<Map<String,Object>> li=dao.doSqlQuery(sql.toString(),null);
			if(li.size()>0){
				StringBuffer s=new StringBuffer();
				for(Map<String,Object> o:li){
					Map<String,Object>ksmap=dao.doSqlLoad("select a.KSMC as KSMC from MS_GHKS a" +
							" where a.JGID='"+HospitalId+"' and a.KSDM='"+DeptId.replaceAll("gh","")+"'",null);
					String DoctorId=o.get("HYYS")+"";
					String TelephoneNo="";
					String KeyWord="";
					String DoctorName="";
					String DoctorGender="";
					String Information="";
					String DoctorRank="";
					String WorkRankID="";
					String WorkRank="";
					if(DoctorId.equals("-")){
						DoctorId="";
					}else{
						StringBuffer yssql=new StringBuffer();
						yssql.append("select a.PERSONNAME as PERSONNAME,a.GENDER as GENDER,")
						.append("a.MAJORQUALIFY as MAJORQUALIFY,a.ISEXPERT as ISEXPERT,")
						.append("a.REMARK as REMARK,a.PYCODE as PYCODE,a.MOBILE as MOBILE")
						.append(" from SYS_PERSONNEL a where a.PERSONID='"+DoctorId+"'");
						Map<String,Object> ysmap=dao.doSqlLoad(yssql.toString(),null);
						DoctorName=ysmap.get("PERSONNAME")+"";
						DoctorGender=(ysmap.get("GENDER")+"").equals("1")?"男":"女";
						WorkRankID="";
						WorkRank="";
						if((ysmap.get("ISEXPERT")+"").equals("1")){
							WorkRankID="3";
							WorkRank="专家门诊";
						}else{
							WorkRankID="1";
							WorkRank="普通门诊";
						}
						DoctorRank="";
						String MAJORQUALIFY=ysmap.get("MAJORQUALIFY")==null?"":ysmap.get("MAJORQUALIFY")+"";
						if(MAJORQUALIFY.equals("231")){
							DoctorRank="主任医师";
						}else if(MAJORQUALIFY.equals("232")){
							DoctorRank="副主任医师";
						}else if(MAJORQUALIFY.equals("233")){
							DoctorRank="主治医师";
						}else if(MAJORQUALIFY.equals("234")){
							DoctorRank="医师";
						}else if(MAJORQUALIFY.equals("235")){
							DoctorRank="医士";
						}else if(MAJORQUALIFY.equals("236")){
							DoctorRank="主管医师";
						}
						TelephoneNo=ysmap.get("MOBILE")==null?"":ysmap.get("MOBILE")+"";
						KeyWord=ysmap.get("PYCODE")==null?"":ysmap.get("PYCODE")+"";
						Information=ysmap.get("REMARK")==null?"":ysmap.get("REMARK")+"";
						
					}
					s.append("<Record><HospitalId>"+HospitalId+"</HospitalId>")
					.append("<DeptName>"+ksmap.get("KSMC")+"</DeptName>")
					.append("<DeptId>"+DeptId+"</DeptId><DoctorId>"+DoctorId+"</DoctorId>")
					.append("<DoctorName>"+DoctorName+"</DoctorName>")
					.append("<DoctorGender>"+DoctorGender+"</DoctorGender>")
					.append("<DoctorRank>"+DoctorRank+"</DoctorRank>")
					.append("<TelephoneNo>"+TelephoneNo+"</TelephoneNo><WorkRankID>"+WorkRankID+"</WorkRankID>")
					.append("<WorkRank>"+WorkRank+"</WorkRank><Information>"+Information+"</Information>")
					.append("<KeyWord>"+KeyWord+"</KeyWord><PictureUrl></PictureUrl>")
					.append("<Flag>1</Flag><tag1>"+o.get("HYSL")+"</tag1></Record>");
				}
				return "<Response><ResultCode>0</ResultCode><ErrorMsg>成功</ErrorMsg><Result>"+
						s.toString()+"</Result></Response>";
			}else{
				return "<Response><ResultCode>1</ResultCode><ErrorMsg>科室无排班</ErrorMsg></Response>";
			}
		}catch(PersistentDataOperationException e){
			e.printStackTrace();
		}
		return"";
	}
	public String ChangeRegStatus(String request,Session sen) {
		BaseDAO dao = getDAO(sen);
		String RegRecordId="";//预约ID
		String tradeNo="";//订单号
		String payType="";//支付方式
		String tradeDate="";//订单日期
		String selfPay="";//自付金额
		try{
			Document document=DocumentHelper.parseText(request);
			Element Request = document.getRootElement();
			Element Parameter = Request.element("Parameter");
			RegRecordId=Parameter.element("RegRecordId").getTextTrim();
			tradeNo=Parameter.element("tradeNo").getTextTrim();
			payType=Parameter.element("payType").getTextTrim();
			tradeDate=Parameter.element("tradeDate").getTextTrim();
			selfPay=Parameter.element("selfPay").getTextTrim();
		}catch(DocumentException e1){
			e1.printStackTrace();
		}
		if(RegRecordId.length()==0){
			return "<Response><ResultCode>1</ResultCode><ErrorMsg>传入参数不正确</ErrorMsg></Response>";
		}
		//payType转换
		String newpayType="0";
		if(payType.equals("02")){//支付宝
			newpayType="1";
		}else if(payType.equals("03")){//微信
			newpayType = "2";
		}else if(payType.equals("04")){//银联
			newpayType = "3";
		}
		StringBuffer findsql=new StringBuffer();
		findsql.append("select count(1) as SFYY from MS_GHMX a where a.THBZ=0")
		.append(" and a.HYXH="+RegRecordId);
		try{
			if(Integer.parseInt(dao.doSqlLoad(findsql.toString(),null).get("SFYY")+"")>0){
				return "<Response><ResultCode>1</ResultCode><ErrorMsg>该号源已被预约</ErrorMsg></Response>";
			}
		}catch(NumberFormatException e1){
			e1.printStackTrace();
		}catch(PersistentDataOperationException e1){
			e1.printStackTrace();
		};
		StringBuffer sql=new StringBuffer();
		sql.append("select a.BRID as BRID,nvl(a.GHF,0) as GHF,nvl(a.ZLF,0) as ZLF,")
		.append("a.PATIENTNAME as PATIENTNAME,a.HYKS as HYKS,a.HYYS as HYYS,")
		.append("b.MZHM as MZHM,to_char(a.HYSJ,'yyyy-MM-dd HH:mi:ss') as HYSJ,")
		.append("a.PATIENTGENDER as PATIENTGENDER,a.SFZH as SFZH,a.PATIENTBIRTHDAY as PATIENTBIRTHDAY,")
		.append("a.CARDTYPE as CARDTYPE,")
		.append("a.HYJG as HYJG,(to_char(a.HYSJ,'HH24')) as SDHH,case when to_char(hysj,'mi')>='30'")
		.append(" then '30' else '00' end  as SDMI,a.CARDID as CARDID")
		.append(" from MS_YYGHHY a,MS_BRDA b where a.BRID=b.BRID and a.HYXH="+RegRecordId);
		try{
			Map<String,Object> yyxx=dao.doSqlLoad(sql.toString(),null);
			if(yyxx!=null && yyxx.size()>0){
				Context ctx = ContextUtils.getContext();
				ctx.put(Context.DB_SESSION,sen);
				if (!ctx.containsKey(Context.USER_ROLE_TOKEN)) {
					User user;
					try{
						user=AccountCenter.getUser("0310581X");
						Object[] urs = user.getUserRoleTokens().toArray();
						ctx.put(Context.USER_ROLE_TOKEN, urs[0]);
					}catch(ControllerException e){
						e.printStackTrace();
					}
				}
				DictionaryController d=DictionaryController.instance();
				String ygbm="";
				String ip="";
				String computername=""; 
				try{
					Dictionary yg=d.get("phis.dictionary.YYGHYG");
					ygbm=yg.getItem(yyxx.get("HYJG")+"").getText();
					Dictionary dicApp=d.get("phis.dictionary.ZjzfSbInfo");
					if(dicApp!=null){
						 ip=dicApp.getItem(yyxx.get("HYJG")+"").getProperty("ip").toString();
						 computername=dicApp.getItem(yyxx.get("HYJG")+"").getProperty("computername").toString();
					}
				}catch(ControllerException e3){
					e3.printStackTrace();
				}
				String wbUrl="";
				try{
					Dictionary yg=d.get("phis.dictionary.fsyypzxx");
					wbUrl=yg.getItem("wbUrl").getText();
				}catch(ControllerException e3){
					e3.printStackTrace();
				}
				Map<String, Object> ghxx=new HashMap<String,Object>();
				ghxx.put("JGID",yyxx.get("HYJG"));
				ghxx.put("BRID",Long.parseLong(yyxx.get("BRID")+""));
				String hysj=yyxx.get("HYSJ")+"";
				try{
					ghxx.put("GHSJ",df.parse(hysj));
				}catch(ParseException e){
					e.printStackTrace();
				}
				ghxx.put("GHLB","1");
				Long ksdm=Long.parseLong(yyxx.get("HYKS")+"");
				ghxx.put("KSDM",ksdm);
				String doctors=yyxx.get("HYYS")==null?"":yyxx.get("HYYS")+"";
				ghxx.put("YSDM",doctors);
				ghxx.put("JZXH",0);
				ghxx.put("GHCS",1);
				double ghf=Double.parseDouble(yyxx.get("GHF")+"");
				double zlf=Double.parseDouble(yyxx.get("ZLF")+"");
				ghxx.put("GHJE",ghf);
				ghxx.put("ZLJE",zlf);
				ghxx.put("ZJFY",0d);
				ghxx.put("BLJE",0d);
				double xjje=Double.parseDouble(selfPay);
				ghxx.put("XJJE",xjje);
				ghxx.put("ZPJE",0d);
				ghxx.put("ZHJE",0d);
				ghxx.put("HBWC",0d);
				ghxx.put("QTYS",0d);
				ghxx.put("JZJS",0);
				ghxx.put("THBZ",0);
				ghxx.put("CZGH",ygbm);
				ghxx.put("CZPB",1);
				double dd=Math.random();
				ghxx.put("JZHM","10"+(dd+"").substring(2,12));
				ghxx.put("MZLB",dao.doSqlLoad("select MZLB as MZLB from MS_MZLB a " +
							" where a.jgid='"+yyxx.get("HYJG")+"'",null).get("MZLB"));
				ghxx.put("YYBZ",0);
				ghxx.put("YSPB",0);
				ghxx.put("SFFS",0);
				ghxx.put("JZZT",0);
				ghxx.put("JKZSB",0);
				ghxx.put("HYXH",Long.parseLong(RegRecordId));
				Date czsj=new Date();
				ghxx.put("CZSJ",czsj);
				ghxx.put("YZJM",0d);
				ghxx.put("YZBZ","0");
				ghxx.put("ZJYYBZ","0");
				ghxx.put("BRXZ",1000L);
				ghxx.put("PAYTYPE",newpayType);
				try{
					Map<String,Object>re=dao.doSave("create",BSPHISEntryNames.MS_GHMX,ghxx,false);
					StringBuffer upsql=new StringBuffer();
					upsql.append("update MS_YYGHHY set HYZT='4',ZFJE="+xjje+",GHXH="+re.get("SBXH"))
					.append(",DDH='"+tradeNo+"',DDSJ='"+tradeDate+"' where HYXH="+RegRecordId);
					dao.doSqlUpdate(upsql.toString(),null);
					StringBuffer xxsql=new StringBuffer();
					xxsql.append("select a.KSMC as KSMC,a.DDXX as DDXX, b.ORGANIZNAME as JGMC")
					.append(" from MS_GHKS a,SYS_ORGANIZATION b where a.JGID=b.ORGANIZCODE")
					.append(" and a.KSDM="+yyxx.get("HYKS"));
					Map<String,Object>xx=dao.doSqlLoad(xxsql.toString(),null);
					StringBuffer s=new StringBuffer();
					String StartTime="";
					String SDHH=yyxx.get("SDHH")+"";
					String SDMI=yyxx.get("SDMI")+"";
					StartTime=SDHH+":"+SDMI;
					String EndTime="";
					if(SDMI.equals("00")){
						EndTime=SDHH+":30";
					}else{
						int h=Integer.parseInt(SDHH)+1;
						if((h+"").length()==1){
							EndTime="0"+h+":00";
						}else{
							EndTime=h+":00";
						}
					}
					Calendar cal=Calendar.getInstance();
					String xq="";
					try{
						cal.setTime(df.parse(yyxx.get("HYSJ")+""));
						int weekday=cal.get(Calendar.DAY_OF_WEEK);
						if(weekday==1){
							xq="日";
						}else if(weekday==2){
							xq="一";
						}else if(weekday==3){
							xq="二";
						}else if(weekday==4){
							xq="三";
						}else if(weekday==5){
							xq="四";
						}else if(weekday==6){
							xq="五";
						}else if(weekday==7){
							xq="六";
						}
					}catch(ParseException e){
						e.printStackTrace();
					}
					DateFormat dfd=new SimpleDateFormat("yyyy-MM-dd");
					
					String yysj=dfd.format(cal.getTime());
					String MedPlace=xx.get("DDXX")==null?"":xx.get("DDXX")+"";
					s.append("<Response><ResultCode>0</ResultCode><ErrorMsg>操作成功</ErrorMsg>")
					.append("<Result><Record><RegMessage>挂号成功</RegMessage>")
					.append("<DeptName>"+xx.get("KSMC")+"</DeptName><HosName>"+xx.get("JGMC")+"</HosName>")
					.append("<Fee>"+xjje+"</Fee><MedDate>"+yysj+xq+StartTime+EndTime+"</MedDate>")
					.append("<Patient>"+yyxx.get("PATIENTNAME")+"</Patient><RegCard>"+yyxx.get("CARDID")+"</RegCard>")
					.append("<MedPlace>"+MedPlace+"</MedPlace><OrderNo>0</OrderNo>");
					String url=FsyyModel.class.getResource("").getPath();
					url=url.replace("/WEB-INF/classes/phis/application/reg/ws/","")+"/ewm/";
					String mzhm=yyxx.get("MZHM")+"";
					buildEwm.createBarcode(mzhm,new File(url+mzhm+".png"),mzhm);
					String ds=df.format(new Date());
					s.append("<BarCode>"+wbUrl+"ewm/"+mzhm+".png</BarCode>")
					.append("<MedNo>"+mzhm+"</MedNo></Record>")
					.append("<payRecord><payService>2</payService>")
					.append("<ip>"+ip+"</ip><organizationCode>"+yyxx.get("HYJG")+"</organizationCode>")
					.append("<computerName>"+computername+"</computerName><hospNo>"+tradeNo+"</hospNo>")
					.append("<paymoney>"+selfPay+"</paymoney><patientType>1000</patientType>")
					.append("<patientId>"+yyxx.get("BRID")+"</patientId><name>"+yyxx.get("PATIENTNAME")+"</name>")
					.append("<sex>"+yyxx.get("PATIENTGENDER")+"</sex><idcard>"+yyxx.get("SFZH")+"</idcard>")
					.append("<birthday/><cardType>"+yyxx.get("CARDTYPE")+"</cardType>")
					.append("<cardNo>"+yyxx.get("CARDID")+"</cardNo><voucherNO>"+ghxx.get("JZHM")+"</voucherNO>")
					.append("<payType>"+newpayType+"</payType><paySource>3</paySource>")
					.append("<collectFeesCode>"+ygbm+"</collectFeesCode><collectFeesName>默认支付员工</collectFeesName>")
					.append("<payTime>"+ds+"</payTime><bank></bank></payRecord></Result></Response>");
					System.out.println("支付返回"+s.toString());
					return s.toString();
				}catch(ValidateException e){
					// TODO Auto-generated catch block
					e.printStackTrace();
					return "<Response><ResultCode>1</ResultCode><ErrorMsg>保存挂号信息失败</ErrorMsg></Response>";
				}
			}else{
				return "<Response><ResultCode>1</ResultCode><ErrorMsg>未查询到预约信息</ErrorMsg></Response>";
			}
		}catch(PersistentDataOperationException e){
			e.printStackTrace();
		}
		return "<Response><ResultCode>1</ResultCode><ErrorMsg>不知道怎么就结束了</ErrorMsg></Response>";
	}
}
