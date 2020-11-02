/**
 * @(#)NOREC.java Created on 2012-2-9 下午03:49:20
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.ctd.exp;
import java.util.List;
import java.util.Map;

import chis.source.BaseDAO;
import chis.source.control.ControlRunner;
import chis.source.util.CNDHelper;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.util.context.ContextUtils;
import ctd.util.exp.ExpException;
import ctd.util.exp.Expression;
import ctd.util.exp.ExpressionProcessor;

/**
 * 
 * @description 根据empiId查找判断是否存在档案.
 * 
 * @author <a href="mailto:huangpf@bsoft.com.cn">huangpf</a>
 */
@SuppressWarnings("rawtypes")
public class NOREC extends Expression {

	public NOREC() {
		symbol = "noRec";
		name = symbol;
	}

//	/**
//	 * 如果不存在记录返回true，否则返回false.
//	 */
//	@SuppressWarnings("unchecked")
//	public Object run(List lsExp, Context ContextUtils.getContext()) throws ExpException {
//		String  sourceEntry =(String) ContextUtils.getContext().get(ControlRunner.toContextName(ControlRunner.SOURCE_ENTRY));
//		String empiId = (String) ContextUtils.getContext().get(ControlRunner.toContextName(ControlRunner.EMPIID));
//		if(sourceEntry ==null){
//			throw new ExpException("验证失败,表名未被放入context中。");
//		}
//		boolean result = false ;
//		Object dataInCtx = ContextUtils.getContext().get(ControlRunner.toContextName(sourceEntry));
//		if(dataInCtx ==null ){
//			dataInCtx = ContextUtils.getContext().get(ControlRunner.toContextNameCache(sourceEntry));
//		}
//		//如果被放入context的数据是NULL 说明外部传入的查询结果就是null，没有数据
//		if(dataInCtx ==null || isEmptyMap(dataInCtx))
//			result= true ;
//		
//		if(empiId==null){
//			throw new ExpException("验证失败,empiId未被放入context中。");
//		}
//		//如果是列表 说明外部已经提供了数据列表，无需再查询
//		if(dataInCtx instanceof List){
//			//对列表中的数据进行排序.
//			List<Map<String,Object>> list  = (List<Map<String,Object>>)dataInCtx ;
//			if(list.size()==0){
//				result= true ;
//			}else{
//				result= false ;
//			}
//		}else{//如果外部提供的是map 需要根据Map中的empiId进行数据查询。
//			//如果外部传入数据是包含主键说明档案已经存在，返回false
//			
//			BaseDAO dao = new BaseDAO();
//			List<?> cnd = CNDHelper.createSimpleCnd("eq", "empiId", "s", empiId);
//			try {
//				Schema sc = SchemaController.instance().getSchema(sourceEntry);
//				StringBuffer order = new StringBuffer(sc.getKey().getId()+" desc");
//				List<Map<String,Object>> list = dao.doQuery(cnd, order.toString(), sc.getTableName());
//				if(list.size()==0){
//					result= true ;
//				}else{
//					//查询出的数据放入ctx中供后续的表达式验证使用。
//					ContextUtils.getContext().put(ControlRunner.toContextNameCache(sourceEntry), list);
//					result= false ;
//				}
//				
//			} catch (PersistentDataOperationException e) {
//				e.printStackTrace();
//				throw new ExpException("表达式数据查询失败:"+e.getMessage());
//			}
//		}
//		
//		System.out.println("exp 'NoRec' for schema '"+sourceEntry+"':"+result);
//		return result; 
//	}
//	
	private boolean isEmptyMap(Object obj){
		if(obj instanceof Map){
			Map map = (Map) obj;
			if(map.size()==0){
				return true ;
			}
		}
		return false ;
	}

	@Override
	public Object run(List<?> ls, ExpressionProcessor processor)
			throws ExpException {
		String  sourceEntry =(String) ContextUtils.getContext().get(ControlRunner.toContextName(ControlRunner.SOURCE_ENTRY));
		String empiId = (String) ContextUtils.getContext().get(ControlRunner.toContextName(ControlRunner.EMPIID));
		if(sourceEntry ==null){
			throw new ExpException("验证失败,表名未被放入context中。");
		}
		boolean result = false ;
		Object dataInCtx = ContextUtils.getContext().get(ControlRunner.toContextName(sourceEntry));
		if(dataInCtx ==null ){
			dataInCtx = ContextUtils.getContext().get(ControlRunner.toContextNameCache(sourceEntry));
		}
		//如果被放入context的数据是NULL 说明外部传入的查询结果就是null，没有数据
		if(dataInCtx ==null || isEmptyMap(dataInCtx))
			result= true ;
		
		if(empiId==null){
			throw new ExpException("验证失败,empiId未被放入context中。");
		}
		//如果是列表 说明外部已经提供了数据列表，无需再查询
		if(dataInCtx instanceof List){
			//对列表中的数据进行排序.
			List<Map<String,Object>> list  = (List<Map<String,Object>>)dataInCtx ;
			if(list.size()==0){
				result= true ;
			}else{
				result= false ;
			}
		}else{//如果外部提供的是map 需要根据Map中的empiId进行数据查询。
			//如果外部传入数据是包含主键说明档案已经存在，返回false
			
			BaseDAO dao = new BaseDAO();
			List<?> cnd = CNDHelper.createSimpleCnd("eq", "empiId", "s", empiId);
			try {
				Schema sc = SchemaController.instance().get(sourceEntry);
				StringBuffer order = new StringBuffer(sc.getKey()+" desc");
				List<Map<String,Object>> list = dao.doQuery(cnd, order.toString(), sc.getEntityName());
				if(list.size()==0){
					result= true ;
				}else{
					//查询出的数据放入ctx中供后续的表达式验证使用。
					ContextUtils.getContext().put(ControlRunner.toContextNameCache(sourceEntry), list);
					result= false ;
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				throw new ExpException("表达式数据查询失败:"+e.getMessage());
			}
		}
		
		System.out.println("exp 'NoRec' for schema '"+sourceEntry+"':"+result);
		return result; 
	}
}
