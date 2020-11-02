//package chis.source.gis;
//
//import java.io.BufferedWriter;
//import java.io.IOException;
//import java.io.OutputStreamWriter;
//import java.io.UnsupportedEncodingException;
//import java.io.Writer;
//import java.net.URLDecoder;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//import javax.servlet.Servlet;
//import javax.servlet.ServletConfig;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import org.apache.commons.lang.StringUtils;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.hibernate.Query;
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//import org.hibernate.transform.Transformers;
//import org.springframework.web.context.WebApplicationContext;
//import org.springframework.web.context.support.WebApplicationContextUtils;
//import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
//
//import ctd.util.context.Context;
//import freemarker.template.Configuration;
//import freemarker.template.Template;
//
//public class ReportServlet extends HttpServlet implements Servlet {
//
//	private static final long serialVersionUID = 5823714493803495747L;
//	private static Log logger = LogFactory.getLog(ReportServlet.class);
//	private static Pattern pattern = Pattern.compile("([^/]+)\\.etReport");
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
//	public void doGet(HttpServletRequest request, HttpServletResponse response) {
//		HashMap<String, Object> query = parseQueryString(request
//				.getQueryString());
//		doChartService(request, response, query);
//	}
//
//	public void doPost(HttpServletRequest request, HttpServletResponse response) {
//		HashMap<String, Object> query = Dispatcher.parseRequest(request);
//		doChartService(request, response, query);
//	}
//
//	@SuppressWarnings("rawtypes")
//	private void doChartService(HttpServletRequest request,
//			HttpServletResponse response, HashMap<String, Object> query) {
//		String id = getChartId(request.getRequestURI());
//		if (StringUtils.isEmpty(id)) {
//			logger.error("Request Url's arg [chartId id] missing");
//			return;
//		}
//		// init context
//		Context ctx = initContext(request);
//		Context qCtx = new Context(query);
//		ctx.putCtx("q", qCtx);
//		ctx.put(Context.APP_CONTEXT, wac);
//
//		ReportSchema reportSchema = ReportSchemaController.instance()
//				.getSchema(id);
//		// for result
//
//		List<HashMap> rs = null;
//		if (id.equals("CDH_kgline")) {
//			rs = getWHOkgData(ctx, query);
//		} else if (id.equals("CDH_cmline")) {
//			rs = getWHOcmData(ctx, query);
//		} else if (id.equals("94CDH_kgline")) {
//			rs = get94kgData(ctx, query);
//		} else if (id.equals("94CDH_cmline")) {
//			rs = get94cmData(ctx, query);
//		} else {
//			rs = reportSchema.run(ctx);
//		}
//		if (rs != null && rs.size() > 0) {
//			try {
//				FreeMarkerConfigurer fc = (FreeMarkerConfigurer)wac.getBean("freeMarkerConfigurer");
//				Configuration freemarker = fc.getConfiguration();
//				String chartTemplate = reportSchema.getChartTemplate();
//				Template t = freemarker.getTemplate(chartTemplate + ".ftl");
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
//
//			}
//		}
//	}
//
//	/**
//	 * 获取儿童体检WHO标准儿童年龄别体重曲线数据
//	 * 
//	 * @param ctx
//	 * @param query
//	 * @return
//	 */
//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	private List<HashMap> getWHOkgData(Context ctx,
//			HashMap<String, Object> query) {
//		WebApplicationContext wac = (WebApplicationContext) ctx
//				.get(Context.APP_CONTEXT);
//		SessionFactory sf = AppContextHolder.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY,SessionFactory.class);
//		Session ss = null;
//		try {
//			ss = sf.openSession();
//			String empiId = (String) query.get("empiId");
//			String sexCode = (String) query.get("sexCode");
//			String hql1 = new StringBuilder(
//					"select a.extend1 as age, sum(b.wSD0) as wSD0 , sum(b.wSD1) as wSD1, sum(b.wSD2) as wSD2 ,sum(b.wSD3) as wSD3,")
//					.append(" sum(b.wSD1neg) as wSD1neg  , sum(b.wSD2neg) as wSD2neg ,sum(b.wSD3neg) as wSD3neg from PUB_VisitPlan a ,  ")
//					.append(" CDH_WHOAge b  where a.businessType='6' and a.empiId='")
//					.append(empiId)
//					.append("'  ")
//					.append(" and b.sexCode='")
//					.append(sexCode)
//					.append("'  and a.extend1=b.age group by a.extend1 order by a.extend1 ")
//					.toString();
//			Query q = ss.createQuery(hql1).setResultTransformer(
//					Transformers.ALIAS_TO_ENTITY_MAP);
//			List<HashMap<String, Object>> list1 = q.list();
//
//			String hql2 = new StringBuilder(
//					"select b.checkupStage as age, sum(b.weight) as weight  from CDH_HealthCard  a ,  ")
//					.append(" CDH_CheckupInOne  b  ,  MPI_DemographicInfo c where  a.empiId='")
//					.append(empiId)
//					.append("'  and  a.empiId= c.empiId and a.phrId = b.phrId   group by b.checkupStage order by b.checkupStage")
//					.toString();
//			q = ss.createQuery(hql2).setResultTransformer(
//					Transformers.ALIAS_TO_ENTITY_MAP);
//			List<HashMap<String, Object>> list2 = q.list();
//
//			String hql3 = new StringBuilder(
//					"select b.checkupStage as age, sum(b.weight) as weight  from CDH_HealthCard  a ,  ")
//					.append(" CDH_CheckupOneToTwo  b  ,  MPI_DemographicInfo c where  a.empiId='")
//					.append(empiId)
//					.append("'  and  a.empiId= c.empiId and a.phrId = b.phrId  group by b.checkupStage order by b.checkupStage")
//					.toString();
//			q = ss.createQuery(hql3).setResultTransformer(
//					Transformers.ALIAS_TO_ENTITY_MAP);
//			List<HashMap<String, Object>> list3 = q.list();
//
//			String hql4 = new StringBuilder(
//					"select b.checkupStage as age, sum(b.weight) as weight   from CDH_HealthCard  a ,  ")
//					.append(" CDH_CheckupThreeToSix  b  ,  MPI_DemographicInfo c where  a.empiId='")
//					.append(empiId)
//					.append("'  and  a.empiId= c.empiId and a.phrId = b.phrId   group by b.checkupStage order by b.checkupStage")
//					.toString();
//			q = ss.createQuery(hql4).setResultTransformer(
//					Transformers.ALIAS_TO_ENTITY_MAP);
//			List<HashMap<String, Object>> list4 = q.list();
//
//			List<HashMap> data = new ArrayList<HashMap>();
//			for (HashMap<String, Object> map : list1) {
//				int age = (Integer) map.get("age");
//				if (age < 12) {
//					if (list2 == null || list2.size() < 1) {
//						map.put("weight", "");
//					} else {
//						boolean recordExists = false;
//						for (Map<String, Object> oneMap : list2) {
//							int oneAge = Integer.valueOf(oneMap.get("age")
//									.toString());
//							if (age == oneAge) {
//								map.putAll(oneMap);
//								recordExists = true;
//								continue;
//							}
//						}
//						if (recordExists == false) {
//							map.put("weight", "");
//						}
//					}
//				} else if (age < 36) {
//					if (list3 == null || list3.size() < 1) {
//						map.put("weight", "");
//					} else {
//						boolean recordExists = false;
//						for (Map<String, Object> twoMap : list3) {
//							int twoAge = Integer.valueOf(twoMap.get("age")
//									.toString());
//							if (age == twoAge) {
//								map.putAll(twoMap);
//								recordExists = true;
//								continue;
//							}
//						}
//						if (recordExists == false) {
//							map.put("weight", "");
//						}
//					}
//				} else {
//					if (list4 == null || list4.size() < 1) {
//						map.put("weight", "");
//					} else {
//						boolean recordExists = false;
//						for (Map<String, Object> threeMap : list4) {
//							int threeAge = Integer.valueOf(threeMap.get("age")
//									.toString());
//							if (age == threeAge) {
//								map.putAll(threeMap);
//								recordExists = true;
//								continue;
//							}
//						}
//						if (recordExists == false) {
//							map.put("weight", "");
//						}
//					}
//				}
//				data.add(map);
//			}
//			return data;
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error("get chart data failed", e);
//		} finally {
//			if (ss != null && ss.isOpen()) {
//				ss.close();
//			}
//		}
//		return null;
//	}
//
//	/**
//	 * 获取儿童体检WHO标准儿童年龄别身长曲线数据
//	 * 
//	 * @param ctx
//	 * @param query
//	 * @return
//	 */
//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	private List<HashMap> getWHOcmData(Context ctx,
//			HashMap<String, Object> query) {
//		WebApplicationContext wac = (WebApplicationContext) ctx
//				.get(Context.APP_CONTEXT);
//		SessionFactory sf = AppContextHolder.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY,SessionFactory.class);
//		Session ss = null;
//		try {
//			ss = sf.openSession();
//			String empiId = (String) query.get("empiId");
//			String sexCode = (String) query.get("sexCode");
//			String hql1 = new StringBuilder(
//					"select a.extend1 as age, sum(b.hSD0) as hSD0 , sum(b.hSD1) as hSD1, sum(b.hSD2) as hSD2 ,sum(b.hSD3) as hSD3,")
//					.append(" sum(b.hSD1neg) as hSD1neg  , sum(b.hSD2neg) as hSD2neg ,sum(b.hSD3neg) as hSD3neg from PUB_VisitPlan a ,  ")
//					.append(" CDH_WHOAge b  where a.businessType='6' and a.empiId='")
//					.append(empiId)
//					.append("'  ")
//					.append(" and b.sexCode='")
//					.append(sexCode)
//					.append("'  and a.extend1=b.age group by a.extend1 order by a.extend1 ")
//					.toString();
//			Query q = ss.createQuery(hql1).setResultTransformer(
//					Transformers.ALIAS_TO_ENTITY_MAP);
//			List<HashMap<String, Object>> list1 = q.list();
//
//			String hql2 = new StringBuilder(
//					"select b.checkupStage as age, sum(b.height) as height  from CDH_HealthCard  a ,  ")
//					.append(" CDH_CheckupInOne  b  ,  MPI_DemographicInfo c where  a.empiId='")
//					.append(empiId)
//					.append("'  and  a.empiId= c.empiId and a.phrId = b.phrId   group by b.checkupStage order by b.checkupStage")
//					.toString();
//			q = ss.createQuery(hql2).setResultTransformer(
//					Transformers.ALIAS_TO_ENTITY_MAP);
//			List<HashMap<String, Object>> list2 = q.list();
//
//			String hql3 = new StringBuilder(
//					"select b.checkupStage as age, sum(b.height) as height  from CDH_HealthCard  a ,  ")
//					.append(" CDH_CheckupOneToTwo  b  ,  MPI_DemographicInfo c where  a.empiId='")
//					.append(empiId)
//					.append("'  and  a.empiId= c.empiId and a.phrId = b.phrId  group by b.checkupStage order by b.checkupStage")
//					.toString();
//			q = ss.createQuery(hql3).setResultTransformer(
//					Transformers.ALIAS_TO_ENTITY_MAP);
//			List<HashMap<String, Object>> list3 = q.list();
//
//			String hql4 = new StringBuilder(
//					"select b.checkupStage as age, sum(b.height) as height   from CDH_HealthCard  a ,  ")
//					.append(" CDH_CheckupThreeToSix  b  ,  MPI_DemographicInfo c where  a.empiId='")
//					.append(empiId)
//					.append("'  and  a.empiId= c.empiId and a.phrId = b.phrId   group by b.checkupStage order by b.checkupStage")
//					.toString();
//			q = ss.createQuery(hql4).setResultTransformer(
//					Transformers.ALIAS_TO_ENTITY_MAP);
//			;
//			List<HashMap<String, Object>> list4 = q.list();
//
//			List<HashMap> data = new ArrayList<HashMap>();
//			for (HashMap<String, Object> map : list1) {
//				int age = (Integer) map.get("age");
//				if (age < 12) {
//					if (list2 == null || list2.size() < 1) {
//						map.put("height", "");
//					} else {
//						boolean recordExists = false;
//						for (Map<String, Object> oneMap : list2) {
//							int oneAge = Integer.valueOf(oneMap.get("age")
//									.toString());
//							if (age == oneAge) {
//								recordExists = true;
//								map.putAll(oneMap);
//								continue;
//							}
//						}
//						if (recordExists == false) {
//							map.put("height", "");
//						}
//					}
//				} else if (age < 36) {
//					if (list3 == null || list3.size() < 1) {
//						map.put("height", "");
//					} else {
//						boolean recordExists = false;
//						for (Map<String, Object> twoMap : list3) {
//							int twoAge = Integer.valueOf(twoMap.get("age")
//									.toString());
//							if (age == twoAge) {
//								map.putAll(twoMap);
//								recordExists = true;
//								continue;
//							}
//						}
//						if (recordExists == false) {
//							map.put("height", "");
//						}
//					}
//				} else {
//					if (list4 == null || list4.size() < 1) {
//						map.put("height", "");
//					} else {
//						boolean recordExists = false;
//						for (Map<String, Object> threeMap : list4) {
//							int threeAge = Integer.valueOf(threeMap.get("age")
//									.toString());
//							if (age == threeAge) {
//								map.putAll(threeMap);
//								recordExists = true;
//								continue;
//							}
//						}
//						if (recordExists == false) {
//							map.put("height", "");
//						}
//					}
//				}
//				data.add(map);
//			}
//			return data;
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error("get chart data failed", e);
//		} finally {
//			if (ss != null && ss.isOpen()) {
//				ss.close();
//			}
//		}
//		return null;
//	}
//
//	/**
//	 * 获取儿童体检9市标准儿童年龄别体重曲线数据
//	 * 
//	 * @param ctx
//	 * @param query
//	 * @return
//	 */
//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	private List<HashMap> get94kgData(Context ctx, HashMap<String, Object> query) {
//		WebApplicationContext wac = (WebApplicationContext) ctx
//				.get(Context.APP_CONTEXT);
//		SessionFactory sf = AppContextHolder.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY,SessionFactory.class);
//		Session ss = null;
//		try {
//			ss = sf.openSession();
//			String empiId = (String) query.get("empiId");
//			String sexCode = (String) query.get("sexCode");
//			String hql1 = new StringBuilder(
//					"select a.extend1 as age, sum(b.wSD0) as wSD0 , sum(b.wSD1) as wSD1, sum(b.wSD2) as wSD2 ,sum(b.wSD3) as wSD3,")
//					.append(" sum(b.wSD1neg) as wSD1neg  , sum(b.wSD2neg) as wSD2neg ,sum(b.wSD3neg) as wSD3neg from PUB_VisitPlan a ,  ")
//					.append(" CDH_9CityAge  b  where a.businessType='6' and a.empiId='")
//					.append(empiId)
//					.append("'  ")
//					.append(" and b.sexCode='")
//					.append(sexCode)
//					.append("'  and a.extend1=b.age group by a.extend1 order by a.extend1 ")
//					.toString();
//			Query q = ss.createQuery(hql1).setResultTransformer(
//					Transformers.ALIAS_TO_ENTITY_MAP);
//			List<HashMap<String, Object>> list1 = q.list();
//
//			String hql2 = new StringBuilder(
//					"select b.checkupStage as age, sum(b.weight) as weight  from CDH_HealthCard  a ,  ")
//					.append(" CDH_CheckupInOne  b  ,  MPI_DemographicInfo c where  a.empiId='")
//					.append(empiId)
//					.append("'  and  a.empiId= c.empiId and a.phrId = b.phrId   group by b.checkupStage order by b.checkupStage")
//					.toString();
//			q = ss.createQuery(hql2).setResultTransformer(
//					Transformers.ALIAS_TO_ENTITY_MAP);
//			List<HashMap<String, Object>> list2 = q.list();
//
//			String hql3 = new StringBuilder(
//					"select b.checkupStage as age, sum(b.weight) as weight  from CDH_HealthCard  a ,  ")
//					.append(" CDH_CheckupOneToTwo  b  ,  MPI_DemographicInfo c where  a.empiId='")
//					.append(empiId)
//					.append("'  and  a.empiId= c.empiId and a.phrId = b.phrId  group by b.checkupStage order by b.checkupStage")
//					.toString();
//			q = ss.createQuery(hql3).setResultTransformer(
//					Transformers.ALIAS_TO_ENTITY_MAP);
//			List<HashMap<String, Object>> list3 = q.list();
//
//			String hql4 = new StringBuilder(
//					"select b.checkupStage as age, sum(b.weight) as weight   from CDH_HealthCard  a ,  ")
//					.append(" CDH_CheckupThreeToSix  b  ,  MPI_DemographicInfo c where  a.empiId='")
//					.append(empiId)
//					.append("'  and  a.empiId= c.empiId and a.phrId = b.phrId   group by b.checkupStage order by b.checkupStage")
//					.toString();
//			q = ss.createQuery(hql4).setResultTransformer(
//					Transformers.ALIAS_TO_ENTITY_MAP);
//			List<HashMap<String, Object>> list4 = q.list();
//
//			List<HashMap> data = new ArrayList<HashMap>();
//			for (HashMap<String, Object> map : list1) {
//				int age = (Integer) map.get("age");
//				if (age < 12) {
//					if (list2 == null || list2.size() < 1) {
//						map.put("weight", "");
//					} else {
//						boolean recordExists = false;
//						for (Map<String, Object> oneMap : list2) {
//							int oneAge = Integer.valueOf(oneMap.get("age")
//									.toString());
//							if (age == oneAge) {
//								map.putAll(oneMap);
//								recordExists = true;
//								continue;
//							}
//						}
//						if (recordExists == false) {
//							map.put("weight", "");
//						}
//					}
//				} else if (age < 36) {
//					if (list3 == null || list3.size() < 1) {
//						map.put("weight", "");
//					} else {
//						boolean recordExists = false;
//						for (Map<String, Object> twoMap : list3) {
//							int twoAge = Integer.valueOf(twoMap.get("age")
//									.toString());
//							if (age == twoAge) {
//								map.putAll(twoMap);
//								recordExists = true;
//								continue;
//							}
//						}
//						if (recordExists == false) {
//							map.put("weight", "");
//						}
//					}
//				} else {
//					if (list4 == null || list4.size() < 1) {
//						map.put("weight", "");
//					} else {
//						boolean recordExists = false;
//						for (Map<String, Object> threeMap : list4) {
//							int threeAge = Integer.valueOf(threeMap.get("age")
//									.toString());
//							if (age == threeAge) {
//								map.putAll(threeMap);
//								recordExists = true;
//								continue;
//							}
//						}
//						if (recordExists == false) {
//							map.put("weight", "");
//						}
//					}
//				}
//				data.add(map);
//			}
//			return data;
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error("get chart data failed", e);
//		} finally {
//			if (ss != null && ss.isOpen()) {
//				ss.close();
//			}
//		}
//		return null;
//	}
//
//	/**
//	 * 获取儿童体检儿童体检9市标准儿童年龄别身长曲线数据
//	 * 
//	 * @param ctx
//	 * @param query
//	 * @return
//	 */
//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	private List<HashMap> get94cmData(Context ctx, HashMap<String, Object> query) {
//		WebApplicationContext wac = (WebApplicationContext) ctx
//				.get(Context.APP_CONTEXT);
//		SessionFactory sf = AppContextHolder.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY,SessionFactory.class);
//		Session ss = null;
//		try {
//			ss = sf.openSession();
//			String empiId = (String) query.get("empiId");
//			String sexCode = (String) query.get("sexCode");
//			String hql1 = new StringBuilder(
//					"select a.extend1 as age, sum(b.hSD0) as hSD0 , sum(b.hSD1) as hSD1, sum(b.hSD2) as hSD2 ,sum(b.hSD3) as hSD3,")
//					.append(" sum(b.hSD1neg) as hSD1neg  , sum(b.hSD2neg) as hSD2neg ,sum(b.hSD3neg) as hSD3neg from PUB_VisitPlan a ,  ")
//					.append(" CDH_9CityAge  b  where a.businessType='6' and a.empiId='")
//					.append(empiId)
//					.append("'  ")
//					.append(" and b.sexCode='")
//					.append(sexCode)
//					.append("'  and a.extend1=b.age group by a.extend1 order by a.extend1 ")
//					.toString();
//			Query q = ss.createQuery(hql1).setResultTransformer(
//					Transformers.ALIAS_TO_ENTITY_MAP);
//			List<HashMap<String, Object>> list1 = q.list();
//
//			String hql2 = new StringBuilder(
//					"select b.checkupStage as age, sum(b.height) as height  from CDH_HealthCard  a ,  ")
//					.append(" CDH_CheckupInOne  b  ,  MPI_DemographicInfo c where  a.empiId='")
//					.append(empiId)
//					.append("'  and  a.empiId= c.empiId and a.phrId = b.phrId   group by b.checkupStage order by b.checkupStage")
//					.toString();
//			q = ss.createQuery(hql2).setResultTransformer(
//					Transformers.ALIAS_TO_ENTITY_MAP);
//			List<HashMap<String, Object>> list2 = q.list();
//
//			String hql3 = new StringBuilder(
//					"select b.checkupStage as age, sum(b.height) as height  from CDH_HealthCard  a ,  ")
//					.append(" CDH_CheckupOneToTwo  b  ,  MPI_DemographicInfo c where  a.empiId='")
//					.append(empiId)
//					.append("'  and  a.empiId= c.empiId and a.phrId = b.phrId  group by b.checkupStage order by b.checkupStage")
//					.toString();
//			q = ss.createQuery(hql3).setResultTransformer(
//					Transformers.ALIAS_TO_ENTITY_MAP);
//			List<HashMap<String, Object>> list3 = q.list();
//
//			String hql4 = new StringBuilder(
//					"select b.checkupStage as age, sum(b.height) as height   from CDH_HealthCard  a ,  ")
//					.append(" CDH_CheckupThreeToSix  b  ,  MPI_DemographicInfo c where  a.empiId='")
//					.append(empiId)
//					.append("'  and  a.empiId= c.empiId and a.phrId = b.phrId   group by b.checkupStage order by b.checkupStage")
//					.toString();
//			q = ss.createQuery(hql4).setResultTransformer(
//					Transformers.ALIAS_TO_ENTITY_MAP);
//			List<HashMap<String, Object>> list4 = q.list();
//
//			List<HashMap> data = new ArrayList<HashMap>();
//			for (HashMap<String, Object> map : list1) {
//				int age = (Integer) map.get("age");
//				if (age < 12) {
//					if (list2 == null || list2.size() < 1) {
//						map.put("height", "");
//					} else {
//						boolean recordExists = false;
//						for (Map<String, Object> oneMap : list2) {
//							int oneAge = Integer.valueOf(oneMap.get("age")
//									.toString());
//							if (age == oneAge) {
//								map.putAll(oneMap);
//								recordExists = true;
//								continue;
//							}
//						}
//						if (recordExists == false) {
//							map.put("height", "");
//						}
//					}
//				} else if (age < 36) {
//					if (list3 == null || list3.size() < 1) {
//						map.put("height", "");
//					} else {
//						boolean recordExists = false;
//						for (Map<String, Object> twoMap : list3) {
//							int twoAge = Integer.valueOf(twoMap.get("age")
//									.toString());
//							if (age == twoAge) {
//								map.putAll(twoMap);
//								recordExists = true;
//								continue;
//							}
//						}
//						if (recordExists == false) {
//							map.put("height", "");
//						}
//					}
//				} else {
//					if (list4 == null || list4.size() < 1) {
//						map.put("height", "");
//					} else {
//						boolean recordExists = false;
//						for (Map<String, Object> threeMap : list4) {
//							int threeAge = Integer.valueOf(threeMap.get("age")
//									.toString());
//							if (age == threeAge) {
//								map.putAll(threeMap);
//								recordExists = true;
//								continue;
//							}
//						}
//						if (recordExists == false) {
//							map.put("height", "");
//						}
//					}
//				}
//				data.add(map);
//			}
//			return data;
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error("run SingleMode failed", e);
//		} finally {
//			if (ss != null && ss.isOpen()) {
//				ss.close();
//			}
//		}
//		return null;
//	}
//
//	@SuppressWarnings("rawtypes")
//	private HashMap<String, Object> perpareData(ReportSchema reportSchema,
//			List<HashMap> rs) {
//		HashMap<String, Object> data = new HashMap<String, Object>();
//		data.put("title", reportSchema.getTitle());
//		data.put("headers", reportSchema.getHeaders());
//
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
//							logger.error("parseQueryStringFailed:", e);
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
//						logger.error("parseQueryStringFailed:", e);
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
//}
