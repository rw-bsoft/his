/**
 * @(#)GetWorkList.java Created on Oct 29, 2009 10:25:01 AM
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.ws;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
import chis.source.util.ApplicationUtil;
import chis.source.util.Base64;
import ctd.service.core.ServiceException;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class GetWorkListService extends AbstractWsService {

	private static final Log logger = LogFactory
			.getLog(GetWorkListService.class);

	static final String ACTION_TYPE_EHR_QUERY = "0";
	static final String ACTION_TYPE_CREATE_DOC = "1";
	// private static final String ACTION_TYPE_QUERY_DOC = "2";
	static final String ACTION_TYPE_BUSINESS_WORK = "3";

	static final String MANDATORY = "1";
	static final String OPTIONAL = "2";

	protected String healthRecord = "B_01";
	protected String createHypertensionRecord = "C_01";
	protected String createDiabetesRecord = "D_01";
	protected String hypertenFirst = "C_01-01";
	protected String checkHypertensionFirst = "C_01-02";
	protected String needHypertensionVisit = "C_03";
	protected String needHypertensionClinic = "C_03-01";
	protected String diabetesSimilarity = "D_01-01";
	protected String needOldPeopleVisit = "B_06";
	protected String createOldPeopleRecord = "B_07";
	// private static final String childRecord = "H01";
	// private static final String womanRecord = "G01";
	static final String needDiabetesVisit = "D_03";
	// private static final String needTumoutVisit = "F02";
	// private static final String needOldPeopleVisit = "E02";

	protected String rootAddr = null;

	protected HashMap<String, ActionEntity> actionMap = null;

	/**
	 * @see chis.source.ws.Service#execute(java.lang.String)
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

		String empiId = reqRoot.elementText("empiId");
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
		String userUnit = reqRoot.elementText("userunit");
		RequestArgs args = new RequestArgs();
		args.setEmpiId(empiId);
		args.setUser(user);
		args.setUserUint(userUnit);
		args.setPassword(password);
		args.setRole(role);
		// Object obj =ContextUtils.getContext();
		Session session = getSessionFactory().openSession();
		Element taskListEle;
		try {
			taskListEle = handleRequest(reqRoot, resRoot, args, session);
		} catch (ServiceException e) {
			logger.error("Request processing failed.", e);
			codeEle.setText(String.valueOf(e.getCode()));
			msgEle.setText(e.getMessage());
			return resRoot.asXML();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
		codeEle.setText(String.valueOf(Constants.CODE_OK));
		msgEle.setText("Service succeeded.");
		logger.info(new StringBuffer("Send response [").append(resDoc.asXML())
				.append("] to client [")
				.append(reqRoot.attributeValue("reqClient")).append("]."));
		resRoot.add(taskListEle);
		return resRoot.asXML();
	}

	public String isActive(String arg) {
		return "echo:" + arg;
	}

	/**
	 * @see chis.source.ws.AbstractWsService#verifyRequest(org.dom4j.Element)
	 */
	protected boolean verifyRequest(Element reqRoot) throws ServiceException {
		super.verifyRequest(reqRoot);
		if (isEmpty(reqRoot.elementText("empiId"))) {
			throw new ServiceException(Constants.CODE_BUSINESS_DATA_NULL,
					"empiId缺失。");
		}
		return true;
	}

	/**
	 * @param personEle
	 * @param resRoot
	 * @param args
	 * @param session
	 * @throws ServiceException
	 */
	private Element handleRequest(Element reqRoot, Element resRoot,
			RequestArgs args, Session session) throws ServiceException {
		List<String> actionIdLst = new ArrayList<String>();
		String phrId;
		try {
			phrId = PublicService.getPhrId(args.getEmpiId(), session);
		} catch (Exception e) {
			throw new ServiceException(Constants.CODE_DATABASE_ERROR,
					"获取个人健康档案编号失败。", e);
		}
		args.setPhrId(phrId);
		Element taskListEle = DocumentHelper.createElement("taskList");
		// @@ 是否要建健康档案。
		try {
			if (false == PublicService.isPersonalHealthRecordExists(
					args.getEmpiId(), session)) {
				taskListEle.add(makeCreateHealthRecordTaskElement(args,
						ACTION_TYPE_CREATE_DOC));
				return taskListEle;
			} else {
				taskListEle.add(makeCreateHealthRecordTaskElement(args,
						ACTION_TYPE_EHR_QUERY));
				actionIdLst.add(healthRecord);
			}
		} catch (Exception e) {
			throw new ServiceException(Constants.CODE_DATABASE_ERROR,
					"判断是否已建立健康档案失败。", e);
		}
		String manaUnitId;
		try {
			manaUnitId = PublicService.getManaUnit(args.getEmpiId(), session);
		} catch (PersistentDataOperationException e) {
			throw new ServiceException(Constants.CODE_DATABASE_ERROR,
					"取管辖机构失败。", e);
		}
		String manaDoc;
		try {
			manaDoc = PublicService.getManaDoctor(args.getEmpiId(),
					BSCHISEntryNames.EHR_HealthRecord, session);
		} catch (PersistentDataOperationException e) {
			throw new ServiceException(Constants.CODE_DATABASE_ERROR,
					"取责任医生失败。", e);
		}

		// String uid = UserFormat.convertToUidRole(args.getUser(),
		// args.getRole());
		// User user = UsersController.instance().getUser(args.getUser());
		String userUnitId = args.getUserUint();
		// @@ 如果登录角色是责任医生或者团队长或者责任护士执行以下任务。
		String role = args.getRole();
		if (role.equals("01") || role.equals("05") || role.equals("system")
				|| role.equals("02")) {
			// @@ 是责任医生本人.
			if (checkManaDoc(manaDoc, args.getUser())) {
				getManaDocTaskList(reqRoot, taskListEle, actionIdLst, args,
						session);
			}
			// @@ 是本中心的。
			if (checkCenter(manaUnitId, userUnitId)) {
				getCenterTaskList(reqRoot, taskListEle, actionIdLst, args,
						session);
			}
			// @@ 高血压随访，特殊处理，不管是否本中心都有操作。
			try {
				if (PublicService.needHypertensionVisit(args.getEmpiId(),
						session)) {
					if (checkCenter(manaUnitId, userUnitId)) {
						taskListEle.add(makeNeedHypertensionVisitTaskElement(
								args, ACTION_TYPE_BUSINESS_WORK));
						actionIdLst.add(needHypertensionVisit);
					} else {
						taskListEle.add(makeNeedHypertensionClinicTaskElement(
								args, ACTION_TYPE_BUSINESS_WORK));
						actionIdLst.add(needHypertensionClinic);
					}
				}
			} catch (Exception e) {
				throw new ServiceException(Constants.CODE_DATABASE_ERROR,
						"判断是否要做高血压随访失败。", e);
			}
		}

		// @@ icd10相关任务。
		if (reqRoot.element("icd10s") == null) {
			return taskListEle;
		}
		List<String> icdActions;
		try {
			icdActions = getIcd10s(reqRoot.element("icd10s"), session);
		} catch (Exception e) {
			throw new ServiceException(Constants.CODE_DATABASE_ERROR,
					"获取icd10码对应的业务代码失败。", e);
		}
		addIcd10Works(args, icdActions, icdActions, taskListEle);
		return taskListEle;
	}

	/**
	 * 当前操作人是责任医生本人需要做的工作列表。
	 * 
	 * @param reqRoot
	 * @param taskListEle
	 * @param actionIdLst
	 * @param args
	 * @param session
	 * @throws ServiceException
	 */
	private void getManaDocTaskList(Element reqRoot, Element taskListEle,
			List<String> actionIdLst, RequestArgs args, Session session)
			throws ServiceException {
		String empiId = args.getEmpiId();
		int needFirst = 0;
		try {
			needFirst = PublicService.ifHypertensionFirst(empiId, session);
		} catch (Exception e) {
			throw new ServiceException(Constants.CODE_DATABASE_ERROR,
					"判断是否要做首诊测压失败。", e);
		}
		if (needFirst == PublicService.DO_HYPERTENSION_FIRST) {
			taskListEle.add(makeHypertensionFirstTaskElement(args,
					ACTION_TYPE_BUSINESS_WORK));
			actionIdLst.add(hypertenFirst);
		}
		if (needFirst == PublicService.DO_HYPERTENSION_CHECK) {
			taskListEle.add(makeCheckHypertensionFirstTaskElement(args,
					ACTION_TYPE_BUSINESS_WORK));
			actionIdLst.add(checkHypertensionFirst);
		}
		if (needFirst == PublicService.DO_HYPERTENSION_CREATE) {
			taskListEle.add(makeCreateHypertensionRecordTaskElement(args,
					ACTION_TYPE_BUSINESS_WORK));
			actionIdLst.add(createHypertensionRecord);
		}
		Boolean hasDiabetes;
		try {
			hasDiabetes = PublicService.isDiabetesRecordExists(empiId, session);
		} catch (PersistentDataOperationException e) {
			throw new ServiceException(Constants.CODE_DATABASE_ERROR,
					"判断是否已建立糖尿病档案失败。", e);
		}

		if (!hasDiabetes) {
			String strBmi = reqRoot.elementText("bmi");
			if (strBmi != null) {
				float bmi = Float.valueOf(strBmi);
				if (bmi >= 24) {
					Object diagnosisType = null;
					try {
						diagnosisType = PublicService.ifDiabetesSimilarity(
								empiId, session);
					} catch (Exception e) {
						throw new ServiceException(
								Constants.CODE_DATABASE_ERROR, "获取糖尿病疑似信息失败。",
								e);
					}
					if (diagnosisType != null
							&& diagnosisType.toString().equals("1")) {
						taskListEle.add(makeCreateDiabetesRecordTaskElement(
								args, ACTION_TYPE_BUSINESS_WORK));
						actionIdLst.add(createDiabetesRecord);
						// return;
					} else {
						taskListEle.add(makeDiabetesSimilarityTaskElement(args,
								ACTION_TYPE_BUSINESS_WORK));
						actionIdLst.add(diabetesSimilarity);
						// return;
					}
				}
			}
		}
		try {
			String strOpa = ApplicationUtil.getProperty(Constants.UTIL_APP_ID,
					"oldPeopleAge");
			if (strOpa == null || strOpa.trim().length() == 0) {
				return;
			}
			int age = PublicService.getAge(empiId, session);
			if (age >= Integer.parseInt(strOpa)) {
				if (false == PublicService.isRecordExists(
						BSCHISEntryNames.MDC_OldPeopleRecord, empiId, true,
						session)) {
					taskListEle.add(makeCreateOldPeopleRecordTaskElement(args,
							ACTION_TYPE_BUSINESS_WORK));
					actionIdLst.add(createOldPeopleRecord);
					return;
				}
			}
		} catch (Exception e) {
			throw new ServiceException(Constants.CODE_DATABASE_ERROR,
					"判断是否需要建立老年人档案失败。", e);
		}
	}

	/**
	 * 
	 * 
	 * @param reqRoot
	 * @param taskListEle
	 * @param actionIdLst
	 * @param args
	 * @param session
	 * @throws ServiceException
	 */
	private void getCenterTaskList(Element reqRoot, Element taskListEle,
			List<String> actionIdLst, RequestArgs args, Session session)
			throws ServiceException {
		// @@ 糖尿病随访。
		try {
			if (PublicService.needDiabetesVisit(args.getEmpiId(), session)) {
				taskListEle.add(makeNeedDiabetesVisitTaskEelement(args,
						ACTION_TYPE_BUSINESS_WORK));
				actionIdLst.add(needDiabetesVisit);
			}
		} catch (Exception e) {
			throw new ServiceException(Constants.CODE_DATABASE_ERROR,
					"判断是否要做糖尿病随访失败。", e);
		}
		// @@ 老年人随访。
//		try {
//			if (PublicService.needOldPeopleVisit(args.getEmpiId(), session)) {
//				taskListEle.add(makeNeedOldPeopleVisitTaskElement(args,
//						ACTION_TYPE_BUSINESS_WORK));
//				actionIdLst.add(needOldPeopleVisit);
//			}
//		} catch (Exception e) {
//			throw new ServiceException(Constants.CODE_DATABASE_ERROR,
//					"判断是否要做老年人随访失败。", e);
//		}
	}

	/**
	 * @param icdActions
	 * @param actionList
	 * @param taskLstElement
	 */
	private void addIcd10Works(RequestArgs rArgs, List<String> icdActions,
			List<String> actionList, Element taskLstElement) {
		for (Iterator<String> it = icdActions.iterator(); it.hasNext();) {
			String actionId = it.next();
			if (actionList.contains(actionId)) {
				continue;
			}
			ActionEntity ae = actionMap.get(actionId);
			StringBuffer sb = new StringBuffer("replace=true&uid=")
					.append(rArgs.getUser()).append("&pass=")
					.append(rArgs.getPassword()).append("&role=")
					.append(rArgs.getRole()).append("&cls=")
					.append(ae.getCls()).append("&empiId=")
					.append(rArgs.getEmpiId());
			if (ae.getCls().equals("chis.script.EHRView")) {
				sb.append("&closeNav=true&initModules=['").append(actionId)
						.append("']");
			}
			String args = Base64.encode(sb.toString().getBytes());
			String url = new StringBuffer(rootAddr).append(args).toString();
			Element e = createTaskElement(actionId, ae.getActionName(),
					ACTION_TYPE_BUSINESS_WORK, MANDATORY, url);
			taskLstElement.add(e);
		}
	}

	/**
	 * @param icd10sEle
	 * @return
	 */
	private ArrayList<String> getIcd10s(Element icd10sEle, Session session)
			throws Exception {
		List<String> icd10Lst = new ArrayList<String>();
		for (Iterator<?> it = icd10sEle.elementIterator("icd10"); it.hasNext();) {
			Element e = (Element) it.next();
			icd10Lst.add(e.getTextTrim());
		}
		ArrayList<String> icdActions = new ArrayList<String>();
		for (Iterator<String> it = icd10Lst.iterator(); it.hasNext();) {
			String icd10 = PublicService.getActionIdByIcd10(it.next(), session);
			if (icdActions.contains(icd10) || icd10 == null) {
				continue;
			}
			icdActions.add(icd10);
		}
		return icdActions;
	}

	/**
	 * 
	 * @return
	 */
	private Element makeHypertensionFirstTaskElement(RequestArgs rArgs,
			String actionType) {
		String args = Base64.encode(new StringBuffer("replace=true&uid=")
				.append(rArgs.getUser()).append("&pass=")
				.append(rArgs.getPassword()).append("&role=")
				.append(rArgs.getRole()).append("&cls=")
				.append(actionMap.get(hypertenFirst).getCls())
				.append("&empiId=").append(rArgs.getEmpiId()).toString()
				.getBytes());
		String url = new StringBuffer(rootAddr).append(args).toString();
		return createTaskElement(actionMap.get(hypertenFirst).getActionId(),
				actionMap.get(hypertenFirst).getActionName(),
				ACTION_TYPE_BUSINESS_WORK, MANDATORY, url);
	}

	/**
	 * @param rArgs
	 * @param actionType
	 * @return
	 */
	private Element makeDiabetesSimilarityTaskElement(RequestArgs rArgs,
			String actionType) {
		String args = Base64.encode(new StringBuffer("replace=true&uid=")
				.append(rArgs.getUser()).append("&pass=")
				.append(rArgs.getPassword()).append("&role=")
				.append(rArgs.getRole()).append("&cls=")
				.append(actionMap.get(diabetesSimilarity).getCls())
				.append("&empiId=").append(rArgs.getEmpiId()).toString()
				.getBytes());
		String url = new StringBuffer(rootAddr).append(args).toString();
		return createTaskElement(actionMap.get(diabetesSimilarity)
				.getActionId(), actionMap.get(diabetesSimilarity)
				.getActionName(), ACTION_TYPE_BUSINESS_WORK, MANDATORY, url);
	}

	/**
	 * @param empiId
	 * @param actionType
	 * @param user
	 * @param password
	 * @return
	 */
	private Element makeCheckHypertensionFirstTaskElement(RequestArgs rArgs,
			String actionType) {
		String args = Base64.encode(new StringBuffer("replace=true&uid=")
				.append(rArgs.getUser()).append("&pass=")
				.append(rArgs.getPassword()).append("&role=")
				.append(rArgs.getRole()).append("&cls=")
				.append(actionMap.get(checkHypertensionFirst).getCls())
				.append("&empiId=").append(rArgs.getEmpiId()).toString()
				.getBytes());
		String url = new StringBuffer(rootAddr).append(args).toString();
		return createTaskElement(actionMap.get(checkHypertensionFirst)
				.getActionId(), actionMap.get(checkHypertensionFirst)
				.getActionName(), ACTION_TYPE_BUSINESS_WORK, MANDATORY, url);
	}

	/**
	 * @param empiId
	 * @param actionType
	 * @param user
	 * @param password
	 * @return
	 */
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
	private Element makeCreateOldPeopleRecordTaskElement(RequestArgs rArgs,
			String actionType) {
		String args = Base64.encode(new StringBuffer("replace=true&uid=")
				.append(rArgs.getUser())
				.append("&pass=")
				.append(rArgs.getPassword())
				.append("&role=")
				.append(rArgs.getRole())
				.append("&cls=")
				.append(actionMap.get(
						actionMap.get(createOldPeopleRecord).getActionId())
						.getCls()).append("&closeNav=true&initModules=['")
				.append(actionMap.get(createOldPeopleRecord).getActionId())
				.append("', 'B_06']&empiId=").append(rArgs.getEmpiId())
				.toString().getBytes());
		String mustDo = OPTIONAL;
		if (actionType.equals(ACTION_TYPE_CREATE_DOC)) {
			mustDo = MANDATORY;
		}
		String url = new StringBuffer(rootAddr).append(args).toString();
		return createTaskElement(actionMap.get(createOldPeopleRecord)
				.getActionId(), actionMap.get(createOldPeopleRecord)
				.getActionName(), actionType, mustDo, url);
	}

	/**
	 * @param rArgs
	 * @param actionType
	 * @return
	 */
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

	/**
	 * @param empiId
	 * @param actionType
	 * @param user
	 * @param password
	 * @return
	 */
	private Element makeNeedOldPeopleVisitTaskElement(RequestArgs rArgs,
			String actionType) {
		StringBuffer args = new StringBuffer("replace=true&uid=")
				.append(rArgs.getUser()).append("&pass=")
				.append(rArgs.getPassword()).append("&role=")
				.append(rArgs.getRole()).append("&cls=")
				.append(actionMap.get(needOldPeopleVisit).getCls())
				.append("&closeNav=true&initModules=['")
				.append(actionMap.get(needOldPeopleVisit).getActionId())
				.append("']&empiId=").append(rArgs.getEmpiId())
				.append("&phrId=").append(rArgs.getPhrId());
		String encodedArgs = Base64.encode(args.toString().getBytes());
		String url = new StringBuffer(rootAddr).append(encodedArgs).toString();
		return createTaskElement(actionMap.get(needOldPeopleVisit)
				.getActionId(), actionMap.get(needOldPeopleVisit)
				.getActionName(), actionType, OPTIONAL, url);
	}

	/**
	 * @param rArgs
	 * @param actionType
	 * @return
	 */
	private Element makeNeedHypertensionClinicTaskElement(RequestArgs rArgs,
			String actionType) {
		String args = Base64.encode(new StringBuffer("replace=true&uid=")
				.append(rArgs.getUser()).append("&pass=")
				.append(rArgs.getPassword()).append("&role=")
				.append(rArgs.getRole()).append("&cls=")
				.append(actionMap.get(needHypertensionClinic).getCls())
				.append("&empiId=").append(rArgs.getEmpiId()).toString()
				.getBytes());
		String url = new StringBuffer(rootAddr).append(args).toString();
		return createTaskElement(actionMap.get(needHypertensionClinic)
				.getActionId(), actionMap.get(needHypertensionClinic)
				.getActionName(), actionType, MANDATORY, url);
	}

	/**
	 * @param empiId
	 * @param actionType
	 * @param user
	 * @param password
	 * @return
	 */
	private Element makeNeedHypertensionVisitTaskElement(RequestArgs rArgs,
			String actionType) {
		String args = Base64.encode(new StringBuffer("replace=true&uid=")
				.append(rArgs.getUser()).append("&pass=")
				.append(rArgs.getPassword()).append("&role=")
				.append(rArgs.getRole()).append("&cls=")
				.append(actionMap.get(needHypertensionVisit).getCls())
				.append("&closeNav=true&initModules=['")
				.append(actionMap.get(needHypertensionVisit).getActionId())
				.append("']&empiId=").append(rArgs.getEmpiId()).toString()
				.getBytes());
		String url = new StringBuffer(rootAddr).append(args).toString();
		return createTaskElement(actionMap.get(needHypertensionVisit)
				.getActionId(), actionMap.get(needHypertensionVisit)
				.getActionName(), actionType, MANDATORY, url);
	}

	/**
	 * @param empiId
	 * @param actionType
	 * @param user
	 * @param password
	 * @return
	 */
	private Element makeNeedDiabetesVisitTaskEelement(RequestArgs rArgs,
			String actionType) {
		String args = Base64.encode(new StringBuffer("replace=true&uid=")
				.append(rArgs.getUser()).append("&pass=")
				.append(rArgs.getPassword()).append("&role=")
				.append(rArgs.getRole()).append("&cls=")
				.append(actionMap.get(needDiabetesVisit).getCls())
				.append("&closeNav=true&initModules=['")
				.append(needDiabetesVisit).append("']&empiId=")
				.append(rArgs.getEmpiId()).toString().getBytes());
		String url = new StringBuffer(rootAddr).append(args).toString();
		return createTaskElement(
				actionMap.get(needDiabetesVisit).getActionId(),
				actionMap.get(needDiabetesVisit).getActionName(), actionType,
				MANDATORY, url);
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
	 * 检查当前操作人跟档案是否在同一个中心。
	 * 
	 * @param manaUnit
	 * @param ctx
	 * @return
	 */
	protected boolean checkCenter(String manaUnit, String userUnit) {
		if (manaUnit == null) {
			return false;
		}
		if (userUnit.length() < 9) {
			return false;
		} else if (manaUnit.startsWith(userUnit.substring(0, 9))) {
			return true;
		}
		return false;
	}

	/**
	 * 检查当前是否责任医生本人在操作。
	 * 
	 * @param manaDoc
	 * @param user
	 * @return
	 */
	protected boolean checkManaDoc(String manaDoc, String user) {
		if (manaDoc.equals(user)) {
			return true;
		}
		return false;
	}

	public HashMap<String, ActionEntity> getActionMap() {
		return actionMap;
	}

	public void setActionMap(HashMap<String, ActionEntity> actionMap) {
		this.actionMap = actionMap;
	}
}
