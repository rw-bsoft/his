/**
 * @(#)GetDiseaseRecordCode.java 创建于 2011-6-16 上午10:13:09
 * 
 * 版权：版本所有 bsoft.com.cn 保留所有权力。
 * 
 */
package chis.source.ws;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import chis.source.Constants;
import chis.source.util.Base64;
import ctd.service.core.ServiceException;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class GetDiseaseRecordCodeService extends AbstractWsService {

	private static final Log logger = LogFactory
			.getLog(GetDiseaseRecordCodeService.class);

	private String rootAddr = null;
	private static final String createHypertensionRecord = "C_01";
	private static final String createDiabetesRecord = "D_01";
	private static final String CODE_LIST = "codeList";

	private HashMap<String, ActionEntity> actionMap = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bsoft.hzehr.ws.Service#execute(java.lang.String)
	 */
	@WebMethod
	public String execute(String request) {
		Object[] result = preExecute(request);
		Document resDoc = (Document) result[1];
		if ((Integer) result[0] == 1) {
			return resDoc.asXML();
		}
		Document reqDoc = (Document) result[2];
		Element resRoot = resDoc.getRootElement();
		Element reqRoot = reqDoc.getRootElement();
		Element codeEle = resRoot.element("code");
		Element msgEle = resRoot.element("msg");

		try {
			String host = reqRoot.elementText("host");
			String port = reqRoot.elementText("port");
			rootAddr = getRootAddr(host,port);
		} catch (UnknownHostException e) {
			logger.error("Fetch host address failed.", e);
			codeEle.setText(String.valueOf(Constants.CODE_UNKNOWN_ERROR));
			msgEle.setText("获取主机地址失败。");
			return resDoc.asXML();
		}

		String user = reqRoot.elementText("user");
		String password = reqRoot.elementText("password");
		String role = reqRoot.elementText("roleId");
		RequestArgs args = new RequestArgs();
		args.setUser(user);
		args.setPassword(password);
		args.setRole(role);

		Element codeListEle = handleRequest(reqRoot, resRoot, args);

		codeEle.setText(String.valueOf(Constants.CODE_OK));
		msgEle.setText("Service succeeded.");
		logger.info(new StringBuffer("Send response [").append(resDoc.asXML())
				.append("] to client [")
				.append(reqRoot.attributeValue("reqClient")).append("]."));
		resRoot.add(codeListEle);
		return resRoot.asXML();
	}

	/**
	 * @param reqRoot
	 * @param resRoot
	 * @param args
	 * @return
	 * @throws ServiceException
	 */
	private Element handleRequest(Element reqRoot, Element resRoot,
			RequestArgs args) {
		Element codeListEle = DocumentHelper.createElement("diseaseRecordList");
		codeListEle.add(makeDiseaseRecordCodeElement(createHypertensionRecord,
				args));
		codeListEle
				.add(makeDiseaseRecordCodeElement(createDiabetesRecord, args));
		return codeListEle;
	}

	/**
	 * @param rArgs
	 * @param actionType
	 * @return
	 */
	private Element makeDiseaseRecordCodeElement(String diseaseType,
			RequestArgs rArgs) {
		String args = Base64.encode(new StringBuffer("replace=true&uid=")
				.append(rArgs.getUser()).append("&pass=")
				.append(rArgs.getPassword()).append("&role=")
				.append(rArgs.getRole()).append("&cls=")
				.append(actionMap.get(diseaseType).getCls())
				.append("&closeNav=true&initModules=['").append(diseaseType)
				.append("']&empiId=").append(rArgs.getEmpiId()).toString()
				.getBytes());
		String url = new StringBuffer(rootAddr).append(args).toString();
		return createCodeElement(diseaseType, url);
	}

	/**
	 * 创建一个表示任务的节点元素。
	 * 
	 * @param actionId
	 * @param actionName
	 * @param actionType
	 * @param mustDo
	 * @param url
	 * @return
	 */
	private Element createCodeElement(String diseaseType, String url) {
		Element ele = DocumentHelper.createElement("diseaseRecord");
		Element actionCodeEle = DocumentHelper.createElement("recordCode");
		Element actionNameEle = DocumentHelper.createElement("recordName");
		Element urlEle = DocumentHelper.createElement("url");
		Element codesEle = DocumentHelper.createElement("codes");

		actionCodeEle.setText(diseaseType);
		actionNameEle.setText(actionMap.get(diseaseType).getActionName());
		urlEle.setText(url);
		@SuppressWarnings("unchecked")
		List<String> codes = (List<String>) actionMap.get(diseaseType)
				.getProperty(CODE_LIST);
		for (String code : codes) {
			Element codeEle = DocumentHelper.createElement("code");
			codeEle.setText(code);
			codesEle.add(codeEle);
		}
		ele.add(actionCodeEle);
		ele.add(actionNameEle);
		ele.add(urlEle);
		ele.add(codesEle);
		return ele;
	}

	/**
	 * @return the actionMap
	 */
	public HashMap<String, ActionEntity> getActionMap() {
		return actionMap;
	}

	/**
	 * @param actionMap
	 *            the actionMap to set
	 */
	public void setActionMap(HashMap<String, ActionEntity> actionMap) {
		this.actionMap = actionMap;
	}

}
