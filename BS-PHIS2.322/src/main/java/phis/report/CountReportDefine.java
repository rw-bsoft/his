package phis.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import ctd.chart.ReportSchema;
import ctd.chart.define.ReportDefine;
import ctd.dao.exception.QueryDataAccessException;
import ctd.schema.DictionaryIndicator;
import ctd.schema.SchemaItem;
import ctd.util.AppContextHolder;
import ctd.util.JSONUtils;
import ctd.util.S;
import ctd.util.context.Context;
import ctd.util.converter.ConversionUtils;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpRunner;

public class CountReportDefine implements ReportDefine {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ReportDefine.class);
	private static String[] aliasList = { "a", "b", "c", "d", "e", "f", "g" };
	protected ReportSchema reportSchema;
	protected List<SchemaItem> headers = new ArrayList<SchemaItem>();
	protected List<SchemaItem> args;
	protected List<Integer> groups = new ArrayList<Integer>();
	protected String[] entryNames;
	protected String[] sorts;
	protected int funcCount;
	protected int fieldCount;
	protected int startRenderIndex;
	List<Object> cnds;
	protected String datelimit;

	public void setDefineXML(Element define) {
		List<Element> els = define.selectNodes("headers/item");
		int i = startRenderIndex;
		for (Element el : els) {
			int index = Integer.parseInt(el.attributeValue("renderIndex",
					String.valueOf(i)));
			SchemaItem si = toSchemaItem(el);
			headers.add(si);

			if (!si.hasProperty("func")) {
				if (!si.isEvalValue()) {
					fieldCount++;
					groups.add(groups.size());
				}

			} else {
				fieldCount++;
				funcCount++;
			}
			i++;
		}

		Element el = define.element("condition");
		if (el != null) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				cnds = mapper.readValue(el.getText().replaceAll("'", "\""),
						List.class);
			} catch (Exception e) {
				LOGGER.error("parse reportCondition failed:", e);
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
					joinCnd.add(ref1);

					List ref2 = new ArrayList();
					ref2.add("$");
					ref2.add(dest);

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

		el = define.element("datelimit");
		if (el != null) {
			try {
				datelimit = el.getTextTrim();
			} catch (Exception e) {
				LOGGER.error("parse reportdatelimit failed:", e);
			}
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

	public void addCondition(List<Object> topCnd) {
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
				Query q = ss.createQuery(hql);
				List<Object[]> rs = q.list();
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
									v = f.eval();
								} else {
									v = r[i];
									i++;
								}
								o.put(k, v);

								if (f.isCodedValue()) {
									o.put(k + "_text", f.toDisplayValue(v));
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
			LOGGER.error("run SingleMode failed", e);
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
				Query q = ss.createQuery(hql);
				List<Object[]> rs = q.list();
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
							Map<String, Object> o = data.get(key);

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
									o.put(k + "_text", f.toDisplayValue(v));
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
								o.put(k + "_text", f.toDisplayValue(v));
							}
						}
					}
				}// totalCount
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("run mutiMode failed", e);
		} finally {
			if (ss != null && ss.isOpen()) {
				ss.close();
			}
		}
	}

	protected String prepareHql(Context ctx) throws ExpException {
		StringBuffer hql = new StringBuffer();
		if (headers == null || headers.size() == 0) {
			LOGGER.error("ReportDefine headers not found");
			return "";
		}
		StringBuffer hqlFields = new StringBuffer();
		StringBuffer hqlGroup = new StringBuffer();
		for (SchemaItem f : headers) {
			if (f.isEvalValue()) {
				continue;
			}
			String expr = f.hasProperty("expr") ? String.valueOf(f
					.getProperty("expr")) : f.getId();
			if (f.hasProperty("func")) {
				expr = f.getProperty("func") + "(" + expr + ")";
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
				Map<String, Object> q = (Map<String, Object>) ctx.get("q");
				if (q == null) {
					q = new HashMap<String, Object>();
				}
				for (int j = 0; j < argCount; j++) {
					SchemaItem a = args.get(j);
					String argName = "q." + a.getId();
					Object dv = a.getDefaultValue();
					if (ctx.get(argName) == null && dv != null) {
						if (a.isCodedValue()) {
							dv = StringUtils
									.isEmpty(((HashMap<String, String>) dv)
											.get("key")) ? ""
									: ((HashMap<String, String>) dv).get("key");
						}
						q.put(a.getId(), dv);
					}
				}
				ctx.put("q", q);
			}

			where = " where " + ExpRunner.toString(cnds, ctx);
		}
		// modified by gaof
		if (datelimit != null && datelimit != "") {
			where += " and ";
			where += datelimit.split(",")[0] + " >=sysdate-"
					+ datelimit.split(",")[1] + " ";
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
		// modified by gaof
		if (group.contains(" as ")) {
			group = group.substring(0, group.indexOf("as") - 1);
		}

		if (group.length() > 0) {
			hql.append(" group by ").append(group);
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

	public static SchemaItem toSchemaItem(Element el) {
		SchemaItem si = ConversionUtils.convert(el, SchemaItem.class);
		String notNull = (String) si.getProperty("not-null");
		if ("1".equals(notNull)) {
			notNull = "true";
		}
		if (!S.isEmpty(notNull)) {
			si.setRequired(Boolean.parseBoolean(notNull));
		}

		Element dicEl = el.element("dic");
		if (dicEl != null) {
			List<Element> items = dicEl.elements("item");
			if (items.size() == 0) {
				if (S.isEmpty(dicEl.attributeValue("id"))) {
					dicEl.addAttribute("id", el.attributeValue("id"));
				}
				DictionaryIndicator dic = ConversionUtils.convert(dicEl,
						DictionaryIndicator.class);
				si.setDic(dic);
			}
		} else {
			String dicName = el.attributeValue("dic");
			if (!StringUtils.isEmpty(dicName)) {
				DictionaryIndicator dic = new DictionaryIndicator();
				dic.setId(dicName);
				si.setDic(dic);
			}
		}

		Element setEl = el.element("set");
		if (setEl != null) {
			String assignType = setEl.attributeValue("type", "exp");
			if (assignType.equals("exp")) {
				List<Object> exp = JSONUtils.parse(setEl.getText(), List.class);
				si.setExp(exp);
			}
		}
		Element keyEl = el.element("key");
		if (keyEl != null) {
			List<Element> rs = keyEl.elements();
			for (Element r : rs) {
				HashMap<String, String> m = new HashMap<String, String>();
				List<Attribute> attrs = r.attributes();
				for (Attribute attr : attrs) {
					m.put(attr.getName(), attr.getValue());
				}
				String text = r.getText();
				if (!"".equals(text) && text != null) {
					m.put("text", text.trim());
				}
				si.addKeyRules(m);
			}
		}
		return si;
	}

}
