/**
 * @(#)TestWsService.java Created on 2013-9-27 下午12:07:30
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.ws;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/**
 * @description
 * 
 * @author <a href="mailto:yaozh@bsoft.com.cn">yaozh</a>
 */
@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class TestWsService extends AbstractWsService {

	/* (non-Javadoc)
	 * @see chis.source.ws.Service#execute(java.lang.String)
	 */
	@Override
	@WebMethod
	public String execute(String request) {
		return "200";
	}

}
