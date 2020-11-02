package ctd.print;

import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JasperPrint;

import ctd.util.context.Context;

public abstract class DynamicPrint implements IHandler {
	
	/**
	 * 实现自定义报表输出
	 * @param request
	 * @param response
	 * @param ctx
	 * @throws PrintException
	 */
	public abstract List<JasperPrint> doPrint(Map<String, Object> request,
			Map<String, Object> response) throws PrintException;

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		// TODO Auto-generated method stub

	}

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		// TODO Auto-generated method stub

	}

}
