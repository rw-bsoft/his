/*
 * 
 */
package chis.source.control.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;

import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;

import ctd.util.exp.ExpException;
import ctd.util.xml.XMLHelper;

/**
 * 解析权限控制配置文件.
 * @description
 * 
 * @author <a href="mailto:huangpf@bsoft.com.cn">huangpf</a>
 */
public class ActionConfigParser  {

	public static void main(String []args) throws Exception{
		ActionConfigParser acp = new ActionConfigParser();
		System.out.println(acp.parse());
	}
	
	//供继承用的配置
	private Map <String,EntryConfig> idConfigCache  = new HashMap<String,EntryConfig>();

	/**
	 * 
	 * @return
	 * @throws IOException 
	 */
	private String getFileName() throws IOException {
		return BSCHISUtil.getConfigPath("ActionControl.xml");
	}
	
	
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Map <String,EntryConfig>   parse() throws Exception{
		File f = new File(getFileName());
		if(!f.exists()){
			throw new Exception("权限验证配置文件不存在:"+getFileName());
		}
		Document doc =XMLHelper.getDocument(f);
		Element root = doc.getRootElement();
		List<Element> entryElements =root.elements();
		for(int i=0;i<entryElements.size();i++){
			EntryConfig ec = parseEntryConfig(root,entryElements.get(i));
			idConfigCache.put(ec.getId(), ec);
		}
		
		return idConfigCache;
	}
	
	/**
	 * 
	 * @param root
	 * @param entryElement
	 * @return
	 * @throws ExpException 
	 */
	@SuppressWarnings("unchecked")
	private EntryConfig parseEntryConfig(Element root,Element entryElement) throws ExpException{
		String id = entryElement.attributeValue("id") ;
		String extend = entryElement.attributeValue("extend") ;
		String clz = entryElement.attributeValue("class") ;
		EntryConfig ec = new EntryConfig();
		ec.setId(id);
		ec.setClz(clz);
		//如果有继承，先从缓存中查看是否被继承配置已解析，若没有找到该节点并解析。
		if(extend!=null){
			EntryConfig pc = idConfigCache.get(extend);
			if(pc==null){
				Element  parent = root.element(extend);
				if(parent!=null){
					pc = parseEntryConfig(root,parent);
					this.idConfigCache.put(pc.getId(), pc);
				}
			}
			//父类的配置拷贝到子类中.
			ec.setActionConfigs(pc.getActionConfigs());
			ec.setMappings(pc.getMappings());
		}
		//解析entry下是否有mapping配置.
		Element mappingsElement = entryElement.element("mappings");
		String [] mappings =null ;
		if(mappingsElement!=null){
			mappings = parseMappings(mappingsElement);
		}
		ec.setMappings(mappings);
		
		//循环解析action配置
		List<Element> actionElements = entryElement.elements("action");
		for(int i =0 ;i<actionElements.size();i++){
			parseActionConfig(ec,actionElements.get(i));
		}
		return ec ;
	}
	
	/**
	 * 
	 * @param actionElement
	 * @return
	 * @throws ExpException 
	 */
	private void parseActionConfig(EntryConfig ec,Element actionElement) {
		String actionName =actionElement.attributeValue("name");
		if(actionName ==null || actionName.trim().length()==0){
			return ;
		}
		
		ActionConfig ac  = new ActionConfig();
		ac.setActionName(actionName.trim());
		if(ec.getMappings()!=null && ec.getMappings().length>0){
			ac.setMappings(ec.getMappings());
		}
		//解析mappings
		Element mappingsElement = actionElement.element("mappings");
		if(mappingsElement!=null){
			ac.setMappings(parseMappings(mappingsElement));
		}
		
		//解析cnd
		Element conditionElement  = actionElement.element("condition");
		if(conditionElement==null){
			return;
		}
		String cnd = conditionElement.getTextTrim();
		if(cnd ==null || "".endsWith(cnd.trim())){
			return ;
		}
		
		cnd = cnd.trim() ;
		cnd = cnd.replaceAll("\\$", "#");
		try{
			ac.setCondition(CNDHelper.toListCnd(cnd));
			System.out.println(ac.getCondition());
		}catch(Exception e){
			e.printStackTrace();
		}
		if(actionName.contains(",")){
			String[] actionNames = actionName.split(",");
			for(int i =0;i<actionNames.length ;i++){
				String acName = actionNames[i];
				ActionConfig cp = ac.clone();
				cp.setActionName(acName);
				ec.addAction(cp);
			}
			return ;
		}
		ec.addAction(ac);
	}
	
	/**
	 * 解析mapping配置
	 * @param mappingsElement
	 * @return
	 */
	private String[] parseMappings(Element mappingsElement){
		List<String> mappings = new ArrayList<String>();
		@SuppressWarnings("unchecked")
		List<Element> mappingList = mappingsElement.elements();
		for(int i = 0 ;i<mappingList.size() ;i++){
			Element me = mappingList.get(i);
			if(!"mapping".equals(me.getName())){
				continue ;
			}
			String mappingInfo = me.getText();
			if(mappingInfo ==null){
				continue ;
			}
			mappingInfo = mappingInfo.replaceAll(" ", "");
			if(!"".equals(mappingInfo)){
				mappings.add(mappingInfo);
			}
		}
		
		String[] res = new String[mappings.size()];
		for(int i = 0 ;i<mappings.size();i++){
			res[i] = mappings.get(i);
		}
		
		return res;
	}
}
