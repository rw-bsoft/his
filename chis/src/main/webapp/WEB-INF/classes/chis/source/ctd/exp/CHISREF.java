/**
 * @(#)REF.java Created on 2012-2-9 下午03:49:20
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.ctd.exp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.BaseDAO;
import chis.source.PersistentDataOperationException;
import chis.source.control.ControlRunner;
import chis.source.control.config.ActionConfig;
import chis.source.util.CNDHelper;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;

/**
 * @description 重新定义$，去context里面取相应的值来.
 * 
 * @author <a href="mailto:huangpf@bsoft.com.cn">huangpf</a>
 */
public class CHISREF extends ctd.util.exp.standard.REF {

	
	public CHISREF() {
		symbol = "#";
		name = symbol;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked","unused" })
	public Object run(List lsExp, ExpressionProcessor processor) throws ExpException {
		String name = (String) lsExp.get(1);
		boolean valueRef = false;
		if (name.startsWith("%")) {
			name = name.substring(1);
			valueRef = true;
		}
		HashMap<String, Object> arg = null;
		int size = lsExp.size();
		if (size > 2) {
			if (size == 4) {
				arg = (HashMap<String, Object>) lsExp.get(3);
			}
			if (size == 3) {
				if (!valueRef) {
					arg = (HashMap<String, Object>) lsExp.get(2);
				}
			}
		}
		if(valueRef){
			System.out.println("exp 'CHISREF' for exp '"+lsExp+"':"+ContextUtils.getContext().get(name));
			return ContextUtils.getContext().get(name);
		}
		
		//字段命中配置有点的分解后到对应的表中取，无点的到sourceEntry表中取。
		int dotPos = name.lastIndexOf(".");
		if(dotPos<0){
			String sourceEntryName =(String) ContextUtils.getContext().get(ControlRunner.toContextName(ControlRunner.SOURCE_ENTRY));
			//如果之前跑过NoRec表达式,可能把查询数据结果缓存到ctx中，是以list形式放的.
			Map<String,Object> data =null ;
			
			Object dataInCtx= ContextUtils.getContext().get(ControlRunner.toContextName(sourceEntryName));
			if(dataInCtx ==null ){
				dataInCtx = ContextUtils.getContext().get(ControlRunner.toContextNameCache(sourceEntryName));
			}
			if(dataInCtx instanceof List){
				List<Map<String,Object>> list =(List<Map<String,Object>>) dataInCtx;
				if(list.size()>0){
					data = list.get(0);
				}
			}else{
				data = (Map<String,Object>)dataInCtx;
			}
			
			if(data==null)
				return null;
			
			Object v= data.get(name);
			if(v instanceof Map){
				return ((Map<String,Object>)v).get("key");
			}
			System.out.println("exp 'CHISREF' for exp '"+lsExp+"':"+v+"@"+sourceEntryName);
			return v;
		}
		
		String entryName = name.substring(0, name.lastIndexOf("."));
		String fieldName = name.substring(name.lastIndexOf(".")+1, name.length());
		if(!ContextUtils.getContext().has(ControlRunner.toContextName(entryName))){
			try {
				loadData(entryName,ContextUtils.getContext());
			} catch (ServiceException e) {
				e.printStackTrace();
				throw new ExpException("权限验证表达式中数据查询失败.",e);
			}
		} 
		Map<String,Object> data =(Map<String,Object>) ContextUtils.getContext().get(ControlRunner.toContextName(entryName));
		if(data ==null ){
			System.out.println("exp 'CHISREF' for exp '"+lsExp+"':null");
			return null ;
		}
		Object v =  data.get(fieldName);
		
		if(v instanceof Map){
			v = ((Map<String,Object>)v).get("key");
			System.out.println("exp 'CHISREF' for exp '"+lsExp+"':"+v+"@"+entryName);
			return v;
		}
		System.out.println("exp 'CHISREF' for exp '"+lsExp+"':"+v+"@"+entryName);
		return v ;
	}
	
	/**
	 * 加载验证过程所需要的表的数据到context中.
	 * @param entryName
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	private void loadData(String entryName ,Context ctx) throws ServiceException{
		ActionConfig keyConfig =(ActionConfig) ctx.get(ControlRunner.toContextName(ControlRunner.KEY_CONFIG));
		String sourceEntry = (String)ctx.get(ControlRunner.toContextName(ControlRunner.SOURCE_ENTRY));
		Map<String,String> mappingInfo = keyConfig.getMapping(entryName, sourceEntry);
		List <?> cnd ;
		Schema sc = SchemaController.instance().getSchema(entryName);
		
		Object sourceDataObject =  ctx.get(ControlRunner.toContextName(sourceEntry));
		if(sourceDataObject ==null ){
			sourceDataObject = ctx.get(ControlRunner.toContextNameCache(sourceEntry));
		}
		
		Map<String,Object> sourceData =null;
		if(sourceDataObject instanceof List){
			sourceData = ((List<Map<String,Object>>)sourceDataObject).get(0);
		}else{
			sourceData = (Map<String,Object>) sourceDataObject;
		}
		
		if(mappingInfo!=null){//有关联根据关联配置查询
			String sourceColumn =(String) mappingInfo.get("sourceColumn");
			String destColumn = (String)mappingInfo.get("destColumn");
			String sourceValue = (String)sourceData.get(sourceColumn);
			cnd = CNDHelper.createSimpleCnd("eq", "a."+destColumn, "s", sourceValue);
		}else{
			String keyName = sc.getKey();
			if(sourceData.get(keyName)!=null){//根据empiId查询
				String key =(String)sourceData.get(keyName);
				cnd = CNDHelper.createSimpleCnd("eq","a."+keyName , "s", key);	
			}else if(sourceData.get("empiId")!=null){//根据phrId查询
				cnd = CNDHelper.createSimpleCnd("eq", "a.empiId", "s", sourceData.get("empiId"));
			}else if(sourceData.get("phrId")!=null){//根据主键查询
				cnd = CNDHelper.createSimpleCnd("eq","a.phrId", "s", sourceData.get("phrId"));
			}else if( ctx.get(ControlRunner.toContextName("empiId"))!=null)
				cnd = CNDHelper.createSimpleCnd("eq", "a.empiId", "s", ctx.get(ControlRunner.toContextName("empiId")));
			else{//没法找了.
				throw new ServiceException("无法查询关联表，权限验证失败。");
			}
		}
		
		try {
			BaseDAO dao = new BaseDAO();
			List<Map<String,Object>> dl = dao.doQuery(cnd, "", sc.getTableName());
			Map<String,Object> data = new HashMap<String,Object>(); 
			if(dl.size()>0){
				data=dl.get(0);
				System.out.println("running exp[chis.sourceRef] query cnd:"+cnd+"@"+entryName+" found rec:"+data);
			}else{
				System.out.println("running exp[chis.sourceRef] query cnd:"+cnd+"@"+entryName+" found no record.");
			}
			ctx.put(ControlRunner.toContextName(entryName),data);
		}catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ServiceException("验证数据查询失败:"+cnd+"@"+entryName);
		}
	}

	@SuppressWarnings("rawtypes")
	public String toString(List lsExp, ExpressionProcessor processor) throws ExpException {
		String name = (String) lsExp.get(1);
		if (name.startsWith("%")) {
			name = (String) run(lsExp, processor);
			int size = lsExp.size();
			if (!(size > 2 && lsExp.get(size - 1).equals("d"))) {
				name = "'" + name + "'";
			}
		}
		return name;
	}

}
