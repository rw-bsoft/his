package ctd.util.exp.standard;

import java.util.HashMap;
import java.util.List;

import ctd.util.exp.Expression;
import ctd.util.exp.ExpressionProcessor;
import ctd.util.exp.ExpException;
import ctd.util.context.ContextUtils;

public class REF extends Expression {

	public REF() {
		symbol = "$";
		name = symbol;
	}

	@Override
	public Object run(List<?> ls, ExpressionProcessor processor)
			throws ExpException {
		try {
			String nm = (String) ls.get(1);
			if (nm.startsWith("%")) {
				nm = nm.substring(1);
			}
			Object v = ContextUtils.get(nm);
			// add by yangl 增加指定类型功能
			if (ls.size() > 2) {
				v = processor.run("['" + ls.get(2) + "'," + v + "]");
			}
			return v;
		} catch (Exception e) {
			throw new ExpException(e.getMessage());
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public String toString(List<?> ls, ExpressionProcessor processor)
			throws ExpException {
		try {
			String nm = (String) ls.get(1);
			if (!nm.startsWith("%")) {
				return nm;
			}
			Boolean forPreparedStatement = ContextUtils.get(
					"$exp.forPreparedStatement", Boolean.class);
			Object o = run(ls, processor);

			if (forPreparedStatement != null && forPreparedStatement == true) {
				nm = nm.substring(1);

				HashMap<String, Object> parameters = ContextUtils.get(
						"$exp.statementParameters", HashMap.class);
				parameters.put(nm, o);
				return ":" + nm;
			} else {
				if (o instanceof Number) {
					return String.valueOf(o);
				} else {
					return "'" + String.valueOf(o) + "'";
				}
			}
		} catch (Exception e) {
			throw new ExpException(e);
		}
	}

}
