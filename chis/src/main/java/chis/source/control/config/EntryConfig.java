/*
 * @(#)Bschis.sourceDAO.java Created on 2012-02-08 下午3:03:44
 *
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.control.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.util.CNDHelper;

/**
 * @description
 * 
 * @author <a href="mailto:huangpf@bsoft.com.cn">huangpf</a>
 */
public class EntryConfig {
	
	private  static Map<String,EntryConfig> confs =null;
	
	private String id ;
	private String[] mappings;
	private String clz;
	private Map<String,ActionConfig> actionConfigs = new HashMap<String,ActionConfig>();
	
//	static {
//		EntryConfig healthrecord = new EntryConfig();
//		ActionConfig create = new ActionConfig();
//		create.setActionName("create");
//		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "MDC_HypertensionRecord.manaDoctorId","$", "%user.userId");
//		List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "status","s", "0");
//		create.setCondition(CNDHelper.createArrayCnd("and", cnd1, cnd2));
//		create.setMappings(new String[]{"EHR_HealthRecord.phrId=MDC_HypertensionRecord.phrId"});
//		healthrecord.addAction(create);
//		//healthrecord.setEntryName("EHR_HealthRecord");
//		confs.put("EHR_HealthRecord", healthrecord);
//	}
	
	public static EntryConfig getContorlConfig(String entryName){
		
		if(confs ==null){
			ActionConfigParser ap = new ActionConfigParser();
			try {
				confs = ap.parse();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return confs.get(entryName);
	}
	
	public String toString(){
		String s ="<[id:"+id+"\n";
		s+=actionConfigs+"]>";
		return s;
	}
	/**
	 * 
	 * @return
	 */
	public String getClz() {
		return clz;
	}

	/**
	 * 
	 * @param clz
	 */
	public void setClz(String clz) {
		this.clz = clz;
	}
	/**
	 * 
	 * @return
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * 
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 
	 * @param keyName
	 * @return
	 */
	public ActionConfig getActionConfig(String keyName){
		return actionConfigs.get(keyName);
	}
	
	/**
	 * 
	 * @return
	 */
	public String[] getMappings() {
		return mappings;
	}

	/**
	 * 
	 * @param mappings
	 */
	public void setMappings(String[] mappings) {
		this.mappings = mappings;
	}
	
	/**
	 * 
	 * @return
	 */
	public String[] getKeyNames(){
		String[] keyNames = new String[this.actionConfigs.size()];
		int i = 0;
		for(String key : actionConfigs.keySet()){
			keyNames[i++]=key;
		}
		return keyNames;
	}
	
	/**
	 * 
	 * @return
	 */
	public ActionConfig[] getActionConfigArray(){
		ActionConfig[] res = new ActionConfig[this.actionConfigs.size()];
		int i = 0;
		for(ActionConfig keyConf : actionConfigs.values()){
			res[i++]=keyConf;
		}
		return res;
	}
	
	/**
	 * 
	 * @return
	 */
	public Map<String,ActionConfig> getActionConfigs(){
		return actionConfigs;
	}
	
	/**
	 * 
	 * @param actionsConfigs
	 */
	public void setActionConfigs(Map<String,ActionConfig> actionsConfigs){
		this.actionConfigs = new HashMap<String,ActionConfig>(actionsConfigs);
	}
	/**
	 * 
	 * @param actionConfig
	 */
	public void addAction(ActionConfig actionConfig) {
		this.actionConfigs.put(actionConfig.getActionName(), actionConfig);
	}
}
