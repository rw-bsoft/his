package ctd.mvc.controller.support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ctd.account.UserRoleToken;
import ctd.monitor.ServiceInfoCollector;
import ctd.mvc.controller.JSONOutputMVCConroller;
import ctd.mvc.controller.util.UserRoleTokenUtils;
import ctd.net.rpc.util.ServiceAdapter;
import ctd.security.exception.SecurityException;
import ctd.util.JSONProtocol;
import ctd.util.JSONUtils;
import ctd.util.ServletUtils;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;
import ctd.util.exception.CodedBaseException;

@Controller("mvcJSONRequester")
public class JSONRequester  extends JSONOutputMVCConroller{
	private static final Logger logger = LoggerFactory.getLogger(JSONRequester.class);
	private static final String SERVICE_ID_KEY = "serviceId";
	private static final String METHOD_KEY = "method";
	private static final String ACTION_ID_KEY = "actionId";
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/**/*.jsonRequest",method=RequestMethod.POST,headers="content-type=application/json")
	public void doJSONRequest(HttpServletRequest request,HttpServletResponse response){
		HashMap<String,Object> resData = new HashMap<String,Object>();
		Date startDt = new Date();
		String actionId = null;
		String beanName = null;
		String method = null;
		HashMap<String,Object> reqData = null;
		String ip = request.getRemoteAddr();
		try {
			reqData = JSONUtils.parse(request.getInputStream(), HashMap.class);
			UserRoleToken token = UserRoleTokenUtils.getUserRoleToken(request);
			ContextUtils.put(Context.USER_ROLE_TOKEN, token);
			ContextUtils.put(Context.USER, token);
			ContextUtils.put(Context.HTTP_REQUEST, request);
			
			actionId = (String)reqData.get(ACTION_ID_KEY);
			if(isAccessableAction(token.getRoleId(),actionId)){
				beanName = (String)reqData.get(SERVICE_ID_KEY);
				method = (String)reqData.get(METHOD_KEY);
				Object reqBody = reqData.get(JSONProtocol.BODY);
				List<Object> parametersList = null;
				if(reqBody != null){
					if(reqBody instanceof List){
						parametersList = (List<Object>) reqBody;
					}else{
						parametersList = new ArrayList<Object>();
						parametersList.add(reqBody);
					}
				}
				Object result = ServiceAdapter.invokeWithUnconvertedParameters(beanName, method, parametersList, reqData, resData);
				if(!resData.containsKey(JSONProtocol.CODE)){
					resData.put(JSONProtocol.CODE, 200);
				}
				if(!resData.containsKey(JSONProtocol.BODY)){
					resData.put(JSONProtocol.BODY, result);
				}
			}
			else{
				resData.put(JSONProtocol.CODE,403);
			}
		}
		catch (SecurityException e) {
			//logger.error(e.getMessage());
			//logger.error(ip);
			//logger.error("【"+ip+"】"+JSONUtils.toString(reqData));
			resData.put(JSONProtocol.CODE, SecurityException.NOT_LOGON);
			resData.put(JSONProtocol.MSG, "NotLogon");
		} 
		catch (CodedBaseException e) {
			Throwable t = e.getCause();
			if(t instanceof CodedBaseException){
				resData.put(JSONProtocol.CODE,((CodedBaseException)t).getCode());
				resData.put(JSONProtocol.MSG,t.getMessage());
				logger.error(t.getMessage(),t);
			}
			else{
				resData.put(JSONProtocol.CODE,e.getCode());
				resData.put(JSONProtocol.MSG,e.getMessage());
				logger.error(e.getMessage(),e);
			}
		}
		catch (Exception e) {
			Throwable t = e.getCause();
			if(t instanceof CodedBaseException){
				resData.put(JSONProtocol.CODE,((CodedBaseException)t).getCode());
				resData.put(JSONProtocol.MSG,t.getMessage());
				logger.error(t.getMessage(),t);
			}
			else{
				logger.error(e.getMessage(),e);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		}
		finally{
			if(!StringUtils.isEmpty(beanName)){
				Date endDt = new Date();
				long timeCost = endDt.getTime() - startDt.getTime();
				ServiceInfoCollector.instance().add(beanName, timeCost, startDt);
			}
		}
		
		try{
			boolean gzip = ServletUtils.isAcceptGzip(request);
			jsonOutput(response,resData,gzip);
		} 
		catch (IOException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			logger.error(e.getMessage());
		}
		finally{
			ContextUtils.clear();
		}
	}
	
	/**
	 * need update
	 * @param principal
	 * @param actionId
	 * @return
	 */
	private boolean isAccessableAction(String principal,String actionId){
//		String[] paths = actionId.split("/");
//		ResourceNode node = Repository.getNode(paths);
//		return node.lookupPermission(principal).getMode().isAccessible();
		return true;
	}
}
