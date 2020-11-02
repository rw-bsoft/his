/**
 * @(#)CHISRecordFilter.java Created on 2013-12-11 下午5:09:11
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.filter;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ctd.account.UserRoleToken;
import ctd.app.Application;
import ctd.controller.exception.ControllerException;
import ctd.service.core.ServiceException;
import ctd.util.S;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;
import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.conf.SystemCofigManageModel;
import chis.source.control.ControlRunner;
import chis.source.dic.DiagnosisType;
import chis.source.empi.EmpiModel;
import chis.source.mdc.DiabetesVisitModel;
import chis.source.mdc.HypertensionRiskModel;
import chis.source.mdc.HypertensionSimilarityModel;
import chis.source.mdc.HypertensionVisitModel;
import chis.source.mdc.MDCBaseModel;
import chis.source.phr.HealthRecordModel;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.tr.TumourQuestionnaireModel;
import chis.source.util.ApplicationUtil;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import chis.source.util.UserUtil;
import chis.source.worklist.WorkListModel;

/**
 * @description 节点三个状态：create (创建) read(查看) hide(隐藏) 依据配置条件 查不到记录 返回 create 查到记录
 *              返回 read 其他必备条件不满足 返回 hide
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class CHISRecordFilter extends AbstractActionService implements
		DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(CHISRecordFilter.class);
	public static final String MS_BCJL = "chis.application.his.schemas.MS_BCJL";
	// 节点状态定义
	protected static final String NODE_STATUS_CREATE = "create";
	protected static final String NODE_STATUS_READ = "read";
	protected static final String NODE_STATUS_HIDE = "hide";

	protected static String riskFactors = "";
	// 返回要做问卷的模板类型列表
	private List<String> mtList = new ArrayList<String>();

	private HashMap<String, ConditionEntity> conditionMap = null;

	public void doGetRiskFactors(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		res.put("riskFactors", riskFactors);
	}

	@SuppressWarnings("unchecked")
	public void doGetNodeFilterParameters(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) {
		Map<String, Object> bodyMap = (Map<String, Object>) req.get("body");
		String empiId = (String) bodyMap.get("empiId");
		String JZXH = (String) bodyMap.get("JZXH");
		// 获取病人诊断相关参数
		Map<String, Object> diMap = null;
		try {
			diMap = this.getDiagnosisInfoForCHIS(empiId, JZXH, dao);
		} catch (ServiceException e1) {
			e1.printStackTrace();
		}
		if (diMap != null) {
			bodyMap.putAll(diMap);
		}
		Application app = null;
		try {
			app = ApplicationUtil.getApplication(Constants.UTIL_APP_ID);
		} catch (ControllerException e) {
			logger.error(e.getMessage(), e);
		}
		String healthCheckType = (String) app.getProperty("healthCheckType");
		Map<String, Object> nsMap = new HashMap<String, Object>();
		Iterator<Entry<String, ConditionEntity>> iter = conditionMap.entrySet()
				.iterator();
		String postnatalVisitType = (String) app
				.getProperty("postnatalVisitType");
		String postnatal42dayType = (String) app
				.getProperty("postnatal42dayType");
		// 提示信息
		List<Map<String, Object>> rsMsgList = new ArrayList<Map<String, Object>>();
		// 获取档案状态信息
		Map<String, Object> riMap = null;
		try {
			riMap = this.getRecordInfo(empiId, dao);
		} catch (ServiceException e1) {
			e1.printStackTrace();
		}
		boolean rootNodeStatus = true;
		while (iter.hasNext()) {
			Entry<String, ConditionEntity> entry = iter.next();
			String key = entry.getKey();
			String nodeStatus = NODE_STATUS_CREATE;
			try {
				nodeStatus = getNodeStatus(key, bodyMap, riMap, dao);
			} catch (ServiceException e1) {
				e1.printStackTrace();
			}
			if (key.equals("chis.ehrViewNav/D_0101")) {
				if (nodeStatus.equals(NODE_STATUS_HIDE)) {
					nodeStatus = NODE_STATUS_READ;
				}
			}
			if (key.equals("chis.ehrViewNav/B_10")
					&& "paper".equals(healthCheckType)) {
				nodeStatus = NODE_STATUS_HIDE;
			}
			if (key.equals("chis.ehrViewNav/B_10_HTML")
					&& "form".equals(healthCheckType)) {
				nodeStatus = NODE_STATUS_HIDE;
			}
			if (key.equals("chis.ehrViewNav/G_06")
					&& "paper".equals(postnatalVisitType)) {
				nodeStatus = NODE_STATUS_HIDE;
			}
			if (key.equals("chis.ehrViewNav/G_06_html")
					&& "form".equals(postnatalVisitType)) {
				nodeStatus = NODE_STATUS_HIDE;
			}
			if (key.equals("chis.ehrViewNav/G_07")
					&& "paper".equals(postnatal42dayType)) {
				nodeStatus = NODE_STATUS_HIDE;
			}
			if (key.equals("chis.ehrViewNav/G_07_html")
					&& "form".equals(postnatal42dayType)) {
				nodeStatus = NODE_STATUS_HIDE;
			}
			if (key.equals("chis.ehrViewNav/B_90") || key.equals("chis.ehrViewNav/B_91")) {
				nodeStatus = NODE_STATUS_READ;
			}
			// 节点状态为create时,判断是否有该人员数据操作权限
			if (NODE_STATUS_CREATE.equals(nodeStatus)) {
				ConditionEntity conditionEntity = conditionMap.get(key);
				boolean accessControl = conditionEntity.isAccessControl();
				if (accessControl) {
					Map<String, Object> dataMap = new HashMap<String, Object>();
					dataMap.put("empiId", empiId);
					HealthRecordModel hrModel = new HealthRecordModel(dao);
					try {
						String phrId = hrModel.getPhrId(empiId);
						dataMap.put("phrId", phrId);
					} catch (ModelDataOperationException e) {
						logger.error("Get phrId by empiId failure.", e);
						e.printStackTrace();
					}
					String acRuleId = conditionEntity.getAcRuleId();
					if (StringUtils.isEmpty(acRuleId)) {
						continue;
					}
					String acExpActon = conditionEntity.getAcExpAction();
					Map<String, Boolean> acMap = null;
					try {
						acMap = ControlRunner.run(acRuleId, dataMap, ctx,
								acExpActon);
					} catch (ServiceException e) {
						e.printStackTrace();
					}
					if (acMap != null && acMap.size() > 0) {
						boolean actionStatus = acMap.get(acExpActon);
						if (actionStatus == false) {
							nodeStatus = NODE_STATUS_HIDE;
						}
					}
				}
			}
			nsMap.put(key, nodeStatus);
			// 收集返回提示信息
			ConditionEntity conditionEntity = conditionMap.get(key);
			if (conditionEntity != null) {
				if (NODE_STATUS_CREATE.equals(nodeStatus)
						&& !key.equals("chis.application.tcm.TCM/TCM/TCM_CRM")
						&& !key.equals("chis.ehrViewNav/TCM_SG")) {
					Map<String, Object> nMap = new HashMap<String, Object>();
					nMap.put("nodeName", conditionEntity.getNodeShowName());
					nMap.put("nodeKey", conditionEntity.getNodeKey());
					rsMsgList.add(nMap);
				}
			}
			// 判断根节点
			if (key.equals("chis.ehrViewNav/B_01,chis.ehrViewNav/B_02,chis.ehrViewNav/B_03,chis.ehrViewNav/B_04")) {
				if (NODE_STATUS_HIDE.equals(nodeStatus)) {
					rootNodeStatus = false;
				}
				rootNodeStatus = true;
			}
		}
		if (rootNodeStatus) {
			nsMap.put("A", NODE_STATUS_READ);
		} else {
			nsMap.put("A", NODE_STATUS_HIDE);
		}
		nsMap.put("mtList", mtList);
		nsMap.put("rsMsgList", rsMsgList);
		logger.info("EMRView node of chis filter === " + nsMap.toString());
		res.put("body", nsMap);
		// 为了不影响其他节点的显示，返回始于要成功
		res.put(RES_CODE, Constants.CODE_OK);
		res.put(RES_MESSAGE, "成功！");
	}

	@SuppressWarnings("unchecked")
	public void doGetNodeFilterParametersFCBP(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, NumberFormatException, ControllerException {
		Map<String, Object> bodyMap = (Map<String, Object>) req.get("body");
		String empiId = (String) bodyMap.get("empiId");
		boolean flag = checkNeedFCBP(empiId, dao);
		Map<String, Object> nsMap = new HashMap<String, Object>();
		nsMap.put("D", NODE_STATUS_READ);
		if (flag) {
			nsMap.put("chis.application.diseasemanage.DISEASEMANAGE/HY/C32",
					NODE_STATUS_CREATE);
		} else {
			nsMap.put("chis.application.diseasemanage.DISEASEMANAGE/HY/C32",
					NODE_STATUS_READ);
		}
		res.put("body", nsMap);
	}

	/**
	 * 
	 * @Description:获取某个节点的状态
	 * @param entryName
	 * @param empiId
	 * @param dao
	 * @return
	 * @throws ServiceException
	 * @author ChenXianRui 2013-12-13 上午10:29:22
	 * @throws ControllerException
	 * @throws NumberFormatException
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	protected String getNodeStatus(String key, Map<String, Object> bodyMap,
			Map<String, Object> riMap, BaseDAO dao) throws ServiceException {
		String nodeStatus = NODE_STATUS_HIDE;
		ConditionEntity conditionEntity = conditionMap.get(key);
		if (conditionEntity == null) {
			return nodeStatus;
		}
		String createReadField = conditionEntity.getCreateReadField();
		if (S.isNotEmpty(createReadField)
				&& (riMap != null && riMap.size() > 0)) {
			int rStatus = 0;
			if (riMap.get(createReadField) != null) {
				rStatus = (Integer) riMap.get(createReadField);
			}
			// if(rStatus == 0){
			// //档案状态表中标识为档案未创建，则直接返回节点状态为“create”
			// nodeStatus = NODE_STATUS_CREATE;
			// return nodeStatus;
			// }
			if (rStatus == 1) {
				// 档案状态表中标识已经创建档案，则直接返回节点状态为“read”
				nodeStatus = NODE_STATUS_READ;
				return nodeStatus;
			}
		}
		String empiId = (String) bodyMap.get("empiId");
		String prerequisite = conditionEntity.getPrerequisite();
		boolean GXY = (Boolean) bodyMap.get("GXY");
		boolean TLB = (Boolean) bodyMap.get("TLB");
		boolean BGK = (Boolean) bodyMap.get("BGK");
		boolean HYSC = (Boolean) bodyMap.get("HYSC");
		List<String> thqmList = (List<String>) bodyMap.get("thqmList");
		double BMI = bodyMap.get("BMI") == null ? 0 : Double
				.parseDouble(bodyMap.get("BMI") + "");

		// 个人签约
		if ("GRQY".equals(prerequisite)) {
			// 如果家庭档案存并且户主已经签约
			if (this.checkGRQY(empiId, dao)) {
				nodeStatus = NODE_STATUS_CREATE;
			}
			return nodeStatus;
		}
		boolean isExecuteSelect = false;
		if ("no".equals(prerequisite)) {
			isExecuteSelect = true;
		} else if ("HAR".equals(prerequisite)) {
			boolean flag = checkNeedCreateHAR(empiId, dao);
			isExecuteSelect = flag;
		} else if ("TLBGWSC".equals(prerequisite)) {
			boolean flag = checkNeedTLBGWSC(empiId, dao, BMI);
			isExecuteSelect = flag;
		} else if ("HYSC".equals(prerequisite)) {
			isExecuteSelect = true;
		} else if ("HYS".equals(prerequisite)) {
			boolean flag = isRecordExists(MDC_HypertensionRecord, empiId, null,
					null, dao);
			if (flag) {
				isExecuteSelect = false;
			} else {
				isExecuteSelect = true;
			}
		} else if ("TYS".equals(prerequisite)) {
			boolean flag = checkNeedTLBYS(empiId, dao);
			isExecuteSelect = flag;
		} else if ("oldPeople".equals(prerequisite)) {
			int age = this.getAge(empiId, dao);
			int oldPeopleAge = 60;
			try {
				oldPeopleAge = Integer.valueOf(ApplicationUtil.getProperty(
						Constants.UTIL_APP_ID, "oldPeopleAge"));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (ControllerException e) {
				e.printStackTrace();
			}
			if (age >= oldPeopleAge) {
				isExecuteSelect = true;
			}
		} else if ("children".equals(prerequisite)) {
			int age = this.getAge(empiId, dao);
			if (age < 7) {
				isExecuteSelect = true;
			}
		} else if ("children1".equals(prerequisite)) {
			int age = this.getAge(empiId, dao);
			if (age < 1) {
				isExecuteSelect = true;
			}
		} else if ("children1-2".equals(prerequisite)) {
			int age = this.getAge(empiId, dao);
			if (age >= 1 && age < 3) {
				isExecuteSelect = true;
			}
		} else if ("children3-6".equals(prerequisite)) {
			int age = this.getAge(empiId, dao);
			if (age >= 3 && age < 7) {
				isExecuteSelect = true;
			}
		} else if ("thqmList".equals(prerequisite)) {
			if (thqmList != null && thqmList.size() > 0) {
				isExecuteSelect = true;
			}
		} else if ("TLB".equals(prerequisite)) {
			if (TLB == false) {
				TLB = checkTNBNeedCreate(empiId, dao);
				bodyMap.put("TLB", TLB);
			}
			isExecuteSelect = true;
		} else {
			isExecuteSelect = true;
		}
		if(key.equals("chis.ehrViewNav/PC_02_02")){
			return NODE_STATUS_HIDE;
		}
		if(key.equals("chis.ehrViewNav/ZJ_JKXW")){
			return NODE_STATUS_READ;
		}
		if(key.equals("chis.application.diseasemanage.DISEASEMANAGE/HY/C32")
				&&(riMap==null || riMap.size()==0)){
			return NODE_STATUS_HIDE;
		}
		if (isExecuteSelect) {
			String entryName = conditionEntity.getEntryName();
			String dependencies = conditionEntity.getDependencies();
			boolean isSelectUnnormal = conditionEntity.isSelectUnnormal();
			Map<String, Object> condtions = conditionEntity.getCondtions();
			if ("base".equals(dependencies)) {
				boolean isExistsNormalRecord = isRecordExists(entryName,
						empiId, Constants.CODE_STATUS_NORMAL, condtions, dao);
				if (isExistsNormalRecord) {
					nodeStatus = NODE_STATUS_READ;
				} else {
					if (isSelectUnnormal) {
						boolean isExistsRecord = isRecordExists(entryName,
								empiId, null, condtions, dao);
						if (isExistsRecord) {
							nodeStatus = NODE_STATUS_HIDE;
						} else {
							nodeStatus = NODE_STATUS_CREATE;
						}
					} else {
						nodeStatus = NODE_STATUS_CREATE;
					}
				}
			} else {
				nodeStatus = getNodeStatus(dependencies, bodyMap, riMap, dao);
				String dependNodeStatus = conditionEntity.getDependNodeStatus();
				if (dependNodeStatus.equals(nodeStatus)) {
					if ("thqmList".equals(prerequisite)) {// 肿瘤问卷
						// ①已做过问卷就不再做问卷 ②已是本年度初筛人群就不再做问卷 ③已是高危、确诊人群就不再做问卷
						TumourQuestionnaireModel tqModel = new TumourQuestionnaireModel(
								dao);
						try {
							List<String> mTypes = tqModel
									.getNeedQuestionnaireOfTemplate(empiId,
											thqmList);
							if (mTypes != null && mTypes.size() > 0) {
								nodeStatus = NODE_STATUS_CREATE;
								mtList.clear();
								mtList = mTypes;
							} else {
								nodeStatus = NODE_STATUS_HIDE;
							}
						} catch (ModelDataOperationException e) {
							throw new ServiceException(e);
						}
					} else if (key.equals("chis.ehrViewNav/M_02")) {
						int xnStatus = isXnReport(empiId, dao);
						if (xnStatus == 0) {
							nodeStatus = NODE_STATUS_HIDE;
						} else if (xnStatus == 1) {
							nodeStatus = NODE_STATUS_CREATE;
						} else {
							nodeStatus = NODE_STATUS_READ;
						}
					} else if ("TCMQ".equals(prerequisite)) {// 中医体质辨识
						nodeStatus = this.getTCMQNodeStatus(empiId, dao);
					} else if ("TCMSG".equals(prerequisite)) {// 中医指导
						//目前南京没有这个模块故隐掉
//						nodeStatus = this.getTCMSGNodeStatus(empiId, dao);
						nodeStatus=NODE_STATUS_HIDE;
					} else {
						boolean isExistsNormalRecord = false;
						if (key.equals("chis.ehrViewNav/C_03")) {

							HypertensionVisitModel dvm = new HypertensionVisitModel(
									dao);
							Map<String, Object> result = null;
							try {
								result = dvm.checkHasVisitInPerCycle(empiId,
										new Date());
							} catch (ModelDataOperationException e) {
								e.printStackTrace();
							}
							if (result != null && !result.isEmpty()) {
								isExistsNormalRecord = !Boolean
										.parseBoolean(result.get("hasVisit")
												.toString());
							} else {
								isExistsNormalRecord = true;
							}
							// isExistsNormalRecord = isRecordExists(
							// entryName, empiId,
							// Constants.CODE_STATUS_NORMAL, condtions, dao);
						} else {
							isExistsNormalRecord = isRecordExists(entryName,
									empiId, Constants.CODE_STATUS_NORMAL,
									condtions, dao);
							if ("chis.application.mh.schemas.SQ_ZKSFJH"
									.equals(entryName)) {
								if (isExistsNormalRecord == false) {
									nodeStatus = NODE_STATUS_READ;
									isExistsNormalRecord = true;
								} else {
									nodeStatus = NODE_STATUS_CREATE;
								}
							}
						}
						if (isExistsNormalRecord) {
							if (BSCHISEntryNames.PUB_VisitPlan
									.equals(entryName)) {
								nodeStatus = NODE_STATUS_CREATE;
							} else if ("chis.application.mh.schemas.SQ_ZKSFJH"
									.equals(entryName)) {
							} else {
								nodeStatus = NODE_STATUS_READ;
							}
						} else {
							if (isSelectUnnormal) {
								boolean isExistsRecord = isRecordExists(
										entryName, empiId, null, condtions, dao);
								if (isExistsRecord) {
									if (BSCHISEntryNames.PUB_VisitPlan
											.equals(entryName)) {
										nodeStatus = NODE_STATUS_READ;
									} else {
										nodeStatus = NODE_STATUS_HIDE;
									}
								} else {
									if (BSCHISEntryNames.PUB_VisitPlan
											.equals(entryName)) {
										nodeStatus = NODE_STATUS_HIDE;
									} else {
										nodeStatus = NODE_STATUS_CREATE;
									}
								}
							} else {
								if (BSCHISEntryNames.PUB_VisitPlan
										.equals(entryName)) {
									nodeStatus = NODE_STATUS_HIDE;
								} else {
									nodeStatus = NODE_STATUS_CREATE;
								}
							}
						}
					}

				} else {
					if (NODE_STATUS_CREATE.equals(nodeStatus)) {
						nodeStatus = NODE_STATUS_HIDE;
					}
					if (NODE_STATUS_HIDE.equals(nodeStatus)) {
						nodeStatus = NODE_STATUS_HIDE;
					}
				}
			}

		}
		WorkListModel wlm = new WorkListModel(dao);
		Map<String, Object> m = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		m.put("empiId", empiId);
		m.put("manaUnitId", user.getManageUnitId());
		m.put("doctorId", user.getUserId());
		String recordId = getEHRRecordId(empiId, dao);
		m.put("recordId", recordId);
		boolean isExistWL = true;
		boolean isExistRecord = true;
		isExistRecord = isExistRecordByType(prerequisite, empiId, dao);
		// @@ == 满足特定条件才显示********
		if ("FCBP".equals(prerequisite)) {
			boolean flag = checkNeedFCBP(empiId, dao);
			if (flag) {
				nodeStatus = NODE_STATUS_CREATE;
			} else {
				nodeStatus = NODE_STATUS_READ;
			}
		} else if ("GXYFZPG".equals(prerequisite)) {
			isExistWL = isExistWorkList(recordId, empiId, "24", dao);
			if (isExistWL) {
				nodeStatus = NODE_STATUS_READ;
			} else if (isExistRecord) {
				nodeStatus = NODE_STATUS_READ;
			} else {
				nodeStatus = NODE_STATUS_HIDE;
			}
		} else if ("GXY".equals(prerequisite)) {
			isExistWL = isExistWorkList(recordId, empiId, "21", dao);
			if (NODE_STATUS_CREATE.equals(nodeStatus) && GXY == false) {
				nodeStatus = NODE_STATUS_HIDE;
			}
			// if (GXY == false) {
			// boolean isExistCHS =
			// isExistConfirmedHypertensionSimilarity(empiId, dao);
			// if (!isExistCHS) {
			// if (NODE_STATUS_CREATE.equals(nodeStatus)) {
			// nodeStatus = NODE_STATUS_HIDE;
			// }
			// }
			// m.put("workType", "21");
			// if (isExistWL == true) {
			// try {
			// wlm.deleteWorkList(m);
			// } catch (ModelDataOperationException e) {
			// e.printStackTrace();
			// }
			// }
			// }
			if (GXY == false && isExistWL == true) {
				m.put("workType", "21");
				try {
					wlm.deleteWorkList(m);
				} catch (ModelDataOperationException e) {
					e.printStackTrace();
				}
			} else if (isExistWL == false && GXY == true
					&& isExistRecord == false) {
				m.put("beginDate", new Date());
				m.put("endDate", new Date(Long.MAX_VALUE));
				m.put("workType", "21");
				try {
					wlm.insertWorkList(m);
				} catch (ModelDataOperationException e) {
					e.printStackTrace();
				}
			} else if (isExistWL == false) {
				if (NODE_STATUS_CREATE.equals(nodeStatus)) {
					nodeStatus = NODE_STATUS_HIDE;
				}
			}
		}
		if ("TLB".equals(prerequisite)) {
			isExistWL = isExistWorkList(recordId, empiId, "18", dao);
			if (NODE_STATUS_CREATE.equals(nodeStatus) && TLB == false) {
				nodeStatus = NODE_STATUS_HIDE;
			}
			if (TLB == false && isExistWL == true) {
				m.put("workType", "18");
				try {
					wlm.deleteWorkList(m);
				} catch (ModelDataOperationException e) {
					e.printStackTrace();
				}
			} else if (isExistWL == false && TLB == true
					&& isExistRecord == false) {
				m.put("beginDate", new Date());
				m.put("endDate", new Date(Long.MAX_VALUE));
				m.put("workType", "18");
				try {
					if(m.get("recordId")!=null){
						wlm.insertWorkList(m);
					}
				} catch (ModelDataOperationException e) {
					e.printStackTrace();
				}
			}
		}
		if ("BGK".equals(prerequisite)) {
			if (NODE_STATUS_READ.equals(nodeStatus)) {// 判断是否还有未创建报卡的传染病诊断
				boolean exist = this.existNoReportOfContagionClinicDiagnosis(
						empiId, dao);
				if (exist) {
					nodeStatus = NODE_STATUS_CREATE;
				}
			}
			if (NODE_STATUS_CREATE.equals(nodeStatus) && BGK == false) {
				nodeStatus = NODE_STATUS_HIDE;
			}
		}
		if ("HYSC".equals(prerequisite)) {
			if (NODE_STATUS_CREATE.equals(nodeStatus) && HYSC == false) {
				nodeStatus = NODE_STATUS_HIDE;
			}
		}
		// @@ 下面是只有两种状态（create ,hide）节点的特殊处理***********************
		// 35岁首诊测压（一年一次），只要有记录就不显示（只创建不查询）
		if ("TFCP35".equals(prerequisite)
				&& NODE_STATUS_READ.equals(nodeStatus)) {
			nodeStatus = NODE_STATUS_HIDE;
		}
		// 高血压疑似：创建
		if ("HYSC".equals(prerequisite)) {
			if (NODE_STATUS_READ.equals(nodeStatus)) {
				nodeStatus = NODE_STATUS_HIDE;
			}
		}
		// 高血压核实：有疑似记录
		if ("HYS".equals(prerequisite)) {
			if (NODE_STATUS_CREATE.equals(nodeStatus)) {
				nodeStatus = NODE_STATUS_HIDE;
			}
			if (NODE_STATUS_READ.equals(nodeStatus)) {
				nodeStatus = NODE_STATUS_CREATE;
			}
		}
		// 糖尿病高危筛查
		if ("TLBGWSC".equals(prerequisite)) {
			boolean flag = this.checkHasRecord(empiId, dao);
			if (NODE_STATUS_CREATE.equals(nodeStatus) && !flag) {
				nodeStatus = NODE_STATUS_READ;
			}
		}
		// 高血压随访
		if ("GXYSF".equals(prerequisite) && nodeStatus == NODE_STATUS_CREATE) {
			HypertensionVisitModel dvm = new HypertensionVisitModel(dao);
			String group;
			try {
				group = dvm.getNearHypertensionGroup(empiId);
				Map<String, Object> result = dvm.checkHasVisitInPerCycle(group,
						empiId, new Date());
				if (result != null && result.size() > 0) {
					boolean flag = (Boolean) result.get("hasVisit");
					if (flag) {
						nodeStatus = NODE_STATUS_HIDE;
					}
				}
			} catch (ModelDataOperationException e) {
				e.printStackTrace();
			}
		}
		// 糖尿病随访
		if ("TLBSF".equals(prerequisite) && nodeStatus == NODE_STATUS_CREATE) {
			DiabetesVisitModel dvm = new DiabetesVisitModel(dao);
			String group;
			try {
				group = dvm.getNearDiabetesGroup(empiId);
				Map<String, Object> result = dvm.checkHasVisitInPerCycle(group,
						empiId, new Date());
				if (result != null && result.size() > 0) {
					boolean flag = (Boolean) result.get("hasVisit");
					if (flag) {
						nodeStatus = NODE_STATUS_HIDE;
					}
				}
			} catch (ModelDataOperationException e) {
				e.printStackTrace();
			}

		}

		return nodeStatus;
	}

	private boolean checkNeedCreateHAR(String empiId, BaseDAO dao) {
		boolean flag = false;
		String sql = "select count(a.GRBM) as totleCount from  SQ_JKPG a "
				+ "where a.GRBM=:empiId and a.LRRQ>=:startDate and a.LRRQ<:endDate";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		Calendar cal = Calendar.getInstance();
		int nowMonth = cal.get(Calendar.MONTH);
		int nowYear = cal.get(Calendar.YEAR);
		Date startDate = cal.getTime();
		Date endDate = cal.getTime();
		if (nowMonth < 9) {
			cal.set(nowYear, 9, 1, 0, 0, 0);
			endDate = cal.getTime();
			cal.set(nowYear - 1, 9, 1, 0, 0, 0);
			startDate = cal.getTime();
		} else {
			cal.set(nowYear, 9, 1, 0, 0, 0);
			startDate = cal.getTime();
			cal.set(nowYear + 1, 9, 1, 0, 0, 0);
			endDate = cal.getTime();
		}
		parameters.put("startDate", startDate);
		parameters.put("endDate", endDate);
		try {
			Map<String, Object> result = dao.doSqlLoad(sql, parameters);
			Long totleCount = Long.parseLong(result.get("TOTLECOUNT") + "");
			if (totleCount == 0) {
				flag = true;
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 
	 * @Description:
	 * @param empiId
	 * @param dao
	 * @return
	 * @throws ServiceException
	 * @author ChenXianRui 2015-8-4 上午11:46:58
	 * @Modify:
	 */
	private Map<String, Object> getRecordInfo(String empiId, BaseDAO dao)
			throws ServiceException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(BSCHISEntryNames.EHR_RecordInfo, empiId);
		} catch (PersistentDataOperationException e) {
			throw new ServiceException(Constants.CODE_DATABASE_ERROR,
					"获取档案状态信息失败！", e);
		}
		if (rsMap == null) {
			rsMap = new HashMap<String, Object>();
		}
		return rsMap;
	}

	private boolean checkHasRecord(String empiId, BaseDAO dao) {
		SystemCofigManageModel scmm = new SystemCofigManageModel(dao);
		String startMonth;
		try {
			startMonth = scmm.getSystemConfigData("diabetesRiskStartMonth");
			String endMonth = scmm.getSystemConfigData("diabetesRiskEndMonth");
			String startDateStr = BSCHISUtil.getStartDateForYear(startMonth);
			String endDateStr = BSCHISUtil.getEndDateForYear(endMonth);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date startDate = sdf.parse(startDateStr);
			Date endDate = sdf.parse(endDateStr);
			String sql = "select OGTTID as OGTTID from "
					+ MDC_DiabetesOGTTRecord
					+ " where registerDate >=:startDate  and registerDate<=:endDate and empiId=:empiId";
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("empiId", empiId);
			parameters.put("startDate", startDate);
			parameters.put("endDate", endDate);
			List<Map<String, Object>> list = dao.doQuery(sql, parameters);
			if (list != null && list.size() > 0) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 
	 * @Description:判断 是否 存在 没有传染病报告卡 的 传染病 门诊诊断
	 * @param empiId
	 * @param dao
	 * @return true:存在 false : 不存在
	 * @throws ServiceException
	 * @author ChenXianRui 2015-5-29 下午4:02:32
	 * @Modify:
	 */
	private boolean existNoReportOfContagionClinicDiagnosis(String empiId,
			BaseDAO dao) throws ServiceException {
		String sql = new StringBuffer("select count(a.JLBH) as recNUM from ")
				.append(" MS_BRZD a ")
				.append(" left join MS_BRDA b on a.BRID = b.BRID ")
				.append(" left join GY_JBBM c on a.ZDXH = c.JBXH ")
				.append(" where c.JBBGK = '06' ")
				.append(" and not exists (select 1 from idr_report d where a.jlbh=d.ms_brzd_jlbh) ")
				.append(" and b.empiId =:empiId").toString();
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("empiId", empiId);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doSqlQuery(sql, pMap);
		} catch (PersistentDataOperationException e) {
			logger.error(
					"check whether no report of  clinical diagnostic contagion exists failure. ",
					e);
			throw new ServiceException(e);
		}
		boolean exist = false;
		if (rsList != null && rsList.size() > 0) {
			Map<String, Object> rsMap = rsList.get(0);
			BigDecimal recNUM = (BigDecimal) rsMap.get("RECNUM");
			if (recNUM.longValue() > 0) {
				exist = true;
			}
		}
		return exist;
	}

	private boolean checkNeedFCBP(String empiId, BaseDAO dao) {
		try {
			int age = this.getAge(empiId, dao);
			if (age < 35) {
				return false;
			}
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		List<Object> cnd1 = CNDHelper.createSimpleCnd("eq",
				"to_char(a.measureDate,'yyyy')", "s", new SimpleDateFormat(
						"yyyy").format(new Date()));
		List<Object> cnd2 = CNDHelper.createSimpleCnd("eq", "a.empiId", "s",
				empiId);
		List<Object> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		try {
			List<Map<String, Object>> list = dao.doList(cnd, "",
					MDC_Hypertension_FCBP);
			if (list != null && list.size() > 0) {
				return false;
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		return true;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private boolean checkGRQY(String empiId, BaseDAO dao) {
		boolean flag = false;
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaDoctorId = user.getUserId();
		Map param = new HashMap();
		param.put("empiId", empiId);
		param.put("manaDoctorId", manaDoctorId);
//		String hql = " select empiId from ehr_healthrecord where familyId=( "
//				+ " select familyId from ehr_healthrecord where empiId=:empiId "
//				+ " and (signFlag is null or signFlag='n') and manaDoctorId=:manaDoctorId) "
//				+ " and familyId is not null and (masterFlag is not null and masterFlag='y') and signFlag='y' ";
//		
		String hql="select empiId from ehr_healthrecord where empiId=:empiId and manaDoctorId=:manaDoctorId and familyId is not null ";
		try {
			List l = dao.doSqlQuery(hql, param);
			if (l.size() > 0) {
				flag = true;
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		return flag;
	}

	private boolean checkNeedTLBYS(String empiId, BaseDAO dao) {
		List<Object> cnd1 = CNDHelper.createSimpleCnd("eq", "a.empiId", "s",
				empiId);
		List<Object> cnd2 = CNDHelper.createSimpleCnd("eq", "a.diagnosisType",
				"s", "2");
		List<Object> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		List<Map<String, Object>> records = null;
		try {
			records = dao.doList(cnd, "", MDC_DiabetesSimilarityCheck);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		if (records != null && records.size() > 0) {
			return true;
		}
		return false;
	}

	private boolean checkTNBNeedCreate(String empiId, BaseDAO dao) {
		String sql = "select result1 as result1,result2 as result2,result3 as result3 "
				+ "from MDC_DiabetesOGTTRecord where empiId=:empiId "
				+ "and inputDate=(select max(t.inputDate) from "
				+ "MDC_DiabetesOGTTRecord t where t.empiId=:empiId)";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		try {
			List<Map<String, Object>> list = dao.doSqlQuery(sql, parameters);
			if (list != null && list.size() > 0) {
				String result1 = list.get(0).get("RESULT1") + "";
				String result2 = list.get(0).get("RESULT2") + "";
				String result3 = list.get(0).get("RESULT3") + "";
				if (!"null".equals(result3)) {
					if ("5".equals(result3) || "3".equals(result3)
							|| "2".equals(result3)) {
						return true;
					}
				} else if (!"null".equals(result2)) {
					if ("5".equals(result2) || "3".equals(result2)
							|| "2".equals(result2)) {
						return true;
					}
				} else if (!"null".equals(result1)) {
					if ("5".equals(result1) || "3".equals(result1)
							|| "2".equals(result1)) {
						return true;
					}
				}
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean checkTNBSCNeedCreate(String empiId, BaseDAO dao) {
		String sql = "select result1 as result1,result2 as result2,result3 as result3 ,"
				+ "checkDate1 as checkDate1,checkDate2 as checkDate2,checkDate3 as checkDate3 "
				+ "from MDC_DiabetesOGTTRecord where empiId=:empiId "
				+ "and inputDate=(select max(t.inputDate) from "
				+ "MDC_DiabetesOGTTRecord t where t.empiId=:empiId)";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		try {
			List<Map<String, Object>> list = dao.doSqlQuery(sql, parameters);
			if (list != null && list.size() > 0) {
				String result1 = list.get(0).get("RESULT1") + "";
				String result2 = list.get(0).get("RESULT2") + "";
				String result3 = list.get(0).get("RESULT3") + "";
				Date checkDate1 = (Date) list.get(0).get("CHECKDATE1");
				Date checkDate2 = (Date) list.get(0).get("CHECKDATE2");
				Date checkDate3 = (Date) list.get(0).get("CHECKDATE3");
				if (!"null".equals(result3)) {
					if ("5".equals(result3)) {
						return true;
					} else if ("2".equals(result3) || "3".equals(result3)) {
						boolean flag = compareDate(new Date(), checkDate3, 1);
						if (flag == true) {
							return true;
						}
					} else if ("1".equals(result3)) {
						boolean flag = compareDate(new Date(), checkDate3, 3);
						if (flag == true) {
							return true;
						}
					}
				} else if (!"null".equals(result2)) {
					if ("5".equals(result2)) {
						return true;
					} else if ("2".equals(result2) || "3".equals(result2)) {
						boolean flag = compareDate(new Date(), checkDate2, 1);
						if (flag == true) {
							return true;
						}
					} else if ("1".equals(result2)) {
						boolean flag = compareDate(new Date(), checkDate2, 3);
						if (flag == true) {
							return true;
						}
					}
				} else if (!"null".equals(result1)) {
					if ("5".equals(result1)) {
						return true;
					} else if ("2".equals(result1) || "3".equals(result1)) {
						boolean flag = compareDate(new Date(), checkDate1, 1);
						if (flag == true) {
							return true;
						}
					} else if ("1".equals(result1)) {
						boolean flag = compareDate(new Date(), checkDate1, 3);
						if (flag == true) {
							return true;
						}
					}
				}
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	private boolean compareDate(Date date, Date checkDate3, int i) {
		if (date == null || checkDate3 == null) {
			return false;
		}
		int nYear = date.getYear();
		int nMonth = date.getMonth();
		int nDay = date.getDate();
		int oYear = checkDate3.getYear();
		int oMonth = checkDate3.getMonth();
		int oDay = checkDate3.getDate();
		if (nYear - oYear < i) {
			return false;
		} else if (nYear - oYear == i) {
			if (nMonth - oMonth == 0) {
				if (nDay - oDay < 0) {
					return false;
				}
			} else if (nMonth - oMonth < 0) {
				return false;
			}
		}
		return true;
	}

	private boolean checkNeedTLBGWSC(String empiId, BaseDAO dao, double bMI) {
		List<Object> cnd = CNDHelper.createSimpleCnd("eq", "a.empiId", "s",
				empiId);
		List<Map<String, Object>> records = null;
		riskFactors = "";
		try {
			records = dao.doList(cnd, "", MDC_DiabetesRecord);
			if (records != null && records.size() > 0) {
				return false;
			}
			boolean flag = checkTNBSCNeedCreate(empiId, dao);
			if (flag == true) {
				return true;
			}
			records = dao.doList(cnd, "", MDC_DiabetesOGTTRecord);
			if (records != null && records.size() > 0) {
				return true;
			} else {
				int score = 9;
				riskFactors = "08";
				if (bMI >= 24) {
					score = score + 8;
					riskFactors = riskFactors + ",02";
				}
				int age = this.getAge(empiId, dao);
				if (age >= 50 && age < 60) {
					score = score + 6;
				} else if (age >= 60 && age < 70) {
					score = score + 10;
				} else if (age >= 70) {
					score = score + 16;
				}
				if (age >= 45) {
					riskFactors = riskFactors + ",01";
				}
				String sql = "select pastHistoryId from " + EHR_PastHistory
						+ " where diseaseCode='0202' and empiId=:empiId";
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("empiId", empiId);
				List<Map<String, Object>> list = dao.doQuery(sql, parameters);
				if (list != null && list.size() > 0) {
					score = score + 5;
					riskFactors = riskFactors + ",09";
				} else {
					List<Map<String, Object>> list1 = dao.doList(cnd, "",
							MDC_HypertensionRecord);
					if (list1 != null && list1.size() > 0) {
						riskFactors = riskFactors + ",09";
						score = score + 5;
					}
				}
				sql = "select pastHistoryId from " + EHR_PastHistory
						+ " where diseaseCode='0203' and empiId=:empiId";
				list = dao.doQuery(sql, parameters);
				if (list != null && list.size() > 0) {
					score = score + 15;
					riskFactors = riskFactors + ",11";
				}
				sql = "select pastHistoryId from EHR_PastHistory where "
						+ "diseaseCode in ('0703','0803','0903','1003') and empiId=:empiId";
				list = dao.doSqlQuery(sql, parameters);
				if (list != null && list.size() > 0) {
					score = score + 10;
					riskFactors = riskFactors + ",07";
				}
				if (score >= 27 || age >= 45) {
					return true;
				}
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean isExistRecordByType(String prerequisite, String empiId,
			BaseDAO dao) {
		String schema = "";
		if ("GXY".equals(prerequisite)) {
			schema = MDC_HypertensionRecord;
		} else if ("TLB".equals(prerequisite)) {
			schema = MDC_DiabetesRecord;
		} else if ("GXYFZPG".equals(prerequisite)) {
			schema = MDC_HypertensionFixGroup;
		} else {
			return true;
		}
		List<Object> cnd = CNDHelper.createSimpleCnd("eq", "a.empiId", "s",
				empiId);
		List<Map<String, Object>> list = null;
		try {
			list = dao.doList(cnd, "a.empiId", schema);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		if (list != null && list.size() > 0) {
			return true;
		}
		return false;
	}

	private boolean isExistWorkList(String recordId, String empiId,
			String workType, BaseDAO dao) {
		if (recordId == null) {
			return false;
		}
		if (empiId == null) {
			return false;
		}
		List<Object> cnd1 = CNDHelper.createSimpleCnd("eq", "a.empiId", "s",
				empiId);
		List<Object> cnd2 = CNDHelper.createSimpleCnd("eq", "a.recordId", "s",
				recordId);
		List<Object> cnd3 = CNDHelper.createSimpleCnd("eq", "a.workType", "s",
				workType);
		List<Object> cnd4 = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		List<Object> cnd = CNDHelper.createArrayCnd("and", cnd3, cnd4);
		List<Map<String, Object>> list = null;
		if ("21".equals(workType) || "18".equals(workType)) {
			cnd = CNDHelper.createArrayCnd("and", cnd3, cnd1);
		}
		try {
			list = dao.doList(cnd, "a.empiId", PUB_WorkList);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		if (list != null && list.size() > 0) {
			return true;
		}
		return false;
	}

	private String getEHRRecordId(String empiId, BaseDAO dao) {
		List<Object> cnd = CNDHelper.createSimpleCnd("eq", "a.empiId", "s",
				empiId);
		List<Map<String, Object>> list = null;
		String recordId = null;
		try {
			list = dao.doList(cnd, "a.empiId", EHR_HealthRecord);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		if (list != null && list.size() > 0) {
			recordId = list.get(0).get("phrId") + "";
		}
		return recordId;
	}

	/**
	 * 
	 * @Description:检查一个表里的对应一个empiId的记录是否已存在
	 * @param table
	 *            表名
	 * @param empiId
	 * @param checkStatus
	 *            记录的状态
	 * @param dao
	 * @return 如果存在返回true，否则返回false。
	 * @throws ServiceException
	 * @author ChenXianRui 2013-12-12 上午10:25:33
	 * @Modify:
	 */
	protected boolean isRecordExists(String table, String empiId,
			String checkStatus, Map<String, Object> condtions, BaseDAO dao)
			throws ServiceException {
		StringBuffer hql = new StringBuffer("select count(*) as countNum from ")
				.append(table).append(" where empiId = :empiId");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		if (condtions != null && condtions.size() > 0) {
			Iterator<Entry<String, Object>> iterator = condtions.entrySet()
					.iterator();
			while (iterator.hasNext()) {
				Entry<String, Object> entry = iterator.next();
				if ("status".equals(entry.getKey())
						&& StringUtils.isNotEmpty(checkStatus)) {
					hql.append(" and status = :status0");
					parameters.put("status0", checkStatus);
				} else if ("status".equals(entry.getKey())
						&& StringUtils.isEmpty(checkStatus)) {
					continue;
				} else if ("planStatus".equals(entry.getKey())
						&& StringUtils.isNotEmpty(checkStatus)) {
					hql.append(" and planStatus = :status1");
					parameters.put("status1", checkStatus);
				} else if ("planStatus".equals(entry.getKey())
						&& StringUtils.isEmpty(checkStatus)) {
					continue;
				} else if ("cnd".equals(entry.getKey())) {
					// 第二次查随访表时不跑表左式，查 是否存在正常的计划
					if (!(StringUtils.isEmpty(checkStatus) && BSCHISEntryNames.PUB_VisitPlan
							.equals(table))) {
						String cnd = (String) entry.getValue();
						try {
							String whereString = ExpressionProcessor.instance()
									.toString(cnd);
							hql.append(" and ").append(whereString);
						} catch (ExpException e) {
							logger.error("Failed to process expression+" + cnd,
									e);
							throw new ServiceException(
									"Failed to process expression.", e);
						}
					}
				} else {
					hql.append(" and ").append(entry.getKey()).append("=")
							.append(entry.getValue());
				}
			}
		}
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			logger.error("Failed to select " + table, e);
			throw new ServiceException(e);
		}
		Long countNum = 0L;
		if (rsMap != null && rsMap.size() > 0) {
			countNum = (Long) rsMap.get("countNum");
		}
		if (countNum == null || countNum.intValue() == 0) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @Description:判断是否需要心脑报卡
	 * @param dao
	 * @return 如果存在返回true，否则返回false。
	 * @throws ServiceException
	 * @author ChenXianRui 2013-12-12 上午10:25:33
	 * @Modify:
	 */
	protected int isXnReport(String empiId, BaseDAO dao)
			throws ServiceException {
		int status = 0;// 0表示无需显示 1表示需要报卡2 表示28天内已经存在

		// 根据empiid查询28天内是存在心脑血管报卡（根据本次发病时间）
		String hql = "select bcfbrqsj as bcfbrqsj,lbid as lbid from cvd_diseasemanagement where empiId=:empiId order by bcfbrqsj desc";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("empiId", empiId);

		String hql1 = "select empiId from cvd_diseaseomission where empiId=:empiId and sfbk=1";
		Map<String, Object> param1 = new HashMap<String, Object>();
		param1.put("empiId", empiId);

		try {
			List<Map<String, Object>> list = dao.doSqlQuery(hql, param);
			List<Map<String, Object>> list1 = dao.doSqlQuery(hql1, param1);
			if (list.size() >= 1) {
				Map<String, Object> resultMap = list.get(0);
				Date bcfbrqsj = (Date) resultMap.get("BCFBRQSJ");

				// DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date d1 = new Date();
				Date d2 = bcfbrqsj;
				long diff = d1.getTime() - d2.getTime();
				long days = diff / (1000 * 60 * 60 * 24);
				if (days >= 28)// 超过28天
				{
					if (list1.size() < 1)// 不存在未报卡
					{
						status = 0;
					} else {
						status = 1;
					}
				} else {
					if (list1.size() < 1)// 不存在未报卡
					{
						status = 2;
					} else {
						// 28天内已经有保卡
						status = 1;
					}
				}

			} else {
				if (list1.size() < 1)// 不存在未报卡
				{
					status = 0;
				} else {
					// 28天内无报卡，但有未报卡，则需要创建报卡
					status = 1;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/**
	 * 
	 * @Description:依据empiId计算出该人年龄
	 * @param empiId
	 * @param dao
	 * @return
	 * @throws ServiceException
	 * @author ChenXianRui 2013-12-13 下午3:21:58
	 * @Modify:
	 */
	protected int getAge(String empiId, BaseDAO dao) throws ServiceException {
		EmpiModel em = new EmpiModel(dao);
		int age = -1;
		try {
			age = em.getAge(empiId);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get age of people.", e);
			throw new ServiceException(e);
		}
		return age;
	}

	/**
	 * 获取高血压确诊疑似记录
	 * 
	 * @Description:
	 * @param empiId
	 * @param dao
	 * @return
	 * @throws ServiceException
	 * @author ChenXianRui 2014-1-24 下午2:38:46
	 * @Modify:
	 */
	protected boolean isExistConfirmedHypertensionSimilarity(String empiId,
			BaseDAO dao) throws ServiceException {
		String hql = new StringBuffer(" from ")
				.append(MDC_HypertensionSimilarity)
				.append(" where empiId =:empiId and diagnosisType =:diagnosisType1")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("diagnosisType1", DiagnosisType.QZ);
		parameters.put("empiId", empiId);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ServiceException(Constants.CODE_DATABASE_ERROR,
					"获取高血压确诊疑似记录失败");
		}
		boolean isExist = false;
		if (rsList != null && rsList.size() > 0) {
			isExist = true;
		}
		return isExist;
	}

	/**
	 * @return the conditionMap
	 */
	public HashMap<String, ConditionEntity> getConditionMap() {
		return conditionMap;
	}

	/**
	 * @param conditionMap
	 *            the conditionMap to set
	 */
	public void setConditionMap(HashMap<String, ConditionEntity> conditionMap) {
		this.conditionMap = conditionMap;
	}

	public void doGetHasFCBPRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String empiId = (String) req.get("empiId");
		res.put("hasFCBPRecord", !this.checkNeedFCBP(empiId, dao));
	}

	@SuppressWarnings({ "unchecked", "unused" })
	public void doSaveHyperFCBP(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String op = (String) reqBodyMap.get("op");
		// 放值isFZ
		String empiId = (String) reqBodyMap.get("empiId");
		int age = (Integer) reqBodyMap.get("age");
		boolean isFZ = (Boolean) reqBodyMap.get("isFZ");
		if (!"".equals(empiId) && !"null".equals(empiId) && empiId != null) {
			Map<String, Object> pam_ks = new HashMap<String, Object>();
			pam_ks.put("empiId", empiId);
			String sb = " select FCBPID from MDC_Hypertension_FCBP where  empiId=:empiId";
			List<Map<String, Object>> rsMap;
			try {
				rsMap = dao.doSqlQuery(sb.toString(), pam_ks);
				if (!"".equals(rsMap) && rsMap != null && rsMap.size() > 0) {
					op = "update";
					String pKey = rsMap.get(0) + "";
					reqBodyMap.put("pKey", pKey);
					reqBodyMap.put("id", pKey);
				}
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
			}
		}
		String fcbpId = (String) reqBodyMap.get("fcbpId");
		String phrId = (String) reqBodyMap.get("phrId");
		if (StringUtils.isEmpty(phrId)) {
			// 从医疗过来，没有phrId,这时到库进去取一下
			phrId = this.getEHRRecordId(empiId, dao);
			if (StringUtils.isNotEmpty(phrId)) {
				reqBodyMap.put("phrId", phrId);
			}
		}
		String curUserId = UserUtil.get(UserUtil.USER_ID);
		String curUnitId = UserUtil.get(UserUtil.MANAUNIT_ID);
		Date curDate = new Date();
		int constriction = parseToInt(reqBodyMap.get("constriction") + "");
		int diastolic = parseToInt(reqBodyMap.get("diastolic") + "");
		String dataSource = "5";
		if (!isFZ && age >= 35) {
			dataSource = "6";
		}
		if (StringUtils.isNotEmpty(phrId)) {
			if ((constriction >= 120 && constriction <= 139)
					|| (diastolic >= 80 && diastolic <= 89)) {
				HypertensionRiskModel hrm = new HypertensionRiskModel(dao);
				hrm.insertHypertensionRisk(empiId, phrId, constriction,
						diastolic, dataSource);
			}
		}
		int hypertensionLevel = MDCBaseModel.decideHypertensionGrade(
				constriction, diastolic);
		reqBodyMap.put("hypertensionLevel", hypertensionLevel);
		reqBodyMap.put("measureDate", curDate);
		reqBodyMap.put("measureDoctor", curUserId);
		reqBodyMap.put("measureUnit", curUnitId);
		reqBodyMap.put("createUnit", curUnitId);
		reqBodyMap.put("createUser", curUserId);
		reqBodyMap.put("createDate", curDate);
		if ("6".equals(dataSource)) {
		}
		// 生成疑似记录
		boolean blnConstriction = !BSCHISUtil.isBlank(reqBodyMap
				.get("constriction"));
		boolean blnDiastolic = !BSCHISUtil.isBlank(reqBodyMap.get("diastolic"));
		if (blnConstriction || blnDiastolic) {
			if ((blnConstriction && parseToInt(reqBodyMap.get("constriction")
					+ "") >= 140)
					|| (blnConstriction && parseToInt(reqBodyMap
							.get("diastolic") + "") >= 90)) {
				HypertensionSimilarityModel hsm = new HypertensionSimilarityModel(
						dao);
				hsm.insertHypertensionSimilarity(empiId,
						(String) reqBodyMap.get("phrId"),
						parseToInt(reqBodyMap.get("constriction") + ""),
						parseToInt(reqBodyMap.get("diastolic") + ""),
						reqBodyMap);
			}
		}
	}

	private int parseToInt(String value) {
		if (value == null || "".equals(value) || "null".equals(value)) {
			return 0;
		}
		return Integer.parseInt(value);
	}

	/**
	 * 
	 * @Description:获取需要访问社区系统的诊断信息（疾病编码、病症报告卡）
	 * @2015-12-25:只检测病人的当前诊断，历史诊断不包括在任务中了
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2013-12-12 下午4:04:07
	 * @Modify:2015-12-25
	 */
	private Map<String, Object> getDiagnosisInfoForCHIS(String empiId,
			String JZXH, BaseDAO dao) throws ServiceException {
		String hql = new StringBuffer(
				"select c.JBPB as JBPB,c.JBBGK as JBBGK from ")
				.append(" GY_JBBM ")
				.append(" c where c.jbxh in (select b.ZDXH from ")
				.append(" MS_BRZD ").append(" b where b.JZXH=:JZXH )")
				// .append(" b where b.BRID in (select a.BRID from ")
				// .append(" MS_BRDA ").append(" a where a.EMPIID=:empiId))")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		// parameters.put("empiId", empiId);
		parameters.put("JZXH", JZXH);
		List<Map<String, Object>> GXYL = null;
		try {
			GXYL = dao.doSqlQuery(hql, parameters);
		} catch (PersistentDataOperationException e) {
			logger.error("Failed to select ", e);
			throw new ServiceException(e);
		}
		boolean GXY = false;
		boolean TLB = false;
		boolean BGK = false;
		List<String> thqmList = new ArrayList<String>();
		String THQM = "03,04,05,06,07,08";
		for (int i = 0; i < GXYL.size(); i++) {
			Map<String, Object> map = GXYL.get(i);
			if (map.get("JBPB") != null) {
				String JBPB = (String) map.get("JBPB");
				if (JBPB.indexOf("01") >= 0) {
					GXY = true;
				} else if (JBPB.indexOf("02") >= 0) {
					TLB = true;
				}
				if (THQM.indexOf(JBPB) >= 0) {
					thqmList.add(JBPB);
				}
			}
			if (map.get("JBBGK") != null
					&& (map.get("JBBGK") + "").indexOf("06") >= 0) {
				BGK = true;
			}
		}
		// @==高血压 首次确诊为高血压 的提示建档---s----
		String HFCHQL = new StringBuffer("select count(*) as recNum from ")
				.append(MDC_Hypertension_FCBP)
				.append(" where hypertensionHistory='1' and empiId=:empiId")
				.append(" and to_char(measureDate,'yyyy')=:currYear")
				.toString();
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("empiId", empiId);
		pMap.put("currYear", Calendar.getInstance().get(Calendar.YEAR) + "");
		Map<String, Object> rMap = null;
		try {
			rMap = dao.doLoad(HFCHQL, pMap);
		} catch (PersistentDataOperationException e1) {
			e1.printStackTrace();
		}
		if (rMap != null && rMap.size() > 0) {
			long recNum = (Long) rMap.get("recNum");
			if (recNum > 0) {
				GXY = true;
			}
		}
		// @==高血压 首次确诊为高血压 的提示建档---e----
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		resBodyMap.put("GXY", GXY);
		resBodyMap.put("TLB", TLB);
		resBodyMap.put("BGK", BGK);
		resBodyMap.put("thqmList", thqmList);
		resBodyMap.put("HYSC", false);// 高血压疑似
		if (thqmList.size() > 0) {
			resBodyMap.put("THQ", true);
		} else {
			resBodyMap.put("THQ", false);
		}
		if (JZXH != null && !"undefined".equals(JZXH)) {
			try {
				Map<String, Object> map = dao.doLoad(MS_BCJL,
						Long.parseLong(JZXH));
				if (map != null) {
					resBodyMap.put("BMI", map.get("BMI"));
				}
			} catch (NumberFormatException e) {
				logger.error("Failed to select ", e);
				throw new ServiceException(e);
			} catch (PersistentDataOperationException e) {
				logger.error("Failed to select ", e);
				throw new ServiceException(e);
			}
		}
		return resBodyMap;
	}

	/**
	 * @Description:获取门诊诊断提示信息
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2015-9-11 下午4:22:27
	 * @throws ControllerException
	 * @throws NumberFormatException
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doGetCDMsgInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> bodyMap = (Map<String, Object>) req.get("body");
		String empiId = (String) bodyMap.get("empiId");
		String JZXH = (String) bodyMap.get("JZXH");
		boolean GXY = (Boolean) bodyMap.get("GXY");
		boolean TNB = (Boolean) bodyMap.get("TNB");
		/*****************modify by zhaojian 2017-05-10*****************
		boolean THQ = (Boolean) bodyMap.get("THQ");
		*/
		boolean THQ=false;
		if(bodyMap.containsKey("isTHQ")){
			THQ = (Boolean) bodyMap.get("isTHQ");
		}
		boolean XN =false;
		if(bodyMap.get("XN")!=null){
			XN = (Boolean) bodyMap.get("XN");
		}
		// 获取档案状态信息
		Map<String, Object> riMap = this.getRecordInfo(empiId, dao);
		// 获取病人诊断相关参数
		Map<String, Object> diMap = this.getDiagnosisInfoForCHIS(empiId, JZXH,
				dao);
		bodyMap.putAll(diMap);
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		List<String> rsMsgList = new ArrayList<String>();
		List<Map<String, Object>> openNodes = new ArrayList<Map<String, Object>>();
		String nodeStatus = NODE_STATUS_HIDE;
		if (GXY) {
			String key = "chis.ehrViewNav/C_01";
			ConditionEntity conditionEntity = conditionMap.get(key);
			if (conditionEntity != null) {
				nodeStatus = getNodeStatus(key, bodyMap, riMap, dao);
				if (NODE_STATUS_CREATE.equals(nodeStatus)) {
					rsMsgList.add("需创建" + conditionEntity.getNodeShowName());
					Map<String, Object> gxyNode = new HashMap<String, Object>();
					gxyNode.put("key", "A03");
					gxyNode.put("ref", key);
					gxyNode.put("type", "ehrView");
					openNodes.add(gxyNode);
				}
			}
		}
		if (TNB) {
			String key = "chis.ehrViewNav/D_01";
			ConditionEntity conditionEntity = conditionMap.get(key);
			if (conditionEntity != null) {
				nodeStatus = getNodeStatus(key, bodyMap, riMap, dao);
				if (NODE_STATUS_CREATE.equals(nodeStatus)) {
					rsMsgList.add("需创建" + conditionEntity.getNodeShowName());
					Map<String, Object> tnbNode = new HashMap<String, Object>();
					tnbNode.put("key", "A07");
					tnbNode.put("ref", key);
					tnbNode.put("type", "ehrView");
					openNodes.add(tnbNode);
				}
			}
		}
		if (THQ) {
			int age = this.getAge(empiId, dao);
			boolean isOpenTHQ = false;// 是否直接打开问卷
			if (age >= 40 && age <= 60) {
				// （40—59岁根据诊断直接跳问卷，其他同高血压、糖尿病 一样弹对话框）
				String key = "chis.application.diseasemanage.DISEASEMANAGE/TR/THQM";
				ConditionEntity conditionEntity = conditionMap.get(key);
				if (conditionEntity != null) {
					nodeStatus = getNodeStatus(key, bodyMap, riMap, dao);
					if (NODE_STATUS_CREATE.equals(nodeStatus)) {
						isOpenTHQ = true;
					}
				}
			} else {
				String key = "chis.application.diseasemanage.DISEASEMANAGE/TR/THQM";
				ConditionEntity conditionEntity = conditionMap.get(key);
				if (conditionEntity != null) {
					nodeStatus = getNodeStatus(key, bodyMap, riMap, dao);
					if (NODE_STATUS_CREATE.equals(nodeStatus)) {
						rsMsgList
								.add("需创建" + conditionEntity.getNodeShowName());
						Map<String, Object> thqNode = new HashMap<String, Object>();
						thqNode.put("key", "A30");
						thqNode.put("ref", key);
						thqNode.put("type", "module");
						openNodes.add(thqNode);
					}
				}
			}
			resBodyMap.put("isOpenTHQ", isOpenTHQ);
		}
		if(XN)
		{
			int state=isXnReport(empiId,dao);
			if(state==1)
			{
				String key = "chis.ehrViewNav/M_02";
				ConditionEntity conditionEntity = conditionMap.get(key);
						rsMsgList.add("需创建" + conditionEntity.getNodeShowName());
						Map<String, Object> xnNode = new HashMap<String, Object>();
						xnNode.put("key", "A03");
						xnNode.put("ref", key);
						xnNode.put("type", "ehrView");
						openNodes.add(xnNode);
	
			}
		}
		resBodyMap.put("rsMsgList", rsMsgList);
		resBodyMap.put("openNodes", openNodes);
		resBodyMap.put("mtList", mtList);
		resBodyMap.put("empiId", empiId);
		res.put("body", resBodyMap);
		logger.info("ClinicDiagnosisEntryList of EMRView  === "
				+ resBodyMap.toString());
	}

	/**
	 * @Description:获取中医体质辨识节点状态
	 * @param empiId
	 * @param dao
	 * @return
	 * @throws ServiceException
	 * @author ChenXianRui 2015-11-13 上午11:28:16
	 * @Modify:
	 */
	private String getTCMQNodeStatus(String empiId, BaseDAO dao)
			throws ServiceException {
		String hql = new StringBuffer("select count(*) as recNum from ")
				.append(MDC_ChineseMedicineManage)
				.append(" where status='0' and empiId=:empiId")
				.append(" and to_char(createDate,'yyyy')=:currYear").toString();
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("empiId", empiId);
		pMap.put("currYear", "" + Calendar.getInstance().get(Calendar.YEAR));
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql, pMap);
		} catch (PersistentDataOperationException e) {
			logger.error("获取中医体质辨识节点状态失败！", e);
			throw new ServiceException(e);
		}
		String tcmqns = NODE_STATUS_CREATE;
		if (rsMap != null && rsMap.size() > 0) {
			long recNum = (Long) rsMap.get("recNum");
			if (recNum > 0) {
				tcmqns = NODE_STATUS_READ;
			}
		}
		return tcmqns;
	}

	/**
	 * @Description:获取中医指导节点状态
	 * @param empiId
	 * @param dao
	 * @return
	 * @throws ServiceException
	 * @author ChenXianRui 2015-11-13 下午1:56:30
	 * @Modify:
	 */
	public String getTCMSGNodeStatus(String empiId, BaseDAO dao)
			throws ServiceException {
		String hql = new StringBuffer("select count(*) as hasRN from ")
				.append(TCM_SickGuidance).append(" where empiId=:empiId")
				.toString();
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("empiId", empiId);
		Map<String, Object> sgMap = null;
		try {
			sgMap = dao.doLoad(hql, pMap);
		} catch (PersistentDataOperationException e1) {
			logger.error("获取该病人的中医指导的记录数据失败！", e1);
			throw new ServiceException(e1);
		}
		String tcmsgns = NODE_STATUS_HIDE;
		if (sgMap != null && sgMap.size() > 0) {
			long hasRN = (Long) sgMap.get("hasRN");
			if (hasRN > 0) {
				tcmsgns = NODE_STATUS_READ;
			}
		}
		String sql = new StringBuffer("select count(*) as recNum from ")
				.append(" TCM_TCMRegister a ")
				.append(" where a.empiId=:empiId")
				.append(" and not exists ")
				.append("(select 1 from TCM_SickGuidance b where a.registerid=b.registerid and b.empiid=:empiId2)")
				.toString();
		pMap.put("empiId2", empiId);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSqlLoad(sql, pMap);
		} catch (PersistentDataOperationException e) {
			logger.error("获取未做中医指导的中医登记记录失败！", e);
			throw new ServiceException(e);
		}
		if (rsMap != null && rsMap.size() > 0) {
			long recNum = ((BigDecimal) rsMap.get("RECNUM")).longValue();
			if (recNum > 0) {
				tcmsgns = NODE_STATUS_CREATE;
			}
		}
		return tcmsgns;
	}

	/**
	 * @Description:根据就诊序号获取门诊医生站病人列表中疾病诊断提示信息
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author zhaojian 2019-1-16
	 * @throws ControllerException
	 * @throws NumberFormatException
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doGetBrlbMsgInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		// 提示信息
		List<Map<String, Object>> rsMsgList = new ArrayList<Map<String, Object>>();
		Map<String, Object> bodyMap = (Map<String, Object>) req.get("body");
		String empiId = (String) bodyMap.get("empiId");
		String GHXH = (String) bodyMap.get("GHXH");
		Map<String, Object> mapGxyvpNum = new HashMap<String, Object>();
		Map<String, Object> mapTnbvpNum = new HashMap<String, Object>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		String sql = "";
		try {
			sql="select case when exists(select EMPIID from PUB_VISITPLAN where EMPIID=:empiId and to_char(BEGINDATE,'yyyy')=to_char(sysdate,'yyyy') and BUSINESSTYPE='1') then 1 else 0 end as yearPlan,cntNum from (select count(1) as cntNum from PUB_VISITPLAN where EMPIID=:empiId and BUSINESSTYPE='1' and to_char(BEGINDATE,'yyyy')=to_char(sysdate,'yyyy') and BEGINDATE<SYSDATE and planStatus='0') a";
			mapGxyvpNum = dao.doSqlLoad(sql,parameters);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		if (mapGxyvpNum.get("YEARPLAN").toString().equals("0") || !mapGxyvpNum.get("CNTNUM").toString().equals("0")) {
			Map<String, Object> nMap = new HashMap<String, Object>();
			nMap.put("nodeName", "高血压随访");
			if(mapGxyvpNum.get("YEARPLAN").toString().equals("0")){
				nMap.put("nodeKey", "A05_noyearplan");
			}else{
				nMap.put("nodeKey", "A05");
			}
			rsMsgList.add(nMap);
		}
		try {
			sql="select case when exists(select EMPIID from PUB_VISITPLAN where EMPIID=:empiId and to_char(BEGINDATE,'yyyy')=to_char(sysdate,'yyyy') and BUSINESSTYPE='2') then 1 else 0 end as yearPlan,cntNum from (select count(1) as cntNum from PUB_VISITPLAN where EMPIID=:empiId and BUSINESSTYPE='2' and to_char(BEGINDATE,'yyyy')=to_char(sysdate,'yyyy') and BEGINDATE<SYSDATE and planStatus='0') a";
			mapTnbvpNum = dao.doSqlLoad(sql,parameters);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		if (mapTnbvpNum.get("YEARPLAN").toString().equals("0") || !mapTnbvpNum.get("CNTNUM").toString().equals("0")) {
			Map<String, Object> nMap = new HashMap<String, Object>();
			nMap.put("nodeName", "糖尿病随访");
			if(mapGxyvpNum.get("YEARPLAN").toString().equals("0")){
				nMap.put("nodeKey", "A08_noyearplan");
			}else{
				nMap.put("nodeKey", "A08");
			}
			rsMsgList.add(nMap);
		}
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		resBodyMap.put("rsMsgList", rsMsgList);
		res.put("body", resBodyMap);
		logger.info("根据就诊序号获取门诊医生站病人列表中疾病诊断提示信息  === "
				+ resBodyMap.toString());
	}

	/**
	 * @Description:获取门诊医生站家医签约病人的履约提示信息
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author zhaojian 2019-8-26
	 * @throws PersistentDataOperationException 
	 * @throws ControllerException
	 * @throws NumberFormatException
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doGetSCMMsgInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, PersistentDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		Map<String, Object> bodyMap = (Map<String, Object>) req.get("body");
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		List<Map<String, Object>> rsMsgList = new ArrayList<Map<String, Object>>();
		String empiId = (String) bodyMap.get("empiId");
		String itemnames = (String) bodyMap.get("itemnames");

		//查询HIS系统中该病人是否已收过家医签约基本服务包（15316、16049）的费用（默认判定一年内）
		String sql_where = " a.YJXH=b.YJXH AND a.YLXH=c.FYXH AND b.BRID IN (SELECT A.BRID FROM MS_BRDA A,(SELECT SFZH FROM MS_BRDA WHERE EMPIID=:EMPIID AND SFZH IS NOT NULL) B WHERE A.SFZH=LOWER(B.SFZH) OR (A.SFZH=UPPER(B.SFZH))) AND b.ZFPB=0  "
				+ "AND b.FPHM IS NOT NULL AND c.FYMC='家庭医生签约基本服务包' AND c.ZFPB=0 AND b.KDRQ>ADD_MONTHS(SYSDATE,-12) ";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("EMPIID", empiId);
		long count = dao.doSqlCount("MS_YJ02 a,MS_YJ01 b,GY_YLSF c",
				sql_where, parameters);
		//查询健康档案系统中该病人是否已签约
		String sql_where2 = " (IDCARD=(SELECT LOWER(SFZH) FROM MS_BRDA WHERE EMPIID=:EMPIID AND SFZH IS NOT NULL)  OR IDCARD=(SELECT UPPER(SFZH) FROM MS_BRDA WHERE EMPIID=:EMPIID AND SFZH IS NOT NULL)) AND SIGNFLAG=1 AND SYSDATE>=SCEBEGINDATE AND SYSDATE<=(SCEENDDATE+1)";
		long count2 = dao.doSqlCount("MPI_DEMOGRAPHICINFO",
				sql_where2, parameters);
		if (count > 0 || count2 > 0) {
			parameters.put("USERID", user.getUserId());
			sql_where = "select to_char(wm_concat(distinct b.itemname)) as ITEMNAME from scm_increaseitems a,scm_serviceitems b,scm_signcontractrecord c " +
					"where a.taskcode=b.itemcode and a.scid=c.scid and c.favoreeempiid=:EMPIID and c.signflag=1 and to_char(c.enddate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') " +
					"and c.createuser=:USERID and ((a.totservicetimes is not null and a.servicetimes<a.totservicetimes) or a.totservicetimes is null) " +
					"and b.itemname in(" + itemnames + ")";
			Map<String, Object> mapItem = dao.doSqlLoad(sql_where, parameters);
			if(mapItem.get("ITEMNAME")!=null){
				Map<String, Object> nMap = new HashMap<String, Object>();
				nMap.put("nodeName", mapItem.get("ITEMNAME"));
				nMap.put("nodeKey", "A91");
				rsMsgList.add(nMap);
			}
		}
		resBodyMap.put("rsMsgList", rsMsgList);
		res.put("body", resBodyMap);
		logger.info("ClinicDiagnosisEntryList of EMRView  === "
				+ resBodyMap.toString());
	}
}
