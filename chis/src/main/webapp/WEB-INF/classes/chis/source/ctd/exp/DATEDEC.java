/**
 * @(#)CHISDEC.java Created on 2012-2-9 下午03:49:20
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.ctd.exp;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import chis.source.util.BSCHISUtil;
import ctd.util.exp.ExpException;
import ctd.util.exp.Expression;
import ctd.util.exp.ExpressionProcessor;

/**
 * 
 * @description 日期相减,返回相差天数
 * 
 * @author <a href="mailto:huangpf@bsoft.com.cn">huangpf</a>
 */
@SuppressWarnings("rawtypes")
public class DATEDEC extends Expression {

	public DATEDEC() {
		symbol = "dateDec";
		name = symbol;
	}

	@Override
	public Object run(List<?> ls, ExpressionProcessor processor)
			throws ExpException {
		List la1 = (List) ls.get(1);
		Object d1 = processor.run(la1);
		List la2 = (List) ls.get(2);
		Object d2 = processor.run(la2);
		if (d1 instanceof String) {
			d1 = BSCHISUtil.toDate((String) d1);
		} else if (d1 instanceof Long) {
			d1 = new Date((Long) d1);
		}

		if (d2 instanceof String) {
			d2 = BSCHISUtil.toDate((String) d2);
		} else if (d2 instanceof Long) {
			d2 = new Date((Long) d2);
		}

		Calendar c1 = Calendar.getInstance();
		c1.setTime((Date) d1);

		Calendar c2 = Calendar.getInstance();
		c2.setTime((Date) d2);

		Double day = (c1.getTimeInMillis() - c2.getTimeInMillis())
				/ (1000.0 * 60 * 60 * 24);
		System.out.println("running exp[chis.sourceDes]: '"
				+ BSCHISUtil.toString((Date) d1) + "' - '"
				+ BSCHISUtil.toString((Date) d2) + "' =" + day);
		return day;
	}
}
