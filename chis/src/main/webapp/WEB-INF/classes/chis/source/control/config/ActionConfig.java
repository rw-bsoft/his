/*
 * @(#)Bschis.sourceDAO.java Created on 2012-02-08 下午3:03:44
 *
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.control.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @description
 * 
 * @author <a href="mailto:huangpf@bsoft.com.cn">huangpf</a>
 */
public class ActionConfig {
	
	private List<?> condition;
	private String[] mappings;
	private String actionName ;
	
	public List<?> getCondition() {
		return condition;
	}
	public void setCondition(List<?> condition) {
		this.condition = condition;
	}
	public String[] getMappings() {
		return mappings;
	}
	public void setMappings(String[] mappings) {
		this.mappings = mappings;
	}
	public String getActionName() {
		return actionName;
	}
	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
	
	public String toString(){
		String s = "mappings:"+mappings+" condition:"+condition;
		return s ;
	}
	
	public ActionConfig clone(){
		ActionConfig ac = new ActionConfig();
		ac.setActionName(this.actionName);
		ac.setCondition(this.condition);
		ac.setMappings(this.mappings);
		return ac;
	}
	
	/**
	 * 是否存在跟这个表有关的mapping信息。
	 * @param entryName
	 * @return
	 */
	public Map<String,String> getMapping(String destEntry,String sourceEntry){
		String sourceColumn=null;
		String sourceFullColumn = null ;
		String destColumn = null ;
		String destFullColumn = null ;
		if(mappings==null || mappings.length==0)
			return null;
		
		for(String mappingInfo : mappings){
			if(mappingInfo.contains(destEntry)){
				String [] mappingPartten = mappingInfo.split("=");
				if(mappingPartten[0].contains(destEntry)){
					sourceFullColumn = mappingPartten[0] ;
					String [] destParttem = sourceFullColumn.split("\\.");
					destColumn=destParttem[1];
					
					destFullColumn =mappingPartten[1] ;
					String [] sroucePartten = destFullColumn.split("\\.");
					sourceColumn=sroucePartten[1];
				}else{
					destFullColumn =mappingPartten[0] ;
					String [] destParttem = destFullColumn.split("\\.");
					destColumn=destParttem[1];
					
					sourceFullColumn = mappingPartten[1] ;
					String [] sroucePartten =sourceFullColumn.split("\\.");
					sourceColumn=sroucePartten[1];
				}
				Map<String,String> result = new HashMap<String,String>();
				result.put("sourceColumn", sourceColumn);
				result.put("destColumn", destColumn);
				result.put("sourceFullColumn", sourceFullColumn);
				result.put("destFullColumn", destFullColumn);
				return result;
			}
		}
		return null;
	}
	
	public static void main(String[] args){
		ActionConfig c = new ActionConfig();
		c.mappings=new String[]{
				"EHR_HealthRecord.phrId = MDC_Hypertension.phrId1",
				"EHR_HealthRecord.phrId = MDC_Diabates.phrId2"
		};
		System.out.println(c.getMapping("MDC_Diabates","EHR_HealthRecord"));
	}
	
}
