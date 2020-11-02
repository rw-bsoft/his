package ctd.util.exp.standard;

import java.util.HashSet;
import java.util.List;

import ctd.util.converter.ConversionUtils;
import ctd.util.exp.ExpException;
import ctd.util.exp.Expression;
import ctd.util.exp.ExpressionProcessor;

public class IN extends Expression {

	public IN() {
		symbol = "in";
	}

	@Override
	public Object run(List<?> ls, ExpressionProcessor processor)
			throws ExpException {
		try {
			Object o = processor.run((List<?>) ls.get(1));
			List<?> rang = (List<?>) ls.get(2);
			HashSet<Object> set = new HashSet<Object>();
			set.addAll(rang);
			return set.contains(o);
		} catch (Exception e) {
			throw new ExpException(e.getMessage());
		}
	}

	@Override
	public String toString(List<?> ls, ExpressionProcessor processor)
			throws ExpException {
		try {
			Object o = processor.toString((List<?>) ls.get(1));
			StringBuffer sb = new StringBuffer(ConversionUtils.convert(o,
					String.class));
			sb.append(" ").append(symbol).append("(");
			List<?> rang = (List<?>) ls.get(2);
			for (int i = 0, size = rang.size(); i < size; i++) {
				if (i > 0) {
					sb.append(",");
				}
				Object r = rang.get(i);
				String s = ConversionUtils.convert(r, String.class);
				if (r instanceof Number) {
					sb.append(s);
				} else if (r instanceof List) {
					s = ConversionUtils.convert(processor.run((List<?>) r),
							String.class);
					sb.append(s);
				} else {
					if ("$".equals(r)) {// add by yangl: in 结果集为表达式
						return sb
								.append(ConversionUtils.convert(rang.get(1),
										String.class)).append(")").toString();
					}
					sb.append("'").append(s).append("'");
				}
			}
			return sb.append(")").toString();
		} catch (Exception e) {
			throw new ExpException(e.getMessage());
		}
	}

}
