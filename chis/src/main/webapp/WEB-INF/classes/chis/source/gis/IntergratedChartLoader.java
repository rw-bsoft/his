package chis.source.gis;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import chis.source.PersistentDataOperationException;

import com.alibaba.fastjson.JSONException;

import ctd.account.user.User;
import ctd.util.context.Context;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class IntergratedChartLoader extends HttpServlet implements Servlet {
//
//	private static final long serialVersionUID = 5823714493803495747L;
//	private static Log logger = LogFactory.getLog(IntergratedChartLoader.class);
//	private static Pattern pattern = Pattern.compile("([^/]+)\\.ichart");
//	private static WebApplicationContext wac;
//
//	public void init(ServletConfig oCfg) throws ServletException {
//		wac = WebApplicationContextUtils.getRequiredWebApplicationContext(oCfg
//				.getServletContext());
//		logger.info("Servlet init");
//	}
//
//	private String getChartId(String url) {
//		Matcher matcher = pattern.matcher(url);
//		if (matcher.find()) {
//			return matcher.group(1);
//		}
//		return null;
//	}
//
//	@SuppressWarnings("unused")
//	public void doGet(HttpServletRequest request, HttpServletResponse response) {
//		HashMap<String, Object> query = parseQueryString(request
//				.getQueryString());
//		doPost(request, response);
//	}
//
//	public void doPost(HttpServletRequest request, HttpServletResponse response) {
//		HashMap<String, Object> query = Dispatcher.parseRequest(request);
//		try {
//			doChartService(request, response, query);
//		} catch (Exception e) {
//			logger.error(e.getMessage(), e);
//		}
//	}
//
//	public static Boolean IsNumeric(String value) {
//		Boolean flag = true;
//		try {
//			Integer.valueOf(value);
//		} catch (Exception e) {
//			flag = false;
//		}
//		return flag;
//	}
//
//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	public List<HashMap> bulidRs(List<HashMap> rs, ReportSchema reportSchema) {
//		for (int i = 0; i < rs.size(); i++) {
//			HashMap rss = rs.get(i);
//			if (reportSchema.getId().equals("Analys_rkzs")) {
//				Long val = Long.parseLong(rss.get("population").toString());
//				Long vas = null;
//				Iterator it = rss.keySet().iterator();
//				for (; it.hasNext();) {
//					String tx = it.next().toString();
//					if (IsNumeric(tx)) {
//						vas = Long.parseLong(rss.get(tx).toString());
//					}
//				}
//				if (val > vas) {
//					rss.put("population", val - vas);
//				} else {
//					rss.put("population", 0);
//				}
//
//			}
//			if ("".equals(rss.get("type_text"))) {
//				rss.put("type_text", "不详");
//			}
//		}
//		return rs;
//	}
//
//	/**
//	 * @param request
//	 * @param response
//	 * @param query
//	 * @throws JSONException
//	 * @throws PersistentDataOperationException
//	 */
//	@SuppressWarnings("rawtypes")
//	private void doChartService(HttpServletRequest request,
//			HttpServletResponse response, HashMap<String, Object> query)
//			throws PersistentDataOperationException {
//		String id = getChartId(request.getRequestURI());
//		if (StringUtils.isEmpty(id)) {
//			logger.error("Request Url's arg [chartId id] missing");
//			return;
//		}
//		// init context
//		Context ctx = initContext(request);
//		Context qCtx = new HashMapContext(query);
//		ctx.putCtx("q", qCtx);
//		ctx.put(Context.APP_CONTEXT, wac);
//
//		ReportSchema reportSchema = null;
//		String code = String.valueOf(query.get("regionCode"));
//		if (code == null || code == "null") {
//			reportSchema = ReportSchemaController.instance().getSchema(id);
//		} else {
//			String hql = "select regionCode,mapSign from EHR_AreaGrid where substring(regionCode,1,"
//					+ code.length()
//					+ ")='"
//					+ code
//					+ "' and (length(regionCode)="
//					+ LayerDic.layerMapping.get(String.valueOf(code.length()))
//					+ ")";
//			HashMap<String, Object> listObj = ServiceUtil.executeSqlObj(hql, ctx);
//			reportSchema = BuildDefine.getCreateSchema(id, listObj, query);
//		}
//
//		// for result
//
//		List<HashMap> rs = reportSchema.run(ctx);
//		bulidRs(rs, reportSchema);
//		if (rs.size() > 0) {
//			try {
//				Configuration freemarker = (Configuration) wac
//						.getBean("freemarkerConfig");
//				String chartTemplate = reportSchema.getChartTemplate();
//				Template t = freemarker.getTemplate(chartTemplate + ".ftl");
//
//				Writer writer = new BufferedWriter(new OutputStreamWriter(
//						response.getOutputStream()));
//				response.addHeader("content-type", "text/xml;charset=gb2312");
//				HashMap<String, Object> data = perpareData(reportSchema, rs);
//				data.put("chartId", query.get("chartId"));
//				data.put("subTitle", reportSchema.getSubTitle(ctx));
//				t.process(data, writer);
//			} catch (Exception e) {
//				logger.error(e.getMessage(), e);
//			}
//		} else {
//			try {
//				response.getWriter().write("<chart/>");
//			} catch (IOException e) {
//				logger.error("Failed to write file.", e);
//			}
//		}
//	}
//
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	private HashMap<String, Object> perpareData(ReportSchema reportSchema,
//			List<HashMap> rs) {
//		HashMap<String, Object> data = new HashMap<String, Object>();
//		data.put("title", reportSchema.getTitle());
//		List l = reportSchema.getHeaders();
//		List ls = new ArrayList();
//		if ("Analys_rkzs".equals(reportSchema.getId())) {
//			HashMap<String, Object> sx = new HashMap<String, Object>();
//			sx = (HashMap<String, Object>) l.get(1);
//			sx.put("alias", "建档数");
//			ls.add(l.get(0));
//			ls.add(l.get(2));
//			ls.add(l.get(1));
//		} else {
//			ls = l;
//		}
//		data.put("headers", ls);
//		int limit = reportSchema.getChartLimit();
//		if (limit == 0) {
//			limit = rs.size();
//		}
//		HashMap diggers = reportSchema.getDiggers();
//		if (diggers != null) {
//			data.put("diggers", diggers);
//		}
//		data.put("rs", rs);
//		data.put("limit", limit);
//		return data;
//	}
//
//	public static HashMap<String, Object> parseQueryString(String q) {
//		HashMap<String, Object> query = new HashMap<String, Object>();
//		query.put("chartId", "");
//		q = q.substring(2);
//		if (q != null) {
//			int p = q.indexOf("@");
//			if (p > -1) {
//				String[] qus = q.split("@");
//				int size = qus.length;
//				for (int i = 0; i < size; i++) {
//					String[] kv = qus[i].split(";");
//					if (kv.length == 2) {
//						try {
//							String key = URLDecoder.decode(kv[0], "utf-8");
//							String value = URLDecoder.decode(kv[1], "utf-8");
//							query.put(key, value);
//						} catch (UnsupportedEncodingException e) {
//							logger.error("parseQueryStringFailed:"
//									+ e.getMessage());
//						}
//					}
//				}
//			} else {
//				String[] kv = q.split(";");
//				if (kv.length == 2) {
//					try {
//						String key = URLDecoder.decode(kv[0], "utf-8");
//						String value = URLDecoder.decode(kv[1], "utf-8");
//						query.put(key, value);
//					} catch (UnsupportedEncodingException e) {
//						logger.error("parseQueryStringFailed:" + e.getMessage());
//					}
//				}
//			}
//		}
//		return query;
//	}
//
//	public static Context initContext(HttpServletRequest request) {
//		Context ctx = Dispatcher.createContext(request);
//		User user = Dispatcher.getUser(request);
//		if (user != null) {
//			Context userCtx = new UserContext(user);
//			ctx.putCtx("user", userCtx);
//		}
//		return ctx;
//	}

}
