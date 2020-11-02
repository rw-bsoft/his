package ctd.print;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.design.JasperDesign;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.util.AppContextHolder;
import ctd.util.context.Context;

public class PrintExporter {

	private final static Logger LOGGER = LoggerFactory.getLogger(PrintExporter.class);

	public static void export(HashMap<String, Object> request, HashMap<String, Object> response, Context ctx, List<String> pages,
			int printType, HttpServletRequest req, HttpServletResponse res,String title) throws PrintException, JRException, IOException {
		List<JasperPrint> list = new ArrayList<JasperPrint>();
		for (String p : pages) {
			Print print = PrintController.instance().getPrint(p);
			if (print == null) {
				LOGGER.error("[" + p + "] print missing.");
				throw new PrintException(105, "print[" + p + "] missing.");
			}
			request.put("jrxml", p);	//?
			List<JasperPrint> jps = (List<JasperPrint>) getPrints(print, request, response, ctx);
			list.addAll(jps);
		}
		PrintUtil.exportToHttpServletResponse(printType, list, req, res,title);
	}

	@SuppressWarnings("deprecation")
	private static List<JasperPrint> getPrints(Print print, Map<String, Object> request, Map<String, Object> response,
			Context ctx) throws PrintException, JRException {
		List<JasperPrint> jps = new ArrayList<JasperPrint>();
		String bean = print.getBean(); // 处理类 IHandle的实现类
		String fill = print.getFill(); // 数据填充方式
		IHandler handler = null;
		if (bean != null) {
			try {
				handler = (IHandler) AppContextHolder.getBean(bean);
			} catch (Exception e) {
				LOGGER.error("get Print [" + print.getId() + "] handler instance:" + bean + " failed.", e);
				throw new PrintException(108, "get spring bean[" + bean + "] failed.");
			}
		}
		
		SessionFactory factory = (SessionFactory)AppContextHolder.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY);
		Session session = null;
		try{
			session = factory.openSession();
			ctx.put(Context.DB_SESSION, session);
			/**
			 * add by yangl 自定义报表
			 */
			if(handler instanceof DynamicPrint) {
				return ((DynamicPrint) handler).doPrint(request, response);
			}
			List<JasperDesign> jds = print.getJasperDesigns(); //多模板
			if (jds == null || jds.size() == 0) {
				throw new PrintException(106, "initialize print [" + print.getId()	+ "]'s JasperDesign failed.");
			}
			for (JasperDesign jd : jds) {
				Map<String, Object> fillMap = null;
				JasperPrint jp = null;
				if (handler != null) {
					handler.getParameters(request, response, ctx);
					fillMap = (Map<String, Object>) ((HashMap<String, Object>)response).clone();
				}
				if (Print.TYPE_OF_FILL_CONNECTION.equals(fill)) {
					Connection con = session.connection();
					jp = PrintUtil.getJasperPrint(	PrintUtil.getJasperReport(jd), fillMap, con);
				} else if (Print.TYPE_OF_FILL_DATASOURCE.equals(fill)) {
					if (handler == null) {
						throw new PrintException(110, "print[" + print.getId() + "] can't process ds without bean.");
					}
					List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
					handler.getFields(request, records, ctx);
					JRDataSource ds = PrintUtil.ds(records);
					jp = PrintUtil.getJasperPrint(PrintUtil.getJasperReport(jd),	fillMap, ds);
				} else {
					jp = PrintUtil.getJasperPrint(PrintUtil.getJasperReport(jd),	fillMap);
				}
				jps.add(jp);
			}
		}catch(Exception e){
			LOGGER.error("get Print [" + print.getId() + "] handler instance:" + bean + " failed.", e);
		}finally{
			if(session!= null && session.isOpen()){
				session.close();
			}
		}
		return jps;
	}

}
