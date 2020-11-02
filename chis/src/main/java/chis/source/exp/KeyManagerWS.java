/**
 * @(#)KeyManagerWS.java Created on Oct 19, 2010 22:10:28 PM
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.exp;

import java.io.IOException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class KeyManagerWS {

	private static String endPointReference = "";
	
	private static String targetNamespace = "";
	
	/**
	 * @return the endPointReference
	 */
	public static String getEndPointReference() {
		return endPointReference;
	}

	/**
	 * @param endPointReference the endPointReference to set
	 */
	public static void setEndPointReference(String endPointReference) {
		KeyManagerWS.endPointReference = endPointReference;
	}

	/**
	 * @return the targetNamespace
	 */
	public static String getTargetNamespace() {
		return targetNamespace;
	}

	/**
	 * @param targetNamespace the targetNamespace to set
	 */
	public static void setTargetNamespace(String targetNamespace) {
		KeyManagerWS.targetNamespace = targetNamespace;
	}

	/**
	 * 通过入口名称得到Key的ID号
	 * 
	 * @param entryName
	 * @return
	 */
	public static String getKeyByName(String entryName) {
		return getKeyByName(entryName, null);
	}

	public static String getKeyByName(String entryName, Context c) {
		HttpClient client = new HttpClient();
		PostMethod method = new PostMethod(endPointReference);
		if(c!=null && c.containsKey("$docManaUnitId")){
			String manaUnitId = c.get("$docManaUnitId").toString();
			NameValuePair nv1 = new NameValuePair("$docManaUnitId", manaUnitId);
			NameValuePair nv2 = new NameValuePair("entryName", entryName);
			method.setRequestBody(new NameValuePair[]{nv1, nv2});
		}else {
			NameValuePair nv2 = new NameValuePair("entryName", entryName);
			method.setRequestBody(new NameValuePair[]{ nv2});
		}
		
		try {
			client.executeMethod(method);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		int code = method.getStatusCode();
		if (code != 200) {
			return null;
		}
		String key;
		try {
			key = method.getResponseBodyAsString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		method.releaseConnection();
		return key;
	}

	/**
	 * 通过对象得到Key值
	 * 
	 * @param obj
	 * @return
	 */
	public static String getKey(Object obj) {
		return null;
	}

	public static String getKey(Object obj, Object par) {
		return null;
	}
}
