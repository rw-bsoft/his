/**
 * @(#)CHISProxyService.java 创建于 2011-6-17 上午11:34:23
 * 
 * 版权：版本所有 bsoft.com.cn 保留所有权力。
 * 
 */
package chis.source.ws;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import chis.source.Constants;
import chis.source.mobile.JSONHelper;
import chis.source.mobile.MobileUtils;
import chis.source.security.DESCoder;
import chis.source.security.EncryptException;
import chis.source.security.EncryptUtil;
import chis.source.security.RSA;

import com.alibaba.fastjson.JSONException;

import ctd.account.AccountCenter;
import ctd.account.UserRoleToken;
import ctd.account.user.User;
import ctd.controller.exception.ControllerException;
import ctd.service.core.ServiceException;
import ctd.service.core.ServiceExecutor;
import ctd.util.MD5StringUtil;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class ChisWSProxyService extends AbstractWsService {

	private static final Log logger = LogFactory
			.getLog(ChisWSProxyService.class);

	private String desKey = "BSmEHR";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bsoft.hzehr.ws.Service#execute(java.lang.String)
	 */
	@SuppressWarnings({ "unchecked" })
	@WebMethod
	public String execute(String request) {
		logger.info("Received request data[" + request + "].");
		HashMap<String, Object> jsonRes = new HashMap<String, Object>();
		String serviceId = null;
		try {
			jsonRes.put(RES_CODE, Constants.CODE_OK);
			String requestData = request;
			if (!request.contains("serviceId")) {
				requestData = decodeRequest(request);
				logger.info("decodeRequest  data[" + requestData + "].");
			}
			Map<String, Object> jsonReq = new HashMap<String, Object>();
			JSONObject jsonObject = new JSONObject(requestData);
			jsonReq = JSONHelper.jsonObject2Map(jsonObject, jsonReq);
			serviceId = (String) jsonReq.get("serviceId");
			jsonRes.put("serviceId", serviceId);
			if (!serviceId.equals("chis.logonService")
					&& !serviceId.equals("chis.getRoleListService")
					&& !serviceId.equals("chis.getPublicKeyService")) {
				HashMap<String, Object> res = preExecute(jsonReq);
				if (((Integer) res.get(RES_CODE)).intValue() != 200) {
					return res.toString();
				}
			}
			if (jsonReq.containsKey("body")
					&& jsonReq.get("body") instanceof Map) {
				Map<String, Object> body = (Map<String, Object>) jsonReq
						.get("body");
				body = MobileUtils.isNullForJSONObject(body);
				jsonReq.put("body", body);
			}
			jsonReq = MobileUtils.isNullForJSONObject(jsonReq);
			ServiceExecutor.execute(serviceId, jsonReq, jsonRes,
					ContextUtils.getContext());
		} catch (Exception e) {
			logger.error("Failed to operate on json object.", e);
			try {
				jsonRes.put(RES_CODE, Constants.CODE_INVALID_REQUEST);
				jsonRes.put(RES_MESSAGE, "Failed to operate on json object.");
			} catch (Exception e1) {
				// @@ ;
			}
		} finally {
			ContextUtils.clear();
		}
		filterResponse(jsonRes);
		logger.info("Send response[" + jsonRes.toString() + "].");
		if ("chis.getPublicKeyService".equals(serviceId)) {
			JSONObject jsonResPKS = new JSONObject();
			try {
				jsonResPKS = JSONHelper.map2JSONObject(jsonRes);
			} catch (org.json.JSONException e) {
				logger.error(
						"Failed to encoded getPublicKeyService response data.",
						e);
			}
			return jsonResPKS.toString();
		} else {
			int index = request.indexOf("_");
			try {
				return serviceId.equals("chis.mEhrPhotoService") ? JSONHelper
						.map2JSONObject(jsonRes).toString() : encodeResponse(
						JSONHelper.map2JSONObject(jsonRes).toString(),
						request.substring(index + 1));
			} catch (Exception e) {
				logger.error("Failed to encoded response data.", e);
				try {
					jsonRes.put(RES_CODE, Constants.CODE_ENCEYPT_ERROR);
					jsonRes.put(RES_MESSAGE, "Failed to get encrypt key.");
				} catch (Exception e1) {
					// @@ ;
				}
				return null;
			}
		}
	}

	/**
	 * 去除回应中为null的JSON元素。
	 * 
	 * @param json
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private HashMap<String, Object> filterResponse(HashMap<String, Object> json) {
		Object o = json.get("body");
		if (o instanceof HashMap) {
			filterHashMap((HashMap<String, Object>) o);
		} else if (o instanceof ArrayList) {
			filterArrayList((ArrayList) o);
		}
		return json;
	}

	/**
	 * 过滤JSON对象，将对象中null的值去除。
	 * 
	 * @param json
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private HashMap<String, Object> filterHashMap(HashMap<String, Object> json) {
		if (json == null) {
			return null;
		}
		for (Iterator<String> it = json.keySet().iterator(); it.hasNext();) {
			String key = it.next();
			Object o = json.get(key);
			if (o == null) {
				it.remove();
			}
			if (o instanceof HashMap) {
				filterHashMap(((HashMap<String, Object>) o));
			}
			if (o instanceof ArrayList) {
				filterArrayList((ArrayList) o);
			}
		}
		return json;
	}

	/**
	 * 过滤JSON数组，将数组中包含的JSON对象中null的值去除。
	 * 
	 * @param array
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List filterArrayList(List array) {
		if (array == null) {
			return null;
		}
		for (int i = 0; i < ((ArrayList) array).size(); i++) {
			Object obj = ((ArrayList) array).get(i);
			if (obj instanceof HashMap) {
				filterHashMap((HashMap<String, Object>) obj);
			} else if (obj instanceof ArrayList) {
				filterArrayList((ArrayList) obj);
			}
		}
		return array;
	}

	/**
	 * @param json
	 * @return
	 * @throws JSONException
	 */
	protected HashMap<String, Object> preExecute(Map<String, Object> jsonReq) {
		logger.info(new StringBuffer("Received request msg [").append(jsonReq)
				.append("]."));
		HashMap<String, Object> res = new HashMap<String, Object>();
		if (jsonReq == null) {
			logger.error("NULL request received.");
			res.put(RES_CODE, Constants.CODE_INVALID_REQUEST);
			res.put(RES_MESSAGE, "空的请求信息！");
			return res;
		}

		try {
			verifyRequest(jsonReq);
		} catch (ServiceException e) {
			logger.error("Failed to verify request.", e);
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			return res;
		}
		String user = (String) jsonReq.get("uid");
		String password = StringUtils.trimToEmpty((String) jsonReq.get("psw"));
		password = MD5StringUtil.MD5Encode(password);
		String role = (String) jsonReq.get("rid");
		try {
			checkUser(user, password, role);
		} catch (Exception e) {
			String msg = new StringBuffer("Invalid user: ").append(user)
					.append(", password: ").append(password).append(", role: ")
					.append(role).toString();
			logger.error(msg, e);
			res.put(RES_CODE, Constants.CODE_SERVICE_ERROR);
			res.put(RES_MESSAGE, msg);
			return res;
		}
		try {
			putUserRoleToken(jsonReq);
		} catch (ControllerException e) {
			logger.error("ContextUtils failed to  put userRoleToken and user",
					e);
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			return res;
		}
		logger.info("User validation passed.");
		res.put(RES_CODE, 200);
		res.put(RES_MESSAGE, "succeeded.");
		return res;
	}

	/**
	 * 解决 not exist in thread context
	 * 
	 * @param jsonReq
	 * @throws ControllerException
	 */
	private void putUserRoleToken(Map<String, Object> jsonReq)
			throws ControllerException {
		String uid = (String) jsonReq.get("uid");
		String rid = (String) jsonReq.get("rid");
		String urole = (String) jsonReq.get("urole");
		int index = urole.indexOf("@");
		String manaUnitId = urole.substring(0, index);
		User user = AccountCenter.getUser(uid);
		Collection<UserRoleToken> userRoleTokens = user.getUserRoleTokens();
		for (UserRoleToken userRoleToken : userRoleTokens) {
			String roleId = userRoleToken.getRoleId();
			String manageUnitId = userRoleToken.getManageUnitId();
			if (rid.equals(roleId)) {
				ContextUtils.put(Context.USER_ROLE_TOKEN, userRoleToken);
				ContextUtils.put(Context.USER, userRoleToken);
			}
		}
	}

	/**
	 * @param json
	 * @throws ServiceException
	 * @throws JSONException
	 */
	protected void verifyRequest(Map<String, Object> json)
			throws ServiceException {
		String uid = (String) json.get("uid");
		String rid = (String) json.get("rid");
		if (isEmpty(uid)) {
			throw new ServiceException(Constants.CODE_BUSINESS_DATA_NULL,
					"用户名缺失。");
		}
		if (isEmpty(rid)) {
			throw new ServiceException(Constants.CODE_BUSINESS_DATA_NULL,
					"未指定角色。");
		}
	}

	/**
	 * @param response
	 * @return
	 * @throws EncryptException
	 * @throws ServiceException
	 */
	protected String encodeResponse(String response, String clientPubKey)
			throws EncryptException, ServiceException {
		byte[] key = desKey.getBytes();
		RSA rsa = new RSA(clientPubKey);
		byte[] publicEncodedKey = rsa.encrypt(key);
		byte[] encodedResponse;
		try {
			encodedResponse = new DESCoder(key).encrypt(response
					.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new ServiceException(
					"Character encoding[UTF-8] not supported.", e);
		}
		return new StringBuffer(EncryptUtil.byteArr2HexStr(encodedResponse))
				.append(".")
				.append(EncryptUtil.byteArr2HexStr(publicEncodedKey))
				.toString();
	}

	/**
	 * @param request
	 * @return
	 * @throws EncryptException
	 * @throws UnsupportedEncodingException
	 * @throws RsaException
	 */
	protected String decodeRequest(String request) throws EncryptException,
			UnsupportedEncodingException {
		int index = request.indexOf(".");
		int index2 = request.indexOf("_");
		byte kk[] = EncryptUtil.hexStr2ByteArr(request.substring(index + 1,
				index2));
		RSA rsa = new RSA(EncryptUtil.getPublicKey(),
				EncryptUtil.getPrivateKey());
		byte[] key2 = rsa.decrypt(kk);
		return new String(new DESCoder(key2).decrypt(EncryptUtil
				.hexStr2ByteArr(request.substring(0, index))), "UTF-8");
	}

	public String getDesKey() {
		return desKey;
	}

	public void setDesKey(String desKey) {
		this.desKey = desKey;
	}
}
