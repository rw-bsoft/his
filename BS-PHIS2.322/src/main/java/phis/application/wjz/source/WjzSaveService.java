package phis.application.wjz.source;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import phis.source.ws.AbstractWsService;
@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class WjzSaveService  extends AbstractWsService{
	private static final Log logger = LogFactory
			.getLog(WjzSaveService.class);
	@Override
	@WebMethod
	public String execute(String request) {
		
		return null;
	}

}
