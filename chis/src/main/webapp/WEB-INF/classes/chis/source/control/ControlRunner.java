/*
 * @(#)Bschis.sourceDAO.java Created on 2012-02-08 下午3:03:44
 *
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.control;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.control.config.ActionConfig;
import chis.source.control.config.EntryConfig;
import chis.source.util.UserUtil;
import ctd.controller.exception.ControllerException;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;

public class ControlRunner {
	//需要验证的类型
	public static final String ALL="all";//全部按钮验证
	public static final String UPDATE="update";//只验证readOnly
	public static final String CREATE="create";//只验证create
	public static final String LOG_OUT="logout";//只验证logout

	
	public static final String CTRL_CTX_NAME = "controlCtx";
	public static final String SOURCE_ENTRY="sourceEntry";
	public static final String EMPIID ="empiId";
	public static final String PKEY="pkey";
	public static final String PHRID="phrId";
	public static final String KEY_CONFIG="keyConfig";
	
	
	/**
	 * @param args
	 * @throws ServiceException 
	 */
	public static void main(String[] args) throws ServiceException {
		run("EHR_Healthrecord",new HashMap<String, Object>(),new Context(),CREATE);//只验证create
		run("EHR_Healthrecord",new HashMap<String, Object>(),new Context(),UPDATE,LOG_OUT);//只验证readonly 和logout
	}
	
	/**
	 * 若果需要自定义验证内容就调用以下方法
	 * @param entryName
	 * @param data 是一个数据列表list或者单条数据的map
	 * @param ctx
	 * @param args 需要被验证的actions
	 * @return
	 * @throws ServiceException
	 */
	public static Map<String,Boolean> run(String entryName,Map<String,Object> data,Context ctx,String ... args )throws ServiceException{
		Schema sc = null;
		try {
			sc = SchemaController.instance().get(entryName);
		} catch (ControllerException e1) {
			e1.printStackTrace();
			throw new ServiceException("权限验证失败,未找到表单:"+entryName);
		}
		String tableName = sc.getEntityName();
		
		Map <String , Boolean> result = new HashMap<String,Boolean>();
		EntryConfig  conf = EntryConfig.getContorlConfig(tableName);
		if(conf==null)
			throw new ServiceException("权限验证失败,未找到配置:"+entryName);
		
		if(data!=null){
			setData(entryName,data,ctx);
		}
		checkControlContext(ctx);
		initControlContext(entryName,ctx);
		
		ActionConfig[] KeyConfigs = conf.getActionConfigArray();
		String curRole = UserUtil.get(UserUtil.JOB_ID);
		
		for(ActionConfig actionConf:KeyConfigs){
			//只验证指定的action
			if(!containAction(args,actionConf.getActionName())){
				continue ;
			}
			
			//这里可能导致一些按钮出现问题，比如注销和恢复按钮需要根据档案的状态为来判断是否可操作的.
			if("system".equals(curRole.substring(curRole.indexOf(".")+1))){
				result.put(actionConf.getActionName(),true);
				continue ;
			}
			
			ctx.put(toContextName(KEY_CONFIG), actionConf);
			try {
				result.put(actionConf.getActionName(),(Boolean) ExpressionProcessor.instance().run(actionConf.getCondition()));
			} catch (ExpException e) {
				throw new ServiceException("权限验证失败,错误的表达式配置:"+actionConf.getCondition(),e);
			}
		}
		data.put("_actions", result);
		return result ;
	}
	
	public static void run(String entryName,List<Map<String,Object>> list,Context ctx,String ... args )throws ServiceException{
		for(int i=0 ;i<list.size();i++){
			Map <String ,Object> data = list.get(i);
			run(entryName,data,ctx,args);
		}
	}
	/**
	 * 默认值验证readonly的action
	 * @param entryName 当前被权限验证的表单。
	 * @param data 是一个数据列表list或者单条数据的map
	 * @param ctx
	 * @return
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public static Map<String,Boolean> run (String entryName,Object data,Context ctx) throws ServiceException{
		if(data instanceof List){
			run(entryName,(List<Map<String,Object>>)data,ctx,UPDATE);
			return new HashMap<String,Boolean>();
		}else{
			return run(entryName,(Map<String,Object>)data,ctx,UPDATE);
		}
	}
	
	/**
	 * 设置单条数据到权限验证上下文中。
	 * @param entryName
	 * @param data 是一个数据列表list或者单条数据的map
	 * @param ctx
	 */
	public static void setData(String entryName , Object data,Context ctx){
			checkControlContext(ctx);
			ctx.put(toContextName(entryName), data);
	}
	
	/**
	 * 单独设置empiId , phrId 或者主键信息
	 * @param name
	 * @param value
	 */
	public static void setData(String name, String value,Context ctx){
		ctx.put(toContextName(name), value);
	}
	
		
	public static String toContextName(String name){
		return new StringBuffer(CTRL_CTX_NAME).append(".").append(name).toString();
	}
	
	public static String toContextNameCache(String name){
		return new StringBuffer(CTRL_CTX_NAME).append(".").append(name).append("_cache").toString();
	}
	
	public static String toContextName(String name1,String name2){
		return new StringBuffer(CTRL_CTX_NAME).append(".").append(name1)
				.append(".").append(name2).toString();
	}
	
	/**
	 * 初始化权限控制上下文。
	 * @param entryName
	 * @param ctx
	 * @throws ServiceException 
	 */
	@SuppressWarnings("unchecked")
	private static void initControlContext(String entryName,Context ctx) throws ServiceException{
		String cN = toContextName(entryName);
		if(!ctx.containsKey(cN)){
			throw new ServiceException("未将数据放入context中进行验证:"+entryName);
		}
		
		Schema sc;
		try {
			sc = SchemaController.instance().get(entryName);
		} catch (ControllerException e) {
			throw new ServiceException("权限验证失败,未找到表单:"+entryName);
		}
		if(sc==null){
			throw new ServiceException("未定义的schema:"+entryName);
		}
		/*将被验证数据中的empiId,phrId,主键放入验证context中.结构如下:
		 * empId:'123',
		 * phrId:'123',										
		 * pkey:'123',							//触发验证表的主键
		 * sourceEntry:'EHR_HealthRecord',      //触发验证的表
		 * ActionConfig: actionConfig,				//当前验证的配置信息
		 * EHR_HealthRecord:{...}				//数据
		 * MDC_HypertensionRecord:{...}			//
		 */
		Map<String,Object> data = null ;
		Object dataInCtx = ctx.get(toContextName(entryName));
		
		if(dataInCtx instanceof List){
			List<Map<String,Object>> list = (List<Map<String,Object>>)dataInCtx;
			if(list.size()>0){
				data = list.get(0);
			}
		}else{
			data =(Map<String,Object>) ctx.get(toContextName(entryName));
		}
		
		if(data.get(EMPIID)!=null){
			ctx.put(toContextName(EMPIID), data.get(EMPIID));
		}
		if(data.get(PHRID)!=null){
			ctx.put(toContextName(PHRID), data.get(PHRID));
		}
		String pkey = sc.getKey();
		ctx.put(toContextName(PKEY), data.get(pkey));
		ctx.put(toContextName(SOURCE_ENTRY), entryName);
	}
	
	/**
	 * 是否包含action名字
	 * @param array
	 * @param element
	 * @return
	 */
	private static boolean containAction(String[]array , String element){
		if(array ==null || array.length ==0||element==null)
			return false ;
		for(int i = 0 ;i<array.length ;i++){
			if(ALL.equals(array[i])){
				return true ;
			}
			if(element.equals(array[i])){
				return true ;
			}
		}
		return false ;
	}
	
	/**
	 * 创建权限控制上下文。
	 * @param ctx
	 */
	private static void checkControlContext(Context ctx){
		if(!ctx.containsKey(CTRL_CTX_NAME)){
			Context controCtx = new Context();
			ctx.put(CTRL_CTX_NAME, controCtx);
		}
	}
}
