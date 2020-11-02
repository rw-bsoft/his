/**
 * @(#)LASTREC.java Created on 2012-2-9 下午03:49:20
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.ctd.exp;

import java.util.List;
import java.util.Map;

import chis.source.BaseDAO;
import chis.source.control.ControlRunner;
import chis.source.util.CNDHelper;
import chis.source.util.OrderByCreateDateDesc;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.util.context.ContextUtils;
import ctd.util.exp.ExpException;
import ctd.util.exp.Expression;
import ctd.util.exp.ExpressionProcessor;
import edu.emory.mathcs.backport.java.util.Collections;

/**
 * @description 根据empiId取出多条档案的最后一条，根据createDate排序，进行后续表达式判定。
 * 
 * @author <a href="mailto:huangpf@bsoft.com.cn">huangpf</a>
 */
public class LASTREC extends Expression {

	
	public LASTREC() {
		symbol = "lastRec";
		name = symbol;
	}
	
//	@SuppressWarnings({ "rawtypes", "unchecked"})
//	public Object run(List lsExp, Context ContextUtils.getContext()) throws ExpException {
//		String sourceEntry =(String) ContextUtils.getContext().get(ControlRunner.toContextName(ControlRunner.SOURCE_ENTRY));
//		String empiId =(String) ContextUtils.getContext().get(ControlRunner.toContextName(ControlRunner.EMPIID));
//		if(sourceEntry ==null || empiId==null){
//			throw new ExpException("验证失败,empiId或者表名未被放入context中。");
//		}
//		Object dataInCtx = ContextUtils.getContext().get(ControlRunner.toContextName(sourceEntry));
//		if(dataInCtx ==null ){
//			dataInCtx = ContextUtils.getContext().get(ControlRunner.toContextNameCache(sourceEntry));
//		}
//		
//		Map<String,Object> data = null ;
//		List<Map<String,Object>> list = null ;
//		if(dataInCtx ==null){
//			throw new ExpException("验证失败,必要的数据未被放入context中。");
//		}
//		if(dataInCtx instanceof List){
//			//对列表中的数据进行排序.
//			list  = (List<Map<String,Object>>)dataInCtx ;
//			//按照建档日期倒叙排序.
//			if(list.size()>1){
//				Collections.sort(list, new OrderByCreateDateDesc());
//			}
//			data = list.get(0);
//		}else{
//			BaseDAO dao = new BaseDAO();
//			List cnd  = CNDHelper.createSimpleCnd("eq", "empiId", "s", empiId);
//			try {
//				StringBuffer order = new StringBuffer("createDate desc,");
//				Schema sc = SchemaController.instance().getSchema(sourceEntry);
//				order.append(sc.getKey()).append(" desc ");
//				list = dao.doQuery(cnd, order.toString(), sc.getTableName());
//			} catch (PersistentDataOperationException e) {
//				e.printStackTrace();
//				throw new ExpException("表达式中数据查询失败.",e);
//			}
//			if(list.size()>0){
//				data =(Map<String,Object>) list.get(0);
//				ContextUtils.getContext().put(ControlRunner.toContextNameCache(sourceEntry), data);
//			}
//		}
//		
//		ContextUtils.getContext().put(ControlRunner.toContextName(sourceEntry), data);
//		System.out.println("running exp[lastRec]:"+data);
//		List la1 = (List) lsExp.get(1);
//		Object result =  ExpRunner.run(la1, ContextUtils.getContext());
//		//验证完毕将原来的数据放回context中。
//		if(dataInCtx!=null){
//			ContextUtils.getContext().put(ControlRunner.toContextName(sourceEntry), dataInCtx);
//		}
//		System.out.println(lsExp+":"+result);
//		return result;
//	}

	@SuppressWarnings("unchecked")
	@Override
	public Object run(List<?> lsExp, ExpressionProcessor processor)
			throws ExpException {
		String sourceEntry =(String) ContextUtils.getContext().get(ControlRunner.toContextName(ControlRunner.SOURCE_ENTRY));
		String empiId =(String) ContextUtils.getContext().get(ControlRunner.toContextName(ControlRunner.EMPIID));
		if(sourceEntry ==null || empiId==null){
			throw new ExpException("验证失败,empiId或者表名未被放入context中。");
		}
		Object dataInCtx = ContextUtils.getContext().get(ControlRunner.toContextName(sourceEntry));
		if(dataInCtx ==null ){
			dataInCtx = ContextUtils.getContext().get(ControlRunner.toContextNameCache(sourceEntry));
		}
		
		Map<String,Object> data = null ;
		List<Map<String,Object>> list = null ;
		if(dataInCtx ==null){
			throw new ExpException("验证失败,必要的数据未被放入context中。");
		}
		if(dataInCtx instanceof List){
			//对列表中的数据进行排序.
			list  = (List<Map<String,Object>>)dataInCtx ;
			//按照建档日期倒叙排序.
			if(list.size()>1){
				Collections.sort(list, new OrderByCreateDateDesc());
			}
			data = list.get(0);
		}else{
			BaseDAO dao = new BaseDAO();
			List<?> cnd  = CNDHelper.createSimpleCnd("eq", "empiId", "s", empiId);
			try {
				StringBuffer order = new StringBuffer("createDate desc,");
				Schema sc = SchemaController.instance().get(sourceEntry);
				order.append(sc.getKey()).append(" desc ");
				list = dao.doQuery(cnd, order.toString(), sc.getEntityName());
			} catch (Exception e) {
				e.printStackTrace();
				throw new ExpException("表达式中数据查询失败.",e);
			}
			if(list.size()>0){
				data =(Map<String,Object>) list.get(0);
				ContextUtils.getContext().put(ControlRunner.toContextNameCache(sourceEntry), data);
			}
		}
		
		ContextUtils.getContext().put(ControlRunner.toContextName(sourceEntry), data);
		System.out.println("running exp[lastRec]:"+data);
		List<?> la1 = (List<?>) lsExp.get(1);
		Object result =  ExpressionProcessor.instance().run(la1);
		//验证完毕将原来的数据放回context中。
		if(dataInCtx!=null){
			ContextUtils.getContext().put(ControlRunner.toContextName(sourceEntry), dataInCtx);
		}
		System.out.println(lsExp+":"+result);
		return result;
	}
}
