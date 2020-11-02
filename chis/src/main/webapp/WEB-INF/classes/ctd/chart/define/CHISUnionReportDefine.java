/**
 * @(#)CHISUnionReportDefine.java Created on 2015-9-14 上午8:49:38
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package ctd.chart.define;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.util.BSCHISUtil;

import com.fasterxml.jackson.databind.ObjectMapper;

import ctd.chart.ReportSchema;
import ctd.dao.exception.QueryDataAccessException;
import ctd.schema.SchemaItem;
import ctd.util.AppContextHolder;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpRunner;

/**
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class CHISUnionReportDefine implements ReportDefine {
	private static ObjectMapper jsonMapper = new ObjectMapper();
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ReportDefine.class);
	private static String[] aliasList = { "a", "b", "c", "d", "e", "f", "g" };
	private ReportSchema reportSchema;
	private List<SchemaItem> headers = new ArrayList<SchemaItem>();
	private List<SchemaItem> diggers = new ArrayList<SchemaItem>();
	private List<SchemaItem> args;
	private List<Integer> groups = new ArrayList<Integer>();
	private String[] entryNames;
	private String[] sorts;
	private int funcCount;
	private int point;
	private int fieldCount;
	private int startRenderIndex;

	List cnds;

	public void setDefineXML(Element define) {
		List<Element> els = define.selectNodes("headers/item");
		if (define.attribute("point") != null) {
			point = BSCHISUtil.parseToInt(define.attribute("point").getValue()
					+ "");
		}
		int i = startRenderIndex;
		for (Element el : els) {
			int index = Integer.parseInt(el.attributeValue("renderIndex",
					String.valueOf(i)));
			SchemaItem si = DefaultReportDefine.toSchemaItem(el);
			si.setSchemaId(reportSchema.getId());
			headers.add(si);
			groups.add(i);
			if (!si.hasProperty("func")) {
				if (!si.isEvalValue()) {
					fieldCount++;
					// groups.add(groups.size());
				}

			} else {
				fieldCount++;
				// funcCount ++;
			}
			i++;
		}
		List<Element> dgs = define.selectNodes("diggers/item");
		if (dgs != null) {
			for (Element el : dgs) {
				SchemaItem si = DefaultReportDefine.toSchemaItem(el);
				si.setSchemaId(reportSchema.getId());
				diggers.add(si);
			}
		}
		Element el = define.element("condition");
		if (el != null) {
			try {
				cnds = jsonMapper.readValue(el.getText().replaceAll("'", "\""),
						List.class);
			} catch (Exception e) {
				LOGGER.error("parse reportCondition failed:" + e.getMessage());
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

					List ref1 = new ArrayList();
					ref1.add("$");
					ref1.add(src);

					List ref2 = new ArrayList();
					ref2.add("$");
					ref2.add(dest);

					joinCnd.add(ref1);
					joinCnd.add(ref2);
					if (cnds == null) {
						cnds = joinCnd;
					} else {
						if (cnds.get(0).equals("and")) {
							cnds.add(joinCnd);
						} else {
							List o = new ArrayList();
							o.add("and");
							o.add(joinCnd);
							o.add(cnds);
							cnds = o;
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
				} else {
					sorts = new String[els.size()];
					i = 0;
					for (Element f : els) {
						sorts[i] = f.attributeValue("id", el.getText()) + " "
								+ f.attributeValue("dict", "");
						i++;
					}
				}
			}
		} else {
			LOGGER.error("src not defined...");
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

	public void addCondition(List topCnd) {
		if (cnds == null) {
			cnds = topCnd;
		} else {
			if (cnds.get(0).equals("and")) {
				cnds.add(topCnd);
			} else {
				List o = new ArrayList();
				o.add("and");
				o.add(cnds);
				o.add(topCnd);
				cnds = o;
			}
		}
	}

	public List<Map<String, Object>> runSingleMode(Context ctx) {
		SessionFactory sf = AppContextHolder.getBean(
				AppContextHolder.DEFAULT_SESSION_FACTORY, SessionFactory.class);
		Session ss = null;
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		try {
			ss = sf.openSession();
			String hql = prepareHql(ctx);
			if (StringUtils.isEmpty(hql)) {
				throw new QueryDataAccessException("init hql failed");
			} else {
				SQLQuery q = ss.createSQLQuery(hql);
				List<Object[]> rs = q.list();
				int totalCount = rs.size();
				ctx.put("totalCount", totalCount);
				ctx.put("pageSize", totalCount);
				ctx.put("pageNo", 1);
				if (totalCount > 0) {
					int fieldCount = headers.size();
					if (fieldCount > 1) {
						int cn = 0;
						int total = rs.size();
						int sn = 0;
						if(total > 20){
							sn=total-20;
						}
						for (Object[] r : rs) {
							if(cn<sn){
								cn++;
								continue;
							}
							cn++;
							HashMap o = new HashMap();
							data.add(o);
							Context rCtx = new Context(o);
							ctx.putCtx("r", rCtx);
							int i = 0;
							for (SchemaItem f : headers) {
								String k = f.getId();
								Object v = null;
								if (f.isEvalValue()) {
									v = f.eval();
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
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("run SingleMode failed");
		} finally {
			if (ss != null && ss.isOpen()) {
				ss.close();
			}
		}
		return data;
	}

	public void runMutiMode(Map<String, Map<String, Object>> data, Context ctx) {
		SessionFactory sf = AppContextHolder.getBean(
				AppContextHolder.DEFAULT_SESSION_FACTORY, SessionFactory.class);
		Session ss = null;
		try {
			ss = sf.openSession();
			String hql = prepareHql(ctx);
			if (StringUtils.isEmpty(hql)) {
				throw new QueryDataAccessException("init hql failed");
			} else {
				Query q = ss.createSQLQuery(hql);
				List<Object[]> rs = q.list();
				int totalCount = rs.size();
				int j = 0;
				if (totalCount > 0) {
					if (fieldCount > 1) {
						int cn = 0;
						int total = rs.size();
						int sn = 0;
						if(total > 20){
							sn=total-20;
						}
						for (Object[] r : rs) {
							if(cn<sn){
								cn++;
								continue;
							}
							cn++;
							String key = "" + j++;
							Map<String, Object> o = data.get(key);
							if (o == null) {
								o = reportSchema.createEmptyRecord();
								o = changeZeroToNull(o);
								data.put(key, o);
							}
							Context rCtx = new Context(o);
							ctx.putCtx("r", rCtx);
							int i = 0;
							for (SchemaItem f : headers) {
								String k = f.getId();
								Object v = null;

								if (f.isEvalValue()) {

									v = f.eval();
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
						Map<String, Object> o = data.get(key);
						if (o == null) {
							o = reportSchema.createEmptyRecord();
							o = changeZeroToNull(o);
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
								v = f.eval();
								o.put(k, v);
							}

							if (f.isCodedValue()) {
								o.put(k + "_text", f.getDisplayValue(v));
							}
						}
					}
				}// totalCount
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("run mutiMode failed");
		} finally {
			if (ss != null && ss.isOpen()) {
				ss.close();
			}
		}
	}

	private Map<String, Object> changeZeroToNull(Map<String, Object> data) {
		if (point != 1) {
			return data;
		}
		Map<String, Object> result = new HashMap<String, Object>();
		for (Iterator it = data.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			Object value = data.get(key);
			if (value instanceof Integer) {
				int v = (Integer) value;
				if (v == 0) {
					continue;
				} else {
					result.put(key, value);
				}
			} else {
				result.put(key, value);
			}
		}
		return result;
	}

	private String prepareHql(Context ctx) throws ExpException {
		StringBuffer hql = new StringBuffer();
		if (headers == null || headers.size() == 0) {
			LOGGER.error("ReportDefine headers not found");
			return "";
		}
		StringBuffer hqlFields = new StringBuffer();
		StringBuffer hqlGroup = new StringBuffer();

		String union = "";

		for (SchemaItem f : headers) {
			if (f.isEvalValue()) {
				continue;
			}
			String expr = f.hasProperty("expr") ? String.valueOf(f
					.getProperty("expr")) : f.getId();
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
		// for(SchemaItem it : diggers){
		// if (it.isEvalValue()) {
		// continue;
		// }
		// String expr = it.hasProperty("expr") ? String.valueOf(it
		// .getProperty("expr")) : it.getId();
		// if (it.hasProperty("func")) {
		// if (it.getProperty("func").equals("union")
		// && expr.indexOf(":") > -1) {
		// union = expr.split(":")[1];
		// expr = expr.split(":")[0];
		// } else {
		// expr = it.getProperty("func") + "(" + expr + ")";
		// }
		// } else {
		// if (funcCount > 0) {
		// hqlGroup.append(expr).append(",");
		// }
		// }
		// if(hqlFields.indexOf(expr) > -1){
		// continue;
		// }
		// hqlFields.append(expr).append(",");
		// }

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
					Object dv = a.getDefaultValue();
					if (!ctx.has(argName) && dv != null) {
						if (a.isCodedValue()) {
							dv = StringUtils
									.isEmpty(((HashMap<String, String>) dv)
											.get("key")) ? ""
									: ((HashMap<String, String>) dv).get("key");
						}
						ctx.put(argName, dv);
					}
				}
			}

			where = " where " + ExpRunner.toString(cnds, ctx);
		}
		if (sorts != null) {
			orderBy = " order by ";
			for (String st : sorts) {
				orderBy += st + ",";
			}
			orderBy = orderBy.substring(0, orderBy.length() - 1);
		}
		hql.append("select ").append(select.toString()).append(" from ")
				.append(from).append(where);
		if (group.length() > 0) {
			hql.append(" group by ").append(group);
		}

		if (union.length() > 0) {
			hql.append(" union ").append(union).append(where);
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
