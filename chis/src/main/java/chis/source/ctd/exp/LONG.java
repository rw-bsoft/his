/**
 * @(#)Long.java Created on 2012-7-25 上午10:08:53
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.ctd.exp;

import java.util.List;

import ctd.util.exp.ExpException;
import ctd.util.exp.Expression;
import ctd.util.exp.ExpressionProcessor;

/**
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class LONG  extends Expression {

//	public Object run(List lsExp, Context ctx) {
//		Long d = (Long)lsExp.get(1);
//		return d;
//	}

	public String toString(List lsExp, ExpressionProcessor processor) throws ExpException {
		return String.valueOf(run(lsExp, processor));
	}

	@Override
	public Object run(List<?> lsExp, ExpressionProcessor processor)
			throws ExpException {
		Long d = (Long)lsExp.get(1);
		return d;
	}

}
