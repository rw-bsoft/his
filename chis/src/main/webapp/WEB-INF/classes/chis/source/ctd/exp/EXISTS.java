/**
 * @(#)EXISTS.java Created on 2010-7-31 上午11:38:02
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.ctd.exp;


import java.util.List;

import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpRunner;
import ctd.util.exp.Expression;
import ctd.util.exp.ExpressionProcessor;

/**
 * @description 表示一个sql里的exists子句。
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class EXISTS extends Expression {
	
	public EXISTS(){
		symbol = "exists";
	}

	/**
	 * @see ctd.util.exp.Expression#run(org.json.JSONArray, ctd.util.context.Context)
	 */
	public String run(List jsonExp, Context ctx) {
		return "false";
	}

//	/**
//	 * @see ctd.util.exp.Expression#toString(org.json.JSONArray, ctd.util.context.Context)
//	 */
	public String toString(List jsonExp, ExpressionProcessor processor) {
		if (jsonExp.size() != 3) {
			return "";
		}
		StringBuffer expr = new StringBuffer();
		try {
			List exp = (List) jsonExp.get(1);
			String s1 = ExpressionProcessor.instance().toString(exp);
			expr.append(" ").append("exists").append(" (select 1 from ")
			.append(s1).append(" where ");
			List exp2 = (List) jsonExp.get(2);
			return expr.append(ExpressionProcessor.instance().toString(exp2)).append(")").toString();
		} catch (Exception e) {
//			logger.error("run JSONException: ", e);
		}
		return "";

	}

	@Override
	public Object run(List<?> jsonExp, ExpressionProcessor processor)
			throws ExpException {
		if (jsonExp.size() != 3) {
			return "";
		}
		StringBuffer expr = new StringBuffer();
		try {
			List exp = (List) jsonExp.get(1);
			String s1 = ExpressionProcessor.instance().toString(exp);
			expr.append(" ").append("exists").append(" (select count(*) from ")
			.append(s1).append(" where ");
			List exp2 = (List) jsonExp.get(2);
			return expr.append(ExpressionProcessor.instance().toString(exp)).append(")").toString();
		} catch (Exception e) {
//			logger.error("run JSONException: ", e);
		}
		return "";

	}
}
