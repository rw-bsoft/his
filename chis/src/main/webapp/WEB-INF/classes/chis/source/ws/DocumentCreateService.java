package chis.source.ws;

import java.net.UnknownHostException;
import java.util.HashMap;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hibernate.Session;

import chis.source.BSCHISEntryNames;
import chis.source.Constants;
import chis.source.PersistentDataOperationException;
import chis.source.pub.PublicService;
import chis.source.util.Base64;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class DocumentCreateService extends AbstractWsService {
	private static final Log logger = LogFactory
			.getLog(GetWorkListService.class);
	
	static final String ACTION_TYPE_EHR_QUERY = "0";
	static final String ACTION_TYPE_CREATE_DOC = "1";
	// private static final String ACTION_TYPE_QUERY_DOC = "2";
	static final String ACTION_TYPE_BUSINESS_WORK = "3";
	
	static final String MANDATORY = "1";
	static final String OPTIONAL = "2";
	
	public static String TASK_IF_HEALTHDOC_CREATE = "healthRecord";
	public static String TASK_IF_HYPERTENSION_CREATE = "hypertensionRecord";
	public static String TASK_IF_HYPERTENSION_VISIT = "hypertensionVisit";
	public static String TASK_IF_MDC_DIABETES_CREATE = "diabetesRecord";
	public static String TASK_IF_MDC_DIABETES_VISIT = "diabetesVisit";

	protected String healthRecord = "B_01";
	protected String createHypertensionRecord = "C_01";
	protected String createDiabetesRecord = "D_01";
	
	
	protected String rootAddr = null;

	protected HashMap<String, ActionEntity> actionMap = null;

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
		String taskName = reqRoot.elementText("taskName");
		String empiId = reqRoot.elementText("empiId");
		if (empiId == null || empiId.length() == 0) {
			codeEle.setText("500");
			msgEle.setText("empiId is null.");
		}

		Session session = getSessionFactory().openSession();
		String user = reqRoot.elementText("user");
		String password = reqRoot.elementText("password");
		String role = reqRoot.elementText("roleId");
		RequestArgs args = new RequestArgs();
		args.setEmpiId(empiId);
		args.setUser(user);
		args.setPassword(password);
		args.setRole(role);
		try {
			if (TASK_IF_HEALTHDOC_CREATE.equals(taskName)) {
				this.healthRecord = "B_01";
				if (PublicService.isRecordExists(
						BSCHISEntryNames.EHR_HealthRecord, empiId, true,
						session)) {
					this.healthRecord = "B_02";
				}
				Element e = makeCreateHealthRecordTaskElement(args,
						ACTION_TYPE_CREATE_DOC);
				resRoot.add(e);
			} else if (TASK_IF_HYPERTENSION_CREATE.equals(taskName)) {
				this.createHypertensionRecord = "C_01";
				if (PublicService.isRecordExists(
						BSCHISEntryNames.MDC_HypertensionRecord, empiId, true,
						session)) {
					this.createHypertensionRecord = "C_02";
				}
				Element e = makeCreateHypertensionRecordTaskElement(args,
						ACTION_TYPE_CREATE_DOC);
				resRoot.add(e);
			} else if (TASK_IF_MDC_DIABETES_CREATE.equals(taskName)) {
				this.createDiabetesRecord = "D_01";
				if (PublicService.isRecordExists(
						BSCHISEntryNames.MDC_DiabetesRecord, empiId, true,
						session)) {
					this.createDiabetesRecord = "D_02";
				}
				Element e = makeCreateDiabetesRecordTaskElement(args,
						ACTION_TYPE_BUSINESS_WORK);
				resRoot.add(e);
			}
		} catch (PersistentDataOperationException e) {
			codeEle.setText("500");
			msgEle.setText("档案是否存在查询失败.");
			return resRoot.asXML();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
		codeEle.setText("200");
		msgEle.setText("succeed.");
		return resRoot.asXML();
	}

	/**
	 * 判断健康档案是否建立 如果已建，返回查看url，如果未建，返回建档url
	 * 
	 * @param reqRoot
	 * @return
	 * @throws PersistentDataOperationException
	 */
	@WebMethod(exclude = true)
	public String ifHealthRecordCreate(Element reqRoot, Session session)
			throws PersistentDataOperationException {
		String empiId = reqRoot.elementText("empiId");
		if (false == PublicService
				.isPersonalHealthRecordExists(empiId, session)) {
		}
		return null;
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
	@WebMethod(exclude = true)
	private Element createTaskElement(String actionId, String actionName,
			String actionType, String mustDo, String url) {
		Element ele = DocumentHelper.createElement("task");
		Element actionCodeEle = DocumentHelper.createElement("actionCode");
		Element actionNameEle = DocumentHelper.createElement("actionName");
		Element actionTypeEle = DocumentHelper.createElement("actionType");
		Element mustDoEle = DocumentHelper.createElement("mustDo");
		Element urlEle = DocumentHelper.createElement("url");

		actionCodeEle.setText(actionId);
		actionNameEle.setText(actionName);
		actionTypeEle.setText(actionType);
		mustDoEle.setText(mustDo);
		urlEle.setText(url);

		ele.add(actionCodeEle);
		ele.add(actionNameEle);
		ele.add(actionTypeEle);
		ele.add(mustDoEle);
		ele.add(urlEle);

		return ele;
	}

	/**
	 * @param empiId
	 * @param actionType
	 * @param user
	 * @param password
	 * @return
	 */
	@WebMethod(exclude = true)
	private Element makeCreateHealthRecordTaskElement(RequestArgs rArgs,
			String actionType) {
		String args = Base64.encode(new StringBuffer("replace=true&uid=")
				.append(rArgs.getUser()).append("&pass=")
				.append(rArgs.getPassword()).append("&role=")
				.append(rArgs.getRole()).append("&cls=")
				.append(actionMap.get(healthRecord).getCls())
				.append("&closeNav=true&initModules=['")
				.append(actionMap.get(healthRecord).getActionId())
				.append("','B_02','B_03','B_04']&empiId=")
				.append(rArgs.getEmpiId()).toString().getBytes());
		String mustDo = OPTIONAL;
		if (actionType.equals(ACTION_TYPE_CREATE_DOC)) {
			mustDo = MANDATORY;
		}
		String url = new StringBuffer(rootAddr).append(args).toString();
		return createTaskElement(actionMap.get(healthRecord).getActionId(),
				actionMap.get(healthRecord).getActionName(), actionType,
				mustDo, url);
	}
	
	/**
	 * @param rArgs
	 * @param actionType
	 * @return
	 */
	@WebMethod(exclude = true)
	private Element makeCreateHypertensionRecordTaskElement(
			RequestArgs rArgs, String actionType) {
		String args = Base64.encode(new StringBuffer("replace=true&uid=")
				.append(rArgs.getUser()).append("&pass=")
				.append(rArgs.getPassword()).append("&role=")
				.append(rArgs.getRole()).append("&cls=")
				.append(actionMap.get(createHypertensionRecord).getCls())
				.append("&closeNav=true&initModules=['")
				.append(actionMap.get(createHypertensionRecord).getActionId())
				.append("','C_02','C_03','C_05','C_04']&empiId=")
				.append(rArgs.getEmpiId()).toString().getBytes());
		String mustDo = OPTIONAL;
		if (actionType.equals(ACTION_TYPE_CREATE_DOC)) {
			mustDo = MANDATORY;
		}
		String url = new StringBuffer(rootAddr).append(args).toString();
		return createTaskElement(actionMap.get(createHypertensionRecord)
				.getActionId(), actionMap.get(createHypertensionRecord)
				.getActionName(), actionType, mustDo, url);
	}
	
	/**
	 * @param rArgs
	 * @param actionType
	 * @return
	 */
	@WebMethod(exclude = true)
	private Element makeCreateDiabetesRecordTaskElement(RequestArgs rArgs,
			String actionType) {
		String args = Base64.encode(new StringBuffer("replace=true&uid=")
				.append(rArgs.getUser()).append("&pass=")
				.append(rArgs.getPassword()).append("&role=")
				.append(rArgs.getRole()).append("&cls=")
				.append(actionMap.get(createDiabetesRecord).getCls())
				.append("&closeNav=true&initModules=['")
				.append(actionMap.get(createDiabetesRecord).getActionId())
				.append("','D_02', 'D_03', 'D_05', 'D_04']&empiId=")
				.append(rArgs.getEmpiId()).toString().getBytes());
		String mustDo = OPTIONAL;
		if (actionType.equals(ACTION_TYPE_CREATE_DOC)) {
			mustDo = MANDATORY;
		}
		String url = new StringBuffer(rootAddr).append(args).toString();
		return createTaskElement(actionMap.get(createDiabetesRecord)
				.getActionId(), actionMap.get(createDiabetesRecord)
				.getActionName(), actionType, mustDo, url);
	}
	
	public HashMap<String, ActionEntity> getActionMap() {
		return actionMap;
	}

	public void setActionMap(HashMap<String, ActionEntity> actionMap) {
		this.actionMap = actionMap;
	}
}
