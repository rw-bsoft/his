package chis.source.gis;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.web.context.WebApplicationContext;
import chis.source.util.CNDHelper;
import ctd.chart.ReportSchema;
import ctd.chart.define.DefaultReportDefine;
import ctd.chart.define.UnionReportDefine;
import ctd.schema.SchemaItem;
import ctd.util.AppContextHolder;
import ctd.util.StringValueParser;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpRunner;
import ctd.util.exp.ExpressionProcessor;
import edu.emory.mathcs.backport.java.util.Collections;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class CustomUnionReportDefine extends UnionReportDefine {

	private static final Log logger = LogFactory
			.getLog(CustomUnionReportDefine.class);

	private static String[] aliasList = { "a", "b", "c", "d", "e", "f", "g" };
	private ReportSchema reportSchema;
	private List<SchemaItem> headers = new ArrayList<SchemaItem>();
	private List<SchemaItem> args;
	private List<Integer> groups = new ArrayList<Integer>();
	private String[] entryNames;
	private String[] sorts;
	private String sortFunc;
	private int funcCount;
	private int fieldCount;
	private int startRenderIndex;
	@SuppressWarnings("rawtypes")
	ArrayList cnds;

	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	public void setDefineXML(Element define) {
		List<Element> els = define.selectNodes("headers/item");
		int i = startRenderIndex;
		for (Element el : els) {
			int index = Integer.parseInt(el.attributeValue("renderIndex",String.valueOf(i)));
			SchemaItem si = DefaultReportDefine.toSchemaItem(el);
			
			headers.add(si);

			if (!si.hasProperty("func")) {
				if (!si.isEvalValue()) {
					fieldCount++;
					groups.add(groups.size());
				}

			} else {
				fieldCount++;
				// funcCount ++;
			}
			i++;
		}

		Element el = define.element("condition");
		if (el != null) {
			try{
				cnds = (ArrayList) CNDHelper.toListCnd(el.getText());
			}catch(ExpException e){
				e.printStackTrace();
			}
		}
		el = define.element("src");
		if (el != null) {
			els = el.elements("entry");
			entryNames = new String[els.size()];
			i = 0;
			for (Element et : els) {
				String nm = et.attributeValue("name", et.getText());
				String alias = et.attributeValue("alias");
				if (alias != null) {
					nm += " " + alias;
				}
				entryNames[i] = nm;
				i++;
			}
			els = el.selectNodes("join/field");
			if (els.size() > 0) {
				for (Element f : els) {
					String src = f.attributeValue("src");
					String dest = f.attributeValue("dest");
					List joinCnd = new ArrayList();
					joinCnd.add("eq");
					joinCnd.add(CNDHelper.createArrayCnd("$", src));
					joinCnd.add(CNDHelper.createArrayCnd("$",dest));
					if (cnds == null) {
						cnds.add(joinCnd.toString());
					} else {
						if (cnds.get(0).equals("and")) {
							cnds.add(joinCnd);
						} else {
							ArrayList o = new ArrayList();
							o.add("and");
							o.add(joinCnd);
							o.add(cnds);
							cnds.add(o);
						}
					}
				}
			}
			el = define.element("sort");
			if (el != null) {
				els = el.elements();
				if (els.size() == 0) {
					sorts = new String[] { el
							.attributeValue("id", el.getText())
							+ " "
							+ el.attributeValue("dict", "") };
					sortFunc = el.attributeValue("func", "");
				} else {
					sorts = new String[els.size()];
					i = 0;
					for (Element f : els) {
						sorts[i] = el.attributeValue("id", el.getText()) + " "
								+ el.attributeValue("dict", "");
						if (el.attributeValue("func") != null)
							sortFunc = el.attributeValue("func");
						i++;
					}
				}
			}
		} else {
			logger.error("src not defined...");
		}

	}

	public List<SchemaItem> getHeaders(boolean group) {
		List<SchemaItem> ls = new ArrayList<SchemaItem>();
		for (SchemaItem i : headers) {
			if (!i.hasProperty("func") && !group && !i.isEvalValue()) {
				continue;
			}
			ls.add(i);
		}
		return ls;
	}

	public void setReportSchema(ReportSchema rs) {
		reportSchema = rs;
	}

	public void setQueryArgs(List<SchemaItem> queryArgs) {
		args = queryArgs;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addCondition(ArrayList topCnd) {
		if (cnds == null) {
			cnds.add(topCnd);
		} else {
			if (cnds.get(0).equals("and")) {
				cnds.add(topCnd);
			} else {
				ArrayList o = new ArrayList();
				o.add("and");
				o.add(cnds);
				o.add(topCnd);
				cnds.add(o);
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Map<String, Object>> runSingleMode(Context ctx) {
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		List<Object[]> rs = runHQL(ctx);
		int totalCount = rs.size();
		ctx.put("totalCount", totalCount);
		ctx.put("pageSize", totalCount);
		ctx.put("pageNo", 1);
		if (totalCount > 0) {
			int fieldCount = headers.size();
			if (fieldCount > 1) {
				for (Object[] r : rs) {
					HashMap o = new HashMap();
					data.add(o);
					Context rCtx = new Context(o);
					ctx.putCtx("r", rCtx);
					int i = 0;
					for (SchemaItem f : headers) {
						String k = f.getId();
						Object v = null;
						if (f.isEvalValue()) {
							try {
								v = f.eval();
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							v = r[i];
							i++;
						}
						o.put(k, v);

						if (f.isCodedValue()) {
							o.put(k + "_text", f.getDisplayValue(v));
						}

					}
				}
			} else { // fieldCount
				Object v = rs.get(0);
				if (v == null) {
					return data;
				}
				HashMap o = new HashMap();
				data.add(o);
				o.put(headers.get(0).getId(), v);

			}
		}
		return data;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void runMutiMode(HashMap<String, HashMap> data, Context ctx)
			throws DataAccessException {
		List<Object[]> rs = runHQL(ctx);
		int totalCount = rs.size();
		if (totalCount > 0) {
			if (fieldCount > 1) {
				for (Object[] r : rs) {
					StringBuffer groupStr = new StringBuffer();
					if (groups.size() > 0) {
						if (funcCount > 0) {
							for (Integer gi : groups) {
								groupStr.append(r[gi]);
							}
						} else {
							groupStr.append(r[0]);
						}
					} else {
						groupStr.append("0");
					}
					String key = groupStr.toString();
					HashMap o = data.get(key);

					if (o == null) {
						o = reportSchema.createEmptyRecord();
						data.put(key, o);
					}
					Context rCtx = new Context(o);
					ctx.putCtx("r", rCtx);
					int i = 0;
					for (SchemaItem f : headers) {

						String k = f.getId();
						Object v = null;

						if (f.isEvalValue()) {

							try {
								v = f.eval();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} else {
							v = r[i];
							i++;
						}
						if (v == null) {
							continue;
						}
						o.put(k, v);
						if (f.isCodedValue()) {
							o.put(k + "_text", f.getDisplayValue(v));
						}
					}
				}// for rs
			}// for fieldCount
			else {
				Object v = rs.get(0);
				String key = "0";
				HashMap o = data.get(key);
				if (o == null) {
					o = reportSchema.createEmptyRecord();
					data.put(key, o);
				}
				if (v == null) {
					return;
				}
				o.put(headers.get(0).getId(), v);
				Context rCtx = new Context(o);
				ctx.putCtx("r", rCtx);
				for (SchemaItem f : headers) {
					String k = f.getId();
					if (f.isEvalValue()) {
						try {
							v = f.eval();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						o.put(k, v);
					}

					if (f.isCodedValue()) {
						o.put(k + "_text", f.toDisplayValue(v));
					}
				}
			}
		}// totalCount

	}

	@SuppressWarnings("unchecked")
	private List<Object[]> runHQL(Context ctx) {
		WebApplicationContext wac = (WebApplicationContext) ctx
				.get(Context.APP_CONTEXT);
		SessionFactory sf = AppContextHolder.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY,SessionFactory.class);
		Session ss = null;
		List<Object[]> list1;
		List<Object[]> list2;
		try {
			ss = sf.openSession();
			String hql = prepareHql(ctx);
			if (StringUtils.isEmpty(hql)) {
				throw new Exception("init hql failed");
			}
			int indx = hql.indexOf("union all");
			if (indx < 0) {
				Query q = ss.createQuery(hql);
				return q.list();
			}
			Query q1 = ss.createQuery(hql.substring(0, indx));
			list1 = q1.list();
			Query q2 = ss.createQuery(hql.substring(indx + "union all".length()));
			list2 = q2.list();
		} catch (Exception e) {
			logger.error("run hql failed", e);
			return new ArrayList<Object[]>();
		} finally {
			if (ss != null && ss.isOpen()) {
				ss.close();
			}
		}
		list1.addAll(list2);
		if (sorts == null || sorts.length == 0) {
			return list1;
		}
		final int[] sortIndxs = new int[sorts.length];
		for (int i = 0; i < sorts.length; i++) {
			for (SchemaItem si : headers) {
				if (si.getId().equals(sorts[i])) {
					sortIndxs[i] = (Integer) si.getProperty("renderIndex");
				}
			}
		}
		Collections.sort(list1, new Comparator<Object[]>() {
			public int compare(Object[] o1, Object[] o2) {
				StringBuffer str1 = new StringBuffer();
				StringBuffer str2 = new StringBuffer();
				for (int i : sortIndxs) {
					str1.append(",").append(o1[i]);
					str2.append(",").append(o2[i]);
				}
				return sorts[sorts.length - 1].endsWith("desc") ? -str1
						.toString().compareTo(str2.toString()) : str1
						.toString().compareTo(str2.toString());
			}
		});
		return list1;
	}

	private String prepareHql(Context ctx) {
		StringBuffer hql = new StringBuffer();
		if (headers == null || headers.size() == 0) {
			logger.error("ReportDefine headers not found");
			return "";
		}
		StringBuffer hqlFields = new StringBuffer();
		StringBuffer hqlGroup = new StringBuffer();
		int n = headers.size();

		String union = "";

		for (SchemaItem f : headers) {
			if (f.isEvalValue()) {
				continue;
			}
			String expr = f.hasProperty("expr") ? f.getProperty("expr")
					.toString() : f.getId();
			if (f.hasProperty("func")) {
				if (f.getProperty("func").equals("union")
						&& expr.indexOf(":") > -1) {
					union = expr.split(":")[1];
					expr = expr.split(":")[0];
				} else {
					expr = f.getProperty("func") + "(" + expr + ")";
				}
			} else {
				if (funcCount > 0) {
					hqlGroup.append(expr).append(",");
				}
			}
			hqlFields.append(expr).append(",");
		}

		String select = hqlFields.substring(0, hqlFields.length() - 1);
		String from = "";
		if (entryNames.length == 1) {
			from = entryNames[0];
		} else {
			int i = 0;
			for (String nm : entryNames) {
				from += nm + " " + aliasList[i] + ",";
				i++;
			}
			from = from.substring(0, from.length() - 1);
		}
		String where = "";
		String group = "";
		int gCount = groups.size();
		if (gCount > 0 && funcCount > 0) {
			group = hqlGroup.substring(0, hqlGroup.length() - 1);
		}
		String orderBy = "";
		if (cnds != null) {
			if (args != null) {
				int argCount = args.size();
				for (int j = 0; j < argCount; j++) {
					SchemaItem a = args.get(j);
					String argName = "q." + a.getId();
					String dv = (String) a.getDefaultValue();
					if (!ctx.has(argName) && dv != null) {
						String v = StringValueParser.parse(dv, String.class);
						ctx.put(argName, v);
					}
				}
			}

			try {
				where = " where " + ExpRunner.toString(cnds,ctx);
			} catch (ExpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		if (sorts != null) {
//			orderBy = " order by ";
//			for (String st : sorts) {
//				orderBy += st + ",";
//			}
//			orderBy = orderBy.substring(0, orderBy.length() - 1);
//		}

		if (sortFunc != null && sortFunc.length() > 0) {
			orderBy = orderBy + sortFunc;
		}

		hql.append("select ").append(select.toString()).append(" from ")
				.append(from).append(where);
		if (group.length() > 0) {
			hql.append(" group by ").append(group);
		}

		if (union.length() > 0) {
			hql.append(" union all ").append(union).append(where);
		}

		hql.append(orderBy);
		return hql.toString();
	}

	public int getHeaderCount() {
		return headers.size();
	}

	public void setStartRenderIndex(int start) {
		startRenderIndex = start;
	}
}
