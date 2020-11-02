package phis.prints.bean;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import ctd.dao.exception.DataAccessException;
import ctd.print.ColumnModel;
import ctd.print.DynaGridPrintUtil;
import ctd.print.DynamicPrint;
import ctd.print.PrintException;
import ctd.print.PrintUtil;
import ctd.util.AppContextHolder;
import ctd.util.JSONUtils;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;

/**
 * 
 * @description 性质费用核算
 * 
 * @author <a href="mailto:gaof@bsoft.com.cn">gaof</a>
 */
public class PropertiesAccountingServlet extends DynamicPrint {
	//属性值
//	private final static String defaultContent = "text/html;charset=UTF-8";
//	private final static String defaultErrorMsg = "打印服务器繁忙，请稍后再试。若问题仍然存在，请与系统管理员联系。";
//	private static WebApplicationContext wac;
//	private final static int pageWidth = 595;
//	private final static int pageHeight = 842;
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
//	private static final String fontName = "宋体";
//	private static final String pdfFontName = "STSong-Light";
//	private static final String pdfEncoding = "UniGB-UCS2-H";
	private final static int bbbh_xzzf = 5;
	private final static int bbbh_mxzf = 6;
	private final static int bbbh_hzzf = 7;

//	public void init(ServletConfig sc) throws ServletException {
//		WebApplicationContext w = AppContextHolder.get();
//		if (w != null) {
//			wac = w;
//		} else {
//			wac = WebApplicationContextUtils
//					.getRequiredWebApplicationContext(sc.getServletContext());
//		}
//		logger.info("PrintServlet inited...");
//	}

	@SuppressWarnings("unchecked")
	public List<JasperPrint> doPrint(Map<String, Object> request,
			Map<String, Object> response) throws PrintException{
		try {
			String strConfig = (String) request.get("config");
			strConfig = URLDecoder.decode(strConfig, "UTF-8");
			HashMap<String, Object> config = JSONUtils.parse(strConfig,
					HashMap.class);
			HashMap<String, Object> requestData = (HashMap<String, Object>) config
					.get("requestData");
            String chargeMode = (String)requestData.get("chargeMode");
            String type = (String) requestData.get("type");
//			logger.info("chargeMode:"+chargeMode);
			int chargeModeInt = Integer.parseInt(chargeMode);
			if(chargeModeInt == 1){
				//按收费日期
				String chargeModeTo_sf = " MS_MZXX.SFRQ ";
				String chargeModeTo_zf = " MS_ZFFP.ZFRQ ";
				System.out.println("chargeMode == " + chargeMode);
				if ("zf".equals(type)) {
					return doPrintZf(request, response, config,chargeModeTo_sf,chargeModeTo_zf);
				}
				if ("mxzf".equals(type)) {
					return doPrintMxZf(request, response, config,chargeModeTo_sf,chargeModeTo_zf);
				}
				if ("hzzf".equals(type)) {
					return doPrintHzZf(request, response, config,chargeModeTo_sf,chargeModeTo_zf);
				}
			}else{
				//按汇总日期
				String chargeModeTo_sf = " MS_MZXX.HZRQ ";
				String chargeModeTo_zf = " MS_ZFFP.HZRQ ";
				if ("zf".equals(type)) {
					return doPrintZf(request, response, config,chargeModeTo_sf,chargeModeTo_zf);
				}
				if ("mxzf".equals(type)) {
					return doPrintMxZf(request, response, config,chargeModeTo_sf,chargeModeTo_zf);
				}
				if ("hzzf".equals(type)) {
					return doPrintHzZf(request, response, config,chargeModeTo_sf,chargeModeTo_zf);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
//		response.setContentType(defaultContent);
//		String uid = (String) request.getSession().getAttribute("uid");
//		ServletOutputStream sos = response.getOutputStream();
//		if (StringUtils.isEmpty(uid)) {
//			logger.error("not logon.");
//			writeMsg(sos, "链接已失效，请登录.");
//			return;
//		}
//		try {
//			ServletOutputStream outputstream = response.getOutputStream();
//			String strConfig = request.getParameter("config");
//			if (StringUtils.isEmpty(strConfig)) {
//				logger.error("print config miss.");
//				writeMsg(outputstream, defaultErrorMsg);
//				return;
//			}
//			strConfig = URLDecoder.decode(strConfig, "UTF-8");
//			HashMap<String, Object> config = MapperUtil.getJsonMapper()
//					.readValue(strConfig, HashMap.class);
//			HashMap<String, Object> requestData = (HashMap<String, Object>) config
//					.get("requestData");
//			String type = (String) requestData.get("type");
			
			//获取chargeMode(收费方式) 
//			String chargeMode = (String)requestData.get("chargeMode");
//			
////			logger.info("chargeMode:"+chargeMode);
//			int chargeModeInt = Integer.parseInt(chargeMode);
//			if(chargeModeInt == 1){
//				//按收费日期
//				String chargeModeTo_sf = " MS_MZXX.SFRQ ";
//				String chargeModeTo_zf = " MS_ZFFP.ZFRQ ";
//				System.out.println("chargeMode == " + chargeMode);
//				if ("zf".equals(type)) {
//					logger.info("----brxz-zf-----");
//					doPrintZf(request, response, config,chargeModeTo_sf,chargeModeTo_zf);
//				}
//				if ("mxzf".equals(type)) {
//					logger.info("----brxz-mxzf-----");
//					doPrintMxZf(request, response, config);
//				}
//				if ("hzzf".equals(type)) {
//					logger.info("----brxz-hzzf-----");
//					doPrintHzZf(request, response, config);
//				}
//			}else{
//				//按汇总日期
//				String chargeModeTo_sf = " MS_MZXX.HZRQ ";
//				String chargeModeTo_zf = " MS_ZFFP.HZRQ ";
//				logger.info("chargeMode == " + chargeMode);
//				if ("zf".equals(type)) {
//					logger.info("-----brxz-zf----");
//					doPrintZf(request, response, config,chargeModeTo_sf,chargeModeTo_zf);
//				}
//				if ("mxzf".equals(type)) {
//					logger.info("----brxz-mxzf-----");
//					doPrintMxZf(request, response, config);
//				}
//				if ("hzzf".equals(type)) {
//					logger.info("----brxz-hzzf-----");
//					doPrintHzZf(request, response, config);
//				}
//			}
//
//			
//
//		} catch (Exception e) {
//			logger.error(e.getMessage(), e);
//			writeMsg(sos, defaultErrorMsg);
//		}
	}
//
//	private void writeMsg(OutputStream outputstream, String msg)
//			throws UnsupportedEncodingException, IOException {
//		outputstream.write(msg.getBytes("UTF-8"));
//	}

	/**
	 * 得到报表列集合
	 * 
	 * @author gaof
	 * @param uid
	 * @param ss
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getListColumn(int bbbh, String uid,String sql_mxzf,
			Session ss, List<Map<String, Object>> list_xmgb) {
		StringBuffer sql_column = new StringBuffer();
		sql_column
				.append("select GY_XMGB.GBXM,GY_XMGB.SXH from GY_XMGB"
						+ " where GY_XMGB.Bbbh = ").append(bbbh)
				.append(" and GY_XMGB.JGID = ").append(uid)
				.append(" and GY_XMGB.SFXM in (select MS_SFMX.SFXM as SFXM FROM MS_MZXX MS_MZXX,MS_SFMX MS_SFMX "+sql_mxzf+" group by MS_SFMX.SFXM)")
				.append(" group by GY_XMGB.GBXM,GY_XMGB.SXH order by SXH");
		List<Map<String, Object>> list_column = ss
				.createSQLQuery(sql_column.toString())
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		// 如果没有对应的，则使用默认规则
		if (list_column.size() == 0 || list_column == null) {
//			StringBuffer sql_column_default = new StringBuffer();
//			sql_column_default
//					.append("SELECT SFXM AS GBXM, SFMC AS XMMC FROM GY_SFXM");
			List<Map<String, Object>> list_column_default = ss
					.createSQLQuery("select GBXM,XMMC from (select GY_SFXM.SFXM as GBXM,GY_SFXM.SFMC AS XMMC FROM MS_MZXX MS_MZXX,MS_SFMX MS_SFMX,GY_SFXM GY_SFXM "+sql_mxzf.toString()+" and MS_SFMX.SFXM=GY_SFXM.SFXM group by GY_SFXM.SFXM,GY_SFXM.SFMC union  select SFXM GBXM,SFMC XMMC from gy_sfxm where SFXM in (select  c.fygb from ms_ghks a left join gy_ylsf c on a.zlsfxm = c.fyxh, ms_ghmx b where b.ksdm = a.ksdm and c.fygb is not null))")
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
					.list();
			return list_column_default;
		}
		// 把收费项目和归并名称的关系存到一个map中
		Map<BigDecimal, String> map_xmgb_relation = new HashMap<BigDecimal, String>();
		for (Map<String, Object> m_xmgb : list_xmgb) {
			map_xmgb_relation.put(new BigDecimal(m_xmgb.get("SFXM")+""),
					(String) m_xmgb.get("XMMC"));
		}
		// 找到对应的在报表显示的项目名称
		for (Map<String, Object> m_column : list_column) {
			String xmmc = map_xmgb_relation.get(m_column.get("GBXM"));
			m_column.put("XMMC", xmmc);
		}
		return list_column;
	}

	/**
	 * 得到项目归并规则
	 * 
	 * @author gaof
	 * @param uid
	 * @param ss
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getListXmgb(int bbbh, String uid,String sql_mxzf,
			Session ss) {
		StringBuffer sql_xmgb = new StringBuffer();
		sql_xmgb.append(
				"select GY_XMGB.Sfxm,GY_XMGB.GBXM,GY_XMGB.Xmmc,GY_XMGB.Sxh from GY_XMGB"
						+ " where GY_XMGB.Bbbh = ").append(bbbh)
				.append(" and GY_XMGB.JGID = ").append(uid)
				.append("and GY_XMGB.SFXM in (select MS_SFMX.SFXM as SFXM FROM MS_MZXX MS_MZXX,MS_SFMX MS_SFMX "+sql_mxzf+" group by MS_SFMX.SFXM)");
		List<Map<String, Object>> list_xmgb = ss
				.createSQLQuery(sql_xmgb.toString())
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		// 如果没有对应的，则使用默认规则
		if (list_xmgb.size() == 0 || list_xmgb == null) {
//			StringBuffer sql_xmgb_default = new StringBuffer();
//			sql_xmgb_default
//					.append("SELECT SFXM,SFXM AS GBXM, SFMC AS XMMC FROM GY_SFXM");
			List<Map<String, Object>> list_xmgb_default = ss
					.createSQLQuery("select SFXM,GBXM,XMMC from (select GY_SFXM.SFXM as SFXM,GY_SFXM.SFXM as GBXM,GY_SFXM.SFMC AS XMMC FROM MS_MZXX MS_MZXX,MS_SFMX MS_SFMX,GY_SFXM GY_SFXM "+sql_mxzf.toString()+" and MS_SFMX.SFXM=GY_SFXM.SFXM group by GY_SFXM.SFXM,GY_SFXM.SFMC union select SFXM SFXM, SFXM GBXM,SFMC XMMC from gy_sfxm where SFXM in (select  c.fygb from ms_ghks a left join gy_ylsf c on a.zlsfxm = c.fyxh, ms_ghmx b where b.ksdm = a.ksdm and c.fygb is not null))")
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
					.list();
			return list_xmgb_default;
		}
		return list_xmgb;
	}

	/**
	 * 打印明细作废
	 * 
	 * @author gaof
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws DataAccessException
	 * @throws JRException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected List<JasperPrint> doPrintMxZf(Map<String, Object> request,
			Map<String, Object> response, HashMap<String, Object> config, String chargeMode_sf,String chargeMode_zf)
			throws IOException, DataAccessException, JRException,
			IllegalAccessException, InstantiationException {
		// 取到机构id
		Context ctx = ContextUtils.getContext();
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		if(ss == null) {
			SessionFactory sf = AppContextHolder.getBean(
					AppContextHolder.DEFAULT_SESSION_FACTORY,
					SessionFactory.class);
			ss = sf.openSession();
			ctx.put(Context.DB_SESSION, ss);
		}
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid = user.getManageUnitId();

		String title ="性质费用核算表"; //(String) config.get("title");
		HashMap<String, Object> requestData = (HashMap<String, Object>) config
				.get("requestData");

		// 获取表单参数
		String dateFrom = (String) requestData.get("dateFrom");
		String dateTo = (String) requestData.get("dateTo");
		List<Object> brxzValue = (List<Object>) requestData.get("brxzValue");

		StringBuffer sql = new StringBuffer();
		sql.append(
				"SELECT MS_MZXX.FPHM,MS_SFMX.SFXM,MS_MZXX.BRXM,MS_MZXX.FYZH,to_char(MS_MZXX.SFRQ,'yyyy-mm-dd hh24:mi:ss') as SFRQ,"
						+ " '' AS   DWMC, sum(MS_SFMX.ZJJE) as ZJJE, sum(MS_SFMX.ZFJE) as ZFJE"
						+ " FROM MS_MZXX,MS_SFMX WHERE ( MS_MZXX.MZXH = MS_SFMX.MZXH ) "
						+ " AND  "+chargeMode_sf+" >= to_date('")
				.append(dateFrom)
				.append("','yyyy-mm-dd HH24:mi:ss')")
				.append(" And "+chargeMode_sf+"  <=  to_date('")
				.append(dateTo)
				.append("','yyyy-mm-dd HH24:mi:ss')")
				.append(" and MS_MZXX.JGID = ")
				.append(uid)
				.append(" And MS_MZXX.BRXZ in (")
				.append(brxzValue.toString().substring(1,
						brxzValue.toString().length() - 1))
				.append(")")
				.append(" group by MS_MZXX.FPHM,MS_SFMX.SFXM,MS_MZXX.SFRQ,MS_MZXX.BRXM,MS_MZXX.FYZH order by MS_MZXX.SFRQ");

		List<Map<String, Object>> list_data = ss.createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

		StringBuffer sql_zf = new StringBuffer();
		sql_zf.append(
				"SELECT MS_MZXX.FPHM,MS_SFMX.SFXM,MS_MZXX.BRXM,MS_MZXX.FYZH,MS_MZXX.SFRQ,"
						+ " '' AS   DWMC, sum(MS_SFMX.ZJJE) as ZJJE, sum(MS_SFMX.ZFJE) as ZFJE"
						+ " FROM MS_MZXX,MS_SFMX,MS_ZFFP WHERE ( MS_MZXX.MZXH = MS_SFMX.MZXH ) AND"
						+ " ( MS_MZXX.MZXH = MS_ZFFP.MZXH ) AND  "+chargeMode_zf+" >= to_date('")
				.append(dateFrom)
				.append("','yyyy-mm-dd HH24:mi:ss')")
				.append(" And "+chargeMode_zf+"  <=  to_date('")
				.append(dateTo)
				.append("','yyyy-mm-dd HH24:mi:ss')")
				.append(" and MS_ZFFP.JGID = ")
				.append(uid)
				.append(" And MS_MZXX.BRXZ in (")
				.append(brxzValue.toString().substring(1,
						brxzValue.toString().length() - 1))
				.append(")")
				.append(" group by MS_MZXX.FPHM,MS_SFMX.SFXM,MS_MZXX.SFRQ,MS_MZXX.BRXM,MS_MZXX.FYZH order by MS_MZXX.SFRQ");

		List<Map<String, Object>> list_data_zf = ss
				.createSQLQuery(sql_zf.toString())
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		StringBuffer sql_mxzf = new StringBuffer();
		sql_mxzf.append("WHERE ( MS_MZXX.MZXH = MS_SFMX.MZXH ) "
				+ " AND  "+chargeMode_sf+" >= to_date('")
		.append(dateFrom)
		.append("','yyyy-mm-dd HH24:mi:ss')")
		.append(" And "+chargeMode_sf+"  <=  to_date('")
		.append(dateTo)
		.append("','yyyy-mm-dd HH24:mi:ss')")
		.append(" and MS_MZXX.JGID = ")
		.append(uid)
		.append(" And MS_MZXX.BRXZ in (")
		.append(brxzValue.toString().substring(1,
				brxzValue.toString().length() - 1))
		.append(")");
		List<Map<String, Object>> list_xmgb = getListXmgb(bbbh_mxzf, uid,sql_mxzf.toString(), ss);
		List<Map<String, Object>> list_column = getListColumn(bbbh_mxzf, uid,sql_mxzf.toString(),
				ss, list_xmgb);
		StringBuffer sql_date = new StringBuffer();
		sql_date.append(
				"SELECT to_char(MS_MZXX.SFRQ,'yyyy-mm-dd hh24:mi:ss') as SFRQ FROM MS_MZXX MS_MZXX WHERE "
						+ chargeMode_sf+" >= to_date('")
				.append(dateFrom)
				.append("','yyyy-mm-dd HH24:mi:ss')")
				.append(" And "+chargeMode_sf+"  <=  to_date('")
				.append(dateTo)
				.append("','yyyy-mm-dd HH24:mi:ss')")
				.append(" and MS_MZXX.JGID = ")
				.append(uid)
				.append(" And MS_MZXX.BRXZ in (")
				.append(brxzValue.toString().substring(1,
						brxzValue.toString().length() - 1))
				.append(")");
		List<Map<String, Object>> list_date = ss.createSQLQuery(sql_date.toString())
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		// 在数据源上加上作废表单数据
		for (int i = 0; i < list_data_zf.size(); i++) {
			Map<String, Object> data_zf = list_data_zf.get(i);
			for (int j = 0; j < list_data.size(); j++) {
				Map<String, Object> data = list_data.get(j);
				if (data.get("FPHM").equals(data_zf.get("FPHM"))
						&& data.get("SFXM").equals(data_zf.get("SFXM"))) {
//					BigDecimal data_value = new BigDecimal(data.get("ZJJE")+"");
//					BigDecimal data_zf_value =new BigDecimal(data_zf.get("ZJJE")+"");
//					//去除空行  2014-1-8
//					if(data_value==data_zf_value){
						list_data.remove(data);
//					}
					// 减去作废金额
//					data.put("ZJJE", String.format("%1$.2f",data_value.subtract(data_zf_value)));
					break;
				}
//				if (j == list_data.size() - 1) {
//					list_data.add(data_zf);
//					list_date.add(data_zf);
//				}
			}
		}

		// 用于存放列,第一个列会在分页后继续显示
		LinkedHashMap<String, ColumnModel> map = new LinkedHashMap<String, ColumnModel>();
		// l_data 用于存放数据
		List<HashMap<String, Object>> l_data = new ArrayList<HashMap<String, Object>>();

		// 生成表结构
		ColumnModel cm0 = new ColumnModel();
		cm0.setName("TOTAL");
		cm0.setText("");
		cm0.setWdith(30);
		map.put("TOTAL", cm0);
		ColumnModel cm2 = new ColumnModel();
		cm2.setName("BRXM");
		cm2.setText("病人姓名");
		cm2.setWdith(60);
		map.put("BRXM", cm2);
		ColumnModel cm3 = new ColumnModel();
		cm3.setName("FYZH");
		cm3.setText("医疗证号");
		cm3.setWdith(60);
		map.put("FYZH", cm3);
		ColumnModel cm4 = new ColumnModel();
		cm4.setName("SFRQ");
		cm4.setText("收费日期");
		cm4.setWdith(100);
		map.put("SFRQ", cm4);
		for (Map<String, Object> column : list_column) {
			ColumnModel cm = new ColumnModel();
			cm.setName(column.get("GBXM").toString());
			cm.setText((String) column.get("XMMC"));
			cm.setWdith(60);
//			cm.setHORIZONTAL_ALIGN(HorizontalAlignEnum.RIGHT);
			map.put(column.get("GBXM").toString(), cm);
		}

		int i = 1;
		
		for(Map<String, Object> m_date : list_date){
			HashMap<String, Object> data = new HashMap<String, Object>();
			for (Map<String, Object> m_data : list_data) {
				if(m_date.get("SFRQ").equals(m_data.get("SFRQ"))){
					for (Map<String, Object> m_xmgb : list_xmgb) {
						if (m_data.get("SFXM").equals(m_xmgb.get("SFXM"))) {
							data.put("BRXM", m_data.get("BRXM"));
							data.put("FYZH", m_data.get("FYZH"));
							data.put("SFRQ", m_data.get("SFRQ"));
							String GBXM_str = String.format("%1$.2f",Double.parseDouble(m_data.get("ZJJE")+""));
							data.put(m_xmgb.get("GBXM").toString(), GBXM_str);
							
						}
					}
				}
			}
			if(data.size()>0){
				data.put("TOTAL", i+"");
				l_data.add(data);
				i++;
			}
		}
		// 去除为0的数据项
		for (Map<String, Object> data : l_data) {
			Set<String> key = data.keySet();
			List<String> list_s = new ArrayList<String>();
			for (Iterator it = key.iterator(); it.hasNext();) {

				String s = (String) it.next();
				if ((data.get(s) + "")
						.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$")
						&& parseDouble(data.get(s)) == 0) {
					list_s.add(s);
				}
			}
			for (String str : list_s) {
				data.remove(str);
			}
		}

		// 计算统计
		HashMap<String, Object> dataTotal = new HashMap<String, Object>();
		dataTotal.put("TOTAL", "合计");
		for (Map<String, Object> column : list_column) {
			BigDecimal value = new BigDecimal(0);
			for (int j = 0; j < l_data.size(); j++) {
				Map<String, Object> d = l_data.get(j);
				if (d.get(column.get("GBXM").toString()) != null) {
					value = value.add(new BigDecimal(d.get(column.get("GBXM")+"")+""));
				}
			}
			dataTotal.put(column.get("GBXM").toString(), String.format("%1$.2f",value));
		}
		l_data.add(dataTotal);

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
//		PrintUtil.exportToMap<String, Object>(1, prints, request, response,
//				title);
		ss.close();
		return prints;
	}

	/**
	 * 打印汇总作废
	 * 
	 * @author gaof
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws DataAccessException
	 * @throws JRException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected List<JasperPrint> doPrintHzZf(Map<String, Object> request,
			Map<String, Object> response, HashMap<String, Object> config, String chargeMode_sf,String chargeMode_zf)
			throws IOException, DataAccessException, JRException,
			IllegalAccessException, InstantiationException {
		// 取到机构id
		Context ctx = ContextUtils.getContext();
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		if(ss == null) {
			SessionFactory sf = AppContextHolder.getBean(
					AppContextHolder.DEFAULT_SESSION_FACTORY,
					SessionFactory.class);
			ss = sf.openSession();
			ctx.put(Context.DB_SESSION, ss);
		}
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid = user.getManageUnitId();

		// 获取表单参数
		String title ="性质费用核算表"; //(String) config.get("title");
		HashMap<String, Object> requestData = (HashMap<String, Object>) config
				.get("requestData");
		String dateFrom = (String) requestData.get("dateFrom");
		String dateTo = (String) requestData.get("dateTo");
		List<Object> brxzValue = (List<Object>) requestData.get("brxzValue");


		StringBuffer sql = new StringBuffer();
		sql.append(
				"SELECT MS_SFMX.SFXM,MS_MZXX.BRXM,MS_MZXX.FYZH,'' AS   DWMC,' ' as ZHXM,"
						+ " sum(MS_SFMX.ZJJE) as ZJJE,sum(MS_SFMX.ZFJE) as ZFJE FROM MS_MZXX,MS_SFMX "
						+ " WHERE ( MS_MZXX.MZXH = MS_SFMX.MZXH ) "
						+ " AND  "+chargeMode_sf+"  >= to_date('")
				.append(dateFrom)
				.append("','yyyy-mm-dd HH24:mi:ss')")
				.append(" And "+chargeMode_sf+"  <=  to_date('")
				.append(dateTo)
				.append("','yyyy-mm-dd HH24:mi:ss')")
				.append(" and MS_MZXX.JGID = ")
				.append(uid)
				.append(" And MS_MZXX.BRXZ in (")
				.append(brxzValue.toString().substring(1,
						brxzValue.toString().length() - 1)).append(")")
				.append(" group by MS_SFMX.SFXM,MS_MZXX.BRXM,MS_MZXX.FYZH");

		List<Map<String, Object>> list_data = ss.createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

		StringBuffer sql_zf = new StringBuffer();
		sql_zf.append(
				"SELECT MS_SFMX.SFXM,MS_MZXX.BRXM,MS_MZXX.FYZH,'' AS   DWMC,' ' as ZHXM,"
						+ " sum(MS_SFMX.ZJJE) as ZJJE,sum(MS_SFMX.ZFJE) as ZFJE FROM MS_MZXX,MS_SFMX,MS_ZFFP"
						+ " WHERE ( MS_MZXX.MZXH = MS_SFMX.MZXH ) AND ( MS_MZXX.MZXH = MS_ZFFP.MZXH )"
						+ " AND  "+chargeMode_zf+" >= to_date('")
				.append(dateFrom)
				.append("','yyyy-mm-dd HH24:mi:ss')")
				.append(" And "+chargeMode_zf+"  <=  to_date('")
				.append(dateTo)
				.append("','yyyy-mm-dd HH24:mi:ss')")
				.append(" and MS_ZFFP.JGID = ")
				.append(uid)
				.append(" And MS_MZXX.BRXZ in (")
				.append(brxzValue.toString().substring(1,
						brxzValue.toString().length() - 1)).append(")")
				.append(" group by MS_SFMX.SFXM,MS_MZXX.BRXM,MS_MZXX.FYZH");

		List<Map<String, Object>> list_data_zf = ss
				.createSQLQuery(sql_zf.toString())
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		StringBuffer sql_mxzf = new StringBuffer();
		sql_mxzf.append("WHERE ( MS_MZXX.MZXH = MS_SFMX.MZXH ) "
				+ " AND  "+chargeMode_sf+" >= to_date('")
		.append(dateFrom)
		.append("','yyyy-mm-dd HH24:mi:ss')")
		.append(" And "+chargeMode_sf+"  <=  to_date('")
		.append(dateTo)
		.append("','yyyy-mm-dd HH24:mi:ss')")
		.append(" and MS_MZXX.JGID = ")
		.append(uid)
		.append(" And MS_MZXX.BRXZ in (")
		.append(brxzValue.toString().substring(1,
				brxzValue.toString().length() - 1))
		.append(")");
		List<Map<String, Object>> list_xmgb = getListXmgb(bbbh_hzzf, uid,sql_mxzf.toString(), ss);
		List<Map<String, Object>> list_column = getListColumn(bbbh_hzzf, uid,sql_mxzf.toString(),
				ss, list_xmgb);

		// 在数据源上加上作废表单数据
		for (int i = 0; i < list_data_zf.size(); i++) {
			Map<String, Object> data_zf = list_data_zf.get(i);
			for (int j = 0; j < list_data.size(); j++) {
				Map<String, Object> data = list_data.get(j);
				String zhxm = (String) data.get("FYZH") + data.get("BRXM");
				String zhxm_zf = (String) data_zf.get("FYZH")
						+ data_zf.get("BRXM");
				if (zhxm_zf.equals(zhxm)
						&& data.get("SFXM").equals(data_zf.get("SFXM"))) {
					BigDecimal data_value = new BigDecimal(data.get("ZJJE")+"");
					BigDecimal data_zf_value =new BigDecimal(data_zf.get("ZJJE")+"");
					// 减去作废金额
					data.put("ZJJE", data_value.subtract(data_zf_value));
					break;
				}
				if (j == list_data.size() - 1) {
					list_data.add(data_zf);
				}
			}
		}

		// 用于存放列,第一个列会在分页后继续显示
		LinkedHashMap<String, ColumnModel> map = new LinkedHashMap<String, ColumnModel>();
		// l_data 用于存放数据
		List<HashMap<String, Object>> l_data = new ArrayList<HashMap<String, Object>>();

		// 生成表结构
		ColumnModel cm0 = new ColumnModel();
		cm0.setName("TOTAL");
		cm0.setText("");
		cm0.setWdith(30);
		map.put("TOTAL", cm0);
		ColumnModel cm2 = new ColumnModel();
		cm2.setName("BRXM");
		cm2.setText("病人姓名");
		cm2.setWdith(60);
		map.put("BRXM", cm2);
		ColumnModel cm3 = new ColumnModel();
		cm3.setName("FYZH");
		cm3.setText("医疗证号");
		cm3.setWdith(60);
		map.put("FYZH", cm3);
		for (Map<String, Object> column : list_column) {
			ColumnModel cm = new ColumnModel();
			cm.setName(column.get("GBXM").toString());
			cm.setText((String) column.get("XMMC"));
			cm.setWdith(60);
			map.put(column.get("GBXM").toString(), cm);
		}

		int i = 1;
		for (Map<String, Object> m : list_data) {
			boolean isExist = false;
			int existIndex = 0;
			// 遍历list_xmgb集合找到对应的归并项目
			for (Map<String, Object> m_xmgb : list_xmgb) {
				if (m.get("SFXM").equals(m_xmgb.get("SFXM"))) {
					// 如果l_data中没有元素
					if (l_data.size() == 0) {
						HashMap<String, Object> data = new HashMap<String, Object>();
						data.put("TOTAL", i+"");
						data.put("BRXM", m.get("BRXM"));
						data.put("FYZH", m.get("FYZH"));
						data.put(m_xmgb.get("GBXM").toString(), String.format("%1$.2f",Double.parseDouble(m.get("ZJJE")+"")));
						l_data.add(data);
						i++;
						continue;
					}
					// 如果l_data中有元素
					if (l_data.size() > 0) {
						// 遍历报表的数据集合：l_data，如果此病人姓名已存在于l_data，则全部累加
						for (int j = 0; j < l_data.size(); j++) {
							Map<String, Object> d = l_data.get(j);
							// 如果有该病人姓名
							if (d.get("BRXM").equals(m.get("BRXM"))) {
								isExist = true;
								existIndex = j;
								break;
							}
						}
					}
					// 如果有该病人姓名
					if (isExist) {
						HashMap<String, Object> existData = l_data
								.get(existIndex);
						// 如果该病人姓名已经有这个归并项目
						if (existData
								.containsKey(m_xmgb.get("GBXM").toString())) {
							BigDecimal oldValue = new BigDecimal(existData
									.get(m_xmgb.get("GBXM")+"")+"");
							BigDecimal newValue = new BigDecimal(m.get("ZJJE")+"");
							existData.put(m_xmgb.get("GBXM").toString(),
									String.format("%1$.2f",oldValue.add(newValue)));
						} else {// 如果没有这个归并项目
							existData.put(m_xmgb.get("GBXM").toString(),
									String.format("%1$.2f",Double.parseDouble(m.get("ZJJE")+"")));
						}
					} else {// 如果没有该病人姓名
						HashMap<String, Object> data = new HashMap<String, Object>();
						data.put("TOTAL", i+"");
						if (m.get("SFXM").equals(m_xmgb.get("SFXM"))) {
							data.put("BRXM", m.get("BRXM"));
							data.put("FYZH", m.get("FYZH"));
							data.put(m_xmgb.get("GBXM").toString(),
									String.format("%1$.2f",m.get("ZJJE")));
						}
						l_data.add(data);
						i++;
					}
				}
			}
		}
		// 去除为0的数据项
		for (Map<String, Object> data : l_data) {
			Set<String> key = data.keySet();
			List<String> list_s = new ArrayList<String>();
			for (Iterator it = key.iterator(); it.hasNext();) {

				String s = (String) it.next();
				if ((data.get(s) + "")
						.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$")
						&& parseDouble(data.get(s)) == 0) {
					list_s.add(s);
				}
			}
			for (String str : list_s) {
				data.remove(str);
			}
		}
		// 计算统计
		HashMap<String, Object> dataTotal = new HashMap<String, Object>();
		dataTotal.put("TOTAL", "合计");
		for (Map<String, Object> column : list_column) {
			BigDecimal value = new BigDecimal(0);
			for (int j = 0; j < l_data.size(); j++) {
				Map<String, Object> d = l_data.get(j);
				if (d.get(column.get("GBXM").toString()) != null) {
					value = value.add(new BigDecimal(d.get(column.get("GBXM")+"")+""));
				}
			}
			dataTotal.put(column.get("GBXM").toString(), String.format("%1$.2f",value));
		}
		l_data.add(dataTotal);

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
//		PrintUtil.exportToMap<String, Object>(1, prints, request, response,
//				title);
		ss.close();
		return prints;
	}

	/**
	 * 打印性质费用作废表单
	 *  
	 * @author gaof
	 * @param request
	 * @param response
	 * @param chargeMode 数据核算方式 ： 收费日期 | 汇总日期 
	 * @throws IOException
	 * @throws DataAccessException
	 * @throws JRException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected List<JasperPrint> doPrintZf(Map<String, Object> request,
			Map<String, Object> response, HashMap<String, Object> config
			, String chargeMode_sf,String chargeMode_zf)
			throws IOException, DataAccessException, JRException,
			IllegalAccessException, InstantiationException {
		// 取到机构id
		Context ctx = ContextUtils.getContext();
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		if(ss == null) {
			SessionFactory sf = AppContextHolder.getBean(
					AppContextHolder.DEFAULT_SESSION_FACTORY,
					SessionFactory.class);
			ss = sf.openSession();
			ctx.put(Context.DB_SESSION, ss);
		}
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid = user.getManageUnitId();

		// 获取表单参数
		HashMap<String, Object> requestData = (HashMap<String, Object>) config
				.get("requestData");
		String title ="性质费用核算表"; //(String) config.get("title");
		//String dateFrom = (String) requestData.get("dateFrom");
		//String dateTo = (String) requestData.get("dateTo");
		String dateFrom = new String(requestData.get("dateFrom").toString().getBytes("iso8859_1"), "UTF-8");
		String dateTo = new String(requestData.get("dateTo").toString().getBytes("iso8859_1"), "UTF-8");
		List<Object> brxzValue = (List<Object>) requestData.get("brxzValue");
//		logger.info("病人性质：" + brxzValue.toString());


		StringBuffer sql = new StringBuffer();
		sql.append(
				"select XZMC,BRXZ,SFXM,ZJJE,ZFJE from (select GY_BRXZ.XZMC,MS_MZXX.BRXZ,MS_SFMX.SFXM,"
						+ "sum(MS_SFMX.ZJJE) as ZJJE,sum(MS_SFMX.ZFJE) as ZFJE"
						+ " from MS_MZXX,MS_SFMX,GY_BRXZ"
						+ " where GY_BRXZ.BRXZ = MS_MZXX.BRXZ and MS_MZXX.MZXH = MS_SFMX.MZXH")
				.append(" AND " + chargeMode_sf + " >= to_date('")
				.append(dateFrom)
				.append("','yyyy-mm-dd HH24:mi:ss')")
				.append(" And " + chargeMode_sf + "  <= to_date('")
				.append(dateTo)
				.append("','yyyy-mm-dd HH24:mi:ss')")
				.append(" And MS_MZXX.JGID = ")
				.append(uid)
				.append(" And MS_MZXX.BRXZ in (")
				.append(brxzValue.toString().substring(1,
						brxzValue.toString().length() - 1)).append(")")
				.append(" and MS_MZXX.ZFPB='0' ")
				.append(" group by GY_BRXZ.XZMC,MS_MZXX.BRXZ,MS_SFMX.SFXM")
				.append(" union all ")
		        .append("select d.xzmc xzmc,b.brxz BRXZ,13 as SFXM, sum(b.xjje+b.zpje+b.zhje+b.qtys) as ZJJE ,sum(b.xjje+b.zpje+b.zhje+b.qtys) as ZFJE")
		         .append(" from ms_ghmx b,gy_brxz d")
		         .append(" where b.brxz = d.brxz and b.jgid =")
		         .append(uid)
		         .append(" and b.ghsj >= to_date('")
		         .append(dateFrom)
		         .append("','yyyy-mm-dd HH24:mi:ss')")
		         .append(" And b.ghsj <=  to_date('")
		         .append(dateTo)
				.append("','yyyy-mm-dd HH24:mi:ss')")
				.append(" And b.BRXZ in (")
				.append(brxzValue.toString().substring(1,
						brxzValue.toString().length() - 1)).append(")")
				.append(" and b.thbz = '0' group by D.XZMC, B.BRXZ)");

		List<Map<String, Object>> list_data = ss.createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

//		StringBuffer sql_zf = new StringBuffer();
//		sql_zf.append(
//				"select GY_BRXZ.XZMC,MS_MZXX.BRXZ,MS_SFMX.SFXM,"
//						+ "sum(MS_SFMX.ZJJE) as ZJJE,sum(MS_SFMX.ZFJE) as ZFJE"
//						+ " from MS_MZXX,MS_SFMX,GY_BRXZ,MS_ZFFP"
//						+ " where GY_BRXZ.BRXZ = MS_MZXX.BRXZ and	MS_MZXX.MZXH = MS_SFMX.MZXH"
//						+ " and MS_MZXX.MZXH = MS_ZFFP.MZXH and	MS_MZXX.ZFPB = 1")
//				.append(" AND  	" + chargeMode_zf + "  >= to_date('")
//			
//				.append(dateFrom)
//				.append("','yyyy-mm-dd HH24:mi:ss')")
//				.append(" And 	" + chargeMode_zf + "  <= to_date('")
//				.append(dateTo)
//				.append("','yyyy-mm-dd HH24:mi:ss')")
//				.append(" And MS_MZXX.JGID = ")
//				.append(uid)
//				.append(" And MS_MZXX.BRXZ in (")
//				.append(brxzValue.toString().substring(1,
//						brxzValue.toString().length() - 1)).append(")")
//				.append(" group by GY_BRXZ.XZMC,MS_MZXX.BRXZ,MS_SFMX.SFXM");
//
//		List<Map<String, Object>> list_data_zf = ss
//				.createSQLQuery(sql_zf.toString())
//				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		StringBuffer sql_mxzf = new StringBuffer();
		sql_mxzf.append("WHERE ( MS_MZXX.MZXH = MS_SFMX.MZXH ) "
				+ " AND  " + chargeMode_sf + " >= to_date('")
		.append(dateFrom)
		.append("','yyyy-mm-dd HH24:mi:ss')")
		.append(" And " + chargeMode_sf + "  <=  to_date('")
		.append(dateTo)
		.append("','yyyy-mm-dd HH24:mi:ss')")
		.append(" and MS_MZXX.JGID = ")
		.append(uid)
		.append(" And MS_MZXX.BRXZ in (")
		.append(brxzValue.toString().substring(1,
				brxzValue.toString().length() - 1))
		.append(")");
		List<Map<String, Object>> list_xmgb = getListXmgb(bbbh_xzzf, uid,sql_mxzf.toString(), ss);
		List<Map<String, Object>> list_column = getListColumn(bbbh_xzzf, uid,sql_mxzf.toString(),
				ss, list_xmgb);

		// 在数据源上加上作废表单数据
//		for (int i = 0; i < list_data_zf.size(); i++) {
//			Map<String, Object> data_zf = list_data_zf.get(i);
//			for (int j = 0; j < list_data.size(); j++) {
//				Map<String, Object> data = list_data.get(j);
//				if (data.get("BRXZ").equals(data_zf.get("BRXZ"))
//						&& data.get("SFXM").equals(data_zf.get("SFXM"))) {
//					BigDecimal data_value = new BigDecimal(data.get("ZJJE")+"");
//					BigDecimal data_zf_value = new BigDecimal(data_zf.get("ZJJE")+"");
//					// 减去作废金额
//					data.put("ZJJE", data_value.subtract(data_zf_value));
//					break;
//				}
//				if (j == list_data.size() - 1) {
//					list_data.add(data_zf);
//				}
//			}
//		}

		// 用于存放列,第一个列会在分页后继续显示
		LinkedHashMap<String, ColumnModel> map = new LinkedHashMap<String, ColumnModel>();

		// 生成表结构
		ColumnModel cm0 = new ColumnModel();
		cm0.setName("TOTAL");
		cm0.setText("");
		cm0.setWdith(30);
		map.put("TOTAL", cm0);
		ColumnModel cm1 = new ColumnModel();
		cm1.setName("BRXZ");
		cm1.setText("病人性质");
		// cm1.setWdith(120);
		map.put("BRXZ", cm1);
		for (Map<String, Object> column : list_column) {
			ColumnModel cm = new ColumnModel();
			cm.setName(column.get("GBXM").toString());
			cm.setText((String) column.get("XMMC"));
			cm.setWdith(60);
			// cm.setHORIZONTAL_ALIGN((byte) -1);
			map.put(column.get("GBXM").toString(), cm);
		}
		ColumnModel cm2 = new ColumnModel();
		cm2.setName("ColumnTotal");
		cm2.setText("合计");
		cm2.setWdith(100);
		map.put("ColumnTotal", cm2);

		// l_data 用于存放数据
		List<HashMap<String, Object>> l_data = new ArrayList<HashMap<String, Object>>();

		int i = 1;
		for (Map<String, Object> m : list_data) {
			boolean isExist = false;
			int existIndex = 0;
			// 遍历list_xmgb集合找到对应的归并项目
			for (Map<String, Object> m_xmgb : list_xmgb) {
				if (m.get("SFXM").equals(m_xmgb.get("SFXM"))) {
					// 如果l_data中没有元素
					if (l_data.size() == 0) {
						HashMap<String, Object> data = new HashMap<String, Object>();
						data.put("TOTAL", i);
						data.put("BRXZ", m.get("XZMC"));
						data.put(m_xmgb.get("GBXM").toString(), String.format("%1$.2f",m.get("ZJJE")));
						l_data.add(data);
						i++;
						continue;
					}
					// 如果l_data中有元素
					if (l_data.size() > 0) {
						// 遍历报表的数据集合：l_data，如果此病人姓名已存在于l_data，则全部累加
						for (int j = 0; j < l_data.size(); j++) {
							Map<String, Object> d = l_data.get(j);
							// 如果有该病人性质
							if (d.get("BRXZ").equals(m.get("XZMC"))) {
								isExist = true;
								existIndex = j;
								break;
							}
						}
					}
					// 如果有该病人性质
					if (isExist) {
						HashMap<String, Object> existData = l_data
								.get(existIndex);
						// 如果该病人姓名已经有这个归并项目
						if (existData
								.containsKey(m_xmgb.get("GBXM").toString())) {
							BigDecimal oldValue = new BigDecimal(existData
									.get(m_xmgb.get("GBXM")+"")+"");
							BigDecimal newValue = new BigDecimal(m.get("ZJJE")+"");
							existData.put(m_xmgb.get("GBXM").toString(),
									String.format("%1$.2f",oldValue.add(newValue)));
						} else {// 如果没有这个归并项目
							existData.put(m_xmgb.get("GBXM").toString(),
									String.format("%1$.2f",m.get("ZJJE")));
						}
					} else {// 如果没有该病人性质
						HashMap<String, Object> data = new HashMap<String, Object>();
						data.put("TOTAL", i);
						if (m.get("SFXM").equals(m_xmgb.get("SFXM"))) {
							data.put("BRXZ", m.get("XZMC"));
							data.put(m_xmgb.get("GBXM").toString(),
									String.format("%1$.2f",m.get("ZJJE")));
						}
						l_data.add(data);
						i++;
					}
				}
			}
		}
		// 去除为0的数据项
		for (Map<String, Object> data : l_data) {
			Set<String> key = data.keySet();
			List<String> list_s = new ArrayList<String>();
			for (Iterator it = key.iterator(); it.hasNext();) {

				String s = (String) it.next();
				if ((data.get(s) + "")
						.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$")
						&& parseDouble(data.get(s)) == 0) {
					list_s.add(s);
				}
			}
			for (String str : list_s) {
				data.remove(str);
			}
		}
		// 行数据统计
		for (Map<String, Object> data : l_data) {
			Set<String> key = data.keySet();
			Double columnTotal = 0d;
			for (Iterator it = key.iterator(); it.hasNext();) {
				String s = (String) it.next();
				if ((data.get(s) + "")
						.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$")
						&& parseDouble(data.get(s)) != 0&&!"TOTAL".equals(s)) {
					Double d = parseDouble(data.get(s));
					columnTotal += d;
				}
			}
			data.put("ColumnTotal", String.format("%1$.2f", columnTotal));
		}
		Double columnTotalAll = 0d;
		for (Map<String, Object> data : l_data) {
			String s = "ColumnTotal";
			if ((data.get(s) + "")
					.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$")
					&& parseDouble(data.get(s)) != 0) {
				columnTotalAll += parseDouble(data.get(s));
			}
		}
		

		// 计算统计
		HashMap<String, Object> dataTotal = new HashMap<String, Object>();
		dataTotal.put("TOTAL", "合计");
		for (Map<String, Object> column : list_column) {
			BigDecimal value = new BigDecimal(0);
			for (int j = 0; j < l_data.size(); j++) {
				Map<String, Object> d = l_data.get(j);
				if (d.get(column.get("GBXM").toString()) != null) {
					value = value.add(new BigDecimal((String)d.get(column.get("GBXM")
							.toString())));
				}
			}
			dataTotal.put(column.get("GBXM").toString(), value);
		}
		dataTotal.put("ColumnTotal", String.format("%1$.2f", columnTotalAll));
		l_data.add(dataTotal);

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
//		PrintUtil.exportToMap<String, Object>(1, prints, request, response,
//				title);
		ss.close();
		return prints;
	}

//	private Context getContext(Map<String, Object> request) {
//		Context ctx = Dispatcher.createContext(request);
//		ctx.put(Context.APP_CONTEXT, wac);
//		User user = Dispatcher.getUser(request);
//		if (user != null) {
//			Context userCtx = new UserContext(Dispatcher.getUser(request));
//			ctx.putCtx("user", userCtx);
//		}
//		SessionFactory sf = (SessionFactory) wac.getBean("mySessionFactory");
//		ctx.put(Context.DB_SESSION, sf.openSession());
//		return ctx;
//	}

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
		List<JasperDesign> designs, String title,ColumnModel[] columnModel, boolean isSeparate)
		throws JRException 
	{
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
		parseTitleText(titleText);
		titleBand.addElement(titleText);
		int totalWidth = 0;
		if (!isSeparate) {
			for (int i = 0; i < columnModel.length; i++) {
				if (columnModel[i].isHide()) {
					continue;
				}
				int width = columnModel[i].getWdith();
				totalWidth += width;
				// 列标题
				JRDesignStaticText staticText = DynaGridPrintUtil
						.getJRDesignStaticText(columnModel[i].getText(),
								fontSize, isColumnHeaderFontBond);
				staticText.setWidth(width);
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
				textField.setWidth(width);
				textField.setX(totalWidth - width);
				textField.setHorizontalAlignment(HorizontalAlignEnum.RIGHT);
				textField.getLineBox().getPen().setLineWidth(1f);
				textField.getLineBox().getPen()
						.setLineStyle(LineStyleEnum.SOLID);
				// textField.setStretchWithOverflow(true); //是否换行打印
				detailBand.addElement(textField);
				// 底部页码
				getPageFooterTextField(pageFooter);
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

	private static void parseTitleText(JRDesignStaticText titleText) {
		titleText.setHorizontalAlignment(HorizontalAlignEnum.CENTER);
		titleText.setVerticalAlignment(VerticalAlignEnum.MIDDLE);
		titleText.setHeight(titleHeight);
		titleText.setWidth(columnWidth);
		titleText.setX(0);
		titleText.setY(0);
	}

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

	public int parseInt(Object o) {
		if (o == null) {
			return 0;
		}
		return Integer.parseInt(o + "");
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
}
