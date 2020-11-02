package phis.application.yb.source;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bsoft.pix.protocol.Request;

import phis.application.mds.source.MedicineCommonModel;
import phis.application.mds.source.MedicineUtils;
import phis.application.pix.source.EmpiModel;
import phis.application.pix.source.EmpiUtil;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.ServiceCode;
import phis.source.utils.CNDHelper;
import phis.source.utils.ParameterUtil;
import phis.source.utils.SchemaUtil;
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.net.rpc.config.ServiceConfig;
import ctd.resource.ResourceCenter;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;
import ctd.validator.ValidateException;

public class YBModel extends AbstractActionService{
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(YBModel.class);

	public YBModel(BaseDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * 读取莱斯xml门诊挂号信息
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws ParseException 
	 */
	@SuppressWarnings("unchecked")
	public void doQueryMzghXmlByPath(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException, ParseException {
		//创建SAX解析器对象 
	    SAXReader reader = new SAXReader(); 
	    //读取XML文件 
	    Document document;
		try {
			document = reader.read(new File("C:\\njyb\\mzghxx.xml"));
		    //获取根元素 
		    Element rootElement = document.getRootElement();
		    Element record;   
		    for (Iterator<Element> i = rootElement.elementIterator("RECORD"); i.hasNext();) {
		    	record = (Element) i.next();
		    	String xm_ls = record.elementText("XM");
		    	if(!"".equals(xm_ls)){
				    ele2map(res, record);
		    		break;
		    	}
		     }
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	/**
	 * 查询医保挂号信息
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryYbghxx(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException, ParseException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = (String) user.getManageUnitId();// 用户的机构ID
		Map<String, Object> datas = (Map<String, Object>) req.get("body");
		Map<String, Object> para = new HashMap<String, Object>();
		para.put("JGID",manaUnitId);
		para.put("SBXH", Long.parseLong(datas.get("GHGL")+""));
		para.put("BRXZ",Long.parseLong(datas.get("BRXZ")+""));
		try {
			List<Map<String, Object>> ybghxxList = dao
					.doQuery("select TBR as TBR,XM as XM,KSM as KSM,KSMC as KSMC,BZM as BZM,BZMC as BZMC from YB_GHXX where JGID=:JGID and SBXH=:SBXH and BRXZ=:BRXZ",
							para);
			if(ybghxxList.size()>0){
				Map<String, Object> ybghxx = ybghxxList.get(0);
				res.put("BZM", ybghxx.get("BZM"));
				res.put("KSM", ybghxx.get("KSM"));
			}
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 保存门诊就诊文件和药费、项目收费明细数据文件
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws ParseException
	 * @throws ControllerException 
	 */
	@SuppressWarnings("unchecked")
	public void doSaveMzjzxxAndCfsh(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException, ParseException, ControllerException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = (String) user.getManageUnitId();// 用户的机构ID
		List<Map<String, Object>> datas = (List<Map<String, Object>>) req
				.get("body");
		Map<String, Object> mzxx = (Map<String, Object>) req.get("MZXX");
		Map<String,Object> ybghMap = new HashMap<String, Object>();//医保挂号
		Map<String, Object> para = new HashMap<String, Object>();
		para.put("JGID",manaUnitId);
		para.put("SBXH", Long.parseLong(mzxx.get("GHGL")+""));
		List<Map<String, Object>> mzgh;
		try {
			mzgh = dao.doQuery(
					"select JGID as JGID,SBXH as SBXH,BRXZ as BRXZ,YSDM as YSDM,JZYS as JZYS from MS_GHMX" +
					" where JGID=:JGID and SBXH=:SBXH and BRXZ=3000", para);
			if(mzgh.size()>0){
				Map<String, Object> MS_GHMX = mzgh.get(0);
				//如果病人性质是医保，保存门诊就诊情况数据文件
				if(MS_GHMX.get("BRXZ").equals("3000")){
					para.clear();
					para.put("JGID",MS_GHMX.get("JGID")+"");
					para.put("BRXZ",Long.parseLong(MS_GHMX.get("BRXZ")+""));
					para.put("SBXH",Long.parseLong(MS_GHMX.get("SBXH")+""));
					long ybgh = dao.doCount("YB_GHXX", "BRXZ=:BRXZ and JGID=:JGID and SBXH=:SBXH",
							para);
					if(ybgh>0){
						List<Map<String, Object>> ybghxxList = dao
								.doQuery("select TBR as TBR,XM as XM,KSM as KSM,KSMC as KSMC,BZM as BZM,BZMC as BZMC from YB_GHXX where JGID=:JGID and SBXH=:SBXH and BRXZ=:BRXZ",
										para);
						List<Map<String,Object>> mapList = new ArrayList<Map<String,Object>>();
						//以下是保存门诊就诊情况数据文件
						if(ybghxxList.size()>0){
							//查询医保人员编号
							para.clear();
							para.put("PERSONID", mzxx.get("YSDM")+"");
							List<Map<String,Object>> ybdmList = dao.doQuery("select YBDM as YBDM from SYS_Personnel" +
									" where PERSONID=:PERSONID and YBDM is not null", para);
							String ybdm = mzxx.get("YSDM")+"";
							if(ybdmList.size()>0){
								ybdm = ybdmList.get(0).get("YBDM")+"";
							}
							Map<String, Object> ybghxx = ybghxxList.get(0);
							ybghMap = new HashMap<String, Object>();
							ybghMap.put("TBR", ybghxx.get("TBR"));
							ybghMap.put("XM", ybghxx.get("XM"));
//							ybghMap.put("YSM", mzxx.get("YSDM"));//059007、Y0007
							ybghMap.put("YSM", ybdm);
							ybghMap.put("YSXM", mzxx.get("YSMC"));
							ybghMap.put("BZBM", mzxx.get("YBBZ"));//病种编码
							ybghMap.put("KSM", ybghxx.get("KSM"));
							ybghMap.put("KSMC", ybghxx.get("KSMC"));
							mapList.add(ybghMap);
						}
						//以下是保存门诊药费、项目收费明细数据文件
						if(datas.size()>0){
							List<Map<String,Object>> ybMapList = new ArrayList<Map<String,Object>>();
							boolean hasAllZbm = true;
							for (int i = 0; i < datas.size(); i++) {
								Map<String,Object> ybMap = new HashMap<String, Object>();
								Map<String, Object> data = datas.get(i);
								para.clear();
								List<Map<String, Object>> listDJ = new ArrayList<Map<String, Object>>();
								String zbmjgid="";
								Dictionary njjb=DictionaryController.instance().get("phis.dictionary.NJJB");
								zbmjgid=njjb.getItem(manaUnitId).getProperty("zbmjgid")+"";
								para.put("JGID",zbmjgid);
								para.put("YPXH",Long.parseLong(data.get("YPXH")+""));
								List<Map<String, Object>> ybypList = new ArrayList<Map<String,Object>>();
								if ("0".equals(data.get("CFLX") + "")) {//项目
									ybypList = dao.doQuery("select YYZBM as YYZBM from GY_YLMX" +
											" where JGID=:JGID and FYXH=:YPXH", para);
									ybMap.put("BZ", 1);//标志:非空,0-药品，1-治疗费
								}else{//药品
									para.put("YPCD",Long.parseLong(data.get("YPCD")+""));
									ybypList = dao.doQuery("select YYZBM as YYZBM from YK_CDXX" +
											" where JGID=:JGID and YPXH=:YPXH and YPCD=:YPCD", para);
									ybMap.put("BZ", 0);//标志:非空,0-药品，1-治疗费
								}
								if(ybypList.size()>0){
									Map<String, Object> ybyp = ybypList.get(0);
									if((ybyp.get("YYZBM")==null || "".equals(ybyp.get("YYZBM")+"")) && Double.parseDouble(data.get("YPDJ")+"")>0.0){//自编码为空
										hasAllZbm = false;
									}
									if((ybyp.get("YYZBM")==null || "".equals(ybyp.get("YYZBM")+"")) && Double.parseDouble(data.get("YPDJ")+"")==0.0){
										continue;
									}
									ybMap.put("TBR", ybghMap.get("TBR"));//社会保障号
									ybMap.put("XM", ybghMap.get("XM"));//姓名
									ybMap.put("ZBM", ybyp.get("YYZBM"));//药品/项目自编码
									if("3".equals(data.get("CFLX")+"")){
										ybMap.put("SL", Long.parseLong(data.get("CFTS")+"") * Double.parseDouble(data.get("YPSL")+""));//中药数量
									}else{
										ybMap.put("SL", data.get("YPSL"));//数量
									}
									ybMap.put("DJ", data.get("YPDJ"));//单价
									ybMap.put("YHLB", 0);//优惠类别
									ybMap.put("YHJ", 0);//优惠价
									ybMapList.add(ybMap);
								}
							}
							if(!hasAllZbm){
								throw new ModelDataOperationException("有收费项目未维护自编码，请仔细核对！");
							}else{
								try {
									byte[] bs = callMapListToXML(ybMapList);
									res.put("xmlBytes", bs);
									res.put("xmlStr",  new String(bs,"GBK"));

									byte[] mzjzxx = callMapListToXML(mapList);
									res.put("xmlStr2",  new String(mzjzxx,"GBK"));
								} catch (UnsupportedEncodingException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					}else{
						throw new ModelDataOperationException("未查到该病人医保门诊挂号信息！");
					}
				}
			}else{
				throw new ModelDataOperationException("未查到门诊挂号信息！");
			}
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * 查询医保系统门诊结算信息
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryMzjsByPath(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException, ParseException {
		String mzjsPath = req.get("mzjsPath") + "";
		String xm = req.get("xm") + "";
		if(mzjsPath==null || "".equals(mzjsPath)){
			return;
		}
		if(xm==null || "".equals(xm)){
			return;
		}
		//创建SAX解析器对象 
	    SAXReader reader = new SAXReader(); 
	    //读取XML文件 
	    Document document;
		try {
//			document = reader.read(new File("C:\\njyb\\mzjshz.xml"));
			document = reader.read(new File(mzjsPath));
		    //获取根元素 
		    Element rootElement = document.getRootElement();
		    Element record;   
		    for (Iterator<Element> i = rootElement.elementIterator("RECORD"); i.hasNext();) {
		    	record = (Element) i.next();
		    	String xm_ls = record.elementText("XM");
		    	if(xm_ls.equals(xm)){
		    		ele2map(res, record);
		    	}
		     }
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	/**
	 * 保存门诊医保结算信息
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveMzjshz(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> mzxx = (Map<String, Object>) req.get("MZXX");
		Map<String, Object> mzhmMap = (Map<String, Object>) req.get("MZXH");
		Map<String, Object> fileMap = (Map<String, Object>) req.get("FILEDATA");
		Long mzxh = Long.parseLong(mzhmMap.get("MZXH")+"");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		String xm = mzxx.get("BRXM")+"";
//		//创建SAX解析器对象 
//	    SAXReader reader = new SAXReader(); 
//	    //读取XML文件 
//	    Document document;
		try {
//			document = reader.read(new File("C:\\njyb\\mzjshz.xml"));
//		    //获取根元素 
//		    Element rootElement = document.getRootElement();
//		    Element record;
//		    Map<String, Object> fileMap = new HashMap<String, Object>();
//		    for (Iterator<Element> i = rootElement.elementIterator("RECORD"); i.hasNext();) {
//		    	record = (Element) i.next();
//		    	String xm_ls = record.elementText("XM");
//		    	if(xm_ls.equals(xm)){
//		    		ele2map(fileMap, record);
//		    	}
//		     }
		    Map<String, Object> parameters = new HashMap<String, Object>();
		    if(fileMap!=null){
		    	//根据BRID查询门诊挂号信息
		    	parameters.put("JGID", mzxx.get("JGID"));
		    	parameters.put("BRID", Long.parseLong(mzxx.get("BRID")+""));
		    	List<Map<String, Object>> sbxhMapList = dao
						.doQuery("SELECT SBXH as SBXH FROM MS_GHMX WHERE JGID =:JGID AND BRID =:BRID order by GHSJ desc",
								parameters);
		    	if(sbxhMapList.size()>0){
		    		Map<String, Object> sbxhMap = sbxhMapList.get(0);
		    		long sbxh = Long.parseLong(sbxhMap.get("SBXH")+"");
		    		//根据识别序号查询TBR
		    		parameters.clear();
					parameters.put("JGID", mzxx.get("JGID"));
					parameters.put("SBXH", sbxh);
					Map<String, Object> ybghMap = dao
							.doLoad("SELECT TBR as TBR,CZGH as CZGH FROM YB_GHXX WHERE JGID =:JGID AND SBXH =:SBXH",
									parameters);
					if(ybghMap!=null){
						String tbr = ybghMap.get("TBR")+"";
						String czgh = ybghMap.get("CZGH")+"";
						parameters.clear();
						parameters.put("BRXZ", Long.parseLong(mzxx.get("BRXZ")+""));
						parameters.put("JGID", mzxx.get("JGID"));
						parameters.put("MZXH", mzxx.get("MZXH"));
						long ybgh = dao.doCount("YB_YBMX", "BRXZ=:BRXZ and JGID=:JGID and MZXH=:MZXH",
								parameters);
						String op = ybgh>0 ? "update" : "create";
						Map<String, Object> ybjsMap = new HashMap<String, Object>();
						ybjsMap.put("JGID", mzxx.get("JGID"));
						ybjsMap.put("JSSJ", sdf.parse(sdf.format(new Date())));
						ybjsMap.put("MZXH", mzxh);
						ybjsMap.put("BRXZ", mzxx.get("BRXZ"));
						ybjsMap.put("TBR", tbr);
						ybjsMap.put("XM", fileMap.get("XM"));
						ybjsMap.put("RYXZ", fileMap.get("RYXZ"));
						ybjsMap.put("ZFY", fileMap.get("ZFY"));
						ybjsMap.put("YF", fileMap.get("YF"));
						ybjsMap.put("XMF", fileMap.get("XMF"));
						ybjsMap.put("GRZL", fileMap.get("GRZL"));
						ybjsMap.put("GRZF", fileMap.get("GRZF"));
						ybjsMap.put("YBZF", fileMap.get("YBZF"));
						ybjsMap.put("ZHZF", fileMap.get("ZHZF"));
						ybjsMap.put("ZHYE", fileMap.get("ZHYE"));
						ybjsMap.put("XJZF", fileMap.get("XJZF"));
						ybjsMap.put("FYLB", fileMap.get("FYLB"));
						ybjsMap.put("DJH", fileMap.get("DJH"));
						ybjsMap.put("XZMC", fileMap.get("XZMC"));
						ybjsMap.put("YH1", fileMap.get("YH1"));
						ybjsMap.put("YH2", fileMap.get("YH2"));
						ybjsMap.put("YH3", fileMap.get("YH3"));
						ybjsMap.put("TCZF", fileMap.get("TCZF"));
						ybjsMap.put("DBZF", fileMap.get("DBZF"));
						ybjsMap.put("BZZF", fileMap.get("BZZF"));
						ybjsMap.put("CZGH", czgh);
						ybjsMap.put("JZRQ", null);
						ybjsMap.put("HZRQ", null);
						ybjsMap.put("ZFPB", 0);
						ybjsMap.put("QCZH", Double.parseDouble(fileMap.get("ZHZF")+"")+Double.parseDouble(fileMap.get("ZHYE")+""));
						ybjsMap.put("YBJE", 0.00);
						ybjsMap.put("GHGL", null);
						ybjsMap.put("MZBZ", fileMap.get("MZBZ"));
						ybjsMap.put("QHMC", fileMap.get("QHMC"));
						ybjsMap.put("MZJZ", 0.00);
						dao.doSave(op, BSPHISEntryNames.YB_YBMX, ybjsMap,
								false);
					}
		    	}
				
			}
		} catch (ValidateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	/**
	 * 读取莱斯xml住院登记信息
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws ParseException 
	 */
	@SuppressWarnings("unchecked")
	public void doQueryXmlByPath(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException, ParseException {
//		String zyh = req.get("zyh") + "";
//		if(zyh==null || "".equals(zyh)){
//			return;
//		}
		//创建SAX解析器对象 
	    SAXReader reader = new SAXReader(); 
	    //读取XML文件 
	    Document document;
		try {
			document = reader.read(new File("C:\\njyb\\zydjxx.xml"));
		    //获取根元素 
		    Element rootElement = document.getRootElement();
		    Element record;   
		    for (Iterator<Element> i = rootElement.elementIterator("RECORD"); i.hasNext();) {
		    	record = (Element) i.next();
//		    	String zyh_ls = record.elementText("ZYH");
//		    	if(zyh.equals(zyh_ls)){
				    ele2map(res, record);
//				    //插入住院序号
//				    record.element("XH").setText(zyh);
//		    		break;
//		    	}
		     }   
		    
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	/**
	 * 查询住院费用病人列表
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryZyBrry(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException, ParseException {
		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = (String) user.getManageUnitId();// 用户的机构ID
		String queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = (String) req.get("cnd");
		}
		Long al_zyh = 0L;
		Long al_bq = 0L;
//		Long al_scbz = 0L;
//		String ldt_starDate = null;
//		String ldt_endDate = null;
		try {
			if (queryCnd!=null && queryCnd.indexOf("#") > -1) {
				String[] strs = queryCnd.split("#");
//				ldt_starDate = sdfdate.format(strs[1]);
//				ldt_endDate = sdfdate.format(strs[2]);
				if(strs.length>0){
					al_bq = Long.parseLong("".equals(strs[0])?"0":strs[0]);
				}
				if(strs.length>1){
					al_zyh = Long.parseLong("".equals(strs[1])?"0":strs[1]);
				}
//				if(strs.length>2){
//					al_scbz = Long.parseLong("".equals(strs[2])?"0":strs[2]);
//				}
			}
			//根据日期查询住院费用明细ZY_FYMX
			Map<String, Object> parameters = new HashMap<String, Object>();
			List<Long> zyhs = new ArrayList<Long>();
			StringBuffer sql = new StringBuffer("select distinct ZY_FYMX.ZYH as ZYH,ZY_BRRY.SBHM as SBHM,ZY_BRRY.YBKH as YBKH,ZY_CWSZ.BRCH as BRCH,ZY_BRRY.BRXM as BRXM from ZY_BRRY ZY_BRRY,ZY_FYMX ZY_FYMX,ZY_CWSZ ZY_CWSZ where ZY_BRRY.JGID = ZY_FYMX.JGID and ZY_BRRY.ZYH = ZY_FYMX.ZYH and ZY_BRRY.JGID = ZY_CWSZ.JGID and ZY_BRRY.ZYH = ZY_CWSZ.ZYH and ZY_FYMX.JGID = '"+manaUnitId+"'");
//			if (ldt_starDate != null && !"".equals(ldt_starDate)) {
//				sql.append(" and ZY_FYMX.FYRQ >= to_date('"+ldt_starDate+" 00:00:00','yyyy-MM-dd HH24:mi:ss')");
//			}
//			if (ldt_endDate != null && !"".equals(ldt_endDate)) {
//				sql.append(" and ZY_FYMX.FYRQ < to_date('"+ldt_endDate+" 23:59:59','yyyy-MM-dd HH24:mi:ss')");
//			}
//			if (ldt_starDate != null && !"".equals(ldt_starDate)) {
//				sql.append(" and ZY_FYMX.FYRQ >= to_date(:startDate,'yyyy-mm-dd HH24:mi:ss')");
//				parameters.put("startDate", ldt_starDate);
//			}
//			if (ldt_endDate != null && !"".equals(ldt_endDate)) {
//				sql.append(" and ZY_FYMX.FYRQ < to_date(:endDate,'yyyy-mm-dd HH24:mi:ss')");
//				parameters.put("endDate", ldt_endDate);
//			}

			if (al_zyh != 0L) {
				sql.append(" and (ZY_FYMX.ZYH=:ZYH or ZY_FYMX.ZYH=0)");
				parameters.put("ZYH", al_zyh);
			}else{
				if (req.containsKey("ZYHS")) {
					sql.append(" and ZY_FYMX.ZYH in(:zyhs)");
					List<Object> l = (List<Object>) req.get("ZYHS");
					for (Object o : l) {
						zyhs.add(MedicineUtils.parseLong(o));
					}
					parameters.put("zyhs", zyhs);
				}
			}
			if (al_bq != 0L) {
				sql.append(" and ZY_BRRY.BRBQ=:BRBQ");
				parameters.put("BRBQ", al_bq);
			}else{
				Long brbq = 0L;//病区
				if (user.getProperty("wardId") != null
						&& user.getProperty("wardId") != "") {
					brbq = Long.parseLong((String) user.getProperty("wardId"));
					sql.append(" and ZY_BRRY.BRBQ=:BRBQ");
					parameters.put("BRBQ", brbq);
				}
			}
			sql.append(" order by ZY_FYMX.ZYH");
			List<Map<String,Object>> brlist = dao.doQuery(sql.toString(),
					parameters);
			res.put("body", brlist);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 查询住院费用明细
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws ParseException
	 */
	public void doGetZyfymxQuery(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException, ParseException {
		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = (String) user.getManageUnitId();// 用户的机构ID
		String queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = (String) req.get("cnd");
		}
		Long al_zyh = 0L;
		Long al_bq = 0L;
		Long al_scbz = 0L;
		String ldt_starDate = null;
		String ldt_endDate = null;
		try {
			if (queryCnd!=null && queryCnd.indexOf("#") > -1) {
				String[] strs = queryCnd.split("#");
				if(strs.length>0){
					al_bq = Long.parseLong("".equals(strs[0])?"0":strs[0]);
				}
				if(strs.length>1){
					al_zyh = Long.parseLong("".equals(strs[1])?"0":strs[1]);
				}
				if(strs.length>2){
					al_scbz = Long.parseLong("".equals(strs[2])?"0":strs[2]);
				}
				if(strs.length>3){
					ldt_starDate = "".equals(strs[3])?null:strs[3];
				}
				if(strs.length>4){
					ldt_endDate = "".equals(strs[4])?null:strs[4];
				}
			}
			Map<String, Object> parameters = new HashMap<String, Object>();
			List<Long> zyhs = new ArrayList<Long>();
			StringBuffer sql = new StringBuffer("select ZY_FYMX.JLXH as JLXH,ZY_FYMX.ZYH as ZYH,ZY_FYMX.FYRQ as FYRQ,ZY_FYMX.FYMC as FYMC,ZY_FYMX.YPCD as YPCD,ZY_FYMX.FYSL as FYSL,ZY_FYMX.FYDJ as FYDJ,ZY_FYMX.ZJJE as ZJJE,ZY_FYMX.FYKS as FYKS,ZY_FYMX.SRGH as SRGH from ZY_BRRY ZY_BRRY,ZY_FYMX ZY_FYMX where ZY_BRRY.JGID = ZY_FYMX.JGID and ZY_BRRY.ZYH = ZY_FYMX.ZYH and ZY_FYMX.JGID = '"+manaUnitId+"'");
			
			if(al_scbz == 0L){
				sql.append(" and (ZY_FYMX.SCBZ = 0 or ZY_FYMX.SCBZ is null)");
			}else{//重新上传
				if (ldt_starDate != null && !"".equals(ldt_starDate)) {
					sql.append(" and ZY_FYMX.FYRQ >= to_date(:startDate,'yyyy-mm-dd HH24:mi:ss')");
					parameters.put("startDate", ldt_starDate);
				}
				if (ldt_endDate != null && !"".equals(ldt_endDate)) {
					sql.append(" and ZY_FYMX.FYRQ < to_date(:endDate,'yyyy-mm-dd HH24:mi:ss')");
					parameters.put("endDate", ldt_endDate);
				}
			}
			if (al_zyh != 0L) {
				sql.append(" and (ZY_FYMX.ZYH=:ZYH or ZY_FYMX.ZYH=0)");
				parameters.put("ZYH", al_zyh);
			}else{
				if (req.containsKey("ZYHS")) {
					sql.append(" and ZY_FYMX.ZYH in(:ZYHS)");
					List<Object> l = (List<Object>) req.get("ZYHS");
					for (Object o : l) {
						zyhs.add(MedicineUtils.parseLong(o));
					}
					parameters.put("ZYHS", zyhs);
				}
			}
			if (al_bq != 0L) {
				sql.append(" and ZY_BRRY.BRBQ=:BRBQ");
				parameters.put("BRBQ", al_bq);
			}else{
				Long brbq = 0L;//病区
				if (user.getProperty("wardId") != null
						&& user.getProperty("wardId") != "") {
					brbq = Long.parseLong((String) user.getProperty("wardId"));
					sql.append(" and ZY_BRRY.BRBQ=:BRBQ");
					parameters.put("BRBQ", brbq);
				}
			}
			sql.append(" order by ZY_FYMX.ZYH");
			List<Map<String,Object>> brfylist = dao.doQuery(sql.toString(),
					parameters);
			brfylist= phis.source.utils.SchemaUtil.setDictionaryMassageForList(brfylist,
					ZY_FYMX_YB) ;
			res.put("body", brfylist);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 保存住院药品、治疗费明细数据文件
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws ParseException
	 * @throws ControllerException 
	 */
	@SuppressWarnings("unchecked")
	public void doSaveZyfymx(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException, ParseException, UnknownHostException, ControllerException {
		Map<String, Object> fyxx = (Map<String, Object>) req.get("body");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = (String) user.getManageUnitId();// 用户的机构ID
		Map<String,Object> ybfyMap = new HashMap<String, Object>();//医保住院费用
		List<Object> jls = new ArrayList<Object>();
		List<Long> jlxhs = new ArrayList<Long>();
		List<Object> zyhso = new ArrayList<Object>();
		List<Long> zyhs = new ArrayList<Long>();
		if (fyxx.containsKey("JLXHS")) {
			jls = (List<Object>) fyxx.get("JLXHS");
			if(jls.size()>0){
				for (Object o : jls) {
					jlxhs.add(MedicineUtils.parseLong(o));
				}
			}
		}
		if(jlxhs.size()==0){
			throw new ModelDataOperationException("请选择待上传的费用明细！");
		}
		Map<String, Object> para = new HashMap<String, Object>();
		para.put("JGID", manaUnitId);
//		para.put("JLXH", jlxhs);
		List<Map<String, Object>> zybrryList;//住院病人入院
		List<Map<String, Object>> zyfymxList;//住院费用明细
		if (fyxx.containsKey("ZYHS")) {
			zyhso = (List<Object>) fyxx.get("ZYHS");
			if(zyhso.size()>0){
				for (Object o : zyhso) {
					zyhs.add(MedicineUtils.parseLong(o));
				}
			}
		}
		if(zyhs.size()==0){
			throw new ModelDataOperationException("请选择病人！");
		}
		try {
			StringBuffer sb = new StringBuffer("select YBHM as YBHM,ZYYS as ZYYS from ZY_BRRY where JGID=:JGID and YBKH is not null");
			if (fyxx.containsKey("ZYHS")) {
				sb.append(" and ZYH in (:ZYH)");
				para.put("ZYH", zyhs);
			}
			zybrryList = dao.doQuery(sb.toString(), para);
			if(zybrryList.size()>0){
				para.clear();
				para.put("JGID", manaUnitId);
				Map<String, Object> zybrry = zybrryList.get(0);
				StringBuffer sql = new StringBuffer("select FYRQ as FYRQ,FYXH as FYXH,FYMC as FYMC,YPCD as YPCD,FYSL as FYSL,FYDJ as FYDJ,JFRQ as JFRQ,YPLX as YPLX from ZY_FYMX where JGID=:JGID");
				if (fyxx.containsKey("ZYHS")) {
					sql.append(" and ZYH in (:ZYH)");
					para.put("ZYH", zyhs);
				}
				if (fyxx.containsKey("JLXHS")) {
					sql.append(" and JLXH in (:JLXHS)");
					para.put("JLXHS", jlxhs);
				}
				zyfymxList = dao.doQuery(sql.toString(), para);
				//更新上传费用明细标志
				para.clear();
				para.put("JGID", manaUnitId);
				StringBuffer updateSql = new StringBuffer("update ZY_FYMX  set SCBZ = '1' where JGID=:JGID");
				if (fyxx.containsKey("ZYHS")) {
					updateSql.append(" and ZYH in (:ZYH)");
					para.put("ZYH", zyhs);
				}
				if (fyxx.containsKey("JLXHS")) {
					updateSql.append(" and JLXH in (:JLXHS)");
					para.put("JLXHS", jlxhs);
				}
				dao.doUpdate(updateSql.toString(), para);
				if(zyfymxList.size()>0){
					List<Map<String,Object>> dataList = new ArrayList<Map<String,Object>>();
					boolean hasAllZbm = true;
					int rowNum = 1;
					for(Map<String, Object> zyfymx:zyfymxList){
						para.clear();
						String zbmjgid="";
						Dictionary njjb=DictionaryController.instance().get("phis.dictionary.NJJB");
						zbmjgid=njjb.getItem(manaUnitId).getProperty("zbmjgid")+"";
						para.put("JGID", zbmjgid);
						para.put("YPXH", Long.parseLong(zyfymx.get("FYXH")+""));
						String lx = zyfymx.get("YPLX")+"";
						List<Map<String, Object>> ybypList = new ArrayList<Map<String,Object>>();
						if("0".equals(lx)){//项目
							ybypList = dao.doQuery("select YYZBM as YYZBM from GY_YLMX" +
									" where JGID=:JGID and FYXH=:YPXH", para);
						}else{//药品
							para.put("YPCD",Long.parseLong(zyfymx.get("YPCD")+""));
							ybypList = dao.doQuery("select YYZBM as YYZBM from YK_CDXX" +
									" where JGID=:JGID and YPXH=:YPXH and YPCD=:YPCD", para);
						}
						if(ybypList.size()>0){
							Map<String, Object> ybyp = ybypList.get(0);
							if((ybyp.get("YYZBM")==null || "".equals(ybyp.get("YYZBM")+"")) && Double.parseDouble(zyfymx.get("FYDJ")+"")>0.0){
								hasAllZbm = false;
							}
							if((ybyp.get("YYZBM")==null || "".equals(ybyp.get("YYZBM")+"")) && Double.parseDouble(zyfymx.get("FYDJ")+"")==0.0){
								continue;
							}
							//查询医保人员编号
							para.clear();
							para.put("PERSONID", zybrry.get("ZYYS")+"");
							List<Map<String, Object>> ybdmList = dao.doQuery("select YBDM as YBDM from SYS_Personnel" +
									" where PERSONID=:PERSONID and YBDM is not null", para);
							String ybdm = zybrry.get("ZYYS")+"";
							if(ybdmList.size()>0){
								ybdm = ybdmList.get(0).get("YBDM")+"";
							}
							
							ybfyMap = new HashMap<String, Object>();
							ybfyMap.put("ID", rowNum);
							ybfyMap.put("XH", zybrry.get("YBHM"));
							ybfyMap.put("BZ", "0".equals(lx)?"1":"0");
							ybfyMap.put("SJ", sdf.format(zyfymx.get("FYRQ")));
							ybfyMap.put("ZBM", ybyp.get("YYZBM"));
							ybfyMap.put("SL", zyfymx.get("FYSL"));
							ybfyMap.put("DJ", zyfymx.get("FYDJ"));
							ybfyMap.put("YSM", ybdm);
							ybfyMap.put("YS", zybrry.get("ZYYS"));
							ybfyMap.put("YHLB", null);
							ybfyMap.put("YHJ", null);
							dataList.add(ybfyMap);
						}
					}
					if(!hasAllZbm){
						throw new ModelDataOperationException("有收费项目未维护自编码，请仔细核对！");
					}else{
						try {
							byte[] bs = callMapListToXML(dataList);
							res.put("xmlBytes", bs);
							res.put("xmlStr",  new String(bs,"GBK"));
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}else{
					throw new ModelDataOperationException("未查到住院费用信息！");
				}
			}else{
				throw new ModelDataOperationException("未查到病人入院登记信息！");
			}
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static InetAddress getInetAddress(){
		  
        try{
            return InetAddress.getLocalHost();
        }catch(UnknownHostException e){
            System.out.println("unknown host!");
        }
        return null;
    }
  
    public static String getHostIp(InetAddress netAddress){
        if(null == netAddress){
            return null;
        }
        String ip = netAddress.getHostAddress(); //get the ip address
        return ip;
    }
  
    public static String getHostName(InetAddress netAddress){
        if(null == netAddress){
            return null;
        }
        String name = netAddress.getHostName(); //get the host address
        return name;
    }
	
	/**
	 * 读取莱斯xml出院结算信息
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws ParseException 
	 */
	@SuppressWarnings("unchecked")
	public void doQueryCyjsdXmlByPath(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException, ParseException {
		String zyh = req.get("zyh") + "";
		if(zyh==null || "".equals(zyh)){
			return;
		}
		//创建SAX解析器对象 
	    SAXReader reader = new SAXReader(); 
	    //读取XML文件 
	    Document document;
		try {
			document = reader.read(new File("C:\\njyb\\cyjsd.xml"));
		    //获取根元素 
		    Element rootElement = document.getRootElement();
		    Element record;   
		    for (Iterator<Element> i = rootElement.elementIterator("RECORD"); i.hasNext();) {
		    	record = (Element) i.next();
//		    	String zyh_ls = record.elementText("ZYH");
//		    	if(zyh.equals(zyh_ls)){
				    ele2map(res, record);
//		    		break;
//		    	}
		     }   
		    
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	/**
	 * 保存医保住院结算信息
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveZyjs(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = (String) user.getManageUnitId();// 用户的机构ID
		Long zyh = Long.parseLong(req.get("ZYH")+"");
		Map<String, Object> fileMap = (Map<String, Object>) req.get("FILEDATA");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
		    Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("JGID", manaUnitId);
	    	//根据zyh查询病人入院信息
	    	StringBuffer sb = new StringBuffer("BRXZ = 3000 and JGID=:JGID and YBKH is not null");
			if (zyh!=null && zyh>0L) {
				sb.append(" and ZYH=:ZYH");
				parameters.put("ZYH", zyh);
			}
			long zybrry = dao.doCount("ZY_BRRY", sb.toString(), parameters);
			if(zybrry > 0){
				parameters.clear();
				parameters.put("JGID", manaUnitId);
				parameters.put("ZYH", zyh);
				long ybjs = dao.doCount("YB_ZYJS", "JGID=:JGID and ZYH=:ZYH",
						parameters);
				String op = ybjs>0 ? "update" : "create";
				Map<String, Object> ybjsMap = new HashMap<String, Object>();
				ybjsMap.put("JGID", manaUnitId);
				ybjsMap.put("ZYH", zyh);
				ybjsMap.put("JSCS", ybjs+1);
				ybjsMap.put("TBR", fileMap.get("TBR"));
				ybjsMap.put("XM", fileMap.get("XM"));
				ybjsMap.put("RYXZ", fileMap.get("RYXZ"));
				ybjsMap.put("XH", fileMap.get("XH"));
				String cysj = "";
				if(fileMap.get("CYSJ")!=null && !"".equals(fileMap.get("CYSJ"))){
					String str = fileMap.get("CYSJ")+"";
					cysj = str.substring(0,4)+"-"+str.substring(4,6)+"-"+str.substring(6,8);
					ybjsMap.put("CYSJ", sdf.parse(cysj));
				}else{
					ybjsMap.put("CYSJ",null);
				}
				ybjsMap.put("ZFY", fileMap.get("ZFY"));
				ybjsMap.put("YF", fileMap.get("YF"));
				ybjsMap.put("XMF", fileMap.get("XMF"));
				ybjsMap.put("GRZL", fileMap.get("GRZL"));
				ybjsMap.put("GRZF", fileMap.get("GRZF"));
				ybjsMap.put("YBZF", fileMap.get("YBZF"));
				ybjsMap.put("ZHZF", fileMap.get("ZHZF"));
				ybjsMap.put("ZHYE", fileMap.get("ZHYE"));
				ybjsMap.put("XJZF", fileMap.get("XJZF"));
				ybjsMap.put("XZMC", fileMap.get("XZMC"));
				ybjsMap.put("DJH", fileMap.get("DJH"));
				ybjsMap.put("YSM", fileMap.get("YSM"));
				ybjsMap.put("YH1", fileMap.get("YH1"));
				ybjsMap.put("YH2", fileMap.get("YH2"));
				ybjsMap.put("YH3", fileMap.get("YH3"));
				ybjsMap.put("TCZF", fileMap.get("TCZF"));
				ybjsMap.put("DBZF", fileMap.get("DBZF"));
				ybjsMap.put("BZZF", fileMap.get("BZZF"));
				ybjsMap.put("YBJE", 0.00);
				ybjsMap.put("QCZH", Double.parseDouble(fileMap.get("ZHZF")==null?"0":fileMap.get("ZHZF")+"")+Double.parseDouble(fileMap.get("ZHYE")==null?"0":fileMap.get("ZHYE")+""));
				ybjsMap.put("XZFS", null);
				ybjsMap.put("MZBZ", fileMap.get("MZBZ"));
				ybjsMap.put("QHMC", fileMap.get("QHMC"));
				dao.doSave(op, BSPHISEntryNames.YB_ZYJS, ybjsMap,
						false);
			}
		} catch (ValidateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/***
	 * 核心方法，里面有递归调用
	 * 
	 * @param map
	 * @param ele
	 */
	@SuppressWarnings("unchecked")
	public static void ele2map(Map<String, Object> map, Element ele) {
		// 获得当前节点的子节点
		List<Element> elements = ele.elements();
		if (elements.size() == 0) {
			// 没有子节点说明当前节点是叶子节点，直接取值即可
			map.put(ele.getName(), ele.getText());
		} else if (elements.size() == 1) {
			// 只有一个子节点说明不用考虑list的情况，直接继续递归即可
			Map<String, Object> tempMap = new HashMap<String, Object>();
			ele2map(tempMap, elements.get(0));
			map.put(ele.getName(), tempMap.get(ele.getName()));
		} else {
			// 多个子节点的话就得考虑list的情况了，比如多个子节点有节点名称相同的
			// 构造一个map用来去重
			Map<String, Object> tempMap = new HashMap<String, Object>();
			for (Element element : elements) {
				tempMap.put(element.getName(), null);
			}
			Set<String> keySet = tempMap.keySet();
			for (String str : keySet) {
				Namespace namespace = elements.get(0).getNamespace();
				List<Element> elements2 = ele.elements(new QName(str,namespace));
				// 如果同名的数目大于1则表示要构建list
				if (elements2.size() > 1) {
					List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
					for (Element element : elements2) {
						Map<String, Object> tempMap1 = new HashMap<String, Object>();
						ele2map(tempMap1, element);
						list.add(tempMap1);
					}
					map.put(str, list);
				} else {
					// 同名的数量不大于1则直接递归去
					Map<String, Object> tempMap1 = new HashMap<String, Object>();
					ele2map(tempMap1, elements2.get(0));
					map.put(str, tempMap1.get(str));
				}
			}
		}
	}
	
	/**
	 * 将医保接口数据写入文件
	 * @param mapList
	 * @param filePath
	 */
	public static void writeData2File(List<Map<String, Object>> mapList, String filePath){
		byte[] bs = callMapListToXML(mapList);
		// 确定写出文件的位置
        File file = new File(filePath);
        // 建立输出字节流
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 用FileOutputStream 的write方法写入字节数组
        try {
            fos.write(bs);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("写入成功");
        // 为了节省IO流的开销，需要关闭
        try {
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
	
	/**
	 * 保存系统和医保金额不一致日志
	 * @param errorMsg
	 * @param filePath
	 */
	public void doSaveYbErrorLogs(String errorMsg){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
		byte[] bs = errorMsg.getBytes();
		String dateStr = sdf.format(new Date());
		// 确定写出文件的位置
        File file = new File("C:\\njyb\\logs\\log_"+dateStr+".txt");
        try{
        	if(!file.exists()){
        		file.createNewFile();
        	}
        	writeFileContent(file,bs);
        } catch(Exception e){
        	e.printStackTrace();
        }
	}
	
	/**
	 * 写入文件内容
	 * @param file
	 * @param bs
	 */
	public void writeFileContent(File file,byte[] bs){
        // 建立输出字节流
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 用FileOutputStream 的write方法写入字节数组
        try {
            fos.write(bs);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("写入错误日志成功！");
        // 为了节省IO流的开销，需要关闭
        try {
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
	
	/**
	 * List<Map>转换成Xml
	 * @param mapList
	 * @return
	 */
	public static byte[] callMapListToXML(List<Map<String, Object>> mapList) {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"GBK\"?>\r\n<ROOT>\r\n");
        if(mapList.size()>0){
        	for(Map<String, Object> map:mapList){
        		sb.append("<RECORD>\r\n");
        		callMapToXML(map, sb);
        		sb.append("</RECORD>\r\n");
        	}
        }
        sb.append("</ROOT>");
        System.out.println("Xml：" + sb.toString());
        try {
            return sb.toString().getBytes("GBK");
        } catch (Exception e) {
        	System.out.println(e);
        }
        return null;
    }
  
	/**
	 * Map转换成Xml
	 * @param map
	 * @param sb
	 */
    private static void callMapToXML(Map<String, Object> map, StringBuffer sb) {
        Set set = map.keySet();
        for (Iterator it = set.iterator(); it.hasNext();) {
            String key = (String) it.next();
            Object value = map.get(key);
            if (null == value)
                value = "";
            if (value.getClass().getName().equals("java.util.ArrayList")) {
                ArrayList list = (ArrayList) map.get(key);
                sb.append("<" + key + ">");
                for (int i = 0; i < list.size(); i++) {
                    HashMap hm = (HashMap) list.get(i);
                    callMapToXML(hm, sb);
                }
                sb.append("</" + key + ">\r\n");
            } else {
                if (value instanceof HashMap) {
                    sb.append("<" + key + ">");
                    callMapToXML((HashMap) value, sb);
                    sb.append("</" + key + ">\r\n");
                } else {
                    sb.append("<" + key + ">" + value + "</" + key + ">\r\n");
                }
            }
        }
    }
    
    /**
     * 医保人员对照,左边list数据查询
     * @param cnd
     * @param req
     * @param res
     * @throws ModelDataOperationException
     */
    public void queryYbrydz(List<?> cnd,Map<String, Object> req,Map<String, Object> res)
			throws ModelDataOperationException {
		StringBuffer hql=new StringBuffer();
		hql.append("select a.PERSONID as PERSONID,a.PERSONNAME as PERSONNAME,a.CARDNUM as CARDNUM,a.GENDER as GENDER,a.PYCODE as PYCODE,a.YBDM as YBDM from SYS_Personnel a ");
		try {
			if(cnd!=null){
				hql.append(" where ").append(ExpressionProcessor.instance().toString(cnd));
			}
			MedicineCommonModel com=new MedicineCommonModel(dao);
			Map<String,Object> map_par=new HashMap<String,Object>();
			//如果有字典要转换的,最后一个参数传sc名(全路径)
			res.putAll(com.getPageInfoRecord(req, map_par, hql.toString(), null));
		} catch (ExpException e) {
			MedicineUtils.throwsException(logger, "表达式有错误", e);
		}
	}
    
    /**
     * 保存医保人员对照
     * @param body
     * @throws ModelDataOperationException
     */
    public void doSaveYbrydz(List<Map<String, Object>> body)
			throws ModelDataOperationException {
		if(body.size()>0){
			for(Map<String,Object> map:body){
				String personId = map.get("PERSONID")+"";
				String ybdm = map.get("YBDM")!=null?map.get("YBDM")+"":"";
				StringBuffer updateSql = new StringBuffer("update SYS_Personnel set YBDM=:YBDM where PERSONID=:PERSONID");
				Map<String,Object> map_par=new HashMap<String,Object>();
				map_par.put("YBDM", ybdm);
				map_par.put("PERSONID", personId);
				try {
					dao.doUpdate(updateSql.toString(), map_par);
				} catch (PersistentDataOperationException e) {
					// TODO Auto-generated catch block
					MedicineUtils.throwsException(logger, "医保人员对照保存失败", e);
				}
			}
		}
	}
    
    /**
     * 医保药品对照,左边list数据查询
     * @param cnd
     * @param req
     * @param res
     * @throws ModelDataOperationException
     */
    public void queryYbypdz(List<?> cnd,Map<String, Object> req,Map<String, Object> res)
			throws ModelDataOperationException {
    	UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = (String) user.getManageUnitId();// 用户的机构ID
		StringBuffer hql=new StringBuffer();
		hql.append("select YPXH as YPXH,YPMC as YPMC,YPGG as YPGG,YPDW as YPDW,CDMC as CDMC,PYDM as PYDM,YYZBM as YYZBM from V_YPXX_YB");
		if(manaUnitId!=null && !"".equals(manaUnitId)){
			hql.append(" and b.JGID=:JGID");
		}
		MedicineCommonModel com=new MedicineCommonModel(dao);
		Map<String,Object> map_par=new HashMap<String,Object>();
		//如果有字典要转换的,最后一个参数传sc名(全路径)
		res.putAll(com.getPageInfoRecord(req, map_par, hql.toString(), null));
	}
    
    /**
     * 保存医保药品对照
     * @param body
     * @throws ModelDataOperationException
     */
    public void doSaveYbypdz(List<Map<String, Object>> body)
			throws ModelDataOperationException {
		if(body.size()>0){
	    	UserRoleToken user = UserRoleToken.getCurrent();
			String manaUnitId = (String) user.getManageUnitId();// 用户的机构ID
			for(Map<String,Object> map:body){
				Long ypxh = Long.parseLong(map.get("YPXH")+"");
				Long ypcd = Long.parseLong(map.get("YPCD")+"");
				String yyzbm = map.get("YYZBM")!=null?map.get("YYZBM")+"":"";
				StringBuffer updateSql = new StringBuffer("update YK_CDXX set YYZBM=:YYZBM where JGID=:JGID and YPXH=:YPXH and YPCD=:YPCD");
				Map<String,Object> map_par=new HashMap<String,Object>();
				map_par.put("YYZBM", yyzbm);
				map_par.put("YPXH", ypxh);
				map_par.put("YPCD", ypcd);
				map_par.put("JGID", manaUnitId);
				try {
					dao.doUpdate(updateSql.toString(), map_par);
				} catch (PersistentDataOperationException e) {
					// TODO Auto-generated catch block
					MedicineUtils.throwsException(logger, "医保人员对照保存失败", e);
				}
			}
		}
	}
    
    /**
     * 医保项目对照,左边list数据查询
     * @param cnd
     * @param req
     * @param res
     * @throws ModelDataOperationException
     */
    public void queryYbxmdz(List<?> cnd,Map<String, Object> req,Map<String, Object> res)
			throws ModelDataOperationException {
    	UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = (String) user.getManageUnitId();// 用户的机构ID
		StringBuffer hql=new StringBuffer();
		hql.append("select FYXH as FYXH,FYMC as FYMC,FYDW as FYDW,PYDM as PYDM,YYZBM as YYZBM from V_YLMX_YB");
		if(manaUnitId!=null && !"".equals(manaUnitId)){
			hql.append(" and b.JGID=:JGID");
		}
		MedicineCommonModel com=new MedicineCommonModel(dao);
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("JGID", manaUnitId);
		//如果有字典要转换的,最后一个参数传sc名(全路径)
		res.putAll(com.getPageInfoRecord(req, map_par, hql.toString(), null));
	}
    
    /**
     * 保存医保项目对照
     * @param body
     * @throws ModelDataOperationException
     */
    public void doSaveYbxmdz(List<Map<String, Object>> body)
			throws ModelDataOperationException {
		if(body.size()>0){
	    	UserRoleToken user = UserRoleToken.getCurrent();
			String manaUnitId = (String) user.getManageUnitId();// 用户的机构ID
			for(Map<String,Object> map:body){
				Long fyxh = Long.parseLong(map.get("FYXH")+"");
				String yyzbm = map.get("YYZBM")!=null?map.get("YYZBM")+"":"";
				StringBuffer updateSql = new StringBuffer("update GY_YLMX set YYZBM=:YYZBM where JGID=:JGID and FYXH=:FYXH");
				Map<String,Object> map_par=new HashMap<String,Object>();
				map_par.put("YYZBM", yyzbm);
				map_par.put("FYXH", fyxh);
				map_par.put("JGID", manaUnitId);
				try {
					dao.doUpdate(updateSql.toString(), map_par);
				} catch (PersistentDataOperationException e) {
					// TODO Auto-generated catch block
					MedicineUtils.throwsException(logger, "医保人员对照保存失败", e);
				}
			}
		}
	}
    
    /**
     * 保存医保信息
     * @param req
     * @param res
     * @param dao
     * @param ctx
     * @throws ServiceException
     */
	@SuppressWarnings("unchecked")
	protected void doSavegrzhxx(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		Map<String, Object> body=(Map<String,Object>)req.get("body");
		if (body.get("cards") != null) {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			list = (List<Map<String, Object>>) body.get("cards");
			Map<String, Object> card = list.get(0);
			String cardsql="select empiId as empiId,cardNo as cardNo from MPI_Card where cardNo=:cardNo and cardTypeCode=:cardTypeCode";
			Map<String, Object> pa=new HashMap<String, Object>();
			pa.put("cardNo", card.get("cardNo").toString());
			pa.put("cardTypeCode", "98");
			try {
				Map<String, Object> cardno=dao.doLoad(cardsql, pa);
				if(cardno==null  || cardno.size() <=0){
					Map<String, Object> records = new HashMap<String, Object>();
					records.putAll(body);
					records = EmpiUtil.changeToPIXFormat(records);
					records.put("photo","");
					Map<String, Object> result = EmpiUtil.submitPerson(dao, ctx, body,records);
					res.put("body", result);
				}else{
					body.put("empiId", cardno.get("empiId")+"");
					body.put("lastModifyTime", new Date());
					Map<String, Object> PIXData = new HashMap<String, Object>();
					PIXData = EmpiUtil.changeToPIXFormat(body);
					if(PIXData.containsKey("lastModifyUnit")){
						PIXData.remove("lastModifyUnit");
					}
					if(PIXData.containsKey("lastModifyTime")){
						PIXData.remove("lastModifyTime");
					}
					if(PIXData.containsKey("lastModifyUser")){
						PIXData.remove("lastModifyUser");
					}
					PIXData.put("photo","");
					Map<String, Object> result = EmpiUtil.updatePerson(dao, ctx, body,
							PIXData);
					res.put("body", result);
				}
			} catch (PersistentDataOperationException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	/**
	 * 卡号查询
	 * 
	 * @param CardOrMZHM
	 * @param res
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> doCheckHasCardInfo(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> body=(Map<String,Object>)req.get("body");
		Boolean flag=(Boolean)req.get("flag");
		Map<String, Object> result = new HashMap<String, Object>();
		res.put("hasCardNo", false);
		if (body.get("cards") != null) {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			list = (List<Map<String, Object>>) body.get("cards");
			Map<String, Object> card = list.get(0);
			Map<String, Object> mzxh = list.get(1);
			String cardNo = card.get("cardNo").toString();
			String mzxhStr = mzxh.get("cardNo").toString();
			if(mzxhStr==null || "".equals(mzxhStr)){
				return res;
			}
			if(!cardNo.equals(mzxhStr) && !flag){
				String cardsql="select empiId as empiId,cardNo as cardNo from MPI_Card where cardNo=:cardNo and cardTypeCode=:cardTypeCode";
				Map<String, Object> pa=new HashMap<String, Object>();
				pa.put("cardNo", cardNo);
				pa.put("cardTypeCode", "98");
				try {
					Map<String, Object> cardno=dao.doLoad(cardsql, pa);
					if(cardno!=null  && cardno.size() > 0){
						res.put("empiId", cardno.get("empiId")+"");
						res.put("hasCardNo", true);
					}
				} catch (PersistentDataOperationException e1) {
					e1.printStackTrace();
				}
				res.put("body", result);
			}else{
				String cardsql="select empiId as empiId from MPI_Card where cardNo=:cardNo and cardTypeCode=:cardTypeCode";
				Map<String, Object> pa=new HashMap<String, Object>();
				pa.put("cardNo", cardNo);
				pa.put("cardTypeCode", "04");
				try {
					Map<String, Object> empiId=dao.doLoad(cardsql, pa);
					if(empiId!=null  && empiId.size() > 0){
						String empiIdStr = empiId.get("empiId")+"";
						if(empiIdStr==null || "".equals(empiIdStr)){
							return res;
						}
						EmpiModel empiModel = new EmpiModel(dao);
						empiModel.deleteCardByEmpiId(empiIdStr);
						res.put("empiId", empiIdStr);
						res.put("cardNo", cardNo);
						String mzxhsql="select MZHM as MZHM from MS_BRDA where EMPIID=:EMPIID ";
						pa.clear();
						pa.put("EMPIID", empiIdStr);
						List<Map<String, Object>> mzhms = dao.doQuery(mzxhsql, pa);
						if(mzhms.size()>0){
							mzxhStr = mzhms.get(0).get("MZHM")+"";
							res.put("mzxh", mzxhStr);
							//保存医保卡
							Map<String, Object> card1 = new HashMap<String, Object>();
							card1.put("empiId", empiIdStr);
							card1.put("cardNo", cardNo);
							card1.put("cardTypeCode", "98");
							card1.put("status", "0");
							empiModel.saveCard(card1);
							//保存就诊卡
							Map<String, Object> card2 = new HashMap<String, Object>();
							card2.put("empiId", empiIdStr);
							card2.put("cardNo", mzxhStr);
							card2.put("cardTypeCode", "04");
							card2.put("status", "0");
							empiModel.saveCard(card2);
						}
						res.put("hasCardNo", true);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
		return res;
	}
	/**
	 * 药品通用品库编码对照 wy 2019年11月18日10:03:21
	 * @param body
	 * @throws ModelDataOperationException
	 */
	public void doSavetypk(List<Map<String, Object>> body) throws ModelDataOperationException {
		if (body.size()>0) {
			for (Map<String, Object> objectMap : body) {

				Object o = objectMap.get("YPXH");
				if (o != null) {

					Long ypxh = Long.valueOf(objectMap.get("YPXH")+"");
					String sbm = objectMap.get("SBM") + "";
					String sql = "update YK_TYPK set sbm=:sbm where YPXH=:ypxh";
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("ypxh", ypxh);
					map.put("sbm", sbm);
					try {
						dao.doUpdate(sql, map);
					} catch (PersistentDataOperationException e) {
						MedicineUtils.throwsException(logger, "药品通用品库编码对照保存失败", e);
					}
				}

			}
		}
	}

	/**
	 * 检查部位比对 wy
	 * @param body
	 * @throws ModelDataOperationException
	 */
	public void doSaveJCBW(List<Map<String, Object>> body) throws ModelDataOperationException {
		if (body.size()>0) {
			for (Map<String, Object> map : body) {
				Object o = map.get("XMID");
				if (o != null) {
					Long xmid = Long.valueOf(o + "");
					String sbm = map.get("SBM") + "";
					String sql = "update YJ_JCSQ_JCXM set SBM=:sbm where XMID=:xmid";
					Map<String, Object> par = new HashMap<String, Object>();
					par.put("sbm", sbm);
					par.put("xmid", xmid);
					try {
						dao.doUpdate(sql, par);
					} catch (PersistentDataOperationException e) {
						MedicineUtils.throwsException(logger, "检查部位对照保存失败", e);

					}
				}
			}
		}
	}
}
