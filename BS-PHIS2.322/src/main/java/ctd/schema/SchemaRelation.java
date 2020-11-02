package ctd.schema;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import ctd.util.JSONUtils;
import ctd.util.S;

public class SchemaRelation implements Serializable{
	private static final long serialVersionUID = -2096091696710262197L;
	private String type;
	private String entryName;
	private Schema entrySchema;
	private String fullName;
	private String alias;
	private String joinWay;
	private List<Properties> updates = new ArrayList<Properties>();
	private List<Properties> joins = new ArrayList<Properties>();
	private List<Object> conditionsCnd = new ArrayList<Object>();
	private List<Object> joinCnds;
	
	public SchemaRelation(String type,String entryName,String alias,String tableName,Schema entrySchema){
		this.type = type;
		this.entryName = entryName;
		this.alias = alias;
		this.fullName = S.join(tableName," ",alias);
		this.entrySchema = entrySchema;
	}
	
	public String getType(){
		return type;
	}
	
	public String getEntryName(){
		return entryName;
	}
	
	public String getAlias(){
		return alias;
	}
	
	public String getFullEntryName(){
		return fullName;
	}
	
	public Schema getEntrySchema(){
		return entrySchema;
	}
	
	public String getJoinWay() {
		return joinWay;
	}

	public void setJoinWay(String joinWay) {
		this.joinWay = joinWay;
	}

	public void addUpdate(String from,String to,String func){
		Properties p = new Properties();
		p.setProperty("from", from);
		p.setProperty("to", to);
		p.setProperty("func", func);
		updates.add(p);
	}
	
	public void addJoinCondition(String parent,String child){
		Properties p = new Properties();
		p.setProperty("parent", parent);
		p.setProperty("child", child);
		joins.add(p);
	}
	
	public void addCondition(String exp){
		if(!StringUtils.isEmpty(exp)){
			try {
				List<?> l = JSONUtils.parse(exp, List.class);
				conditionsCnd.add(l);
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> getJoinCondition(){
		if(joinCnds != null){
			return joinCnds;
		}
		String pAlias = "";
		String cAlias = "";
		if(type.equals("parent")){
			pAlias = alias;
			cAlias = "a";
		}
		else{
			pAlias = "a";
			cAlias = alias;
		}
		int size = joins.size();
		if(size > 0){
			List<Object> cnds = new ArrayList<Object>();
			for(Properties p : joins){
				List<Object> cnd = new ArrayList<Object>();
				String f1 = pAlias + "." + p.getProperty("parent");
				String f2 = cAlias + "." + p.getProperty("child");
							
				cnd.add("eq");
				
				List<Object> ref1 = new ArrayList<Object>();
				ref1.add("$");
				ref1.add(f1);
				
				List<Object> ref2 = new ArrayList<Object>();
				ref2.add("$");
				ref2.add(f2);
				
				cnd.add(ref1);
				cnd.add(ref2);
				cnds.add(cnd);
			}
			if(conditionsCnd.size() > 0){
				cnds.addAll(conditionsCnd);
			}
			if(cnds.size() == 1){
				joinCnds = (List<Object>) cnds.get(0);
			}else{
				cnds.add(0, "and");
				joinCnds = cnds;
			}
		}
		return joinCnds;
	}
	
	public List<Properties>getUpdates(){
		return updates;
	}
	
	public List<Properties>getJoinConditions(){
		return joins;
	}	
}
