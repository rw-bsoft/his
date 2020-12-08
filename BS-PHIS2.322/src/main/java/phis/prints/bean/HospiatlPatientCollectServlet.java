package phis.prints.bean;

import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.EvaluationTimeEnum;
import net.sf.jasperreports.engine.type.HorizontalAlignEnum;
import net.sf.jasperreports.engine.type.LineStyleEnum;
import net.sf.jasperreports.engine.type.VerticalAlignEnum;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;

import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;
import ctd.print.ColumnModel;
import ctd.print.DynaGridPrintUtil;
import ctd.print.PrintException;
import ctd.print.PrintUtil;
import ctd.util.AppContextHolder;
import ctd.util.JSONUtils;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;

public class HospiatlPatientCollectServlet extends DynamicPrint_BySZ {

	private final static int columnWidth = 830;
	public final static int textWidth = 80;
	public final static int textHeight = 15;
	public final static int fontSize = 10;
	private final static int titleFontSize = 16;
	private final static int columnHeaderHeight = 18;
	private final static boolean isColumnHeaderFontBond = true;
	private final static int titleHeight = 20;
	private final static int detailHeight = 15;
	private final static int pageFooterHeight = 15;

	// private static final String fontName = "宋体";
	// private static final String pdfFontName = "STSong-Light";
	// private static final String pdfEncoding = "UniGB-UCS2-H";

	@SuppressWarnings("unchecked")
	public List<JasperPrint> doPrint(Map<String, Object> request,
			Map<String, Object> response) throws PrintException {
		try {
			String strConfig = (String) request.get("config");
			strConfig = URLDecoder.decode(strConfig, "UTF-8");
			HashMap<String, Object> config = JSONUtils.parse(strConfig,
					HashMap.class);
			HashMap<String, Object> requestData = (HashMap<String, Object>) config
					.get("requestData");
			int ib_ks = 0;
			if (requestData.get("ib_ks") != null) {
				ib_ks = Integer.parseInt(requestData.get("ib_ks") + "");
			}
			int ib_ys = 0;
			if (requestData.get("ib_ys") != null) {
				ib_ys = Integer.parseInt(requestData.get("ib_ys") + "");
			}
			if (ib_ks == 1) {
				return doPrintSummaryks(request, response);
			} else if (ib_ys == 1) {
				return doPrintSummaryys(request, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 住院医生核算表
	 * @param request
	 * @param response
	 * @return
	 * @throws PrintException
	 */
	@SuppressWarnings("unchecked")
	public List<JasperPrint> doPrintSummaryys(Map<String, Object> request,
			Map<String, Object> response) throws PrintException {
		// 取到机构id
		Context ctx = ContextUtils.getContext();
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		try {
			String strConfig = (String) request.get("config");
			strConfig = URLDecoder.decode(strConfig, "UTF-8");
			HashMap<String, Object> config = JSONUtils.parse(strConfig,
					HashMap.class);
			if (ss == null) {
				SessionFactory sf = AppContextHolder.getBean(
						AppContextHolder.DEFAULT_SESSION_FACTORY,
						SessionFactory.class);
				ss = sf.openSession();
				ctx.put(Context.DB_SESSION, ss);
			}
			UserRoleToken user = UserRoleToken.getCurrent();
			String uid = user.getManageUnitId();
			String title = "住院医生核算表";
			DecimalFormat df = new DecimalFormat("#0.00");
			// 获取表单参数
			HashMap<String, Object> requestData = (HashMap<String, Object>) config
					.get("requestData");
			String dateFrom = (String) requestData.get("dateFrom");
			String dateTo = (String) requestData.get("dateTo");
			
			HashMap<String, HashMap<String, Object>> data=new HashMap<String, HashMap<String, Object>>();
			//tot 用于存放合计
			HashMap<String, Object> tot=new HashMap<String, Object>();
			tot.put("YSXM","合计:");
			tot.put("FYHJ",0.00);
			//就诊统计
//			String jzsql="select a.ZZYS as YSDM ,count(1) as JZRC from zy_brry a "+
//					" where a.jgid='"+uid+"' and a.cyrq >=to_date('"+dateFrom+"','yyyy-mm-dd HH24:mi:ss')"+
//					" and  a.cyrq <=to_date('"+dateTo+"','yyyy-mm-dd HH24:mi:ss') "+
//					" group by a.ZZYS ";
			String jzsql="select b.YSGH as YSDM,count(distinct b.zyh) as JZRC from zy_brry a,zy_fymx b  where a.zyh = b.zyh and a.jgid= b.jgid"+
					" and  a.jgid='"+uid+"' and a.cyrq>=to_date('"+dateFrom+"','yyyy-mm-dd HH24:mi:ss')"+
					" and  a.cyrq <=to_date('"+dateTo+"','yyyy-mm-dd HH24:mi:ss') "+
					" group by b.YSGH ";
			List<Map<String, Object>> jzlist = ss.createSQLQuery(jzsql)
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
			if(jzlist.size()==0){
				tot.put("JZRC",0);
			}else{
				for(Map<String, Object> one :jzlist){
					tot.put("JZRC", tot.get("JZRC")==null?Long.parseLong(one.get("JZRC")+""):
						Long.parseLong(tot.get("JZRC")+"")+Long.parseLong(one.get("JZRC")+""));
					if(data.get(one.get("YSDM")+"")==null){
						HashMap<String, Object> temp=new HashMap<String, Object>();
						temp.put("JZRC", one.get("JZRC"));
						try {
							temp.put("YSXM", DictionaryController.instance().get("phis.dictionary.user").getText(one.get("YSDM")+""));
						} catch (ControllerException e) {
							e.printStackTrace();
						}
						data.put(one.get("YSDM")+"", temp);
					}else{
						data.get(one.get("YSDM")+"").put("JZRC", one.get("JZRC"));
					}
				}
			}
			//检查单统计
			String jcsql="select ysgh as YSDM,sum(jcdsl) as JCDSL from ("+
					" select a.ysgh,1 as jcdsl"+
					" from zy_bqyz a join zy_brry b on a.jgid = b.jgid and a.zyh = b.zyh" +
					" where a.jgid='"+uid+"' and a.xmlx = 4 and a.zfpb = 0 and a.bzxx is not null"+
					" and b.cyrq >=to_date('"+dateFrom+"','yyyy-mm-dd HH24:mi:ss')"+
					" and b.cyrq <=to_date('"+dateTo+"','yyyy-mm-dd HH24:mi:ss') "+
					" ) group by ysgh";
			List<Map<String, Object>> jclist = ss.createSQLQuery(jcsql)
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
			if(jclist.size()==0){
				tot.put("JCDSL",0);
			}else{
				for(Map<String, Object> one :jclist){
					tot.put("JCDSL", tot.get("JCDSL")==null?Long.parseLong(one.get("JCDSL")+""):
						Long.parseLong(tot.get("JCDSL")+"")+Long.parseLong(one.get("JCDSL")+""));
					if(data.get(one.get("YSDM")+"")==null){
						HashMap<String, Object> temp=new HashMap<String, Object>();
						temp.put("JCDSL", one.get("JCDSL"));
						try {
							temp.put("YSXM", DictionaryController.instance().get("phis.dictionary.user").getText(one.get("YSDM")+""));
						} catch (ControllerException e) {
							e.printStackTrace();
						}

						data.put(one.get("YSDM")+"", temp);
					}else{
						data.get(one.get("YSDM")+"").put("JCDSL", one.get("JCDSL"));
					}
				}
			}
			//处方数统计
//			String cfsql="select ZZYS as YSDM,sum(XYCFS) as XYCFS,sum(ZYCFS) as ZYCFS ,sum(CYCFS) as CYCFS from ("+
//					" select b.zzys,case a.yplx when 1 then 1 else 0 end  as XYCFS,case a.yplx when 2 then 1 else 0 end as ZYCFS,"+
//					" case a.yplx when 3 then 1 else 0 end  as CYCFS"+
//					" from zy_fymx a join zy_brry b on a.jgid = b.jgid and a.zyh = b.zyh where a.jgid='"+uid+"'" +
//					" and b.cyrq >=to_date('"+dateFrom+"','yyyy-mm-dd HH24:mi:ss')"+
//					" and b.cyrq <=to_date('"+dateTo+"','yyyy-mm-dd HH24:mi:ss') "+
//					" ) group by zzys";
			String cfsql="select ysgh as YSDM,sum(XYCFS) as XYCFS,sum(ZYCFS) as ZYCFS ,sum(CYCFS) as CYCFS from ("+
					" select a.ysgh,case a.yplx when 1 then 1 else 0 end  as XYCFS,case a.yplx when 2 then 1 else 0 end as ZYCFS,"+
					" case a.yplx when 3 then 1 else 0 end  as CYCFS"+
					" from zy_fymx a join zy_brry b on a.jgid = b.jgid and a.zyh = b.zyh where a.jgid='"+uid+"'" +
					" and b.cyrq >=to_date('"+dateFrom+"','yyyy-mm-dd HH24:mi:ss')"+
					" and b.cyrq <=to_date('"+dateTo+"','yyyy-mm-dd HH24:mi:ss') "+
					" ) group by ysgh";
			List<Map<String, Object>> cflist = ss.createSQLQuery(cfsql.toString())
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
			if(cflist.size()==0){
				tot.put("XYCFS",0);
				tot.put("ZYCFS",0);
				tot.put("CYCFS",0);
			}else{
				for(Map<String, Object> one :cflist){
					tot.put("XYCFS", tot.get("XYCFS")==null?Long.parseLong(one.get("XYCFS")+""):
						Long.parseLong(tot.get("XYCFS")+"")+Long.parseLong(one.get("XYCFS")+""));
					tot.put("ZYCFS", tot.get("ZYCFS")==null?Long.parseLong(one.get("ZYCFS")+""):
						Long.parseLong(tot.get("ZYCFS")+"")+Long.parseLong(one.get("ZYCFS")+""));
					tot.put("CYCFS", tot.get("CYCFS")==null?Long.parseLong(one.get("CYCFS")+""):
						Long.parseLong(tot.get("CYCFS")+"")+Long.parseLong(one.get("CYCFS")+""));
					if(data.get(one.get("YSDM")+"")==null){
						HashMap<String, Object> temp=new HashMap<String, Object>();
						temp.put("XYCFS", one.get("XYCFS"));
						temp.put("ZYCFS", one.get("ZYCFS"));
						temp.put("CYCFS", one.get("CYCFS"));
						try {
							temp.put("YSXM", DictionaryController.instance().get("phis.dictionary.user").getText(one.get("YSDM")+""));
						} catch (ControllerException e) {
							e.printStackTrace();
						}
						data.put(one.get("YSDM")+"", temp);
					}else{
						data.get(one.get("YSDM")+"").put("XYCFS", one.get("XYCFS"));
						data.get(one.get("YSDM")+"").put("ZYCFS", one.get("ZYCFS"));
						data.get(one.get("YSDM")+"").put("CYCFS", one.get("CYCFS"));
					}
				}
			}
			//处方金额统计
//			String cfjesql="select zzys as YSDM ,fyxm as FYGB,sum(zjje) as HJJE from ("+
//					" select b.zzys as zzys,a.fyxm as fyxm,a.zjje as zjje"+
//					" from zy_fymx a join zy_brry b on a.jgid = b.jgid and a.zyh = b.zyh " +
//					" where a.jgid='"+uid+"' and b.cyrq >=to_date('"+dateFrom+"','yyyy-mm-dd HH24:mi:ss')"+
//					" and b.cyrq <=to_date('"+dateTo+"','yyyy-mm-dd HH24:mi:ss')"+
//					" ) group by zzys,fyxm";
			String cfjesql="select ysgh as YSDM ,fyxm as FYGB,sum(zjje) as HJJE from ("+
					" select a.ysgh,a.fyxm as fyxm,a.zjje as zjje"+
					" from zy_fymx a join zy_brry b on a.jgid = b.jgid and a.zyh = b.zyh " +
					" where a.jgid='"+uid+"' and b.cyrq >=to_date('"+dateFrom+"','yyyy-mm-dd HH24:mi:ss')"+
					" and b.cyrq <=to_date('"+dateTo+"','yyyy-mm-dd HH24:mi:ss')"+
					" ) group by ysgh,fyxm";
			List<Map<String, Object>> cfjelist = ss.createSQLQuery(cfjesql)
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
			for(Map<String, Object> one :cfjelist){
				tot.put(one.get("FYGB")+"", df.format(tot.get(one.get("FYGB")+"")==null?Double.parseDouble(one.get("HJJE")+""):
					Double.parseDouble(tot.get(one.get("FYGB")+"")+"")+Double.parseDouble(one.get("HJJE")+"")));
				tot.put("FYHJ",df.format(Double.parseDouble(one.get("HJJE")+"")+Double.parseDouble(tot.get("FYHJ")+"")));
				
				if(data.get(one.get("YSDM")+"")==null){
					HashMap<String, Object> temp=new HashMap<String, Object>();
					temp.put(one.get("FYGB")+"", one.get("HJJE"));
					temp.put("FYHJ", df.format(one.get("HJJE")));
					try {
						temp.put("YSXM", DictionaryController.instance().get("phis.dictionary.user").getText(one.get("YSDM")+""));
					} catch (ControllerException e) {
						e.printStackTrace();
					}
					data.put(one.get("YSDM")+"", temp);
				}else{
					data.get(one.get("YSDM")+"").put(one.get("FYGB")+"",
							data.get(one.get("YSDM")+"").get(one.get("FYGB")+"")==null?Double.parseDouble(one.get("HJJE")+""):
								Double.parseDouble(one.get("HJJE")+"")+
								Double.parseDouble(data.get(one.get("YSDM")+"").get(one.get("FYGB")+"")+""));
					data.get(one.get("YSDM")+"").put("FYHJ", df.format(data.get(one.get("YSDM")+"").get("FYHJ")==null?
							Double.parseDouble(one.get("HJJE")+""):Double.parseDouble(data.get(one.get("YSDM")+"").get("FYHJ")+"")+
							Double.parseDouble(one.get("HJJE")+"")));
				}
			}
			//检查特殊金额--Wangjl  中医治疗费
//			String jctsjesql="select ysdm as YSDM,sum(zyzlf) as ZYZLF from ("+
//					" select b.zzys as ysdm,"+
//					" case when c.zyzlf =1  then a.zjje else 0 end as zyzlf"+
//					" from zy_fymx a join zy_brry b on a.jgid=b.jgid and a.zyh=b.zyh"+
//					" join gy_ylsf c on a.fyxh = c.fyxh and a.fyxm = c.fygb"+
//					" where a.jgid='"+uid+"' and b.cyrq >=to_date('"+dateFrom+"','yyyy-mm-dd HH24:mi:ss') "+
//					" and  b.cyrq <=to_date('"+dateTo+"','yyyy-mm-dd HH24:mi:ss') "+
//					" and c.zyzlf =1)  group by ysdm ";
			String jctsjesql="select ysgh as YSDM,sum(zyzlf) as ZYZLF from ("+
					" select a.ysgh,"+
					" case when c.zyzlf =1  then a.zjje else 0 end as zyzlf"+
					" from zy_fymx a join zy_brry b on a.jgid=b.jgid and a.zyh=b.zyh"+
					" join gy_ylsf c on a.fyxh = c.fyxh and a.fyxm = c.fygb"+
					" where a.jgid='"+uid+"' and b.cyrq >=to_date('"+dateFrom+"','yyyy-mm-dd HH24:mi:ss') "+
					" and  b.cyrq <=to_date('"+dateTo+"','yyyy-mm-dd HH24:mi:ss') "+
					" and c.zyzlf =1)  group by ysgh ";
			List<Map<String, Object>> jctsjelist = ss.createSQLQuery(jctsjesql)
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
			if(jctsjelist.size()==0){
				tot.put("ZYZLF", 0);
			}else{
				for(Map<String, Object> one :jctsjelist){
					tot.put("ZYZLF", df.format(tot.get("ZYZLF")==null?Double.parseDouble(one.get("ZYZLF")+""):
						Double.parseDouble(tot.get("ZYZLF")+"")+Double.parseDouble(one.get("ZYZLF")+"")));
					if(data.get(one.get("YSDM")+"")==null){
						HashMap<String, Object> temp=new HashMap<String, Object>();
						temp.put("ZYZLF", one.get("ZYZLF"));
						try {
							temp.put("YSXM", DictionaryController.instance().get("phis.dictionary.user").getText(one.get("YSDM")+""));
						} catch (ControllerException e) {
							e.printStackTrace();
						}
						data.put(one.get("YSDM")+"", temp);
					}else{
						data.get(one.get("YSDM")+"").put("ZYZLF",
								data.get(one.get("YSDM")+"").get("ZYZLF")==null?Double.parseDouble(one.get("ZYZLF")+""):
									Double.parseDouble(one.get("ZYZLF")+"")+
									Double.parseDouble(data.get(one.get("YSDM")+"").get("ZYZLF")+""));
					}
				}
			}
			
			//收费项目
//			String xmsql="select s.sfxm as SFXM,s.sfmc as SFMC  from gy_sfxm s ,("+
//					" select distinct a.fyxm from zy_fymx a join zy_brry b on a.jgid = b.jgid and a.zyh = b.zyh "+
//					" where a.jgid='"+uid+"' and b.cyrq >=to_date('"+dateFrom+"','yyyy-mm-dd HH24:mi:ss') "+
//					" and b.cyrq <=to_date('"+dateTo+"','yyyy-mm-dd HH24:mi:ss') "+
//					" ) t where s.sfxm=t.fyxm order by s.sfxm";
			String xmsql="select s.sfxm as SFXM,s.sfmc as SFMC  from gy_sfxm s ,("+
					" select distinct a.fyxm from zy_fymx a join zy_brry b on a.jgid = b.jgid and a.zyh = b.zyh "+
					" where a.jgid='"+uid+"' and b.cyrq >=to_date('"+dateFrom+"','yyyy-mm-dd HH24:mi:ss') "+
					" and b.cyrq <=to_date('"+dateTo+"','yyyy-mm-dd HH24:mi:ss') "+
					" ) t where s.sfxm=t.fyxm order by s.sfxm";
			List<Map<String, Object>> list_column = ss.createSQLQuery(xmsql)
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
			
			LinkedHashMap<String, ColumnModel> map = new LinkedHashMap<String, ColumnModel>();
			ColumnModel cm0 = new ColumnModel();
			cm0.setName("YSXM");
			cm0.setText("医生姓名");
			map.put("YSXM", cm0);
			cm0 = new ColumnModel();
			cm0.setName("JZRC");
			cm0.setText("就诊人次");
			map.put("JZRC", cm0);
			cm0 = new ColumnModel();
			cm0.setName("JCDSL");
			cm0.setText("检查单");
			map.put("JCDSL", cm0);
			cm0 = new ColumnModel();
			cm0.setName("XYCFS");
			cm0.setText("西药方");
			map.put("XYCFS", cm0);
			cm0 = new ColumnModel();
			cm0.setName("ZYCFS");
			cm0.setText("中药方");
			map.put("ZYCFS", cm0);
			cm0 = new ColumnModel();
			cm0.setName("CYCFS");
			cm0.setText("草药方");
			map.put("CYCFS", cm0);
			for (Map<String, Object> column : list_column) {
				ColumnModel cm = new ColumnModel();
				cm.setName(column.get("SFXM").toString());
				cm.setText((String) column.get("SFMC"));
				map.put(column.get("SFXM").toString(), cm);
			}
			cm0 = new ColumnModel();
			cm0.setName("ZYZLF");
			cm0.setText("其中中医治疗费");
			map.put("ZYZLF", cm0);
			cm0 = new ColumnModel();
			cm0.setName("FYHJ");
			cm0.setText("费用合计");
			map.put("FYHJ", cm0);

			// l_data 用于存放数据
			List<HashMap<String, Object>> l_data = new ArrayList<HashMap<String, Object>>();
			Iterator iter = data.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				HashMap<String, Object> dataMap = (HashMap<String, Object>)entry.getValue();
				for (Map.Entry<String, ColumnModel> mapEntry : map.entrySet()) { 
					if(!dataMap.containsKey(mapEntry.getValue().getName())){
						dataMap.put(mapEntry.getValue().getName(), "0");
					}
				}
				l_data.add(dataMap);
			}
			l_data.add(tot);
			
			ColumnModel[] columnModel = map.values().toArray(
					new ColumnModel[map.size()]);
			List<JasperReport> reports = getDynamicJasperReport(
					new ArrayList<JasperDesign>(), title, columnModel, false);
			List<JasperPrint> prints = new ArrayList<JasperPrint>();
			for (JasperReport r : reports) {
				prints.add(PrintUtil.getJasperPrint(r,
						new HashMap<String, Object>(), DynaGridPrintUtil
								.createJRBeanCollectionDataSource(columnModel,
										l_data)));
			}

			return prints;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} finally {
			if (ss != null) {
				ss.close();
			}
		}
	}

	/**
	 * 住院科室收入核算表
	 * @param request
	 * @param response
	 * @return
	 * @throws PrintException
	 */
	public List<JasperPrint> doPrintSummaryks(Map<String, Object> request,
			Map<String, Object> response) throws PrintException {
		// 取到机构id
		Context ctx = ContextUtils.getContext();
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		try {
			String strConfig = (String) request.get("config");
			strConfig = URLDecoder.decode(strConfig, "UTF-8");
			HashMap<String, Object> config = JSONUtils.parse(strConfig,
					HashMap.class);
			if (ss == null) {
				SessionFactory sf = AppContextHolder.getBean(
						AppContextHolder.DEFAULT_SESSION_FACTORY,
						SessionFactory.class);
				ss = sf.openSession();
				ctx.put(Context.DB_SESSION, ss);
			}
			UserRoleToken user = UserRoleToken.getCurrent();
			String ref = user.getManageUnit().getRef();
			String uid = user.getManageUnitId();

			// 获取表单参数
			HashMap<String, Object> requestData = (HashMap<String, Object>) config
					.get("requestData");
			String title = "住院收入核算表(按汇总日期统计)";//(String) config.get("title");
			String dateFrom = (String) requestData.get("dateFrom");
			String dateTo = (String) requestData.get("dateTo");
			List<Object> ksdmarray = (List<Object>) requestData.get("ksdm");
			String ksdmstrs = ksdmarray + "";
			String ksdm = ksdmstrs.substring(1, ksdmstrs.length() - 1);
			int ksnum = ksdm.indexOf(",");
			// 用于存放列,第一个列会在分页后继续显示
			LinkedHashMap<String, ColumnModel> map = new LinkedHashMap<String, ColumnModel>();
			// l_data 用于存放数据
			List<HashMap<String, Object>> l_data = new ArrayList<HashMap<String, Object>>();
			if (ksnum > 0) {
				l_data.clear();
				map.clear();
				StringBuilder sqlBuilder = new StringBuilder();
				ColumnModel cm0 = new ColumnModel();
				cm0.setName("TOTAL");
				cm0.setText("序号");
				map.put("TOTAL", cm0);
				ColumnModel cm2 = new ColumnModel();
				cm2.setName("KSMC");
				cm2.setText("科室名称");
				map.put("KSMC", cm2);
				int bbbz = 11;// 报表标志
				List<Map<String, Object>> list_column = new ArrayList<Map<String, Object>>();

				sqlBuilder = new StringBuilder();
				sqlBuilder
						.append("select ID as ID , OFFICENAME as OFFICENAME from sys_office where  ORGANIZCODE = '");
				sqlBuilder.append(ref);
				sqlBuilder.append("' and (OFFICECODE not in  ");
				sqlBuilder
						.append("(select PARENTID from SYS_Office  where PARENTID is not null)");
				sqlBuilder.append("  ) and HOSPITALDEPT='1' ");
				if (ksdm != null && !"".equals(ksdm) && !"null".equals(ksdm)) {
					sqlBuilder.append(" and ID in (").append(ksdm).append(")");
				}
				sqlBuilder.append(" and LOGOFF<>'1'");
				List<Map<String, Object>> list_data = ss
						.createSQLQuery(sqlBuilder.toString())
						.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
						.list();
				List<Map<String, Object>> detail_list = null;
				String id = "";
				sqlBuilder = new StringBuilder();
				sqlBuilder
						.append(" SELECT SUM(ZJJE) as ZJJE, SFXM as SFXM, KSDM as KSDM");
				sqlBuilder.append(" FROM ZY_SRHZ where KSLB=1 and JGID = '"
						+ uid + "' ");
				sqlBuilder
						.append(" and to_char(HZRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
								+ dateFrom
								+ "' and to_char(HZRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
								+ dateTo + "' ");
				if (list_data != null && list_data.size() > 0) {
					sqlBuilder.append(" AND KSDM in (");
					for (int i = 0; i < list_data.size(); i++) {
						id = String.valueOf(list_data.get(i).get("ID"));
						sqlBuilder.append(id);
						if (i != list_data.size() - 1) {
							sqlBuilder.append(" , ");
						}
					}
					sqlBuilder.append(")");
				}
				sqlBuilder.append(" GROUP BY KSDM, SFXM  order by SFXM");
				detail_list = ss.createSQLQuery(sqlBuilder.toString())
						.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
						.list();
				List<Map<String, Object>> list_xmgb = this.getListXmgb(bbbz,
						uid, ss);
				List<Map<String, Object>> list_columnALL = this.getListColumn(
						bbbz, uid, ss, list_xmgb);
				for (int i = 0; i < list_columnALL.size(); i++) {
					Map<String, Object> m = list_columnALL.get(i);
					for (int j = 0; j < detail_list.size(); j++) {
						Map<String, Object> m1 = detail_list.get(j);
						if (m1.get("SFXM").toString()
								.equals(m.get("GBXM").toString())
								&& !list_column.contains(m)) {
							list_column.add(m);
						}
					}
				}
				for (int i = 0; i < list_column.size(); i++) {
					ColumnModel cm = new ColumnModel();
					cm.setName(list_column.get(i).get("GBXM").toString());
					cm.setText((String) list_column.get(i).get("XMMC"));
					map.put(list_column.get(i).get("GBXM").toString(), cm);
				}
				boolean isAdded = false;
				for (Map<String, Object> m : detail_list) {
					isAdded = false;
					// 遍历报表的数据集合：l_data，如果此病人性质已存在于l_data，则全部累加
					for (int j = 0; j < l_data.size(); j++) {
						Map<String, Object> d = l_data.get(j);
						// 如果有该科室
						if (d.get("KSDM").equals(m.get("KSDM"))) {
							d.put(m.get("SFXM").toString(),
									String.format("%1$.2f", m.get("ZJJE")));
							isAdded = true; // 该条数据已添加，则标记为true
							break;
						}
					}
					// 遍历完l_data 没有找到该病人性质，则新增一个map放进去
					if (isAdded == false) {
						HashMap<String, Object> map_ls = new HashMap<String, Object>();
						map_ls.put("TOTAL", l_data.size() + 1);
						map_ls.put("KSDM", m.get("KSDM"));
						for (int i = 0; i < list_data.size(); i++) {
							if (list_data.get(i).get("ID")
									.equals(m.get("KSDM"))) {
								map_ls.put("KSMC",
										list_data.get(i).get("OFFICENAME"));
							}
						}
						map_ls.put(m.get("SFXM") + "",
								String.format("%1$.2f", m.get("ZJJE")));
						map_ls.put("SFXM", m.get("SFXM"));
						l_data.add(map_ls);
						isAdded = false;

					}

				}
				// 将有指定归并的项目，归并到指定项目
				for (Map<String, Object> m : l_data) {
					for (Map<String, Object> m_xmgb : list_xmgb) {
						if (m.containsKey(m_xmgb.get("SFXM") + "")
								&& m.containsKey(m_xmgb.get("GBXM") + "")) {
							if (!m_xmgb.get("SFXM").toString()
									.equals(m_xmgb.get("GBXM").toString())) {
								double gbxmvalue = Double.parseDouble(m
										.get(m_xmgb.get("GBXM") + "") + "");
								double sfxmvalue = Double.parseDouble(m
										.get(m_xmgb.get("SFXM") + "") + "");
								m.put(String.valueOf(m_xmgb.get("GBXM")),
										String.format("%1$.2f",
												(gbxmvalue + sfxmvalue)));
							}
						}
					}
				}
				// 计算最底下一列的统计
				HashMap<String, Object> dataTotal = new HashMap<String, Object>();
				dataTotal.put("KSMC", "合计");
				for (Map<String, Object> column : list_column) {
					double value = 0.00;
					for (int j = 0; j < l_data.size(); j++) {
						Map<String, Object> d = l_data.get(j);
						if (d.get(column.get("GBXM") + "") != null
								&& !"null"
										.equals(d.get(column.get("GBXM") + ""))) {
							value += Double.parseDouble(d.get(column
									.get("GBXM") + "")
									+ "");
						}
					}
					dataTotal.put(column.get("GBXM").toString(),
							String.format("%1$.2f", value));
				}
				l_data.add(dataTotal);
			} else {
				l_data.clear();
				map.clear();
				StringBuilder sqlBuilder = new StringBuilder();
				ColumnModel cm0 = new ColumnModel();
				cm0.setName("TOTAL");
				cm0.setText("序号");
				map.put("TOTAL", cm0);
				ColumnModel cm1 = new ColumnModel();
				cm1.setName("ZYHM");
				cm1.setText("住院号码");
				map.put("ZYHM", cm1);
				ColumnModel cm2 = new ColumnModel();
				cm2.setName("BRXM");
				cm2.setText("病人姓名");
				map.put("BRXM", cm2);
				ColumnModel cm3 = new ColumnModel();
				cm3.setName("BRCH");
				cm3.setText("病人床号");
				map.put("BRCH", cm3);
				ColumnModel cm4 = new ColumnModel();
				cm4.setName("BRXZ");
				cm4.setText("病人性质");
				map.put("BRXZ", cm4);
				int bbbz = 11;// 报表标志
				List<Map<String, Object>> list_column = new ArrayList<Map<String, Object>>();

				sqlBuilder = new StringBuilder();
				sqlBuilder
						.append("select ID as ID , OFFICENAME as OFFICENAME from sys_office where  ORGANIZCODE = '");
				sqlBuilder.append(ref);
				sqlBuilder.append("' and (OFFICECODE not in  ");
				sqlBuilder
						.append("(select PARENTID from SYS_Office  where PARENTID is not null)");
				sqlBuilder.append("  ) and HOSPITALDEPT='1' ");
				if (ksdm != null && !"".equals(ksdm) && !"null".equals(ksdm)) {
					sqlBuilder.append(" and ID = '").append(ksdm).append("'");
				}
				sqlBuilder.append(" and LOGOFF<>'1'");
				List<Map<String, Object>> list_data = ss
						.createSQLQuery(sqlBuilder.toString())
						.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
						.list();
				List<Map<String, Object>> detail_list = null;
				String id = "";
				sqlBuilder = new StringBuilder();
				sqlBuilder
						.append(" SELECT C.ZYHM as ZYHM,D.BRXM as BRXM,D.BRID as BRID,C.BRCH as BRCH,C.BRXZ as BRXZ,SUM(B.ZJJE) as ZJJE, A.SFXM as SFXM");
				sqlBuilder
						.append(" FROM ZY_SRHZ A,ZY_FYMX B,ZY_BRRY C,MS_BRDA D where A.JGID=B.JGID and A.HZRQ=B.HZRQ and A.KSDM=B.FYKS and A.SFXM=B.FYXM AND B.ZYH=C.ZYH AND C.BRID=D.BRID and A.KSLB=1 and A.JGID = '"
								+ uid + "' ");
				sqlBuilder
						.append(" and to_char(A.HZRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
								+ dateFrom
								+ "' and to_char(A.HZRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
								+ dateTo + "' ");
				if (list_data != null && list_data.size() > 0) {
					sqlBuilder.append(" AND A.KSDM in (");
					for (int i = 0; i < list_data.size(); i++) {
						id = String.valueOf(list_data.get(i).get("ID"));
						sqlBuilder.append(id);
						if (i != list_data.size() - 1) {
							sqlBuilder.append(" , ");
						}
					}
					sqlBuilder.append(")");
				}
				sqlBuilder
						.append(" GROUP BY C.ZYHM,D.BRXM,D.BRID,C.BRCH,C.BRXZ, A.SFXM order by A.SFXM");
				detail_list = ss.createSQLQuery(sqlBuilder.toString())
						.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
						.list();
				List<Map<String, Object>> list_xmgb = this.getListXmgb(bbbz,
						uid, ss);
				List<Map<String, Object>> list_columnALL = this.getListColumn(
						bbbz, uid, ss, list_xmgb);
				for (int i = 0; i < list_columnALL.size(); i++) {
					Map<String, Object> m = list_columnALL.get(i);
					for (int j = 0; j < detail_list.size(); j++) {
						Map<String, Object> m1 = detail_list.get(j);
						if (m1.get("SFXM").toString()
								.equals(m.get("GBXM").toString())
								&& !list_column.contains(m)) {
							list_column.add(m);
						}
					}
				}
				for (int i = 0; i < list_column.size(); i++) {
					ColumnModel cm = new ColumnModel();
					cm.setName(list_column.get(i).get("GBXM").toString());
					cm.setText((String) list_column.get(i).get("XMMC"));
					map.put(list_column.get(i).get("GBXM").toString(), cm);
				}
				//增加最右侧总计列 zhaojian 2017-09-19
				ColumnModel cmzj = new ColumnModel();
				cmzj.setName("zj");
				cmzj.setText("总计");
				map.put("zj", cmzj);
				boolean isAdded = false;
				for (Map<String, Object> m : detail_list) {
					isAdded = false;
					// 遍历报表的数据集合：l_data，如果此病人性质已存在于l_data，则全部累加
					for (int j = 0; j < l_data.size(); j++) {
						Map<String, Object> d = l_data.get(j);
						// 如果有该科室
						if (d.get("ZYHM").equals(m.get("ZYHM"))) {
							d.put(m.get("SFXM").toString(),
									String.format("%1$.2f", m.get("ZJJE")));
							if (m.get("BRCH") != null && m.get("BRCH") != "") {
								d.put("BRCH", m.get("BRCH") + "");
							} else {
								d.put("BRCH", "");
							}
							if (m.get("BRXZ") != null && m.get("BRXZ") != "") {
								d.put("BRXZ",
										DictionaryController
												.instance()
												.getDic("phis.dictionary.patientProperties_ZY")
												.getText(m.get("BRXZ") + ""));
							} else {
								d.put("BRXZ", "");
							}
							isAdded = true; // 该条数据已添加，则标记为true
							break;
						}
					}
					// 遍历完l_data 没有找到该病人性质，则新增一个map放进去
					if (isAdded == false) {
						HashMap<String, Object> map_ls = new HashMap<String, Object>();
						map_ls.put("TOTAL", l_data.size() + 1);
						map_ls.put("ZYHM", m.get("ZYHM"));
						map_ls.put("BRXM", m.get("BRXM"));
						map_ls.put(m.get("SFXM") + "",
								String.format("%1$.2f", m.get("ZJJE")));

						if (m.get("BRCH") != null && m.get("BRCH") != "") {
							map_ls.put("BRCH", m.get("BRCH") + "");
						} else {
							map_ls.put("BRCH", "");
						}
						if (m.get("BRXZ") != null && m.get("BRXZ") != "") {
							map_ls.put(
									"BRXZ",
									DictionaryController
											.instance()
											.getDic("phis.dictionary.patientProperties_ZY")
											.getText(m.get("BRXZ") + ""));
						} else {
							map_ls.put("BRXZ", "");
						}
						map_ls.put("SFXM", m.get("SFXM"));
						l_data.add(map_ls);
						isAdded = false;

					}

				}
				// 将有指定归并的项目，归并到指定项目
				for (Map<String, Object> m : l_data) {
					for (Map<String, Object> m_xmgb : list_xmgb) {
						if (m.containsKey(m_xmgb.get("SFXM") + "")
								&& m.containsKey(m_xmgb.get("GBXM") + "")) {
							if (!m_xmgb.get("SFXM").toString()
									.equals(m_xmgb.get("GBXM").toString())) {
								double gbxmvalue = Double.parseDouble(m
										.get(m_xmgb.get("GBXM") + "") + "");
								double sfxmvalue = Double.parseDouble(m
										.get(m_xmgb.get("SFXM") + "") + "");
								m.put(String.valueOf(m_xmgb.get("GBXM")),
										String.format("%1$.2f",
												(gbxmvalue + sfxmvalue)));
							}
						}
					}
				}
				//计算最右侧一列的统计-总计 zhaojian 2017-09-19
				double valuezj = 0.00;
				for (int j = 0; j < l_data.size(); j++) {
					Map<String, Object> d = l_data.get(j);
					double value = 0.00;
					for (Map<String, Object> column : list_column) {
					if (d.get(column.get("GBXM") + "") != null
							&& !"null"
									.equals(d.get(column.get("GBXM") + ""))) {
						value += Double.parseDouble(d.get(column
								.get("GBXM") + "")
								+ "");
						valuezj += Double.parseDouble(d.get(column
								.get("GBXM") + "")
								+ "");
					}
				}
					d.put("zj",	String.format("%1$.2f", value));
			}
				// 计算最底下一列的统计
				HashMap<String, Object> dataTotal = new HashMap<String, Object>();
				dataTotal.put("BRXZ", "合计");
				for (Map<String, Object> column : list_column) {
					double value = 0.00;
					for (int j = 0; j < l_data.size(); j++) {
						Map<String, Object> d = l_data.get(j);
						if (d.get(column.get("GBXM") + "") != null
								&& !"null"
										.equals(d.get(column.get("GBXM") + ""))) {
							value += Double.parseDouble(d.get(column
									.get("GBXM") + "")
									+ "");
						}
					}
					dataTotal.put(column.get("GBXM").toString(),
							String.format("%1$.2f", value));
				}
				//增加最右侧总计列 zhaojian 2017-09-19
				dataTotal.put("zj",String.format("%1$.2f", valuezj));
				l_data.add(dataTotal);
			}
			ColumnModel[] columnModel = map.values().toArray(
					new ColumnModel[map.size()]);
			List<JasperReport> reports = getDynamicJasperReport(
					new ArrayList<JasperDesign>(), title, columnModel, false);
			List<JasperPrint> prints = new ArrayList<JasperPrint>();

			for (JasperReport r : reports) {
				prints.add(PrintUtil.getJasperPrint(r,
						new HashMap<String, Object>(), DynaGridPrintUtil
								.createJRBeanCollectionDataSource(columnModel,
										l_data)));
			}

			return prints;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} finally {
			if (ss != null) {
				ss.close();
			}
		}
	}

	/**
	 * @param designs
	 *            空的一个JasperDesign集合，原因：当所有的列宽度超过一页宽度时候，自动扩展一页（递归实现）
	 * @param title
	 *            标题
	 * @param columnModel
	 *            列模型
	 * @return
	 * @throws JRException
	 */
	public List<JasperReport> getDynamicJasperReport(
			List<JasperDesign> designs, String title,
			ColumnModel[] columnModel, boolean isSeparate) throws JRException {
		List<JasperReport> reports = new ArrayList<JasperReport>();
		List<JasperDesign> process_designs = getDynamicJasperDesign(designs,
				title, columnModel, isSeparate);
		for (JasperDesign design : process_designs) {
			reports.add(JasperCompileManager.compileReport(design));
		}
		return reports;
	}

	private List<JasperDesign> getDynamicJasperDesign(
			List<JasperDesign> designs, String title,
			ColumnModel[] columnModel, boolean isSeparate) throws JRException {
		JasperDesign design = DynaGridPrintUtil.getJasperDesign(title, true);
		designs.add(design);
		JRDesignBand titleBand = DynaGridPrintUtil.getJRDesignBand(titleHeight);
		JRDesignBand columnHeaderBand = DynaGridPrintUtil
				.getJRDesignBand(columnHeaderHeight);
		JRDesignBand detailBand = DynaGridPrintUtil
				.getJRDesignBand(detailHeight);
		JRDesignBand pageFooter = DynaGridPrintUtil
				.getJRDesignBand(pageFooterHeight);
		setBand(design, titleBand, columnHeaderBand, detailBand, pageFooter);
		// 标题
		JRDesignStaticText titleText = DynaGridPrintUtil.getJRDesignStaticText(
				title, titleFontSize, true);
		parseTitleText(titleText, columnModel.length);
		titleBand.addElement(titleText);
		int totalWidth = 0;
		if (!isSeparate) {
			if (columnModel.length > 1) {
				if ("KSMC".equals(columnModel[1].getName() + "")) {
					for (int i = 0; i < columnModel.length; i++) {
						if (columnModel[i].isHide()) {
							continue;
						}
						int width = columnModel[i].getWdith();
						totalWidth += width;
						// 列标题
						JRDesignStaticText staticText = DynaGridPrintUtil
								.getJRDesignStaticText(
										columnModel[i].getText(), fontSize,
										isColumnHeaderFontBond);
						staticText.setWidth(width);
						staticText
								.setHorizontalAlignment(HorizontalAlignEnum.CENTER);
						staticText.setX(totalWidth - width);
						staticText.getLineBox().getPen().setLineWidth(1f);
						staticText.getLineBox().getPen()
								.setLineStyle(LineStyleEnum.SOLID);
						columnHeaderBand.addElement(staticText);
						// 字段
						JRDesignField field = new JRDesignField();
						field.setName(columnModel[i].getName());
						field.setValueClass(String.class);
						design.addField(field);
						// 字段组件
						JRDesignTextField textField = DynaGridPrintUtil
								.getJRDesignTextField(columnModel[i].getName(),
										String.class);
						if (i == 1) {
							textField
									.setHorizontalAlignment(HorizontalAlignEnum.LEFT);
						} else {
							textField
									.setHorizontalAlignment(HorizontalAlignEnum.RIGHT);
						}
						textField.setX(totalWidth - width);
						textField.getLineBox().getPen().setLineWidth(1f);
						textField.getLineBox().getPen()
								.setLineStyle(LineStyleEnum.SOLID);
						// textField.setStretchWithOverflow(true); //是否换行打印
						detailBand.addElement(textField);
						// 底部页码
						getPageFooterTextField(pageFooter);
					}
				} else {
					for (int i = 0; i < columnModel.length; i++) {
						if (columnModel[i].isHide()) {
							continue;
						}
						int width = columnModel[i].getWdith();
						totalWidth += width;
						// 列标题
						JRDesignStaticText staticText = DynaGridPrintUtil
								.getJRDesignStaticText(
										columnModel[i].getText(), fontSize,
										isColumnHeaderFontBond);
						staticText.setWidth(width);
						staticText
								.setHorizontalAlignment(HorizontalAlignEnum.CENTER);
						staticText.setX(totalWidth - width);
						staticText.getLineBox().getPen().setLineWidth(1f);
						staticText.getLineBox().getPen()
								.setLineStyle(LineStyleEnum.SOLID);
						columnHeaderBand.addElement(staticText);
						// 字段
						JRDesignField field = new JRDesignField();
						field.setName(columnModel[i].getName());
						field.setValueClass(String.class);
						design.addField(field);
						// 字段组件
						JRDesignTextField textField = DynaGridPrintUtil
								.getJRDesignTextField(columnModel[i].getName(),
										String.class);
						if (i > 0 && i < 5) {
							textField
									.setHorizontalAlignment(HorizontalAlignEnum.LEFT);
						} else {
							textField
									.setHorizontalAlignment(HorizontalAlignEnum.RIGHT);
						}
						textField.setX(totalWidth - width);
						textField.getLineBox().getPen().setLineWidth(1f);
						textField.getLineBox().getPen()
								.setLineStyle(LineStyleEnum.SOLID);
						// textField.setStretchWithOverflow(true); //是否换行打印
						detailBand.addElement(textField);
						// 底部页码
						getPageFooterTextField(pageFooter);
					}

				}
			}
			return designs;
		}
		totalWidth = 0;
		for (int i = 0; i < columnModel.length; i++) {
			if (columnModel[i].isHide()) {
				continue;
			}
			int width = columnModel[i].getWdith();
			totalWidth += width;
			if (totalWidth > columnWidth) {
				int z = 1;
				while (columnModel[i - z].isHide()) {
					z++;
				}
				columnModel[i - z].setWdith(columnModel[i - z].getWdith()
						+ columnWidth - (totalWidth - width));
				break;
			}
		}
		totalWidth = 0;
		for (int i = 0; i < columnModel.length; i++) {
			if (columnModel[i].isHide()) {
				continue;
			}
			int width = columnModel[i].getWdith();
			totalWidth += width;
			if (totalWidth > columnWidth) {
				ColumnModel[] models = new ColumnModel[columnModel.length - i
						+ 1];
				for (int j = 0; j < models.length; j++) {
					models[j] = new ColumnModel();
					if (j == 0) {
						models[0].setName(columnModel[0].getName());
						models[0].setText(columnModel[0].getText());
					} else {
						models[j].setName(columnModel[i + j - 1].getName());
						models[j].setText(columnModel[i + j - 1].getText());
					}
				}

				// System.arraycopy(columnModel, i, models, 1, models.length);
				return getDynamicJasperDesign(designs, title, models,
						isSeparate);
			}
			// 列标题
			JRDesignStaticText staticText = DynaGridPrintUtil
					.getJRDesignStaticText(columnModel[i].getText(), fontSize,
							isColumnHeaderFontBond);
			staticText.setWidth(width);
			staticText.setX(totalWidth - width);
			staticText.getLineBox().getPen().setLineWidth(1f);
			staticText.getLineBox().getPen().setLineStyle(LineStyleEnum.SOLID);
			columnHeaderBand.addElement(staticText);
			// 字段
			JRDesignField field = new JRDesignField();
			field.setName(columnModel[i].getName());
			field.setValueClass(String.class);
			design.addField(field);
			// 字段组件
			JRDesignTextField textField = DynaGridPrintUtil
					.getJRDesignTextField(columnModel[i].getName(),
							String.class);
			textField.setWidth(width);
			textField.setX(totalWidth - width);
			textField.getLineBox().getPen().setLineWidth(1f);
			textField.getLineBox().getPen().setLineStyle(LineStyleEnum.SOLID);
			// textField.setStretchWithOverflow(true); //是否换行打印
			detailBand.addElement(textField);
			// 底部页码
			getPageFooterTextField(pageFooter);
		}
		return designs;
	}

	private void setBand(JasperDesign jd, JRDesignBand title,
			JRDesignBand columnheader, JRDesignBand detail,
			JRDesignBand pagefooter) {
		jd.setTitle(title);
		jd.setColumnHeader(columnheader);
		JRDesignSection jrds = (JRDesignSection) jd.getDetailSection();
		jrds.addBand(detail);
		jd.setPageFooter(pagefooter);
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-1-9
	 * @description 标题
	 * @updateInfo
	 * @param titleText
	 */
	private static void parseTitleText(JRDesignStaticText titleText,
			int columnLength) {
		// titleText.setHorizontalAlignment(HorizontalAlignEnum.CENTER);
		titleText.setVerticalAlignment(VerticalAlignEnum.MIDDLE);
		titleText.setHeight(titleHeight);
		titleText.setWidth(columnWidth);
		titleText.setX(50 * (columnLength / 2));
		titleText.setY(0);
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-1-9
	 * @description 获取底部页码
	 * @updateInfo
	 * @param pageFooter
	 */
	private static void getPageFooterTextField(JRDesignBand pageFooter) {
		JRDesignTextField f1 = DynaGridPrintUtil.getJRDesignTextField("",
				String.class);
		JRDesignTextField f2 = DynaGridPrintUtil.getJRDesignTextField("",
				String.class);
		f1.setEvaluationTime(EvaluationTimeEnum.NOW); // JREvaluationTime .now
		f2.setEvaluationTime(EvaluationTimeEnum.REPORT); // .report
		((JRDesignExpression) f1.getExpression())
				.setText("\"第\"+$V{PAGE_NUMBER}+\"页\"");
		((JRDesignExpression) f2.getExpression())
				.setText(" \"共\"+$V{PAGE_NUMBER}+\"页\"");
		f1.setX(320);
		f2.setX(400);
		pageFooter.addElement(f1);
		pageFooter.addElement(f2);
	}

}
