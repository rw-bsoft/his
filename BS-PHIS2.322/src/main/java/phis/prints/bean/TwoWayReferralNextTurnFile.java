package phis.prints.bean;

import java.util.List;
import java.util.Map;

import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class TwoWayReferralNextTurnFile implements IHandler {
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {

	}

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {

	}

	public long parseLong(Object o) {
		if (o == null || "null".equals(o)) {
			return new Long(0);
		}
		return Long.parseLong(o + "");
	}

	public int parseInt(Object o) {
		if (o == null || "null".equals(o)) {
			return new Integer(0);
		}
		return Integer.parseInt(o + "");
	}
}
