package phis.application.ccl.source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.application.lis.source.ModelOperationException;
import phis.application.lis.source.PHISBaseModel;
import phis.source.utils.BSPHISUtil;

import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class PHISCheckApplyModel extends PHISBaseModel {
	protected Logger logger = LoggerFactory.getLogger(PHISCheckApplyModel.class);
	protected BaseDAO dao;
	private String unExistNode = "";//记录不存在的字符串节点
	/**
	 * 构造方法
	 * 
	 * @param ctx
	 */
	public PHISCheckApplyModel(Context ctx) {
		super(ctx);
		dao = new BaseDAO(ctx);
	}
	/**
	 * 获取检查申请单信息
	 * @param map
	 * @return
	 * @throws ModelOperationException
	 */
	public Map<String, Object> getCheckApplyInfo(Map<String, Object> map)
			throws ModelOperationException {
		String xml = (String) map.get("xml");
		logger.info(xml);
		Date date = new Date();
		Map<String, Object> res = new HashMap<String, Object>();
		//验证xml是否符合标准
		if(!validateXmlFormat(getCheckApplyInfoStandardList(),xml)){
			logger.error("获取检查申请单信息参数不全！xml消息为："+ unExistNode + " 节点不存在.xml为:"+xml);
			String resxml = "<code>500</code><message>获取检查申请单信息参数不全！xml消息为："+ unExistNode + " 节点不存在</message>";
			res.put("body", resxml);
			return res;
		}
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(xml);
			Element bodyElt = (Element) doc.getRootElement().element("body"); // 获取body节点
			String jgid = bodyElt.elementTextTrim("jgid");// 机构代码
			Integer yllb = Integer.parseInt(bodyElt.elementTextTrim("yllb"));// 医疗类别1.门诊2.住院
			String jclb = bodyElt.elementTextTrim("jclb");//检查类别 1.心电2.放射3.B超
			String mzhm = bodyElt.elementTextTrim("mzhm");// 门诊号码
			String zyhm = bodyElt.elementTextTrim("zyhm");// 住院号码
			String fphm = bodyElt.elementTextTrim("fphm");// 发票号码
			
			StringBuffer hql = new StringBuffer("");
			Map<String,Object> parameters = new HashMap<String,Object>();
			StringBuffer resSbXml = new StringBuffer("<code>200</code><message>success</message><body>");//返回的字符串xml
			List<Map<String,Object>> brxxlist;
			switch(yllb){
			case 1://门诊
				//获得病人相关信息(心电比较特殊 特殊处理)
				hql.append("select '1' as YLLB,b.SQDH as SQDH,c.MZHM as MZHM,'' as ZYHM,'' as BRCH,a.BRXM as BRXM,c.BRXB as BRXB,"
						+ "c.SFZH as SFZH,to_char(c.CSNY,'yyyy-mm-dd') as CSRQ,c.LXDZ as LXDZ,c.JTDH as LXDH,a.YSDM as YSDM,"
						+ "to_char(a.KDRQ,'yyyy-mm-dd hh24:mi:ss') as SQSJ,e.YGXM as YSMC,a.KSDM as KSDM,f.KSMC as KSMC,"
						+ "d.ZDMC as ZD,b.ZSXX as ZSXX,case(b.SSLX) when 1 then (b.CTXX||' 心界:'||b.XJ||',心率:'||b.xl||',"
						+ "心音:'||b.xy||',心律:'||b.xlv||',心力衰竭:'||b.xlsj||',X光检查:'||b.xgjc) "
						+ "ELSE  b.CTXX END as CTXX,b.SYXX as SYXX,b.BZXX as BZXX from MS_YJ01 a,YJ_JCSQ_KD01 b,MS_BRDA c,"
						+ "MS_BRZD d,GY_YGDM e,GY_KSDM f where a.YJXH = b.SQDH and b.YLLB=:YLLB and a.BRID=c.BRID and "
						+ "a.JZXH = d.JZXH and a.YSDM=e.YGDM and a.KSDM = f.KSDM and d.ZZBZ=1 and a.ZXPB=0 and a.ZFPB=0 "
						+ "and b.SSLX=:SSLX and b.djzt=0 and a.JGID like :JGID || '%' and a.FPHM is not null");
				parameters.put("YLLB", yllb);
				parameters.put("SSLX", jclb);
				parameters.put("JGID", jgid);//站点的病人中心可以看到
				if(!"".equals(fphm)){//若有发票，先根据发票号码查询
					parameters.put("FPHM", fphm);
					hql.append(" and a.FPHM=:FPHM ");
					brxxlist = dao.doSqlQuery(hql.toString(), parameters);
					if(brxxlist.size()==0){
						String resxml = "<code>300</code><message>病人检查信息不存在</message>";
						res.put("body", resxml);
						return res;
					}
					resSbXml = getCheckApplyDetailsInfo(yllb,brxxlist,resSbXml,dao);//拼接字符串返回
				}else if(!"".equals(mzhm)){//没有发票号码，根据门诊号码查询
					parameters.put("MZHM", mzhm);
					hql.append(" and c.MZHM=:MZHM ");
					brxxlist = dao.doSqlQuery(hql.toString(), parameters);
					if(brxxlist.size()==0){
						String resxml = "<code>300</code><message>病人检查信息不存在</message>";
						res.put("body", resxml);
						return res;
					}
					resSbXml = getCheckApplyDetailsInfo(yllb,brxxlist,resSbXml,dao);//拼接字符串返回
				}else{
//					String resxml = "<code>400</code><message>查询的该门诊检查单，传入的发票号码和门诊号码均为空，查询失败</message>";
//					res.put("body", resxml);
//					return res;
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					String startdate = sdf.format(date)+" 00:00:00";
					String enddate = sdf.format(date)+" 23:59:59";
					parameters.put("STARTDATE", startdate);
					parameters.put("ENDDATE", enddate);
					hql.append(" and a.KDRQ>=to_date(:STARTDATE,'yyyy-mm-dd hh24:mi:ss') and a.KDRQ<=to_date(:ENDDATE,'yyyy-mm-dd hh24:mi:ss')");
					brxxlist = dao.doSqlQuery(hql.toString(), parameters);
					resSbXml = getCheckApplyDetailsInfo(yllb,brxxlist,resSbXml,dao);//拼接字符串返回
				}
				break;
			case 2://住院(心电比较特殊 特殊处理)  and b.LSBZ=1 and b.QRSJ is not null 2016-03-07 zyf
				hql.append("select distinct '2' as YLLB,c.SQDH as SQDH,'' as MZHM,a.ZYHM as ZYHM,a.BRCH as BRCH,a.BRXM as BRXM,a.BRXB as BRXB,"
						+ "a.SFZH as SFZH,to_char(a.CSNY,'yyyy-mm-dd') as CSRQ,a.LXDZ as LXDZ,a.JTDH as LXDH,b.CZGH as YSDM,d.YGXM as YSMC,"
						+ "to_char(b.KSSJ,'yyyy-mm-dd hh24:mi:ss') as SQSJ,b.SRKS as KSDM,e.KSMC as KSMC,a.RYZD as ZD,"
						+ "c.ZSXX as ZSXX,case(c.SSLX) when 1 then (c.CTXX||' 心界:'||c.XJ||',心率:'||c.xl||',"
						+ "心音:'||c.xy||',心律:'||c.xlv||',心力衰竭:'||c.xlsj||',X光检查:'||c.xgjc) "
						+ "ELSE  c.CTXX END as CTXX,c.SYXX as SYXX,c.BZXX as BZXX from ZY_BRRY a,ZY_BQYZ b,YJ_JCSQ_KD01 c,"
						+ "GY_YGDM d,GY_KSDM e where a.ZYH=b.ZYH and b.YZZH = c.SQDH and b.CZGH=d.YGDM and b.SRKS = e.KSDM and b.JFBZ=9 "
						+ "and c.YLLB=:YLLB  and b.ZFBZ=0 and b.LSBZ=1 and c.SSLX=:SSLX  and c.djzt=0 and b.JGID=:JGID");
				parameters.put("YLLB", yllb);
				parameters.put("SSLX", jclb);
				parameters.put("JGID", jgid);
				if(!"".equals(mzhm)){
					parameters.put("MZHM", mzhm);
					hql.append(" and a.MZHM=:MZHM ");
					brxxlist = dao.doSqlQuery(hql.toString(), parameters);
					if(brxxlist.size()==0){
						String resxml = "<code>300</code><message>病人未登记检查信息不存在</message>";
						res.put("body", resxml);
						return res;
					}
					resSbXml = getCheckApplyDetailsInfo(yllb,brxxlist,resSbXml,dao);//拼接字符串返回
				}else if(!"".equals(zyhm)){
					parameters.put("ZYHM", zyhm);
					hql.append(" and a.ZYHM=:ZYHM and a.JGID=:JGID ");
					brxxlist = dao.doSqlQuery(hql.toString(), parameters);
					if(brxxlist.size()==0){
						String resxml = "<code>300</code><message>病人未登记检查信息不存在</message>";
						res.put("body", resxml);
						return res;
					}
					resSbXml = getCheckApplyDetailsInfo(yllb,brxxlist,resSbXml,dao);//拼接字符串返回
				}else{
//					String resxml = "<code>400</code><message>查询的该住院检查单，传入的门诊号码和住院号码均为空，查询失败</message>";
//					res.put("body", resxml);
//					return res;
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					String startdate = sdf.format(date)+" 00:00:00";
					String enddate = sdf.format(date)+" 23:59:59";
					parameters.put("STARTDATE", startdate);
					parameters.put("ENDDATE", enddate);
					hql.append(" and b.KSSJ>=to_date(:STARTDATE,'yyyy-mm-dd hh24:mi:ss') and b.KSSJ<=to_date(:ENDDATE,'yyyy-mm-dd hh24:mi:ss')");
					brxxlist = dao.doSqlQuery(hql.toString(), parameters);
					resSbXml = getCheckApplyDetailsInfo(yllb,brxxlist,resSbXml,dao);//拼接字符串返回
				}
				break;
			}
			//以下为成功
			resSbXml.append("</body>");
			res.put("body", resSbXml.toString());
			return res;
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new ModelOperationException("获取检查申请单信息失败", e);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelOperationException("获取检查申请单信息失败", e);
		} catch (Exception e){
			e.printStackTrace();
			ss.getTransaction().rollback();
			throw new ModelOperationException("获取检查申请单信息失败", e);
		}
		
	}
	/**
	 * 取消执行检查申请单
	 * @param map
	 * @return
	 * @throws ModelOperationException
	 */
	public Map<String, Object> cancelCheckApplyExecution(Map<String, Object> map)
			throws ModelOperationException {
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		ss.beginTransaction();
		String xml = (String) map.get("xml");
		logger.info(xml);
		Map<String, Object> res = new HashMap<String, Object>();
		//验证xml是否符合标准
		if(!validateXmlFormat(cancelCheckApplyExecutionStandardList(),xml)){
			logger.error("取消执行检查申请单参数不全！xml消息为："+ unExistNode + " 节点不存在.xml为:"+xml);
			String resxml = "<code>500</code><message>取消执行检查申请单参数不全！xml消息为："+ unExistNode + " 节点不存在</message>";
			res.put("body", resxml);
			return res;
		}
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(xml);
			Element bodyElt = (Element) doc.getRootElement().element("body"); // 获取body节点
			String jgid = bodyElt.elementTextTrim("jgid");// 机构代码
			String sqid = bodyElt.elementTextTrim("sqid");// 申请单号
			Integer yllb = Integer.parseInt(bodyElt.elementTextTrim("yllb"));// 医疗类别1.门诊2.住院
			String jclb = bodyElt.elementTextTrim("jclb");//检查类别 1.心电2.放射3.B超
			String mzhm = bodyElt.elementTextTrim("mzhm");// 门诊号码
			String zyhm = bodyElt.elementTextTrim("zyhm");// 住院号码
			String brxm = bodyElt.elementTextTrim("brxm");// 病人姓名
			StringBuffer hql = new StringBuffer("");
			Map<String,Object> parameters = new HashMap<String,Object>();
			long count = 0;
			switch(yllb){
			case 1://门诊
				hql.append("select a.BRXM as BRXM,b.SQDH as SQDH,b.SSLX as SSLX from MS_YJ01 a,YJ_JCSQ_KD01 b "
						+ "where a.YJXH=b.SQDH and a.ZXPB=1 and b.SQDH=:SQDH and b.SSLX=:SSLX and a.BRXM=:BRXM "
						+ "and b.YLLB=:YLLB");
				parameters.put("SQDH", sqid);
				parameters.put("SSLX", jclb);
				parameters.put("BRXM", brxm);
				parameters.put("YLLB", yllb);
				count = dao.doSqlQuery(hql.toString(), parameters).size();
				if(count==0){
					logger.error("取消执行失败:不存在检查类别:"+jclb+",申请单号:"+sqid+",病人姓名:"+brxm+"的已执行门诊申请单.xml为:"+xml);
					String resxml = "<code>400</code><message>取消执行失败:不存在检查类别:"+jclb+",申请单号:"+sqid+",病人姓名:"+brxm+"的已执行门诊申请单</message>";
					res.put("body", resxml);
					return res;
				}
				hql.setLength(0);
				parameters.clear();
				hql.append("update MS_YJ01 set ZXPB=0 where YJXH=:YJXH");
				parameters.put("YJXH", Long.parseLong(sqid));
				dao.doUpdate(hql.toString(), parameters);
				break;
				
			case 2://住院
				hql.append("select distinct c.BRXM as BRXM,b.SQDH as SQDH,b.SSLX as SSLX from ZY_BQYZ a,YJ_JCSQ_KD01 b, "
						+ "ZY_BRRY c where a.YZZH=b.SQDH and a.ZYH=c.ZYH and a.LSBZ=1 and b.SQDH=:SQDH "
						+ "and b.SSLX=:SSLX and c.BRXM=:BRXM and b.YLLB=:YLLB");
				parameters.put("SQDH", sqid);
				parameters.put("SSLX", jclb);
				parameters.put("BRXM", brxm);
				parameters.put("YLLB", yllb);
				count = dao.doSqlQuery(hql.toString(), parameters).size();
				if(count==0){
					logger.error("取消执行失败:不存在检查类别:"+jclb+",申请单号:"+sqid+",病人姓名:"+brxm+"的已执行住院申请单.xml为:"+xml);
					String resxml = "<code>400</code><message>取消执行失败:不存在检查类别:"+jclb+",申请单号:"+sqid+",病人姓名:"+brxm+"的已执行住院申请单</message>";
					res.put("body", resxml);
					return res;
				}
				hql.setLength(0);
				parameters.clear();
				//更新状态为未执行
				hql.append("update ZY_BQYZ set LSBZ=0,QRSJ=null where YZZH=:YZZH and JFBZ=9");
				parameters.put("YZZH", Long.parseLong(sqid));
				dao.doUpdate(hql.toString(), parameters);
//				//获取原有计费明细，插入负记录
//				hql.setLength(0);
//				hql.append("select JLXH as JLXH from ZY_BQYZ  where YZZH=:YZZH");
//				List<Map<String,Object>> list = dao.doSqlQuery(hql.toString(), parameters);
//				for(Map<String,Object> fymxMap : list){
//					parameters.clear();
//					hql.setLength(0);
//					hql.append("select JGID as JGID,ZYH as ZYH,FYXH as FYXH,FYMC as FYMC,YPCD as YPCD,"
//							+ "-FYSL as FYSL,-FYDJ as FYDJ,-ZJJE as ZJJE,-ZFJE as ZFJE,YSGH as YSGH,SRGH as SRGH,"
//							+ "QRGH as QRGH,FYBQ as FYBQ,FYKS as FYKS,ZXKS as ZXKS,XMLX as XMLX,"
//							+ "YPLX as YPLX,FYXM as FYXM,JSCS as JSCS,ZFBL as ZFBL,YZXH as YZXH,ZLJE as ZLJE,ZLXZ as ZLXZ,"
//							+ "YEPB as YEPB,DZBL as DZBL from ZY_FYMX where YZXH=:JLXH");
//					parameters.put("JLXH", fymxMap.get("JLXH")+"");
//					Map<String,Object> fymxNewMap = dao.doSqlQuery(hql.toString(), parameters).get(0);
//					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//					fymxNewMap.put("FYRQ", sdf.parse(sdf.format(new Date())));
//					fymxNewMap.put("JFRQ", sdf.parse(sdf.format(new Date())));
//					dao.doSave("create", BSPHISEntryNames.ZY_FYMX, fymxNewMap, false);
//				}
				break;
			}
			res.put("body", "<code>200</code><message>success</message>");
			ss.getTransaction().commit();
		} catch (DocumentException e) {
			e.printStackTrace();
			ss.getTransaction().rollback();
			throw new ModelOperationException("取消执行检查申请单失败", e);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			ss.getTransaction().rollback();
			throw new ModelOperationException("取消执行检查申请单失败", e);
		} catch (RuntimeException e){
			e.printStackTrace();
			ss.getTransaction().rollback();
			throw new ModelOperationException("取消执行检查申请单失败", e);
		}
//		catch (ValidateException e) {
//			e.printStackTrace();
//			ss.getTransaction().rollback();
//			throw new ModelOperationException("取消执行检查申请单失败", e);
//		} catch (ParseException e) {
//			e.printStackTrace();
//			ss.getTransaction().rollback();
//			throw new ModelOperationException("取消执行检查申请单失败", e);
//		}
		catch (Exception e){
			e.printStackTrace();
			ss.getTransaction().rollback();
			throw new ModelOperationException("取消执行检查申请单失败", e);
		}
		
		
		return res;
	}
	/**
	 * 登记检查申请单
	 * @param map
	 * @return
	 * @throws ModelOperationException
	 */
	public Map<String, Object> registerCheckApply(Map<String, Object> map)
			throws ModelOperationException {
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		ss.beginTransaction();
		String xml = (String) map.get("xml");
		logger.info(xml);
		Map<String, Object> res = new HashMap<String, Object>();
		//验证xml是否符合标准
		if(!validateXmlFormat(registerCheckApplyStandardList(),xml)){
			logger.error("登记检查申请单参数不全！xml消息为："+ unExistNode + " 节点不存在.xml为:"+xml);
			String resxml = "<code>500</code><message>登记检查申请单参数不全！xml消息为："+ unExistNode + " 节点不存在</message>";
			res.put("body", resxml);
			return res;
		}
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(xml);
			Element bodyElt = (Element) doc.getRootElement().element("body"); // 获取body节点
			String jgid = bodyElt.elementTextTrim("jgid");// 机构代码
			String sqid = bodyElt.elementTextTrim("sqid");// 申请单号
			Integer yllb = Integer.parseInt(bodyElt.elementTextTrim("yllb"));// 医疗类别1.门诊2.住院
			String jclb = bodyElt.elementTextTrim("jclb");//检查类别 1.心电2.放射3.B超
			String brxm = bodyElt.elementTextTrim("brxm");// 病人姓名
			Integer djbz = Integer.parseInt(bodyElt.elementTextTrim("djbz"));//1.登记 2.取消登记
			Integer djzt = djbz==1?0:1;//登记则获取未登记记录，取消登记则获取已登记记录
			StringBuffer hql = new StringBuffer("");
			Map<String,Object> parameters = new HashMap<String,Object>();
			long count = 0;
			switch(yllb){
			case 1://门诊
				hql.append("select a.BRXM as BRXM,b.SQDH as SQDH,b.SSLX as SSLX from MS_YJ01 a,YJ_JCSQ_KD01 b "
						+ "where a.YJXH=b.SQDH and a.ZXPB=0 and b.SQDH=:SQDH and b.SSLX=:SSLX and a.BRXM=:BRXM "
						+ "and b.YLLB=:YLLB and b.DJZT=:DJZT and a.JGID like :JGID || '%'");
				parameters.put("SQDH", sqid);
				parameters.put("SSLX", jclb);
				parameters.put("BRXM", brxm);
				parameters.put("YLLB", yllb);
				parameters.put("DJZT", djzt);
				parameters.put("JGID", jgid);
				count = dao.doSqlQuery(hql.toString(), parameters).size();
				if(count==0){
					logger.error("登记失败:不存在检查类别:"+jclb+",申请单号:"+sqid+",病人姓名:"+brxm+"的未登记/已登记门诊申请单.xml为:"+xml);
					String resxml = "<code>400</code><message>登记失败:不存在检查类别:"+jclb+",申请单号:"+sqid+",病人姓名:"+brxm+"的未登记/已登记门诊申请单</message>";
					res.put("body", resxml);
					return res;
				}
				break;
				
			case 2://住院
				hql.append("select distinct c.BRXM as BRXM,b.SQDH as SQDH,b.SSLX as SSLX from ZY_BQYZ a,YJ_JCSQ_KD01 b, "
						+ "ZY_BRRY c where a.YZZH=b.SQDH and a.ZYH=c.ZYH and a.LSBZ=0 and b.SQDH=:SQDH "
						+ "and b.SSLX=:SSLX and c.BRXM=:BRXM and b.YLLB=:YLLB and b.DJZT=:DJZT and a.JGID=:JGID");
				parameters.put("SQDH", sqid);
				parameters.put("SSLX", jclb);
				parameters.put("BRXM", brxm);
				parameters.put("YLLB", yllb);
				parameters.put("DJZT", djzt);
				parameters.put("JGID", jgid);
				count = dao.doSqlQuery(hql.toString(), parameters).size();
				if(count==0){
					logger.error("登记失败:不存在检查类别:"+jclb+",申请单号:"+sqid+",病人姓名:"+brxm+"的未登记/已登记住院申请单.xml为:"+xml);
					String resxml = "<code>400</code><message>登记失败:不存在检查类别:"+jclb+",申请单号:"+sqid+",病人姓名:"+brxm+"的未登记/已登记住院申请单</message>";
					res.put("body", resxml);
					return res;
				}
				break;
			}
			hql.setLength(0);
			parameters.clear();
			hql.append("update YJ_JCSQ_KD01 set DJZT=:DJZT where SQDH=:SQDH and YLLB=:YLLB");
			parameters.put("SQDH", Long.parseLong(sqid));
			parameters.put("YLLB", yllb);
			parameters.put("DJZT", djbz==1?1:0);//登记，则改为1，取消，则改为0 
			dao.doUpdate(hql.toString(), parameters);
			res.put("body", "<code>200</code><message>success</message>");
			ss.getTransaction().commit();
		} catch (DocumentException e) {
			e.printStackTrace();
			ss.getTransaction().rollback();
			throw new ModelOperationException("登记检查申请单失败", e);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			ss.getTransaction().rollback();
			throw new ModelOperationException("登记检查申请单失败", e);
		} catch (Exception e){
			e.printStackTrace();
			ss.getTransaction().rollback();
			throw new ModelOperationException("登记检查申请单失败", e);
		}
		
		return res;
	}
	
	
	
	/**
	 * 回写检查报告
	 * @param map
	 * @return
	 * @throws ModelOperationException
	 */
	public Map<String, Object> receiveCheckApplyReport(Map<String, Object> map)
			throws ModelOperationException {
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		ss.beginTransaction();
		String xml = (String) map.get("xml");
		logger.info(xml);
		Map<String, Object> res = new HashMap<String, Object>();
		//验证xml是否符合标准
		if(!validateXmlFormat(receiveCheckApplyReportStandardList(),xml)){
			logger.error("回写检查报告参数不全！xml消息为："+ unExistNode + " 节点不存在.xml为:"+xml);
			String resxml = "<code>500</code><message>回写检查报告参数不全！xml消息为："+ unExistNode + " 节点不存在</message>";
			res.put("body", resxml);
			return res;
		}
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(xml);
			Element bodyElt = (Element) doc.getRootElement().element("body"); // 获取body节点
			String jgid = bodyElt.elementTextTrim("jgid");// 机构代码
			String sqid = bodyElt.elementTextTrim("sqid");// 申请单号
			String yllb = bodyElt.elementTextTrim("yllb");// 医疗类别1.门诊2.住院
			String jclb = bodyElt.elementTextTrim("jclb");//检查类别 1.心电2.放射3.B超
			String brxm = bodyElt.elementTextTrim("brxm");// 病人姓名
			String mzhm = bodyElt.elementTextTrim("mzhm");// 门诊号码
			String zyhm = bodyElt.elementTextTrim("zyhm");// 住院号码
			String zxysdm = bodyElt.elementTextTrim("zxysdm");// 执行医生代码
			String zxysmc = "".equals(bodyElt.elementTextTrim("zxysmc"))?"未知":bodyElt.elementTextTrim("zxysmc");// 执行医生名称
			Date zxsj = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss").parse(bodyElt.elementTextTrim("zxsj"));// 执行日期
			String zxksdm = bodyElt.elementTextTrim("zxksdm");// 执行科室代码
			String zxksmc = "".equals(bodyElt.elementTextTrim("zxksmc"))?"未知":bodyElt.elementTextTrim("zxksmc");// 执行科室名称
			String zd = "".equals(bodyElt.elementTextTrim("zd"))?"未传输":bodyElt.elementTextTrim("zd");// 临床诊断或入院诊断
			String bgzd = bodyElt.elementTextTrim("bgzd");// 报告诊断
			String bgms = bodyElt.elementTextTrim("bgms");// 报告描述
			String jgjy = bodyElt.elementTextTrim("jgjy");// 结果建议
			String twdz = bodyElt.elementTextTrim("twdz");// 图文地址
			Integer brid = 0;// 病人id
			Integer zyh = 0;// 住院号
			Integer brxz = 0;// 病人性质,用于住院费用计算用
			boolean needPay = false;//判断是否已计费，若病区已计费（执行过），则不再计
			Map<String, Object> parameters = new HashMap<String, Object>();
			Map<String, Object> brxxMap = new HashMap<String,Object>();
			/*************************************** 根据病人门诊号码或住院号码获得病人brid等关键信息**********************************************/
			switch(Integer.parseInt(yllb)){
			case 1://门诊
				if(!"".equals(mzhm)){
					parameters.put("MZHM", mzhm);
					brxxMap = dao.doLoad("select BRID as BRID,MZHM as MZHM from MS_BRDA where MZHM=:MZHM", parameters);
				}else{
					logger.error("检查报告申请单号为:"+sqid+"的门诊检查报告的门诊号码为空,回传失败.xml为:"+xml);
					String resxml = "<code>400</code><message>检查报告申请单号为:"+sqid+"的门诊检查报告的门诊号码为空,回传失败</message>";
					res.put("body", resxml);
					return res;
				}
				break;
			case 2://住院
				if(!"".equals(mzhm)){
					parameters.put("MZHM", mzhm);
					parameters.put("JGID", jgid);
					brxxMap = dao.doLoad("select BRID as BRID,BRXZ as BRXZ,ZYH as ZYH,MZHM as MZHM from ZY_BRRY where"
							+ " MZHM=:MZHM and CYPB=0 and JGID=:JGID", parameters);
				}else if(!"".equals(zyhm)){
					parameters.put("ZYHM", zyhm);
					parameters.put("JGID", jgid);
					brxxMap = dao.doLoad("select BRID as BRID,BRXZ as BRXZ,ZYH as ZYH,MZHM as MZHM from ZY_BRRY where"
							+ " ZYHM=:ZYHM and CYPB=0 and JGID=:JGID", parameters);
					
				}else{
					logger.error("检查报告申请单号为:"+sqid+"的住院检查报告的门诊号码和住院号码都为空,回传失败.xml为:"+xml);
					String resxml = "<code>400</code><message>检查报告申请单号为:"+sqid+"的住院检查报告的门诊号码和住院号码都为空,回传失败</message>";
					res.put("body", resxml);
					return res;
				}
				break;
			}
			if(brxxMap==null){
				logger.error("查找病人信息失败，请确认门诊号码或住院号码无误.xml为:"+xml);
				String resxml = "<code>400</code><message>查找病人信息失败，请确认门诊号码或住院号码无误</message>";
				res.put("body", resxml);
				return res;
			}
			mzhm = brxxMap.get("MZHM")==null?"0":brxxMap.get("MZHM") + "";
			brid = brxxMap.get("BRID")==null?0:Integer.parseInt(brxxMap.get("BRID") + "");
			zyh = brxxMap.get("ZYH")==null?0:Integer.parseInt(brxxMap.get("ZYH") + "");
			brxz = brxxMap.get("BRXZ")==null?0:Integer.parseInt(brxxMap.get("BRXZ") + "");
			/*************************************************************************************************************************/
			//查找数据库YJ_JCSQ_BGMX表，若已存在对应的申请单号，则更新，若不存在，则插入
			parameters.clear();
			parameters.put("SQDH", Long.parseLong(sqid));
			parameters.put("YLLB", yllb);
			long count = dao.doCount(BSPHISEntryNames.YJ_JCSQ_BGMX,"SQDH=:SQDH and YLLB=:YLLB", parameters);
			String state = "";
			if (count == 0) {
				state = "create";
			} else if (count > 0) {
				state = "update";
			}
			//根据是否已存在，选择插入或更新检查报告
			if ("".equals(state) || "create".equals(state)) {
				// 获取数据插入yj_bgmx表
				Map<String, Object> Insertparameters = new HashMap<String, Object>();
				Insertparameters.put("SQDH", sqid);
				Insertparameters.put("YLLB", yllb);
				Insertparameters.put("JCLB", jclb);// 根据检查类别存1.心电2.放射3.B超
				Insertparameters.put("JGID", jgid);
				Insertparameters.put("BRID", brid);
				Insertparameters.put("MZHM", mzhm);
				Insertparameters.put("ZYH", zyh);
				Insertparameters.put("YSDM", zxysdm);
				Insertparameters.put("YSMC", zxysmc);
				Insertparameters.put("ZXSJ", zxsj);
				Insertparameters.put("KSDM", zxksdm);
				Insertparameters.put("KSMC", zxksmc);
				Insertparameters.put("LCZD", zd);
				Insertparameters.put("BGZD", bgzd);
				Insertparameters.put("BGMS", bgms);
				Insertparameters.put("JGJY", jgjy);
				Insertparameters.put("TWDZ",twdz);
				dao.doInsert(BSPHISEntryNames.YJ_JCSQ_BGMX, Insertparameters, false);
				if ("1".equals(yllb)) {// 若为门诊，则改变医技单执行状态
					StringBuffer updateZXPBHql = new StringBuffer("");
					updateZXPBHql.append("update MS_YJ01 set ZXPB=1,ZXRQ=:ZXRQ,ZXKS=:ZXKS,ZXYS=:ZXYS where YJXH=:YJXH");
					Map<String, Object> mzParameters = new HashMap<String, Object>();
					mzParameters.put("YJXH", Long.parseLong(sqid));
					mzParameters.put("ZXRQ", new Date());
					mzParameters.put("ZXKS", Long.parseLong(zxksdm+""));
					mzParameters.put("ZXYS", zxysdm);
					dao.doUpdate(updateZXPBHql.toString(), mzParameters);
				} else if ("2".equals(yllb)) {// 若为住院，则改变临时医嘱状态，并插入收费记录
					/** 改变临时医嘱状态 **//*
					StringBuffer updateZXPBHql = new StringBuffer("");
					updateZXPBHql.append("update ZY_BQYZ set QRSJ=:QRSJ,LSBZ=1 where YZZH=:YJXH");
					Map<String, Object> zyParameters = new HashMap<String, Object>();
					zyParameters.put("YJXH", Long.parseLong(sqid));
					zyParameters.put("QRSJ", new Date());
					dao.doUpdate(updateZXPBHql.toString(), zyParameters);
					zyParameters.clear();
					zyParameters.put("YJXH",sqid);
					List<Map<String,Object>> list = dao.doSqlQuery("select JLXH as JLXH from ZY_BQYZ where YZZH=:YJXH", zyParameters);
					for(Map<String,Object> fymxMap : list){
						zyParameters.clear();
						zyParameters.put("JLXH", Long.parseLong(fymxMap.get("JLXH")+""));
						long fymxCount = dao.doCount(BSPHISEntryNames.ZY_FYMX, "YZXH=:JLXH", zyParameters);
						if (fymxCount==0) {
							//未计费
							needPay = true;
							break;
						} 
					}
					if(needPay){//做判断是防止若医生在病区执行了，重复计费
						*//** 插入收费记录 **//*
						zyParameters.clear();
						zyParameters.put("YZZH", sqid);
						// 根据医嘱组号获得医嘱信息
						StringBuffer getBqyzListHql = new StringBuffer("select JLXH as JLXH,YPXH as YPXH,YZMC as YZMC,YPDJ as YPDJ,YCSL as YCSL,");
						getBqyzListHql.append("XMLX as XMLX,YSGH as YSGH,CZGH as CZGH,BRKS as BRKS,BRBQ as BRBQ,to_char(KSSJ,'yyyy-mm-dd hh24:mi:ss') as KSSJ,YZZH as YZZH from ");
						getBqyzListHql.append("ZY_BQYZ where YZZH=:YZZH");
						List<Map<String, Object>> bqyzList = dao.doSqlQuery(getBqyzListHql.toString(), zyParameters);
						Map<String, Object> fymxMap = new HashMap<String, Object>();
						for (Map<String, Object> bqyzMap : bqyzList) {
							fymxMap.clear();
							double ycsl = Double.parseDouble(bqyzMap.get("YCSL")+ "");// 数量
							double fydj = Double.parseDouble(bqyzMap.get("YPDJ")+ "");// 单价
							long fyxh = Long.parseLong(bqyzMap.get("YPXH") + "");// 费用序号
							*//** 获得费用归并，以便后面获得自负比例 **//*
							long fygb = BSPHISUtil.getfygb(0, fyxh, dao, ctx);// 费用归并,0表示医疗费用
							*//************************//*
							fymxMap.put("JGID", jgid);
							fymxMap.put("ZYH", zyh);
							fymxMap.put("FYRQ",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(bqyzMap.get("KSSJ") + ""));
							fymxMap.put("FYXH", fyxh);
							fymxMap.put("FYMC", bqyzMap.get("YZMC") + "");// 医嘱名称
							fymxMap.put("YPCD", 0);
							fymxMap.put("FYSL", ycsl);
							*//******* 获得该病人的自负比例及相关金额 *****//*
							Map<String,Object> zfblMap = BSPHISUtil.getje(0, brxz, fyxh, fygb, fydj, ycsl, dao, ctx);//获取金额
							double zfbl = Double.parseDouble(zfblMap.get("ZFBL")+ "");
							double zfje = Double.parseDouble(zfblMap.get("ZFJE")+ "");
							double zlje = Double.parseDouble(zfblMap.get("ZLJE")+ "");
							double zjje = Double.parseDouble(zfblMap.get("ZJJE")+ "");
							*//************************//*
							fymxMap.put("FYDJ", fydj);
							fymxMap.put("ZJJE", zjje);
							fymxMap.put("ZFJE", zfje);
							fymxMap.put("FYXM", fygb);
							fymxMap.put("YSGH", bqyzMap.get("YSGH") + "");
							fymxMap.put("SRGH", bqyzMap.get("CZGH") + "");
							fymxMap.put("QRGH", bqyzMap.get("CZGH") + "");
							fymxMap.put("FYBQ",Long.parseLong(bqyzMap.get("BRBQ") + ""));
							fymxMap.put("FYKS",Long.parseLong(bqyzMap.get("BRKS") + ""));
							fymxMap.put("ZXKS",Long.parseLong(bqyzMap.get("BRKS") + ""));
							fymxMap.put("JFRQ", new Date());
							fymxMap.put("XMLX", 6);// 检查申请记账
							fymxMap.put("YPLX", 0);
							fymxMap.put("JSCS", 0);
							fymxMap.put("ZFBL", zfbl);
							fymxMap.put("YZXH", Long.parseLong(bqyzMap.get("JLXH") + ""));
							fymxMap.put("ZLJE", zlje);
							fymxMap.put("ZLXZ", 0);
							fymxMap.put("TEPB", 0);
							fymxMap.put("DZBL", 0);
							dao.doSave("create", BSPHISEntryNames.ZY_FYMX,fymxMap, false);
						}
					}*/
				}
			} else {
				StringBuffer updateBgmxHql = new StringBuffer("");
				updateBgmxHql.append("update YJ_JCSQ_BGMX set BGZD=:BGZD,BGMS=:BGMS where SQDH=:SQDH and YLLB=:YLLB");
				Map<String, Object> updateBgmsParameters = new HashMap<String, Object>();
				updateBgmsParameters.put("BGZD", bgzd);
				updateBgmsParameters.put("BGMS", bgms);
				updateBgmsParameters.put("SQDH", Long.parseLong(sqid));
				updateBgmsParameters.put("YLLB", yllb);
				dao.doUpdate(updateBgmxHql.toString(), updateBgmsParameters);
			}
			res.put("body", "<code>200</code><message>success</message>");
			ss.getTransaction().commit();
		} catch (DocumentException e) {
			e.printStackTrace();
			ss.getTransaction().rollback();
			throw new ModelOperationException("回写检查报告失败", e);
		} catch (ParseException e) {
			e.printStackTrace();
			ss.getTransaction().rollback();
			throw new ModelOperationException("回写检查报告失败", e);
		} catch(RuntimeException e){
			e.printStackTrace();
			ss.getTransaction().rollback();
			throw new ModelOperationException("回写检查报告失败", e);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			ss.getTransaction().rollback();
			throw new ModelOperationException("回写检查报告失败", e);
		} catch (ValidateException e) {
			e.printStackTrace();
			ss.getTransaction().rollback();
			throw new ModelOperationException("回写检查报告失败", e);
		}
//		catch (ModelDataOperationException e) {
//			e.printStackTrace();
//			ss.getTransaction().rollback();
//			throw new ModelOperationException("回写检查报告失败", e);
//		}
		catch (Exception e){
			e.printStackTrace();
			ss.getTransaction().rollback();
			throw new ModelOperationException("回写检查报告失败", e);
		}
		
		
		return res;
	}
	/**
	 * 获取检查申请信息的xml标准
	 * @return
	 */
	private List<String> getCheckApplyInfoStandardList(){
		List<String> list = new ArrayList<String>();
		list.add("body");
		list.add("jgid");
		list.add("yllb");
		list.add("jclb");
		list.add("mzhm");
		list.add("zyhm");
		list.add("fphm");
		return list;
	}
	/**
	 * 获取取消检查执行的xml标准
	 * @return
	 */
	private List<String> cancelCheckApplyExecutionStandardList(){
		List<String> list = new ArrayList<String>();
		list.add("body");
		list.add("jgid");
		list.add("sqid");
		list.add("yllb");
		list.add("jclb");
		list.add("mzhm");
		list.add("zyhm");
		list.add("brxm");
		return list;
	}
	/**
	 * 获取登记检查单的xml标准
	 * @return
	 */
	private List<String> registerCheckApplyStandardList(){
		List<String> list = new ArrayList<String>();
		list.add("body");
		list.add("jgid");
		list.add("sqid");
		list.add("yllb");
		list.add("jclb");
		list.add("djbz");
		list.add("brxm");
		return list;
	}
	/**
	 * 回写检查报告的xml标准
	 * @return
	 */
	private List<String> receiveCheckApplyReportStandardList(){
		List<String> list = new ArrayList<String>();
		list.add("body");
		list.add("jgid");
		list.add("sqid");
		list.add("yllb");
		list.add("brxm");
		list.add("mzhm");
		list.add("zyhm");
		list.add("zxysdm");
		list.add("zxysmc");
		list.add("zxsj");
		list.add("zxksdm");
		list.add("zxksmc");
		list.add("zd");
		list.add("bgzd");
		list.add("bgms");
		list.add("jgjy");
		list.add("twdz");
		return list;
	}
	/**
	 * 验证xml是否符合标准
	 * @param list
	 * @return
	 */
	private boolean validateXmlFormat(List<String> list,String xml){
		for(String s : list){
			if(xml.indexOf("<"+s+">")==-1||xml.indexOf("</"+s+">")==-1){
				unExistNode = s;//记录
				return false;
			}
		}
		return true;
	}
	/**
	 * map转化xml(非通用方法)
	 * @param map
	 * @return
	 */
	private String mapToXml(Map<String,Object> map){
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
		    sb.append("<"+entry.getKey().toLowerCase()+">"+(entry.getValue()==null?"":entry.getValue())+"</"+entry.getKey().toLowerCase()+">");
		}
		return sb.toString();
	}
	/**
	 * 获取检查申请明细信息，并拼接字符串返回
	 * @param yllb
	 * @param list
	 * @param sb
	 * @param dao
	 * @return
	 * @throws NumberFormatException
	 * @throws PersistentDataOperationException
	 */
	private StringBuffer getCheckApplyDetailsInfo(Integer yllb,List<Map<String,Object>> list,StringBuffer sb,BaseDAO dao) throws NumberFormatException, PersistentDataOperationException{
		Map<String,Object> parameters = new HashMap<String,Object>();
		StringBuffer hql = new StringBuffer("");
		StringBuffer jcbwSb = new StringBuffer("");
		StringBuffer jcxmSb = new StringBuffer("");
		StringBuffer jclbSb = new StringBuffer("");
		for(Map<String,Object> brxxMap : list){
			sb.append("<sqdxx>");//包裹一个申请单的信息
			hql.setLength(0);//清空，效率较高
			jclbSb.setLength(0);
			jcbwSb.setLength(0);
			jcxmSb.setLength(0);
			parameters.clear();
			String sqdh = brxxMap.get("SQDH")+"";//申请单号
			/*********************************获得检查信息*********************************************/
			parameters.put("SQDH", sqdh);
			parameters.put("YLLB", yllb);
			hql.append("select b.LBMC as LBMC,c.BWMC as BWMC,d.XMMC as XMMC from YJ_JCSQ_KD02 a,YJ_JCSQ_JCLB b,"
					+ "YJ_JCSQ_JCBW c,YJ_JCSQ_JCXM d where a.LBID=b.LBID and a.BWID=c.BWID and a.XMID=d.XMID and"
					+ " a.SQDH = :SQDH and a.YLLB =:YLLB");
			List<Map<String,Object>> jcxxList = dao.doSqlQuery(hql.toString(), parameters);
			//获得检查项目信息，并拼接
			for(int i=0;i<jcxxList.size();i++){
				Map<String,Object> jcxxMap = jcxxList.get(i);
				if(i>0){
					jcbwSb.append(",");
					jcxmSb.append(",");
				}else{
					jclbSb.append(jcxxMap.get("LBMC")+"");
				}
				jcbwSb.append(jcxxMap.get("BWMC")+"");
				jcxmSb.append(jcxxMap.get("XMMC")+"");
			}
			/***************************************************************************************/
			/*********************************获得总费用信息*********************************************/
			parameters.remove("YLLB");
			hql.setLength(0);
			if(yllb==1){//门诊
				hql.append("select sum(HJJE) as JCFY from MS_YJ02  where YJXH=:SQDH");
			}else{//住院
				hql.append("select sum(YCSL*YPDJ) as JCFY from ZY_BQYZ  where YZZH=:SQDH");
			}
			String jcfy = String.format("%.2f", Double.parseDouble(dao.doSqlQuery(hql.toString(), parameters).get(0).get("JCFY")+""));
			/***************************************************************************************/
			sb.append(mapToXml(brxxMap));
			sb.append("<jclx>"+jclbSb.toString()+"</jclx><jcbw>"+jcbwSb.toString()+"</jcbw><jcxm>"+jcxmSb.toString()+"</jcxm>");
			sb.append("<jcfy>"+jcfy+"</jcfy>");
			sb.append("</sqdxx>");
		}
		return sb;
	}
}
