package ctd.mvc.controller.support;

import ctd.account.UserRoleToken;
import ctd.monitor.ServiceInfoCollector;
import ctd.mvc.controller.util.UserRoleTokenUtils;
import ctd.net.rpc.util.ServiceAdapter;
import ctd.security.exception.SecurityException;
import ctd.spring.AppDomainContext;
import ctd.util.JSONProtocol;
import ctd.util.JSONUtils;
import ctd.util.ServletUtils;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;
import ctd.util.exception.CodedBaseException;
import ctd.util.exp.ExpressionProcessor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class SecurityJSONRequester extends JSONRequester {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityJSONRequester.class);
    private static final String SERVICE_ID_KEY = "serviceId";
    private static final String METHOD_KEY = "method";
    private static final String ACTION_ID_KEY = "actionId";
    private boolean banXss = false;

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/**/*.jsonRequest", method = RequestMethod.POST, headers = "content-type=application/json")
    public void doJSONRequest(HttpServletRequest request, HttpServletResponse response) {
        HashMap<String, Object> resData = new HashMap<String, Object>();
        Date startDt = new Date();
        String beanName = null;
        Map<String, Object> serviceLog = null;
        try {
            UserRoleToken token = UserRoleTokenUtils.getUserRoleToken(request);
            ContextUtils.put(Context.USER_ROLE_TOKEN, token);
            ContextUtils.put(Context.USER, token);
            ContextUtils.put(Context.HTTP_REQUEST, request);
            HashMap<String, Object> reqData = JSONUtils.parse(request.getInputStream(), HashMap.class);
            if (banXss) {
                Map<String, Object> temp = new HashMap<String, Object>();
                for (Iterator<Map.Entry<String, Object>> it = reqData.entrySet().iterator(); it.hasNext(); ) {
                    Map.Entry<String, Object> entry = it.next();
                    try {
                        Object o = entry.getValue();
                        if (o instanceof String) {
                            ExpressionProcessor.instance().toString((String) o);
                        }
                        if (o instanceof List) {
                            ExpressionProcessor.instance().toString((List) o);
                        }
                    } catch (Exception e) {
                    	temp.put(entry.getKey(), entry.getValue());
                        it.remove();
                    }
                }
                reqData = JSONUtils.parse(cleanXSS(JSONUtils.toString(reqData)), HashMap.class);
                reqData.putAll(temp);
            }
            String actionId = (String) reqData.get(ACTION_ID_KEY);
            if (isAccessableAction(token.getRoleId(), actionId)) {
                beanName = (String) reqData.get(SERVICE_ID_KEY);
                String method = (String) reqData.get(METHOD_KEY);
                Object reqBody = reqData.get(JSONProtocol.BODY);
                List<Object> parametersList = null;
                if (reqBody != null) {
                    if (reqBody instanceof List) {
                        parametersList = (List<Object>) reqBody;
                    } else {
                        parametersList = new ArrayList<Object>();
                        parametersList.add(reqBody);
                    }
                }
                serviceLog = new HashMap<String, Object>();
                serviceLog.put("serviceId", beanName);
                serviceLog.put("method", method);
                serviceLog.put("fromDomain", AppDomainContext.getName());
                serviceLog.put("parameters", "execute".equals(method) ? reqData : parametersList);
                serviceLog.put("userId", token.getUserId());
                serviceLog.put("roleId", token.getRoleId());
                serviceLog.put("manageUnitId", token.getManageUnitId());
                serviceLog.put("domain", token.getDomain());
                serviceLog.put("ipAddress", ServletUtils.getIpAddress(request));
                Object result = ServiceAdapter.invokeWithUnconvertedParameters(beanName, method, parametersList, reqData, resData);
                if (!resData.containsKey(JSONProtocol.CODE)) {
                    resData.put(JSONProtocol.CODE, 200);
                }
                if (!resData.containsKey(JSONProtocol.BODY)) {
                    resData.put(JSONProtocol.BODY, result);
                }
            } else {
                resData.put(JSONProtocol.CODE, 403);
            }
        } catch (SecurityException e) {
//			logger.error(e.getMessage());
            resData.put(JSONProtocol.CODE, SecurityException.NOT_LOGON);
            resData.put(JSONProtocol.MSG, "NotLogon");
        } catch (CodedBaseException e) {
            Throwable t = e.getCause();
            if (t instanceof CodedBaseException) {
                resData.put(JSONProtocol.CODE, ((CodedBaseException) t).getCode());
                resData.put(JSONProtocol.MSG, t.getMessage());
                LOGGER.error(t.getMessage(), t);
            } else {
                resData.put(JSONProtocol.CODE, e.getCode());
                resData.put(JSONProtocol.MSG, e.getMessage());
                LOGGER.error(e.getMessage(), e);
            }
        } catch (Exception e) {
            Throwable t = e.getCause();
            if (t instanceof CodedBaseException) {
                resData.put(JSONProtocol.CODE, ((CodedBaseException) t).getCode());
                resData.put(JSONProtocol.MSG, t.getMessage());
                LOGGER.error(t.getMessage(), t);
            } else {
                LOGGER.error(e.getMessage(), e);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } finally {
            if (!StringUtils.isEmpty(beanName)) {
                ServiceInfoCollector.instance().add(beanName, System.currentTimeMillis() - startDt.getTime(), startDt);
            }
            if (serviceLog != null) {
                serviceLog.put("resCode", resData.get(JSONProtocol.CODE));
                serviceLog.put("resMsg", resData.get(JSONProtocol.MSG));
                serviceLog.put("costTime", System.currentTimeMillis() - startDt.getTime());
                LOGGER.info(JSONUtils.toString(serviceLog));
            }
        }

        try {
            boolean gzip = ServletUtils.isAcceptGzip(request);
            jsonOutput(response, resData, gzip);
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            LOGGER.error(e.getMessage());
        } finally {
            ContextUtils.clear();
        }
    }

    /**
     * need update
     *
     * @param principal
     * @param actionId
     * @return
     */
    private boolean isAccessableAction(String principal, String actionId) {
//		String[] paths = actionId.split("/");
//		ResourceNode node = Repository.getNode(paths);
//		return node.lookupPermission(principal).getMode().isAccessible();
        return true;
    }

    private String cleanXSS(String value) {
    	LOGGER.info("before parse:"+value);
        value = value.replaceAll("<", "& lt;").replaceAll(">", "& gt;");
        value = value.replaceAll("\\(", "& #40;").replaceAll("\\)", "& #41;");
        value = value.replaceAll("'", "& #39;");
        value = value.replaceAll("eval\\((.*)\\)", "");
        value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
        value = value.replaceAll("script", "");
        LOGGER.info("after parse:"+value);
        return value;
    }

    public void setBanXss(boolean banXss) {
        this.banXss = banXss;
    }
}
