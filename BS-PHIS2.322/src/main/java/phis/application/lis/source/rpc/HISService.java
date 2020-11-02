package phis.application.lis.source.rpc;

import java.util.Map;
import ctd.util.context.Context;

public abstract interface HISService {
	public abstract Map<String, Object> process(Map<String, Object> paramMap,
			Context paramContext) throws HISException;  
}
