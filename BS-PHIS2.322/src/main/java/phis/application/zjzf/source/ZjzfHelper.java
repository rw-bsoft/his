package phis.application.zjzf.source;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.slf4j.LoggerFactory;

import ctd.account.AccountCenter;
import ctd.account.UserRoleToken;
import ctd.account.user.User;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.DictionaryItem;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;
import ctd.validator.ValidateException;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.BSHISUtil;
import phis.source.utils.BSPHISUtil;
import phis.application.cfg.source.ConfigLogisticsInventoryControlModel;
import phis.application.mds.source.MedicineUtils;
import phis.application.pay.source.MobilePaymentModel;
import phis.application.pha.source.PharmacyDispensingModel;
import phis.application.zjzf.source.ZjzfUtil;

/*import ctd.dictionary.support.TableDictionary;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.DictionaryItem;
import ctd.dictionary.support.TableDictionary;*/

public class ZjzfHelper {
/*	protected Logger logger = LoggerFactory
			.getLogger(ZjzfHelper.class);*/
	
	private String getXmlInfo(String paramsXml) {
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version='1.0' encoding='UTF-8'?>");
//		sb.append("<body>");
		sb.append(paramsXml); 
//		sb.append("</body>");
		return sb.toString();
	}	

	/*****************接口1：获取未支付列表  begin***************************************/	
	public String GetDiagnosisRecordXml(String request){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println("=========="+df.format(new Date())+"【开始】 调用HIS接口[APP]：获取未支付列表==========");
		String res = "";
		try {
			//List<DictionaryItem> drugWay = ((TableDictionary) DictionaryController.instance().get("phis.dictionary.drugWay")).initAllItems();
			Document doc = DocumentHelper.parseText(getXmlInfo(request));
			Element root = doc.getRootElement();
			//Element getUnpayedListRequest = root.element("certificates"); // 获取body节点
			String orgId =  root.elementText("orgId");//机构代码
			String beginDate =  root.elementText("beginDate");//起始日期
			String endDate =  root.elementText("endDate");//结束日期
			String certificateType = "";//证件类型
			String certificateNo = "";//证件号
			String nationality = "";//机构归属国家
			Iterator certificates = root.elementIterator("certificates");
				while (certificates.hasNext()) {
/*					SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//可以方便地修改日期格式		
					Date now2 = new Date();
					String strDate2 = dateFormat2.format(now2);  */
	                Element certificateEle = (Element) certificates.next();
	                Iterator iter = certificateEle.elementIterator("certificate");
	                while (iter.hasNext()) {
	                	Element recordEle = (Element) iter.next();
	                	if(recordEle.elementText("certificateType").equals("01")){//身份证
		                    certificateType = recordEle.elementText("certificateType");//证件类型
		                    certificateNo = recordEle.elementText("certificateNo");//证件号
		                    nationality = recordEle.elementText("nationality");//机构归属国家
	                	}
	                }
				}
				Iterator cards = root.elementIterator("cards");
				while (cards.hasNext()) {
/*					SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//可以方便地修改日期格式		
					Date now2 = new Date(); 
					String strDate2 = dateFormat2.format(now2); */
	                Element card = (Element) cards.next();
	                Iterator iter = card.elementIterator("certificate");
	                while (iter.hasNext()) {
	                	Element recordEle = (Element) iter.next();
	                	if(recordEle.elementText("cardType").equals("02")){//就诊卡
		                    String cardType = recordEle.elementTextTrim("cardType");//卡类型
		                    String cardNo = recordEle.elementTextTrim("cardNo");//卡号
		                    String domain = recordEle.elementTextTrim("domain");//发卡机构
	                	}
	                }
				}
				System.out.println("=====成功解析请求报文=====");
			//获取HIS待支付列表信息
			res = getUnpayedList(orgId,certificateNo);
			//return orgId+"==certificateType:"+certificateType+"==<getUnpayListResponse><resultCode>1</resultCode><resultMessage>成功</resultMessage><diagnosisRecords><diagnosisRecord><patientId>023829</patientId><diagnosisRecordId>1223</diagnosisRecordId><diagnosisDate>2016-07-10</diagnosisDate><deptId>0939-2020-1020-112</deptId><deptName>呼吸科</deptName><doctorId>9290-109-1991-122</doctorId><doctorName>赵三</doctorName>";
/*		} catch (ControllerException e) {
			e.printStackTrace();*/
		}catch (Exception e) {
			LoggerFactory
			.getLogger(ZjzfHelper.class).error("load failed.", e);
		}
		System.out.println("=========="+df.format(new Date())+"【结束】 调用HIS接口[APP]：获取未支付列表==========");
		System.out.println("");
		return res;
	}		
	
/*	public static void main(String[] args)
	{
		String request = "<getUnpayedListRequest><orgId>12981891-19399-298289-111</orgId><beginDate>2016-07-10</beginDate><endDate>2016-07-10</endDate><certificates><certificate><certificateType>01</certificateType><certificateNo>094889348039</certificateNo><nationality>01</nationality></certificate></certificates><cards><card><cardType>01</cardType><cardNo>A29239292</cardNo><domain>01</domain></card></cards></getUnpayedListRequest>";
		 String res = GetDiagnosisRecordXml(request);
		 System.out.println("请求成功："+res); 
		 System.out.println(System.currentTimeMillis());
	}*/
	
	private String getUnpayedList(String jgid, String sfzh)
	{
		System.out.println("=====开始执行获取待支付列表业务=====");
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("SFZH", sfzh);
			parameters.put("JGID", jgid);
			parameters.put("QYMZSF", 0);//启用门诊审方标志，0表示不启用，1表示启用
			long current=System.currentTimeMillis();//当前时间毫秒数
			long zero=current/(1000*3600*24)*(1000*3600*24)-TimeZone.getDefault().getRawOffset();//今天零点零分零秒的毫秒数
/*			parameters
					.put("KDRQ",
							new Date(
									((Calendar.getInstance().getTimeInMillis()) - (1 * 24 * 60 * 60 * 1000))));*/
			parameters.put("KDRQ",new Date(zero));
			StringBuffer hql1 = new StringBuffer(
					"SELECT 1 as XQ,a.CFLX as CFLX,case a.CFLX when 1 then '西药方' when 2 then '中药方' else '草药方' end as DJLX_text,a.KFRQ as KDRQ,a.KSDM as KDKS,a.YSDM as KDYS,a.CFSB as CFSB,SUM(b.HJJE) as HJJE,0 as LSBZ,a.JZXH as JZXH,case a.CFLX when 1 then 21 when 2 then 22 when 3 then 23 end as DJLY,case a.CFLX when 1 then '西药' when 2 then '成药' when 3 then '中草药' end as DJLY_TEXT,a.MZXH,c.KSMC,d.NAME,a.BRID FROM MS_CF01");
			hql1.append(" a,MS_CF02 b,GY_KSDM c,BASE_USER d WHERE b.ZFYP!=1 and a.CFSB = b.CFSB and a.KSDM = c.KSDM and a.YSDM=d.id AND a.JGID =:JGID and a.BRID in(select BRID from MS_BRDA where ZXBZ=0 and (upper(SFZH)=:SFZH or lower(SFZH)=:SFZH)) and ( a.FPHM is null or a.FPHM = '' ) and a.ZFPB = 0 and a.FYBZ = 0 and ( a.CFBZ is null or a.CFBZ <> 2 ) and ( a.KFRQ >=:KDRQ) and ( a.DJLY <> 7 OR a.DJLY IS NULL) and ");
			hql1.append("( :QYMZSF = 0 or not exists (select 1 from MS_CF02 c  where a.CFSB = c.CFSB AND a.JGID = :JGID and a.BRID in(select BRID from MS_BRDA where ZXBZ=0 and (upper(SFZH)=:SFZH or lower(SFZH)=:SFZH)) and a.KFRQ >= :KDRQ and ");
			hql1.append("  ( c.SFJG <> 1  or  c.SFJG is null )) ) ");

			StringBuffer hql2 = new StringBuffer(
					"SELECT e.XQ as XQ,e.CFLX as CFLX,e.DJLX_text as DJLX_text,e.KDRQ as KDRQ,e.KDKS as KDKS,e.KDYS as KDYS,e.CFSB as CFSB,SUM(e.HJJE) as HJJE,e.LSBZ as LSBZ,e.JZXH as JZXH,e.DJLY as DJLY,e.DJLY_TEXT as DJLY_TEXT,e.MZXH,e.KSMC,e.NAME,e.BRID FROM ( ");
			hql2.append("SELECT 1 as XQ,0 as CFLX,'检查单' as DJLX_text,a.KDRQ as KDRQ,a.KSDM as KDKS,a.YSDM as KDYS,a.YJXH as CFSB,b.HJJE as HJJE,0 as LSBZ,a.JZXH as JZXH,case a.DJLY when 1 then 13 when 6 then 51 when 8 then 31 when 9 then 41 end as DJLY,case a.DJLY when 1 then '诊查费' when 6 then '治疗' when 8 then '检验' when 9 then '检查' end as DJLY_TEXT,a.MZXH,c.KSMC,d.NAME,a.BRID FROM MS_YJ01");
			hql2.append(" a,MS_YJ02 b,GY_KSDM c,BASE_USER d WHERE a.YJXH=b.YJXH and a.KSDM = c.KSDM and a.YSDM=d.id  AND a.JGID =:JGID and a.BRID in(select BRID from MS_BRDA where ZXBZ=0 and (upper(SFZH)=:SFZH or lower(SFZH)=:SFZH)) and ( a.FPHM is null or a.FPHM = '' ) and a.ZFPB = 0 and a.ZXPB = 0 and ( a.CFBZ is null or a.CFBZ <> 2 ) and ( a.KDRQ >=:KDRQ)  and ( a.DJLY <> 7 OR a.DJLY IS NULL) and (a.FJGL IS NULL OR a.FJGL = 0) ");

			hql2.append(" union all SELECT 1 as XQ,0 as CFLX,'检查单' as DJLX_text,a.KDRQ as KDRQ,a.KSDM as KDKS,a.YSDM as KDYS,a.YJXH as CFSB,b.HJJE as HJJE,0 as LSBZ,a.JZXH as JZXH,case a.DJLY when 1 then 13 when 6 then 51 when 8 then 31 when 9 then 41 end as DJLY,case a.DJLY when 1 then '诊查费' when 6 then '治疗' when 8 then '检验' when 9 then '检查' end as DJLY_TEXT,a.MZXH,c.KSMC,d.NAME,a.BRID FROM ");
			hql2.append("MS_YJ01 a,MS_YJ02 b,GY_KSDM c,BASE_USER d WHERE a.YJXH=b.YJXH and a.KSDM = c.KSDM and a.YSDM=d.id AND a.JGID =:JGID and a.BRID in(select BRID from MS_BRDA where ZXBZ=0 and (upper(SFZH)=:SFZH or lower(SFZH)=:SFZH)) and ( a.FPHM is null or a.FPHM = '' ) and a.ZFPB = 0 and a.ZXPB = 0 and ( a.CFBZ is null or a.CFBZ <> 2 ) and ( a.KDRQ >=:KDRQ)  and ( a.DJLY <> 7 OR a.DJLY IS NULL) "
					+ "  and  (a.FJGL IS NOT NULL AND a.FJGL > 0 )  and "
					+ " (a.FJLB=3 or :QYMZSF = 0 or exists (SELECT 1 FROM MS_CF01 c,MS_CF02 d where c.CFSB = d.CFSB AND c.JGID =:JGID and c.BRID in(select BRID from MS_BRDA where ZXBZ=0 and (upper(SFZH)=:SFZH or lower(SFZH)=:SFZH))  and (d.SFJG = 1 and d.SFJG is not null ) and c.KFRQ >= :KDRQ and c.ZFPB = 0  and ( c.CFBZ is null or c.CFBZ <> 2 )  and ( c.DJLY <> 7 OR c.DJLY IS NULL)  and a.FJGL = d.YPZH ))) e ");
			hql1.append(" GROUP BY a.CFLX,a.KFRQ,a.KSDM,a.YSDM,a.CFSB,a.JZXH,a.DJLY,a.MZXH,c.KSMC,d.NAME,a.BRID ");
			hql2.append(" GROUP BY e.XQ,e.CFLX,e.DJLX_text,e.KDRQ,e.KDKS,e.KDYS,e.CFSB,e.LSBZ,e.JZXH,e.DJLY,e.DJLY_TEXT,e.MZXH,e.KSMC,e.NAME,e.BRID ");
			// System.out.println(hql1.toString());
/*			List<Map<String, Object>> listCF = dao.doSqlQuery(hql1.toString(),
					parameters);
			List<Map<String, Object>> listYJ = dao.doSqlQuery(hql2.toString(),
					parameters1);*/
			StringBuffer hql = new StringBuffer("select f.XQ as XQ,f.CFLX as CFLX,f.DJLX_text as DJLX_text,f.KDRQ as KDRQ,f.KDKS as KDKS,f.KDYS as KDYS,f.CFSB as CFSB,f.HJJE as HJJE,f.LSBZ as LSBZ,f.JZXH as JZXH,f.DJLY as DJLY,f.DJLY_TEXT as DJLY_TEXT,f.MZXH,f.KSMC,f.NAME,f.BRID from("+hql1.toString()+" union "+hql2.toString()+") f  order by f.MZXH,f.KDRQ");
			BaseDAO dao = new BaseDAO();
			System.out.println("=====开始执行sql查询=====");
			List<Map<String, Object>> listDj = dao.doSqlQuery(hql.toString(),
					parameters);
			System.out.println("=====结束sql查询：共查询到"+listDj.size()+"条信息=====");
			System.out.println("=====结束执行获取待支付列表业务=====");
			return genResponseXml_UnpayedList(listDj);
		} catch (PersistentDataOperationException e) {
			LoggerFactory
			.getLogger(ZjzfHelper.class).error("load failed.", e);
			System.out.println("=====执行获取待支付列表业务失败："+e.getMessage()+"=====");
			return "";
		}
	}
	
	private String genResponseXml_UnpayedList(List<Map<String, Object>> listData)
	{
		//<getUnpayListResponse><resultCode>1</resultCode><resultMessage>成功</resultMessage>
		//<diagnosisRecords><diagnosisRecord><patientId>023829</patientId><diagnosisRecordId>1223</diagnosisRecordId><diagnosisDate>2016-07-10</diagnosisDate><deptId>0939-2020-1020-112</deptId><deptName>呼吸科</deptName><doctorId>9290-109-1991-122</doctorId><doctorName>赵三</doctorName>
		try {
			StringBuffer resXml = new StringBuffer("");
			if(listData!=null && listData.size()>0){
				System.out.println("=====开始构建返回报文=====");
				String mzxh = "";
				StringBuffer diagnosisRecordsXml =  new StringBuffer("<diagnosisRecord>");
				resXml.append("<getUnpayListResponse><resultCode>1</resultCode><resultMessage>成功</resultMessage><diagnosisRecords>");
				for(int i=0;i<listData.size();i++){
					if(i==0){
						diagnosisRecordsXml.append("<patientId>"+(listData.get(i).get("BRID")==null?"":listData.get(i).get("BRID").toString())+"</patientId>");
						diagnosisRecordsXml.append("<diagnosisRecordId>"+(listData.get(i).get("MZXH")==null?"":listData.get(i).get("MZXH").toString())+"</diagnosisRecordId>");
						diagnosisRecordsXml.append("<diagnosisDate>"+(listData.get(i).get("KDRQ")==null?"":listData.get(i).get("KDRQ").toString())+"</diagnosisDate>");
						diagnosisRecordsXml.append("<deptId>"+(listData.get(i).get("KDKS")==null?"":listData.get(i).get("KDKS").toString())+"</deptId>");
						diagnosisRecordsXml.append("<deptName>"+(listData.get(i).get("KSMC")==null?"":listData.get(i).get("KSMC").toString())+"</deptName>");
						diagnosisRecordsXml.append("<doctorId>"+(listData.get(i).get("KSYS")==null?"":listData.get(i).get("KSYS").toString())+"</doctorId>");
						diagnosisRecordsXml.append("<doctorName>"+(listData.get(i).get("NAME")==null?"":listData.get(i).get("NAME").toString())+"</doctorName>");
						diagnosisRecordsXml.append("<feeRecords>");
					}
					if(i>0 && i<listData.size()-1 && !mzxh.equals((listData.get(i).get("MZXH")==null?"":listData.get(i).get("MZXH").toString()))){
						diagnosisRecordsXml.append("</feeRecords></diagnosisRecord>");
						resXml.append(diagnosisRecordsXml.toString());
						diagnosisRecordsXml.setLength(0);
						diagnosisRecordsXml.append("<diagnosisRecords><diagnosisRecord>");
					}
					diagnosisRecordsXml.append("<feeRecord>");
					diagnosisRecordsXml.append("<feeNo>"+(listData.get(i).get("CFSB")==null?"":listData.get(i).get("CFSB").toString())+"</feeNo>");
					diagnosisRecordsXml.append("<feeTypeCode>"+(listData.get(i).get("DJLY")==null?"":listData.get(i).get("DJLY").toString())+"</feeTypeCode>");
					diagnosisRecordsXml.append("<feeType>"+(listData.get(i).get("DJLY_TEXT")==null?"":listData.get(i).get("DJLY_TEXT").toString())+"</feeType>");
					diagnosisRecordsXml.append("<feeDesc></feeDesc>");
					diagnosisRecordsXml.append("<feeDate>"+(listData.get(i).get("KDRQ")==null?"":listData.get(i).get("KDRQ").toString()+"</feeDate>"));
					diagnosisRecordsXml.append("<totalFee>"+(listData.get(i).get("HJJE")==null?"":listData.get(i).get("HJJE").toString()+"</totalFee>"));
					diagnosisRecordsXml.append("<required>0</required>");
					diagnosisRecordsXml.append("</feeRecord>");
					if(i==listData.size()-1){
						diagnosisRecordsXml.append("</feeRecords></diagnosisRecord>");
						resXml.append(diagnosisRecordsXml.toString());
					}
				}
				resXml.append("</diagnosisRecords></getUnpayListResponse>");
				System.out.println("=====结束构建返回报文=====");
			}
			return resXml.toString();
		} catch (Exception e) {
			LoggerFactory
			.getLogger(ZjzfHelper.class).error("load failed.", e);
			System.out.println("=====构建返回报文失败："+e.getMessage()+"=====");
			return "";
		}
	}	
	/*****************接口1：获取未支付列表  end***************************************/

	/*****************接口2：获取已支付列表  begin***************************************/
	//获取已支付列表xml
	public String GetPayedListXml(String request){		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println("=========="+df.format(new Date())+"【开始】 调用HIS接口[APP]：获取已支付列表==========");
		String res = "";
		try {
			//List<DictionaryItem> drugWay = ((TableDictionary) DictionaryController.instance().get("phis.dictionary.drugWay")).initAllItems();
			Document doc = DocumentHelper.parseText(getXmlInfo(request));
			Element root = doc.getRootElement();
			//Element getUnpayedListRequest = root.element("certificates"); // 获取body节点
			String orgId =  root.elementText("orgId");//机构代码
			String beginDate =  root.elementText("beginDate");//起始日期
			String endDate =  root.elementText("endDate");//结束日期
			String executeFlag =  root.elementText("executeFlag");//执行标志 0本参数无效 1已执行 2未执行	3 已退费 4 已作废
			String certificateType = "";//证件类型
			String certificateNo = "";//证件号
			String nationality = "";//机构归属国家
			Iterator certificates = root.elementIterator("certificates");
				while (certificates.hasNext()) {
/*					SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//可以方便地修改日期格式		
					Date now2 = new Date();
					String strDate2 = dateFormat2.format(now2);  */
	                Element certificateEle = (Element) certificates.next();
	                Iterator iter = certificateEle.elementIterator("certificate");
	                while (iter.hasNext()) {
	                	Element recordEle = (Element) iter.next();
	                	if(recordEle.elementText("certificateType").equals("01")){//身份证
		                    certificateType = recordEle.elementText("certificateType");//证件类型
		                    certificateNo = recordEle.elementText("certificateNo");//证件号
		                    nationality = recordEle.elementText("nationality");//机构归属国家
	                	}
	                }
				}
				Iterator cards = root.elementIterator("cards");
				while (cards.hasNext()) {
/*					SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//可以方便地修改日期格式		
					Date now2 = new Date(); 
					String strDate2 = dateFormat2.format(now2); */
	                Element card = (Element) cards.next();
	                Iterator iter = card.elementIterator("certificate");
	                while (iter.hasNext()) {
	                	Element recordEle = (Element) iter.next();
	                	if(recordEle.elementText("cardType").equals("02")){//就诊卡
		                    String cardType = recordEle.elementTextTrim("cardType");//卡类型
		                    String cardNo = recordEle.elementTextTrim("cardNo");//卡号
		                    String domain = recordEle.elementTextTrim("domain");//发卡机构
	                	}
	                }
				}
				System.out.println("=====成功解析请求报文=====");
			//获取HIS待支付列表信息
			res = getPayedList(beginDate,endDate,orgId,certificateNo,executeFlag);
			//return orgId+"==certificateType:"+certificateType+"==<getUnpayListResponse><resultCode>1</resultCode><resultMessage>成功</resultMessage><diagnosisRecords><diagnosisRecord><patientId>023829</patientId><diagnosisRecordId>1223</diagnosisRecordId><diagnosisDate>2016-07-10</diagnosisDate><deptId>0939-2020-1020-112</deptId><deptName>呼吸科</deptName><doctorId>9290-109-1991-122</doctorId><doctorName>赵三</doctorName>";
/*		} catch (ControllerException e) {
			e.printStackTrace();*/
		}catch (Exception e) {
			LoggerFactory
			.getLogger(ZjzfHelper.class).error("load failed.", e);
		}
		System.out.println("=========="+df.format(new Date())+"【结束】 调用HIS接口[APP]：获取已支付列表==========");
		System.out.println("");
		return res;
	}
	
	//获取已支付列表
	private String getPayedList(String beginDate,String endDate,String jgid, String sfzh,String executeFlag)
	{
		System.out.println("=====开始执行获取已支付列表业务=====");
		try {
			String sql1 = " and a.ZFPB = 0 and a.FYBZ=0";
			String sql2 = " and a.ZFPB = 0 and a.ZXPB = 0";
			String sql3 = " and c.ZFPB = 0";
			switch(Integer.parseInt(executeFlag)){
				case 1://已执行
					sql1 = " and a.ZFPB = 0 and a.FYBZ=1";
					sql2 = " and a.ZFPB = 0 and (a.ZXPB = 1 or a.ZXKS>0)";
				break;
				case 2://未执行
				break;
				case 3://已退费
					sql1 = " and a.ZFPB = 1 and e.THPB=1 and a.FYBZ=1";
					sql2 = " and a.ZFPB = 1 and e.THPB=1 and (a.ZXPB = 1 or a.ZXKS>0)";
					sql3 = " and c.ZFPB = 1";
				case 4://已作废
					sql1 = " and a.ZFPB = 1 and e.THPB=0 and a.FYBZ=1";
					sql2 = " and a.ZFPB = 1 and a.THPB=0 and (a.ZXPB = 1 or a.ZXKS>0)";
					sql3 = " and c.ZFPB = 1";
				break;
				default :
				break;
			}
			
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("SFZH", sfzh);
			parameters.put("JGID", jgid);
			parameters.put("QYMZSF", 0);//启用门诊审方标志，0表示不启用，1表示启用
			long current=System.currentTimeMillis();//当前时间毫秒数
			long zero=current/(1000*3600*24)*(1000*3600*24)-TimeZone.getDefault().getRawOffset()-24*60*60*1000;//今天零点零分零秒的毫秒数
/*			parameters
					.put("KDRQ",
							new Date(
									((Calendar.getInstance().getTimeInMillis()) - (1 * 24 * 60 * 60 * 1000))));*/
			parameters.put("BDATE",beginDate);
			parameters.put("EDATE",endDate);
			StringBuffer hql1 = new StringBuffer(
					"SELECT 1 as XQ,a.CFLX as CFLX,case a.CFLX when 1 then '西药方' when 2 then '中药方' else '草药方' end as DJLX_text,a.KFRQ as KDRQ,a.KSDM as KDKS,a.YSDM as KDYS,a.CFSB as CFSB,SUM(b.HJJE) as HJJE,0 as LSBZ,a.JZXH as JZXH,case a.CFLX when 1 then 21 when 2 then 22 when 3 then 23 end as DJLY,case a.CFLX when 1 then '西药' when 2 then '成药' when 3 then '中草药' end as DJLY_TEXT,a.MZXH,c.KSMC,d.NAME,a.BRID,a.BRXM,a.FPHM,e.CZGH,f.NAME as CZYName FROM MS_CF01");
			hql1.append(" a,MS_CF02 b,GY_KSDM c,BASE_USER d,MS_MZXX e,BASE_USER f WHERE b.ZFYP!=1 and a.CFSB = b.CFSB and a.KSDM = c.KSDM and a.YSDM=d.id and a.FPHM = e.FPHM AND a.JGID = e.JGID and e.CZGH = f.ID AND a.JGID =:JGID and a.BRID in(select BRID from MS_BRDA where ZXBZ=0 and (upper(SFZH)=:SFZH or lower(SFZH)=:SFZH)) and a.FPHM is  not null "+sql1+" and ( a.CFBZ is null or a.CFBZ <> 2 ) and (to_char(A.KFRQ,'yyyy-mm-dd')>=:BDATE and to_char(A.KFRQ,'yyyy-mm-dd')<=:EDATE) and ( a.DJLY <> 7 OR a.DJLY IS NULL) and ");
			hql1.append("( :QYMZSF = 0 or not exists (select 1 from MS_CF02 c  where a.CFSB = c.CFSB AND a.JGID = :JGID and a.BRID in(select BRID from MS_BRDA where ZXBZ=0 and (upper(SFZH)=:SFZH or lower(SFZH)=:SFZH)) and to_char(A.KFRQ,'yyyy-mm-dd')>=:BDATE and to_char(A.KFRQ,'yyyy-mm-dd')<=:EDATE and ");
			hql1.append("  ( c.SFJG <> 1  or  c.SFJG is null )) ) ");

			StringBuffer hql2 = new StringBuffer(
					"SELECT e.XQ as XQ,e.CFLX as CFLX,e.DJLX_text as DJLX_text,e.KDRQ as KDRQ,e.KDKS as KDKS,e.KDYS as KDYS,e.CFSB as CFSB,SUM(e.HJJE) as HJJE,e.LSBZ as LSBZ,e.JZXH as JZXH,e.DJLY as DJLY,e.DJLY_TEXT as DJLY_TEXT,e.MZXH,e.KSMC,e.NAME,e.BRID,e.BRXM,e.FPHM,e.CZGH,e.CZYName FROM ( ");
			hql2.append("SELECT 1 as XQ,0 as CFLX,'检查单' as DJLX_text,a.KDRQ as KDRQ,a.KSDM as KDKS,a.YSDM as KDYS,a.YJXH as CFSB,b.HJJE as HJJE,0 as LSBZ,a.JZXH as JZXH,case a.DJLY when 1 then 13 when 6 then 51 when 8 then 31 when 9 then 41 end as DJLY,case a.DJLY when 1 then '诊查费' when 6 then '治疗' when 8 then '检验' when 9 then '检查' end as DJLY_TEXT,a.MZXH,c.KSMC,d.NAME,a.BRID,a.BRXM,a.FPHM,e.CZGH,f.NAME as CZYName FROM MS_YJ01");
			hql2.append(" a,MS_YJ02 b,GY_KSDM c,BASE_USER d,MS_MZXX e,BASE_USER f WHERE a.YJXH=b.YJXH and a.KSDM = c.KSDM and a.YSDM=d.id and a.FPHM = e.FPHM AND a.JGID = e.JGID and e.CZGH = f.ID  AND a.JGID =:JGID and a.BRID in(select BRID from MS_BRDA where ZXBZ=0 and (upper(SFZH)=:SFZH or lower(SFZH)=:SFZH)) and  a.FPHM is  not null and a.ZFPB = 0 and a.ZXPB = 0 and ( a.CFBZ is null or a.CFBZ <> 2 ) and (to_char(A.KDRQ,'yyyy-mm-dd')>=:BDATE and to_char(A.KDRQ,'yyyy-mm-dd')<=:EDATE)  and ( a.DJLY <> 7 OR a.DJLY IS NULL) and (a.FJGL IS NULL OR a.FJGL = 0) ");

			hql2.append(" union all SELECT 1 as XQ,0 as CFLX,'检查单' as DJLX_text,a.KDRQ as KDRQ,a.KSDM as KDKS,a.YSDM as KDYS,a.YJXH as CFSB,b.HJJE as HJJE,0 as LSBZ,a.JZXH as JZXH,case a.DJLY when 1 then 13 when 6 then 51 when 8 then 31 when 9 then 41 end as DJLY,case a.DJLY when 1 then '诊查费' when 6 then '治疗' when 8 then '检验' when 9 then '检查' end as DJLY_TEXT,a.MZXH,c.KSMC,d.NAME,a.BRID,a.BRXM,a.FPHM,e.CZGH,f.NAME as CZYName FROM ");
			hql2.append("MS_YJ01 a,MS_YJ02 b,GY_KSDM c,BASE_USER d,MS_MZXX e,BASE_USER f WHERE a.YJXH=b.YJXH and a.KSDM = c.KSDM and a.YSDM=d.id and a.FPHM = e.FPHM AND a.JGID = e.JGID and e.CZGH = f.ID AND a.JGID =:JGID and a.BRID in(select BRID from MS_BRDA where ZXBZ=0 and (upper(SFZH)=:SFZH or lower(SFZH)=:SFZH)) and  a.FPHM is  not null "+sql2+" and ( a.CFBZ is null or a.CFBZ <> 2 ) and (to_char(A.KDRQ,'yyyy-mm-dd')>=:BDATE and to_char(A.KDRQ,'yyyy-mm-dd')<=:EDATE)  and ( a.DJLY <> 7 OR a.DJLY IS NULL) "
					+ "  and  (a.FJGL IS NOT NULL AND a.FJGL > 0 )  and "
					+ " (a.FJLB=3 or :QYMZSF = 0 or exists (SELECT 1 FROM MS_CF01 c,MS_CF02 d where c.CFSB = d.CFSB AND c.JGID =:JGID and c.BRID in(select BRID from MS_BRDA where ZXBZ=0 and (upper(SFZH)=:SFZH or lower(SFZH)=:SFZH))  and (d.SFJG = 1 and d.SFJG is not null ) and (to_char(C.KFRQ,'yyyy-mm-dd')>=:BDATE and to_char(C.KFRQ,'yyyy-mm-dd')<=:EDATE) "+sql3+"  and ( c.CFBZ is null or c.CFBZ <> 2 )  and ( c.DJLY <> 7 OR c.DJLY IS NULL)  and a.FJGL = d.YPZH ))) e ");
			hql1.append(" GROUP BY a.CFLX,a.KFRQ,a.KSDM,a.YSDM,a.CFSB,a.JZXH,a.DJLY,a.MZXH,c.KSMC,d.NAME,a.BRID,a.BRXM,a.FPHM,e.CZGH,f.Name ");
			hql2.append(" GROUP BY e.XQ,e.CFLX,e.DJLX_text,e.KDRQ,e.KDKS,e.KDYS,e.CFSB,e.LSBZ,e.JZXH,e.DJLY,e.DJLY_TEXT,e.MZXH,e.KSMC,e.NAME,e.BRID,e.BRXM,e.FPHM,e.CZGH,e.CZYName");
			//根据身份证号获取年龄和性别
			int sfzhlen = sfzh.length();
			Map<String, Object> sfinfo = new HashMap<String, Object>();
			ZjzfUtil zjzfutil = new ZjzfUtil();
			if(sfzhlen==15){
				try {
					sfinfo = zjzfutil.getCarInfo15W(sfzh);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					sfinfo.put("sex", "");
					sfinfo.put("age", "");
				}
			}
			if(sfzhlen==18){
				try {
					sfinfo = zjzfutil.getCarInfo(sfzh);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					sfinfo.put("sex", "");
					sfinfo.put("age", "");
				}
			}
			StringBuffer hql = new StringBuffer("select f.XQ as XQ,f.CFLX as CFLX,f.DJLX_text as DJLX_text,f.KDRQ as KDRQ,f.KDKS as KDKS,f.KDYS as KDYS,f.CFSB as CFSB,f.HJJE as HJJE,f.LSBZ as LSBZ,f.JZXH as JZXH,f.DJLY as DJLY,f.DJLY_TEXT as DJLY_TEXT,f.MZXH,f.KSMC,f.NAME,f.BRID,f.BRXM,'"+sfinfo.get("sex").toString()+"' as SEX,'"+sfinfo.get("age").toString()+"' as AGE,'"+executeFlag+"' as EXECUTEFLAG,f.FPHM,f.CZGH,f.CZYName from("+hql1.toString()+" union "+hql2.toString()+") f  order by f.MZXH,f.KDRQ");
			BaseDAO dao = new BaseDAO();
			System.out.println("=====开始执行sql查询=====");
			List<Map<String, Object>> listDj = dao.doSqlQuery(hql.toString(),
					parameters);
			System.out.println("=====结束sql查询：共查询到"+listDj.size()+"条信息=====");
			System.out.println("=====结束执行获取已支付列表业务=====");
			return genResponseXml_PayedList(jgid,listDj);
		} catch (PersistentDataOperationException e) {
			LoggerFactory
			.getLogger(ZjzfHelper.class).error("load failed.", e);
			System.out.println("=====执行获取已支付列表业务失败："+e.getMessage()+"=====");
			return "";
		}
	}
	
	//生成已支付列表接口返回xml报文
	private String genResponseXml_PayedList(String jgid,List<Map<String, Object>> listData)
	{
		//<getUnpayListResponse><resultCode>1</resultCode><resultMessage>成功</resultMessage>
		//<diagnosisRecords><diagnosisRecord><patientId>023829</patientId><diagnosisRecordId>1223</diagnosisRecordId><diagnosisDate>2016-07-10</diagnosisDate><deptId>0939-2020-1020-112</deptId><deptName>呼吸科</deptName><doctorId>9290-109-1991-122</doctorId><doctorName>赵三</doctorName>
		try {
			StringBuffer resXml = new StringBuffer("");
			if(listData!=null && listData.size()>0){
				System.out.println("=====开始构建返回报文=====");
				String mzxh = "";
				resXml.append("<getPayedListResponse><resultCode>1</resultCode><resultMessage>成功</resultMessage><feeRecords>");
				for(int i=0;i<listData.size();i++){
					resXml.append("<feeRecord>");
					resXml.append("<patientId>"+(listData.get(i).get("BRID")==null?"":listData.get(i).get("BRID").toString())+"</patientId>");
					resXml.append("<patientName>"+(listData.get(i).get("BRXM")==null?"":listData.get(i).get("BRXM").toString())+"</patientName>");
					resXml.append("<sex>"+(listData.get(i).get("SEX")==null?"":listData.get(i).get("SEX").toString())+"</sex>");
					resXml.append("<age>"+(listData.get(i).get("AGE")==null?"":listData.get(i).get("AGE").toString())+"</age>");
					resXml.append("<diagnosisRecordId>"+(listData.get(i).get("MZXH")==null?"":listData.get(i).get("MZXH").toString())+"</diagnosisRecordId>");
					resXml.append("<feeNo>"+(listData.get(i).get("CFSB")==null?"":listData.get(i).get("CFSB").toString())+"</feeNo>");
					resXml.append("<feeTypeCode>"+(listData.get(i).get("DJLY")==null?"":listData.get(i).get("DJLY").toString())+"</feeTypeCode>");
					resXml.append("<feeType>"+(listData.get(i).get("DJLY_TEXT")==null?"":listData.get(i).get("DJLY_TEXT").toString())+"</feeType>");
					resXml.append("<feeDesc></feeDesc>");
					resXml.append("<feeDate>"+(listData.get(i).get("KDRQ")==null?"":listData.get(i).get("KDRQ").toString())+"</feeDate>");
					resXml.append("<payedDate>"+(listData.get(i).get("KDRQ")==null?"":listData.get(i).get("KDRQ").toString())+"</payedDate>");
					resXml.append("<invoiceNo>"+(listData.get(i).get("FPHM")==null?"":listData.get(i).get("FPHM").toString())+"</invoiceNo>");
					resXml.append("<barCode>"+(listData.get(i).get("FPHM")==null?"":listData.get(i).get("FPHM").toString())+"</barCode>");
					resXml.append("<totalFee>"+(listData.get(i).get("HJJE")==null?"":listData.get(i).get("HJJE").toString())+"</totalFee>");
					resXml.append("<tollCollector>"+(listData.get(i).get("HJGH")==null?"":listData.get(i).get("HJGH").toString())+"</tollCollector>");
					resXml.append("<tollCollectorName>"+(listData.get(i).get("HJNAME")==null?"":listData.get(i).get("HJNAME").toString())+"</tollCollectorName>");
					resXml.append("<executeFlag>"+(listData.get(i).get("EXECUTEFLAG")==null?"":listData.get(i).get("EXECUTEFLAG").toString())+"</executeFlag>");
					resXml.append("<executeDesc></executeDesc>");
					resXml.append("<guides><guide><itemName></itemName><address>"+DictionaryController.instance().getDic("phis.dictionary.Zjzfzydz").getText(jgid+"@"+(listData.get(i).get("DJLY")==null?"":listData.get(i).get("DJLY").toString()))+"</address></guide></guides>");
					resXml.append("</feeRecord>");
				}
				resXml.append("</feeRecords></getPayedListResponse>");
				System.out.println("=====结束构建返回报文=====");
			}
			return resXml.toString();
		} catch (Exception e) {
			LoggerFactory
			.getLogger(ZjzfHelper.class).error("load failed.", e);
			System.out.println("=====构建返回报文失败："+e.getMessage()+"=====");
			return "";
		}
	}
	/*****************接口2：获取已支付列表  end***************************************/
	
	/*****************接口3：获取费用预结算金额  begin***************************************/
	//获取费用预结算金额xml
	public String GetPrecalculatedFeeXml(String request){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println("=========="+df.format(new Date())+"【开始】 调用HIS接口[APP]：获取费用预结算金额==========");
		String res = "";
		try {
			List<Map<String, Object>> feelist = new ArrayList<Map<String, Object>>();
			//List<DictionaryItem> drugWay = ((TableDictionary) DictionaryController.instance().get("phis.dictionary.drugWay")).initAllItems();
			Document doc = DocumentHelper.parseText(getXmlInfo(request));
			Element root = doc.getRootElement();
			//Element getUnpayedListRequest = root.element("certificates"); // 获取body节点
			String orgId =  root.elementText("orgId");//机构代码
			String patientId =  root.elementText("patientId");//病人标识
			Iterator feerecords = root.elementIterator("feeRecords");
			while (feerecords.hasNext()) {
                Element feerecordEle = (Element) feerecords.next();
                Iterator iter = feerecordEle.elementIterator("feeRecord");
                while (iter.hasNext()) {
        			Map<String, Object> fee =  new HashMap<String, Object>();
                	Element recordEle = (Element) iter.next();
                	String feeTypeCode = recordEle.elementText("feeTypeCode");//费用类别
                	String feeNo = recordEle.elementText("feeNo");//费用标识
                	fee.put("feeTypeCode", feeTypeCode);
                	fee.put("feeNo", feeNo);
                	feelist.add(fee);
                }
			}
			System.out.println("=====成功解析请求报文=====");
			//获取HIS待支付列表信息
			res = getPrecalculatedFee(orgId,patientId,feelist);
			//return orgId+"==certificateType:"+certificateType+"==<getUnpayListResponse><resultCode>1</resultCode><resultMessage>成功</resultMessage><diagnosisRecords><diagnosisRecord><patientId>023829</patientId><diagnosisRecordId>1223</diagnosisRecordId><diagnosisDate>2016-07-10</diagnosisDate><deptId>0939-2020-1020-112</deptId><deptName>呼吸科</deptName><doctorId>9290-109-1991-122</doctorId><doctorName>赵三</doctorName>";
/*		} catch (ControllerException e) {
			e.printStackTrace();*/
		}catch (Exception e) {
			LoggerFactory
			.getLogger(ZjzfHelper.class).error("load failed.", e);
		}
		System.out.println("=========="+df.format(new Date())+"【结束】 调用HIS接口[APP]：获取费用预结算金额==========");
		System.out.println("");
		return res;
	}
	
	//获取费用预结算金额
	private String getPrecalculatedFee(String jgid, String patientId, List<Map<String, Object>> feelist)
	{
		System.out.println("=====开始 获取费用预结算金额 业务=====");
		try {
			//获取病人信息
			Map<String, Object> brxx = getBrxx(patientId);
			double totalFee = 0;//总金额
			double selfPay = 0;//自付金额
			String extraFeeDesc = "";//加收费费用描述
			String errorFeeNos = "";//错误的费用标识，需返回给app
			String precalId = "";//预结算标识：使用@拼接ms_cf01和ms_yj01主键，其中ms_cf01表cfsb从小到大以|符号拼接
			/************自费************/
			if(brxx.get("BRXZ").toString().equals("1000")){
				System.out.println("=====开始 根据请求报文中费用标识获取HIS中未结算费用标识【自费】=====");
				//解析费用清单：包括处方、医技
				if(feelist!=null && feelist.size()>0){
					String precalId_cf = "";
					String precalId_yj = "";
					int feeNo_cf_pre = 0;//上一次 
					int feeNo_yj_pre = 0;//上一次 
					for(int i=0;i<feelist.size();i++){
						//费用类别
						int feeTypeCode = (feelist.get(i).get("feeTypeCode")==null?0:Integer.parseInt(feelist.get(i).get("feeTypeCode").toString()));
						//费用标识
						int feeNo = (feelist.get(i).get("feeNo")==null?0:Integer.parseInt(feelist.get(i).get("feeNo").toString()));
						switch(feeTypeCode){
							case 21://西药
							case 22://成药
							case 23://中草药
								List<Map<String, Object>> cfxx = getCf02(feeNo);
								if(cfxx==null || cfxx.size()==0){
									errorFeeNos += feeNo+",";
								}
								else{
									if(feeNo_cf_pre==0){
										precalId_cf += feeNo+"|";
									}else if(feeNo_cf_pre<feeNo){
										precalId_cf += feeNo+"|";
									}else if(feeNo_cf_pre>feeNo){
										precalId_cf = feeNo+"|"+precalId_cf;
									}
									totalFee = BSHISUtil.doublesum(totalFee,BSPHISUtil.getDouble(cfxx.get(0).get("ZLJE"), 2));
									selfPay = BSHISUtil.doublesum(selfPay,BSPHISUtil.getDouble(cfxx.get(0).get("ZLJE"), 2));
									feeNo_cf_pre = feeNo;
								}
								break;
							case 13://诊查费
							case 51://治疗
							case 31://检验
							case 41://检查
								List<Map<String, Object>> yjxx = getYj02(feeNo);
								if(yjxx==null || yjxx.size()==0){
									errorFeeNos += feeNo+",";
								}
								else{
									if(feeNo_yj_pre==0){
										precalId_yj += feeNo+"|";
									}else if(feeNo_yj_pre<feeNo){
										precalId_yj += feeNo+"|";
									}else if(feeNo_cf_pre>feeNo){
										precalId_yj = feeNo+"|"+precalId_yj;
									}
									totalFee = BSHISUtil.doublesum(totalFee,BSPHISUtil.getDouble(yjxx.get(0).get("ZLJE"), 2));
									selfPay = BSHISUtil.doublesum(selfPay,BSPHISUtil.getDouble(yjxx.get(0).get("ZLJE"), 2));
									feeNo_yj_pre = feeNo;
								}
								break;
							default:
								break;
						}
					}
					if(errorFeeNos.length()==0){
						if(!precalId_cf.equals("")){
							precalId_cf = precalId_cf.substring(0,precalId_cf.length()-1);
						}
						if(!precalId_yj.equals("")){
							precalId_yj = precalId_yj.substring(0,precalId_yj.length()-1);
						}
						if(!precalId_cf.equals("") && !precalId_yj.equals("")){
							precalId = precalId_cf +"@"+precalId_yj;
						}
						else{
							precalId = precalId_cf + precalId_yj;
						}
					}
					System.out.println("=====结束 根据请求报文中费用标识获取HIS中未结算费用标识【自费】=====");
				}
			}
			/************农合************/
			else if(brxx.get("BRXZ").toString().equals("6000")){
				System.out.println("=====开始 根据请求报文中费用标识获取HIS中未结算费用标识【农合】=====");
				System.out.println("=====结束 根据请求报文中费用标识获取HIS中未结算费用标识【农合】=====");
			}
			/************医保************/
			else if(brxx.get("BRXZ").toString().equals("2000") || brxx.get("BRXZ").toString().equals("3000")){
				System.out.println("=====开始 根据请求报文中费用标识获取HIS中未结算费用标识【医保】=====");
				System.out.println("=====结束 根据请求报文中费用标识获取HIS中未结算费用标识【医保】=====");	
			}
			//return genResponseXml_PrecalculatedFee(listDj);
			//目前只开通自费结算，所以返回报文写死
			if(errorFeeNos.length()>0){
				System.out.println("=====完成 请求预结算费用清单的状态校验失败：费用单据未查询到，单据号为"+errorFeeNos+"=====");
				return "<getPrecalculatedFeeResponse><resultCode>0</resultCode><resultMessage>失败：费用单据未查询到，单据号为"+errorFeeNos+"</resultMessage></getPrecalculatedFeeResponse>";
			}
			System.out.println("=====结束 获取费用预结算金额业务=====");
			System.out.println("=====完成 构建获取费用预结算金额返回报文=====");
			return "<getPrecalculatedFeeResponse><resultCode>1</resultCode><resultMessage>成功</resultMessage>" +
					"<precalId>"+precalId+"</precalId><totalFee>"+totalFee+"</totalFee><selfPay>"+selfPay+"</selfPay><extraFeeDesc></extraFeeDesc><items><item><displayName>自理金额</displayName><value>"+selfPay+"</value></item></items><insuranceType></insuranceType></getPrecalculatedFeeResponse>";
		}catch (Exception e) {
			LoggerFactory
			.getLogger(ZjzfHelper.class).error("load failed.", e);
			System.out.println("=====结束 获取费用预结算金额业务失败："+e.getMessage()+"=====");
			return "";
		}
	}
	
	//生成费用预结算金额接口返回xml报文
	private String genResponseXml_PrecalculatedFee(List<Map<String, Object>> listData)
	{
		return "";
	}
	/*****************接口3：获取费用预结算金额  end***************************************/
	
	/*****************接口4：确认费用结算处理  begin***************************************/
	//确认费用结算处理xml
	public String GetNotifyPayedXml(String request)
			throws ModelDataOperationException{
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println("=========="+df.format(new Date())+"【开始】 调用HIS接口[APP]：确认费用结算处理==========");
		String res = "";
		try {
			List<Map<String, Object>> feelist = new ArrayList<Map<String, Object>>();
			//List<DictionaryItem> drugWay = ((TableDictionary) DictionaryController.instance().get("phis.dictionary.drugWay")).initAllItems();
			Document doc = DocumentHelper.parseText(getXmlInfo(request));
			Element root = doc.getRootElement();
			//Element getUnpayedListRequest = root.element("certificates"); // 获取body节点
			String orgId =  root.elementText("orgId");//机构代码
			String patientId =  root.elementText("patientId");//病人标识
			String tradeType =  root.elementText("tradeType");//服务项目代码固定值：0108
			String tradeNo =  root.elementText("tradeNo");//订单号
			String precalId =  root.elementText("precalId");//预结算标识
			String tradeDate =  root.elementText("tradeDate");//订单日期
			String payer =  root.elementText("payer");//付款人
			double selfPay =  BSPHISUtil.getDouble(root.elementText("selfPay"), 2);//自付金额
			String payState =  root.elementText("payState");//支付标志 01待付款 02过期 03取消 04退款中 05已退款 06支付成功 07支付失败 08已支付, 医院端处理失败 09app端成功 10hcn处理失败
			String payType =  root.elementText("payType");//支付方式
			//判断支付标志是否为app端成功(09)状态
			if(!payState.equals("09")){
				System.out.println("==========费用结算失败：该订单支付状态（"+payState+"）有误，请核实！==========");
				return "<notifyPayedResponse><resultCode>0</resultCode><resultMessage>费用结算失败：该订单支付状态（"+payState+"）有误，请核实！</resultMessage></notifyPayedResponse>";
			}
			Iterator feerecords = root.elementIterator("feeRecords");
			while (feerecords.hasNext()) {
                Element feerecordEle = (Element) feerecords.next();
                Iterator iter = feerecordEle.elementIterator("feeRecord");
                while (iter.hasNext()) {
        			Map<String, Object> fee =  new HashMap<String, Object>();
                	Element recordEle = (Element) iter.next();
                	String feeTypeCode = recordEle.elementText("feeTypeCode");//费用类别
                	String feeNo = recordEle.elementText("feeNo");//费用标识
                	fee.put("feeTypeCode", feeTypeCode);
                	fee.put("feeNo", feeNo);
                	feelist.add(fee);
                }
			}
			System.out.println("=====成功解析请求报文=====");
			//获取HIS待支付列表信息
			res = getNotifyPayed(orgId,patientId,precalId,selfPay,payType,tradeNo,feelist);
			//return orgId+"==certificateType:"+certificateType+"==<getUnpayListResponse><resultCode>1</resultCode><resultMessage>成功</resultMessage><diagnosisRecords><diagnosisRecord><patientId>023829</patientId><diagnosisRecordId>1223</diagnosisRecordId><diagnosisDate>2016-07-10</diagnosisDate><deptId>0939-2020-1020-112</deptId><deptName>呼吸科</deptName><doctorId>9290-109-1991-122</doctorId><doctorName>赵三</doctorName>";
		/*} catch (ControllerException e) {
			e.printStackTrace();*/
		}catch (Exception e) {
			LoggerFactory
			.getLogger(ZjzfHelper.class).error("load failed.", e);
			res = "<notifyPayedResponse><resultCode>0</resultCode><resultMessage>"+e.getMessage()+"</resultMessage></notifyPayedResponse>";
		}
		System.out.println("=========="+df.format(new Date())+"【结束】 调用HIS接口[APP]：确认费用结算处理==========");
		System.out.println("");
		return res;
	}

	//确认费用结算处理
	private String getNotifyPayed(String jgid, String patientId, String precalId_app, double selfPay_app,String payType,String tradeNo, List<Map<String, Object>> feelist)
			throws ModelDataOperationException	{
		System.out.println("=====开始执行费用结算业务=====");
		Map<String, Object> MS_MZXX =  new HashMap<String, Object>();
		BaseDAO dao = new BaseDAO();
		int error=0;
		try {
			Context ctx = ContextUtils.getContext();
			//String uid=DictionaryController.instance().getDic("phis.dictionary.Zjzfsfy").getText(jgid); 
			String uid=DictionaryController.instance().getDic("phis.dictionary.YYGHYG").getText(jgid); //改为使用预约挂号员工字典
			if(uid==null || uid.length() == 0){
				System.out.println("=====未找到该机构的APP支付默认收费员信息，请确认该机构已开放APP支付！！！=====");
				return "<notifyPayedResponse><resultCode>0</resultCode><resultMessage>未找到该机构的APP支付默认收费员信息，请确认该机构已开放APP支付！！！</resultMessage></notifyPayedResponse>";
				//throw new ModelDataOperationException("未找到该机构的APP支付默认收费员信息，请确认该机构已开放APP支付！！！");
			}
			Session ss = (Session) ctx.get(Context.DB_SESSION);
			ctx.put(Context.DB_SESSION,ss);
			if (!ctx.containsKey(Context.USER_ROLE_TOKEN)) {
				User user;
				try{
					user=AccountCenter.getUser(uid);
					Object[] urs = user.getUserRoleTokens().toArray();
					ctx.put(Context.USER_ROLE_TOKEN, urs[0]);
				}catch(ControllerException e){
					e.printStackTrace();
				}
			}
			//获取病人信息
			Map<String, Object> brxx = getBrxx(patientId);
			double totalFee = 0;//总金额
			double selfPay = 0;//自付金额
			String extraFeeDesc = "";//加收费费用描述
			String errorFeeNos = "";//错误的费用标识，需返回给app
			String precalId = "";//预结算标识：使用@拼接ms_cf01和ms_yj01主键，其中ms_cf01表cfsb从小到大以|符号拼接
			String cf02s = "";
			String yj02s = "";
			String fphm = "";
			/************自费************/
			if(brxx.get("BRXZ").toString().equals("1000")){
				//解析费用清单：包括处方、医技
				if(feelist!=null && feelist.size()>0){
					System.out.println("=====开始 根据请求报文中费用标识获取HIS中未结算费用标识【自费】=====");
					String precalId_cf = "";
					String precalId_yj = "";
					int feeNo_cf_pre = 0;//上一次 
					int feeNo_yj_pre = 0;//上一次 
					for(int i=0;i<feelist.size();i++){
						//费用类别
						int feeTypeCode = (feelist.get(i).get("feeTypeCode")==null?0:Integer.parseInt(feelist.get(i).get("feeTypeCode").toString()));
						//费用标识
						int feeNo = (feelist.get(i).get("feeNo")==null?0:Integer.parseInt(feelist.get(i).get("feeNo").toString()));
						switch(feeTypeCode){
							case 21://西药
							case 22://成药
							case 23://中草药
								List<Map<String, Object>> cfxx = getCf02(feeNo);
								if(cfxx==null || cfxx.size()==0){
									errorFeeNos += feeNo+",";
								}
								else{
									if(feeNo_cf_pre==0){
										precalId_cf += feeNo+"|";
									}else if(feeNo_cf_pre<feeNo){
										precalId_cf += feeNo+"|";
									}else if(feeNo_cf_pre>feeNo){
										precalId_cf = feeNo+"|"+precalId_cf;
									}
									totalFee = BSHISUtil.doublesum(totalFee,BSPHISUtil.getDouble(cfxx.get(0).get("ZLJE"), 2));
									selfPay = BSHISUtil.doublesum(selfPay,BSPHISUtil.getDouble(cfxx.get(0).get("ZLJE"), 2));
									feeNo_cf_pre = feeNo;
									cf02s += feeNo+",";
								}
								break;
							case 13://诊查费
							case 51://治疗
							case 31://检验
							case 41://检查
								List<Map<String, Object>> yjxx = getYj02(feeNo);
								if(yjxx==null || yjxx.size()==0){
									errorFeeNos += feeNo+",";
								}
								else{
									if(feeNo_yj_pre==0){
										precalId_yj += feeNo+"|";
									}else if(feeNo_yj_pre<feeNo){
										precalId_yj += feeNo+"|";
									}else if(feeNo_cf_pre>feeNo){
										precalId_yj = feeNo+"|"+precalId_yj;
									}
									totalFee = BSHISUtil.doublesum(totalFee,BSPHISUtil.getDouble(yjxx.get(0).get("ZLJE"), 2));
									selfPay = BSHISUtil.doublesum(selfPay,BSPHISUtil.getDouble(yjxx.get(0).get("ZLJE"), 2));
									feeNo_yj_pre = feeNo;
									yj02s += feeNo+",";
								}
								break;
							default:
								break;
						}
					}
					if(errorFeeNos.length()==0){
						if(!precalId_cf.equals("")){
							precalId_cf = precalId_cf.substring(0,precalId_cf.length()-1);
						}
						if(!precalId_yj.equals("")){
							precalId_yj = precalId_yj.substring(0,precalId_yj.length()-1);
						}
						if(!precalId_cf.equals("") && !precalId_yj.equals("")){
							precalId = precalId_cf +"@"+precalId_yj;
						}
						else{
							precalId = precalId_cf + precalId_yj;
						}
					}
					System.out.println("=====结束 根据请求报文中费用标识获取HIS中未结算费用标识【自费】=====");
				}
			}
			/************农合************/
			else if(brxx.get("BRXZ").toString().equals("6000")){
				System.out.println("=====开始 根据请求报文中费用标识获取HIS中未结算费用标识【农合】=====");
				System.out.println("=====结束 根据请求报文中费用标识获取HIS中未结算费用标识【农合】=====");
			}
			/************医保************/
			else if(brxx.get("BRXZ").toString().equals("2000") || brxx.get("BRXZ").toString().equals("3000")){	
				System.out.println("=====开始 根据请求报文中费用标识获取HIS中未结算费用标识【医保】=====");
				System.out.println("=====结束 根据请求报文中费用标识获取HIS中未结算费用标识【医保】=====");			
			}
			//根据请求的单据清单生成预结算标识并与请求的预结算标识进行对比，同时还需对比总自费金额，两个数值一致后才能继续结算
			if(!precalId.equals(precalId_app) || selfPay!=selfPay_app){
				System.out.println("=====完成 请求结算费用清单的状态校验：请求的结算单状态已发生变化，请核对！=====");
				return "<notifyPayedResponse><resultCode>0</resultCode><resultMessage>费用结算失败：请求的结算单状态已发生变化，请核对！</resultMessage></notifyPayedResponse>";
			}
			if(cf02s.length()>0){
				cf02s =cf02s.substring(0,(cf02s.length()-1));
			}
			else{
				cf02s = "0";
			}
			if(yj02s.length()>0){
				yj02s = yj02s.substring(0,(yj02s.length()-1));
			}
			else{
				yj02s = "0";
			}
			System.out.println("=====完成 请求结算费用清单的状态校验=====");
			List<Map<String, Object>> datas = getCfAndYjMx(jgid,cf02s,yj02s);
			if(datas==null){
				System.out.println("=====结束 获取需结算费用清单的明细信息失败：未获取到任何明细信息=====");
				return "<notifyPayedRequest><resultCode>0</resultCode><resultMessage>未获取到需结算的费用清单！</resultMessage></notifyPayedRequest>";
			}
			System.out.println("=====结束 获取需结算费用清单的明细信息：共查询到"+datas.size()+"条信息=====");
			Date date = new Date();
				int wpjfbz =0;
				int wzsfxmjg =0;
				/**************APP支付****************/
				MS_MZXX.put("ZJJE", totalFee);
				MS_MZXX.put("ZFJE", selfPay);
				MS_MZXX.put("YSK", selfPay);
				MS_MZXX.put("JKJE", selfPay);
				MS_MZXX.put("ZPJE", selfPay);
				MS_MZXX.put("FFFS", getFffs(payType));//APP支付在gy_fkfs表中对应的值
				MS_MZXX.put("XJJE", 0d);// 现金金额
				MS_MZXX.put("QTYS", 0d);
				MS_MZXX.put("JJZF", 0d);
				MS_MZXX.put("ZHJE", 0d);
				MS_MZXX.put("JYLYJMJE", 0d);//家医履约减免金额（app端暂时不考虑履约收费）
				MS_MZXX.put("MZGL", 0);
				MS_MZXX.put("JGID", jgid);// 机构ID
				MS_MZXX.put("HBWC", 0d);// 货币误差
				MS_MZXX.put("ZFPB", 0);// 作废判别
				MS_MZXX.put("THPB", 0);// 退号判别
				MS_MZXX.put("SFFS", 0);// 收费方式
				MS_MZXX.put("HBBZ", 0);// 合并标志
				MS_MZXX.put("SFRQ", date);// 收费日期
				MS_MZXX.put("CZGH", uid);
				MS_MZXX.put("MZLB", BSPHISUtil.getMZLB(jgid, dao));
				MS_MZXX.put("TZJE", 0d);
				MS_MZXX.put("FPZS", 1);//发票张数
				MS_MZXX.put("BRID", brxx.get("BRID").toString());// 
				MS_MZXX.put("BRXM", brxx.get("BRXM").toString());// 
				MS_MZXX.put("BRXB", brxx.get("BRXB").toString());// 
				MS_MZXX.put("BRXZ", brxx.get("BRXZ").toString());// 
				MS_MZXX.put("GHGL", "");// 
				double id_hbwc = 0d;
				
				BSPHISUtil bpu = new BSPHISUtil();
				fphm = bpu.getNotesNumber(uid, jgid, 2, dao,
						ctx);
				System.out.println("=====发票号码："+fphm+"=====");
				if(fphm==null){
					System.out.println("=====未获取到发票号码，请确认收费员是否已维护发票号码！=====");
					//error=1;
					return "<notifyPayedRequest><resultCode>0</resultCode><resultMessage>未获取到发票号码，请确认收费员是否已维护发票号码！</resultMessage></notifyPayedRequest>";
					//throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR,"未获取到发票号码，请确认收费员是否已维护发票号码！");
				}
				MS_MZXX.put("FPHM", fphm);
				 //yx-2017-09-14-改成先查mzxh完成业务再保存mzxx
				 Map<String, Object> temp=new HashMap<String, Object>();
				 temp.put("XH", 1);
				 Map<String, Object> mzxhmap=dao.doSqlLoad("select SEQ_MS_MZXX.NEXTVAL as MZXH from  dual where 1=:XH", temp);
				 MS_MZXX.put("MZXH", Long.parseLong(mzxhmap.get("MZXH")+""));
				 Map<String, Object> mzxh = MS_MZXX;
				 
				Set<Long> fygbs = new HashSet<Long>();
				Set<Long> cfsbs = new HashSet<Long>();
				Set<Long> yjxhs = new HashSet<Long>();
				Map<Long, List<Long>> cfsb_xy = new HashMap<Long, List<Long>>();
				Map<Long, List<Long>> cfsb_zy = new HashMap<Long, List<Long>>();
				Map<Long, List<Long>> cfsb_cy = new HashMap<Long, List<Long>>();
				// 药房允许切换时
				Set<Long> fyyf_xy = new HashSet<Long>();// 西药
				Set<Long> fyyf_zy = new HashSet<Long>();// 中药
				Set<Long> fyyf_cy = new HashSet<Long>();// 草药
				for (int i = 0; i < datas.size(); i++) {
					Map<String, Object> data = datas.get(i);
					fygbs.add(Long.parseLong(data.get("FYGB") + ""));
					if ("0".equals(data.get("CFLX") + "")) {
						if (data.containsKey("YJXH")) {
							yjxhs.add(Long.parseLong(data.get("YJXH") + ""));
						} else {
							yjxhs.add(Long.parseLong(data.get("CFSB") + ""));
						}
					} else {
						int cfs = cfsbs.size();
						cfsbs.add(Long.parseLong(data.get("CFSB") + ""));
						if (cfs != cfsbs.size()) {
							Long yfsb = Long.parseLong(data.get("YFSB") + "");
							if ("1".equals(data.get("CFLX") + "")) {// 以前是对比YS_MZ_FYYF_XY来判断类型，现在改为CFLX
								fyyf_xy.add(yfsb);
								if (cfsb_xy.containsKey(yfsb)) {
									cfsb_xy.get(yfsb).add(Long.parseLong(data.get("CFSB") + ""));
								} else {
									List<Long> list = new ArrayList<Long>();
									list.add(Long.parseLong(data.get("CFSB") + ""));
									cfsb_xy.put(yfsb, list);
								}
							} else if ("2".equals(data.get("CFLX") + "")) {
								fyyf_zy.add(yfsb);
								if (cfsb_zy.containsKey(yfsb)) {
									cfsb_zy.get(yfsb).add(Long.parseLong(data.get("CFSB") + ""));
								} else {
									List<Long> list = new ArrayList<Long>();
									list.add(Long.parseLong(data.get("CFSB") + ""));
									cfsb_zy.put(yfsb, list);
								}
							} else if ("3".equals(data.get("CFLX") + "")) {
								fyyf_cy.add(yfsb);
								if (cfsb_cy.containsKey(yfsb)) {
									cfsb_cy.get(yfsb).add(Long.parseLong(data.get("CFSB") + ""));
								} else {
									List<Long> list = new ArrayList<Long>();
									list.add(Long.parseLong(data.get("CFSB") + ""));
									cfsb_cy.put(yfsb, list);
								}
							}
						}
					}
				}
				Iterator<Long> it = fygbs.iterator();
				while (it.hasNext()) {
					long fygb = it.next();
					Map<String, Object> sfmx = new HashMap<String, Object>();
					sfmx.put("MZXH", mzxh.get("MZXH"));
					sfmx.put("JGID", jgid);
					sfmx.put("SFXM", fygb);
					sfmx.put("FPHM", MS_MZXX.get("FPHM"));
					double zjje = 0;
					double zfje = 0;
					for (int i = 0; i < datas.size(); i++) {
						Map<String, Object> data = datas.get(i);
						if (fygb == Long.parseLong(data.get("FYGB") + "")) {
							String HJJE = data.get("HJJE") + "";
							String ZFBL = data.get("ZFBL") + "";
							zjje = BSPHISUtil.getDouble(zjje + BSPHISUtil.getDouble(Double.parseDouble(HJJE),2), 2);
							zfje = BSPHISUtil.getDouble(zfje + BSPHISUtil.getDouble(Double.parseDouble(HJJE)
									* Double.parseDouble(ZFBL), 2), 2);
						}
					}
					sfmx.put("ZJJE", zjje);
					sfmx.put("ZFJE", zfje);
					System.out.println("=====开始保存收费明细信息MS_SFMX=====");
					/************************************门诊_收费明细表************************************/
					dao.doSave("create", "phis.application.ivc.schemas.MS_SFMX", sfmx, false);
					System.out.println("=====成功保存收费明细信息MS_SFMX=====");
				}
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("FPHM", MS_MZXX.get("FPHM"));
				parameters.put("MZXH", mzxh.get("MZXH"));
				Iterator<Long> cf = cfsbs.iterator();
				StringBuffer cfsb = new StringBuffer();
				List<Long> cfsb_list = new ArrayList<Long>();
				while (cf.hasNext()) {
					String cfsbStr = cf.next() + "";
					if (cfsb.length() > 0) {
						cfsb.append(",");
						cfsb.append(cfsbStr);
					} else {
						cfsb.append(cfsbStr);
					}
					cfsb_list.add(Long.parseLong(cfsbStr));
				}

				Iterator<Long> yj = yjxhs.iterator();
				StringBuffer yjxh = new StringBuffer();
				while (yj.hasNext()) {
					if (yjxh.length() > 0) {
						yjxh.append(",");
						yjxh.append(yj.next());
					} else {
						yjxh.append(yj.next());
					}
				}
				if (yjxh.length() != 0) {
					parameters.put("HJGH", uid);// add by liyunt
					dao.doUpdate("update MS_YJ01 set FPHM=:FPHM,MZXH=:MZXH,HJGH =:HJGH where YJXH in ("
									+ yjxh + ") and FPHM is null and MZXH is null",parameters);
					if (wpjfbz == 1 && wzsfxmjg == 1) {
						Map<String, Object> parameterssbxh = new HashMap<String, Object>();
						parameterssbxh.put("MZXH",Long.parseLong(mzxh.get("MZXH") + ""));
						List<Map<String, Object>> sbxhList = dao.doQuery(
										"select b.SBXH as SBXH from MS_YJ01 a,MS_YJ02 b where a.YJXH=b.YJXH and a.MZXH=:MZXH",
										parameterssbxh);
						for (int q = 0; q < sbxhList.size(); q++) {
							Long sbxh = 0L;
							if (sbxhList.get(q).get("SBXH") != null) {
								sbxh = Long.parseLong(sbxhList.get(q).get("SBXH")+"");
								BSPHISUtil.updateXHMXZT(dao, sbxh, "1", jgid);
							}
						}
					}
					parameters.remove("HJGH");
					ConfigLogisticsInventoryControlModel cic = new ConfigLogisticsInventoryControlModel(dao);
//app支付暂不考虑
/*					cic.saveMzWzxx(yjxh.toString(), Long.parseLong(MS_MZXX.get("GHKS")==null ? "0":MS_MZXX.get("GHKS")+""),
							Long.parseLong(MS_MZXX.get("GHGL") == null ? "0": MS_MZXX.get("GHGL") + ""), Long
									.parseLong(mzxh.get("MZXH") + ""), ctx);*/
				}
				if (cfsb.length() != 0) {
					Map<String, Object> parameters1 = new HashMap<String, Object>();
					String hql = "select CKBH as CKBH from YF_CKBH where JGID=:JGID and YFSB=:YFSB and QYPB=1 order by PDCF";
					String up_cf01 = "update MS_CF01 set FPHM=:FPHM,MZXH=:MZXH,FYCK=:FYCK where CFSB in (:CFSB) and YFSB=:YFSB and FPHM is null and MZXH is null";
					parameters1.put("JGID", jgid);
					if (cfsb_xy.size() > 0) {// 西药处方
						for (Long yfsb : fyyf_xy) {
							parameters1.put("YFSB", yfsb);
							parameters1.remove("CKBH");
							List<Map<String, Object>> ll_ckbh_xyfList = dao.doQuery(hql, parameters1);
							int ll_ckbh_xyf = -1;
							if (ll_ckbh_xyfList.size() > 0) {
								ll_ckbh_xyf = Integer.parseInt(ll_ckbh_xyfList.get(0).get("CKBH")+"");
							}
							parameters.put("FYCK", ll_ckbh_xyf);
							parameters.put("CFSB", cfsb_xy.get(yfsb));
							parameters.put("YFSB", yfsb);
							int updatecfsl = dao.doUpdate(up_cf01, parameters);

							if (ll_ckbh_xyf > 0) { // 西药房
								parameters1.put("CKBH", ll_ckbh_xyf);
								dao.doUpdate("update YF_CKBH set PDCF = PDCF+"+updatecfsl+ 
											" where JGID=:JGID and YFSB=:YFSB and CKBH=:CKBH ",parameters1);
							}
						}
					}
					if (cfsb_zy.size() > 0) {// 中药处方
						for (Long yfsb : fyyf_zy) {
							parameters1.put("YFSB", yfsb);
							parameters1.remove("CKBH");
							List<Map<String, Object>> ll_ckbh_zyfList = dao.doQuery(hql, parameters1);
							int ll_ckbh_zyf = -1;
							if (ll_ckbh_zyfList.size() > 0) {
								ll_ckbh_zyf = Integer.parseInt(ll_ckbh_zyfList.get(0).get("CKBH")+ "");
							}
							parameters.put("FYCK", ll_ckbh_zyf);
							parameters.put("CFSB", cfsb_zy.get(yfsb));
							parameters.put("YFSB", yfsb);
							int updatecfsl = dao.doUpdate(up_cf01, parameters);
							if (updatecfsl != cfsb_zy.get(yfsb).size()) {
								error=1;
								throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR,"门诊结算失败,有单据已结算，请重新导入后再结算!");
							}
							if (ll_ckbh_zyf > 0) { // 中药房
								parameters1.put("CKBH", ll_ckbh_zyf);
								dao.doUpdate(" update YF_CKBH set PDCF = PDCF + "+ updatecfsl+
											 " where JGID=:JGID and YFSB=:YFSB and CKBH=:CKBH ",
											 parameters1);
							}
						}
					}
					if (cfsb_cy.size() > 0) {// 草药处方
						for (Long yfsb : fyyf_cy) {
							parameters1.put("YFSB", yfsb);
							parameters1.remove("CKBH");
							List<Map<String, Object>> ll_ckbh_cyfList = dao
									.doQuery(hql, parameters1);
							int ll_ckbh_cyf = -1;
							if (ll_ckbh_cyfList.size() > 0) {
								ll_ckbh_cyf = Integer.parseInt(ll_ckbh_cyfList.get(0).get("CKBH")+ "");
							}

							parameters.put("FYCK", ll_ckbh_cyf);
							parameters.put("CFSB", cfsb_cy.get(yfsb));
							parameters.put("YFSB", yfsb);
							int updatecfsl = dao.doUpdate(up_cf01, parameters);
							if (updatecfsl != cfsb_cy.get(yfsb).size()) {
								error=1;
								throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR,"门诊结算失败,有单据已结算，请重新导入后再结算!");
							}
							if (ll_ckbh_cyf > 0) { // 草药房
								parameters1.put("CKBH", ll_ckbh_cyf);
								dao.doUpdate("update YF_CKBH set PDCF = PDCF + "+ updatecfsl
											+" where JGID=:JGID and YFSB=:YFSB and CKBH=:CKBH ",parameters1);
							}
						}

					}				
				}
				Map<String, Object> countParameters = new HashMap<String, Object>();
				countParameters.put("MZXH", Long.parseLong(mzxh.get("MZXH") + ""));
				long YJ02count = dao.doCount("MS_YJ01 a,MS_YJ02 b",
						"a.YJXH=b.YJXH and a.MZXH=:MZXH", countParameters);
				long CF02count = dao.doCount("MS_CF01 a,MS_CF02 b",
						"a.CFSB=b.CFSB and a.MZXH=:MZXH and b.ZFYP!=1",
						countParameters);
				if ((YJ02count + CF02count) != datas.size()) {
					error=1;
					throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR,
							"门诊结算失败,结算费用数量已发生变化，请重新导入后再结算!");
				}
				double YJ02hjje = Double.parseDouble(dao.doLoad("select nvl(sum(b.HJJE),0) as HJJE " +
						" from MS_YJ01 a,MS_YJ02 b where a.YJXH=b.YJXH and a.MZXH=:MZXH",countParameters).get("HJJE")+ "");
				double CF02hjje = Double.parseDouble(dao.doLoad("select nvl(sum(b.HJJE),0) as HJJE from MS_CF01 a,MS_CF02 b" +
						" where a.CFSB=b.CFSB and a.MZXH=:MZXH and b.ZFYP!=1",countParameters).get("HJJE")+"");
				if ((BSPHISUtil.getDouble(YJ02hjje + CF02hjje, 2)) != BSPHISUtil
						.getDouble(totalFee, 2)) {//app支付
					error=1;
					throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR,
							"门诊结算失败,结算费用金额已发生变化，请重新导入后再结算!");
				}

				if (id_hbwc != 0) {
					Map<String, Object> parameters2 = new HashMap<String, Object>();
					parameters2.put("HBWC", "1");
					Long FKFS = Long.parseLong(dao.doLoad("select FKFS as FKFS from GY_FKFS where SYLX=1 and HBWC=:HBWC",
											parameters2).get("FKFS")+ "");
					Map<String, Object> MS_FKXX = new HashMap<String, Object>();
					MS_FKXX.put("JGID", jgid);
					MS_FKXX.put("MZXH", mzxh.get("MZXH"));
					MS_FKXX.put("FKFS", FKFS);
					MS_FKXX.put("FKJE", id_hbwc);
					/************************************门诊_门诊收费付款信息************************************/
					System.out.println("=====开始保存门诊收费付款信息（货币误差）MS_FKXX=====");
					dao.doSave("create", BSPHISEntryNames.MS_FKXX, MS_FKXX, false);
					System.out.println("=====成功保存门诊收费付款信息（货币误差）MS_FKXX=====");
				}
				Map<String, Object> MS_FKXX = new HashMap<String, Object>();
				MS_FKXX.put("JGID", jgid);
				MS_FKXX.put("MZXH", mzxh.get("MZXH"));
				/************** APP支付****************/
				MS_FKXX.put("FKFS",getFffs(payType));
				MS_FKXX.put("FKJE", BSPHISUtil.getDouble(selfPay, 2));		
				/************************************门诊_门诊收费付款信息************************************/
				System.out.println("=====开始保存门诊收费付款信息MS_FKXX=====");
				dao.doSave("create", BSPHISEntryNames.MS_FKXX, MS_FKXX, false);
				System.out.println("=====成功保存门诊收费付款信息MS_FKXX=====");
/*				PharmacyDispensingModel mpmm = new PharmacyDispensingModel(dao);
				String SFZJFY = "1";// YDCZSF为0，则为移动收费，设置收费直接发药参数
				if ("1".equals(SFZJFY)) {
					for (int i = 0; i < cfsb_list.size(); i++) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("cfsb", cfsb_list.get(i));
						map.put("fygh", uid);
						Map<String, Object> m = mpmm.saveDispensing(map, ctx);
						if (m.containsKey("x-response-code")) {
							if (Integer.parseInt(m.get("x-response-code") + "") > 300) {
								error=1;
								throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR,
										m.get("x-response-msg")+"");
							}
						}
					}
				}*/
/*				if (body.containsKey("details")) {
					List<Map<String, Object>> dta = (List<Map<String, Object>>) body.get("details");
					Map<String, Object> bllxpar = new HashMap<String, Object>();
					for (int i = 0; i < dta.size(); i++) {
						Map<String, Object> detail = dta.get(i);
						if ("0".equals(String.valueOf(detail.get("CFLX")))) {
							bllxpar.put("SBXH", parseLong(detail.get("SBXH")));
							bllxpar.put("ZFBL", parseDouble(detail.get("ZFBL")));
							dao.doUpdate("update MS_YJ02 set ZFBL=:ZFBL where SBXH=:SBXH",bllxpar);
						} else {
							bllxpar.put("SBXH", parseLong(detail.get("SBXH")));
							bllxpar.put("ZFBL", parseDouble(detail.get("ZFBL")));
							dao.doUpdate("update MS_CF02 set ZFBL=:ZFBL where SBXH=:SBXH",bllxpar);
						}
					}
				}*/
				// 库存冻结代码
/*				int SFQYYFYFY = MedicineUtils.parseInt(ParameterUtil.getParameter(
						jgid, BSPHISSystemArgument.SFQYYFYFY, ctx));// 是否启用库存冻结
				int MZKCDJSJ = MedicineUtils.parseInt(ParameterUtil.getParameter(
						jgid, BSPHISSystemArgument.MZKCDJSJ, ctx));// 库存冻结时间
				if (SFQYYFYFY == 1 && MZKCDJSJ == 2) {// 如果启用库存冻结,并且在收费时冻结
					// 先删除过期的冻结库存
					//MedicineCommonModel model = new MedicineCommonModel(dao);
					//model.deleteKCDJ(manaUnitId, ctx);
					StringBuffer hql_yfbz = new StringBuffer();
					hql_yfbz.append("select YFBZ as YFBZ from YF_YPXX where YPXH=:ypxh and YFSB=:yfsb");
					for (Map<String, Object> map_cf02 : datas) {
						if ("0".equals(map_cf02.get("CFLX") + "")) {
							continue;
						}
						Map<String, Object> map_par_yfbz = new HashMap<String, Object>();
						map_par_yfbz.put("ypxh",MedicineUtils.parseLong(map_cf02.get("YPXH")));
						map_par_yfbz.put("yfsb",MedicineUtils.parseLong(map_cf02.get("YFSB")));
						int yfbz = MedicineUtils.parseInt(dao.doLoad(
								hql_yfbz.toString(), map_par_yfbz).get("YFBZ"));
						Map<String, Object> map_kcdj = new HashMap<String, Object>();
						map_kcdj.put("JGID", jgid);
						map_kcdj.put("YFSB",MedicineUtils.parseLong(map_cf02.get("YFSB")));
						map_kcdj.put("CFSB",MedicineUtils.parseLong(map_cf02.get("CFSB")));
						map_kcdj.put("YPXH",MedicineUtils.parseLong(map_cf02.get("YPXH")));
						map_kcdj.put("YPCD",MedicineUtils.parseLong(map_cf02.get("YPCD")));
						map_kcdj.put("YPSL",MedicineUtils.simpleMultiply(2,map_cf02.get("YPSL"), map_cf02.get("CFTS")));
						map_kcdj.put("YFBZ", yfbz);
						map_kcdj.put("DJSJ", new Date());
						map_kcdj.put("JLXH",MedicineUtils.parseLong(map_cf02.get("SBXH")));
						*//************************************库存冻结************************************//*
						dao.doSave("create","phis.application.pha.schemas.YF_KCDJ", map_kcdj,false);
					}
				}*/
				// 库存冻结代码结束
				if (MS_MZXX.containsKey("ZDXH")
						&& MedicineUtils.parseLong(MS_MZXX.get("ZDXH")) != 0
						&& MS_MZXX.containsKey("GHGL")
						&& MedicineUtils.parseLong(MS_MZXX.get("GHGL")) != 0) {
					StringBuffer hql = new StringBuffer();
					hql.append("update MS_GHMX set ZDXH=:zdxh where SBXH=:ghgl");
					Map<String, Object> map_par = new HashMap<String, Object>();
					map_par.put("zdxh",MedicineUtils.parseLong(MS_MZXX.get("ZDXH")));
					map_par.put("ghgl",MedicineUtils.parseLong(MS_MZXX.get("GHGL")));
					dao.doUpdate(hql.toString(), map_par);
				}
				/************************************门诊_门诊收费信息************************************/
				System.out.println("=====开始保存门诊信息MS_MZXX=====");
				dao.doSave("create",BSPHISEntryNames.MS_MZXX, MS_MZXX, false);
				System.out.println("=====成功保存门诊信息MS_MZXX=====");
			//return genResponseXml_PrecalculatedFee(listDj);
			//目前只开通自费结算，所以返回报文写死
			//return "<getPrecalculatedFeeResponse><resultCode>1</resultCode><resultMessage>成功</resultMessage>" +
			//		"<precalId>"+precalId+"</precalId><totalFee>"+totalFee+"</totalFee><selfPay>"+selfPay+"</selfPay><extraFeeDesc></extraFeeDesc><items><item><displayName>自理金额</displayName><value>"+selfPay+"</value></item></items><insuranceType></insuranceType></getPrecalculatedFeeResponse>";
//String jgid, String patientId, String precalId_app, double selfPay_app,String payType
				Map<String, Object> payrecord = new HashMap<String, Object>();
				payrecord.put("BRID",patientId);
				payrecord.put("JGID",jgid);
				payrecord.put("VOUCHERNO",fphm);
				payrecord.put("PAYMONEY",selfPay_app+"");
				payrecord.put("PAYTYPE",getPaytypeForJhpt(payType));
				payrecord.put("COLLECTFEESCODE",uid);
				UserRoleToken user = (UserRoleToken) ctx.get(Context.USER_ROLE_TOKEN);
				payrecord.put("COLLECTFEESNAME",user.getUserName());
				//payrecord.put("VERIFYNO",tradeNo);
				payrecord.put("HOSPNO",tradeNo);
				String res = genResponseXml_NotifyPayed(precalId_app,fphm,datas,payrecord,ss);
				if(res.equals("")){
					error=1;
					throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "门诊结算失败");
				}
				return res;
		}catch (ValidateException e) {
			error=1;
			System.out.println("=====门诊结算失败："+e.getMessage()+"=====");
			//logger.error("ValidateException Prescribing a single query to fail.", e);
			throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "门诊结算失败");
		}catch (PersistentDataOperationException e) {
			error=1;
			System.out.println("=====门诊结算失败："+e.getMessage()+"=====");
			//logger.error("PersistentDataOperationException Prescribing a single query to fail.", e);
			throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "门诊结算失败");
		}finally {
			Map<String, Object> map_pa = new HashMap<String, Object>();
			map_pa.put("MZXH", MS_MZXX.get("MZXH"));
			if(error==1){
				try {
					System.out.println("=====开始 撤销门诊结算业务：删除MS_SFMX、MS_FKXX信息，重置MS_CF01、MS_YJ01结算信息=====");
					dao.doSqlUpdate("delete from MS_SFMX where MZXH=:MZXH", map_pa);
					dao.doSqlUpdate("delete from MS_FKXX where MZXH=:MZXH", map_pa);
					dao.doSqlUpdate("DELETE FROM PAYRECORD WHERE VOUCHERNO IN (SELECT FPHM FROM MS_CF01 WHERE MZXH=:MZXH) OR VOUCHERNO IN (SELECT FPHM FROM MS_YJ01 WHERE MZXH=:MZXH)", map_pa);
					dao.doSqlUpdate("update MS_CF01 set FPHM=null,MZXH=null,FYCK=null where MZXH=:MZXH", map_pa);
					dao.doSqlUpdate("update MS_YJ01 set FPHM=null,MZXH=null,HJGH=null where MZXH=:MZXH", map_pa);
					System.out.println("=====完成 撤销门诊结算业务：删除MS_SFMX、MS_FKXX信息，重置MS_CF01、MS_YJ01结算信息=====");
				} catch (PersistentDataOperationException e) {
					System.out.println("=====撤销门诊结算业务失败："+e.getMessage()+"=====");
					e.printStackTrace();
				}
			}
			if(error==1){
				System.out.println("=====门诊结算失败=====");
				return "<notifyPayedRequest><resultCode>0</resultCode><resultMessage>门诊结算失败</resultMessage></notifyPayedRequest>";
			}
		}
	}
	
	//生成费用预结算金额接口返回xml报文
	private String genResponseXml_NotifyPayed(String precalId,String fphm,List<Map<String, Object>> listData,Map<String, Object> payrecord,Session ss)
	{
		System.out.println("=====开始构建返回报文=====");
		try{
			StringBuffer resXml = new StringBuffer("");
			if(listData!=null && listData.size()>0){
				String mzxh = "";
				resXml.append("<notifyPayedResponse><resultCode>1</resultCode><resultMessage>成功</resultMessage><hcnTradeNo></hcnTradeNo><resultId>"+fphm+"</resultId><precalId>"+precalId+"</precalId><feeRecords>");
				for(int i=0;i<listData.size();i++){
					resXml.append("<feeRecord>");
					//费用类别
					double hjje = BSPHISUtil.getDouble(listData.get(i).get("HJJE"), 2);
					int feeNo = (listData.get(i).get("CFSB")==null?0:Integer.parseInt(listData.get(i).get("CFSB").toString()));
					resXml.append("<feeTypeCode>"+(listData.get(i).get("DJLY")==null?"":listData.get(i).get("DJLY").toString())+"</feeTypeCode>");
					resXml.append("<feeType>"+(listData.get(i).get("DJLY_TEXT")==null?"":listData.get(i).get("DJLY_TEXT").toString())+"</feeType>");
					resXml.append("<feeNo>"+feeNo+"</feeNo><barCode>"+fphm+"</barCode><totalFee>"+hjje+"</totalFee></feeRecord>");
				}
				resXml.append("</feeRecords>");
				
				System.out.println("=====开始构建返回报文：聚合支付平台订单信息=====");
				String resXml_OtherPay = doSavePayrecord(payrecord,ss);
				resXml.append(resXml_OtherPay);
				System.out.println("=====结束构建返回报文：聚合支付平台订单信息=====");
				
				resXml.append("</notifyPayedResponse>");
			}
			System.out.println("=====完成构建返回报文=====");
			return resXml.toString();
		} catch (Exception e) {
			System.out.println("=====构建返回报文失败："+e.getMessage()+"=====");
			return "";
		}
	}
	/*****************接口4：确认费用结算处理  end***************************************/

	/*****************接口5：获取收费单据详细列表  begin***************************************/
	//获取收费单据详细列表xml
	public String GetFeeDetailXml(String request){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println("=========="+df.format(new Date())+"【开始】 调用HIS接口[APP]：获取收费单据详细列表==========");
		String res = "";
		try {
			//List<DictionaryItem> drugWay = ((TableDictionary) DictionaryController.instance().get("phis.dictionary.drugWay")).initAllItems();
			Document doc = DocumentHelper.parseText(getXmlInfo(request));
			Element root = doc.getRootElement();
			//Element getUnpayedListRequest = root.element("certificates"); // 获取body节点
			String orgId =  root.elementText("orgId");//机构代码
			String patientId =  root.elementText("patientId");//病人标识
			//收费单据类别
			int feeTypeCode = (root.elementText("feeTypeCode")==null?0:Integer.parseInt(root.elementText("feeTypeCode")));
			//收费单据标识
			int feeNo = (root.elementText("feeNo")==null?0:Integer.parseInt(root.elementText("feeNo")));
			System.out.println("=====成功解析请求报文=====");
			//获取HIS待支付列表信息
			res = getFeeDetail(orgId,patientId,feeTypeCode,feeNo);
			//return orgId+"==certificateType:"+certificateType+"==<getUnpayListResponse><resultCode>1</resultCode><resultMessage>成功</resultMessage><diagnosisRecords><diagnosisRecord><patientId>023829</patientId><diagnosisRecordId>1223</diagnosisRecordId><diagnosisDate>2016-07-10</diagnosisDate><deptId>0939-2020-1020-112</deptId><deptName>呼吸科</deptName><doctorId>9290-109-1991-122</doctorId><doctorName>赵三</doctorName>";
/*		} catch (ControllerException e) {
			e.printStackTrace();*/
		}catch (Exception e) {
			LoggerFactory
			.getLogger(ZjzfHelper.class).error("load failed.", e);
		}
		System.out.println("=========="+df.format(new Date())+"【结束】 调用HIS接口[APP]：获取收费单据详细列表==========");
		System.out.println("");
		return res;
	}
	
	//获取收费单据详细列表
	private String getFeeDetail(String jgid, String patientId,int feeTypeCode,int feeNo)
	{
		try {
			String hql = "";//查询处方表、医技表
			switch(feeTypeCode){
				case 21://西药
				case 22://成药
					hql = "SELECT A.FEENO,A.GORUPNO,A.ITEMCODE,A.ITEMNAME,A.SPECIFICATION,A.QUANTITY,A.PRICE,A.TOTALFEE," +
							"A.REMARK,A.DOSAGE,A.DOSAGEUNIT,A.FREQUENCYCODE,A.FREQUENCY,A.USAGECODE,A.USAGE,A.DAYS,B.FEETYPECODE,B.FEETYPE,B.FEENAME," +
							"B.DEPTID,B.DEPTNAME,B.DOCTORID,B.DOCTORNAME,B.FEEDATE,B.TOTALFEE AS TOTALFEE_TOTAL FROM (" +
							"SELECT A.CFSB AS FEENO,A.YPZH AS GORUPNO,A.YPXH AS ITEMCODE,C.YPMC AS ITEMNAME, " +
							"A.YFGG AS SPECIFICATION,A.YPSL AS QUANTITY,A.YPDJ AS PRICE,A.HJJE AS TOTALFEE,A.BZXX AS REMARK," +
							"A.YCJL AS DOSAGE,A.YFDW AS DOSAGEUNIT,A.YPYF AS FREQUENCYCODE,E.PCMC AS FREQUENCY,A.GYTJ AS USAGECODE,D.XMMC AS USAGE,A.YYTS AS DAYS " +
							"FROM MS_CF02 A LEFT JOIN GY_SYPC E ON A.YPYF = E.PCBM  LEFT JOIN ZY_YPYF D ON A.GYTJ = D.YPYF,MS_CF01 B,YK_TYPK C WHERE A.CFSB=B.CFSB AND A.YPXH=C.YPXH AND B.ZFPB=0  AND (B.CFLX=1 OR B.CFLX=2) " +
							"AND A.CFSB=:FEENO AND B.BRID=:PATIENTID AND A.JGID=:JGID) A,(" +
							"SELECT A.CFSB AS FEENO,CASE B.CFLX WHEN 1 THEN 21 WHEN 2 THEN 22 WHEN 3 THEN 23 END AS FEETYPECODE," +
							"CASE B.CFLX WHEN 1 THEN '西药' WHEN 2 THEN '成药' WHEN 3 THEN '中草药' END AS FEETYPE," +
							"CASE B.CFLX WHEN 1 THEN '西药' WHEN 2 THEN '成药' WHEN 3 THEN '中草药' END AS FEENAME,B.KSDM AS DEPTID,C.KSMC AS DEPTNAME," +
							"B.YSDM AS DOCTORID,D.NAME AS DOCTORNAME, B.KFRQ AS FEEDATE,SUM(A.HJJE) AS TOTALFEE " +
							"FROM MS_CF02 A,MS_CF01 B,GY_KSDM C,BASE_USER D WHERE A.CFSB=B.CFSB AND B.KSDM=C.KSDM " +
							"AND B.YSDM=D.ID AND B.ZFPB=0 AND (B.CFLX=1 OR B.CFLX=2) AND A.CFSB=:FEENO AND B.BRID=:PATIENTID AND A.JGID=:JGID " +
							"GROUP BY A.CFSB,B.CFLX,B.KSDM,C.KSMC,B.YSDM,D.NAME,B.KFRQ) B WHERE A.FEENO=B.FEENO";
					break;
				case 23://中草药
					hql = "SELECT A.FEENO,A.GORUPNO,A.ITEMCODE,A.ITEMNAME,A.SPECIFICATION,A.QUANTITY,A.PRICE,A.TOTALFEE," +
							"A.REMARK,A.USAGE,B.FEETYPECODE,B.FEETYPE,B.FEENAME,B.DEPTID,B.DEPTNAME,B.DOCTORID,B.DOCTORNAME," +
							"B.FEEDATE,B.TOTALFEE AS TOTALFEE_TOTAL,B.QUANTITY AS QUANTITY_TOTAL,B.DECOCTIONDESC " +
							"FROM (SELECT A.CFSB AS FEENO,A.YPZH AS GORUPNO,A.YPXH AS ITEMCODE,C.YPMC AS ITEMNAME," +
							"A.YFGG AS SPECIFICATION,A.YPSL AS QUANTITY,A.YPDJ AS PRICE,A.HJJE AS TOTALFEE,A.BZXX AS REMARK," +
							"D.XMMC AS USAGE " +
							"FROM MS_CF02 A LEFT JOIN ZY_YPYF D ON A.GYTJ = D.YPYF,MS_CF01 B,YK_TYPK C WHERE A.CFSB=B.CFSB AND A.YPXH=C.YPXH " +
							"AND B.ZFPB=0 AND B.CFLX=3 AND A.CFSB=:FEENO AND B.BRID=:PATIENTID AND A.JGID=:JGID) A,(" +
							"SELECT A.CFSB AS FEENO," +
							"CASE B.CFLX WHEN 1 THEN 21 WHEN 2 THEN 22 WHEN 3 THEN 23 END AS FEETYPECODE," +
							"CASE B.CFLX WHEN 1 THEN '西药' WHEN 2 THEN '成药' WHEN 3 THEN '中草药' END AS FEETYPE," +
							"CASE B.CFLX WHEN 1 THEN '西药' WHEN 2 THEN '成药' WHEN 3 THEN '中草药' END AS FEENAME,B.KSDM AS DEPTID,C.KSMC AS DEPTNAME," +
							"B.YSDM AS DOCTORID,D.NAME AS DOCTORNAME, B.KFRQ AS FEEDATE,SUM(A.HJJE) AS TOTALFEE," +
							"A.CFTS AS QUANTITY,B.JZF AS DECOCTIONDESC " +
							"FROM MS_CF02 A,MS_CF01 B,GY_KSDM C,BASE_USER D WHERE A.CFSB=B.CFSB AND B.KSDM=C.KSDM " +
							"AND B.YSDM=D.ID AND B.ZFPB=0 AND B.CFLX=3 AND A.CFSB=:FEENO AND B.BRID=:PATIENTID AND A.JGID=:JGID " +
							"GROUP BY A.CFSB,B.CFLX,B.KSDM,C.KSMC,B.YSDM,D.NAME,B.KFRQ,A.CFTS,B.JZF) B WHERE A.FEENO=B.FEENO";
					break;
				case 13://诊查费
				case 51://治疗
				case 31://检验
					hql = "SELECT A.FEENO,A.ITEMCODE,A.ITEMNAME,A.SPECIFICATION,A.QUANTITY,A.PRICE,A.TOTALFEE,A.REMARK," +
							"B.FEETYPECODE,B.FEETYPE,B.FEENAME,B.DEPTID,B.DEPTNAME,B.DOCTORID,B.DOCTORNAME,B.FEEDATE," +
							"B.TOTALFEE AS TOTALFEE_TOTAL FROM (SELECT A.YJXH AS FEENO,A.YLXH AS ITEMCODE,C.FYMC AS ITEMNAME," +
							"'' AS SPECIFICATION,A.YLSL AS QUANTITY,A.YLDJ AS PRICE,A.HJJE AS TOTALFEE,A.BZXX AS REMARK " +
							"FROM MS_YJ02 A,MS_YJ01 B,GY_YLSF C " +
							"WHERE A.YJXH=B.YJXH AND A.YLXH=C.FYXH AND B.ZFPB=0 AND (B.DJLY=1 OR B.DJLY=6 OR B.DJLY=8) " +
							"AND A.YJXH=:FEENO AND B.BRID=:PATIENTID AND A.JGID=:JGID) A,(" +
							"SELECT A.YJXH AS FEENO,CASE B.DJLY WHEN 1 THEN 13 WHEN 6 THEN 51 WHEN 8 THEN 31 WHEN 9 THEN 41 END AS FEETYPECODE," +
							"CASE B.DJLY WHEN 1 THEN '诊查费' WHEN 6 THEN '治疗' WHEN 8 THEN '检验' WHEN 9 THEN '检查' END AS FEETYPE," +
							"CASE B.DJLY WHEN 1 THEN '诊查费' WHEN 6 THEN '治疗' WHEN 8 THEN '检验' WHEN 9 THEN '检查' END AS FEENAME," +
							"B.KSDM AS DEPTID,C.KSMC AS DEPTNAME,B.YSDM AS DOCTORID,D.NAME AS DOCTORNAME,B.KDRQ AS FEEDATE," +
							"SUM(A.HJJE) AS TOTALFEE FROM MS_YJ02 A,MS_YJ01 B,GY_KSDM C,BASE_USER D " +
							"WHERE A.YJXH=B.YJXH AND B.KSDM=C.KSDM AND B.YSDM=D.ID AND B.ZFPB=0 AND (B.DJLY=1 OR B.DJLY=6 OR B.DJLY=8) " +
							"AND A.YJXH=:FEENO AND B.BRID=:PATIENTID AND A.JGID=:JGID GROUP BY A.YJXH,B.DJLY,B.KSDM,C.KSMC,B.YSDM,D.NAME,B.KDRQ) B WHERE A.FEENO=B.FEENO";
					break;
				case 41://检查
					hql = "SELECT A.FEENO,A.ITEMCODE,A.ITEMNAME,A.SPECIFICATION,A.QUANTITY,A.PRICE,A.TOTALFEE,A.POSITION," +
							"B.FEETYPECODE,B.FEETYPE,B.FEENAME,B.DEPTID,B.DEPTNAME,B.DOCTORID,B.DOCTORNAME,B.FEEDATE," +
							"B.TOTALFEE AS TOTALFEE_TOTAL FROM (SELECT A.YJXH AS FEENO,A.YLXH AS ITEMCODE,C.FYMC AS ITEMNAME," +
							"'' AS SPECIFICATION,A.YLSL AS QUANTITY,A.YLDJ AS PRICE,A.HJJE AS TOTALFEE,E.BWMC AS POSITION " +
							"FROM MS_YJ02 A,MS_YJ01 B,GY_YLSF C,YJ_JCSQ_KD02 D,YJ_JCSQ_JCBW E " +
							"WHERE A.YJXH=B.YJXH AND A.YLXH=C.FYXH AND A.YJXH=D.SQDH AND D.BWID=E.BWID AND B.ZFPB=0 " +
							"AND B.DJLY=9 AND A.YJXH=:FEENO AND B.BRID=:PATIENTID AND A.JGID=:JGID) A,(" +
							"SELECT A.YJXH AS FEENO,CASE B.DJLY WHEN 1 THEN 13 WHEN 6 THEN 51 WHEN 8 THEN 31 WHEN 9 THEN 41 END AS FEETYPECODE," +
							"CASE B.DJLY WHEN 1 THEN '诊查费' WHEN 6 THEN '治疗' WHEN 8 THEN '检验' WHEN 9 THEN '检查' END AS FEETYPE," +
							"CASE B.DJLY WHEN 1 THEN '诊查费' WHEN 6 THEN '治疗' WHEN 8 THEN '检验' WHEN 9 THEN '检查' END AS FEENAME," +
							"B.KSDM AS DEPTID,C.KSMC AS DEPTNAME,B.YSDM AS DOCTORID,D.NAME AS DOCTORNAME,B.KDRQ AS FEEDATE," +
							"SUM(A.HJJE) AS TOTALFEE FROM MS_YJ02 A,MS_YJ01 B,GY_KSDM C,BASE_USER D " +
							"WHERE A.YJXH=B.YJXH AND B.KSDM=C.KSDM AND B.YSDM=D.ID AND B.ZFPB=0 AND B.DJLY=9 AND A.YJXH=:FEENO AND B.BRID=:PATIENTID AND A.JGID=:JGID " +
							"GROUP BY A.YJXH,B.DJLY,B.KSDM,C.KSMC,B.YSDM,D.NAME,B.KDRQ) B WHERE A.FEENO=B.FEENO";
					break;
				default:
					break;
			}
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("FEENO", feeNo);
			parameters.put("JGID", jgid);
			parameters.put("PATIENTID", patientId);
			BaseDAO dao = new BaseDAO();
			System.out.println("=====开始执行查询sql=====");
			//System.out.println(hql.toString());
			System.out.println("parameters：feeNo="+feeNo+"，jgid="+jgid+"，patientId="+patientId);
			List<Map<String, Object>> listDj = dao.doSqlQuery(hql.toString(),
					parameters);
			System.out.println("=====完成sql查询：共查询到"+listDj.size()+"条费用详细信息=====");
			return genResponseXml_FeeDetail(feeTypeCode,listDj);
		} catch (PersistentDataOperationException e) {
			LoggerFactory
			.getLogger(ZjzfHelper.class).error("load failed.", e);
			return "";
		}
	}
	
	//生成获取收费单据详细列表接口返回xml报文
	private String genResponseXml_FeeDetail(int feeTypeCode,List<Map<String, Object>> listData)
	{
		try {
			StringBuffer resXml = new StringBuffer("");
			if(listData!=null && listData.size()>0){
				System.out.println("=====开始构建返回报文=====");
				resXml.append("<getFeeDetailResponse><resultCode>1</resultCode><resultMessage>成功</resultMessage><feeRecord>");
				for(int i=0;i<listData.size();i++){
					if(i==0){
						resXml.append("<feeName>"+(listData.get(i).get("FEENAME")==null?"":listData.get(i).get("FEENAME").toString())+"</feeName>");
						resXml.append("<feeTypeCode>"+(listData.get(i).get("FEETYPECODE")==null?"":listData.get(i).get("FEETYPECODE").toString())+"</feeTypeCode>");
						resXml.append("<feeType>"+(listData.get(i).get("FEETYPE")==null?"":listData.get(i).get("FEETYPE").toString())+"</feeType>");
						resXml.append("<feeNo>"+(listData.get(i).get("FEENO")==null?"":listData.get(i).get("FEENO").toString())+"</feeNo>");
						resXml.append("<deptId>"+(listData.get(i).get("DEPTID")==null?"":listData.get(i).get("DEPTID").toString())+"</deptId>");
						resXml.append("<deptName>"+(listData.get(i).get("DEPTNAME")==null?"":listData.get(i).get("DEPTNAME").toString())+"</deptName>");
						resXml.append("<doctorId>"+(listData.get(i).get("DOCTORID")==null?"":listData.get(i).get("DOCTORID").toString())+"</doctorId>");
						resXml.append("<doctorName>"+(listData.get(i).get("DOCTORNAME")==null?"":listData.get(i).get("DOCTORNAME").toString())+"</doctorName>");
						resXml.append("<feeDate>"+(listData.get(i).get("FEEDATE")==null?"":listData.get(i).get("FEEDATE").toString())+"</feeDate>");
						resXml.append("<totalFee>"+(listData.get(i).get("TOTALFEE_TOTAL")==null?"":listData.get(i).get("TOTALFEE_TOTAL").toString())+"</totalFee>");
						switch(feeTypeCode){
						case 23://中草药
							resXml.append("<quantity>"+(listData.get(i).get("QUANTITY_TOTAL")==null?"":listData.get(i).get("QUANTITY_TOTAL").toString())+"</quantity>");
							resXml.append("<decoctionDesc>"+(listData.get(i).get("DECOCTIONDESC")==null?"":listData.get(i).get("DECOCTIONDESC").toString())+"</decoctionDesc>");
							resXml.append("<usage>"+(listData.get(i).get("USAGE")==null?"":listData.get(i).get("USAGE").toString())+"</usage>");
							break;
							default:
							break;
						}
						resXml.append("<details>");
					}
					resXml.append("<detail>");
					switch(feeTypeCode){
					case 21://西药
					case 22://成药
					case 23://中草药
						resXml.append("<groupNo>"+(listData.get(i).get("GROUPNO")==null?"":listData.get(i).get("GROUPNO").toString())+"</groupNo>");
						break;
						default:
						break;
					}
					resXml.append("<itemCode>"+(listData.get(i).get("ITEMCODE")==null?"":listData.get(i).get("ITEMCODE").toString())+"</itemCode>");
					resXml.append("<itemName>"+(listData.get(i).get("ITEMNAME")==null?"":listData.get(i).get("ITEMNAME").toString())+"</itemName>");
					resXml.append("<specification>"+(listData.get(i).get("SPECIFICATION")==null?"":listData.get(i).get("SPECIFICATION").toString())+"</specification>");
					resXml.append("<quantity>"+(listData.get(i).get("QUANTITY")==null?"":listData.get(i).get("QUANTITY").toString())+"</quantity>");
					resXml.append("<price>"+(listData.get(i).get("PRICE")==null?"":listData.get(i).get("PRICE").toString())+"</price>");
					resXml.append("<totalFee>"+(listData.get(i).get("TOTALFEE")==null?"":listData.get(i).get("TOTALFEE").toString())+"</totalFee>");
					switch(feeTypeCode){
					case 21://西药
					case 22://成药
						resXml.append("<remark>"+(listData.get(i).get("REMARK")==null?"":listData.get(i).get("REMARK").toString())+"</remark>");
						resXml.append("<dosage>"+(listData.get(i).get("DOSAGE")==null?"":listData.get(i).get("DOSAGE").toString())+"</dosage>");
						resXml.append("<dosageUnit>"+(listData.get(i).get("DOSAGEUNIT")==null?"":listData.get(i).get("DOSAGEUNIT").toString())+"</dosageUnit>");
						resXml.append("<frequencyCode>"+(listData.get(i).get("FREQUENCYCODE")==null?"":listData.get(i).get("FREQUENCYCODE").toString())+"</frequencyCode>");
						resXml.append("<frequency>"+(listData.get(i).get("FREQUENCY")==null?"":listData.get(i).get("FREQUENCY").toString())+"</frequency>");
						resXml.append("<usageCode>"+(listData.get(i).get("USAGECODE")==null?"":listData.get(i).get("USAGECODE").toString())+"</usageCode>");
						resXml.append("<usage>"+(listData.get(i).get("USAGE")==null?"":listData.get(i).get("USAGE").toString())+"</usage>");
						resXml.append("<days>"+(listData.get(i).get("DAYS")==null?"":listData.get(i).get("DAYS").toString())+"</days>");
						break;
					case 23://中草药
						resXml.append("<remark>"+(listData.get(i).get("REMARK")==null?"":listData.get(i).get("REMARK").toString())+"</remark>");
						break;
					case 13://诊查费
					case 51://治疗
					case 31://检验
						resXml.append("<remark>"+(listData.get(i).get("REMARK")==null?"":listData.get(i).get("REMARK").toString())+"</remark>");
						break;
					case 41://检查
						resXml.append("<position>"+(listData.get(i).get("POSITION")==null?"":listData.get(i).get("POSITION").toString())+"</position>");
						resXml.append("<remark>"+(listData.get(i).get("REMARK")==null?"":listData.get(i).get("REMARK").toString())+"</remark>");
						break;
						default:
						break;
					}
					resXml.append("</detail>");
				}
				resXml.append("</details></feeRecord></getFeeDetailResponse>");
				System.out.println("=====完成构建返回报文=====");
			}
			return resXml.toString();
		} catch (Exception e) {
			LoggerFactory
			.getLogger(ZjzfHelper.class).error("load failed.", e);
			System.out.println("=====构建返回报文失败："+e.getMessage()+"=====");
			return "";
		}
	}
	/*****************接口5：获取收费单据详细列表  end***************************************/
	
	/*****************接口6：校验关联业务退费条件  begin***************************************/
	//获取已支付列表xml
	public String CheckRefundConditionXml(String request){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println("=========="+df.format(new Date())+"【开始】 调用HIS接口[APP]：校验关联业务退费条件==========");
		String res = "";
		try {
			List<Map<String, Object>> feelist = new ArrayList<Map<String, Object>>();
			Document doc = DocumentHelper.parseText(getXmlInfo(request));
			Element root = doc.getRootElement();
			String orgId =  root.elementText("orgId");//机构代码
			String patientId =  root.elementText("patientId");//病人标识
			String precalId =  root.elementText("precalId");//预结算标识
			Iterator feerecords = root.elementIterator("feeRecords");
			while (feerecords.hasNext()) {
                Element feerecordEle = (Element) feerecords.next();
                Iterator iter = feerecordEle.elementIterator("feeRecord");
                while (iter.hasNext()) {
        			Map<String, Object> fee =  new HashMap<String, Object>();
                	Element recordEle = (Element) iter.next();
                	String feeTypeCode = recordEle.elementText("feeTypeCode");//费用类别
                	String feeNo = recordEle.elementText("feeNo");//费用标识
                	fee.put("feeTypeCode", feeTypeCode);
                	fee.put("feeNo", feeNo);
                	feelist.add(fee);
                }
			}
			System.out.println("=====成功解析请求报文=====");
			//校验关联业务退费条件
			res = checkRefundCondition(orgId,patientId,precalId,feelist);
		}catch (Exception e) {
			LoggerFactory
			.getLogger(ZjzfHelper.class).error("load failed.", e);
		}
		System.out.println("=========="+df.format(new Date())+"【结束】 调用HIS接口[APP]：校验关联业务退费条件==========");
		System.out.println("");
		return res;
	}
	
	//校验关联业务退费条件
	private String checkRefundCondition(String jgid, String patientId,String precalId_app,List<Map<String, Object>> feelist)
			throws ModelDataOperationException	{
		int error=0;
		String errorMessage = "";//错误信息
		String fphm = "";
		try {
			//获取病人信息
			Map<String, Object> brxx = getBrxx(patientId);
			String errorFeeNos = "";//错误的费用标识，需返回给app
			String precalId = "";//预结算标识：使用@拼接ms_cf01和ms_yj01主键，其中ms_cf01表cfsb从小到大以|符号拼接
			/************自费************/
			if(brxx.get("BRXZ").toString().equals("1000")){
				System.out.println("=====开始执行自费病人费用信息校验业务=====");
				//解析费用清单：包括处方、医技
				if(feelist!=null && feelist.size()>0){
					String precalId_cf = "";
					String precalId_yj = "";
					int feeNo_cf_pre = 0;//上一次 
					int feeNo_yj_pre = 0;//上一次 
					for(int i=0;i<feelist.size();i++){
						//费用类别
						int feeTypeCode = (feelist.get(i).get("feeTypeCode")==null?0:Integer.parseInt(feelist.get(i).get("feeTypeCode").toString()));
						//费用标识
						int feeNo = (feelist.get(i).get("feeNo")==null?0:Integer.parseInt(feelist.get(i).get("feeNo").toString()));
						switch(feeTypeCode){
							case 21://西药
							case 22://成药
							case 23://中草药
								//处方单状态：0 无效状态  1 已执行 2 未执行 3 已退费 4 已作废
								int status_cf=0;
								Map<String,Object> mapStatus_cf = getStatusCf02(feeNo);
								if(mapStatus_cf!=null &&mapStatus_cf.get("FEESTATUS")!=null){
									status_cf = Integer.parseInt(mapStatus_cf.get("FEESTATUS").toString());
									fphm = (mapStatus_cf.get("FPHM")==null?"":mapStatus_cf.get("FPHM").toString());
								}
								if(status_cf!=2){
									errorFeeNos += feeNo+",";
									errorMessage += getMessageByFeeStatus(feeTypeCode,feeNo,status_cf);
								}
								else{
									if(feeNo_cf_pre==0){
										precalId_cf += feeNo+"|";
									}else if(feeNo_cf_pre<feeNo){
										precalId_cf += feeNo+"|";
									}else if(feeNo_cf_pre>feeNo){
										precalId_cf = feeNo+"|"+precalId_cf;
									}
									feeNo_cf_pre = feeNo;
								}
								break;
							case 13://诊查费
							case 51://治疗
							case 31://检验
							case 41://检查
								//医技单状态：0 无效状态  1 已执行 2 未执行 3 已退费 4 已作废
								int status_yj=0;
								Map<String,Object> mapStatus_yj = getStatusYj02(feeNo);
								if(mapStatus_yj!=null &&mapStatus_yj.get("FEESTATUS")!=null){
									status_yj = Integer.parseInt(mapStatus_yj.get("FEESTATUS").toString());
									fphm = (mapStatus_yj.get("FPHM")==null?"":mapStatus_yj.get("FPHM").toString());
								}
								if(status_yj!=2){
									errorFeeNos += feeNo+",";
									errorMessage += getMessageByFeeStatus(feeTypeCode,feeNo,status_yj);
								}
								else{
									if(feeNo_yj_pre==0){
										precalId_yj += feeNo+"|";
									}else if(feeNo_yj_pre<feeNo){
										precalId_yj += feeNo+"|";
									}else if(feeNo_cf_pre>feeNo){
										precalId_yj = feeNo+"|"+precalId_yj;
									}
									feeNo_yj_pre = feeNo;
								}
								break;
							default:
								break;
						}
					}
					if(errorFeeNos.length()==0){
						if(!precalId_cf.equals("")){
							precalId_cf = precalId_cf.substring(0,precalId_cf.length()-1);
						}
						if(!precalId_yj.equals("")){
							precalId_yj = precalId_yj.substring(0,precalId_yj.length()-1);
						}
						if(!precalId_cf.equals("") && !precalId_yj.equals("")){
							precalId = precalId_cf +"@"+precalId_yj;
						}
						else{
							precalId = precalId_cf + precalId_yj;
						}
					}
				}
				System.out.println("=====完成执行自费病人费用信息校验业务=====");
			}
			/************农合************/
			else if(brxx.get("BRXZ").toString().equals("6000")){			
				System.out.println("=====开始执行农合病人费用信息校验业务=====");
				System.out.println("=====完成执行农合病人费用信息校验业务=====");	
			}
			/************医保************/
			else if(brxx.get("BRXZ").toString().equals("2000") || brxx.get("BRXZ").toString().equals("3000")){	
				System.out.println("=====开始执行医保病人费用信息校验业务=====");	
				System.out.println("=====完成执行医保病人费用信息校验业务=====");			
			}
			System.out.println("=====开始构建返回报文=====");	
			//根据请求的单据清单生成预结算标识并与请求的预结算标识进行对比，两个数值一致表示所有费用单均符合退费条件
			if(!precalId.equals(precalId_app) || errorFeeNos.length()>0){
				System.out.println("=====完成构建返回报文：<resultMessage>校验关联业务退费条件失败："+errorMessage+"</resultMessage>=====");
				return "<checkRefundConditionResponse><resultCode>2</resultCode><resultMessage>校验关联业务退费条件失败："+errorMessage+"</resultMessage><resultId>"+fphm+"</resultId><allowRefundFlag>0</allowRefundFlag></checkRefundConditionResponse>";
			}
			System.out.println("=====完成构建返回报文：<resultMessage>成功</resultMessage>=====");
			return "<checkRefundConditionResponse><resultCode>1</resultCode><resultMessage>成功</resultMessage><resultId>"+fphm+"</resultId><allowRefundFlag>1</allowRefundFlag></checkRefundConditionResponse>";
		}catch (Exception e) {
			error=1;
			//logger.error("ValidateException Prescribing a single query to fail.", e);
			throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "校验关联业务退费条件失败");
		}finally {
			if(error==1){
				System.out.println("=====完成构建返回报文：<resultMessage>校验关联业务退费条件失败："+errorMessage+"</resultMessage>=====");
				return "<checkRefundConditionResponse><resultCode>2</resultCode><resultMessage>校验关联业务退费条件失败："+errorMessage+"</resultMessage><resultId>"+fphm+"</resultId><allowRefundFlag>0</allowRefundFlag></checkRefundConditionResponse>";
			}
		}
		
	}
	/*****************接口6：校验关联业务退费条件  end***************************************/
	
	/**
	 * 获取病人信息
	 * @param brid
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getBrxx(String brid)throws ModelDataOperationException{
		try {
			BaseDAO dao = new BaseDAO();
			return dao.doLoad("phis.application.cic.schemas.MS_BRDA", brid);
		} catch (PersistentDataOperationException e) {
			//logger.error("load failed.", e);
			System.out.println("=====获取病人信息："+e.getMessage()+"=====");
			throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "获取病人信息失败");
		}
	}
	
	/**
	 * 获取处方信息
	 * @param cfsb
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getCf01(String cfsb)throws Exception{
		try {
			BaseDAO dao = new BaseDAO();
			return dao.doLoad("phis.application.cic.schemas.MS_CF01", cfsb);
		} catch (PersistentDataOperationException e) {
			//logger.error("load failed.", e);
			return null;
		}
	}
	
	/**
	 * 获取处方信息
	 * @param cfsb
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getCf02(int cfsb)throws ModelDataOperationException{
		try {
			BaseDAO dao = new BaseDAO();
			String hql = "select a.CFSB,SUM(a.HJJE) as ZLJE from MS_CF02 a,MS_CF01 b where b.ZFPB=0 and (b.FPHM is null or b.FPHM = '') and a.CFSB=b.CFSB and a.CFSB=:CFSB group by a.CFSB";
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("CFSB", cfsb);
			return dao.doSqlQuery(hql, parameters);
		} catch (PersistentDataOperationException e) {
			//logger.error("load failed.", e);
			System.out.println("=====获取处方信息："+e.getMessage()+"=====");
			throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "获取处方信息失败");
		}
	}
	
	/**
	 * 获取医技信息
	 * @param yjxh
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getYj01(String yjxh)throws Exception{
		try {
			BaseDAO dao = new BaseDAO();
			return dao.doLoad("phis.application.cic.schemas.MS_YJ01", yjxh);
		} catch (PersistentDataOperationException e) {
			//logger.error("load failed.", e);
			return null;
		}
	}
	
	/**
	 * 获取医技信息
	 * @param yjxh
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getYj02(int yjxh)throws ModelDataOperationException{
		try {
			BaseDAO dao = new BaseDAO();
			String hql = "select a.YJXH,SUM(a.HJJE) as ZLJE from MS_YJ02 a,MS_YJ01 b where b.ZFPB=0 and (b.FPHM is null or b.FPHM = '') and a.YJXH=b.YJXH and a.YJXH=:YJXH group by a.YJXH";
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("YJXH", yjxh);
			return dao.doSqlQuery(hql, parameters);
		} catch (PersistentDataOperationException e) {
			//logger.error("load failed.", e);
			System.out.println("=====获取医技信息："+e.getMessage()+"=====");
			throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "获取医技信息失败");
		}
	}
	
	/**
	 * 获取明细信息
	 * @param yjxh
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getCfAndYjMx(String jgid,String cfsbs,String yjxhs)throws ModelDataOperationException{
		System.out.println("=====开始 获取需结算费用清单的明细信息=====");
		try {
			BaseDAO dao = new BaseDAO();
			String hql = "select a.CFSB,a.FYGB,b.CFLX,case b.CFLX when 1 then 21 when 2 then 22 when 3 then 23 end as DJLY," +
					"case b.CFLX when 1 then '西药' when 2 then '成药' when 3 then '中草药' end as DJLY_TEXT," +
					"B.YFSB,a.HJJE,a.ZFBL from MS_CF02 a,MS_CF01 b where b.ZFPB=0" +
					" and (b.FPHM is null or b.FPHM='') and a.jgid=:JGID and a.CFSB=b.CFSB and a.CFSB in("+cfsbs+")" +
					" UNION ALL " +
					" select a.YJXH as CFSB,a.FYGB,0 as CFLX,case b.DJLY when 1 then 13 when 6 then 51 when 8 then 31 when 9 then 41 end as DJLY," +
					"case b.DJLY when 1 then '诊查费' when 6 then '治疗' when 8 then '检验' when 9 then '检查' end as DJLY_TEXT," +
					"0 as YFSB,a.HJJE,a.ZFBL from MS_YJ02 a,MS_YJ01 b where b.ZFPB=0" +
					" and (b.FPHM is null or b.FPHM='') and a.JGID=:JGID and a.YJXH=b.YJXH and a.YJXH in("+yjxhs+")";
			Map<String, Object> parameters = new HashMap<String, Object>();
			//parameters.put("CFSBS", cfsbs);
			//parameters.put("YJXHS", yjxhs);
			parameters.put("JGID", jgid);
			return dao.doSqlQuery(hql, parameters);
		} catch (PersistentDataOperationException e) {
			//logger.error("load failed.", e);
			System.out.println("=====获取需结算费用清单的明细信息失败："+e.getMessage()+"=====");
			throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "获取明细信息失败");
		}
	}
	
	/**
	 * 获取处方单状态：0 无效状态  1 已执行 2 未执行 3 已退费 4 已作废
	 * @param cfsb
	 * @return
	 * @throws Exception
	 */
	public Map<String,Object> getStatusCf02(int cfsb)throws ModelDataOperationException{
		try {
			BaseDAO dao = new BaseDAO();
			String hql = "SELECT A.FPHM AS FPHM,CASE WHEN (B.ZFPB=0 AND A.FYBZ=1) THEN 1 WHEN (B.ZFPB=1 AND B.THPB=0) THEN 4 WHEN (B.ZFPB=1 AND B.THPB=1) THEN 3 " +
					"WHEN (B.ZFPB=0 AND A.FYBZ=0) THEN 2 ELSE 0 END AS FEESTATUS FROM MS_CF01 A,MS_MZXX B WHERE A.FPHM=B.FPHM AND A.CFSB=:CFSB";
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("CFSB", cfsb);
			return dao.doSqlLoad(hql, parameters);
		} catch (PersistentDataOperationException e) {
			//logger.error("load failed.", e);
			System.out.println("=====获取处方单状态失败："+e.getMessage()+"=====");
			throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "获取处方单状态失败");
		}
	}
	
	/**
	 * 获取医技单状态：0 无效状态  1 已执行 2 未执行 3 已退费 4 已作废
	 * @param yjxh
	 * @return
	 * @throws Exception
	 */
	public Map<String,Object> getStatusYj02(int yjxh)throws ModelDataOperationException{
		try {
			BaseDAO dao = new BaseDAO();
			String hql = "SELECT A.FPHM AS FPHM,CASE WHEN (B.ZFPB=1 AND (A.ZXPB = 1 OR A.ZXKS>0)) THEN 1 WHEN (B.ZFPB=1 AND B.THPB=0) THEN 4 WHEN (B.ZFPB=1 AND B.THPB=1) THEN 3 " +
					"WHEN (B.ZFPB=0 AND (A.ZXPB=0 OR A.ZXKS IS NULL)) THEN 2 ELSE 0 END AS FEESTATUS  FROM MS_YJ01 A,MS_MZXX B WHERE A.FPHM=B.FPHM AND A.YJXH=:YJXH";
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("YJXH", yjxh);
			return dao.doSqlLoad(hql, parameters);
		} catch (PersistentDataOperationException e) {
			//logger.error("load failed.", e);
			System.out.println("=====获取医技单状态失败："+e.getMessage()+"=====");
			throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "获取医技单状态失败");
		}
	}
	
	public long parseLong(Object o) {
		if (o == null) {
			return 0L;
		}
		return Long.parseLong(o + "");
	}
	public double parseDouble(Object o) {
		if (o == null) {
			return new Double(0);
		}
		return Double.parseDouble(o + "");
	}
	
	//状态：0 无效状态  1 已执行 2 未执行 3 已退费 4 已作废
	private String getMessageByFeeStatus(int feeTypeCode,int feeNo,int status) {
		String res = "";
		switch(feeTypeCode){
			case 21://西药
				res = "西药方";
			case 22://成药
				res = "成药方";
			case 23://草药
				res = "草药方";
			case 13://诊查费
				res = "诊查单";
			case 51://治疗
				res = "治疗单";
			case 31://检验
				res = "检验单";
			case 41://检查
				res = "检查单";
				break;
			default:				
				break;
		}
		res += "("+feeNo+")：";
		switch(status){
			case 0:
				res += "无效状态.";
				break;
			case 1:
				res += "已执行.";
				break;
			case 3:
				res += "已退费.";
				break;
			case 4:
				res += "已作废.";
				break;
			default:				
				break;
		}
		return res;
	}
	
	//将APP端支付方式转换为HIS端支付方式
	private String getFffs(String payType) {
		String res ="36";
		if(payType.equals("02")){//支付宝
			res = "36";
		}
		else if(payType.equals("03")){//微信
			res = "35";
		}
		return res;
	}	
	
	//将APP端支付方式转换为聚合支付平台支付方式
	private String getPaytypeForJhpt(String payType) {
		String res ="1";
		if(payType.equals("02")){//支付宝
			res = "1";
		}
		else if(payType.equals("03")){//微信
			res = "2";
		}
		else if(payType.equals("04")){//银联
			res = "3";
		}
		return res;
	}
	
	private String getAppUserid(String jgid) 
			throws ModelDataOperationException	{
		try {
			String userid="";
			String hql = "SELECT B.ID FROM BASE_USERROLES A,BASE_USER B WHERE A.USERID=B.ID AND A.MANAGEUNITID=:JGID " +
					"AND A.ROLEID='PHIS.60' AND LOGOFF=0 AND LOWER(NAME)='app'";
			BaseDAO dao = new BaseDAO();
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("JGID", jgid);
			Map<String,Object> map = dao.doSqlLoad(hql, parameters);
			if(map!=null && map.get("ID")!=null){
				userid = map.get("ID").toString();
			}
			return userid;
		} catch (PersistentDataOperationException e) {
			//logger.error("load failed.", e);
			System.out.println("=====获取指定机构APP收费员id失败："+e.getMessage()+"=====");
			throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "获取指定机构APP收费员id失败");
		}
	}
	
	
	private String doSavePayrecord(Map<String, Object> payrecord,Session ss) 
			throws ModelDataOperationException{
		Map<String, Object> orderrecord = new HashMap<String, Object>();
		String ip ="";
		String computername = "";
		String brxz ="";
		String brxm ="";
		String brxb ="";
		String sfzh ="";
		String csny ="";
		String resXml ="";
		try{
			if(payrecord==null){
				return "";
			}
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");  
			String payTime = sdf.format(date);
			//获取医院app收费设备信息
			DictionaryItem dicApp = DictionaryController.instance().getDic("phis.dictionary.ZjzfSbInfo").getItem(payrecord.get("JGID").toString());
			if(dicApp!=null){
				 ip = dicApp.getProperty("ip").toString();
				 computername =  dicApp.getProperty("computername").toString();
			}
			Map<String, Object> brxx = getBrxx(payrecord.get("BRID").toString());
			if(brxx != null && brxx.get("SFZH")!=null){
				brxz = (brxx.get("BRXZ")==null?"":brxx.get("BRXZ").toString());
				brxm = (brxx.get("BRXM")==null?"":brxx.get("BRXM").toString());
				brxb = (brxx.get("BRXB")==null?"":brxx.get("BRXB").toString());
				sfzh = (brxx.get("SFZH")==null?"":brxx.get("SFZH").toString());
				csny = (brxx.get("CSNY")==null?"":brxx.get("CSNY").toString());
			}
		orderrecord.put("PAYSERVICE", "2");
		orderrecord.put("IP", ip);
		orderrecord.put("ORGANIZATIONCODE",payrecord.get("JGID").toString());
		orderrecord.put("COMPUTERNAME", computername);
		//医院流水号生成规则：SF+yyyyMMddHHmmss+发票号码+APP
		//orderrecord.put("HOSPNO", "SF"+sdf2.format(date)+payrecord.get("VOUCHERNO").toString()+"APP");
		orderrecord.put("HOSPNO", payrecord.get("HOSPNO").toString());
		orderrecord.put("PAYMONEY", Double.parseDouble(payrecord.get("PAYMONEY").toString()));
		//MS_MZXX.put("QTYS",BSPHISUtil.getDouble(BCTCZFJE+BCDBJZZF+BCMZBZZF+BCDBBXZF, 2));
		orderrecord.put("VOUCHERNO", payrecord.get("VOUCHERNO").toString());
		orderrecord.put("PATIENTYPE", brxz);
		orderrecord.put("PATIENTID", payrecord.get("BRID").toString());
		orderrecord.put("NAME", brxm);
		orderrecord.put("SEX", brxb);
		orderrecord.put("IDCARD", sfzh);
		orderrecord.put("BIRTHDAY",BSHISUtil.toDate(csny));
		orderrecord.put("PAYTIME",date);
		//orderrecord.put("VERIFYNO", payrecord.get("VERIFYNO").toString());//需app提供
		orderrecord.put("VERIFYNO", "");//不需要提供，退费时使用HOSPNO
		orderrecord.put("BANKTYPE", "");
		orderrecord.put("BANKCODE", "");
		orderrecord.put("BANKNO", "");
		orderrecord.put("PAYTYPE", payrecord.get("PAYTYPE").toString());//支付类型：1支付宝 2微信 3银联卡
		orderrecord.put("AUTH_CODE", "");
		orderrecord.put("PAYSOURCE", "3");//1、窗口 2、自助机 3、app
		orderrecord.put("TERMINALNO", "");
		orderrecord.put("PAYNO", "");
		orderrecord.put("COLLECTFEESCODE", payrecord.get("COLLECTFEESCODE").toString());//操作员工号
		orderrecord.put("COLLECTFEESNAME", payrecord.get("COLLECTFEESNAME").toString());//操作员姓名
		orderrecord.put("CARDTYPE", "");
		orderrecord.put("CARDNO", "");
		orderrecord.put("STATUS", "1");
		orderrecord.put("RETURN_CODE", "200");
		orderrecord.put("RETURN_MSG", "成功");
		orderrecord.put("TRADENO", "");
		orderrecord.put("TKBZ", "0");
		orderrecord.put("REFUND_FEE", 0);
		orderrecord.put("HOSPNO_ORG", "");
		BaseDAO dao = new BaseDAO();
		Map<String, Object> genValue = dao.doSave("create",
				BSPHISEntryNames.PAYRECORD, orderrecord, false);
		ss.flush();
		//构建APP推送到支付平台的xml
		resXml =  "<payRecord><payService>2</payService><ip>"+ip+"</ip>" +
				"<organizationCode>"+payrecord.get("JGID").toString()+"</organizationCode><computerName>"+computername+"</computerName>" +
				"<hospNo>"+orderrecord.get("HOSPNO").toString()+"</hospNo><paymoney>"+payrecord.get("PAYMONEY").toString()+"</paymoney>" +
				"<patientType>"+brxz+"</patientType><patientId>"+payrecord.get("BRID").toString()+"</patientId><name>"+brxm+"</name>" +
				"<sex>"+brxb+"</sex><idcard>"+sfzh+"</idcard><birthday>"+csny+"</birthday><cardType></cardType><cardNo></cardNo>" +
				"<voucherNO>"+payrecord.get("VOUCHERNO").toString()+"</voucherNO><payType>"+payrecord.get("PAYTYPE").toString()+"</payType>" +
				"<paySource>3</paySource><collectFeesCode>"+payrecord.get("COLLECTFEESCODE").toString()+"</collectFeesCode>" +
				"<collectFeesName>"+payrecord.get("COLLECTFEESNAME").toString()+"</collectFeesName><payTime>"+payTime+"</payTime><bank></bank></payRecord>";
		}catch(Exception e){
			System.out.println("=====保存支付记录失败："+e.getMessage()+"=====");
			throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "保存支付记录失败");
		}
		return resXml;
	}
}


