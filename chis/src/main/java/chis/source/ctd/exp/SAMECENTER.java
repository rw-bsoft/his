/**
 * @(#)LASTREC.java Created on 2012-2-9 下午03:49:20
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.ctd.exp;

import java.util.List;

import chis.source.util.CNDHelper;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpRunner;
import ctd.util.exp.Expression;
import ctd.util.exp.ExpressionProcessor;

/**
 * @description 判定两个机构是否属于同一中心
 * 
 * @author <a href="mailto:huangpf@bsoft.com.cn">huangpf</a>
 */
public class SAMECENTER extends Expression {

	
	public SAMECENTER() {
		symbol = "sameCenter";
		name = symbol;
	}
	
	private static final int CENTER_LENGTH=9;
	
//	@SuppressWarnings({ "rawtypes", "unchecked"})
//	public Object run(List lsExp, Context ctx) throws ExpException {
//		List<?> la1 =(List<?>) lsExp.get(1);
//		String v1 =(String)ExpRunner.run(la1, ctx);
//		int len1 = CENTER_LENGTH ;
//		if(v1.length()<CENTER_LENGTH){
//			len1 = v1.length();
//		}
//		if(v1==null)
//			return false ;
//		
//		List<?>la2 =(List<?>) lsExp.get(2);
//		String v2 = (String)ExpRunner.run(la2, ctx);
//		int len2 = CENTER_LENGTH ;
//		if(v2.length()<CENTER_LENGTH){
//			len2 = v2.length();
//		}
//		
//		if(v2==null)
//			return false;
//		
//		String ctr1 = v1.substring(0, len1);
//		String ctr2 = v2.substring(0, len2);
//		if(ctr1.equals(ctr2)){
//			return true ;
//		}
//		return false;
//	}
	
	
	public static void main(String []args) throws ExpException{
		SAMECENTER s = new SAMECENTER();
		List c = CNDHelper.toListCnd("['sv',['s','1234567890'],['s','1234567890']]");
		String ctr1 = "123".substring(0, CENTER_LENGTH);
		System.out.println(ctr1);
		//s.run(c, new Context());
	}


	@Override
	public Object run(List<?> lsExp, ExpressionProcessor processor)
			throws ExpException {
		List<?> la1 =(List<?>) lsExp.get(1);
		String v1 =(String)processor.run(la1);
		int len1 = CENTER_LENGTH ;
		if(v1.length()<CENTER_LENGTH){
			len1 = v1.length();
		}
		if(v1==null) {
			return false ;
		}
		
		List<?>la2 =(List<?>) lsExp.get(2);
		String v2 = (String)processor.run(la2);
		int len2 = CENTER_LENGTH ;
		if(v2.length()<CENTER_LENGTH){
			len2 = v2.length();
		}
		
		if(v2==null) {
			return false;
		}
		
		String ctr1 = v1.substring(0, len1);
		String ctr2 = v2.substring(0, len2);
		if(ctr1.equals(ctr2)){
			return true ;
		}
		return false;
	}
}
