package ctd.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;

public class QueryContext {
	private List<String> fields = new ArrayList<String>();
	private List<String> alias = new ArrayList<String>();
	private List<String> entryNames = new ArrayList<String>();
	private StringBuffer fullEntryName = new StringBuffer();
	private String where = "";
	private String sortInfo = "";

	public void addField(String f) {
		addField(f, "");
	}

	public void addField(String f, String a) {
		fields.add(f);
		alias.add(a);
	}

	public String getFieldName(int i) {
		return fields.get(i);
	}

	public String getFullFieldName(int i) {
		String a = alias.get(i) + ".";
		if (a.equals("a.")) {
			a = "";
		}
		return a + fields.get(i);
	}

	public List<String> getAllFields() {
		return fields;
	}

	public void addEntryName(String entry) {
		if (entryNames.contains(entry)) {
			return;
		}
		if (entryNames.size() > 0) {
			fullEntryName.append(",");
		}
		fullEntryName.append(entry);
		entryNames.add(entry);
	}

	public void addEntryNameByJoin(String entry, String joinWay,
			List<Object> cnds) throws ExpException {
		if (entryNames.contains(entry)) {
			return;
		}
		if (entryNames.size() > 0) {
			fullEntryName.append(" ");
			fullEntryName.append(joinWay);
			fullEntryName.append(" join ");
		}
		fullEntryName.append(entry);
		fullEntryName.append(" on ");
		fullEntryName.append(ExpressionProcessor.instance().toString(cnds));
		entryNames.add(entry);

	}

	public void setWhere(String where) {
		this.where = where;
	}

	public void setSortInfo(String sortInfo) {
		this.sortInfo = sortInfo;
	}

	public String buildCountHql() {
		StringBuffer hql = new StringBuffer();
		hql.append("select count(*) from ").append(fullEntryName).append(where);
		return hql.toString();
	}

	public int getFieldCount() {
		return fields.size();
	}

	public String buildQueryHql() {
		StringBuffer hql = new StringBuffer();
		String sort = "";
		int n = fields.size();
		for (int i = 0; i < n; i++) {
			String f = alias.get(i) + "." + fields.get(i);
			hql.append(",").append(f);
		}
		if (!StringUtils.isEmpty(sortInfo)) {
			sort = " order by " + sortInfo;
		}
		hql.append(" from ").append(fullEntryName).append(where).append(sort);
		return "select " + hql.substring(1);
	}
}