package phis.application.lis.source.rpc;

import java.util.Map;
/**
 * @description
 * @author <a href="mailto:chengzx@bsoft.com.cn">chzhxiang</a>
 */
public abstract interface IPHISService {
	 public abstract Map<String, Object> execute(Map<String, Object> paramMap)
	    throws PHISServiceException;
}
